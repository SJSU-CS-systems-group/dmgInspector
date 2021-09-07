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
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.position(buffer.position() + 32);
        start = buffer.getLong();
        end = buffer.getLong();
        lbaSectors = (end - start) + 1;
        size = getPartitionSize(lbaSectors);


        System.out.println("Start: " + start + " | " + "End: " + end + " | " + "Sectors: " + lbaSectors + " | " + "Size: " + size);
    }

    private String getPartitionSize(long sectors) {
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
}
