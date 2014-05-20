/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CollisionController.java,v 1.14 2010/07/16 22:30:35 stefan Exp $ 
 * 
 */

package teal.sim.collision;

import java.io.Serializable;

import javax.vecmath.*;

/**
 * Abstract class for collision controllers, which define how to resolve collisions between objects.
 */
public abstract class CollisionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8204554976460926886L;
	
	protected double tolerance = 0.0;
	protected double elasticity = 1.0;
	protected HasCollisionController object = null;
	
	public int pushPriority = 0;
	

	public static final int INTERPENETRATES = 1, COLLIDES = 2, ADHERES = 4, TOUCHES = 8, PRECIPITATES = 16;

	/**
	 * Creates a CollisionController attached to the supplied object.
	 * 
	 * @param oobject object to which Controller should be attached.
	 */
	public CollisionController(HasCollisionController oobject) {
		set_default(oobject);
	}
	
	public CollisionController(CollisionController c) {
		set(c);
	}

	/**
	 * Sets the collision tolerance for this controller.  Tolerance determines the precision required to trigger a collision.
	 * 
	 * @param ttolerance new tolerance (should be greater than zero).
	 */
	final public void setTolerance(double ttolerance) {
		tolerance = ttolerance;
	}
	/**
	 * Returns the tolerance of the CollisionController.
	 * 
	 * @return tolerance.
	 */
	final public double getTolerance() {
		return tolerance;
	}
	/**
	 * Sets the elasticity of this controller.  Elasticity determines the amount of energy lost in a collision.  An 
	 * elasticity of 1.0 corresponds to a perfectly elastic collision, while a value of zero corresponds to a completely
	 * inelastic collision.
	 * 
	 * @param eelasticity
	 */
	final public void setElasticity(double eelasticity) {
		elasticity = eelasticity;
	}
	/**
	 * Returns the elasticity of this CollisionController.
	 * 
	 * @return elasticity.
	 */
	final public double getElasticity() {
		return elasticity;
	}
	
	
	/**
	 * Sets the object to which this CollisionController is attached.
	 * 
	 * @param oobject object.
	 */
	final public void setObject(HasCollisionController oobject) {
		object = oobject;
	}
	final public HasCollisionController getObject() {
		return object;
	}

	/**
	 * This method should return the state of collision between this CollisionController and the one passed as an argument.
	 * The returned value should be an ORed int of the possible collision states (not colliding, colliding, interpenetrating,
	 * adhering, etc).  The method is abstract, as the details of calculating these states depends on the types of 
	 * CollisionControllers involved.
	 * 
	 * @param x the CollisionController to test against.
	 * @return an ORed int representing the collision state.
	 */
	abstract public int collisionStatus( CollisionController x );
	/**
	 * This method should test whether this CollisionController interpenetrates with the one passed as an argument.  
	 * Interpenetration is defined as an overlap of the colliding geometry outside the tolerance range for a successful
	 * collision.  Interpenetration needs to be resolved in the collision tracking phase.
	 * 
	 * @param x the CollisionController to test against.
	 * @return do these CollisionControllers interpenetrate?
	 */
	abstract public boolean interpenetrates( CollisionController x );
	/**
	 * This method should test whether this CollisionController collides with the one passed as an argument.
	 * Collision is defined as contact between two two geometries within the specified tolerance range.
	 * 
	 * @param x the CollisionController to test against.
	 * @return are these CollisionControllers colliding?
	 */
	abstract public boolean collides( CollisionController x );
	/**
	 * This method should test whether this CollisionController is adhered to the one passed as an argument.
	 * Adherence is defined as resting contact between the two geometries, and is used to resolve collisions between
	 * objects that are in constant contact.
	 * 
	 * @param x the CollisionController to test against.
	 * @return are these CollisionControllers adhered to one another?
	 */
	abstract public boolean adheres( CollisionController x );
	
	/**
	 * This should return a vector pointing from this CollisionController to the one passed as an argument.
	 * 
	 * @param x the CollisionController to which you want to find the touch direction.
	 * @return a vector representing this direction.
	 */
	abstract public Vector3d touchDirection( CollisionController x );

	/**
	 * Provided a collision has occured, this method should resolve the collision between this CollisionController and the
	 * one passed as an argument.  Resolution of a collision involves calculating reflected trajectories, impulses, etc., and
	 * depends completely on the geometry of the CollisionControllers involved.
	 * 
	 * @param x the CollisionController with which to resolve the collision.
	 */
	abstract public void resolveCollision( CollisionController x );
	/**
	 * This method should be used to resolve adherence between this CollisionController and the one passed as an argument.
	 * Adherence involves resting contact, and is thus resolved somewhat differently than a regular collision.  The details
	 * of this resolution depend heavily on the geometry of the CollisionControllers involved.
	 * 
	 * @param x the CollisionController with which to resolve adherence.
	 */
	abstract public void resolveAdherence( CollisionController x );
	
	/**
	 * This method sets the properties of this CollisionController to match those of the one passed as an argument.
	 * 
	 * @param c the CollisionController whose properties (tolerance, elasticity, associated object) should be copied.
	 */
	public void set(CollisionController c) {
		setTolerance(c.getTolerance());
		setElasticity(c.getElasticity());
		setObject(c.getObject());
	}
	
	/**
	 * Sets default values for this CollisionController.
	 * 
	 * @param oobject object to associate with this CollisionController.
	 */
	public void set_default(HasCollisionController oobject) {
		setTolerance(1e-2);
		setElasticity(1);
		setObject(oobject);
	}
	
	/**
	 * This method should return a copy of this CollisionController.
	 * 
	 * @return a copy of this CollisionController.
	 */
	abstract public CollisionController replica();

}