package apfs.kv.keys;

import java.nio.ByteBuffer;

class INODEKey extends FSObjectKey {
    public int test = 0;

    public INODEKey(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    public String toString() {
        return "INODEKey{" +
                hdr +
                '}';
    }
}
