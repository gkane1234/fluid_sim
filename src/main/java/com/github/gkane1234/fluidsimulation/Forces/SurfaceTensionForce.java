package com.github.gkane1234.fluidsimulation.Forces;

import com.github.gkane1234.fluidsimulation.Vector;
import com.github.gkane1234.fluidsimulation.Particle;
import com.github.gkane1234.fluidsimulation.Kernels.KernelObject;
import com.github.gkane1234.fluidsimulation.Measurements.DensityMeasurement;

public class SurfaceTensionForce extends GridForce {
    private final double SURFACE_TENSION_FACTOR_MULTIPLIER = 1;
    private double MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE = 1e-6;
    public SurfaceTensionForce(KernelObject kernel, double forceConstant) {
        super(kernel, forceConstant,"Surface Tension Factor");
        this.addMeasurement(DensityMeasurement.getInstance());
    }

    public SurfaceTensionForce(KernelObject kernel) {
        this(kernel, 1);
    }

    @Override
    public Vector calculateForce(Particle p, Particle neighbor, double smoothingWidth) {
        double distance = p.getPosition().distanceTo(neighbor.getPosition());
        Vector unnormalizedForceDirection = calculateUnnormalizedSurfaceTensionForceDirection(p, neighbor, distance, smoothingWidth);
        double forceMagnitude = calculateSurfaceTensionForceMagnitude(p, neighbor, distance, smoothingWidth);
        if (unnormalizedForceDirection.magnitude() < MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE) {
            return new Vector(0, 0);
        }


        return unnormalizedForceDirection.normalize().multiply(forceMagnitude);
    }
    

    private Vector calculateUnnormalizedSurfaceTensionForceDirection(Particle p, Particle neighbor,double distance, double smoothingWidth) {
        double surfaceTensionDerivative = kernel.kernelDerivative(distance, smoothingWidth);
        Vector directionOfSurfaceTension = neighbor.getPosition().subtract(p.getPosition()).normalize();
        return directionOfSurfaceTension.multiply(surfaceTensionDerivative);
    }

    private double calculateSurfaceTensionForceMagnitude(Particle p, Particle neighbor,double distance, double smoothingWidth) {
        double surfaceTensionSecondDerivative = kernel.kernelSecondDerivative(distance, smoothingWidth);
        double surfaceTensionMagnitude = -SURFACE_TENSION_FACTOR_MULTIPLIER*getForceConstant()*neighbor.getMass()/neighbor.getMeasurement("Density");
        return surfaceTensionSecondDerivative*surfaceTensionMagnitude;
    }

    @Override
    public String getType() {
        return "Surface Tension";
    }
    
}
