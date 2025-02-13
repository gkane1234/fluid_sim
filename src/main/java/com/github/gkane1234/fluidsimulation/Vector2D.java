package com.github.gkane1234.fluidsimulation;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.getX(), y + other.getY());
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public double dotProduct(Vector2D other) {
        return x * other.getX() + y * other.getY();
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.getX(), y - other.getY());
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        double magnitude = magnitude();
        if (magnitude == 0) {
            return new Vector2D(0, 0);
        }
        return new Vector2D(x / magnitude, y / magnitude);
    }

    public Vector2D rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    public Vector2D project(Vector2D other) {
        double magnitude = other.magnitude();
        return other.normalize().multiply(dotProduct(other) / magnitude);
    }

    public double distanceTo(Vector2D other) {
        return Math.sqrt(Math.pow(x - other.getX(), 2) + Math.pow(y - other.getY(), 2));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    
}
