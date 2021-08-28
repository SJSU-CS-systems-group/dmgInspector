import java.io.FileInputStream;
import java.io.IOException;

public class Utils {


    public static byte[] getImageBytes(String imagePath, int numBytes) throws IOException {
        FileInputStream in = null;

        byte[] bytes = new byte[numBytes];

        try {
            in = new FileInputStream(imagePath);
            in.read(bytes);
        } finally {
            in.close();
        }

        return bytes;
    }

    public static String[] byteToHexArray(byte[] bytes) {
        String[] hexVals = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            String byteAsHex = decToHex(signedByteToUnsigned(bytes[i]));
            hexVals[i] = byteAsHex;
        }
        return hexVals;
    }


    private static int signedByteToUnsigned(byte b) {
        // See https://mkyong.com/java/java-convert-bytes-to-unsigned-bytes
        return b & 0xFF;
    }


    private static String decToHex(int dec) {
        String hexStr = Integer.toHexString(dec);

        if (hexStr.length() == 1) {
            hexStr = "0" + hexStr;
        }

        return hexStr;
    }

}
