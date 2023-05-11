package org.totallyspies.evosim.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.scene.paint.Color;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.EvosimException;

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
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Prey.class, name = "prey"),
        @JsonSubTypes.Type(value = Predator.class, name = "predator")
})
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@SuppressWarnings({"checkstyle:ParameterNumber"})
public abstract class Entity {

    /**
     * Number of nodes on the second layer.
     */
    private static final int SECOND_LAYER_NODES_NUMBER = 10;

    /**
     * Number of nodes on the third layer.
     */
    private static final int THIRD_LAYER_NODES_NUMBER = 2;

    /**
     * An array of sensors represented by custom Line objects.
     */
    private final Line[] sensors;

    /**
     * A list of detected distances from the sensors.
     */
    private final double[] inputs;

    /**
     * Distance from left.
     */
    private static final int INPUTS_LEFT_OFFSET = -1;

    /**
     * Distance from top.
     */
    private static final int INPUTS_TOP_OFFSET = -2;

    /**
     * Distance from right.
     */
    private static final int INPUTS_RIGHT_OFFSET = -3;

    /**
     * Distance from bottom.
     */
    private static final int INPUTS_BOTTOM_OFFSET = -4;

    /**
     * The fixed entity speed randomly chosen at birth for an entity.
     */
    private final double speed;

    /**
     * The position of the entity.
     */
    private final Circle body;

    /**
     * The angle of the field of view cone of this entity in degrees.
     */
    private final double fovAngleInRadians;

    /**
     * The color of the entity.
     */
    @JsonIgnore
    private final Color color;

    /**
     * The birth time of the entity.
     */
    @JsonIgnore
    private final long birthTime;

    /**
     * Simulation this entity is in.
     */
    @Getter(AccessLevel.PROTECTED)
    @JsonIgnore
    @Setter
    private Simulation simulation;

    /**
     * If the entity is dead or not.
     */
    @Setter
    private boolean dead;

    /**
     * If the entity split.
     */
    @Setter
    private boolean split;

    /**
     * The neural network of the entity.
     * <p>
     * The network is passed input data from the entity's sensors and outputs data that will be
     * used to make it's next decision.
     * </p>
     */
    @Setter
    private NeuralNetwork brain;

    /**
     * The current amount of energy this Entity has left.
     * <p>
     * Energy is drained slowly whenever the entity moves. It is a double value within a range of
     * 0 to 1, where at 0 the the entity cannot move.
     * </p>
     */
    @Setter
    private double energy;

    /**
     * The current amount of split energy this Entity has accumulated.
     * <p>
     * Split energy determines whether or not the entity can multiply yet. It is bounded between
     * 0 to 1, where at 1 the entity will multiply.
     * </p>
     */
    @Setter
    private double splitEnergy;

    /**
     * The direction the entity is facing in radians.
     */
    private double directionAngleInRadians;

    /**
     * The number of children born from this entity.
     */
    @Setter
    private int childCount;

    /**
     * Constructs a new Entity.
     *
     * @param newSimulation    Simulation for the entity to be created in.
     * @param entitySpeed      The speed of the entity.
     * @param entityPosition   The position of the entity.
     * @param newViewAngle     The view angle of the entity.
     * @param newRotationAngle The rotation angle of the entity.
     * @param newCol            The color of the entity.
     */
    protected Entity(final Simulation newSimulation, final double entitySpeed,
        final Point entityPosition, final double newViewAngle,
        final double newRotationAngle, final Color newCol) throws EvosimException {
        this.simulation = newSimulation;
        this.birthTime = 0L;
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
                List.of(inputCount, SECOND_LAYER_NODES_NUMBER, THIRD_LAYER_NODES_NUMBER));

