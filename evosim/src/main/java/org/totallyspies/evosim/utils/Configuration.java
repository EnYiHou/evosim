package org.totallyspies.evosim.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import lombok.ToString;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.simulation.Simulation;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Configuration class consists of saving the settings the user configurations. If the user close
 * the application without saving the configurations, the applications will remember them and
 * will load them if needed.
 *
 * @author niakouu
 */
@ToString
@SuppressWarnings("MissingJavadocMethod")
public final class Configuration {

    /**
     * Defaults values for each variable.
     */
    public static class Defaults {

        /**
         * The maximum speed at which the entity can rotate.
         */
        public static final double ENTITY_MAX_ROTATION_SPEED = 0.02;

        /**
         * The energy regained passively by a prey for surviving.
         */
        public static final double PREY_ENERGY_FILLING_SPEED = 0.001;

        /**
         * The energy regained by a predator from eating prey.
         */
        public static final double PREDATOR_ENERGY_FILLING_SPEED = 0.4;

        /**
         * The base energy lost rate of a predator.
         */
        public static final double PREDATOR_ENERGY_BASE_DRAINING_SPEED = 0.00003;

        /**
         * The number of sensors each entity has.
         */
        public static final int ENTITY_SENSORS_COUNT = 25;

        /**
         * The length of an entity's sensors.
         */
        public static final double ENTITY_SENSORS_LENGTH = 400.0;

        /**
         * The radius of an entity.
         */
        public static final double ENTITY_RADIUS = 15.0;

        /**
         * The maximum speed of an entity.
         */
        public static final double ENTITY_MAX_SPEED = 5.0;

        /**
         * The minimum possible speed of an entity.
         */
        public static final double ENTITY_MIN_SPEED = 1.0;

        /**
         * The mutation rate of the speed of the entity.
         */
        public static final double ENTITY_SPEED_MUTATION_RATE = 0.2;

        /**
         * The speed at which the energy of the entity will be drained.
         */
        public static final double ENTITY_ENERGY_DRAIN_RATE = 0.001;

        /**
         * The maximum number of predator that could be generated.
         */
        public static final int PREDATOR_MAX_NUMBER = 100;

        /**
         * The initial quantity of predator spawned.
         */
        public static final int PREDATOR_INITIAL_POPULATION = 20;

        /**
         * The view angle of a predator.
         */
        public static final double PREDATOR_VIEW_ANGLE = 60.0;

        /**
         * The split energy that a predator will gain when it eats a prey.
         */
        public static final double PREDATOR_SPLIT_ENERGY_FILLING_SPEED = 0.5;

        /**
         * The maximum number of prey that could be generated.
         */
        public static final int PREY_MAX_NUMBER = 100;

        /**
         * The initial quantity of prey spawned.
         */
        public static final int PREY_INITIAL_POPULATION = 60;

        /**
         * The view angle of a prey.
         */
        public static final double PREY_VIEW_ANGLE = 300.0;

        /**
         * The split energy that a prey will gain when it is not moving.
         */
        public static final double PREY_SPLIT_ENERGY_FILLING_SPEED = 0.0005;

        /**
         * The number of layers the neural network will contain.
         */
        public static final int NEURAL_NETWORK_LAYERS_NUMBER = 3;

        /**
         * Number of grids in the horizontal axis.
         */
        public static final int MAP_SIZE_X = 15;

        /**
         * Number of grids in the vertical axis.
         */
        public static final int MAP_SIZE_Y = 15;

        /**
         * Width and height of a single grid.
         */
        public static final int GRID_SIZE = 150;

        /**
         * The default timer duration.
         */
        public static final Duration DURATION = Duration.ZERO;

        /**
         * The default encoded image from Base64.
         */
        public static final String IMAGE_BASE_64 = "";

        /**
         * The default Color of the map.
         */
        public static final String COLOR_MAP = Color.LIGHTSKYBLUE.toString();

        /**
         * The default nodes of the layers at the middle.
         */
        public static final List<Integer> LAYER_SIZE_MIDDLE = List.of(10);

