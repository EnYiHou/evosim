package org.totallyspies.evosim.fxml;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.NoArgsConstructor;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.MapCanvas;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.util.Optional;
import org.totallyspies.evosim.utils.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input fields.
 */
public final class MainController {

    /**
     * FXML reference to the map where simulation is rendered.
     */
    @FXML
    private MapCanvas mapCanvas;

    /**
     * The simulation to be rendered.
     */
    private Simulation simulation;

    /**
     * The StackPane within the center of the root BorderPane.
     */
    @FXML
    private StackPane centerStack;

     *  In order to explore the user's files.
     */
    private FileChooser fileChooser;

    /**
     * The global configuration of the application.
     */
    private static Configuration configuration;

    /**
     * Constructor to create the MainController object.
     */
    public MainController() {
        this.fileChooser = new FileChooser();
        MainController.configuration = Configuration.getConfiguration();
    }

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() {
        this.simulation = new Simulation();
        this.mapCanvas.attach(simulation);
        Scene scene = EvosimApplication.getApplication().getStage().getScene();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.ESCAPE) {
                escapeClicked();
            }
            if (!MapCanvas.getPressedKeys().contains(code)) {
                MapCanvas.getPressedKeys().push(code);
            }
        });

        scene.setOnKeyReleased(event ->
                MapCanvas.getPressedKeys().remove(event.getCode()));

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
        fileChooser.setTitle("Save Configuration");
        File file = fileChooser.showSaveDialog(EvosimApplication.getApplication().getStage());

        if (file != null) {
            configuration.saveConfiguration(file);
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

    /**
     * Display an alert to the user to confirm if they'd like to close the app.
     */
    private void escapeClicked() {
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you'd like to exit Evosim?",
                ButtonType.YES,
                ButtonType.NO
        );

        Optional<ButtonType> selection = confirmation.showAndWait();
        if (selection.isEmpty() || selection.get() == ButtonType.NO) {
            confirmation.close();
        } else if (selection.get() == ButtonType.YES) {
            System.out.println("Shutting down application...");
            EvosimApplication.getApplication().getStage().close();
        }
    }

}
