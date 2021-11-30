package apfs.kv.values;

import utils.Utils;

import java.nio.ByteBuffer;

public class DRECValue implements FSObjectValue {
    public long fileId;
    public long dateAdded;
    public short flags;
    public byte xfields;

    public DRECValue(ByteBuffer buffer) {
        fileId = buffer.getLong();
        dateAdded = buffer.getLong();
        flags = buffer.getShort();
        xfields = buffer.get();
    }

    @Override
    public String toString() {
        return "DRECValue{" +
                "fileId=" + fileId +
                "\tdateAdded=" + Utils.nanoEpochToDateTime(dateAdded) +
                '}';
    }
}
