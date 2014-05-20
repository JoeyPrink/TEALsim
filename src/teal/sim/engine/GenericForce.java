/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GenericForce.java,v 1.2 2007/07/16 22:05:01 pbailey Exp $ 
 * 
 */

package teal.sim.engine;

import javax.vecmath.*;

import teal.sim.*;
import teal.sim.properties.*;

/**
 * Objects that represent a force.
 */
public class GenericForce extends SimObj implements ForceModel {

    private static final long serialVersionUID = 3834025879046535224L;

    protected Vector3d force;
    protected boolean isActive = true;

    public GenericForce() {
        force = new Vector3d();
    }

    public GenericForce(Vector3d f) {
        force = new Vector3d(f);
    }

    /**
     * <code>getForce</code> returns the instanteous force on a physical object.
     * and time.
     */
    public Vector3d getForce(PhysicalElement phys) {
        return force;
    }

    /**
     * <code>getForce</code> returns the force at a position
     *
     */

    public Vector3d getForce(Vector3d position) {
        return force;
    }

    /**
     * <code>getForce</code> returns the force 
     *
     */

    public Vector3d getForce() {
        return force;
    }

    public void setForce(Vector3d f) {
        force = f;
    }

    /**
     * <code>isActive/code> returns whether or not you are
     * currently exerting a force.
     *
     */
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean b) {
        isActive = b;
    }
}
