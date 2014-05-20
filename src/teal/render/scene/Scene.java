/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Scene.java,v 1.7 2010/07/16 21:41:36 stefan Exp $ 
 * 
 */

package teal.render.scene;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.media.j3d.Transform3D;

//import javax.vecmath.Color3f;
//import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.core.AbstractElement;
import teal.render.BoundingBox;
import teal.render.Bounds;
import teal.render.HasBoundingArea;
import teal.render.HasPosition;
import teal.render.HasRotation;
import teal.render.HasTransform;
import teal.render.TDrawable;
import teal.render.TMaterial;
import teal.render.TRenderEngine;
import teal.render.TRenderListener;

import teal.render.TAbstractRendered;
import teal.sim.engine.TSimEngine;
import teal.sim.simulation.SimDrawOrder;
import teal.sim.simulation.TSimulation;
import teal.util.TDebug;


/** 
 * The graphics engine nutral SceneGraph. This is to be used to contain
 * all elements of the scene, currently the view Transform is 
 * not part of the scene this will enable the scene to be
 * rendered into different viewports.
 */

public class Scene extends AbstractElement{ // implements TRenderListener{
	protected TSimulation theSim;
protected Bounds mBounds;
protected TSimEngine mEngine;
//protected Transform3D defaultViewPoint;

 protected ArrayList<Object> dontDraw;;
 protected ArrayList<TLight> lights;
 protected ArrayList<TMaterial> materials;
 protected ArrayList<TNode> tNodes;



// Fog
// Lights
// Background

public Scene()
{
 
  
}


    public Iterator<TLight> getLights(){return lights.iterator();}
    public Iterator<TMaterial> getMaterials(){return materials.iterator();}
    public Iterator<TNode> getTNodes(){return tNodes.iterator();}
    public void add(TNode obj){}
    public void remove(TNode obj){}
  
    public void setFogEnabled(Boolean state){}
    public void setFogColor(Color color){}
    public void setFogTransformBackScale(double value){}
    public void setFogTransformFrontScale(double value){}
    public void setAlternateAppearence(TMaterial app){}
  
    
    //public void setLookAt(Point3d from, Point3d to, Vector3d up){}
 /**
     * The method called once all objects have been updated.
	 * The off screen foreground image is redrawn with the new information.
     * Repaint is then called to put the new data to the screen, after the
	 * repaint is complete <code>renderComplete()</code> is called.
     */
    public synchronized void render() {
		//TDebug.println(2,getID() + " render called");
		render(true);
	}

    public void render(boolean doRepaint){};

  

	/**
	* gets the Graphics2D for this component.
	*/
	//public abstract Graphics2D getGraphics2D();

    /**
	* gets the world coordinates of the viewable area.
	*/
    public Bounds getBoundingArea() {
        return mBounds;
    }
    
    /**
    * Sets the World coordinates displayed within the viewArea, also re-calculates the
    * drawing transform.
    */
    
    public void setBoundingArea(Bounds bb) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this,"boundingArea",
        new BoundingBox(mBounds), new BoundingBox(bb));
        mBounds = bb;
        firePropertyChange(pce);
		TDebug.println(1, getID() +": bounds=" +mBounds);
		//render(true);
    }
    public void clear(){};
	public void initialize()
	{
	}
public void destroy()
	{
 		//simListeners.clear();
		mBounds = null;

	}
	
	
}
