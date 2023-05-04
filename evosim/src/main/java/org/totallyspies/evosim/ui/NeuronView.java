package org.totallyspies.evosim.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.totallyspies.evosim.neuralnetwork.Neuron;

public class NeuronView extends StackPane {


    /**
     * The radius of a neuron in the view.
     */
    private static final double NEURON_RADIUS = 10;

    /**
     * The neuron that this view is displaying.
     */
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
     */
    public NeuronView(Neuron neuron) {
        this.neuron = neuron;
        this.valueLabel = new Label();
        this.valueLabel.setText(String.valueOf(this.neuron.getValue()));
        this.getChildren().add(new Circle(NEURON_RADIUS, Color.LIGHTGRAY));
        this.getChildren().add(this.valueLabel);
    }

    public void update() {
        this.valueLabel.setText(String.valueOf(this.neuron.getValue()));
    }

    public void setCenterX(double centerX) {
        this.setTranslateX(centerX - NEURON_RADIUS);
    }

    public void setCenterY(double centerY) {
        this.setTranslateY(centerY - NEURON_RADIUS);
    }

    public double getCenterX() {
        return this.getTranslateX() + NEURON_RADIUS;
    }

    public double getCenterY() {
        return this.getTranslateY() + NEURON_RADIUS;
    }
}
