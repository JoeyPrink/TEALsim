/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ShapeNode3D.java,v 1.18 2007/07/16 22:04:55 pbailey Exp $ 
 * 
 */

package teal.render.j3d;


import javax.media.j3d.*;

import teal.render.HasColor;

/**
 * provides SceneGraphNode for the management of a single Element
 * which contains at most one Shape3D and provides access to the shape's
 * methods. May want to support multiple shapes, but use multiple geometries
 * for now.
 *
 * @author Phil Bailey
 * @version $Revision: 1.18 $
 *
 **/

public interface ShapeNode3D extends HasColor
{
	public void setShape3D(Shape3D shape);
	public Shape3D getShape3D();
	public void setAppearance(Appearance app);
	public Appearance getAppearance();
    public void addGeometry(Geometry geo);
    public void setGeometry(Geometry geo,int idx);
    public void setGeometry(Geometry geo);
}


