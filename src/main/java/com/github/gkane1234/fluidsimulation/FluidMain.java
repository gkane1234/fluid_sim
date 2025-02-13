package com.github.gkane1234.fluidsimulation;

import javax.swing.*;

public class FluidMain {
    public static void main(String[] args) {
        FluidSimulator simulator = new FluidSimulator();
        FluidDisplay display = new FluidDisplay(simulator);
        JFrame frame = new JFrame("Fluid Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(display);
        frame.pack();
        frame.setVisible(true);
        display.run();
    }
}