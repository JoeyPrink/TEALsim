/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WallPlaneConstraint.java,v 1.13 2007/07/17 15:46:58 pbailey Exp $ 
 * 
 */

package teal.sim.constraint;

import teal.physics.physical.*;

import javax.vecmath.Vector3d;

/**
 * @author mesrob
 *
 */
public class WallPlaneConstraint implements Constraint {

	/* We will represent a plane using a point and a normal.	 */

	protected Wall wall = null;
	protected Vector3d lastReaction = new Vector3d();

	public WallPlaneConstraint( Wall wwall ) {
		setWall( wwall );
	}

	public void setWall( Wall wwall ) {
		wall = wwall;
	}	

	public Wall getWall() {
		return wall;
	}	

	public Vector3d getReaction(Vector3d position, Vector3d velocity, Vector3d action, double mass) {
		if(!withinWall(position)) {
			System.out.println("WITHIN WALL: false");
			return new Vector3d();
		}
		//System.out.println("WITHIN WALL: true");
		double reactive = action.dot(wall.getNormal());
		Vector3d reaction = new Vector3d(wall.getNormal());
		reaction.scale(-reactive);
		if (reaction != null) lastReaction.set(reaction);
		return reaction;
	}
	
	public Vector3d getReaction2(Vector3d position, Vector3d velocity, Vector3d action, double mass) {
		double dot = velocity.dot(wall.getNormal());
		if (withinWall(position) && dot < 0) {
			
			
				Vector3d nvel = new Vector3d(wall.getNormal());
				nvel.scale(dot);
				nvel.add(velocity);
				return nvel;
			
		} else {
			return velocity;
		}
	}
	
	public Vector3d getLastReaction() {
		return lastReaction;
	}
	
	
/*
	public Vector3d getVelocity(Vector3d position, Vector3d forced_velocity) {
		if(!withinWall(position)) return forced_velocity;
		double dump = forced_velocity.dot(wall.getNormal());
		Vector3d velocity = new Vector3d(wall.getNormal());
		velocity.scaleAdd(-dump, forced_velocity);
		return velocity;
	}

	public Vector3d getPosition(Vector3d initial_position, Vector3d targeted_position) {
		if(!withinWall(initial_position)) return targeted_position;
		Vector3d displacement = new Vector3d(targeted_position);
		displacement.sub(wall.getPosition());
		double dump = displacement.dot(wall.getNormal());
		Vector3d effectiveDisplacement = new Vector3d(wall.getNormal());
		effectiveDisplacement.scaleAdd( -dump, displacement);
		Vector3d position = new Vector3d(wall.getPosition());
		position.add(effectiveDisplacement);
		return position;
	}
*/
	public void set_default() {
	}

	public void set( Constraint c ) {
		if( c instanceof WallPlaneConstraint ) {
			setWall( ((WallPlaneConstraint) c).getWall() );
		}	
	}
/*
	// Not a true (deep) copy, because of the references within wall.
	public Constraint replica() {
		return new WallPlaneConstraint(wall);
	}
*/
	private boolean withinWall(Vector3d pposition) {
		boolean within = false;
		
		Vector3d relativeDistance = pposition;
		relativeDistance.sub(wall.getPosition2());

		boolean onPlane =
			Math.abs( relativeDistance.dot(wall.getNormal()) ) <=
			wall.getCollisionController().getTolerance();

		Vector3d length = wall.getEdge1(); 
        length.normalize();
		Vector3d width = wall.getEdge2(); 
        width.normalize();
		within = onPlane;
		
		return within;
	}

}
