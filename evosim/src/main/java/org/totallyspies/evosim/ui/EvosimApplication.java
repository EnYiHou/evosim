package org.totallyspies.evosim.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.Getter;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.utils.ResourceManager;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class EvosimApplication extends Application {

    /**
     * Sole scene used by the stage.
     */
    private Scene scene;

    /**
     * Sole stage of the application.
     */
    @Getter
    private Stage stage;

    /**
     * Instance of the application to be kept.
     */
    @Getter
    private static EvosimApplication application;

    @Override
    public void start(final Stage primaryStage) {
        application = this;
        stage = primaryStage;
        WindowUtils.setSceneRoot(primaryStage,
                this.getClass().getResource(ResourceManager.FXML_WELCOME_VIEW),
                this.getClass().getResource(ResourceManager.CSS_GLOBAL).toExternalForm());
        primaryStage.setTitle("Evosim");
        primaryStage.show();
    }

    /**
     * Sets the root of {@link #scene} to an FXML at {#code path}.
     *
     * @param path Resource folder path for the new FXMLs
     */
    public void setRoot(final String path) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(path));

        try {
            if (scene == null) {
                this.scene = new Scene(loader.load());

                this.scene.getStylesheets().add(Objects.requireNonNull(
                        EvosimApplication.class.getResource(
                                ResourceManager.CSS_GLOBAL)).toExternalForm()
                );
            } else {
                this.scene.setRoot(loader.load());
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Evosim Fatal Error");
            alert.setContentText(ex.getMessage());

            ex.printStackTrace();
        }
    }

    /**
     * Main method of the application.
     *
     * @param args Command-line arguments passed to the application
     */
    public static void main(final String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        Simulation.stopAll();
    }

    private static void createTempDirectory()
            throws IOException {
        final File temp;
        temp = File.createTempFile("temp", "evosim/");

        if (!temp.exists()) {
            throw new IOException("Folder already exists: " + temp.getAbsolutePath());
        }

        if (!temp.delete()) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!temp.mkdir()) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
    }
}
