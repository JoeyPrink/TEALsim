/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GeneratesE.java,v 1.5 2007/07/17 15:46:54 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.Vector3d;

/**
 * Objects that generate a Electric field should implement <code>
 * GeneratesE</code>.  When a <code>World</code> responds to
 * <code>getE</code> requests, it will only sum up the electric
 * fields of <code>GeneratesE</code> objects.
 */
public interface GeneratesE {

    /**
     * <code>getE</code> returns the ELECTRIC field at a position
     * and time.
     *
     * @param position a <code>Vector3D</code> representing where
     * to calculate the magnetic field at.
     * @param time a <code>double</code> representing the <code>
     * World</code> time to calculate the magnetic field at.
     * @return a <code>Vector3d</code> representing the magnetic field
     * generated by this object.
     */
    Vector3d getE(Vector3d position, double time);

    /**
     * <code>getE</code> returns the Magnetic field at a position
     * at the current <code>World</code> time.
     *
     * @param position a <code>Vector3D</code> representing where
     * to calculate the electric field.
     * @return a <code>Vector3d</code> representing the field
     */

    Vector3d getE(Vector3d position);

    double getEFlux(Vector3d position);

    /**
     * <code>isGeneratingE/code> returns whether or not you are
     * currently generating a field.
     *
     * @return <code>true</code> if currently generating a
     * field, <code>false</code> otherwise
     */
    boolean isGeneratingE();

    double getEPotential(Vector3d position);
}
