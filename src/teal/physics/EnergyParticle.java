/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: EnergyParticle.java,v 1.17 2010/08/10 18:12:32 stefan Exp $ 
 * 
 */

package teal.physics;

import java.awt.Color;

import javax.vecmath.*;

import teal.config.Teal;
import teal.sim.engine.*;
import teal.physics.em.BField;
import teal.physics.em.Circuit;
import teal.physics.em.EField;
import teal.sim.spatial.TrailVisualization;

public class EnergyParticle extends Particle {

    private static final long serialVersionUID = 3258130254210873652L;
    
    private final Color3f Ecolor = new Color3f(Teal.DefaultEFieldColor);
    private final Color3f Bcolor = new Color3f(Teal.DefaultBFieldColor);

    Vector3d initial_position = new Vector3d();
    public TrailVisualization tv = null;

    public EnergyParticle() {
        tv = new TrailVisualization(this, 10, 0.1);
    }

    public void nextSpatial() {
        float ratio = (float) getElectricEnergyRatio();
        if (ratio == -1f) return;
        Color3f color = new Color3f(Ecolor);
        Color3f temp = new Color3f(Bcolor);
        color.scale(ratio);
        temp.scale(1f - ratio);
        color.add(temp);
        setColor(new Color(color.x, color.y, color.z));
    }

    public Vector3d getVelocity() {
        if (theEngine == null) return new Vector3d();
        Vector3d pos = new Vector3d(position_d);
        Vector3d velocity = new Vector3d();
//        Vector3d E = ((EMEngine)theEngine).getEField().get(pos);
        Vector3d E = theEngine.getElementByType(EField.class).get(pos);
//        Vector3d B = ((EMEngine)theEngine).getBField().get(pos);
        Vector3d B = theEngine.getElementByType(BField.class).get(pos);
        velocity.cross(E, B);
        double factor = 2. / (Circuit.epsilon0 * Circuit.mu0 * E.lengthSquared() + B.lengthSquared());
        velocity.scale(factor);
        //		System.out.println("particle pos: " + pos + " => r = " + pos.length());
        //		System.out.println("particle E: " + E);
        //		System.out.println("particle B: " + B);
        //		System.out.println("particle velocity: " + velocity + " => v = " + velocity.length());
        return velocity;
    }

    public double getMagneticEnergyRatio() {
        if (theEngine == null) return -1.;
//      EField efield = (EField) ((EMEngine)theEngine).getEField();
        EField efield = theEngine.getElementByType(EField.class);
//        BField bfield = (BField) ((EMEngine)theEngine).getBField();
        BField bfield = theEngine.getElementByType(BField.class);
        if (efield == null) return -1.;
        if (bfield == null) return -1.;
        double E = efield.get(position).lengthSquared() * Circuit.epsilon0;
        double B = bfield.get(position).lengthSquared() / Circuit.mu0;
        double T = E + B;
        return B / T;
    }

    public double getElectricEnergyRatio() {
        if (theEngine == null) return -1.;
//        EField efield = (EField) ((EMEngine)theEngine).getEField();
        EField efield = theEngine.getElementByType(EField.class);
//        BField bfield = (BField) ((EMEngine)theEngine).getBField();
        BField bfield = theEngine.getElementByType(BField.class);
        if (efield == null) return -1.;
        if (bfield == null) return -1.;
        double E = efield.get(position).lengthSquared() * Circuit.epsilon0;
        double B = bfield.get(position).lengthSquared() / Circuit.mu0;
        double T = E + B;
        return E / T;
    }
}
