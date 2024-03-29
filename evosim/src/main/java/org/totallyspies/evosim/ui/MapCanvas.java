package org.totallyspies.evosim.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.entities.Prey;
import org.totallyspies.evosim.fxml.MainController;
import org.totallyspies.evosim.fxml.ResizableCanvas;
import org.totallyspies.evosim.geometry.Coordinate;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.math.Assert;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.EvosimException;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents the map of the simulation, on which entities will be drawn.
 */
public final class MapCanvas extends ResizableCanvas {

    /**
     * The color of the map.
     */
    @Getter
    private static Color mapColor;

    /**
     * The background image of the map.
     */
    @Setter
    private static Image mapImage = null;


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
     * Number of seconds in a minute.
     */
    private static final int SECONDS_IN_MINUTE = 60;

    /**
     * A list of keycodes being pressed.
     */
    @Getter
    private static final LinkedList<KeyCode> PRESSED_KEYS = new LinkedList<>();

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
     * The loop updating the focused entities stats on focus.
     */
    private final AnimationTimer updSensorsStats;

    /**
     * Whether the camera is following an entity or not.
     */
    private final AtomicBoolean followingEntity;

    /**
     * The simulation that is being rendered by the map.
     */
    @Getter
    private Simulation simulation;

    /**
     * The entity currently being followed.
     */
    private Entity followedEntity;

    /**
     * Previous point of the dragging action.
     */
    private final Point dragAnchor;

