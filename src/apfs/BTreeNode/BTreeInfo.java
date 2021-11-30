package apfs.BTreeNode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BTreeInfo {
    public BTreeInfoFixed bTreeInfoFixed;
    public int bt_longest_key;
    public int bt_longest_val;
    public long bt_key_count;
    public long bt_node_count;

    public BTreeInfo(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        bTreeInfoFixed = new BTreeInfoFixed(buffer);
        bt_longest_key = buffer.getInt();
        bt_longest_val = buffer.getInt();
        bt_key_count = buffer.getLong();
        bt_node_count = buffer.getLong();
    }

    @Override
    public String toString() {
        return String.format("\nB-Tree Info Fixed: %s\nLongest Key: %s\nLongest Value: %s\nKey Count: %s\nNode Count: %s\n", bTreeInfoFixed, bt_longest_key, bt_longest_val, bt_key_count, bt_node_count);
    }
}
