/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasElementManager.java,v 1.3 2007/07/16 22:04:44 pbailey Exp $
 * 
 */
package teal.core;


/**
 * @author pbailey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface HasElementManager {
    
    public void setElementManager(TElementManager em);
    public TElementManager getElementManager();

}
