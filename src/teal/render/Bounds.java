/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Bounds.java,v 1.5 2010/07/29 23:07:48 stefan Exp $ 
 * 
 */

package teal.render;

import java.io.Serializable;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

/** Interface for the
 */

public abstract class Bounds implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5326042619271477844L;

	public abstract Object clone();
	public abstract void transform(Matrix4d trans);
	public abstract boolean intersect(Point3d target);
	public abstract double getDiameter();
}
