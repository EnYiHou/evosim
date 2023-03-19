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
    this.entitySensorsCount = 30;
    this.entityRadius = 15.0d;
    this.entitySensorsLength = 1000.0d;
    this.entityMaxSpeed = 3.0d;
    this.entitySpeedMutationRate = 0.2d;
    this.entityEnergyDrainRate = 0.01d;

    this.predatorMaxNumber = 100;
    this.predatorViewAngle = 60.0d;
    this.predatorSplitEnergyFillingSpeed = 0.5d;

    this.preyMaxNumber = 100;
    this.preyViewAngle = 300.0d;
    this.preySplitEnergyFillingSpeed = 0.5d;
  }


}
