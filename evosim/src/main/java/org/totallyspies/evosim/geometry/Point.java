package org.totallyspies.evosim.geometry;


/**
 * This class represents a point in 2D space.
 * @author EnYi Hou
 */
public class Point {

    /**
     * The x coordinate of the point.
     */
    private double x;
    /**
     * The y coordinate of the point.
     */
    private double y;
    /**
     * Creates a new point with the given coordinates.
     * @param positionX the x coordinate of the point
     * @param positionY the y coordinate of the point
     */
    public Point(final double positionX, final double positionY) {
        this.x = positionX;
        this.y = positionY;
    }

    /**
     * Creates a new point with the coordinates (0, 0).
     */
    public Point() {
        this(0, 0);
    }

    public final double getPositionX() {
        return x;
    }

    public final double getPositionY() {
        return y;
    }

    public final void setPositionX(final double positionX) {
        this.x = positionX;
    }

    public final void setPositionY(final double positionY) {
        this.y = positionY;
    }

}