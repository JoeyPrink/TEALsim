/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PropertyInteger.java,v 1.4 2009/04/24 19:35:57 pbailey Exp $ 
 * 
 */

package teal.ui.control;


import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import teal.core.*;
import teal.util.*;

public class PropertyInteger extends PropertySlider {

    private static final long serialVersionUID = 3760843476040036661L;

    protected int min = 0;
    protected int max = 100;
    protected int lastValue = 0;

    protected JSpinner mSpinner;
    protected SpinnerNumberModel mModel;

    //private Hashtable labelTable;

    public PropertyInteger() {
        super();
        mModel = new SpinnerNumberModel((max - min) / 2, min, max, 1);
        mSpinner = new JSpinner();
        mSpinner.addChangeListener(this);
        mSpinner.setModel(mModel);

        add(mSpinner);

        setVisible(true);
        //mLabel.setVisible(showLabel);
        mSlider.addChangeListener(this);
        mSpinner.addChangeListener(this);
    }

    public void setValue(int val) {
        setValue(new Integer(val));

    }

    public void setValue(Object obj) {
        if (obj instanceof Number) {
            mSlider.setValue(checkBounds(((Number) obj).intValue()));
        } else if (obj instanceof String) {
            try {
                int i = Integer.parseInt((String) obj);
                mSlider.setValue(checkBounds(i));
            } catch (NumberFormatException ne) {
                TDebug.println(0, "NumberFormatException: '" + obj);
            }
        }

    }

    public Object getValue() {
        return new Integer(mSlider.getValue());
    }

    public boolean getValueVisible() {
        return mSpinner.isVisible();
    }

    public void setValueVisible(boolean b) {
        mSpinner.setVisible(b);
    }

    public void setMinimum(int m) {
        min = m;
        mModel.setMinimum(new Integer(min));
        mSlider.setMinimum(min);
    }

    public int getMinimum() {
        return min;
    }

    public void setMaximum(int m) {
        max = m;
        mModel.setMaximum(new Integer(max));
        mSlider.setMaximum(max);
    }

    public int getMaximum() {
        return max;
    }

    public void setExtent(int ex) {
        mSlider.setExtent(ex);
    }

    public int getExtent() {
        return mSlider.getExtent();
    }

    public void setStepSize(int max) {
        mModel.setStepSize(new Integer(max));
    }

    public int getStepSize() {
        return mModel.getStepSize().intValue();
    }

    public void setPaintTicks(boolean b) {
        if (b == true) {
            mSlider.setMajorTickSpacing((getMaximum() - getMinimum()) / 2);
            mSlider.setMinorTickSpacing((getMaximum() - getMinimum()) / 10);
            //mSlider.setMinorTickSpacing(1);
        }
        mSlider.setPaintTicks(b);
        mSlider.setPaintLabels(b);
    }

    protected int checkBounds(int i) {
        int val = i;
        if (val < min)
            val = min;
        else if (val > max) val = max;
        return val;
    }

    public void stateChanged(ChangeEvent ev) {
        boolean status = false;
        int val = 0;
        Object src = ev.getSource();
        if (src == mSlider) {
            val = mSlider.getValue();
            if (val != lastValue) {
                TDebug.println(1, "Slider changed: " + val);
                mSpinner.setValue(new Integer(val));
                status = true;
            }
        } else if (src == mSpinner) {
            val = ((Number) mSpinner.getValue()).intValue();
            if (val != lastValue) {
                mSlider.setValue(val);
                status = true;
            }
        }
        if (status) {
            PropertyChangeEvent pce = PCUtil.makePCEvent(this, "value", lastValue, val);
            firePropertyChange(pce);
            lastValue = val;
        }

    }

    @SuppressWarnings("unchecked")
	public void setEnabled(boolean arg0) {
        mSlider.setEnabled(arg0);
        mSpinner.setEnabled(arg0);
        mLabel.setEnabled(arg0);
        Dictionary labelTable = new Hashtable();
        labelTable = mSlider.getLabelTable();
        Enumeration temp = labelTable.elements();
        while (temp.hasMoreElements()) {
            ((JLabel) temp.nextElement()).setEnabled(arg0);
        }

    }

}
