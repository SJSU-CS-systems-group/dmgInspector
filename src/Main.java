import apfs.APFS;
import apfs.APFSContainer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        String imgPath = utils.Utils.chooseImagePath();
//        MBRPartitionTable partitionTable = MBRPartitionTable.parseImage(imgPath);
//
//        // Determine MBR or GPT
//        if (partitionTable.partitionEntries.get(0).type.equals("GUID Parition Table")) {
//            // Read LBA 1
//            GPTPartitionTable gptPartitionTable = GPTPartitionTable.parseImage(imgPath);
//            gptPartitionTable.print(imgPath.substring(imgPath.lastIndexOf('/') + 1));
//        } else {
//            partitionTable.print(imgPath.substring(imgPath.lastIndexOf('/') + 1));
//        }

//        String dmgFile = "src/images/bigandsmall.dmg";
//        String dmgFile = "src/images/Many Files.dmg";
//        DMGInspector dmgInspector = DMGInspector.parseImage(dmgFile);

        String filepath = "output/4_diskimageApple_APFS4";
        APFS apfs = new APFS(filepath);
//        System.out.println(apfs);
    }
}
