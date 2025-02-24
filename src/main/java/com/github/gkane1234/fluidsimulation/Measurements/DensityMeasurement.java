package com.github.gkane1234.fluidsimulation.Measurements;

import com.github.gkane1234.fluidsimulation.Kernels.KernelObject;
import com.github.gkane1234.fluidsimulation.Kernels.GaussianKernel;
import com.github.gkane1234.fluidsimulation.Particle;
public class DensityMeasurement extends MeasurementKernelObject {
    private static DensityMeasurement instance;
    public DensityMeasurement(KernelObject kernel) {
        super("Density", kernel, true);
    }

    public static DensityMeasurement getInstance() {
        if (instance == null) {
            instance = new DensityMeasurement(new GaussianKernel());
        }
        return instance;
    }

    public DensityMeasurement() {
        this(new GaussianKernel());
    }

    @Override
    public double calculateMeasurement(Particle p, Particle neighbor, double smoothingWidth) {
        double distance = p.getPosition().distanceTo(neighbor.getPosition());
        return kernel.kernel(distance, smoothingWidth);
    }

}