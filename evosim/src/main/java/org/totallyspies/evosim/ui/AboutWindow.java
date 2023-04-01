package org.totallyspies.evosim.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.totallyspies.evosim.utils.ResourceManager;

import java.io.IOException;


/**
 * The AboutWindow is a small, locked modal window that displays basic
 * information about the project and team members. It must be closed before
 * the main application can be accessed again.
 *
 * @author mattlep11
 */
public final class AboutWindow {

    /**
     * The fixed height of this window.
     */
    private static final double ABT_WINDOW_HEIGHT = 450.0;

    /**
     * The fixed width of this window.
     */
    private static final double ABT_WINDOW_WIDTH = 400.0;

    /**
     * The sole stage used for the "about" window.
     */
    private Stage abtStage;

    /**
     * The JavaFX scene for the stage.
     */
    private Scene scene;

    private AboutWindow() { };

    /**
     * Constructs a new modal AboutWindow.
     *
     * @param parent the parent stage of this window
     */
    public AboutWindow(final Stage parent) {
        this.abtStage = new Stage();
        this.setRoot(ResourceManager.FXML_ABOUT);
        abtStage.setTitle("ABOUT Evosim");
        abtStage.initModality(Modality.APPLICATION_MODAL);
        abtStage.initOwner(parent);
        abtStage.setResizable(false);
        abtStage.setScene(scene);
    }

    /**
     * Sets the root of {@link #scene} to an FXML at {#code path}.
     * @param path Resource folder path for the new FXMLs
     *
     * @author ptrstr
     */
    private void setRoot(final String path) {
        FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource(path)
        );

        try {
            if (scene == null) {
                this.scene = new Scene(loader.load(),
                        ABT_WINDOW_WIDTH, ABT_WINDOW_HEIGHT);
            } else {
                this.scene.setRoot(loader.load());
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Evosim Fatal Error");
            alert.setContentText(ex.getMessage());

            ex.printStackTrace();
        }
    }

    public Stage getAbtStage() {
        return abtStage;
    }
}
