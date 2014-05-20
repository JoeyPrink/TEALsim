/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Referenced.java,v 1.7 2009/09/21 18:02:09 pbailey Exp $
 * 
 */

package teal.core;

import java.util.*;

/**
 * Interface for managing objects that are referenced by other objects.  Referent objects should implement <code>HasReference</code>.
 * 
 * @see teal.core.HasReference
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.7 $
 */
public interface Referenced {

    /**
     * Adds a reference to the given referring (HasReference) object.
     * 
     * @param ref
     */
    public void addReferent(HasReference ref);

    /**
     * Removes the reference to the given referring (HasReference) object.
     * 
     * @param ref
     */
    public void removeReferent(HasReference ref);

    /**
     * Removes all references to referring (HasReference) objects.
     * 
     */
    public void removeReferents();

    /**
     * Returns a Collection of all referring objects.
     * 
     * @return collection of all referring objects.
     */
    public Collection<HasReference> getReferents();
}
