import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MBRPartitionTable {
    private static final int SECTOR_SIZE = 512;
    private static final int PARTITION_TABLE_BYTES = 64;
    private static final int PARTITION_COUNT = 4;
    private static final int PARTITION_BYTES = 16;
    private static final int BOOT_SIGNATURE_BYTES = 2;
    private static final int EBR_PARTITION_COUNT = 2;
    public List<MBRPartitionEntry> partitionEntries;

    private MBRPartitionTable(String imgPath) throws IOException {
        partitionEntries = getPartitionEntries(imgPath);
    }

    /**
     * @param imgPath The image to read bytes from
     * @return List of MBR and EBR partition entries
     * @throws IOException
     */
    private static ArrayList<MBRPartitionEntry> getPartitionEntries(String imgPath) throws IOException {
        // Partition Entries start as just the four primary partitions of the MBR.
        ArrayList<MBRPartitionEntry> partitionEntries = getMBRPartitionEntries(imgPath);

        // If one of the primary partitions is an extended partition, we append its EBRs to the list of partition entries.
        ArrayList<MBRPartitionEntry> ebrPartitionEntries = null;
        for (MBRPartitionEntry e : partitionEntries) {
            if (e.isExtended()) {
                ebrPartitionEntries = getEBRPartitionEntries(imgPath, e.start);
                break; // We can break since only one primary partition can be extended.
            }
        }

        if (ebrPartitionEntries != null) {
            for (MBRPartitionEntry e : ebrPartitionEntries) {
                partitionEntries.add(e);
            }
        }

        return partitionEntries;
    }

    /**
     * @param imgPath The image to read the MBR bytes from
     * @return List of MBR Partition entries
     * @throws IOException
     */
    private static ArrayList<MBRPartitionEntry> getMBRPartitionEntries(String imgPath) throws IOException {
        byte[] mbrBytes = Utils.getImageBytes(imgPath, SECTOR_SIZE);
        int partitionStart = SECTOR_SIZE - PARTITION_TABLE_BYTES - BOOT_SIGNATURE_BYTES;
        int partitionEnd = SECTOR_SIZE - BOOT_SIGNATURE_BYTES;

        byte[] mbrPartitionBytes = Arrays.copyOfRange(mbrBytes, partitionStart, partitionEnd);

        ArrayList<MBRPartitionEntry> mbrPartitionEntries = new ArrayList<>();
        for (int i = 0; i < PARTITION_COUNT; i++) {
            byte[] entryBytes = Arrays.copyOfRange(mbrPartitionBytes, i * PARTITION_BYTES, i * PARTITION_BYTES + PARTITION_BYTES);
            MBRPartitionEntry entry = new MBRPartitionEntry(entryBytes);
            mbrPartitionEntries.add(entry);
        }

        return mbrPartitionEntries;
    }

    /**
     * @param imgPath The image to read the EBR bytes from
     * @return List of EBR Partition entries
     * @throws IOException
     */
    private static ArrayList<MBRPartitionEntry> getEBRPartitionEntries(String imgPath, int startSector) throws IOException {
        ArrayList<MBRPartitionEntry> ebrPartitionEntries = new ArrayList<>();

        // Each EBR's first partition has a partition type
        // Their second partition is either an extended partition or unused -- we'll follow the chain of EBRs until there are no more extended partitions to follow.
        MBRPartitionEntry[] ebrPartitions = getEBRPartitions(imgPath, startSector);
        ebrPartitionEntries.add(ebrPartitions[0]);

        while (ebrPartitions[1].isExtended()) {
            ebrPartitions = getEBRPartitions(imgPath, ebrPartitions[1].start);
            ebrPartitionEntries.add(ebrPartitions[0]);
        }

        return ebrPartitionEntries;
    }

    /**
     * @param imgPath     The image to read the EBR bytes from
     * @param startSector sector at which the EBR starts
     * @return An array containing the first and second EBR partitions
     * @throws IOException
     */
    private static MBRPartitionEntry[] getEBRPartitions(String imgPath, int startSector) throws IOException {
        int extPartOffset = startSector * SECTOR_SIZE;
        int partitionStart = extPartOffset + SECTOR_SIZE - PARTITION_TABLE_BYTES - BOOT_SIGNATURE_BYTES;
        int partitionEnd = extPartOffset + SECTOR_SIZE - BOOT_SIGNATURE_BYTES;
        byte[] ebrBytes = Utils.getImageBytes(imgPath, startSector * SECTOR_SIZE + SECTOR_SIZE);
        byte[] ebrPartBytes = Arrays.copyOfRange(ebrBytes, partitionStart, partitionEnd);

        // First EBR Partition has a partition type (e.g. Linux)
        // Second EBR Partition is either an Extended Partition or Unused.
        MBRPartitionEntry[] ebrPartitions = new MBRPartitionEntry[EBR_PARTITION_COUNT];
        for (int i = 0; i < EBR_PARTITION_COUNT; i++) {
            byte[] entryBytes = Arrays.copyOfRange(ebrPartBytes, i * PARTITION_BYTES, i * PARTITION_BYTES + PARTITION_BYTES);
            ebrPartitions[i] = new MBRPartitionEntry(entryBytes, startSector);
        }

        return ebrPartitions;
    }


    public static MBRPartitionTable parseImage(String imgPath) throws IOException {
        return new MBRPartitionTable(imgPath);
    }

    public void print(String imageName) {
        System.out.printf("%-45s %-10s %-10s %-10s %-10s %-10s %-10s %n", "Partition", "Boot", "Start", "End", "Sectors", "Size", "Type");
        for (int i = 0; i < partitionEntries.size(); i++) {
            System.out.printf("%-45s %s", imageName + (i + 1), partitionEntries.get(i).toString());
        }
    }

    public void printPartitionEntriesAsHex() {
        for (MBRPartitionEntry entry : partitionEntries) {
            System.out.println(OriginalBytesToHexString(entry.originalBytes));
        }
    }

    private String OriginalBytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}


