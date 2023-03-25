package org.totallyspies.evosim;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public final class HelloController {
    /**
     * The welcome text.
     */
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
