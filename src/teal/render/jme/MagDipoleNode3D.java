/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: MagDipoleNode3D.java,v 1.13 2010/09/02 19:44:18 stefan Exp $ 
 * 
 */
package teal.render.jme;

import java.awt.Color;
import java.io.IOException;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

import teal.render.ColorUtil;
import teal.render.TAbstractRendered;
import teal.render.TMaterial;
import teal.render.scene.TMagDipoleNode3D;
import teal.sim.properties.HasLength;
import teal.sim.properties.HasRadius;

public class MagDipoleNode3D extends Node3D implements TMagDipoleNode3D{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1075042391738298288L;
	private transient UprightCylinder blueShape;
	private transient UprightCylinder redShape;
	private transient UprightCylinder centerShape;
	
	private float length = 1f;
	private float radius = 0.2f;

	public MagDipoleNode3D() {
		updateGeometry(length, radius);		
	}
	
	public MagDipoleNode3D(TAbstractRendered elem) {
		super(elem);
		updateGeometry(length, radius);
	}

	@Override
	protected void doSetElement(TAbstractRendered element, final boolean inRenderThread){
		super.doSetElement(element, inRenderThread);
		if(element instanceof HasLength) 
			length = (float)((HasLength)element).getLength();
		if(element instanceof HasRadius)
			radius = (float)((HasRadius)element).getRadius();		
	}
	
	//TODO: replace this by TealMaterial Constructor call
	private static TMaterial getMaterial(Color col) {
		TMaterial redMat = ColorUtil.getMaterial(col);
		redMat.setShininess(0.5f);	
		return redMat;
	}
	
	public	void updateGeometry(double length, double radius) {
		this.length = (float)length;
		this.radius = (float)radius;
		if(redShape == null) {
			redShape = new UprightCylinder();
			redShape.setName("red shape");
			
			redShape.setLocalTranslation(0, this.length * 0.375f, 0);
			this.attachChild(redShape);
			
			Node3D.setMaterial(getMaterial(Color.red), redShape);			
		}

		if(blueShape == null) {
			blueShape = new UprightCylinder();
			blueShape.setName("blue shape");
			
			blueShape.setLocalTranslation(0, -this.length * 0.375f, 0);
			this.attachChild(blueShape);

			Node3D.setMaterial(getMaterial(Color.blue), blueShape);
		}

		if(centerShape == null) {
			centerShape = new UprightCylinder();
			centerShape.setName("center shape");
			this.attachChild(centerShape);
			
			TMaterial cMat = ColorUtil.getMaterial(Color.gray);
			cMat.setShininess(90f/128f);
			Node3D.setMaterial(cMat, centerShape);
		}
		
		redShape.updateGeometry(4, 24, this.radius, this.radius, this.length/4f, true, false);
		blueShape.updateGeometry(4, 24, this.radius, this.radius, this.length/4f, true, false);
		centerShape.updateGeometry(4, 24, this.radius, this.radius, this.length/2f, true, false);

	}

	public void fixColor(double mu) {
		if(mu >= 0) {
			Node3D.setMaterial(getMaterial(Color.red), redShape);
			Node3D.setMaterial(getMaterial(Color.blue), blueShape);
		} else {
			Node3D.setMaterial(getMaterial(Color.blue), blueShape);
			Node3D.setMaterial(getMaterial(Color.blue), blueShape);
		}
	}

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        float len = capsule.readFloat("mdlength", 1f);
        float rad = capsule.readFloat("mdradius", 0.2f);
        updateGeometry(len, rad);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(length, "mdlength", 1f);
        capsule.write(radius, "mdradius", 0.2f);
    }

}
