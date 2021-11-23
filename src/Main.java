import apfs.APFS;
import apfs.APFSContainer;

import java.io.IOException;

public class Main {
    // TODO: Final project
    // dmginspector CLI TOOL --
    // 1. Dump volume info -- dmgi volumes
    // 2. Dump volume file objects -- dmgi fsobj <?volume>
    // 3. Dump file system structure -- dmgi files
    // 4. Extract file(s) -- dmgi extract <?specific_file>

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
