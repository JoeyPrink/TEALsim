/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: IsMoveable.java,v 1.4 2007/07/16 22:04:51 pbailey Exp $ 
 * 
 */

package teal.render;


/**
 * Objects that may be translated should implement this interface.
 */
public interface IsMoveable {
	public void setMoveable(boolean b);
	public boolean isMoveable();
}

