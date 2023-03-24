package org.totallyspies.evosim.neuralnetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
 * @author Matthew Edelina
 */
public class NeuralNetwork {

    /**
     * A list containing each list of Neurons, which each represent a layer.
     */
    private List<List<Neuron>> neuronLayers;

    /**
     * Constructs a Neural Network with a set number of layers with specific
     * sizes.
     *
     * @param layerSizes a list of integers representing the size of each layer.
     *                   the number of elements in the list is the number of
     *                   layers.
     */
    public NeuralNetwork(final List<Integer> layerSizes) {
        this.neuronLayers = new ArrayList<>();

        Function<Double, Double> activationFunction =
            Formulas
                .ACTIVATION_FUNCTIONS
                .get(Rng.RNG.nextInt(0, Formulas.ACTIVATION_FUNCTIONS.size()));

        // populate neural network
        int lastSize = 0;
        for (int curSize : layerSizes) { // layer number
            int finalLastSize = lastSize;

            this.neuronLayers.add(
                Stream
                    .generate(() -> finalLastSize == 0
                        ? new Neuron(1, activationFunction, false)
                        : new Neuron(finalLastSize, activationFunction, true))
                    .limit(curSize)
                    .toList()
            );

            lastSize = curSize;
        }
    }

    private NeuralNetwork() {
        this.neuronLayers = null;
    }

    /**
     * Test the Neural Network.
     *
     * @param args
     */
    public static void main(final String[] args) {
        NeuralNetwork neuralNetwork = new NeuralNetwork(List.of(2, 2, 1));
        System.out.println(neuralNetwork.calcNetworkDecision(
            List.of(4d, 5d)));

        System.out.println(neuralNetwork.neuronLayers);
    }

    /**
     * Create a copy of this Neural Network.
     *
     * @return a copy of the Neural Network with similar variables
     */
    public NeuralNetwork copy() {

        return null;
    }

    /**
     * Calculates the network's final decision.
     *
     * @param inputs the original inputs from the Entity sensors
     * @return a List containing the final computations. the only values of
     * importance are the elements within the length equal to the number of
     * neurons in the output layer.
     */
    public List<Double> calcNetworkDecision(final List<Double> inputs) {
        Iterator<List<Neuron>> iterator = this.neuronLayers.iterator();
        List<Neuron> firstLayer = iterator.next();

        if (inputs.size() != firstLayer.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        List<Double> outputs = IntStream
            .range(0, inputs.size())
            .mapToDouble(i -> firstLayer.get(i).feed(List.of(inputs.get(i))))
            .boxed()
            .toList();

        System.out.println(outputs);

        while (iterator.hasNext()) {
            List<Double> latestOutputs = outputs;
            outputs = iterator.next()
                .stream()
                .map(neuron -> neuron.feed(latestOutputs))
                .toList();
            System.out.println(outputs);
        }

        return outputs;
    }

    /**
     * Clones and mutates the current neural network for another entity.
     * <p>
     * The neural network is copied for use of new entity. To mutate it, the
     * values of all the weights and biases are regenerated based on a set
     * mutation rate.
     * </p>
     *
     * @return the mutated brain
     */
    private NeuralNetwork mutate() {
        NeuralNetwork mutatedNeuralNetwork = new NeuralNetwork();

        // TODO: Use config for mutation rate
        mutatedNeuralNetwork.neuronLayers =
            this.neuronLayers
                .stream()
                .map(neurons -> neurons
                    .stream()
                    .map(neuron -> neuron.mutate(0.25))
                    .toList()
                )
                .toList();

        return mutatedNeuralNetwork;
    }

}
