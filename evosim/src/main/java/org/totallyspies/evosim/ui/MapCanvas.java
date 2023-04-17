package org.totallyspies.evosim.ui;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.fxml.ResizableCanvas;
import org.totallyspies.evosim.geometry.Coordinate;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.math.Assert;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.utils.Configuration;

/**
 * This class represents the map of the simulation, on which entities will be drawn.
 */
public final class MapCanvas extends ResizableCanvas {

    /**
     * The color of the map.
     */
    public static final Color MAP_COLOR = Color.LIGHTSKYBLUE;
    /**
     * A list of keycodes being pressed.
     */
    private static final LinkedList<KeyCode> PRESSED_KEYS = new LinkedList<>();
    /**
     * The camera of the map.
     */
    private final Camera camera;
    /**
     * The loop to render the map.
     */
    private final AnimationTimer anim;
    /**
     * Whether the camera is following an entity or not.
     */
    private final AtomicBoolean followingEntity;
    /**
     * The simulation that is being rendered by the map.
     */
    private Simulation simulation;
    /**
     * The entity currently being followed.
     */
    private Entity followedEntity;

    /**
     * Construct an instance of MapCanvas.
     */
    public MapCanvas() {
        super();
        this.camera = new Camera(
                new Point(0, 0),
                new Point(
                        Simulation.MAP_SIZE_X * Simulation.GRID_SIZE,
                        Simulation.MAP_SIZE_Y * Simulation.GRID_SIZE
                )
        );

        this.camera.center();

        this.anim = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                update(now);
            }
        };
        this.followingEntity = new AtomicBoolean(false);

        this.setEntityOnClick();
    }

    public static LinkedList<KeyCode> getPressedKeys() {
        return PRESSED_KEYS;
    }

    /**
     * Draw the grid lines on the map.
     */
    public void drawGrids() {
        this.getGraphicsContext2D().setStroke(Color.BLACK);

        for (int i = 0; i <= Simulation.MAP_SIZE_X; i++) {
            Point verticalStartingPoint = computePointPosition(i * Simulation.GRID_SIZE, 0);
            Point verticalEndingPoint = computePointPosition(
                    i * Simulation.GRID_SIZE, Simulation.GRID_SIZE * Simulation.MAP_SIZE_Y
            );

            Point horizontalStartingPoint = computePointPosition(0, i * Simulation.GRID_SIZE);
            Point horizontalEndingPoint = computePointPosition(
                    Simulation.GRID_SIZE * Simulation.MAP_SIZE_X, i * Simulation.GRID_SIZE
            );

            // draw vertical grids lines
            this.getGraphicsContext2D().strokeLine(
                    verticalStartingPoint.getX(), verticalStartingPoint.getY(),
                    verticalEndingPoint.getX(), verticalEndingPoint.getY());

            // draw horizontal grids lines
            this.getGraphicsContext2D().strokeLine(
                    horizontalStartingPoint.getX(), horizontalStartingPoint.getY(),
                    horizontalEndingPoint.getX(), horizontalEndingPoint.getY());
        }
    }

    /**
     * Compute the relative position of an absolute point according to the camera
     * and the map.
     *
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     * @return The relative position in a point.
     */
    private Point computePointPosition(final double x, final double y) {
        double zoom = this.camera.getZoom();
        double newX = zoom * (x - this.camera.getX()) + this.getWidth() / 2;
        double newY = -(y - this.camera.getY()) * zoom + this.getHeight() / 2;

        return new Point(newX, newY);
    }

    /**
     * Compute the absolute position of a relative point according to the camera
     * and the map.
     *
     * @param x The x coordinate of the relative point.
     * @param y The y coordinate of the relative point.
     * @return The absolute position in a point.
     */
    private Point computePositionPoint(final double x, final double y) {
        double zoom = this.camera.getZoom();
        double newX = (x - this.getWidth() / 2) / zoom + this.camera.getX();
        double newY = -(y - this.getHeight() / 2) / zoom + this.camera.getY();

        return new Point(newX, newY);
    }

    /**
     * Draw an entity on the map.
     *
     * @param entity The entity to be drawn on the map
     */
    public void drawEntity(final Entity entity) {
        double radius = Configuration.getConfiguration().getEntityRadius();
        double zoom = camera.getZoom();
        this.getGraphicsContext2D().setFill(entity.getColor());

        Point position = computePointPosition(entity.getBodyCenter().getX(),
                entity.getBodyCenter().getY());

        this.getGraphicsContext2D().fillOval(
                position.getX() - radius * zoom,
                position.getY() - radius * zoom,
                radius * 2 * zoom,
                radius * 2 * zoom);
    }

    /**
     * Draw an entity's sensors on the map.
     *
     * @param entity The entity, whose sensors to be drawn on the map
     */
    public void drawEntitySensors(final Entity entity) {
        this.getGraphicsContext2D().setStroke(Color.HOTPINK);
        for (Line sensor : entity.getSensors()) {

            Point startPoint = computePointPosition(sensor.getStartPoint().getX(),
                    sensor.getStartPoint().getY());

            Point endPoint = computePointPosition(sensor.getEndPoint().getX(),
                    sensor.getEndPoint().getY());

            this.getGraphicsContext2D().strokeLine(
                    startPoint.getX(), startPoint.getY(),
                    endPoint.getX(), endPoint.getY());
        }
    }

    /**
     * Follow an entity on the map.
     *
     * @param entity The entity to be followed
     */
    public void followEntity(final Entity entity) {
        //Set the camera's position to the entity's position.
        this.camera.setPoint(entity.getBodyCenter());
        autoZoom(1.2d);
    }

    /**
     * Unfollow an entity on the map.
     *
     * @param entity The entity to be unfollowed
     */
    public void unfollowEntity(final Entity entity) {
        this.camera.setPoint(new Point(entity.getBodyCenter().getX(),
                entity.getBodyCenter().getY()));
        autoZoom(0.5);

    }

    /**
     * Automatic zoom in and zoom out to a specific value.
     * This effect will be a smooth transition.
     *
     * @param value The value to be zoomed to
     */
    public void autoZoom(final double value) {
        Thread thread = new Thread(() -> {
            double minIncrement = Math.signum(
                    value - this.camera.getZoom()) * Camera.DEFAULT_ZOOMING_SPEED;
            while (!Assert.assertEquals(this.camera.getZoom(), value, 0.01d)) {

                this.camera.zoom(
                        minIncrement);

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * Delete all drawings on the map.
     * Prepare to draw a new frame.
     */
    public void clearMap() {
        this.getGraphicsContext2D().setFill(MapCanvas.MAP_COLOR);
        this.getGraphicsContext2D().fillRect(
                0d, 0d, this.getWidth(), this.getHeight());
    }

    /**
     * Returns the camera of the map.
     *
     * @return the camera of the map
     */
    public Camera getCamera() {
        return this.camera;
    }

    /**
     * Attaches a simulation to this map.
     *
     * @param newSimulation Simulation to be attached.
     */
    public void attach(final Simulation newSimulation) {
        this.simulation = newSimulation;
        this.anim.start();
    }

    private void update(final long now) {
        clearMap();
        drawGrids();

        final double camTranslateSpeed = Camera.CAMERA_TRANSLATE_SPEED;
        final double camZoomIncrement = Camera.CAMERA_ZOOM_INCREMENT;
        if (!PRESSED_KEYS.isEmpty()) {
            if (!followingEntity.get()) { // cannot control camera when tracking
                for (KeyCode code : PRESSED_KEYS) {
                    switch (code) { // camera controls
                        case W -> this.getCamera().translateY(camTranslateSpeed);
                        case S -> this.getCamera().translateY(-camTranslateSpeed);
                        case D -> this.getCamera().translateX(camTranslateSpeed);
                        case A -> this.getCamera().translateX(-camTranslateSpeed);
                        case C -> this.getCamera().center();
                        case EQUALS -> this.getCamera().zoom();
                        case MINUS -> {
                            if (this.getCamera().getZoom() > camZoomIncrement) {
                                this.getCamera().unzoom();
                            }
                        }
                        default -> {
                        }
                    }
                }
            } else {
                if (PRESSED_KEYS.contains(KeyCode.SPACE)) {
                    if (followedEntity != null) {
                        this.unfollowEntity(followedEntity);
                        followingEntity.set(false);
                    }
                }
            }
        }
        Coordinate camChunk = Simulation.coordsToChunkCoords(this.camera.getPoint());

        // only render entities if they're within the visible square radius
        int radiusX = (int) Math.ceil(
                this.getWidth() / (Simulation.GRID_SIZE * this.camera.getZoom())) / 2 + 1;
        int radiusY = (int) Math.ceil(
                this.getWidth() / (Simulation.GRID_SIZE * this.camera.getZoom())) / 2 + 1;

        for (int x = camChunk.getX() - radiusX; x <= camChunk.getX() + radiusX; ++x) {
            for (int y = camChunk.getY() - radiusY; y <= camChunk.getY() + radiusY; ++y) {
                if (x < 0 || x >= Simulation.MAP_SIZE_X || y < 0 || y >= Simulation.MAP_SIZE_Y) {
                    continue;
                }

                simulation.getGridEntities(x, y).forEach(this::drawEntity);
            }
        }
    }


    /**
     * Checks if the mouse is clicking on an entity.
     */
    private void setEntityOnClick() {

        this.setOnMouseClicked((e) -> {
            double x = e.getX();
            double y = e.getY();

            Point abs = this.computePositionPoint(x, y);

            int chunkX = (int) abs.getX() / Simulation.GRID_SIZE;
            int chunkY = (int) abs.getY() / Simulation.GRID_SIZE;

            System.out.println("Clicked on " + abs.getX() + " " + abs.getY());
            System.out.println("Chunk " + chunkX + " " + chunkY);




            if (chunkX < 0 || chunkX >= Simulation.MAP_SIZE_X
                    || chunkY < 0 || chunkY >= Simulation.MAP_SIZE_Y) {
                return;
            }

            for (Entity entity : simulation.getGridEntities(chunkX, chunkY)) {
                Point entityCenter = entity.getBodyCenter();
                if (Formulas.distance(entityCenter.getX(), entityCenter.getY(),
                        abs.getX(), abs.getY()) <= Configuration.getConfiguration().getEntityRadius()*2) {

                    if (!followingEntity.get()) {
                        this.followEntity(entity);
                        followingEntity.set(true);
                        followedEntity = entity;
                    }
                }
            }

            System.out.println("Followed" + followingEntity.get());
        });

    }

}
