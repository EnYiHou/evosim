package org.totallyspies.evosim.geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a point in 2D space.
 * @author ptrstr
 */
@Getter
@Setter
@AllArgsConstructor
public class Coordinate {

    /**
     * The x coordinate of the point.
     */
    private int x;

    /**
     * The y coordinate of the point.
     */
    private int y;

    /**
     * Creates a new point with the coordinates (0, 0).
     */
    public Coordinate() {
        this(0, 0);
    }

    /**
     * Sets the coordinates of the point.
     *
     * @param positionX the x coordinate of the point
     * @param positionY the y coordinate of the point
     */
    public final void setCoordinates(final int positionX, final int positionY) {
        this.x = positionX;
        this.y = positionY;
    }
}
