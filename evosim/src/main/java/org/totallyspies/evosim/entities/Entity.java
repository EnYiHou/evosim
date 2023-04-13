package org.totallyspies.evosim.entities;

import javafx.scene.paint.Color;
import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.utils.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * An Entity is an abstract member of the evolution simulation that can take the form of a
 * Predator or Prey. Each type have a different set of criteria that must be fulfilled to
 * reproduce or die. Each Entity is controlled by their brains, neural networks designed to learn
 * the best possible option for survival when faced with an enemy.
 *
 * @author EnYi, Matthew
 */
public abstract class Entity {

    /**
     * An array of sensors represented by custom Line objects.
     */
    private final Line[] sensors;

    /**
     * A list of detected distances from the sensors.
     */
    private final Double[] sensorsData;

    /**
     * If the entity is dead or not.
     */
    private boolean death;

    /**
     * If the entity split.
     */
    private boolean split;

    /**
     * The fixed entity speed randomly chosen at birth for an entity.
     */
    private final double speed;

    /**
     * The neural network of the entity.
     * <p>
     * The network is passed input data from the entity's sensors and outputs data that will be
     * used to make it's next decision.
     * </p>
     */
    private NeuralNetwork brain;

    /**
     * The position of the entity.
     */
    private final Circle body;

    /**
     * The current amount of energy this Entity has left.
     * <p>
     * Energy is drained slowly whenever the entity moves. It is a double value within a range of
     * 0 to 1, where at 0 the the entity cannot move.
     * </p>
     */
    private double energy;

    /**
     * The current amount of split energy this Entity has accumulated.
     * <p>
     * Split energy determines whether or not the entity can multiply yet. It is bounded between
     * 0 to 1, where at 1 the entity will multiply.
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
    private final double fovAngleInDegrees;


    /**
     * The x index of the grid this entity is in.
     */
    private int gridX;

    /**
     * The y index of the grid this entity is in.
     */
    private int gridY;

    /**
     * The color of the entity.
     */
    private Color color;

    /**
     * Clones this entity and mutates some of its properties.
     *
     * @return the cloned entity.
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
    protected Entity(final double entitySpeed, final Point entityPosition,
                     final double newViewAngle, final double newRotationAngle) {
        // initialize entity properties
        this.energy = 1d;
        this.splitEnergy = 0d;
        this.death = false;
        this.split = false;
        this.childCount = 0;
        this.speed = entitySpeed;
        this.directionAngleInRadians = newRotationAngle;
        this.fovAngleInDegrees = newViewAngle;
        this.body = new Circle(entityPosition, Configuration.getConfiguration().getEntityRadius());

        int sensorCount = Configuration.getConfiguration().getEntitySensorsCount();
        // initialize neural network
        this.brain = new NeuralNetwork(List.of(sensorCount, 10, 2));

        // initialize sensors
        this.sensors = new Line[sensorCount];
        for (int i = 0; i < sensorCount; i++) {
            this.sensors[i] = new Line(0, 0, 0, 0);
        }
        this.sensorsData = new Double[sensorCount];
        Arrays.fill(this.sensorsData, Configuration.getConfiguration().getEntitySensorsLength());
        this.adjustSensors();
    }

    /**
     * Moves the entity according to the given movement speed and its current rotation angle.
     * <p>
     * If the entity moves off the map, it will wrap around to the other side. The energy of the
     * entity will be drained by the amount of movement.
     * </p>
     *
     * @param movementSpeed the speed of the movement.
     */
    public void move(final double movementSpeed) {
        Point position = this.body.getCenter();

        // wrap around the map
        double positionX = Math.max(0, Math.min(
            position.getX() + Math.cos(this.directionAngleInRadians) * movementSpeed,
            Simulation.MAP_SIZE_X * Simulation.GRID_SIZE
        ));

        double positionY = Math.max(0, Math.min(
            position.getY() + Math.sin(this.directionAngleInRadians) * movementSpeed,
            Simulation.MAP_SIZE_Y * Simulation.GRID_SIZE
        ));

        position.setX(positionX);
        position.setY(positionY);

        // drain energy
        this.energy -= Configuration.getConfiguration().getEntityEnergyDrainRate() * movementSpeed;
    }

