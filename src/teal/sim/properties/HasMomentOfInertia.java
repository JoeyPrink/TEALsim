/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasMomentOfInertia.java,v 1.3 2007/07/16 22:05:08 pbailey Exp $ 
 * 
 */

package teal.sim.properties;


/** All  EM objects which have Moment Of Inertia should implement this interface.
 */
public interface  HasMomentOfInertia
{
        /** Used to access the moment of inertia
         * 
         * @return Moment of Inertia
         */
	public double getMomentOfInertia();
        /** Used to set the moment of inertia
         * 
         * @param mi moment of inertia
         */
	public void setMomentOfInertia(double mi);
}
