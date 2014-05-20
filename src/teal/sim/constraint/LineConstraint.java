/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: LineConstraint.java,v 1.10 2007/07/16 22:05:00 pbailey Exp $ 
 * 
 */

package teal.sim.constraint;

import javax.vecmath.Vector3d;


/**
 * @author mesrob
 *
 */
public class LineConstraint implements Constraint {

	/* We will represent a line in parametric notation:
	 *   x = ax*t + x0;
	 *   y = ay*t + y0;
	 *   z = az*t + z0;
	 * 
	 * Of course, [ax, ay, az] represents a "direction" vector,
	 * and [x0, y0, z0] is some reference "point" on the line.
	 * 
	 * Notice that the reference point is not used explicitly in reaction
	 * calculation. That is because it is assumed that the constrained 
	 * object is already on the line, making the only relevant
	 * information that of the direction.
	 */

	protected Vector3d point = null, direction = null;
	protected Vector3d lastReaction = new Vector3d();

	public LineConstraint( Vector3d ppoint, Vector3d ddirection ) {
		setCoefficients( ppoint, ddirection );
	}

	public void setCoefficients( Vector3d ppoint, Vector3d ddirection ) {
		point = new Vector3d(ppoint);
		direction = new Vector3d(ddirection);
		direction.normalize();
	}	

	public void setPoint( Vector3d ppoint ) {
		point = new Vector3d(ppoint);
	}	
	public Vector3d getPoint() {
		return new Vector3d(point);
	}	

	public void setDirection( Vector3d ddirection ) {
		direction = new Vector3d(ddirection);
		direction.normalize();
	}	
	public Vector3d getDirection() {
		return new Vector3d(direction);
	}	

	public Vector3d getReaction(Vector3d position, Vector3d velocity, Vector3d action, double mass) {
		double effective = action.dot(direction);
		Vector3d reaction = new Vector3d(direction);
		reaction.scaleAdd(-effective, action);
		reaction.negate();
		lastReaction.set(reaction);
		return reaction;
	}
/*
	public Vector3d getVelocity(Vector3d position, Vector3d forced_velocity) {
		double effective = forced_velocity.dot(direction);
		Vector3d velocity = new Vector3d(direction);
		velocity.scale(forced_velocity.length()*(effective>0?1.:-1));
		return velocity;
	}

	public Vector3d getPosition(Vector3d initial_position, Vector3d targeted_position) {
		Vector3d displacement = new Vector3d(targeted_position);
		displacement.sub(point);
		Vector3d effectiveDisplacement = new Vector3d(direction);
		effectiveDisplacement.scale( displacement.dot(direction) );
		Vector3d position = new Vector3d(point);
		position.add(effectiveDisplacement);
		return position;
	}
*/
	public void set_default() {
		setCoefficients(	new Vector3d(0., 0., 0.),
							new Vector3d(1., 0., 0.) );
	}

	public void set( Constraint c ) {
		if( c instanceof LineConstraint ) {
			setCoefficients(	((LineConstraint) c).getPoint(),
								((LineConstraint) c).getDirection() );
		}	
	}
/*
	public Constraint replica() {
		return new LineConstraint(point, direction);
	}
*/
	public Vector3d getLastReaction() {
		return this.lastReaction;
	}
}