    /**
     * Construct an instance of MapCanvas.
     */
    public MapCanvas() throws EvosimException {
        super();

        MapCanvas.mapColor = Configuration.getConfiguration().getColorMap();

        this.anim = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                update(now);
            }
        };

        this.updSensorsStats = new AnimationTimer() {
            private final MainController controller = MainController.getController();

            @Override
            public void handle(final long now) {
                if (followedEntity != null) {
                    controller.getPbEnergy().setProgress(followedEntity.getEnergy());
                    controller.getPbSplit().setProgress(followedEntity.getSplitEnergy());
                    controller.getSpeedLabel().setText(
                        String.format("Base Speed: %.2f", followedEntity.getSpeed()));
                    controller.getChildCountLabel().setText(
                        "Child Count: " + followedEntity.getChildCount());

                    int livingTime = followedEntity.getLivingTime(
                        controller.getTimerProperty().getValue().getSeconds());
                    controller.getLivingTimeLabel().setText(
                        String.format(
                            "Time Alive: %dm : %02ds",
                            livingTime / SECONDS_IN_MINUTE,
                            livingTime % SECONDS_IN_MINUTE
                        )
                    );
                }
            }
        };

        this.followingEntity = new AtomicBoolean(false);
        this.dragAnchor = new Point();

        this.setOnMouseClicked(this::checkEntityOnClick);
        this.setOnScroll(this::onScroll);
        this.setOnMousePressed(this::onDragged);
        this.setOnMouseDragged(this::onDragged);
    }

    private void onDragged(final MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY || followingEntity.get()) {
            return;
        }

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            this.camera.translateX(
                (mouseEvent.getX() - dragAnchor.getX()) * -Camera.CAMERA_TRANSLATE_SPEED / 2
            );

            this.camera.translateY(
                (mouseEvent.getY() - dragAnchor.getY()) * Camera.CAMERA_TRANSLATE_SPEED / 2
            );
        }

        dragAnchor.setX(mouseEvent.getX());
        dragAnchor.setY(mouseEvent.getY());
    }

    private void onScroll(final ScrollEvent scrollEvent) {
        this.camera.zoom(
            Camera.CAMERA_ZOOM_INCREMENT
                * 2
                * (scrollEvent.getDeltaY() / scrollEvent.getMultiplierY())
        );
    }

    /**
     * Draw the grid lines on the map.
     */
    public void drawGrids() {
        this.getGraphicsContext2D().setStroke(Color.BLACK);

        for (int i = 0; i <= this.simulation.getMapSizeX(); i++) {
            Point verticalStartingPoint = absToRelPosition(
                i * this.simulation.getGridSize(), 0
            );

            Point verticalEndingPoint = absToRelPosition(
                i * this.simulation.getGridSize(),
                this.simulation.getGridSize() * this.simulation.getMapSizeY()
            );

            // draw vertical grids lines
            this.getGraphicsContext2D().strokeLine(
                verticalStartingPoint.getX(), verticalStartingPoint.getY(),
                verticalEndingPoint.getX(), verticalEndingPoint.getY());
        }

        for (int i = 0; i <= this.simulation.getMapSizeY(); i++) {
            Point horizontalStartingPoint = absToRelPosition(
                0, i * this.simulation.getGridSize()
            );

            Point horizontalEndingPoint = absToRelPosition(
                this.simulation.getGridSize() * this.simulation.getMapSizeX(),
                i * this.simulation.getGridSize()
            );

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
    private Point absToRelPosition(final double x, final double y) {
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
    private Point relToAbsPosition(final double x, final double y) {
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
    public void drawEntity(final Entity entity) throws EvosimException {
        final double radius = Configuration.getConfiguration().getEntityRadius();
        final double zoom = camera.getZoom();
        final double lookAt = entity.getDirectionAngleInRadians();
        final double eyeAngle = Math.PI / 10;
        final double eyeRadius = radius / 3;
        final double pupilRadius = eyeRadius / 2;

        if (entity == followedEntity) {
            drawEntitySensors(entity);
        }

        if (entity instanceof Prey) {
            this.getGraphicsContext2D().setFill(Prey.getBodyColour());
        } else {
            this.getGraphicsContext2D().setFill(Predator.getBodyColour());
        }

        Point position = absToRelPosition(entity.getBodyCenter().getX(),
            entity.getBodyCenter().getY());

        this.getGraphicsContext2D().fillOval(
                position.getX() - radius * zoom,
                position.getY() - radius * zoom,
                radius * 2 * zoom,
                radius * 2 * zoom);

        // Eyes
        final Point leftEyeCenter = new Point(
            position.getX()
                + ((radius - eyeRadius) * Math.cos(lookAt + eyeAngle)) * zoom,
            position.getY()
                - ((radius - eyeRadius) * Math.sin(lookAt + eyeAngle)) * zoom
        );

        final Point rightEyeCenter = new Point(
            position.getX()
                + ((radius - eyeRadius) * Math.cos(lookAt - eyeAngle)) * zoom,
            position.getY()
                - ((radius - eyeRadius) * Math.sin(lookAt - eyeAngle)) * zoom
        );

        this.getGraphicsContext2D().setFill(Color.WHITE);
        this.getGraphicsContext2D().fillOval(
            leftEyeCenter.getX() - eyeRadius * zoom,
            leftEyeCenter.getY() - eyeRadius * zoom,
            eyeRadius * 2 * zoom,
            eyeRadius * 2 * zoom
        );

        this.getGraphicsContext2D().fillOval(
            rightEyeCenter.getX() - eyeRadius * zoom,
            rightEyeCenter.getY() - eyeRadius * zoom,
            eyeRadius * 2 * zoom,
            eyeRadius * 2 * zoom
        );

        // Pupil
        int shortestSensor = entity.getSensorCount() / 2;
        for (int i = 0; i < entity.getSensorCount(); ++i) {
            if (entity.getSensorDistance(i) < entity.getSensorDistance(shortestSensor)) {
                shortestSensor = i;
            }
        }

        final Line shortestLine = entity.getSensor(shortestSensor);
        final Point shortestLineEnd = absToRelPosition(
            shortestLine.getEndPoint().getX(),
            shortestLine.getEndPoint().getY()
        );

        final double leftAngle = Math.atan2(
            shortestLineEnd.getY() - leftEyeCenter.getY(),
            shortestLineEnd.getX() - leftEyeCenter.getX()
        );

        final double rightAngle = Math.atan2(
            shortestLineEnd.getY() - rightEyeCenter.getY(),
            shortestLineEnd.getX() - rightEyeCenter.getX()
        );

        final Point leftPupilCenter = new Point(
            leftEyeCenter.getX() + (eyeRadius - pupilRadius) * Math.cos(leftAngle) * zoom,
            leftEyeCenter.getY() + (eyeRadius - pupilRadius) * Math.sin(leftAngle) * zoom
        );

        final Point rightPupilCenter = new Point(
            rightEyeCenter.getX() + (eyeRadius - pupilRadius) * Math.cos(rightAngle) * zoom,
            rightEyeCenter.getY() + (eyeRadius - pupilRadius) * Math.sin(rightAngle) * zoom
        );

        this.getGraphicsContext2D().setFill(Color.BLACK);

        this.getGraphicsContext2D().fillOval(
            leftPupilCenter.getX() - pupilRadius * zoom,
            leftPupilCenter.getY() - pupilRadius * zoom,
            pupilRadius * 2 * zoom,
            pupilRadius * 2 * zoom
        );

        this.getGraphicsContext2D().fillOval(
            rightPupilCenter.getX() - pupilRadius * zoom,
            rightPupilCenter.getY() - pupilRadius * zoom,
            pupilRadius * 2 * zoom,
            pupilRadius * 2 * zoom
        );

        if (entity instanceof Predator) {
            // Eyebrows
            this.getGraphicsContext2D().setFill(Predator.getBodyColour());

            this.getGraphicsContext2D().beginPath();

            this.getGraphicsContext2D().moveTo(
                position.getX()
                    + ((radius - eyeRadius) * Math.cos(lookAt)) * zoom,
                position.getY()
                    - ((radius - eyeRadius) * Math.sin(lookAt)) * zoom
            );

            this.getGraphicsContext2D().lineTo(
                leftEyeCenter.getX() - eyeRadius * Math.cos(lookAt - eyeAngle) * zoom,
                leftEyeCenter.getY() + eyeRadius * Math.sin(lookAt - eyeAngle) * zoom
            );

            this.getGraphicsContext2D().lineTo(
                rightEyeCenter.getX() - eyeRadius * Math.cos(lookAt + eyeAngle) * zoom,
                rightEyeCenter.getY() + eyeRadius * Math.sin(lookAt + eyeAngle) * zoom
            );

            this.getGraphicsContext2D().closePath();

            this.getGraphicsContext2D().fill();
        }
    }

    /**
     * Draw an entity's sensors on the map.
     *
     * @param entity The entity, whose sensors to be drawn on the map
     */
    public void drawEntitySensors(final Entity entity) {
        this.getGraphicsContext2D().setStroke(Color.HOTPINK);

        for (Line sensor : entity.getSensors()) {

            Point startPoint = absToRelPosition(sensor.getStartPoint().getX(),
                sensor.getStartPoint().getY());

            Point endPoint = absToRelPosition(sensor.getEndPoint().getX(),
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
        MainController.getController().getTabPane()
            .getSelectionModel().selectLast();
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
        MainController.getController().getTabPane()
            .getSelectionModel().selectFirst();
        MainController.getController().getNeuralNetworkTab().setNeuralNetwork(null);
    }

    /**
     * Automatic zoom in and zoom out to a specific value.
     * This effect will be a smooth transition.
     *
     * @param value The value to be zoomed to
     */
    public void autoZoom(final double value) {

        camera.setZooming(true);
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
            camera.setZooming(false);
        });
        thread.start();

    }

    /**
     * Delete all drawings on the map.
     * Prepare to draw a new frame.
     */
    public void clearMap() {
        this.getGraphicsContext2D().setFill(MapCanvas.mapColor);
        this.getGraphicsContext2D().fillRect(
            0d, 0d, this.getWidth(), this.getHeight());
    }

    /**
     * Attaches a simulation to this map.
     *
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
                this.simulation.getMapSizeY() * this.simulation.getGridSize()
            )
        );

        this.camera.center();
        this.anim.start();
    }

    /**
     * Resumes playback of the stat tracker for this MapCanvas.
     */
    public void play() {
        if (followingEntity.get()) {
            this.updSensorsStats.start();
        }
    }

    private void update(final long now) {
        clearMap();
        if (mapImage != null) {
            this.getGraphicsContext2D().drawImage(mapImage, 0, 0,
                this.getWidth(), this.getHeight());
        }
        drawGrids();

        if (this.followingEntity.get() && this.followedEntity.isDead()) {
            if (!camera.isZooming()) {
                this.unfollowEntity(followedEntity);
            }
            followingEntity.set(false);
            followedEntity = null;
            untrackEntityStats();
        }

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
                    if (followedEntity != null && !camera.isZooming()) {
                        this.unfollowEntity(followedEntity);
                        followingEntity.set(false);
                        followedEntity = null;
                        untrackEntityStats();
                    }
                }
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

                simulation.forEachGridEntities(x, y, entity -> {
                    try {
                        drawEntity(entity);
                    } catch (EvosimException e) {
                        throw new RuntimeException(e);
                    }
                });
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

        Point abs = this.relToAbsPosition(x, y);

        int chunkX = (int) abs.getX() / this.simulation.getGridSize();
        int chunkY = (int) abs.getY() / this.simulation.getGridSize();

        if (chunkX < 0 || chunkX >= this.simulation.getMapSizeX()
            || chunkY < 0 || chunkY >= this.simulation.getMapSizeY()) {
            return;
        }

        // check in this grid and nearby grids
        int radius = 1;
        for (int x1 = chunkX - radius; x1 <= chunkX + radius; ++x1) {
            for (int y1 = chunkY - radius; y1 <= chunkY + radius; ++y1) {
                if (
                    x1 < 0 || x1 >= this.simulation.getMapSizeX()
                        || y1 < 0 || y1 >= this.simulation.getMapSizeY()
                ) {
                    continue;
                }

                simulation.forEachGridEntities(x1, y1, entity -> {
                    Point entityCenter = entity.getBodyCenter();
                    try {
                        if (Formulas.distance(entityCenter.getX(), entityCenter.getY(),
                            abs.getX(), abs.getY())
                            <= Configuration.getConfiguration().getEntityRadius() * 2
                        ) {

                            if (!followingEntity.get() && !camera.isZooming()) {
                                this.followEntity(entity);
                                followingEntity.set(true);
                                followedEntity = entity;
                                trackEntityStats();
                                MainController.getController().getNeuralNetworkTab()
                                    .setNeuralNetwork(entity.getBrain());
                            }
                        }
                    } catch (EvosimException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
    }

    /**
     * Enables the entity stats and begins {@code updStat} to update the values for the entity.
     */
    private void trackEntityStats() {
        MainController.getController().getEntityStats().setVisible(true);
        updSensorsStats.start();
    }

    /**
     * Stops the animation timer tracking the entity.
     */
    private void untrackEntityStats() {
        MainController.getController().getEntityStats().setVisible(false);
        updSensorsStats.stop();
    }

    /**
     * Setting the map color.
     *
     * @param newColor
     */
    public static void setMapColor(final Color newColor) {
        MapCanvas.mapColor = newColor;
        Configuration.getConfiguration().setColorMap(newColor);
    }

}
