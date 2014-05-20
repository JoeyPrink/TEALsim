/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TwoBodyEnergyGraph2.java,v 1.1 2006/11/23 15:33:54 cshubert Exp $ 
 * 
 */

package teal.plot;

import java.io.Serializable;

import javax.vecmath.Vector3d;

import teal.core.TElement;


public class TwoBodyEnergyGraph2 implements PlotItem, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 451249033330287503L;
	
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
    
	public TwoBodyEnergyGraph2() {

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
	}
	
	public void reset() {
	
	}
	
	public void doPlot(Graph graph) {

        if(!initialized) {
        	initialize();
	        graph.setXLabel("Time");
	        graph.setYLabel("Energy"); 
	        graph.addLegend(0,"Potential");
	        graph.addLegend(1,"Kinetic");      
	        graph.addLegend(2,"Total");      
        }
        
//        Number q1 = (Number) b1.getProperty(b1Charge);
//        Number q2 = (Number) b2.getProperty(b2Charge);
        Number m1 = (Number) b1.getProperty(b1Mass);
        Number m2 = (Number) b2.getProperty(b2Mass);
        Vector3d pos1 = (Vector3d) b1.getProperty(b1Pos);
        Vector3d vel1 = (Vector3d) b1.getProperty(b1Vel);
        Vector3d pos2 = (Vector3d) b2.getProperty(b2Pos);
        Vector3d vel2 = (Vector3d) b2.getProperty(b2Vel);
        
        Vector3d relpos = new Vector3d();
        relpos.set(pos2);
        relpos.sub(pos1);

    	Number t = (Number) indObj.getProperty(indVar);
    
    	double gpEnergy;
    	gpEnergy = - m1.doubleValue() * m2.doubleValue() / relpos.length();
    	//gpEnergy = gpEnergy; // To account for units and scales.
    	graph.addPoint(0,t.doubleValue(),gpEnergy,connected); 

    	double kEnergy;
    	kEnergy = 0.5 * m1.doubleValue() * vel1.lengthSquared() * 1.;
    	kEnergy += 0.5 * m2.doubleValue() * vel2.lengthSquared() * 1.;
    	//kEnergy = kEnergy; // // To account for units and scales.
    	
    	graph.addPoint(1,t.doubleValue(),kEnergy,connected);   
        
        double totalEnergy = gpEnergy + kEnergy;
        
       	graph.addPoint(2,t.doubleValue(),totalEnergy,connected);   
        
    }

}
