/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PlotProperties.java,v 1.9 2010/09/01 20:14:04 stefan Exp $ 
 * 
 */

package teal.plot;

import java.io.Serializable;
import java.lang.reflect.Method;

import teal.core.TElement;
import teal.util.TDebug;

public class PlotProperties implements PlotItem, Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6738487338938485243L;
	
	TElement objX = null;
    String propX = null;
    Method methodX = null;
    double scaleX;
    TElement objY = null;
    String propY = null;
    Method methodY = null;
    double scaleY;
    int dataChannel = 0;
    boolean connected = true;
    boolean initialized = false;
    
    
    public PlotProperties()
    {
    }
    
    public void setObjectX(TElement obj)
    {
        objX = obj;
        initialized = false;
    }
    public TElement getObjectX()
    {
        return objX;
    }
    public void setPropertyX(String propName)
    {
            propX = propName;
            initialized = false;
    }
    public String getPropertyX()
    {
        return propX;
    }
    public void setObjectY(TElement obj)
    {
        objY = obj;
        initialized = false;
    }
    public TElement getObjectY()
    {
        return objY;
    }
    public void setPropertyY(String propName)
    {
            propY = propName;
            initialized = false;
    }
    public String getPropertyY()
    {
        return propY;
    }
    public void setDataChannel(int ch)
    {
        dataChannel = ch;
    }
    public int getDataChannel()
    {
        return dataChannel;
    }
    
    protected void initialize()
    {
        initialized = true;
    }
    
    public void doPlot(Graph graph)
    {
        TDebug.println(1,"In doPlot");
        if(!initialized)
            initialize();
        
        Number x  = (Number) objX.getProperty(propX);
        Number y  = (Number) objY.getProperty(propY);
        graph.addPoint(dataChannel,x.doubleValue(),y.doubleValue(),connected);    
    }
}
