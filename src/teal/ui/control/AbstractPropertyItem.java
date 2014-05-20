/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: AbstractPropertyItem.java,v 1.9 2010/06/07 22:00:31 pbailey Exp $ 
 * 
 */

package teal.ui.control;

import java.awt.*;

import javax.swing.*;
import javax.vecmath.Color3f;

import teal.core.*;
import teal.ui.*;

/**
 *  Provides an abstract component for the display & optional 
 *  input properties of a TElement.
 *  Most derived PropertyItems will contain a label, a data type specific
 *  control and an optional JTextField to display the value.
 *  
 *  User modification of the PropertyItem's value will trigger propertyChange events.
 *  The PropertyItem is a PropertChangeListener and may be registered as a Listener,
 *  external propertyChangeEvents trigger a rebroadcast based on the rebroadcast state.
 *  
 *  Each of the derived classes will deal with the complexity 
 *  of the supported data type. The goal is that collections of ProperyItems will
 *  be used in forms to provide application/simulation controls, in this case 
 *  they should be able to be managed by the layoutmanager of the inclosing form.
 *  
 **/

public abstract class AbstractPropertyItem extends UIPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5668610617418431510L;
	/**
	 * 
	 */
	
	protected JLabel mLabel = null;
	boolean rebroadcast = false;
	boolean showLabel = true;
	Box mLabelBox = null;
	int labelWidth = 90;
			
	public AbstractPropertyItem()
	{
		super();
		mLabel = new JLabel();
		mLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		add(mLabel);
	}
	/**
	 * A specialized Route for the PropertyItem automaticly 
	 * assigned to the 'value' property.
	 * */
    public void addRoute(TElement target, String targetName)
    {
        addRoute("value",target,targetName);
    }
    
   
     /** 
     * Remove a route from this object's value attribute and the target's output attribute. 
     **/
    public void removeRoute(TElement target, String targetName)
    {
        removeRoute("value",target,targetName);
    }
    /**
     * Sets the value of this control, this is not the method used 
     * to reconcile changes to the value caused by user modification of the 
     * control. Triggers property change events if rebroadcast is true.
     */
	public abstract void setValue(Object obj);
	
	/**
	 * Returns the current value of the control as an object.
	 */
	public abstract Object getValue();
	
	public abstract boolean getControlVisible();
	public abstract void setControlVisible(boolean b);
	//public abstract boolean getValueVisible();
	//public abstract void setValueVisible(boolean b);
	
	/**
	 * Set the rebroadcast state of the label.
	 */
	public void setRebroadcast(boolean b)
	{
		rebroadcast = b;
	}
	/**
	 * returns the rebroadcast state of the label.
	 */
	public boolean getRebroadcast()
	{
		return rebroadcast;
	}
	/**
	 * Returns the label's text.
	 */
	public String getText() {
		return mLabel.getText();
	}
	/**
	 * Sets the label's text.
	 */
	public void setText(String txt) {
		mLabel.setText(txt);
		setLabelWidth(labelWidth);
	}
	/**
	 * returns the label this is a read only property.
	 */
	public JLabel getLabel()
	{
		return mLabel; 
	}
	/**
	 * Set the display state of the label.
	 */
	public void setLabelVisible(boolean b)
	{
		showLabel = b;
		mLabel.setVisible(showLabel);
	}
	/**
	 * returns the display state of the label.
	 */
	public boolean getLabelVisible()
	{
		return showLabel;
	}
		
	public void setLabelColor(Color c)
	{
		mLabel.setForeground(c);
	}
	
	public void setLabelColor(Color3f c)
	{
		mLabel.setForeground(new Color(c.x,c.y,c.z));
	}
	
	public Color getLabelColor()
	{
		return mLabel.getForeground();
	}

	public void setLabelWidth(int i)
	{
		labelWidth = i;
		Dimension sz = mLabel.getPreferredSize();
		sz.width = i;
		mLabel.setPreferredSize(sz);
	}
	
	public int getLabelWidth()
	{
		return (int) mLabel.getPreferredSize().getWidth();
	}
	
	public void setLabelHorizontalAlignment(int i)
	{
		mLabel.setHorizontalAlignment(i);
	}
	
	public int getLabelHorizontalAlignment()
	{
		return mLabel.getHorizontalAlignment();
	}
	
	public void setIcon(Icon i)
	{
		mLabel.setIcon(i);
	}
	
	public Icon getIcon()
	{
		return mLabel.getIcon(); 
	}
	
}
