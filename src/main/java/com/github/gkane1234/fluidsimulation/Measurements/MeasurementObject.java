package com.github.gkane1234.fluidsimulation.Measurements;

import com.github.gkane1234.fluidsimulation.Particle;


/**
 * A class that represents a measurement object.
 * This class is used to measurements such as density and pressure.
 * Density and pressure uses kernels and are MeasurementKernelObjects, however other measurements may not.
 */
public abstract class MeasurementObject {
    private String name;
    protected double constant;
    protected boolean changingMeasurement;



    public MeasurementObject(String name, boolean changingMeasurement, double constant) {

        this.name = name;
        this.constant = constant;
        this.changingMeasurement = changingMeasurement;
    }

    public MeasurementObject(String name, boolean changingMeasurement) {
        this(name, changingMeasurement, 1);
    }

    public String getName() {
        return name;
    }

    public boolean isChangingMeasurement() {
        return changingMeasurement;
    }

    public double getConstant() {
        return constant;
    }   

    public void setConstant(double constant) {
        this.constant = constant;
    }

    public abstract double calculateMeasurement(Particle p);

}
