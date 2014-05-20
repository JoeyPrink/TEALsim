/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BoxNode.java,v 1.20 2010/05/29 14:47:55 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.Color;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 * Node for rendering a six-sided box.
 */
public class BoxNode extends Node3D /*MultiShapeNode*/ {

	protected static int sTexMap[] = { 0 };
    
   protected final static Point3d [] coords = { 
           new Point3d(-1, 0, 0.5),new Point3d(0, 0, 0.5),
           new Point3d(0, 0, -0.5),new Point3d(-1, 0, -0.5),
           new Point3d(-1, 1, 0.5),new Point3d(0, 1, 0.5),
           new Point3d(0, 1, -0.5),new Point3d(-1, 1, -0.5)};
                       
	protected final static int [] idxF = 
        { 0,1,2,3, 0,1,5,4, 1,2,6,5, 2,6,7,3, 3,7,4,0, 4,5,6,7};
  
	protected final static int [] idxE = 
        { 0,1, 1,2, 2,3, 3,0, 0,4, 1,5, 2,6, 3,7, 4,5, 5,6, 6,7, 7,4 };


	protected IndexedQuadArray faces;
	protected IndexedLineArray edges;
	protected Shape3D fillShape;
	protected Shape3D frameShape;



	public BoxNode() {
		super();
		fillShape = new Shape3D();
		frameShape = new Shape3D();
        faces = new IndexedQuadArray(8,GeometryArray.COORDINATES,24);
        faces.setCoordinates(0,coords,0,8);
        faces.setCoordinateIndices(0,idxF);
        edges = new IndexedLineArray(8,GeometryArray.COORDINATES,24);
        edges.setCoordinates(0,coords,0,8);
        edges.setCoordinateIndices(0,idxE);
		fillShape.setGeometry(faces);
        frameShape.setGeometry(edges);
        
		Appearance fillAppearance = Node3D.makeAppearance(new Color3f(Color.RED),null,0.5f,0.5f,false,PolygonAttributes.POLYGON_FILL);
		Appearance frameAppearance = Node3D.makeAppearance(new Color3f(Color.BLACK),null,0.f,0.f,false,PolygonAttributes.POLYGON_LINE);
		
		fillShape.setAppearance(fillAppearance);
		frameShape.setAppearance(frameAppearance);
		
		mContents.addChild(fillShape);
		mContents.addChild(frameShape);


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
