package org.totallyspies.evosim.entity;

import org.totallyspies.evosim.neuralnetwork.NeuralNetwork;

/**
 * @author EnYi
 */
public abstract class Entity {

    public static final double ENTITY_RADIUS = 15.0d;
    public static final int SENSORS_COUNT = 30;

    public static final double SENSORS_LENGTH = 1000.0d;


    protected double[][] sensors;

    final protected double speed;
    final protected NeuralNetwork brain;
    protected double positionX;
    protected double positionY;
    protected double rotationAngleInRadians;
    protected int childCount;

    public Entity(double speed) {
        this.brain = new NeuralNetwork();
        this.speed = speed;

        //randomize initial rotation angle
        this.rotationAngleInRadians = Math.random() * 2 * Math.PI;

        //initialize sensors
        this.sensors = new double[Entity.SENSORS_COUNT][4];
        adjustSensors();

    }

    public void move(double distance){
        this.positionX += Math.cos(this.rotationAngleInRadians) * distance;
        this.positionY += Math.sin(this.rotationAngleInRadians) * distance;
    }

    public abstract void onUpdate();

    public abstract void adjustSensors();


    public void update() {

        this.adjustSensors();
        //compute sensors value
        //get output of neural network
        this.move(speed);
        this.onUpdate();
    }

    public boolean checkCollide(Entity entity) {
        double distance = Math.sqrt(Math.pow(this.positionX - entity.positionX, 2)
                + Math.pow(this.positionY - entity.positionY, 2));
        return (distance < Entity.ENTITY_RADIUS * 2) ? true : false;
    }

    public double[] getSensorValues(){
        double[] sensorValues = new double[Entity.SENSORS_COUNT];
        for (int i = 0; i < Entity.SENSORS_COUNT; i++) {
            sensorValues[i] = this.sensors[i][4];
        }
        return sensorValues;
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



}
