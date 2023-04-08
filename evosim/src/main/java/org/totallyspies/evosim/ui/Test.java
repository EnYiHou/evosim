package org.totallyspies.evosim.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.utils.Rng;


/**
 * This class represents the map of the simulation, on which entities will be drawn.
 */
public class Test extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

//        ResizableCanvas canvas = new ResizableCanvas();
//        canvas.getGraphicsContext2D().setFill(Color.GREEN);
//        canvas.getGraphicsContext2D().fillRect(0, 0, 1000, 1000);
//        primaryStage.setScene(new Scene(canvas));

//        StackPane root = new StackPane();
//        Canvas canvas1 = new Canvas();
//        canvas1.setHeight(800);
//        canvas1.setWidth(800);
//        root.getChildren().add(canvas1);
//        canvas1.getGraphicsContext2D().setFill(Color.RED);
//        canvas1.getGraphicsContext2D().fillRect(0, 0, 100, 100);
//        primaryStage.setScene(new Scene(root));


        Map map = Map.getInstance();
        Scene scene = new Scene(map);
        primaryStage.setScene(scene);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);

        Predator predator = new Predator(3, new Point(2, 3), 2);


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                map.clearMap();
                map.drawGrids();
                predator.adjustSensors();
                map.drawEntitySensors(predator);
                map.drawEntity(predator);
                predator.move(1);
                // rotate the predator by a random angle sometimes
                if (Rng.RNG.nextInt(100) < 5) {
                    predator.setDirectionAngleInRadians(predator.getDirectionAngleInRadians() + Rng.RNG.nextDouble() * 2 * Math.PI);
                }
            }
        };


        scene.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.D) {
                map.getCamera().translateX(5);
            }
            if (e.getCode() == KeyCode.W) {
                map.getCamera().translateY(5);
            }
            if (e.getCode() == KeyCode.A) {
                map.getCamera().translateX(-5);
            }
            if (e.getCode() == KeyCode.S) {
                map.getCamera().translateY(-5);
            }
            if (e.getCode() == KeyCode.EQUALS) {
                map.getCamera().zoom(0.05);
                System.out.println("zoomed");
            }
            if (e.getCode() == KeyCode.MINUS) {
                map.getCamera().zoom(-0.05);
            }
        });
        timer.start();
        primaryStage.setTitle("Evosim");
        primaryStage.show();


    }
}
