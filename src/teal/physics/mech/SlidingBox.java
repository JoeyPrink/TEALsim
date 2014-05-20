/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SlidingBox.java,v 1.15 2010/04/12 20:13:17 stefan Exp $ 
 * 
 */

package teal.physics.mech;

import javax.vecmath.*;


import teal.render.j3d.*;
import teal.render.scene.*;
import teal.sim.properties.HasLength;
import teal.physics.physical.*;

public class SlidingBox extends PhysicalObject implements HasLength {

    private static final long serialVersionUID = 3762817069280211509L;

    protected double length = 2., width = 1., height = 1.;

    protected InclinedPlane incl = null;

    public SlidingBox(InclinedPlane iincl) {
        super();
        setInclinedPlane(iincl);
        updateFromIncline();
    }
    
    public void updateFromIncline() {
    	Vector3d normal = incl.getNormal();
        Vector3d direction = incl.getNormal();
        if (direction.angle(new Vector3d(0., 1., 0.)) > Math.PI / 2.) direction.negate();
        setDirection(direction);
    }

    public void setInclinedPlane(InclinedPlane iincl) {
        incl = iincl;
    }

    public InclinedPlane getInclinedPlane() {
        return incl;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double wwidth) {
        width = wwidth;
    }

    public double getHeight() {
        return length;
    }

    public void setHeight(double hheight) {
        height = hheight;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double llength) {
        length = llength;
    }

    protected TNode3D makeNode() {
        BoxNode node = new BoxNode();

        // Scale
        //		Vector3d scaling = new Vector3d(width, height, length);
        Vector3d scaling = new Vector3d(length, height, width);
        node.setScale(scaling);

        // Rotation (Handled by the rotation controller).

        // Position
        //		node.setPosition(position);

        return node;
    }

}
