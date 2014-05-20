/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PropertyCombo.java,v 1.6 2009/04/24 19:35:57 pbailey Exp $ 
 * 
 */

package teal.ui.control;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import teal.core.*;
import teal.util.*;

public class PropertyCombo extends AbstractPropertyItem implements TextListener, ItemListener {

    private static final long serialVersionUID = 4049642304014726962L;
    
    //java.awt.Choice mCombo;
    javax.swing.JComboBox mCombo;
    java.util.Vector mValues;
    Object lastValue = null;

    public PropertyCombo() {
        super();
        mValues = new java.util.Vector();
        //mCombo = new ComboBox();
        mCombo = new JComboBox();
        mCombo.setMaximumRowCount(10);
        mCombo.addItemListener(this);
        add(mCombo);
    }

    public void setValue(Object obj) {
        Object value = mValues.elementAt(mCombo.getSelectedIndex());
        setSelected(obj);
        //PropertyChangeEvent pc = PCUtil.makePCEvent(this, "value", lastValue, value);
        firePropertyChange("value", lastValue, value);
        lastValue = value;
    }

    public Object getValue() {
        return mValues.elementAt(mCombo.getSelectedIndex());
    }
    public boolean getControlVisible(){
		return mCombo.isVisible();
	}
	public void setControlVisible(boolean b){
		mCombo.setVisible(b);
	}
    // setBounds does not seem to work properly.
    public void setBounds(int arg0, int arg1, int arg2, int arg3) {
        if (mCombo != null) mCombo.setBounds(arg0, arg1, arg2, arg3);
        super.setBounds(arg0, arg1, arg2, arg3);
    }

    public void setFont(Font arg0) {
        if (mCombo != null) mCombo.setFont(arg0);
        super.setFont(arg0);
    }

    public void add(NamedValue value) {
        mCombo.addItem(value.getName());
        mValues.addElement(value.getValue());
    }

    public void add(String name, Object value) {
        mCombo.addItem(name);
        mValues.addElement(value);
    }

    public void delItem(int index) {
        mCombo.remove(index);
        mValues.removeElementAt(index);
    }

    public void insert(NamedValue value, int position) {
        insert(value.getName(), value.getValue(), position);
    }

    public void insert(String tag, Object obj, int position) {
        mCombo.insertItemAt(tag, position);
        mValues.add(position, obj);
    }

    public void removeAll() {
        mCombo.removeAll();
        mValues.removeAllElements();
    }

    public void load(Collection<NamedValue> list) {
        removeAll();
        if (list != null) {
            append(list);
        }
    }

    public void append(Collection<NamedValue> valueList) {
        if (valueList != null) {
            NamedValue value = null;
            Iterator it = valueList.iterator();
            while (it.hasNext()) {
                value = (NamedValue) it.next();
                mCombo.addItem(value.getName());
                mValues.addElement(value.getValue());
            }
        }
    }

    public Object getSelected() {

        return mCombo.getSelectedItem();

    }

    public void setSelected(Object target) {
        mCombo.setSelectedItem(target);
    }

    public int getSelectedIndex() {
        return mCombo.getSelectedIndex();
    }

    public void setSelectedIndex(int i) {
        mCombo.setSelectedIndex(i);
    }

    public Object getSelectedObject() {
        Object curValue = null;
        int ix = mCombo.getSelectedIndex();
        if (ix != -1) curValue = mValues.elementAt(ix);
        return curValue;
    }

    public boolean setSelectedObject(Object targetValue) {
        boolean status = false;
        int idx = mValues.indexOf(targetValue);
        if (idx != -1) {
            mCombo.setSelectedIndex(idx);
            status = true;
        }
        return status;
    }

    public String getValueString(Object targetValue) {
        Object str = null;
        int idx = mValues.indexOf(targetValue);
        if (idx != -1) {
            str = mCombo.getItemAt(idx);
        }
        return str.toString();
    }

    /**
     *  returns the index of the first substring match in 
     *  the list  for a given substring, or -1 if none. 
     *
     */
    public int indexSubstring(String target) {
        int idx = -1;
        int count = mCombo.getItemCount();
        for (int i = 0; i < count; i++) {
            if (mCombo.getItemAt(i).toString().regionMatches(true, 0, target, 0, target.length())) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    public void textValueChanged(TextEvent e) {
        TextComponent tc = (TextComponent) e.getSource();
        String target = tc.getText();
        if ((target != null) && (target.length() != 0)) {
            int idx = indexSubstring(target);
            if (idx >= 0) {
                mCombo.setSelectedIndex(idx);
            } else {
                int selected = mCombo.getSelectedIndex();
                if (selected >= 0) {
                    getToolkit().beep();
                    mCombo.setSelectedIndex(-1);
                }
            }

        } else {
            mCombo.setSelectedIndex(-1);
        }
    }

    public void itemStateChanged(ItemEvent ie) {
        Object value = null;
        if (ie.getItemSelectable() == mCombo) {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                int idx = mCombo.getSelectedIndex();
                if (idx > -1) {
                    if (mValues.size() > idx) value = mValues.elementAt(idx);
                }
                if (lastValue != value) {
                    PropertyChangeEvent pc = PCUtil.makePCEvent(this, "value", lastValue, value);
                    lastValue = value;
                    //propertyChange(pc);
                    firePropertyChange(pc);

                }
            }
        }
    }

}
