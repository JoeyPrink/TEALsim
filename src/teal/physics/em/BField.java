/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BField.java,v 1.6 2010/07/21 21:57:18 stefan Exp $ 
 * 
 */

package teal.physics.em;

import java.util.*;

import javax.vecmath.*;

import teal.core.*;
import teal.field.*;
import teal.physics.em.*;

/** This class is a magnetic field implementation of CompositeField.
 *
 */
public class BField extends CompositeField
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5154736978272771364L;

	/**
     * The list of objects contributing to this CompositeField.
     */
    protected Vector<GeneratesB> objects;
    
	public BField() {
        objects = new Vector<GeneratesB>();
    }
    
    /**
     * Returns the objects contributing to this CompositeField.
     * 
     * @return	a Vector of objects.
     */
    public Vector<?> getObjects() {
        return objects;
    }
    
	  /**
     * Removes the indicated object from the CompositeField.
     * 
     * @param obj Object to remove from the CompositeField.
     */
    public void remove(TElement obj) {
        objects.remove(obj);
    }
	

	public int getType(){
		return Field.B_FIELD;
	}

	public void add(TElement obj)
		throws ClassCastException
	{
		try
		{
			GeneratesB ob = (GeneratesB) obj;
			objects.add(ob);
		}
		catch(ClassCastException e)
		{
			throw new ClassCastException("ClassCastException to GeneratesB for " + obj.getID());
		}
	}
	

	public Vector3d get(Vector3d pos,Vector3d field)
	{
		field.set(0.,0.,0.);
		
		Iterator<GeneratesB> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesB obj = (GeneratesB) it.next();
			if (obj.isGeneratingB())
			{
				field.add(obj.getB(pos));
			}
		}
		
	    return field;
		
	}
	
	public Vector3d get(Vector3d pos,Vector3d field, double t)
	{
		field.set(0.,0.,0.);
		
		Iterator<GeneratesB> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesB obj = (GeneratesB) it.next();
			if (obj.isGeneratingB())
			{
				field.add(obj.getB(pos,t));
			}
		}
	    return field;
		
	}
	
	/** This method gives the magnetic field due to all the EM objects together in the collection
	 *
	 * @param pos Position at which the efield has to be calculated.
	 * @return Electic Field of all the EM objects in the collection.
	 */
	public Vector3d get(Vector3d pos)
	{
		Vector3d field = new Vector3d();
		
		Iterator<GeneratesB> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesB obj = (GeneratesB) it.next();
			if (obj.isGeneratingB())
			{
				field.add(obj.getB(pos));
			}
		}
		
	    return field;
		
	}
	
	
	/** This method gives the magnetic flux due to all the EM objects together in the collection
	 *
	 * @param pos Position at which the flux has to be calculated.
	 * @return magnetic flux of all the EM objects in the collection.
	 */
	public double getFlux(Vector3d pos)
	{
		double flux = 0.;
		
		Iterator<GeneratesB> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesB obj = (GeneratesB) it.next();
			if (obj.isGeneratingB())
			{
				flux += (obj.getBFlux(pos));
			}
		}
		
	    return flux;
		
	}
	
	public Vector3d get(Vector3d pos,double t)
	{
		Vector3d field = new Vector3d();
		
		Iterator<GeneratesB> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesB obj = (GeneratesB) it.next();
			if (obj.isGeneratingB())
			{
				field.add(obj.getB(pos,t));
			}
		}
	    return field;
		
	}
	/** Similar to the above function but excludes one electromagnetic object when calculating the field due to all the objects in the collection.
	 *
	 * @param xobj The EM object that has to excluded in calculating the
	 *     total magnetic field
	 * @param pos Position at which the electric field has to be
	 *     calculated.
	 * @return magnetic field at pos.
	 */
	public Vector3d get(Vector3d pos,TElement xobj)
	{
		Vector3d field = new Vector3d();
		Iterator<GeneratesB> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesB obj = (GeneratesB) it.next();             ;
			if ((obj != xobj) && (obj.isGeneratingB()))
			{
				field.add(obj.getB(pos));
			}
		}
		
	    return field;
		
	}
	public Vector3d get(Vector3d pos,TElement xobj,double t)
	{
	    Vector3d field = new Vector3d();
		Iterator<GeneratesB> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesB obj = (GeneratesB) it.next();
			if ((obj != xobj) && (obj.isGeneratingB()))
			{
				field.add(obj.getB(pos,t));
			}
		}
		
	    return field;
		
	}
	public String toString(){
		return "B Field";
	}
}




