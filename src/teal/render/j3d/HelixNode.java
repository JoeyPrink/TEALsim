/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HelixNode.java,v 1.23 2010/09/22 15:48:10 pbailey Exp $ 
 * 
 */

package teal.render.j3d;


import javax.media.j3d.*;
import javax.vecmath.Vector3d;

import teal.render.*;
import teal.render.primitives.Helix;
import teal.sim.properties.HasFromTo;


/**
 * 
 * HelixNode is used to generate a helix-shaped spline, typically used to represent a spring.
 */
public class HelixNode extends ShapeNode implements HasFromTo
{
	protected LineStripArray helixGeo;
	protected int segments;
	protected float radius;
	protected float turns;
	protected Vector3d lastDir; 


    public HelixNode(){
	    super();
	    lastDir = new Vector3d(0.,1.,0.);        
    }
	public HelixNode(TAbstractRendered element){
		this();
		setElement(element);
		segments = ((Helix)element).getSegments();
		radius = (float) ((Helix)element).getRadius();
		turns = ((Helix)element).getTurns();
		setAppearance(Node3D.makeAppearance(element.getMaterial()));
		
		setRotable(true);
	    setDefaultGeometry();
	    
	    
		
	}
	
	/**
	 * generates helix line geometry with specified number of segments.
	 * 
	 * @param segments the number of segments along the length of the helix.
	 * @return completed Geometry.
	 */
	protected Geometry makeHelixGeo(int segments) {
		int[] strip = {segments};
		helixGeo = new LineStripArray(segments,GeometryArray.COORDINATES,strip);
		float[] coords = new float[3*segments];
		int k = 0;
		int off = 0;
		
		
		
		while (k < segments) {
			
			float floatk = (float)k;
			float floatsegs = (float)segments;
			coords[off++] = radius*(float)Math.sin((floatk/floatsegs) * Math.PI * 2.0 * turns);
			coords[off++] =  floatk/(floatsegs);
			coords[off++] = radius*(float)Math.cos((floatk/floatsegs) * Math.PI * 2.0 * turns);
			k++;
		}
		
		helixGeo.setCoordinates(0, coords);
		
		return helixGeo;
	}

    protected void setDefaultGeometry()
    {
        mShape.setGeometry(makeHelixGeo(segments),0);
    }
    
    public double getlength()
    {
        double len = 0;
        Transform3D trans = new Transform3D();
        mTransform.getTransform(trans);
        len = trans.getScale();
        return len;
    }
        
    public void setLength(double len)
    {
        Transform3D trans = new Transform3D();
        mTransform.getTransform(trans);
        trans.setScale(len);
        mTransform.setTransform(trans);
    }
    
	public void setFromTo(Vector3d from, Vector3d to) {
		Vector3d tmp = new Vector3d(to);
		tmp.sub(from);
		double length = tmp.length();
		if (length > 0) {
			if (!isVisible())
				setVisible(true);
			setPosition(from);
			tmp.normalize();
			
			// This test is to eliminate "jittering" that was resulting from round-off error in the normalization
			// of tmp.  The threshold value may need to be adjusted if there are problems in future applications of this thing.
			//if (1.0 - dot > 0.0001) {
				setDirection(tmp);
			//}
			lastDir.set(tmp.x, tmp.y, tmp.z);
			setScale(new Vector3d(1.,length,1.));
			
		} else if (isVisible())
			setVisible(false);
	}


}
