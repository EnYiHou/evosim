package org.totallyspies.evosim.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Tab;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;
import org.totallyspies.evosim.neuralnetwork.Neuron;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetworkView extends Tab {


    /**
     * The NeuralNetwork that this view is displaying.
     */
    private NeuralNetwork neuralNetwork;

    /**
     * The renderer for this view.
     */
    private AnimationTimer renderer;

    /**
     * The pane in which the neural network is displayed.
     */
    private final Pane neuralNetworkView;

    /**
     * The structure of the neurons in the neural network.
     */
    private final ArrayList<ArrayList<NeuronView>> neuronStructure;

    /**
     * The structure of the weights in the neural network.
     */
    private final ArrayList<ArrayList<ArrayList<Line>>> weightsStructure;

    /**
     * Constructs a new Tab for displaying a NeuralNetwork.
     */
    public NeuralNetworkView() {
        super("Neural Network View");
        this.neuralNetworkView = new Pane();
        this.setContent(this.neuralNetworkView);
        this.neuralNetworkView.setBackground(Background.fill(Color.ALICEBLUE));
        this.neuronStructure = new ArrayList<>();
        this.weightsStructure = new ArrayList<>();
        this.neuralNetwork = null;
        this.setRenderer();
        this.renderer.start();
    }


    private void generateNeuralNetworkStructure() {
        this.neuronStructure.clear();

        for (int layer = 0; layer < this.neuralNetwork.getNeuronLayers().size(); layer++) {
            ArrayList<NeuronView> neuronLayer = new ArrayList<>();
            ArrayList<ArrayList<Line>> weightLayer = new ArrayList<>();

            for (
                int neuron = 0;
                neuron < this.neuralNetwork.getNeuronLayers().get(layer).size();
                neuron++
            ) {
                Neuron sNeuron = this.neuralNetwork.getNeuronLayers().get(layer).get(neuron);
                ArrayList<Line> specificNeuron = new ArrayList<>();
                NeuronView neuronView = new NeuronView(sNeuron);
                neuronLayer.add(neuronView);

                if (layer != 0) {
                    for (int weight = 0; weight < sNeuron.getWeights().length; weight++) {
                        Line specificWeight = new Line();
                        NeuronView previousNeuron = this.neuronStructure.get(layer - 1)
                            .get(weight);
                        NeuronView currentNeuron = neuronView;
                        specificWeight.setStroke(Color.BLACK);
                        specificWeight.startXProperty().bind(previousNeuron.translateXProperty()
                            .add(previousNeuron.widthProperty().divide(2)));
                        specificWeight.startYProperty().bind(previousNeuron.translateYProperty()
                            .add(previousNeuron.heightProperty().divide(2)));
                        specificWeight.endXProperty().bind(currentNeuron.translateXProperty()
                            .add(currentNeuron.widthProperty().divide(2)));
                        specificWeight.endYProperty().bind(currentNeuron.translateYProperty()
                            .add(currentNeuron.heightProperty().divide(2)));
                        specificNeuron.add(specificWeight);
                        specificWeight.toBack();
                        neuralNetworkView.getChildren().add(specificWeight);
                    }
                }

                weightLayer.add(specificNeuron);
            }

            this.neuronStructure.add(neuronLayer);
            this.weightsStructure.add(weightLayer);
        }
    }

    private void adjustToView() {
        int layer = this.neuronStructure.size();

        for (int specificLayer = 0; specificLayer < layer; specificLayer++) {
            List<NeuronView> neuronViews = this.neuronStructure.get(specificLayer);
            for (int specificNeuron = 0; specificNeuron < neuronViews.size(); specificNeuron++) {
                NeuronView neuronView = neuronViews.get(specificNeuron);

                //automatically adjust the positions of the neuronView
                //based on the size of the neuralNetworkView
                neuronView.translateXProperty().bind(
                    this.neuralNetworkView.widthProperty().divide(layer + 1)
                        .multiply(specificLayer + 1)
                        .subtract(neuronView.widthProperty().divide(2)));

                neuronView.translateYProperty().bind(
                    this.neuralNetworkView.heightProperty().divide(neuronViews.size() + 1)
                        .multiply(specificNeuron + 1)
                        .subtract(neuronView.heightProperty().divide(2)));

                this.neuralNetworkView.getChildren().add(neuronView);
            }
        }

    }

    private void setRenderer() {
        renderer = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                for (List<NeuronView> layer : neuronStructure) {
                    for (NeuronView neuronView : layer) {
                        neuronView.update();
                        if (layer != neuronStructure.get(0)) {
                            for (int weightIndex = 0; weightIndex < neuronView.getNeuron()
                                .getWeights().length; weightIndex++) {
                                Line weight = weightsStructure.get(neuronStructure.indexOf(layer))
                                    .get(layer.indexOf(neuronView)).get(weightIndex);
                                weight.setStrokeWidth(
                                    Math.abs(neuronView.getNeuron()
                                        .getWeights()[weightIndex]));
                                weight.setOpacity(
                                    Math.abs(neuronView.getNeuron()
                                        .getWeights()[weightIndex]));
                            }
                        }
                    }
                }
            }
        };
    }


    /**
     * Sets the NeuralNetwork that this view is displaying.
     *
     * @param newNeuralNetwork The NeuralNetwork to display.
     */
    public void setNeuralNetwork(final NeuralNetwork newNeuralNetwork) {
        this.neuralNetworkView.getChildren().clear();
        this.neuronStructure.clear();
        this.weightsStructure.clear();
        this.neuralNetwork = newNeuralNetwork;
        if (newNeuralNetwork == null) {
            this.renderer.stop();
            this.setDisable(true);
        } else {
            this.generateNeuralNetworkStructure();
            this.adjustToView();
            this.renderer.start();
            this.setDisable(false);

        }
    }
}
