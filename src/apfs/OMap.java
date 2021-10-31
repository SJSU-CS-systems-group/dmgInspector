package apfs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class OMap {
    BlockHeader om_o;
    int om_flags;
    int om_snap_count;
    int om_tree_type;
    int om_snapshot_tree_type;
    long om_tree_oid;
    long om_snapshot_tree_oid;
    long om_most_recent_snap;
    long om_pending_revert_min;

    public OMap(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        om_o = new BlockHeader(buffer);

        om_flags = buffer.getInt();
        om_snap_count = buffer.getInt();
        om_tree_type = buffer.getInt();
        om_snapshot_tree_type = buffer.getInt();

        om_tree_oid = buffer.getLong();
        om_snapshot_tree_oid = buffer.getLong();
        om_most_recent_snap = buffer.getLong();
        om_pending_revert_min = buffer.getLong();
    }

    @Override
    public String toString() {
        return "OMap{" +
                "om_o=" + om_o +
                ", om_flags=" + om_flags +
                ", om_snap_count=" + om_snap_count +
                ", om_tree_type=" + om_tree_type +
                ", om_snapshot_tree_type=" + om_snapshot_tree_type +
                ", om_tree_oid=" + om_tree_oid +
                ", om_snapshot_tree_oid=" + om_snapshot_tree_oid +
                ", om_most_recent_snap=" + om_most_recent_snap +
                ", om_pending_revert_min=" + om_pending_revert_min +
                '}';
    }
}
