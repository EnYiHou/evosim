package org.totallyspies.evosim;

/**
 * Configuration class consists of saving the settings the user configurations.
 * If the user close the application without saving the configurations,
 * the applications will remember them and will load them if needed.
 *
 * @author edeli
 */
public class Configuration {

  /**
   * Defaults values for each variable.
   */
  public static class Defaults {

    /**
     * The number of sensors each entity has.
     */
    public static final int ENTITY_SENSORS_COUNT = 30;

    /**
     *  The radius of an entity.
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
  }

  /**
   * The number of sensors each entity has.
   */
  private int entitySensorsCount;

  /**
   *  The radius of an entity.
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
   * Create a new default Configuration class, and the setup.
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
    this.predatorSplitEnergyFillingSpeed = Defaults.PREDATOR_SPLIT_ENERGY_FILLING_SPEED;

    this.preyMaxNumber = Defaults.PREY_MAX_NUMBER;
    this.preyViewAngle = Defaults.PREY_VIEW_ANGLE;
    this.preySplitEnergyFillingSpeed = Defaults.PREY_SPLIT_ENERGY_FILLING_SPEED;

    this.neuralNetworkLayersNumber = Defaults.NEURAL_NETWORK_LAYERS_NUMBER;
  }

  /**
   * Saves a Configuration file in the temporary files of the user's computer.
   *
   * @return if the file was saved without any issues
   */
  public String saveFileConfiguration() {


    return "Saved!";
  }

  // Setters
  public void setEntitySensorsCount(int entitySensorsCount) {
    this.entitySensorsCount = entitySensorsCount;
  }

  public void setEntityRadius(double entityRadius) {
    this.entityRadius = entityRadius;
  }

  public void setEntitySensorsLength(double entitySensorsLength) {
    this.entitySensorsLength = entitySensorsLength;
  }

  public void setEntityMaxSpeed(double entityMaxSpeed) {
    this.entityMaxSpeed = entityMaxSpeed;
  }

  public void setEntitySpeedMutationRate(double entitySpeedMutationRate) {
    this.entitySpeedMutationRate = entitySpeedMutationRate;
  }

  public void setEntityEnergyDrainRate(double entityEnergyDrainRate) {
    this.entityEnergyDrainRate = entityEnergyDrainRate;
  }

  public void setPredatorMaxNumber(double predatorMaxNumber) {
    this.predatorMaxNumber = predatorMaxNumber;
  }

  public void setPredatorViewAngle(double predatorViewAngle) {
    this.predatorViewAngle = predatorViewAngle;
  }

  public void setPredatorSplitEnergyFillingSpeed(double predatorSplitEnergyFillingSpeed) {
    this.predatorSplitEnergyFillingSpeed = predatorSplitEnergyFillingSpeed;
  }

  public void setPreyMaxNumber(double preyMaxNumber) {
    this.preyMaxNumber = preyMaxNumber;
  }

  public void setPreyViewAngle(double preyViewAngle) {
    this.preyViewAngle = preyViewAngle;
  }

  public void setPreySplitEnergyFillingSpeed(double preySplitEnergyFillingSpeed) {
    this.preySplitEnergyFillingSpeed = preySplitEnergyFillingSpeed;
  }

  public void setNeuralNetworkLayersNumber(int neuralNetworkLayersNumber) {
    this.neuralNetworkLayersNumber = neuralNetworkLayersNumber;
  }
}
