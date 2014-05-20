/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: UIPanel.java,v 1.9 2010/08/23 22:09:16 stefan Exp $ 
 * 
 */

package teal.ui;

import java.awt.*;
import java.beans.*;
import java.lang.reflect.*;
import java.util.UUID;

import javax.swing.*;

import teal.core.Route;
import teal.core.TElement;
import teal.util.TDebug;


public class UIPanel extends JPanel implements TElement {

    private static final long serialVersionUID = 3834869200219813168L;
    private Image image = null;
    protected String id;
    protected boolean displayPanel = true;

    public UIPanel() {
        super();
        if(id==null) {
        	id = this.getClass().getName() + "-" + UUID.randomUUID().toString();
        }
    }

    public String toString() {
        return (id);
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
    }

    /** 
     * Establish a route from this object's source attribute and the targeted 
     * attribute of another TElement. 
     **/
    public void addRoute(Route r) {
        addPropertyChangeListener(r.getSrcProperty(), r);
    }

    /** 
     * Establish a route from this object's source attribute and the targeted 
     * attribute of another TElement. 
     **/
    public void addRoute(String attribute, TElement target, String targetName) {
        Route r = new Route(attribute, target, targetName);
        addPropertyChangeListener(attribute, r);
    }

    /** 
     * Remove a route from this object's source attribute and the target's output attribute. 
     **/
    public void removeRoute(String attribute, TElement listener, String targetName) {
        PropertyChangeListener[] listeners = getPropertyChangeListeners(attribute);
        if (listeners.length > 0) {
            Route r = new Route(attribute, listener, targetName);
            for (int i = 0; i < listeners.length; i++) {
                if (r.equals(listeners[i])) {
                    removePropertyChangeListener(listeners[i]);
                }
            }
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (pce != null) setProperty(pce.getPropertyName(), pce.getNewValue());
    }

    public void firePropertyChange(PropertyChangeEvent pce) {
        if (pce != null) super.firePropertyChange(pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
    }

    public void firePropertyChange(String name, Object oldValue, Object newValue) {
        super.firePropertyChange(name, oldValue, newValue);
    }

    public void firePropertyChange(String name, boolean oldValue, boolean newValue) {
        super.firePropertyChange(name, oldValue, newValue);
    }

    public void firePropertyChange(String name, int oldValue, int newValue) {
        super.firePropertyChange(name, oldValue, newValue);
    }

    /**
     * Check if there are any listeners for a specific property.
     */
    public boolean hasPropertyChangeListeners(String propertyName) {
        PropertyChangeListener[] listeners = getPropertyChangeListeners(propertyName);
        return (listeners.length > 0);
    }

    public Object getProperty(String name)
    //throws NoSuchMethodException
    {
        TDebug.println(3, " In getProperty() " + getID() + ": " + name);
        Object obj = null;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name, this.getClass());
            Method theMethod = pd.getReadMethod();
            if (theMethod != null) {
                obj = theMethod.invoke(this, (Object[]) null);
            } else {
                TDebug.println(1, "Getter method for " + name + " not found");
            }
        } catch (IntrospectionException ie) {
            TDebug.printThrown(1, ie, "Warning: Getter IntrospectionEx: " + ie.getMessage() + "  " + this.getClass().getName());
        } catch (InvocationTargetException cnfe) {
            TDebug.printThrown(1, cnfe, cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.printThrown(1, ille, ille.getMessage());
        }

        return obj;
    }

    public boolean setProperty(String name, Object prop)
    //throws NoSuchMethodException
    {
        TDebug.println(3, " In setProperty() " + getID() + ": " + name + " = " + prop.toString());
        boolean status = false;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name, this.getClass());
            Method theMethod = pd.getWriteMethod();
            if (theMethod != null) {
                theMethod.invoke(this, prop);
                status = true;
            } else {
                TDebug.println(1, "Setter method for " + name + " not found");
            }
        } catch (IntrospectionException ie) {
            TDebug.printThrown(1, ie, getID() + " Warning: Setter IntrospectionEx: " + ie.getMessage() + "  " + this.getClass().getName());
        } catch (InvocationTargetException cnfe) {
            TDebug.printThrown(1, cnfe, getID() + " InvocTargetEx: " + cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.printThrown(1, ille, getID() + " IllegalAccess: " + ille.getMessage());
        }

        return status;
    }

    public void setDisplayJPanel(boolean b) {
        displayPanel = b;
    }

    public boolean getDisplayJPanel() {
        return displayPanel;
    }

    public JPanel getJPanel() {
        return this;
    }

    public void paintComponent(Graphics gc) {
        if (image == null) {
            super.paintComponent(gc);
        } else {
            Dimension size = new Dimension();
            getSize(size);
            int ih = image.getHeight(null);
            int iw = image.getWidth(null);

            int vc = (size.height / ih) + 1;
            int hc = (size.width / iw) + 1;

            for (int y = 0; y < vc; y++)
                for (int x = 0; x < hc; x++)
                    gc.drawImage(image, x * iw, y * ih, null);
        }
    }
}
