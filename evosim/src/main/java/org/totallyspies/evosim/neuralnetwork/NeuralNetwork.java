package org.totallyspies.evosim.neuralnetwork;

import java.util.List;

/**
 * A Neural Network is a series of layers filled with Neuron objects that each
 * store float values. Between two adjacent layers, each Neuron is connected to
 * one another through a weight represented by a float value that are initially
 * randomly generated. The first layer's Neuron values (input layer) are all 
 * data collected by the Entity sensors. Every other layer has their values 
 * calculated by taking the dot product of the list of Neuron values and the 
 * list of weights. The final layer's values (output layer) determines what the
 * Entity's decision will be.
 */
public class NeuralNetwork {
    /**A list containing each list of Neurons, which each represent a layer.*/
    private List<List<Neuron>> neurons;
    
    /**
     * A 3D list containing all the weights of the network.
     * 
     * Each neuron of the network has a weight attached to every other neuron
     * from the preceding layer. The first inner list selects which layer and
     * the second inner list selects which neuron of that layer to look at. 
     */
    private List<List<List<Float>>> weights; 
    
    /**
     * Creates an empty Neural Network structure. 
     */
    public NeuralNetwork() {
        // def
    }
}
