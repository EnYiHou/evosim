package org.totallyspies.evosim.fxml;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.totallyspies.evosim.ui.WindowUtils;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.utils.Configuration.Defaults;
import org.totallyspies.evosim.utils.EvosimException;
import org.totallyspies.evosim.utils.FileSelector;
import org.totallyspies.evosim.utils.ResourceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input fields.
 *
 * @author ptrstr
 */
public final class WelcomeController {
    /**
     * Callbacks to be called to when the `Start` button is pressed. Functions defined here set
     * the configuration and exceptions are raised to the user.
     */
    private final List<Runnable> submissionCallbacks;

    /**
     * Split pane exported from FXML.
     */
    @FXML
    private SplitPane splitPane;

    /**
     * Accordion containing the options to be configured by category.
     */
    @FXML
    private Accordion options;

    /**
     * Constructs the WelcomeController to have an empty arraylist of submission callbacks.
     */
    public WelcomeController() {
        this.submissionCallbacks = new ArrayList<>();
    }

    /**
     * Initializes the {@code welcome.fxml} by setting {@link #splitPane}'s background image as
     * well as adding all prompts dynamically.
     */
    public void initialize() {
        Platform.runLater(() -> {
                this.splitPane.setBackground(new Background(new BackgroundImage(
                    new Image(this.getClass().getResourceAsStream(ResourceManager.IMAGE_WELCOME)),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, 1, true, true, false, true)
                )));
        });

        Configuration config = Configuration.getConfiguration();

        TitledPane generalDropdown = new TitledPane("General", new VBox(
            this.createSliderDefault("Map size X", config::setMapSizeX, Defaults.MAP_SIZE_X),
            this.createSliderDefault("Map size Y", config::setMapSizeY, Defaults.MAP_SIZE_Y),
            this.createSliderDefault("Grid size", config::setGridSize, Defaults.GRID_SIZE)
        ));

        TitledPane entityDropdown = new TitledPane("Entities", new VBox(
            this.createSliderDefault(
                "Entity max rotation speed",
                config::setEntityMaxRotationSpeed,
                Defaults.ENTITY_MAX_ROTATION_SPEED
            ),
            this.createSliderDefault(
                "Entity sensor count",
                config::setEntitySensorsCount,
                Defaults.ENTITY_SENSORS_COUNT
            ),
            this.createSliderDefault(
                "Entity radius",
                config::setEntityRadius,
                Defaults.ENTITY_RADIUS
            ),
            this.createSliderDefault(
                "Entity sensors length",
                config::setEntitySensorsLength,
                Defaults.ENTITY_SENSORS_LENGTH
            ),
            this.createSliderDefault(
                "Entity max speed",
                config::setEntityMaxSpeed,
                Defaults.ENTITY_MAX_SPEED
            ),
            this.createSliderDefault(
                "Entity minimum speed",
                config::setEntityMinSpeed,
                Defaults.ENTITY_MIN_SPEED
            ),
            this.createSliderDefault(
                "Entity speed mutation rate",
                config::setEntitySpeedMutationRate,
                Defaults.ENTITY_SPEED_MUTATION_RATE
            ),
            this.createSliderDefault(
                "Entity energy drain rate",
                config::setEntityEnergyDrainRate,
                Defaults.ENTITY_ENERGY_DRAIN_RATE
            )
        ));

        TitledPane predatorDropdown = new TitledPane("Predators", new VBox(
            this.createSliderDefault(
                "Predator energy filling speed",
                config::setPredatorEnergyFillingSpeed,
                Defaults.PREDATOR_ENERGY_FILLING_SPEED
            ),
            this.createSliderDefault(
                "Predator energy base draining speed",
                config::setPredatorEnergyBaseDrainingSpeed,
                Defaults.PREDATOR_ENERGY_BASE_DRAINING_SPEED
            ),
            this.createSliderDefault(
                "Predator max number",
                config::setPredatorMaxNumber,
                Defaults.PREDATOR_MAX_NUMBER
            ),
            this.createSliderDefault(
                "Predator initial population",
                config::setPredatorInitialPopulation,
                Defaults.PREDATOR_INITIAL_POPULATION
            ),
            this.createSliderDefault(
                "Predator view angle",
                config::setPredatorViewAngle,
                Defaults.PREDATOR_VIEW_ANGLE
            ),
            this.createSliderDefault(
                "Predator split energy filling speed",
                config::setPredatorSplitEnergyFillingSpeed,
                Defaults.PREDATOR_SPLIT_ENERGY_FILLING_SPEED
            )
        ));


