/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GenericPField.java,v 1.4 2007/07/17 15:46:54 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.Vector3d;

import teal.field.GenericFieldGenerator;
import teal.sim.SimObj;

/**
 * This is a wrapper class that wraps a <code>GenericFieldGenerator</code> as a Pauli field.
 */
public class GenericPField extends SimObj implements GeneratesP{
	GenericFieldGenerator fieldGenerator;
	boolean isGeneratingP = true;
	
	public GenericPField(GenericFieldGenerator gen) {
		fieldGenerator = gen;
	}
	
	public boolean isGeneratingP() {
		return isGeneratingP;
	}
	
	public void setGeneratingP(boolean gen) {
		isGeneratingP = gen;
	}
	
	public Vector3d getP(Vector3d pos) {
		return fieldGenerator.getVectorField(pos);	
	}
	
	public Vector3d getP(Vector3d pos, double t) {
		return getP(pos);
	}
	
	public double getPFlux(Vector3d pos) {
		return fieldGenerator.getFirstScalarField(pos);
	}
	
	public double getPPotential(Vector3d pos) {
		return fieldGenerator.getSecondScalarField(pos);
	}
}

