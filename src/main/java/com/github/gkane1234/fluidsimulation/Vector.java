package com.github.gkane1234.fluidsimulation;

public class Vector {
    private double[] components;

    public Vector(double[] components) {
        this.components = components;
    }

    public Vector(double x, double y) {
        this.components = new double[] { x, y };
    }

    public Vector(double x, double y, double z) {
        this.components = new double[] { x, y, z };
    }

    public Vector(int dimensions) {
        this.components = new double[dimensions];
    }


    public double getX() {
        return components[0];
    }

    public double getY() {
        if (components.length < 2) {
            throw new IllegalArgumentException("Vector does not have a y-component");
        }
        return components[1];
    }

    public double getZ() {
        if (components.length < 3) {
            throw new IllegalArgumentException("Vector does not have a z-component");
        }
        return components[2];
    }

    public void setX(double x) {
        components[0] = x;
    }

    public void setY(double y) {  
        if (components.length < 2) {
            throw new IllegalArgumentException("Vector does not have a y-component");
        }
        components[1] = y;
    }   

    public void setZ(double z) {
        if (components.length < 3) {
            throw new IllegalArgumentException("Vector does not have a z-component");
        }
        components[2] = z;
    }

    public double[] getComponents() {
        return components;
    }

    public double getComponent(int index) {
        return components[index];
    }

    public void setComponent(int index, double value) {
        components[index] = value;
    }

    public Vector add(Vector other) {
        Vector result = new Vector(components.length);
        for (int i = 0; i < components.length; i++) {
            result.setComponent(i, components[i] + other.getComponent(i));
        }
        return result;
    }

    public double dotProduct(Vector other) {
        double result = 0;
        for (int i = 0; i < components.length; i++) {
            result += components[i] * other.getComponent(i);
        }
        return result;
    }

    public Vector subtract(Vector other) {
        Vector result = new Vector(components.length);
        for (int i = 0; i < components.length; i++) {
            result.setComponent(i, components[i] - other.getComponent(i));
        }
        return result;
    }

    public double magnitude() {
        double result = 0;
        for (int i = 0; i < components.length; i++) {
            result += components[i] * components[i];
        }
        return Math.sqrt(result);
    }

    public Vector normalize() {
        double magnitude = magnitude();
        if (magnitude == 0) {
            return zeroVector(components.length);
        }
        Vector result = new Vector(components.length);
        for (int i = 0; i < components.length; i++) {
            result.setComponent(i, components[i] / magnitude);
        }
        return result;
    }

    public Vector project(Vector other) {
        double dotProduct = dotProduct(other);
        Vector otherNormalized = other.normalize();
        return otherNormalized.multiply(dotProduct);
    }

    public double distanceTo(Vector other) {
        return subtract(other).magnitude();
    }

    public Vector multiply(double scalar) {
        Vector result = new Vector(components.length);
        for (int i = 0; i < components.length; i++) {
            result.setComponent(i, components[i] * scalar);
        }
        return result;
    }

    public Vector rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector(new double[] { getComponent(0) * cos - getComponent(1) * sin, getComponent(0) * sin + getComponent(1) * cos });
    }

    public static Vector zeroVector(int dimensions) {
        return new Vector(new double[dimensions]);
    }

    @Override
    public String toString() {
        String result = "(";
        for (int i = 0; i < components.length; i++) {
            result += components[i];
            if (i < components.length - 1) {
                result += ", ";
            }
        }
        return result + ")";
    }
}
