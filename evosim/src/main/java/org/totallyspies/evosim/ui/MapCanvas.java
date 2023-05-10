package org.totallyspies.evosim.ui;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.totallyspies.evosim.entities.Entity;
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
     * The zoom level when follow entity.
     */
    public static final double ENTITY_FOLLOWING_ZOOM = 1.2;

    /**
     * The zoom level when unfollow entity.
     */
    public static final double ENTITY_UNFOLLOWING_ZOOM = 0.5;

    /**
     * The delta zoom when comparing zoom.
     */
    public static final double DELTA_ZOOM = 0.01;

    /**
     * A list of keycodes being pressed.
     */
    @Getter
    private static final LinkedList<KeyCode> PRESSED_KEYS = new LinkedList<>();

    /**
     * The Keys that are accepted to PRESSED_KEYS array.
     */
    @Getter
    private static final LinkedList<KeyCode> ACCEPTED_KEYS = new LinkedList<>() {{
        add(KeyCode.W);
        add(KeyCode.S);
        add(KeyCode.A);
        add(KeyCode.D);
        add(KeyCode.MINUS);
        add(KeyCode.EQUALS);
        add(KeyCode.C);
        add(KeyCode.B);
    }};

    /**
     * The camera of the map.
     */
    @Getter
    private Camera camera;

    /**
     * The loop to render the map.
     */
    private final AnimationTimer anim;

    /**
     * The simulation that is being rendered by the map.
     */
    @Getter
    private Simulation simulation;

    /**
     * Whether the camera is following an entity or not.
     */
    private final AtomicBoolean followingEntity;

    /**
     * The entity currently being followed.
     */
    private Entity followedEntity;

    public static LinkedList<KeyCode> getPressedKeys() {
        return PRESSED_KEYS;
    }

    /**
     * Construct an instance of MapCanvas.
     */
    public MapCanvas() {
        super();

        this.anim = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                update(now);
            }
        };
        this.followingEntity = new AtomicBoolean(false);

        this.setOnMouseClicked(this::checkEntityOnClick);
    }

    /**
     * Draw the grid lines on the map.
     */
    public void drawGrids() {
        this.getGraphicsContext2D().setStroke(Color.BLACK);

        for (int i = 0; i <= this.simulation.getMapSizeX(); i++) {
            Point verticalStartingPoint = computePointPosition(
                i * this.simulation.getGridSize(), 0
            );

            Point verticalEndingPoint = computePointPosition(
                i * this.simulation.getGridSize(),
                this.simulation.getGridSize() * this.simulation.getMapSizeY()
            );

            Point horizontalStartingPoint = computePointPosition(
                0, i * this.simulation.getGridSize()
            );

            Point horizontalEndingPoint = computePointPosition(
                this.simulation.getGridSize() * this.simulation.getMapSizeX(),
                i * this.simulation.getGridSize()
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
     * Draw an entity on the map.
     *
     * @param entity The entity to be drawn on the map
     */
    public void drawEntity(final Entity entity) {
        final double radius = Configuration.getConfiguration().getEntityRadius();
        final double zoom = camera.getZoom();
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
        autoZoom(ENTITY_FOLLOWING_ZOOM);
    }

    /**
     * Unfollow an entity on the map.
     *
     * @param entity The entity to be unfollowed
     */
    public void unfollowEntity(final Entity entity) {
        this.camera.setPoint(new Point(entity.getBodyCenter().getX(),
                entity.getBodyCenter().getY()));
        autoZoom(ENTITY_UNFOLLOWING_ZOOM);

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
            while (!Assert.assertEquals(this.camera.getZoom(), value, DELTA_ZOOM)) {

                this.camera.zoom(
                        minIncrement);

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    return;
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
     * Attaches a simulation to this map.
     * @param newSimulation Simulation to be attached.
     */
    public void attach(final Simulation newSimulation) {
        this.anim.stop();

        if (this.simulation != null) {
            this.simulation.shutdown();
        }

        this.simulation = newSimulation;

        this.camera = new Camera(
            new Point(0, 0),
            new Point(
                this.simulation.getMapSizeX() * this.simulation.getGridSize(),
                this.simulation.getMapSizeX() * this.simulation.getGridSize()
            )
        );

        this.camera.center();
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
                        default -> { }
                    }
                }
            } else if (PRESSED_KEYS.contains(KeyCode.B)) {
                this.unfollowEntity(followedEntity);
                followingEntity.set(false);
                followedEntity = null;
            }
        }

        Coordinate camChunk = this.simulation.pointToGridCoord(this.camera.getPoint());

        // only render entities if they're within the visible square radius
        int radiusX = (int) Math.ceil(
                this.getWidth() / (this.simulation.getGridSize() * this.camera.getZoom())) / 2 + 1;
        int radiusY = (int) Math.ceil(
                this.getHeight() / (this.simulation.getGridSize() * this.camera.getZoom())) / 2 + 1;

        for (int x = camChunk.getX() - radiusX; x <= camChunk.getX() + radiusX; ++x) {
            for (int y = camChunk.getY() - radiusY; y <= camChunk.getY() + radiusY; ++y) {
                if (
                    x < 0 || x >= this.simulation.getMapSizeX()
                    || y < 0 || y >= this.simulation.getMapSizeY()
                ) {
                    continue;
                }

                simulation.forEachGridEntities(x, y, this::drawEntity);
            }
        }
    }

    /**
     * Checks all entities within the clicked chunk. If the mouse clicked an entity, follow it.
     *
     * @param e the click event
     */
    private void checkEntityOnClick(final MouseEvent e) {
        if (simulation == null) {
            return;
        }

        double x = e.getX();
        double y = e.getY();

        Point abs = this.computePointPosition(x, y);

        int chunkX = (int) abs.getX() / this.simulation.getGridSize();
        int chunkY = (int) abs.getY() / this.simulation.getGridSize();

        if (chunkX < 0 || chunkX >= this.simulation.getMapSizeX()
                || chunkY < 0 || chunkY >= this.simulation.getMapSizeY()) {
            return;
        }

        simulation.forEachGridEntities(chunkX, chunkY, entity -> {
            Point entityCenter = entity.getBodyCenter();
            if (Formulas.distance(entityCenter.getX(), entityCenter.getY(),
                    abs.getX(), abs.getY())
                    <= Configuration.getConfiguration().getEntityRadius() * 2
            ) {

                if (!followingEntity.get()) {
                    this.followEntity(entity);
                    followingEntity.set(true);
                    followedEntity = entity;
                }
            }
        });
    }

}
