package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.math.Formulas;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;
import org.totallyspies.evosim.simulation.Simulation;

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
    public static final double SENSORS_LENGTH = 100.0;

    /**
     * The maximum speed that can be chosen for an entity during mutation.
     */
    public static final double MAX_SPEED = 0.00001;

    /**
     * The minimum speed that can be chosen for an entity during mutation.
     */
    public static final double MIN_SPEED = 0.0;

    /**
     * A constant used to mutate the entities speed during reproduction.
     */
    protected static final double SPEED_MUTATION_RATE = 0.2;

    /**
     * The speed at which the energy of the entity will be drained.
     */
    public static final double ENERGY_DRAIN_RATE = 0.01;

    /**
     * The maximum speed at which the entity can rotate.
     */
    public static final double MAX_ROTATION_SPEED = 0.002;

    /**
     * An array of sensors represented by custom Line objects.
     */
    private Line[] sensors;

    /**
     * A list of detected distances from the sensors.
     */
    private Double[] sensorsData;

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
     * The x index of the grid this entity is in.
     */
    private int gridX;

    /**
     * The y index of the grid this entity is in.
     */
    private int gridY;

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
    protected Entity(final double entitySpeed,
                     final Point entityPosition,
                     final double newViewAngle,
                     final double newRotationAngle) {

        // initialize entity properties
        this.energy = 1.0;
        this.splitEnergy = 0.0;
        this.death = false;
        this.split = false;
        this.childCount = 0;
        this.speed = entitySpeed;
        this.directionAngleInRadians = newRotationAngle;
        this.fovAngleInDegrees = newViewAngle;
        this.body = new Circle(entityPosition, Entity.ENTITY_RADIUS);

        // initialize neural network
        this.brain = new NeuralNetwork(
                Arrays.stream(new int[]{SENSORS_COUNT, 10, 2})
                        .boxed().collect(ArrayList::new,
                                ArrayList::add,
                                ArrayList::addAll));

        // initialize sensors
        this.sensors = new Line[Entity.SENSORS_COUNT];
        for (int i = 0; i < Entity.SENSORS_COUNT; i++) {
            this.sensors[i] = new Line(0, 0, 0, 0);
        }
        this.sensorsData = new Double[Entity.SENSORS_COUNT];
        adjustSensors();
    }

    /**
     * Moves the entity according to the given movement speed and its current
     * rotation angle.
     * If the entity moves off the map, it will wrap around to the other side.
     * <p>
     * The energy of the entity will be drained by the amount of movement.
     *
     * @param movementSpeed the speed of the movement.
     */
    public void move(final double movementSpeed) {
        Point position = this.body.getCenter();

        // wrap around the map
        double positionX = (position.getX()
                + Math.cos(this.directionAngleInRadians) * movementSpeed)
                % Simulation.MAP_WIDTH;
        double positionY = (position.getY()
                + Math.sin(this.directionAngleInRadians) * movementSpeed)
                % Simulation.MAP_HEIGHT;
        if (positionX < 0) {
            positionX += Simulation.MAP_WIDTH;
        }
        if (positionY < 0) {
            positionY += Simulation.MAP_HEIGHT;
        }

        position.setX(positionX);
        position.setY(positionY);

        // drain energy
        this.energy -= Entity.ENERGY_DRAIN_RATE * movementSpeed;
    }

    /**
     * Adjusts this entity's sensors based on its position and its direction.
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
     * Processes data from this entity's sensors and moves according to its
     * decision.
     */
    public final void update() {
        this.adjustSensors();
        this.onUpdate();
        if (this.splitEnergy > 1) {
            this.splitEnergy -= 1;
            this.split = true;
        }
        double[] calculatedDecision =
                this.brain.calcNetworkDecision(Arrays.asList(this.sensorsData));

        // Assuming the first output is the rotation
        // of the direction of the entity, and the second output is the speed.
        this.directionAngleInRadians += Entity.MAX_ROTATION_SPEED
                * calculatedDecision[0];
        this.move(this.speed * calculatedDecision[1]);

    }

    /**
     * Checks if this entity is colliding with other entities
     * within the nearby grids.
     *
     * @return true if this entity is colliding with another entity.
     * false otherwise.
     */
    public final boolean checkCollisions() {

        Arrays.fill(this.sensorsData, Entity.SENSORS_LENGTH);
        int nearbyGrids = 1;
        int startingGridX = (this.gridX - nearbyGrids) <= 0
                ? 0 : this.gridX - nearbyGrids;
        int startingGridY = (this.gridY - nearbyGrids) <= 0
                ? 0 : this.gridY - nearbyGrids;
        int endingGridX = (this.gridX + nearbyGrids) >= Simulation.GRID_X
                ? Simulation.GRID_X : this.gridX + nearbyGrids;
        int endingGridY = (this.gridY + nearbyGrids) >= Simulation.GRID_Y
                ? Simulation.GRID_Y : this.gridY + nearbyGrids;

        for (int i = startingGridX; i < endingGridX; i++) {
            for (int j = startingGridY; j < endingGridY; j++) {
                for (Entity entity : Simulation.GRIDS.get(i).get(j)) {
                    if (!entity.getClass().equals(this.getClass())) {
                        double distance =
                                Formulas.distance(this.getBodyCenter().getX(),
                                        this.getBodyCenter().getY(),
                                        entity.getBodyCenter().getX(),
                                        entity.getBodyCenter().getY());
                        if (distance < Entity.ENTITY_RADIUS * 2) {
                            return true;
                        }
                        for (int sensorIndex = 0; sensorIndex
                                < this.sensors.length; sensorIndex++) {
                            Line sensor = this.sensors[sensorIndex];
                            Double distanceToEntity =
                                    Formulas.closestIntersection(sensor,
                                            entity.getBody());
                            if (distanceToEntity
                                    < this.sensorsData[sensorIndex]) {
                                this.sensorsData[sensorIndex] =
                                        distanceToEntity;
                            }
                        }
                    }
                }
            }
        }
        return false;
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
        return death;
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
}
