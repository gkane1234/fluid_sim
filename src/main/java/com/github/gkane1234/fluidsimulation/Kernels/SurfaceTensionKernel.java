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

    public double kernelInside(double distance, double smoothingWidth) {
        return gaussianKernel.kernelInside(distance, smoothingWidth);
    }

    public double kernelDerivativeInside(double distance, double smoothingWidth) {
        return gaussianKernel.kernelDerivativeInside(distance, smoothingWidth);
    }

    public double kernelSecondDerivativeInside(double distance, double smoothingWidth) {
        return gaussianKernel.kernelSecondDerivativeInside(distance, smoothingWidth);
    }
}