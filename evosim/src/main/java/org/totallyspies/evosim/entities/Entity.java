package org.totallyspies.evosim.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.paint.Color;
import lombok.*;
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
@Getter
public abstract class Entity {

    /**
     * An array of sensors represented by custom Line objects.
     */
    @JsonProperty("sensors")
    private final Line[] sensors;

    /**
     * A list of detected distances from the sensors.
     */
    @JsonProperty("sensorsData")
    private final double[] sensorsData;

    /**
     * The fixed entity speed randomly chosen at birth for an entity.
     */
    @JsonProperty("speed")
    private final double speed;

    /**
     * The position of the entity.
     */
    @JsonProperty("body")
    private final Circle body;

    /**
     * The angle of the field of view cone of this entity in degrees.
     */
    @JsonProperty("fovAngleInDegrees")
    private final double fovAngleInDegrees;

    /**
     * The color of the entity.
     */
    @JsonProperty("color")
    private final Color color;

    /**
     * The birth time of the entity.
     */

    @JsonProperty("birthTime")
    private final long birthTime;
    /**
     * If the entity is dead or not.
     */
    @Setter
    @JsonProperty("dead")
    private boolean dead;

    /**
     * If the entity split.
     */
    @Setter
    @JsonProperty("split")
    private boolean split;

    /**
     * The neural network of the entity.
     * <p>
     * The network is passed input data from the entity's sensors and outputs data that will be
     * used to make it's next decision.
     * </p>
     */
    @Setter
    @JsonProperty("brain")
    private NeuralNetwork brain;

    /**
     * The current amount of energy this Entity has left.
     * <p>
     * Energy is drained slowly whenever the entity moves. It is a double value within a range of
     * 0 to 1, where at 0 the the entity cannot move.
     * </p>
     */
    @Setter
    @JsonProperty("energy")
    private double energy;

    /**
     * The current amount of split energy this Entity has accumulated.
     * <p>
     * Split energy determines whether or not the entity can multiply yet. It is bounded between
     * 0 to 1, where at 1 the entity will multiply.
     * </p>
     */
    @Setter
    @JsonProperty("splitEnergy")
    private double splitEnergy;

    /**
     * The direction the entity is facing in radians.
     */
    @JsonProperty("directionAngleInRadians")
    private double directionAngleInRadians;

    /**
     * The number of children born from this entity.
     */
    @Setter
    @JsonProperty("childCount")
    private int childCount;

    /**
     * Constructs a new Entity.
     *
     * @param entitySpeed      The speed of the entity.
     * @param entityPosition   The position of the entity.
     * @param newBirthTime     The birth time of the entity.
     * @param newViewAngle     The view angle of the entity.
     * @param newRotationAngle The rotation angle of the entity.
     * @param newCol            The color of the entity
     */
    protected Entity(final double entitySpeed, final Point entityPosition, final long newBirthTime,
                     final double newViewAngle, final double newRotationAngle, final Color newCol) {
        this.birthTime = newBirthTime;
        this.color = newCol;
        // initialize entity properties
        this.energy = 1d;
        this.splitEnergy = 0d;
        this.dead = false;
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
        this.sensorsData = new double[sensorCount];
        Arrays.fill(this.sensorsData, Configuration.getConfiguration().getEntitySensorsLength());
        this.adjustSensors();
    }

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

        double positionX = Math.max(0, Math.min(
                position.getX() + Math.cos(this.directionAngleInRadians) * movementSpeed,
                Simulation.MAP_SIZE_X * Simulation.GRID_SIZE
        ));

        double positionY = Math.max(0, Math.min(
                position.getY() + Math.sin(this.directionAngleInRadians) * movementSpeed,
                Simulation.MAP_SIZE_Y * Simulation.GRID_SIZE
        ));

        //this.updateGridRegistration(new Point(positionX, positionY), this.getBodyCenter());

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
        if (this.isDead()) {
            return;
        }

        this.adjustSensors();

        // check for collisions and update sensors data;
        this.onUpdate();

        if (this.splitEnergy > 1) {
            this.splitEnergy -= 1;
            this.split = true;
        }

        final double[] calculatedDecision =
                this.brain.calcNetworkDecision(this.sensorsData);

        // Assuming the first output is the rotation
        // of the direction of the entity, and the second output is the speed.
        this.directionAngleInRadians += Configuration.getConfiguration()
                .getEntityMaxRotationSpeed() * calculatedDecision[0];
        this.move(this.speed * calculatedDecision[1]);

    }

    /**
     * Determines if this entity collides with another entity.
     *
     * @param other Entity to be checked.
     * @return If both entities collide.
     */
    public boolean collidesWith(final Entity other) {
        if (other.getClass().equals(this.getClass()) || this.dead || other.dead) {
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
     *
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


    public final Point getBodyCenter() {
        return this.body.getCenter();
    }

    /**
     * Returns the living time of this entity.
     *
     * @param currentTime The current time.
     * @return The living time of this entity.
     */
    public final int getLivingTime(final long currentTime) {
        return (int) ((currentTime - this.birthTime) / 1000d);
    }
}
