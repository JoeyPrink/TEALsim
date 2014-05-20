/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ForceVector.java,v 1.10 2007/07/17 21:00:54 pbailey Exp $ 
 * 
 */

package teal.sim.spatial;

import teal.physics.physical.PhysicalObject;

public class ForceVector extends SpatialVector {

    private static final long serialVersionUID = 3691037651750893363L;

    public ForceVector() {
    }

    public ForceVector(PhysicalObject x) {
        setPhysicalObject(x);
    }

    public void nextSpatial() {
        value.set(object.getTotalForces());
        registerRenderFlag(GEOMETRY_CHANGE);
    }

}
