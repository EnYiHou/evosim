package org.totallyspies.evosim.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.utils.Rng;

import java.util.concurrent.atomic.AtomicBoolean;


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

        AtomicBoolean wPressed = new AtomicBoolean(false);
        AtomicBoolean aPressed = new AtomicBoolean(false);
        AtomicBoolean sPressed = new AtomicBoolean(false);
        AtomicBoolean dPressed = new AtomicBoolean(false);
        AtomicBoolean zoomInPressed = new AtomicBoolean(false);
        AtomicBoolean zoomOutPressed = new AtomicBoolean(false);

        scene.setOnKeyPressed((e) -> {
                    if (e.getCode() == KeyCode.W) {
                        wPressed.set(true);
                    } else if (e.getCode() == KeyCode.A) {
                        aPressed.set(true);
                    } else if (e.getCode() == KeyCode.S) {
                        sPressed.set(true);
                    } else if (e.getCode() == KeyCode.D) {
                        dPressed.set(true);
                    } else if (e.getCode() == KeyCode.EQUALS) {
                        zoomInPressed.set(true);
                    } else if (e.getCode() == KeyCode.MINUS) {
                        zoomOutPressed.set(true);
                    }
                }
        );

        scene.setOnKeyReleased((e) -> {
                    if (e.getCode() == KeyCode.W) {
                        wPressed.set(false);
                    } else if (e.getCode() == KeyCode.A) {
                        aPressed.set(false);
                    } else if (e.getCode() == KeyCode.S) {
                        sPressed.set(false);
                    } else if (e.getCode() == KeyCode.D) {
                        dPressed.set(false);
                    } else if (e.getCode() == KeyCode.EQUALS) {
                        zoomInPressed.set(false);
                    } else if (e.getCode() == KeyCode.MINUS) {
                        zoomOutPressed.set(false);
                    }
                }
        );

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(final long now) {
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

                if (zoomInPressed.get()) {
                    map.getCamera().zoom(map.getCamera().getZoom() * 0.01);
                }
                if (zoomOutPressed.get()) {
                    map.getCamera().zoom(-map.getCamera().getZoom() * 0.01);
                }
                if (wPressed.get()) {
                    map.getCamera().translateY(1);
                }
                if (aPressed.get()) {
                    map.getCamera().translateX(-1);
                }
                if (sPressed.get()) {
                    map.getCamera().translateY(-1);
                }
                if (dPressed.get()) {
                    map.getCamera().translateX(1);
                }
            }
        };


        timer.start();
        primaryStage.setTitle("Evosim");
        primaryStage.show();


    }
}
