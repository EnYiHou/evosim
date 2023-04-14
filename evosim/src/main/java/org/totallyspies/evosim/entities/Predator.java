package org.totallyspies.evosim.entities;

import javafx.scene.paint.Color;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.geometry.Point;

import java.util.Date;

/**
 * A Predator is a member of the evolution simulation that survives by hunting prey. To multiply,
 * a predator must eat a certain number of prey by colliding with them to accumulate split energy.
 * To die, a predator must run out of energy, which decreases upon every movement.
 *
 * @author EnYi, Matthew
 */
public final class Predator extends Entity {

    /**
     * Constructs a new predator.
     *
     * @param speed                  the speed of the predator
     * @param position               the position of the predator
     * @param rotationAngleInRadians the rotation angle of the predator
     */
    public Predator(final double speed, final Point position, final double rotationAngleInRadians, final long birthTime) {
        super(speed, position, birthTime, Configuration.getConfiguration().getPredatorViewAngle(),
                rotationAngleInRadians, Color.RED);
    }

    /**
     * Determines if this predator should split or die based on its collision and energy.
     * <p>
     * If this Predator has run out of energy, it will die.
     * If this Predator has enough split energy, it will clone itself.
     * </p>
     */
    @Override
    public void onUpdate() {
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
        Predator predator = new Predator(
                (Math.random()
                        < Configuration.getConfiguration().getEntitySpeedMutationRate())
                        ? Math.random() * Configuration.getConfiguration().getEntityMaxSpeed()
                        : this.getSpeed(),

                new Point(this.getBodyCenter().getX(), this.getBodyCenter().getY()),
                this.getDirectionAngleInRadians(), System.currentTimeMillis());
        // mutate the brain of the predator
        predator.setBrain(this.getBrain().mutate());

        this.setChildCount(this.getChildCount() + 1);
        return predator;
    }

    @Override
    protected void onCollideHandler(final Entity other) {
        this.setSplitEnergy(this.getSplitEnergy()
                + Configuration.getConfiguration().getPredatorSplitEnergyFillingSpeed());
        this.setEnergy(Math.min(1, this.getEnergy()
                + Configuration.getConfiguration().getPredatorEnergyFillingSpeed()));
    }
}
