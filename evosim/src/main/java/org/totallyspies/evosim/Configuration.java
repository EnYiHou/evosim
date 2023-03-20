package org.totallyspies.evosim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import org.json.JSONObject;

/**
 * Configuration class consists of saving the settings the user configurations.
 * If the user close the application without saving the configurations, the
 * applications will remember them and will load them if needed.
 *
 * @author edeli
 */
public final class Configuration {

  /**
   * Defaults values for each variable.
   */
  public static class Defaults {

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
    public static final double ENTITY_SENSORS_LENGTH = 1000.0d;

    /**
     * The maximum speed of an entity.
     */
    public static final double ENTITY_MAX_SPEED = 3.0d;

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
     * The view angle of a prey.
     */
    public static final double PREY_VIEW_ANGLE = 300.0d;

    /**
     * The split energy that a prey will gain when it is not moving.
     */
    public static final double PREY_SPLIT_ENERGY_FILLING_SPEED = 0.5d;

    /**
     * The number of layers the neural network will contain.
     */
    public static final int NEURAL_NETWORK_LAYERS_NUMBER = 2;

    /**
     * The default name of a configuration file.
     */
    private static final String DEFAULT_CONFIGURATION_FILE_NAME = "defaultConfigurations.json";

  }

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
   * Create a new default Configuration object, and the setup.
   */
  public Configuration() {
    this.entitySensorsCount = Defaults.ENTITY_SENSORS_COUNT;
    this.entityRadius = Defaults.ENTITY_RADIUS;
    this.entitySensorsLength = Defaults.ENTITY_SENSORS_LENGTH;
    this.entityMaxSpeed = Defaults.ENTITY_MAX_SPEED;
    this.entitySpeedMutationRate = Defaults.ENTITY_SPEED_MUTATION_RATE;
    this.entityEnergyDrainRate = Defaults.ENTITY_ENERGY_DRAIN_RATE;

    this.predatorMaxNumber = Defaults.PREDATOR_MAX_NUMBER;
    this.predatorViewAngle = Defaults.PREDATOR_VIEW_ANGLE;
    this.predatorSplitEnergyFillingSpeed = Defaults.
        PREDATOR_SPLIT_ENERGY_FILLING_SPEED;

    this.preyMaxNumber = Defaults.PREY_MAX_NUMBER;
    this.preyViewAngle = Defaults.PREY_VIEW_ANGLE;
    this.preySplitEnergyFillingSpeed = Defaults.PREY_SPLIT_ENERGY_FILLING_SPEED;

    this.neuralNetworkLayersNumber = Defaults.NEURAL_NETWORK_LAYERS_NUMBER;
  }

