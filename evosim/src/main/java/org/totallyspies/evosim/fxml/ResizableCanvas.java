package org.totallyspies.evosim.fxml;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
/**
 * This class consists of a StackPane containing the
 * Canvas in which the simulation is rendered.
 * The Canvas will be automatically resized when this is resized.
 */
public class ResizableCanvas extends StackPane {

    /**
     * The canvas in which the simulation is rendered.
     */
    private final Canvas canvas;

    /**
     * Creates a new ResizableCanvas.
     */
    public ResizableCanvas() {
        this.canvas = new Canvas();
        this.getChildren().add(canvas);
        this.canvas.widthProperty().bind(this.widthProperty());
        this.canvas.heightProperty().bind(this.heightProperty());
    }

    /**
     * Returns the graphicsContext2D of the canvas.
     * Used to perform drawing operations on the canvas.
     *
     * @return the graphicsContext2D of the canvas.
     */
    public GraphicsContext getGraphicsContext2D() {
        return this.canvas.getGraphicsContext2D();
    }
}