        TitledPane preyDropdown = new TitledPane("Preys", new VBox(
            this.createSliderDefault(
                "Prey energy filling speed",
                config::setPreyEnergyFillingSpeed,
                Defaults.PREY_ENERGY_FILLING_SPEED
            ),
            this.createSliderDefault(
                "Prey max number",
                config::setPreyMaxNumber,
                Defaults.PREY_MAX_NUMBER
            ),
            this.createSliderDefault(
                "Prey initial population",
                config::setPreyInitialPopulation,
                Defaults.PREY_INITIAL_POPULATION
            ),
            this.createSliderDefault(
                "Prey view angle",
                config::setPreyViewAngle,
                Defaults.PREY_VIEW_ANGLE
            ),
            this.createSliderDefault(
                "Prey split energy filling speed",
                config::setPreySplitEnergyFillingSpeed,
                Defaults.PREY_SPLIT_ENERGY_FILLING_SPEED
            )
        ));

        TitledPane neuralNetworkDropdown = new TitledPane("Neural network", new VBox(
            this.createSliderDefault(
                "Neural network layers",
                config::setNeuralNetworkLayersNumber,
                Defaults.NEURAL_NETWORK_LAYERS_NUMBER
            )
        ));

        this.options.getPanes().addAll(
            generalDropdown, entityDropdown, predatorDropdown, preyDropdown, neuralNetworkDropdown
        );

        this.hideLockDividers();
    }

    @FXML
    private void pressOnImportBtn(final ActionEvent event) throws EvosimException {
            FileChooser fileChooser = FileSelector.getFileChooserJson();
            fileChooser.setTitle("Import Configuration");
            File file = fileChooser.showOpenDialog(EvosimApplication.getApplication().getStage());
            if (file != null) {
                Configuration.getConfiguration().loadFile(file);
                EvosimApplication.getApplication().getStage().setUserData(file);
                WindowUtils.setSceneRoot(EvosimApplication.getApplication().getStage(),
                        this.getClass().getResource(ResourceManager.FXML_MAIN_VIEW),
                        "");
            }
    }

    @FXML
    private void pressOnLatestBtn(final ActionEvent event) throws EvosimException {
        Configuration.getConfiguration().loadLastFile();
        boolean isLatest = true;
        EvosimApplication.getApplication().getStage().setUserData(isLatest);
        WindowUtils.setSceneRoot(EvosimApplication.getApplication().getStage(),
                this.getClass().getResource(ResourceManager.FXML_MAIN_VIEW),
                "");
    }

    /**
     * Hides and lock dividers inside the {@link SplitPane}.
     */
    private void hideLockDividers() {
        final double[] baseDividerPositions = this.splitPane.getDividerPositions();
        final int dividerCnt = this.splitPane.getDividers().size();

        for (int i = 0; i < dividerCnt; ++i) {
            final int curI = i;

            // Makes any change to the divider position go back to its backed up
            // state
            this.splitPane.getDividers().get(i).positionProperty().addListener(
                    (o, oldValue, newValue) -> {
                        if (newValue.doubleValue() != baseDividerPositions[curI]) {
                            splitPane.setDividerPosition(curI, baseDividerPositions[curI]);
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

    private <T extends Number> boolean isNumberFloatingPoint(final T num) {
        if (!List.of(Integer.class, Double.class).contains(num.getClass())) {
            throw new RuntimeException("T not Double or Integer");
        }

        return num.getClass() == Double.class;
    }

    private <T extends Number> SafeSlider createSliderDefault(final String name,
                                                              final Consumer<T> setter,
                                                              final T defaultValue
    ) {
        return createSlider(
                name,
                setter,
                (T) (Integer.valueOf(0)),
                true,
                (isNumberFloatingPoint(defaultValue)
                        ? (T) Double.valueOf(defaultValue.doubleValue() * 2)
                        : (T) Integer.valueOf(defaultValue.intValue() * 2)
                ),
                false,
                defaultValue
        );
    }

    private <T extends Number> SafeSlider createSlider(
            final String name,
            final Consumer<T> setter,
            final T min, final boolean hardMin,
            final T max, final boolean hardMax,
            final T defaultValue
    ) {
        final SafeSlider slider = new SafeSlider();
        slider.setFloatingPoint(isNumberFloatingPoint(defaultValue));
        slider.setMin(min);
        slider.setHardMin(hardMin);
        slider.setMax(max);
        slider.setHardMax(hardMax);
        slider.setName(name);
        slider.setValue(defaultValue);

        this.submissionCallbacks.add(
                () -> setter.accept((T) slider.getValue())
        );

        return slider;
    }

    @FXML
    private void onNext() {
        try {
            this.submissionCallbacks.forEach(Runnable::run);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Evosim");

            alert.setContentText(ex.getMessage());

            alert.show();
            return;
        }
        WindowUtils.setSceneRoot(EvosimApplication.getApplication().getStage(),
                this.getClass().getResource(ResourceManager.FXML_MAIN_VIEW),
                "");
    }
}
