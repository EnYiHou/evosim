package org.totallyspies.evosim.fxml;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import org.totallyspies.evosim.EvosimApplication;
import org.totallyspies.evosim.ResourceManager;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input
 * fields.
 * @author ptrstr
 */
public final class WelcomeController {
    /**
     * Split pane exported from FXML.
     */
    @FXML
    private SplitPane splitPane;

    /**
     * Initializes the {@code welcome.fxml} by setting
     * {@link #splitPane}'s background image as well as adding all
     * prompts dynamically.
     */
    public void initialize() {
        this.splitPane.setBackground(new Background(new BackgroundImage(
            new Image(ResourceManager.IMAGE_WELCOME),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(
                BackgroundSize.AUTO,
                1,
                true,
                true,
                false,
                true
            )
        )));

        this.hideLockDividers();
    }

    /**
     * Hides and lock dividers inside the {@link SplitPane}.
     */
    private void hideLockDividers() {
        final double[] baseDividerPositions =
            this.splitPane.getDividerPositions();
        final int dividerCnt = this.splitPane.getDividers().size();

        for (int i = 0; i < dividerCnt; ++i) {
            final int currentIndex = i;

            // Makes any change to the divider position go back to its backed up
            // state
            this.splitPane.getDividers().get(i).positionProperty().addListener(
                (o, oldValue, newValue) -> {
                    if (
                        newValue.doubleValue()
                        != baseDividerPositions[currentIndex]
                    ) {
                        splitPane.setDividerPosition(
                            currentIndex, baseDividerPositions[currentIndex]
                        );
                    }
                }
            );
        }

        Platform.runLater(() -> {
            for (Node node : this.splitPane.lookupAll(".split-pane-divider")) {
                node.setStyle("-fx-padding: 0;");
                node.setMouseTransparent(true);
            }
        });
    }

    @FXML
    private void onNext() {
        EvosimApplication.getApplication().setRoot(
            ResourceManager.FXML_MAIN_VIEW
        );
    }
}
