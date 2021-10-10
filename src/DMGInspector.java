import utils.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DMGInspector {
    private static final int KOLY_BLOCK_SIZE = 512;
    private static byte[] kolyBlockBytes = new byte[KOLY_BLOCK_SIZE];
    private static DMGKolyBlock dmgKolyBlock;
    private static Plist plist;

    public DMGInspector(String dmgFile) throws IOException {
        kolyBlockBytes = getKolyBlockBytes(dmgFile);
        dmgKolyBlock = new DMGKolyBlock(kolyBlockBytes);

        byte[] dataForkWithPlistBytes = Utils.getImageBytes(dmgFile, (int) dmgKolyBlock.XMLLength + (int) dmgKolyBlock.XMLOffset);
        ByteBuffer dataForkWithPlistBuffer = ByteBuffer.wrap(dataForkWithPlistBytes);
        ByteBuffer dataForkBuffer = dataForkWithPlistBuffer.slice(0,(int) dmgKolyBlock.XMLOffset);
        ByteBuffer plistBuffer = dataForkWithPlistBuffer.slice((int) dmgKolyBlock.XMLOffset, (int) dmgKolyBlock.XMLLength);

//        byte[] dataForkBytes = Arrays.copyOfRange(dataForkWithPlistBytes, 0, (int) dmgKolyBlock.XMLOffset + 1); // Do we need this +1?
//        byte[] plistBytes = Arrays.copyOfRange(dataForkWithPlistBytes, (int) dmgKolyBlock.XMLOffset, (int) dmgKolyBlock.XMLLength + (int) dmgKolyBlock.XMLOffset);

        plist = new Plist(plistBuffer, dataForkBuffer);

        /*StringBuilder sb = new StringBuilder();
        for (byte b : kolyBlockBytes) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());*/
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



