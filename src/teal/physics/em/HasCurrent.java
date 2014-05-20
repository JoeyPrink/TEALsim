/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasCurrent.java,v 1.4 2007/07/17 15:46:55 pbailey Exp $ 
 * 
 */

package teal.physics.em;

/**
 * WorldObjects that have Current (I) should implement
 * <code>HasCurrent</code>
 *
 * Implementing this interface allows read / write access to
 * your current property.
 */
public interface HasCurrent {
  
  /**
   * Sets current to new value.
   *
   * @param I a new <code>double</code> current value.
   */
  public void setCurrent(double I);
  
  /**
   * Gets current current value.
   *
   * @return current current value in <code>double</code> form.
   */
  public double getCurrent();
}
