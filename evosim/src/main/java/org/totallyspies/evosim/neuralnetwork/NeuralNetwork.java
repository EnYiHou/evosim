package org.totallyspies.evosim.neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.DoubleStream;
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

    private  NeuralNetwork() {
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
            within each weight layer, there are as many weight lists as 
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

    /**
     * Calculates the network's final decision.
     *
     * This method calls {@link #computeLayer(double[], java.util.List,
     * java.util.List, boolean) computeLayer} and {@link #passForwards(double[],
     * double[]) passForwards} to calculate the dot products between each weight
     * layer and their associated neuron layer. After the first layer, the
     * inputs are activated via the network's assigned activation function
     * before computation. Afterwards, they're multiplied by the neurons bias.
     * The array of computations is returned on completion.
     *
     * @param inputs the original inputs from the Entity sensors
     * @return an array containing the final computations. the only values of
     * importance are the elements within the length equal to the number of
     * neurons in the output layer.
     */
    public double[] calcNetworkDecision(final List<Double> inputs) {
        // create an array with the size of the largest neuron layer 
        int maxLayerIndex = 0;
        for (int i = 0; i < this.neurons.size(); i++)
            if (this.neurons.get(i).size()
                    > this.neurons.get(maxLayerIndex).size())
                maxLayerIndex = i;

        double[] decision = new double[this.neurons.get(maxLayerIndex).size()];
        boolean isFirstLayer = true;

        // calculate decision for each layer
        for (int i = 0; i < this.weights.size(); i++) {
            if (isFirstLayer) {
                passForwards(computeLayer(
                        DoubleStream.builder().build().toArray(),
                        this.weights.get(i),
                        this.neurons.get(i + 1),
                        isFirstLayer),
                        decision);
                isFirstLayer = false;
            } else {
                passForwards(computeLayer(decision,
                        this.weights.get(i),
                        this.neurons.get(i + 1),
                        isFirstLayer),
                        decision);
            }
        }
        return decision;
    }

    /**
     * Computes the resultant layer of the neural network.
     *
     * This computes the dot products between the input values and the weights
     * for one layer of the neural network. If the layer is not the first layer,
     * each value must be activated using the network's activation function
     * before computation and must be multiplied by the neuron's bias after.
     * Returns the array of dot product results at the end.
     *
     * @param inputs        an array of input values, either the inputs from the
     *                      sensors or results from previous layers
     * @param weights       the layer of weights between the two neuron layers
     * @param neurons       the target layer of neurons
     * @param isFirstLayer  a boolean, true if it's the first layer, else false
     * @return              the array of dot product results
     */
    private double[] computeLayer(double[] inputs,
            final List<List<Double>> weights, final List<Neuron> neurons,
            final boolean isFirstLayer) {
        double[] dotProducts = new double[weights.size()];

        // activate each value before computation
        if (!isFirstLayer)
            for (int i = 0; i < neurons.size(); i++)
                inputs[i] = this.activationFunction.apply(inputs[i]);

        // compute dot product between inputs & weights for each target neuron
        for (int i = 0; i < weights.size(); i++) {
            double dotProduct = 0;
            for (int j = 0; j < weights.get(i).size(); j++) {
                dotProduct += inputs[i] * weights.get(i).get(j);
            }
            dotProducts[i] = dotProduct;
        }

        // multiply each result by the bias 
        if (!isFirstLayer)
            for (int i = 0; i < neurons.size(); i++)
                dotProducts[i] *= neurons.get(i).getBias();

        return dotProducts;
    }

    /**
     * Passes forwards the results from the layer computation.
     *
     * This overwrites values written in the destination array but does not
     * modify any values past the length of the source array.
     *
     * @param src array containing results from layer computation
     * @param dest target array to copy values into
     */
    private void passForwards(final double[] src, double[] dest) {
        for (int i = 0; i < src.length; i++)
            dest[i] = src[i];
    }

}
