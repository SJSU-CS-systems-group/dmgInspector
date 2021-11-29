import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.Inflater;


public class BLKXBlockDecompress {
    private static final int SECTOR_SIZE = 512; // not sure if this is the correct sector size.

    public static byte[] decompressBLKXBlock(ByteBuffer dataForkBuffer, MishBlock.BLKXChunkEntry block) throws Exception {
        // Don't decompress if the bytes aren't zlib compressed.
        //if (block.EntryType != 0x80000005) // hex code for zlib compression
        //    throw new Exception("Non-zLib compressed bytes with type " + String.format("%08X", block.EntryType) + ". Skipping decompression.");

        ByteBuffer compressedChunkBytesBuffer = dataForkBuffer.slice((int) block.CompressedOffset, (int) block.CompressedLength);
        byte[] compressedBytes = new byte[compressedChunkBytesBuffer.remaining()];
        compressedChunkBytesBuffer.get(compressedBytes);

        Inflater decompresser = new Inflater();
        decompresser.setInput(compressedBytes, 0, compressedBytes.length);
        int decompressedSize = (int) block.SectorCount * SECTOR_SIZE;
        byte[] result = new byte[decompressedSize];
        decompresser.inflate(result);
        decompresser.end();
        return result;
    }
}
