/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PickListener.java,v 1.2 2010/03/25 21:00:56 stefan Exp $ 
 * 
 */

package teal.render.jme;

import java.awt.event.MouseEvent;

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
    public javax.vecmath.Vector3d getMouseMoveScale();
    public void setMouseMoveScale(javax.vecmath.Vector3d scale);
    
}    
