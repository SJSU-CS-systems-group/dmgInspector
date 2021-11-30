package apfs.kv.values;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class OMAPValue {
    public int ov_flags;
    public int ov_size;
    public long paddr_t;

    public OMAPValue(ByteBuffer buffer, boolean isLeaf) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Looking at the 010 Editor APFS bytes, it seems like only leaf nodes have flags & size.
        // This means intermediary nodes (which are non-leaf nodes) only have phys addr.
        if (isLeaf) ov_flags = buffer.getInt();
        if (isLeaf) ov_size = buffer.getInt();
        paddr_t = buffer.getLong();
    }

    @Override
    public String toString() {
        return "OMAPValue{" +
                "ov_flags=" + ov_flags +
                ", ov_size=" + ov_size +
                ", paddr_t=" + paddr_t +
                '}';
    }
}


