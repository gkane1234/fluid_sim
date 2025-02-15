package com.github.gkane1234.fluidsimulation;

import javax.swing.SwingUtilities;

public class SimulationManager {
    private FluidSimulator simulator;
    private FluidGPU renderer;
    private boolean running = true;
    private SwingMenu menu;

    public SimulationManager() {
        this.simulator = new FluidSimulator();
        this.renderer = new FluidGPU(simulator);


        this.menu = new SwingMenu(simulator);
            

    }

    public void run() {
        System.err.println("Running simulation...");
        while (!renderer.shouldClose()) {
            // Run simulation

            for (int i = 0; i < simulator.getSubSteps(); i++) {
                simulator.timeStep();
            }
            
            renderer.render();
            //try {
            //    Thread.sleep((long)(simulator.getTimeStep() * 1000));
            //} catch (InterruptedException e) {
            //    break;
            //}
        }
        
        renderer.cleanup();
    }
}
