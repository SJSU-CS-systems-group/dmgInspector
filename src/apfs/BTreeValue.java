package apfs;

import utils.Utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class BTreeValue {
    int ov_flags;
    int ov_size;
    long paddr_t;

    public BTreeValue(ByteBuffer buffer) {
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


class FSObjectValueFactory {

    public static FSObjectValue get(ByteBuffer buffer, int start_position, FSObjectKey fsObjectKey) {
        buffer.position(start_position);
        switch ((int) fsObjectKey.hdr.obj_type) {
            case 3:
                return new INODEValue(buffer);
            case 8:
                return new EXTENTValue(buffer);
            case 9:
                return new DRECValue(buffer);
            default:
                return null;
        }
    }
}


// Variable length keys are FS Objects (see page 71 of APFS Reference)
interface FSObjectValue {
}

class DRECValue implements FSObjectValue {
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
                ", dateAdded=" + Utils.nanoEpochToDateTime(dateAdded) +
                ", flags=" + flags +
                ", xfields=" + xfields +
                '}';
    }
}


class INODEValue implements FSObjectValue {
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
                "parentId=" + parentId +
                ", privateId=" + privateId +
                ", createTime=" + Utils.nanoEpochToDateTime(createTime) +
                ", modTime=" + Utils.nanoEpochToDateTime(modTime) +
                ", changeTime=" + Utils.nanoEpochToDateTime(changeTime) +
                ", accessTime=" + Utils.nanoEpochToDateTime(accessTime) +
                ", internalFlags=" + internalFlags +
                ", nChildren=" + nChildren +
                ", nLink=" + nLink +
                ", cpKeyClasst=" + cpKeyClasst +
                ", writeGenerationCounter=" + writeGenerationCounter +
                ", bsdFlags=" + bsdFlags +
                ", uid_t=" + uid_t +
                ", gid_t=" + gid_t +
                ", mode_t=" + mode_t +
                ", pad1=" + pad1 +
                ", uncompressedSize=" + uncompressedSize +
                ", xfields=" + xfields +
                '}';
    }
}


class EXTENTValue implements FSObjectValue {
    public long lenAndKind;
    public long length;
    public long physBlockNum;
    public long cryptoId;
    public static final BigInteger J_FILE_EXTENT_LEN_MASK = new BigInteger("00ffffffffffffff", 16);
    public static final BigInteger J_FILE_EXTENT_FLAG_MASK = new BigInteger("ff00000000000000", 16);
    public static final int J_FILE_EXTENT_FLAG_SHIFT = 56;

    public EXTENTValue(ByteBuffer buffer) {
        lenAndKind = buffer.getLong();
        length = J_FILE_EXTENT_LEN_MASK.and(BigInteger.valueOf(lenAndKind)).longValue();
        physBlockNum = buffer.getLong();
        cryptoId = buffer.getLong();
    }

    @Override
    public String toString() {
        return "EXTENTValue{" +
                "lenAndKind=" + lenAndKind +
                ", length=" + length +
                ", physBlockNum=" + physBlockNum +
                ", cryptoId=" + cryptoId +
                '}';
    }
}