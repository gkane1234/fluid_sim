package com.github.gkane1234.fluidsimulation;

import java.awt.Point;

public class Attractor {
    private Point position; 
    private double strength;
    private double radius;
    public Attractor(Point position, double strength, double radius) {
        this.position = position;
        this.strength = strength;
        this.radius = radius;
    }

    public Point getPosition() {
        return position;
    }

    public double getStrength() {
        return strength;
    }

    public double getRadius() {
        return radius;
    }
}