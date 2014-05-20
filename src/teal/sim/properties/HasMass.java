/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasMass.java,v 1.2 2007/07/16 22:05:08 pbailey Exp $ 
 * 
 */

package teal.sim.properties;

/**
 * WorldObjects that have Mass (M) should implement
 * <code>HasMass</code>
 *
 * Implementing this interface allows read / write access to
 * your mass property.
 */
public interface HasMass {
  
  /**
   * Sets mass to new value.
   *
   * @param M a new <code>double</code> mass value.
   */
  public void setMass(double M);
  
  /**
   * Gets current mass value.
   *
   * @return current mass value in <code>double</code> form.
   */
  public double getMass();
}
