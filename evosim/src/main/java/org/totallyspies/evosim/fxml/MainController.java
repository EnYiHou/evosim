package org.totallyspies.evosim.fxml;

import javafx.fxml.FXML;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.MapCanvas;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input fields.
 */
public final class MainController {

    /**
     * FXML reference to the map where simulation is rendered.
     */
    @FXML
    private MapCanvas mapCanvas;

    /**
     * The simulation to be rendered.
     */
    private Simulation simulation;

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() {
        this.simulation = new Simulation();

        this.mapCanvas.attach(simulation);
    }

    /**
     * Opens a modal AboutWindow when the "About" option is clicked under Help.
     */
    @FXML
    private void aboutMenuClicked() {
        AboutWindow aw = new AboutWindow(EvosimApplication.getApplication().getStage());
        aw.getAbtStage().showAndWait();

        aw.getAbtStage().close();
    }
}
