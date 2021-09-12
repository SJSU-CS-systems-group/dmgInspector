import java.io.FileInputStream;
import java.io.IOException;

public class DMGInspector {
    private static final int KOLY_BLOCK_SIZE = 512;
    private static byte[] kolyBlockBytes = new byte[KOLY_BLOCK_SIZE];
    private static DMGKolyBlock dmgKolyBlock;

    public DMGInspector (String dmgFile) throws IOException {
        kolyBlockBytes = getKolyBlockBytes(dmgFile);
        dmgKolyBlock = new DMGKolyBlock(kolyBlockBytes);

        System.out.println(dmgKolyBlock);
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



