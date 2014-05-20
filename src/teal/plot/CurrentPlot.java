/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CurrentPlot.java,v 1.16 2010/09/01 20:14:04 stefan Exp $
 * 
 */

package teal.plot;

import java.io.Serializable;

import teal.physics.em.RingOfCurrent;

/**
 * PlotItem for plotting the current in a RingOfCurrent versus time.
 */
public class CurrentPlot implements PlotItem,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2002537090894623067L;
	
	RingOfCurrent ring = null;
    boolean initialized = false;
    boolean reset = true;
    boolean timeAutoscale = true;
    boolean currentAutoscale = true;
    double cumulativeTime = 0.;

    double [] initial_xrange = null;
    double [] initial_yrange = null;
    
    public CurrentPlot() {
    }
    
	public void setRing(RingOfCurrent ring) {
		this.ring = ring;
	}

	protected void initialize(Graph graph) {
		initialized = true;
		initial_xrange = graph.getXRange();
		initial_yrange = graph.getYRange();
	}
	
	public void reset() {
		cumulativeTime = 0.;
		reset = true;
	}
	
	
	public void setTimeAutoscale( boolean x ) {
		timeAutoscale = x;
	}

	public void setCurrentAutoscale( boolean x ) {
		currentAutoscale = x;
	}

	
	public void doPlot(Graph graph)
    {
        if(!initialized) {
        	initialize(graph);
        }
        
		double time = ring.getSimEngine().getTime();
		double current = ring.getCurrent();
		
		// Hack, just to observe the position of the magnet.
//		ArrayList array = (ArrayList) ring.getModel().getPhysicalObjs();
//		current = ((PhysicalObject) array.get(0)).getPosition().x; 
		


		double [] xrange = graph.getXRange();
		double [] yrange = graph.getYRange();
		
		
		if( timeAutoscale ) {
			if( time > xrange[1] ) {
				graph.setXRange(xrange[0], time);
			}
		}
		if( currentAutoscale ) {
			if( !timeAutoscale && time > cumulativeTime+(xrange[1]-xrange[0]) ) {
				graph.setYRange(initial_yrange[0], initial_yrange[1]);
				cumulativeTime += (xrange[1]-xrange[0]);
				yrange = graph.getYRange();
				// Enforce clearing.
				graph.clear(0);
			}
			if( current > yrange[1] ) {
				graph.setYRange(yrange[0], current);
			}
			if( current < yrange[0] ) {
				graph.setYRange(current, yrange[1]);
			}
		}
		if(reset) {
	       	graph.addPoint(0, time, current, true);
			reset = false;
		} else {
	       	graph.addPoint(0, time, current, true);
		}

     }

}
