/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TWallCollection.java,v 1.1 2010/07/06 19:40:32 pbailey Exp $ 
 * 
 */

package teal.physics.physical;

import java.util.Collection;

import teal.sim.TSimElement;

public interface TWallCollection extends TSimElement{
	public Collection<Wall> getWalls();
}
