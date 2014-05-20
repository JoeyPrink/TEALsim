/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasTransform.java,v 1.6 2007/07/16 22:04:51 pbailey Exp $ 
 * 
 */

package teal.render;

import javax.media.j3d.Transform3D;

/**
 Objects that have a transform (Transform3D) should implement this interface.
 */
public interface  HasTransform
{
   
	public Transform3D getTransform();
	public void setTransform(Transform3D trans);
}
