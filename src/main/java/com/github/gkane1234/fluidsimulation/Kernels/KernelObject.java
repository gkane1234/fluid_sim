package com.github.gkane1234.fluidsimulation.Kernels;

public abstract class KernelObject {
    public abstract String getType();

    public KernelObject() {
    }

    public abstract double kernel(double distance, double smoothingWidth);
    public abstract double kernelDerivative(double distance, double smoothingWidth);
    public abstract double kernelSecondDerivative(double distance, double smoothingWidth);

}
