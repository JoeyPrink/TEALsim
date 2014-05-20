/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: EField.java,v 1.5 2010/07/21 21:57:19 stefan Exp $ 
 * 
 */

package teal.physics.em;

import java.util.*;

import javax.vecmath.*;

import teal.core.*;
import teal.field.*;
import teal.util.*;

/** This class is an electric field implementation of CompositeField.
 *
 */
public class EField extends CompositeField
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8854859831476963177L;
	
	/**
     * The list of objects contributing to this CompositeField.
     */
    protected Vector<GeneratesE> objects;
    
	public EField() {
        objects = new Vector<GeneratesE>();
    }
    
    /**
     * Returns the objects contributing to this CompositeField.
     * 
     * @return	a Vector of objects.
     */
    public Vector<?> getObjects() {
        return objects;
    }
	
	public int getType()
	{
		return Field.E_FIELD;
	}

	
	public void add(TElement obj)
		throws ClassCastException
	{
		try
		{
			GeneratesE ob = (GeneratesE) obj;
			objects.add(ob);
		}
		catch(ClassCastException e)
		{
			TDebug.println(0,"ClassCastException to GeneratesE for " + obj.getID());
			throw new ClassCastException("ClassCastException to GeneratesE for " + obj.getID());
		}
	}
	
	  /**
     * Removes the indicated object from the CompositeField.
     * 
     * @param obj Object to remove from the CompositeField.
     */
    public void remove(TElement obj) {
        objects.remove(obj);
    }
	
	/** This method gets the electric field due to all the GeneratesE objects together in the collection and puts the
	 * result in data.
	 *
	 * @param pos Position at which the efield has to be calculated.
	 * @param data Vector3d used to hold the value of the field.
	 * @return data.
	 */
	public Vector3d get(Vector3d pos,Vector3d data)
	{
		data.set(0.,0.,0.);
		Iterator<GeneratesE> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesE obj = (GeneratesE) it.next();
			if (obj.isGeneratingE())
			{
				data.add(obj.getE(pos));
			}
		}
		
	    return data;
		
	}
	
	
	/** This method gives the electric eield due to all the GeneratesE objects together in the collection
	 *
	 * @param pos Position at which the efield has to be calculated.
	 * @return Electic Field of all the GeneratesE objects in the collection.
	 */
	public Vector3d get(Vector3d pos)
	{
		Vector3d field = new Vector3d();
		
		Iterator<GeneratesE> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesE obj = (GeneratesE) it.next();
			if (obj.isGeneratingE())
			{
				field.add(obj.getE(pos));
			}
		}
		
	    return field;
		
	}

	public Vector3d get(Vector3d pos,Vector3d data, double t)
	{
	    data.set(0.,0.,0.);
		
		Iterator<GeneratesE> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesE obj = (GeneratesE) it.next();
			if (obj.isGeneratingE())
			{
				data.add(obj.getE(pos,t));
			}
		}
		
	    return data;
		
	}
	
	public Vector3d get(Vector3d pos, double t)
	{
	    Vector3d data = new Vector3d();
		
		Iterator<GeneratesE> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesE obj = (GeneratesE) it.next();
			if (obj.isGeneratingE())
			{
				data.add(obj.getE(pos,t));
			}
		}
		
	    return data;
		
	}
	
	/** Similar to the above function but excludes one Electro Magnetic object when calculating the field due to all the objects in the collection.
	 *
	 * @param xobj The EM object that has to excluded in calculating the
	 *     total electric field
	 * @param pos Position at which the electric field has to be
	 *     calculated.
	 * @return electric field at pos.
	 */
	public Vector3d get(Vector3d pos,TElement xobj)
	{
		Vector3d field = new Vector3d();
		Iterator<GeneratesE> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesE obj = (GeneratesE) it.next();             ;
			if ((obj != xobj) && (obj.isGeneratingE()))
			{
				field.add(obj.getE(pos));
			}
		}
		
	    return field;
		
	}
	
	/* (non-Javadoc)
	 * @see teal.field.CompositeField#get(javax.vecmath.Vector3d, teal.core.TElement, double)
	 */
	public Vector3d get(Vector3d pos,TElement xobj,double t)
	{
	    Vector3d field = new Vector3d();
		Iterator<GeneratesE> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesE obj = (GeneratesE) it.next();             ;
			if ((obj != xobj) && (obj.isGeneratingE()))
			{
				field.add(obj.getE(pos,t));
			}
		}
		
	    return field;
		
	}
    
    
	
	/** This method gives the flux due to all the EM objects together in the collection
	 *
	 * @param pos Position at which the flux has to be calculated.
	 * @return electric flux of all the EM objects in the collection.
	 */
	public double getFlux(Vector3d pos)
	{
		double flux = 0.;
		
		Iterator<GeneratesE> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesE obj = (GeneratesE) it.next();
			if (obj.isGeneratingE())
			{
				flux += obj.getEFlux(pos);
			}
		}
		
	    return flux;
		
	}

	public double getPotential(Vector3d pos) {
		double potential = 0.;
		Iterator<GeneratesE> it = objects.iterator();
		while (it.hasNext()) {
			GeneratesE obj = (GeneratesE) it.next();
			if (obj.isGeneratingE()) {
				potential += obj.getEPotential(pos);
			}
		}
		return potential;
	}
 
	public String toString(){
		return "E Field";
	}
}







