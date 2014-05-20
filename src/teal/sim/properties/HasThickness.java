/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasThickness.java,v 1.1 2010/04/30 04:39:37 pbailey Exp $
 * 
 */

package teal.sim.properties;

/**
 * 
 * @author Phil Bailey
 * @version $Revision: 1.1 $
 */

/**
 * Interface for rendered objects that have a secondary radius.
 */
public interface HasThickness {

    /**
     * Sets the radius of this object. 
     * 
     * @param radius new radius.
     */
    public void setThickness(double radius);

    /**
     * Gets the radius of this object. 
     * 
     * @return radius.
     */
    public double getThickness();
}
