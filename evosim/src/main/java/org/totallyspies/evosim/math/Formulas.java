package org.totallyspies.evosim.math;

import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.geometry.Line;

import java.util.List;
import java.util.function.Function;
import org.totallyspies.evosim.geometry.Point;

/**
 * This class contains all the activation functions used during the neural-network computation.
 *
 * @author EnYi
 */
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "checkstyle:MagicNumber"})
public final class Formulas {

    /**
     * Logistic sigmoid activation function.
     *
     * @param x input value
     * @return activated value
     */
    public static double logistic(final double x) {
        return (1 / (1 + Math.exp(-x)));
    }

    /**
     * Hyperbolic tangent activation function.
     *
     * @param x input value
     * @return activated value
     */
    public static double hyperbolicTangent(final double x) {
        return Math.tanh(x);
    }

    /**
     * Arc tangent activation function.
     *
     * @param x input value
     * @return activated value
     */
    public static double arctangent(final double x) {
        return Math.atan(x);
    }

    /**
     * List of all activation functions.
     */
    public static final List<Function<Double, Double>> ACTIVATION_FUNCTIONS =
            List.of(
                    Formulas::logistic,
                    Formulas::hyperbolicTangent,
                    Formulas::arctangent
            );

    /**
     * Calculates the distance between a line and circle.
     * <p>
     * If the line is outside the circle, the distance is the length of the line.
     * <p>
     * If the line is inside the circle, the distance is 0.
     * <p>
     * If the line intersects the circle, the distance is the distance between the start of the
     * line and the closest intersection.
     *
     * @param line   the line
     * @param circle the circle
     * @return the closest distance between the line and the circle
     */
    public static Double closestIntersection(final Line line, final Circle circle) {
        double slope;
        double yInt;
        double lineStartX = line.getStartPoint().getX();
        double lineStartY = line.getStartPoint().getY();
        double lineEndX = line.getEndPoint().getX();
        double lineEndY = line.getEndPoint().getY();
        final double circleCenterX = circle.getCenter().getX();
        final double circleCenterY = circle.getCenter().getY();
        final double circleRadius = circle.getRadius();
        double deltaX = lineEndX - lineStartX;
        double length = distance(lineStartX, lineStartY, lineEndX, lineEndY);

        if (distance(circleCenterX, circleCenterY, lineStartX, lineStartY)
                - circleRadius > length) {
            return length;

        }
        if (deltaX == 0) {
            double x = lineStartX;
            double sqrt = Math.sqrt(circleRadius * circleRadius
                    - (x - circleCenterX) * (x - circleCenterX));
            double distance1 = distance(x, sqrt
                    + circleCenterY, lineStartX, lineStartY);
            double distance2 = distance(x, -sqrt
                    + circleCenterY, lineStartX, lineStartY);

            return Math.min(distance1, distance2);
        }

        if (distance(circleCenterX, circleCenterY, lineStartX, lineStartY)
            - circleRadius < 0) {
            return 0d;
        } else {
            //calculate the slope of the line
            slope = (lineEndY - lineStartY) / (deltaX);
            //calculate the y intercept of the line
            yInt = lineStartY - (slope * lineStartX);

            double a;
            double b;
            double c;

            a = (slope * slope + 1);
            b = (-2 * circleCenterX) + 2 * slope * (yInt - circleCenterY);
            c = circleCenterX * circleCenterX + (yInt - circleCenterY)
                    * (yInt - circleCenterY) - circleRadius * circleRadius;
            double[] results = getQuadraticEquationResults(a, b, c);

            if (results.length == 0) {
                return length;
            }
            if ((results[0] > lineEndX || results[0] < lineStartX)
                    && (results[1] > lineEndX || results[1] < lineStartX)) {
                return length;
            }
            if (results[0] <= lineEndX && results[0] >= lineStartX
                    && (results[1] > lineEndX || results[1] < lineStartX)) {
                return distance(lineStartX, lineStartY,
                        results[0], slope * results[0] + yInt);
            }
            if (results[1] <= lineEndX && results[1] >= lineStartX
                    && (results[0] > lineEndX || results[0] < lineStartX)) {
                return distance(lineStartX, lineStartY,
                        results[1], slope * results[1] + yInt);
            } else {
                double distance1 = distance(results[0], slope * results[0] + yInt,
                        lineStartX, lineStartY);
                double distance2 = distance(results[1],
                        slope * results[1] + yInt,
                        lineStartX, lineStartY);

                return (distance1 <= distance2 ? distance1 : distance2);

            }
        }
    }

    /**
     * Calculates the distance between two points.
     *
     * @param x1 x coordinate of the first point
     * @param y1 y coordinate of the first point
     * @param x2 x coordinate of the second point
     * @param y2 y coordinate of the second point
     * @return the distance between the two points
     */
    public static double distance(final double x1, final double y1,
                                  final double x2, final double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Solves a quadratic equation. a * x^2 + b * x + c = 0
     *
     * @param a the a value in the quadratic equation
     * @param b the b value in the quadratic equation
     * @param c the c value in the quadratic equation
     * @return an array of the results of the quadratic equation
     */
    public static double[] getQuadraticEquationResults(final double a,
                                                       final double b,
                                                       final double c) {
        double determinant = (b * b) - (4 * a * c);
        if (determinant < 0) {
            return new double[0];

        } else {
            double x1 = (-b + Math.sqrt(determinant)) / (2 * a);
            double x2 = (-b - Math.sqrt(determinant)) / (2 * a);
            double[] results = new double[2];
            results[0] = x1;
            results[1] = x2;

            return results;
        }
    }

    /**
     * Normalizes angle to be within the range -pi to pi.
     * @param angle The angle in radians to be normalized.
     * @return The normalized angle.
     */
    public static double normAngle(final double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }

    /**
     * Given a point and an angle, computes the distance between a circle and that point at the
     * given angle.
     * @param point The point to calculate distance from.
     * @param angle The angle at which to calculate the distance.
     * @param circle The circle to calculate.
     * @return The distance, or {@link Double#POSITIVE_INFINITY} if impossible.
     */
    public static double distanceCircleAngled(
        final Point point,
        final double angle,
        final Circle circle
    ) {
        final double circlePointCenterAngle = Math.atan2(
            circle.getCenter().getY() - point.getY(),
            circle.getCenter().getX() - point.getX()
        );

        final double deltaAngle = circlePointCenterAngle - angle;

        final double circlePointDistance = distance(
            point.getX(),
            point.getY(),
            circle.getCenter().getX(),
            circle.getCenter().getY()
        );

        final double midpointDistance = circlePointDistance * Math.cos(deltaAngle);
        if (midpointDistance < 0) {
            return Double.POSITIVE_INFINITY;
        }

        final double midpointX = point.getX() + midpointDistance * Math.cos(angle);
        final double midpointY = point.getY() + midpointDistance * Math.sin(angle);

        final double midpointCircleCenterDistance = distance(
            midpointX,
            midpointY,
            circle.getCenter().getX(),
            circle.getCenter().getY()
        );

        if (circle.getRadius() < midpointCircleCenterDistance) {
            return Double.POSITIVE_INFINITY;
        }

        final double distanceDelta = Math.sqrt(
            Math.pow(circle.getRadius(), 2) - Math.pow(midpointCircleCenterDistance, 2)
        );

        return distance(point.getX(), point.getY(), midpointX, midpointY) - distanceDelta;
    }
}
