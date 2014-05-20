/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Simulation3DBeanInfo.java,v 1.4 2009/09/21 20:07:19 pbailey Exp $ 
 * 
 */

package teal.sim.simulation;

import java.beans.*;
import java.util.*;

import teal.core.*;
import teal.util.*;

public class Simulation3DBeanInfo extends AbstractElementBeanInfo {

    static Class<Simulation3D> baseClass = Simulation3D.class;
    protected static ArrayList<EventSetDescriptor> sEvents = null;
    protected static ArrayList<PropertyDescriptor> sProperties = null;

    static {

        if (baseClass != null) {
            sEvents = new ArrayList<EventSetDescriptor>(AbstractElementBeanInfo.getEventSetList());
            try {
                PropertyDescriptor pd = null;
                sProperties = new ArrayList<PropertyDescriptor>(AbstractElementBeanInfo.getPropertyList());

                pd = new PropertyDescriptor("engine", baseClass);
                pd.setBound(true);
                pd = new PropertyDescriptor("viewer", baseClass);
                pd.setBound(true);
                pd = new PropertyDescriptor("engineControl", baseClass);
                pd.setBound(true);

                pd = new PropertyDescriptor("actions", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("elements", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("framework", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("gui", baseClass);
                pd.setBound(true);
                pd = new PropertyDescriptor("guiElements", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("menuElements", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("selected", baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("selectManager", baseClass);
                pd.setBound(true);
                pd = new PropertyDescriptor("title", baseClass);
                sProperties.add(pd);

                TDebug.println("Simulation3DBeanInfo: array complete");
            } catch (IntrospectionException ie) {
                TDebug.println(ie.getMessage());
            }
        }

        TDebug.println("Simulation3DBeanInfo static complete");
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        TDebug.println("Simulation3DBeanInfo: getPropertyDescriptors");
        return (PropertyDescriptor[]) sProperties.toArray(BeanInfoAdapter.sPropertyTemplate);
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        TDebug.println(baseClass.getName() + "BeanInfo: " + this.getClass().getName() + ": getEventSet");
        return (EventSetDescriptor[]) sEvents.toArray(BeanInfoAdapter.sEventSetTemplate);

    }

}