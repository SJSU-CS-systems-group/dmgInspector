package apfs;

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


// Variable length keys are FS Objects (see page 71 of APFS Reference)
abstract class FSObjectKey {
    FSKeyHeader hdr;

    public FSObjectKey(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        hdr = new FSKeyHeader(buffer);
        System.out.println(hdr);
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

        System.out.println(name_len);
        System.out.println(hash);

        name = new byte[name_len];
        buffer.get(name);
    }

    @Override
    public String toString() {
        return "DRECKey{" +
                "name=" + new String(name) +
                ", name_len=" + Integer.toUnsignedString(name_len) +
                ", hash=" + Integer.toUnsignedString(hash) +
                '}';
    }
}

// See object types at APFS refrence pg.84
// TODO: Parse inode key (3)
// TODO: Parse extent key (8)

// Later? (Can ignore for now since we're just interested in parsing Inodes)
// TODO: Parse x-attr (4)
// TODO: Parse dstream (4)


