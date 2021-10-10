import java.nio.ByteBuffer;
import java.util.zip.Inflater;


public class BLKXBlockDecompress {
    private static final int SECTOR_SIZE = 512; // not sure if this is the correct sector size.

    public static String decompressBLKXBlock(ByteBuffer dataForkBuffer, MishBlock.BLKXChunkEntry block) throws Exception {
        // Don't decompress if the bytes aren't zlib compressed.
        if (block.EntryType != 0x80000005) // hex code for zlib compression
            throw new Exception("Non-zLib compressed bytes with type " + String.format("%08X", block.EntryType) + ". Skipping decompression.");

        System.out.println(block);

        ByteBuffer compressedChunkBytesBuffer = dataForkBuffer.slice((int) block.CompressedOffset, (int) block.CompressedLength);
        System.out.println(compressedChunkBytesBuffer.remaining());
        byte[] compressedBytes = new byte[compressedChunkBytesBuffer.remaining()];
        compressedChunkBytesBuffer.get(compressedBytes);

        Inflater decompresser = new Inflater();
        decompresser.setInput(compressedBytes, 0, compressedBytes.length);
        byte[] result = new byte[(int) block.SectorCount * SECTOR_SIZE];
        System.out.println(result.length);
        int resultLength = decompresser.inflate(result);
        System.out.println(resultLength);
        decompresser.end();
        String outputString = new String(result, 0, resultLength, "UTF-8");
        return outputString;
    }
}
