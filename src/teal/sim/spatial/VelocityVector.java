/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: VelocityVector.java,v 1.10 2007/07/17 21:00:54 pbailey Exp $ 
 * 
 */

package teal.sim.spatial;

import teal.physics.physical.PhysicalObject;

public class VelocityVector extends SpatialVector {

    private static final long serialVersionUID = 3905807487092537653L;

    public VelocityVector() {
    }

    public VelocityVector(PhysicalObject x) {
        setPhysicalObject(x);
    }

    public void nextSpatial() {
        value.set(object.getVelocity());
        registerRenderFlag(GEOMETRY_CHANGE);
        
    }

}
