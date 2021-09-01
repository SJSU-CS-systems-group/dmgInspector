import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

public class PartitionEntry {
    public byte[] originalBytes;
    private byte id;
    private byte type;
    private byte boot;
    private int start;
    private int end;
    private int sectors;
    private String size;
//    private static HashMap<Byte, String> typeByteToName = new HashMap<>() {{
//        put(Byte.valueOf('12'), "FAT32 LBA");
//        put(0x83, "LINUX");
//        put('0', "Empty");
//        put(0x05, "Extended");
//        put(0x07, "HPFS/NTFS/exFAT");
//    }};


    public PartitionEntry(byte[] partitionEntry) {
        originalBytes = partitionEntry;

        ByteBuffer buffer = ByteBuffer.wrap(partitionEntry);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        boot = buffer.get();
        buffer.position(buffer.position()+3);
        id = buffer.get();
//        type = typeByteToName.get(id);
        buffer.position(buffer.position()+3);
        start = buffer.getInt();
        sectors = buffer.getInt();
        end = start + sectors - 1;
        size = getPartitionSize(sectors);
    }

    public String toString() {
        return String.format("%-10s %-10s %-10s %-10s %-10s %-10s %n", boot, start, end, sectors, size, id);
    }

    private String getPartitionSize(int sectors) {
        return bytesIntoHumanReadable(sectors * 512);
    }

    private String bytesIntoHumanReadable(long bytes) {
        long kilobyte = 1024;
        long megabyte = kilobyte * 1024;
        long gigabyte = megabyte * 1024;
        long terabyte = gigabyte * 1024;

        if ((bytes >= 0) && (bytes < kilobyte)) {
            return bytes + " B";

        } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
            return (bytes / kilobyte) + " KB";

        } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
            return (bytes / megabyte) + " MB";

        } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
            return (bytes / gigabyte) + " GB";

        } else if (bytes >= terabyte) {
            return (bytes / terabyte) + " TB";

        } else {
            return bytes + " Bytes";
        }
    }
}
