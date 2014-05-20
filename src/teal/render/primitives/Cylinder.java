/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Cylinder.java,v 1.3 2010/07/06 19:31:27 pbailey Exp $
 * 
 */
 
package teal.render.primitives;

import teal.sim.properties.HasLength;

public class Cylinder extends Sphere implements HasLength {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6012343927907045941L;
	protected double length = 1.;
	public Cylinder(){
		super();
		nodeType = NodeType.CYLINDER;
	}
	
	public Cylinder(double rad, double len){
		super(rad);
		length = len;
		nodeType = NodeType.CYLINDER;
	}
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

}
