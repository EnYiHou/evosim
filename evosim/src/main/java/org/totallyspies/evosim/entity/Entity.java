package org.totallyspies.evosim.entity;

import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;

/**
 * This class represents an entity in the simulation.
 * @author EnYi
 */
public abstract class Entity {

    /**
     * The radius of an entity.
     */
    public static final double ENTITY_RADIUS = 15.0d;

    /**
     * The number of sensors each entity has.
     */
    public static final int SENSORS_COUNT = 30;

    /**
     * The length of each sensor.
     */
    public static final double SENSORS_LENGTH = 1000.0d;

    /**
     * The sensors of the entity.
     * Each sensor is represented by a 4-element array of doubles.
     * The first two elements are the coordinates of the sensor's origin.
     * The last two elements are the coordinates of the sensor's end.
     * The sensors are ordered from left to right.
     */
    protected double[][] sensors;

    /**
     * The speed of the entity. The entity will move according to this speed on every update.
     */
    final protected double speed;

    final static protected double MAX_SPEED = 3.0;
    /**
     * The rotation angle of the entity in radians.
     */
    final static protected double SPEED_MUTATION_RATE = 0.2;

    /**
     * The neural network of the entity.
     * The neural network will be used to compute the output of the entity.
     * The output of the neural network will be used to move the entity.
     */
    final protected NeuralNetwork brain;

    /**
     * The positionX of the entity.
     */
    protected double positionX;

    /**
     * The positionY of the entity.
     */
    protected double positionY;

    /**
     * The energy of the entity.
     * The energy will be drained when the entity moves.
     * Then entity will not be able to move when the energy is 0.
     *
     * The value of the energy will be between 0 and 1.
     */
    protected double energy;

    protected static double ENERGY_DRAIN_RATE = 0.01;

    /**
     * The accumulated split energy of the entity.
     * The entity will multiply when the accumulated split energy reaches 1.
     * The accumulated split energy will be set to 0 when the entity splits.
     */
    protected double splitEnergy;
    /**
     * The rotation angle of the entity in radians.
     */
    protected double rotationAngleInRadians;

    /**
     * The number of children of the entity.
     */
    protected int childCount;

    /**
     * The constructor of the entity.
     *
     * @param speed The speed of the entity.
     */
    public Entity(double speed) {
        this.brain = new NeuralNetwork();
        this.speed = speed;

        //randomize initial rotation angle
        this.rotationAngleInRadians = Math.random() * 2 * Math.PI;

        //initialize sensors
        this.sensors = new double[Entity.SENSORS_COUNT][4];
        adjustSensors();

    }

    /**
     * Move the entity according to the given movement speed
     * and its current rotation angle.
     * The energy of the entity will be drained according to the movement speed.
     *
     * @param movementSpeed The speed of the movement.
     */
    public void move(double movementSpeed){
        this.positionX += Math.cos(this.rotationAngleInRadians) * movementSpeed;
        this.positionY += Math.sin(this.rotationAngleInRadians) * movementSpeed;
        this.energy -= Entity.ENERGY_DRAIN_RATE*movementSpeed;
    }

    /**
     * Each subclass entity will have its own implementation of this method.
     * This method will be called on every update.
     */
    public abstract void onUpdate();

    /**
     * Each subclass entity will have its own implementation of this method.
     * They will adjust its sensors according to its own implementation.
     * The sensors will be adjusted on every update,
     * according to the Entity's position and rotation angle.
     */
    public abstract void adjustSensors();

    /**
     * Each subclass entity will have its own implementation of this method.
     * This method will be called to clone the entity.
     * @return The cloned entity.
     */
    public abstract Entity clone();

    /**
     * Each entity will have its own implementation of this method.
     * This method will be called when the entity collides with another entity.
     *
     * @param entity The entity that the entity collides with.
     */
    public abstract void onCollide(Entity entity);

    /**
     * Each subclass entity will have its own implementation of this method.
     * This method will be called when the entity splits.
     */
    public abstract void onSplit();

    /**
     * This method will be called when the entity dies.
     */
    public void onDie(){
        //TODO: implement this method
    }




    public void update() {

        this.adjustSensors();
        //compute sensors value
        //get output of neural network
        this.move(speed);
        this.onUpdate();
    }

    public boolean checkCollide(Entity entity) {
        double distance = Math.sqrt(Math.pow(this.positionX - entity.positionX, 2)
                + Math.pow(this.positionY - entity.positionY, 2));
        return (distance < Entity.ENTITY_RADIUS * 2) ? true : false;
    }




    public double[] getSensorValues(){
        double[] sensorValues = new double[Entity.SENSORS_COUNT];
        for (int i = 0; i < Entity.SENSORS_COUNT; i++) {
            sensorValues[i] = this.sensors[i][4];
        }
        return sensorValues;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }



}
