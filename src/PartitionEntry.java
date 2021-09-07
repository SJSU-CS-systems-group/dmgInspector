import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PartitionEntry {
    public byte[] originalBytes;
    public byte id;
    public String type;
    public byte boot;
    public int start;
    public int end;
    public int sectors;
    public String size;

    public PartitionEntry(byte[] partitionEntry) {
        originalBytes = partitionEntry;

        ByteBuffer buffer = ByteBuffer.wrap(partitionEntry);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        boot = buffer.get();
        buffer.position(buffer.position() + 3);
        id = buffer.get();
        type = Utils.getPartitionType(id);
        buffer.position(buffer.position() + 3);
        start = buffer.getInt();
        sectors = buffer.getInt();
        end = start + sectors - 1;
        size = getPartitionSize(sectors);
    }

    // Constructor for a partition entry that has its start and end sectors offset by its Extended Partition's start sector.
    // Use this for EBR partition entries.
    public PartitionEntry(byte[] partitionEntry, int startSector) {
        this(partitionEntry);
        start += startSector;
        end += startSector;
    }

    public String toString() {
        return String.format("%-10s %-10s %-10s %-10s %-10s %-10s %n", boot, start, end, sectors, size, type);
    }

    private String getPartitionSize(int sectors) {
        return bytesIntoHumanReadable(sectors * 512);
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

    public boolean isExtended() {
        return id == 5;
    }
}
