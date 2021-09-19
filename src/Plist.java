import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;

public class Plist {

    public byte[] originalPlistBytes;

    public Plist(byte[] pListBytes) {
        originalPlistBytes = pListBytes;
        String plistXML = new String(pListBytes);
//        System.out.println(plistXML);

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = f.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(pListBytes);
            Document d = builder.parse(is);
//            d.getDocumentElement().normalize();

            NodeList dataDocs = d.getElementsByTagName("data");
            int dataLen = dataDocs.getLength();
//            String[] data = new String[dataLen];
            for (int i = 0; i < dataLen; i++) {
                // Todo: Parse data from each data entry's bytes

//                data[i] = dataDocs.item(i).getTextContent();
                String s = dataDocs.item(i).getTextContent().replaceAll("[\\n\\s]", "");
                byte[] mishBytes = Base64.getDecoder().decode(s);
               MishBlock block = new MishBlock(mishBytes);
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
