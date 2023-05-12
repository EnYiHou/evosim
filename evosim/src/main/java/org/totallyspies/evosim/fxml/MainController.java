package org.totallyspies.evosim.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import lombok.Getter;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.MapCanvas;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.totallyspies.evosim.ui.NeuralNetworkView;
import org.totallyspies.evosim.ui.SettingsWindow;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.EvosimException;
import org.totallyspies.evosim.utils.FileSelector;

import java.io.File;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input fields.
 */
public final class MainController {

    /**
     * The current controller being used.
     */
    @Getter
    private static MainController controller;

    /**
     * One seconds in milliseconds.
     */
    private static final long ONE_SECOND_IN_MILLISECONDS = 1000;

    /**
     * One decisecond in milliseconds.
     */
    private static final long ONE_DECISECOND_IN_MILLISECONDS = 100;


    /**
     * Max chart points.
     */
    private static final int MAX_CHART_POINTS
            = (int) (30 * ONE_SECOND_IN_MILLISECONDS / ONE_DECISECOND_IN_MILLISECONDS);

    /**
     * Maximum opacity of WASD keys.
     */
    private static final double MAX_OPACITY = 1d;

    /**
     * Minimum opacity of WASD keys.
     */
    private static final double MIN_OPACITY = 0.55;

    /**
     * FXML reference to the map where simulation is rendered.
     */
    @FXML
    private MapCanvas mapCanvas;

    /**
     * The AnchorPane containing all elements on top of the map.
     */
    @FXML
    private AnchorPane mapOverlay;

    /**
     * Play button.
     */
    @FXML
    private Button playBtn;

    /**
     * Pause button.
     */
    @FXML
    private Button pauseBtn;

    /**
     * Total population chart.
     */
    @FXML
    private LineChart totalPopulationChart;

    /**
     * Prey population chart.
     */
    @FXML
    private LineChart preyPopulationChart;

    /**
     * Predator population chart.
     */
    @FXML
    private LineChart predatorPopulationChart;

    /**
     * Tab pane containing the charts and the neural network view.
     */
    @FXML
    @Getter
    private TabPane tabPane;

    /**
     * The neural network view.
     */
    @Getter
    private NeuralNetworkView neuralNetworkTab;


    /**
     * A {@code #VBox} containing the nodes displaying entity stats on focus.
     */
    @FXML
    @Getter
    private VBox entityStats;

    /**
     * Label for the chosen entity's speed.
     */
    @FXML
    @Getter
    private Label speedLabel;

    /**
     * Label for the chosen entity's child count.
     */
    @FXML
    @Getter
    private Label childCountLabel;

    /**
     * Label for the chosen entity's livingTime.
     */
    @FXML
    @Getter
    private Label livingTimeLabel;

    /**
     * A {@code #ProgressBar} displaying the tracked entity's energy level.
     */
    @FXML
    @Getter
    private ProgressBar pbEnergy;

    /**
     * A {@code #ProgressBar} displaying the tracked entity's split energy level.
     */
    @FXML
    @Getter
    private ProgressBar pbSplit;

    /**
     * Rectangle for W key.
     */
    @FXML
    private Rectangle wKey;

    /**
     * Rectangle for A key.
     */
    @FXML
    private Rectangle aKey;

    /**
     * Rectangle for S key.
     */
    @FXML
    private Rectangle sKey;

    /**
     * Rectangle for D key.
     */
    @FXML
    private Rectangle dKey;

    /**
     * The chosen Entity.
     */
    @FXML
    @Getter
    private HBox energyBar;

    /**
     * An {@code #HBox} containing the progress bar and label for the entity energy.
     */
    @FXML
    @Getter
    private HBox splitEnergyBar;

    /**
     * The timer label of the simulation.
     */
    @FXML
    private Label timerLabel;

    /**
     * The timer of the simulation.
     */
    @Getter
    private ObjectProperty<java.time.Duration> timerProperty;

    /**
     * The counter of the simulation.
     */
    private Timeline timerTimeLine;

    /**
     * In order to explore the user's files.
     */
    private final FileChooser fileChooser;

    /**
     * The configuration File running.
     */
    private File configurationFile;

    /**
     * The global configuration of the application.
     */
    private static Configuration configuration;

