package apfs.kv.keys;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// Variable length keys are FS Objects (see page 71 of APFS Reference)
public abstract class FSObjectKey {
    public FSKeyHeader hdr;

    public FSObjectKey(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        hdr = new FSKeyHeader(buffer);
    }

    @Override
    public String toString() {
        return "FSObjectKey{" +
                hdr +
                '}';
    }
}