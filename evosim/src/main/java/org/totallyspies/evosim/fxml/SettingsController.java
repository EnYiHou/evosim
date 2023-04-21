package org.totallyspies.evosim.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Controller for the {@code settings.fxml} file. Dynamically adds all input fields.
 *
 * @author mattlep11
 */
@NoArgsConstructor
public final class SettingsController {

    /**
     * Checkbox toggling the visibility of the entity's energy.
     */
    @FXML
    private CheckBox cbEnergy;

    /**
     * If the energy bar should be visible.
     */
    @Getter
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
    @Getter
    private static boolean speedVisible = true;

    /**
     * Checkbox toggling the visibility of the entity's child count.
     */
    @FXML
    private CheckBox cbChild;

    /**
     * If the child count should be visible.
     */
    @Getter
    private static boolean childCountVisible = true;

    /**
     * Checkbox toggling the visibility of the entity's time alive timer.
     */
    @FXML
    private CheckBox cbTimer;

    /**
     * If the timer should be visible.
     */
    @Getter
    private static boolean timerVisible = true;

    /**
     * If the stats menu should be visible when focused.
     */
    @Getter
    private static boolean statsVisible = true;

    /**
     * Initializes {@code settings.fxml}. Sets the checkbox states on open.
     */
    public void initialize() {
        // set states on launch
        cbEnergy.setSelected(energyVisible);
        cbSplitEnergy.setSelected(splitEnergyVisible);
        cbSpeed.setSelected(speedVisible);
        cbChild.setSelected(childCountVisible);
        cbTimer.setSelected(timerVisible);
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

        statsVisible = energyVisible || splitEnergyVisible || speedVisible
                || childCountVisible || timerVisible;
    }


}
