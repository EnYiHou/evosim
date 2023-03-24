package org.totallyspies.evosim.neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import org.totallyspies.evosim.utils.Rng;

/**
 * A Neuron is a single node of the greater Neural Network system storing a
 * double valued bias. Each layer of the network is made up of many Neuron
 * objects.
 *
 * @author Matthew
 */
public final class Neuron {

    /**
     * A double value to shift the results neural network's activation.
     */
    private final double bias;

    /**
     * The weights the Neuron has, which are necessery for computations.
     */
    private final List<Double> weights;

    /**
     * The upper bound used to generate random weights.
     */
    private static final double WEIGHT_MAX = 0.99d;

    /**
     * The lower bound used to generate random weights.
     */
    private static final double WEIGHT_MIN = -0.99d;

    /**
     * The upper bound for generating random biases.
     */
    private static final double BIAS_MAX = 1d;

    /**
     * The lower bound for generating random biases.
     */
    private static final double BIAS_MIN = 0d;

    /**
     * Creates a Neuron with a randomly generated bias.
     */
    public Neuron(final int inputs) {
        this.bias = Rng.RNG.nextDouble(BIAS_MIN, BIAS_MAX);

        this.weights = new ArrayList<>();
        for (int i = 0; i < inputs - 1; i++) {
            this.weights.add(Rng.RNG.nextDouble(WEIGHT_MIN, WEIGHT_MAX));
        }
    }

    /**
     * Creates a Neuron with a predefined bias.
     *
     * @param neuronBias the bias used to shift the activation function
     * @param neuronWeights the weight used to do the computations
     */
    public Neuron(final double neuronBias, final List<Double> neuronWeights) {
        this.bias = neuronBias;
        this.weights = neuronWeights;
    }

    public double getBias() {
        return this.bias;
    }

    public List<Double> getWeights() {
        return this.weights;
    }

}
