package org.totallyspies.evosim.neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.utils.Rng;

/**
 * A Neural Network is a series of layers filled with Neuron objects that each
 * store a bias. Between two adjacent layers, each Neuron is connected to one
 * another through a weight represented by a double value that are initially
 * randomly generated. The first layer's Neuron values (input layer) are all
 * data collected by the Entity sensors. Every other layer has their values
 * calculated by taking the dot product of the list of Neuron values and the
 * list of weights. The final layer's values (output layer) determines what the
 * Entity's decision will be.
 *
 * @author Matthew
 *
 */
public class NeuralNetwork {

    /**
     * A list containing each list of Neurons, which each represent a layer.
     */
    private List<List<Neuron>> neurons;

    /**
     * A 3D list containing all the weights of the network.
     *
     * Each neuron of the network has a weight attached to every other neuron
     * from the preceding layer. The first inner list selects which layer and
     * the second inner list selects which neuron of that layer to look at.
     */
    private List<List<List<Double>>> weights;

    /**
     * The activation function randomly selected
     */
    private Function<Double, Double> activationFunction;

    /**
     * The upper bound used to generate random weights.
     */
    private final double WEIGHT_MAX = 0.99d;

    /**
     * The lower bound used to generate random weights.
     */
    private final double WEIGHT_MIN = -0.99d;

    /**
     * Creates an empty Neural Network structure.
     */
    public NeuralNetwork() {
        // default
    }

    /**
     * Constructs a Neural Network with a set number of layers with specific
     * sizes.
     *
     * @param sizes a list of integers representing the size of each layer. the
     * number of elements in the list is the number of layers.
     */
    public NeuralNetwork(final List<Integer> sizes) {
        this.neurons = new ArrayList<List<Neuron>>();
        this.weights = new ArrayList<List<List<Double>>>();
        this.activationFunction = Formulas.ACTIVATION_FUNCTIONS.get(
                Rng.RNG.nextInt(0, Formulas.ACTIVATION_FUNCTIONS.size()));

        // populate neural network
        for (int i = 0; i < sizes.size(); i++) { // layer number
            neurons.add(new ArrayList<Neuron>());
            for (int j = 0; j < sizes.get(i); j++) { // layer size
                neurons.get(i).add(new Neuron());
            }
        }

        /*populate weights:
            within each weight layer, there are as many weight collections as 
            neurons on the output (right) side, and within each list there are 
            as many weights as neurons on the input (left) side.*/
        for (int i = 0; i < sizes.size() - 1; i++) {
            weights.add(new ArrayList<List<Double>>());
            int outputNeuronNum = sizes.get(i + 1);
            int inputNeuronNum = sizes.get(i);

            for (int j = 0; j < outputNeuronNum; j++) {
                weights.get(i).add(new ArrayList<Double>());
                for (int k = 0; k < inputNeuronNum; k++) {
                    weights.get(i).get(j).add(
                            Rng.RNG.nextDouble(WEIGHT_MIN, WEIGHT_MAX));
                }
            }
        }
    }

}
