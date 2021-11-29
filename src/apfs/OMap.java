package apfs;

import utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.HashMap;

// See APFS Reference pg. 44
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

    // OID -> Physical Address
    HashMap<Long, Long> parsedOmap = new HashMap<>();

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

        ArrayDeque<BTreeNode> nodes = new ArrayDeque<>();
        nodes.add(rootNode);
        while (nodes.size() > 0) {
            BTreeNode n = nodes.removeFirst();
            // System.out.println(n);
            if (n.btn_flags_is_leaf) {
                for (int i = 0; i < n.omapKeys.size(); i++) {
                    OMAPKey key = n.omapKeys.get(i);
                    OMAPValue val = n.omapValues.get(i);
//                    System.out.println("LEAF ENTRY KEY " + key.ok_oid);
//                    System.out.println("LEAF ENTRY VAL " + val);
                    parsedOmap.put(key.ok_oid, val.paddr_t);
                }
            } else {
                for (OMAPValue omapVal : n.omapValues) {
//                    System.out.println("CHILD NODE AT BLOCK " + omapVal.paddr_t);
                    int physOffset = (int) omapVal.paddr_t * blockSize;
                    ByteBuffer childNodeBytes = Utils.GetBuffer(imagePath, physOffset, blockSize);
                    nodes.add(new BTreeNode(childNodeBytes));
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format("\n\tOMAP Header %s\n\tFlags: %s, Snap Count: %s, Tree type: %s, Snapshot Tree Type: %s, Tree OID: %s, Snapshot Tree OID: %s, Most Recent Snap: %s\n\tParsed OMAP: %s", om_o, om_flags, om_snap_count, om_tree_type, om_snapshot_tree_type, om_tree_oid, om_snapshot_tree_oid, om_most_recent_snap, parsedOmap.toString());
    }
}
