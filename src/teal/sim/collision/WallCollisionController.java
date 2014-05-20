/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WallCollisionController.java,v 1.34 2010/07/16 22:30:36 stefan Exp $ 
 * 
 */

package teal.sim.collision;

import javax.vecmath.*;

import teal.sim.engine.*;
import teal.physics.physical.*;


/**
 * Collision controller for Walls (or any planar objects).  Defines how to resolve collisions involving planes.
 */
public class WallCollisionController extends CollisionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7245781198962700750L;


	public WallCollisionController(HasCollisionController oobject) {
		super(oobject);
	}
	
	public WallCollisionController( CollisionController cg ) {
		super(cg);
	}

	public int collisionStatus( CollisionController x ) {
		int status = 0;
		if( !(object instanceof Wall) ) return status;
		Wall wall = (Wall) object;
		if( x instanceof SphereCollisionController ) {

			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;

			// Are we performing wall to sphere collision?
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) {
				// If not, return empty status.
				return status;
			}

		
			// Prepare required quantities.
			double tol = Math.max(tolerance, y.getTolerance());
			double radius = y.getRadius();
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			// A wall doesn't have a velocity, but the below is there for generality.
			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
			relativeVelocity.sub(object.getVelocity2());
			Vector3d normal = wall.getNormal();
			double distance = relativeDistance.dot(normal);
			Vector3d incident = new Vector3d(normal);
			if( distance < 0. ) incident.negate();
			double incidentVelocity = incident.dot(relativeVelocity);
//			double separation = Math.abs(distance) - (1-tol)*radius;
			double separation = Math.abs(distance) - radius;
			boolean doesPenetratePlane = ( separation < 0. );
			 // "planeRadius" is ideally zero, but will be non-zero due to the tolerance.
			double planeRadius = doesPenetratePlane ? Math.sqrt( radius*radius - distance*distance ) : 0.;
			Vector3d length = wall.getEdge1(); double length_ = length.length(); length.normalize();
			Vector3d width = wall.getEdge2(); double width_ = width.length(); width.normalize();
			boolean withinLength = Math.abs(length.dot(relativeDistance))-(length_/2.+planeRadius) < 0;
			boolean withinWidth = Math.abs(width.dot(relativeDistance))-(width_/2.+planeRadius) < 0;
			boolean withinRange = withinWidth && withinLength;

			// Touch
			boolean lengthEdgeTouch = doesPenetratePlane && ( Math.abs(Math.abs(length.dot(relativeDistance))-(length_/2.+planeRadius)) < tol*radius );
			boolean widthEdgeTouch = doesPenetratePlane && ( Math.abs(Math.abs(width.dot(relativeDistance))-(width_/2.+planeRadius)) < tol*radius );
			boolean planeTouch = ( separation < tol * radius ) && (separation >= 0.) ;
			boolean doesTouch = ( planeTouch && withinRange ) || lengthEdgeTouch || widthEdgeTouch;


			// Interpenetration
			Vector3d pre_relativeDistance = new Vector3d(y.object.getPosition1());
			pre_relativeDistance.sub(wall.getPosition2());
			boolean doesTunnel = pre_relativeDistance.dot(normal) * distance < 0.;
			// The concept of "withinLength" and "withinWidth" should change in the case of tunneling.
			if( doesTunnel ) {
				// Case: Tunneling
				double delta = 1./(1.-distance/pre_relativeDistance.dot(normal));
				Vector3d estimatedPlanePosition = new Vector3d();
				estimatedPlanePosition.set(relativeDistance);
				estimatedPlanePosition.sub(pre_relativeDistance);
				estimatedPlanePosition.scale(delta);
				estimatedPlanePosition.add(pre_relativeDistance);
				withinLength = Math.abs(length.dot(estimatedPlanePosition))-(length_/2.+radius) < 0.;
				withinWidth = Math.abs(width.dot(estimatedPlanePosition))-(width_/2.+radius) < 0.;
				withinRange = withinLength && withinWidth;
				
			}
			boolean doesInterpenetrate = ( doesPenetratePlane || doesTunnel ) && withinRange;

			// Adherence
			double deltaTime = 1.;
			if(y.object instanceof HasSimEngine) {
				deltaTime =((HasSimEngine) y.object).getSimEngine().getDeltaTime();
			}
			double veltol = 0.1*tol/deltaTime;
			boolean doesAdhere = (incidentVelocity < veltol) && (incidentVelocity >= 0.) && doesTouch;

			// Precipitation
			boolean doesPrecipitate = ( incidentVelocity < 0. );

			// Collision 
			boolean doesCollide = doesTouch && doesPrecipitate;
		
			// Update status.
			if( doesInterpenetrate ) 	status = status | INTERPENETRATES;
			if( doesCollide )			status = status | COLLIDES;
			if( doesAdhere )			status = status | ADHERES;
			if( doesTouch ) 			status = status | TOUCHES;
			if( doesPrecipitate )		status = status | PRECIPITATES;
			
		}
		
		return status;
	}

	public boolean interpenetrates ( CollisionController x ) {
		boolean doesInterpenetrate = false;
		if( !(object instanceof Wall) ) return false;
		Wall wall = (Wall) object;
		if( x instanceof SphereCollisionController ) {

			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;

			// Are we performing wall to sphere collision?
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) {
				// If not, return empty status.
				return false;
			}

		
			// Prepare required quantities.
			double radius = y.getRadius();
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			Vector3d normal = wall.getNormal();
			double distance = relativeDistance.dot(normal);
			double separation = Math.abs(distance) - radius;
			boolean doesPenetratePlane = ( separation < 0. );

			// "planeRadius" is ideally zero, but will be non-zero due to the tolerance.
			double planeRadius = doesPenetratePlane ? Math.sqrt( radius*radius - distance*distance ) : 0.;
			Vector3d length = wall.getEdge1(); double length_ = length.length(); length.normalize();
			Vector3d width = wall.getEdge2(); double width_ = width.length(); width.normalize();
			boolean withinLength = Math.abs(length.dot(relativeDistance))-(length_/2.+planeRadius) < 0;
			boolean withinWidth = Math.abs(width.dot(relativeDistance))-(width_/2.+planeRadius) < 0;
			boolean withinRange = withinWidth && withinLength;

			// Interpenetration
			Vector3d pre_relativeDistance = new Vector3d(y.object.getPosition1());
			pre_relativeDistance.sub(wall.getPosition2());
			boolean doesTunnel = pre_relativeDistance.dot(normal) * distance < 0.;
			// The concept of "withinLength" and "withinWidth" should change in the case of tunneling.
			if( doesTunnel ) {
				// Case: Tunneling
				double delta = 1./(1.-distance/pre_relativeDistance.dot(normal));
				Vector3d estimatedPlanePosition = new Vector3d();
				estimatedPlanePosition.set(relativeDistance);
				estimatedPlanePosition.sub(pre_relativeDistance);
				estimatedPlanePosition.scale(delta);
				estimatedPlanePosition.add(pre_relativeDistance);
				withinLength = Math.abs(length.dot(estimatedPlanePosition))-(length_/2.+radius) < 0.;
				withinWidth = Math.abs(width.dot(estimatedPlanePosition))-(width_/2.+radius) < 0.;
				withinRange = withinLength && withinWidth;
				
			}
			doesInterpenetrate = ( doesPenetratePlane || doesTunnel ) && withinRange;

		
		}
		
		return doesInterpenetrate;
	}

	public boolean collides( CollisionController x ) {
		boolean doesCollide = false;
		if( !(object instanceof Wall) ) return false;
		Wall wall = (Wall) object;
		if( x instanceof SphereCollisionController ) {

			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;

			// Are we performing wall to sphere collision?
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) {
				// If not, return empty status.
				return false;
			}

		
			// Prepare required quantities.
			double tol = Math.max(tolerance, y.getTolerance());
			double radius = y.getRadius();
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			// A wall doesn't have a velocity, but the below is there for generality.
			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
			relativeVelocity.sub(object.getVelocity2());
			Vector3d normal = wall.getNormal();
			double distance = relativeDistance.dot(normal);
			Vector3d incident = new Vector3d(normal);
			if( distance < 0. ) incident.negate();
			double incidentVelocity = incident.dot(relativeVelocity);
//			double separation = Math.abs(distance) - (1-tol)*radius;
			double separation = Math.abs(distance) - radius;
			boolean doesPenetratePlane = ( separation < 0. );
			 // "planeRadius" is ideally zero, but will be non-zero due to the tolerance.
			double planeRadius = doesPenetratePlane ? Math.sqrt( radius*radius - distance*distance ) : 0.;
			Vector3d length = wall.getEdge1(); double length_ = length.length(); length.normalize();
			Vector3d width = wall.getEdge2(); double width_ = width.length(); width.normalize();
			boolean withinLength = Math.abs(length.dot(relativeDistance))-(length_/2.+planeRadius) < 0;
			boolean withinWidth = Math.abs(width.dot(relativeDistance))-(width_/2.+planeRadius) < 0;
			boolean withinRange = withinWidth && withinLength;

			// Touch
			boolean lengthEdgeTouch = doesPenetratePlane && ( Math.abs(Math.abs(length.dot(relativeDistance))-(length_/2.+planeRadius)) < tol*radius );
			boolean widthEdgeTouch = doesPenetratePlane && ( Math.abs(Math.abs(width.dot(relativeDistance))-(width_/2.+planeRadius)) < tol*radius );
			boolean planeTouch = ( separation < tol * radius ) && (separation >= 0.) ;
			boolean doesTouch = ( planeTouch && withinRange ) || lengthEdgeTouch || widthEdgeTouch;

			// Precipitation
			boolean doesPrecipitate = ( incidentVelocity < 0. );

			// Collision 
			doesCollide = doesTouch && doesPrecipitate;
		
		}
		
		return doesCollide;
	}

	public boolean adheres( CollisionController x ) {
		boolean doesAdhere = false;
		if( !(object instanceof Wall) ) return false;
		Wall wall = (Wall) object;
		if( x instanceof SphereCollisionController ) {

			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;

			// Are we performing wall to sphere collision?
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) {
				// If not, return empty status.
				return false;
			}

		
			// Prepare required quantities.
			double tol = Math.max(tolerance, y.getTolerance());
			double radius = y.getRadius();
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			// A wall doesn't have a velocity, but the below is there for generality.
			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
			relativeVelocity.sub(object.getVelocity2());
			Vector3d normal = wall.getNormal();
			double distance = relativeDistance.dot(normal);
			Vector3d incident = new Vector3d(normal);
			if( distance < 0. ) incident.negate();
			double incidentVelocity = incident.dot(relativeVelocity);
//			double separation = Math.abs(distance) - (1-tol)*radius;
			double separation = Math.abs(distance) - radius;
			boolean doesPenetratePlane = ( separation < 0. );
			 // "planeRadius" is ideally zero, but will be non-zero due to the tolerance.
			double planeRadius = doesPenetratePlane ? Math.sqrt( radius*radius - distance*distance ) : 0.;
			Vector3d length = wall.getEdge1(); double length_ = length.length(); length.normalize();
			Vector3d width = wall.getEdge2(); double width_ = width.length(); width.normalize();
			boolean withinLength = Math.abs(length.dot(relativeDistance))-(length_/2.+planeRadius) < 0;
			boolean withinWidth = Math.abs(width.dot(relativeDistance))-(width_/2.+planeRadius) < 0;
			boolean withinRange = withinWidth && withinLength;

			// Touch
			boolean lengthEdgeTouch = doesPenetratePlane && ( Math.abs(Math.abs(length.dot(relativeDistance))-(length_/2.+planeRadius)) < tol*radius );
			boolean widthEdgeTouch = doesPenetratePlane && ( Math.abs(Math.abs(width.dot(relativeDistance))-(width_/2.+planeRadius)) < tol*radius );
			boolean planeTouch = ( separation < tol * radius ) && (separation >= 0.) ;
			boolean doesTouch = ( planeTouch && withinRange ) || lengthEdgeTouch || widthEdgeTouch;


			// Adherence
			double deltaTime = 1.;
			if(y.object instanceof HasSimEngine) {
				deltaTime =((HasSimEngine) y.object).getSimEngine().getDeltaTime();
			}
			double veltol = 0.1*tol/deltaTime;
			doesAdhere = (incidentVelocity < veltol) && (incidentVelocity >= 0.) && doesTouch;

		}
		
		return doesAdhere;
	}


	public void resolveCollision( CollisionController x ) {
		if( !(object instanceof Wall) ) return;
		Wall wall = (Wall) object;
		if( x instanceof SphereCollisionController ) {

			SphereCollisionController y = (SphereCollisionController) x;
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) return;

			// A wall doesn't have a velocity, but the below is there for generality.
			Vector3d v1 = object.getVelocity2();
			Vector3d v2 = y.object.getVelocity2();

			Vector3d lineOfAction = wall.getNormal();

			// Walls have infinite mass too, but that I have accounted for here,
			// in order to avoid dealing with infinites.
			double m2 = y.object.getMass();

			double v1_ = v1.dot(lineOfAction);
			double v2_ = v2.dot(lineOfAction);
			
			double vr = v1_-v2_;
			double e = Math.sqrt(elasticity*y.getElasticity());
			
			// Attempt to reduce oscillations at relative rest.
//			if( x.object.isAdheredTo(object) ) {
//				e = 0.;
//			}
			
			double J = -vr*(e+1)/(0.+1/m2);
			
//			Vector3d impulse1 = new Vector3d(lineOfAction); impulse1.scale(J);
			Vector3d impulse2 = new Vector3d(lineOfAction); impulse2.scale(-J);
//			object.applyImpulse(impulse1);
			y.object.applyImpulse(impulse2);

		}

	}


	public void resolveAdherence( CollisionController x ) {
		if( !(object instanceof Wall) ) return;
		Wall wall = (Wall) object;
		if( x instanceof SphereCollisionController ) {
			SphereCollisionController y = (SphereCollisionController) x;
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) return;
			Vector3d normal = wall.getNormal();

			double radius = y.getRadius();
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(wall.getPosition2());
			double distance = relativeDistance.dot(normal);
			
			Vector3d correction = new Vector3d(normal);
			double length = (distance>0.?1.:-1.)*radius*(1+tolerance) - distance;
			correction.scale(length);
			y.object.applyCorrection(correction);
			
		}
	}


	public CollisionController replica() {
	 	WallCollisionController cg = new WallCollisionController(object);
		cg.set(this);
		return cg;
	}


	public Vector3d touchDirection( CollisionController x ) {
		if( !(object instanceof Wall) ) return new Vector3d();
		Wall wall = (Wall) object;
		Vector3d direction = new Vector3d();
		if( x instanceof SphereCollisionController ) {
			direction.set(x.object.getPosition2());
			direction.sub(object.getPosition2());
			Vector3d normal = wall.getNormal();
			double distance = direction.dot(normal);
			direction.set(normal);
			if( distance < 0. ) direction.negate();
		}
		return direction;
	}

}
