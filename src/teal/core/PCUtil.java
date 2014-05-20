/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PCUtil.java,v 1.4 2007/07/16 22:04:44 pbailey Exp $
 * 
 */

package teal.core;

import java.beans.*;

public class PCUtil {

    public static PropertyChangeEvent makePCEvent(THasPropertyChange source, String name, Object oldV, Object newval) {
        //if (!source.hasPropertyChangeListeners(name))
        //	return null;
        //else
        return new PropertyChangeEvent(source, name, oldV, newval);
    }

    public static PropertyChangeEvent makePCEvent(THasPropertyChange source, String name, int oldV, int newval) {
        //if (!source.hasPropertyChangeListeners(name))
        //	return null;
        //else
        return new PropertyChangeEvent(source, name, new Integer(oldV), new Integer(newval));
    }

    public static PropertyChangeEvent makePCEvent(THasPropertyChange source, String name, double oldV, double newval) {
        //if (!source.hasPropertyChangeListeners(name))
        //	return null;
        //else
        return new PropertyChangeEvent(source, name, new Double(oldV), new Double(newval));
    }

    public static PropertyChangeEvent makePCEvent(THasPropertyChange source, String name, float oldV, float newval) {
        //if (!source.hasPropertyChangeListeners(name))
        //	return null;
        //else
        return new PropertyChangeEvent(source, name, new Float(oldV), new Float(newval));
    }

    public static PropertyChangeEvent makePCEvent(THasPropertyChange source, String name, boolean oldV, boolean newval) {
        //if (!source.hasPropertyChangeListeners(name))
        //	return null;
        //else
        return new PropertyChangeEvent(source, name, new Boolean(oldV), new Boolean(newval));
    }
}