package org.totallyspies.evosim.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Configuration class consists of saving the settings the user configurations. If the user close
 * the application without saving the configurations, the applications will remember them and
 * will load them if needed.
 *
 * @author niakouu
 */
@Getter
@Setter
@ToString
public final class Configuration {

    /**
     * Defaults values for each variable.
     */
    public static class Defaults {

        /**
         * The maximum speed at which the entity can rotate.
         */
        public static final double ENTITY_MAX_ROTATION_SPEED = 0.02d;

        /**
         * The energy regained passively by a prey for surviving.
         */
        public static final double PREY_ENERGY_FILLING_SPEED = 0.0005d;

        /**
         * The energy regained by a predator from eating prey.
         */
        public static final double PREDATOR_ENERGY_FILLING_SPEED = 0.0005d;

        /**
         * The base energy lost rate of a predator.
         */
        public static final double PREDATOR_ENERGY_BASE_DRAINING_SPEED = 0.01d;

        /**
         * The number of sensors each entity has.
         */
        public static final int ENTITY_SENSORS_COUNT = 30;

        /**
         * The radius of an entity.
         */
        public static final double ENTITY_RADIUS = 15.0d;

        /**
         * The length of an entity's sensors.
         */
        public static final double ENTITY_SENSORS_LENGTH = 300.0d;

        /**
         * The maximum speed of an entity.
         */
        public static final double ENTITY_MAX_SPEED = 5.0d;

        /**
         * The minimum possible speed of an entity.
         */
        public static final double ENTITY_MIN_SPEED = 1.0d;

        /**
         * The mutation rate of the speed of the entity.
         */
        public static final double ENTITY_SPEED_MUTATION_RATE = 0.2d;

        /**
         * The speed at which the energy of the entity will be drained.
         */
        public static final double ENTITY_ENERGY_DRAIN_RATE = 0.01d;

        /**
         * The maximum number of predator that could be generated.
         */
        public static final int PREDATOR_MAX_NUMBER = 100;

        /**
         * The initial quantity of predator spawned.
         */
        public static final int PREDATOR_INITIAL_POPULATION = 10;
        
        /**
         * The view angle of a predator.
         */
        public static final double PREDATOR_VIEW_ANGLE = 60.0d;

        /**
         * The split energy that a predator will gain when it eats a prey.
         */
        public static final double PREDATOR_SPLIT_ENERGY_FILLING_SPEED = 0.5d;

        /**
         * The maximum number of prey that could be generated.
         */
        public static final int PREY_MAX_NUMBER = 100;
        
         /**
          * The initial quantity of prey spawned.
          */
         public static final int PREY_INITIAL_POPULATION = 50;

        /**
         * The view angle of a prey.
         */
        public static final double PREY_VIEW_ANGLE = 300.0d;

        /**
         * The split energy that a prey will gain when it is not moving.
         */
        public static final double PREY_SPLIT_ENERGY_FILLING_SPEED = 0.0005d;

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
     * The rate of energy that a prey will gain when it is not moving.
     */
    private double preyEnergyFillingSpeed;

    /**
     * The rate of energy that a predator will gain when it eats a prey.
     */
    private double predatorEnergyFillingSpeed;

    /**
     * The base energy lost rate of a predator.
     */
    private double predatorEnergyBaseDrainingSpeed;

    /**
     * The maximum speed at which the entity can rotate.
     */
    private double entityMaxRotationSpeed;

    /**
     * The number of sensors each entity has.
     */
    private int entitySensorsCount;

    /**
     * The radius of an entity.
     */
    private double entityRadius;

    /**
     * The length of an entity's sensors.
     */
    private double entitySensorsLength;

    /**
     * The maximum speed of an entity.
     */
    private double entityMaxSpeed;

    /**
     * The minimum speed of an entity.
     */
    private double entityMinSpeed;

    /**
     * The mutation rate of the speed of the entity.
     */
    private double entitySpeedMutationRate;

    /**
     * The speed at which the energy of the entity will be drained.
     */
    private double entityEnergyDrainRate;

    /**
     * The maximum number of predator that could be generated.
     */
    private double predatorMaxNumber;

    /**
     * The view angle of a predator.
     */
    private double predatorViewAngle;

    /**
     * The split energy that a predator will gain when it eats a prey.
     */
    private double predatorSplitEnergyFillingSpeed;

    /**
     * The maximum number of prey that could be generated.
     */
    private double preyMaxNumber;

    /**
     * The view angle of a prey.
     */
    private double preyViewAngle;

    /**
     * The split energy that a prey will gain when it is not moving.
     */
    private double preySplitEnergyFillingSpeed;

    /**
     * The number of layers the neural network will contain.
     */
    private int neuralNetworkLayersNumber;

    /**
     * The only configuration that exists using the Singleton Pattern.
     */
    private static final Configuration CONFIGURATION = new Configuration();

