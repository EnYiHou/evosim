package org.totallyspies.evosim.neuralnetwork;

import java.util.Arrays;
import java.util.function.Function;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.utils.Rng;

/**
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
  @Setter
  private double bias;

  /**
   * The value of the Neuron, which is the result of the activation function.
   */
  private double value;

  /**
   * The weights the Neuron has, which are necessary for computations.
   */
  private final double[] weights;

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
   * Value to clamp to.
   */
  @Setter
  private double clamp;

  /**
   * Creates a Neuron with a randomly generated bias.
   *
   * @param inputs                the inputs that feed the neuron
   * @param newActivationFunctionIndex the function index to initialize the value
   * @param randomizeWeights      if weight should be randomized
   */
  public Neuron(
      final int inputs,
      final int newActivationFunctionIndex,
      final boolean randomizeWeights
  ) {
    this(inputs, newActivationFunctionIndex, randomizeWeights, 1);
  }

  /**
   * Creates a Neuron with a randomly generated bias and a clamp.
   *
   * @param inputs                the inputs that feed the neuron
   * @param newActivationFunctionIndex the function index to initialize the value
   * @param randomizeWeights      if weight should be randomized
   * @param newClamp              value to clamp to
   */
  public Neuron(
      final int inputs,
      final int newActivationFunctionIndex,
      final boolean randomizeWeights,
      final double newClamp
  ) {
    this.bias = 0;

    this.weights = new double[inputs];
    for (int i = 0; i < this.weights.length; i++) {
      this.weights[i] = randomizeWeights
          ? Rng.RNG.nextDouble(Neuron.WEIGHT_MIN, Neuron.WEIGHT_MAX)
          : 1;
    }

    this.activationFunctionIndex = newActivationFunctionIndex;

    this.activationFunction = Formulas.ACTIVATION_FUNCTIONS.get(newActivationFunctionIndex);

    this.clamp = newClamp;
  }

  /**
   * Creates a Neuron with a predefined bias.
   *
   * @param neuronBias            the bias used to shift the activation function
   * @param neuronWeights         the weights used to do the computations
   * @param newActivationFunctionIndex new activation function to be used
   * @param newClamp              the value to clamp the inputs to
   */
  @JsonCreator
  public Neuron(
          @JsonProperty("bias") final double neuronBias,
          @JsonProperty("weights") final double[] neuronWeights,
          @JsonProperty("activationFunctionIndex") final int newActivationFunctionIndex,
          @JsonProperty("clamp") final Double newClamp
  ) {
    this.bias = neuronBias;
    this.weights = neuronWeights;
    this.activationFunctionIndex = newActivationFunctionIndex;
    this.activationFunction = Formulas.ACTIVATION_FUNCTIONS.get(newActivationFunctionIndex);
    this.clamp = newClamp;
  }

  /**
   * Feeds the input forward.
   *
   * @param inputs outputs from the previous layer or root inputs of the network
   * @return dot product of activated inputs and weights
   */
  public double feed(final double[] inputs) {
    if (inputs.length != this.weights.length) {
      throw new ArrayIndexOutOfBoundsException();
    }

    return this.feedUnchecked(inputs, 0);
  }

  /**
   * Feeds the input forward without checking the input size.
   *
   * @param inputs outputs from the previous layer or root inputs of the network
   * @param fromIndex starts at index
   * @return dot product of activated inputs and weights
   */
  public double feedUnchecked(final double[] inputs, final int fromIndex) {
    double sum = this.bias;

    for (int i = 0; i < this.weights.length; ++i) {
      sum += (inputs[fromIndex + i] / clamp) * this.weights[i];
    }

    this.value = sum;

    return Formulas.hyperbolicTangent(sum);
  }

  /**
   * Mutates the neuron. For each field, if a random value between 0 and 1 is under the mutation
   * rate, the field (weight or bias) is randomized.
   *
   * @param mutationRate The mutation rate for the mutations.
   * @return New neuron based on parent
   */
  public Neuron mutate(final double mutationRate) {
    return new Neuron(
        this.bias,
        Arrays.stream(this.weights).map(
            w -> Rng.RNG.nextDouble() < mutationRate
                ? Rng.RNG.nextDouble(Neuron.WEIGHT_MIN, Neuron.WEIGHT_MAX)
                : w
        ).toArray(),
        this.activationFunctionIndex,
        this.clamp
    );
  }

}
