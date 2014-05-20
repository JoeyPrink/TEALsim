/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: EMRadiator.java,v 1.17 2010/09/28 21:40:41 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.render.TAbstractRendered;
import teal.render.scene.SceneFactory;
import teal.render.scene.TNode3D;
import teal.sim.properties.IsSpatial;


/**
 * This is a very specialized object for use in the EMRadiatorApp.  Represents an EM wave generator that generates
 * an oscillating E and B field.
 */
public class EMRadiator extends EMObject implements GeneratesE, GeneratesB, IsSpatial {

    private static final long serialVersionUID = 3905521583920788275L;
    protected double charge;
    protected Vector3d[] positions;
    protected double[] times;

    protected Vector3d lastPos, newPos;
    protected double lastTime;

    protected int historyLength = 100;
    protected double propSpeed = 1.;
    protected double range = 100.;

    protected boolean isGeneratingE = true;
    protected boolean isGeneratingB = true;

    public EMRadiator() {
        super();
        nodeType = TAbstractRendered.NodeType.WALL;
        charge = 1.0;
        positions = new Vector3d[historyLength];
        times = new double[historyLength];
        clearHistory();
        lastPos = new Vector3d();
    }

    public Vector3d getE(Vector3d pos) {
        // initially assuming an infinite current sheet as the generator, i guess
        double x = pos.x;
        x = (x / 0.1);
        double T = x / propSpeed;
        
        int absT = (int) Math.abs(T);
        Vector3d E = new Vector3d();
        if (absT < historyLength) {
            E = positions[absT];
        }
        
        E.scale(-1.);
        return E;
    }

    public Vector3d getE(Vector3d pos, double t) {
        return getE(pos);
    }

    public double getEFlux(Vector3d pos) {
        return 0.;
    }

    public double getEPotential(Vector3d pos) {
        return 0.;
    }

    public boolean isGeneratingE() {
        return isGeneratingE;
    }

    public Vector3d getB(Vector3d pos) {
        Vector3d dir = new Vector3d(1, 0, 0);
        if (pos.x < 0) dir.scale(-1.);
        Vector3d E = getE(pos);
        Vector3d B = new Vector3d();
        B.cross(E, dir);
        
        return B;
    }

    public Vector3d getB(Vector3d pos, double t) {
        return getB(pos);
    }

    public double getBFlux(Vector3d pos) {
        return 0.;
    }

    public double getBPotential(Vector3d pos) {
        return 0.;
    }

    public boolean isGeneratingB() {
        return isGeneratingB;
    }

    public void setPosition(Vector3d pos) {
        pos.x = 0;
        super.setPosition(pos);
    }

    public void nextSpatial() {
        // update history
        newPos = getPosition();
        //lastPos = getPosition();
        lastTime = theEngine.getTime();
        int j = 1;
        while (j < historyLength) {

            positions[(historyLength) - j] = new Vector3d(positions[((historyLength) - j - 1)]);
            times[(historyLength) - j] = times[((historyLength) - j - 1)];
            j++;
        }
        Vector3d vel = new Vector3d();

        

        double alpha = 0.4;
        vel.set(positions[1]);
        vel.scale(alpha);
        Vector3d vel2 = new Vector3d();
        vel2.set(velocity);
        vel2.scale(5.);
        vel2.scale(1 - alpha);
        vel.add(vel2);

        positions[0] = vel;
        times[0] = lastTime;
        lastPos.set(newPos);
        theEngine.requestSpatial();
        
    }

    public void needsSpatial() {

    }

    public void render() {
        // render some junk
        super.render();
    }

    protected TNode3D makeNode() {
        
        TNode3D node = SceneFactory.makeNode(this);
        node.setScale(30.);
        node.setElement(this);
//        Appearance app = ((WallNode) node).getFillAppearance();
//        ColoringAttributes ca = app.getColoringAttributes();
//        ca.setColor(new Color3f(1.0f, 0.8f, 0.8f));
//        app.setColoringAttributes(ca);
//        ((WallNode) node).setFillAppearance(app);
        AxisAngle4d aa = new AxisAngle4d(new Vector3d(0, 1, 0), Math.PI * 0.5);
        Quat4d quat = new Quat4d();
        quat.set(aa);
        setRotation(quat);
        
        return node;
    }

    
    public void clearHistory() {
        for (int i = 0; i < positions.length; i++) {
            positions[i] = getPosition();
            times[i] = 0.0;
        }
    }

    /**
     * @return Returns the propSpeed.
     */
    public double getPropSpeed() {
        return propSpeed;
    }

    /**
     * @param propSpeed The propSpeed to set.
     */
    public void setPropSpeed(double propSpeed) {
        this.propSpeed = propSpeed;
    }

    /**
     * @return Returns the historyLength.
     */
    public int getHistoryLength() {
        return historyLength;
    }

    /**
     * @param historyLength The historyLength to set.
     */
    public void setHistoryLength(int historyLength) {
        this.historyLength = historyLength;
        positions = new Vector3d[historyLength];
        times = new double[historyLength];
        clearHistory();
    }
}
