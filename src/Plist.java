import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Base64;

public class Plist {

    public Plist(ByteBuffer pListBuffer, ByteBuffer dataForkBuffer) {
//        String plistXML = new String(pListBuffer.array());
//        System.out.println(plistXML);
        System.out.println(pListBuffer.arrayOffset());
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = f.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(pListBuffer.array());
            Document d = builder.parse(is);

            NodeList dataDocs = d.getElementsByTagName("data");
            int dataLen = dataDocs.getLength();

            File decompressedOutputFolder = new File("./output");
            if (decompressedOutputFolder.exists()) {
                decompressedOutputFolder.delete();
            }
            decompressedOutputFolder.mkdir();

            for (int mishBlockIndex = 0; mishBlockIndex < dataLen; mishBlockIndex++) {
                String s = dataDocs.item(mishBlockIndex).getTextContent().replaceAll("[\\n\\s]", "");
                byte[] mishBytes = Base64.getDecoder().decode(s);
                MishBlock block = new MishBlock(mishBytes);

                // DECOMPRESS
                MishBlock.BLKXChunkEntry[] blkxChunks = block.getBlkxChunkEntries();

                File decompressedMishFile = new File("./output/decompressed" + mishBlockIndex );
                FileWriter decompressedChunkWriter = new FileWriter(decompressedMishFile.getAbsolutePath());

                if (blkxChunks.length > 0) {
                    for (int chunkIndex = 0; chunkIndex < blkxChunks.length; chunkIndex++) {
                        try {
                            String decompressedChunk = BLKXBlockDecompress.decompressBLKXBlock(dataForkBuffer, blkxChunks[chunkIndex]);
                            decompressedChunkWriter.write(decompressedChunk);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }

                decompressedChunkWriter.close();
            }

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
