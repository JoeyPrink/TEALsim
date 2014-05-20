/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasResistance.java,v 1.4 2007/07/17 15:46:55 pbailey Exp $ 
 * 
 */

package teal.physics.em;

/**
 * WorldObjects that have Resistance (R) should implement
 * <code>HasResistance</code>
 *
 * Implementing this interface allows read / write access to
 * your resistance property.
 */
public interface HasResistance {
  
  /**
   * Sets resistance to new value.
   *
   * @param R a new <code>double</code> resistance value.
   */
  public void setResistance(double R);
  
  /**
   * Gets current resistance value.
   *
   * @return current resistance value in <code>double</code> form.
   */
  public double getResistance();
}
