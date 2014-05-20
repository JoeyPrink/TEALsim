/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SeafloorPlot.java,v 1.7 2007/07/17 15:46:58 pbailey Exp $ 
 * 
 */

package teal.plot;

import javax.vecmath.Vector3d;

import teal.physics.em.Seafloor;
/**
 * @author danziger
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SeafloorPlot implements PlotItem {
	Seafloor seafloor;
	int resolution;
	double scanHeight;
	
	public SeafloorPlot(Seafloor sf,int res) {
		super();
		seafloor = sf;
		resolution = 100;
		scanHeight = seafloor.getScanHeight();
		
	}
	
	
	public void doPlot(Graph graph) {
		graph.clear(0);
		graph.clear(1);
		graph.clear(3);
		
		//graph.setColor(false);
		scanHeight = seafloor.getScanHeight();
		
		double xmin = -seafloor.getRange();
		double xmax = seafloor.getRange();
		graph.setXRange(xmin,xmax);
		//graph.setYRange(scanHeight*0.5,scanHeight*1.5);
		graph.setYRange(-20,20);
		
		double step = (xmax-xmin)/(double)resolution;
		
		Vector3d earthb = new Vector3d(seafloor.getEarthField());
		earthb.normalize();
		
		for (int i = 0; i < resolution; i++) {
			double xgraph = xmin+(double)i*step;
			//System.out.println("xgraph = "  + xgraph);
			Vector3d xloc = new Vector3d(xgraph,scanHeight,0);
			Vector3d b = new Vector3d(seafloor.getB(xloc));
			double bdoteb = b.dot(earthb);
			//graph.addPoint(0,xgraph,bdoteb*0.5,true);
			if (i == resolution -1) {
				graph.addPoint(0,xgraph,bdoteb*0.5,true);
			} else {
				graph.addPoint(0,xgraph,bdoteb*0.5,true);
			}
		}
		
		// These are the lines that show the position of each edge slider
		for (int i = 0; i < seafloor.getRightEdges().size(); i++) {
			double edgex = ((Seafloor.EdgeNode)(seafloor.getRightEdges().get(i))).getPosition().x;
			graph.addPoint(1,edgex,50,false);
			graph.addPoint(1,edgex,-50,true);
			graph.addPoint(1,-edgex,50,false);
			graph.addPoint(1,-edgex,-50,true);
		}
		
		// This is just a line down the middle
		graph.addPoint(3,0,100,true);
		graph.addPoint(3,0,-100,true);
		
	}
	
	
	/**
	 * @return Returns the resolution.
	 */
	public int getResolution() {
		return resolution;
	}
	/**
	 * @param resolution The resolution to set.
	 */
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	/**
	 * @return Returns the scanHeight.
	 */
	public double getScanHeight() {
		return scanHeight;
	}
	/**
	 * @param scanHeight The scanHeight to set.
	 */
	public void setScanHeight(double scanHeight) {
		this.scanHeight = scanHeight;
	}
}
