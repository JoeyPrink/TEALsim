/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WallNode.java,v 1.12 2010/04/09 17:00:07 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.Color;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.render.HasColor;


/**
 * Node for rendering a Wall, which is just a rectangular plane.
 */
public class WallNode extends Node3D implements HasColor /*MultiShapeNode*/ {

	protected static int sTexMap[] = { 0 };
	protected static QuadArray sQa;
	protected static QuadArray sQaFrame;
    protected TransformGroup mFrameTrans = null;
    protected Shape3D frameShape;
    protected Shape3D fillShape;
    
	static {
		sQa =
			new QuadArray(
				4,
				GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2,
				1,
				sTexMap);
		sQa.setCoordinate(0, new Point3d(-0.5, -0.5, 0));
		sQa.setCoordinate(1, new Point3d(0.5, -0.5, 0));
		sQa.setCoordinate(2, new Point3d(0.5, 0.5, 0));
		sQa.setCoordinate(3, new Point3d(-0.5, 0.5, 0));

		sQaFrame =
			new QuadArray(
				4,
				GeometryArray.COORDINATES);
		sQaFrame.setCoordinate(0, new Point3d(-0.5, -0.5, 0));
		sQaFrame.setCoordinate(1, new Point3d(0.5, -0.5, 0));
		sQaFrame.setCoordinate(2, new Point3d(0.5, 0.5, 0));
		sQaFrame.setCoordinate(3, new Point3d(-0.5, 0.5, 0));


		sQa.setTextureCoordinate(0, 0, new TexCoord2f(0, 0));
		sQa.setTextureCoordinate(0, 1, new TexCoord2f(1, 0));
		sQa.setTextureCoordinate(0, 2, new TexCoord2f(1, 1));
		sQa.setTextureCoordinate(0, 3, new TexCoord2f(0, 1));
	}



	public WallNode() {
		super();
		fillShape = new Shape3D(sQa);
		frameShape = new Shape3D(sQaFrame);
		
		fillShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		fillShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);		
		fillShape.setAppearance(Node3D.makeAppearance(new Color3f(Color.GRAY),0.5f,0.75f,false));
		frameShape.setAppearance(Node3D.makeAppearance(new Color3f(Color.BLACK),null,0.f,0.f,false,PolygonAttributes.POLYGON_LINE));
		
		mContents.addChild(fillShape);
		mContents.addChild(frameShape);
	}
	
	/**
	 * Sets the Appearance of the interior of the plane.
	 * 
	 * @param app interior Appearance
	 */
	public void setFillAppearance(Appearance app){

		fillShape.setAppearance(app);
	}	
	
	/**
	 * Sets the Appearance of the edges (or "frame") of the plane.
	 * @param app edge Appearance
	 */
	public void setFrameAppearance(Appearance app){
		frameShape.setAppearance(app);
	}	

	public Appearance getFillAppearance(){
		return fillShape.getAppearance();
	}	
	
	public Appearance getFrameAppearance(){
		return frameShape.getAppearance();
	}	
	
	public Color3f getColor() {
		ColoringAttributes ca = fillShape.getAppearance().getColoringAttributes();
		Color3f c = new Color3f();
		ca.getColor(c);
		return c;
	}
	public void setColor(Color c){
		setColor(new Color3f(c));
	}
	
	public void setColor(Color3f c) {	
		Appearance app = Node3D.makeAppearance(c,0.5f,0.75f,false);
		fillShape.setAppearance(app);
	
	}
}
