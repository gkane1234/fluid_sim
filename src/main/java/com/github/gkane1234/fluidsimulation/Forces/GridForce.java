package com.github.gkane1234.fluidsimulation.Forces;

import com.github.gkane1234.fluidsimulation.Particle;
import com.github.gkane1234.fluidsimulation.Vector;
import com.github.gkane1234.fluidsimulation.Kernels.KernelObject;
import com.github.gkane1234.fluidsimulation.Constants.Variable;

public abstract class GridForce extends ForceObject {
    protected KernelObject kernel;


    public GridForce(KernelObject kernel, double forceConstant,String name) {
        super();
        this.kernel = kernel;
        this.variables.put("Force Constant", new Variable(name, forceConstant));

    }
    public GridForce(KernelObject kernel,String name) {
        this(kernel, 1,name);
    }

    public void setKernel(KernelObject kernel) {
        this.kernel = kernel;
    }

    public KernelObject getKernel() {
        return kernel;
    }

    public double getForceConstant() {
        return variables.get("Force Constant").getValue();
    }

    public void setForceConstant(double forceConstant) {
        variables.get("Force Constant").setValue(forceConstant);
    }


    @Override
    public Vector calculateForce(Particle p) {
        throw new UnsupportedOperationException("A GridForce must be calculated with a neighbor and distance");
    }

    public abstract Vector calculateForce(Particle p, Particle neighbor, double smoothingWidth);

}
