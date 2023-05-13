package org.totallyspies.evosim.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.totallyspies.evosim.utils.ResourceManager;
import java.util.LinkedList;
import java.util.Optional;

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

    /**
     * The functions we need to run in order to pre-shutdown the application.
     */
    @Getter
    private LinkedList<Runnable> preShutdownHooks;

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
        this.preShutdownHooks = new LinkedList<>();

        application = this;
        stage = primaryStage;
        WindowUtils.setSceneRoot(primaryStage,
                this.getClass().getResource(ResourceManager.FXML_WELCOME_VIEW),
                this.getClass().getResource(ResourceManager.CSS_GLOBAL).toExternalForm());
        primaryStage.setTitle("EVOSIM");
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

    private void showError(final Thread thread, final Throwable throwable) {
        Throwable rootCause = throwable;
        rootCause.printStackTrace();

        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Evosim Error");
        alert.setHeaderText("Evosim Error");
        alert.setContentText(rootCause.getMessage());

        alert.showAndWait();
    }

    /**
     * Requesting an confirmation for the exit of the application.
     * @param event
     */
    public void requestExit(final WindowEvent event) {
        this.preShutdownHooks.forEach(Runnable::run);
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you'd like to exit EVOSIM?",
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
