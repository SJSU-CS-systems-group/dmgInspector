package apfs.BTreeNode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BTreeInfoFixed {
    public int bt_flags;
    public int bt_node_size;
    public int bt_key_size;
    public int bt_val_size;

    public BTreeInfoFixed(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        bt_flags = buffer.getInt();
        bt_node_size = buffer.getInt();
        bt_key_size = buffer.getInt();
        bt_val_size = buffer.getInt();
    }

    @Override
    public String toString() {
        return String.format("{Flags: %s, Node Size: %s, Key Size: %s, Value Size: %s}", bt_flags, bt_node_size, bt_key_size, bt_val_size);
    }
}
