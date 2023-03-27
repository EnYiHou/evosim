package org.totallyspies.evosim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public final class EvosimApplication extends Application {
    /**
     * Sole scene used by the stage.
     */
    private Scene scene;

    /**
     * Instance of the application to be kept.
     */
    private static EvosimApplication application;

    @Override
    public void start(final Stage primaryStage) {
        application = this;
        this.setRoot(ResourceManager.FXML_WELCOME_VIEW);
        primaryStage.setTitle("Evosim");
        primaryStage.setScene(this.scene);
        primaryStage.show();
    }

    /**
     * Sets the root of {@link #scene} to an FXML at {#code path}.
     * @param path Resource folder path for the new FXMLs
     */
    public void setRoot(final String path) {
        FXMLLoader loader = new FXMLLoader(
            this.getClass().getResource(path)
        );

        try {
            if (scene == null) {
                this.scene = new Scene(loader.load());

                this.scene.getStylesheets().add(Objects.requireNonNull(
                    EvosimApplication.class.getResource(
                        ResourceManager.CSS_GLOBAL
                    )).toExternalForm()
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
     * @param args Command-line arguments passed to the application
     */
    public static void main(final String[] args) {
        launch();
    }

    public static EvosimApplication getApplication() {
        return application;
    }
}
