package utils;

import apfs.APFSVolume;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static String getPartitionType(byte id) {
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

    public static ByteBuffer GetBuffer(String pathname, int offset, int length) throws FileNotFoundException, IOException {
        RandomAccessFile ras = new RandomAccessFile(pathname, "r");
        ras.seek(offset);
        byte[] superBlockBytes = new byte[length];
        ras.read(superBlockBytes);
        ByteBuffer buffer = ByteBuffer.wrap(superBlockBytes);
        return buffer;
    }

    public static String nanoEpochToDateTime(long nanoEpoch) {
        Date date = new Date(nanoEpoch / 1_000_000);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    public static void extentRangeToFile(String imagePath, String outPath, long physAddr, long len) throws IOException {

        Path path = Paths.get(outPath);
        try{
            Files.createDirectories(path.getParent());
        }catch(Exception e){

        }

        byte[] extentBytes = new byte[(int) len];
        ByteBuffer buff = GetBuffer(imagePath, (int) physAddr, (int) len);
        buff.get(extentBytes);

        File decompressedMishFile = new File(outPath);
        FileOutputStream fileWriter = new FileOutputStream(decompressedMishFile.getAbsolutePath());
        fileWriter.write(extentBytes);
        fileWriter.close();
    }

    // Recursively delete an entire folder
    // We use this to delete the APFS output folder so files from previous runs aren't kept
    // Source: https://www.tutorialspoint.com/how-to-delete-folder-and-sub-folders-using-java
    public static void deleteFolder(File file) {
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory()) {
                deleteFolder(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }
}
