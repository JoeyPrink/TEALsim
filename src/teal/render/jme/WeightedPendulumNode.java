/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WeightedPendulumNode.java,v 1.9 2010/07/06 19:23:44 stefan Exp $ 
 * 
 */
package teal.render.jme;

import java.awt.Color;
import java.nio.FloatBuffer;

import teal.render.ColorUtil;
import teal.render.TMaterial;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Tube;
import com.jme.util.geom.BufferUtils;

public class WeightedPendulumNode extends Node3D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6971378699000293348L;

	private final static String FILL_NAME = "fill shape";
	private final static String FRAME_NAME = "frame shape";
	private final static String RING_NAME = "ring shape";
	
	public WeightedPendulumNode() {
		Node topNode = new Node("topnode");
		this.attachChild(topNode);
		
		Box	fillShape = new Box(FILL_NAME, new Vector3f(-1.0f,0.0f,-0.5f),
				new Vector3f(0.0f,1.0f,0.5f));
		topNode.attachChildAt(fillShape, 0);

		TMaterial matBox = ColorUtil.getMaterial(new Color(255,50,50));
		matBox.setShininess(0.5f);
		matBox.setTransparancy(0.5f);		
		Node3D.setMaterial(matBox, fillShape);
		
		Line frameShape = new Line(FRAME_NAME);
		
        FloatBuffer lines = BufferUtils.createFloatBuffer(3*BoxNode.idxE.length);
        for(int i=0; i<BoxNode.idxE.length; i++){
        	Vector3f vert = BoxNode.coords[BoxNode.idxE[i]];
        	lines.put(vert.x).put(vert.y).put(vert.z);
        }
        lines.flip();
		Node3D.setMaterial(ColorUtil.getMaterial(Color.BLACK), frameShape);
		topNode.attachChild(frameShape);
		
		
		Tube ringshape=new Tube(RING_NAME, 1.5f, 0.5f, 1.0f, 2, 20);	
		ringshape.setLocalRotation(new Quaternion(0.5f,-0.5f,0.5f,-0.5f));
		setMaterial(ColorUtil.getMaterial(new Color(0,105,154)));
		topNode.attachChild(ringshape);
	}

	public void setArrowDirection(double current) {
		((Node)this.getChild(0)).getChild(RING_NAME).setLocalScale((float)current);
	}

	public void setBoxScale(javax.vecmath.Vector3d s) {
		Vector3f scale = new Vector3f((float)s.x, (float)s.y, (float)s.z);
		((Node)this.getChild(0)).getChild(FILL_NAME).setLocalScale(scale);
		((Node)this.getChild(0)).getChild(FRAME_NAME).setLocalScale(scale);
	}

	//ATTENTION: this places the box into the center of the x axis!
	public void updateRodGeometry(double l, double w, double d) {
		Vector3f [] newcoords = {
				new Vector3f((float)(-w*0.5),-(float)l,(float)(d*0.5)), 
				new Vector3f((float)(w*0.5),-(float)l,(float)(d*0.5)),
				new Vector3f((float)(w*0.5),-(float)l,(float)(-d*0.5)), 
				new Vector3f((float)(-w*0.5),-(float)l,(float)(-d*0.5)),
				new Vector3f((float)(-w*0.5),0f,(float)(d*0.5)), 
				new Vector3f((float)(w*0.5),0f,(float)(d*0.5)),
				new Vector3f((float)(w*0.5),0f,(float)(-d*0.5)), 
				new Vector3f((float)(-w*0.5),0f,(float)(-d*0.5))};
	
		((Box)((Node)this.getChild(0)).getChild(FILL_NAME)).updateGeometry(new Vector3f(0f,-0.5f,0f), (float)w/2, (float)l/2, (float)d/2);		
		
        FloatBuffer lines = BufferUtils.createFloatBuffer(3*BoxNode.idxE.length);
        for(int i=0; i<BoxNode.idxE.length; i++){
        	Vector3f vert = newcoords[BoxNode.idxE[i]];
        	lines.put(vert.x).put(vert.y).put(vert.z);
        }
        lines.flip();
        
        ((Line)((Node)this.getChild(0)).getChild(FRAME_NAME)).reconstruct(lines, null, null, null);

	}

	
	public void updateRingGeometry(double r1, double r2, double d) {
		((Tube)((Node)this.getChild(0)).getChild(RING_NAME)).updateGeometry((float)r2, (float)r1, (float)d, 2, 20);
	}

	public void setRingPosition(double pos) {
		((Node)this.getChild(0)).getChild(RING_NAME).setLocalTranslation(0f, -(float)pos, 0f);
	}

}
