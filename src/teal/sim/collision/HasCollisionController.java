/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasCollisionController.java,v 1.11 2007/07/16 22:04:59 pbailey Exp $ 
 * 
 */

package teal.sim.collision;

import javax.vecmath.*;


/**
 * Provides methods for all objects implementing a collision controller.
 */
public interface HasCollisionController {

	public boolean isColliding();
	/**
	 * Sets whether or not this object is included in collision calculations.
	 * 
	 * @param x is colliding?
	 */
	public void setColliding(boolean x);
	
	/**
	 * Returns the CollisionController associated with this object.
	 * 
	 * @return CollisionController.
	 */
	public CollisionController getCollisionController();
	/**
	 * Sets the CollisionController associated with this object.
	 * 
	 * @param cg new CollisionController.
	 */
	public void setCollisionController(CollisionController cg);

	/**
	 * Returns pre-collision position.
	 */
	public Vector3d getPosition1();
	/**
	 * Returns projected collision position.
	 */
	public Vector3d getPosition2();
	/**
	 * Returns pre-collision velocity.
	 */
	public Vector3d getVelocity1();
	/**
	 * Returns projected collision velocity.
	 */
	public Vector3d getVelocity2();
	/**
	 * Updates position and velocity variables that represent the motional
	 * state of the object prior to collision. This method is called from
	 * within doDynamic() in the model.
	 */
	public void updateCollision();

	public double getMass();
	/**
	 * Applies the supplied impulse to the object.  Impulse acts directly on velocity, rather than acceleration.
	 * 
	 * @param impulse impulse to apply.
	 */
	public void applyImpulse(Vector3d impulse);
	public void applyCorrection(Vector3d correction);
	
	public void addAdheredObject(HasCollisionController x);
	public void removeAdheredObject(HasCollisionController x);
	public boolean isAdheredTo(HasCollisionController x);
	
	public Vector3d getReactionDueTo(HasCollisionController x);
	public void setReactionDueTo(HasCollisionController x, Vector3d reaction);
	public Vector3d getReactionDueToAllExcept(HasCollisionController x);
	public Vector3d getReactionDueToAll();
	public Vector3d getExternalForces();
	public boolean solveReactionStep();

}
