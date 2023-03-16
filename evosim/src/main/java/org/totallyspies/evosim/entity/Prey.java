package org.totallyspies.evosim.entity;

public class Prey extends Entity {

    public final static double ANGLE_BETWEEN_SENSORS = 300.0/Entity.SENSORS_COUNT;
    public Prey(double speed) {
        super(speed, Prey.ANGLE_BETWEEN_SENSORS);
    }

    @Override
    public void move(double distance) {

    }

    @Override
    public void onUpdate(int x, float[] fl) {

    }
}
