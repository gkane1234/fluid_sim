package com.github.gkane1234.fluidsimulation.Forces;

import com.github.gkane1234.fluidsimulation.Particle;
import com.github.gkane1234.fluidsimulation.Vector2D;
import com.github.gkane1234.fluidsimulation.Kernels.KernelObject;
import com.github.gkane1234.fluidsimulation.Measurements.MeasurementObject;
import java.util.List;

public abstract class GridForce extends ForceObject {
    protected KernelObject kernel;


    public GridForce(KernelObject kernel, double forceConstant) {
        super();
        this.kernel = kernel;
        this.forceConstant = forceConstant;

    }
    public GridForce(KernelObject kernel) {
        this(kernel, 1);
    }

    public void setKernel(KernelObject kernel) {
        this.kernel = kernel;
    }

    public KernelObject getKernel() {
        return kernel;
    }

    public double getForceConstant() {
        return forceConstant;
    }

    public void setForceConstant(double forceConstant) {
        this.forceConstant = forceConstant;
    }


    public void addMeasurement(MeasurementObject measurement) {
        measurements.add(measurement);
    }

    public List<MeasurementObject> getMeasurements() {
        return measurements;
    }

    @Override
    public Vector2D calculateForce(Particle p) {
        throw new UnsupportedOperationException("A GridForce must be calculated with a neighbor and distance");
    }

    public abstract Vector2D calculateForce(Particle p, Particle neighbor,double smoothingWidth);

}
