package org.totallyspies.evosim.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import lombok.Getter;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.MapCanvas;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Controller for the {@code welcome.fxml} file. Dynamically adds all input fields.
 */
public final class MainController {

    /**
     * Max chart points.
     */
    private static final int MAX_CHART_POINTS = 10;

    /**
     * One seconds in milliseconds.
     */
    private static final long ONE_SECOND_IN_MILLISECONDS = 1000;

    /**
     * One decisecond in milliseconds.
     */
    private static final long ONE_DECISECOND_IN_MILLISECONDS = 100;

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
     * The timer label of the simulation.
     */
    @FXML
    private Label timerLabel;

    /**
     * Label for the chosen entity's energy.
     */
    @FXML
    private Label energyLabel;

    /**
     * Label for the chosen entity's splitEnergy.
     */
    @FXML
    private Label splitEnergyLabel;

    /**
     * Label for the chosen entity's speed.
     */
    @FXML
    private Label speedLabel;

    /**
     * Label for the chosen entity's child count.
     */
    @FXML
    private Label childCountLabel;

    /**
     * Label for the chosen entity's livingTime.
     */
    @FXML
    private Label livingTimeLabel;

    /**
     * Entity information tab.
     */
    @Getter
    @FXML
    private Tab entityInfoTab;

    /**
     * Entity information label.
     */
    @FXML
    private Label entityInfoLabel;

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
    private ObjectProperty<Entity> chosenEntityProperty;


    /**
     * The timer of the simulation.
     */
    private ObjectProperty<java.time.Duration> timerProperty;

    /**
     * The counter of the simulation.
     */
    private Timeline timerTimeLine;

    /**
     *  In order to explore the user's files.
     */
    private FileChooser fileChooser;

    /**
     * The configuration File running.
     */
    private File configurationFile;

    /**
     * As if the variable is saved or not on the drive.
     */
    private boolean isSaved;

    /**
     * The global configuration of the application.
     */
    private static Configuration configuration;

    /**
     * Constructor to create the MainController object.
     */
    public MainController() {
        this.fileChooser = new FileChooser();
        MainController.configuration = Configuration.getConfiguration();
        isSaved = false;
        EvosimApplication.getApplication().getShutdownHooks().add(this::shutdown);
    }

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() throws IOException {
        this.newDefaultSimulation();

        this.setPlayPauseButtons();
        this.setTimer();

        this.setChart(totalPopulationChart);
        this.setChart(preyPopulationChart);
        this.setChart(predatorPopulationChart);

        this.setChosenEntityProperty();
        this.setEntityInfoTab();
        this.setKeyPress();

        this.setupSavingDirectory();

    }

