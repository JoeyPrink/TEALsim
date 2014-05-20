/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HelixNode.java,v 1.9 2010/09/22 15:48:10 pbailey Exp $ 
 * 
 */

package teal.render.jme;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.vecmath.Vector3d;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Line;
import com.jme.scene.Line.Mode;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

import teal.render.TAbstractRendered;
import teal.render.primitives.Helix;
import teal.sim.properties.HasFromTo;

public class HelixNode extends ShapeNode implements HasFromTo
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8407651944952741604L;

	private final static String HELIX_NAME = "HelixLine";
	private transient Line helixGeo = new Line(HELIX_NAME);
	
	protected int segments = 360;
	protected float radius = 1f;
	protected float turns = 1f;
	
	public HelixNode(){
		setRotable(true);
		setHelixGeo();
		this.attachChild(helixGeo);
	}

	public HelixNode(TAbstractRendered element) {
		if(element != null) {
			doSetElement(element,false);

			segments = ((Helix)element).getSegments();
			radius = (float) ((Helix)element).getRadius();
			turns = ((Helix)element).getTurns();
			
		}
		setRotable(true);
		setHelixGeo();
		this.attachChild(helixGeo);

	}
	
	protected void setHelixGeo() {
		
		FloatBuffer buf = BufferUtils.createVector3Buffer(segments);
		buf.rewind();

		float floatsegs = (float)segments;

		for (int k = 0; k < segments; ++k) {
			
			float floatk = (float)k;
			buf.put(radius * FastMath.sin((floatk/floatsegs) * FastMath.PI * 2.0f * turns));
			buf.put(floatk/floatsegs);
			buf.put(radius * FastMath.cos((floatk/floatsegs) * FastMath.PI * 2.0f * turns));
		}
		helixGeo.reconstruct(buf,null,null,null);
		helixGeo.setMode(Mode.Connected);
		
	}
	
	public double getlength() {
		return getScale().length();
	}
	
	public void setLength(double len) {
		setScale(len);
	}
	
	
	
	public void read(JMEImporter e) throws IOException {
		super.read(e);
		InputCapsule capsule = e.getCapsule(this);
		selected = capsule.readBoolean("selected", false);	
		segments = capsule.readInt("segments", 360);
		radius = capsule.readFloat("radius", 1);
		turns = capsule.readFloat("turns", 1);		
		setHelixGeo();
	}

	public void write(JMEExporter e) throws IOException {
		super.write(e);
		OutputCapsule capsule = e.getCapsule(this);
		capsule.write(segments, "segments", 360);
		capsule.write(radius, "radius", 1);
		capsule.write(turns, "turns", 1);
	}
	
	public void setFromTo(Vector3d from, Vector3d to){
		setFromTo(Node3D.getVector3f(from),Node3D.getVector3f(to));
	}
	
	public void setFromTo(Vector3f from, Vector3f to) {
		Vector3f vec = to.subtract(from);
		float length = vec.length();
		if(length > 0) {
			if(!isVisible())
				setVisible(true);
			this.setLocalTranslation(from);
			
			//sets direction
			setDirection(vec.normalize());
			this.setLocalScale(length);
		} else if (isVisible()) {
			setVisible(false);
		}
	}

	
}
