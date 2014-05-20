/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Stepping.java,v 1.4 2007/08/17 19:38:29 jbelcher Exp $ 
 * 
 */

package teal.sim.properties;

/**
 * SimElements implementing the Stepping interface will have their nextStep() method called by the SimEngine at 
 * each frame of the simulation.  In principle, this should only be used to handle independently evolving processes such as
 * wave generators (ie. unrelated to the dynamic integration of the SimEngine).
 *
 */
public interface Stepping {

    /**
     * Returns whether or not this object is currently stepping.
     */
    public boolean isStepping();

    /**
     * Sets whether this object is currently stepping.
     * 
     * @param b
     */
    public void setStepping(boolean b);

    /**
     * The stepped functionality of this object should be contained in nextStep().  This method is called by the SimEngine
     * at each frame of a simulation.
     * @param dt simulation time step
     */
    public void nextStep(double dt);

    /**
     * Resets whatever stepping process is occurning.
     */
    public void reset();
}