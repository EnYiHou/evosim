package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.geometry.Point;

/**
 * This class represents a predator in the simulation.
 * It is an Entity that can eat prey.
 *
 * @author EnYi
 */
public class Predator extends Entity {

    /**
     * The view angle of the predator.
     */
    public static final double VIEW_ANGLE = 60.0d;

    /**
     * The split energy that the predator will gain when it eats a prey.
     */
    public static final double SPLIT_ENERGY_FILLING_SPEED = 0.5d;

    /**
     * Creates a new predator with the given speed and position.
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
     * This method will be called when the predator is updated.
     */
    @Override
    public void onUpdate() {
    }

    /**
     * Clone the predator and mutate its speed.
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

    /**
     * If the predator collides with a prey, it will gain energy.
     * If the predator gained enough energy, it will split.
     */
    @Override
    public void onCollide() {
        this.setEnergy(this.getEnergy()
                + Predator.SPLIT_ENERGY_FILLING_SPEED);
        if (this.getEnergy() > 1) {
            this.setEnergy(this.getEnergy() - 1);
            this.onSplit();
        }

    }

    /**
     * Split the predator into two predators.
     * Add the new predator to the list of entities.
     */
    @Override
    public void onSplit() {
        Predator predator = this.clone();
        // TODO add the predator to the list of entities

    }
}
