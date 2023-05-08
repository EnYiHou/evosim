package org.totallyspies.evosim.neuralnetwork;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for Neural Network.
 *
 * @author niakouu
 */
class NeuralNetworkTest {

  @Test
  public void generateOneNeuralNetwork() {
    NeuralNetwork neuralNetwork = new NeuralNetwork(List.of(2, 4, 1));
  }


}
