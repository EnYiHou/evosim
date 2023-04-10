package org.totallyspies.evosim.geometry;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class represents a circle in 2D space.
 */
@Getter
@AllArgsConstructor
public class Circle {

    /**
     * The radius of the circle.
     */
    private double radius;

    /**
     * The center of the circle.
     */
    private Point center;
}
