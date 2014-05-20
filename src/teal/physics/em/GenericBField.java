/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GenericBField.java,v 1.4 2007/07/17 15:46:54 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.Vector3d;

import teal.field.GenericFieldGenerator;
import teal.sim.SimObj;

/**
 * This is a wrapper class that wraps a <code>GenericFieldGenerator</code> as a magnetic field.
 */
public class GenericBField extends SimObj implements GeneratesB{
	GenericFieldGenerator fieldGenerator;
	boolean isGeneratingB = true;
	
	public GenericBField(GenericFieldGenerator gen) {
		fieldGenerator = gen;
	}
	
	public boolean isGeneratingB() {
		return isGeneratingB;
	}
	
	public void setGeneratingB(boolean gen) {
		isGeneratingB = gen;
	}
	
	public Vector3d getB(Vector3d pos) {
		return fieldGenerator.getVectorField(pos);	
	}
	
	public Vector3d getB(Vector3d pos, double t) {
		return getB(pos);
	}
	
	public double getBFlux(Vector3d pos) {
		return fieldGenerator.getFirstScalarField(pos);
	}
	
	public double getBPotential(Vector3d pos) {
		return fieldGenerator.getSecondScalarField(pos);
	}
}
