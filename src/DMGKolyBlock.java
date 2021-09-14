import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

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
    private byte[] DataChecksum = new byte[128];      // Up to 128-bytes (32 x 4) of checksum

    public long XMLOffset;             // Offset of property list in DMG, from beginning
    public long XMLLength;             // Length of property list
    private byte[] Reserved1 = new byte[120];        // 120 reserved bytes - zeroed

    private int ChecksumType;          // Master
    private int ChecksumSize;          //  Checksum information
    private byte[] Checksum = new byte[128];          // Up to 128-bytes (32 x 4) of checksum

    private int ImageVariant;          // Commonly 1
    private long SectorCount;           // Size of DMG when expanded, in sectors

    private int reserved2;             // 0
    private int reserved3;             // 0
    private int reserved4;             // 0


    public DMGKolyBlock(byte[] dmgKolyBytes) {
        originalDmgKolyBytes = dmgKolyBytes;
        ByteBuffer buffer = ByteBuffer.wrap(dmgKolyBytes);
        buffer.order(ByteOrder.BIG_ENDIAN);


        // DMG Signature: "koly" (0x6B6F6C79)
        byte[] kolyBytes = new byte[4];
        buffer.get(kolyBytes);

        Version = buffer.getInt();
        HeaderSize = buffer.getInt();
        Flags = buffer.getInt();
        RunningDataForkOffset = buffer.getLong();
        DataForkOffset = buffer.getLong();
        DataForkLength = buffer.getLong();
        RsrcForkOffset = buffer.getLong();
        RsrcForkLength = buffer.getLong();

        SegmentNumber = buffer.getInt();
        SegmentCount = buffer.getInt();
        buffer.get(SegmentID);

        DataChecksumType = buffer.getInt();
        DataChecksumSize = buffer.getInt();
        buffer.get(DataChecksum);

        XMLOffset = buffer.getLong();
        XMLLength = buffer.getLong();
        buffer.get(Reserved1);

        ChecksumType = buffer.getInt();
        ChecksumSize = buffer.getInt();
        buffer.get(Checksum);

        ImageVariant = buffer.getInt();
        SectorCount = buffer.getLong();
        reserved2 = buffer.getInt();
        reserved3 = buffer.getInt();
        reserved4 = buffer.getInt();
    }

    @Override
    public String toString() {
        String output = "";
        output += "Version: " + Version + "\n";
        output += "HeaderSize: " + HeaderSize + "\n";
        output += "Flags: " + Flags + "\n";
        output += "RunningDataForkOffset: " + RunningDataForkOffset + "\n";
        output += "DataForkOffset: " + DataForkOffset + "\n";
        output += "DataForkLength: " + DataForkLength + "\n";
        output += "RsrcForkOffset: " + RsrcForkOffset + "\n";
        output += "RsrcForkLength: " + RsrcForkLength + "\n";
        output += "SegmentNumber: " + SegmentNumber + "\n";
        output += "SegmentCount: " + SegmentCount + "\n";
        output += "SegmentID: " + Arrays.toString(SegmentID) + "\n";
        output += "DataChecksumType: " + DataChecksumType + "\n";
        output += "DataChecksumSize: " + DataChecksumSize + "\n";
        output += "DataChecksum: " + Arrays.toString(DataChecksum) + "\n";
        output += "XMLOffset: " + XMLOffset + "\n";
        output += "XMLLength: " + XMLLength + "\n";
        // NOT PRINTING Reserved 1
        output += "ChecksumType: " + ChecksumType + "\n";
        output += "ChecksumSize: " + ChecksumSize + "\n";
        output += "Checksum: " + Arrays.toString(Checksum) + "\n";
        output += "ImageVariant: " + ImageVariant + "\n";
        output += "SectorCount: " + SectorCount + "\n";

        // NOT PRINTING Reserved 2,3,4

        return output;
    }


    private void printHexString(int input) {
        signature = String.format("%08X", input);
        System.out.println(signature);
    }
}