        /**
         * Middle layers default number of nodes.
         */
        public static final int NODES_PER_LAYER = 10;
    }

    /**
     * The name of the latest configuration file.
     */
    public static final File LATEST_CONFIGURATION =
        new File(System.getProperty("java.io.tmpdir"), "latestConfigurations.json");

    /**
     * The name of the tmp path of an img.
     */
    public static final File TMP_IMG_PATH =
        new File(System.getProperty("java.io.tmpdir"), "imgEvosim.png");

    /**
     * All the number variables needed for the configuration.
     */
    private HashMap<String, Number> numberVariables;

    /**
     * All the defaults values needed for the application.
     */
    private HashMap<String, Number> defaultsNumberVariables;

    /**
     * All the object variables needed for the configuration.
     */
    private HashMap<String, Object> objectVariables;

    /**
     * All the defaults objects needed for the application.
     */
    private HashMap<String, Object> defaultObjectVariables;

    /**
     * The only configuration that exists using the Singleton Pattern.
     */
    private static final Configuration CONFIGURATION = new Configuration();

    /**
     * An object mapper in order to serialize and deserialize values.
     */
    private ObjectMapper mapper;

    /**
     * Create a new default Configuration object, and the setup.
     */
    private Configuration() {
        this.defaultsNumberVariables = new HashMap<>();
        this.defaultObjectVariables = new HashMap<>();

        this.defaultsNumberVariables.put("entityMaxRotationSpeed",
            Defaults.ENTITY_MAX_ROTATION_SPEED);
        this.defaultsNumberVariables.put("entitySensorsCount", Defaults.ENTITY_SENSORS_COUNT);
        this.defaultsNumberVariables.put("entityRadius", Defaults.ENTITY_RADIUS);
        this.defaultsNumberVariables.put("entitySensorsLength", Defaults.ENTITY_SENSORS_LENGTH);
        this.defaultsNumberVariables.put("entityMaxSpeed", Defaults.ENTITY_MAX_SPEED);
        this.defaultsNumberVariables.put("entityMinSpeed", Defaults.ENTITY_MIN_SPEED);
        this.defaultsNumberVariables.put("entitySpeedMutationRate",
            Defaults.ENTITY_SPEED_MUTATION_RATE);
        this.defaultsNumberVariables.put("entityEnergyDrainRate",
            Defaults.ENTITY_ENERGY_DRAIN_RATE);

        this.defaultsNumberVariables.put("predatorMaxNumber", Defaults.PREDATOR_MAX_NUMBER);
        this.defaultsNumberVariables.put("predatorViewAngle", Defaults.PREDATOR_VIEW_ANGLE);
        this.defaultsNumberVariables.put("predatorSplitEnergyFillingSpeed",
            Defaults.PREDATOR_SPLIT_ENERGY_FILLING_SPEED);
        this.defaultsNumberVariables.put("predatorEnergyBaseDrainingSpeed",
            Defaults.PREDATOR_ENERGY_BASE_DRAINING_SPEED);
        this.defaultsNumberVariables.put("predatorEnergyFillingSpeed",
            Defaults.PREDATOR_ENERGY_FILLING_SPEED);
        this.defaultsNumberVariables.put("predatorInitialPopulation",
            Defaults.PREDATOR_INITIAL_POPULATION);

        this.defaultsNumberVariables.put("preyMaxNumber", Defaults.PREY_MAX_NUMBER);
        this.defaultsNumberVariables.put("preyViewAngle", Defaults.PREY_VIEW_ANGLE);
        this.defaultsNumberVariables.put("preySplitEnergyFillingSpeed",
            Defaults.PREY_SPLIT_ENERGY_FILLING_SPEED);
        this.defaultsNumberVariables.put("preyEnergyFillingSpeed",
            Defaults.PREY_ENERGY_FILLING_SPEED);
        this.defaultsNumberVariables.put("preyInitialPopulation", Defaults.PREY_INITIAL_POPULATION);

        this.defaultsNumberVariables.put("neuralNetworkLayersNumber",
            Defaults.NEURAL_NETWORK_LAYERS_NUMBER);

        this.defaultsNumberVariables.put("mapSizeX", Defaults.MAP_SIZE_X);
        this.defaultsNumberVariables.put("mapSizeY", Defaults.MAP_SIZE_Y);
        this.defaultsNumberVariables.put("gridSize", Defaults.GRID_SIZE);

        this.defaultObjectVariables.put("duration", Defaults.DURATION);
        this.defaultObjectVariables.put("backgroundImageBase64", Defaults.IMAGE_BASE_64);
        this.defaultObjectVariables.put("colorMap", Defaults.COLOR_MAP);
        this.defaultObjectVariables.put("layerSizeMiddle", Defaults.LAYER_SIZE_MIDDLE);


        this.mapper = new ObjectMapper();
        restoreToDefaults();
    }

