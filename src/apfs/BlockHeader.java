package apfs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BlockHeader {
    public long checksum;
    public long block_id;
    public long version;
    public short block_type;
    public short flags;
    public int padding;

    public BlockHeader(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        checksum = buffer.getLong();
        block_id = buffer.getLong();
        version = buffer.getLong();
        block_type = buffer.getShort();
        flags = buffer.getShort();
        padding = buffer.getInt();
    }

    @Override
    public String toString () {
        return String.format("{Checksum: %s, Block ID: %s, Version: %s, Block Type: %s, Flags: %s, Padding: %s}", Long.toUnsignedString(checksum), block_id, version, block_type, Short.toUnsignedInt(flags), padding);
    }
}
