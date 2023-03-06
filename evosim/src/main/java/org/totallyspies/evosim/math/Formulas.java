package org.totallyspies.evosim.math;

import java.util.List;
import java.util.function.Function;

/**
 * This class contains all the activation functions
 * used during the neural-network computation.
 */
public final class Formulas {
    private Formulas() {
    }

    /**
     * Logistic sigmoid activation function.
     * @param x input value
     * @return activated value
     */
    public static double logistic(final double x) {
        return (1 / (1 + Math.exp(-x)));
    }

    /**
     * Hyperbolic tangent activation function.
     * @param x input value
     * @return activated value
     */
    public static double hyperbolicTangent(final double x) {
        return Math.tanh(x);
    }

    /**
     * Arctangent activation function.
     *
     * @param x input value
     * @return activated value
     */
    public static double arctangent(final double x) {
        return Math.atan(x);
    }

    /**
     * ReLU activation function.
     * @param x input value
     * @return activated value
     */
    public static double relu(final double x) {
        return Math.max(0, x);
    }

    /**
     * List of all activation functions.
     */
    public static final List<Function<Double, Double>> ACTIVATION_FUNCTIONS =
        List.of(
            Formulas::logistic,
            Formulas::hyperbolicTangent,
            Formulas::arctangent,
            Formulas::relu
        );
}