    /**
     * Saves the default files that the user didn't have time to save.
     *
     * @param simulation The simulati
     */
    public void saveLatestConfiguration(final Simulation simulation) throws EvosimException {
        saveConfiguration(LATEST_CONFIGURATION, simulation);
    }

    /**
     * Saves a Configuration file in the temporary files of the user's computer.
     *
     * @param jsonFile   location of the new file place.
     * @param simulation simulation used.
     */
    public void saveConfiguration(
        final File jsonFile, final Simulation simulation) throws EvosimException {
        try {
            JSONObject jsonText = getJSONObject(simulation);

            if (jsonFile.exists()) {
                jsonFile.createNewFile();
            }

            try (FileWriter writer = new FileWriter(jsonFile)) {
                jsonText.write(writer);
            }
        } catch (Exception e) {
            throw new EvosimException("Could not save the JSON Configuration.", e);
        }
    }

    /**
     * Render the last configuration the user used before closing the
     * application.
     *
     * @return entity list saved.
     */
    public List<Entity> loadLastFile() throws EvosimException {
        return loadFile(LATEST_CONFIGURATION);
    }

    /**
     * Get a saved configuration in the temp file.
     *
     * @param jsonFile file we want to load.
     * @return entity list
     */
    public List<Entity> loadFile(final File jsonFile) throws EvosimException {
        JSONObject jsonGlobal = loadSavedFile(jsonFile);

        JSONObject jsonConfiguration = jsonGlobal.getJSONObject("configuration");
        if (jsonConfiguration != null) {
            loadConfiguration(jsonConfiguration);
        }

        JSONArray jsonEntities = jsonGlobal.getJSONArray("entities");
        return loadEntities(jsonEntities);
    }

    /**
     * Change configuration based on the jsonObject.
     *
     * @param jsonConfiguration
     */
    private void loadConfiguration(final JSONObject jsonConfiguration) {
        Set<String> numberKeys = this.numberVariables.keySet();
        numberKeys.forEach((key) -> this.numberVariables.replace(key,
            jsonConfiguration.getJSONObject("numbers").getNumber(key)));

        Set<String> objectKeys = this.objectVariables.keySet();
        objectKeys.forEach((key) -> this.objectVariables.replace(key,
            jsonConfiguration.getJSONObject("objects").get(key)));
    }

    private List<Entity> loadEntities(final JSONArray jsonEntities) throws EvosimException {
        List<Entity> entities;
        try {
            entities = mapper
                .readValue(jsonEntities.toString(), new TypeReference<>() {
                });

            return entities;
        } catch (Exception e) {
            throw new EvosimException("Couldn't load the entities of the JSON File.", e);
        }
    }

    /**
     * Load a saved Configuration JSON file and turn it into an JSONObject.
     *
     * @param jsonFile The file name of the json file we want to load.
     * @return JSONObject from a source JSON Configuration file.
     */
    private static JSONObject loadSavedFile(final File jsonFile) throws EvosimException {
        String jsonText = " ";
        try {
            jsonText = Files.readString(Path.of(jsonFile.getPath()));
            return new JSONObject(jsonText);
        } catch (Exception e) {
            throw new EvosimException("Couldn't load the saved Configuration JSON file.", e);
        }
    }

