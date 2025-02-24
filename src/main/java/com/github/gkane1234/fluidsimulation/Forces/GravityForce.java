package com.github.gkane1234.fluidsimulation.Forces;

import com.github.gkane1234.fluidsimulation.Particle;
import com.github.gkane1234.fluidsimulation.Vector2D;

public class GravityForce extends ForceObject {
    private Vector2D gravity; 
    public GravityForce(Vector2D gravity) {
        super();
        this.gravity = gravity;
    }

    public void setGravity(Vector2D gravity) {
        this.gravity = gravity;
    }

    public Vector2D getGravity() {
        return gravity;
    }

    @Override
    public Vector2D calculateForce(Particle p) {
        return gravity;
    }

    @Override
    public String getType() {
        return "Gravity";
    }
}
