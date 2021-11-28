import apfs.APFS;
import apfs.APFSContainer;
import utils.Utils;

import java.io.File;
import java.io.IOException;

public class Main {
    // TODO: Final project
    // dmginspector CLI TOOL --
    // 1. Dump volume info -- dmgi volumes -> getAPFSVolumes()
    // 2. Dump volume file objects -- dmgi fsobj <?volume> -> getFSObjects(index): ArrayList<FSKeyValue>
    // 3. Dump file system structure -- dmgi files -> getFSStructure(index): ArrayList<FSObject>
    // 4. Extract file(s) -- dmgi extract <?specific_file>
    //  TODO: temp folder for extracted DMG Files?

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


        // Re-make output directory (deletes previous output to prevent overlap with new output)
        File outputDir = new File("output/");
        if (outputDir.exists()) {
            Utils.deleteFolder(outputDir);
        }
        outputDir.mkdir();

        // DMG file to parse
        // TODO: CLI input instead of hardcoding file paths
        // Current flow:
        // 1. Run the program by uncommenting ONE of the below dmgFile Strings
        // 2. APFS structure (and other parts) get parsed from the DMG (see 0-7 files in output/)
        // 3. APFS image (4) root folder gets parsed to output/root

        // Uncomment ONE of these
        String dmgFile = "src/images/bigandsmall.dmg";
//        String dmgFile = "src/images/Many Files.dmg";

        DMGInspector dmgInspector = DMGInspector.parseImage(dmgFile);

        String filepath = "temp/4_diskimageApple_APFS4";
        APFS apfs = new APFS(filepath);
    }
}
