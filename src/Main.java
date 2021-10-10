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
//        DMGInspector dmgInspector = DMGInspector.parseImage(dmgFile);

//        String filepath = "src/apfs/4.Apple_APFS";   // This is what our APFS file should look like. This works with our APFS inspector.
        String filepath = "output/4_diskimageApple_APFS4"; // This is our incorrectly decompressed APFS file -- TODO: Fix DMG decompression
        APFSContainer apfsSuperBlock = APFSContainer.parseImage(filepath);
        System.out.println(apfsSuperBlock);




    }
}
