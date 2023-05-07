package org.totallyspies.evosim.neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.ToString;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.utils.Rng;

/**
 * A Neural Network is a series of layers filled with Neuron objects that each store a bias.
 * Between two adjacent layers, each Neuron is connected to one another through a weight
 * represented by a double value that are initially randomly generated. The first layer's Neuron
 * values (input layer) are all data collected by the Entity sensors. Every other layer has their
 * values calculated by taking the dot product of the list of Neuron values and the list of
 * weights. The final layer's values (output layer) determines what the Entity's decision will be.
 *
 * @author mattlep11, niakouu
 */
@ToString
@Getter
public class NeuralNetwork {

    /**
     * A list containing each list of Neurons, which each represent a layer.
     */
    private List<List<Neuron>> neuronLayers;


    /**
     * Arrays used for calculating the output of the neural network.
     * This limits the number of newly instantiated classes on each calculation.
     */
    private double[][] calculationArrays;

    /**
     * Constructs a Neural Network with a set number of layers with specific sizes.
     *
     * @param layerSizes a list of integers representing the size of each layer. the number of
     *                   elements in the list is the number of layers.
     */
    public NeuralNetwork(final List<Integer> layerSizes) {
        this.neuronLayers = new ArrayList<>();

        int activationFunctionIndex = Rng.RNG.nextInt(0, Formulas.ACTIVATION_FUNCTIONS.size());

        // populate neural network
        int lastSize = 0;
        int maxSize = 0;
        for (int curSize : layerSizes) { // layer number
            int finalLastSize = lastSize;

            this.neuronLayers.add(
                Stream
                    .generate(() -> finalLastSize == 0
                        ? new Neuron(1, activationFunctionIndex, false)
                        : new Neuron(finalLastSize, activationFunctionIndex, true))
                    .limit(curSize)
                    .toList()
            );

            lastSize = curSize;

            if (maxSize < curSize) {
                maxSize = curSize;
            }
        }

        this.calculationArrays = new double[2][maxSize];
    }

    /**
     * Constructor used internally to not have to initialize the {@link #neuronLayers} expensively.
     */
    private NeuralNetwork() {
        this.neuronLayers = null;
        this.calculationArrays = null;
    }

    /**
     * Calculates the network's final decision.
     *
     * @param inputs the original inputs from the Entity sensors
     * @return a List containing the final computations. the only values of importance are the
     * elements within the length equal to the number of neurons in the output layer.
     */
    public double[] calcNetworkDecision(final double[] inputs) {
        int flipFlop = 0;

        if (inputs.length != this.neuronLayers.get(0).size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(inputs, 0, this.calculationArrays[flipFlop], 0, inputs.length);

        for (List<Neuron> layer : this.neuronLayers) {
            final int newFlipFlop = ~flipFlop & 1;
            for (int i = 0; i < layer.size(); ++i) {
                this.calculationArrays[newFlipFlop][i] = layer.get(i).feedUnchecked(
                    this.calculationArrays[flipFlop]
                );
            }

            flipFlop = newFlipFlop;
        }

        return this.calculationArrays[flipFlop];
    }

    /**
     * Clones and mutates the current neural network for another entity.
     * <p>
     * The neural network is copied for use of new entity. To mutate it, the values of all the
     * weights and biases are regenerated based on a set mutation rate.
     * </p>
     *
     * @return the mutated brain
     */
    public NeuralNetwork mutate() {
        NeuralNetwork mutatedNeuralNetwork = new NeuralNetwork();

        mutatedNeuralNetwork.neuronLayers =
            this.neuronLayers
                .stream()
                .map(neurons -> neurons
                    .stream()
                    .map(neuron -> neuron.mutate(
                        Configuration.getConfiguration().getEntitySpeedMutationRate())

                    )
                    .toList()
                )
                .toList();

        mutatedNeuralNetwork.calculationArrays =
            new double[this.calculationArrays.length][this.calculationArrays[0].length];

        return mutatedNeuralNetwork;
    }

    /**
     * Function called by Jackson to deserialize the neural network.
     * Cannot be a constructor as clashing constructor exists.
     * @param neurons The neurons of the network.
     * @return The neural network containing the given neurons.
     */
    @JsonCreator
    public static NeuralNetwork fromNeurons(final List<List<Neuron>> neurons) {
        NeuralNetwork nn = new NeuralNetwork();
        nn.neuronLayers = neurons;
        return nn;
    }
}
