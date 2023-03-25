package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;
import org.totallyspies.evosim.simulation.SimulationApp;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An Entity is an abstract member of the evolution simulation that can take
 * the form of a Predator or Prey. Each type have a different set of criteria
 * that must be fulfilled to reproduce or die. Each Entity is controlled by
 * their brains, neural networks designed to learn the best possible option
 * for survival when faced with an enemy.
 *
 * @author EnYi, Matthew
 */
public abstract class Entity {

    /**
     * The radius of an entity.
     */
    public static final double ENTITY_RADIUS = 15.0;

    /**
     * The number of sensors each entity has.
     */
    public static final int SENSORS_COUNT = 30;

    /**
     * The length of each sensor.
     */
    public static final double SENSORS_LENGTH = 1000.0;

    /**
     * The maximum speed that can be chosen for an entity during mutation.
     */
    public static final double MAX_SPEED = 3.0;

    /**
     * The minimum speed that can be chosen for an entity during mutation.
     */
    public static final double MIN_SPEED = 0.5;

    /**
     * A constant used to mutate the entities speed during reproduction.
     */
    protected static final double SPEED_MUTATION_RATE = 0.2;

    /**
     * The speed at which the energy of the entity will be drained.
     */
    public static final double ENERGY_DRAIN_RATE = 0.01;

    /**
     * An array of sensors represented by custom Line objects.
     */
    private Line[] sensors;

    /**
     * The fixed entity speed randomly chosen at birth for an entity.
     */
    private final double speed;

    /**
     * The neural network of the entity.
     * <p>
     * The network is passed input data from the entity's sensors and outputs
     * data that will be used to make it's next decision.
     * </p>
     */
    private final NeuralNetwork brain;

    /**
     * The position of the entity.
     */
    private final Circle body;

    /**
     * The current amount of energy this Entity has left.
     * <p>
     * Energy is drained slowly whenever the entity moves. It is a double
     * value within a range of 0 to 1, where at 0 the the entity cannot move.
     * </p>
     */
    private double energy;

    /**
     * The current amount of split energy this Entity has accumulated.
     * <p>
     * Split energy determines whether or not the entity can multiply yet. It is
     * bounded between 0 to 1, where at 1 the entity will multiply.
     * </p>
     */
    private double splitEnergy;

    /**
     * The direction the entity is facing in radians.
     */
    private double directionAngleInRadians;

    /**
     * The number of children born from this entity.
     */
    private int childCount;

    /**
     * The angle of the field of view cone of this entity in degrees.
     */
    private double fovAngleInDegrees;

    /**
     * Clones this entity and mutates some of its properties.
     *
     * @return  the cloned entity.
     */
    public abstract Entity clone();

    /**
     * Handles what happens on update every frame to an entity.
     */
    public abstract void onUpdate();

    /**
     * Constructs a new Entity.
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
     * Moves the entity according to the given movement speed and its current
     * rotation angle.
     * <p>
     * The entity's energy is drained according to their speed.
     * </p>
     *
     * @param movementSpeed the speed of the movement.
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
     * Adjusts this entity's sensors based on its position and rotation onto
     * its new front side.
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
     * Removes the entity from the simulation and list of entities.
     */
    protected void die() {
        SimulationApp.ENTITY_LIST.remove(this);
    }

    /**
     * Multiplies this Entity by spending its split energy.
     */
    protected void split() {
        SimulationApp.ENTITY_LIST.add(this.clone());
        this.setSplitEnergy(this.getSplitEnergy() - 1);
    }

    /**
     * Processes data from this entity's sensors and moves according to its
     * decision.
     */
    public final void update() {
        this.adjustSensors();
        // TODO compute output from the sensors and pass it into the NN
        // TODO use NN output in this.move() for the Entity

        this.move(speed);
        this.onUpdate();
    }

    /**
     * Detects collision between this entity and another.
     * <p>
     * Collision between two entities of the same type are ignored and will
     * return false.
     * </p>
     *
     * @return true if a prey and predator collide.
     */
    public final boolean checkCollide() {
//        double distance = Formulas.distance(this.getBodyCenter().getX(),
//                this.getBodyCenter().getY(),
//                entity.getBodyCenter().getX(),
//                entity.getBodyCenter().getY());
        boolean collide = false;
//        if (distance < Entity.ENTITY_RADIUS * 2) {
//            collide = true;
//        }
        // TODO refactor this method to only return true for the prey and
        //  predator involved in the condition (within the grid subset)
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
