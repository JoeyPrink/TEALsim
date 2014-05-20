/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Helix.java,v 1.11 2010/04/30 03:14:19 pbailey Exp $ 
 * 
 */

package teal.render.primitives;

import javax.vecmath.*;

import teal.render.*;
import teal.render.scene.*;
import teal.sim.properties.HasFromTo;
import teal.sim.properties.HasRadius;


/**
 * This is a Rendered object in the shape of a helix that can be used to represent a spring.
 * It is implemented identically to Line, but with these additional parameters: 
 * - "turns" (float, number of times the helix winds around its axis)
 * - "segments" (int, number of vertices along the entire helix)
 * - "radius" (float, radius of the helix)
 * 
 * Right now this thing scales only along the y-axis to get the "stretching spring" effect.
 * @author danziger
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Helix extends Line implements HasRadius{

    private static final long serialVersionUID = 3256439188181168179L;

    protected float turns = 20.f;
    protected int segments = 360;
    protected double radius = 0.01;

    /**
     * Creates a helix between the two supplied points.
     * 
     * @param pos start point.
     * @param obj end point.
     */
    public Helix(Vector3d pos, Vector3d obj) {
        super(pos,obj);
        nodeType = NodeType.HELIX;
    }
    /**
     * Creates a helix between a point and an object.
     * 
     * @param pos start point.
     * @param obj object to draw to.
     */
    public Helix(Vector3d pos, HasPosition obj) {
        super(pos,obj);
        nodeType = NodeType.HELIX;
    }
    /**
     * Creates a helix between two objects.
     * 
     * @param pos "from" object.
     * @param obj "to" object.
     */
    public Helix(HasPosition pos, HasPosition obj) {
        super(pos,obj);
        nodeType = NodeType.HELIX;
    }
  
    /** 
     * Returns the number of turns in this helix.
     * 
     */
    public float getTurns() {
        return turns;
    }

    /**
     * Sets the number of turns along the length of this helix.
     * 
     * @param turns the number of turns.
     */
    public void setTurns(float turns) {
        this.turns = turns;
        renderFlags |= GEOMETRY_CHANGE;
    }

    /**
     * Returns the number of segments (vertices) along the length of this helix.
     * 
     * @return segments
     */
    public int getSegments() {
        return segments;
    }

    /**
     * Sets the number of segments (vertices) along the length of this helix.
     * 
     * @param segs the number of segments.
     */
    public void setSegments(int segs) {
        segments = segs;
        renderFlags = GEOMETRY_CHANGE;
    }

    /**
     * Returns the radius of this helix.
     * @return radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the helix.
     * 
     * @param r new radius.
     */
    public void setRadius(double r){
        this.radius = r;
        renderFlags |= GEOMETRY_CHANGE;
    }


    protected TNode3D makeNode() {
        return SceneFactory.makeNode(this);
    }

    public void render() {
        if (mNode != null) {
            if (((renderFlags & POSITION_CHANGE) == POSITION_CHANGE)
                || ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE)){
                ((HasFromTo) mNode).setFromTo(position, drawTo);

                if((renderFlags & POSITION_CHANGE) == POSITION_CHANGE){
                    renderFlags ^= POSITION_CHANGE;
                }
                if((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE){
                    renderFlags ^= GEOMETRY_CHANGE;
                }
            }
            super.render();
        }
        
    }

}
