/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PivotConstraint.java,v 1.8 2007/07/16 22:05:00 pbailey Exp $ 
 * 
 */

package teal.sim.constraint;

import javax.vecmath.Vector3d;


public class PivotConstraint implements Constraint {

	/* Tentative pivot constraint. This is equivalent to translational equlibrium.
	 * 
	 * Currently only cancels all forces. Eventually, I would like it to cancel them only
	 * when the object touches the pivot.
	 * 
	 */
	
	protected Vector3d lastReaction = new Vector3d();

	public PivotConstraint() {
	}


	public Vector3d getReaction(Vector3d position, Vector3d velocity, Vector3d action, double mass) {
		Vector3d reaction = new Vector3d(action);
		reaction.negate();
		lastReaction.set(reaction);
		return reaction;
	}
/*
	public Vector3d getVelocity(Vector3d position, Vector3d forced_velocity) {
		return new Vector3d();
	}

	public Vector3d getPosition(Vector3d initial_position, Vector3d targeted_position) {
		return initial_position;
	}
*/
	public void set_default() {
	}

	public void set( Constraint c ) {
		if( c instanceof PivotConstraint ) {
		}	
	}
/*
	public Constraint replica() {
		return new PivotConstraint();
	}
*/
	public Vector3d getLastReaction() {
		return this.lastReaction;
	}
}
