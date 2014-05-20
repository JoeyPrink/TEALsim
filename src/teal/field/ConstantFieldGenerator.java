/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ConstantFieldGenerator.java,v 1.4 2007/07/16 22:04:45 pbailey Exp $
 * 
 */

package teal.field;

import javax.vecmath.*;


/**
 * This is a simple implementation of a constant background field.  Wrap in a GenericXField to add it to the world.
 */
public class ConstantFieldGenerator implements GenericFieldGenerator {
	Vector3d fieldDir;
	
	public ConstantFieldGenerator(Vector3d dir) {
		fieldDir = dir;
	}
	
	public Vector3d getVectorField(Vector3d pos) {
		return fieldDir;
	}
	
	public double getFirstScalarField(Vector3d pos) {
		return 0.;
	}
	
	public double getSecondScalarField(Vector3d pos) {
		return 0.;
	}
	
	public void setFieldDirection(Vector3d dir) {
		fieldDir = dir;
	}
	
	public Vector3d getFieldDirection() {
		return fieldDir;
	}
}
