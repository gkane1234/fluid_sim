package com.github.gkane1234.fluidsimulation.Kernels;

public class GaussianKernel extends KernelObject {

    public String getType() {
        return "Gaussian";
    }

    public double kernelInside(double distance, double smoothingWidth) {
        double PI_TO_THE_THREE_HALVES = Math.pow(Math.PI, 1.5);
        return (1/(PI_TO_THE_THREE_HALVES*smoothingWidth*smoothingWidth*smoothingWidth))*Math.exp(-distance*distance/(smoothingWidth*smoothingWidth));
    }

    public double kernelDerivativeInside(double distance, double smoothingWidth) {
        double PI_TO_THE_THREE_HALVES = Math.pow(Math.PI, 1.5);
        return (1/(PI_TO_THE_THREE_HALVES*smoothingWidth*smoothingWidth*smoothingWidth))*(-2*distance/Math.pow(smoothingWidth, 2))*Math.exp(-distance*distance/(smoothingWidth*smoothingWidth));
    }

    public double kernelSecondDerivativeInside(double distance, double smoothingWidth) {
        double PI_TO_THE_THREE_HALVES = Math.pow(Math.PI, 1.5);
        return (1/(PI_TO_THE_THREE_HALVES*smoothingWidth*smoothingWidth*smoothingWidth))*Math.exp(-distance*distance/(smoothingWidth*smoothingWidth))*((-2/Math.pow(smoothingWidth, 2))+(4*distance*distance/Math.pow(smoothingWidth, 4)));
    }
}
