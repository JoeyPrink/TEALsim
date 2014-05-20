/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: InclinedPlaneNode.java,v 1.12 2010/07/21 21:46:49 stefan Exp $ 
 * 
 */
package teal.render.jme;

import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;

import teal.render.ColorUtil;
import teal.render.TMaterial;

import com.jme.math.Vector3f;
import com.jme.scene.Line;
import com.jme.scene.TriMesh;
import com.jme.scene.Line.Mode;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

public class InclinedPlaneNode extends Node3D{

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 7222057847808610223L;

	protected final static Vector3f [] default_coords = {
    	new Vector3f(-0.5f,-0.5f,-0.5f),
    	new Vector3f(0.5f,-0.5f,-0.5f),
    	new Vector3f(0.5f,-0.5f,0.5f),
    	new Vector3f(-0.5f,-0.5f,0.5f),
    	new Vector3f(-0.5f,0.5f,-0.5f),
    	new Vector3f(0.5f,0.5f,-0.5f)
    };
	
	private Vector3f [] coords = null;
	
	private transient TriMesh fillShape = null;
	private transient Line frameShape = null;

	protected final static int [] idxF = {0,3,4,  1,2,5,  0,5,4,  0,1,5,  0,2,3,  0,1,2,  3,2,5,  3,5,4};
    protected final static int [] idxE = {0,1,   1,2,   1,5,   2,5,   4,5,   2,3,    4,3,    3,0,   4,0};

    public InclinedPlaneNode() {
    	this(default_coords);
    }
    
    public InclinedPlaneNode(Vector3f [] data) {
    	TMaterial nodeMat = ColorUtil.getMaterial(Color.GRAY);
    	nodeMat.setShininess(0.5f);
    	nodeMat.setTransparancy(0.75f);
    	Node3D.setMaterial(nodeMat, this);
    	
    	updateGeometry(data);
    }
    
    public void updateGeometry(Vector3f [] geo) {
    	coords = geo.clone(); //FIXXME: is that necessary?
    	if(fillShape == null) {
    		fillShape = new TriMesh("fillShape");
    		fillShape.setIndexBuffer(BufferUtils.createIntBuffer(idxF));
    		this.attachChild(fillShape);
    	}
    	
    	if(frameShape == null) {
    		frameShape = new Line("frameShape");
    		frameShape.setIndexBuffer(BufferUtils.createIntBuffer(idxF));    		
    		frameShape.setMode(Mode.Segments);

    		Node3D.setMaterial(ColorUtil.getMaterial(Color.BLACK), frameShape);
    		this.attachChild(frameShape);
    	}

    	FloatBuffer coordsBuf = BufferUtils.createFloatBuffer(coords);
    	fillShape.reconstruct(coordsBuf , null, null, null);
    	
        FloatBuffer lines = BufferUtils.createFloatBuffer(3*idxE.length); //FIXXME: efficiency?
        for(int i=0; i<idxE.length; i++){
        	Vector3f vert = coords[idxE[i]];
        	lines.put(vert.x).put(vert.y).put(vert.z);
        }
//        frameShape.setMode(Mode.Loop);
    	frameShape.reconstruct(lines, null, null, null);
//    	frameShape.setIndexBuffer(BufferUtils.createIntBuffer(idxF));    	
    }
    
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        coords = (Vector3f [])capsule.readSavableArray("coords", default_coords);
        updateGeometry(coords);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(coords, "coords", default_coords);
    }
    
}
