package org.totallyspies.evosim.fxml;

import javafx.fxml.FXML;
import lombok.NoArgsConstructor;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input fields.
 */
@NoArgsConstructor
public final class MainController {

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() {
        // initialize main.fxml
    }

    /**
     * Opens a modal AboutWindow when the "About" option is clicked under Help.
     */
    @FXML
    private void aboutMenuClicked() {
        AboutWindow aw = new AboutWindow(EvosimApplication.getApplication().getStage());
        aw.getAbtStage().show();
    }
}
