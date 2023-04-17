package org.totallyspies.evosim.ui;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The AboutWindow is a small, locked modal window that displays basic information about the
 * project and team members. It must be closed before the main application can be accessed again.
 *
 * @author mattlep11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
    @Getter
    private Stage abtStage;

    /**
     * The JavaFX scene for the stage.
     */
    private Scene scene;

    /**
     * Constructs a new modal AboutWindow.
     *
     * @param parent the parent stage of this window
     */
    public AboutWindow(final Stage parent) {
        this.abtStage = new Stage();
        abtStage.setTitle("ABOUT Evosim");
        abtStage.initModality(Modality.APPLICATION_MODAL);
        abtStage.initOwner(parent);
        abtStage.setResizable(false);
        abtStage.setScene(scene);
    }
}
