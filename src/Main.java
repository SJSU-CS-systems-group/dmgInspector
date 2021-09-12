import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        /*String imgPath = Utils.chooseImagePath();
        MBRPartitionTable partitionTable = MBRPartitionTable.parseImage(imgPath);

        // Determine MBR or GPT
        if(partitionTable.partitionEntries.get(0).type.equals("GUID Parition Table")) {
            // Read LBA 1
            GPTPartitionTable gptPartitionTable = GPTPartitionTable.parseImage(imgPath);
            gptPartitionTable.print(imgPath.substring(imgPath.lastIndexOf('/') + 1));
        } else {
            partitionTable.print(imgPath.substring(imgPath.lastIndexOf('/') + 1));
        }*/
        String dmgFile = Utils.chooseImagePath();
        DMGInspector dmgInspector = DMGInspector.parseImage(dmgFile);
    }
}
