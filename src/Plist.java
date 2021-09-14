import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Plist {

    public byte[] originalPlistBytes;

    public Plist(byte[] pListBytes) {
        originalPlistBytes = pListBytes;
        System.out.println(new String(pListBytes));
    }

    @Override
    public String toString() {
        String output = "";
//        output += "Version: " + Version + "\n";
//        output += "HeaderSize: " + HeaderSize + "\n";
        return output;
    }
}
