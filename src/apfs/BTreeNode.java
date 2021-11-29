package apfs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class BTreeNode {
    public BlockHeader btn_o;
    public short btn_flags;
    public boolean btn_flags_is_fixed_KV_size;
    public boolean btn_flags_is_root;
    public boolean btn_flags_is_leaf;
    public boolean btn_flags_is_hashed;
    public short btn_level;
    public int btn_nkeys;
    public short btn_table_space_off;
    public short btn_table_space_len;
    public short btn_freespace_off;
    public short btn_freespace_len;
    public short btn_key_free_list_off;
    public short btn_key_free_list_len;
    public short btn_val_free_list_off;
    public short btn_val_free_list_len;
    public ArrayList<BTreeTOCEntry> tableOfContents = new ArrayList<>();

    public ArrayList<FSObjectKey> fsKeys = new ArrayList<>();

    public ArrayList<OMAPKey> omapKeys = new ArrayList<>();
    public ArrayList<OMAPValue> omapValues = new ArrayList<>();
    public ArrayList<FSObjectValue> fsValues = new ArrayList<>();
    public ArrayList<FSKeyValue> fsKeyValues = new ArrayList<>();

    public BTreeInfo bTreeInfo;

    public BTreeNode(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int start_of_node = buffer.position();

        btn_o = new BlockHeader(buffer);
        btn_flags = buffer.getShort();
        mapFlags();
        btn_level = buffer.getShort();
        btn_nkeys = buffer.getInt();
        btn_table_space_off = buffer.getShort();
        btn_table_space_len = buffer.getShort();
        btn_freespace_off = buffer.getShort();
        btn_freespace_len = buffer.getShort();
        btn_key_free_list_off = buffer.getShort();
        btn_key_free_list_len = buffer.getShort();
        btn_val_free_list_off = buffer.getShort();
        btn_val_free_list_len = buffer.getShort();

        // Fixed-size indicates the node contains OMAP keys & values.
        // Otherwise, it contains variable-length FS Object keys & values
        boolean isOMAP = this.btn_flags_is_fixed_KV_size;

        int toc_start = buffer.position() + btn_table_space_off;
        int toc_end = buffer.position() + btn_table_space_off + btn_table_space_len;
        buffer.position(toc_start);

        for (int keyNum = 1; keyNum <= btn_nkeys; keyNum++) {
            tableOfContents.add(new BTreeTOCEntry(buffer, isOMAP));
        }
        buffer.position(toc_end);

        // remember the key start position -- key offsets are calculated relative to this position
        int key_start_pos = buffer.position();

        // Keys
        for (BTreeTOCEntry b : tableOfContents) {
            buffer.position(key_start_pos + b.key_offset);
            if (isOMAP) {
                omapKeys.add(new OMAPKey(buffer));
            } else {
                FSObjectKey obj = FSObjectKeyFactory.get(buffer, key_start_pos + b.key_offset);
                fsKeys.add(obj);
            }
        }

        int infoSize = (btn_flags_is_root ? 40 : 0); // Only root nodes have a BTreeInfo structure
        int value_end_pos = start_of_node + 4096 - infoSize;

        for (int i = 0; i < tableOfContents.size(); i++) {
            BTreeTOCEntry entry = tableOfContents.get(i);
            int start_pos = value_end_pos - entry.value_offset;
            buffer.position(start_pos);
            if (isOMAP) {
                omapValues.add(new OMAPValue(buffer, btn_flags_is_leaf));
            } else {
                try {
                    FSObjectValue val = FSObjectValueFactory.get(buffer, fsKeys.get(i));
                    fsValues.add(val);
                } catch (Exception e) {
                    // TODO: Fix Buffer Underflow Exceptions
                    e.printStackTrace();
                    // Not sure why we're getting it, but if it happens we'll add a null value as a placeholder
                    fsValues.add(null);
                }
            }
        }

        for (int i = 0; i < fsKeys.size(); i++) {
            fsKeyValues.add(new FSKeyValue(fsKeys.get(i), fsValues.get(i)));
        }

        // Move the the start of the value area, which is just before the BTreeNodeInfo (if this node is a root node)
        if (!btn_flags_is_root) return;
        buffer.position(value_end_pos);
        bTreeInfo = new BTreeInfo(buffer);
    }

    private void mapFlags() {
        btn_flags_is_root = (btn_flags & 1) > 0;
        btn_flags_is_leaf = (btn_flags & 2) > 0;
        btn_flags_is_fixed_KV_size = (btn_flags & 4) > 0;
        btn_flags_is_hashed = (btn_flags & 8) > 0;
    }


    @Override
    public String toString() {
        return String.format("B-Tree Node\nBlock Header %s\nIs Root: %s\tIs Leaf: %s\nLevel: %s\nKey Count: %s\nTable Space Offset: %s\tTable Space Length: %s\nFreespace Offset: %s\tFreespace Length: %s\nKey Freelist Offset: %s\tKey Freelist Length: %s\nKey Value Offset: %s\tKey Value Length: %s\nTable of Contents: %s\nOMAP Keys %s\nOMAP Values %s\nB-Tree Info %s\n", btn_o, btn_flags_is_root, btn_flags_is_leaf, btn_level, btn_nkeys, btn_table_space_off, btn_table_space_len, btn_freespace_off, btn_freespace_len, btn_key_free_list_off, btn_key_free_list_len, btn_val_free_list_off, btn_val_free_list_len, tableOfContents, omapKeys, omapValues, bTreeInfo);
    }
}


// Table of Contents entry
// See APFS Reference pg. 129
class BTreeTOCEntry {
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

class BTreeInfo {
    BTreeInfoFixed bTreeInfoFixed;
    int bt_longest_key;
    int bt_longest_val;
    long bt_key_count;
    long bt_node_count;

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

class BTreeInfoFixed {
    int bt_flags;
    int bt_node_size;
    int bt_key_size;
    int bt_val_size;

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

