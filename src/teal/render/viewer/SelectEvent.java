/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SelectEvent.java,v 1.4 2007/07/16 22:04:58 pbailey Exp $ 
 * 
 */

package teal.render.viewer;

import java.util.*;


public class SelectEvent extends EventObject{

    private static final long serialVersionUID = 3976741375884735285L;
    
    public static final int NOT_SELECTED = 0;
    public static final int SELECT = 1;
    public static final int MULTI_SELECT = 2;
    
    
    protected int status;
    
    public SelectEvent(Object source, int status)
    {
        super(source);
        this.status = status;
    }
    
    public int getStatus()
    {
        return status;
    }

    public void setStatus(int st)
    {
        status = st;
    }
}