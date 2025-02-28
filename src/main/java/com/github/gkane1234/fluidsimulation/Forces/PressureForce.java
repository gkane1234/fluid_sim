package com.github.gkane1234.fluidsimulation.Forces;

import com.github.gkane1234.fluidsimulation.Particle;
import com.github.gkane1234.fluidsimulation.Vector;
import com.github.gkane1234.fluidsimulation.Kernels.KernelObject;
import com.github.gkane1234.fluidsimulation.Measurements.DensityMeasurement;



public class PressureForce extends GridForce {
    private final double PRESSURE_FACTOR_MULTIPLIER = 1;

    public PressureForce(KernelObject kernel, double forceConstant) {
        super(kernel, forceConstant,"Pressure Factor");
        this.addMeasurement(DensityMeasurement.getInstance());
        //this.measurements.add(PressureMeasurement.getInstance());

    }

    public PressureForce(KernelObject kernel) {
        this(kernel, 1);
    }

    @Override
    public Vector calculateForce(Particle p, Particle neighbor, double smoothingWidth) {

        double distance = p.getPosition().distanceTo(neighbor.getPosition());
        double pressureMagnitude = PRESSURE_FACTOR_MULTIPLIER*neighbor.getMass()*(getForceConstant()*(neighbor.getMeasurement("Density")+p.getMeasurement("Density"))/(2*neighbor.getMeasurement("Density")));
        double pressureDerivative = kernel.kernelDerivative(distance, smoothingWidth);
        Vector directionOfParticle = neighbor.getPosition().subtract(p.getPosition()).normalize();
        Vector pressureForce = directionOfParticle.multiply(pressureDerivative*pressureMagnitude);

        return pressureForce;

    }
    @Override
    public String getType() {
        return "Pressure";
    }
    
    

}
