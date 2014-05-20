/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CylindricalWallCollisionController.java,v 1.12 2010/07/16 22:30:35 stefan Exp $ 
 * 
 */

package teal.sim.collision;

import javax.vecmath.*;

import teal.sim.engine.*;

/**
 * Collision controller for cylindrically shaped objects.  Defines how to resolve collisions with cylinders.
 */
public class CylindricalWallCollisionController extends CollisionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5435803360560413078L;

	protected double radius = 0.0;
	protected Vector3d direction = new Vector3d(0.,1.,0.);
	
	public CylindricalWallCollisionController(HasCollisionController oobject) {
		super(oobject);
	}
	
	public CylindricalWallCollisionController( CollisionController cg ) {
		super(cg);
	}

	public void setRadius(double rradius) {
		radius = rradius;
	}

	public double getRadius() {
		return radius;
	}	

	public int collisionStatus( CollisionController x ) {
		int status = 0;
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
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			//
			Vector3d kernelDistance = new Vector3d(direction);
			kernelDistance.scale(relativeDistance.dot(direction));
			relativeDistance.sub(kernelDistance);
			//
			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
			relativeVelocity.sub(object.getVelocity2());
			Vector3d incident = new Vector3d(relativeDistance );
			incident.normalize();
			incident.scale((relativeDistance.length()-radius>=0.)?1.:-1.);
			// double radiiSum = (1.-tol)*(radius + y.getRadius());
			// double separation = relativeDistance.length() - radiiSum;
			double separation = Math.abs(relativeDistance.length()-radius) - y.getRadius()*(1.-tol);
			double incidentVelocity = incident.dot(relativeVelocity);

			// Touch
			// boolean doesTouch = (separation < tol * radiiSum) && (separation >= 0.);
			boolean doesTouch = (separation < tol *  y.getRadius()) && (separation >= 0.);
			
			// Interpenetration
			boolean doesInterpenetrate = separation < 0.;

			// Adherence
			double deltaTime = 1.;
			if(object instanceof HasSimEngine) {
				deltaTime =((HasSimEngine) object).getSimEngine().getDeltaTime();
			}
			// Hardcoded tolerance proportionality constant.
			double veltol = 0.1*tolerance/deltaTime;
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
			
			// System.out.println("Separation: " + separation + " doesPrecipitate: " + doesPrecipitate );
			
		}
		
		return status;
	}

	public boolean interpenetrates( CollisionController x ) {
		boolean doesInterpenetrate = false;
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
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			//
			Vector3d kernelDistance = new Vector3d(direction);
			kernelDistance.scale(relativeDistance.dot(direction));
			relativeDistance.sub(kernelDistance);
			//
			
//			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
//			relativeVelocity.sub(object.getVelocity2());
//			Vector3d incident = new Vector3d(relativeDistance );
//			incident.normalize();
//			incident.scale((relativeDistance.length()-radius>=0.)?1.:-1.);

			// double radiiSum = (1.-tol)*(radius + y.getRadius());
			// double separation = relativeDistance.length() - radiiSum;
			double separation = Math.abs(relativeDistance.length()-radius) - y.getRadius()*(1.-tol);
//			double incidentVelocity = incident.dot(relativeVelocity);

			// Interpenetration
			doesInterpenetrate = separation < 0.;

		}
		
		return doesInterpenetrate;
	}

	public boolean collides( CollisionController x ) {
		boolean doesCollide = false;
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
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			//
			Vector3d kernelDistance = new Vector3d(direction);
			kernelDistance.scale(relativeDistance.dot(direction));
			relativeDistance.sub(kernelDistance);
			//
			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
			relativeVelocity.sub(object.getVelocity2());
			Vector3d incident = new Vector3d(relativeDistance );
			incident.normalize();
			incident.scale((relativeDistance.length()-radius>=0.)?1.:-1.);
			// double radiiSum = (1.-tol)*(radius + y.getRadius());
			// double separation = relativeDistance.length() - radiiSum;
			double separation = Math.abs(relativeDistance.length()-radius) - y.getRadius()*(1.-tol);
			double incidentVelocity = incident.dot(relativeVelocity);

			// Touch
			// boolean doesTouch = (separation < tol * radiiSum) && (separation >= 0.);
			boolean doesTouch = (separation < tol *  y.getRadius()) && (separation >= 0.);

			// Precipitation
			boolean doesPrecipitate = ( incidentVelocity < 0. );

			// Collision 
			doesCollide = doesTouch && doesPrecipitate;
		
		}
		
		return doesCollide;
	}

	public boolean adheres( CollisionController x ) {
		boolean doesAdhere = false;
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
			Vector3d relativeDistance = new Vector3d(y.object.getPosition2());
			relativeDistance.sub(object.getPosition2());
			//
			Vector3d kernelDistance = new Vector3d(direction);
			kernelDistance.scale(relativeDistance.dot(direction));
			relativeDistance.sub(kernelDistance);
			//
			Vector3d relativeVelocity = new Vector3d(y.object.getVelocity2());
			relativeVelocity.sub(object.getVelocity2());
			Vector3d incident = new Vector3d(relativeDistance );
			incident.normalize();
			incident.scale((relativeDistance.length()-radius>=0.)?1.:-1.);
			// double radiiSum = (1.-tol)*(radius + y.getRadius());
			// double separation = relativeDistance.length() - radiiSum;
			double separation = Math.abs(relativeDistance.length()-radius) - y.getRadius()*(1.-tol);
			double incidentVelocity = incident.dot(relativeVelocity);

			// Touch
			// boolean doesTouch = (separation < tol * radiiSum) && (separation >= 0.);
			boolean doesTouch = (separation < tol *  y.getRadius()) && (separation >= 0.);

			// Adherence
			double deltaTime = 1.;
			if(object instanceof HasSimEngine) {
				deltaTime =((HasSimEngine) object).getSimEngine().getDeltaTime();
			}
			// Hardcoded tolerance proportionality constant.
			double veltol = 0.1*tol/deltaTime;
			doesAdhere = (incidentVelocity < veltol) && (incidentVelocity >= 0.) && doesTouch;

		}
		
		return doesAdhere;
	}
	

	public void resolveCollision( CollisionController x ) {
		if( x instanceof SphereCollisionController ) {

			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;

			// Are we performing wall to sphere collision?
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) {
				// If not, return empty status.
				return;
			}

			// A wall doesn't have a velocity, but the below is there for generality.
			Vector3d v1 = object.getVelocity2();
			Vector3d v2 = y.object.getVelocity2();

			Vector3d x1 = object.getPosition2();
			Vector3d x2 = y.object.getPosition2();
			Vector3d lineOfAction = new Vector3d();
			lineOfAction.sub(x2,x1);
			//
			Vector3d kernelDistance = new Vector3d(direction);
			kernelDistance.scale(lineOfAction.dot(direction));
			lineOfAction.sub(kernelDistance);
			//
			lineOfAction.normalize();

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
		if( x instanceof SphereCollisionController ) {
			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;

			// Are we performing wall to sphere collision?
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) {
				// If not, return empty status.
				return;
			}

			Vector3d x1 = object.getPosition2();
			Vector3d x2 = y.object.getPosition2();
			Vector3d relativeDistance = new Vector3d();
			relativeDistance.sub(x2,x1);
			//
			Vector3d kernelDistance = new Vector3d(direction);
			kernelDistance.scale(relativeDistance.dot(direction));
			relativeDistance.sub(kernelDistance);
			//
			double separation = Math.abs(relativeDistance.length()-radius) - y.getRadius();

			if(separation>0.) return;
			
			Vector3d normal = new Vector3d(relativeDistance);
			normal.normalize();
			normal.scale((relativeDistance.length()-radius>=0.)?1.:-1.);

			Vector3d correction = new Vector3d(normal);
			correction.scale(-separation);
			y.object.applyCorrection(correction);
			
		}
	}


	/*
	public void resolveCollision( CollisionController x ) {
		if( x instanceof SphereCollisionController ) {
			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;

			// Are we performing wall to sphere collision?
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) {
				// If not, return empty status.
				return;
			}

			Vector3d x1 = object.getPosition2();
			Vector3d x2 = y.object.getPosition2();
			Vector3d lineOfAction = new Vector3d();
			lineOfAction.sub(x2,x1);
			//
			Vector3d kernelDistance = new Vector3d(direction);
			kernelDistance.scale(lineOfAction.dot(direction));
			lineOfAction.sub(kernelDistance);
			//
			lineOfAction.normalize();

			Vector3d v1 = object.getVelocity2();
			Vector3d v2 = y.object.getVelocity2();

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

		}
	}



			
	public void resolveAdherence( CollisionController x ) {
		if( x instanceof SphereCollisionController ) {
			// Cast to proper class.
			SphereCollisionController y = (SphereCollisionController) x;

			// Are we performing wall to sphere collision?
			if( ( y.getMode() & SphereCollisionController.WALL_SPHERE ) == 0) {
				// If not, return empty status.
				return;
			}
			
			double tol = Math.max(tolerance, x.getTolerance());

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
			Vector3d dir = touchDirection(x);
			
			double a1, a2;

//			double m1 = Math.abs(dir.dot(d1));
//			double m2 = Math.abs(dir.dot(d2));
//			if( m1+m2 < Teal.doubleZero ) {
//				a1 = 0.5;
//				a2 = 0.5;
//			} else {
//				a1 = m2/(m1+m2);
//				a2 = m1/(m1+m2);
//			}
			
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
*/


	public void set(CollisionController c) {
		super.set(c);
		if( c instanceof CylindricalWallCollisionController) {
			setRadius(((CylindricalWallCollisionController) c).getRadius());
			setDirection(((CylindricalWallCollisionController) c).getDirection());
		}
	}
	
	public void set_default(HasCollisionController oobject) {
		super.set_default(oobject);
		radius = 1.0;
	}
	
	public CollisionController replica() {
		CylindricalWallCollisionController cg = new CylindricalWallCollisionController(object);
		cg.set(this);
		return cg;
	}


	public Vector3d touchDirection( CollisionController x ) {
		Vector3d tdirection = new Vector3d();
		if( x instanceof SphereCollisionController ) {
			tdirection.set(x.object.getPosition2());
			tdirection.sub(object.getPosition2());
			//
			Vector3d kernelDistance = new Vector3d(direction);
			kernelDistance.scale(tdirection.dot(direction));
			tdirection.sub(kernelDistance);
			//
			tdirection.normalize();
		}
		return tdirection;
	}


	/**
	 * @return Returns the direction.
	 */
	public Vector3d getDirection() {
		return direction;
	}
	/**
	 * @param direction The direction to set.
	 */
	public void setDirection(Vector3d direction) {
		this.direction = direction;
	}
}
