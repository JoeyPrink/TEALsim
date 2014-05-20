/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PlanetaryPotentialGraph.java,v 1.1 2006/11/23 15:33:54 cshubert Exp $ 
 * 
 */

package teal.plot;

import java.io.Serializable;

import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.core.TElement;


public class PlanetaryPotentialGraph implements PlotItem, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2411426959566863629L;

	TElement b1 = null;
    String b1Charge = null;
    String b1Mass = null;
    String b1Pos = null;
    String b1Vel = null;
    
    TElement b2 = null;
    String b2Charge = null;
    String b2Mass = null;
    String b2Pos = null;
    String b2Vel = null;
    
    TElement indObj = null;
    String indVar = null;
    boolean connected = true;
    boolean initialized = false;
    
	public PlanetaryPotentialGraph() {

		b1Charge = "charge";
		b2Charge = "charge";
		b1Pos = "position";
		b2Pos = "position";
		b1Vel = "velocity";
		b2Vel = "velocity";
		b1Mass = "mass";
		b2Mass = "mass";
		indVar = "time";
	};

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
		updateAngularMomentum();
		updateTotalEnergy();
	}
	
	public void reset() {
		updateAngularMomentum();
		updateTotalEnergy();
	}
	
	private boolean updateRequest = true;
	private double angularMomentum = 0;
	private double totalEnergy = 0;

	public void updateAngularMomentum() {
        Number m1 = (Number) b1.getProperty(b1Mass);
        Number m2 = (Number) b2.getProperty(b2Mass);
        Vector3d pos1 = (Vector3d) b1.getProperty(b1Pos);
        Vector3d vel1 = (Vector3d) b1.getProperty(b1Vel);
        Vector3d pos2 = (Vector3d) b2.getProperty(b2Pos);
        Vector3d vel2 = (Vector3d) b2.getProperty(b2Vel);
		double mass1 = m1.doubleValue();
		double mass2 = m2.doubleValue();
		
		Vector3d centerOfMass = new Vector3d();
		Vector3d temp = new Vector3d();
		temp.set(pos1);
		temp.scale(mass1);
		centerOfMass.set(pos2);
		centerOfMass.scale(mass2);
		centerOfMass.add(temp);
		centerOfMass.scale(1/(mass1+mass2));

		Vector3d relPos1 = new Vector3d(pos1);
		relPos1.sub(centerOfMass);
		Vector3d relPos2 = new Vector3d(pos2); 
		relPos2.sub(centerOfMass);
		
        Vector3d angularMomentum1 = new Vector3d();
		angularMomentum1.cross(relPos1, vel1 );
		angularMomentum1.scale(mass1);
		
        Vector3d angularMomentum2 = new Vector3d();
		angularMomentum2.cross(relPos2, vel2 );
		angularMomentum2.scale(mass2);
		
        Vector3d vecAngularMomentum = new Vector3d(angularMomentum1);
		vecAngularMomentum.add( angularMomentum2 );
		
		angularMomentum = vecAngularMomentum.length();
		
		updateRequest = true;
	}

	public void updateTotalEnergy() {
        Number m1 = (Number) b1.getProperty(b1Mass);
        Number m2 = (Number) b2.getProperty(b2Mass);
        Vector3d pos1 = (Vector3d) b1.getProperty(b1Pos);
        Vector3d vel1 = (Vector3d) b1.getProperty(b1Vel);
        Vector3d pos2 = (Vector3d) b2.getProperty(b2Pos);
        Vector3d vel2 = (Vector3d) b2.getProperty(b2Vel);
        
        Vector3d relpos = new Vector3d();
        relpos.set(pos2);
        relpos.sub(pos1);

		Vector3d radial = new Vector3d(relpos);
		radial.normalize();
		
    
    	double gpEnergy;
    	gpEnergy = - m1.doubleValue() * m2.doubleValue() / relpos.length();

    	double kEnergy;
    	kEnergy = 0.5 * m1.doubleValue() * vel1.lengthSquared() * 1.;
    	kEnergy += 0.5 * m2.doubleValue() * vel2.lengthSquared() * 1.;

        totalEnergy = gpEnergy + kEnergy;
		
		updateRequest = true;
	}

	
	public void doPlot(Graph graph) {
		
        if(!initialized) {
        	initialize();
	        graph.addLegend(0,"Total Energy");
	        graph.addLegend(1,"Veff");
   	        graph.setXLabel("r");
        }

		if( updateRequest == true ) {
			updateRequest = false;
		} else {
			return;
		}

		graph.clear(0);
		graph.clear(1);
		graph.clear(3);
		// Potential Well -----------------------------------------------------------
        Number m1 = (Number) b1.getProperty(b1Mass);
        Number m2 = (Number) b2.getProperty(b2Mass);
        double mass1 = m1.doubleValue();
        double mass2 = m2.doubleValue();
        double mu = mass1*mass2/(mass1+mass2);
		// [----------  Auto-scale -----------]
			double rmin =	(angularMomentum * angularMomentum) /
							( mu * mass1 * mass2 * Teal.G_Constant );
			double Vmin =	(angularMomentum * angularMomentum) / (2. * mu * rmin * rmin )
							- Teal.G_Constant * mass1 * mass2 / rmin;
			double xmin = -1e-3;
			double xmax = 8 * rmin;
			double ymin = Vmin * 1.2;
			double ymax = 1.2 * ( totalEnergy>(-Vmin)?totalEnergy:(-Vmin) );
			graph.setXRange( xmin, xmax);
			graph.setYRange( ymin, ymax);
		// [----------------------------------]
        for( double r = xmin; r < 1.2*xmax; r += (1.2*xmax-xmin)/200. ) {
			double Veff =	(angularMomentum * angularMomentum) / (2. * mu * r * r )
							- Teal.G_Constant * mass1 * mass2 / r;
							
			graph.addPoint(1, r, Veff, connected); 
        }

		// Total Energy -------------------------------------------------------------
    	graph.addPoint(0,-0.1*xmax,totalEnergy, connected); 
    	graph.addPoint(0,1.2*xmax,totalEnergy, connected); 

		// Zero Energy --------------------------------------------------------------
    	graph.addPoint(3,-0.1*xmax,0, connected); 
    	graph.addPoint(3,1.2*xmax,0, connected); 

        graph.setTitle("E = " + Math.floor(totalEnergy*100.)/100. + ", Vmin = " + Math.floor(Vmin*100.)/100.);
    }

}
