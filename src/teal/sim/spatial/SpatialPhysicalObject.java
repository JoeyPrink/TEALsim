/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SpatialPhysicalObject.java,v 1.14 2010/04/09 17:00:09 pbailey Exp $ 
 * 
 */

package teal.sim.spatial;

import java.awt.Color;

import javax.vecmath.Color3f;

import teal.core.HasReference;
import teal.core.Referenced;
import teal.physics.physical.PhysicalObject;

public abstract class SpatialPhysicalObject extends Spatial implements HasReference {

	
    protected PhysicalObject object;

    public SpatialPhysicalObject() {
        super();
        setColor(Color.WHITE);
        object = null;
    }

    public SpatialPhysicalObject(PhysicalObject x) {
        super();
        setColor(Color.WHITE);
        setPhysicalObject(x);
    }

    public void setPhysicalObject(PhysicalObject x) {
        //object = x;
        setReference(x);
        needsSpatial();
    }

    public PhysicalObject getPhysicalObject() {
        return object;
    }
    
    
    /* (non-Javadoc)
	 * @see teal.core.HasReference#addReference(teal.core.Referenced)
	 */
	public void addReference(Referenced elm) {
		this.object = (PhysicalObject)elm;

	}
	/* (non-Javadoc)
	 * @see teal.core.HasReference#getReference()
	 */
	public Referenced getReference() {
		return this.object;
	}
	/* (non-Javadoc)
	 * @see teal.core.HasReference#removeReference(teal.core.Referenced)
	 */
	public void removeReference(Referenced elm) {
		if ((elm != null) && (object != null) && (elm == object)) {
            object.removeReferent(this);
            object = null;
        }

	}
	/* (non-Javadoc)
	 * @see teal.core.HasReference#setReference(teal.core.Referenced)
	 */
	public void setReference(Referenced elm) {
		if (object != null) {
            object.removeReferent(this);
        }
        object = (PhysicalObject)elm;
        if (elm != null) {
            object.addReferent(this);
        }

	}
    
    
}
