/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BeanInfoAdapter.java,v 1.9 2008/02/11 19:40:59 pbailey Exp $
 * 
 */

package teal.core;

import java.beans.*;

import teal.util.*;

/**
 *  
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.9 $
 */

public class BeanInfoAdapter extends SimpleBeanInfo {

    public static EventSetDescriptor[] sEventSetTemplate = new EventSetDescriptor[0];
    public static PropertyDescriptor[] sPropertyTemplate = new PropertyDescriptor[0];

    public int getDefaultEventIndex() {
        TDebug.println(2,"BeanInfoAdapter:  " + this.getClass().getName() + ": getDefaultEventIndex");
        return -1;
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        TDebug.println(2,"BeanInfoAdapter: " + this.getClass().getName() + ": getEventSet");
        //return new EventSetDescriptor[0];
        return null;
    }

}