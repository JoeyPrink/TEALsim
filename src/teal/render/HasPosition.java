/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasPosition.java,v 1.9 2007/07/16 22:04:51 pbailey Exp $ 
 * 
 */

package teal.render;

import javax.vecmath.Vector3d;

/**
 * Objects that have a physical position should implement this
 * interface.
 *
 * We assume Objects that implements this interface will store their
 * position using x, y, and z coordinates in a <code>Vector3d</code>.
 * By implementing this interface, a Object allows read/write access
 * to itsposition.
 *
 * <code>Vector3d</code> objects can be used to set and retrieve position.
 *
 */

public interface HasPosition {

 /**
 * Sets the position of this object.
 *
 * @param pos the new position.
 */
public void setPosition(Vector3d pos);

  /**
   * Returns the position of this object.
 * @return the position.
 */
public Vector3d getPosition();

}
