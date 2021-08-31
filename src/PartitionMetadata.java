import java.util.Arrays;
import java.util.HashMap;

public class PartitionMetadata {

    private String id;
    private String type;
    private String boot;
    private int start;
    private int end;
    private int sectors;
    private int size;
    private static HashMap<String, String> typeHexToName = new HashMap<>() {{
        put("0c", "FAT32 LBA");
        put("83", "LINUX");
        put("00", "Empty");
    }};


    public PartitionMetadata(String[] partitionMetadata) {
        boot = partitionMetadata[0];
        id = partitionMetadata[4];
        type = typeHexToName.get(partitionMetadata[4]);
        start = Utils.hexToDecimal(Utils.hexArrayToLEHexString(Arrays.copyOfRange(partitionMetadata, 8, 12)));
        sectors = Utils.hexToDecimal(Utils.hexArrayToLEHexString(Arrays.copyOfRange(partitionMetadata, 12, 16)));
        end = start + sectors - 1;
        size = getPartitionSize(sectors);
    }

    public String toString() {
        return String.format("%-10s %-10s %-10s %-10s %-10s %-10s %n", boot, start, end, sectors, size+"M", type);
    }

    private int getPartitionSize(int sectors) {
        return (sectors * 512) / 1000000;
    }
}
