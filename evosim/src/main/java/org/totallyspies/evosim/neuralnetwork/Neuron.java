package org.totallyspies.evosim.neuralnetwork;

import java.util.function.Function;

/**
 * A Neuron is a single node of the greater Neural Network system storing a bias
 * and float value. Each layer of the network is made up of many Neuron objects. 
 */
public class Neuron {
    /**A float value to shift the results neural network's activation.*/
    private float bias;
    
    /**A float value assigned due to sensor input or computation results.*/
    private float value;
    
    /**
     * Creates an empty Neuron.
     */
    public Neuron() {
        // def
    }
    
    /**
     * Creates an empty Neuron with a predefined bias.
     * 
     * @param bias  the bias used to shift the activation function
     */
    public Neuron(float bias) {
        this.bias = bias;
    }
    
    /**
     * Activates this Neuron's value by passing it through the selected
     * activation function.
     * 
     * @param activationFunction    the networks activation function that will
     *                              accept and return a float value
     * @return                      the activated value  
     */
    public float activate(Function<Float, Float> activationFunction) {
        return activationFunction.apply(this.value);
    }

    public float getBias() {
        return this.bias;
    }
    
    public void setBias(float bias) {
        this.bias = bias;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }

}
