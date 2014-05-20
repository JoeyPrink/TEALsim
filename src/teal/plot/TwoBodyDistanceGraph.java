/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TwoBodyDistanceGraph.java,v 1.1 2006/11/23 15:33:54 cshubert Exp $ 
 * 
 */

package teal.plot;

import java.io.Serializable;

import javax.vecmath.Vector3d;

import teal.core.TElement;

public class TwoBodyDistanceGraph implements PlotItem, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8359079405118140832L;
	
	TElement b1 = null;
    String b1Mass = null;
    String b1Pos = null;
    String b1Vel = null;
    
    TElement b2 = null;
    String b2Mass = null;
    String b2Pos = null;
    String b2Vel = null;
    
    TElement indObj = null;
    String indVar = null;
    boolean connected = true;
    boolean initialized = false;

    Vector3d planeNormal = null;
    
    public TwoBodyDistanceGraph() {
    
	    b1Pos = "position";
	    b2Pos = "position";
	    b1Vel = "velocity";
	    b2Vel = "velocity";
	    b1Mass = "mass";
	    b2Mass = "mass";
	    indVar = "time";
	    
	    planeNormal = new Vector3d(0, 0, 1);
    
    }
    
    public void setPlane( Vector3d pplaneNormal ) {
    	planeNormal = new Vector3d(pplaneNormal);
    }
    
	public void setBodyOne(TElement obj) {
		b1 = obj;
	}

	public void setBodyTwo(TElement obj) {
		b2 = obj;
	}

	public void setIndObj(TElement obj) {
		indObj = obj;
	}

	public void setIndVar(String var) {
		indVar = var;
	}

	protected void initialize() {
		initialized = true;
	}
	
	private boolean firstTime = true;
	private boolean started = false;
	private boolean ended = false;
	private Vector3d initial = null;	

	public void reset() {
		firstTime = true;
		started = false;
		ended = false;
		initial = null;	
	}
	
	
	Vector3d _planePosition = new Vector3d();
	double stepSize = 0.01;	
		
	public void doPlot(Graph graph)
    {
        if(!initialized) {
        	initialize();
			graph.setMarksStyle("various", 1);
	        graph.setXLabel("X");
	        graph.setYLabel("Y"); 
	        graph.addLegend(0,"Relative Orbit");
        }
        
        Vector3d pos1 = (Vector3d) b1.getProperty(b1Pos);
        Vector3d pos2 = (Vector3d) b2.getProperty(b2Pos);
        
        Vector3d relpos = new Vector3d();
        relpos.set(pos2);
        relpos.sub(pos1);
        
        Vector3d planePosition = new Vector3d();
        planePosition.set(planeNormal);
        planePosition.scale( - relpos.dot(planeNormal) );
        planePosition.add(relpos);


		double [] xrange = graph.getXRange();
		double [] yrange = graph.getYRange();
		
		
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
		

		if( planePosition.x > xrange[1] ) {
			graph.setXRange(xrange[0], planePosition.x);
		}
		if( planePosition.x < xrange[0] ) {
			graph.setXRange(planePosition.x, xrange[1]);
		}
		if( planePosition.y > yrange[1] ) {
			graph.setYRange(yrange[0], planePosition.y);
		}
		if( planePosition.y < yrange[0] ) {
			graph.setYRange(planePosition.y, yrange[1]);
		}


		graph.clear(1);
       	graph.addPoint(1, planePosition.x, planePosition.y, connected);

    	if(ended) return;

		double Dx = Math.abs(graph.getXRange()[0]-graph.getXRange()[1]);
		double Dy = Math.abs(graph.getYRange()[0]-graph.getYRange()[1]);
		double epsilon = 0.01;

		stepSize = epsilon*Math.sqrt(Dx*Dx+Dy*Dy)/2.;

		if( firstTime ) {
			firstTime = false;
			initial = new Vector3d(planePosition);
       		graph.addPoint(0, planePosition.x, planePosition.y, connected);
       		_planePosition.set(planePosition);
		} else {
			Vector3d increment = new Vector3d(planePosition);
			increment.sub(_planePosition);
			if(increment.length() > stepSize) {
	       		graph.addPoint(0, planePosition.x, planePosition.y, connected);
	       		_planePosition.set(planePosition);
			}
		}
		
		
		if(	started &&
				Math.abs(planePosition.x-initial.x) < epsilon*Dx &&
				Math.abs(planePosition.y-initial.y) < epsilon*Dy ) {
				// Did we come back into the initial "region"?				
				ended = true;
		}

		if(	Math.abs(planePosition.x-initial.x) > epsilon*Dx &&
				Math.abs(planePosition.y-initial.y) > epsilon*Dy ) {
				// Did we get out of the initial "region"?				
				started = true;
		}


		

       	
     }

}
