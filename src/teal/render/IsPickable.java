/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: IsPickable.java,v 1.6 2007/07/16 22:04:51 pbailey Exp $ 
 * 
 */

package teal.render;

/**
 * This interface defines methods for handling "picking" (ie clicking on objects) in the viewer.
 * 
 * 
 */

public interface IsPickable extends HasBoundingArea,HasPosition {
	public boolean getPickable();
    public boolean isPickable();
	public void setPickable(boolean b);
	public boolean getPicked();
    public boolean isPicked();
	public void setPicked(boolean b);
}

