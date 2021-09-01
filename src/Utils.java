import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    /**
     * @return Image path chosen by the user via their File Explorer.
     * @throws FileNotFoundException Reference: https://mkyong.com/swing/java-swing-jfilechooser-example/
     */
    public static String chooseImagePath() throws FileNotFoundException {
        JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue != JFileChooser.APPROVE_OPTION) throw new FileNotFoundException("Invalid file.");

        File selectedFile = jfc.getSelectedFile();
        return selectedFile.getAbsolutePath();
    }

    /**
     * @param imgPath path of the file to get the file name of. (e.g. C:\Users\Mat\(...)\src\images\2021-05-07-raspios-buster-armhf-lite.img)
     * @return name of the file at the path. (e.g. 2021-05-07-raspios-buster-armhf-lite.img)
     * Reference: https://www.geeksforgeeks.org/path-getfilename-method-in-java-with-examples/
     */
    public static String getPathFileName(String imgPath) {
        Path p = Paths.get(imgPath);
        return p.getFileName().toString();
    }
}
