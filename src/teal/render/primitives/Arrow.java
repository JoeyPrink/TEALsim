/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Arrow.java,v 1.11 2010/04/30 04:44:28 pbailey Exp $ 
 * 
 */

package teal.render.primitives;

import javax.vecmath.*;

import teal.render.scene.*;
import teal.sim.properties.HasFromTo;

/**
 * This class generates a Rendered arrow, which can be added to the world directly.
 * 
 */
public class Arrow extends Line {

    private static final long serialVersionUID = 3257850995487815993L;
   
    public Arrow() {
       this(false);
        
    }
    
    public Arrow(boolean solid) {
        super();
        if(solid)
    		nodeType = NodeType.ARROW_SOLID;
    	else
    		nodeType = NodeType.ARROW;
    }

    /**
     * Creates an arrow that points from and to the given points.
     * 
     * @param from position of the base of the arrow.
     * @param to position of the head of the arrow.
     */
    public Arrow(Vector3d from, Vector3d to) {
        this(from, to,false);
        nodeType = NodeType.ARROW;
    }
    
    public Arrow(Vector3d from, Vector3d to, boolean solid) {
    	super(from,to);
    	if(solid)
    		nodeType = NodeType.ARROW_SOLID;
    	else
    		nodeType = NodeType.ARROW;
    	
    }

    protected TNode3D makeNode() {
    	TNode3D node;
    	node = SceneFactory.makeNode(this);
        ((HasFromTo) node).setFromTo(position, drawTo);
        return node;
    }
}
