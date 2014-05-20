/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasBoundingArea.java,v 1.10 2010/07/16 21:41:01 stefan Exp $ 
 * 
 */

package teal.render;

/**
 * Objects that have a bounding area should implement this interface.
 *
 */
public interface HasBoundingArea {
	/**
	 * Returns the bounding area of this object.
	 * 
	 * @return Bounding area
	 */
	public Bounds getBoundingArea();
}
