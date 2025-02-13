package com.github.gkane1234.fluidsimulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
// have it so that the different forces can have their own kernels and specific smoothing distances
// allow for new forces and a way to save how they work
// create a kernel object
// create a force object
public class FluidSimulator {
    private static final double VISCOSITY_FACTOR_MULTIPLIER = 1000;
    private static final double PRESSURE_FACTOR_MULTIPLIER = 1000;
    private static final double SURFACE_TENSION_FACTOR_MULTIPLIER = 100000;
    private int width;
    private int height;
    private int numParticles;
    private List<Particle> particles;
    private ArrayList<Particle>[][] grid;
    private double timeStep;
    private int subSteps;
    private double simulationSpeed;
    private Vector2D gravity;
    private double damping;
    private int gridSize;
    private int numThreads;
    private double smoothingWidth;
    private List<VariableSlider> variableSliders;
    private List<DropDown> dropDowns;
    private String[] kernelNames;
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
    public FluidSimulator() {
        this.PRESSURE_FACTOR = 1200;
        this.VISCOSITY_FACTOR = 41; //460
        this.SURFACE_TENSION_FACTOR = 10; //4.4e-3
        this.MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE = 4.1e-6; //3.2e-6

        this.width = 1200;
        this.height = 1200;
        this.timeStep = 0.05;
        this.simulationSpeed = 1; //51
        this.gravity = new Vector2D(0, 2.2); //2

        this.numParticles = 2000;

        this.damping = 0.8; //0.95
        this.gridSize = 120; // number of grids per axis
        this.numThreads = 500;
        this.smoothingWidth = 10;
        this.subSteps = 1; //44
        this.mouseRadius = 100;
        this.mousePower = 10;
        this.threadingChunkSize = 100;
        this.kernelNames = new String[]{"Gaussian", "Poly6", "Pressure", "Viscosity", "Surface Tension"};
        this.variableSliders = new ArrayList<VariableSlider>();
        this.variableSliders.add(new VariableSlider("Simulation Speed", 0, 100, simulationSpeed, value -> this.simulationSpeed = value));
        this.variableSliders.add(new VariableSlider("Time Step", 0, 1000, timeStep*1000, value -> this.timeStep = value/1000));
        this.variableSliders.add(new VariableSlider("Sub Steps", 1, 100, subSteps, value -> this.subSteps = (int)Math.round(value)));
        this.variableSliders.add(new VariableSlider("Damping", 0.0, 1.0, damping, value -> this.damping = value));
        this.variableSliders.add(new VariableSlider("Gravity X", -1.0, 1.0, gravity.getX(), value -> this.gravity = new Vector2D(value, this.gravity.getY())));
        this.variableSliders.add(new VariableSlider("Gravity Y", -10.0, 10.0, gravity.getY(), value -> this.gravity = new Vector2D(this.gravity.getX(), value)));
        this.variableSliders.add(new VariableSlider("Smoothing Width", 1.0, 20, smoothingWidth, value -> this.smoothingWidth = value));
        this.variableSliders.add(new VariableSlider("Pressure Factor", 0, 20000.0, PRESSURE_FACTOR, value -> PRESSURE_FACTOR = value));
        this.variableSliders.add(new VariableSlider("Viscosity Factor", 0, 100, VISCOSITY_FACTOR, value -> VISCOSITY_FACTOR = value));
        this.variableSliders.add(new VariableSlider("Surface Tension Factor",0, 100, SURFACE_TENSION_FACTOR, value -> SURFACE_TENSION_FACTOR = value));
        this.variableSliders.add(new VariableSlider("Minimum Surface Tension Force Magnitude", 0, 10, MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE, value -> MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE = value));
        this.variableSliders.add(new VariableSlider("Mouse Radius", 1.0, 1000, mouseRadius, value -> this.mouseRadius = value));
        this.variableSliders.add(new VariableSlider("Mouse Power", -100, 100, mousePower, value -> this.mousePower = value));
        this.variableSliders.add(new VariableSlider("Threading Chunk Size", 1, 1000, threadingChunkSize, value -> this.threadingChunkSize = (int)Math.round(value)));
        
        this.dropDowns = new ArrayList<DropDown>();
        this.dropDowns.add(new DropDown("Pressure", kernelNames, "Pressure"));
        this.dropDowns.add(new DropDown("Viscosity", kernelNames, "Viscosity"));
        this.dropDowns.add(new DropDown("Surface Tension", kernelNames, "Surface Tension"));
        this.grid = new ArrayList[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                this.grid[i][j] = new ArrayList<Particle>();
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

    public void timeStep() {
        double consolidatedTimeStep = simulationSpeed*timeStep/subSteps;
         // Update particle grid positions (cannot be parallelized)
        long startTime = System.nanoTime();
        for (Particle p : particles) {
            p.updatePosition(consolidatedTimeStep);
            updateGridPosition(p);
        }
        long endTime = System.nanoTime();
        timeTaken[0] += endTime - startTime;
        // Create thread pool for grid cells
        startTime = System.nanoTime();
        executeParallelTask(p -> {
            p.setDensity(calculateDensity(p));
            p.setPressure(calculatePressure(p));
        }, executor);
        endTime = System.nanoTime();
        timeTaken[1] += endTime - startTime;
        // Apply forces
        //latch = new CountDownLatch(gridSize * gridSize);
        startTime = System.nanoTime();
        executeParallelTask(p -> {
            p.applyForce(calculatePressureAndViscosityForces(p), consolidatedTimeStep);
            p.applyForce(gravity, consolidatedTimeStep);
            if (mousePosition != null && p.getPosition().distanceTo(new Vector2D(mousePosition.getX(), mousePosition.getY())) < mouseRadius) {
                Vector2D force = new Vector2D(mousePosition.getX() - p.getX(), mousePosition.getY() - p.getY()).normalize().multiply(mousePower);
                p.applyForce(force, consolidatedTimeStep);
            }
            for (Attractor attractor : attractors) {
                if (p.getPosition().distanceTo(new Vector2D(attractor.getPosition().getX(), attractor.getPosition().getY())) < attractor.getRadius()) {
                    Vector2D force = new Vector2D(attractor.getPosition().getX() - p.getX(), attractor.getPosition().getY() - p.getY()).normalize().multiply(attractor.getStrength());
                    p.applyForce(force, consolidatedTimeStep);
                }
            }
        }, executor);
        endTime = System.nanoTime();
        timeTaken[2] += endTime - startTime;
        // Move particles and apply boundary conditions
       // latch = new CountDownLatch(gridSize * gridSize);
        startTime = System.nanoTime();
        executeParallelTask(p -> {
            p.applyBoundaryConditions(width, height, damping);
            p.setColor();
        }, executor);
        endTime = System.nanoTime();
        timeTaken[3] += endTime - startTime;
    }

    private void executeParallelTask(ParticleTask task, ExecutorService executor) {
        // Use a larger chunk size to reduce overhead
        int numChunks = (int) Math.ceil((double) particles.size() / threadingChunkSize);
        final CountDownLatch latch = new CountDownLatch(numChunks);

        for (int i = 0; i < numChunks; i++) {
            final int startIndex = i * threadingChunkSize;
            final int endIndex = Math.min((i + 1) * threadingChunkSize, particles.size());
            
            executor.submit(() -> {
                try {
                    // Process particles in this chunk
                    for (int j = startIndex; j < endIndex; j++) {
                        task.processParticle(particles.get(j));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    private interface ParticleTask {
        void processParticle(Particle p);
    }

    private double calculateDensity(Particle p) {
        int[] gridPos = p.getGridPosition();
        double density = 0;
        // Look at surrounding grid cells including current cell
        for (int i = Math.max(0, gridPos[0]-1); i <= Math.min(gridSize-1, gridPos[0]+1); i++) {
            for (int j = Math.max(0, gridPos[1]-1); j <= Math.min(gridSize-1, gridPos[1]+1); j++) {
                // For each particle in this grid cell
                for (Particle neighbor : grid[i][j]) {
                    if (neighbor != p) {
                        double distance = p.getPosition().distanceTo(neighbor.getPosition());
                        double kernelValue = Kernel(distance, 0);
                        density+=kernelValue;                        
                    }
                }
            }
        }
        density+=p.getMass();
        return density;
    }

    private double calculatePressure(Particle p) {
        return PRESSURE_FACTOR*p.getDensity();
    }

    private Vector2D calculatePressureAndViscosityForces(Particle p) {
        int[] gridPos = p.getGridPosition();
        Vector2D pressureForce = new Vector2D(0, 0);
        Vector2D viscosityForce = new Vector2D(0, 0);
        Vector2D surfaceTensionForceUnNormalizedDirection = new Vector2D(0, 0);
        double surfaceTensionForceMagnitude = 0;
        for (int i = Math.max(0, gridPos[0]-1); i <= Math.min(gridSize-1, gridPos[0]+1); i++) {
            for (int j = Math.max(0, gridPos[1]-1); j <= Math.min(gridSize-1, gridPos[1]+1); j++) {
                // For each particle in this grid cell
                for (Particle neighbor : grid[i][j]) {
                    if (neighbor != p) {

                        double distance = p.getPosition().distanceTo(neighbor.getPosition());
                        pressureForce = pressureForce.add(calculatePressureForceBetweenParticles(p, neighbor, distance));
                        viscosityForce = viscosityForce.add(calculateViscosityForceBetweenParticles(p, neighbor, distance));
                        surfaceTensionForceUnNormalizedDirection = surfaceTensionForceUnNormalizedDirection.add(calculateUnnormalizedSurfaceTensionForceDirection(p, neighbor, distance));
                        surfaceTensionForceMagnitude += calculateSurfaceTensionForceMagnitude(p, neighbor, distance);
                    }
                }
            }
        }
        //System.err.println(surfaceTensionForceUnNormalizedDirection.normalize().multiply(surfaceTensionForceMagnitude).magnitude());
        if (surfaceTensionForceUnNormalizedDirection.magnitude() > MINIMUM_SURFACE_TENSION_FORCE_MAGNITUDE) {
            //System.err.println(surfaceTensionForceUnNormalizedDirection.normalize().multiply(surfaceTensionForceMagnitude).magnitude());
            Vector2D surfaceTensionForce = surfaceTensionForceUnNormalizedDirection.normalize().multiply(surfaceTensionForceMagnitude);
            return pressureForce.add(viscosityForce).add(surfaceTensionForce);
        }
        return pressureForce.add(viscosityForce);
    }

    private Vector2D calculatePressureForceBetweenParticles(Particle p, Particle neighbor,double distance) {
        double pressureMagnitude = PRESSURE_FACTOR_MULTIPLIER*neighbor.getMass()*(neighbor.getPressure()+p.getPressure())/(2*neighbor.getDensity());
        double pressureDerivative = Kernel_Derivative(distance, 0);
        Vector2D directionOfParticle = neighbor.getPosition().subtract(p.getPosition()).normalize();
        return directionOfParticle.multiply(pressureDerivative*pressureMagnitude);
    }

    private Vector2D calculateViscosityForceBetweenParticles(Particle p, Particle neighbor,double distance) {
        double viscosityMagnitude = VISCOSITY_FACTOR_MULTIPLIER*VISCOSITY_FACTOR*neighbor.getMass()*neighbor.getVelocity().subtract(p.getVelocity()).magnitude()/neighbor.getDensity();
        double viscositySecondDerivative = Kernel_Second_Derivative(distance, 1);
        Vector2D directionOfVelocity = neighbor.getVelocity().subtract(p.getVelocity()).normalize();
        return directionOfVelocity.multiply(viscositySecondDerivative*viscosityMagnitude);
    }

    private Vector2D calculateSurfaceTensionForceBetweenParticles(Particle p, Particle neighbor,double distance) {
        //unused
        double surfaceTensionSecondDerivative = Kernel_Second_Derivative(distance, 2);
        double surfaceTensionDerivative = Kernel_Derivative(distance, 2);
        double surfaceTensionMagnitude = calculateSurfaceTensionForceMagnitude(p, neighbor, distance);
        Vector2D directionOfSurfaceTension = calculateUnnormalizedSurfaceTensionForceDirection(p, neighbor, distance);
        return directionOfSurfaceTension.multiply(surfaceTensionMagnitude);

    }

    private Vector2D calculateUnnormalizedSurfaceTensionForceDirection(Particle p, Particle neighbor,double distance) {
        double surfaceTensionDerivative = Kernel_Derivative(distance, 2);
        Vector2D directionOfSurfaceTension = neighbor.getPosition().subtract(p.getPosition()).normalize();
        return directionOfSurfaceTension.multiply(surfaceTensionDerivative);
    }

    private double calculateSurfaceTensionForceMagnitude(Particle p, Particle neighbor,double distance) {
        double surfaceTensionSecondDerivative = Kernel_Second_Derivative(distance, 2);
        double surfaceTensionMagnitude = -SURFACE_TENSION_FACTOR_MULTIPLIER*SURFACE_TENSION_FACTOR*neighbor.getMass()/neighbor.getDensity();
        return surfaceTensionSecondDerivative*surfaceTensionMagnitude;
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

    public  double Gaussian_Kernel(double distance) {
        double PI_TO_THE_THREE_HALVES = Math.pow(Math.PI, 1.5);
        return (1/(PI_TO_THE_THREE_HALVES*smoothingWidth*smoothingWidth*smoothingWidth))*Math.exp(-distance*distance/(smoothingWidth*smoothingWidth));
    }

    public double Gaussian_Kernel_Derivative(double distance) {
        double PI_TO_THE_THREE_HALVES = Math.pow(Math.PI, 1.5);
        return (1/(PI_TO_THE_THREE_HALVES*smoothingWidth*smoothingWidth*smoothingWidth))*(-2*distance/Math.pow(smoothingWidth, 2))*Math.exp(-distance*distance/(smoothingWidth*smoothingWidth));
    }

    public double Gaussian_Kernel_Second_Derivative(double distance) {
        double PI_TO_THE_THREE_HALVES = Math.pow(Math.PI, 1.5);
        return (1/(PI_TO_THE_THREE_HALVES*smoothingWidth*smoothingWidth*smoothingWidth))*Math.exp(-distance*distance/(smoothingWidth*smoothingWidth))*((-2/Math.pow(smoothingWidth, 2))+(4*distance*distance/Math.pow(smoothingWidth, 4)));
    }

    public double Poly6_Kernel(double distance) {
        return 315/(64*Math.PI*Math.pow(smoothingWidth, 9))*Math.pow(smoothingWidth*smoothingWidth-distance*distance, 3);
    }

    public double Poly6_Kernel_Derivative(double distance) {
        return -945/(32*Math.PI*Math.pow(smoothingWidth, 9))*distance*(Math.pow(smoothingWidth*smoothingWidth-distance*distance, 2));
    }

    public double Poly6_Kernel_Second_Derivative(double distance) {
        return 945/(16*Math.PI*Math.pow(smoothingWidth, 9))*(2*distance*distance*(smoothingWidth*smoothingWidth-distance*distance)-Math.pow(smoothingWidth*smoothingWidth-distance*distance, 2));
    }
    public double Pressure_Kernel(double distance) {
        return 15/(Math.PI*Math.pow(smoothingWidth, 6))*Math.pow(smoothingWidth-distance, 3);
    }

    public double Pressure_Kernel_Derivative(double distance) {
        return -45/(Math.PI*Math.pow(smoothingWidth, 6))*(Math.pow(smoothingWidth-distance, 2));
    }

    public double Pressure_Kernel_Second_Derivative(double distance) {
        return 90/(Math.PI*Math.pow(smoothingWidth, 6))*(smoothingWidth-distance);
    }

    public double Viscosity_Kernel(double distance) {
        return 15/(2*Math.PI*Math.pow(smoothingWidth, 3))*(-distance*distance*distance/(2*smoothingWidth*smoothingWidth*smoothingWidth)+distance*distance/(smoothingWidth*smoothingWidth)-distance/(2*smoothingWidth)-1);
    }

    public double Viscosity_Kernel_Derivative(double distance) {
        return 15/(2*Math.PI*Math.pow(smoothingWidth, 3))*(-3*distance*distance/(2*smoothingWidth*smoothingWidth*smoothingWidth)+2*distance/(smoothingWidth*smoothingWidth)-2/smoothingWidth);
    }

    public double Viscosity_Kernel_Second_Derivative(double distance) {
        return 45/(Math.PI*Math.pow(smoothingWidth, 6))*(smoothingWidth-distance);
    }

    public double Surface_Tension_Kernel(double distance) {
        return Gaussian_Kernel(distance);
    }

    public double Surface_Tension_Kernel_Derivative(double distance) {
        return Gaussian_Kernel_Derivative(distance);
    }
    public double Surface_Tension_Kernel_Second_Derivative(double distance) {
        return Gaussian_Kernel_Second_Derivative(distance);
    }

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
