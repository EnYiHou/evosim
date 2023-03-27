package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.geometry.Point;

/**
 * A Predator is a member of the evolution simulation that survives by
 * hunting prey. To multiply, a predator must eat a certain number of prey
 * by colliding with them to accumulate split energy. To die, a predator must
 * run out of energy, which decreases upon every movement.
 *
 * @author EnYi, Matthew
 */
public final class Predator extends Entity {

    /**
     * The angle of the predator's view cone.
     */
    public static final double VIEW_ANGLE = 60.0;

    /**
     * The split energy gained upon eating prey.
     */
    public static final double SPLIT_ENERGY_FILLING_SPEED = 0.5;

    /**
     * The energy regained after eating a prey.
     */
    public static final double ENERGY_FILLING_SPEED = 0.5;

    /**
     * The energy lost rate.
     */
    public static final double ENERGY_BASE_DRAINING_SPEED = 0.1;


    /**
     * Constructs a new predator.
     *
     * @param speed    the speed of the predator
     * @param position the position of the predator
     * @param rotationAngleInRadians the rotation angle of the predator
     */
    public Predator(final double speed,
                    final Point position,
                    final double rotationAngleInRadians) {
        super(speed, position,
                Predator.VIEW_ANGLE,
                rotationAngleInRadians);
    }

    /**
     * Determines if this predator should split or die based on its collision
     * and energy.
     * <p>
     * If this Predator has run out of energy, it will die.
     * If this Predator has enough split energy, it will clone itself.
     * </p>
     */
    @Override
    public void onUpdate() {
        this.setEnergy(this.getEnergy() - ENERGY_BASE_DRAINING_SPEED);

        // collision with prey
        if (checkCollisions()) {
            this.setSplitEnergy(this.getSplitEnergy()
                    + SPLIT_ENERGY_FILLING_SPEED);
            this.setEnergy(Math.min(1, this.getEnergy()
                    + ENERGY_FILLING_SPEED));
        }

        if (this.getEnergy() <= 0) {
            this.setDeath(true);
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
                (Math.random() < Entity.SPEED_MUTATION_RATE)
                        ? Math.random() * Entity.MAX_SPEED : this.getSpeed(),
                new Point(this.getBodyCenter().getX(),
                        this.getBodyCenter().getY()),
                this.getDirectionAngleInRadians());

        // TODO copy the brain of the prey

        this.setChildCount(this.getChildCount() + 1);
        return predator;
    }
}
