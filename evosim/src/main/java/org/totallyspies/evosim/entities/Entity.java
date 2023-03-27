package org.totallyspies.evosim.entities;

import java.util.List;

import org.totallyspies.evosim.Configuration;
import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;

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
     * An array of sensors represented by custom Line objects.
     */
    private final Line[] sensors;
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
    private final double directionAngleInRadians;

    /**
     * The number of children born from this entity.
     */
    private int childCount;

    /**
     * The angle of the field of view cone of this entity in degrees.
     */
    private final double fovAngleInDegrees;

    /**
     * Constructs a new Entity.
     *
     * @param entitySpeed      The speed of the entity.
     * @param entityPosition   The position of the entity.
     * @param newViewAngle     The view angle of the entity.
     * @param newRotationAngle The rotation angle of the entity.
     */
    protected Entity(
        final double entitySpeed,
        final Point entityPosition,
        final double newViewAngle,
        final double newRotationAngle
    ) {
        this.body = new Circle(
            entityPosition,
            Configuration.getConfiguration().getEntityRadius()
        );

        // TODO: Add configuration for # layers and nodes per layer
        this.brain = new NeuralNetwork(
            List.of(
                Configuration.getConfiguration().getEntitySensorsCount(),
                10,
                2
            ));

        this.speed = entitySpeed;

        this.directionAngleInRadians = newRotationAngle;

        //initialize sensors
        this.sensors = new Line[
            Configuration.getConfiguration().getEntitySensorsCount()
            ];

        this.fovAngleInDegrees = newViewAngle;
        adjustSensors();
    }

    /**
     * Clones this entity and mutates some of its properties.
     *
     * @return the cloned entity.
     */
    public abstract Entity clone();

    /**
     * Handles what happens on collision between certain entity types.
     */
    public abstract void onCollide();

    /**
     * Handles what happens when an entity multiplies.
     */
    public abstract void onSplit();

    /**
     * Handles what happens on update every frame to an entity.
     */
    public abstract void onUpdate();

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
        this.energy -= (
            Configuration.getConfiguration().getEntityEnergyDrainRate()
            * movementSpeed
        );
    }

    /**
     * Adjusts this entity's sensors based on its position and rotation onto
     * its new front side.
     */
    public void adjustSensors() {
        double angleBetweenSensors = this.fovAngleInDegrees
            / (Configuration.getConfiguration().getEntitySensorsCount() - 1);

        for (
            int i = 0;
            i < Configuration.getConfiguration().getEntitySensorsCount();
            i++
        ) {
            double angle = this.directionAngleInRadians
                + Math.toRadians(-this.fovAngleInDegrees / 2
                + angleBetweenSensors * i);
            this.sensors[i].getStartPoint()
                .setCoordinates(
                    this.getBodyCenter().getX(),
                    this.getBodyCenter().getY()
                );

            this.sensors[i].getEndPoint()
                .setCoordinates(
                    this.getBodyCenter().getX()
                        + Math.cos(angle) * Configuration
                        .getConfiguration()
                        .getEntitySensorsCount(),
                    this.getBodyCenter().getY()
                        + Math.sin(angle)
                        * Configuration.getConfiguration()
                            .getEntitySensorsCount()
                );
        }
    }

    /**
     * Removes the entity from the simulation and list of entities.
     */
    public void onDie() {
    }

    /**
     * Processes data from this entity's sensors and moves according to its
     * decision.
     */
    public final void update() {
        this.adjustSensors();
        //compute sensors value
        //get output of neural network
        this.move(speed);
        this.onUpdate();
    }

    /**
     * Detects collision between this entity and another.
     *
     * @param entity The entity to check collision with.
     * @return true if the two entities collide, false otherwise.
     */
    public final boolean checkCollide(final Entity entity) {
        // TODO check if the two entities collide
        double distance = Formulas.distance(
            this.getBodyCenter().getX(),
            this.getBodyCenter().getY(),
            entity.getBodyCenter().getX(),
            entity.getBodyCenter().getY()
        );
        boolean collide = false;
        if (distance < Configuration.getConfiguration().getEntityRadius() * 2) {
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

    protected final void setEnergy(final double entityEnergy) {
        this.energy = entityEnergy;
    }

    public final double getSplitEnergy() {
        return this.splitEnergy;
    }

    protected final void setSplitEnergy(final double entitySplitEnergy) {
        this.splitEnergy = entitySplitEnergy;
    }

    public final int getChildCount() {
        return this.childCount;
    }

    protected final void setChildCount(final int entityChildCount) {
        this.childCount = entityChildCount;
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
}
