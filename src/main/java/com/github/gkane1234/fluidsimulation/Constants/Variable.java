package com.github.gkane1234.fluidsimulation.Constants;

import com.github.gkane1234.fluidsimulation.gui.VariableSlider;
import java.util.function.Consumer;

public class Variable{
    private String name;
    private double value;
    private double[] bounds;

    public Variable(String name, double value, double[] bounds) {
        this.name = name;
        this.value = value;
        this.bounds = bounds;
    }

    public Variable(String name, double value, double min, double max) {
        this(name, value, new double[]{min, max});
    }

    public Variable(String name, double value) {
        this(name, value, 0, 100);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public VariableSlider createVariableSlider(Consumer<Double> updateFunction) {
        return new VariableSlider(name, bounds[0], bounds[1], value, updateFunction);
    }

    public VariableSlider createVariableSlider() {
        return new VariableSlider(name, bounds[0], bounds[1], value, (Double value) -> setValue(value));
    }
}