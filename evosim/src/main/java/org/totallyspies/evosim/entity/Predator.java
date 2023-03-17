package org.totallyspies.evosim.entity;

public class Predator extends Entity{

    public final static double VIEW_ANGLE = 60.0d;
    public final static double ANGLE_BETWEEN_SENSORS = Predator.VIEW_ANGLE/(Entity.SENSORS_COUNT-1);

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
                    + Math.toRadians(- Predator.VIEW_ANGLE / 2
                    + Predator.ANGLE_BETWEEN_SENSORS * i);
            this.sensors[i][0] = this.positionX;
            this.sensors[i][1] = this.positionY;
            this.sensors[i][2] = this.positionX + Math.cos(angle) * Entity.SENSORS_LENGTH;
            this.sensors[i][3] = this.positionY + Math.sin(angle) * Entity.SENSORS_LENGTH;
        }
    }
}
