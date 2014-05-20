/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ElectricDipoleNode3D.java,v 1.8 2010/07/06 19:23:44 stefan Exp $ 
 * 
 */

package teal.render.jme;

import java.io.IOException;

import javax.vecmath.Color3f;

import teal.config.Teal;
import teal.render.TMaterial;
import teal.render.TealMaterial;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

public class ElectricDipoleNode3D extends Node3D {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6041711604622573404L;
	
	private transient Sphere redShape;
	private transient Sphere blueShape;
	private transient TMaterial redMaterial;
	private transient TMaterial blueMaterial;
	
	private float length = 1f;
	private float radius = 0.2f;
	private transient boolean isPositive = false;
	
	public ElectricDipoleNode3D() {
		redShape = new Sphere("red Shape", 24,24, radius);
		redShape.setLocalTranslation(0, length/4, 0);
		redShape.setLocalRotation(new Quaternion().fromAngleNormalAxis(FastMath.PI/2f,new Vector3f(0,0,1)));

		blueShape = new Sphere("blue Shape", 24,24, radius);
		blueShape.setLocalTranslation(0, -length/4, 0);
		blueShape.setLocalRotation(new Quaternion().fromAngleNormalAxis(-FastMath.PI/2f,new Vector3f(0,0,1)));
		
		redMaterial = new TealMaterial();
		redMaterial.setAmbient(new Color3f(Teal.PointChargePositiveColor));
		redMaterial.setEmissive(new Color3f()); //FIXXME: keep synchronized with j3d version
		redMaterial.setDiffuse(new Color3f(Teal.PointChargePositiveColor));
		redMaterial.setSpecular(new Color3f(1,1,1));
		redMaterial.setShininess(0.5f);

		blueMaterial = new TealMaterial();
		blueMaterial.setAmbient(new Color3f(Teal.PointChargeNegativeColor));
		blueMaterial.setEmissive(new Color3f()); //FIXXME: keep synchronized with j3d version
		blueMaterial.setDiffuse(new Color3f(Teal.PointChargeNegativeColor));
		blueMaterial.setSpecular(new Color3f(1,1,1));
		blueMaterial.setShininess(0.5f);
		
		//putting it all together
		Node3D.setMaterial(blueMaterial, blueShape);
		Node3D.setMaterial(redMaterial, redShape);

		this.attachChild(blueShape);
		this.attachChild(redShape);
		
	}
	
	
	public void updateGeometry(float length, float radius) {
		if(this.radius != radius) {
			this.radius = radius;
			redShape.updateGeometry(new Vector3f(), 24, 24, radius);
			blueShape.updateGeometry(new Vector3f(), 24, 24, radius);
		}

		if(this.length != length) {		
			this.length = length;
			redShape.setLocalTranslation(0, length/4, 0);
			blueShape.setLocalTranslation(0, -length/4, 0);
		}
//		fixColor(1);
	}
	
	public void fixColor(double mu) {
	    if((mu >= 0.0) != isPositive){
	    	isPositive = (mu >= 0.0);
	    	if(isPositive) {
	    		Node3D.setMaterial(blueMaterial, blueShape);
	    		Node3D.setMaterial(redMaterial, redShape);
	    	} else {
	    		Node3D.setMaterial(blueMaterial, redShape);
	    		Node3D.setMaterial(redMaterial, blueShape);			
	    	}
	    }
	}

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        float len = capsule.readFloat("edlength", 1f);
        float rad = capsule.readFloat("edradius", 0.2f);
        isPositive = capsule.readBoolean("edisPositive", false);
        updateGeometry(len, rad);
//        fixColor(isPositive?1:-1);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(length, "edlength", 1f);
        capsule.write(radius, "edradius", 0.2f);
        capsule.write(isPositive, "edisPositive", false);
    }
	
}
