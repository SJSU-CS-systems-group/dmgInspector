import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {

    private static final String IMAGE_PATH = "src/images/2021-05-07-raspios-buster-armhf-lite.img";
    private static final int MBR_BYTES = 512; // MBR is in the first 512 bytes of the image
    private static final int PARTITION_COUNT = 4;
    private static final int PARTITION_TABLE_BYTES = 64;
    private static final int PARTITION_BYTES = 16;
    private static String[] mbrHexValues = {};
    private static String[] mbrPartitionTableHexValues = {};
    private static List<PartitionMetadata> partitionData = new ArrayList<>();

    /*
     * Reading MBR Partitions:
     *  1. [X] Read the MBR bytes as Hex values.
     *  2. [] Map each hex value to its partition value (e.g. 0x0C is FAT32 with LBA).
     *      - Refer to: https://en.wikipedia.org/wiki/Partition_type
     *  3. [] Re-create `fdisk -l <img>` output.
     *  4. [] Ensure it works for the any file system.
     *
     * */

    public static void main(String[] args) throws IOException {
        getMBRHexValues();
        splitPartitions();
        printPartitionMetadata();
    }

    private static void getMBRHexValues() throws IOException {
        byte[] bytes = Utils.getImageBytes(IMAGE_PATH, MBR_BYTES);
        mbrHexValues = Utils.byteToHexArray(bytes);
        int partitionHexValStart = MBR_BYTES - PARTITION_TABLE_BYTES - 2;
        int partitionHexValEnd = MBR_BYTES - 2;
        mbrPartitionTableHexValues = Arrays.copyOfRange(mbrHexValues, partitionHexValStart, partitionHexValEnd);
    }

    private static void splitPartitions() {
        String[][] partitions = new String[4][16];

        for(int c = 0; c < PARTITION_COUNT; c++) {
            partitions[c] = Arrays.copyOfRange(mbrPartitionTableHexValues, c * PARTITION_BYTES, c * PARTITION_BYTES + PARTITION_BYTES);
        }

        for(int c = 0; c < partitions.length; c++) {
            partitionData.add(new PartitionMetadata(partitions[c]));
        }


        /*for(int c = 0; c < partitions.length; c++) {
            System.out.print("Parition " + (c + 1) + ": ");
            for (int m = 0; m < partitions[c].length; m++) {
                System.out.print(partitions[c][m] + ", ");
            }
            System.out.println();
        }*/
    }

    private static void printPartitionMetadata() {
        for(PartitionMetadata pmd: partitionData) {
            System.out.println(pmd.toString());
        }
    }


    /*private static void run() {
        try {
            String[] hexVals = getMBRHexValues();
            for (int i = 0; i < MBR_BYTES; i++) {
                String pretty = i + "\t" + hexVals[i];
                System.out.println(pretty);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }*/


    /*private static String[] getMBRHexValues() throws IOException {
        byte[] bytes = Utils.getImageBytes(IMAGE_PATH, MBR_BYTES);
        String[] hexVals = Utils.byteToHexArray(bytes);
        return hexVals;
    }*/


    /*
    PARTITION TABLE HEX VALUES FOR RASPIOS-LITE
    Based on:
    - MBR Analysis Video: https://www.youtube.com/watch?v=jRj_HzbHeWU&list=LL&index=2&t=1047s
    - MBR Cheatsheet: https://writeblocked.org/resources/MBR_GPT_cheatsheet.pdf
    - MBR Partition Types: https://en.wikipedia.org/wiki/Partition_type

     BYTE HEX-PAIR

    PARTITION 1
        446 00 -- Boot Indicator: Non-Bootable Partition
        447	00 -- Starting Head
        448	01 -- Start Sector
        449	40 -- Start Cylinder
        450	0c -- SysID/Type: 0x0c = FAT32 LBA
        451	03 -- End Head
        452	e0 -- End Sector
        453	ff -- End Cylinder

        454	00 -- Relative Sectors (Little Endian): 0x00 00 20 00 = 8,192
        455	20
        456	00
        457	00

        458	00 -- Total Sectors (Size, Little Endian): 0x00 08 00 00 = 524,288 => 525,288 * 512 = 268,947,456 B = ~0.034 GB
        459	00
        460	08
        461	00
    PARTITION 2
        462	00 -- Boot Indicator -- Non-Bootable Partition
        463	03 -- Starting Head
        464	e0 -- Start Sector
        465	ff -- Start Cylinder
        466	83 -- SysID/Type: 0x83 = Linux
        467	03 -- End Head
        468	e0 -- End Sector
        469	ff -- End Cylinder

        470	00 -- Relative Sectors (Little Endian):  0x00 08 20 00 = 532,480
        471	20
        472	08
        473	00

        474	00 -- Total Sectors (Size, Little Endian): 0x00 2f c0 00 = 3129344 => 3129344 * 512 = 1,602,224,128 B = ~1.60 GB
        475	c0
        476	2f
        477	00
    PARTITION 3
        478	00 -- Boot Indicator -- Non-Bootable Partition
        479	00 -- Starting Head
        480	00 -- Start Sector
        481	00 -- Start Cylinder
        482	00 -- SysID/Type: 0x00 = Empty
        483	00 -- End Head
        484	00 -- End Sector
        485	00 -- End Cylinder

        486	00 -- Relative Sectors (Little Endian):  0x00 00 00 00 = 0
        487	00
        488	00
        489	00

        490	00 -- Total Sectors (Size, Little Endian): 0x00 00 00 00 = 0 => 0 GB
        491	00
        492	00
        493	00
    PARTITION 4
        494	00 -- Boot Indicator -- Non-Bootable Partition
        495	00 -- Starting Head
        496	00 -- Start Sector
        497	00 -- Start Cylinder
        498	00 -- SysID/Type: 0x00 = Empty
        499	00 -- End Head
        500	00 -- End Sector
        501	00 -- End Cylinder

        502	00 -- Relative Sectors (Little Endian):  0x00 00 00 00 = 0
        503	00
        504	00
        505	00

        506	00  -- Total Sectors (Size, Little Endian): 0x00 00 00 00 = 0 => 0 GB
        507	00
        508	00
        509	00



    OTHER:
    Disk Identifier:
    (I found this by looking at the fdisk output and searched for the hex pairs in the output from this Java program)

    440	6b
    441	49
    442	30
    443	97

     */
}


