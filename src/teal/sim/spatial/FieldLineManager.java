/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FieldLineManager.java,v 1.21 2010/08/18 20:45:45 stefan Exp $ 
 * 
 */

package teal.sim.spatial;

import java.awt.Color;
import java.util.*;

import javax.vecmath.Vector3d;

import teal.core.HasElementManager;
import teal.core.TElementManager;
import teal.core.TElement;
import teal.sim.TSimElement;
import teal.sim.engine.EngineObj;
import teal.sim.engine.TSimEngine;
import teal.util.TDebug;

/**
 * The FieldLineManger is a utility for managing a Collection of FieldLines.  By adding 
 * the FieldLines to the manager, you can call various functions on all 
 * of them at once. Lines added to the manager are added to the model when the manager 
 * is added to the model. If the manager has been added to the model lines are added to
 * the model as they are added to the manager. Lines added to the model through the 
 * manager should be removed from the manager, setting the managers model to null will 
 * remove all managed lines from the model.
 */
public class FieldLineManager extends EngineObj implements HasElementManager {
	
	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -7935269186318035592L;

    private transient TElementManager mElementManager; 
    /**
	 * This is the ArrayList that holds the FieldLines.
	 */
	private ArrayList<FieldLine> fLines;
	
	/**
	 * Visibility flag for fieldlines.  Note that this flag only reports the group isDrawn as set by the manager, and
	 * otherwise assumes that all the FieldLines are in the same isDrawn state (ie. changes to the isDrawn of
	 * individual fieldlines in the list are obviously not reflected in this flag.  This flag only reports the state
	 * associated with the last setVisibility() call). 
	 */
	int integrationMode = FieldLine.EULER;
	int colorMode = FieldLine.COLOR_VERTEX;
	Color mColor = null;
	double colorScale = 1.0;
	boolean isDrawn = true;
	boolean isReceivingFog = false;
	boolean isSymmetryEnabled = false;
	/**
	 * Variable representing the group symmetryCount.  Like isDrawn, this variable only reflects the value of the
	 * last setSymmetryCount() call, and is otherwise meaningless if the symmetryCount values in individual fieldlines
	 * are changed.
	 */
	int symmetryCount = 2;
	Vector3d symmetryAxis;
	
	public FieldLineManager() {
		super();
		fLines = new ArrayList<FieldLine>();
		symmetryAxis = new Vector3d(0,1,0);
	}
	
	public void setElementManager(TElementManager mgr){
	    mElementManager = mgr;
	    if(mElementManager != null){
	        if(!fLines.isEmpty()){
	            Iterator<? extends FieldLine> it = fLines.iterator();
	            while(it.hasNext()){
	                mElementManager.addElement(it.next(),true);
	            }
	        }
	    }
	}
	
	public TElementManager getElementManager(){
	    return mElementManager;
	}
	    
public void setSimEngine(TSimEngine engine){
	super.setSimEngine(engine);
	if(engine == null)
		return;
	if(!fLines.isEmpty()){
		Iterator<? extends FieldLine> it = fLines.iterator();
        while(it.hasNext()){
            theEngine.addSimElement((TSimElement) it.next());
        }
	}
}
	/**
	 * Adds a single FieldLine to the manager's List.
     * None of the FieldLineManager properties are assigned 
     * to the line as it is added, call individual methods 
     * to set a property on all lines managed.
	 * 
	 * @param fl FieldLine to be added.
	 */
	public void addFieldLine(FieldLine fl) {
        
	    fl.setDrawn(isDrawn);
	    if(isSymmetryEnabled){
	        fl.setSymmetry(symmetryCount,symmetryAxis);
	    }
	    /*
	    if(mColor != null)
	    fl.setColor(mColor);
        */
	    fl.setColorMode(colorMode);
	    fl.setColorScale(colorScale);
        /*
	    fl.setIntegrationMode(integrationMode);
	    fl.setReceivingFog(isReceivingFog);
        */
		fLines.add(fl);
		if(mElementManager != null){
		    mElementManager.addTElement(fl,true);
		}
		if(theEngine != null){
			theEngine.addSimElement(fl);
		}
	}
	
