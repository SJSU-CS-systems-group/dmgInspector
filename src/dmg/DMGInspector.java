package dmg;

import utils.Utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DMGInspector {
    private static final int KOLY_BLOCK_SIZE = 512;
    private static byte[] kolyBlockBytes = new byte[KOLY_BLOCK_SIZE];
    private static DMGKolyBlock dmgKolyBlock;
    public static Plist plist;

    public DMGInspector(String dmgFile) throws IOException {
        kolyBlockBytes = getKolyBlockBytes(dmgFile);
        dmgKolyBlock = new DMGKolyBlock(kolyBlockBytes);

        byte[] dataForkWithPlistBytes = Utils.getImageBytes(dmgFile, (int) dmgKolyBlock.XMLLength + (int) dmgKolyBlock.XMLOffset);
        ByteBuffer dataForkWithPlistBuffer = ByteBuffer.wrap(dataForkWithPlistBytes);

        int dataForkLength = (int) dmgKolyBlock.XMLOffset;
        ByteBuffer dataForkBuffer = dataForkWithPlistBuffer.slice(0, dataForkLength);

        int plistLength = (int) dmgKolyBlock.XMLLength;
        ByteBuffer plistBuffer = dataForkWithPlistBuffer.slice(dataForkLength, plistLength);

        plist = new Plist(plistBuffer, dataForkBuffer);
    }

    private static byte[] getKolyBlockBytes(String dmgFile) throws IOException {
        FileInputStream fis = new FileInputStream(dmgFile);
        fis.getChannel().position(fis.getChannel().size() - KOLY_BLOCK_SIZE);
        byte[] kolyBytes = new byte[KOLY_BLOCK_SIZE];
        fis.read(kolyBytes);
        return kolyBytes;
    }

    public static DMGInspector parseImage(String imgPath) throws IOException {
        return new DMGInspector(imgPath);
    }
}
