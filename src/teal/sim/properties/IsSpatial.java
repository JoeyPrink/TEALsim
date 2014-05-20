/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: IsSpatial.java,v 1.4 2007/07/16 22:05:08 pbailey Exp $ 
 * 
 */

package teal.sim.properties;


/** All EM objects which have a point  charge should implement this interface.
 */
public interface  IsSpatial
{
    public void nextSpatial();
	public void needsSpatial();
}
