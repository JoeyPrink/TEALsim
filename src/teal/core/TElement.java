/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TElement.java,v 1.11 2008/02/11 15:59:32 pbailey Exp $
 * 
 */

package teal.core;

import java.beans.PropertyChangeListener;
import java.io.Serializable;


/**
 * The base for all TEAL componenets. Any object which may be specified,
 * included or defined within the simulation must implement this interface,
 * including physical, graphic, control objects and simulation viewers.
 * <p>
 * All TElements are JavaBean complient. As a result many of the attribute
 * processing methods are handled via the JavaBean interfaces and PropertyChange
 * patterns.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.11 $
 */

public interface TElement extends HasID, THasPropertyChange, PropertyChangeListener, Serializable {

    /**
     * Used if a requested attribute is not a member of the TElement derived
     * Class.
     */
    public final Object NOT_DEFINED = "ATT_NOT_DEFINED";

    /**
     * return the attribute value or teal.sim.TElement.NOT_DEFINED
     **/
    public Object getProperty(String attName);

    /**
     * set the attribute value return value is success
     **/
    public boolean setProperty(String attName, Object value);

     public void addRoute(Route r);
    
    /** 
     * Establish a route from this object's source attribute and the targeted 
     * attribute of another TElement. 
     **/
    public void addRoute(String attribute, TElement listener, String targetName);
    
  
     /** 
     * Remove a route from this object's source attribute and the target's output attribute. 
     **/
    public void removeRoute(String attribute, TElement listener, String targetName);
}