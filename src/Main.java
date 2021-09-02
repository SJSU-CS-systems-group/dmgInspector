import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // String imgPath = Utils.chooseImagePath();
        String imgPath = "src/images/partex.img";
        //String imgPath = "src/images/2021-05-07-raspios-buster-armhf-lite.img";
        PartitionTable partitionTable = PartitionTable.parseImage(imgPath);
        partitionTable.printPartitionEntriesAsHex();
        partitionTable.print();
    }
}