    /**
     * Makes a JSONObject, and put all the Configuration variables into it.
     *
     * @param simulation simulation used by MapCanvas.
     * @return JSONObject with Configuration's variables.
     */
    private JSONObject getJSONObject(
        final Simulation simulation) throws JsonProcessingException {
        JSONObject jsonObjectGlobal = new JSONObject();
        jsonObjectGlobal.put("configuration", getConfigurationJson());
        jsonObjectGlobal.put("entities", getEntitiesJSON(simulation));
        return jsonObjectGlobal;
    }

    private JSONObject getConfigurationJson() {
        JSONObject jsonConfiguration = new JSONObject();
        jsonConfiguration.put("numbers", this.numberVariables);
        jsonConfiguration.put("objects", this.objectVariables);
        return jsonConfiguration;
    }

    private JSONArray getEntitiesJSON(
        final Simulation simulation) throws JsonProcessingException {
        List<Entity> allEntities = new ArrayList<>();

        for (int x = 0; x < simulation.getMapSizeX(); x++) {
            for (int y = 0; y < simulation.getMapSizeY(); y++) {
                simulation.forEachGridEntities(x, y, allEntities::add);
            }
        }
        String allEntitiesTxt = mapper
            .writerFor(new TypeReference<List<Entity>>() {
            }).writeValueAsString(allEntities);

        return new JSONArray(allEntitiesTxt);
    }

    /**
     * Restore to default configuration values.
     */
    public void restoreToDefaults() {
        this.numberVariables = this.defaultsNumberVariables;
        this.objectVariables = this.defaultObjectVariables;
    }

    /**
     * Get a default configuration.
     *
     * @return Configuration saved from a Json File
     */
    public static Configuration getConfiguration() {
        return Configuration.CONFIGURATION;
    }

    public List<Integer> getLayerSizeMiddle() throws EvosimException {
        Object oList = getObjectValue("layerSizeMiddle");

        if (oList instanceof JSONArray) {
            oList = ((JSONArray) oList).toList();
        }

        return new ArrayList<>(((List) oList).stream().map(x -> (Integer) x).toList());
    }

    public void setLayerSizeMiddle(final List<Integer> newLayerSizeMiddle) throws EvosimException {
        this.objectVariables.replace("layerSizeMiddle", newLayerSizeMiddle);
    }

    public Image getBackgroundImage() throws EvosimException {
        try {
            if (this.objectVariables.get("backgroundImageBase64").equals("")) {
                return null;
            }
            byte[] decodedBytes = Base64
                .getDecoder()
                .decode((String) this.objectVariables.get("backgroundImageBase64"));
            FileUtils.writeByteArrayToFile(TMP_IMG_PATH, decodedBytes);
            return new Image(TMP_IMG_PATH.toURI().toString());
        } catch (Exception e) {
            throw new EvosimException("Couldn't load the image.", e);
        }
    }

    public void setBackgroundImage(final Image image) throws EvosimException {
        try {
            if (image == null) {
                this.objectVariables.replace("backgroundImageBase64", "");
                return;
            }

            File imgFile = new File(new URL(image.getUrl()).getFile());
            byte[] fileContent = FileUtils.readFileToByteArray(imgFile);

            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            this.objectVariables.replace("backgroundImageBase64", encodedString);
        } catch (Exception e) {
            throw new EvosimException("Couldn't load the image.", e);
        }
    }

    public Color getColorMap() throws EvosimException {
        return Color.web(getObjectValue("colorMap"));
    }

    public void setColorMap(final Color newColorMap) {
        this.objectVariables.replace("colorMap", newColorMap.toString());
    }

    public Duration getDuration() throws EvosimException {
        if (this.objectVariables.get("duration") instanceof String) {
            return Duration.parse((String) this.objectVariables.get("duration"));
        }
        return getObjectValue("duration");
    }

