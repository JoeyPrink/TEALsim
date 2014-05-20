/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BoundingBox.java,v 1.4 2010/07/29 23:07:47 stefan Exp $ 
 * 
 */

package teal.render;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/** Interface for the
 */

public class BoundingBox extends Bounds{


	/**
	 * 
	 */
	private static final long serialVersionUID = -3757514686237318332L;
	
	private Point3d upper;
	private Point3d lower;
	
	public BoundingBox(){
		upper = new Point3d(1.,1.,1.);
		lower = new Point3d(-1.,-1.,-1.);
	}
	
	public BoundingBox(BoundingBox bb){
		upper = new Point3d(bb.upper);
		lower = new Point3d(bb.lower);
	}
	public BoundingBox(BoundingSphere bs){
		Point3d center = bs.getCenter();
		double r = bs.getRadius();
		upper = new Point3d(center.x +r, center.y +r, center.z +r );
		lower = new Point3d(center.x -r, center.y -r, center.z -r);
	}
	
	public BoundingBox(Bounds bs){
		if( bs instanceof BoundingSphere ){
			Point3d center = ((BoundingSphere)bs).getCenter();
			double r = ((BoundingSphere)bs).getRadius();
			upper = new Point3d(center.x +r, center.y +r, center.z +r );
			lower = new Point3d(center.x -r, center.y -r, center.z -r);
		}
		else if(bs instanceof BoundingBox){
			upper = new Point3d(((BoundingBox)bs).upper);
			lower = new Point3d(((BoundingBox)bs).lower);
		}
		
	}
	
	public BoundingBox(Point3d lower, Point3d upper){
		this.upper = upper;
		this.lower = lower;
	}
	
/*	public Point3d getCenter(){
		double x = (upper.x - lower.x)/2.;
		double y = (upper.y - lower.y)/2.;
		double z = (upper.z - lower.z)/2.;
		return new Point3d(x,y,z);	
	}
	
	public void setCenter(Point3d center){
		Point3d origin = getCenter();
		origin.sub(center);
		upper.add(origin);
		lower.add(origin);
	}*/
	public Point3d getLower(){
		return lower;
	}
	
	public void setLower(Point3d l){
		lower = l;
	}
	
	public Point3d getUpper(){
		return upper;
	}
	
	public void setUpper(Point3d l){
		upper = l;
	}

	@Override
	public Object clone(){
		return new BoundingBox(this.lower,this.upper);
	}
	
	public void transform(Matrix4d trans){
	}
	
	@Override
	public boolean intersect(Point3d target) {
		assert(lower.x <= upper.x && lower.y <= upper.y && lower.z <= upper.z);
		return (lower.x <= target.x && lower.y <= target.y && lower.z <= target.z &&
				target.x <= upper.x && target.y <= upper.y && target.z <= upper.z);
	}

	@Override
	public double getDiameter() {
		Vector3d tmp = new Vector3d(upper);
		tmp.sub(lower);
		return tmp.length();
	}

	
}