  /**
   * Create a new Configuration object form a JSONObject.
   *
   * @param jsonObject JSONObject loaded from a json file.
   */
  private Configuration(final JSONObject jsonObject) {
    this.entitySensorsCount = jsonObject.getInt("entitySensorsCount");
    this.entityRadius = jsonObject.getDouble("entityRadius");
    this.entitySensorsLength = jsonObject.getDouble("entitySensorsLength");
    this.entityMaxSpeed = jsonObject.getDouble("entityMaxSpeed");
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

  @Override
  public String toString() {
    return "Configuration{" +
        "entitySensorsCount=" + entitySensorsCount +
        ", entityRadius=" + entityRadius +
        ", entitySensorsLength=" + entitySensorsLength +
        ", entityMaxSpeed=" + entityMaxSpeed +
        ", entitySpeedMutationRate=" + entitySpeedMutationRate +
        ", entityEnergyDrainRate=" + entityEnergyDrainRate +
        ", predatorMaxNumber=" + predatorMaxNumber +
        ", predatorViewAngle=" + predatorViewAngle +
        ", predatorSplitEnergyFillingSpeed=" + predatorSplitEnergyFillingSpeed +
        ", preyMaxNumber=" + preyMaxNumber +
        ", preyViewAngle=" + preyViewAngle +
        ", preySplitEnergyFillingSpeed=" + preySplitEnergyFillingSpeed +
        ", neuralNetworkLayersNumber=" + neuralNetworkLayersNumber +
        '}';
  }

  /**
   * Saves the default files that the user didn't have time to save.
   *
   * @return "Saved!" if the last configuration has saved.
   */
  public String saveLastConfiguration() {
    try {
      saveFileConfiguration(Defaults.DEFAULT_CONFIGURATION_FILE_NAME);
    } catch (Exception e) {
      e.getMessage();
    }
    return "Saved!";
  }

  /**
   * Saves a Configuration file in the temporary files of the user's computer.
   *
   * @param fileName a new file name for the save file
   * @return "Saved!" if the file was saved without any issues
   */
  public String saveFileConfiguration(final String fileName) {
    try {
      JSONObject jsonText = getJSONObject();

      File jsonFile = new File(System.getProperty("java.io.tmpdir"),
          fileName);
      if (jsonFile.exists()) {
        jsonFile.createNewFile();
      }

      try (FileWriter writer = new FileWriter(jsonFile)) {
        jsonText.write(writer);
      }

    } catch (Exception e) {
      return e.getMessage();
    }
    return "Saved!";
  }

  /**
   * Get a saved configuration in the temp file.
   *
   * @param fileName The name of the file we want to load.
   * @return Configuration saved from a Json File.
   */
  public static Configuration getConfiguration(final String fileName) {
    JSONObject jsonObject = loadSavedConfiguration(fileName);

    if (jsonObject != null) {
      return new Configuration(jsonObject);
    }

    return null;
  }

  /**
   * Render the last configuration the user used before closing the
   * application.
   *
   * @return Configuration object with the last Configuration the user used.
   */
  public static Configuration getLast() {
    try{
      return getConfiguration(Defaults.DEFAULT_CONFIGURATION_FILE_NAME);
    } catch (Exception e) {
      System.out.println("Last Configuration doesn't exists yet.");
      return null;
    }
  }

  /**
   * Load a saved Configuration JSON file and turn it into an JSONObject.
   *
   * @param fileName The file name of the json file we want to load.
   * @return JSONObject from a source JSON Configuration file.
   */
  private static JSONObject loadSavedConfiguration(final String fileName) {
    String jsonText = "";
    try {
      String tmpdir = System.getProperty("java.io.tmpdir");
      File jsonFile = new File(tmpdir, fileName);

      try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
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
    jsonObject.put("entitySensorsCount", this.entitySensorsCount);
    jsonObject.put("entityRadius", this.entityRadius);
    jsonObject.put("entitySensorsLength", this.entitySensorsLength);
    jsonObject.put("entityMaxSpeed", this.entityMaxSpeed);
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

  // Setters
  public void setEntitySensorsCount(
      final int newEntitySensorsCount) {
    this.entitySensorsCount = newEntitySensorsCount;
  }

  public void setEntityRadius(
      final double newEntityRadius) {
    this.entityRadius = newEntityRadius;
  }

  public void setEntitySensorsLength(
      final double newEntitySensorsLength) {
    this.entitySensorsLength = newEntitySensorsLength;
  }

  public void setEntityMaxSpeed(
      final double newEntityMaxSpeed) {
    this.entityMaxSpeed = newEntityMaxSpeed;
  }

  public void setEntitySpeedMutationRate(
      final double newEntitySpeedMutationRate) {
    this.entitySpeedMutationRate = newEntitySpeedMutationRate;
  }

  public void setEntityEnergyDrainRate(
      final double newEntityEnergyDrainRate) {
    this.entityEnergyDrainRate = newEntityEnergyDrainRate;
  }

  public void setPredatorMaxNumber(
      final double newPredatorMaxNumber) {
    this.predatorMaxNumber = newPredatorMaxNumber;
  }

  public void setPredatorViewAngle(
      final double newPredatorViewAngle) {
    this.predatorViewAngle = newPredatorViewAngle;
  }

  public void setPredatorSplitEnergyFillingSpeed(
      final double newPredatorSplitEnergyFillingSpeed) {
    this.predatorSplitEnergyFillingSpeed = newPredatorSplitEnergyFillingSpeed;
  }

  public void setPreyMaxNumber(
      final double newPreyMaxNumber
  ) {
    this.preyMaxNumber = newPreyMaxNumber;
  }

  public void setPreyViewAngle(
      final double newPreyViewAngle) {
    this.preyViewAngle = newPreyViewAngle;
  }

  public void setPreySplitEnergyFillingSpeed(
      final double newPreySplitEnergyFillingSpeed) {
    this.preySplitEnergyFillingSpeed = newPreySplitEnergyFillingSpeed;
  }

  public void setNeuralNetworkLayersNumber(
      final int newNeuralNetworkLayersNumber) {
    this.neuralNetworkLayersNumber = newNeuralNetworkLayersNumber;
  }

  // Getters
  public int getEntitySensorsCount() {
    return this.entitySensorsCount;
  }

  public double getEntityRadius() {
    return this.entityRadius;
  }

  public double getEntitySensorsLength() {
    return this.entitySensorsLength;
  }

  public double getEntityMaxSpeed() {
    return this.entityMaxSpeed;
  }

  public double getEntitySpeedMutationRate() {
    return this.entitySpeedMutationRate;
  }

  public double getEntityEnergyDrainRate() {
    return this.entityEnergyDrainRate;
  }

  public double getPredatorMaxNumber() {
    return this.predatorMaxNumber;
  }

  public double getPredatorViewAngle() {
    return this.predatorViewAngle;
  }

  public double getPredatorSplitEnergyFillingSpeed() {
    return this.predatorSplitEnergyFillingSpeed;
  }

  public double getPreyMaxNumber() {
    return this.preyMaxNumber;
  }

  public double getPreyViewAngle() {
    return this.preyViewAngle;
  }

  public double getPreySplitEnergyFillingSpeed() {
    return this.preySplitEnergyFillingSpeed;
  }

  public int getNeuralNetworkLayersNumber() {
    return this.neuralNetworkLayersNumber;
  }
}