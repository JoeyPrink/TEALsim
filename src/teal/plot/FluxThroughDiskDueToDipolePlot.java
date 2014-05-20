/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FluxThroughDiskDueToDipolePlot.java,v 1.2 2010/09/01 20:14:04 stefan Exp $
 * 
 */

package teal.plot;

import java.io.Serializable;

import teal.physics.em.MagneticDipole;
import teal.render.j3d.ShapeNode;
import javax.vecmath.*;

import teal.math.SpecialFunctions;

/**
 * PlotItem for plotting the magnetic flux through a disk in the presence of a magnetic dipole versus time.
 */
public class FluxThroughDiskDueToDipolePlot implements PlotItem, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6494022666353453210L;
	
	MagneticDipole mag = null;
	ShapeNode ShapeNodeDisk = null;
	Vector3d NormalDisk = new Vector3d();
	double radDisk;
    boolean initialized = false;
    boolean reset = true;
    boolean timeAutoscale = true;
    boolean magneticfluxAutoscale = true;
    double cumulativeTime = 0.;

    double [] initial_xrange = null;
    double [] initial_yrange = null;
    
    public FluxThroughDiskDueToDipolePlot() {
    }
    
	public void setMagneticDipole(MagneticDipole mag) {
		this.mag = mag;
	}

	public void setShapeNode(ShapeNode ShapeNodeDisk) {
		this.ShapeNodeDisk = ShapeNodeDisk;
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
		magneticfluxAutoscale = x;
	}


	public void setNormalDisk( Vector3d NormalDisk ) {
		this.NormalDisk = NormalDisk;
	}


	public void setRadiusDisk( double radDisk ) {
		this.radDisk = radDisk;
	}

	public void doPlot(Graph graph)
    {
        if(!initialized) {
        	initialize(graph);
        }
        
		double time = mag.getSimEngine().getTime();

		double magneticflux = 0.;   //mag.getCurrent();
		
		Vector3d posDisk = ShapeNodeDisk.getPosition();

//	public static double FluxThroughRingDueToDipole(Vector3d posDip, Vector3d dirDip, Vector3d posDisk, Vector3d dirDisk, double radDisk, double dipMoment) 

		magneticflux = SpecialFunctions.FluxThroughRingDueToDipole(new Vector3d(0.,0.,0.), new Vector3d(0.,1.,0.), posDisk, NormalDisk, radDisk, 1.) ;
//		System.out.println("plot position "+ posDisk + " rotation "+ rotation + " magneticflux "+magneticflux);
		double [] xrange = graph.getXRange();
		double [] yrange = graph.getYRange();
		
		
		if( timeAutoscale ) {
			if( time > xrange[1] ) {
				graph.setXRange(xrange[0], time);
			}
		}
		if( magneticfluxAutoscale ) {
			if( !timeAutoscale && time > cumulativeTime+(xrange[1]-xrange[0]) ) {
				graph.setYRange(initial_yrange[0], initial_yrange[1]);
				cumulativeTime += (xrange[1]-xrange[0]);
				yrange = graph.getYRange();
				// Enforce clearing.
				graph.clear(0);
			}
			if( magneticflux > yrange[1] ) {
				graph.setYRange(yrange[0], magneticflux);
			}
			if( magneticflux < yrange[0] ) {
				graph.setYRange(magneticflux, yrange[1]);
			}
		}
		if(reset) {
	       	graph.addPoint(0, time, magneticflux, true);
			reset = false;
		} else {
	       	graph.addPoint(0, time, magneticflux, true);
		}

     }

}
