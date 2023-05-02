package org.totallyspies.evosim.utils;

import lombok.ToString;
import org.json.JSONObject;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    }

    /**
     * All the variables needed for the configuration.
     */
    private HashMap<String, Number> variables;

    /**
     * All the defaults values needed for the application.
     */
    private HashMap<String, Number> defaultsValues;

    /**
     * The only configuration that exists using the Singleton Pattern.
     */
    private static final Configuration CONFIGURATION = new Configuration();

    /**
     * Create a new default Configuration object, and the setup.
     */
    private Configuration() {
        this.defaultsValues = new HashMap<>();
        this.defaultsValues.put("entityMaxRotationSpeed", Defaults.ENTITY_MAX_ROTATION_SPEED);
        this.defaultsValues.put("entitySensorsCount", Defaults.ENTITY_SENSORS_COUNT);
        this.defaultsValues.put("entityRadius", Defaults.ENTITY_RADIUS);
        this.defaultsValues.put("entitySensorsLength", Defaults.ENTITY_SENSORS_LENGTH);
        this.defaultsValues.put("entityMaxSpeed", Defaults.ENTITY_MAX_SPEED);
        this.defaultsValues.put("entityMinSpeed", Defaults.ENTITY_MIN_SPEED);
        this.defaultsValues.put("entitySpeedMutationRate", Defaults.ENTITY_SPEED_MUTATION_RATE);
        this.defaultsValues.put("entityEnergyDrainRate", Defaults.ENTITY_ENERGY_DRAIN_RATE);

        this.defaultsValues.put("predatorMaxNumber", Defaults.PREDATOR_MAX_NUMBER);
        this.defaultsValues.put("predatorViewAngle", Defaults.PREDATOR_VIEW_ANGLE);
        this.defaultsValues.put("predatorSplitEnergyFillingSpeed",
                Defaults.PREDATOR_SPLIT_ENERGY_FILLING_SPEED);
        this.defaultsValues.put("predatorEnergyBaseDrainingSpeed",
                Defaults.PREDATOR_ENERGY_BASE_DRAINING_SPEED);
        this.defaultsValues.put("predatorEnergyFillingSpeed",
                Defaults.PREDATOR_ENERGY_FILLING_SPEED);
        this.defaultsValues.put("predatorInitialPopulation", Defaults.PREDATOR_INITIAL_POPULATION);

        this.defaultsValues.put("preyMaxNumber", Defaults.PREY_MAX_NUMBER);
        this.defaultsValues.put("preyViewAngle", Defaults.PREY_VIEW_ANGLE);
        this.defaultsValues.put("preySplitEnergyFillingSpeed",
                Defaults.PREY_SPLIT_ENERGY_FILLING_SPEED);
        this.defaultsValues.put("preyEnergyFillingSpeed", Defaults.PREY_ENERGY_FILLING_SPEED);
        this.defaultsValues.put("preyInitialPopulation", Defaults.PREY_INITIAL_POPULATION);

        this.defaultsValues.put("neuralNetworkLayersNumber", Defaults.NEURAL_NETWORK_LAYERS_NUMBER);

        this.variables = this.defaultsValues;

        restoreToDefaults();
    }

    /**
     * Saves the default files that the user didn't have time to save.
     */
    public void saveLatestConfiguration() throws IOException {
        saveConfiguration(Defaults.LATEST_CONFIGURATION);
    }

    /**
     * Saves a Configuration file in the temporary files of the user's computer.
     *
     * @param jsonFile location of the new file place.
     */
    public void saveConfiguration(final File jsonFile) throws IOException {
        JSONObject jsonText = getJSONObject();

        if (jsonFile.exists()) {
            jsonFile.createNewFile();
        }

        try (FileWriter writer = new FileWriter(jsonFile)) {
            jsonText.write(writer);
        }
    }

    /**
     * Render the default configuration.
     *
     */
    public void loadDefaultConfiguration() {
        restoreToDefaults();
    }

    /**
     * Render the last configuration the user used before closing the
     * application.
     */
    public void loadLastConfiguration() {
        try {
            loadConfiguration(Defaults.LATEST_CONFIGURATION);
        } catch (Exception e) {
            System.out.println("Last Configuration doesn't exists yet.");
        }
    }

    /**
     * Get a saved configuration in the temp file.
     *
     * @param jsonFile file we want to load.
     */
    public void loadConfiguration(final File jsonFile) {
        JSONObject jsonObject = loadSavedConfiguration(jsonFile);

        if (jsonObject != null) {
            changeConfiguration(jsonObject);
        }
    }

    /**
     * Get a default configuration.
     *
     * @return Configuration saved from a Json File
     */
    public static Configuration getConfiguration() {
        return Configuration.CONFIGURATION;
    }

    /**
     * Load a saved Configuration JSON file and turn it into an JSONObject.
     *
     * @param jsonFile The file name of the json file we want to load.
     * @return JSONObject from a source JSON Configuration file.
     */
    private static JSONObject loadSavedConfiguration(final File jsonFile) {
        String jsonText = "";
        try {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(jsonFile))) {
                jsonText = reader.readLine();
            }

        } catch (Exception e) {
            e.getMessage();
            return null;
        }
        return new JSONObject(jsonText);
    }

    /**
     * Makes a JSONObject, and put all the Configuration variables into it.
     *
     * @return JSONObject with Configuration's variables.
     */
    private JSONObject getJSONObject() {
        //JSONObject jsonObjectGlobal = new JSONObject();
        //jsonObjectGlobal.put("configuration", getConfigurationJson());

        return getConfigurationJson();
    }

    private JSONObject getConfigurationJson() {
        return new JSONObject(this.variables);
    }

    private JSONObject getNeuralNetworkJSON(final List<Entity> entityList) {
        JSONObject jsonObjectNeuralNetworks = new JSONObject();

        for (int i = 0; i < entityList.size(); i++) {
            NeuralNetwork neuralNetwork = entityList.get(i).getBrain();

        }

        return null;
    }

    /**
     * Change configuration based on the jsonObject.
     * @param jsonObject
     */
    private void changeConfiguration(final JSONObject jsonObject) {
        Set<String> keys = this.variables.keySet();

        keys.forEach((key) -> this.variables.replace(key, jsonObject.getNumber(key)));
    }

    private void restoreToDefaults() {
        this.variables = this.defaultsValues;
    }

    public double getEntityMaxRotationSpeed() {
        return (double) this.variables.get("entityMaxRotationSpeed");
    }

    public void setEntityMaxRotationSpeed(final double newEntityMaxRotationSpeed) {
        this.variables.replace("entityMaxRotationSpeed", newEntityMaxRotationSpeed);
    }

    public int getEntitySensorsCount() {
        return (int) this.variables.get("entitySensorsCount");
    }

    public void setEntitySensorsCount(final int newEntitySensorsCount) {
        this.variables.replace("entitySensorsCount", newEntitySensorsCount);
    }

    public double getEntityRadius() {
        return (double) this.variables.get("entityRadius");
    }

    public void setEntityRadius(final double newEntityRadius) {
        this.variables.replace("entityEntityRadius", newEntityRadius);
    }

    public double getEntitySensorsLength() {
        return (double) this.variables.get("entitySensorsLength");
    }

    public void setEntitySensorsLength(final double newEntitySensorsLength) {
        this.variables.replace("entitySensorsLength", newEntitySensorsLength);
    }

    public double getEntityMaxSpeed() {
        return (double) this.variables.get("entityMaxSpeed");
    }

    public void setEntityMaxSpeed(final double newEntityMaxSpeed) {
        this.variables.replace("entityMaxSpeed", newEntityMaxSpeed);
    }

    public double getEntityMinSpeed() {
        return (double) this.variables.get("entityMinSpeed");
    }

    public void setEntityMinSpeed(final double newEntityMinSpeed) {
        this.variables.replace("entityMinxSpeed", newEntityMinSpeed);
    }

    public double getEntitySpeedMutationRate() {
        return (double) this.variables.get("entitySpeedMutationRate");
    }

    public void setEntitySpeedMutationRate(final double newEntitySpeedMutationRate) {
        this.variables.replace("entitySpeedMutationRate", newEntitySpeedMutationRate);
    }

    public double getEntityEnergyDrainRate() {
        return (double) this.variables.get("entityEnergyDrainRate");
    }

    public void setEntityEnergyDrainRate(final double newEntityEnergyDrainRate) {
        this.variables.replace("entityEnergyDrainRat", newEntityEnergyDrainRate);
    }

    public int getPredatorMaxNumber() {
        return (int) this.variables.get("predatorMaxNumber");
    }

    public void setPredatorMaxNumber(final int newPredatorMaxNumber) {
        this.variables.replace("predatorMaxNumber", newPredatorMaxNumber);
    }

    public double getPredatorViewAngle() {
        return (double) this.variables.get("predatorViewAngle");
    }

    public void setPredatorViewAngle(final double newPredatorViewAngle) {
        this.variables.replace("predatorViewAngle", newPredatorViewAngle);
    }

    public double getPredatorSplitEnergyFillingSpeed() {
        return (double) this.variables.get("predatorSplitEnergyFillingSpeed");
    }

    public void setPredatorSplitEnergyFillingSpeed(
            final double newPredatorSplitEnergyFillingSpeed) {
        this.variables.replace(
                "predatorSplitEnergyFillingSpeed", newPredatorSplitEnergyFillingSpeed);
    }

    public double getPredatorEnergyFillingSpeed() {
        return (double) this.variables.get("predatorEnergyFillingSpeed");
    }

    public void setPredatorEnergyFillingSpeed(final double newPredatorEnergyFillingSpeed) {
        this.variables.replace("predatorEnergyFillingSpeed", newPredatorEnergyFillingSpeed);
    }

    public double getPredatorEnergyBaseDrainingSpeed() {
        return (double) this.variables.get("predatorEnergyBaseDrainingSpeed");
    }

    public void setPredatorEnergyBaseDrainingSpeed(
            final double newPredatorEnergyBaseDrainingSpeed) {
        this.variables.replace(
                "predatorEnergyBaseDrainingSpeed", newPredatorEnergyBaseDrainingSpeed);
    }

    public int getPredatorInitialPopulation() {
        return (int) this.variables.get("predatorInitialPopulation");
    }

    public void setPredatorInitialPopulation(final int newPredatorInitialPopulation) {
        this.variables.replace("predatorInitialPopulation", newPredatorInitialPopulation);
    }

    public int getPreyInitialPopulation() {
        return (int) this.variables.get("preyInitialPopulation");
    }

    public void setPreyInitialPopulation(final int newPreyInitialPopulation) {
        this.variables.replace("preyInitialPopulation", newPreyInitialPopulation);
    }

    public int getPreyMaxNumber() {
        return (int) this.variables.get("preyMaxNumber");
    }

    public void setPreyMaxNumber(final int newPreyMaxNumber) {
        this.variables.replace("preyMaxNumber", newPreyMaxNumber);
    }

    public double getPreyViewAngle() {
        return (double) this.variables.get("preyViewAngle");
    }

    public void setPreyViewAngle(final double newPreyViewAngle) {
        this.variables.replace("preyViewAngle", newPreyViewAngle);
    }

    public double getPreySplitEnergyFillingSpeed() {
        return (double) this.variables.get("preySplitEnergyFillingSpeed");
    }

    public void setPreySplitEnergyFillingSpeed(final double newPreySplitEnergyFillingSpeed) {
        this.variables.replace("preySplitEnergyFillingSpeed", newPreySplitEnergyFillingSpeed);
    }

    public double getPreyEnergyFillingSpeed() {
        return (double) this.variables.get("preyEnergyFillingSpeed");
    }

    public void setPreyEnergyFillingSpeed(final double newPreyEnergyFillingSpeed) {
        this.variables.replace("preyEnergyFillingSpeed", newPreyEnergyFillingSpeed);
    }

    public int getNeuralNetworkLayersNumber() {
        return (int) this.variables.get("neuralNetworkLayersNumber");
    }

    public void setNeuralNetworkLayersNumber(final double newNeuralNetworkLayersNumber) {
        this.variables.replace("neuralNetworkLayersNumber", newNeuralNetworkLayersNumber);
    }
}
