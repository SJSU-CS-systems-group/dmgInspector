package apfs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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


class FSObjectValueFactory{

    public static FSObjectValue get(ByteBuffer buffer, int start_position, FSObjectKey fsObjectKey){
        buffer.position(start_position);
        switch (fsObjectKey.getClass().getName()) {
            case "INODEKey":
                return new INODEValue(buffer);
            case "EXTENTKey":
                return new EXTENTValue(buffer);
            case "DRECKey":
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
    public DRECValue(ByteBuffer buffer) {
    }
}


class INODEValue implements FSObjectValue{
    public INODEValue(ByteBuffer buffer){
    }
}


class EXTENTValue implements FSObjectValue{
    public EXTENTValue(ByteBuffer buffer){
    }
}