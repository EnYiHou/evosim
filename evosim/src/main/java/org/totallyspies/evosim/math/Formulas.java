package org.totallyspies.evosim.math;
    
/**
 * This class contains all the activation functions
 * used during the neural-network computation.
 */
public class Formulas {
    private Formulas() {
    }
    /**
     * Logistic sigmoid activation function.
     * @param x input value
     * @return activated value
     */
    public static float logistic(final float x) {
        return (float) (1 / (1 + Math.exp(-x)));
    }

    /**
     * Hyperbolic tangent activation function.
     * @param x input value
     * @return activated value
     */
    public static float hyperbolicTangent(final float x) {
        return (float) Math.tanh(x);
    }

    /**
     * Arctangent activation function.
     *
     * @param x input value
     * @return activated value
     */
    public static float arctangent(final float x) {
        return (float) Math.atan(x);
    }

    /**
     * ReLU activation function.
     * @param x input value
     * @return activated value
     */
    public static float relu(final float x) {
        return Math.max(0, x);
    }
}
