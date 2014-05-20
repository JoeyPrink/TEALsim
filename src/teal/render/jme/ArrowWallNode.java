/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ArrowWallNode.java,v 1.3 2010/04/12 20:13:18 stefan Exp $ 
 * 
 */

package teal.render.jme;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;

import com.sun.j3d.utils.geometry.Cone;

/**
 * ArrowWallNode is essentially a WallNode with an arrow in the middle of it.  It was designed specifically for use
 * in the Seafloor application.  The arrow scales and rotates independently of the wall; the rotation is set with
 * setArrowRotation().
 */
public class ArrowWallNode extends WallNode {

	TransformGroup arrow = new TransformGroup();
	public ArrowWallNode() {
		super();
		Shape3D line=new Shape3D();
		initShape(line);
		
		Geometry stem  = teal.render.j3d.geometry.Cylinder.makeGeometry(20, 0.05, 1, 0.5).getIndexedGeometryArray(true);
		line.setGeometry(stem);

		Cone fatcone = new Cone(0.2f,0.25f);
		Shape3D cone = new Shape3D();
		initShape(cone);
		cone.setGeometry(fatcone.getShape(Cone.BODY).getGeometry());
		cone.addGeometry(fatcone.getShape(Cone.CAP).getGeometry());
		
		TransformGroup translated_cone=new TransformGroup();
		Transform3D tran = new Transform3D(); 
		tran.set( new javax.vecmath.Vector3f(0.f,1.f,0.f));
		translated_cone.addChild(cone);
		translated_cone.setTransform(tran);

		arrow.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		arrow.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Transform3D tran2 = new Transform3D(); 
		arrow.setTransform(tran2);
		
		TransformGroup arrowOffset = new TransformGroup();
		arrowOffset.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		arrowOffset.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D offset = new Transform3D();
		offset.setTranslation(new javax.vecmath.Vector3d(0f,-0.5f,0f));
		arrowOffset.setTransform(offset);
		arrowOffset.addChild(line);
		arrowOffset.addChild(translated_cone);
		arrow.addChild(arrowOffset);
		
		
		Appearance arrowAppearance = Node3D.makeAppearance(new Color3f(new Color(0,50,250)),0.f,0.f,false);
		line.setAppearance(arrowAppearance);
		cone.setAppearance(arrowAppearance);
		mSwitch.addChild(arrow);
		
		
	}
	
	public void setScale(double s) {
		super.setScale(s);
		Transform3D tran = new Transform3D();
		arrow.getTransform(tran);
		tran.setScale(1./s);
		tran.setScale(0.5);
		arrow.setTransform(tran);
	}
	
	public void setScale(javax.vecmath.Vector3d s) {
		super.setScale(s);
		Transform3D tran = new Transform3D();
		arrow.getTransform(tran);
		arrow.setTransform(tran);
	}
	
	/**
	 * Sets the arrow rotation to the rotation Matrix3d rot.
	 * 
	 * @param rot new rotation Matrix3d.
	 */
	public void setArrowRotation(Matrix3d rot) {
		Transform3D temp = new Transform3D();
		arrow.getTransform(temp);
		
		temp.setRotation(rot);
		
		arrow.setTransform(temp);
	}
	
	public void setPosition(javax.vecmath.Vector3d pos) {
		super.setPosition(pos);
		Transform3D t = new Transform3D();
		arrow.getTransform(t);
		t.setTranslation(pos);
		arrow.setTransform(t);
	}
	
}
