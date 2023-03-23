package org.totallyspies.evosim.entities;

import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.simulation.SimulationApp;

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
    public static final double VIEW_ANGLE = 300.0d;

    /**
     * The split energy gained by not moving.
     */
    public static final double SPLIT_ENERGY_FILLING_SPEED = 0.5d;

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
     */
    @Override
    public void onUpdate() {
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

    /**
     * Handles collision between this prey and another Entity.
     * <p>
     * If the prey collides with a predator, it will be eaten and die.
     * </p>
     */
    @Override
    public void onCollide() {
        this.onDie();
    }

    /**
     * Splits this prey by cloning it and adding the new one to the list
     * of entities.
     */
    @Override
    public void onSplit() {
        SimulationApp.ENTITY_LIST.add(this.clone());
    }
}
