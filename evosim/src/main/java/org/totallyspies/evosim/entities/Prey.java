package org.totallyspies.evosim.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.scene.paint.Color;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.geometry.Point;


/**
 * A Prey is member of the evolution simulation that survives by evading collision with predators.
 * To multiply, a prey must remain stationary to accumulate split energy. To die, a prey must
 * collide with a predator and get eaten.
 *
 * @author EnYi, Matthew
 */
public final class Prey extends Entity {

    /**
     * Constructs a new prey.
     *
     * @param speed                  the speed of the prey
     * @param position               the position of the prey
     * @param rotationAngleInRadians the rotation angle of the prey
     * @param birthTime              the time the prey was born
     */
    public Prey(final double speed,
                final Point position,
                final double rotationAngleInRadians,
                final long birthTime) {
        super(speed, position, birthTime, Configuration.getConfiguration().getPreyViewAngle(),
                rotationAngleInRadians, Color.GREEN
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
     * @return the cloned prey
     */
    @Override
    public Prey clone() {
        // Mutate the speed of the prey
        Prey prey = new Prey(
                (Math.random()
                        < Configuration.getConfiguration().getEntitySpeedMutationRate())
                        ? Math.random() * Configuration.getConfiguration().getEntityMaxSpeed()
                        : this.getSpeed(),
                new Point(this.getBodyCenter().getX(), this.getBodyCenter().getY()),
                this.getDirectionAngleInRadians(),
                System.currentTimeMillis());


        // mutate the brain of the prey
        prey.setBrain(this.getBrain().mutate());
        this.setChildCount(this.getChildCount() + 1);

        return prey;
    }

    @Override
    protected void onCollideHandler(final Entity other) {
        this.setDead(true);
    }

    @Override
    public String toString() {
        return "Prey";
    }
}
