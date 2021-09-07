import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GPTPartitionEntry {
    public byte[] originalBytes;
    public byte id;
    public String type;
    public long start;
    public long end;
    public long lbaSectors;
    public String size;


    public GPTPartitionEntry(byte[] partitionEntry) {
        originalBytes = partitionEntry;

        ByteBuffer buffer = ByteBuffer.wrap(partitionEntry);

        // Get the GUID for finding the Partition Type
        String guidHex = getGuidHex(buffer);
        System.out.println(guidHex);


        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(buffer.position() + 16);
        start = buffer.getLong();
        end = buffer.getLong();
        lbaSectors = (end - start) + 1;
        size = getPartitionSize(lbaSectors);


        System.out.println("Start: " + start + " | " + "End: " + end + " | " + "Sectors: " + lbaSectors + " | " + "Size: " + size);
    }

    private String getPartitionSize(long sectors) {
        return bytesIntoHumanReadable(sectors * 512);
    }

    private String OriginalBytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    private String bytesIntoHumanReadable(double bytes) {
        double kilobyte = 1024;
        double megabyte = kilobyte * 1024;
        double gigabyte = megabyte * 1024;
        double terabyte = gigabyte * 1024;

        NumberFormat formatter = new DecimalFormat("#0.0");

        if ((bytes >= 0) && (bytes < kilobyte)) {
            return bytes + " B";

        } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
            return formatter.format(bytes / kilobyte) + " KB";

        } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
            return formatter.format(bytes / megabyte) + " MB";

        } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
            return formatter.format(bytes / gigabyte) + " GB";

        } else if (bytes >= terabyte) {
            return formatter.format(bytes / terabyte) + " TB";

        } else {
            return bytes + " Bytes";
        }
    }

    private static String getGuidHex(ByteBuffer buffer) {
        String guidHex = "";
        int first8GuidBytes = buffer.getInt();
        String first8GuidHex = String.format("%08X", first8GuidBytes);
        guidHex += first8GuidHex;

        buffer.order(ByteOrder.BIG_ENDIAN);
        short next2GuidBytes = buffer.getShort();
        String next2GuidHex = String.format("%04X", next2GuidBytes);
        guidHex += "-" + next2GuidHex;

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        next2GuidBytes = buffer.getShort();
        next2GuidHex = String.format("%04X", next2GuidBytes);
        guidHex += "-" + next2GuidHex;

        buffer.order(ByteOrder.BIG_ENDIAN);
        next2GuidBytes = buffer.getShort();
        next2GuidHex = String.format("%04X", next2GuidBytes);
        guidHex += "-" + next2GuidHex;

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        // First 16 bytes are the Partition GUID
        String last6BytesHex = "";
        for (int i = 0; i < 6; i++) {
            last6BytesHex += String.format("%02X", buffer.get() & 0xff);
        }
        guidHex += "-" + last6BytesHex;
        return guidHex;
    }
}
