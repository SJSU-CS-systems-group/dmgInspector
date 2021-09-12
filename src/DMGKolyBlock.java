import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class DMGKolyBlock {

    private byte[] originalDmgKolyBytes;
    private String signature;          // Magic ('koly')
    private int Version;               // Current version is 4
    private int HeaderSize;            // sizeof(this), always 512
    private int Flags;                 // Flags
    private long RunningDataForkOffset; //
    private long DataForkOffset;        // Data fork offset (usually 0, beginning of file)
    private long DataForkLength;        // Size of data fork (usually up to the XMLOffset, below)
    private long RsrcForkOffset;        // Resource fork offset, if any
    private long RsrcForkLength;        // Resource fork length, if any
    private int SegmentNumber;         // Usually 1, may be 0
    private int SegmentCount;          // Usually 1, may be 0
    private byte[] SegmentID = new byte[16];             // 128-bit GUID identifier of segment (if SegmentNumber !=0)

    private int DataChecksumType;      // Data fork
    private int DataChecksumSize;      //  Checksum Information
    private int[] DataChecksum = new int[32];      // Up to 128-bytes (32 x 4) of checksum

    private long XMLOffset;             // Offset of property list in DMG, from beginning
    private long XMLLength;             // Length of property list
    private byte[] Reserved1 = new byte[120];        // 120 reserved bytes - zeroed

    private int ChecksumType;          // Master
    private int ChecksumSize;          //  Checksum information
    private int[] Checksum = new int[32];          // Up to 128-bytes (32 x 4) of checksum

    private int ImageVariant;          // Commonly 1
    private long SectorCount;           // Size of DMG when expanded, in sectors

    private int reserved2;             // 0
    private int reserved3;             // 0
    private int reserved4;             // 0


    public DMGKolyBlock(byte[] dmgKolyBytes) {
        originalDmgKolyBytes = dmgKolyBytes;
        ByteBuffer buffer = ByteBuffer.wrap(dmgKolyBytes);
        buffer.order(ByteOrder.BIG_ENDIAN);
        int kolyHex = buffer.getInt();
        signature = String.format("%08X", kolyHex);
        System.out.println(signature);
    }
}
