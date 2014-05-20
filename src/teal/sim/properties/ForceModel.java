/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ForceModel.java,v 1.6 2007/07/16 22:05:07 pbailey Exp $ 
 * 
 */

package teal.sim.properties;

import javax.vecmath.*;

/**
 * Objects that represent a force.
 */
public interface ForceModel {

  /**
   * <code>getForce</code> returns the instanteous force on a physical object.
   * and time.
   */
  public Vector3d getForce(PhysicalElement phys);

  /**
   * <code>getForce</code> returns the force at a position
   *
   */

	public Vector3d getForce(Vector3d position);
	
  /**
   * <code>isActive/code> returns whether or not you are
   * currently exerting a force.
   *
   */
  public boolean isActive();
  public void setActive(boolean b);
  
}
