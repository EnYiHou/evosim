package org.totallyspies.evosim.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Tab;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final ArrayList<ArrayList<NeuronView>> neuralNetworkStructure;


    public NeuralNetworkView() {
        super("Neural Network View");
        this.neuralNetworkView = new Pane();
        this.setContent(this.neuralNetworkView);
        this.neuralNetworkView.setBackground(Background.fill(Color.ALICEBLUE));
        this.neuralNetworkStructure = new ArrayList<>();
        this.neuralNetwork = null;
        this.setRenderer();
        this.renderer.start();
    }

    private void generateNeuralNetworkStructure() {
        this.neuralNetworkStructure.clear();
        this.neuralNetwork.getNeuronLayers().forEach(layer -> {
            ArrayList<NeuronView> neuronViews = layer.stream()
                    .map(NeuronView::new)
                    .collect(Collectors.toCollection(ArrayList::new));
            this.neuralNetworkStructure.add(neuronViews);
        });
    }

    private void adjustToView() {
        int layer = this.neuralNetworkStructure.size();

        double width = this.neuralNetworkView.getWidth();
        double height = this.neuralNetworkView.getHeight();

        double widthPerLayer = width / (layer + 1);

        for (int i = 0; i < layer; i++) {
            List<NeuronView> neuronViews = this.neuralNetworkStructure.get(i);
            double heightPerNeuron = height / (neuronViews.size() + 1);
            for (int j = 0; j < neuronViews.size(); j++) {
                NeuronView neuronView = neuronViews.get(j);
                neuronView.setCenterX(widthPerLayer * (i + 1));
                neuronView.setCenterY(heightPerNeuron * (j + 1));
                this.neuralNetworkView.getChildren().add(neuronView);

                if (i != 0) {
                    List<NeuronView> previousNeuronViews = this.neuralNetworkStructure.get(i - 1);
                    for (NeuronView previousNeuronView : previousNeuronViews) {
                        Line weight = new Line();
                        weight.setStroke(Color.BLACK);
                        weight.setStartX(previousNeuronView.getCenterX());
                        weight.setStartY(previousNeuronView.getCenterY());

                        weight.setEndX(neuronView.getCenterX());
                        weight.setEndY(neuronView.getCenterY());

                        this.neuralNetworkView.getChildren().add(weight);
                        weight.toBack();
                    }
                }
            }
        }

    }

    private void setRenderer() {
        renderer = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                for (List<NeuronView> layer : neuralNetworkStructure) {
                    for (NeuronView neuronView : layer) {
                        neuronView.update();
                    }
                }
            }
        };
    }


    /**
     * Sets the NeuralNetwork that this view is displaying.
     */
    public void setNeuralNetwork(final NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
        if (neuralNetwork == null) {
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
