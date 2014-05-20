/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WallNode.java,v 1.15 2011/05/27 15:39:40 pbailey Exp $ 
 * 
 */
package teal.render.jme;

import java.awt.Color;

import javax.vecmath.Color3f;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Line;
import com.jme.scene.Line.Mode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.MaterialState.MaterialFace;

import teal.render.ColorUtil;
import teal.render.HasColor;
import teal.render.TMaterial;
import teal.render.TealMaterial;

public class WallNode extends Node3D implements HasColor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WallNode() {
		Quad fillShape = new Quad("fillShape", 1f,1f);
		setFaceMode(fillShape,MaterialFace.FrontAndBack);
		TMaterial fillMat = ColorUtil.getMaterial(Color.GRAY);
		fillMat.setTransparancy(0.75f);
		fillMat.setShininess(0.5f);
		Node3D.setMaterial(fillMat, fillShape);		
		
		fillShape.setModelBound(new BoundingBox());
		fillShape.updateModelBound();

		this.attachChild(fillShape);
		

		Line frameShape = new Line("frameShape", new Vector3f[]{
				new Vector3f(-0.5f,-0.5f,0f), new Vector3f(0.5f,-0.5f,0f),
				new Vector3f(0.5f,0.5f,0f), new Vector3f(-0.5f,0.5f,0f)
		} ,null,null,null);
		
		frameShape.setMode(Mode.Loop);
		
		TMaterial frameMat = ColorUtil.getMaterial(Color.black);
		frameMat.setCullMode(TMaterial.CULL_FRONT);
		Node3D.setMaterial(frameMat, frameShape);
		
		this.attachChild(frameShape);
	}


	public Color3f getColor() {
		return ColorUtil.getColor3f(this.getMaterial().getDiffuse());
	}

	public void setColor(Color3f q) {
		TMaterial mat = Node3D.getMaterial(this);
		if(mat == null) 
			mat = new TealMaterial();

		Color3f ambient = new Color3f(q);
		ambient.scale(0.9f);
		mat.setAmbient(ambient);
		mat.setDiffuse(q);
		mat.setSpecular(q);
		Node3D.setMaterial(mat, this);
	}

	public void setColor(Color q) {
		setColor(new Color3f(q));
	}

}
