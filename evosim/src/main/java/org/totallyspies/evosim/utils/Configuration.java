package org.totallyspies.evosim.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Duration;
import lombok.ToString;
import org.json.JSONArray;
import org.json.JSONObject;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.simulation.Simulation;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
        public static final double PREY_ENERGY_FILLING_SPEED = 0.0005;

        /**
         * The energy regained by a predator from eating prey.
         */
        public static final double PREDATOR_ENERGY_FILLING_SPEED = 0.0005;

        /**
         * The base energy lost rate of a predator.
         */
        public static final double PREDATOR_ENERGY_BASE_DRAINING_SPEED = 0.01;

        /**
         * The number of sensors each entity has.
         */
        public static final int ENTITY_SENSORS_COUNT = 30;

        /**
         * The radius of an entity.
         */
        public static final double ENTITY_RADIUS = 15.0;

        /**
         * The length of an entity's sensors.
         */
        public static final double ENTITY_SENSORS_LENGTH = 300.0;

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
        public static final double ENTITY_ENERGY_DRAIN_RATE = 0.01;

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
        public static final int NEURAL_NETWORK_LAYERS_NUMBER = 2;

        /**
         * The default name of a configuration file.
         */
        public static final File LATEST_CONFIGURATION =
                new File(System.getProperty("java.io.tmpdir"), "latestConfigurations.json");

        public static final Duration DEFAULT_DURATION = Duration.ZERO;
    }

    /**
     * All the variables needed for the configuration.
     */
    private HashMap<String, Number> numberVariables;

    /**
     * All the defaults values needed for the application.
     */
    private HashMap<String, Number> defaultsNumberVariables;

    private HashMap<String, Object> objectVariables;

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

        this.defaultsNumberVariables.put("entityMaxRotationSpeed", Defaults.ENTITY_MAX_ROTATION_SPEED);
        this.defaultsNumberVariables.put("entitySensorsCount", Defaults.ENTITY_SENSORS_COUNT);
        this.defaultsNumberVariables.put("entityRadius", Defaults.ENTITY_RADIUS);
        this.defaultsNumberVariables.put("entitySensorsLength", Defaults.ENTITY_SENSORS_LENGTH);
        this.defaultsNumberVariables.put("entityMaxSpeed", Defaults.ENTITY_MAX_SPEED);
        this.defaultsNumberVariables.put("entityMinSpeed", Defaults.ENTITY_MIN_SPEED);
        this.defaultsNumberVariables.put("entitySpeedMutationRate", Defaults.ENTITY_SPEED_MUTATION_RATE);
        this.defaultsNumberVariables.put("entityEnergyDrainRate", Defaults.ENTITY_ENERGY_DRAIN_RATE);

        this.defaultsNumberVariables.put("predatorMaxNumber", Defaults.PREDATOR_MAX_NUMBER);
        this.defaultsNumberVariables.put("predatorViewAngle", Defaults.PREDATOR_VIEW_ANGLE);
        this.defaultsNumberVariables.put("predatorSplitEnergyFillingSpeed",
                Defaults.PREDATOR_SPLIT_ENERGY_FILLING_SPEED);
        this.defaultsNumberVariables.put("predatorEnergyBaseDrainingSpeed",
                Defaults.PREDATOR_ENERGY_BASE_DRAINING_SPEED);
        this.defaultsNumberVariables.put("predatorEnergyFillingSpeed",
                Defaults.PREDATOR_ENERGY_FILLING_SPEED);
        this.defaultsNumberVariables.put("predatorInitialPopulation", Defaults.PREDATOR_INITIAL_POPULATION);

        this.defaultsNumberVariables.put("preyMaxNumber", Defaults.PREY_MAX_NUMBER);
        this.defaultsNumberVariables.put("preyViewAngle", Defaults.PREY_VIEW_ANGLE);
        this.defaultsNumberVariables.put("preySplitEnergyFillingSpeed",
                Defaults.PREY_SPLIT_ENERGY_FILLING_SPEED);
        this.defaultsNumberVariables.put("preyEnergyFillingSpeed", Defaults.PREY_ENERGY_FILLING_SPEED);
        this.defaultsNumberVariables.put("preyInitialPopulation", Defaults.PREY_INITIAL_POPULATION);

        this.defaultsNumberVariables.put("neuralNetworkLayersNumber", Defaults.NEURAL_NETWORK_LAYERS_NUMBER);

        this.defaultObjectVariables.put("duration", Defaults.DEFAULT_DURATION);

        this.mapper = new ObjectMapper();
        restoreToDefaults();
    }

    /**
     * Saves the default files that the user didn't have time to save.
     * @param simulation The simulati
     */
    public void saveLatestConfiguration(final Simulation simulation) throws ConfigurationException {
        saveConfiguration(Defaults.LATEST_CONFIGURATION, simulation);
    }

    /**
     * Saves a Configuration file in the temporary files of the user's computer.
     *
     * @param jsonFile location of the new file place.
     * @param simulation simulation used.
     */
    public void saveConfiguration(
            final File jsonFile, final Simulation simulation) throws ConfigurationException {
        try {
            JSONObject jsonText = getJSONObject(simulation);

            if (jsonFile.exists()) {
                jsonFile.createNewFile();
            }

            try (FileWriter writer = new FileWriter(jsonFile)) {
                jsonText.write(writer);
            }
        } catch (Exception e) {
            throw new ConfigurationException("Could not save the JSON Configuration.");
        }
    }

    /**
     * Render the last configuration the user used before closing the
     * application.
     * @return entity list saved.
     */
    public List<Entity> loadLastFile() throws ConfigurationException {
        return loadFile(Defaults.LATEST_CONFIGURATION);
    }

    /**
     * Get a saved configuration in the temp file.
     *
     * @param jsonFile file we want to load.
     * @return entity list
     */
    public List<Entity> loadFile(final File jsonFile) throws ConfigurationException {
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
     * @param jsonConfiguration
     */
    private void loadConfiguration(final JSONObject jsonConfiguration) {
        Set<String> keys = this.numberVariables.keySet();
        keys.forEach((key) -> this.numberVariables.replace(key, jsonConfiguration.getNumber(key)));
    }

    private List<Entity> loadEntities(final JSONArray jsonEntities) throws ConfigurationException {
        List<Entity> entities;
        try {
            entities = mapper
                    .readValue(jsonEntities.toString(), new TypeReference<>() { });

            return entities;
        } catch (Exception e) {
            throw new ConfigurationException("Couldn't load the entities of the JSON File.");
        }
    }

    /**
     * Load a saved Configuration JSON file and turn it into an JSONObject.
     *
     * @param jsonFile The file name of the json file we want to load.
     * @return JSONObject from a source JSON Configuration file.
     */
    private static JSONObject loadSavedFile(final File jsonFile) throws ConfigurationException {
        String jsonText = " ";
        try {
            jsonText = Files.readString(Path.of(jsonFile.getPath()));
            return new JSONObject(jsonText);
        } catch (Exception e) {
            throw new ConfigurationException("Couldn't load the saved Configuration JSON file.");
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

        for (int x = 0; x < Simulation.MAP_SIZE_X; x++) {
            for (int y = 0; y < Simulation.MAP_SIZE_Y; y++) {
                simulation.forEachGridEntities(x, y, allEntities::add);
            }
        }
        String allEntitiesTxt = mapper
                .writerFor(new TypeReference<List<Entity>>() { }).writeValueAsString(allEntities);

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

    public double getEntityMaxRotationSpeed() {
        return this.numberVariables.get("entityMaxRotationSpeed").doubleValue();
    }

    public void setEntityMaxRotationSpeed(final double newEntityMaxRotationSpeed) {
        this.numberVariables.replace("entityMaxRotationSpeed", newEntityMaxRotationSpeed);
    }

    public int getEntitySensorsCount() {
        return this.numberVariables.get("entitySensorsCount").intValue();
    }

    public void setEntitySensorsCount(final int newEntitySensorsCount) {
        this.numberVariables.replace("entitySensorsCount", newEntitySensorsCount);
    }

    public double getEntityRadius() {
        return this.numberVariables.get("entityRadius").doubleValue();
    }

    public void setEntityRadius(final double newEntityRadius) {
        this.numberVariables.replace("entityEntityRadius", newEntityRadius);
    }

    public double getEntitySensorsLength() {
        return this.numberVariables.get("entitySensorsLength").doubleValue();
    }

    public void setEntitySensorsLength(final double newEntitySensorsLength) {
        this.numberVariables.replace("entitySensorsLength", newEntitySensorsLength);
    }

    public double getEntityMaxSpeed() {
        return this.numberVariables.get("entityMaxSpeed").doubleValue();
    }

    public void setEntityMaxSpeed(final double newEntityMaxSpeed) {
        this.numberVariables.replace("entityMaxSpeed", newEntityMaxSpeed);
    }

    public double getEntityMinSpeed() {
        return this.numberVariables.get("entityMinSpeed").doubleValue();
    }

    public void setEntityMinSpeed(final double newEntityMinSpeed) {
        this.numberVariables.replace("entityMinxSpeed", newEntityMinSpeed);
    }

    public double getEntitySpeedMutationRate() {
        return this.numberVariables.get("entitySpeedMutationRate").doubleValue();
    }

    public void setEntitySpeedMutationRate(final double newEntitySpeedMutationRate) {
        this.numberVariables.replace("entitySpeedMutationRate", newEntitySpeedMutationRate);
    }

    public double getEntityEnergyDrainRate() {
        return this.numberVariables.get("entityEnergyDrainRate").doubleValue();
    }

    public void setEntityEnergyDrainRate(final double newEntityEnergyDrainRate) {
        this.numberVariables.replace("entityEnergyDrainRat", newEntityEnergyDrainRate);
    }

    public int getPredatorMaxNumber() {
        return this.numberVariables.get("predatorMaxNumber").intValue();
    }

    public void setPredatorMaxNumber(final int newPredatorMaxNumber) {
        this.numberVariables.replace("predatorMaxNumber", newPredatorMaxNumber);
    }

    public double getPredatorViewAngle() {
        return this.numberVariables.get("predatorViewAngle").doubleValue();
    }

    public void setPredatorViewAngle(final double newPredatorViewAngle) {
        this.numberVariables.replace("predatorViewAngle", newPredatorViewAngle);
    }

    public double getPredatorSplitEnergyFillingSpeed() {
        return this.numberVariables.get("predatorSplitEnergyFillingSpeed").doubleValue();
    }

    public void setPredatorSplitEnergyFillingSpeed(
            final double newPredatorSplitEnergyFillingSpeed) {
        this.numberVariables.replace(
                "predatorSplitEnergyFillingSpeed", newPredatorSplitEnergyFillingSpeed);
    }

    public double getPredatorEnergyFillingSpeed() {
        return this.numberVariables.get("predatorEnergyFillingSpeed").doubleValue();
    }

    public void setPredatorEnergyFillingSpeed(final double newPredatorEnergyFillingSpeed) {
        this.numberVariables.replace("predatorEnergyFillingSpeed", newPredatorEnergyFillingSpeed);
    }

    public double getPredatorEnergyBaseDrainingSpeed() {
        return this.numberVariables.get("predatorEnergyBaseDrainingSpeed").doubleValue();
    }

    public void setPredatorEnergyBaseDrainingSpeed(
            final double newPredatorEnergyBaseDrainingSpeed) {
        this.numberVariables.replace(
                "predatorEnergyBaseDrainingSpeed", newPredatorEnergyBaseDrainingSpeed);
    }

    public int getPredatorInitialPopulation() {
        return this.numberVariables.get("predatorInitialPopulation").intValue();
    }

    public void setPredatorInitialPopulation(final int newPredatorInitialPopulation) {
        this.numberVariables.replace("predatorInitialPopulation", newPredatorInitialPopulation);
    }

    public int getPreyInitialPopulation() {
        return this.numberVariables.get("preyInitialPopulation").intValue();
    }

    public void setPreyInitialPopulation(final int newPreyInitialPopulation) {
        this.numberVariables.replace("preyInitialPopulation", newPreyInitialPopulation);
    }

    public int getPreyMaxNumber() {
        return this.numberVariables.get("preyMaxNumber").intValue();
    }

    public void setPreyMaxNumber(final int newPreyMaxNumber) {
        this.numberVariables.replace("preyMaxNumber", newPreyMaxNumber);
    }

    public double getPreyViewAngle() {
        return this.numberVariables.get("preyViewAngle").doubleValue();
    }

    public void setPreyViewAngle(final double newPreyViewAngle) {
        this.numberVariables.replace("preyViewAngle", newPreyViewAngle);
    }

    public double getPreySplitEnergyFillingSpeed() {
        return this.numberVariables.get("preySplitEnergyFillingSpeed").doubleValue();
    }

    public void setPreySplitEnergyFillingSpeed(final double newPreySplitEnergyFillingSpeed) {
        this.numberVariables.replace("preySplitEnergyFillingSpeed", newPreySplitEnergyFillingSpeed);
    }

    public double getPreyEnergyFillingSpeed() {
        return this.numberVariables.get("preyEnergyFillingSpeed").doubleValue();
    }

    public void setPreyEnergyFillingSpeed(final double newPreyEnergyFillingSpeed) {
        this.numberVariables.replace("preyEnergyFillingSpeed", newPreyEnergyFillingSpeed);
    }

    public int getNeuralNetworkLayersNumber() {
        return this.numberVariables.get("neuralNetworkLayersNumber").intValue();
    }

    public void setNeuralNetworkLayersNumber(final double newNeuralNetworkLayersNumber) {
        this.numberVariables.replace("neuralNetworkLayersNumber", newNeuralNetworkLayersNumber);
    }
}
