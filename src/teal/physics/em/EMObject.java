/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: EMObject.java,v 1.9 2007/07/17 15:46:54 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.*;

import teal.physics.physical.PhysicalObject;

/**
 * Provides a common base for all Electro-Magnetic classes, currently only 
 * used to trigger Spatial changes.
 */
public class EMObject extends PhysicalObject {

    private static final long serialVersionUID = 3258408426375033651L;

    public void setPosition(Vector3d pos, boolean sendPCE) {
        if (theEngine != null) theEngine.requestSpatial();
        super.setPosition(pos, sendPCE);

    }

    public void setRotation(Quat4d rot, boolean sendPCE) {
        if (theEngine != null) theEngine.requestSpatial();
        super.setRotation(rot, sendPCE);
    }
}
