package org.totallyspies.evosim.fxml;

import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.util.Duration;
import lombok.Getter;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.ui.AboutWindow;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.ui.MapCanvas;
import java.util.concurrent.atomic.AtomicLong;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
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
     * The simulation to be rendered.
     */
    private Simulation simulation;

    /**
     *  In order to explore the user's files.
     */
    private FileChooser fileChooser;

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
    }


    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() throws IOException {

        this.simulation = new Simulation();
        this.setPlayPauseButtons();
        this.setChart(totalPopulationChart);
        this.setChart(preyPopulationChart);
        this.setChart(predatorPopulationChart);
        this.setTimer();
        this.chosenEntityProperty = new SimpleObjectProperty<>();
        this.chosenEntityProperty.set(new Predator(1, new Point(0, 0),
                1, System.currentTimeMillis()));
        this.setEntityInfoTab();
        this.mapCanvas.attach(simulation);

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
        configuration.saveLatestConfiguration();
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

    private void checkChartSize(final XYChart.Series<String, Number> series) {
        if (series.getData().size() > MAX_CHART_POINTS) {
            series.getData().remove(0);
        }
    }

    /**
     * Set the play and pause buttons.
     */
    public void setPlayPauseButtons() {
        this.playBtn.setOnAction(e -> {
            this.simulation.getAnimationLoop().start();
            this.playBtn.setDisable(true);
            this.pauseBtn.setDisable(false);
            this.timerTimeLine.play();
        });
        this.pauseBtn.setOnAction(e -> {
            this.simulation.getAnimationLoop().stop();
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
        aw.getAbtStage().showAndWait();
    }

    @FXML
    private void clickOnSave(final ActionEvent event) throws IOException {
        if(!isSaved) {
            fileChooser.setTitle("Save Configuration");
            File file = fileChooser.showSaveDialog(EvosimApplication.getApplication().getStage());

            if (file != null) {
                configuration.saveConfiguration(file);
            }
            isSaved = true;
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
}
