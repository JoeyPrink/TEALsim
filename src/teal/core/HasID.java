/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasID.java,v 1.1 2007/08/13 22:05:59 pbailey Exp $
 * 
 */

package teal.core;

import java.io.Serializable;


public interface HasID extends Serializable {
    public String getID();
    public void setID(String id);
}