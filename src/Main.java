import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // String imgPath = Utils.chooseImagePath();
        String imgPath = "src/images/partex.img";
        PartitionTable partitionTable = PartitionTable.parseImage(imgPath);
        partitionTable.printPartitionEntriesAsHex();
        partitionTable.print();
    }
}