    public void setDuration(final Duration newDuration) {
        this.objectVariables.replace("duration", newDuration);
    }

    public double getEntityMaxRotationSpeed() throws EvosimException {
        return getDoubleValue("entityMaxRotationSpeed");
    }

    public void setEntityMaxRotationSpeed(final double newEntityMaxRotationSpeed) {
        this.numberVariables.replace("entityMaxRotationSpeed", newEntityMaxRotationSpeed);
    }

    public int getEntitySensorsCount() throws EvosimException {
        return getIntegerValue("entitySensorsCount");
    }

    public void setEntitySensorsCount(final int newEntitySensorsCount) {
        this.numberVariables.replace("entitySensorsCount", newEntitySensorsCount);
    }

    public double getEntityRadius() throws EvosimException {
        return getDoubleValue("entityRadius");
    }

    public void setEntityRadius(final double newEntityRadius) {
        this.numberVariables.replace("entityRadius", newEntityRadius);
    }

    public double getEntitySensorsLength() throws EvosimException {
        return getDoubleValue("entitySensorsLength");
    }

    public void setEntitySensorsLength(final double newEntitySensorsLength) {
        this.numberVariables.replace("entitySensorsLength", newEntitySensorsLength);
    }

    public double getEntityMaxSpeed() throws EvosimException {
        return getDoubleValue("entityMaxSpeed");
    }

    public void setEntityMaxSpeed(final double newEntityMaxSpeed) {
        this.numberVariables.replace("entityMaxSpeed", newEntityMaxSpeed);
    }

    public double getEntityMinSpeed() throws EvosimException {
        return getDoubleValue("entityMinSpeed");
    }

    public void setEntityMinSpeed(final double newEntityMinSpeed) {
        this.numberVariables.replace("entityMinxSpeed", newEntityMinSpeed);
    }

    public double getEntitySpeedMutationRate() throws EvosimException {
        return getDoubleValue("entitySpeedMutationRate");
    }

    public void setEntitySpeedMutationRate(final double newEntitySpeedMutationRate) {
        this.numberVariables.replace("entitySpeedMutationRate", newEntitySpeedMutationRate);
    }

    public double getEntityEnergyDrainRate() throws EvosimException {
        return getDoubleValue("entityEnergyDrainRate");
    }

    public void setEntityEnergyDrainRate(final double newEntityEnergyDrainRate) {
        this.numberVariables.replace("entityEnergyDrainRat", newEntityEnergyDrainRate);
    }

    public int getPredatorMaxNumber() throws EvosimException {
        return getIntegerValue("predatorMaxNumber");
    }

    public void setPredatorMaxNumber(final int newPredatorMaxNumber) {
        this.numberVariables.replace("predatorMaxNumber", newPredatorMaxNumber);
    }

    public double getPredatorViewAngle() throws EvosimException {
        return getDoubleValue("predatorViewAngle");
    }

    public void setPredatorViewAngle(final double newPredatorViewAngle) {
        this.numberVariables.replace("predatorViewAngle", newPredatorViewAngle);
    }

    public double getPredatorSplitEnergyFillingSpeed() throws EvosimException {
        return getDoubleValue("predatorSplitEnergyFillingSpeed");
    }

    public void setPredatorSplitEnergyFillingSpeed(
        final double newPredatorSplitEnergyFillingSpeed) {
        this.numberVariables.replace(
            "predatorSplitEnergyFillingSpeed", newPredatorSplitEnergyFillingSpeed);
    }

    public double getPredatorEnergyFillingSpeed() throws EvosimException {
        return getDoubleValue("predatorEnergyFillingSpeed");
    }

    public void setPredatorEnergyFillingSpeed(final double newPredatorEnergyFillingSpeed) {
        this.numberVariables.replace("predatorEnergyFillingSpeed", newPredatorEnergyFillingSpeed);
    }

    public double getPredatorEnergyBaseDrainingSpeed() throws EvosimException {
        return getDoubleValue("predatorEnergyBaseDrainingSpeed");
    }

