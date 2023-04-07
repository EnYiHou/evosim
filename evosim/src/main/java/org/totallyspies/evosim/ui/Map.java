package org.totallyspies.evosim.ui;

import javafx.scene.paint.Color;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.fxml.ResizableCanvas;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.utils.Configuration;

/**
 * This class represents the map of the simulation, on which entities will be drawn.
 */
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
   * The number of grids columns on the map.
   */
  public static final int ROW_COLUMN_COUNT = Map.MAP_SIZE / Map.GRID_SIZE;

  /**
   * /**
   * The camera of the map.
   */
  private Camera camera;

  /**
   * The instance of the map.
   */
  private static Map instance = new Map();

  /**
   * Construct an instance of Map.
   */
  private Map() {
    super();
    this.camera = new Camera();
  }

  /**
   * Draw the grid lines on the map.
   */
  private void drawGrids() {
    double zoom = camera.getZoom();
    this.getGraphicsContext2D().setStroke(Color.LIGHTGRAY);


    for (int i = 0; i <= Map.ROW_COLUMN_COUNT; i++) {
      double x = zoom * (Map.GRID_SIZE * i - camera.getX()) + this.getWidth() / 2;
      double y = zoom * (Map.GRID_SIZE * i - camera.getY()) + this.getHeight() / 2;

      // draw vertical grids lines
      this.getGraphicsContext2D().strokeLine(
              x, 0, x, this.getHeight());

      // draw horizontal grids lines
      this.getGraphicsContext2D().strokeLine(
              y, 0, y, this.getWidth());
    }
  }


  /**
   * Draw an entity on the map.
   *
   * @param entity The entity to be drawn on the map
   */
  private void drawEntity(final Entity entity) {
    double radius = Configuration.getCONFIGURATION().getEntityRadius();
    double zoom = camera.getZoom();

    this.getGraphicsContext2D().fillOval(
            (zoom * (entity.getBodyCenter().getX() - radius / 2 - this.camera.getX())) + this.getWidth() / 2,
            (zoom * (entity.getBodyCenter().getY() - radius / 2 - this.camera.getY())) + this.getHeight() / 2,
            radius * zoom,
            radius * zoom);
  }

  /**
   * Draw an entity's sensors on the map.
   *
   * @param entity The entity, whose sensors to be drawn on the map
   */
  private void drawEntitySensors(final Entity entity) {
    this.getGraphicsContext2D().setStroke(Color.RED);
    for (Line sensor : entity.getSensors()) {
      this.getGraphicsContext2D().strokeLine(
              sensor.getStartPoint().getX(), sensor.getStartPoint().getY(),
              sensor.getEndPoint().getX(), sensor.getEndPoint().getY());
    }
  }


  /**
   * Delete all drawings on the map.
   * Prepare to draw a new frame.
   */
  private void clearMap() {
    this.getGraphicsContext2D().clearRect(
            0, 0, Map.MAP_SIZE, Map.MAP_SIZE);
  }

  // Getters and setters

  /**
   * Returns the instance of the map.
   *
   * @return the instance of the map
   */
  public static Map getInstance() {
    return instance;
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
