import apfs.APFS;
import apfs.APFSVolume;
import picocli.CommandLine;
import utils.Tuple;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


// TODO: Final project
// dmginspector CLI TOOL --
// 1. Dump volume info -- dmgi volumes -> getAPFSVolumes()
// 2. Dump volume file objects -- dmgi fsobj <?volume> -> getFSObjects(index): ArrayList<FSKeyValue>
// 3. Dump file system structure -- dmgi files -> getFSStructure(index): ArrayList<FSObject>
// 4. Extract file(s) -- dmgi extract <?specific_file>
//  TODO: temp folder for extracted DMG Files?


@CommandLine.Command(name = "dmgi",
        description = "DMGI Tool that can read and extract dmg files",
        version = "1.0.0",
        mixinStandardHelpOptions = true)
public class DMGInspectorCLI implements Runnable {

    APFS apfs;

    @CommandLine.Option(names = {"-p", "--path"}, required = true, description = "path of the dmg file")
    String path;

    @CommandLine.Option(names = {"--volumes"}, description = "print all the volumes in the APFS Structure")
    Boolean vols;

    @CommandLine.Option(names = {"-a", "--all"}, description = "extract all the content of the volumes")
    Boolean extractAll;

    @CommandLine.Option(names = {"-f", "--files"}, description = "show all files")
    Boolean show;

    @CommandLine.Option(names = "--extract", description = "extract one file")
    Integer fileId;

    @CommandLine.Option(names = "--fs", description = "show all the FS objects")
    Boolean fsObjs;


    public static void main(String[] args) {
        new CommandLine(new DMGInspectorCLI()).execute(args);
    }

    @Override
    public void run() {

        getTempFiles();

        if (vols != null) {
            System.out.println("----Volumes----");
            for (int i = 0; i < apfs.volumes.size(); i++) {
                System.out.println(i + ". " + apfs.volumes.get(i));
            }
        }

        if (show != null) {
            System.out.println("----Files----");
            for (int i = 0; i < apfs.volumes.get(0).files.size(); i++) {
                File file = new File(apfs.volumes.get(0).files.get(i).x);
                System.out.println(String.format("%d. File: %s \n\tExtent: %s", i, file.getName(), apfs.volumes.get(0).files.get(i).y));
            }
        }

        if (extractAll != null) {
            try {
                apfs.volumes.get(0).extractAllFiles();
            } catch (IOException e) {
                throw new CommandLine.ParameterException(new CommandLine(this), "Unable to extract all files");
            }
        }

        if (fileId != null) {
            try {
                apfs.volumes.get(0).extractFile(fileId);
            } catch (IOException e) {
                throw new CommandLine.ParameterException(new CommandLine(this), "Unable to extract file" + fileId);
            }
        }

        if (fsObjs != null) {
            System.out.println("---File System Objects---\n");
            System.out.println("Inode Records\n");

            // TODO: Print records for a specific volume
            // We're only printing the first volume's info right now since our test images only have 1 volume
            ArrayList inodeRecords = new ArrayList(apfs.volumes.get(0).inodeRecords.values());
            for (int i = 0; i < inodeRecords.size(); i++) {
                System.out.println(String.format("%d. %s", i, inodeRecords.get(i)));
            }

            ArrayList drecRecords = new ArrayList(apfs.volumes.get(0).drecRecords.values());
            System.out.println("\nDREC Records - Similar objects grouped together\n");
            for (int i = 0; i < drecRecords.size(); i++) {
                ArrayList records = (ArrayList) drecRecords.get(i);
                for (int j = 0; j < records.size(); j++) {
                    System.out.println(String.format("%d-%d. %s",i, j, records.get(j)));
                }
                System.out.println("\n");
            }

            ArrayList extentRecords = new ArrayList(apfs.volumes.get(0).extentRecords.values());
            System.out.println("\nExtent Records\n");
            for (int i = 0; i < extentRecords.size(); i++) {
                System.out.println(String.format("%d. %s", i, extentRecords.get(i)));
            }

        }

        Utils.deleteFolder(new File("temp"));
    }

    private void getTempFiles() {
        try {
            File outputDir = new File("temp/");
            if (outputDir.exists()) {
                Utils.deleteFolder(outputDir);
            }
            outputDir.mkdir();
            DMGInspector dmgInspector = DMGInspector.parseImage(this.path);
            apfs = new APFS("temp/4_diskimageApple_APFS4");
        } catch (IOException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), path + " is invalid path");
        }
    }
}
