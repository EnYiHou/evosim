package org.totallyspies.evosim.neuralnetwork;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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
     * The activation function randomly selected.
     */
    private Function<Double, Double> activationFunction;

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
     *
     * @param inputs the inputs that feed the neuron
     * @param newActivationFunction the function to initialize the value
     */
    public Neuron(
        final int inputs,
        final Function<Double, Double> newActivationFunction) {

        this.bias = Rng.RNG.nextDouble(BIAS_MIN, BIAS_MAX);

        this.weights = Stream
            .generate(() -> Rng.RNG.nextDouble(WEIGHT_MIN, WEIGHT_MAX))
            .limit(inputs)
            .toList();

        this.activationFunction = newActivationFunction;
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

    /**
     * Feeds the input forward.
     *
     * @param inputs outputs from the previous layer or root inputs of the
     *               network
     * @return dot product of activated inputs and weights
     */
    public double feed(final List<Double> inputs) {
        if (inputs.size() != this.weights.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return IntStream
            .range(0, inputs.size())
            .mapToDouble(
                i -> this.activationFunction.apply(inputs.get(i))
                    * this.weights.get(i))
            .sum() * this.bias;
    }
}
