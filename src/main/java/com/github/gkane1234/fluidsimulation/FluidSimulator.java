package com.github.gkane1234.fluidsimulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.gkane1234.fluidsimulation.Forces.*;
import com.github.gkane1234.fluidsimulation.Kernels.*;
import com.github.gkane1234.fluidsimulation.Measurements.*;
import com.github.gkane1234.fluidsimulation.gui.*;
import com.github.gkane1234.fluidsimulation.Constants.*;

import java.util.concurrent.CountDownLatch;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.util.Arrays;
// have it so that the different forces can have their own kernels and specific smoothing distances
// allow for new forces and a way to save how they work
// create a kernel object
// create a force object
public class FluidSimulator {
    private int width;
    private int height;
    private int numParticles;
    private List<Particle> particles;
    private ArrayList<Particle>[][] grid;
    private double timeStep;
    private int subSteps;
    private double simulationSpeed;
    private Vector gravity;
    private double damping;
    private int gridSize;
    private int numThreads;
    private double smoothingWidth;
    private List<VariableSlider> variableSliders;
    private List<DropDown> dropDowns;
    private Point mousePosition;
    private double mouseRadius;
    private double mousePower;

    private double PRESSURE_FACTOR;
    private double VISCOSITY_FACTOR; //460
    private double SURFACE_TENSION_FACTOR; //4.4e-3
    private double MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE; //3.2e-6

    private String[] timeTakenNames;
    private long[] timeTaken;
    private ExecutorService executor;
    private int threadingChunkSize;

    private List<Attractor> attractors;
    private List<ForceObject> forces;
    private List<KernelObject> kernels;
    private List<MeasurementObject> measurementObjects;

    public FluidSimulator() {
        setConstants();
        addAllKernels();

        PressureForce pressureForce = new PressureForce(kernels.get(2), PRESSURE_FACTOR);
        ViscosityForce viscosityForce = new ViscosityForce(kernels.get(3), VISCOSITY_FACTOR);
        SurfaceTensionForce surfaceTensionForce = new SurfaceTensionForce(kernels.get(4), SURFACE_TENSION_FACTOR);
        GravityForce gravityForce = new GravityForce(gravity);
        this.forces = new ArrayList<>();
        this.forces.add(pressureForce);
        this.forces.add(viscosityForce);
        this.forces.add(surfaceTensionForce);
        this.forces.add(gravityForce);
        

        measurementObjects = new ArrayList<>();
        addAllMeasurements();


        //this.kernelNames = new String[]{"Gaussian", "Poly6", "Pressure", "Viscosity", "Surface Tension"};
        this.variableSliders = new ArrayList<VariableSlider>();
        this.variableSliders.add(new VariableSlider("Simulation Speed", 0, 100, simulationSpeed, value -> this.simulationSpeed = value));
        this.variableSliders.add(new VariableSlider("Time Step", 0, 1000, timeStep*1000, value -> this.timeStep = value/1000));
        this.variableSliders.add(new VariableSlider("Sub Steps", 1, 100, subSteps, value -> this.subSteps = (int)Math.round(value)));
        this.variableSliders.add(new VariableSlider("Damping", 0.0, 1.0, damping, value -> this.damping = value));
        this.variableSliders.add(new VariableSlider("Smoothing Width", 1.0, 20, smoothingWidth, value -> this.smoothingWidth = value));
        this.variableSliders.add(new VariableSlider("Mouse Radius", 1.0, 1000, mouseRadius, value -> this.mouseRadius = value));
        this.variableSliders.add(new VariableSlider("Mouse Power", -100, 100, mousePower, value -> this.mousePower = value));
        this.variableSliders.add(new VariableSlider("Threading Chunk Size", 1, 1000, threadingChunkSize, value -> this.threadingChunkSize = (int)Math.round(value)));
        

        for (ForceObject force : forces) {
            for (Variable variable : force.getVariables()) {
                this.variableSliders.add(variable.createVariableSlider());
            }
        }


        //this.variableSliders.add(new VariableSlider("Gravity X", -1.0, 1.0, gravityForce.getGravity().getX(), value -> gravityForce.setGravity(new Vector2D(value, gravityForce.getGravity().getY()))));
        //this.variableSliders.add(new VariableSlider("Gravity Y", -10.0, 10.0, gravityForce.getGravity().getY(), value -> gravityForce.setGravity(new Vector2D(gravityForce.getGravity().getX(), value))));
        //this.variableSliders.add(new VariableSlider("Pressure Factor", 0, 20000.0, pressureForce.getForceConstant(), value -> pressureForce.setForceConstant(value)));
        //this.variableSliders.add(new VariableSlider("Viscosity Factor", 0, 100, viscosityForce.getForceConstant(), value -> viscosityForce.setForceConstant(value)));
        //this.variableSliders.add(new VariableSlider("Surface Tension Factor",0, 100, surfaceTensionForce.getForceConstant(), value -> surfaceTensionForce.setForceConstant(value)));
        this.variableSliders.add(new VariableSlider("Minimum Surface Tension Force Magnitude", 0, 10, MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE, value -> MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE = value));
        
        String[] kernelNames = new String[kernels.size()];
        for (int i = 0; i < kernels.size(); i++) {
            kernelNames[i] = kernels.get(i).getType();
        }
        this.dropDowns = new ArrayList<DropDown>();
        for (ForceObject force : forces) {
            if (force instanceof GridForce) {
                this.dropDowns.add(new DropDown(force.getType(), kernelNames, ((GridForce)force).getKernel().getType(), (String selection) -> ((GridForce)force).setKernel(kernels.get(Arrays.asList(kernelNames).indexOf(selection)))));
            }
        }

        this.grid = new ArrayList[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = new ArrayList<Particle>();
            }
        }
        this.particles = new ArrayList<>();
        addRandomParticles(numParticles, width, height, new double[]{0, 0}, new double[]{0, 0});

