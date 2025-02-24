package com.github.gkane1234.fluidsimulation.Forces;

import com.github.gkane1234.fluidsimulation.Particle;
import com.github.gkane1234.fluidsimulation.Vector2D;
import com.github.gkane1234.fluidsimulation.Measurements.MeasurementObject;
import java.util.List;
import java.util.ArrayList;
public abstract class ForceObject {
    protected double forceConstant;
    protected List<MeasurementObject> measurements;

    public ForceObject() {
        this.measurements = new ArrayList<>();
    }

    public void setForceConstant(double forceConstant) {
        this.forceConstant = forceConstant;
    }

    public List<MeasurementObject> getMeasurements() {
        return measurements;
    }

    public abstract String getType();
    public abstract Vector2D calculateForce(Particle p);


}
