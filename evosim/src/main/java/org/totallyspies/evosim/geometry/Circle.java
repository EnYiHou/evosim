package org.totallyspies.evosim.geometry;

/**
 * This class represents a circle in 2D space.
 */
public class Circle {
    /**
     * The radius of the circle.
     */
    private double circleRadius;
    /**
     * The center of the circle.
     */
    private Point center;
    /**
     * Creates a new circle with the given center and radius.
     * @param centerPoint the center of the circle
     * @param radius the radius of the circle
     */
    public Circle(final Point centerPoint, final double radius) {
        this.center = centerPoint;
        this.circleRadius = radius;
    }
    /**
     * Creates a new circle with the given center and radius.
     * @param centerX the x coordinate of the center of the circle
     * @param centerY the y coordinate of the center of the circle
     * @param radius the radius of the circle
     */
    public Circle(final double centerX, final double centerY,
                  final double radius) {
        this(new Point(centerX, centerY), radius);
    }
    /**
     * get the center of the circle.
     * @return the center of the circle
     */
    public final Point getCenter() {
        return this.center;
    }
    /**
     * get the radius of the circle.
     * @return the radius of the circle
     */
    public final double getRadius() {
        return this.circleRadius;
    }
}