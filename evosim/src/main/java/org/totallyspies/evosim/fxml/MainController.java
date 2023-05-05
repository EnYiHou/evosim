package org.totallyspies.evosim.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
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
     * The simulation to be rendered.
     */
    private Simulation simulation;

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
    }

    /**
     * Initializes {@code main.fxml}.
     */
    public void initialize() throws IOException {
        this.simulation = new Simulation();
        this.mapCanvas.attach(simulation);

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
    private void clickOnSave(final ActionEvent event) throws IOException {
        if (configurationFile == null) {
            fileChooser.setTitle("Save Configuration");
            configurationFile = fileChooser.showSaveDialog(EvosimApplication.getApplication().getStage());
        }
        if(configurationFile != null) {
            configuration.saveConfiguration(configurationFile, mapCanvas.getSimulation());
        }
    }

    /**
     * Saving as a configuration with the menu bar.
     * @param event on click
     * @throws IOException
     */
    @FXML
    private void clickOnSaveAs(final ActionEvent event) throws IOException {
        fileChooser.setTitle("Save Configuration");
        configurationFile = fileChooser.showSaveDialog(EvosimApplication.getApplication().getStage());
        if(configurationFile != null) {
            configuration.saveConfiguration(configurationFile, mapCanvas.getSimulation());
        }
    }

    /**
     * Loading the configuration.
     * @param event on click
     * @throws IOException
     */
    @FXML
    private void clickOnLoad(final ActionEvent event) throws IOException {
        fileChooser.setTitle("Load Configuration");
        File file = fileChooser.showOpenDialog(EvosimApplication.getApplication().getStage());

        if (file != null) {
            configuration.loadFile(file);
            System.out.println(configuration.toString());
        } else {
            System.out.println("File doesn't exist or is not JSON.");
        }

    }

    /**
     * Loading the latest configuration.
     * @param event on click
     */
    @FXML
    private void clickOnLoadLatest(final ActionEvent event) {
        configuration.loadLastFile();
        System.out.println(configuration.toString());
    }

    /**
     * Loading the default configuration.
     * @param event on click
     */
    @FXML
    private void clickOnLoadDefault(final ActionEvent event) {
        configuration.loadDefaultFileAndSetup();
        System.out.println(configuration.toString());
    }

    /**
     * Display an alert to the user to confirm if they'd like to close the app.
     * @param event
     */
    @FXML
    private void clickOnExit(final ActionEvent event) throws IOException {
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

    /**
     * Unfollow an entity clicking on the menu.
     */
    @FXML
    private void clickOnUnfollow() {
        Thread tread =  new Thread(() -> {
            MapCanvas.getPRESSED_KEYS().add(KeyCode.B);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            MapCanvas.getPRESSED_KEYS().remove(KeyCode.B);
        });
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
        this.configuration.saveLatestConfiguration(simulation);
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
            this.simulation.playUpdate();
            this.playBtn.setDisable(true);
            this.pauseBtn.setDisable(false);
            this.timerTimeLine.play();
        });
        this.pauseBtn.setOnAction(e -> {
            this.simulation.pauseUpdate();
            this.playBtn.setDisable(false);
            this.pauseBtn.setDisable(true);
            this.timerTimeLine.stop();
        });
    }

    /**
     * Set the chosen entity property that will be displayed.
     */
    private void setChosenEntityProperty() {
        this.chosenEntityProperty = new SimpleObjectProperty<>();
        this.chosenEntityProperty.set(new Predator(
                1,
                new Point(0, 0),
                1,
                System.currentTimeMillis())
        );
    }

    /**
     * Change the opacity of WASD keys rectangles.
     * @param keyCode key associated with the rectangle
     * @param darkerOpacity should the rectangle be darker
     */
    private void changeOpacityWASD(final KeyCode keyCode, final boolean darkerOpacity) {

        double opacityPercentage = darkerOpacity ? 1d : 0.55d;

        if (keyCode == KeyCode.W) {
            wKey.setOpacity(opacityPercentage);
        }
        if (keyCode == KeyCode.A) {
            aKey.setOpacity(opacityPercentage);
        }
        if (keyCode == KeyCode.S) {
            sKey.setOpacity(opacityPercentage);
        }
        if (keyCode == KeyCode.D) {
            dKey.setOpacity(opacityPercentage);
        }
    }

}
