package org.totallyspies.evosim.entity;

public class Predator extends Entity{

    public final static double ANGLE_BETWEEN_SENSORS = 60.0/Entity.SENSORS_COUNT;
    public Predator(double speed) {
        super(speed, Prey.ANGLE_BETWEEN_SENSORS);
    }

    @Override
    public void move(double distance) {

    }

    @Override
    public void onUpdate(int x, float[] fl) {

    }
}
