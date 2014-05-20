/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Constraint.java,v 1.2 2007/07/16 22:05:00 pbailey Exp $ 
 * 
 */

package teal.sim.constraint;

import javax.vecmath.*;

/**
 * @author mesrob
 *
 */
public interface Constraint {

	/**
	 * This method should return the reaction (force) required to keep the constrained object constrained. 
	 * 
	 * @param position position of constrained object.
	 * @param velocity velocity of constrained object.
	 * @param action external force on object.
	 * @param mass mass of object.
	 * 
	 * @return reactive force.
	 */
	abstract public Vector3d getReaction(Vector3d position, Vector3d velocity, Vector3d action, double mass);
	//abstract public Vector3d getReaction2(Vector3d position, Vector3d velocity, Vector3d action, double mass);
	abstract public Vector3d getLastReaction();
	/**
	 * Sets the values of this Constraint to those of the one passed as an argument.
	 * 
	 * @param c
	 */
	abstract public void set(Constraint c);

	abstract public void set_default();

}
