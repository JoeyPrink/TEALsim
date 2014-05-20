/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ImageStatusEvent.java,v 1.3 2007/07/16 22:05:19 pbailey Exp $ 
 * 
 */

package teal.visualization.image;

import java.util.*;


public class ImageStatusEvent extends EventObject{

    private static final long serialVersionUID = 3545520595006861875L;
    
    public static final int IDLE = -1;
    public static final int START = 0;

    public static final int COMPLETE = 1;
    public static final int INVALID = 2;
    public static final int VALID = 3;
    public static final int END_FAILURE = 4;
    public static final int END_FORCED = 5;
    public static final int PROGRESS = 6;
    
    protected int status;
    protected Object data = null;
    
    public ImageStatusEvent(Object source, int status)
    {
        super(source);
        this.status = status;
        //Throwable t = new Throwable("Image: " + source +" status" + status);
        //TDebug.printThrown(0,t);
    }
    
    public ImageStatusEvent(Object source, int status,Object obj)
    {   
        this(source,status);
        data = obj;
    }
    public int getStatus()
    {
        return status;
    }

    public void setStatus(int st)
    {
        status = st;
    }
    
    public void setData(Object d)
    {
        data = d;
    }
    
    public Object getData()
    {
        return data;
    }
}