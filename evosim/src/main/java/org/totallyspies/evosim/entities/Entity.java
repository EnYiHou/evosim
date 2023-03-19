package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;

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
     * Each sensor is a line that will be used to detect other entities.
     */
    private Line[] sensors;


    /**
     * The speed of the entity.
     * The entity will move according to this speed on every update.
     */
    private final double speed;

    /**
     * The maximum speed of the entity.
     */
    protected static final double MAX_SPEED = 3.0;
    /**
     * The Mutation rate of the speed of the entity.
     */
    protected static final double SPEED_MUTATION_RATE = 0.2;

    /**
     * The neural network of the entity.
     * The neural network will be used to compute the output of the entity.
     * The output of the neural network will be used to move the entity.
     */
    private final NeuralNetwork brain;

    /**
     * The position of the entity.
     */
    private Point position;

    /**
     * The energy of the entity.
     * The energy will be drained when the entity moves.
     * Then entity will not be able to move when the energy is 0.
     * <p>
     * The value of the energy will be between 0 and 1.
     */
    private double energy;

    /**
     * The speed at which the energy of the entity will be drained.
     */
    public static final double ENERGY_DRAIN_RATE = 0.01;

    /**
     * The accumulated split energy of the entity.
     * The entity will multiply when the accumulated split energy reaches 1.
     * The accumulated split energy will be set to 0 when the entity splits.
     */
    private double splitEnergy;
    /**
     * The rotation angle of the entity in radians.
     */
    private double rotationAngleInRadians;

    /**
     * The number of children of the entity.
     */
    private int childCount;


    /**
     * The constructor of the entity.
     *
     * @param entitySpeed    The speed of the entity.
     * @param entityPosition The position of the entity.
     */
    public Entity(final double entitySpeed, final Point entityPosition) {
        this.brain = new NeuralNetwork(
                Arrays.stream(new int[]{SENSORS_COUNT, 10, 2})
                .boxed().collect(
                        ArrayList::new, ArrayList::add, ArrayList::addAll));
        this.speed = entitySpeed;

        //randomize initial rotation angle
        this.rotationAngleInRadians = Math.random() * 2 * Math.PI;

        //initialize sensors
        this.sensors = new Line[Entity.SENSORS_COUNT];
        this.position = entityPosition;
        adjustSensors();
    }

    /**
     * The constructor of the entity.
     *
     * @param entitySpeed The speed of the entity.
     */
    public Entity(final double entitySpeed) {
        this(entitySpeed, new Point());
    }

    /**
     * Move the entity according to the given movement speed
     * and its current rotation angle.
     * The energy of the entity will be drained according to the movement speed.
     *
     * @param movementSpeed The speed of the movement.
     */
    public void move(final double movementSpeed) {
        this.position.setPositionX(this.position.getPositionX()
                + Math.cos(this.rotationAngleInRadians) * movementSpeed);
        this.position.setPositionY(this.position.getPositionX()
                + Math.sin(this.rotationAngleInRadians) * movementSpeed);
        this.energy -= Entity.ENERGY_DRAIN_RATE * movementSpeed;
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
     */
    public abstract void onCollide();

    /**
     * Each subclass entity will have its own implementation of this method.
     * This method will be called when the entity splits.
     */
    public abstract void onSplit();

    /**
     * This method will be called when the entity dies.
     */
    public void onDie() {
    }

    /**
     * This method will be called at each update.
     * This method will be in charge of the behavior of the entity.
     */
    public final void update() {
        this.adjustSensors();
        //compute sensors value
        //get output of neural network
        this.move(speed);
        this.onUpdate();
    }

    /**
     * This method will be called to detect collision between this entity
     * and another entity.
     *
     * @param entity The entity to check collision with.
     * @return true if the two entities collide, false otherwise.
     */
    public final boolean checkCollide(final Entity entity) {
        // TODO check if the two entities collide
        double distance = 0;
        boolean collide = false;
        if (distance < Entity.ENTITY_RADIUS * 2) {
            collide = true;
            onCollide();
        }
        //distance between the two entities
        return collide;
    }

    /**
     * This method returns the sensors of the entity.
     *
     * @return The sensors of the entity.
     */
    public final Line[] getSensors() {
        return this.sensors;
    }

    /**
     * This method returns the position of the entity.
     *
     * @return The position of the entity.
     */
    public final Point getPosition() {
        return this.position;
    }

    /**
     * This method returns the energy of the entity.
     *
     * @return The energy of the entity.
     */
    public final double getEnergy() {
        return this.energy;
    }

    /**
     * This method returns the split energy of the entity.
     *
     * @return The split energy of the entity.
     */
    public final double getSplitEnergy() {
        return this.splitEnergy;
    }

    /**
     * This method returns the child count of the entity.
     *
     * @return The child count of the entity.
     */
    public final int getChildCount() {
        return this.childCount;
    }

    /**
     * This method returns the rotation angle of the entity in radians.
     *
     * @return The rotation angle of the entity in radians.
     */
    public final double getRotationAngleInRadians() {
        return this.rotationAngleInRadians;
    }

    /**
     * This method returns the speed of the entity.
     *
     * @return The speed of the entity.
     */
    public final double getSpeed() {
        return this.speed;
    }

    /**
     * This method returns the neural network of the entity.
     *
     * @return The neural network of the entity.
     */
    public final NeuralNetwork getBrain() {
        return this.brain;
    }

    /**
     * This method sets the position of the entity.
     *
     * @param entityPosition The position of the entity.
     */
    public final void setPosition(final Point entityPosition) {
        this.position = entityPosition;
    }
    /**
     * This method sets the energy of the entity.
     *
     * @param entityEnergy The energy of the entity.
     */
    public final void setEnergy(final double entityEnergy) {
        this.energy = entityEnergy;
    }
    /**
     * This method sets the split energy of the entity.
     *
     * @param entitySplitEnergy The split energy of the entity.
     */
    public final void setSplitEnergy(final double entitySplitEnergy) {
        this.splitEnergy = entitySplitEnergy;
    }
    /**
     * This method sets the child count of the entity.
     *
     * @param entityChildCount The child count of the entity.
     */
    public final void setChildCount(final int entityChildCount) {
        this.childCount = entityChildCount;
    }
    /**
     * This method sets the rotation angle of the entity in radians.
     *
     * @param entityRotationAngleInRadians The rotation angle of the
     *                                     entity in radians.
     */
    public final void setRotationAngleInRadians(
            final double entityRotationAngleInRadians) {
        this.rotationAngleInRadians = entityRotationAngleInRadians;
    }
}
