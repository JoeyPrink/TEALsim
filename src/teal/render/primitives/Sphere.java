/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Sphere.java,v 1.8 2010/07/06 19:31:27 pbailey Exp $
 * 
 */
 
package teal.render.primitives;

import teal.render.Rendered;
import teal.render.scene.SceneFactory;
import teal.render.scene.TNode3D;
import teal.render.scene.TShapeNode;
import teal.sim.properties.HasRadius;

public class Sphere extends Rendered implements HasRadius{
	
	private static final long serialVersionUID = 1L;
	//private int segments = 12;
	protected double radius = 1.;
	//protected float transparency = 0f;
	//protected boolean transparencyChanged = false;
	
	
	public Sphere() {
		super();
		nodeType = NodeType.SPHERE;
	}
	
	public Sphere(double radius) {
		this();
		this.radius = radius;
	}
	
	public TNode3D makeNode() {
		TNode3D node = SceneFactory.makeNode(this);
		return node;
	}
	
	public void render() {
//		if (transparencyChanged) {
//			((TShapeNode)mNode).setTransparency(transparency);
//			transparencyChanged = false;
//		}
		super.render();
	}

	/**
	 * @return Returns the radius.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @param radius The radius to set.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

//	/**
//	 * @return Returns the transparency.
//	 */
//	public float getTransparency() {
//		return transparency;
//	}

//	/**
//	 * @param transparency The transparency to set.
//	 */
//	public void setTransparency(float transparency) {
//		this.transparency = transparency;
//		transparencyChanged = true;
//	}
	
	
}
