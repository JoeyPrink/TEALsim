/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FieldVector.java,v 1.22 2010/09/24 21:00:22 pbailey Exp $ 
 * 
 */

package teal.sim.spatial;

import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.render.TAbstractRendered.NodeType;
import teal.render.scene.SceneFactory;
import teal.render.scene.TNode3D;
import teal.render.scene.TShapeNode;
import teal.physics.em.BField;
import teal.physics.em.EField;

public class FieldVector extends SpatialVector {

    private static final long serialVersionUID = 3979265858943857712L;
    protected int fieldType = 0;
    public static int E_FIELD = 0;
    public static int B_FIELD = 1;
    protected boolean scaleByMagnitude = false;
    protected double scaleFactor = 1.;

    public FieldVector() {
    	nodeType = NodeType.ARROW_SOLID;
    }

    public FieldVector(Vector3d pos, int fieldType, boolean scale) {
        nodeType = NodeType.ARROW_SOLID;

        position = pos;
        this.fieldType = fieldType;
        this.scaleByMagnitude = scale;

        if (fieldType == E_FIELD) {
            this.setColor(Teal.DefaultEFieldColor);
        } else if (fieldType == B_FIELD) {
            this.setColor(Teal.DefaultBFieldColor);
        }
    }

    public void nextSpatial() {
        if (fieldType == E_FIELD) {
//            value.set(((EMEngine)theEngine).getEField().get(position));
            value.set(theEngine.getElementByType(EField.class).get(position));
            value.scale(scaleFactor);
        } else {
//            value.set(((EMEngine)theEngine).getBField().get(position));
            value.set(theEngine.getElementByType(BField.class).get(position));
            value.scale(scaleFactor);
        }
        registerRenderFlag(GEOMETRY_CHANGE);
    }

    protected TNode3D makeNode() {
//        SolidArrowNode node = new SolidArrowNode( );
        TShapeNode node = (TShapeNode) SceneFactory.makeNode(this);
        node.setPickable(false);
        node.setVisible(true);
        node.setColor(getColor());
        updateNode3D(node);
        return node;
    }

 //   @Override
//    protected void updateNode3D(LineNode node) {
//        if ((node == null) || (theEngine == null)) {
//            return;
//        }
//        Vector3d vector = new Vector3d(value);
//        if (scaleByMagnitude) {
//            vector.scale(value.length());
//        } else {
//            vector.scale(arrowScale);
//        }
//
//        assert(position == this.object.getPosition());
//        
//        Vector3d from = new Vector3d(position);
//        Vector3d to = new Vector3d(from);
//        to.add(vector);
//        node.setFromTo(from, to);
//
//    }

    @Override
    protected void updateNode3D(TShapeNode node) {
        if ((node == null) || (theEngine == null)) {
            return;
        }
        Vector3d vector = new Vector3d(value);
        if (scaleByMagnitude) {
            node.setScale(value.length());
        } else {
            node.setScale(arrowScale);
        }

        node.setPosition(position);
        if (vector.length() > 0) {
            node.setDirection(new Vector3d(vector));
        }

    }

    /**
     * @return Returns the fieldType.
     */
    public double getFieldType() {
        return fieldType;
    }

    /**
     * @param fieldType The scaleFactor to set.
     */
    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * @return Returns the scaleFactor.
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * @param scaleFactor The scaleFactor to set.
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
    public Vector3d getValue() {
        return this.value;
    }
}
