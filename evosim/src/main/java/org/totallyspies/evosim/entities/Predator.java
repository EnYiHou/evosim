package org.totallyspies.evosim.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.totallyspies.evosim.simulation.Simulation;
import org.totallyspies.evosim.geometry.Circle;
import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.utils.EvosimException;


/**
 * A Predator is a member of the evolution simulation that survives by hunting prey. To multiply,
 * a predator must eat a certain number of prey by colliding with them to accumulate split energy.
 * To die, a predator must run out of energy, which decreases upon every movement.
 *
 * @author EnYi, Matthew
 */
@SuppressWarnings({"checkstyle:ParameterNumber"})
public final class Predator extends Entity {

    /**
     * The paint colour of the entity's body.
     */
    @Getter
    private static Color bodyColour;

    /**
     * Constructs a new predator.
     *
     * @param newSimulation          Simulation for the entity to be created in.
     * @param speed                  the speed of the predator
     * @param position               the position of the predator
     * @param rotationAngleInRadians the rotation angle of the predator
     */
    public Predator(final Simulation newSimulation,
                    final double speed,
                    final Point position,
                    final double rotationAngleInRadians) throws EvosimException {
        super(newSimulation, speed, position,
            Configuration.getConfiguration().getPredatorViewAngle(), rotationAngleInRadians);
        Predator.bodyColour = Configuration.getConfiguration().getColorPredator();
    }

    /**
     * Construct an Predator from a JSON.
     *
     * @param inputs                  the sensors data of the predator.
     * @param speed                   the speed of the predator.
     * @param body                    the body of the predator.
     * @param fovAngleInRadians       the fov angle in radians of the predator.
     * @param dead                    if dead of the predator.
     * @param split                   the split of the predator.
     * @param brain                   the brain of the predator.
     * @param energy                  the energy of the predator.
     * @param splitEnergy             the split energy of the predator.
     * @param directionAngleInRadians the direction angle in radians of the predator.
     * @param childCount              the child count of the predator.
     */
    @JsonCreator
    public Predator(
        @JsonProperty("inputs") final double[] inputs,
        @JsonProperty("speed") final double speed,
        @JsonProperty("body") final Circle body,
        @JsonProperty("fovAngleInRadians") final double fovAngleInRadians,
        @JsonProperty("dead") final boolean dead,
        @JsonProperty("split") final boolean split,
        @JsonProperty("brain") final NeuralNetwork brain,
        @JsonProperty("energy") final double energy,
        @JsonProperty("splitEnergy") final double splitEnergy,
        @JsonProperty("directionAngleInRadians") final double directionAngleInRadians,
        @JsonProperty("childCount") final int childCount
    ) throws EvosimException {
        super(
            speed,
            fovAngleInRadians,
            directionAngleInRadians,
            inputs,
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
     * Determines if this predator should split or die based on its collision and energy.
     * <p>
     * If this Predator has run out of energy, it will die.
     * If this Predator has enough split energy, it will clone itself.
     * </p>
     */
    @Override
    public void onUpdate() throws EvosimException {
        this.setEnergy(this.getEnergy() - Configuration.getConfiguration()
            .getPredatorEnergyBaseDrainingSpeed());

        if (this.getEnergy() <= 0) {
            this.setDead(true);
        }
    }

    /**
     * Clones the predator and mutates its speed and neural network.
     *
     * @return the cloned predator
     */
    @Override
    public Predator clone() {
        // Mutate the speed of the predator
        Predator predator = null;
        try {
            predator = new Predator(
                this.getSimulation(),
                (Math.random()
                    < Configuration.getConfiguration().getEntitySpeedMutationRate())
                    ? Math.random() * Configuration.getConfiguration().getEntityMaxSpeed()
                    : this.getSpeed(),

                new Point(this.getBodyCenter().getX(), this.getBodyCenter().getY()),
                this.getDirectionAngleInRadians());
        } catch (EvosimException e) {
            throw new RuntimeException(e);
        }

        // mutate the brain of the predator
        predator.setBrain(this.getBrain().mutate());

        this.setChildCount(this.getChildCount() + 1);

        return predator;
    }

    @Override
    protected void onCollideHandler(final Entity other) throws EvosimException {
        this.setSplitEnergy(this.getSplitEnergy()
            + Configuration.getConfiguration().getPredatorSplitEnergyFillingSpeed());
        this.setEnergy(Math.min(1, this.getEnergy()
            + Configuration.getConfiguration().getPredatorEnergyFillingSpeed()));
    }

    /**
     * Set body color of the predators.
     * @param newColor
     */
    public static void setBodyColour(final Color newColor) {
        Configuration.getConfiguration().setColorPredator(newColor);
        Predator.bodyColour = newColor;
    }
}
