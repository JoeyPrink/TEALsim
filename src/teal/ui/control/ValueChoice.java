/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ValueChoice.java,v 1.5 2010/08/23 22:09:16 stefan Exp $ 
 * 
 */

package teal.ui.control;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

import teal.core.*;
import teal.util.*;

/** 
 * Designed to provide a AWT list which maintains a link to the objects
 * which are listed.
 * @see org.nmis.util.NamedValue
 */

public class ValueChoice extends java.awt.Choice

implements TextListener, ItemListener, TElement {

    private static final long serialVersionUID = 4051048557911881016L;
    protected String id;
    java.util.Vector mValues;
    Object lastValue = null;

    public ValueChoice() {
        super();
        mValues = new java.util.Vector();
        addItemListener(this);
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        String old = new String(this.id);
        this.id = id;
        firePropertyChange("iD", old, this.id);
    }

   public void addRoute(Route r) {
        addPropertyChangeListener(r.getSrcProperty(),r);
    }
    public void addRoute(String att, TElement listener, String target) {
        Route r = new Route(att, listener, target);
        addRoute(r);
    }

       /** 
     * Remove a route from this object's source attribute and the target's output attribute. 
     **/
    public void removeRoute(String attribute, TElement listener, String targetName)
    {
        PropertyChangeListener [] listeners = getPropertyChangeListeners(attribute);
        if (listeners.length > 0)
        {
            Route r = new Route(attribute,listener,targetName);
            for(int i=0; i< listeners.length;i++)
            {
                if(r.equals(listeners[i])){
                    removePropertyChangeListener(listeners[i]);
                }
            }
        }
    }


    /**
     * Check if there are any listeners for a specific property.
     */
    public boolean hasPropertyChangeListeners(String propertyName) {
        PropertyChangeListener[] listeners = getPropertyChangeListeners(propertyName);
        return (listeners.length > 0);
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (pce != null) setProperty(pce.getPropertyName(), pce.getNewValue());
    }

    public void firePropertyChange(PropertyChangeEvent pce) {
        if (pce != null) firePropertyChange(pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
    }

    public Object getProperty(String name)
    //throws  NoSuchMethodException
    {
        TDebug.println(1, " In getProperty() " + getID() + ": " + name);
        Object obj = null;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name, this.getClass());
            Class<?> paramClass[] = { pd.getPropertyType() };
            Method theMethod = pd.getReadMethod();
            if (theMethod != null) {
                obj = theMethod.invoke(this, (Object[])  null);
            } else {
                TDebug.println(0, "Getter method for " + name + " not found");
            }
        } catch (IntrospectionException ie) {
            TDebug.println(0, " IntrospectionEx: " + ie.getMessage());
        } catch (InvocationTargetException cnfe) {
            TDebug.println(0, cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.println(0, ille.getMessage());
        }

        return obj;
    }

    public boolean setProperty(String name, Object prop)
    //throws NoSuchMethodException
    {
        TDebug.println(1, " In setProperty() " + getID() + ": " + name + " = " + prop.toString());
        boolean status = false;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name, this.getClass());
            Class<?> paramClass[] = { pd.getPropertyType() };
            Method theMethod = pd.getWriteMethod();
            if (theMethod != null) {
                theMethod.invoke(this, prop);
                status = true;
            } else {
                TDebug.println(0, "Setter method for " + name + " not found");
            }
        } catch (IntrospectionException ie) {
            TDebug.println(0, " IntrospectionEx: " + ie.getMessage());
        } catch (InvocationTargetException cnfe) {
            TDebug.println(0, " InvocTargetEx: " + cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.println(0, "IllegalAccess: " + ille.getMessage());
        }

        return status;
    }

    public void add(NamedValue value) {
        add(value.getName());
        mValues.addElement(value.getValue());
    }

    public void add(String name, Object value) {
        add(name);
        mValues.addElement(value);
    }

    public void delItem(int index) {
        remove(index);
        mValues.removeElementAt(index);
    }

    public void insert(NamedValue value, int position) {
        insert(value.getName(), value.getValue(), position);
    }

    public void insert(String tag, Object obj, int position) {
        super.insert(tag, position);
        mValues.add(position, obj);
    }

    public void removeAll() {
        super.removeAll();
        mValues.removeAllElements();
    }

    public void load(Vector list) {
        removeAll();
        if (list != null) {
            append(list);
        }
    }

    public void append(Vector valueList) {
        if (valueList != null) {
            NamedValue value = null;
            for (Enumeration<NamedValue> e = valueList.elements(); e.hasMoreElements();) {
                value = (NamedValue) e.nextElement();
                add(value.getName());
                mValues.addElement(value.getValue());
            }
        }
    }

    public Object getSelectedObject() {
        Object curValue = null;
        int ix = getSelectedIndex();
        if (ix != -1) curValue = mValues.elementAt(ix);
        return curValue;
    }

    public boolean setSelectedObject(Object targetValue) {
        boolean status = false;
        int idx = mValues.indexOf(targetValue);
        if (idx != -1) {
            select(idx);
            status = true;
        }
        return status;
    }

    public String getValueString(Object targetValue) {
        String str = null;
        int idx = mValues.indexOf(targetValue);
        if (idx != -1) {
            str = getItem(idx);
        }
        return str;
    }

    /**
     *  returns the index of the first substring match in 
     *  the list  for a given substring, or -1 if none. 
     *
     */
    public int indexSubstring(String target) {
        int idx = -1;
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (getItem(i).regionMatches(true, 0, target, 0, target.length())) {
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
                select(idx);
            } else {
                int selected = getSelectedIndex();
                if (selected >= 0) {
                    getToolkit().beep();
                    select("");
                }
            }

        } else {
            select("");
        }
    }

    public void itemStateChanged(ItemEvent ie) {
        if (ie.getItemSelectable() == this) {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                Object value = mValues.elementAt(getSelectedIndex());
                PropertyChangeEvent pc = PCUtil.makePCEvent(this, "value", lastValue, value);
                firePropertyChange(pc);
                lastValue = value;
            }
        }
    }

}
