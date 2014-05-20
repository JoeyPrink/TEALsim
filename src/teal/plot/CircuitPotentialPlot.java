/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CircuitPotentialPlot.java,v 1.11 2010/09/01 20:14:04 stefan Exp $
 * 
 */

package teal.plot;

import java.io.Serializable;

import teal.physics.em.Circuit;

public class CircuitPotentialPlot implements PlotItem, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7243494578515667008L;

	Circuit circuit = null;
    boolean initialized = false;

    public CircuitPotentialPlot() {
    }
    
	public void setCircuit(Circuit circuit) {
		this.circuit = circuit;
	}

	protected void initialize(Graph graph) {
 
        initialized = true;
	}
	
	public void reset() {
	}
	
	
	public void doPlot(Graph graph)
    {
        int N = circuit.getNsamples();
        circuit.updateSurfacePotential();
		double [] V = circuit.getSurfacePotential();

		if(!initialized) {
        	initialize(graph);
			graph.setMarksStyle("various", 1);
/*            double min = V[0];
            double max = V[0];
            for(int i=1; i<N; i++) {
//            	System.out.println(V[i]);
            	if(V[i]>max) max=V[i];
            	if(V[i]<min) min=V[i];
            }
            TDebug.println(1, "max(V[k]) = " + max);
            TDebug.println(1, "min(V[k]) = " + min);
            graph.setXRange(0, N-1);
            graph.setYRange(min, max);
*/		}
//		System.out.println("update");
        graph.clear(0);
        graph.clear(1);
        //double current = circuit.getCurrent();
        //graph.clear(2);
        for(int k=0; k<N; k++) {
        	graph.addPoint(0, k, V[k],true);
        	graph.addPoint(1, k, V[k],true);
        	//graph.addPoint(2, k, +current*Math.sin(2.*Math.PI*(double)k/(double)N),true);
        }
    	graph.addPoint(0, N, V[0],true);
    	graph.addPoint(1, N, V[0],true);
//    	graph.addPoint(2, N, +current*Math.sin(2.*Math.PI*(double)0/(double)N),true);
     }

}
