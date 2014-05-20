/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Wall.java,v 1.46 2010/07/29 21:40:11 pbailey Exp $ 
 * 
 */

package teal.physics.physical;

import java.awt.Color;
import java.util.*;

import javax.vecmath.*;

import teal.math.RectangularPlane;
import teal.render.TAbstractRendered;
import teal.render.TealMaterial;
import teal.render.scene.*;
import teal.sim.collision.*;
import teal.sim.engine.EngineRendered;

/**
 * Represents a rectangular wall in space.
 */
public class Wall extends EngineRendered implements HasCollisionController {

    private static final long serialVersionUID = 3257845502224970038L;

    protected Vector3d edge1 = null;
    protected Vector3d edge2 = null;
    protected Vector3d normal = null;
    protected ArrayList<HasCollisionController> adherenceList = null;

    protected boolean is_colliding = false;
    protected WallCollisionController collisionController = null;

    public Wall() {
        super();
        nodeType = NodeType.WALL;
        normal = new Vector3d();
        adherenceList = new ArrayList<HasCollisionController>();
        setPickable(false);
        collisionController = new WallCollisionController(this);
        collisionController.setElasticity(1.);
        setColliding(true);
        position = new Vector3d(0., 0., 0.);
        edge1 = new Vector3d(1., 0., 0.);
        edge2 = new Vector3d(0., 0., 1.);
        calculateNormal();
        
        mMaterial = new TealMaterial(Color.gray,0.5f,0.75f);
        
    }

    /**
     * Constructs a wall with the center at position, using the edge1 and
     * edge2 vectors to define edge directions and lengths.
     */
    public Wall(Vector3d position, Vector3d edge1, Vector3d edge2) {
        this();
        this.position = new Vector3d(position);
        this.edge1 = new Vector3d(edge1);
        this.edge2 = new Vector3d(edge2);
        calculateNormal();
    }

    public Wall(RectangularPlane rec) {
        this();
        set(rec);
    }

    public void set(RectangularPlane rec) {
        position = new Vector3d(rec.getCenter());
        edge1 = rec.getEdge1();
        edge2 = rec.getEdge2();
        calculateNormal();
        registerRenderFlag(TAbstractRendered.GEOMETRY_CHANGE);
    }

    public void setElasticity(double eelasticity) {
        collisionController.setElasticity(eelasticity);
    }

    public double getElasticity() {
        return collisionController.getElasticity();
    }
    
    public void setTolerance(double tol) {
    	collisionController.setTolerance(tol);
    
    }

    public void setEdge1(Vector3d edge1) {
        this.edge1 = new Vector3d(edge1);
        calculateNormal();
        registerRenderFlag(TAbstractRendered.GEOMETRY_CHANGE);
    }

    public void setEdge2(Vector3d edge2) {
        this.edge2 = new Vector3d(edge2);
        calculateNormal();
        registerRenderFlag(TAbstractRendered.GEOMETRY_CHANGE);
    }

    public Vector3d getEdge1() {
        return new Vector3d(edge1);
    }

    public Vector3d getEdge2() {
        return new Vector3d(edge2);
    }

    // *****************************************************
    // Collision-related Section	
    // *****************************************************

    public boolean isColliding() {
        return is_colliding;
    }

    public void setColliding(boolean x) {
        is_colliding = x;
    }

    public CollisionController getCollisionController() {
        return collisionController.replica();
    }

    public void setCollisionController(CollisionController cg) {
        if (cg instanceof WallCollisionController) {
            collisionController = (WallCollisionController) cg.replica();
        }
    }

    public Vector3d getPosition1() {
        return new Vector3d(position);
    }

    public Vector3d getVelocity1() {
        return new Vector3d();
    }

    public Vector3d getPosition2() {
        return new Vector3d(position);
    }

    public Vector3d getVelocity2() {
        return new Vector3d();
    }

    public void updateCollision() {
    }

    public double getMass() {
        return Double.POSITIVE_INFINITY;
    }

    public void applyImpulse(Vector3d impulse) {
    }

    public void applyCorrection(Vector3d correction) {
    }

    public void addAdheredObject(HasCollisionController x) {
        if (!adherenceList.contains(x)) {
            adherenceList.add(x);
        }
    }

    public void removeAdheredObject(HasCollisionController x) {
        if (adherenceList.contains(x)) {
            adherenceList.remove(x);
        }
    }

    public boolean isAdheredTo(HasCollisionController x) {
        return adherenceList.contains(x);
    }

    public Vector3d getReactionDueTo(HasCollisionController x) {
        return new Vector3d();
    }

    public void setReactionDueTo(HasCollisionController x, Vector3d reaction) {
    }

    public Vector3d getReactionDueToAllExcept(HasCollisionController x) {
        return new Vector3d();
    }

    public Vector3d getReactionDueToAll() {
        return new Vector3d();
    }

    public Vector3d getExternalForces() {
        return new Vector3d();
    }

    public boolean solveReactionStep() {
        return true;
    }

    public void calculateNormal() {
        normal.cross(edge1, edge2);
        normal.normalize();
    }

    public Vector3d getNormal() {
        return new Vector3d(normal);
    }

    protected TNode3D makeNode() {
        TNode3D node = SceneFactory.makeNode(this);
        node.setElement(this);
        return updateNode(node);
    }
    
    public TNode3D updateNode(TNode3D node){
    // Scale
    Vector3d scaling = new Vector3d(edge1.length(), edge2.length(), 0.);
    node.setScale(scaling);

    // Position
    node.setPosition(position);

    // Rotation

    // IMPORTANT: One problem that might be encountered here is that of
    // having no rotation, or perhaps having a 180 degree rotation. That
    // should be dealt with by proper condition checking.

    Vector3d normal = getNormal();

    Vector3d axis = new Vector3d();
    axis.cross(new Vector3d(0., 0., 1.), normal);
    double angle = normal.angle(new Vector3d(0., 0., 1.));
    axis.normalize();
    AxisAngle4d axisangle = new AxisAngle4d();
    axisangle.set(axis, angle);
    Quat4d quaternion = new Quat4d();
    quaternion.set(axisangle);
    quaternion.normalize();
    Matrix3d rotation = new Matrix3d();
    rotation.set(quaternion);
    Vector3d reference = new Vector3d(edge1.length(), 0., 0.);
    Vector3d rotated = new Vector3d(reference);
    rotation.transform(rotated);

    Vector3d axis2 = new Vector3d();
    double angle2 = edge1.angle(rotated);
    axis2.cross(rotated, edge1);
    AxisAngle4d axisangle2 = new AxisAngle4d();
    axisangle2.set(axis2, angle2);
    Quat4d quaternion2 = new Quat4d();
    quaternion2.set(axisangle2);
    quaternion2.normalize();
    Matrix3d rotation2 = new Matrix3d();
    rotation2.set(quaternion2);

    Matrix3d total = new Matrix3d();
    total.mul(rotation2, rotation);

    setRotation(total);
    node.setRotation(total);
    return node;
}

}
