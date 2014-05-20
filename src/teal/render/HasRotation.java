/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasRotation.java,v 1.15 2007/07/16 22:04:51 pbailey Exp $ 
 * 
 */

package teal.render;

import javax.vecmath.*;

/**
 * Objects that may be rotated should implement this
 * interface.
 *
 */

public interface HasRotation{

  
public void setRotating(boolean state);
  public boolean isRotating();
  /**
   * Sets this object's rotation as a Matrix3d (rotation matrix).  The value is converted to a Quat4d internally.
   * 
 * @param rot new rotation as Matrix3d.
 */
public void setRotation(Matrix3d rot);
  /**
   * Sets this object's rotation as a Quat4d.
   * 
 * @param orientation new rotation as Quat4d.
 */
public void setRotation(Quat4d orientation);

  /**
   * Returns this object's rotation as a Quat4d.
   * 
 * @return rotation as Quat4d.
 */
public Quat4d getRotation();
  
  
  public boolean isRotable();
  /**
   * Sets whether this object should be allowed to rotate (if false, the object will not respond to rotation
   * behaviors).
   * 
 * @param b 
 */
  public void setRotable(boolean b);
  
  
 
}
