/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GenericEField.java,v 1.4 2007/07/17 15:46:54 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import teal.field.GenericFieldGenerator;
import teal.sim.*;
import javax.vecmath.*;

/**
 * This is a wrapper class that wraps a <code>GenericFieldGenerator</code> as an electric field.
 */
public class GenericEField extends SimObj implements GeneratesE {
	
	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -8529245513206332966L;
    GenericFieldGenerator fieldGenerator;
	boolean isGeneratingE = true;
	
	public GenericEField(GenericFieldGenerator gen) {
		fieldGenerator = gen;
	}
	
	public boolean isGeneratingE() {
		return isGeneratingE;
	}
	
	public void setGeneratingE(boolean gen) {
		isGeneratingE = gen;
	}
	
	public Vector3d getE(Vector3d pos) {
		return fieldGenerator.getVectorField(pos);	
	}
	
	public Vector3d getE(Vector3d pos, double t) {
		return getE(pos);
	}
	
	public double getEFlux(Vector3d pos) {
		return fieldGenerator.getFirstScalarField(pos);
	}
	
	public double getEPotential(Vector3d pos) {
		return fieldGenerator.getSecondScalarField(pos);
	}
}
