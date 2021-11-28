import apfs.APFS;
import picocli.CommandLine;
import utils.Utils;
import java.io.File;
import java.io.IOException;


// TODO: Final project
    // dmginspector CLI TOOL --
    // 1. Dump volume info -- dmgi volumes -> getAPFSVolumes()
    // 2. Dump volume file objects -- dmgi fsobj <?volume> -> getFSObjects(index): ArrayList<FSKeyValue>
    // 3. Dump file system structure -- dmgi files -> getFSStructure(index): ArrayList<FSObject>
    // 4. Extract file(s) -- dmgi extract <?specific_file>
    //  TODO: temp folder for extracted DMG Files?


@CommandLine.Command(name="dmgi",
        description="DMGI Tool that can read and extract dmg files",
        version="1.0.0",
        mixinStandardHelpOptions = true)
public class DMGInspectorCLI implements Runnable{

    APFS apfs;

    @CommandLine.Option(names = {"-p", "--path"}, required = true, description = "path of the dmg file")
    String path;

    @CommandLine.Option(names = {"--volumes"}, description = "print all the volumes in the APFS Structure")
    Boolean vols;

    @CommandLine.Option(names = "--extractAll", description = "extract all the content of the file")
    Boolean extractAll;

    @CommandLine.Option(names = {"-s","--show"}, description = "show all files")
    Boolean showAll;

    @CommandLine.Option(names = "--extractOne", description = "extract one file")
    Integer fileId;

    public static void main(String[] args) {
        new CommandLine(new DMGInspectorCLI()).execute(args);
    }

    @Override
    public void run(){

        getTempFiles();

        if (vols != null){
            System.out.println(apfs.volumes);
        }

        if (extractAll!= null){
            try{
                apfs.volumes.get(0).extractAllFiles();
            }catch (IOException e){
                throw new CommandLine.ParameterException(new CommandLine(this), "Unable to extract all files");
            }
        }

        if (showAll!= null) {
            System.out.println(apfs.volumes.get(0).files);
        }

        if (fileId!= null) {
            try {
                apfs.volumes.get(0).extractFile(fileId);
            } catch (IOException e) {
                throw new CommandLine.ParameterException(new CommandLine(this), "Unable to extract file" + fileId);
            }
        }
    }

    private void getTempFiles() {
        try{
            File outputDir = new File("temp/");
            if (outputDir.exists()) {
                Utils.deleteFolder(outputDir);
            }
            outputDir.mkdir();
            DMGInspector dmgInspector = DMGInspector.parseImage(this.path);
            apfs = new APFS("temp/4_diskimageApple_APFS4");
        }catch (IOException e){
            throw new CommandLine.ParameterException(new CommandLine(this), path + " is invalid path");
        }
    }
}