	public void removeFieldLine(FieldLine fl){
	    if(fLines.contains(fl)){
	        fLines.remove(fl);
	        if(mElementManager != null)
	            mElementManager.removeTElement(fl);
	        if(theEngine != null){
				theEngine.removeSimElement(fl);
			}
	    }
	}
	public void removeFieldLines(Collection<? extends FieldLine> lines){
	    Iterator<? extends FieldLine> it = lines.iterator();
	    while(it.hasNext()){
	        removeFieldLine((FieldLine) it.next());
	    }
	}
	/**
	 * Sets the manager's list to this Vector (previously added FieldLines are discarded).
	 *  
	 * @param fls new FieldLine Collection.
	 */
	public void setFieldLines(Collection<? extends FieldLine> fls) {
	    if(!fLines.isEmpty()){
	            removeFieldLines(fLines);
	    }   
		fLines = new ArrayList<FieldLine>();
		addFieldLines(fls);
	}

	/**
	 * Adds a Collection of FieldLines to the manager's List (this list is appended to the existing list).
	 * 
	 * @param fls ArrayList of FieldLines to be appended.
	 */
	public void addFieldLines(Collection<? extends FieldLine> fls) {
	    Iterator<? extends FieldLine> it = fls.iterator();
	    while(it.hasNext()){
	        addFieldLine((FieldLine) it.next());
		}
	}
	
	/**
	 * Returns the FieldLine at this index.
	 * 
	 * @param index index of FieldLine to return.
	 * @return FieldLine at this index.
	 */
	public FieldLine getIndexedFieldLine(int index) {
		return (FieldLine)fLines.get(index);
	}
	
	/**
	 * Returns the manager's entire list as an ArrayList.
	 * 
	 * @return the manager's ArrayList of FieldLines.
	 */
	public ArrayList<FieldLine> getFieldLineArrayList() {
		return fLines;
	}
	
	/**
	 * Returns the manager's entire list as an array.
	 * 
	 * @return the manager's array of FieldLines.
	 */
	public FieldLine[] getFieldLineArray() {
		Object[] fls;
		fls = fLines.toArray();
		FieldLine[] fls2 = new FieldLine[fls.length];
		for (int i = 0; i < fls.length; i++) {
			fls2[i] = (FieldLine)fls[i];
		}
		
		return fls2;
	}
	public void setAll(){
        Iterator<FieldLine> it = fLines.iterator();
        while ( it.hasNext()) {
            FieldLine f = (FieldLine)it.next();
            setAll(f);
        }
    }
	
	public void setAll(FieldLine f){
       
            f.setDrawn(isDrawn);
            f.setIntegrationMode(integrationMode);
            //f.setColor(mColor);
            f.setColorScale(colorScale);
            f.setColorMode(colorMode);
            if(isSymmetryEnabled)
                f.setSymmetry(symmetryCount,symmetryAxis);
            else
                f.setSymmetry(1,new Vector3d(0,1,0));
            f.setReceivingFog(isReceivingFog);
    }
        
	
	public void setReceivingFog(boolean state){
	    isReceivingFog = state;
	    Iterator<FieldLine> it = fLines.iterator();
		while ( it.hasNext()) {
			FieldLine f = (FieldLine)it.next();
			f.setReceivingFog(isReceivingFog);
		}
		if(theEngine != null)
		    theEngine.requestRefresh();
	}
	
	/**
	 * Sets the colorMode of all FieldLines to state.  Note that state is a boolean, even though there are more
	 * than two colorModes for FieldLines.  This method is used only to toggle between COLOR_VERTEX and 
	 * COLOR_VERTEX_FLAT, which are our two most common modes.
	 * 
	 * @param state true (COLOR_VERTEX) or false (COLOR_VERTEX_FLAT).
	 */
	public void setColorMode(boolean state) {

		 setColorMode(colorMode = (state) ? FieldLine.COLOR_VERTEX : FieldLine.COLOR_VERTEX_FLAT);
		
	}
	
