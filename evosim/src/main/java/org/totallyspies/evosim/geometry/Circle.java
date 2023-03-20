package org.totallyspies.evosim.geometry;

/**
 * This class represents a circle in 2D space.
 */
public class Circle {

    /**
     * The radius of the circle.
     */
    private double radius;

    /**
     * The center of the circle.
     */
    private Point center;

    /**
     * Creates a new circle with the given center and radius.
     * @param centerPoint the center of the circle
     * @param newRadius the radius of the circle
     */
    public Circle(final Point centerPoint, final double newRadius) {
        this.center = centerPoint;
        this.radius = newRadius;
    }

    /**
     * Creates a new circle with the given center and radius.
     * @param centerX the x coordinate of the center of the circle
     * @param centerY the y coordinate of the center of the circle
     * @param newRadius the radius of the circle
     */
    public Circle(final double centerX, final double centerY,
                  final double newRadius) {
        this(new Point(centerX, centerY), newRadius);
    }

    public final Point getCenter() {
        return this.center;
    }

    public final double getRadius() {
        return this.radius;
    }
}
