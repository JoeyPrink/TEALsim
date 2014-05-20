/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BoxNode.java,v 1.11 2010/07/06 19:23:44 stefan Exp $ 
 * 
 */
package teal.render.jme;

import java.awt.Color;
import java.nio.FloatBuffer;

import teal.render.ColorUtil;
import teal.render.TMaterial;

import com.jme.math.Vector3f;
import com.jme.scene.Line;
import com.jme.scene.shape.Box;
import com.jme.util.geom.BufferUtils;


public class BoxNode extends Node3D {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4443247203360558531L;

	protected final static Vector3f [] coords = { 
		new Vector3f(-1, 0, 0.5f),new Vector3f(0, 0, 0.5f),
		new Vector3f(0, 0, -0.5f),new Vector3f(-1, 0, -0.5f),
		new Vector3f(-1, 1, 0.5f),new Vector3f(0, 1, 0.5f),
		new Vector3f(0, 1, -0.5f),new Vector3f(-1, 1, -0.5f)};

	protected final static int [] idxE = 
	{ 0,1, 1,2, 2,3, 3,0, 0,4, 1,5, 2,6, 3,7, 4,5, 5,6, 6,7, 7,4 };
	
	public BoxNode() {
		Box fillShape = new Box("Box",
				new Vector3f(-1.0f,0.0f,-0.5f),
				new Vector3f(0.0f,1.0f,0.5f));
		
		TMaterial matRed = ColorUtil.getMaterial(Color.red);
		matRed.setShininess(0.5f);
		matRed.setTransparancy(0.5f);
		
		this.setMaterial(matRed);
		this.attachChild(fillShape);
		
		Vector3f [] vertices = new Vector3f[idxE.length];
		for(int i=0; i < idxE.length; i++)
			vertices[i] = new Vector3f(coords[idxE[i]]);
		
		FloatBuffer lines = BufferUtils.createFloatBuffer(vertices);
		
		Line frameShape = new Line("Box frame",lines, null, null, null);
		
		TMaterial matBlack = ColorUtil.getMaterial(Color.black);
        Node3D.setMaterial(matBlack, frameShape);
        
        this.attachChild(frameShape);

	}

}
