package org.totallyspies.evosim.neuralnetwork;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Delegate;
import lombok.experimental.FieldNameConstants;
import lombok.extern.jackson.Jacksonized;
import org.json.JSONPropertyIgnore;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.utils.Rng;

/**
 * A Neuron is a single node of the greater Neural Network system storing a double valued bias.
 * Each layer of the network is made up of many Neuron objects.
 *
 * @author mattlep11, niakouu
 */
@ToString
@Getter
public final class Neuron {

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
   * A double value to shift the results neural network's activation.
   */
  private final double bias;

  /**
   * The weights the Neuron has, which are necessary for computations.
   */
  private final List<Double> weights;

  /**
   * The activation function randomly selected.
   */
  @JsonIgnore
  private Function<Double, Double> activationFunction;

  /**
   * The activation function id.
   */
  private final int activationFunctionIndex;

  /**
   * Creates a Neuron with a randomly generated bias.
   *
   * @param inputs                the inputs that feed the neuron
   * @param activationFunctionIndex the function index to initialize the value
   * @param randomizeWeights      if weight should be randomized
   */
  public Neuron(
      final int inputs,
      final int activationFunctionIndex,
      final boolean randomizeWeights
  ) {
    this.bias = Rng.RNG.nextDouble(Neuron.BIAS_MIN, Neuron.BIAS_MAX);

    this.weights = Stream
        .generate(() -> randomizeWeights ?
            Rng.RNG.nextDouble(Neuron.WEIGHT_MIN, Neuron.WEIGHT_MAX) : 1)
        .limit(inputs)
        .toList();

    this.activationFunctionIndex = activationFunctionIndex;

    this.activationFunction = Formulas.ACTIVATION_FUNCTIONS.get(activationFunctionIndex);
  }

  /**
   * Creates a Neuron with a predefined bias.
   *
   * @param neuronBias            the bias used to shift the activation function
   * @param neuronWeights         the weights used to do the computations
   * @param newActivationFunctionIndex new activation function to be used
   */
  @JsonCreator
  public Neuron(
          @JsonProperty("bias") final double neuronBias,
          @JsonProperty("weights") final List<Double> neuronWeights,
          @JsonProperty("activationFunctionIndex") final int newActivationFunctionIndex
  ) {
    this.bias = neuronBias;
    this.weights = neuronWeights;
    this.activationFunctionIndex = newActivationFunctionIndex;
    this.activationFunction = Formulas.ACTIVATION_FUNCTIONS.get(newActivationFunctionIndex);
  }

  /**
   * Feeds the input forward.
   *
   * @param inputs outputs from the previous layer or root inputs of the network
   * @return dot product of activated inputs and weights
   */
  public double feed(final List<Double> inputs) {
    if (inputs.size() != this.weights.size()) {
      throw new ArrayIndexOutOfBoundsException();
    }

    return this.activationFunction.apply(IntStream
        .range(0, inputs.size())
        .mapToDouble(i -> (inputs.get(i)) * this.weights.get(i))
        .sum() + this.bias);
  }

  /**
   * Mutates the neuron. For each field, if a random value between 0 and 1 is under the mutation
   * rate, the field (weight or bias) is randomized.
   *
   * @param mutationRate The mutation rate for the mutations.
   * @return New neuron based on parent
   */
  public Neuron mutate(final double mutationRate) {
    final double newBias = Rng.RNG.nextDouble() < mutationRate
        ? Rng.RNG.nextDouble(Neuron.BIAS_MIN, Neuron.BIAS_MAX)
        : this.bias;

    return new Neuron(
        newBias,
        this.weights.stream().map(
            w -> Rng.RNG.nextDouble() < mutationRate
                ? Rng.RNG.nextDouble(Neuron.WEIGHT_MIN, Neuron.WEIGHT_MAX)
                : w
        ).toList(),
        this.activationFunctionIndex
    );
  }

}
