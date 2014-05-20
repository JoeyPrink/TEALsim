/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasDipoleMoment.java,v 1.5 2007/07/17 15:46:55 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.Vector3d;

/** All EM objects which have dipole moment should implement this interface. Eg: Electric Dipole , Magnetic Dipole.
 */
public interface  HasDipoleMoment
{
        /** Used to access  the dipole moment
         * 
         * @return dipole moment
         */
	public Vector3d getDipoleMoment();
        /** Used to set the dipole moment
         * 
         * @param dip Dipole Moment
         */
	public void setDipoleMoment(Vector3d dip);
}
