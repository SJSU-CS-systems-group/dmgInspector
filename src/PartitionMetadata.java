import java.util.Arrays;
import java.util.HashMap;

public class PartitionMetadata {

    private String partitionType;
    private String startHead;
    private String startSector;
    private String startCylinder;
    private String endHead;
    private String endSector;
    private String endCylinder;
    private int partitionStartSector;
    private int partitionEndSector;
    private int partitionSize;
    private static HashMap<String, String> typeHexToName = new HashMap<>() {{
        put("0c", "FAT32 LBA");
        put("83", "LINUX");
        put("00", "Empty");
    }};


    public PartitionMetadata(String[] partitionMetadata) {
        startHead = partitionMetadata[1];
        startSector = partitionMetadata[2];
        startCylinder = partitionMetadata[3];
        partitionType = typeHexToName.get(partitionMetadata[4]);
        endHead = partitionMetadata[5];
        endSector = partitionMetadata[6];
        endCylinder = partitionMetadata[7];

        partitionStartSector = Utils.hexToDecimal(Utils.hexArrayToLEHexString(Arrays.copyOfRange(partitionMetadata, 8, 12)));
        partitionEndSector = Utils.hexToDecimal(Utils.hexArrayToLEHexString(Arrays.copyOfRange(partitionMetadata, 12, 16)));
    }

    public String toString() {
        return partitionType;
    }
}
