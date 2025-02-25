package com.github.gkane1234.fluidsimulation.Measurements;

import com.github.gkane1234.fluidsimulation.Kernels.KernelObject;
import com.github.gkane1234.fluidsimulation.Particle;
import com.github.gkane1234.fluidsimulation.Kernels.PressureKernel;
public class DensityMeasurement extends MeasurementKernelObject {
    private static DensityMeasurement instance;
    public DensityMeasurement(KernelObject kernel) {
        super("Density", kernel, true,true);
    }

    public static DensityMeasurement getInstance() {
        if (instance == null) {
            instance = new DensityMeasurement();
        }
        return instance;
    }

    public DensityMeasurement() {
        this(new PressureKernel());
    }

    @Override
    public double calculateMeasurement(Particle p, Particle neighbor, double smoothingWidth) {
        if (neighbor == p) {
            return 1;
        }
        double distance = p.getPosition().distanceTo(neighbor.getPosition());
        double kernelValue = kernel.kernel(distance, smoothingWidth);
        return neighbor.getMass() * kernelValue;
    }

}