package org.totallyspies.evosim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public final class EvosimApplication extends Application {
    @Override
    public void start(final Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            this.getClass().getResource(ResourceManager.FXML_MAIN_VIEW)
        );

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Evosim");

        primaryStage.show();
    }

    /**
     * Main method of the application.
     * @param args Command-line arguments passed to the application
     */
    public static void main(final String[] args) {
        launch();
    }
}
