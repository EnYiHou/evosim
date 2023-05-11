package org.totallyspies.evosim.fxml;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.MapCanvas;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.EvosimException;
import org.totallyspies.evosim.utils.FileSelector;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Controller for the {@code settings.fxml} file. Dynamically adds all input fields.
 *
 * @author mattlep11
 */
@NoArgsConstructor
public final class SettingsController {

    /**
     * Time wait for an image to load.
     */
    private static final int BACKGROUND_LOAD_WAIT_TIME = 500;

    /**
     * Checkbox toggling the visibility of the entity's energy.
     */
    @FXML
    private CheckBox cbEnergy;

    /**
     * If the energy bar should be visible.
     */
    private static boolean energyVisible = true;

    /**
     * Checkbox toggling the visibility of the entity's split energy.
     */
    @FXML
    private CheckBox cbSplitEnergy;

    /**
     * If the split energy bar should be visible.
     */
    @Getter
    private static boolean splitEnergyVisible = true;

    /**
     * Checkbox toggling the visibility of the entity's base speed.
     */
    @FXML
    private CheckBox cbSpeed;

    /**
     * If the speed count should be visible.
     */
    private static boolean speedVisible = true;

    /**
     * Checkbox toggling the visibility of the entity's child count.
     */
    @FXML
    private CheckBox cbChild;

    /**
     * If the child count should be visible.
     */
    private static boolean childCountVisible = true;

    /**
     * Checkbox toggling the visibility of the entity's time alive timer.
     */
    @FXML
    private CheckBox cbTimer;

    /**
     * If the timer should be visible.
     */
    private static boolean timerVisible = true;

    /**
     * If the stats menu should be visible when focused.
     */
    private static boolean statsVisible = true;

    /**
     * ColorPicker to select the map's background colour.
     */
    @FXML
    private ColorPicker cpMap;

    /**
     * ColorPicker to select the prey's body colour.
     */
    @FXML
    private ColorPicker cpPrey;

    /**
     * ColorPicker to select the predator's body colour.
     */
    @FXML
    private ColorPicker cpPred;

    /**
     * A FileChooser to select an image for the map background.
     */
    private static FileChooser fileChooser;

    /**
     * Initializes {@code settings.fxml}. Sets the checkbox states on open.
     */
    public void initialize() {
        // set states on launch
        this.cbEnergy.setSelected(energyVisible);
        this.cbSplitEnergy.setSelected(splitEnergyVisible);
        this.cbSpeed.setSelected(speedVisible);
        this.cbChild.setSelected(childCountVisible);
        this.cbTimer.setSelected(timerVisible);
        this.cpMap.setValue(MapCanvas.getMapColor());

        fileChooser = FileSelector.getFileChooserImage();

        cpMap.valueProperty().addListener((o, ov, nv) -> MapCanvas.setMapColor(nv));
    }

    /**
     * Changes the visibility setting of the clicked checkbox.
     *
     * @param e the mouse event
     */
    @FXML
    private void checkBoxClicked(final MouseEvent e) {
        if (e.getSource().equals(cbEnergy)) {
            energyVisible = !energyVisible;
        } else if (e.getSource().equals((cbSplitEnergy))) {
            splitEnergyVisible = !splitEnergyVisible;
        } else if (e.getSource().equals((cbSpeed))) {
            speedVisible = !speedVisible;
        } else if (e.getSource().equals((cbChild))) {
            childCountVisible = !childCountVisible;
        } else if (e.getSource().equals((cbTimer))) {
            timerVisible = !timerVisible;
        }

        // should the container be displayed
        statsVisible = energyVisible || splitEnergyVisible || speedVisible
                || childCountVisible || timerVisible;

        MainController.getController().getEntityStats().setOpacity(statsVisible ? 1 : 0);
        clearAndResize();
    }

    /**
     * Clears the parent container and adds back only the enabled children.
     */
    private void clearAndResize() {
        final MainController controller = MainController.getController();
        final ObservableList<Node> children = controller.getEntityStats().getChildren();
        children.remove(1, children.size()); // keep the label at the top

        if (energyVisible) {
            children.add(controller.getEnergyBar());
        }
        if (splitEnergyVisible) {
            children.add(controller.getSplitEnergyBar());
        }
        if (speedVisible) {
            children.add(controller.getSpeedLabel());
        }
        if (childCountVisible) {
            children.add(controller.getChildCountLabel());
        }
        if (timerVisible) {
            children.add(controller.getLivingTimeLabel());
        }
    }

    /**
     * Opens the file explorer to select an image for the map background.
     */
    @FXML
    private void selectImgClicked() throws EvosimException, InterruptedException {
        File chosenFile = fileChooser.showOpenDialog(EvosimApplication.getApplication().getStage());
        if (chosenFile != null) {
            Configuration.getConfiguration().setBackgroundImage(new Image(chosenFile.toURI().toString()));
            Image config = Configuration.getConfiguration().getBackgroundImage();

            while (config.isBackgroundLoading()) {
                Thread.sleep(BACKGROUND_LOAD_WAIT_TIME);
            }

            if (config.isError()) {
                throw new EvosimException("Couldn't load the image.");
            }

            MapCanvas.setMapImage(config);
        }
    }

    /**
     * Clears the MapCanvas' selected image.
     */
    @FXML
    private void clearImgClicked() throws EvosimException {
        Configuration.getConfiguration().setBackgroundImage(null);
        MapCanvas.setMapImage(Configuration.getConfiguration().getBackgroundImage());
    }

}
