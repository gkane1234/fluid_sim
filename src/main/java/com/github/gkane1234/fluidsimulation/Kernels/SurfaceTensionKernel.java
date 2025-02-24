package com.github.gkane1234.fluidsimulation.Kernels;

public class SurfaceTensionKernel extends KernelObject {
    private GaussianKernel gaussianKernel;

    public String getType() {
        return "SurfaceTension (Gaussian)";
    }

    public SurfaceTensionKernel() {
        super();
        gaussianKernel = new GaussianKernel();
    }

    public double kernel(double distance, double smoothingWidth) {
        return gaussianKernel.kernel(distance, smoothingWidth);
    }

    public double kernelDerivative(double distance, double smoothingWidth) {
        return gaussianKernel.kernelDerivative(distance, smoothingWidth);
    }

    public double kernelSecondDerivative(double distance, double smoothingWidth) {
        return gaussianKernel.kernelSecondDerivative(distance, smoothingWidth);
    }
}