package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.math.Formulas;
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
     * The maximum possible speed of the entity during a mutation.
     */
    protected static final double MAX_SPEED = 3.0;

    /**
     * The Mutation rate of the speed of the entity.
     */
    protected static final double SPEED_MUTATION_RATE = 0.2;

    /**
     * The speed at which the energy of the entity will be drained.
     */
    public static final double ENERGY_DRAIN_RATE = 0.01;

    /**
     * The sensors of the entity.
     * Each sensor is a line that will be used to detect other entities.
     */
    private Line[] sensors;

    /**
     * The random speed of the entity.
     * This value determines the maximum speed of the entity.
     */
    private final double speed;

    /**
     * The neural network of the entity.
     * The neural network will be used to compute the output of the entity.
     * The output of the neural network will be used to move the entity.
     */
    private final NeuralNetwork brain;

    /**
     * The position of the entity.
     */
    private final Circle body;

    /**
     * The energy of the entity.
     * The energy will be drained when the entity moves.
     * Then entity will not be able to move when the energy is 0.
     * <p>
     * The value of the energy will be between 0 and 1.
     */
    private double energy;


    /**
     * The accumulated split energy of the entity.
     * The entity will multiply when the accumulated split energy reaches 1.
     * The accumulated split energy will be set to 0 when the entity splits.
     */
    private double splitEnergy;

    /**
     * The rotation angle of the entity in radians.
     * This angle represents the direction of the entity.
     */
    private double directionAngleInRadians;

    /**
     * The number of children of the entity.
     */
    private int childCount;

    /**
     * The view angle of the entity.
     * The view angle is the angle of the field of view of the entity.
     */
    private double fovAngleInDegrees;


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
     * The constructor of the entity.
     *
     * @param entitySpeed      The speed of the entity.
     * @param entityPosition   The position of the entity.
     * @param newViewAngle     The view angle of the entity.
     * @param newRotationAngle The rotation angle of the entity.
     */
    protected Entity(final double entitySpeed,
                     final Point entityPosition,
                     final double newViewAngle,
                     final double newRotationAngle) {
        this.body = new Circle(entityPosition, Entity.ENTITY_RADIUS);
        this.brain = new NeuralNetwork(
                Arrays.stream(new int[]{SENSORS_COUNT, 10, 2})
                        .boxed().collect(ArrayList::new,
                                ArrayList::add,
                                ArrayList::addAll));
        this.speed = entitySpeed;

        this.directionAngleInRadians = newRotationAngle;

        //initialize sensors
        this.sensors = new Line[Entity.SENSORS_COUNT];
        this.fovAngleInDegrees = newViewAngle;
        adjustSensors();
    }

    /**
     * Move the entity according to the given movement speed
     * and its current rotation angle.
     * The energy of the entity will be drained according to the movement speed.
     *
     * @param movementSpeed The speed of the movement.
     */
    public void move(final double movementSpeed) {
        Point position = this.body.getCenter();
        position.setX(position.getX()
                + Math.cos(this.directionAngleInRadians) * movementSpeed);
        position.setY(position.getX()
                + Math.sin(this.directionAngleInRadians) * movementSpeed);
        this.energy -= Entity.ENERGY_DRAIN_RATE * movementSpeed;
    }

    /**
     * Each subclass entity will have its own implementation of this method.
     * This method will be called on every update.
     */
    public abstract void onUpdate();

    /**
     * This method will adjust the sensors of the
     * entity according to the entity's position and rotation angle.
     */
    public void adjustSensors() {
        double angleBetweenSensors = this.fovAngleInDegrees
                / (Entity.SENSORS_COUNT - 1);
        for (int i = 0; i < Entity.SENSORS_COUNT; i++) {
            double angle = this.directionAngleInRadians
                    + Math.toRadians(-this.fovAngleInDegrees / 2
                    + angleBetweenSensors * i);
            this.sensors[i].getStartPoint()
                    .setCoordinates(this.getBodyCenter().getX(),
                            this.getBodyCenter().getY());

            this.sensors[i].getEndPoint()
                    .setCoordinates(this.getBodyCenter().getX()
                                    + Math.cos(angle) * Entity.SENSORS_LENGTH,
                            this.getBodyCenter().getY()
                                    + Math.sin(angle) * Entity.SENSORS_LENGTH);
        }
    }

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
        double distance = Formulas.distance(this.getBodyCenter().getX(),
                this.getBodyCenter().getY(),
                entity.getBodyCenter().getX(),
                entity.getBodyCenter().getY());
        boolean collide = false;
        if (distance < Entity.ENTITY_RADIUS * 2) {
            collide = true;
            onCollide();
        }
        //distance between the two entities
        return collide;
    }

    public final Line[] getSensors() {
        return this.sensors;
    }
    public final double getEnergy() {
        return this.energy;
    }
    public final double getSplitEnergy() {
        return this.splitEnergy;
    }
    public final int getChildCount() {
        return this.childCount;
    }

    public final double getDirectionAngleInRadians() {
        return this.directionAngleInRadians;
    }

    public final double getSpeed() {
        return this.speed;
    }

    public final Point getBodyCenter() {
        return this.body.getCenter();
    }

    public final NeuralNetwork getBrain() {
        return this.brain;
    }

    protected final void setEnergy(final double entityEnergy) {
        this.energy = entityEnergy;
    }

    protected final void setSplitEnergy(final double entitySplitEnergy) {
        this.splitEnergy = entitySplitEnergy;
    }

    protected final void setChildCount(final int entityChildCount) {
        this.childCount = entityChildCount;
    }
}
