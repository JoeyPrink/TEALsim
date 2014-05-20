/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CylindricalField.java,v 1.5 2007/12/04 20:59:13 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.*;

import teal.core.*;
import teal.sim.*;
import teal.sim.engine.HasSimEngine;
import teal.sim.engine.TSimEngine;

/**
 * Generates a constant field (maybe deprecated by ConstantField?)
 */
public class CylindricalField extends AbstractElement implements TSimElement, GeneratesE, HasSimEngine  {

    private static final long serialVersionUID = 3257282526568396082L;
    
    protected TSimEngine theModel;
	protected Vector3d zdir;
	protected Vector3d xdir;
	protected Vector3d position;
	protected double magnitude;
	protected double radius;
	protected double height;
	protected boolean generatingEField= true;
	
	public CylindricalField(Vector3d pos, Vector3d dir, double mag)
	{
		position = pos;
		dir.normalize();
		zdir = dir;
		magnitude = mag;
		
		Vector3d temp = new Vector3d(0,0,0);
		temp.cross(zdir, new Vector3d(0,0,1.0));
		
		if (temp.length() != 0.0)
		{
			temp.normalize();
			xdir = temp;
		}
		else
		{
			xdir = new Vector3d(1.,0,0);
		}
		
	}	
	
	public TSimEngine getSimEngine()
	{
		return theModel;
	}
	
	public void setSimEngine(TSimEngine model)
	{
		theModel = model;
	}
	
	public double getMagnitude()
	{
		return magnitude;
	}
	
	public void setMagnitude(double newmag)
	{
		Double old = new Double(magnitude);
		magnitude = newmag;
		if (theModel != null)
		{
			theModel.requestSpatial();
		}
		firePropertyChange("magnitude",old, new Double(magnitude));
	}
	
	
	
	
	public Vector3d getE(Vector3d pos)
	{
		Vector3d efield = new Vector3d();
		efield.scale(magnitude,zdir);
		return efield;
	}
	
	public Vector3d getE(Vector3d pos, double t)
	{
		return getE(pos);
	}
	
	public double getEFlux(Vector3d pos)
	{
		double flux = 0;
		Vector3d r = new Vector3d();
		r.sub(pos, position);
		
		double rlength = r.length();
		double RdotZ = r.dot(zdir);
		double dd = rlength*rlength - RdotZ*RdotZ;
		
		flux = Math.PI * magnitude * dd;
		return flux;
	}
		
	public double getEPotential(Vector3d pos) {
		return 0;
	}
	public boolean isGeneratingE()
	{
		return generatingEField;
	}
	
	
	

}
