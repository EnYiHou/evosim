package org.totallyspies.evosim.entities;

import javafx.scene.paint.Color;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.geometry.Point;

/**
 * A Prey is member of the evolution simulation that survives by evading collision with predators.
 * To multiply, a prey must remain stationary to accumulate split energy. To die, a prey must
 * collide with a predator and get eaten.
 *
 * @author EnYi, Matthew
 */
public class Prey extends Entity {

    /**
     * Constructs a new prey.
     *
     * @param speed                  the speed of the prey
     * @param position               the position of the prey
     * @param rotationAngleInRadians the rotation angle of the prey
     */
    public Prey(final double speed, final Point position, final double rotationAngleInRadians) {
        super(speed, position, Configuration.getCONFIGURATION().getPreyViewAngle(),
                rotationAngleInRadians
        );
        this.setColor(Color.GREEN);

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
        // collide with predator
        if (checkCollisions()) {
            this.setDeath(true);
        }

        // passively gain energy
        this.setSplitEnergy(this.getSplitEnergy()
                + Configuration.getCONFIGURATION().getPreySplitEnergyFillingSpeed());
        this.setEnergy(Math.min(this.getEnergy()
                + Configuration.getCONFIGURATION().getPreyEnergyFillingSpeed(), 1));
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
                (Math.random()
                        < Configuration.getCONFIGURATION().getEntitySpeedMutationRate())
                        ? Math.random() * Configuration.getCONFIGURATION().getEntityMaxSpeed()
                        : this.getSpeed(),
                new Point(this.getBodyCenter().getX(), this.getBodyCenter().getY()),
                this.getDirectionAngleInRadians()
        );

        // mutate the brain of the prey
        prey.setBrain(this.getBrain().mutate());
        this.setChildCount(this.getChildCount() + 1);

        return prey;
    }
}
