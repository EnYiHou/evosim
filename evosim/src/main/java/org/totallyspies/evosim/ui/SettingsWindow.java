package org.totallyspies.evosim.ui;

import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.totallyspies.evosim.utils.ResourceManager;

/**
 * The SettingsWindow is a modal window that presents various controls used to modify the
 * application preferences.
 *
 * @author mattlep11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsWindow {

    /**
     * The sole stage used for the "settings" window.
     */
    @Getter
    private Stage settingsStage;

    /**
     * Constructs a new modal SettingsWindow.
     *
     * @param parent the parent stage of this window
     */
    public SettingsWindow(final Stage parent) {
        this.settingsStage = new Stage();
        settingsStage.setTitle("EVOSIM Settings");
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initOwner(parent);
        settingsStage.setResizable(false);

        WindowUtils.setSceneRoot(this.settingsStage,
                this.getClass().getResource(ResourceManager.FXML_SETTINGS),
                "");
    }
}

