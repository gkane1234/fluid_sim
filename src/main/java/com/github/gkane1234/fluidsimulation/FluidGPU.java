package com.github.gkane1234.fluidsimulation;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.awt.Color;
import java.awt.Point;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class FluidGPU {
    private long window;
    private FluidSimulator simulator;
    private int width;
    private int height;
    private static final int PARTICLE_SIZE = 5;
    private static final int ATTRACTOR_SIZE = 10;
    private boolean mousePressed = false;
    private double mouseX, mouseY;
    
    public FluidGPU(FluidSimulator simulator) {
        this.simulator = simulator;
        this.width = simulator.getWidth();
        this.height = simulator.getHeight();
        initWindow();
        initGL();
    }
    
    private void initWindow() {
        // Initialize GLFW with error checking
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Set window hints
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        
        // Create window
        window = glfwCreateWindow(width, height, "Fluid Simulation", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create window");
        }

        // Set up keyboard callbacks
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
            if (key == GLFW_KEY_A && action == GLFW_PRESS) {
                simulator.addAttractor(new Attractor(new Point((int)mouseX, (int)mouseY), simulator.getMousePower(), simulator.getMouseRadius()));
            }
        });

        // Set up mouse callbacks
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                if (action == GLFW_PRESS) {
                    mousePressed = !mousePressed;
                    System.out.println("Mouse pressed: " + mousePressed);
                }
            }
        });

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            mouseX = xpos;
            mouseY = ypos;

            if (mousePressed) {
                System.out.println("Mouse position: " + xpos + ", " + ypos);
                simulator.setMousePosition(new Point((int)xpos, (int)ypos));
            }
            else {
                simulator.setMousePosition(null);
            }
        });

        // Center window
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Make OpenGL context current
        glfwMakeContextCurrent(window);
        
        // Enable v-sync
        glfwSwapInterval(1);
        
        // Show the window
        glfwShowWindow(window);
    }

    private void initGL() {
        // Create capabilities (must be after context creation)
        GL.createCapabilities();
        glViewport(0, 0, width, height);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glPointSize(PARTICLE_SIZE);
    }

    public void render() {
        // Clear and render
        glClear(GL_COLOR_BUFFER_BIT);
        
        // Draw particles
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_POINTS);
        for (Particle p : simulator.getParticles()) {
            Color color = p.getColor();
            glColor3f((float)color.getRed()/255.0f, (float)color.getGreen()/255.0f, (float)color.getBlue()/255.0f);
            glVertex2f((float)p.getX(), (float)p.getY());
        }
        glEnd();
        
        // Draw attractors
        glPointSize(ATTRACTOR_SIZE);
        glColor3f(1.0f, 0.0f, 0.0f);
        glBegin(GL_POINTS);
        for (Attractor a : simulator.getAttractors()) {
            glVertex2f(a.getPosition().x, a.getPosition().y);
        }
        glEnd();
        glPointSize(PARTICLE_SIZE);

        // Swap buffers
        glfwSwapBuffers(window);
        
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void cleanup() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
