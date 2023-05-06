package org.totallyspies.evosim.entities;

import javafx.scene.paint.Color;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
    @Getter
    private final Line[] sensors;

    /**
     * A list of detected distances from the sensors.
     */
    private final double[] inputs;
    /**
     * The fixed entity speed randomly chosen at birth for an entity.
     */
    @Getter
    private final double speed;
    /**
     * The position of the entity.
     */
    @Getter
    private final Circle body;
    /**
     * The angle of the field of view cone of this entity in degrees.
     */
    private final double fovAngleInRadians;
    /**
     * The color of the entity.
     */
    @Getter
    private final Color color;
    /**
     * The birth time of the entity.
     */
    @Getter
    private final long birthTime;

    /**
     * Simulation this entity is in.
     */
    @Getter(AccessLevel.PROTECTED)
    private final Simulation simulation;

    /**
     * If the entity is dead or not.
     */
    @Getter
    @Setter
    private boolean dead;
    /**
     * If the entity split.
     */
    @Getter
    @Setter
    private boolean split;

    /**
     * The neural network of the entity.
     * <p>
     * The network is passed input data from the entity's sensors and outputs data that will be
     * used to make it's next decision.
     * </p>
     */
    @Getter
    @Setter
    private NeuralNetwork brain;

    /**
     * The current amount of energy this Entity has left.
     * <p>
     * Energy is drained slowly whenever the entity moves. It is a double value within a range of
     * 0 to 1, where at 0 the the entity cannot move.
     * </p>
     */
    @Getter
    @Setter
    private double energy;
    /**
     * The current amount of split energy this Entity has accumulated.
     * <p>
     * Split energy determines whether or not the entity can multiply yet. It is bounded between
     * 0 to 1, where at 1 the entity will multiply.
     * </p>
     */
    @Getter
    @Setter
    private double splitEnergy;
    /**
     * The direction the entity is facing in radians.
     */
    @Getter
    private double directionAngleInRadians;
    /**
     * The number of children born from this entity.
     */
    @Getter
    @Setter
    private int childCount;

    /**
     * Constructs a new Entity.
     *
     * @param newSimulation    Simulation for the entity to be created in.
     * @param entitySpeed      The speed of the entity.
     * @param entityPosition   The position of the entity.
     * @param newBirthTime     The birth time of the entity.
     * @param newViewAngle     The view angle of the entity.
     * @param newRotationAngle The rotation angle of the entity.
     * @param newCol            The color of the entity
     */
    protected Entity(final Simulation newSimulation, final double entitySpeed,
        final Point entityPosition, final long newBirthTime, final double newViewAngle,
        final double newRotationAngle, final Color newCol) {
        this.birthTime = newBirthTime;
        this.simulation = newSimulation;
        this.color = newCol;
        // initialize entity properties
        this.energy = 1d;
        this.splitEnergy = 0d;
        this.dead = false;
        this.split = false;
        this.childCount = 0;
        this.speed = entitySpeed;
        this.directionAngleInRadians = newRotationAngle;
        this.fovAngleInRadians = Math.toRadians(newViewAngle);

        this.body = new Circle(entityPosition, Configuration.getConfiguration().getEntityRadius());

        final int sensorCount = Configuration.getConfiguration().getEntitySensorsCount();

        // Inputs with distances from each side
        final int inputCount = sensorCount + 4;

        // initialize neural network
        //TODO: List of layers
        this.brain = new NeuralNetwork(
            List.of(inputCount, 12, 6, 2)
        );

        // initialize sensors
        this.sensors = new Line[sensorCount];
        for (int i = 0; i < sensorCount; i++) {
            this.sensors[i] = new Line(0, 0, 0, 0);
        }
        this.inputs = new double[inputCount];
        Arrays.fill(this.inputs, Configuration.getConfiguration().getEntitySensorsLength());
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

        double positionX = Math.max(0,
            Math.min(position.getX() + Math.cos(this.directionAngleInRadians) * movementSpeed,
                this.simulation.getMapSizeX() * this.simulation.getGridSize()));

        double positionY = Math.max(0,
            Math.min(position.getY() + Math.sin(this.directionAngleInRadians) * movementSpeed,
                this.simulation.getMapSizeY() * this.simulation.getGridSize()));

        position.setX(positionX);
        position.setY(positionY);

        // drain energy
        this.energy -= Configuration.getConfiguration().getEntityEnergyDrainRate() * movementSpeed;
    }

    /**
     * Processes data from this entity's sensors and moves according to its decision.
     */
    public final void update() {
        if (this.isDead()) {
            return;
        }

        // check for collisions and update sensors data;
        this.onUpdate();

        if (this.splitEnergy > 1) {
            this.splitEnergy -= 1;
            this.split = true;
        }

        final double xPos = this.getBodyCenter().getX();
        final double yPos = this.getBodyCenter().getY();

        this.inputs[this.inputs.length - 4] = xPos;
        this.inputs[this.inputs.length - 3] = yPos;
        this.inputs[this.inputs.length - 2] =
            this.simulation.getMapSizeX() * this.simulation.getGridSize() - xPos;

        this.inputs[this.inputs.length - 1] =
            this.simulation.getMapSizeY() * this.simulation.getGridSize() - yPos;

        final double[] calculatedDecision =
                this.brain.calcNetworkDecision(this.inputs);

        // Assuming the first output is the rotation
        // of the direction of the entity, and the second output is the speed.
        this.directionAngleInRadians += Configuration.getConfiguration()
                .getEntityMaxRotationSpeed() * (calculatedDecision[0] - 0.5);
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

        final double angle = Math.atan2(
            other.getBodyCenter().getY() - this.getBodyCenter().getY(),
            other.getBodyCenter().getX() - this.getBodyCenter().getX()
        );

        this.updateSensors(distance, angle);

        return distance < Configuration.getConfiguration().getEntityRadius() * 2;
    }

    private void updateSensors(final double distance, final double angle) {
        final double start = Formulas.normAngle(
            this.directionAngleInRadians - this.fovAngleInRadians / 2
        );

        final double end = Formulas.normAngle(
            this.directionAngleInRadians + this.fovAngleInRadians / 2
        );

        if (!(start < angle && angle < end)) {
            return;
        }

        final double delta = angle - start;

        final int sensorIndex = (int) ((delta * this.sensors.length) / (2 * Math.PI));

        this.inputs[sensorIndex] = Math.min(
            distance, Configuration.getConfiguration().getEntitySensorsLength()
        );
    }

    protected abstract void onCollideHandler(Entity other);

    /**
     * Event when this entity collides with another.
     *
     * @param other The entity that has been collided into.
     */
    public void onCollide(final Entity other) {
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
