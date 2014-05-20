/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BoundsBox.java,v 1.3 2010/07/16 16:57:15 pbailey Exp $ 
 * 
 */

package teal.render;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

/** Interface for the
 */

public class BoundsBox extends BoundsT{


	private Point3d upper;
	private Point3d lower;
	
	public BoundsBox(){
		upper = new Point3d(1.,1.,1.);
		lower = new Point3d(-1.,-1.,-1.);
	}
	
	public BoundsBox(BoundsBox bb){
		upper = new Point3d(bb.upper);
		lower = new Point3d(bb.lower);
	}
	public BoundsBox(BoundsSphere bs){
		Point3d center = bs.getCenter();
		double r = bs.getRadius();
		upper = new Point3d(center.x +r, center.y +r, center.z +r );
		lower = new Point3d(center.x -r, center.y -r, center.z -r);
	}
	
	public BoundsBox(BoundsT bs){
		if( bs instanceof BoundsSphere ){
			Point3d center = bs.getCenter();
			double r = ((BoundsSphere)bs).getRadius();
			upper = new Point3d(center.x +r, center.y +r, center.z +r );
			lower = new Point3d(center.x -r, center.y -r, center.z -r);
		}
		else if(bs instanceof BoundsBox){
			upper = new Point3d(((BoundsBox)bs).upper);
			lower = new Point3d(((BoundsBox)bs).lower);
		}
		
	}
	
	public BoundsBox(Point3d lower, Point3d upper){
		this.upper = upper;
		this.lower = lower;
	}
	
	public Point3d getCenter(){
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
	}
	public Point3d getLower(){
		return lower;
	}
	
	public void setLower(Point3d l){
		lower = l;
	}
	
	public Point3d getUpper(){
		return upper;
	}
	
	public void setUpperr(Point3d l){
		upper = l;
	}
	public Object clone(){
		return new BoundsBox(this.lower,this.upper);
	}
	
	public void transform(Matrix4d trans){
	
	}
	
	
}
