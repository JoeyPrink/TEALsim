/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasFieldLines.java,v 1.3 2007/07/16 22:05:08 pbailey Exp $ 
 * 
 */

package teal.sim.properties;

public interface HasFieldLines {
	public void setShowFieldLines(boolean showFieldLines);
	public boolean getShowFieldLines();
	public void setNumberOfFieldLines(int n);
	public void setLengthOfFieldLines(int lengthLine);
	public void setStepSizeOfFieldLines(float step);
	public int getNumberOfFieldLines();
	public int getLengthOfFieldLines();
	public float getStepSizeOfFieldLines();
	public void addFieldLines();
}

