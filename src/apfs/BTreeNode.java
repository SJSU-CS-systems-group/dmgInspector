package apfs;

import utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;

public class BTreeNode {

    public static final int BTREE_KEY_LENGTH = 8;
    public static final int BTREE_TOC_LENGTH = 8;

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
    public ArrayList<BTreeTOCEntry> bTreeTOC = new ArrayList<BTreeTOCEntry>();
    public ArrayList<BTreeKey> bTreeKeys = new ArrayList<BTreeKey>();
    public ArrayList<BTreeValue> bTreeValues = new ArrayList<BTreeValue>();


    public BTreeNode(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
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

        buffer.position(buffer.position() + btn_table_space_off);
        for(int i=0;i<btn_table_space_len/BTREE_TOC_LENGTH; i++){
            if (i<btn_nkeys)
                bTreeTOC.add(new BTreeTOCEntry(buffer));
            else
                new BTreeTOCEntry(buffer);
        }
        System.out.println(bTreeTOC);

        int btn_info_t_pos = (btn_nkeys * 16) + btn_key_free_list_len + btn_freespace_len + btn_val_free_list_len + (btn_nkeys * 16);
        //BTreeKey bTreeKey = new BTreeKey(buffer);
        //System.out.println(bTreeKey);
         //Setting buffer position to start of btree_info_t
//        buffer.position(buffer.position()+ btn_info_t_pos - ((btn_nkeys * 16) + (btn_nkeys * 16)));
//        BTreeValue bTreeValue = new BTreeValue(buffer);
//        System.out.println(bTreeValue);
//        BTreeInfo bTreeInfo = new BTreeInfo(buffer);
//        System.out.println(bTreeInfo);
        //first_value_offset = bTreeValue.paddr_t;
        for(BTreeTOCEntry b : bTreeTOC){
            System.out.println(b.key_offset+ ", "+ b.key_length);
            buffer.position(buffer.position() + b.key_offset);
            bTreeKeys.add(new BTreeKey(buffer));
        }
        // add key free list space
        buffer.position(buffer.position() + btn_key_free_list_len + btn_freespace_len + btn_val_free_list_len);
        for(int i=bTreeTOC.size()-1; i>=0; i--){
            BTreeTOCEntry b = bTreeTOC.get(i);
            System.out.println(b.value_offset+ ", "+ b.value_length);
            bTreeValues.add(new BTreeValue(buffer));
            buffer.position(buffer.position() + b.value_offset);
        }
        Collections.reverse(bTreeValues);





    }

    private void mapFlags(){
        btn_flags_is_root = (btn_flags & 1) > 0;
        btn_flags_is_leaf = (btn_flags & 2) > 0;
        btn_flags_is_fixed_KV_size = (btn_flags & 4) > 0;
        btn_flags_is_hashed = (btn_flags & 8) > 0;
    }

    @Override
    public String toString() {
        return "BTreeNode{" +
                "btn_o=" + btn_o +
                ", btn_flags=" + btn_flags +
                ", btn_flags_is_fixed_KV_size=" + btn_flags_is_fixed_KV_size +
                ", btn_flags_is_root=" + btn_flags_is_root +
                ", btn_flags_is_leaf=" + btn_flags_is_leaf +
                ", btn_flags_is_hashed=" + btn_flags_is_hashed +
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
                ", bTreeKeys=" + bTreeKeys +
                ", bTreeValues=" + bTreeValues +
                '}';
    }
}

/*
interface BTreeTOCEntry {

}

class BTreeTOCEntryV implements BTreeTOCEntry{

    public short key_offset;
    public short key_length;
    public short value_offset;
    public short value_length;

    public BTreeTOCEntryV(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        key_offset = buffer.getShort();
        key_length = buffer.getShort();
        value_offset = buffer.getShort();
        value_length = buffer.getShort();
    }
    interface BTreeTOCEntry {

}

class BTreeTOCEntryV implements BTreeTOCEntry{

    public short key_offset;
    public short key_length;
    public short value_offset;
    public short value_length;

    public BTreeTOCEntryV(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        key_offset = buffer.getShort();
        key_length = buffer.getShort();
        value_offset = buffer.getShort();
        value_length = buffer.getShort();
    }
*/


class BTreeTOCEntry {

    public short key_offset;
    public short key_length;
    public short value_offset;
    public short value_length;

    public BTreeTOCEntry(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        key_offset = buffer.getShort();
        key_length = buffer.getShort();
        value_offset = buffer.getShort();
        value_length = buffer.getShort();
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


//class BTreeKey{
//    byte[] obj_id = new byte[7];
//    byte entry_kind;
//
//    public BTreeKey(ByteBuffer buffer){
//        buffer.order(ByteOrder.LITTLE_ENDIAN);
//        buffer.get(obj_id);
//        entry_kind = buffer.get();
//    }
//
//    @Override
//    public String toString() {
//        return "BTreeKey{" +
//                "obj_id=" + Arrays.toString(obj_id) +
//                ", entry_kind=" + String.format("%02X", entry_kind) +
//                '}';
//    }
//}

class BTreeKey{
    long ok_oid;
    long ok_xid;

    public BTreeKey(ByteBuffer buffer){
        ok_oid = buffer.getLong();
        ok_xid = buffer.getLong();
    }

    @Override
    public String toString() {
        return "BTreeKey{" +
                "ok_oid=" + ok_oid +
                ", ok_xid=" + ok_xid +
                '}';
    }
}

class BTreeValue{
    int ov_flags;
    int ov_size;
    long paddr_t;

    public BTreeValue(ByteBuffer buffer){
        ov_flags = buffer.getInt();
        ov_size = buffer.getInt();
        paddr_t = buffer.getLong();
    }

    @Override
    public String toString() {
        return "BTreeValue{" +
                "ov_flags=" + ov_flags +
                ", ov_size=" + ov_size +
                ", paddr_t=" + paddr_t +
                '}';
    }
}


class BTreeInfo{
    BTreeInfoFixed bTreeInfoFixed;
    int bt_longest_key;
    int bt_longest_val;
    long bt_key_count;
    long bt_node_count;

    public BTreeInfo(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        bTreeInfoFixed = new BTreeInfoFixed(buffer);
        bt_longest_key = buffer.getInt();
        bt_longest_val = buffer.getInt();
        bt_key_count = buffer.getLong();
        bt_node_count = buffer.getLong();
    }

    @Override
    public String toString() {
        return "BTreeInfo{" +
                "bTreeInfoFixed=" + bTreeInfoFixed +
                ", bt_longest_key=" + bt_longest_key +
                ", bt_longest_val=" + bt_longest_val +
                ", bt_key_count=" + bt_key_count +
                ", bt_node_count=" + bt_node_count +
                '}';
    }
}

class BTreeInfoFixed {
    int bt_flags;
    int bt_node_size;
    int bt_key_size;
    int bt_val_size;

    public BTreeInfoFixed(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        bt_flags = buffer.getInt();
        bt_node_size = buffer.getInt();
        bt_key_size = buffer.getInt();
        bt_val_size = buffer.getInt();
    }

    @Override
    public String toString() {
        return "BTreeInfoFixed{" +
                "bt_flags=" + bt_flags +
                ", bt_node_size=" + bt_node_size +
                ", bt_key_size=" + bt_key_size +
                ", bt_val_size=" + bt_val_size +
                '}';
    }
}

