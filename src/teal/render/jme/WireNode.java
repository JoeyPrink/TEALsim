/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WireNode.java,v 1.9 2010/07/06 19:23:44 stefan Exp $ 
 * 
 */
package teal.render.jme;

import java.awt.Color;

import com.jme.scene.Node;

import teal.render.ColorUtil;
import teal.render.TMaterial;

public class WireNode extends Node3D {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 452824862039911408L;
	
	private final static String ARROW_NAME = "arrow";

	public WireNode(float len, float rad) {
		super();
		
		UprightCylinder wire = new UprightCylinder("wire", 4, 24, rad, len, true, false);
		TMaterial wiremat = ColorUtil.getMaterial(new Color(154, 105,0));
		wiremat.setShininess(0.8f);
		wiremat.setTransparancy(0.5f);
		
		Node3D.setMaterial(wiremat, wire);
		
		this.attachChild(wire);
		
		Node arrowNode = new Node(ARROW_NAME);
		Node actualArrow = new Node();
		arrowNode.attachChildAt(actualArrow, 0);
		
		UprightCylinder line = new UprightCylinder("line", 4, 20, 0.05f, 1f, true, false);
		line.setLocalTranslation(0f, 0.5f, 0f);
		actualArrow.attachChild(line);
		
		UprightCylinder cone = new UprightCylinder("cone", 4, 15, 0.2f, 0f, 0.25f, true, false);
		cone.setLocalTranslation(0f, 1f, 0f);
		actualArrow.attachChild(cone);
		
		actualArrow.setLocalScale(4);
		this.attachChild(arrowNode);
		
		Node3D.setMaterial(ColorUtil.getMaterial(new Color(154,105,0)), arrowNode);		
	}
	
	public void setArrowDirection(float current) {
		((Node)this.getChild(ARROW_NAME)).getChild(0).setLocalScale(current);
	}

}
