/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ControlGroup.java,v 1.7 2009/04/24 19:35:57 pbailey Exp $ 
 * 
 */

package teal.ui.control;

import java.awt.Component;
import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;

import teal.core.Route;
import teal.core.TElement;
import teal.util.TDebug;

import teal.ui.swing.JTaskPaneGroup;


/**
 * @author pbailey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ControlGroup extends JTaskPaneGroup implements TElement
{
    protected String id = null;
    protected ArrayList<Object> mElements;
    
    public ControlGroup(){
        mElements = new ArrayList<Object>();
    }
    
    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        String temp = this.id;
        this.id = new String(id);
        firePropertyChange("ID", temp, id);
    }
    public Collection<Object> getElements(){
        return mElements;
    }
    
    public void addElement(Object obj)
    {
        mElements.add(obj);
        if(obj instanceof Component){
            add((Component)obj);
        }
        else if(obj instanceof Action){
            add((Action)obj);
        }       
    }
    
    public void removeElement(Object obj){
        mElements.remove(obj);
        if(obj instanceof Component)
        remove((Component)obj);
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
    

    public Object getProperty(String name)
    //throws  NoSuchMethodException
    {
        TDebug.println(3, " In getProperty() " + getID() + ": " + name);
        Object obj = null;
        try {

            PropertyDescriptor pd = new PropertyDescriptor(name, this.getClass());
            //Class paramClass[] = { pd.getPropertyType()};
            Method theMethod = pd.getReadMethod();
            //Method theMethod = BeanTool.getReadMethod(name,this.getClass());
            if (theMethod != null) {
                obj = theMethod.invoke(this,  (Object[]) null);
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
        TDebug.println(3, " In setProperty() " + getID() + ": " + name + " = " + prop.toString());
        boolean status = false;
        Object param[] = { prop };
        Class<?> classType[] = { prop.getClass() };

        try {
            PropertyDescriptor pd = new PropertyDescriptor(name, this.getClass());
            Class<?> paramClass[] = { pd.getPropertyType() };
            Method theMethod = pd.getWriteMethod();
            //Method theMethod = BeanTool.getWriteMethod(name,this.getClass(),classType);
            if (theMethod != null) {
                theMethod.invoke(this, param);
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
    public void propertyChange(PropertyChangeEvent pce){
    }

	
}