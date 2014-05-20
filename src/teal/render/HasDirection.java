/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasDirection.java,v 1.4 2007/07/16 22:04:51 pbailey Exp $ 
 * 
 */

package teal.render;

import javax.vecmath.Vector3d;

/** All objects which have the ability to have a direction need to implement this.
 */
public interface  HasDirection
{
        /** Used to access the direction
         *
         * @return direction
         */
	public Vector3d getDirection();
        /** Used to set the direction
         *
         * @param direction
         */
	public void setDirection(Vector3d direction);
}
