package com.github.gkane1234.fluidsimulation.Forces;

import com.github.gkane1234.fluidsimulation.Particle;
import com.github.gkane1234.fluidsimulation.Vector;
import com.github.gkane1234.fluidsimulation.Kernels.KernelObject;
import com.github.gkane1234.fluidsimulation.Measurements.DensityMeasurement;

public class ViscosityForce extends GridForce {
    private final double VISCOSITY_FACTOR_MULTIPLIER = 1;
    public ViscosityForce(KernelObject kernel, double forceConstant) {
        super(kernel, forceConstant,"Viscosity Factor");       
        this.addMeasurement(DensityMeasurement.getInstance());

    }

    public ViscosityForce(KernelObject kernel) {
        this(kernel, 1);
    }

    @Override
    public Vector calculateForce(Particle p, Particle neighbor, double smoothingWidth) {
        double distance = p.getPosition().distanceTo(neighbor.getPosition());
        double viscosityMagnitude = VISCOSITY_FACTOR_MULTIPLIER*getForceConstant()*neighbor.getMass()*neighbor.getVelocity().subtract(p.getVelocity()).magnitude()/neighbor.getMeasurement("Density");
        double viscositySecondDerivative = kernel.kernelSecondDerivative(distance, smoothingWidth);
        Vector directionOfVelocity = neighbor.getVelocity().subtract(p.getVelocity()).normalize();
        Vector viscosityForce = directionOfVelocity.multiply(viscositySecondDerivative*viscosityMagnitude);
 
        return viscosityForce;

    }
    
    @Override
    public String getType() {
        return "Viscosity";
    }
    
}
