package org.totallyspies.evosim.ui;

import javafx.scene.paint.Color;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.fxml.ResizableCanvas;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.math.Assert;
import org.totallyspies.evosim.utils.Configuration;

/**
 * This class represents the map of the simulation, on which entities will be drawn.
 */
@SuppressWarnings("ClassEscapesDefinedScope")
public final class Map extends ResizableCanvas {

    /**
     * The height of the whole map.
     */
    public static final int MAP_SIZE = 5000;

    /**
     * The height of a single grid.
     */
    public static final int GRID_SIZE = 500;

    /**
     * The color of the map.
     */
    public static final Color MAP_COLOR = Color.LIGHTSKYBLUE;

    /**
     * The number of grids columns on the map.
     */
    public static final int ROW_COLUMN_COUNT = Map.MAP_SIZE / Map.GRID_SIZE;
    /**
     * The instance of the map.
     */
    private static final Map INSTANCE = new Map();
    /**
     * /**
     * The camera of the map.
     */
    private final Camera camera;

    /**
     * Construct an instance of Map.
     */
    private Map() {
        super();
        this.camera = new Camera();
    }

    /**
     * Returns the instance of the map.
     *
     * @return the instance of the map
     */
    public static Map getInstance() {
        return INSTANCE;
    }

    /**
     * Draw the grid lines on the map.
     */
    public void drawGrids() {
        this.getGraphicsContext2D().setStroke(Color.BLACK);


        for (int i = 0; i <= Map.ROW_COLUMN_COUNT; i++) {
            Point verticalStartingPoint = computePointPosition(i * Map.GRID_SIZE, 0);
            Point verticalEndingPoint = computePointPosition(i * Map.GRID_SIZE, Map.MAP_SIZE);
            Point horizontalStartingPoint = computePointPosition(0, i * Map.GRID_SIZE);
            Point horizontalEndingPoint = computePointPosition(Map.MAP_SIZE, i * Map.GRID_SIZE);

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
        double radius = Configuration.getCONFIGURATION().getEntityRadius();
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
            double minIncrement = Math.signum(value - this.camera.getZoom()) * 0.003;
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
        this.getGraphicsContext2D().setFill(Map.MAP_COLOR);
        this.getGraphicsContext2D().fillRect(
                0, 0, this.getWidth(), this.getHeight());
    }

    /**
     * Returns the camera of the map.
     *
     * @return the camera of the map
     */
    public Camera getCamera() {
        return this.camera;
    }
}
