/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TViewer2D.java,v 1.5 2007/07/16 22:04:58 pbailey Exp $ 
 * 
 */

package teal.render.viewer;

import java.awt.*;
import java.awt.geom.*;

import javax.vecmath.*;

import teal.render.*;

/**
*	Interface Viewer2D :
 *	A viewer2d displays a 2d representation of a 3d world.
 * we project all the objects of the world on a 2d plane,
 * XY will be the plane the most used, but it can be any plane.
 * Therefore we have 3 coordinates system :
 *	the world coordinate system
 *	the viewer coordinate system
 *	the screen coordinate system
 * 	the Affine Transform is used from the viewer to the screen
 * 	the projection from the world to the viewer.
**/

public interface TViewer2D extends TViewer,HasBackgroundImage
{
	public static final int XY_VIEW = 1;
	public static final int YZ_VIEW = 2;
	public static final int XZ_VIEW = 3;
	public static final int CUSTOM_VIEW = 4;
	
	public Graphics2D getGraphics2D();

	public Vector3d project(Vector3d position);
	
	public Vector3d viewerToWorld(Vector3d position);
	
	public AffineTransform getInvertedAffineTransform();
	
	public void setViewerSize( int width, int height);
	
	public Dimension getViewerSize();
	public int getViewPlane();
    //public VectorIterator getGridIterator(int resolution);
	
	

}
