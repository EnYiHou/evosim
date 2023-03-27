package org.totallyspies.evosim.geometry;

/**
 * This class represents a line in 2D space.
 *
 * @author EnYi
 */
public class Line {

    /**
     * The start point of the line.
     */
    private final Point startPoint;

    /**
     * The end point of the line.
     */
    private final Point endPoint;

    /**
     * Creates a new line with the given start and end points.
     * @param start the start point
     * @param end the end point
     */
    public Line(final Point start, final Point end) {
        this.startPoint = start;
        this.endPoint = end;
    }

    /**
     * Creates a new line with the given start and end points.
     * @param startX the x coordinate of the start point
     * @param startY the y coordinate of the start point
     * @param endX the x coordinate of the end point
     * @param endY the y coordinate of the end point
     */
    public Line(final double startX, final double startY,
                final double endX, final double endY) {
        this(new Point(startX, startY), new Point(endX, endY));
    }

    public final Point getStartPoint() {
        return this.startPoint;
    }

    public final Point getEndPoint() {
        return this.endPoint;
    }
}