    /**
     * Constructor to create the MainController object.
     */
    public MainController() {
        controller = this;
        this.fileChooser = FileSelector.getFileChooserJson();
        MainController.configuration = Configuration.getConfiguration();
        EvosimApplication.getApplication().getPreShutdownHooks().add(this::pauseAnimation);
        EvosimApplication.getApplication().getShutdownHooks().add(this::shutdown);
    }

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() throws EvosimException {
        this.timerProperty = new SimpleObjectProperty<>();
        this.initializeSimulation();

        this.timerLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%02d:%02d:%02d",
                                timerProperty.getValue().toHoursPart(),
                                timerProperty.getValue().toMinutesPart(),
                                timerProperty.getValue().toSecondsPart()),
                        this.timerProperty));

        NeuralNetworkView neuronView = new NeuralNetworkView();
        neuronView.setDisable(true);
        tabPane.getTabs().add(neuronView);
        this.neuralNetworkTab = neuronView;

        this.setXYCharts();
        this.setPlayPauseButtons();

        this.setChart(totalPopulationChart);
        this.setChart(preyPopulationChart);
        this.setChart(predatorPopulationChart);

        this.setKeyPress();

        playAnimation();
    }

    private void initializeSimulation() throws EvosimException {
        configurationFile = (File) EvosimApplication.getApplication().getStage().getUserData();
        if (configurationFile != null) {
            newSimulation(configuration.loadFile(configurationFile));
        } else {
            this.newDefaultSimulation();
        }
    }

    private void newSimulation(final List<Entity> entityList) throws EvosimException {
        Simulation simulation = new Simulation(
                Configuration.getConfiguration().getMapSizeX(),
                Configuration.getConfiguration().getMapSizeY(),
                Configuration.getConfiguration().getGridSize(),
                false
        );

        entityList.forEach(simulation::addEntity);
        mapCanvas.attach(simulation);
        MapCanvas.setMapImage(configuration.getBackgroundImage());
        this.timerProperty.set(configuration.getDuration());
    }

    private void newDefaultSimulation() throws EvosimException {
        this.mapCanvas.attach(new Simulation(
                Configuration.getConfiguration().getMapSizeX(),
                Configuration.getConfiguration().getMapSizeY(),
                Configuration.getConfiguration().getGridSize(),
                true
        ));
        configuration.setDuration(java.time.Duration.ZERO);
        configuration.setBackgroundImage(null);
        MapCanvas.setMapImage(configuration.getBackgroundImage());
        this.timerProperty.set(configuration.getDuration());
    }

    /**
     * Opens a modal AboutWindow when the "About" option is clicked under Help.
     */
    @FXML
    private void aboutMenuClicked() {
        AboutWindow aw = new AboutWindow(EvosimApplication.getApplication().getStage());
        aw.getAbtStage().show();
    }

    /**
     * Saving a configuration with the menu bar.
     *
     * @param event on click
     */
    @FXML
    private void clickOnSave(final ActionEvent event) throws EvosimException {
        if (configurationFile == null) {
            pauseAnimation();
            this.fileChooser.setTitle("Save Configuration");
            configurationFile = fileChooser
                    .showSaveDialog(EvosimApplication.getApplication().getStage());
            if (configurationFile != null) {
                configuration.saveConfiguration(configurationFile, mapCanvas.getSimulation());
            }
        }
    }

    /**
     * Saving as a configuration with the menu bar.
     *
     * @param event on click
     */
    @FXML
    private void clickOnSaveAs(final ActionEvent event) throws EvosimException {
        pauseAnimation();
        fileChooser.setTitle("Save Configuration");
        configurationFile = fileChooser
                .showSaveDialog(EvosimApplication.getApplication().getStage());
        if (configurationFile != null) {
            configuration.saveConfiguration(configurationFile, mapCanvas.getSimulation());
        }
        playAnimation();
    }

    /**
     * Loading the configuration.
     *
     * @param event on click
     */
    @FXML
    private void clickOnLoad(final ActionEvent event) throws EvosimException {
        pauseAnimation();
        fileChooser.setTitle("Load Configuration");
        File file = fileChooser.showOpenDialog(EvosimApplication.getApplication().getStage());
        if (file != null) {
            newSimulation(configuration.loadFile(file));
            return;
        }
        playAnimation();
    }

    /**
     * Loading the latest configuration.
     *
     * @param event on click
     */
    @FXML
    private void clickOnLoadLatest(final ActionEvent event) throws EvosimException {
        pauseAnimation();
        this.newSimulation(configuration.loadLastFile());
    }

    /**
     * Loading the default configuration.
     *
     * @param event on click
     */
    @FXML
    private void clickOnLoadDefault(final ActionEvent event) throws EvosimException {
        pauseAnimation();
        configuration.restoreToDefaults();
        this.newDefaultSimulation();
    }

    /**
     * Display an alert to the user to confirm if they'd like to close the app.
     *
     * @param event
     */
    @FXML
    private void clickOnExit(final ActionEvent event) {
        pauseAnimation();
        EvosimApplication.getApplication().requestExit(null);
    }

    /**
     * Unfollow an entity clicking on the menu.
     */
    @FXML
    private void clickOnUnfollow() {
        Thread tread = new Thread(() -> {
            MapCanvas.getPRESSED_KEYS().add(KeyCode.B);
            try {
                Thread.sleep(ONE_SECOND_IN_MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            MapCanvas.getPRESSED_KEYS().remove(KeyCode.B);
        });
        tread.start();
    }

    private void shutdown() {
        try {
            playAnimation();
            Configuration
                    .getConfiguration().saveLatestConfiguration(this.mapCanvas.getSimulation());
        } catch (EvosimException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Setting the keys movements.
     */
    private void setKeyPress() {
        Scene scene = EvosimApplication.getApplication().getStage().getScene();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (!MapCanvas.getPRESSED_KEYS().contains(code)
                    && !event.isControlDown()) {
                MapCanvas.getPRESSED_KEYS().push(code);
                changeOpacityWASD(code, true);
            }
        });

        scene.setOnKeyReleased(event -> {
            MapCanvas.getPRESSED_KEYS().remove(event.getCode());
            changeOpacityWASD(event.getCode(), false);
        });
    }

    /**
     * Sets the charts of the simulation.
     *
     * @param chart the chart to be set
     */
    private void setChart(final LineChart chart) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        chart.getData().add(series);
        chart.getXAxis().setTickMarkVisible(false);
        chart.getXAxis().setTickLabelsVisible(false);

        chart.setCreateSymbols(false); // disable symbols
    }

    private void setXYCharts() {
        AtomicLong counter = new AtomicLong();
        this.timerTimeLine = new Timeline(
                new javafx.animation.KeyFrame(
                        Duration.millis(ONE_DECISECOND_IN_MILLISECONDS), e -> {

                    configuration.setDuration(
                            this.timerProperty.get().plusMillis(ONE_DECISECOND_IN_MILLISECONDS));

                    try {
                        this.timerProperty.set(configuration.getDuration());
                    } catch (EvosimException ex) {
                        throw new RuntimeException(ex);
                    }

                    XYChart.Series<String, Number> totalPopulationChartSeries;
                    totalPopulationChartSeries = (XYChart.Series<String, Number>) totalPopulationChart
                            .getData().get(0);
                    totalPopulationChartSeries.getData().add(
                            new XYChart.Data<>(counter.toString(),
                                    this.mapCanvas.getSimulation().getPreyCount()
                                            + this.mapCanvas.getSimulation().getPredatorCount()));

                    XYChart.Series<String, Number> preyPopulationChartSeries;
                    preyPopulationChartSeries = (XYChart.Series<String, Number>) preyPopulationChart
                            .getData().get(0);
                    preyPopulationChartSeries.getData().add(
                            new XYChart.Data<>(
                                    counter.toString(), this.mapCanvas.getSimulation().getPreyCount()));

                    XYChart.Series<String, Number> predatorPopulationChartSeries;
                    predatorPopulationChartSeries = (XYChart.Series<String, Number>) predatorPopulationChart
                            .getData().get(0);
                    predatorPopulationChartSeries.getData().add(
                            new XYChart.Data<>(counter.toString(),
                                    this.mapCanvas.getSimulation().getPredatorCount()));

                    checkChartSize(totalPopulationChartSeries);
                    checkChartSize(preyPopulationChartSeries);
                    checkChartSize(predatorPopulationChartSeries);

                    counter.getAndAdd(1);
                }));
        this.timerTimeLine.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Verify the chart sizes.
     *
     * @param series chart series
     */
    private void checkChartSize(final XYChart.Series<String, Number> series) {
        if (series.getData().size() > MAX_CHART_POINTS) {
            series.getData().remove(0);
        }
    }

    /**
     * Set the play and pause buttons.
     */
    private void setPlayPauseButtons() {
        this.playBtn.setOnAction(e -> {
            playAnimation();
        });
        this.pauseBtn.setOnAction(e -> {
            pauseAnimation();
        });
    }

    private void playAnimation() {
        this.mapCanvas.getSimulation().playUpdate();
        this.playBtn.setDisable(true);
        this.pauseBtn.setDisable(false);
        this.timerTimeLine.play();
    }

    private void pauseAnimation() {
        this.mapCanvas.getSimulation().pauseUpdate();
        this.playBtn.setDisable(false);
        this.pauseBtn.setDisable(true);
        this.timerTimeLine.stop();
    }

    /**
     * Opens a modal SettingsWindow when the "Modify Preferences" option is clicked under Settings.
     */
    @FXML
    private void settingsMenuClicked() {
        SettingsWindow sw = new SettingsWindow(EvosimApplication.getApplication().getStage());
        sw.getSettingsStage().show();
    }


    /**
     * Change the opacity of WASD keys rectangles.
     *
     * @param keyCode       key associated with the rectangle
     * @param darkerOpacity should the rectangle be darker
     */
    private void changeOpacityWASD(final KeyCode keyCode, final boolean darkerOpacity) {
        double opacityPercentage = darkerOpacity ? MAX_OPACITY : MIN_OPACITY;

        switch (keyCode) {
            case W -> wKey.setOpacity(opacityPercentage);
            case A -> aKey.setOpacity(opacityPercentage);
            case S -> sKey.setOpacity(opacityPercentage);
            case D -> dKey.setOpacity(opacityPercentage);
            default -> {
            }
        }
    }
}
