/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Potential.java,v 1.6 2010/07/21 21:54:28 stefan Exp $
 * 
 */

package teal.field;

import java.io.Serializable;

import javax.vecmath.*;

/**
 * A generic wrapper class for a Vector3dField which gets the potential of the field.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.6 $ 
 */

public class Potential implements Vector3dField, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2069612915538191066L;
	
	private int type = Field.EP_FIELD;
    private Vector3dField field = null;
    private Vector3d normal = new Vector3d(0, 0, 1);

    public Potential(Vector3dField ffield) {
        setField(ffield);
    }

    public Potential(Vector3dField ffield, Vector3d nnormal) {
        setField(ffield);
        setNormal(nnormal);
    }

    public void setField(Vector3dField ffield) {
        field = ffield;
        if (ffield.getType() == Field.E_FIELD) {
            type = Field.EP_FIELD;
        }
    }

    public int getType() {
        return type;
    }

    public void setNormal(Vector3d nnormal) {
        normal = nnormal;
    }

    public Vector3d getNormal() {
        return normal;
    }

    public Vector3d get(Vector3d pos) {
        Vector3d value = new Vector3d();
        return get(pos, value);
    }

    public Vector3d get(Vector3d pos, Vector3d value) {
        if (field != null) {
            value.cross(field.get(pos), normal);
        }
        return value;
    }

}
