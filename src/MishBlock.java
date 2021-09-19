import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MishBlock {
    private static final int BLKX_BYTES = 40;

    private int Signature;          // Magic ('mish')
    private int Version;            // Current version is 1
    private long SectorNumber;       // Starting disk sector in this blkx descriptor
    private long SectorCount;        // Number of disk sectors in this blkx descriptor

    private long DataOffset;
    private int BuffersNeeded;
    private int BlockDescriptors;   // Number of descriptors

    private int reserved1;
    private int reserved2;
    private int reserved3;
    private int reserved4;
    private int reserved5;
    private int reserved6;

    // For now, we're only decompressing zLib compression types -- 0x80000005

    // Figured this number out based on trial and error of possible byte numbers based on BLKXChunk bytes, which are 40 bytes each
    // For example, we tried 16, 56, and 96. We knew 136 because we got a valid number of block chunks (e.g. 2, 17...)
    private byte[] checksum = new byte[136];
    private int NumberOfBlockChunks;
    private BLKXChunkEntry[] blkxChunkEntries;

    public MishBlock(byte[] mishBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(mishBytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        Signature = buffer.getInt();
        Version = buffer.getInt();
        SectorNumber = buffer.getLong();
        SectorCount = buffer.getLong();

        DataOffset = buffer.getLong();
        BuffersNeeded = buffer.getInt();
        BlockDescriptors = buffer.getInt();

        reserved1 = buffer.getInt();
        reserved2 = buffer.getInt();
        reserved3 = buffer.getInt();
        reserved4 = buffer.getInt();
        reserved5 = buffer.getInt();
        reserved6 = buffer.getInt();

        buffer.get(checksum);
        NumberOfBlockChunks = buffer.getInt();

        blkxChunkEntries = new BLKXChunkEntry[NumberOfBlockChunks];
        for (int i = 0; i < NumberOfBlockChunks; i++) {
            byte[] blockBytes = new byte[BLKX_BYTES];
            buffer.get(blockBytes);
            blkxChunkEntries[i] = new BLKXChunkEntry(blockBytes);
        }

        System.out.println("MISH BLOCK ENTRY");
        System.out.println(toString());

        for (int i = 0; i < NumberOfBlockChunks; i++) {
            System.out.println("\t\t" + blkxChunkEntries[i]);
        }
//        System.out.println(Utils.OriginalBytesToHexString(everythingElse));
    }

    @Override
    public String toString() {
        return "MishBlock{" +
                "Signature=" + Signature +
                ", Version=" + Version +
                ", SectorNumber=" + SectorNumber +
                ", SectorCount=" + SectorCount +
                ", DataOffset=" + DataOffset +
                ", BuffersNeeded=" + BuffersNeeded +
                ", BlockDescriptors=" + BlockDescriptors +
//                ", reserved1=" + reserved1 +
//                ", reserved2=" + reserved2 +
//                ", reserved3=" + reserved3 +
//                ", reserved4=" + reserved4 +
//                ", reserved5=" + reserved5 +
//                ", reserved6=" + reserved6 +
//                ", checksum=" + Arrays.toString(checksum) +
                ", NumberOfBlockChunks=" + NumberOfBlockChunks +
                '}';
    }

    static class BLKXChunkEntry {
        private int EntryType;         // Compression type used or entry type (see next table)
        private int Comment;           // "+beg" or "+end", if EntryType is comment (0x7FFFFFFE). Else reserved.
        private long SectorNumber;      // Start sector of this chunk
        private long SectorCount;       // Number of sectors in this chunk
        private long CompressedOffset;  // Start of chunk in data fork
        private long CompressedLength;  // Count of bytes of chunk, in data fork

        public BLKXChunkEntry(byte[] blkxChunkBytes) {
            ByteBuffer buffer = ByteBuffer.wrap(blkxChunkBytes);
            buffer.order(ByteOrder.BIG_ENDIAN);

            EntryType = buffer.getInt();
            Comment = buffer.getInt();

            SectorNumber = buffer.getLong();
            SectorCount = buffer.getLong();
            CompressedOffset = buffer.getLong();
            CompressedLength = buffer.getLong();
        }


        @Override
        public String toString() {
            return "BLKXChunkEntry{" +
                    "EntryType=" + String.format("%08X", EntryType) +
                    ", Comment=" + Comment +
                    ", SectorNumber=" + SectorNumber +
                    ", SectorCount=" + SectorCount +
                    ", CompressedOffset=" + CompressedOffset +
                    ", CompressedLength=" + CompressedLength +
                    '}';
        }
    }


}




