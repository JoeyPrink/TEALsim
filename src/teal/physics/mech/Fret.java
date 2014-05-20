/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Fret.java,v 1.20 2010/04/30 03:15:58 pbailey Exp $ 
 * 
 */

package teal.physics.mech;

import java.awt.*;
import java.beans.*;

import javax.vecmath.*;

import teal.render.TealMaterial;
import teal.render.j3d.*;
import teal.render.j3d.geometry.Cylinder;
import teal.render.scene.*;
import teal.sim.*;
import teal.sim.properties.HasLength;
import teal.sim.properties.HasRadius;

public class Fret extends SimRendered implements HasLength, HasRadius {

    private static final long serialVersionUID = 3761413027307665201L;

    private boolean visible = true;
    private Boolean state = new Boolean(false);

    protected double length = 1.;
    protected double radius = 0.1;

    public Fret(Vector3d pposition, double llength, double rradius) {
        mMaterial.setDiffuse(Color.BLUE);
        mMaterial.setTransparancy(0.5f);
        setPosition(pposition);
        setLength(llength);
        setRadius(rradius);
    }

    public void setVisible(boolean vvisible) {
        if (visible != vvisible) renderFlags |= VISIBILITY_CHANGE;
        visible = vvisible;
    }

    public void setLength(double llength) {
        renderFlags |= GEOMETRY_CHANGE;
        length = llength;
    }

    public double getLength() {
        return length;
    }

    public void setRadius(double rradius) {
        renderFlags |= GEOMETRY_CHANGE;
        radius = rradius;
    }

    public double getRadius() {
        return radius;
    }

    public void propertyChange(PropertyChangeEvent pce) {
        // super.propertyChange(pce);
        if (pce.getPropertyName().equalsIgnoreCase("position")) {
            Object obj = pce.getSource();
            Vector3d vec = new Vector3d();
            if (obj instanceof SlidingBox) {
                SlidingBox box = (SlidingBox) obj;
                InclinedPlane incl = box.getInclinedPlane();
                vec = new Vector3d(incl.getSlideDirection());
                vec.scale(box.length);
            }

            Vector3d pre = new Vector3d((Vector3d) pce.getOldValue());
            Vector3d post = new Vector3d((Vector3d) pce.getNewValue());
            pre.add(vec);
            post.add(vec);

            //			System.out.println( "pre: " + pre );
            //			System.out.println( "post: " + post );

            pre.sub(position);
            post.sub(position);

            Vector3d normal = new Vector3d();
            normal.cross(pre, new Vector3d(length, 0, 0));
            // Condition 1: Coplanar vectors.
            if (Math.abs(post.dot(normal)) < 1e-5) {
                Vector3d unit = new Vector3d(length, 0, 0);
                Vector3d pre_ = new Vector3d(unit);
                pre_.scaleAdd(-pre.dot(unit), pre);
                Vector3d post_ = new Vector3d(unit);
                post_.scaleAdd(-post.dot(unit), post);
                // Condition 2: Opposing vectors.
                if (pre_.dot(post_) < 0.) {
                    firePropertyChange(new PropertyChangeEvent(this, "fretstate", state, new Boolean(true)));
                    state = new Boolean(true);
                    System.out.println("Fret conditions met.");
                } else {
                    firePropertyChange(new PropertyChangeEvent(this, "fretstate", state, new Boolean(false)));
                    state = new Boolean(false);
                }

            }
        }
    }

    /*
     public void propertyChange(PropertyChangeEvent pce) {
     // super.propertyChange(pce);
     if( pce.getPropertyName().equalsIgnoreCase("position") ) {
     Object obj = pce.getSource();
     Vector3d pre = new Vector3d( (Vector3d) pce.getOldValue() );
     Vector3d post = new Vector3d( (Vector3d) pce.getNewValue() );
     pre.sub(position);
     post.sub(position);
     if( obj instanceof SlidingBox ) {
     SlidingBox box = (SlidingBox) obj;
     InclinedPlane incl = box.getInclinedPlane();
     Vector3d offset =  new Vector3d(incl.getSlideDirection());
     offset.scale(box.length);
     pre.add(offset);
     post.add(offset);
     }
     
     Vector3d normal = new Vector3d();
     normal.cross(pre, length);
     // Condition 1: Coplanar vectors.
     if( Math.abs(post.dot(normal))<radius ) {
     normal.normalize();
     Vector3d unit = new Vector3d();
     unit.cross(normal,length);
     unit.normalize();
     // Condition 2: Opposing vectors.
     if( pre.dot(unit)*post.dot(unit) < 0. ) {
     firePropertyChange( new PropertyChangeEvent(this, "fretstate", state, new Boolean(true)) );
     state = new Boolean(true);
     } else {
     firePropertyChange( new PropertyChangeEvent(this, "fretstate", state, new Boolean(false)) );
     state = new Boolean(false);
     }
     }		}
     }
     */

    protected TNode3D makeNode() {

        TShapeNode node = (TShapeNode) new ShapeNode();
        node.setGeometry(Cylinder.makeGeometry(16, radius, 1));
        node.setColor(TealMaterial.getColor3f(mMaterial.getDiffuse()));
        node.setShininess(mMaterial.getShininess());
        
        if (!visible) {
            node.setTransparency(1.f);
        }
        else{
        	node.setTransparency(mMaterial.getTransparancy());
        }
        // Scale
        Vector3d scaling = new Vector3d(1., length, 1.);
        node.setScale(scaling);

        // Position
        node.setPosition(position);

        // Rotation
        node.setRotation(orientation);

        return node;
    }

}
