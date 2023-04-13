package org.totallyspies.evosim.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import lombok.NoArgsConstructor;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.utils.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input
 * fields.
 */
@NoArgsConstructor
public final class MainController {

    private static final String ACCEPTED_FILE_EXTENSION = "json";
    private FileChooser fileChooser;

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() {
        String evosimDir = Paths.get(System.getProperty("user.home"), "Documents", "Evosim").toString();
        File evosimFolder = new File(evosimDir);

        if(!evosimFolder.exists()) {
            evosimFolder.mkdir();
        }

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON File", "*.json"));
        fileChooser.setInitialDirectory(
                new File(evosimDir));
    }

    /**
     * Opens the AboutWindow when the "About" option is clicked under Help.
     */
    @FXML
    private void aboutMenuClicked() {
        AboutWindow aw = new AboutWindow(
                EvosimApplication.getApplication().getStage());
        aw.getAbtStage().showAndWait();

        aw.getAbtStage().close();
    }

    @FXML
    private void clickOnSave(ActionEvent event) throws IOException {
        fileChooser.setTitle("Save Configuration");
        File file = fileChooser.showSaveDialog(EvosimApplication.getApplication().getStage());

        if (file != null) {
            Configuration.getCONFIGURATION().saveConfiguration(file);
        }
    }

    private void clickOnLoad(ActionEvent event) throws IOException {
        fileChooser.setTitle("Save Configuration");
        File file = fileChooser.showOpenDialog(EvosimApplication.getApplication().getStage());

        if (file != null) {
            Configuration conf = Configuration.getCONFIGURATION();
            conf.saveConfiguration();
        } else {
            System.out.println("File doesn't exist or is not JSON.");
        }

    }
}
