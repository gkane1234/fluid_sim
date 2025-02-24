package com.github.gkane1234.fluidsimulation.Measurements;

import com.github.gkane1234.fluidsimulation.Particle;
public class PressureMeasurement extends MeasurementObject {
    private static PressureMeasurement instance;
    public PressureMeasurement(double pressureConstant) {
        super("Pressure", true, pressureConstant);
    }

    public static PressureMeasurement getInstance() {
        if (instance == null) {
            instance = new PressureMeasurement(1);
        }
        return instance;
    }
    
    @Override
    public double calculateMeasurement(Particle p) {
        return p.getMeasurement("Density")*constant;
    }
}
