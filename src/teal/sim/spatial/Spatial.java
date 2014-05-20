/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Spatial.java,v 1.19 2007/07/16 22:05:10 pbailey Exp $ 
 * 
 */

package teal.sim.spatial;

import teal.sim.engine.EngineRendered;
import teal.sim.properties.IsSpatial;

/**
 * Spatial provides a base class for field representations.
 *
 *
 * @author Phil Bailey - Center for Educational Computing Initiatives / MIT
 */

public abstract class Spatial extends EngineRendered implements IsSpatial
{

	protected boolean mNeedsSpatial = false;
	
	public void needsSpatial()
	{
		mNeedsSpatial = true;
		if(theEngine != null)
		    theEngine.requestRefresh();
	}
	
    abstract public void nextSpatial();
    
    public void setDrawn(boolean b){
        needsSpatial();
        super.setDrawn(b);
    }

}