    /**
     * Adjusts this entity's sensors based on its position and its direction.
     */
    public void adjustSensors() {
        double angleBetweenSensors = this.fovAngleInDegrees
                / (Configuration.getConfiguration().getEntitySensorsCount() - 1);

        for (int i = 0; i < Configuration.getConfiguration().getEntitySensorsCount(); i++) {
            double angle = this.directionAngleInRadians
                    + Math.toRadians(-this.fovAngleInDegrees / 2 + angleBetweenSensors * i);

            this.sensors[i].getStartPoint()
                    .setCoordinates(this.getBodyCenter().getX(), this.getBodyCenter().getY());

            this.sensors[i].getEndPoint().setCoordinates(
                    this.getBodyCenter().getX()
                            + Math.cos(angle) * Configuration
                            .getConfiguration()
                            .getEntitySensorsLength(),
                    this.getBodyCenter().getY()
                            + Math.sin(angle)
                            * Configuration.getConfiguration()
                            .getEntitySensorsLength()
            );
        }
    }

    /**
     * Processes data from this entity's sensors and moves according to its decision.
     */
    public final void update() {
        this.adjustSensors();

        // check for collisions and update sensors data;
        this.onUpdate();

        if (this.splitEnergy > 1) {
            this.splitEnergy -= 1;
            this.split = true;
        }

        List<Double> calculatedDecision =
                this.brain.calcNetworkDecision(Arrays.asList(this.sensorsData));

        // Assuming the first output is the rotation
        // of the direction of the entity, and the second output is the speed.
        this.directionAngleInRadians += Configuration.getConfiguration()
                .getEntityMaxRotationSpeed() * calculatedDecision.get(0);
        this.move(this.speed * calculatedDecision.get(1));

    }

    /**
     * Determines if this entity collides with another entity.
     * @param other Entity to be checked.
     * @return If both entities collide.
     */
    public boolean collidesWith(final Entity other) {
        if (other.getClass().equals(this.getClass())) {
            return false;
        }

        double distance = Formulas.distance(
            this.getBodyCenter().getX(),
            this.getBodyCenter().getY(),
            other.getBodyCenter().getX(),
            other.getBodyCenter().getY()
        );

        return distance < Configuration.getConfiguration().getEntityRadius() * 2;
    }

    protected abstract void onCollideHandler(Entity other);

    /**
     * Event when this entity collides with another.
     * @param other The entity that has been collided into.
     */
    public void onCollide(final Entity other) {
        for (int sensorIndex = 0; sensorIndex < this.sensors.length; sensorIndex++) {
            Line sensor = this.sensors[sensorIndex];
            Double distanceToEntity =
                Formulas.closestIntersection(sensor, other.getBody());
            this.sensorsData[sensorIndex] =
                Math.min(this.sensorsData[sensorIndex], distanceToEntity);
        }

        this.onCollideHandler(other);
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

    public final boolean getDeath() {
        return this.death;
    }

    public final Color getColor() {
        return this.color;
    }

    public final void setDeath(final boolean isDead) {
        this.death = isDead;
    }

    public final boolean getSplit() {
        return split;
    }

    public final void setSplit(final boolean isSplit) {
        this.split = isSplit;
    }

    public final double getSpeed() {
        return this.speed;
    }

    public final Point getBodyCenter() {
        return this.body.getCenter();
    }

    public final Circle getBody() {
        return this.body;
    }

    public final NeuralNetwork getBrain() {
        return this.brain;
    }

    public final void setBrain(final NeuralNetwork entityBrain) {
        this.brain = entityBrain;
    }

    protected final void setEnergy(final double entityEnergy) {
        this.energy = entityEnergy;
    }

    public final void setSplitEnergy(final double entitySplitEnergy) {
        this.splitEnergy = entitySplitEnergy;
    }

    public final void setChildCount(final int entityChildCount) {
        this.childCount = entityChildCount;
    }

    public final void setGridX(final int entityGridX) {
        this.gridX = entityGridX;
    }

    public final void setGridY(final int entityGridY) {
        this.gridY = entityGridY;
    }

    public final void setColor(final Color entityColor) {
        this.color = entityColor;
    }

    public final void setDirectionAngleInRadians(final double entityDirectionAngleInRadians) {
        this.directionAngleInRadians = entityDirectionAngleInRadians;
    }
}
