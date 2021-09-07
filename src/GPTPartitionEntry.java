import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class GPTPartitionEntry {
    public byte[] originalBytes;
    public String type;
    public long start;
    public long end;
    public long lbaSectors;
    public String size;
    public Map<String, String> guidMap = new HashMap<String, String>()
    {{
        put("48465300-0000-11AA-AA11-00306543ECAC", "Hierarchical File System Plus (HFS+)");
        put("00000000-0000-0000-0000-000000000000", "unused");
    }};


    public GPTPartitionEntry(byte[] partitionEntry) {
        originalBytes = partitionEntry;
        ByteBuffer buffer = ByteBuffer.wrap(partitionEntry);
        // Get the GUID for finding the Partition Type
        type = guidMap.get(getGuidHex(buffer));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(buffer.position() + 16);
        start = buffer.getLong();
        end = buffer.getLong();
        lbaSectors = (end - start) + 1;
        size = getPartitionSize(lbaSectors);
    }

    public String toString() {
        return String.format("%-10s %-10s %-10s %-10s %-10s %n", start, end, lbaSectors, size, type);
    }

    public boolean determineIfUnusedPartition() {
        return this.type.equals("unused");
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
        buffer.order(ByteOrder.LITTLE_ENDIAN);
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
