/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: InclinedPlaneNode.java,v 1.16 2007/07/16 22:04:54 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.Color;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;


/**
 * This node creates the inclined plane used in the InclinedPlane applet.
 */
public class InclinedPlaneNode extends Node3D /*MultiShapeNode*/ {

	protected static int sTexMap[] = { 0 };
    protected final static int [] idxF = {0,3,4,  1,2,5,  0,5,4,  0,1,5,  0,2,3,  0,1,2,  3,2,5,  3,5,4};
    protected final static int [] idxE = {0,1,   1,2,   1,5,   2,5,   4,5,   2,3,    4,3,    3,0,   4,0};
    
    protected final static Point3d [] default_coords = {
    	new Point3d(-0.5,-0.5,-0.5),
    	new Point3d(0.5,-0.5,-0.5),
    	new Point3d(0.5,-0.5,0.5),
    	new Point3d(-0.5,-0.5,0.5),
    	new Point3d(-0.5,0.5,-0.5),
    	new Point3d(0.5,0.5,-0.5)
    };
    Point3d [] coords = null;
    IndexedTriangleArray faces;
    IndexedLineArray edges; 
    
    protected Shape3D fillShape;
    protected Shape3D frameShape;

    public InclinedPlaneNode() {
        this(default_coords);
    }

	public InclinedPlaneNode(Point3d [] data) {
		super();
        if(data == null)
            coords = default_coords;
        else    
            coords = data;
        faces = new IndexedTriangleArray(6, GeometryArray.COORDINATES, 24);
	    faces.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
	    faces.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
	    faces.setCoordinates(0,coords,0,6);
	    faces.setCoordinateIndices(0,idxF);
        edges = new IndexedLineArray(6, GeometryArray.COORDINATES,18);
        edges.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
	    edges.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
        edges.setCoordinates(0,coords,0,6);
	    edges.setCoordinateIndices(0,idxE);
	    fillShape = new Shape3D(faces);
	    frameShape = new Shape3D(edges);
		
		fillShape.setAppearance(Node3D.makeAppearance(new Color3f(Color.GRAY),0.5f,0.75f,false));
		frameShape.setAppearance(Node3D.makeAppearance(new Color3f(Color.BLACK),null,0.f,0.f,false,PolygonAttributes.POLYGON_LINE));

		mContents.addChild(fillShape);
		mContents.addChild(frameShape);
	}
	
    public void updateGeometry(Point3d [] geo)
    {
        coords = geo;
        faces.setCoordinates(0,coords,0,6);
        edges.setCoordinates(0,coords,0,6);
    }
    
	public void setFillAppearance(Appearance app){
		fillShape.setAppearance(app);
	}	
	
	public void setFrameAppearance(Appearance app){
		frameShape.setAppearance(app);
	}	

	public Appearance getFillAppearance(){
		return fillShape.getAppearance();
	}	
	
	public Appearance getFrameAppearance(){
		return frameShape.getAppearance();
	}	

}