    /**
     * Create a new default Configuration object, and the setup.
     */
    private Configuration() {
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("preyEnergyFillingSpeed", this.preyEnergyFillingSpeed);
        jsonObject.put("predatorEnergyFillingSpeed",
                this.predatorEnergyFillingSpeed);
        jsonObject.put("predatorEnergyBaseDrainingSpeed",
                this.predatorEnergyBaseDrainingSpeed);
        jsonObject.put("entityMaxRotationSpeed", this.entityMaxRotationSpeed);
        jsonObject.put("entitySensorsCount", this.entitySensorsCount);
        jsonObject.put("entityRadius", this.entityRadius);
        jsonObject.put("entitySensorsLength", this.entitySensorsLength);
        jsonObject.put("entityMaxSpeed", this.entityMaxSpeed);
        jsonObject.put("entityMinSpeed", this.entityMinSpeed);
        jsonObject.put("entitySpeedMutationRate", this.entitySpeedMutationRate);
        jsonObject.put("entityEnergyDrainRate", this.entityEnergyDrainRate);
        jsonObject.put("predatorMaxNumber", this.predatorMaxNumber);
        jsonObject.put("predatorViewAngle", this.predatorViewAngle);
        jsonObject.put("predatorSplitEnergyFillingSpeed",
                this.predatorSplitEnergyFillingSpeed);
        jsonObject.put("preyMaxNumber", this.preyMaxNumber);
        jsonObject.put("preySplitEnergyFillingSpeed",
                this.preySplitEnergyFillingSpeed);
        jsonObject.put("preyViewAngle", this.preyViewAngle);
        jsonObject.put("neuralNetworkLayersNumber", this.neuralNetworkLayersNumber);
        return jsonObject;
    }

    /**
     * Change configuration based on the jsonObject.
     * @param jsonObject
     */
    private void changeConfiguration(final JSONObject jsonObject) {
        this.preyEnergyFillingSpeed = jsonObject.getDouble(
                "preyEnergyFillingSpeed");
        this.predatorEnergyFillingSpeed = jsonObject.getDouble(
                "predatorEnergyFillingSpeed");
        this.predatorEnergyBaseDrainingSpeed = jsonObject.getDouble(
                "predatorEnergyBaseDrainingSpeed");
        this.entityMaxRotationSpeed = jsonObject.getDouble(
                "entityMaxRotationSpeed");
        this.entitySensorsCount = jsonObject.getInt("entitySensorsCount");
        this.entityRadius = jsonObject.getDouble("entityRadius");
        this.entitySensorsLength = jsonObject.getDouble("entitySensorsLength");
        this.entityMaxSpeed = jsonObject.getDouble("entityMaxSpeed");
        this.entityMinSpeed = jsonObject.getDouble("entityMinSpeed");
        this.entitySpeedMutationRate = jsonObject.getDouble(
                "entitySpeedMutationRate");
        this.entityEnergyDrainRate = jsonObject.getDouble(
                "entityEnergyDrainRate");

        this.predatorMaxNumber = jsonObject.getInt("predatorMaxNumber");
        this.predatorViewAngle = jsonObject.getDouble("predatorViewAngle");
        this.predatorSplitEnergyFillingSpeed = jsonObject.getDouble(
                "predatorSplitEnergyFillingSpeed");

        this.preyMaxNumber = jsonObject.getInt("preyMaxNumber");
        this.preyViewAngle = jsonObject.getDouble("preyViewAngle");
        this.preySplitEnergyFillingSpeed = jsonObject.getDouble(
                "preySplitEnergyFillingSpeed");

        this.neuralNetworkLayersNumber = jsonObject.getInt(
                "neuralNetworkLayersNumber");
    }

    private void restoreToDefaults() {
        this.entityMaxRotationSpeed = Defaults.ENTITY_MAX_ROTATION_SPEED;
        this.preyEnergyFillingSpeed = Defaults.PREY_ENERGY_FILLING_SPEED;
        this.predatorEnergyFillingSpeed = Defaults.PREDATOR_ENERGY_FILLING_SPEED;
        this.predatorEnergyBaseDrainingSpeed = Defaults.
                PREDATOR_ENERGY_BASE_DRAINING_SPEED;
        this.entitySensorsCount = Defaults.ENTITY_SENSORS_COUNT;
        this.entityRadius = Defaults.ENTITY_RADIUS;
        this.entitySensorsLength = Defaults.ENTITY_SENSORS_LENGTH;
        this.entityMaxSpeed = Defaults.ENTITY_MAX_SPEED;
        this.entityMinSpeed = Defaults.ENTITY_MIN_SPEED;
        this.entitySpeedMutationRate = Defaults.ENTITY_SPEED_MUTATION_RATE;
        this.entityEnergyDrainRate = Defaults.ENTITY_ENERGY_DRAIN_RATE;

        // Predator
        this.predatorMaxNumber = Defaults.PREDATOR_MAX_NUMBER;
        this.predatorViewAngle = Defaults.PREDATOR_VIEW_ANGLE;
        this.predatorSplitEnergyFillingSpeed = Defaults.
                PREDATOR_SPLIT_ENERGY_FILLING_SPEED;

        this.preyMaxNumber = Defaults.PREY_MAX_NUMBER;
        this.preyViewAngle = Defaults.PREY_VIEW_ANGLE;
        this.preySplitEnergyFillingSpeed = Defaults.PREY_SPLIT_ENERGY_FILLING_SPEED;

        this.neuralNetworkLayersNumber = Defaults.NEURAL_NETWORK_LAYERS_NUMBER;
    }
}
