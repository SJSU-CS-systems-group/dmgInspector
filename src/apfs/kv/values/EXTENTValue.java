package apfs.kv.values;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class EXTENTValue implements FSObjectValue {
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
        return String.format("{Length: %s, Physical Block Number: %s}", length, physBlockNum);
        /*return "EXTENTValue{" +
                "lenAndKind=" + lenAndKind +
                ", length=" + length +
                ", physBlockNum=" + physBlockNum +
                ", cryptoId=" + cryptoId +
                '}';*/
    }
}
