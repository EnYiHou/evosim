package org.totallyspies.evosim.engine;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

/**
 * This class consists of a StackPane containing the Canvas in which the simulation is rendered.
 * The Canvas will be automatically resized when this is resized.
 */
public class ResizableCanvas extends StackPane {

    final private Canvas canvas = new Canvas();

    /**
     * Creates a new ResizableCanvas
     */
    public ResizableCanvas() {
        this.getChildren().add(canvas);
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
    }

    /**
     * Returns the graphicContext2D of the canvas. Used to perform drawing operations on the canvas.
     * @return the graphicContext2D of the canvas
     */
    public GraphicsContext getGraphicsContext2D() {
        return canvas.getGraphicsContext2D();
    }


}
