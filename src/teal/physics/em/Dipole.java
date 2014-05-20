/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Dipole.java,v 1.24 2010/07/16 21:41:33 stefan Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.*;

import teal.config.*;
import teal.render.*;
import teal.sim.properties.HasLength;
import teal.sim.properties.HasRadius;
import teal.util.*;

/** Represents a Three Dimension Dipole in Cartesian Coordinates.
 */
public abstract class Dipole extends EMObject 
    implements HasRadius,HasLength,GeneratesB,GeneratesE,GeneratesP
{
    protected boolean generatingBField = false;
    protected boolean generatingEField = false;
    protected boolean generatingPField = true;
    protected boolean generatingEPotential = true;;
    protected double radius;
    protected double length;
	protected double epsilon = 1.0;
	
  

 /** Default Constructor
  */
 public Dipole()
 {
 	super();	
	setMomentOfInertia(1);
	setPickable(true);

 }
 /**
    Stub must be replaced 
    */
 public double getEFlux(Vector3d pos){
    return 0.;
 }
    public String toString()
    {
	    return "Dipole " + id;
    }
	

	public abstract Vector3d getDipoleMoment();
	

	protected abstract void updateNodeColor();
	protected abstract void updateNodeGeometry();
	
	public void render() {
		if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {
			updateNodeGeometry();
			renderFlags ^= GEOMETRY_CHANGE;
		}
		if ((renderFlags & COLOR_CHANGE) == COLOR_CHANGE) {
			updateNodeColor();
			renderFlags ^= GEOMETRY_CHANGE;
		}
		super.render();
	}

	public double getLength()
	{
		return length;
	}
	public void setLength(double h)
	{
		TDebug.println(1,"Setting length");
		try
		{
		length = h;
        bounds = null;
        renderFlags |= GEOMETRY_CHANGE;
		
		if (theEngine != null)
		{
			theEngine.requestSpatial();
			theEngine.requestRefresh();
			TDebug.println(1,"refresh called");
		}
		}
		catch(Exception e)
		{
			TDebug.printThrown(0,e);
		}
	}
	
	public double getRadius()
	{
	return radius;
	}
	public void setRadius(double w)
	{
	    radius = w;
	    bounds = null;
        renderFlags |= GEOMETRY_CHANGE; 
	    
	    if(theEngine != null)
		    theEngine.requestRefresh();
	}
    
	protected void createBounds()
    {
        bounds = new BoundingBox(new Point3d(-radius,-length/2.0,-radius),new Point3d(radius,length/2.0,radius));
    }
 



   public void setGeneratingB(boolean b) {
    generatingBField = b;
	if(theEngine != null)
		theEngine.requestSpatial();
  }

  public boolean isGeneratingB() {
    return generatingBField;
  }
   public void setGeneratingE(boolean b) {
    generatingEField = b;
	if(theEngine != null)
		theEngine.requestSpatial();
  }

  public boolean isGeneratingE() {
    return generatingEField;
  }
     public void setGeneratingP(boolean b) {
    generatingPField = b;
	if(theEngine != null)
		theEngine.requestSpatial();
  }

  public boolean isGeneratingP() {
    return generatingPField;
  }
public void setGeneratingEPotential(boolean b) {
		generatingEPotential = b;
		if(theEngine != null)
		theEngine.requestSpatial();
	}  
	public boolean isGeneratingEPotential(){
		return generatingEPotential;
	}

  	/** this just calls getE(pos) need to fix it. */
	 public Vector3d getE( Vector3d pos,double t )
	 {
	 	return getE(pos);
	 }
  
    
	/** this just calls getB(pos) need to fix it. */
	 public Vector3d getB( Vector3d pos,double t )
	 {
	 	return getB(pos);
	 }
     
	 public Vector3d getP( Vector3d pos,double t )
	 {
	 	return getP(pos);
	 }
 
	public Vector3d getP(Vector3d pos) {
		Vector3d r = new Vector3d();
		double E=getE(pos).length();
		r.sub(pos,position_d);
		double rlength = r.length();
		double pauliDistance = 2*radius;
		r.normalize();
		r.scale(Teal.PauliConstant*E*(Math.pow(rlength/pauliDistance,
						       Teal.PauliPower-3)));
		return r;
	}
	

    /** Calculates the gradient of the E field at a point.
      *
      * @param pos position at which the gradient has to be calculated
      * @return the gradient of the efield.
      */
     public Matrix3d getGradientEField(Vector3d pos)
     {
         Matrix3d m = new Matrix3d();
         Vector3d fieldTest;
		 Vector3d field;
		   	
     	 field = getE(pos);
         fieldTest = getE(new Vector3d(pos.x + epsilon,pos.y,pos.z));
         m.m00 = m.m10 = m.m20 = (fieldTest.x - field.x )/epsilon;
         fieldTest = getE(new Vector3d(pos.x,pos.y + epsilon,pos.z));
         m.m01 = m.m11 = m.m21 = (fieldTest.y - field.y )/epsilon;
         fieldTest = getE(new Vector3d(pos.x,pos.y,pos.z + epsilon));
         m.m02 = m.m12 = m.m22 = (fieldTest.z - field.z )/epsilon;
     	
      	return m;
     }
     
     

    /** Creates a new Matrix3d with all values = zero
     *
     * @param pos
     */
    public Matrix3d getGradientBField(Vector3d pos)
      {

      return new Matrix3d();
      }
 

}

