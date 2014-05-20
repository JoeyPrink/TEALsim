/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PlaneConstraint.java,v 1.10 2007/07/16 22:05:00 pbailey Exp $ 
 * 
 */

package teal.sim.constraint;

import javax.vecmath.Vector3d;

import teal.math.Plane;

/**
 * @author mesrob
 *
 */
public class PlaneConstraint implements Constraint {

	/* We will represent a plane using a point and a normal.	 */

	protected Vector3d point = null, normal = null;
	protected Vector3d lastReaction = new Vector3d();

	public PlaneConstraint( Vector3d ppoint, Vector3d nnormal ) {
		setCoefficients( ppoint, nnormal );
	}

    public PlaneConstraint(Plane plane)
    {
        this(plane.getVertex0(),plane.getNormal());
    }
    
	public void setCoefficients( Vector3d ppoint, Vector3d nnormal ) {
		point = new Vector3d(ppoint);
		normal = new Vector3d(nnormal);
		normal.normalize();
	}	

	public void setPoint( Vector3d ppoint ) {
		point = new Vector3d(ppoint);
	}	
	public Vector3d getPoint() {
		return new Vector3d(point);
	}	

	public void setNormal( Vector3d nnormal ) {
		normal = new Vector3d(nnormal);
		normal.normalize();
	}	
	public Vector3d getNormal() {
		return new Vector3d(normal);
	}	

	public Vector3d getReaction(Vector3d position, Vector3d velocity, Vector3d action, double mass) {
		double reactive = action.dot(normal);
		Vector3d reaction = new Vector3d(normal);
		reaction.scale(-reactive);
		lastReaction.set(reaction);
		return reaction;
	}
/*
	public Vector3d getVelocity(Vector3d position, Vector3d forced_velocity) {
		double dump = forced_velocity.dot(normal);
		Vector3d velocity = new Vector3d(normal);
		velocity.scaleAdd(-dump, forced_velocity);
		return velocity;
	}

	public Vector3d getPosition(Vector3d initial_position, Vector3d targeted_position) {
		Vector3d displacement = new Vector3d(targeted_position);
		displacement.sub(point);
		double dump = displacement.dot(normal);
		Vector3d effectiveDisplacement = new Vector3d(normal);
		effectiveDisplacement.scaleAdd( -dump, displacement);
		Vector3d position = new Vector3d(point);
		position.add(effectiveDisplacement);
		return position;
	}
*/
	public void set_default() {
		setCoefficients(	new Vector3d(0., 0., 0.),
							new Vector3d(0., 1., 0.) );
	}

	public void set( Constraint c ) {
		if( c instanceof PlaneConstraint ) {
			setCoefficients(	((PlaneConstraint) c).getPoint(),
								((PlaneConstraint) c).getNormal() );
		}	
	}
/*
	public Constraint replica() {
		return new PlaneConstraint(point, normal);
	}
*/
	
	public Vector3d getLastReaction() {
		return this.lastReaction;
	}
}
