/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasCharge.java,v 1.4 2007/07/17 15:46:54 pbailey Exp $ 
 * 
 */

package teal.physics.em;

/**
 * WorldObjects that have a Charge (Q) should implement
 * <code>HasCharge</code>
 *
 * Implementing this interface allows read / write access to
 * your charge property.
 */
public interface HasCharge {
  
  /**
   * Sets charge to new value.
   *
   * @param Q a new <code>double</code> charge value.
   */
  public void setCharge(double Q);
  
  /**
   * Gets current charge value.
   *
   * @return current charge value in <code>double</code> form.
   */
  public double getCharge();
}
