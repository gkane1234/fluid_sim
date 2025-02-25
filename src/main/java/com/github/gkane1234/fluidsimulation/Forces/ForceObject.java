package com.github.gkane1234.fluidsimulation.Forces;

import com.github.gkane1234.fluidsimulation.Particle;
import com.github.gkane1234.fluidsimulation.Vector2D;
import com.github.gkane1234.fluidsimulation.Measurements.MeasurementSet;
import com.github.gkane1234.fluidsimulation.Measurements.MeasurementObject;
import com.github.gkane1234.fluidsimulation.Constants.Variable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public abstract class ForceObject {
    protected MeasurementSet measurements;
    protected Map<String, Variable> variables;

    public ForceObject() {
        this.measurements = new MeasurementSet();
        this.variables = new HashMap<>();
    }

    public void setVariable(String name, double value) {
        this.variables.get(name).setValue(value);
    }

    public List<MeasurementObject> getMeasurements() {
        return measurements.getMeasurements();
    }

    public void addMeasurement(MeasurementObject measurement) {
        measurements.addMeasurement(measurement);
    }

    public List<Variable> getVariables() {
        return new ArrayList<>(variables.values());
    }

    public abstract String getType();
    public abstract Vector2D calculateForce(Particle p);


}
