package org.totallyspies.evosim;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public final class HelloApplication extends Application {
    @Override
    public void start(final Stage primaryStage) throws IOException {
        primaryStage.show();
    }
    /**
     * Main method.
     * @param args
     */
    public static void main(final String[] args) {
        launch();
    }

}
