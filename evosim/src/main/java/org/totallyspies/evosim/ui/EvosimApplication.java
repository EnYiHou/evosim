package org.totallyspies.evosim.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.totallyspies.evosim.utils.ResourceManager;

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
     * Main method of the application.
     *
     * @param args Command-line arguments passed to the application
     */
    public static void main(
            final String[] args) {
        launch();
    }
}
