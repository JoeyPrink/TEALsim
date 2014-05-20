/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ShapeNode.java,v 1.12 2010/08/27 22:31:12 stefan Exp $ 
 * 
 */
package teal.render.jme;

import java.awt.Color;
import java.util.Enumeration;

import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.GeometryInfo;

import teal.render.ColorUtil;
import teal.render.TAbstractRendered;
import teal.render.TMaterial;
import teal.render.TealMaterial;
import teal.render.scene.TShapeNode;
import teal.util.TDebug;

public class ShapeNode extends Node3D implements TShapeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5677387007020318388L;

	public ShapeNode() {
		
	}
	
	public ShapeNode(TAbstractRendered element) {
		this();
		doSetElement(element,false);		
	}
		
	@Override
    public void setSelected(boolean state) {
		if(state == this.isSelected())
			return;

		super.setSelected(state);
    	TMaterial mat = Node3D.getMaterial(this);
    	Color3f oldCol = ColorUtil.getColor3f(mat.getAmbient());
    	Color3f change = new Color3f(Color.WHITE);
    	change.scale(0.6f);
    	if(state) 
    		oldCol.add(change);
    	else 
    		oldCol.sub(change);
    	mat.setAmbient(oldCol);
    	Node3D.setMaterial(mat, this);
    }
	
	public void addGeometry(GeometryInfo geo) {
		TDebug.println(1,"Not implemented due to j3d incompatibility");
	}

	public Enumeration<GeometryInfo> getAllGeometries() {
		TDebug.println(1,"Not implemented due to j3d incompatibility");
		return null;
	}

	public GeometryInfo getGeometry() {
		TDebug.println(1,"Not implemented due to j3d incompatibility");
		return null;
	}

	public void removeAllGeometry() {
		this.detachAllChildren();
	}

	public void setGeometry(GeometryInfo geo) {
		TDebug.println(1,"Not implemented due to j3d incompatibility");		
		
	}

	public void setGeometry(GeometryInfo geo, int idx) {
		TDebug.println(1,"Not implemented due to j3d incompatibility");
	}

	public void setShininess(float shine) {
		TMaterial mat = Node3D.getMaterial(this);
		if(mat == null)
			mat = new TealMaterial();
		if(shine != mat.getShininess()) {
			mat.setShininess(shine);
			Node3D.setMaterial(mat, this);
		}
	}
	

	public void setTransparency(float x) {
		TMaterial mat = Node3D.getMaterial(this);
		if(mat == null)
			mat = new TealMaterial();
		if(x != mat.getTransparancy()) {
			mat.setTransparancy(x);
			Node3D.setMaterial(mat, this);
		}
	}

	public Color3f getColor() {
		TMaterial mat = Node3D.getMaterial(this);
		if(mat == null)
			mat = new TealMaterial();
		return ColorUtil.getColor3f(mat.getDiffuse());
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
