package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.geometry.Point;

/**
 * A Predator is a member of the evolution simulation that survives by
 * hunting prey. To multiply, a predator must eat a certain number of prey
 * by colliding with them to accumulate split energy. To die, a predator must
 * run out of energy, which decreases upon every movement.
 *
 * @author EnYi, Matthew
 */
public class Predator extends Entity {

    /**
     * Constructs a new predator.
     *
     * @param speed                  the speed of the predator
     * @param position               the position of the predator
     * @param rotationAngleInRadians the rotation angle of the predator
     */
    public Predator(
        final double speed,
        final Point position,
        final double rotationAngleInRadians
    ) {
        super(
            speed,
            position,
            Configuration.getConfiguration().getPredatorViewAngle(),
            rotationAngleInRadians
        );
    }

    /**
     * Determines if this predator should split or die based on its collision
     * and energy.
     */
    @Override
    public void onUpdate() {
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
                ? Math.random() * Configuration.getConfiguration()
                .getEntityMaxSpeed() : this.getSpeed(),
            new Point(
                this.getBodyCenter().getX(),
                this.getBodyCenter().getY()
            ),
            this.getDirectionAngleInRadians()
        );

        // TODO copy the brain of the prey

        this.setChildCount(this.getChildCount() + 1);
        return predator;
    }

    /**
     * Handles collision between this predator and another Entity.
     * <p>
     * If the predator collides with a prey, it will eat it and
     * accumulate split energy. If doing so provides enough energy, it will
     * split.
     * </p>
     */
    @Override
    public void onCollide() {
        this.setEnergy(this.getEnergy()
            + Configuration
            .getConfiguration()
            .getPredatorSplitEnergyFillingSpeed()
        );

        if (this.getEnergy() > 1) {
            this.setEnergy(this.getEnergy() - 1);
            this.onSplit();
        }

    }

    /**
     * Splits this predator by cloning it and adding the new one to the list
     * of entities.
     */
    @Override
    public void onSplit() {
        Predator predator = this.clone();
        // TODO add the predator to the list of entities

    }
}
