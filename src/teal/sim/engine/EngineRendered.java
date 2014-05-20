/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: EngineRendered.java,v 1.11 2010/08/18 20:45:44 stefan Exp $ 
 * 
 */

package teal.sim.engine;

import java.awt.Component;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.*;

import javax.media.j3d.Transform3D;
import javax.vecmath.*;

import teal.core.*;
import teal.sim.*;

/**
 * The base for any object which may be Rendered in a TEAL TSimulation
 *
 * @author Phil Bailey
 * @version $Revision: 1.11 $ 
 */

public class EngineRendered extends SimRendered implements HasSimEngine, Referenced {

    private static final long serialVersionUID = 4048795675682419249L;
    
    protected ArrayList<HasReference> mReferents = null;
    protected TSimEngine theEngine = null;

    public void setSimEngine(TSimEngine engine) {
        theEngine = engine; 
    }

    public TSimEngine getSimEngine() {
        return theEngine;
    }

    protected boolean checkEngine() {
        boolean status = false;
        if (theEngine != null) {
            status = true;
        }
        return status;
    }

    /* (non-Javadoc)
     * @see teal.render.Rendered#setPosition(javax.vecmath.Vector3d, boolean)
     */
    public void setPosition(Vector3d pos, boolean trigger) {
        super.setPosition(pos, trigger);
        if (theEngine != null) {
        	theEngine.requestSpatial();
        	theEngine.requestRefresh();
        }

    }

    public void setRotation(Quat4d rot) {
        setRotation(rot, true);
    }

    public void setRotation(Matrix3d rot) {
        setRotation(rot, true);
    }

    public void setRotation(Quat4d rot, boolean sendPCE) {
        super.setRotation(rot, sendPCE);
        if (theEngine != null) {
        	theEngine.requestSpatial();
        	theEngine.requestRefresh();
        }
    }

    public void setRotation(Matrix3d rot, boolean sendPCE) {
        super.setRotation(rot, sendPCE);
        if (theEngine != null) {
        	theEngine.requestSpatial();
        	theEngine.requestRefresh();
        }
    }

    public void setDirection(Vector3d newDirection) {
        super.setDirection(newDirection);
        if (theEngine != null) {
        	theEngine.requestSpatial();
        	theEngine.requestRefresh();
        }
    }

    /**
     * Utility to support HasReference classes should only 
     * be called by the referencing object.
     */
    public void addReferent(HasReference ref) {
        if (mReferents == null) mReferents = new ArrayList<HasReference>();
        mReferents.add(ref);
    }

    /**
     * Utility to support HasReference classes should only 
     * be called by the referencing object.
     */
    public void removeReferent(HasReference ref) {
        if (mReferents != null) {
            mReferents.remove(ref);
            if (mReferents.isEmpty()) mReferents = null;
        }
    }

    /**
     * The published method to remove the link to HasReference
     * objects that reference this element. Should be called if
     * Element is removed from the world
     */
    public void removeReferents() {
        if (mReferents == null)
            return;
        else {
            ArrayList<HasReference> tmp = new ArrayList<HasReference>(mReferents);
            Iterator<HasReference> it = tmp.iterator();
            while (it.hasNext()) {
                HasReference obj = (HasReference) it.next();
                obj.removeReference(this);
            }
        }

    }

    /**
     * Provides a copy of the current referents.
     */
    public Collection<HasReference> getReferents() {
        if (mReferents == null)
            return null;
        else return (Collection<HasReference>) mReferents.clone();
    }
}
