package apfs.kv.values;

import utils.Utils;

import java.nio.ByteBuffer;

public class INODEValue implements FSObjectValue {
    public long parentId;
    public long privateId;
    public long createTime;
    public long modTime;
    public long changeTime;
    public long accessTime;
    public long internalFlags;
    public int nChildren;
    public int nLink;
    public int cpKeyClasst;
    public int writeGenerationCounter;
    public int bsdFlags;
    public int uid_t;
    public int gid_t;
    public short mode_t;
    public short pad1;
    public long uncompressedSize;
    public byte xfields;

    public INODEValue(ByteBuffer buffer) {
        parentId = buffer.getLong();
        privateId = buffer.getLong();
        createTime = buffer.getLong();
        modTime = buffer.getLong();
        changeTime = buffer.getLong();
        accessTime = buffer.getLong();
        internalFlags = buffer.getLong();
        nChildren = buffer.getInt();
        nLink = buffer.getInt();
        cpKeyClasst = buffer.getInt();
        writeGenerationCounter = buffer.getInt();
        bsdFlags = buffer.getInt();
        uid_t = buffer.getInt();
        gid_t = buffer.getInt();
        mode_t = buffer.getShort();
        pad1 = buffer.getShort();
        uncompressedSize = buffer.getLong();
        xfields = buffer.get();

    }

    @Override
    public String toString() {
        return "INODEValue{" +
                "Parent Id=" + parentId +
                "\tPrivate Id=" + privateId +
                "\tCreate Time=" + Utils.nanoEpochToDateTime(createTime) +
//                ", modTime=" + Utils.nanoEpochToDateTime(modTime) +
//                ", changeTime=" + Utils.nanoEpochToDateTime(changeTime) +
                "\tAccess Time=" + Utils.nanoEpochToDateTime(accessTime) +
//                ", internalFlags=" + internalFlags +
                "\tChildren=" + nChildren +
//                ", nLink=" + nLink +
//                ", cpKeyClasst=" + cpKeyClasst +
//                ", writeGenerationCounter=" + writeGenerationCounter +
//                ", bsdFlags=" + bsdFlags +
//                ", uid_t=" + uid_t +
//                ", gid_t=" + gid_t +
//                ", mode_t=" + mode_t +
//                ", pad1=" + pad1 +
                "\tuncompressedSize=" + uncompressedSize +
//                ", xfields=" + xfields +
                '}';
    }
}
