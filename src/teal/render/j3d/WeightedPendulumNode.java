/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WeightedPendulumNode.java,v 1.7 2010/04/12 20:13:17 stefan Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.Color;

import javax.media.j3d.*;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;


/**
 * This is the node for the WeightedPhysicalPendulum PhysicalObject.  It contains methods for rendering the the pendulum
 * itself, and the disc-shaped weight attached to it.
 */
public class WeightedPendulumNode extends Node3D {
	protected IndexedQuadArray faces;
	protected IndexedLineArray edges;
	protected Shape3D fillShape;
	protected Shape3D frameShape;
	protected Shape3D ringshape;
	
	TransformGroup box_tg = new TransformGroup();
	TransformGroup ring_tg = new TransformGroup();
	
	protected final static Point3d [] coords = { 
	           new Point3d(-1, 0, 0.5),new Point3d(0, 0, 0.5),
	           new Point3d(0, 0, -0.5),new Point3d(-1, 0, -0.5),
	           new Point3d(-1, 1, 0.5),new Point3d(0, 1, 0.5),
	           new Point3d(0, 1, -0.5),new Point3d(-1, 1, -0.5)};
	
	
	public WeightedPendulumNode() {
//		super(2);
//		
//		setGeometry(0,Cylinder.makeGeometry(24,rad,len).getIndexedGeometryArray(),0);
		super();
		// Box part
		fillShape = new Shape3D();
		frameShape = new Shape3D();
		initShape(fillShape);
		initShape(frameShape);
        faces = new IndexedQuadArray(8,GeometryArray.COORDINATES,24);
        faces.setCoordinates(0,BoxNode.coords,0,8);
        faces.setCoordinateIndices(0,BoxNode.idxF);
        edges = new IndexedLineArray(8,GeometryArray.COORDINATES,24);
        edges.setCoordinates(0,BoxNode.coords,0,8);
        edges.setCoordinateIndices(0,BoxNode.idxE);
		fillShape.setGeometry(faces);
        frameShape.setGeometry(edges);
        
		Appearance fillAppearance = Node3D.makeAppearance(new Color3f(new Color(255,50,50)),0.5f,0.5f,false);
		
		Appearance frameAppearance = Node3D.makeAppearance(new Color3f(Color.BLACK),0.f,0.f,false);
		frameAppearance.setPolygonAttributes(
			new PolygonAttributes(
				PolygonAttributes.POLYGON_LINE,
				PolygonAttributes.CULL_NONE,
				0f));
		
		fillShape.setAppearance(fillAppearance);
		frameShape.setAppearance(frameAppearance);
		box_tg.addChild(fillShape);
		box_tg.addChild(frameShape);
		mContents.addChild(box_tg);
		//mContents.addChild(fillShape);
		//mContents.addChild(frameShape);
		/// end box part
		
		
		// ring part
		ringshape=new Shape3D();
		initShape(ringshape);
		//line.setGeometry(sLine);
		Geometry stem  = teal.render.j3d.geometry.Pipe.makeGeometry(20, 1., 1., 1.).getIndexedGeometryArray(true);
		ringshape.setGeometry(stem);
		
		
		Transform3D tran = new Transform3D(); 
		AxisAngle4d aa = new AxisAngle4d(new Vector3d(1,0,0), Math.PI*0.5);
		tran.setRotation(aa);
		ring_tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		ring_tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//arrow.addChild(line);
		//arrow.addChild(translated_cone);
		Appearance arrowAppearance = Node3D.makeAppearance(new Color3f(new Color(0,105,154)),0.f,0.f,false);
		ringshape.setAppearance(arrowAppearance);
		
		ring_tg.addChild(ringshape);
		

		Transform3D tran2 = new Transform3D(); 
		//tran2.set(new Vector3f(0.f,-0.5f,0.f));
		tran2.setScale(1.);
		ring_tg.setTransform(tran);
		mContents.addChild(ring_tg);
		
		
	}
	
	/**
	 * This is called from InfiniteWire when the current changes.  Scales the arrow indicator to reflect the
	 * change in current.
	 * 
	 * @param current
	 */
	public void setArrowDirection(double current) {
		Transform3D t = new Transform3D();
		t.setScale(current);
		
		ring_tg.setTransform(t);
		//txt.setString("Current: " + current + "\n linebreak?");
		
	}
	
	public void setBoxScale(Vector3d s) {
		Transform3D trans = new Transform3D();
		box_tg.getTransform(trans);
		trans.setScale(s);
		box_tg.setTransform(trans);
	}
	
	public static void initShape(Shape3D shape){
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		
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
	
	public void updateRodGeometry(double l, double w, double d) {
		Point3d [] newcoords = {
				new Point3d(-w*0.5,-l,d*0.5), new Point3d(w*0.5,-l,d*0.5),
				new Point3d(w*0.5,-l,-d*0.5), new Point3d(-w*0.5,-l,-d*0.5),
				new Point3d(-w*0.5,0,d*0.5), new Point3d(w*0.5,0,d*0.5),
				new Point3d(w*0.5,0,-d*0.5), new Point3d(-w*0.5,0,-d*0.5)};
		
	
		faces = new IndexedQuadArray(8,GeometryArray.COORDINATES,24);
		faces.setCoordinates(0,newcoords,0,8);
		faces.setCoordinateIndices(0,BoxNode.idxF);
		edges = new IndexedLineArray(8,GeometryArray.COORDINATES,24);
		edges.setCoordinates(0,newcoords,0,8);
		edges.setCoordinateIndices(0,BoxNode.idxE);
		
		frameShape.setGeometry(edges);
		fillShape.setGeometry(faces);
	}
	
	public void updateRingGeometry(double r1, double r2, double d) {
		Geometry stem  = teal.render.j3d.geometry.Pipe.makeGeometry(20, (r1+r2)*0.5, r2-r1, d).getIndexedGeometryArray(true);
		ringshape.setGeometry(stem);
	}
	
	public void setRingPosition(double pos) {
		Transform3D trans = new Transform3D();
		ring_tg.getTransform(trans);
		trans.setTranslation(new Vector3d(0.,-pos,0.));
		ring_tg.setTransform(trans);
	}
}
