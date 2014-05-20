/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: InclinedPlane.java,v 1.17 2007/07/17 15:46:56 pbailey Exp $ 
 * 
 */

package teal.physics.mech;

import java.awt.Color;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.config.Teal;
import teal.render.j3d.*;
import teal.render.scene.TNode3D;
import teal.physics.physical.Wall;

public class InclinedPlane extends Wall {

    private static final long serialVersionUID = 3690762812448584496L;
    protected double iWidth = 1.;
    protected double iAngle = Math.PI / 4.;
    protected double iBase = 1.;
    protected Vector3d iDirection = new Vector3d(1., 0., 0.);
    protected Vector3d iPosition = new Vector3d(0., 0., 0.);

    public InclinedPlane() {
        super();
    }

    protected TNode3D makeNode() {

        InclinedPlaneNode node = (InclinedPlaneNode) new InclinedPlaneNode();
        Appearance fillAppearance = Node3D.makeAppearance(new Color3f(Color.GRAY), 0.5f, 0.5f, false);
        fillAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.75f));
        Appearance frameAppearance = Node3D.makeAppearance(new Color3f(Color.BLACK), 0.f, 0.f, false);
        frameAppearance.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_LINE,
            PolygonAttributes.CULL_NONE, 0f));
        node.setFillAppearance(fillAppearance);
        node.setFrameAppearance(frameAppearance);
        updateNode(node);
/*        
        Appearance fillAppearance = Node3D.makeAppearance(Color.GRAY, 0.5f, 0.5f, false);
        fillAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.75f));
        Appearance frameAppearance = Node3D.makeAppearance(Color.BLACK, 0.f, 0.f, false);
        frameAppearance.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_LINE,
            PolygonAttributes.CULL_NONE, 0f));
        node.setFillAppearance(fillAppearance);
        node.setFrameAppearance(frameAppearance);

        // Scale
        Vector3d scaling = new Vector3d(iWidth, iBase * Math.tan(iAngle), iBase);
        node.setScale(scaling);

        // Rotation

        Vector3d normal = getNormal();
        Vector3d reference = new Vector3d(0., 0., -1.);
        Vector3d axis = new Vector3d();
        double angle = reference.angle(iDirection);
        if (Math.abs(angle - Math.PI) < Teal.DoubleZero || Math.abs(angle) < Teal.DoubleZero) {
//            In this case, the cross product would yield zero. In general, we should pick
//             a vector orthogonal to the reference and new directions. Here, it's simpler,
//             because we know that all rotations of the plane are around the y axis. The
//             reason why we don't do this even if the cross product is non-zero is that
//             it provides information about the direction of rotation. In this case, that
//             information is not needed, since we're either not rotating, or rotating by
//             180 degrees.
            
            axis.set(0., 1., 0.);
        } else {
            axis.cross(reference, iDirection);
            axis.normalize();
        }

        System.out.println(angle);
        AxisAngle4d axisangle = new AxisAngle4d();
        axisangle.set(axis, angle);
        Quat4d quaternion = new Quat4d();
        quaternion.set(axisangle);
        node.setPickable(false);
        setRotation(quaternion);
*/
        return node;
    }

    protected void updateNode(InclinedPlaneNode node) {

    	// Reset transform
    	node.setTransform(new Transform3D());
    	
    	// Scale
        Vector3d scaling = new Vector3d(iWidth, iBase * Math.tan(iAngle), iBase);
        node.setScale(scaling);

        // Rotation

        Vector3d normal = getNormal();
        Vector3d reference = new Vector3d(0., 0., -1.);
        Vector3d axis = new Vector3d();
        double angle = reference.angle(iDirection);
        if (Math.abs(angle - Math.PI) < Teal.DoubleZero || Math.abs(angle) < Teal.DoubleZero) {
            /*	In this case, the cross product would yield zero. In general, we should pick
             a vector orthogonal to the reference and new directions. Here, it's simpler,
             because we know that all rotations of the plane are around the y axis. The
             reason why we don't do this even if the cross product is non-zero is that
             it provides information about the direction of rotation. In this case, that
             information is not needed, since we're either not rotating, or rotating by
             180 degrees.
             */
            axis.set(0., 1., 0.);
        } else {
            axis.cross(reference, iDirection);
            axis.normalize();
        }

        //System.out.println(angle);
        AxisAngle4d axisangle = new AxisAngle4d();
        axisangle.set(axis, angle);
        Quat4d quaternion = new Quat4d();
        quaternion.set(axisangle);
        node.setPickable(false);
        setRotation(quaternion);
    }

    public double getInclineAngle() {
        return iAngle;
    }

    public void setInclineAngle(double angle) {
        iAngle = angle;
        regenerateWallParameters();
    }

    public double getInclineBase() {
        return iBase;
    }

    public void setInclineBase(double base) {
        iBase = base;
        regenerateWallParameters();
    }

    public Vector3d getInclineDirection() {
        return iDirection;
    }

    public void setInclineDirection(Vector3d direction) {
        iDirection.set(direction);
        iDirection.y = 0.;
        iDirection.normalize();
        regenerateWallParameters();
    }

    public Vector3d getInclinePosition() {
        return iPosition;
    }

    public void setInclinePosition(Vector3d position) {
        iPosition.set(position);
        regenerateWallParameters();
    }

    public double getInclineWidth() {
        return iWidth;
    }

    public void setInclineWidth(double width) {
        iWidth = width;
        regenerateWallParameters();
    }

    public void regenerateWallParameters() {
        double iHeight = iBase * Math.tan(iAngle);
        Vector3d position = new Vector3d(iPosition.x, iPosition.y + 0.5 * iHeight, iPosition.z);
        Vector3d edge1 = new Vector3d(iDirection.x * iBase, iHeight, iDirection.z * iBase);
        Vector3d edge2 = new Vector3d(iDirection.z * iWidth, 0., -iDirection.x * iWidth);
        setPosition(position);
        setEdge1(edge1);
        setEdge2(edge2);
        renderFlags |= GEOMETRY_CHANGE;
    }

    public Vector3d getSlideDirection() {
        Vector3d direction = new Vector3d(iDirection);
        direction.scale(-iBase);
        direction.y = -iBase * Math.tan(iAngle);
        direction.normalize();
        return direction;
    }

    public Vector3d locationToPosition(double location) {
        Vector3d slide = getSlideDirection();
        slide.scale(-location);
        Vector3d offset = new Vector3d(iDirection);
        offset.scale(-iBase / 2.);
        Vector3d position = new Vector3d(iPosition);
        position.add(offset);
        position.add(slide);
        return position;
    }

    public double positionToLocation(Vector3d position) {
        Vector3d slide = getSlideDirection();
        Vector3d offset = new Vector3d(iDirection);
        offset.scale(-iBase / 2.);
        Vector3d pos = new Vector3d(position);
        pos.sub(offset);
        pos.sub(iPosition);
        double location = -slide.dot(pos);
        return location;
    }
    
    public void render() {
    	if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {

            if (mNode != null) {
                this.calculateNormal();
                updateNode((InclinedPlaneNode) mNode);
                //mNode = makeNode();
                
            }
            renderFlags ^= GEOMETRY_CHANGE;
            

        }
        super.render();
    }

}
