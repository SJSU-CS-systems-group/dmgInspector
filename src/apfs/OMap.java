package apfs;

import utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

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

    // OID -> OMAPValue
    HashMap<Long, OMAPValue> parsedOmap = new HashMap<>();

    public OMap(ByteBuffer buffer, String imagePath, int blockSize) throws IOException {
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

        parseOmapBTree(imagePath, blockSize);
    }


    /**
     * Parses the OMAP's BTree, mapping oids to OMAP Values in a hash map
     *
     * @param imagePath
     * @param blockSize
     * @throws IOException
     */
    private void parseOmapBTree(String imagePath, int blockSize) throws IOException {
        // Parse the OMAP's root node
        int csbOMAPBTreeOffset = (int) om_tree_oid * blockSize;
        ByteBuffer rootNodeBuffer = Utils.GetBuffer(imagePath, csbOMAPBTreeOffset, blockSize);
        BTreeNode rootNode = new BTreeNode(rootNodeBuffer);

        System.out.println(rootNode);


        // TODO: Traverse OMAP BTree to get all OMAP Keys & Values
        // Parse the entire B-Tree

        // TODO: Insert each key oid-value pair into the OMAP BTree Structure for ALL nodes
        for (int i = 0; i < rootNode.omapKeys.size(); i++) {
            long oid = rootNode.omapKeys.get(i).ok_oid;
            OMAPValue val = rootNode.omapValues.get(i);
            parsedOmap.put(oid, val);
        }
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
                ", \n\tParsed OMap=" + parsedOmap.toString() +
                '}';
    }
}
