package org.totallyspies.evosim.entity;

public class Predator extends Entity {

    public final static double VIEW_ANGLE = 60.0d;
    public final static double ANGLE_BETWEEN_SENSORS = Predator.VIEW_ANGLE / (Entity.SENSORS_COUNT - 1);
    public final static double SPLIT_ENERGY_FILLING_SPEED = 0.5d;

    public Predator(double speed) {
        super(speed);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void adjustSensors() {
        for (int i = 0; i < Entity.SENSORS_COUNT; i++) {
            double angle = this.rotationAngleInRadians
                    + Math.toRadians(-Predator.VIEW_ANGLE / 2
                    + Predator.ANGLE_BETWEEN_SENSORS * i);
            this.sensors[i][0] = this.positionX;
            this.sensors[i][1] = this.positionY;
            this.sensors[i][2] = this.positionX + Math.cos(angle) * Entity.SENSORS_LENGTH;
            this.sensors[i][3] = this.positionY + Math.sin(angle) * Entity.SENSORS_LENGTH;
        }
    }

    /**
     * Clone the predator and mutate its speed.
     *
     * @return the cloned predator
     */
    @Override
    public Predator clone() {
        Predator predator = new Predator(
                (Math.random() < Entity.SPEED_MUTATION_RATE)
                        ? Math.random()*Entity.MAX_SPEED : this.speed);
        predator.positionX = this.positionX;
        predator.positionY = this.positionY;
        predator.rotationAngleInRadians = this.rotationAngleInRadians;
        //predator.brain = this.brain.cloneAndMutate();
        return predator;
    }


    /**
     * If the predator collides with a prey, it will gain energy.
     * If the predator has enough energy, it will split.
     *
     * @param entity the entity that the predator collided with
     */
    @Override
    public void onCollide(Entity entity) {
        if (entity instanceof Prey)
            this.energy += Predator.SPLIT_ENERGY_FILLING_SPEED;

        if (this.energy > 1) {
            this.energy -= 1;
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
