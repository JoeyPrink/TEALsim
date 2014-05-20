/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Stem.java,v 1.8 2010/04/30 03:14:19 pbailey Exp $ 
 * 
 */

package teal.render.primitives;

import javax.vecmath.*;

import teal.render.*;
import teal.render.scene.*;
import teal.sim.properties.HasFromTo;
import teal.sim.properties.HasRadius;

/**
 * The Stem class generates a Rendered cylinder that uses the HasFromTo interface used by the Line class.
 */
public class Stem extends Line implements HasRadius{

    private static final long serialVersionUID = 3978708385107621945L;
    protected double radius = 0.05;

    public Stem(Vector3d position, Vector3d drawTo) {
        super(position, drawTo);
        nodeType = NodeType.STEM;
    }

    public Stem(Vector3d pos, HasPosition obj) {
        super(pos, obj);

    }

    public Stem(HasPosition obj1, HasPosition obj2) {
        super(obj1, obj2);

    }

    protected TNode3D makeNode() {
    	TNode3D node = SceneFactory.makeNode(this);
        ((HasFromTo)node).setFromTo(position, drawTo);
        return node;
    }

    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
        renderFlags |= GEOMETRY_CHANGE;
    }
}
