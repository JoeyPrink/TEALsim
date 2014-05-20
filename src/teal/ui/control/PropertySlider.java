/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PropertySlider.java,v 1.3 2007/07/16 22:05:11 pbailey Exp $ 
 * 
 */

package teal.ui.control;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

public abstract class PropertySlider extends AbstractPropertyItem implements ChangeListener {

	protected JSlider mSlider;

	public PropertySlider() {
		super();
		mSlider = new JSlider();
		setSliderWidth(120);
		add(mSlider);
	}
	public boolean getControlVisible(){
		return mSlider.isVisible();
	}
	public void setControlVisible(boolean b){
		mSlider.setVisible(b);
	}
	public void setSliderWidth(int w) {
		Dimension siz = mSlider.getPreferredSize();
		siz.width = w;
		siz.height = 40;
		mSlider.setPreferredSize(siz);
	}
	
	public int getSliderWidth() {
		return mSlider.getWidth();
	}
	
	public abstract void stateChanged(ChangeEvent ev);

}