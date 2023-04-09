package org.totallyspies.evosim.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * WindowUtils contains various methods useful for creating new windows within Evosim.
 *
 * @author mattlep11
 */
public final class WindowUtils {

    private WindowUtils() {
    }

    /**
     * Sets the root of the {@code stage}'s scene to an FXML at {@code path}.
     * <p>
     * If the scene does not yet have a scene, it's created and assigned a root
     * and stylesheet. An empty string must be passed if no stylesheet is used.
     *
     * @param stage     the stage
     * @param fxmlPath  the path of the FXML
     * @param cssPath   the path of the stylesheet
     */
    public static void setSceneRoot(final Stage stage, final URL fxmlPath,
                                    final String cssPath) {
        FXMLLoader loader = new FXMLLoader(fxmlPath);
        Scene scene = stage.getScene();

        try {
            if (scene == null) {
                scene = new Scene(loader.load());

                if (!cssPath.equals("")) {
                    scene.getStylesheets().add(Objects.requireNonNull(cssPath));
                }

            } else {
                scene.setRoot(loader.load());
            }

            stage.setScene(scene);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Evosim Fatal Error");
            alert.setContentText(ex.getMessage());

            ex.printStackTrace();
        }
    }

}