    public void setPredatorEnergyBaseDrainingSpeed(
        final double newPredatorEnergyBaseDrainingSpeed) {
        this.numberVariables.replace(
            "predatorEnergyBaseDrainingSpeed", newPredatorEnergyBaseDrainingSpeed);
    }

    public int getPredatorInitialPopulation() throws EvosimException {
        return getIntegerValue("predatorInitialPopulation");
    }

    public void setPredatorInitialPopulation(final int newPredatorInitialPopulation) {
        this.numberVariables.replace("predatorInitialPopulation", newPredatorInitialPopulation);
    }

    public int getPreyInitialPopulation() throws EvosimException {
        return getIntegerValue("preyInitialPopulation");
    }

    public void setPreyInitialPopulation(final int newPreyInitialPopulation) {
        this.numberVariables.replace("preyInitialPopulation", newPreyInitialPopulation);
    }

    public int getPreyMaxNumber() throws EvosimException {
        return getIntegerValue("preyMaxNumber");
    }

    public void setPreyMaxNumber(final int newPreyMaxNumber) {
        this.numberVariables.replace("preyMaxNumber", newPreyMaxNumber);
    }

    public double getPreyViewAngle() throws EvosimException {
        return getDoubleValue("preyViewAngle");
    }

    public void setPreyViewAngle(final double newPreyViewAngle) {
        this.numberVariables.replace("preyViewAngle", newPreyViewAngle);
    }

    public double getPreySplitEnergyFillingSpeed() throws EvosimException {
        return getDoubleValue("preySplitEnergyFillingSpeed");
    }

    public void setPreySplitEnergyFillingSpeed(final double newPreySplitEnergyFillingSpeed) {
        this.numberVariables.replace("preySplitEnergyFillingSpeed", newPreySplitEnergyFillingSpeed);
    }

    public double getPreyEnergyFillingSpeed() throws EvosimException {
        return getDoubleValue("preyEnergyFillingSpeed");
    }

    public void setPreyEnergyFillingSpeed(final double newPreyEnergyFillingSpeed) {
        this.numberVariables.replace("preyEnergyFillingSpeed", newPreyEnergyFillingSpeed);
    }

    public int getNeuralNetworkLayersNumber() throws EvosimException {
        return getIntegerValue("neuralNetworkLayersNumber");
    }

    public void setNeuralNetworkLayersNumber(final int newNeuralNetworkLayersNumber) {
        this.numberVariables.replace("neuralNetworkLayersNumber", newNeuralNetworkLayersNumber);
    }

    public int getMapSizeX() throws EvosimException {
        return getIntegerValue("mapSizeX");
    }

    public int getMapSizeY() throws EvosimException {
        return getIntegerValue("mapSizeY");
    }

    public int getGridSize() throws EvosimException {
        return getIntegerValue("gridSize");
    }

    public void setMapSizeX(final int newMapSizeX) {
        this.numberVariables.replace("mapSizeX", newMapSizeX);
    }

    public void setMapSizeY(final int newMapSizeY) {
        this.numberVariables.replace("mapSizeY", newMapSizeY);
    }

    public void setGridSize(final int newGridSize) {
        this.numberVariables.replace("gridSize", newGridSize);
    }

    private int getIntegerValue(final String variable) throws EvosimException {
        try {
            return this.numberVariables.get(variable).intValue();
        } catch (Exception e) {
            throw new EvosimException("Couldn't load variable: " + variable + ".", e);
        }
    }

    private double getDoubleValue(final String variable) throws EvosimException {
        try {
            return this.numberVariables.get(variable).doubleValue();
        } catch (Exception e) {
            throw new EvosimException("Couldn't load variable: " + variable + ".", e);
        }
    }

    private <T> T getObjectValue(final String variable) throws EvosimException {
        try {
            return (T) this.objectVariables.get(variable);
        } catch (Exception e) {
            throw new EvosimException("Couldn't load variable: " + variable + ".", e);
        }
    }
}
