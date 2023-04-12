package org.totallyspies.evosim.fxml;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.Map;

import java.util.Optional;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input fields.
 *
 * @author mattlep11
 */
public final class MainController {

    /**
     * The StackPane within the center of the root BorderPane.
     */
    @FXML
    private StackPane centerStack;

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() {
        Scene scene = EvosimApplication.getApplication().getStage().getScene();
        centerStack.getChildren().add(Map.getInstance());

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.ESCAPE) {
                escapeClicked();
            }
            if (!Simulation.getPressedKeys().contains(code)) {
                Simulation.getPressedKeys().push(code);
            }
        });

        scene.setOnKeyReleased(event ->
                Simulation.getPressedKeys().remove(event.getCode()));

        // TODO add on click event to track and follow entities.

        Simulation sim = new Simulation();
        sim.getAnimationLoop().start();
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
