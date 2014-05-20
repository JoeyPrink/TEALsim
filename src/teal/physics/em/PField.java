/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PField.java,v 1.6 2010/07/21 21:57:19 stefan Exp $ 
 * 
 */

package teal.physics.em;

import java.util.*;

import javax.vecmath.*;

import teal.core.*;
import teal.field.*;


/** A utility class used in construction if Electric Field Lines
 *
 */
public class PField extends CompositeField
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5436391599873051772L;
	
	/**
     * The list of objects contributing to this CompositeField.
     */
    protected Vector<GeneratesP> objects;
    
	public PField() {
        objects = new Vector<GeneratesP>();
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
	
	public int getType()
	{
			return Field.P_FIELD;
	}
 
 
 public void add(TElement obj)
 throws ClassCastException
 {
 	try
 	{
 		GeneratesP ob = (GeneratesP) obj;
		objects.add(ob);
 	}
	catch(ClassCastException e)
	{
		throw new ClassCastException("ClassCastException to GeneratesP for " + obj.getID());
	}
 }
 

        /** This method gives the "Pauli field" due to all the EM objects together in the collection
         *
         * @param pos Position at which the Pauli field has to be calculated.
         * @return Pauli field of all the EM objects in the collection.
         */
	public Vector3d get(Vector3d pos)
	{
        Vector3d field = new Vector3d();
		
		Iterator<GeneratesP> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesP obj = (GeneratesP) it.next();
			if (obj.isGeneratingP())
			{
				field.add(obj.getP(pos));
			}
		}

	    return field;

	}
	
	public Vector3d get(Vector3d pos,double t)
	{
	    Vector3d field = new Vector3d();
		
		Iterator<GeneratesP> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesP obj = (GeneratesP) it.next();
			if (obj.isGeneratingP())
			{
				field.add(obj.getP(pos,t));
			}
		}

	    return field;

	}

	public Vector3d get(Vector3d pos,Vector3d field)
	{
	    field.set(0.,0.,0.);
		
		Iterator<GeneratesP> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesP obj = (GeneratesP) it.next();
			if (obj.isGeneratingP())
			{
				field.add(obj.getP(pos));
			}
		}

	    return field;

	}
	
	public Vector3d get(Vector3d pos,Vector3d field,double t)
	{
	    field.set(0.,0.,0.);
		
		Iterator<GeneratesP> it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesP obj = (GeneratesP) it.next();
			if (obj.isGeneratingP())
			{
				field.add(obj.getP(pos,t));
			}
		}

	    return field;

	}
	
        /** Similar to the above function but excludes one Electro Magnetic object when calculating the field due to all the objects in the collection.
         *
         * @param xobj The EM object that has to excluded in calculating the
         *     total electric field
         * @param pos Position at which the Pauli field has to be
         *     calculated.
         * @return Pauli field at pos.
         */
	public Vector3d get(Vector3d pos,TElement xobj)
	{
		
        Vector3d field = new Vector3d();
		Iterator it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesP obj = (GeneratesP) it.next();             ;
			if ((obj != xobj) && (obj.isGeneratingP()))
			{
				field.add(obj.getP(pos));
			}
		}

	    return field;

	}
	
	public Vector3d get(Vector3d pos,TElement xobj,double t)
	{
	    Vector3d field = new Vector3d();
		Iterator it = objects.iterator();
		while (it.hasNext())
		{
			GeneratesP obj = (GeneratesP) it.next();             ;
			if ((obj != xobj) && (obj.isGeneratingP()))
			{
				field.add(obj.getP(pos,t));
			}
		}

	    return field;

	}
    
	
	/**
	* A stub for now we must fix this or decide that the 
	* methoud  should not be part of the general interface.
	*/
	public double getFlux(Vector3d pos)
	{
	    return 0.0;
	}
    
	public String toString(){
		return "P Field";
	}
}

