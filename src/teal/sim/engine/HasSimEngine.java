/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasSimEngine.java,v 1.1 2007/12/04 21:00:35 pbailey Exp $ 
 * 
 */

package teal.sim.engine;


/**
* Provides for any object which is directly 
* controled through the SimEngine.
**/
public interface HasSimEngine
{
	/**
	 * Returns the SimEngine for this object.
	 * 
	 * @return the model.
	 */
	public TSimEngine getSimEngine();
	/**
	 * Sets the SimEngine for this object. 
	 * 
	 * @param model
	 */
	public void setSimEngine(TSimEngine model);
}
