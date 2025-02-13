package com.github.gkane1234.fluidsimulation;

import javax.swing.*;
import java.util.function.Consumer;
import java.awt.FlowLayout;
import java.io.*;
import java.util.Properties;

public class VariableSlider {
    private JSlider slider;
    private JLabel label;
    private double minValue;
    private double maxValue;
    private double currentValue;
    private String variableName;
    private Consumer<Double> updateFunction;

    public VariableSlider(String name, double min, double max, double initial, Consumer<Double> updateFunction) {
        this.minValue = min;
        this.maxValue = max;
        this.currentValue = initial;
        this.variableName = name;
        this.updateFunction = updateFunction;

        // Create slider with 100 steps between min and max
        slider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(100 * (initial - min) / (max - min)));
        slider.addChangeListener(e -> {
            currentValue = minValue + (maxValue - minValue) * slider.getValue() / 100.0;
            String format = (Math.abs(currentValue) > 1000 || (Math.abs(currentValue) < 0.01 && currentValue != 0)) ? "%.2e" : "%.2f";
            label.setText(String.format("%s: " + format, variableName, currentValue));
            updateFunction.accept(currentValue);
        });

        String format = (Math.abs(initial) > 1000 || (Math.abs(initial) < 0.01 && initial != 0)) ? "%.2e" : "%.2f";
        label = new JLabel(String.format("%s: " + format, variableName, initial));
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        
        JPanel sliderContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton minusButton = new JButton("-");
        minusButton.addActionListener(e -> {
            minValue /= 10;
            maxValue /= 10;
            double newValue = currentValue;
            setValue(newValue);
        });
        
        JButton plusButton = new JButton("+");
        plusButton.addActionListener(e -> {
            minValue *= 10;
            maxValue *= 10;
            double newValue = currentValue;
            setValue(newValue);
        });
        
        sliderContainer.add(minusButton);
        sliderContainer.add(slider);
        sliderContainer.add(plusButton);
        
        panel.add(sliderContainer);
        return panel;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setValue(double value) {
        currentValue = Math.max(minValue, Math.min(maxValue, value));
        slider.setValue((int)(100 * (currentValue - minValue) / (maxValue - minValue)));
    }

    public void saveValue(File file) {
        try {
            Properties props = new Properties();
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                props.load(in);
                in.close();
            }
            props.setProperty(variableName, String.valueOf(currentValue));
            FileOutputStream out = new FileOutputStream(file);
            props.store(out, "Slider Values");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadValue(File file) {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(file);
            props.load(in);
            in.close();
            String value = props.getProperty(variableName);
            if (value != null) {
                setValue(Double.parseDouble(value));
                updateFunction.accept(currentValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
