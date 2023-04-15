package org.totallyspies.evosim.ui;

import org.totallyspies.evosim.geometry.Point;

import com.google.common.util.concurrent.AtomicDouble;

public final class Camera {

    /**
     * The default zoom level of the camera.
     */
    public static final double DEFAULT_ZOOM = 1.0d;

    /**
     * The default position of the camera.
     */
    public static final Point DEFAULT_POINT = new Point(0d, 0d);

    /**
     * The maximum zoom level of the camera.
     */
    public static final double MAX_ZOOM = 10.0d;

    /**
     * The minimum zoom level of the camera.
     */
    public static final double MIN_ZOOM = 0.05d;

    /**
     * The default zooming speed of the camera.
     */
    public static final double DEFAULT_ZOOMING_SPEED = 0.003d;


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
     * Creates a new camera with the given position and zoom level.
     *
     * @param newPoint the position of the camera
     * @param newZoom  the zoom level of the camera
     */
    public Camera(final Point newPoint, final double newZoom) {
        this.point = newPoint;
        this.zoom = new AtomicDouble(newZoom);
    }

    /**
     * Creates a new camera with a default point and a default zoom level of 1.
     */
    public Camera() {
        this(DEFAULT_POINT, DEFAULT_ZOOM);
    }

    /**
     * Translates the camera by the given amount in x direction.
     *
     * @param x the amount to translate the camera in x direction
     *          (positive values move the camera to the right)
     */
    public void translateX(final double x) {
        this.point.setX(this.point.getX() + x);
    }

    /**
     * Translates the camera by the given amount in y direction.
     *
     * @param y the amount to translate the camera in y direction
     *          (positive values move the camera down)
     */
    public void translateY(final double y) {
        this.point.setY(this.point.getY() + y);
    }

    /**
     * Zoom the camera by the given amount.
     *
     * @param increment the amount to zoom the camera
     *                  (positive values to zoom in the camera)
     */
    public void zoom(final double increment) {
        if (this.zoom.addAndGet(increment) >= Camera.MIN_ZOOM
                && this.zoom.addAndGet(increment) <= Camera.MAX_ZOOM) {
            this.zoom.getAndAdd(increment);
        }
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
