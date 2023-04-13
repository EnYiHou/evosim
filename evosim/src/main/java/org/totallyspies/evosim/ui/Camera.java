package org.totallyspies.evosim.ui;

import org.totallyspies.evosim.geometry.Point;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * This class represents a moveable camera.
 *
 * @author EnYi
 */
public final class Camera {

    /**
     * The default position of the camera.
     */
    public static final Point DEFAULT_POINT =
            new Point(0, 0);

    /**
     * The maximum zoom level of the camera.
     * <p>
     * This value indicates the maximum scale constant when the camera is zoomed out to its min.
     */
    public static final double MAX_ZOOM = 1.55;

    /**
     * The minimum zoom level of the camera.
     * <p>
     * This value indicates the minimum scale constant when the camera is zoomed out to its max.
     */
    public static final double MIN_ZOOM = 0.145;

    /**
     * The default zoom level of the camera.
     */
    public static final double DEFAULT_ZOOM = MAX_ZOOM;

    /**
     * The default zooming speed of the camera.
     */
    public static final double DEFAULT_ZOOMING_SPEED = 0.003;

    /**
     * The speed at which the camera translates.
     */
    public static final double CAMERA_TRANSLATE_SPEED = 7.5;

    /**
     * The increment value of the camera's zoom.
     */
    public static final double CAMERA_ZOOM_INCREMENT = 0.01;

    /**
     * The position of the camera.
     *
     * @see Point
     */
    private Point point;

    /**
     * The zoom level of the camera.
     */
    private AtomicDouble zoom;

    /**
     * Top left limit of pan.
     */
    final Point topLeft;

    /**
     * Bottom right limit of pan.
     */
    final Point bottomRight;

    /**
     * Creates a new camera with the given position and zoom level.
     *
     * @param newPoint the position of the camera
     * @param newZoom  the zoom level of the camera
     */
    public Camera(final Point newPoint, final double newZoom, final Point newTopLeft, final Point newBottomRight) {
        this.point = newPoint;
        this.zoom = new AtomicDouble(newZoom);
        this.topLeft = newTopLeft;
        this.bottomRight = newBottomRight;
    }

    /**
     * Creates a new camera with a default point and a default zoom level of 1.
     */
    public Camera(final Point topLeft, final Point bottomRight) {
        this(DEFAULT_POINT, DEFAULT_ZOOM, topLeft, bottomRight);
    }

    /**
     * Sets the camera back to the position.
     */
    public void center() {
        this.point.setX((this.bottomRight.getX() - this.topLeft.getX()) / 2.0);
        this.point.setY((this.bottomRight.getY() - this.topLeft.getY()) / 2.0);
    }

    /**
     * Translates the camera by the given amount in x direction.
     *
     * @param x the amount to translate the camera in x direction
     *          (positive values move the camera to the right)
     */
    public void translateX(final double x) {
        if (this.point.getX() + x >= this.topLeft.getX() && this.point.getX() + x <= this.bottomRight.getX()) {
            this.point.setX(this.point.getX() + x);
        }
    }

    /**
     * Translates the camera by the given amount in y direction.
     *
     * @param y the amount to translate the camera in y direction
     *          (positive values move the camera down)
     */
    public void translateY(final double y) {
        if (this.point.getY() + y <= this.bottomRight.getY() && this.point.getY() + y >= this.topLeft.getY()) {
            this.point.setY(this.point.getY() + y);
        }
    }

    /**
     * Zoom the camera by the given amount.
     *
     * @param increment the amount to zoom the camera
     *                  (positive values to zoom in the camera)
     */
    public void zoom(final double increment) {
        if (this.zoom.get() + increment >= Camera.MIN_ZOOM
            && this.zoom.get() + increment <= Camera.MAX_ZOOM) {
            this.zoom.getAndAdd(increment);
        }
    }

    /**
     * Zoom the camera by the default amount.
     */
    public void zoom() {
        this.zoom(CAMERA_ZOOM_INCREMENT);
    }

    /**
     * Unzoom the camera by the default amount.
     */
    public void unzoom() {
        this.zoom(-CAMERA_ZOOM_INCREMENT);
    }

    /**
     * Returns absolute the x-coordinates of the camera position.
     *
     * @return the x-coordinates of the camera position
     */
    public double getX() {
        return this.point.getX();
    }

    /**
     * Returns absolute the y-coordinates of the camera position.
     *
     * @return the y-coordinates of the camera position
     */
    public double getY() {
        return this.point.getY();
    }

    public Point getPoint() {
        return this.point;
    }

    /**
     * Set the x and y coordinates of the camera.
     *
     * @param newPoint the x coordinate of the camera
     */
    public void setPoint(final Point newPoint) {
        this.point = newPoint;
    }

    public double getZoom() {
        return this.zoom.get();
    }

}
