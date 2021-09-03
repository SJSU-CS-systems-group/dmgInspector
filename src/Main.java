import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
         String imgPath = Utils.chooseImagePath();
//        String imgPath = "src/images/partex.img";
        //String imgPath = "src/images/2021-05-07-raspios-buster-armhf-lite.img";
        PartitionTable partitionTable = PartitionTable.parseImage(imgPath);

        partitionTable.printPartitionEntriesAsHex();
        partitionTable.print();

//        UNCOMMENT THE CODE BELOW TO SEE THE EXTENDED PARTITION PARSING EXAMPLE
//          This only works on partex.img
//        // To-do: Un-hardcode extended partition reading logic
//        PartitionTable extPart1 = partitionTable.getExtendedPartition(imgPath, 22528);
//        extPart1.printPartitionEntriesAsHex();
//        extPart1.print();
//
//        PartitionTable extPart2 = partitionTable.getExtendedPartition(imgPath, 8192+22528);
//        extPart2.printPartitionEntriesAsHex();
//        extPart2.print();
    }
}
