package org.totallyspies.evosim.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;

class FormulasTest {

  @Test
  public void testLogistic() {
    assertEquals(0.6224d, Formulas.logistic(0.5),
        0.0001d);
    assertEquals(0.5498d, Formulas.logistic(0.2),
        0.0001d);
  }

  @Test
  public void testHyperbolicTangent() {
    assertEquals(0.0996d, Formulas.hyperbolicTangent(0.1),
        0.0001d);
    assertEquals(0.7573d, Formulas.hyperbolicTangent(0.99),
        0.0001d);
  }

  @Test
  public void testArctangent() {
    assertEquals(0.5956d, Formulas.arctangent(0.67777d),
        0.0001d);
    assertEquals(0.7159d, Formulas.arctangent(0.87d),
        0.0001d);
  }

  @Test
  public void testClosestIntersection() {
    assertEquals(1.561, Formulas.closestIntersection(
            new Line(1.61d, 0.83d, 3.457, 6.371),
            new Circle(new Point(3.4d, 3.7d), 1.9d)),
          0.001d);

    assertEquals(0, Formulas.closestIntersection(
            new Line(2.556d, 3.668d, 3.457, 6.371),
            new Circle(new Point(3.4d, 3.7d), 1.9d)),
        0.001d);
  }

  @Test
  public void testDistance() {
    assertEquals(
        2.65d,
        Formulas.distance(3.4d, 5.6d, 0.99d, 4.5d),
        0.01d);
    assertEquals(
        1.12d,
        Formulas.distance(8.9d, 2.1d, 8.7d, 1d),
        0.01d);
  }

  @Test
  public void testQuadraticEquation() {
    double[] quadratic1 = Formulas.getQuadraticEquationResults(4d, -2d, -2d);
    assertEquals(1d, quadratic1[0], 0.01d);
    assertEquals(-0.5d, quadratic1[1]);

    double[] quadratic2 = Formulas.getQuadraticEquationResults(0.99d, -4d, -9d);
    assertEquals(5.65, quadratic2[0], 0.01d);
    assertEquals(-1.609d, quadratic2[1], 0.001d);
  }
}
