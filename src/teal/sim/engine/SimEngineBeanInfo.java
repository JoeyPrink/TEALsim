/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SimEngineBeanInfo.java,v 1.5 2009/04/24 19:35:56 pbailey Exp $ 
 * 
 */

package teal.sim.engine;

import java.beans.*;
import java.util.*;

import teal.core.*;
import teal.util.*;

public class SimEngineBeanInfo extends AbstractElementBeanInfo {

    static Class<SimEngine> baseClass = SimEngine.class;
    protected static EventSetDescriptor[] sEvents = null;
    protected static ArrayList<PropertyDescriptor> sProperties = null;

    static {

        if (baseClass != null) {
            sEvents = new EventSetDescriptor[0];
            try {
                PropertyDescriptor pd = null;
                sProperties = new ArrayList<PropertyDescriptor>(AbstractElementBeanInfo.getPropertyList());

                pd = new PropertyDescriptor("boundingArea", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("deltaTime", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("engineControl", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("frameRate", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("idleDelay", baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("simState",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("showTime", baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("time", baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                

                TDebug.println("SimEngineBeanInfo: array complete");
            } catch (IntrospectionException ie) {
                TDebug.println(ie.getMessage());
            }
        }

        TDebug.println("SimEngineBeanInfo static complete");
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        TDebug.println("SimEngineBeanInfo: getPropertyDescriptors");
        return (PropertyDescriptor[]) sProperties.toArray(sPropertyTemplate);
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        TDebug.println(baseClass.getName() + "BeanInfo: " + this.getClass().getName() + ": getEventSet");
        //return new EventSetDescriptor[0];
        return sEvents;
    }

}