package apfs.BTreeNode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// Table of Contents entry
// See APFS Reference pg. 129
public class BTreeTOCEntry {
    // -1 will indicate there is no key length
    //      -- because fixed-length TOC Entries will don't read length bytes.
    public short key_offset;
    public short key_length = -1;
    public short value_offset;
    public short value_length = -1;

    public BTreeTOCEntry(ByteBuffer buffer, boolean isFixed) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        key_offset = buffer.getShort();
        if (!isFixed) key_length = buffer.getShort();
        value_offset = buffer.getShort();
        if (!isFixed) value_length = buffer.getShort();
    }

    @Override
    public String toString() {
        return "BTreeTOCEntry{" +
                "key_offset=" + key_offset +
                ", key_length=" + key_length +
                ", value_offset=" + value_offset +
                ", value_length=" + value_length +
                '}';
    }
}
