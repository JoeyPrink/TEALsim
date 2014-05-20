/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PropertyCheck.java,v 1.6 2007/07/16 22:05:11 pbailey Exp $ 
 * 
 */

package teal.ui.control;

import java.awt.FlowLayout;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;

import teal.core.*;
import teal.util.*;

public class PropertyCheck extends AbstractPropertyItem implements ActionListener, ChangeListener
{

	private static final long serialVersionUID = 3257853198823076404L;

	protected JCheckBox mCheck;
	protected boolean lastValue = false;

	public PropertyCheck()
	{
		super();
		mCheck = new JCheckBox();
		mCheck.setSelected( lastValue );
		mCheck.addActionListener( this );
		mCheck.addChangeListener( this );
		mCheck.setVisible( true );
		add( mCheck );
	}

	public void setValue( boolean newValue )
	{
		setValue( newValue, true );
	}

	public void setValue( boolean newValue, boolean report )
	{
		if( report )
		{
			PropertyChangeEvent pc = PCUtil.makePCEvent( this, "value", lastValue, newValue );
			firePropertyChange( pc );
		}
		mCheck.setSelected( newValue );
		lastValue = newValue;
	}

	public void setValue( Object obj )
	{
		if( obj instanceof Boolean )
		{
			setValue( ( (Boolean)obj ).booleanValue() );
		}
		else if( obj instanceof String )
		{
			try
			{
				boolean value = Boolean.getBoolean( (String)obj );
				setValue( value );
			}
			catch( NumberFormatException ne )
			{
				TDebug.println( 0, "NumberFormatException: '" + obj );
			}
		}
	}

	public Object getValue()
	{
		return new Boolean( lastValue );
	}

	public boolean getControlVisible()
	{
		return mCheck.isVisible();
	}

	public void setControlVisible( boolean b )
	{
		mCheck.setVisible( b );
	}

	public void setIcon( Icon icon )
	{
		mCheck.setIcon( icon );
	}

	public Icon getIcon()
	{
		return mCheck.getIcon();
	}

	public void stateChanged( ChangeEvent evt )
	{
		if( evt.getSource() == mCheck )
		{
			boolean val = mCheck.isSelected();
			if( val != lastValue )
			{
				PropertyChangeEvent pce = PCUtil.makePCEvent( this, "value", lastValue, val );
				propertyChange( pce );
				firePropertyChange( pce );
				lastValue = val;
			}
		}
	}

	public void actionPerformed( ActionEvent evt )
	{
		if( evt.getSource() == mCheck )
		{
			boolean val = mCheck.isSelected();
			if( val != lastValue )
			{
				PropertyChangeEvent pce = PCUtil.makePCEvent( this, "value", lastValue, val );
				propertyChange( pce );
				firePropertyChange( pce );
				lastValue = val;
			}
		}
	}

}
