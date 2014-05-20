/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: AccelerationVector.java,v 1.10 2007/07/17 15:46:59 pbailey Exp $ 
 * 
 */

package teal.sim.spatial;

import teal.physics.physical.PhysicalObject;

public class AccelerationVector extends SpatialVector {

    private static final long serialVersionUID = 3257008743777317171L;

    public AccelerationVector() {
    }

    public AccelerationVector(PhysicalObject x) {
        setPhysicalObject(x);
    }

    public void nextSpatial() {
        value.set(object.getTotalForces());
        value.scale(1. / object.getMass());
        registerRenderFlag(GEOMETRY_CHANGE);
    }

}
