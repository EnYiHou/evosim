package org.totallyspies.evosim.neuralnetwork;

import org.totallyspies.evosim.utils.Rng;

/**
 * A Neuron is a single node of the greater Neural Network system storing a
 * double valued bias. Each layer of the network is made up of many Neuron
 * objects.
 *
 * @author Matthew
 */
public class Neuron {

    /**
     * A double value to shift the results neural network's activation.
     */
    private double bias;

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
    public Neuron() {
        this.bias = Rng.RNG.nextDouble(BIAS_MIN, BIAS_MAX);
    }

    /**
     * Creates a Neuron with a predefined bias.
     *
     * @param neuronBias the bias used to shift the activation function
     */
    public Neuron(final double neuronBias) {
        this.bias = neuronBias;
    }

    public double getBias() {
        return this.bias;
    }

    public void setBias(final double bias) {
        this.bias = bias;
    }

}
