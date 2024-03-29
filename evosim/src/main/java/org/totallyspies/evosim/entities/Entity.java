package org.totallyspies.evosim.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
import org.totallyspies.evosim.neuralnetwork.Neuron;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.EvosimException;

import java.util.ArrayList;
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
     * Number of nodes on the third layer.
     */
    private static final int LAST_LAYER_NODES_NUMBER = 2;

    /**
     * In order to convert MILLISECONDS_TO_SECONDS.
     */
    private static final double SECONDS_TO_MILLISECONDS = 1000d;

    /**
     * A list of detected distances from the sensors.
     */
    private final double[] inputs;

    /**
     * Distance from left.
     */
    private static final int INPUTS_LEFT_OFFSET = 0;

    /**
     * Distance from top.
     */
    private static final int INPUTS_TOP_OFFSET = 1;

    /**
     * Distance from right.
     */
    private static final int INPUTS_RIGHT_OFFSET = 2;

    /**
     * Distance from bottom.
     */
    private static final int INPUTS_BOTTOM_OFFSET = 3;


    /**
     * Distance from bottom.
     */
    private static final int INPUTS_ENERGY_OFFSET = 4;

    /**
     * Bias for the position and energy inputs.
     */
    private static final double BIAS = -0.5;

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
     * The number of sensors of this entity.
     */
    @JsonIgnore
    private final int sensorCount;

    /**
     * Constructs a new Entity.
     *
     * @param newSimulation    Simulation for the entity to be created in.
     * @param entitySpeed      The speed of the entity.
     * @param entityPosition   The position of the entity.
     * @param newViewAngle     The view angle of the entity.
     * @param newRotationAngle The rotation angle of the entity.
     */
    protected Entity(final Simulation newSimulation, final double entitySpeed,
                     final Point entityPosition, final double newViewAngle,
                     final double newRotationAngle) throws EvosimException {

        this.simulation = newSimulation;
        this.birthTime = 0L;
        // initialize entity properties
        this.energy = 1d;
        this.splitEnergy = 0d;
        this.dead = false;
        this.split = false;
        this.childCount = 0;
        this.speed = entitySpeed;
        this.directionAngleInRadians = newRotationAngle;
        this.fovAngleInRadians = Math.toRadians(newViewAngle);

        Configuration config = Configuration.getConfiguration();

        this.body = new Circle(entityPosition, config.getEntityRadius());
        this.sensorCount = config.getEntitySensorsCount();

        // Inputs with distances from each side
        final int inputCount = this.sensorCount + 5;

        // initialize neural network

        List<Integer> layers = new ArrayList<>(List.of(inputCount));
        List<Integer> middleLayer = Configuration.getConfiguration().getLayerSizeMiddle();
        middleLayer.forEach(layer -> layers.add(layer));
        layers.add(LAST_LAYER_NODES_NUMBER);

        this.brain = new NeuralNetwork(layers);

        final double sensorLength = Configuration.getConfiguration().getEntitySensorsLength();
        final List<Neuron> firstLayer = this.brain.getNeuronLayers().get(0);
        firstLayer.subList(0, this.sensorCount).forEach(neuron -> {
            neuron.setClamp(sensorLength);
            neuron.setBias(-1);
        });

        firstLayer.get(this.sensorCount + INPUTS_LEFT_OFFSET).setClamp(
            this.simulation.getGridSize() * this.simulation.getMapSizeX()
        );
        firstLayer.get(this.sensorCount + INPUTS_RIGHT_OFFSET).setClamp(
            this.simulation.getGridSize() * this.simulation.getMapSizeX()
        );
        firstLayer.get(this.sensorCount + INPUTS_TOP_OFFSET).setClamp(
            this.simulation.getGridSize() * this.simulation.getMapSizeY()
        );
        firstLayer.get(this.sensorCount + INPUTS_BOTTOM_OFFSET).setClamp(
            this.simulation.getGridSize() * this.simulation.getMapSizeY()
        );

        firstLayer.subList(this.sensorCount, inputCount).forEach(neuron -> neuron.setBias(BIAS));

        this.inputs = new double[inputCount];
    }

    /**
     * Construct a new entity from a JSON.
     *
     * @param newSpeed                   The speed of entity.
     * @param newFovAngleInRadians       The angle in degrees of entity.
     * @param newDirectionAngleInRadians The direction angle in radians of entity.
     * @param newInputs                  The sensors data of entity.
     * @param newBody                    The body of entity.
     * @param newDead                    If dead of the entity.
     * @param newSplit                   The split energy of entity.
     * @param newBrain                   The brain of entity.
     * @param newEnergy                  The energy of entity.
     * @param newSplitEnergy             The split energy of entity.
     * @param newChildCount              The child count of entity.
     */
    protected Entity(
        final double newSpeed,
        final double newFovAngleInRadians,
        final double newDirectionAngleInRadians,
        final double[] newInputs,
        final Circle newBody,
        final boolean newDead,
        final boolean newSplit,
        final NeuralNetwork newBrain,
        final double newEnergy,
        final double newSplitEnergy,
        final int newChildCount) throws EvosimException {

        this.birthTime = 0L;
        this.simulation = null;
        this.speed = newSpeed;
        this.directionAngleInRadians = newDirectionAngleInRadians;
        this.fovAngleInRadians = newFovAngleInRadians;
        this.inputs = newInputs;
        this.body = newBody;
        this.dead = newDead;
        this.split = newSplit;
        this.brain = newBrain;
        this.energy = newEnergy;
        this.splitEnergy = newSplitEnergy;
        this.childCount = newChildCount;
        this.sensorCount = Configuration.getConfiguration().getEntitySensorsCount();
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

        double remainingEnergy = this.energy - Configuration.getConfiguration()
            .getEntityEnergyDrainRate() * movementSpeed;

        if (remainingEnergy >= 0) {
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
            this.energy = Math.max(0, remainingEnergy);
        }
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

        this.inputs[this.sensorCount + INPUTS_LEFT_OFFSET] = xPos;
        this.inputs[this.sensorCount + INPUTS_TOP_OFFSET] = yPos;
        this.inputs[this.sensorCount + INPUTS_RIGHT_OFFSET] =
            this.simulation.getMapSizeX() * this.simulation.getGridSize() - xPos;

        this.inputs[this.sensorCount + INPUTS_BOTTOM_OFFSET] =
            this.simulation.getMapSizeY() * this.simulation.getGridSize() - yPos;

        this.inputs[this.sensorCount + INPUTS_ENERGY_OFFSET] = this.energy;

        final double[] calculatedDecision =
            this.brain.calcNetworkDecision(this.inputs);

        this.directionAngleInRadians += Configuration.getConfiguration()
            .getEntityMaxRotationSpeed() * (calculatedDecision[0]);

        this.move(this.speed * Math.abs(calculatedDecision[1]));
    }

    /**
     * Updates the relation (collision and sensors) between two entities.
     *
     * @param a First entity to update.
     * @param b Second entity to update.
     */
    public static void updateRelation(final Entity a, final Entity b) throws EvosimException {
        if (a.getClass().equals(b.getClass()) || a.dead || b.dead) {
            return;
        }

        final double distance = Formulas.distance(
            a.getBodyCenter().getX(),
            a.getBodyCenter().getY(),
            b.getBodyCenter().getX(),
            b.getBodyCenter().getY()
        );

        if (distance < Configuration.getConfiguration().getEntitySensorsLength()) {
            a.updateSensors(b);
            b.updateSensors(a);
        }

        if (distance < a.getBody().getRadius() + b.getBody().getRadius()) {
            a.onCollide(b);
            b.onCollide(a);
        }
    }

    private void updateSensors(final Entity other) {
        final double baseAngle = this.directionAngleInRadians - (this.fovAngleInRadians / 2);

        for (int i = 0; i < this.sensorCount; ++i) {
            final double angle = baseAngle + i * (this.getFovAngleInRadians() / this.sensorCount);

            final double distance = Formulas.distanceCircleAngled(
                this.getBodyCenter(), angle, other.getBody()
            );

            this.inputs[i] = Math.min(
                this.inputs[i],
                distance
            );
        }
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

    /**
     * Returns an array of lines of the sensors coming out of the entity.
     *
     * @return The lines of length of the sensors.
     */
    @JsonIgnore
    public Line[] getSensors() {
        final Line[] sensors = new Line[this.sensorCount];

        for (int i = 0; i < sensors.length; ++i) {
            sensors[i] = getSensor(i);
        }

        return sensors;
    }

    /**
     * Returns line of the sensor coming out of entity at index.
     * @param sensorIndex The index of the sensor.
     * @return The line of the length of the sensor.
     */
    public Line getSensor(final int sensorIndex) {
        final double angle = getSensorAngle(sensorIndex);

        return new Line(
            this.getBodyCenter().getX(),
            this.getBodyCenter().getY(),
            this.getBodyCenter().getX() + this.inputs[sensorIndex] * Math.cos(angle),
            this.getBodyCenter().getY() + this.inputs[sensorIndex] * Math.sin(angle)
        );
    }

    /**
     * Gets distance for sensor.
     * @param sensorIndex The index to get the distance from.
     * @return The distance of the sensor.
     */
    public double getSensorDistance(final int sensorIndex) {
        if (sensorIndex >= this.sensorCount) {
            throw new IllegalArgumentException("Sensor index out of range");
        }

        return this.inputs[sensorIndex];
    }

    /**
     * Gets angle for sensor.
     * @param sensorIndex The index to get an angle from.
     * @return Angle of index in radians.
     */
    public double getSensorAngle(final int sensorIndex) {
        if (sensorIndex >= this.sensorCount) {
            throw new IllegalArgumentException("Sensor index out of range");
        }

        final double baseAngle = this.getDirectionAngleInRadians() - (this.fovAngleInRadians / 2);

        return baseAngle + sensorIndex * (this.getFovAngleInRadians() / this.sensorCount);
    }

    /**
     * Resets sensors to their default length.
     */
    public void resetSensors() throws EvosimException {
        Arrays.fill(
            this.inputs,
            0,
            this.sensorCount,
            Configuration.getConfiguration().getEntitySensorsLength()
        );
    }
}
