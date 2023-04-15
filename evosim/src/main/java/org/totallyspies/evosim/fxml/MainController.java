package org.totallyspies.evosim.fxml;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.utils.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input fields.
 */
public final class MainController {

    /**
     *  In order to explore the user's files.
     */
    private FileChooser fileChooser;

    /**
     * As if the variable is saved or not on the drive.
     */
    private boolean isSaved;

    /**
     * The global configuration of the application.
     */
    private static Configuration configuration;

    /**
     * Constructor to create the MainController object.
     */
    public MainController() {
        this.fileChooser = new FileChooser();
        MainController.configuration = Configuration.getCONFIGURATION();
        isSaved = false;
    }


    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() throws IOException {

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
        configuration.saveLatestConfiguration();
    }

    /**
     * Opens a modal AboutWindow when the "About" option is clicked under Help.
     */
    @FXML
    private void aboutMenuClicked() {
        AboutWindow aw = new AboutWindow(EvosimApplication.getApplication().getStage());
        aw.getAbtStage().show();
    }

    @FXML
    private void clickOnSave(final ActionEvent event) throws IOException {
        if(!isSaved) {
            fileChooser.setTitle("Save Configuration");
            File file = fileChooser.showSaveDialog(EvosimApplication.getApplication().getStage());

            if (file != null) {
                configuration.saveConfiguration(file);
            }
            isSaved = true;
        }
    }

    @FXML
    private void clickOnLoad(final ActionEvent event) throws IOException {
        fileChooser.setTitle("Load Configuration");
        File file = fileChooser.showOpenDialog(EvosimApplication.getApplication().getStage());

        if (file != null) {
            configuration.loadConfiguration(file);
            System.out.println(configuration.toString());
        } else {
            System.out.println("File doesn't exist or is not JSON.");
        }

    }

    @FXML
    private void clickOnLoadLatest(final ActionEvent event) {
        configuration.loadLastConfiguration();
        System.out.println(configuration.toString());
    }

    @FXML
    private void clickOnLoadDefault(final ActionEvent event) {
        configuration.loadDefaultConfiguration();
        System.out.println(configuration.toString());
    }

    @FXML
    private void clickOnExit(final ActionEvent event) throws IOException {
        configuration.saveLatestConfiguration();
        Platform.exit();
    }
}
