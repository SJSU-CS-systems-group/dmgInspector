package apfs;

import utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

public class BTreeNode {
    public static final int BTREE_KEY_LENGTH = 8;
    public static final int BTREE_TOC_LENGTH = 8;
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
    public ArrayList bTreeTOC = new ArrayList();
    // paddr_t of omap_val_t of BTree root node
    public long volume_superb_offset;

    public BTreeNode(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        // TODO: Object header being stored before the BTreeNode object being called
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

        buffer.position(buffer.position() + btn_table_space_off);
        for(int i=0;i<btn_table_space_len/BTREE_TOC_LENGTH; i++){
            bTreeTOC.add(new BTreeTOCEntry(buffer));
        }
        System.out.println(bTreeTOC);

        //int btn_info_t_pos = btn_freespace_off + btn_freespace_len + btn_val_free_list_off + btn_val_free_list_len;
        //int btn_info_t_pos = (btn_nkeys * key_length) + btn_key_free_list_len + btn_freespace_len + btn_val_free_list_len + (btn_nkeys * value_length)
        int btn_info_t_pos = (btn_nkeys * 16) + btn_key_free_list_len + btn_freespace_len + btn_val_free_list_len + (btn_nkeys * 16);
        BTreeKey bTreeKey = new BTreeKey(buffer);
        System.out.println(bTreeKey);
        //System.out.println(Utils.OriginalBytesToHexString(key));
        // Setting buffer position to start of btree_info_t
        //buffer.position(buffer.position()+ btn_info_t_pos - (btn_nkeys * key_length) + (btn_nkeys * value_length));
        buffer.position(buffer.position()+ btn_info_t_pos - ((btn_nkeys * 16) + (btn_nkeys * 16)));
        BTreeValue bTreeValue = new BTreeValue(buffer);
        System.out.println(bTreeValue);
        BTreeInfo bTreeInfo = new BTreeInfo(buffer);
        System.out.println(bTreeInfo);
        volume_superb_offset = bTreeValue.paddr_t;



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
                '}';
    }
}


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

