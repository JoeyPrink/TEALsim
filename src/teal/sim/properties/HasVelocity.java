/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasVelocity.java,v 1.4 2007/07/16 22:05:08 pbailey Exp $ 
 * 
 */

package teal.sim.properties;

import javax.vecmath.*;

/**
 * WorldObjects that move around (have velocity) should implement this
 * interface.
 *
 * We assume a WorldObject that implements this interface will store their
 * velocity in vector format, where the vector is composed of x, y, and z
 * <code>double</code> coordinates.
 *
 * By implementing this interface, a WorldObject allows read/write access
 * to their current velocity.
 *
 * <code>Vector3d</code> objects can be used to set and retrieve velocity.
 *
 * Additionally, double parameters can be passed and returned to avoid the
 * overhead of <code>Vector3d</code> object creation.
 */
public interface HasVelocity {

  public void setVelocity(Vector3d X);

  public void setVx(double newX);

  public void setVy(double newY);

  public void setVz(double newZ);

  public Vector3d getVelocity();

  public double getVx();

  public double getVy();

  public double getVz();
}
