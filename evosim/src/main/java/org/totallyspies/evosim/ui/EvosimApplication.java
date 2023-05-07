package org.totallyspies.evosim.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.utils.ResourceManager;
import java.io.File;
import java.io.IOException;

public final class EvosimApplication extends Application {

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
        primaryStage.setTitle("EVOSIM");
        primaryStage.show();
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

        Simulation.shutdownAll();
    }

    private static void createTempDirectory() throws IOException {
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
