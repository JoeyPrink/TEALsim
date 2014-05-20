/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TUpdatable.java,v 1.3 2007/07/16 22:04:45 pbailey Exp $
 * 
 */

package teal.core;

/**
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.3 $
 */
public interface TUpdatable

{

    /**
     * Insures that any working values are  synchronized with the 'published' values, 
     * and that PropertyChange and visual changes are propagated. This should not be
     * called in highly compute intensive sections of code, but only at the end of the
     * scene integration.
     **/
    public void update();
}
