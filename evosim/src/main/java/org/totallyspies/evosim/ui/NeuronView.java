package org.totallyspies.evosim.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import org.totallyspies.evosim.neuralnetwork.Neuron;

public final class NeuronView extends StackPane {


    /**
     * The radius of a neuron in the view.
     */
    private static final double NEURON_RADIUS = 12;

    /**
     * The neuron that this view is displaying.
     */
    @Getter
    private final Neuron neuron;

    /**
     * The Label that displays the value of the neuron.
     */
    private final Label valueLabel;

    private NeuronView() {
        this.neuron = null;
        this.valueLabel = null;
    }

    /**
     * Constructs a new NeuronView.
     *
     * @param newNeuron the neuron to display
     */
    public NeuronView(final Neuron newNeuron) {
        this.neuron = newNeuron;
        this.valueLabel = new Label();
        this.valueLabel.setText(String.valueOf(this.neuron.getValue()));
        this.getChildren().add(new Circle(NEURON_RADIUS, Color.LIGHTGRAY));
        this.getChildren().add(this.valueLabel);
    }

    /**
     * Updates the value of the neuron in the view.
     */
    public void update() {
        //Integer value
        this.valueLabel.setText(String.valueOf((int) this.neuron.getValue()));
    }
}
