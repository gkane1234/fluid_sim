package com.github.gkane1234.fluidsimulation.gui;

import javax.swing.*;

import com.github.gkane1234.fluidsimulation.FluidSimulator;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SwingMenu extends JFrame {
    private FluidSimulator simulator;
    private List<VariableSlider> sliders;
    private List<DropDown> dropDowns;
    
    public SwingMenu(FluidSimulator simulator) {
        this.simulator = simulator;
        this.sliders = simulator.getVariableSliders();
        this.dropDowns = simulator.getDropDowns();
        
        setTitle("Fluid Simulation Controls");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        // Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        
        for (VariableSlider slider : sliders) {
            controlPanel.add(slider.getPanel());
        }

        for (DropDown dropdown : dropDowns) {
            controlPanel.add(dropdown.getPanel());
        }

        
        add(controlPanel, BorderLayout.EAST);
        pack();
        setVisible(true);
    }
    
    public List<VariableSlider> getSliders() {
        return sliders;
    }
    
    public List<DropDown> getDropDowns() {
        return dropDowns;
    }
}
