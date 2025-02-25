package com.github.gkane1234.fluidsimulation.Kernels;

public class PressureKernel extends KernelObject {

    public String getType() {
        return "Pressure";
    }

    public double kernelInside(double distance, double smoothingWidth) {
        return 15/(Math.PI*Math.pow(smoothingWidth, 6))*Math.pow(smoothingWidth-distance, 3);
    }

    public double kernelDerivativeInside(double distance, double smoothingWidth) {
        return -45/(Math.PI*Math.pow(smoothingWidth, 6))*(Math.pow(smoothingWidth-distance, 2));
    }

    public double kernelSecondDerivativeInside(double distance, double smoothingWidth) {
        return 90/(Math.PI*Math.pow(smoothingWidth, 6))*(smoothingWidth-distance);
    }
}
