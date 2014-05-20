/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SimWorld.java,v 1.21 2010/08/18 20:45:44 stefan Exp $ 
 * 
 */
package teal.sim.simulation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Action;
import javax.vecmath.Vector3d;

import teal.app.SimGUI;
import teal.config.Teal;
import teal.core.AbstractElement;
import teal.core.HasElementManager;
import teal.core.HasID;
import teal.core.TElement;
import teal.framework.HasFramework;
import teal.framework.MenuElement;
import teal.framework.TAbstractFramework;
import teal.framework.TFramework;
import teal.framework.TGui;
import teal.framework.TealAction;
import teal.render.TAbstractRendered;
import teal.render.j3d.ViewerJ3D;
import teal.render.viewer.SelectListener;
import teal.render.viewer.SelectManager;
import teal.render.viewer.SelectManagerImpl;
import teal.render.viewer.TViewer;
import teal.render.viewer.TViewer3D;
import teal.sim.TSimElement;
import teal.sim.engine.TEngineControl;
import teal.sim.engine.SimEngine;
import teal.sim.engine.EngineControl;
import teal.sim.engine.TSimEngine;
import teal.sim.engine.TSimEngine.EngineType;
import teal.ui.control.ControlGroup;
import teal.util.TDebug;

/**
 * The SimWorld provides management for a simulation with a default 3D viewer and
 * and SimEngine. THis class is the basis for most of the tealsim Simulations.
 *
 * @version $Revision: 1.21 $
 * @author Phil Bailey
 */
public class SimWorld extends Simulation3D {

  private static final long serialVersionUID = 3258126942908789043L;
  protected TSimEngine theEngine;
  protected double damping = Teal.DefaultWorldDumping;
  protected Vector3d gravity = new Vector3d(Teal.G_Vector);

  public SimWorld() {
    super();
    mEngineType = EngineType.KINETIC;


    SelectManager select = new SelectManagerImpl();
    setSelectManager(select);

    TealAction ta = new TealAction(TViewer.RESET_CAMERA, this);
    addAction("View", ta);
  }

  public double getDamping() {
    return damping;
  }

  public void setDamping(double percent) {
    PropertyChangeEvent pce = new PropertyChangeEvent(this, "damping", damping, percent);
    damping = percent;
    firePropertyChange(pce);
  }

  public Vector3d getGravity() {
    return gravity;
  }

  public void setGravity(Vector3d gravityVec) {
    PropertyChangeEvent pce = new PropertyChangeEvent(this, "gravity", gravity, gravityVec);
    gravity = gravityVec;
    firePropertyChange(pce);
  }

  public void setEngine(TSimEngine model) {
    if (theEngine != null && theEngine != model) {
      theEngine.dispose();
      theEngine = null;
    }

    theEngine = model;
//    	if(model instanceof SimEngine){
//    		theEngine = (SimEngine) model;
//    		theEngine.setBoundingArea(boundingArea);
//    		theEngine.setDeltaTime(deltaTime);
//    		theEngine.setDamping(damping);
//    		theEngine.setGravity(gravity);
//    	}
//    	else{
//    		throw new IllegalArgumentException("Wrong engine type in SimWorld");
//    	}
    //if(mViewer != null)
    //	theEngine.setScene(mViewer);
//        if(mSEC != null)
//        	mSEC.setEngine((TSimEngine)theEngine);
    //loadEngine();

  }

  public TSimEngine getEngine() {
    return theEngine;
  }

  public void addSimElement(TSimElement elm) {
    if (theEngine != null)
      theEngine.addSimElement(elm);
  }

  public void removeSimElement(TSimElement elm) {
    if (theEngine != null)
      theEngine.removeSimElement(elm);
  }
}
