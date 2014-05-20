/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasBackgroundImage.java,v 1.5 2007/07/16 22:04:50 pbailey Exp $ 
 * 
 */

package teal.render;

import java.awt.Image;

public interface HasBackgroundImage{

	public void  setBackgroundImage(Image img);
	public Image getBackgroundImage();

	public void setShowBackground(boolean b);
	public boolean getShowBackground();
}
