package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.geometry.Point;

/**
 * This class represents a prey in the simulation.
 * It is an Entity that can be eaten by predators.
 *
 * @author EnYi
 */
public class Prey extends Entity {

    /**
     * The view angle of the prey.
     */
    public static final double VIEW_ANGLE = 300.0d;

    /**
     * The split energy that the prey will gain when it
     * is not moving.
     */
    public static final double SPLIT_ENERGY_FILLING_SPEED = 0.5d;

    /**
     * Creates a new prey with the given speed and position.
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
     * This method will be called when the prey is updated.
     */
    @Override
    public void onUpdate() {
    }

    /**
     * Clone the prey and mutate its speed.
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

    /**
     * If the prey collides with a predator, it will die.
     */
    @Override
    public void onCollide() {
        // TODO remove the prey from the list of entities
    }

    /**
     * Split the prey into two preys.
     * Add the new prey to the list of entities.
     */
    @Override
    public void onSplit() {
        Prey prey = this.clone();
        // TODO add the prey to the list of entities
    }
}
