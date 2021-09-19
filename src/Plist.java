import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Base64;

public class Plist {

    public Plist(byte[] pListBytes, byte[] dataForkBytes) {
//        String plistXML = new String(pListBytes);
//        System.out.println(plistXML);

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = f.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(pListBytes);
            Document d = builder.parse(is);

            NodeList dataDocs = d.getElementsByTagName("data");
            int dataLen = dataDocs.getLength();

            File decompressedChunkDataFile = new File("./decompressed.txt");
            if (decompressedChunkDataFile.exists()) {
                decompressedChunkDataFile.delete();
            }
            decompressedChunkDataFile.createNewFile();
            FileWriter decompressedChunkWriter = new FileWriter(decompressedChunkDataFile.getAbsolutePath());

            for (int mishBlockIndex = 0; mishBlockIndex < dataLen; mishBlockIndex++) {
                String s = dataDocs.item(mishBlockIndex).getTextContent().replaceAll("[\\n\\s]", "");
                byte[] mishBytes = Base64.getDecoder().decode(s);
                MishBlock block = new MishBlock(mishBytes);

                // DECOMPRESS
                MishBlock.BLKXChunkEntry[] blkxChunks = block.getBlkxChunkEntries();
                if (blkxChunks.length > 0) {
                    for (int chunkIndex = 0; chunkIndex < blkxChunks.length; chunkIndex++) {
                        try {
                            decompressedChunkWriter.write(String.format("\n$$$ MISH BLOCK #%d | BLKX CHUNK #%d $$$\n", mishBlockIndex, chunkIndex));
                            String decompressedChunk = BLKXBlockDecompress.decompressBLKXBlock(dataForkBytes, blkxChunks[chunkIndex]);
                            decompressedChunkWriter.write(decompressedChunk);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            }

            decompressedChunkWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public String toString() {
        String output = "";
//        output += "Version: " + Version + "\n";
//        output += "HeaderSize: " + HeaderSize + "\n";
        return output;
    }
}
