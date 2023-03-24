package org.totallyspies.evosim.neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
 * @author Matthew
 */
public class NeuralNetwork {

  /**
   * A list containing each list of Neurons, which each represent a layer.
   */
  private List<List<Neuron>> neuronsLayers;

  /**
   * The activation function randomly selected.
   */
  private Function<Double, Double> activationFunction;

  /**
   * An array with the same size as the greatest neuron layer used to store the
   * result of all computations.
   * <p>
   * The final decision of the network will be stored within the length of
   * indices equal to the size of the output layer.
   * </p>
   */
  private double[] computations;

  /**
   * Constructs a Neural Network with a set number of layers with specific
   * sizes.
   *
   * @param layers a list of integers representing the size of each layer. the
   *               number of elements in the list is the number of layers.
   */
  public NeuralNetwork(final List<Integer> layerSizes) {
    this.neuronsLayers = new ArrayList<>();

    this.activationFunction = Formulas.ACTIVATION_FUNCTIONS.get(
        Rng.RNG.nextInt(0, Formulas.ACTIVATION_FUNCTIONS.size()));

    // populate neural network
    int maxLayerSize = 0;
    int lastSize = 0;
    for (int curSize : layerSizes) { // layer number
      int finalLastSize = lastSize;
      this.neuronsLayers.add(
          Stream
              .generate(() -> new Neuron(finalLastSize))
              .limit(curSize)
              .toList()
      );
      if (curSize > maxLayerSize) {
        maxLayerSize = curSize;
      }
      lastSize = curSize;
    }

    // create an array with the size of the largest neuron layer
    this.computations = new double[maxLayerSize];
  }

  /**
   * Calculates the network's final decision.
   * <p>
   * This method calls
   * {@link #computeLayer(double[], java.util.List, java.util.List, boolean)
   * computeLayer} and {@link #passForwards(double[], double[]) passForwards} to
   * calculate the dot products between each weight layer and their associated
   * neuron layer. After the first layer, the inputs are activated via the
   * network's assigned activation function before computation. Afterwards,
   * they're multiplied by the neurons bias. The array of computations is
   * returned on completion.
   * </p>
   *
   * @param inputs the original inputs from the Entity sensors
   * @return an array containing the final computations. the only values of
   * importance are the elements within the length equal to the number of
   * neurons in the output layer.
   */
  public double[] calcNetworkDecision(final List<Double> inputs) {
    boolean isFirstLayer = true;

    // calculate decision for each layer
    for (int i = 0; i < this.weightsLayers.size(); i++) {
      if (isFirstLayer) {
        passForwards(
            computeLayer(
                inputs.stream().mapToDouble(x -> x).toArray(),
                this.weightsLayers.get(i),
                this.neuronsLayers.get(i + 1),
                true),
            this.computations);

        isFirstLayer = false;
      } else {
        passForwards(
            computeLayer(this.computations,
                this.weightsLayers.get(i),
                this.neuronsLayers.get(i + 1),
                isFirstLayer),
            this.computations);
      }
    }
    return this.computations;
  }

  /**
   * Computes the resultant layer of the neural network.
   * <p>
   * This computes the dot products between the input values and the weights for
   * one layer of the neural network. If the layer is not the first layer, each
   * value must be activated using the network's activation function before
   * computation and must be multiplied by the neuron's bias after. Returns the
   * array of dot product results at the end.
   * </p>
   *
   * @param inputs       an array of input values, either the inputs from the
   *                     sensors or results from previous layers
   * @param weightsList  the layer of weights between the two neuron layers
   * @param neuronsList  the target layer of neurons
   * @param isFirstLayer a boolean, true if it's the first layer, else false
   * @return the array of dot product results
   */
  private double[] computeLayer(final double[] inputs,
      final List<List<Double>> weightsList,
      final List<Neuron> neuronsList,
      final boolean isFirstLayer) {
    double[] dotProducts = new double[weightsList.size()];

    // activate each value before computation
    if (!isFirstLayer) {
      for (int i = 0; i < neuronsList.size(); i++) {
        inputs[i] = this.activationFunction.apply(inputs[i]);
      }
    }

    // compute dot product between inputs & weights for each target neuron
    for (int i = 0; i < weightsList.size(); i++) {
      double dotProduct = 0;
      for (int j = 0; j < weightsList.get(i).size(); j++) {
        dotProduct += inputs[j] * weightsList.get(i).get(j);
      }
      dotProducts[i] = dotProduct;
    }

    // multiply each result by the bias
    if (!isFirstLayer) {
      for (int i = 0; i < neuronsList.size(); i++) {
        dotProducts[i] += neuronsList.get(i).getBias();
      }
    }

    return dotProducts;
  }

  /**
   * Passes forwards the results from the layer computation to a destination
   * array.
   * <p>
   * This overwrites values written in the destination array but does not modify
   * any values past the length of the source array.
   * </p>
   *
   * @param src  array containing results from layer computation
   * @param dest target array to copy values into
   */
  private void passForwards(final double[] src, final double[] dest) {
    System.arraycopy(src, 0, dest, 0, src.length);
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
  private NeuralNetwork cloneAndMutate() {
    return null;
  }

}
