/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SphereNode.java,v 1.15 2010/04/12 20:13:17 stefan Exp $ 
 * 
 */

package teal.render.j3d;

import javax.media.j3d.*;

import teal.render.*;
import teal.render.j3d.geometry.Sphere;

/**
 * Node for rendering a sphere primitive.
 */
public class SphereNode extends ShapeNode
{
    private static Geometry sSphere;
    private static Geometry sSphereLow;
    
    static
    {
        //sSphere = Sphere.makeGeometry(1.0).getIndexedGeometryArray();
    	com.sun.j3d.utils.geometry.Sphere s = new com.sun.j3d.utils.geometry.Sphere(1.0f);
    	sSphere = s.getShape().getGeometry();
    	
    	sSphereLow = Sphere.makeGeometry(3, 1.f).getIndexedGeometryArray();
    }

    public SphereNode()
    {
       super();
        setRotable(false);
    	setGeometry(sSphere);
        //setGeometry(Sphere.makeGeometry(24, 1.f).getIndexedGeometryArray(true));
    }
    public SphereNode(double radius,int divisions)
    {
        super();
        setRotable(false);
    	setGeometry(Sphere.makeGeometry(divisions, radius).getIndexedGeometryArray(true));
    }
    
    public SphereNode(TAbstractRendered element)
    {
        this();
        setElement(element);
        
    }
    
    public void setLowDetailGeometry() {
    	setGeometry(sSphereLow);
    }
    
    public void setHighDetailGeometry() {
    	setGeometry(sSphere);
    }
}