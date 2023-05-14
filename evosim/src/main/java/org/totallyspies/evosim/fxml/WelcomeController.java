package org.totallyspies.evosim.fxml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
     * Saving the safe sliders that should be used in the welcome.fxml.
     */
    private List<SafeSlider> layersSafeSliders;

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
    @SuppressWarnings("checkstyle:MethodLength")
    public void initialize() throws EvosimException {
        this.layersSafeSliders = new ArrayList<>();
        this.splitPane.setBackground(new Background(new BackgroundImage(
            new Image(this.getClass().getResource(ResourceManager.IMAGE_WELCOME).toString()),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, 1, true, true, false, true)
        )));

        Configuration config = Configuration.getConfiguration();

        TitledPane generalDropdown = new TitledPane("General", new VBox(
            this.createSliderDefault("Map size X", config::setMapSizeX, 0, Defaults.MAP_SIZE_X),
            this.createSliderDefault("Map size Y", config::setMapSizeY, 0, Defaults.MAP_SIZE_Y),
            this.createSliderDefault("Grid size", config::setGridSize, 0, Defaults.GRID_SIZE)
        ));

        TitledPane entityDropdown = new TitledPane("Entities", new VBox(
            this.createSliderDefault(
                "Max Rotation Speed",
                config::setEntityMaxRotationSpeed,
                0d,
                Defaults.ENTITY_MAX_ROTATION_SPEED
            ),
            this.createSliderDefault(
                "Sensor Count",
                config::setEntitySensorsCount,
                0,
                Defaults.ENTITY_SENSORS_COUNT
            ),
            this.createSliderDefault(
                "Length of Sensors",
                config::setEntitySensorsLength,
                0d,
                Defaults.ENTITY_SENSORS_LENGTH
            ),
            this.createSliderDefault(
                "Body Radius",
                config::setEntityRadius,
                0d,
                Defaults.ENTITY_RADIUS
            ),
            this.createSliderDefault(
                "Maximum Speed",
                config::setEntityMaxSpeed,
                0d,
                Defaults.ENTITY_MAX_SPEED
            ),
            this.createSliderDefault(
                "Minimum Speed",
                config::setEntityMinSpeed,
                0d,
                Defaults.ENTITY_MIN_SPEED
            ),
            this.createSliderDefault(
                "Speed mutation Rate",
                config::setEntitySpeedMutationRate,
                0d,
                Defaults.ENTITY_SPEED_MUTATION_RATE
            ),
            this.createSliderDefault(
                "Energy Drain Rate",
                config::setEntityEnergyDrainRate,
                0d,
                Defaults.ENTITY_ENERGY_DRAIN_RATE
            )
        ));

        TitledPane predatorDropdown = new TitledPane("Predators", new VBox(
            this.createSliderDefault(
                "Energy Fill Speed",
                config::setPredatorEnergyFillingSpeed,
                0d,
                Defaults.PREDATOR_ENERGY_FILLING_SPEED
            ),
            this.createSliderDefault(
                "Energy Base Drain Speed",
                config::setPredatorEnergyBaseDrainingSpeed,
                0d,
                Defaults.PREDATOR_ENERGY_BASE_DRAINING_SPEED
            ),
            this.createSliderDefault(
                "Maximum Population",
                config::setPredatorMaxNumber,
                0,
                Defaults.PREDATOR_MAX_NUMBER
            ),
            this.createSliderDefault(
                "Initial Population",
                config::setPredatorInitialPopulation,
                0,
                Defaults.PREDATOR_INITIAL_POPULATION
            ),
            this.createSliderDefault(
                "View Cone Angle",
                config::setPredatorViewAngle,
                0d,
                Defaults.PREDATOR_VIEW_ANGLE
            ),
            this.createSliderDefault(
                "Split Energy Fill Speed",
                config::setPredatorSplitEnergyFillingSpeed,
                0d,
                Defaults.PREDATOR_SPLIT_ENERGY_FILLING_SPEED
            )
        ));


        TitledPane preyDropdown = new TitledPane("Preys", new VBox(
            this.createSliderDefault(
                "Energy Fill Speed",
                config::setPreyEnergyFillingSpeed,
                0d,
                Defaults.PREY_ENERGY_FILLING_SPEED
            ),
            this.createSliderDefault(
                "Maximum Population",
                config::setPreyMaxNumber,
                0,
                Defaults.PREY_MAX_NUMBER
            ),
            this.createSliderDefault(
                "Initial Population",
                config::setPreyInitialPopulation,
                0,
                Defaults.PREY_INITIAL_POPULATION
            ),
            this.createSliderDefault(
                "View Cone Angle",
                config::setPreyViewAngle,
                0d,
                Defaults.PREY_VIEW_ANGLE
            ),
            this.createSliderDefault(
                "Split Energy Fill Speed",
                config::setPreySplitEnergyFillingSpeed,
                0d,
                Defaults.PREY_SPLIT_ENERGY_FILLING_SPEED
            )
        ));

        SafeSlider sliderLayer = this.createSliderDefault(
                "Neural network layers",
                null,
                2,
                Defaults.NEURAL_NETWORK_LAYERS_NUMBER
                );
        VBox neuralNetworksSliders = new VBox(sliderLayer);

        addLayersSliders();
        neuralNetworksSliders.getChildren().addAll(this.layersSafeSliders);
        sliderLayer.valueProperty().addListener(event -> {
            try {
                config.setNeuralNetworkLayersNumber((Integer) sliderLayer.getValue());
                neuralNetworksSliders.getChildren().removeAll(this.layersSafeSliders);
                addLayersSliders();
                neuralNetworksSliders.getChildren().addAll(this.layersSafeSliders);
            } catch (EvosimException e) {
                throw new RuntimeException(e);
            }
        });

        this.submissionCallbacks.add(this::setLayersSizeMiddle);

        TitledPane neuralNetworkDropdown = new TitledPane(
                "Neural network",
                neuralNetworksSliders
        );

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
                                                              final T minValue,
                                                              final T defaultValue
    ) {
        return createSlider(
                name,
                setter,
                (isNumberFloatingPoint(minValue)
                        ? (T) Double.valueOf(minValue.doubleValue())
                        : (T) Integer.valueOf(minValue.intValue())
                ),
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
        final SafeSlider slider = createSlider(
                name,
                min, hardMin,
                max, hardMax,
                defaultValue
        );

        if (setter != null) {
            this.submissionCallbacks.add(
                    () -> setter.accept((T) slider.getValue())
            );
        }

        return slider;
    }

    private <T extends Number> SafeSlider createSlider(
            final String name,
            final T min, final boolean hardMin,
            final T max, final boolean hardMax,
            final Number defaultValue
    ) {
        final SafeSlider slider = new SafeSlider();
        slider.setFloatingPoint(isNumberFloatingPoint(defaultValue));
        slider.setMin(min);
        slider.setHardMin(hardMin);
        slider.setMax(max);
        slider.setHardMax(hardMax);
        slider.setName(name);
        slider.setValue(defaultValue);
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

    private void setLayersSizeMiddle() {
        try {
            List<Integer> slidersValue =
                    this.layersSafeSliders
                            .stream()
                            .map(safeSlider -> safeSlider.getValue().intValue())
                            .toList();

            Configuration.getConfiguration().setLayerSizeMiddle(slidersValue);
        } catch (EvosimException e) {
            throw new RuntimeException(e);
        }
    }

    private void addLayersSliders() throws EvosimException {
        int layersNumber = Configuration.getConfiguration().getNeuralNetworkLayersNumber();

        int listSize = this.layersSafeSliders.size();
        if (layersNumber - 2 > listSize) {
            this.layersSafeSliders.add(this.createSliderDefault(
                    "Neural Network middle layer #" + (listSize + 1),
                    null,
                    1,
                    Defaults.NODES_PER_LAYER
            ));
        }
        if (layersNumber - 2 < listSize) {
            this.layersSafeSliders.remove(listSize - 1);
        }
    }
}
