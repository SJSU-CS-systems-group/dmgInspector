package apfs.kv.keys;

import java.nio.ByteBuffer;

public class FSObjectKeyFactory {

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
