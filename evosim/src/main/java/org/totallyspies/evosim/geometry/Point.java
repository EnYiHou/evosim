package org.totallyspies.evosim.geometry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/**
 * This class represents a point in 2D space.
 * @author EnYi
 */
@Getter
@Setter
@Builder
@Jacksonized
@AllArgsConstructor
@EqualsAndHashCode
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
        this.x = positionX;
        this.y = positionY;
    }
}
