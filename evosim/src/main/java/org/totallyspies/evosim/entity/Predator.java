package org.totallyspies.evosim.entity;

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
     * The angle between each sensor.
     */
    public static final double ANGLE_BETWEEN_SENSORS =
            Predator.VIEW_ANGLE / (Entity.SENSORS_COUNT - 1);
    /**
     * The split energy that the predator will gain when it eats a prey.
     */
    public static final double SPLIT_ENERGY_FILLING_SPEED = 0.5d;

    /**
     * Creates a new predator with the given speed.
     *
     * @param speed the speed of the predator
     */
    public Predator(final double speed) {
        super(speed);
    }

    /**
     * Creates a new predator with the given speed and position.
     *
     * @param speed    the speed of the predator
     * @param position the position of the predator
     */
    public Predator(final double speed, final Point position) {
        super(speed, position);
    }

    /**
     * This method will be called when the predator is updated.
     */
    @Override
    public void onUpdate() {

    }

    /**
     * Adjust the sensors of the predator.
     * The sensors will be adjusted on every update,
     * according to the Entity's position and rotation angle.
     */
    @Override
    public void adjustSensors() {
        for (int i = 0; i < Entity.SENSORS_COUNT; i++) {
            double angle = this.getRotationAngleInRadians()
                    + Math.toRadians(-Predator.VIEW_ANGLE / 2
                    + Predator.ANGLE_BETWEEN_SENSORS * i);
            this.getSensors()[i].getStartPoint()
                    .setPositionX(this.getPosition().getPositionX());
            this.getSensors()[i].getStartPoint()
                    .setPositionY(this.getPosition().getPositionY());
            this.getSensors()[i].getEndPoint()
                    .setPositionX(this.getPosition().getPositionX()
                            + Math.cos(angle) * Entity.SENSORS_LENGTH);
            this.getSensors()[i].getEndPoint()
                    .setPositionY(this.getPosition().getPositionY()
                            + Math.sin(angle) * Entity.SENSORS_LENGTH);
        }
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
                new Point(this.getPosition().getPositionX(),
                        this.getPosition().getPositionY()));

        predator.setRotationAngleInRadians(this.getRotationAngleInRadians());

        //predator.brain = this.brain.cloneAndMutate();
        return predator;
    }


    /**
     * If the predator collides with a prey, it will gain energy.
     * If the predator has enough energy, it will split.
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

    /**
     * Remove the predator from the list of entities.
     */
    @Override
    public void onDie() {
        // TODO remove the predator from the list of entities
    }
}
