package org.totallyspies.evosim.entity;

import org.totallyspies.evosim.geometry.Point;

public class Prey extends Entity {

    /**
     * The view angle of the prey.
     */
    public static final double VIEW_ANGLE = 300.0d;
    /**
     * The angle between each sensor.
     */
    public static final double ANGLE_BETWEEN_SENSORS =
            Predator.VIEW_ANGLE / (Entity.SENSORS_COUNT - 1);
    /**
     * The split energy that the prey will gain when it
     * is not moving.
     */
    public static final double SPLIT_ENERGY_FILLING_SPEED = 0.5d;

    /**
     * Creates a new prey with the given speed.
     *
     * @param speed the speed of the prey
     */
    public Prey(final double speed) {
        super(speed);
    }

    /**
     * Creates a new prey with the given speed and position.
     *
     * @param speed    the speed of the prey
     * @param position the position of the prey
     */
    public Prey(final double speed, final Point position) {
        super(speed, position);
    }

    /**
     * This method will be called when the prey is updated.
     */
    @Override
    public void onUpdate() {

    }

    /**
     * Adjust the sensors of the prey.
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
     * Clone the prey and mutate its speed.
     *
     * @return the cloned prey
     */
    @Override
    public Predator clone() {
        // Mutate the speed of the prey
        Predator prey = new Predator(
                (Math.random() < Entity.SPEED_MUTATION_RATE)
                        ? Math.random() * Entity.MAX_SPEED : this.getSpeed(),
                new Point(this.getPosition().getPositionX(),
                        this.getPosition().getPositionY()));

        prey.setRotationAngleInRadians(this.getRotationAngleInRadians());
        // TODO copy the brain of the prey
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
        Predator prey = this.clone();
        // TODO add the prey to the list of entities

    }

    /**
     * Remove the prey from the list of entities.
     */
    @Override
    public void onDie() {
        // TODO remove the prey from the list of entities
    }
}
