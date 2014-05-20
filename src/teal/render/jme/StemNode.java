/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: StemNode.java,v 1.8 2010/06/28 23:07:32 stefan Exp $ 
 * 
 */
package teal.render.jme;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

import teal.render.TAbstractRendered;

public class StemNode extends LineNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4561929755934215187L;
		
	protected float radius = 1f; 
	
	public StemNode () {
		super();
	}
	
	public StemNode(TAbstractRendered element){
		super(element);
	}

	@Override
	protected void setDefaultGeometry() {
		UprightCylinder newCyl = new UprightCylinder("stem geometry" ,4 ,
				Node3D.stemSegments, Node3D.stemRadius, Node3D.stemHeight, false);
		newCyl.setLocalTranslation(0, Node3D.stemOffset, 0);
		Node cylHolder = new Node("stem holder");
		cylHolder.attachChild(newCyl);
		this.attachChild(cylHolder);
	}

	@Override
	public void setFromTo(javax.vecmath.Vector3d from, javax.vecmath.Vector3d to) {
		super.setFromTo(from, to);
		float length = this.getLocalScale().y;
		this.setLocalScale(new Vector3f(radius,length,radius));
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
		this.radius = (float)radius;
	}
	
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        radius = capsule.readFloat("sradius", 1f);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(radius, "sradius", 1);
    }
	
}
