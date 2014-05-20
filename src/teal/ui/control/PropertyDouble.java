/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PropertyDouble.java,v 1.7 2010/08/23 22:09:16 stefan Exp $ 
 * 
 */

package teal.ui.control;

import java.beans.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import teal.core.*;
import teal.util.*;

/**
 * Broken Class need to work out mapping between slider and double value.
 */
public class PropertyDouble extends PropertySlider {

    private static final long serialVersionUID = 3257566191928162359L;
    
    protected double min = 0;
	protected double max = 1;
	protected double stepSize;
	protected double precision = 0.01;
	protected int iMin;
	protected int iMax;
	protected int iVal;
	protected double lastValue = (max - min) / 2.0;
	protected JFormattedTextField mTextVal;
	protected NumberFormat numFormat;
	private Hashtable labelTable;

	public PropertyDouble() {
		super();
		iMin = new Double(min / precision).intValue();
		iMax = new Double(max / precision).intValue();
		mSlider.setMinimum(iMin);
		mSlider.setMaximum(iMax);
		mSlider.setValue(new Double(lastValue / precision).intValue());
		numFormat = NumberFormat.getInstance();
		mTextVal = new JFormattedTextField(numFormat);
		//mTextVal.setPreferredSize(new Dimension(44,22));
		mTextVal.setColumns(3);
		mTextVal.setHorizontalAlignment(JTextField.RIGHT);
		add(mTextVal);
		mSlider.addChangeListener(this);
		mTextVal.addPropertyChangeListener(this);
		//setDefaultBorder();
		//setDefaultBackground();
	}

	public void setValue(double val) {
		setValue(val, true);
	}

	public void setValue(double val, boolean sendEvent) {
		if (val == Double.NaN) {
			throw (new ArithmeticException("setValue: Double.NaN"));
		}
		int iVal = checkBounds(val);
		mSlider.setValue(iVal);
		if (sendEvent && (val != lastValue)) {
			//PropertyChangeEvent pce = PCUtil.makePCEvent(this, "value", lastValue, val);
			mTextVal.setValue(new Double(val));
			firePropertyChange("value", lastValue, val);
			lastValue = val;
		}
	}

	public void setValue(Object obj) {
		setValue(obj, true);
	}

	public void setValue(Object obj, boolean sendEvent) {
		//TDebug.println(0,"setValue" + obj);
		if (obj instanceof Number) {
			setValue(((Number) obj).doubleValue(), sendEvent);
		} else if (obj instanceof String) {
			try {
				double d = Double.parseDouble((String) obj);
				setValue(d, sendEvent);
			} catch (NumberFormatException ne) {
				TDebug.println(0, "NumberFormatException: '" + obj);
			}
		}
	}

	public Object getValue() {
		TDebug.println(2, "PropertyDouble.getValue: " + mSlider.getValue() * precision);
		return new Double(mSlider.getValue() * precision);
	}
	 public boolean getValueVisible(){
		return mTextVal.isVisible();
	}
	public void setValueVisible(boolean b){
		mTextVal.setVisible(b);
	}
	public void setMinimum(double m) {
		min = m;
		mSlider.setMinimum(new Double(min / precision).intValue());
		//mSlider.setMinimum( new Double(min).intValue());
	}

	public double getMinimum() {
		return min;
	}

	public void setMaximum(double m) {
		max = m;
		//mModel.setMaximum(new Double(max));
		mSlider.setMaximum(new Double(max / precision).intValue());
		//mSlider.setMaximum(new Double(max).intValue());
	}

	public double getMaximum() {
		return max;
	}

	public void setExtent(double ex) {
		Double d = new Double(ex / precision);
		mSlider.setExtent(d.intValue());
	}

	public double getExtent() {
		return (double) mSlider.getExtent() * precision;
	}

	public void setPrecision(double d) {
		precision = d;
	}

	public double getPrecision() {
		return precision;
	}

	protected int checkBounds(double i) {
		double val = i;
		if (val < min)
			val = min;
		else if (val > max)
			val = max;
		Double v = new Double(val / precision);
		return v.intValue();
	}

	public void setPaintTicks(boolean b) {
		if (b == true) {
			int majorStep = (mSlider.getMaximum() - mSlider.getMinimum()) / 2;
			double stepValue = mSlider.getMinimum() * precision;
			labelTable = new Hashtable();
			labelTable.put(new Integer(0), new JLabel(new Integer(0).toString()));
			int stepKey = mSlider.getMinimum();
			for (int i = 0; i <= 2; i++) {
				labelTable.put(new Integer(stepKey), new JLabel(new Double(stepKey * precision).toString()));
				stepKey += majorStep;
			}
			mSlider.setLabelTable(labelTable);
			mSlider.setMajorTickSpacing(new Integer((mSlider.getMaximum() - mSlider.getMinimum()) / 2).intValue());
			mSlider.setMinorTickSpacing(new Integer((mSlider.getMaximum() - mSlider.getMinimum()) / 10).intValue());
		}
		mSlider.setPaintTicks(b);
		mSlider.setPaintLabels(b);
	}

	public void stateChanged(ChangeEvent ev) {
		double val = 0.0;
		Double dVal = null;
		Object src = ev.getSource();
		if (src == mSlider) {
			val = mSlider.getValue() * precision;
			if (val != lastValue) {
				TDebug.println(2, "Slider changed: " + val);
				mTextVal.setValue(new Double(val));
				//TDebug.println(0,"valueSet: " + val);
				PropertyChangeEvent pce = PCUtil.makePCEvent(this, "value", lastValue, val);
				firePropertyChange(pce);
				lastValue = val;
			}
		}
	}

	//XXX: this method does exactly the same as the super method...could be removed
	public void propertyChange(PropertyChangeEvent pce) {
		TDebug.println(2, "propertyChange: " + pce);
		if (pce.getSource() == mTextVal) {
			/*
			 * Double dVal = (Double)mTextVal.getValue(); double val; if (dVal !=
			 * null) { val = dVal.doubleValue();
			 * 
			 * if (val != lastValue) { setValue(checkBounds(val)); } }
			 */
			setValue(mTextVal.getValue());
		} else {
			super.propertyChange(pce);
		}
	}

	@SuppressWarnings("unchecked")
	public void setEnabled(boolean arg0) {
		mSlider.setEnabled(arg0);
		mTextVal.setEnabled(arg0);
		mLabel.setEnabled(arg0);
        if (labelTable != null)
        {
		    Enumeration temp = labelTable.elements();
		    while (temp.hasMoreElements()) {
			    ((JLabel) temp.nextElement()).setEnabled(arg0);
		    }
	    }
	}
}