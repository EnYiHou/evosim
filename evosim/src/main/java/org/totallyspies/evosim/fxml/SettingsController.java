package org.totallyspies.evosim.fxml;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.MapCanvas;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.EvosimException;
import org.totallyspies.evosim.utils.FileSelector;

import java.io.File;
import java.util.HashMap;

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
     * ChoiceBox to select the theme preset.
     */
    @FXML
    private ChoiceBox<String> themeSelector;

    /**
     * A map with all the default themes, where the key is the name and value is the hex code.
     */
    private static final HashMap<String, String> DEFAULT_THEMES = new HashMap<>();

    /**
     * The current selected theme.
     */
    private static String theme = "Default";

    /**
     * A FileChooser to select an image for the map background.
     */
    private static FileChooser fileChooser;

    /**
     * The global configuration.
     */
    private Configuration configuration;

    /**
     * Initializes {@code settings.fxml}. Sets the checkbox states on open.
     */
    public void initialize() {
        configuration = Configuration.getConfiguration();

        // set states on launch
        this.cbEnergy.setSelected(energyVisible);
        this.cbSplitEnergy.setSelected(splitEnergyVisible);
        this.cbSpeed.setSelected(speedVisible);
        this.cbChild.setSelected(childCountVisible);
        this.cbTimer.setSelected(timerVisible);
        this.cpMap.setValue(MapCanvas.getMapColor());
        this.themeSelector.setValue(theme);

        populateThemes();

        fileChooser = FileSelector.getFileChooserImage();

        cpMap.valueProperty().addListener((o, ov, nv) -> MapCanvas.setMapColor(nv));
        themeSelector.valueProperty().addListener((o, ov, nv) -> {
            this.changeTheme(nv);
            theme = nv;
        });
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
        boolean statsVisible = energyVisible || splitEnergyVisible || speedVisible
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
            configuration.setBackgroundImage(
                    new Image(chosenFile.toURI().toString()));
            Image config = configuration.getBackgroundImage();

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
        configuration.setBackgroundImage(null);
        MapCanvas.setMapImage(configuration.getBackgroundImage());
    }

    /**
     * Populates {@code #DEFAULT_THEMES} with all the default theme names and hex codes.
     */
    private void populateThemes() {
        if (!DEFAULT_THEMES.isEmpty()) {
            populateChoices();
            return;
        }

        DEFAULT_THEMES.put("Default", "#F2F2F2");
        DEFAULT_THEMES.put("Tropical Orange", "#FFCCB3");
        DEFAULT_THEMES.put("Grassy Green", "#99CC99");
        DEFAULT_THEMES.put("Floral Pink", "#FFB3B3");
        DEFAULT_THEMES.put("Sour Lemon", "#FFFFCC");
        DEFAULT_THEMES.put("Grey", "#B3B3B3");
        DEFAULT_THEMES.put("Lavender", "#CCB3FF");
        DEFAULT_THEMES.put("Cool Teal", "#80B3B3");
        populateChoices();
    }

    /**
     * Populates the {@code #ChoiceBox} themeSelector with the names of the new default keys.
     */
    private void populateChoices() {
        DEFAULT_THEMES.forEach((k, v) -> {
            if (k.equals("Default")) {
                this.themeSelector.getItems().add(0, k);
            } else {
                this.themeSelector.getItems().add(k);
            }
        });
    }

    /**
     * Switches the application theme.
     * @param newTheme  the new theme's name
     */
    private void changeTheme(final String newTheme) {
        String hexCode = DEFAULT_THEMES.get(newTheme);

        EvosimApplication.getApplication().getStage().getScene().getRoot().setStyle(
                "-fx-base: " + hexCode + ";".toUpperCase()
        );
        cpMap.setValue(Color.valueOf(hexCode));
    }

}
