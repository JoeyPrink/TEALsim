/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasInductance.java,v 1.5 2007/07/17 15:46:55 pbailey Exp $ 
 * 
 */

package teal.physics.em;


/** 
 * All EM objects which have inductance should implement this interface.
 */
 
public interface HasInductance
{

	public double getInductance();
	public void setInductance(double i);

}
