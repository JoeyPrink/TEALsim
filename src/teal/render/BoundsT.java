/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BoundsT.java,v 1.3 2010/07/16 16:57:16 pbailey Exp $ 
 * 
 */

package teal.render;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

/** Interface for the
 */

public abstract class BoundsT{

	public abstract Point3d getCenter();
	public abstract void setCenter(Point3d center);
	public abstract Object clone();
	public abstract void transform(Matrix4d trans);
	public abstract boolean intersect(Point3d target);
}
