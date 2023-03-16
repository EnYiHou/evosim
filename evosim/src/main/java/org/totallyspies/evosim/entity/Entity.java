package org.totallyspies.evosim.entity;

import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;

/**
 * @author EnYi
 */
public abstract class Entity {

    public static final double ENTITY_RADIUS = 15.0d;
    public static final int SENSORS_COUNT = 30;

    final private NeuralNetwork brain;
    private double positionX;
    private double positionY;
    final private double speed;
    final private double angleBetweenSensors;
    private double rotationAngleInRadians;
    private int childCount;

    public Entity(double speed, double angleBetweenSensors) {
        this.brain = new NeuralNetwork();
        this.speed = speed;
        this.angleBetweenSensors = angleBetweenSensors;

    }

    public abstract void move(double distance);

    public abstract void onUpdate(int x, float[] fl);

    public void update(int x) {
    }

    public boolean checkCollide(Entity entity) {
        return false;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public void generateSensors(){

    }


}
