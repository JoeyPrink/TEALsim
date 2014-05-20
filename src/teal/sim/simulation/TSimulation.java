/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TSimulation.java,v 1.28 2010/07/16 21:41:38 stefan Exp $ 
 * 
 */

package teal.sim.simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.media.j3d.Transform3D;

import javax.swing.Action;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.core.HasID;
import teal.core.TElement;
import teal.core.TElementManager;
import teal.framework.HasFramework;
import teal.framework.MenuElement;
import teal.render.Bounds;
import teal.render.TAbstractRendered;
import teal.render.TRenderEngine;
import teal.render.TRenderListener;
import teal.render.scene.Scene;
import teal.render.viewer.SelectListener;
import teal.sim.TSimElement;
import teal.sim.engine.HasEngineControl;
import teal.sim.engine.TSimEngine;
import teal.sim.engine.TSimEngine.EngineType;

import teal.ui.HasGUI;

/**
 * The TSimulation provides a interface for the specification and management 
 * of all elements related to a specific simulation. 
 * This could include Actions, GUI and simulated objects, the model may 
 * reside within the simulation. As envisioned a simulation will support 
 * an XML loader/dumper interface and should be able to be basic unit 
 * assigned to a TFramework which supports Simulations.
 *
 * @version $Revision: 1.28 $
 * @author Phil Bailey
 */

public interface TSimulation extends TElement, TElementManager, HasFramework, HasEngineControl,  HasID, ActionListener, HasGUI, TRenderListener , SelectListener
	{
    public void initialize();
    public void setProperty(String element, String property, String value);
    public Collection<?> getGuiElements();
    public Collection<Action> getActions();
    public MenuElement[] getMenuElements();
    public Collection<TSimElement> getSimElements();
    public void dispose();
    public void reset();

    public Bounds getBoundingArea();
    public void setBoundingArea(Bounds bounds);
    
    public double getDamping();
    public void setDamping(double delta);
    public double getDeltaTime();
    public void setDeltaTime(double delta);
    public TSimEngine getEngine();
    public void setEngine(TSimEngine model);
    public EngineType getEngineType();
   
    public Scene getScene();
    public void setScene(Scene scene);
    public void addSimElement(TSimElement elm);
    public void removeSimElement(TSimElement elm);
    public void addRenderEngine(TRenderEngine renderer);
    public void removeRenderEngine(TRenderEngine renderer);
    public Collection<TAbstractRendered> getRenderedElements();
    
    public void setTitle(String title);
    public String getTitle();
    public void addAction(Action ac);
    public void addAction(String section, Action ac);
   
    // Viewer parameters
    public Color getBackgroundColor();
    public void setBackgroundColor(Color color);
    public Dimension getViewerSize();
    public void setViewerSize(Dimension dim);
    public void setViewerSize(int w, int h);
    public int getNavigationMode();
    public void setNavigationMode(int mode);
    public Dimension getViewerMinimumSize();
    public void setViewerMinimumSize(Dimension dim);
    public Vector3d getMouseMoveScale();
    public void setMouseMoveScale(Vector3d vec);
    public void setMouseMoveScale(double x, double y, double z);
    public Transform3D getDefaultViewpoint();
    public void setDefaultViewpoint(Transform3D vpTransform);
    public void setLookAt(Point3d from,Point3d to,Vector3d up);
    
    public Transform3D getViewpoint();
    public void setViewpoint(Transform3D vpTransform);
    
    public Boolean getShowGizmos();
    public void setShowGizmos(Boolean state);
    public Boolean getCursorOnDrag();
    public void setCursorOnDrag(Boolean state);
    public Boolean getRefreshOnDrag();
    public void setRefreshOnDrag(Boolean state);


}
