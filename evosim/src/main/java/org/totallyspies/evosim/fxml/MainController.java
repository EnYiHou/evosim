package org.totallyspies.evosim.fxml;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import lombok.Getter;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.MapCanvas;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.totallyspies.evosim.ui.SettingsWindow;
import org.totallyspies.evosim.utils.Configuration;

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
     * The current controller being used.
     */
    @Getter
    private static MainController controller;

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
     * A {@code #AnchorPane} containing the entity stats. Disabled when no entities are tracked.
     */
    @FXML
    @Getter
    private AnchorPane statsContainer;

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
     * An {@code #HBox} containing the progress bar and label for the entity split energy.
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
     * The simulation to be rendered.
     */
    private Simulation simulation;

    /**
     *  In order to explore the user's files.
     */
    private final FileChooser fileChooser;

    /**
     * The global configuration of the application.
     */
    private static Configuration configuration;

    /**
     * Constructor to create the MainController object.
     */
    public MainController() {
        controller = this;
        this.fileChooser = new FileChooser();
        MainController.configuration = Configuration.getConfiguration();
    }

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() {
        this.simulation = new Simulation();
        this.mapCanvas.attach(simulation);
        this.setPlayPauseButtons();
        this.setChart(totalPopulationChart);
        this.setChart(preyPopulationChart);
        this.setChart(predatorPopulationChart);
        this.setTimer();
        Scene scene = EvosimApplication.getApplication().getStage().getScene();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.ESCAPE) {
                escapeClicked();
            }
            if (!MapCanvas.getPressedKeys().contains(code)) {
                MapCanvas.getPressedKeys().push(code);
            }
        });

        scene.setOnKeyReleased(event ->
                MapCanvas.getPressedKeys().remove(event.getCode()));

        String evosimDir = Paths.get(
                System.getProperty("user.home"), "Documents", "Evosim").toString();
        File evosimFolder = new File(evosimDir);

        if (!evosimFolder.exists()) {
            evosimFolder.mkdir();
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON File", "*.json"));
        fileChooser.setInitialDirectory(
                new File(evosimDir));
    }

    /**
     * Sets the chart of the simulation.
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
    public void setTimer() {
        AtomicLong counter = new AtomicLong();
        this.timerProperty = new SimpleObjectProperty<>(java.time.Duration.ZERO);
        this.timerLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%02d:%02d:%02d",
                                timerProperty.getValue().toHoursPart(),
                                timerProperty.getValue().toMinutesPart(),
                                timerProperty.getValue().toSecondsPart()),
                        this.timerProperty));
        this.timerTimeLine = new Timeline(new javafx.animation.KeyFrame(Duration.millis(100), e -> {
            this.timerProperty.set(this.timerProperty.get().plusMillis(100));

            XYChart.Series<String, Number> totalPopulationChartSeries;
            totalPopulationChartSeries = (XYChart.Series<String, Number>) totalPopulationChart
                    .getData().get(0);
            totalPopulationChartSeries.getData().add(
                    new XYChart.Data<>(counter.toString(),
                            this.simulation.getPreyCount() + this.simulation.getPredatorCount()));

            XYChart.Series<String, Number> preyPopulationChartSeries;
            preyPopulationChartSeries = (XYChart.Series<String, Number>) preyPopulationChart
                    .getData().get(0);
            preyPopulationChartSeries.getData().add(
                    new XYChart.Data<>(
                            counter.toString(), this.simulation.getPreyCount()));

            XYChart.Series<String, Number> predatorPopulationChartSeries;
            predatorPopulationChartSeries = (XYChart.Series<String, Number>) predatorPopulationChart
                    .getData().get(0);
            predatorPopulationChartSeries.getData().add(
                    new XYChart.Data<>(counter.toString(),
                            this.simulation.getPredatorCount()));

            checkChartSize(totalPopulationChartSeries);
            checkChartSize(preyPopulationChartSeries);
            checkChartSize(predatorPopulationChartSeries);

            counter.getAndAdd(1);
        }));
        this.timerTimeLine.setCycleCount(Timeline.INDEFINITE);
        this.timerTimeLine.play();
    }

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
            this.simulation.getAnimationLoop().start();
            this.mapCanvas.play();
            this.playBtn.setDisable(true);
            this.pauseBtn.setDisable(false);
            this.timerTimeLine.play();
        });
        this.pauseBtn.setOnAction(e -> {
            this.simulation.getAnimationLoop().stop();
            this.mapCanvas.pause();
            this.playBtn.setDisable(false);
            this.pauseBtn.setDisable(true);
            this.timerTimeLine.stop();
        });
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
     * Opens a modal SettingsWindow when the "Modify Preferences" option is clicked under Settings.
     */
    @FXML
    private void settingsMenuClicked() {
        SettingsWindow sw = new SettingsWindow(EvosimApplication.getApplication().getStage());
        sw.getSettingsStage().show();
    }

    @FXML
    private void clickOnSave(final ActionEvent event) throws IOException {
        fileChooser.setTitle("Save Configuration");
        File file = fileChooser.showSaveDialog(EvosimApplication.getApplication().getStage());

        if (file != null) {
            configuration.saveConfiguration(file);
        }
    }

    @FXML
    private void clickOnLoad(final ActionEvent event) throws IOException {
        fileChooser.setTitle("Load Configuration");
        File file = fileChooser.showOpenDialog(EvosimApplication.getApplication().getStage());

        if (file != null) {
            configuration.loadConfiguration(file);
            System.out.println(configuration.toString());
        } else {
            System.out.println("File doesn't exist or is not JSON.");
        }

    }

    @FXML
    private void clickOnLoadLatest(final ActionEvent event) {
        configuration.loadLastConfiguration();
        System.out.println(configuration.toString());
    }

    @FXML
    private void clickOnLoadDefault(final ActionEvent event) {
        configuration.loadDefaultConfiguration();
        System.out.println(configuration.toString());
    }


    @FXML
    private void clickOnExit(final ActionEvent event) throws IOException {
        configuration.saveLatestConfiguration();
        Platform.exit();
    }

    /**
     * Display an alert to the user to confirm if they'd like to close the app.
     */
    @FXML
    private void escapeClicked() {
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you'd like to exit Evosim?",
                ButtonType.YES,
                ButtonType.NO
        );

        Optional<ButtonType> selection = confirmation.showAndWait();
        if (selection.isEmpty() || selection.get() == ButtonType.NO) {
            confirmation.close();
        } else if (selection.get() == ButtonType.YES) {
            System.out.println("Shutting down application...");
            EvosimApplication.getApplication().getStage().close();
        }
    }

}