        this.executor = Executors.newFixedThreadPool(numThreads);

        this.timeTakenNames = new String[]{"Update Grid Positions", "Calculate Density and Pressure", "Apply Forces", "Apply Boundary Conditions"};
        this.timeTaken = new long[timeTakenNames.length];
        resetTimeTaken();

        this.attractors = new ArrayList<>();

    }

    private void setConstants() {
        this.PRESSURE_FACTOR = 50;
        this.VISCOSITY_FACTOR = 5; //460
        this.SURFACE_TENSION_FACTOR = 5; //4.4e-3
        this.MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE = 4.1e-6; //3.2e-6

        this.width = 500;
        this.height = 500;
        this.timeStep = 0.05;
        this.simulationSpeed = 1; //51
        this.gravity = new Vector(0,2.2); //2

        this.numParticles = 1000;

        this.damping = 0.8; //0.95
        this.gridSize = width/4; // number of grids per axis
        this.numThreads = 100;
        this.smoothingWidth = 2;
        this.subSteps = 1; //44
        this.mouseRadius = 100;
        this.mousePower = 10;
        this.threadingChunkSize = 100;
    }

    private void addAllKernels() {

        this.kernels = new ArrayList<>();
        this.kernels.add(new GaussianKernel());
        this.kernels.add(new Poly6Kernel());
        this.kernels.add(new PressureKernel());
        this.kernels.add(new ViscosityKernel());
        this.kernels.add(new SurfaceTensionKernel());

    }

    private void addAllMeasurements() {
        for (ForceObject force : forces) {
            for (MeasurementObject measurement : force.getMeasurements()) {
                if (!measurementObjects.contains(measurement)) {
                    measurementObjects.add(measurement);
                }
            }
        }
    }


    public void addAttractor(Attractor attractor) {
        attractors.add(attractor);
    }

    public void removeAttractor(Attractor attractor) {
        attractors.remove(attractor);
    }

    public List<Attractor> getAttractors() {
        return attractors;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public double getMouseRadius() {
        return mouseRadius;
    }

    public double getMousePower() {
        return mousePower;
    }

    public List<VariableSlider> getVariableSliders() {
        return variableSliders;
    }

    public List<DropDown> getDropDowns() {
        return dropDowns;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public int getSubSteps() {
        return subSteps;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public long[] getTimeTaken() {
        return timeTaken;
    }

    public String[] getTimeTakenNames() {
        return timeTakenNames;
    }

    public void setMousePosition(Point mousePosition) {
        this.mousePosition = mousePosition;
    }

    public void resetTimeTaken() {
        for (int i = 0; i < timeTaken.length; i++) {
            timeTaken[i] = 0;
        }
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
        int gridX = (int)(particle.getX() / (width / gridSize));
        int gridY = (int)(particle.getY() / (height / gridSize));
        grid[gridX][gridY].add(particle);
        particle.setGridPosition(new int[]{gridX, gridY});
    }

    private List<Particle> getNearbyParticles(Particle p,boolean includeSelf) {
        int[] gridPos = p.getGridPosition();
        // Get particles from 3x3 grid around current particle
        List<Particle> nearbyParticles = new ArrayList<>();
        for (int i = Math.max(0, gridPos[0]-1); i <= Math.min(gridSize-1, gridPos[0]+1); i++) {
            for (int j = Math.max(0, gridPos[1]-1); j <= Math.min(gridSize-1, gridPos[1]+1); j++) {
                for (Particle neighbor : grid[i][j]) {
                    if (includeSelf || !neighbor.equals(p)) {
                        nearbyParticles.add(neighbor);
                    }
                }
            }
        }
        return nearbyParticles;
    }

    public void timeStep() {

        double consolidatedTimeStep = simulationSpeed*timeStep/subSteps;
        long startTime = System.nanoTime();
        // Update particle positions and grid position.
        for (Particle p : particles) {
            p.updatePosition(consolidatedTimeStep);
            updateGridPosition(p);
        }
        long endTime = System.nanoTime();
        timeTaken[0] += endTime - startTime;
        // Calculate measurements
        startTime = System.nanoTime();
        executeParallelTask(p -> {
            for (MeasurementObject measurement : measurementObjects) {
                if (measurement.isChangingMeasurement()) {
                    if (measurement instanceof MeasurementKernelObject) {
                        // For kernel measurements, use nearby particles
                        double totalMeasurement = 0;
                        for (Particle neighbor : getNearbyParticles(p,((MeasurementKernelObject)measurement).includeSelf)) {
                            totalMeasurement += ((MeasurementKernelObject)measurement).calculateMeasurement(p, neighbor, smoothingWidth);
                        }
                        p.setMeasurement(measurement.getName(), totalMeasurement);
                    } else {
                        p.setMeasurement(measurement.getName(), measurement.calculateMeasurement(p));
                    }
                }
            }
        }, executor);
        endTime = System.nanoTime();
        timeTaken[1] += endTime - startTime;
        
        // Apply forces
        startTime = System.nanoTime();
        executeParallelTask(p -> {
            for (ForceObject force : forces) {
                if (force instanceof GridForce) {
                    // For grid forces, use nearby particles
                    Vector totalForce = new Vector(0,0);
                    for (Particle neighbor : getNearbyParticles(p,false)) {
                        Vector nextForce = ((GridForce)force).calculateForce(p, neighbor, smoothingWidth);
                        totalForce= totalForce.add(nextForce);

                    }
                    p.applyForce(totalForce, consolidatedTimeStep);
                } else {
                    p.applyForce(force.calculateForce(p), consolidatedTimeStep);

                    
                }
            }

            if (mousePosition != null && p.getPosition().distanceTo(new Vector(new double[] { mousePosition.getX(), mousePosition.getY() })) < mouseRadius) {
                Vector force = new Vector(mousePosition.getX() - p.getX(), mousePosition.getY() - p.getY()).normalize().multiply(mousePower);
                p.applyForce(force, consolidatedTimeStep);
            }

            for (Attractor attractor : attractors) {
                if (p.getPosition().distanceTo(new Vector(new double[] { attractor.getPosition().getX(), attractor.getPosition().getY() })) < attractor.getRadius()) {
                    Vector force = new Vector(attractor.getPosition().getX() - p.getX(), attractor.getPosition().getY() - p.getY()).normalize().multiply(attractor.getStrength());
                    p.applyForce(force, consolidatedTimeStep);
                }
            }
        }, executor);
        endTime = System.nanoTime();
        timeTaken[2] += endTime - startTime;
        // apply boundary conditions
        startTime = System.nanoTime();
        executeParallelTask(p -> {
            p.applyBoundaryConditions(width, height, damping);
            p.setColor();
            p.clearMeasurements();
        }, executor);
        endTime = System.nanoTime();
        timeTaken[3] += endTime - startTime;

    }

    private void executeParallelTask(ParticleTask task, ExecutorService executor) {
         //Use a larger chunk size to reduce overhead
        int numChunks = (int) Math.ceil((double) particles.size() / threadingChunkSize);
        final CountDownLatch latch = new CountDownLatch(numChunks);

        for (int i = 0; i < numChunks; i++) {
            final int startIndex = i * threadingChunkSize;
            final int endIndex = Math.min((i + 1) * threadingChunkSize, particles.size());
            
            executor.submit(() -> {
                try {
                    // Process particles in this chunk
                    for (int j = startIndex; j < endIndex; j++) {
                        try {
                            task.processParticle(particles.get(j));
                        } catch (Exception e) {
                            System.err.println("Error processing particle " + j + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error in chunk processing: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("Parallel task interrupted: " + e.getMessage());
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    private interface ParticleTask {
        void processParticle(Particle p);
    }
    private void updateGridPosition(Particle p) {
        int gridX = (int)(p.getX() / (width / gridSize));
        int gridY = (int)(p.getY() / (height / gridSize));
        if (gridX < 0) {
            gridX = 0;
        }
        if (gridY < 0) {
            gridY = 0;
        }
        if (gridX >= gridSize) {
            gridX = gridSize - 1;
        }
        if (gridY >= gridSize) {
            gridY = gridSize - 1;
        }
        if (p.getGridPosition() != new int[]{gridX, gridY}) {
            grid[p.getGridPosition()[0]][p.getGridPosition()[1]].remove(p);
            grid[gridX][gridY].add(p);
            p.setGridPosition(new int[]{gridX, gridY});
        }
    }
    /*

    public double Kernel(double distance, int forceIndex) {
        if (distance < smoothingWidth) {
            if (dropDowns.get(forceIndex).getCurrentSelection().equals("Gaussian")) {
                return Gaussian_Kernel(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Poly6")) {
                return Poly6_Kernel(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Pressure")) {
                return Pressure_Kernel(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Viscosity")) {
                return Viscosity_Kernel(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Surface Tension")) {
                return Surface_Tension_Kernel(distance);
            }

        }
        return 0;
    }

    public double Kernel_Derivative(double distance, int forceIndex) {
        if (distance < smoothingWidth) {
            if (dropDowns.get(forceIndex).getCurrentSelection().equals("Gaussian")) {
                return Gaussian_Kernel_Derivative(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Poly6")) {
                return Poly6_Kernel_Derivative(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Pressure")) {
                return Pressure_Kernel_Derivative(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Viscosity")) {
                return Viscosity_Kernel_Derivative(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Surface Tension")) {
                return Surface_Tension_Kernel_Derivative(distance);
            }
        }
        return 0;
    }

    public double Kernel_Second_Derivative(double distance, int forceIndex) {
        if (distance < smoothingWidth) {
            if (dropDowns.get(forceIndex).getCurrentSelection().equals("Gaussian")) {
                return Gaussian_Kernel_Second_Derivative(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Poly6")) {
                return Poly6_Kernel_Second_Derivative(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Pressure")) {
                return Pressure_Kernel_Second_Derivative(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Viscosity")) {
                return Viscosity_Kernel_Second_Derivative(distance);
            }
            else if (dropDowns.get(forceIndex).getCurrentSelection().equals("Surface Tension")) {
                return Surface_Tension_Kernel_Second_Derivative(distance);
            }
        }
        return 0;
    }
    */



    public void addRandomParticles(int numParticles, int width, int height, double[] xVelocityBounds, double[] yVelocityBounds) {
        // Create a smaller box centered horizontally and 2/3 up vertically
        double boxWidth = width * 0.3; // 30% of screen width
        double boxHeight = height * 0.2; // 20% of screen height
        double boxX = (width - boxWidth) / 2; // Center horizontally
        double boxY = height * 0.33; // Position at 1/3 from top
        
        for (int i = 0; i < numParticles; i++) {
            double x = boxX + (Math.random() * boxWidth);
            double y = boxY + (Math.random() * boxHeight);
            double vx = Math.random() * (xVelocityBounds[1] - xVelocityBounds[0]) + xVelocityBounds[0];
            double vy = Math.random() * (yVelocityBounds[1] - yVelocityBounds[0]) + yVelocityBounds[0];
            Particle p = new Particle(x, y, vx, vy, 1);
            addParticle(p);
        }
    }
    
    
}
