/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FieldValue.java,v 1.41 2010/07/16 21:41:39 stefan Exp $ 
 * 
 */

package teal.sim.spatial;

import java.awt.geom.GeneralPath;
import java.util.*;

import javax.vecmath.*;

import teal.config.*;
import teal.field.*;
import teal.render.BoundingSphere;
import teal.render.Bounds;
import teal.render.HasPosition;
import teal.render.TAbstractRendered;
import teal.render.j3d.ArrayNode;
import teal.render.j3d.ArrowNode;
import teal.render.j3d.LineNode;
import teal.render.j3d.Node3D;
import teal.render.scene.TNode3D;
import teal.sim.engine.*;
import teal.physics.em.*;
import teal.util.TDebug;

public class FieldValue extends SpatialField {

    private static final long serialVersionUID = 3256446906170488884L;
    double arrowLength = 0.05;

    public FieldValue() {
        setPickable(true);
    }

    public FieldValue(Field field) {
        this();
        setField(field);
    }

    protected TNode3D makeNode() {
        ArrayNode node = new ArrayNode();
        updateNode3D(node);
        return node;
    }

    public void nextSpatial() {
        registerRenderFlag(GEOMETRY_CHANGE);
    }

    protected GeneralPath getGeneralPath(Vector3d pos, Vector3d vec, double lengthRef) {

        GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 6);
        double lenght = vec.length();
        gp.moveTo((float) pos.x, (float) pos.y);
        double vecLength = vec.length() * Teal.FieldValueLength / lengthRef;
        vec.normalize();
        Vector3d vec2 = new Vector3d(-vec.y, vec.x, vec.z);
        float vx = (float) (vecLength * vec.x + pos.x);
        float vy = (float) (vecLength * vec.y + pos.y);
        vec2.scale(arrowLength);
        vec.scale(arrowLength);
        gp.lineTo(vx, vy);
        gp.lineTo((float) (vx - vec.x + vec2.x), (float) (vy - vec.y + vec2.y));
        gp.lineTo((float) (vx - vec.x - vec2.x), (float) (vy - vec.y - vec2.y));
        gp.lineTo((float) (vx), (float) (vy));
        return gp;
    }

    public double getArrowLenght() {
        return arrowLength;
    }

    public void setArrowLenght(double length) {
        arrowLength = length;
        nextSpatial();
    }

    public void render() {
        if (mNode == null) return;
        if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {
            updateNode3D((ArrayNode) mNode);
            renderFlags ^= GEOMETRY_CHANGE;
        }
    }

    public void updateNode3D(ArrayNode node) {
        if ((node == null) || (theEngine == null)) {
            return;
        }
        Vector3d pos = getPosition();
        Vector3d objPos = new Vector3d();
        Vector3d pointPos = new Vector3d();

        Vector objects = ((CompositeField) field).getObjects();
        int count = (objects.size() * 2) + 1;
        Vector3d fieldValue = field.get(pos);
        double lengthRef = fieldValue.length();
        if (lengthRef == 0) lengthRef = 1.;

        if (node.getNodeCount() != count) {
            TDebug.println(1, "\tUpdateNode3d adding nodes");
            node.removeAll();
            ArrowNode ar = new ArrowNode();
            ar.setPickable(false);
            ar.setAppearance(Node3D.makeAppearance(new Color3f(Teal.fieldValueMainColor)));
            node.addNode(ar);
            for (int i = 0; i < objects.size(); i++) {
                ar = new ArrowNode();
                ar.setPickable(false);
                node.addNode(ar);
                LineNode line = new LineNode();
                line.setPickable(false);
                line.setColor(new Color3f(Teal.fieldValueSecondColor));
                node.addNode(line);
            }
        }

        Iterator nodes = node.iterator();

        // Arrow for total field at position
        ArrowNode arrow = (ArrowNode) nodes.next();
        //arrow.setAppearance(TNode.makeAppearance(Colors.fieldValueMainColor));
        arrow.setTransform(Teal.DefaultOrigin, fieldValue, fieldValue.length() * Teal.FieldValueLength / lengthRef);

        arrow.setVisible(true);

        Iterator it = objects.iterator();

        Object bObj = null;
        while (it.hasNext()) {
            bObj = it.next();
            if (field instanceof BField) {
                fieldValue = ((GeneratesB) bObj).getB(pos);
            } else if (field instanceof EField) {
                fieldValue = ((GeneratesE) bObj).getE(pos);
            } else if (field instanceof PField) {
                fieldValue = ((GeneratesP) bObj).getP(pos);
            } else {
                fieldValue = new Vector3d();
            }

            // Arrow for object's field contribution at position
            arrow = (ArrowNode) nodes.next();
            if (fieldValue.length() > 0) {
                if (!arrow.isVisible()) arrow.setVisible(true);

                arrow.setTransform(Teal.DefaultOrigin, fieldValue, fieldValue.length() * Teal.FieldValueLength
                    / lengthRef);
                arrow.setColor(((TAbstractRendered) bObj).getColor());
            } else {
                if (arrow.isVisible()) arrow.setVisible(false);
            }
            // arrow from object to position
            LineNode line = (LineNode) nodes.next();
            objPos.sub(((HasPosition) bObj).getPosition(), pos);
            line.setFromTo(objPos, Teal.DefaultOrigin);
        }
    }

    public Bounds getBoundingArea() {
        return new BoundingSphere(new Point3d(position), arrowLength);
    }
  
}
