package com.github.gkane1234.fluidsimulation.Kernels;

public class Poly6Kernel extends KernelObject {

    public String getType() {
        return "Poly6";
    }

    public double kernel(double distance, double smoothingWidth) {
        return 315/(64*Math.PI*Math.pow(smoothingWidth, 9))*Math.pow(smoothingWidth*smoothingWidth-distance*distance, 3);
     }

    public double kernelDerivative(double distance, double smoothingWidth) {
        return -945/(32*Math.PI*Math.pow(smoothingWidth, 9))*distance*(Math.pow(smoothingWidth*smoothingWidth-distance*distance, 2));
    }

    public double kernelSecondDerivative(double distance, double smoothingWidth) {
        return 945/(16*Math.PI*Math.pow(smoothingWidth, 9))*(2*distance*distance*(smoothingWidth*smoothingWidth-distance*distance)-Math.pow(smoothingWidth*smoothingWidth-distance*distance, 2));
    }
}