	/**
	 * Sets the colorMode of all fieldlines to mode.  See FieldLine for modes.
	 * 
	 * @param mode color mode.
	 */
	public void setColorMode(int mode) {
		if (mode == FieldLine.COLOR_FLAT || mode == FieldLine.COLOR_VERTEX_FLAT) {
		    colorMode = FieldLine.COLOR_VERTEX_FLAT;
        }
        else if(mode == FieldLine.COLOR_VERTEX){
            colorMode = FieldLine.COLOR_VERTEX;
        }
        else{
            TDebug.println(0,"FieldLineManager: Unrecognized color mode");
            return;
        }
            
		Iterator<FieldLine> it = fLines.iterator();
			
		while ( it.hasNext()) {
			FieldLine f = (FieldLine)it.next();
			f.setColorMode(colorMode);
        }
		if(theEngine != null){
			    theEngine.requestRefresh();	
		}
	}
	
	/**
	 * Sets the isDrawn state of all FieldLines.
	 * 
	 * @param visible
	 */
	public void setDrawn(boolean visible) {
		isDrawn = visible;
		Iterator<FieldLine> it = fLines.iterator();
		
		while ( it.hasNext()) {
			FieldLine f = (FieldLine)it.next();
			f.setDrawn(isDrawn);
		}
		if(theEngine != null)
		    theEngine.requestRefresh();
	}
	
	/**
	 * Gets the manager's FieldLine isDrawn state.  See isDrawn for details.
	 * 
	 * @return isDrawn state.
	 */
	public boolean isDrawn() {
		return isDrawn;
	}
	
	/**
	 * Sets the symmetryCount of all FieldLines.
	 * 
	 * @param count symmetry count.
	 */
	public void setSymmetryCount(int count) {
		symmetryCount = count;
		Iterator<FieldLine> it = fLines.iterator();
		
		while ( it.hasNext()) {
			FieldLine f = (FieldLine)it.next();
			f.setSymmetryCount(symmetryCount);
		}
		if(theEngine != null)
		    theEngine.requestRefresh();
	}
	
	/**
	 * Gets the manager's FieldLine symmetryCount state.  See symmetryCount for details.
	 * 
	 * @return symmetry count.
	 */
	public int getSymmetryCount() {
		return symmetryCount;
	}
	
	/**
	 * Sets the symmetry axis for all FieldLines.
	 * 
	 * @param axis new symmetry axis.
	 */
	public void setSymmetryAxis(Vector3d axis) {
	    symmetryAxis = axis;
		Iterator<FieldLine> it = fLines.iterator();
		
		while ( it.hasNext()) {
			FieldLine f = (FieldLine)it.next();
			f.setSymmetryAxis(symmetryAxis);
		}
		if(theEngine != null)
		theEngine.requestRefresh();
	}
	
	/**
	 * Sets the color of all FieldLines.
	 * 
	 * @param color new Color.
	 */
	public void setColor(Color color) {
	    mColor = color;
		Iterator<FieldLine> it = fLines.iterator();
		
		while ( it.hasNext()) {
			FieldLine f = (FieldLine)it.next();
			f.setColor(mColor);
		}
		if(theEngine != null)
		theEngine.requestRefresh();
	}
	
	/**
	 * Sets the color scale of all FieldLines.
	 * 
	 * @param scale new colorScale.
	 */
	public void setColorScale(double scale) {
	    colorScale = scale;
		Iterator<FieldLine> it = fLines.iterator();
		
		while ( it.hasNext()) {
			FieldLine f = (FieldLine)it.next();
			f.setColorScale(colorScale);
		}
		if(theEngine != null)
		    theEngine.requestRefresh();
	}
	
	/**
	 * Sets integration mode of all FieldLines.
	 * 
	 * @param mode new integration mode.
	 */
	public void setIntegrationMode(int mode) {
	    integrationMode = mode;
		Iterator<FieldLine> it = fLines.iterator();
		
		while ( it.hasNext()) {
			FieldLine f = (FieldLine)it.next();
			f.setIntegrationMode(integrationMode);
		}
		if(theEngine != null)
		    theEngine.requestSpatial();
	}
}
