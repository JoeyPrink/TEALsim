/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Particle.java,v 1.20 2010/04/09 17:00:09 pbailey Exp $ 
 * 
 */

package teal.physics;

import java.awt.Color;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import teal.core.TUpdatable;
import teal.math.Integratable;
import teal.render.j3d.*;
import teal.render.scene.TNode3D;
import teal.sim.engine.EngineRendered;
import teal.sim.properties.IsSpatial;

public class Particle extends EngineRendered implements Integratable, TUpdatable, IsSpatial {

    /**
     * 
     */
    private static final long serialVersionUID = 3979274637824046897L;
    boolean integrating = true;
    transient Vector3d position_d = new Vector3d();
    int size = 6;
    protected boolean mNeedsSpatial = false;

    public void needsSpatial() {
        mNeedsSpatial = true;
    }

    static PointArray sPoint;

    static {
        sPoint = new PointArray(1, GeometryArray.COORDINATES);

    }

    public Particle() {
        mMaterial.setDiffuse(Color.WHITE);
    }

    public void nextSpatial() {
    }

    public Vector3d getVelocity() {
        return new Vector3d();
    }

    public final void setPosition(Vector3d pos) {
        position_d.set(pos);
        super.setPosition(pos);
    }

    public final TNode3D makeNode() {
        ShapeNode node = new ShapeNode();
        node.setGeometry(sPoint);
        node.setColor(mMaterial.getDiffuse());
        Appearance app = node.getAppearance();
        app.setPointAttributes(new PointAttributes(size, true));
        node.setAppearance(app);
        return node;
    }

    public final void setColor(Color3f color) {
        mMaterial.setDiffuse(color);
        if (mNode == null) return;
        ShapeNode node = (ShapeNode) getNode3D();
        Appearance app = Node3D.makeAppearance(color);
        app.setPointAttributes(new PointAttributes(size, true));
        node.setAppearance(app);
    }

    public final void setSize(int size) {
        this.size = size;
        if (mNode == null) return;
        ShapeNode node = (ShapeNode) getNode3D();
        Appearance app = Node3D.makeAppearance(mMaterial.getDiffuse());
        app.setPointAttributes(new PointAttributes(size, true));
        node.setAppearance(app);
    }

    public final void update() {
        if (position != position_d) setPosition(position_d);
    }

    public final void getDependentDerivatives(double[] depDerivatives, int offset, double time) {
        Vector3d velocity = getVelocity();
        depDerivatives[offset++] = velocity.x;
        depDerivatives[offset++] = velocity.y;
        depDerivatives[offset++] = velocity.z;
    }

    public final void getDependentValues(double[] depValues, int offset) {
        depValues[offset++] = position_d.x;
        depValues[offset++] = position_d.y;
        depValues[offset++] = position_d.z;
    }

    public final double getIndependentValue() {
        if (theEngine == null) return 0.;
        return theEngine.getTime();
    }

    public final int getNumberDependentValues() {
        return 3;
    }

    public final void setIntegrating(boolean integrating) {
        this.integrating = integrating;
    }

    public final boolean isIntegrating() {
        return integrating;
    }

    public final void reconcile() {
        //position.set(position_d);
    }

    public final void setDependentValues(double[] newDepValues, int offset) {
        position_d.x = newDepValues[offset++];
        position_d.y = newDepValues[offset++];
        position_d.z = newDepValues[offset++];
    }

}
