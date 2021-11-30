package apfs.kv.values;

import apfs.kv.keys.FSObjectKey;

import java.nio.ByteBuffer;

public class FSObjectValueFactory {

    public static final int OBJ_TYPE_INODE = 3;
    public static final int OBJ_TYPE_EXTENT = 8;
    public static final int OBJ_TYPE_DREC = 9;

    public static FSObjectValue get(ByteBuffer buffer, FSObjectKey fsObjectKey) {
        switch ((int) fsObjectKey.hdr.obj_type) {
            case OBJ_TYPE_INODE:
                return new INODEValue(buffer);
            case OBJ_TYPE_EXTENT:
                return new EXTENTValue(buffer);
            case OBJ_TYPE_DREC:
                return new DRECValue(buffer);
            default:
                return null;
        }
    }
}
