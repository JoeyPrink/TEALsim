/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasFramework.java,v 1.5 2007/07/17 21:00:53 pbailey Exp $
 * 
 */
package teal.framework;


/**
 * @author pbailey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface HasFramework {
    public TFramework getFramework();
    public void setFramework(TFramework fw);
}
