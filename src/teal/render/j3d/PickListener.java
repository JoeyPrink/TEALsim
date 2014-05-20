/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PickListener.java,v 1.12 2007/07/16 22:04:55 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.event.MouseEvent;

import javax.vecmath.Vector3d;

import teal.render.viewer.*;
 /**
 * @deprecated
 */
public interface PickListener extends teal.render.TransformChangeListener {
    public void setViewer(TViewer viewer);
    public TViewer getViewer();
    public void setSelectManager(SelectManager manager);
    //public void setPickCanvas(PickCanvas pCanvas);
    // public PickCanvas getPickCanvas();
    public void mousePressed(MouseEvent me);
    public Vector3d getMouseMoveScale();
    public void setMouseMoveScale(Vector3d scale);
    
}    
