/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasLength.java,v 1.3 2010/04/12 20:13:18 stefan Exp $
 * 
 */

package teal.sim.properties;

/**
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.3 $
 */

/**
 * Interface for rendered objects that have length.
 */
public interface HasLength {

    /**
     * Sets the length of this object.
     * 
     * @param length new length.
     */
    public void setLength(double length);

    /**
     * Gets the length of this object.
     * 
     * @return length.
     */
    public double getLength();
}