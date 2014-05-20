/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: AbstractElementBeanInfo.java,v 1.7 2009/09/21 18:02:08 pbailey Exp $
 * 
 */

package teal.core;

import java.beans.*;
import java.util.*;

import teal.util.*;

/**
 *  
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.7 $
 */

public class AbstractElementBeanInfo extends BeanInfoAdapter {

    static Class<AbstractElement> baseClass = AbstractElement.class;
    static List<EventSetDescriptor> sEvents = null;
    static List<PropertyDescriptor> sProperties = null;

    static {
        try {
            sEvents = new ArrayList<EventSetDescriptor>();
            sEvents.add(new EventSetDescriptor(baseClass, "propertyChange", PropertyChangeListener.class,
                "propertyChange"));

            sProperties = new ArrayList<PropertyDescriptor>();
            PropertyDescriptor pd = new PropertyDescriptor("iD", baseClass);
            pd.setBound(true);
            sProperties.add(pd);
            TDebug.println(1, baseClass.getName() + "BeanInfo: array complete");
        } catch (IntrospectionException ie) {
            TDebug.println(ie.getMessage());
        }

        TDebug.println(1, baseClass.getName() + "BeanInfo: static complete");
    }
    
    public static Collection<EventSetDescriptor> getEventSetList() {
        return sEvents;
    }

    public static Collection<PropertyDescriptor> getPropertyList() {
        return sProperties;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return (PropertyDescriptor[]) sProperties.toArray(BeanInfoAdapter.sPropertyTemplate);
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        //TDebug.println(baseClass.getName() + "BeanInfo: " + this.getClass().getName() + ": getEventSet");
        return (EventSetDescriptor[]) sEvents.toArray(BeanInfoAdapter.sEventSetTemplate);
    }

}