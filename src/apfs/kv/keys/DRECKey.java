package apfs.kv.keys;

import java.nio.ByteBuffer;

// j_drec_hashed_key_t structure from APFS reference pg. 78
public class DRECKey extends FSObjectKey {
    public final static int J_DREC_LEN_MASK = 0x000003ff;
    public final static int J_DREC_HASH_MASK = 0xfffff400;
    public final static int J_DREC_HASH_SHIFT = 10;

    public byte[] name;
    public int name_len;
    public int hash;


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
