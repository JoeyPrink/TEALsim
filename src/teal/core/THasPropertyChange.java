/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: THasPropertyChange.java,v 1.9 2007/07/16 22:04:45 pbailey Exp $
 * 
 */

package teal.core;

import java.beans.*;

/**
 * This interface implements a wrapper based on the java.beans.PropertyChangeSupport class
 * so that all TEAL objects will be able to access an internal PropertyChangeSupport member
 * as if they were derived from one if.
 *
 * It does not seem that Java supplies a standard interface or base class for beans.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.9 $
 */

public interface THasPropertyChange {

    /**
     * Add a PropertyChangeListener to the listener list.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a PropertyChangeListener for a specific property.
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list.
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * PropetyChangeEvent callback.  This method is called when a PropertyChangeEvent is fielded.
     * @param ev fielded PropetyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent ev);
   
}
