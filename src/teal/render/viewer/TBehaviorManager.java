/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TBehaviorManager.java,v 1.6 2007/07/16 22:04:58 pbailey Exp $ 
 * 
 */

package teal.render.viewer;

import java.awt.event.*;

import javax.vecmath.*;

import teal.render.*;

/**
 * TBehaviorManager
 *  
 * @version  $Revision: 1.6 $
 **/

/**
 * An interface to the manager for all behavior classes resposible for processing
 * all viewer mouse events. It should be 
 * possible to provide default handlers for each of the types of behavior 
 * processing that needs to be done, or to be able to specify handlers 
 * for Pick, Translate, Rotate & Zoom.
 *
 */

public interface TBehaviorManager extends teal.render.TransformChangeListener  
		
{
    /** 
    * Set this flag if you want to invert the inputs.  This is useful when
    * the transform for the view platform is being changed instead of the 
    * transform for the object.
    */
    public static final int INVERT_INPUT = 0x1;

    //public void addToScene(BranchGroup scene);
    
    //public void setPickCanvas(PickCanvas pCanvas);
    // public PickCanvas getPickCanvas();
    public void mousePressed(MouseEvent me);
    public Vector3d getMouseMoveScale();
    public void setMouseMoveScale(Vector3d scale);

    public void setRefreshOnDrag(boolean state);
    public boolean getRefreshOnDrag();

    public void setCursorOnDrag(boolean state);
    public boolean getCursorOnDrag();

    public SelectManager getSelectManager(); 
    public void setSelectManager(SelectManager sm);

    public void setViewer(TViewer viewer);
    public TViewer getViewer();

    //public void setPickCanvas(PickCanvas canvas);
    //public PickCanvas getPickCanvas();
    //public void setPickListener(PickListener pick);
    //public PickListener getPickListener();
    public void setPicking(boolean enable); 
    //public void setSchedulingBounds(Bounds bounds);
    public int getNavigationMode();
    public void setNavigationMode(int flag); 


    
    public Vector3d getVpTranslateScale();
    public void setVpTranslateScale(Vector3d vec);
    /**
    * Return the x-axis movement multipler.
    **/
    public double getXScale();
    /**
    * Return the y-axis movement multipler.
    **/
    public double getYScale();
    /**
    * Set the x-axis amd y-axis movement multipler with factor.
    **/
    public void setScale( double factor);

    /**
    * Set the x-axis amd y-axis movement multipler with xFactor and yFactor
    * respectively.
    **/
    public void setScale( double xFactor, double yFactor);

    /**
    * Return the y-axis movement multipler.
    **/
    public double getZScale();
    /**
    * Set the y-axis movement multipler with factor.
    **/
    public void setZScale( double factor);
    public TransformChangeListener getRotationListener();
    /**
    * The transformChanged method in the callback class will
    * be called every time the transform is updated
    */
    public void setRotationListener( TransformChangeListener callback ); 


    public TransformChangeListener getTranslationListener();
    /**
    * The transformChanged method in the callback class will
    * be called every time the transform is updated
    */
    public void setTranslationListener( TransformChangeListener callback ); 
    public TransformChangeListener getZoomListener();
    /**
    * The transformChanged method in the callback class will
    * be called every time the transform is updated
    */
    public void setZoomListener( TransformChangeListener callback );



}







