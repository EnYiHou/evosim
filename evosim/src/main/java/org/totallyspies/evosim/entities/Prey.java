package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.geometry.Point;

/**
 * A Prey is member of the evolution simulation that survives by evading
 * collision with predators. To multiply, a prey must remain stationary to
 * accumulate split energy. To die, a prey must collide with a predator and
 * get eaten.
 *
 * @author EnYi, Matthew
 */
public class Prey extends Entity {

    /**
     * The angle of the prey's view cone.
     */
    public static final double VIEW_ANGLE = 300.0;

    /**
     * The split energy gained passively for surviving.
     */
    public static final double SPLIT_ENERGY_FILLING_SPEED = 0.5;

    /**
     * The energy regained passively for surviving.
     */
    public static final double ENERGY_FILLING_SPEED = 0.005;

    /**
     * Constructs a new prey.
     *
     * @param speed    the speed of the prey
     * @param position the position of the prey
     * @param rotationAngleInRadians the rotation angle of the prey
     */
    public Prey(final double speed, final Point position,
                final double rotationAngleInRadians) {
        super(speed, position, Prey.VIEW_ANGLE, rotationAngleInRadians);
    }

    /**
     * Determines if this prey should split or die based on its collision and
     * energy.
     * <p>
     * If this Prey has run out of energy or collided a predator, it will die.
     * If this Prey has enough split energy, it will clone itself.
     * </p>
     */
    @Override
    public void onUpdate() {
        // collide with predator
        if (checkCollide() || this.getEnergy() <= 0) {
            this.die();
        }

        // passively gain energy
        this.setSplitEnergy(
                this.getSplitEnergy() + SPLIT_ENERGY_FILLING_SPEED > 1
                        ? 1 : this.getSplitEnergy()
                        + SPLIT_ENERGY_FILLING_SPEED);
        this.setEnergy(
                this.getEnergy() + ENERGY_FILLING_SPEED > 1
                        ? 1 : this.getEnergy() + ENERGY_FILLING_SPEED);

        // multiply
        if (this.getSplitEnergy() > 1) {
            this.split();
        }
    }

    /**
     * Clones the prey and mutates its speed and neural network.
     *
     * @return the cloned prey
     */
    @Override
    public Prey clone() {
        // Mutate the speed of the prey
        Prey prey = new Prey(
                (Math.random() < Entity.SPEED_MUTATION_RATE)
                        ? Math.random() * Entity.MAX_SPEED : this.getSpeed(),
                new Point(this.getBodyCenter().getX(),
                        this.getBodyCenter().getY()),
                this.getDirectionAngleInRadians());

        // TODO copy the brain of the prey

        this.setChildCount(this.getChildCount() + 1);

        return prey;
    }
}