    private void newDefaultSimulation() {
        this.mapCanvas.attach(new Simulation(true));
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
     * @param event on click
     * @throws IOException
     */
    @FXML
    private void clickOnSave(final ActionEvent event) throws ConfigurationException {
        pauseAnimation();
        if (configurationFile == null) {
            fileChooser.setTitle("Save Configuration");
            configurationFile = fileChooser
                    .showSaveDialog(EvosimApplication.getApplication().getStage());
        }
        if (configurationFile != null) {
            configuration.saveConfiguration(configurationFile, mapCanvas.getSimulation());
        }
        playAnimation();
    }

    /**
     * Saving as a configuration with the menu bar.
     * @param event on click
     * @throws IOException
     */
    @FXML
    private void clickOnSaveAs(final ActionEvent event) throws ConfigurationException {
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
     * @param event on click
     * @throws IOException
     */
    @FXML
    private void clickOnLoad(final ActionEvent event) throws ConfigurationException {
        pauseAnimation();
        fileChooser.setTitle("Load Configuration");
        File file = fileChooser.showOpenDialog(EvosimApplication.getApplication().getStage());
        newSimulation(configuration.loadFile(file));
    }

    private void newSimulation(final List<Entity> entityList) {
        Simulation simulation = new Simulation(false);
        entityList.forEach(simulation::addEntity);
        mapCanvas.attach(simulation);
        pauseAnimation();
    }

    /**
     * Loading the latest configuration.
     * @param event on click
     */
    @FXML
    private void clickOnLoadLatest(final ActionEvent event) throws ConfigurationException {
        pauseAnimation();
        this.newSimulation(configuration.loadLastFile());
    }

    /**
     * Loading the default configuration.
     * @param event on click
     */
    @FXML
    private void clickOnLoadDefault(final ActionEvent event) {
        configuration.restoreToDefaults();
        this.newDefaultSimulation();
    }

    /**
     * Display an alert to the user to confirm if they'd like to close the app.
     * @param event
     */
    @FXML
    private void clickOnExit(final ActionEvent event) {
        EvosimApplication.getApplication().requestExit(null);
    }

    private void shutdown() {
        try {
            pauseAnimation();
            Configuration
                    .getConfiguration().saveLatestConfiguration(this.mapCanvas.getSimulation());
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unfollow an entity clicking on the menu.
     */
    @FXML
    private void clickOnUnfollow() {
        Thread tread =  new Thread(() -> {
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

    /**
     * Setting the keys movements.
     */
    private void setKeyPress() {
        Scene scene = EvosimApplication.getApplication().getStage().getScene();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (!MapCanvas.getPRESSED_KEYS().contains(code)
                    && MapCanvas.getACCEPTED_KEYS().contains(code)
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
     * Setting the saving directory for the project.
     * @throws IOException
     */
    private void setupSavingDirectory() throws IOException {
        String evosimDir = Paths.get(
                System.getProperty("user.home"), "Documents", "Evosim").toString();
        File evosimFolder = new File(evosimDir);

        if (!evosimFolder.exists()) {
            evosimFolder.mkdir();
        }

        this.fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON File", "*.json"));
        this.fileChooser.setInitialDirectory(
                new File(evosimDir));
    }

    /**
     * Sets the play and pause buttons.
     */
    private void setEntityInfoTab() {
        this.entityInfoLabel.setText(this.chosenEntityProperty.getValue().toString());
        this.energyLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Energy: %.2f",
                        this.chosenEntityProperty.getValue().getEnergy()),
                this.chosenEntityProperty));
        this.splitEnergyLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Split Energy: %.2f",
                        this.chosenEntityProperty.getValue().getSplitEnergy()),
                this.chosenEntityProperty));
        this.speedLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Speed: %.2f",
                        this.chosenEntityProperty.getValue().getSpeed()),
                this.chosenEntityProperty));
        this.childCountLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Child Count: %d",
                        this.chosenEntityProperty.getValue().getChildCount()),
                this.chosenEntityProperty));
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


    /**
     * Sets the timer of the simulation.
     */
    private void setTimer() {
        AtomicLong counter = new AtomicLong();
        this.timerProperty = new SimpleObjectProperty<>(java.time.Duration.ZERO);
        this.timerLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%02d:%02d:%02d",
                                timerProperty.getValue().toHoursPart(),
                                timerProperty.getValue().toMinutesPart(),
                                timerProperty.getValue().toSecondsPart()),
                        this.timerProperty));
        this.timerTimeLine = new Timeline(new javafx.animation.KeyFrame(
                Duration.millis(ONE_DECISECOND_IN_MILLISECONDS), e -> {
            this.timerProperty.set(
                    this.timerProperty.get().plusMillis(ONE_DECISECOND_IN_MILLISECONDS));

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

            livingTimeLabel.setText(
                    String.format("Living Time: %d s",
                            this.chosenEntityProperty.getValue()
                                    .getLivingTime(System.currentTimeMillis())));
            checkChartSize(totalPopulationChartSeries);
            checkChartSize(preyPopulationChartSeries);
            checkChartSize(predatorPopulationChartSeries);

            counter.getAndAdd(1);
        }));
        this.timerTimeLine.setCycleCount(Timeline.INDEFINITE);
        this.timerTimeLine.play();
    }

    /**
     * Verify the chart sizes.
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
     * Set the chosen entity property that will be displayed.
     */
    private void setChosenEntityProperty() {
        this.chosenEntityProperty = new SimpleObjectProperty<>();
        this.chosenEntityProperty.set(new Predator(
                1,
                new Point(0, 0),
                1)
        );
    }

    /**
     * Change the opacity of WASD keys rectangles.
     * @param keyCode key associated with the rectangle
     * @param darkerOpacity should the rectangle be darker
     */
    private void changeOpacityWASD(final KeyCode keyCode, final boolean darkerOpacity) {

        double opacityPercentage = darkerOpacity ? MAX_OPACITY : MIN_OPACITY;

        switch (keyCode) {
            case W -> wKey.setOpacity(opacityPercentage);
            case A -> aKey.setOpacity(opacityPercentage);
            case S -> sKey.setOpacity(opacityPercentage);
            case D -> dKey.setOpacity(opacityPercentage);
            default -> { }
        }
    }

}
