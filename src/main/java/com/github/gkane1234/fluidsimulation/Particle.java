package com.github.gkane1234.fluidsimulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Particle {
    private Vector2D position;
    private Vector2D velocity;
    private Vector2D force;
    private double mass;
    private double density;
    private double pressure;
    private Color color;
    private int[] gridPosition;
    

    public Particle(double x, double y) {
        this(new Vector2D(x, y), new Vector2D(0, 0), 1);
    }

    public Particle(double x, double y, double vx, double vy, double mass) {
        this(new Vector2D(x, y), new Vector2D(vx, vy), mass);
    }

    public Particle(Vector2D position, Vector2D velocity, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.color = new Color(0, 0, 0);
        this.density = 0;
        this.pressure = 0;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public Vector2D getPosition() {
        return position;
    }   

    public Vector2D getVelocity() {
        return velocity;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public void setGridPosition(int[] gridPosition) {
        //Random random = new Random(gridPosition[0]*gridPosition[1]+gridPosition[0]+gridPosition[1]);
        //this.color = new Color((int)(random.nextDouble()*255), (int)(random.nextDouble()*255), (int)(random.nextDouble()*255));
        this.gridPosition = gridPosition;
    }

    public int[] getGridPosition() {
        return gridPosition;
    }

    public Color getColor() {
        return color;
    }

    public void setColor() {
        Color stationaryColor = new Color(0, 0, 255);
        Color movingColor = new Color(255, 0, 0);
        int maxVelocity = 40;
        double velocityMagnitude = Math.min(maxVelocity, velocity.magnitude());
        this.color = new Color((int)(lerp(stationaryColor.getRed(), movingColor.getRed(), velocityMagnitude/maxVelocity)), (int)(lerp(stationaryColor.getGreen(), movingColor.getGreen(), velocityMagnitude/maxVelocity)), (int)(lerp(stationaryColor.getBlue(), movingColor.getBlue(), velocityMagnitude/maxVelocity)));
    }
    public double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    public void updatePosition(double timeStep) {
        position=position.add(velocity.multiply(timeStep));
    }

    public void applyForce(Vector2D force, double damping, double timeStep) {
        force = force.multiply(1/mass);
        velocity=velocity.add(force.multiply(timeStep));
        velocity=velocity.multiply(damping);
    }

    public void applyForce(Vector2D force, double timeStep) {
        applyForce(force,1, timeStep);
    }

    public void applyBoundaryConditions(int width, int height, double damping) {
        if (position.getX() < 0) {
            position=new Vector2D(0, position.getY());
            velocity=new Vector2D(-velocity.getX()*damping, velocity.getY());
        }
        if (position.getX() > width) {
            position=new Vector2D(width, position.getY());
            velocity=new Vector2D(-velocity.getX()*damping, velocity.getY());
        }
        if (position.getY() < 0) {
            position=new Vector2D(position.getX(), 0);
            velocity=new Vector2D(velocity.getX(), -velocity.getY()*damping);
        }
        if (position.getY() > height) {
            position=new Vector2D(position.getX(), height);
            velocity=new Vector2D(velocity.getX(), -velocity.getY()*damping);
        }
        
        
    }

}
