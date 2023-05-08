package org.totallyspies.evosim.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.ResourceManager;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;

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

    /**
     * The functions we need to run in order to shutdown the application.
     */
    @Getter
    private LinkedList<Runnable> shutdownHooks;

    @Override
    public void start(final Stage primaryStage) {
        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(this::requestExit);

        this.shutdownHooks = new LinkedList<>();
        application = this;
        stage = primaryStage;
        WindowUtils.setSceneRoot(primaryStage,
                this.getClass().getResource(ResourceManager.FXML_WELCOME_VIEW),
                this.getClass().getResource(ResourceManager.CSS_GLOBAL).toExternalForm());
        primaryStage.setTitle("Evosim");
        primaryStage.show();

        Thread.setDefaultUncaughtExceptionHandler(this::showError);
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
        this.shutdownHooks.forEach(Runnable::run);
        super.stop();
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

    private void showError(Thread thread, Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Evosim Error");
        alert.setHeaderText("Evosim Error");
        alert.setContentText(rootCause.getMessage());

        alert.showAndWait();
    }

    public void requestExit(WindowEvent event) {
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you'd like to exit Evosim?",
                ButtonType.YES,
                ButtonType.NO
        );

        Optional<ButtonType> selection = confirmation.showAndWait();
        if (selection.isEmpty() || selection.get() == ButtonType.NO) {
            confirmation.close();
            if (event != null) {
                event.consume();
            }
        } else {
            Platform.exit();
        }
    }
}
