package com.github.gkane1234.fluidsimulation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.github.gkane1234.fluidsimulation.gui.VariableSlider;

import com.github.gkane1234.fluidsimulation.gui.DropDown;

import java.io.File;

public class FluidDisplay extends JPanel {
    private final FluidSimulator simulator;
    private int width;
    private int height;
    private static final int PARTICLE_SIZE = 5;
    private static final int ATTRACTOR_SIZE = 10;
    private JPanel controlPanel;
    private Point mousePosition;
    private JPanel simulationPanel;

    private long sleepingStartTime;

    public FluidDisplay(FluidSimulator simulator) {
        this.simulator = simulator;
        this.width = simulator.getWidth();
        this.height = simulator.getHeight();
        this.mousePosition = null;
        
        setLayout(new BorderLayout());
        simulationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintSimulation(g);
            }
        };
        simulationPanel.setPreferredSize(new Dimension(width, height));
        simulationPanel.setBackground(Color.BLACK);
        
        simulationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                simulator.setMousePosition(e.getPoint());
                simulationPanel.requestFocusInWindow();
                // Check if clicked on an attractor to remove it
                Point clickPoint = e.getPoint();
                List<Attractor> attractors = simulator.getAttractors();
                for (Attractor attractor : attractors) {
                    Point pos = attractor.getPosition();
                    if (Math.abs(pos.x - clickPoint.x) < ATTRACTOR_SIZE && 
                        Math.abs(pos.y - clickPoint.y) < ATTRACTOR_SIZE) {
                        simulator.removeAttractor(attractor);
                        break;
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                simulator.setMousePosition(null);
            }
        });
        
        simulationPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                simulator.setMousePosition(e.getPoint());
            }
        });

        // Add key listener for 'A' key to add attractors
        simulationPanel.setFocusable(true);
        simulationPanel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A') {
                    Point mousePos = simulationPanel.getMousePosition();
                    if (mousePos != null) {
                        simulator.addAttractor(new Attractor(mousePos, simulator.getMousePower(), simulator.getMouseRadius()));
                    }
                }
            }
        });
        
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        
        // Add all variable sliders from simulator
        List<VariableSlider> sliders = simulator.getVariableSliders();
        if (sliders != null) {
            for (VariableSlider slider : sliders) {
                controlPanel.add(slider.getPanel());
            }
        }

        // Add all drop downs from simulator
        List<DropDown> dropDowns = simulator.getDropDowns();
        if (dropDowns != null) {
            for (DropDown dropDown : dropDowns) {
                controlPanel.add(dropDown.getPanel());
            }
        }

        // Add preset save/load buttons
        JPanel presetPanel = new JPanel();
        presetPanel.setLayout(new FlowLayout());

        JButton saveButton = new JButton("Save Preset");
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Preset files (*.preset)", "preset"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String path = file.getAbsolutePath();
                if (!path.endsWith(".preset")) {
                    file = new File(path + ".preset");
                }
                for (VariableSlider slider : sliders) {
                    slider.saveValue(file);
                }
            }
            simulationPanel.requestFocusInWindow();
        });

        JButton loadButton = new JButton("Load Preset");
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Preset files (*.preset)", "preset"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                for (VariableSlider slider : sliders) {
                    slider.loadValue(file);
                }
            }
            simulationPanel.requestFocusInWindow();
        });

        presetPanel.add(saveButton);
        presetPanel.add(loadButton);
        controlPanel.add(presetPanel);

        add(simulationPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        
    }
    
    private void paintSimulation(Graphics g) {
        long startTime = System.currentTimeMillis();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, width, height);
        List<Particle> particles = simulator.getParticles();

        g2d.setColor(Color.BLUE);
        
        for(Particle p : particles) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            g2d.setColor(p.getColor());
            g2d.fillOval(x - PARTICLE_SIZE/2, y - PARTICLE_SIZE/2, 
                        PARTICLE_SIZE, PARTICLE_SIZE);
        }

        // Draw attractors
        g2d.setColor(Color.RED);
        for (Attractor attractor : simulator.getAttractors()) {
            Point pos = attractor.getPosition();
            g2d.fillOval(pos.x - ATTRACTOR_SIZE/2, pos.y - ATTRACTOR_SIZE/2,
                        ATTRACTOR_SIZE, ATTRACTOR_SIZE);
        }

        g2d.setColor(Color.WHITE);
        long[] timeTaken = simulator.getTimeTaken();
        String[] timeTakenNames = simulator.getTimeTakenNames();
        for (int i = 0; i < timeTaken.length; i++) {
            g2d.drawString(timeTakenNames[i] + ": " + timeTaken[i]/1000000 + "ms", 10, 20 + i * 20);
        }
        simulator.resetTimeTaken();
        //g2d.drawString("Sleeping for: " + (simulator.getTimeStep()*1000 - (System.currentTimeMillis() - sleepingStartTime)) + "ms", 10, 20 + timeTaken.length * 20);
    }
    
    public Point getMousePosition() {
        return mousePosition;
    }

    public void run() {
        while (true) {  
            sleepingStartTime = System.currentTimeMillis();
            for (int i = 0; i < simulator.getSubSteps(); i++) {
                simulator.timeStep();
            }
            repaint();
            long endTime = System.currentTimeMillis();
            long sleepTime = (long)(simulator.getTimeStep()*1000) - (endTime - sleepingStartTime);
            System.out.println("Sleeping for: " + sleepTime + "ms");
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
