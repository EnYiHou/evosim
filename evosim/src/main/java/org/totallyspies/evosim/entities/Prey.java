package org.totallyspies.evosim.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.paint.Color;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.geometry.Line;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.geometry.Point;


/**
 * A Prey is member of the evolution simulation that survives by evading collision with predators.
 * To multiply, a prey must remain stationary to accumulate split energy. To die, a prey must
 * collide with a predator and get eaten.
 *
 * @author EnYi, Matthew
 */
@SuppressWarnings({"checkstyle:ParameterNumber"})
public final class Prey extends Entity {

    /**
     * Constructs a new prey.
     *
     * @param newSimulation    Simulation for the entity to be created in.
     * @param speed                  the speed of the prey
     * @param position               the position of the prey
     * @param rotationAngleInRadians the rotation angle of the prey
     */
    public Prey(final Simulation newSimulation,
        final double speed,
        final Point position,
        final double rotationAngleInRadians) {
        super(newSimulation, speed, position,
            Configuration.getConfiguration().getPredatorViewAngle(), rotationAngleInRadians,
            Color.GREEN);

    }

    /**
     * Construct a Prey from a JSON.
     * @param sensors                   the sensors of the prey.
     * @param sensorsData               the sensors data of the prey.
     * @param speed                     the speed of the prey.
     * @param body                      the body of the prey.
     * @param fovAngleInDegrees         the fov angle in degrees of the prey.
     * @param dead                      if dead of the prey.
     * @param split                     the split of the prey.
     * @param brain                     the brain of the prey.
     * @param energy                    the energy of the prey.
     * @param splitEnergy               the split energy of the prey.
     * @param directionAngleInRadians   the direction angle in radians of the prey.
     * @param childCount                the child count of the prey.
     */
    @JsonCreator
    public Prey(
            @JsonProperty("sensors") final Line[] sensors,
            @JsonProperty("sensorsData") final double[] sensorsData,
            @JsonProperty("speed") final double speed,
            @JsonProperty("body") final Circle body,
            @JsonProperty("fovAngleInDegrees") final double fovAngleInDegrees,
            @JsonProperty("dead") final boolean dead,
            @JsonProperty("split") final boolean split,
            @JsonProperty("brain") final NeuralNetwork brain,
            @JsonProperty("energy") final double energy,
            @JsonProperty("splitEnergy") final double splitEnergy,
            @JsonProperty("directionAngleInRadians") final double directionAngleInRadians,
            @JsonProperty("childCount") final int childCount
    ) {
        super(
                speed,
                fovAngleInDegrees,
                directionAngleInRadians,
                Color.GREEN,
                sensors,
                sensorsData,
                body,
                dead,
                split,
                brain,
                energy,
                splitEnergy,
                childCount
        );
    }

    /**
     * Determines if this prey should split or die based on its collision and energy.
     * <p>
     * If this Prey has run out of energy or collided a predator, it will die.
     * If this Prey has enough split energy, it will clone itself.
     * </p>
     */
    @Override
    public void onUpdate() {
        // passively gain energy
        this.setSplitEnergy(this.getSplitEnergy()
                + Configuration.getConfiguration().getPreySplitEnergyFillingSpeed());
        this.setEnergy(Math.min(this.getEnergy()
                + Configuration.getConfiguration().getPreyEnergyFillingSpeed(), 1));

    }

    /**
     * Clones the prey and mutates its speed and neural network.
     *
     * @param cloneTime the clone time in seconds
     *
     * @return the cloned prey
     */
    @Override
    public Prey clone() {
        // Mutate the speed of the prey
        Prey prey = new Prey(
                this.getSimulation(),
                (Math.random()
                        < Configuration.getConfiguration().getEntitySpeedMutationRate())
                        ? Math.random() * Configuration.getConfiguration().getEntityMaxSpeed()
                        : this.getSpeed(),
                new Point(this.getBodyCenter().getX(), this.getBodyCenter().getY()),
                this.getDirectionAngleInRadians());

        // mutate the brain of the prey
        prey.setBrain(this.getBrain().mutate());
        this.setChildCount(this.getChildCount() + 1);

        return prey;
    }

    @Override
    protected void onCollideHandler(final Entity other) {
        this.setDead(true);
    }
}
