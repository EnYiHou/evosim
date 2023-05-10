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
     * a file or save a file.
     */
    private static FileChooser fileChooser;

    /**
     * Getting the fileChooser variable, and initializing it, if it is null.
     * @return static fileChooser variable
     */
    public static FileChooser getFileSelector() {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            String evosimDir = Paths.get(
                    System.getProperty("user.home"), "Documents", "Evosim").toString();
            File evosimFolder = new File(evosimDir);

            if (!evosimFolder.exists()) {
                evosimFolder.mkdir();
            }

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON File", "*.json"));
            fileChooser.setInitialDirectory(
                    new File(evosimDir));
        }
        return fileChooser;
    }
}