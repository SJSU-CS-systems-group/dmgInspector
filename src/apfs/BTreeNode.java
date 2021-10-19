package apfs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class BTreeNode {
    public static final int BTREE_KEY_LENGTH = 8;
    public short btn_flags;
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
    public BTreeTOC bTreeTOC;
    public BTreeKey[] bTreeKeys;

    public BTreeNode(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        btn_flags = buffer.getShort();
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
        bTreeTOC = new BTreeTOC(buffer);
        buffer.position(buffer.position()+btn_key_free_list_len);
        int numBTreeNodeKeys = bTreeTOC.key_length/BTREE_KEY_LENGTH;
        bTreeKeys = new BTreeKey[numBTreeNodeKeys];
        for (int i = 0; i < numBTreeNodeKeys; i++) {
            bTreeKeys[i] = new BTreeKey(buffer);
        }
    }

    @Override
    public String toString() {
        return "BTreeNode{" +
                "btn_flags=" + btn_flags +
                ", btn_level=" + btn_level +
                ", btn_nkeys=" + btn_nkeys +
                ", btn_table_space_off=" + btn_table_space_off +
                ", btn_table_space_len=" + btn_table_space_len +
                ", btn_freespace_off=" + btn_freespace_off +
                ", btn_freespace_len=" + btn_freespace_len +
                ", btn_key_free_list_off=" + btn_key_free_list_off +
                ", btn_key_free_list_len=" + btn_key_free_list_len +
                ", btn_val_free_list_off=" + btn_val_free_list_off +
                ", btn_val_free_list_len=" + btn_val_free_list_len +
                ", bTreeTOC=" + bTreeTOC +
                ", bTreeKeys=" + Arrays.toString(bTreeKeys) +
                '}';
    }
}


class BTreeTOC {

    public short key_offset;
    public short key_length;
    public short value_offset;
    public short value_length;

    public BTreeTOC(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        key_offset = buffer.getShort();
        key_length = buffer.getShort();
        value_offset = buffer.getShort();
        value_length = buffer.getShort();
    }

    @Override
    public String toString() {
        return "BTreeTOC{" +
                "key_offset=" + key_offset +
                ", key_length=" + key_length +
                ", value_offset=" + value_offset +
                ", value_length=" + value_length +
                '}';
    }
}


class BTreeKey{
    byte[] obj_id = new byte[7];
    byte entry_kind;

    public BTreeKey(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.get(obj_id);
        entry_kind = buffer.get();
    }

    @Override
    public String toString() {
        return "BTreeKey{" +
                "obj_id=" + Arrays.toString(obj_id) +
                ", entry_kind=" + String.format("%02X", entry_kind) +
                '}';
    }
}