package com.github.gkane1234.fluidsimulation.Kernels;

public class ViscosityKernel extends KernelObject {

    public String getType() {
        return "Viscosity";
    }

    public double kernel(double distance, double smoothingWidth) {
        return 15/(2*Math.PI*Math.pow(smoothingWidth, 3))*(-distance*distance*distance/(2*smoothingWidth*smoothingWidth*smoothingWidth)+distance*distance/(smoothingWidth*smoothingWidth)-distance/(2*smoothingWidth)-1);
    }

    public double kernelDerivative(double distance, double smoothingWidth) {
        return 15/(2*Math.PI*Math.pow(smoothingWidth, 3))*(-3*distance*distance/(2*smoothingWidth*smoothingWidth*smoothingWidth)+2*distance/(smoothingWidth*smoothingWidth)-2/smoothingWidth);
     }

    public double kernelSecondDerivative(double distance, double smoothingWidth) {
        return 45/(Math.PI*Math.pow(smoothingWidth, 6))*(smoothingWidth-distance);
    }
}

