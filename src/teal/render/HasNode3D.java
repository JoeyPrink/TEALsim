/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasNode3D.java,v 1.2 2007/07/16 22:04:51 pbailey Exp $ 
 * 
 */

package teal.render;

import teal.render.scene.TNode3D;

/**
 * Objects that have a modifiable color should implement this interface.
 */
public interface HasNode3D {
    public TNode3D getNode3D();
    public void setNode3D(TNode3D n);
}
