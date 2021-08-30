import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    /**
     * @return Image path chosen by the user via their File Explorer.
     * @throws FileNotFoundException Reference: https://mkyong.com/swing/java-swing-jfilechooser-example/
     */
    public static String chooseImagePath() throws FileNotFoundException {
        JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue != JFileChooser.APPROVE_OPTION) throw new FileNotFoundException("Invalid file.");

        File selectedFile = jfc.getSelectedFile();
        return selectedFile.getAbsolutePath();
    }

    /**
     * @param imgPath path of the file to get the file name of. (e.g. C:\Users\Mat\(...)\src\images\2021-05-07-raspios-buster-armhf-lite.img)
     * @return name of the file at the path. (e.g. 2021-05-07-raspios-buster-armhf-lite.img)
     * Reference: https://www.geeksforgeeks.org/path-getfilename-method-in-java-with-examples/
     */
    public static String getPathFileName(String imgPath) {
        Path p = Paths.get(imgPath);
        return p.getFileName().toString();
    }


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


    public static int signedByteToUnsigned(byte b) {
        // See https://mkyong.com/java/java-convert-bytes-to-unsigned-bytes
        return b & 0xFF;
    }


    public static String decToHex(int dec) {
        String hexStr = Integer.toHexString(dec);

        if (hexStr.length() == 1) {
            hexStr = "0" + hexStr;
        }

        return hexStr;
    }

    public static Integer hexToDecimal(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static String hexArrayToLEHexString(String[] hexArray) {
        String hexString = "";
        for (int c = hexArray.length - 1; c >= 0; c--) {
            hexString += hexArray[c];
        }
        return hexString;
    }

    public static int getPartitionSize(int startPartition, int endPartition) {
        return ((endPartition - startPartition) * 512) / 1000000;
    }


}
