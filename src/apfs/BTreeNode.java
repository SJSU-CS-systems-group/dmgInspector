package apfs;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;

public class BTreeNode {

    public static final int BTREE_KEY_LENGTH = 8;
    public static final int BTREE_TOC_LENGTH = 8;
    public static final int BTREE_VALUE_LENGTH = 16;

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
    public ArrayList<FSObjectKey> fsObjectKeys = new ArrayList<>();
    public ArrayList<FSObjectValue> fsObjectValues = new ArrayList<>();
    public ArrayList<BTreeValue> bTreeValues = new ArrayList<BTreeValue>();


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

        buffer.position(buffer.position() + btn_table_space_off);
        for (int i = 0; i < btn_table_space_len / BTREE_TOC_LENGTH; i++) {
            BTreeTOCEntry entry = new BTreeTOCEntry(buffer);
            if (i < btn_nkeys)
                bTreeTOC.add(entry);
        }

        // remember the key start position -- key offsets are calculated relative to this position
        int key_start_pos = buffer.position();
        System.out.println(key_start_pos);
        for (BTreeTOCEntry b : bTreeTOC) {
            buffer.position(key_start_pos + b.key_offset);
            bTreeKeys.add(new BTreeKey(buffer));

            FSObjectKey obj = FSObjectKeyFactory.get(buffer, key_start_pos + b.key_offset);
            //System.out.println(obj);
            fsObjectKeys.add(obj);
        }

        // TODO: 4096 skip to track values area
//        for (BTreeTOCEntry b : bTreeTOC) {
//            // TODO: Are we using value length properly? e.g. some values have length 16 while others might have 32.... FIX ME
//            buffer.position(value_start_pos - b.value_offset - BTREE_VALUE_LENGTH);
//            bTreeValues.add(new BTreeValue(buffer));
//            buffer.position(value_start_pos - b.value_offset - BTREE_VALUE_LENGTH);
//            byte[] bytes = new byte[b.value_length];
//            buffer.get(bytes);
////            System.out.println(Utils.OriginalBytesToHexString(bytes));
//        }
//        Collections.reverse(bTreeValues);

        int value_start_pos = start_of_node + 4096 - 40;
        for(int i=0;i<bTreeTOC.size();i++){
            int start_pos = value_start_pos - bTreeTOC.get(i).value_offset - bTreeTOC.get(i).value_length;
            buffer.position(start_pos);
            bTreeValues.add(new BTreeValue(buffer));
            buffer.position(start_pos);
            FSObjectValue val = FSObjectValueFactory.get(buffer, start_pos, fsObjectKeys.get(i));
            fsObjectValues.add(val);
        }


        buffer.position(value_start_pos);
        BTreeInfo bTreeInfo = new BTreeInfo(buffer);
    }

    private void mapFlags() {
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
                ", fsObjectKeys=" + fsObjectKeys +
                ", fsObjectValues=" + fsObjectValues +
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

    public BTreeTOCEntry(ByteBuffer buffer) {
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


// Variable-length key FS Objects from APFS reference pg. 71
interface FSObject {

}

// j_key_t structure from APFS reference pg. 72
class FSKeyHeader {
    public static final BigInteger OBJ_ID_MASK = new BigInteger("0fffffffffffffff", 16);
    public static final BigInteger OBJ_TYPE_MASK = new BigInteger("f000000000000000", 16);
    public static final int OBJ_TYPE_SHIFT = 60;
    public static final BigInteger SYSTEM_OBJ_ID_MARK = new BigInteger("0fffffff00000000", 16);

    long obj_id_and_type;
    long obj_type;

    public FSKeyHeader(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        obj_id_and_type = buffer.getLong();
        obj_type = OBJ_TYPE_MASK.and(BigInteger.valueOf(obj_id_and_type)).shiftRight(OBJ_TYPE_SHIFT).longValue();
    }

    @Override
    public String toString() {
        return "FSKeyHeader{" +
                "obj_id_and_type=" + Long.toUnsignedString(obj_id_and_type) +
                ", obj_type=" + obj_type +
                '}';
    }
}


// TODO: Parse variable-length values (similar to how we're parsing variable-length BTreeKeys)
// ...


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

    public BTreeInfoFixed(ByteBuffer buffer) {
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

