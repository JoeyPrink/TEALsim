/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SphereCollisionController.java,v 1.31 2010/07/16 22:30:35 stefan Exp $ 
 * 
 */

package teal.sim.collision;

import javax.vecmath.Vector3d;

import teal.sim.engine.HasSimEngine;


/**
 * Collision controller for spherical objects.  Defines how to resolve collisions involving spheres.
 */
public class SphereCollisionController extends CollisionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -552547078454267120L;

	protected double radius = 0.0;

	// Collision-mode States
	public static final int SPHERE_SPHERE = 1;
	public static final int WALL_SPHERE = 2;

	protected int mode = SPHERE_SPHERE | WALL_SPHERE ;


	public SphereCollisionController(HasCollisionController oobject) {
		super(oobject);
	}
	
	public SphereCollisionController( CollisionController cg ) {
		super(cg);
	}

	public void setRadius(double rradius) {
		radius = rradius;
	}

	public double getRadius() {
		return radius;
	}	

	public void setMode(int mmode) {
		mode = mmode;
	}

	public int getMode() {
		return mode;
	}	

	public int collisionStatus( CollisionController x ) {
		int status = 0;
		if( x instanceof SphereCollisionController ) {

			// Are we performing sphere to sphere collision?
			if( ( mode & SPHERE_SPHERE ) == 0) {
				// If not, return empty status.
				return status;
			}

			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;
		
			// Prepare required quantities.
			double tol = Math.max(tolerance, y.getTolerance());
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
			relativeVelocity.sub(object.getVelocity2());
			Vector3d incident = new Vector3d(relativeDistance );
			incident.normalize();
			double radiiSum = (1.-tol)*(radius + y.getRadius());
			double separation = relativeDistance.length() - radiiSum;
			double incidentVelocity = incident.dot(relativeVelocity);

			// Touch
			boolean doesTouch = (separation < tol * radiiSum) && (separation >= 0.);

			// Interpenetration
			boolean doesInterpenetrate = separation < 0.;

			// Adherence
			double deltaTime = 1.;
			if(object instanceof HasSimEngine) {
				deltaTime =((HasSimEngine) object).getSimEngine().getDeltaTime();
			}
			// Hardcoded tolerance proportionality constant.
			double veltol = 0.05*tol/deltaTime;
			boolean doesAdhere = (incidentVelocity < veltol) && (incidentVelocity >= -1e-5) && doesTouch;

			// Precipitation
			boolean doesPrecipitate = ( incidentVelocity < 0 );

			// Collision 
			boolean doesCollide = doesTouch && doesPrecipitate && !doesAdhere;
		
			// Update status.
			if( doesInterpenetrate ) 	status = status | INTERPENETRATES;
			if( doesCollide )			status = status | COLLIDES;
			if( doesAdhere )			status = status | ADHERES;
			if( doesTouch ) 			status = status | TOUCHES;
			if( doesPrecipitate )		status = status | PRECIPITATES;
			
			
		} else if ( x instanceof WallCollisionController ) {
			status = x.collisionStatus(this);
		}
		return status;
	}

	public boolean interpenetrates( CollisionController x ) {
		boolean doesInterpenetrate = false;
		if( x instanceof SphereCollisionController ) {

			// Are we performing sphere to sphere collision?
			if( ( mode & SPHERE_SPHERE ) == 0) {
				// If not, return empty status.
				return false;
			}

			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;
		
			// Prepare required quantities.
			double tol = Math.max(tolerance, y.getTolerance());
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
//			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
//			relativeVelocity.sub(object.getVelocity2());
//			Vector3d incident = new Vector3d(relativeDistance );
//			incident.normalize();
			double radiiSum = (1.-tol)*(radius + y.getRadius());
			double separation = relativeDistance.length() - radiiSum;
//			double incidentVelocity = incident.dot(relativeVelocity);

			// Interpenetration
			doesInterpenetrate = separation < 0.;

		} else if ( x instanceof WallCollisionController ) {
			doesInterpenetrate = x.interpenetrates(this);
		}
		
		return doesInterpenetrate;
	}

	public boolean collides( CollisionController x ) {
		boolean doesCollide = false;
		if( x instanceof SphereCollisionController ) {

			// Are we performing sphere to sphere collision?
			if( ( mode & SPHERE_SPHERE ) == 0) {
				// If not, return empty status.
				return false;
			}

			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;
		
			// Prepare required quantities.
			double tol = Math.max(tolerance, y.getTolerance());
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
			relativeVelocity.sub(object.getVelocity2());
			Vector3d incident = new Vector3d(relativeDistance );
			incident.normalize();
			double radiiSum = (1.-tol)*(radius + y.getRadius());
			double separation = relativeDistance.length() - radiiSum;
			double incidentVelocity = incident.dot(relativeVelocity);

			// Touch
			boolean doesTouch = (separation < tol * radiiSum) && (separation >= 0.);

			// Precipitation
			boolean doesPrecipitate = ( incidentVelocity < 0. );

			// Collision 
			doesCollide = doesTouch && doesPrecipitate;
		
		} else if ( x instanceof WallCollisionController ) {
			doesCollide = x.collides(this);
		}
		
		return doesCollide;
	}

	public boolean adheres( CollisionController x ) {
		boolean doesAdhere = false;
		if( x instanceof SphereCollisionController ) {

			// Are we performing sphere to sphere collision?
			if( ( mode & SPHERE_SPHERE ) == 0) {
				// If not, return empty status.
				return false;
			}

			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;
		
			// Prepare required quantities.
			double tol = Math.max(tolerance, y.getTolerance());
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
			relativeVelocity.sub(object.getVelocity2());
			Vector3d incident = new Vector3d(relativeDistance );
			incident.normalize();
			double radiiSum = (1.-tol)*(radius + y.getRadius());
			double separation = relativeDistance.length() - radiiSum;
			double incidentVelocity = incident.dot(relativeVelocity);

			// Touch
			boolean doesTouch = (separation < tol * radiiSum) && (separation >= 0.);

			// Adherence
			double deltaTime = 1.;
			if(object instanceof HasSimEngine) {
				deltaTime =((HasSimEngine) object).getSimEngine().getDeltaTime();
			}
			// Hardcoded tolerance proportionality constant.
			double veltol = 0.05*tol/deltaTime;
			doesAdhere = (incidentVelocity < veltol) && (incidentVelocity >= 0.) && doesTouch;

		} else if ( x instanceof WallCollisionController ) {
			doesAdhere = x.adheres(this);
		}
		return doesAdhere;
	}
	

	public void resolveCollision( CollisionController x ) {
		if( x instanceof SphereCollisionController ) {
			// Are we performing sphere to sphere collision?
			if( ( mode & SPHERE_SPHERE ) == 0) {
				// If not, do not perform collision detection.
				return;
			}
			SphereCollisionController y = (SphereCollisionController) x;

			Vector3d x1 = object.getPosition2();
			Vector3d x2 = y.object.getPosition2();

			Vector3d v1 = object.getVelocity2();
			Vector3d v2 = y.object.getVelocity2();

			Vector3d lineOfAction = new Vector3d();
			lineOfAction.sub(x2,x1); lineOfAction.normalize();


			double e = Math.sqrt(elasticity*y.getElasticity());

			// Attempt to reduce oscillations at relative rest.
//			if( x.object.isAdheredTo(object) ) {
//				e = 0.;
//			}
			
			double m1 = object.getMass();
			double m2 = y.object.getMass();

			double v1_ = v1.dot(lineOfAction);
			double v2_ = v2.dot(lineOfAction);
			
			double vr = v1_-v2_;
			
			double J = -vr*(e+1)/(1/m1+1/m2);
			
			Vector3d impulse1 = new Vector3d(lineOfAction); impulse1.scale(J);
			Vector3d impulse2 = new Vector3d(lineOfAction); impulse2.scale(-J);
			object.applyImpulse(impulse1);
			y.object.applyImpulse(impulse2);
			
			//System.out.println("Resolving Collision...");

		} else if ( x instanceof WallCollisionController ) {
			x.resolveCollision(this);
		}

	}



			
	public void resolveAdherence( CollisionController x ) {
		if( x instanceof SphereCollisionController ) {
			// Are we performing sphere to sphere collision?
			if( ( mode & SPHERE_SPHERE ) == 0) {
				// If not, do not perform collision detection.
				return;
			}
			
			Vector3d x1 = object.getPosition2();
			Vector3d x2 = x.object.getPosition2();

			Vector3d center = new Vector3d(x1);
			center.add(x2);
			center.scale(0.5);

			Vector3d relativeDistance = new Vector3d(x2);
			relativeDistance.sub(x1);
			double distance = relativeDistance.length();

			Vector3d correction1 = new Vector3d(x1);
			correction1.sub(center);
			correction1.normalize();
			
			Vector3d correction2 = new Vector3d(x2);
			correction2.sub(center);
			correction2.normalize();

			double r1 = radius;
			double r2 = ((SphereCollisionController) x).radius;
			
			double length = (r1+r2) - distance;
			
			Vector3d x1_ = object.getPosition1();
			Vector3d x2_ = x.object.getPosition1();
			Vector3d d1 = new Vector3d(x1); d1.sub(x1_);			
			Vector3d d2 = new Vector3d(x2); d2.sub(x2_);
			touchDirection(x);
			
			double a1, a2;

			if( this.pushPriority > x.pushPriority ) {
				a1 = 0.;
				a2 = 1.;
				x.pushPriority = this.pushPriority + 1;
			}
			else if( this.pushPriority < x.pushPriority ) {
				a1 = 1.;
				a2 = 0.;
				this.pushPriority = x.pushPriority + 1;
			} else {
				a1 = 0.5;
				a2 = 0.5;
			}
			
			correction1.scale(length*a1);
			correction2.scale(length*a2);
			
			object.applyCorrection(correction1);
			x.object.applyCorrection(correction2);
		}
	}



	public void set(CollisionController c) {
		super.set(c);
		if( c instanceof SphereCollisionController) {
			setRadius(((SphereCollisionController) c).getRadius());
			setMode(((SphereCollisionController) c).getMode());
		}
	}
	
	public void set_default(HasCollisionController oobject) {
		super.set_default(oobject);
		radius = 1.0;
	}
	
	public CollisionController replica() {
		SphereCollisionController cg = new SphereCollisionController(object);
		cg.set(this);
		return cg;
	}


	public Vector3d touchDirection( CollisionController x ) {
		Vector3d direction = new Vector3d();
		if( x instanceof SphereCollisionController ) {
			direction.set(x.object.getPosition2());
			direction.sub(object.getPosition2());
			direction.normalize();
		} else if ( x instanceof WallCollisionController ) {
			direction = x.touchDirection(this);
			direction.negate();
		}
		return direction;
	}


}
