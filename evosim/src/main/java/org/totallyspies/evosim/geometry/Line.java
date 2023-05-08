package org.totallyspies.evosim.geometry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * This class represents a line in 2D space.
 *
 * @author EnYi
 */
@Getter
@Builder
@Jacksonized
@AllArgsConstructor
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
     * @param startX the x coordinate of the start point
     * @param startY the y coordinate of the start point
     * @param endX   the x coordinate of the end point
     * @param endY   the y coordinate of the end point
     */
    public Line(final double startX, final double startY, final double endX, final double endY) {
        this(new Point(startX, startY), new Point(endX, endY));
    }
}