        // initialize sensors
        this.sensors = new Line[sensorCount];
        for (int i = 0; i < sensorCount; i++) {
            this.sensors[i] = new Line(0, 0, 0, 0);
        }
        this.inputs = new double[inputCount];
        Arrays.fill(this.inputs, Configuration.getConfiguration().getEntitySensorsLength());
    }

    /**
     * Construct a new entity from a JSON.
     * @param newSpeed                     The speed of entity.
     * @param newFovAngleInRadians         The angle in degrees of entity.
     * @param newDirectionAngleInRadians   The direction angle in radians of entity.
     * @param newColor                     The color of entity.
     * @param newSensors                   The sensors of entity.
     * @param newInputs                    The sensors data of entity.
     * @param newBody                      The body of entity.
     * @param newDead                      If dead of the entity.
     * @param newSplit                     The split energy of entity.
     * @param newBrain                     The brain of entity.
     * @param newEnergy                    The energy of entity.
     * @param newSplitEnergy               The split energy of entity.
     * @param newChildCount                The child count of entity.
     */
    protected Entity(
            final double newSpeed,
            final double newFovAngleInRadians,
            final double newDirectionAngleInRadians,
            final Color newColor,
            final Line[] newSensors,
            final double[] newInputs,
            final Circle newBody,
            final boolean newDead,
            final boolean newSplit,
            final NeuralNetwork newBrain,
            final double newEnergy,
            final double newSplitEnergy,
            final int newChildCount) {
        this.birthTime = 0L;
        this.simulation = null;
        this.color = newColor;
        this.speed = newSpeed;
        this.directionAngleInRadians = newDirectionAngleInRadians;
        this.fovAngleInRadians = newFovAngleInRadians;
        this.sensors = newSensors;
        this.inputs = newInputs;
        this.body = newBody;
        this.dead = newDead;
        this.split = newSplit;
        this.brain = newBrain;
        this.energy = newEnergy;
        this.splitEnergy = newSplitEnergy;
        this.childCount = newChildCount;
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
    public abstract void onUpdate() throws EvosimException;

    /**
     * Moves the entity according to the given movement speed and its current rotation angle.
     * <p>
     * If the entity moves off the map, it will wrap around to the other side. The energy of the
     * entity will be drained by the amount of movement.
     * </p>
     *
     * @param movementSpeed the speed of the movement.
     */
    public void move(final double movementSpeed) throws EvosimException {
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
        this.energy = Math.max(0,
                Configuration.getConfiguration().getEntityEnergyDrainRate() * movementSpeed);
    }

    /**
     * Processes data from this entity's sensors and moves according to its decision.
     */
    public final void update() throws EvosimException {
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

        this.inputs[this.inputs.length + INPUTS_LEFT_OFFSET] = xPos;
        this.inputs[this.inputs.length + INPUTS_TOP_OFFSET] = yPos;
        this.inputs[this.inputs.length + INPUTS_RIGHT_OFFSET] =
            this.simulation.getMapSizeX() * this.simulation.getGridSize() - xPos;

        this.inputs[this.inputs.length + INPUTS_BOTTOM_OFFSET] =
            this.simulation.getMapSizeY() * this.simulation.getGridSize() - yPos;

        final double[] calculatedDecision =
                this.brain.calcNetworkDecision(this.inputs);

        // Assuming the first output is the rotation
        // of the direction of the entity, and the second output is the speed.
        this.directionAngleInRadians += Configuration.getConfiguration()
                .getEntityMaxRotationSpeed() * (calculatedDecision[0] * 2 - 1);
        this.move(this.speed * calculatedDecision[1]);
    }

    /**
     * Determines if this entity collides with another entity.
     *
     * @param other Entity to be checked.
     * @return If both entities collide.
     */
    public boolean collidesWith(final Entity other) throws EvosimException {
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

    private void updateSensors(final double distance, final double angle) throws EvosimException {
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

    protected abstract void onCollideHandler(Entity other) throws EvosimException;

    /**
     * Event when this entity collides with another.
     *
     * @param other The entity that has been collided into.
     */
    public void onCollide(final Entity other) throws EvosimException {
        this.onCollideHandler(other);
    }

    @JsonIgnore
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
        return (int) ((currentTime - this.birthTime));
    }
}
