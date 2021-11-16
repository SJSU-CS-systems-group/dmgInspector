package apfs;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// OMAP Key structure for fixed-length keys -- see APFS reference pg. 46
class BTreeKey {
    long ok_oid;
    long ok_xid;

    public BTreeKey(ByteBuffer buffer) {
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


class FSObjectKeyFactory {

    public static final int KEY_TYPE_INODE = 3;
    public static final int KEY_TYPE_EXTENT = 8;
    public static final int KEY_TYPE_DREC = 9;

    public static FSObjectKey get(ByteBuffer buffer, int start_position) {
        buffer.position(start_position);
        FSKeyHeader kh = new FSKeyHeader(buffer);
        buffer.position(start_position);
        switch ((int) kh.obj_type) {
            case KEY_TYPE_INODE:
                return new INODEKey(buffer);
            case KEY_TYPE_EXTENT:
                return new EXTENTKey(buffer);
            case KEY_TYPE_DREC:
                return new DRECKey(buffer);
            default:
                return new FSObjectKey(buffer) {
                };
        }
    }
}


// j_key_t structure from APFS reference pg. 72
class FSKeyHeader {
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
        return "FSKeyHeader{" +
                "obj_id=" + obj_id +
                ", obj_type=" + obj_type +
                '}';
    }
}

// Variable length keys are FS Objects (see page 71 of APFS Reference)
abstract class FSObjectKey {
    FSKeyHeader hdr;

    public FSObjectKey(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        hdr = new FSKeyHeader(buffer);
    }

    @Override
    public String toString() {
        return "FSObjectKey{" +
                "hdr=" + hdr +
                '}';
    }
}

// j_drec_hashed_key_t structure from APFS reference pg. 78
class DRECKey extends FSObjectKey {
    public final static int J_DREC_LEN_MASK = 0x000003ff;
    public final static int J_DREC_HASH_MASK = 0xfffff400;
    public final static int J_DREC_HASH_SHIFT = 10;

    byte[] name;
    int name_len;
    int hash;


    public DRECKey(ByteBuffer buffer) {
        super(buffer);
        int name_len_and_hash = buffer.getInt();

        name_len = name_len_and_hash & J_DREC_LEN_MASK;
        hash = (name_len_and_hash & J_DREC_HASH_MASK) >> J_DREC_HASH_SHIFT;

        name = new byte[name_len];
        buffer.get(name);
    }

    @Override
    public String toString() {
        return "DRECKey{" +
                "hdr=" + hdr +
                ", name=" + new String(name) +
                ", name_len=" + Integer.toUnsignedString(name_len) +
                ", hash=" + Integer.toUnsignedString(hash) +
                '}';
    }
}

// See object types at APFS refrence pg.84

class INODEKey extends FSObjectKey {
    public int test = 0;

    public INODEKey(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    public String toString() {
        return "INODEKey{" +
                "hdr=" + hdr +
                ", test=" + test +
                '}';
    }
}


class EXTENTKey extends FSObjectKey {
    public long logicalAddr;

    public EXTENTKey(ByteBuffer buffer) {
        super(buffer);
        logicalAddr = buffer.getLong();
    }

    @Override
    public String toString() {
        return "EXTENTKey{" +
                "hdr=" + hdr +
                "logicalAddr=" + logicalAddr +
                '}';
    }
}

// Later? (Can ignore for now since we're just interested in parsing Inodes)
// TODO: Parse x-attr (4)
// TODO: Parse dstream (4)


