package org.totallyspies.evosim.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.utils.Rng;

import java.util.LinkedList;
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

        Map map = Map.getInstance();
        Scene scene = new Scene(map);
        primaryStage.setScene(scene);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);

        Predator predator = new Predator(3, new Point(2, 3), 2);


        AtomicBoolean follow = new AtomicBoolean(false);

        LinkedList<KeyCode> pressedKeys = new LinkedList<>();
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.ESCAPE) {
                System.exit(0);
            }
            if (code == KeyCode.SPACE) {
                if (follow.get()) {
                    map.unfollowEntity(predator);
                } else {
                    map.followEntity(predator);
                }
                follow.set(!follow.get());

            } else {
                if (!pressedKeys.contains(code)) {
                    pressedKeys.push(code);
                }
            }
        });


        scene.setOnKeyReleased(event ->
                pressedKeys.remove(event.getCode()));


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
                if (Rng.RNG.nextInt(100) < 1) {
                    predator.setDirectionAngleInRadians(
                            predator.getDirectionAngleInRadians()
                                    + Rng.RNG.nextDouble() * 2 * Math.PI);
                }
                if (!pressedKeys.isEmpty()) {
                    if (!follow.get()) {
                        for (KeyCode code : pressedKeys) {
                            switch (code) {
                                case W:
                                    map.getCamera().translateY(1);
                                    break;
                                case S:
                                    map.getCamera().translateY(-1);
                                    break;
                                case D:
                                    map.getCamera().translateX(1);
                                    break;
                                case A:
                                    map.getCamera().translateX(-1);
                                    break;
                                case EQUALS:
                                    map.getCamera().zoom(0.005);
                                    break;
                                case MINUS:

                                    if (map.getCamera().getZoom() > 0.005) {
                                        map.getCamera().zoom(-0.005);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        };

        timer.start();
        primaryStage.show();


    }
}
