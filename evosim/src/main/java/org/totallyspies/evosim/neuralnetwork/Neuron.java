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
     * Creates a Neuron with a randomly generated bias.
     */
    public Neuron() {
        this.bias = Rng.RNG.nextDouble(0d, 1d);
    }

    /**
     * Creates a Neuron with a predefined bias.
     *
     * @param bias the bias used to shift the activation function
     */
    public Neuron(double bias) {
        this.bias = bias;
    }

    public double getBias() {
        return this.bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

}
