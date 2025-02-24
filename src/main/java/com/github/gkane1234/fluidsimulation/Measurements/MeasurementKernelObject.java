package com.github.gkane1234.fluidsimulation.Measurements;

import com.github.gkane1234.fluidsimulation.Kernels.KernelObject;
import com.github.gkane1234.fluidsimulation.Particle;

public abstract class MeasurementKernelObject extends MeasurementObject {
    protected KernelObject kernel;

    public MeasurementKernelObject(String name, KernelObject kernel, boolean changingMeasurement) {
        super(name, changingMeasurement);
        this.kernel = kernel;
    }

    public abstract double calculateMeasurement(Particle p, Particle neighbor, double smoothingWidth);

    public double calculateMeasurement(Particle p) {
        throw new UnsupportedOperationException("A MeasurementKernelObject must be calculated with a neighbor and distance");
    }
}
