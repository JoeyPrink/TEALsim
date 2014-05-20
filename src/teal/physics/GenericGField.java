/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GenericGField.java,v 1.4 2007/07/17 15:46:52 pbailey Exp $ 
 * 
 */

package teal.physics;

import javax.vecmath.Vector3d;

import teal.field.GenericFieldGenerator;
import teal.sim.SimObj;

/**
 * This is a wrapper class that wraps a <code>GenericFieldGenerator</code> as a gravitational field.
 */
public class GenericGField extends SimObj implements GeneratesG{
	GenericFieldGenerator fieldGenerator;
	boolean isGeneratingG = true;
	
	public GenericGField(GenericFieldGenerator gen) {
		fieldGenerator = gen;
	}
	
	public boolean isGeneratingG() {
		return isGeneratingG;
	}
	
	public void setGeneratingG(boolean gen) {
		isGeneratingG = gen;
	}
	
	public Vector3d getG(Vector3d pos) {
		return fieldGenerator.getVectorField(pos);	
	}
	
	public Vector3d getG(Vector3d pos, double t) {
		return getG(pos);
	}
	
	public double getGFlux(Vector3d pos) {
		return fieldGenerator.getFirstScalarField(pos);
	}
	
	public double getGPotential(Vector3d pos) {
		return fieldGenerator.getSecondScalarField(pos);
	}
}

