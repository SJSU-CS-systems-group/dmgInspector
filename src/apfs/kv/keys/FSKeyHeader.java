package apfs.kv.keys;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// j_key_t structure from APFS reference pg. 72
public class FSKeyHeader {
    public static final BigInteger OBJ_ID_MASK = new BigInteger("0fffffffffffffff", 16);
    public static final BigInteger OBJ_TYPE_MASK = new BigInteger("f000000000000000", 16);
    public static final int OBJ_TYPE_SHIFT = 60;
    public static final BigInteger SYSTEM_OBJ_ID_MARK = new BigInteger("0fffffff00000000", 16);

    long obj_id_and_type;
    public long obj_id;
    public long obj_type;

    public FSKeyHeader(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        obj_id_and_type = buffer.getLong();
        obj_id = OBJ_ID_MASK.and(BigInteger.valueOf(obj_id_and_type)).longValue();
        obj_type = OBJ_TYPE_MASK.and(BigInteger.valueOf(obj_id_and_type)).shiftRight(OBJ_TYPE_SHIFT).longValue();

    }


    @Override
    public String toString() {
        return "FSKeyHeader{ " +
                "obj_id=" + obj_id +
                "\tobj_type=" + obj_type + "}";
    }
}
