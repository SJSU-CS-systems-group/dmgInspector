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
    public int sectors;
    public String size;


    public GPTPartitionEntry(byte[] partitionEntry) {
        originalBytes = partitionEntry;

        ByteBuffer buffer = ByteBuffer.wrap(partitionEntry);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.position(buffer.position() + 32);
        start = buffer.getLong();
        end = buffer.getLong();

        System.out.println("Start: " + start + " | " + "End: " + end);
    }
}
