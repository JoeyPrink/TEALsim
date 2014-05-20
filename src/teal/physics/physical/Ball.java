/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Ball.java,v 1.36 2010/08/18 20:45:42 stefan Exp $ 
 * 
 */

package teal.physics.physical;

import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.physics.*;
import teal.render.TealMaterial;
import teal.render.j3d.SphereNode;
import teal.render.scene.*;
import teal.sim.collision.SphereCollisionController;
import teal.sim.engine.AbstractEngine;
import teal.sim.engine.TSimEngine;
import teal.sim.properties.HasRadius;

/**
 * The Ball class represents a spherical PhysicalObject that feels only gravitational effects.  Unlike most objects,
 * it is automatically fitted with a SphereCollisionController.
 */
public class Ball extends PhysicalObject implements GeneratesG, HasRadius{

    private static final long serialVersionUID = 3257008756696103217L;
    protected double radius = 1.;
    boolean generatingGField = false;

    public Ball() {
        super();
        mMaterial.setShininess(0.5f);
        setRotable(false);
        SphereCollisionController scc = new SphereCollisionController(this);
        scc.setRadius(1.);
        scc.setElasticity(1.);
        setCollisionController(scc);
        setColliding(true);
        
        nodeType = NodeType.SPHERE;
    }

    /**
     * Sets the elasticity of the CollisionController attached to this Ball.
     * 
     * @param eelasticity new elasticity.
     */
    public void setElasticity(double eelasticity) {
        collisionController.setElasticity(eelasticity);
    }

    /**
     * Returns the elasticity of the CollisionController attached to this Ball.
     * 
     * @return elasticity of CollisioController.
     */
    public double getElasticity() {
        return collisionController.getElasticity();
    }

    /**
     * Sets the radius of this Ball (and of it's associated CollisionController).
     * 
     * @param rradius new radius.
     */
    public void setRadius(double rradius) {
        radius = rradius;
        ((SphereCollisionController) collisionController).setRadius(rradius);
    }

    /**
     * Returns the radius of this Ball.
     * 
     * @return radius
     */
    public double getRadius() {
        return radius;
    }

    protected TNode3D makeNode() {
        TShapeNode node = (TShapeNode) SceneFactory.makeNode(this);
//        node.setScale(radius);

//        node.setColor(TealMaterial.getColor3f(mMaterial.getDiffuse()));
//        node.setShininess(mMaterial.getShininess());
        node.setTransparency(mMaterial.getTransparancy());        
        node.setPosition(position);

        return node;
    }

    public Vector3d getExternalForces() {
        Vector3d externalForces = super.getExternalForces();
        Vector3d temp = new Vector3d();
        if (isMoveable()) {
//            temp.scale(mass_d, ((EMEngine)theEngine).getGField().get(position_d, this));
//            temp.scale(mass_d, theEngine.getElementByType(GField.class).get(position_d, this));
            temp.scale(mass_d, ((GField)theEngine.getElementByType(TSimEngine.EngineElementType.GFIELD)).get(position_d, this));
            
            externalForces.add(temp);
        }
        return externalForces;
    }

    // **********************************************************************
    // GeneratesG methods.
    // **********************************************************************

    public Vector3d getG(Vector3d position, double t) {
        return getG(position);
    }

    public Vector3d getG(Vector3d pos) {
        Vector3d r = new Vector3d();
        r.sub(pos, position_d);
        double d2 = r.lengthSquared();
        r.normalize();
        r.scale(-Teal.G_Constant * this.mass_d / d2);
        return r;
    }

    public void setGeneratingG(boolean b) {
        generatingGField = b;
    }

    public boolean isGeneratingG() {
        return generatingGField;
    }
}
