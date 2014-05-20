/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SpatialGraph.java,v 1.5 2007/07/16 22:04:49 pbailey Exp $ 
 * 
 */

package teal.plot;

import java.util.Iterator;

import teal.sim.properties.IsSpatial;
/**
 * @author danziger
 *
 * SpatialGraph extends Graph, but calls doPlot through nextSpatial() rather than update().  This allows for 
 * non-time dependent graphs that refresh only when needsSpatial() is called, rather than constantly updating even
 * if nothing has changed.
 */
public class SpatialGraph extends Graph implements IsSpatial {

    private static final long serialVersionUID = 3258415044919570489L;

    public SpatialGraph() {
		super();
	}
	
	public void update() {
		// do nothing
	}
	
	public void needsSpatial() {
		// whatever
	}
	
	public void nextSpatial() {
		Iterator it = plotItems.iterator();
        while (it.hasNext())
        {
            PlotItem pi = (PlotItem) it.next();
            //TDebug.println(0,"Calling doPlot");
           pi.doPlot(this);
        }
        repaint();
        //Thread t = new Thread();
        Thread.yield();
	}
}
