package com.github.gkane1234.fluidsimulation.Measurements;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class MeasurementSet {
    private Map<String, MeasurementObject> measurements;

    public MeasurementSet() {
        this.measurements = new HashMap<>();
    }

    public void addMeasurement(MeasurementObject measurement) {
        measurements.put(measurement.getName(), measurement);
    }
    
    public MeasurementObject getMeasurement(String name) {
        return measurements.get(name);
    }


    public List<MeasurementObject> getMeasurements() {
        return new ArrayList<>(measurements.values());
    }

    
}
