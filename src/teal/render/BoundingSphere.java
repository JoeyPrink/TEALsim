/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BoundingSphere.java,v 1.5 2010/07/29 23:07:47 stefan Exp $ 
 * 
 */

package teal.render;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.jme.scene.Point;

/** Interface for the
 */

public class BoundingSphere extends Bounds{


	/**
	 * 
	 */
	private static final long serialVersionUID = 3772647993303130807L;
	
	private Point3d center;
	private double radius;;
	
	public BoundingSphere(){
		center = new Point3d();
		radius = 1.0;
	}
	
	public BoundingSphere(Point3d center, double radius){
		this.center =center;
		this.radius = radius;
	}
	
	public BoundingSphere(BoundingBox bb){
		center = new Point3d(getBoxCenter(bb));
		radius = bb.getUpper().distance(bb.getLower())/2.;
	}
	public BoundingSphere(BoundingSphere bs){
		center = new Point3d(bs.getCenter());
		radius = bs.getRadius();
		
	}
	
	private static Point3d getBoxCenter(BoundingBox box) {
		Point3d upper = box.getUpper();
		Point3d lower = box.getLower();
		double x = (upper.x - lower.x)/2.;
		double y = (upper.y - lower.y)/2.;
		double z = (upper.z - lower.z)/2.;
		return new Point3d(x,y,z);
	}
	
	public BoundingSphere(Bounds bs){
		if( bs instanceof BoundingSphere ){
			center = new Point3d(((BoundingSphere)bs).getCenter());
			radius = ((BoundingSphere)bs).getRadius();
		}
		else if(bs instanceof BoundingBox){
			Point3d upper = ((BoundingBox)bs).getUpper();
			Point3d lower = ((BoundingBox)bs).getLower();
			radius = upper.distance(lower)/2.;			
			center = getBoxCenter((BoundingBox)bs);
		}
		
	}
	
	public Point3d getCenter(){
		return center;
	}

	public void getCenter(Point3d c) {
		if(c == null)
			c = new Point3d();
		c.set(center);
	}
	
	public void setCenter(Point3d center){
		this.center = center;
	}
	
	public double getRadius(){
		return radius;
	}
	public void setRadius(double rad){
		radius = rad;
	}
	
	@Override
	public Object clone(){
		return new BoundingSphere(this.center,this.radius);
	}
	
	public void transform(Matrix4d trans){
	
	}

	@Override
	public boolean intersect(Point3d target) {
		return (target.distance(center) <= radius);
	}

	@Override
	public double getDiameter() {
		return radius*2;
	}

}
