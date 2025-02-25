package com.github.gkane1234.fluidsimulation.Kernels;

public abstract class KernelObject {
    public abstract String getType();

    public KernelObject() {
    }

    public double kernel(double distance, double smoothingWidth) {
        if (distance > smoothingWidth) {
            return 0;
        }
        return kernelInside(distance, smoothingWidth);
    }

    public double kernelDerivative(double distance, double smoothingWidth) {
        if (distance > smoothingWidth) {
            return 0;
        }
        return kernelDerivativeInside(distance, smoothingWidth);
    }

    public double kernelSecondDerivative(double distance, double smoothingWidth) {
        if (distance > smoothingWidth) {
            return 0;
        }
        return kernelSecondDerivativeInside(distance, smoothingWidth);
    }

    protected abstract double kernelInside(double distance, double smoothingWidth);
    protected abstract double kernelDerivativeInside(double distance, double smoothingWidth);
    protected abstract double kernelSecondDerivativeInside(double distance, double smoothingWidth);

}
