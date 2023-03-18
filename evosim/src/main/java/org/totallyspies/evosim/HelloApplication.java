package org.totallyspies.evosim;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        Group root = new Group();
        root.setRotationAxis(Rotate.X_AXIS);
        root.setRotate(30);

        Scene scene = new Scene(root, 500, 300, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Example");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
