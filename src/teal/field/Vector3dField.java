/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Vector3dField.java,v 1.6 2007/07/16 22:04:46 pbailey Exp $
 * 
 */

package teal.field;

import javax.vecmath.*;

/**
 * Simple specification for any field of 3D double data, 
 * which is accessed through a location within the field.
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.6 $ 
 */

public interface Vector3dField {

    /** 
     * returns a new copy of the value of the field at a the specified location.
     */
    public Vector3d get(Vector3d pos);

    /** 
     * Sets dest to the value of the field at a the specified location, a 
     * reference to dest is returned as a convenience.
     */
    public Vector3d get(Vector3d pos, Vector3d dest);

    /**
     * The application specific field Type.
     */
    public int getType();
}