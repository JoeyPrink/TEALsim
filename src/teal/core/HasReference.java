/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasReference.java,v 1.4 2007/07/16 22:04:44 pbailey Exp $
 * 
 */

package teal.core;

/**
 * Interface for handling objects that reference other objects.  Referenced objects should implement <code>Referenced</code>.
 * 
 * @see teal.core.Referenced
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.4 $
 */

public interface HasReference {

    /**
     * Adds or sets the current Referenced object, the actual management of the reference 
     * should be handled by the Referenced object to prevent looping.
     *
     */
    public void addReference(Referenced elm);

    /**
     * Adds or sets the current Referenced Object, the actual management of the reference 
     * should be handled by the Referenced object to prevent looping.
     *
     */
    public void setReference(Referenced elm);

    /**
     * removes or clears the current Referenced oBject, the actual management of the reference
     * should be handled by the Referenced object to prevent looping.
     *
     */
    public void removeReference(Referenced elm);

    public Referenced getReference();
}
