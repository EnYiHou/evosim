package org.totallyspies.evosim.fxml;

import javafx.fxml.FXML;
import org.totallyspies.evosim.simulation.Simulation;
import lombok.NoArgsConstructor;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.MapCanvas;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.util.Optional;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input fields.
 *
 * @author mattlep11
 */
@NoArgsConstructor
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
     * The StackPane within the center of the root BorderPane.
     */
    @FXML
    private StackPane centerStack;

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() {
        this.simulation = new Simulation();

        this.mapCanvas.attach(simulation);

        Scene scene = EvosimApplication.getApplication().getStage().getScene();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.ESCAPE) {
                escapeClicked();
            }
            if (!this.mapCanvas.getPressedKeys().contains(code)) {
                this.mapCanvas.getPressedKeys().push(code);
            }
        });

        scene.setOnKeyReleased(event ->
                this.mapCanvas.getPressedKeys().remove(event.getCode()));

        // TODO add on click event to track and follow entities.
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

    /**
     * Display an alert to the user to confirm if they'd like to close the app.
     */
    private void escapeClicked() {
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you'd like to exit Evosim?",
                ButtonType.YES,
                ButtonType.NO
        );

        Optional<ButtonType> selection = confirmation.showAndWait();
        if (selection.isEmpty() || selection.get() == ButtonType.NO) {
            confirmation.close();
        } else if (selection.get() == ButtonType.YES) {
            System.out.println("Shutting down application...");
            EvosimApplication.getApplication().getStage().close();
        }
    }

}
