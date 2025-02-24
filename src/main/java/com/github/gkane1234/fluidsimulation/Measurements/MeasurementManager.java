package com.github.gkane1234.fluidsimulation.Measurements;

import com.github.gkane1234.fluidsimulation.Particle;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class MeasurementManager {
    private static MeasurementManager instance;
    private Map<String, Double> measurementCache;
    private List<MeasurementObject> measurements;


    public MeasurementManager() {
        this.measurementCache = new HashMap<>();
        this.measurements = new ArrayList<>();
    }

    public static MeasurementManager getInstance() {
        if (instance == null) {
            instance = new MeasurementManager();
        }
        return instance;
    }

    public void addMeasurement(MeasurementObject measurement) {
        measurements.add(measurement);
    }
    
    
    public Double getMeasurement(MeasurementKernelObject measurement, Particle p, double smoothingWidth) {
        String key = createKey(p, measurement.getName());
        if (measurementCache.containsKey(key)) {
                return measurementCache.get(key);
            }
        return null;
    }

    public void setMeasurement(MeasurementKernelObject measurement, Particle p, double smoothingWidth, double value) {
        String key = createKey(p, measurement.getName());
        measurementCache.put(key, value);
    }

    private String createKey(Particle p, String measurementType) {
        return p.hashCode() + "-" + measurementType;
    }

    public void clearCache() {
        measurementCache.clear();
    }
}
