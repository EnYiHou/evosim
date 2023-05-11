package org.totallyspies.evosim.utils;

import javafx.stage.FileChooser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class FileSelector {

    /**
     * A <code>FileChooser</code> used for generating a file explorer in order to choose
     * a file or save a .json file.
     */
    private static FileChooser fileChooserJson;

    /**
     * A <code>FileChooser</code> used for generating a file explorer in order to choose
     * a file or save an image.
     */
    private static FileChooser fileChooserImage;

    /**
     * Getting the fileChooser variable, and initializing it, if it is null.
     * @return static fileChooserJSON variable
     */
    public static FileChooser getFileChooserJson() {
        if (fileChooserJson == null) {
            fileChooserJson = getFileChooser("JSON File", "*.json");
        }
        return fileChooserJson;
    }

    /**
     * Getting the fileChooser variable, and initializing it, if it is null.
     * @return static fileChooserImage variable
     */
    public static FileChooser getFileChooserImage() {
        if (fileChooserImage == null) {
            fileChooserImage = getFileChooser("Image Files", "*.png", "*.jpg", "*.gif");
            fileChooserImage.setTitle("Select an Image");
        }
        return fileChooserImage;
    }

    private static FileChooser getFileChooser(
            final String description, final String... extension) {
        FileChooser fileChooser = new FileChooser();
        String evosimDir = Paths.get(
                System.getProperty("user.home"), "Documents", "Evosim").toString();
        File evosimFolder = new File(evosimDir);

        if (!evosimFolder.exists()) {
            evosimFolder.mkdir();
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(description, extension));
        fileChooser.setInitialDirectory(
                new File(evosimDir));

        return fileChooser;
    }
}
