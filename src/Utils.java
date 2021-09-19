import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Utils {
    public static String[] partitionType = new String[256];
    static {
        partitionType[0] = "unused";
        partitionType[12] = "FAT32";
        partitionType[131] = "Linux";
        partitionType[5] = "Extended";
        partitionType[7] = "HPFS/NTFS/exFAT";
        partitionType[175] = "HFS/HFS+";
        partitionType[238] = "GUID Parition Table";
    }

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

    public static String getPartitionType(byte id){
        return Utils.partitionType[id & 0xff];

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

    public static byte[] getImageBytes(String imagePath, int numBytes, int offset) throws IOException {
        FileInputStream in = null;
        byte[] bytes = new byte[numBytes];
        try {
            in = new FileInputStream(imagePath);
            in.read(bytes, offset, 4);
        } finally {
            in.close();
        }
        return bytes;
    }

    public static int getImageBytesSize(String imagePath) throws IOException {
        FileInputStream in = null;
        int size = 0;
        try {
            in = new FileInputStream(imagePath);
            size = in.available();
        } finally {
            in.close();
        }
        return size;
    }

    public static String OriginalBytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
