/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BoundsSphere.java,v 1.3 2010/07/16 16:57:15 pbailey Exp $ 
 * 
 */

package teal.render;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

/** Interface for the
 */

public class BoundsSphere extends BoundsT{


	private Point3d center;
	private double radius;;
	
	public BoundsSphere(){
		center = new Point3d();
		radius = 1.0;
	}
	
	public BoundsSphere(Point3d center, double radius){
		this.center =center;
		this.radius = radius;
	}
	
	public BoundsSphere(BoundsBox bb){
		center = new Point3d(bb.getCenter());
		radius = bb.getUpper().distance(bb.getLower())/2.;
	}
	public BoundsSphere(BoundsSphere bs){
		center = new Point3d(bs.getCenter());
		radius = bs.getRadius();
		
	}
	
	public BoundsSphere(BoundsT bs){
		if( bs instanceof BoundsSphere ){
			center = new Point3d(((BoundsSphere)bs).getCenter());
			radius = ((BoundsSphere)bs).getRadius();
		}
		else if(bs instanceof BoundsBox){
			center = new Point3d(bs.getCenter());
			radius = ((BoundsBox)bs).getUpper().distance(((BoundsBox)bs).getLower())/2.;
		}
		
	}
	
	public Point3d getCenter(){
		return center;
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
	
	public Object clone(){
		return new BoundsSphere(this.center,this.radius);
	}
	
	public void transform(Matrix4d trans){
	
	}
}
