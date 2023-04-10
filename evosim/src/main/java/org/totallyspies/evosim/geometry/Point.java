package org.totallyspies.evosim.geometry;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * This class represents a point in 2D space.
 *
 * @author EnYi Hou
 */
public class Point {

    /**
     * The x coordinate of the point.
     */
    private AtomicDouble x;

    /**
     * The y coordinate of the point.
     */
    private AtomicDouble y;

    /**
     * Creates a new point with the given coordinates.
     *
     * @param positionX the x coordinate of the point
     * @param positionY the y coordinate of the point
     */
    public Point(final double positionX, final double positionY) {
        this.x = new AtomicDouble(positionX);
        this.y = new AtomicDouble(positionY);
    }

    /**
     * Creates a new point with the coordinates (0, 0).
     */
    public Point() {
        this(0d, 0d);
    }

    /**
     * Sets the coordinates of the point.
     *
     * @param positionX the x coordinate of the point
     * @param positionY the y coordinate of the point
     */
    public final void setCoordinates(final double positionX, final double positionY) {
        this.x.set(positionX);
        this.y.set(positionY);
    }

    public final double getX() {
        return this.x.get();
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public final void setX(final double positionX) {
        this.x.set(positionX);
    }

    public final double getY() {
        return this.y.get();
    }


    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public final void setY(final double positionY) {
        this.y.set(positionY);
    }
}
