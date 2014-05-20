/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: InfiniteWire.java,v 1.29 2010/04/12 20:13:17 stefan Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.*;

import teal.config.*;
import teal.render.j3d.*;
import teal.render.scene.*;
import teal.sim.properties.HasLength;
import teal.sim.properties.HasRadius;

/**
 * Represents an inifinite current carrying wire.
 */
public class InfiniteWire extends EMObject implements HasRadius, HasLength, HasCurrent, HasInductance, GeneratesE,
    GeneratesB {

    private static final long serialVersionUID = 3257005470995724084L;

    double inductance;
    double current;

    protected boolean generatingBField = true;
    protected boolean generatingEField = true;
    protected boolean generatingEPotential = false;
    protected boolean generatingPField = false;
    protected double length;
    protected double radius;

    //Vector3d orientation;

    public InfiniteWire() {
        super();
        isMoveable = false;
        setCurrent(Teal.InfiniteWireDefaultCurrent);
        setMass(Teal.InfiniteWireMass);
        radius = Teal.InfiniteWireDefaultRadius;
        length = 20.0;
        
    }

    /**
     Stub must be replaced 
     */
    public double getEFlux(Vector3d pos) {
        return 0.;
    }

    public void setGeneratingB(boolean b) {
        generatingBField = b;
        if (theEngine != null) theEngine.requestSpatial();
    }

    public boolean isGeneratingB() {
        return generatingBField;
    }

    public void setGeneratingE(boolean b) {
        generatingEField = b;
        if (theEngine != null) theEngine.requestSpatial();
    }

    public boolean isGeneratingE() {
        return generatingEField;
    }

    public void setGeneratingP(boolean b) {
        generatingPField = b;
        if (theEngine != null) theEngine.requestSpatial();
    }

    public boolean isGeneratingP() {
        return generatingPField;
    }

    public Vector3d getB(Vector3d pos) {
        Vector3d B = new Vector3d();
        Vector3d r = new Vector3d();
        Vector3d rperp = new Vector3d();
        Vector3d rpar = new Vector3d();
        Vector3d v = new Vector3d();

        // This should get the distance to the closest point on the wire
        r.sub(pos, position_d);
        v = getDirection();

        rpar.scale(r.dot(v) / (v.length() * v.length()), v);
        rperp.sub(r, rpar);

        
        B.cross(rperp, v);
        if (r.length() == 0)
            return new Vector3d();
        else {
            B.scale(current / (rperp.length() * rperp.length()));
            return B;
        }
    }

    public Vector3d getB(Vector3d x, double t) {
        return getB(x);
    }

    // placeholder
    public double getBFlux(Vector3d pos) {
        Vector3d r = new Vector3d();
        Vector3d rperp = new Vector3d();
        Vector3d rpar = new Vector3d();
        Vector3d v = new Vector3d();
        double bflux;

        // This should get the distance to the closest point on the wire
        r.sub(pos, position_d);
        v = getDirection();

        rpar.scale(r.dot(v) / (v.length() * v.length()), v);
        rperp.sub(r, rpar);

        bflux = -Math.log(rperp.length()) * (current / (2. * Math.PI));
        return bflux;
    }

    public Vector3d getE(Vector3d x, double t) {
        return getE(x);
    }

    public Vector3d getE(Vector3d x) {
        return new Vector3d();
    }

    // Placeholder. Must return the electric potential.
    public double getEPotential(Vector3d pos) {
        return 0.;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double i) {
        Double old = new Double(current);
        current = i;
        firePropertyChange("current", old, new Double(current));

        // This needs to be set now that there is an arrow on the wire that indicates the direction of the current
        renderFlags |= GEOMETRY_CHANGE;

        if (theEngine != null)
        //synchronized (theEngine){
            theEngine.requestSpatial();
        //}
    }

    public double getInductance() {
        return inductance;
    }

    public void setInductance(double i) {
        Double old = new Double(inductance);
        inductance = i;
        firePropertyChange("inductance", old, new Double(inductance));

        if (theEngine != null) theEngine.requestSpatial();
    }

    public double getLength() {
        return length;
    }

    public void setLength(double h) {
        length = h;
        renderFlags |= GEOMETRY_CHANGE;
        if (theEngine != null) theEngine.requestSpatial();
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double r) {
        radius = r;
        renderFlags |= GEOMETRY_CHANGE;
        if (theEngine != null) theEngine.requestRefresh();
    }

    public void render() {
        if (mNode != null) {
            if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {

                if (mNode != null) {
                    
                    ((WireNode) mNode).setArrowDirection(current);
                }
                renderFlags ^= GEOMETRY_CHANGE;
                

            }
            super.render();
        }
    }

    protected TNode3D makeNode() {
        TNode3D node = new WireNode(length, radius);
        return node;
    }

}
