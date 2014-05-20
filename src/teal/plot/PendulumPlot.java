/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PendulumPlot.java,v 1.8 2010/09/01 20:14:04 stefan Exp $ 
 * 
 */

package teal.plot;

import java.io.Serializable;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import teal.physics.mech.WeightedPhysicalPendulum;

/**
 * PlotItem for plotting rotational motion of a pendulum.
 */
public class PendulumPlot implements PlotItem, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3979838456125208902L;
	
	WeightedPhysicalPendulum pendulum = null;
    boolean initialized = false;
    boolean reset = true;
    boolean timeAutoscale = true;
    boolean fluxAutoscale = true;
    
    double [] initial_xrange = null;
    double [] initial_yrange = null;
    double cumulativeTime = 0.;
    
    AxisAngle4d aa = new AxisAngle4d();
    
    public PendulumPlot() {
    }
    
	public void setPendulum(WeightedPhysicalPendulum p) {
		this.pendulum = p;
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

	public void setFluxAutoscale( boolean x ) {
		fluxAutoscale = x;
	}
		
	public void doPlot(Graph graph)
    {
        if(!initialized) {
        	initialize(graph);
        }
        
		double time = pendulum.getSimEngine().getTime();
		
		aa.set(pendulum.getRotation()); // orientation_d?
		Vector3d aaxis = new Vector3d(aa.x,aa.y,aa.z);
		double theta = aa.angle * aaxis.dot(pendulum.getPivot_axis());
		//System.out.println("PendulumPlot theta = " + theta);
		
		double torque = pendulum.getTorque().dot(pendulum.getPivot_axis());
		torque *= 100.;
		double max = (theta>torque)?theta:torque;
		double min = (theta<torque)?theta:torque;


		double [] xrange = graph.getXRange();
		double [] yrange = graph.getYRange();
		
/*		
		double wr = (xrange[1] - xrange[0]) / (double)graph.getBoxWidth();
		double hr = (yrange[1] - yrange[0]) / (double)graph.getBoxHeight();
		if(  wr > hr ) {
			double p = ( yrange[0] + yrange[1] )/2.;
			double q = wr / hr;
			graph.setYRange((yrange[0]-p)*q+p, (yrange[1]-p)*q+p);
		} else {
			double p = ( xrange[0] + xrange[1] )/2.;
			double q = hr / wr;
			graph.setXRange((xrange[0]-p)*q+p, (xrange[1]-p)*q+p);
		}
*/		
		
		if( timeAutoscale ) {
			if( time > xrange[1] ) {
				graph.setXRange(xrange[0], time);
			}
		}
		if( fluxAutoscale ) {
			if( !timeAutoscale && time > cumulativeTime+(xrange[1]-xrange[0]) ) {
				graph.setYRange(initial_yrange[0], initial_yrange[1]);
				cumulativeTime += (xrange[1]-xrange[0]);
				yrange = graph.getYRange();
				// Enforce clearing.
				graph.clear(0);
				graph.clear(1);
			}
			if( max > yrange[1] ) {
				graph.setYRange(yrange[0], max);
			}
			if( min < yrange[0] ) {
				graph.setYRange(min, yrange[1]);
			}
		}
		if(reset) {
			graph.addPoint(0, time, theta, false);
			graph.addPoint(1, time, torque, false);
			reset = false;
		} else {
			graph.addPoint(0, time, theta, true);
			graph.addPoint(1, time, torque, true);
		}

     }

}
