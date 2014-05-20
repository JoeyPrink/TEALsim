/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: AbstractElement.java,v 1.16 2010/08/23 22:09:15 stefan Exp $
 * 
 */

package teal.core;

import java.beans.*;
import java.lang.reflect.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import teal.util.*;

/**
 * This class is the base for all T Objects that are not extended from a Java
 * class. Each TObj class will provide accessor methods for any attribute.
 *  
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.16 $
 */

public abstract class AbstractElement implements TElement {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 6815857262289913536L;
//	private static int nextID;
//	private static AtomicInteger nextID;

//    static {
//        nextID = new AtomicInteger(0);
//    }
    
    protected String id = null;
    protected PropertyChangeSupport propSupport;

    /**
     * Checks whether the given <code>TElement</code> has a valid ID and,
     * if not, assigns one to it.
     */
    public static void checkID(HasID obj) {
        String id = obj.getID();
        // Need to check for uniqueness
        if (id == null) {
            id = AbstractElement.autoName(obj);
            obj.setID(id);
        }
    }

    public static String autoName(HasID obj) {
        Class<? extends HasID> cl = obj.getClass();

//        StringBuffer buf = new StringBuffer(cl.getName());
//        buf.append(nextID.incrementAndGet());
        return cl.getName() + "-" + UUID.randomUUID();
    }

 

    public AbstractElement() {
        propSupport = new PropertyChangeSupport(this);
    }
    
    /** 
     * Establish a route from this object's source attribute and the targeted 
     * attribute of another TElement. 
     **/
    public void addRoute(Route r)
    {
        addPropertyChangeListener(r.getSrcProperty(),r);
    }
    
    /** 
     * Establish a route from this object's source attribute and the targeted 
     * attribute of another TElement. 
     **/
    public void addRoute(String attribute, TElement target, String targetName)
    {
        Route r = new Route(attribute,target,targetName);
        addRoute(r);
    }
 
     /** 
     * Remove a route from this object's source attribute and the target's output attribute. 
     **/
    public void removeRoute(String attribute, TElement listener, String targetName)
    {
        PropertyChangeListener [] listeners = propSupport.getPropertyChangeListeners(attribute);
        if (listeners.length > 0)
        {
            Route r = new Route(attribute,listener,targetName);
            for(int i=0; i< listeners.length;i++)
            {
                if(r.equals(listeners[i])){
                    propSupport.removePropertyChangeListener(listeners[i]);
                }
            }
        }
    }
    

    /** the following methods wrap access to the PropertyChangeSupport member */
    public void propertyChange(PropertyChangeEvent pce) {
        TDebug.println(3, getID() + ": in propertyChange trying to set " + pce.getPropertyName());
        setProperty(pce.getPropertyName(), pce.getNewValue());
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        propSupport.addPropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property.
     */
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        TDebug.println(3, "addingPropertyChangeListener for " + propertyName);
        propSupport.addPropertyChangeListener(propertyName, listener);
    }

    /** 
     * Sends the PropertyChangeEvent to any registered listeners and routes, if 
     * the event is null no action is performed.
     */
    public synchronized void firePropertyChange(PropertyChangeEvent evt) {
        if (evt != null) {
              propSupport.firePropertyChange(evt);
        }
    }
	public synchronized void firePropertyChange(String name,Object oldValue,Object newValue) {
              propSupport.firePropertyChange(name,oldValue,newValue);
    }
	public synchronized void firePropertyChange(String name,boolean oldValue,boolean newValue) {
              propSupport.firePropertyChange(name,oldValue,newValue);
    }
	public synchronized void firePropertyChange(String name,int oldValue,int newValue) {
              propSupport.firePropertyChange(name,oldValue,newValue);
    }

    /**
     * Check if there are any listeners for a specific property.
     */
    public boolean hasPropertyChangeListeners(String propertyName) {
        return propSupport.hasListeners(propertyName);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        propSupport.removePropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     */
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propSupport.removePropertyChangeListener(propertyName, listener);
    }

    public String toString() {
        return (id);
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        String temp = this.id;
        this.id = new String(id);
        firePropertyChange("ID", temp, id);
    }

    public Object getProperty(String name)
    {
        TDebug.println(3, " In getProperty() " + getID() + ": " + name);
        Object obj = null;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name, this.getClass());
            Method theMethod = pd.getReadMethod();
            if (theMethod != null) {
                obj = theMethod.invoke(this, (Object[])null);
            } else {
                TDebug.println(1, "Getter method for " + name + " not found");
            }
        } catch (IntrospectionException ie) {
            TDebug.printThrown(0, ie, getID() + " Warning: Getter IntrospectionEx: " + ie.getMessage() + "  "
                + this.getClass().getName());
        } catch (InvocationTargetException cnfe) {
            TDebug.println(0, getID() + cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.println(0, getID() + ille.getMessage());
        }

        return obj;
    }

    public boolean setProperty(String name, Object prop) {
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
            TDebug.println(0, getID() + " Warning: Setter IntrospectionEx: " + ie.getMessage() + "  "
                + this.getClass().getName());
        } catch (InvocationTargetException cnfe) {
            TDebug.println(0, getID() + " InvocTargetEx: " + cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.println(0, getID() + "IllegalAccess: " + ille.getMessage());
        }

        return status;
    }    
}
