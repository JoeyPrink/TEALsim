/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RadiationFieldLine.java,v 1.13 2007/07/16 22:05:10 pbailey Exp $ 
 * 
 */

package teal.sim.spatial;

import javax.vecmath.Vector3d;

import teal.sim.properties.PhysicalElement;
import teal.util.TDebug;

/**
 * Special fieldline used in EMRadiator to model EM wave propogation.  Position of the source object is buffered and 
 * propogated outward as a displacement of the fieldline.
 */
public class RadiationFieldLine extends RelativeFLine {

    private static final long serialVersionUID = 3906926785668068914L;
    double angle; // angle from object measured from x axis
    double emag; //don't know what this is yet
    double propSpeed;

    Vector3d lastPos, thisPos;
    Vector3d recentPos[] = new Vector3d[kMax];

    double lastTime;
    double recentTime[] = new double[kMax];

    public RadiationFieldLine(PhysicalElement obj, double myAngle) {
        super(obj);
        this.angle = myAngle;
        setOffset(myAngle);
        this.propSpeed = 1.0;
        int j = 0;

        while (j < kMax) {
            recentPos[j] = getPosition();
            recentTime[j] = 0.;
            j++;
        }
    }

    public void clearHistory() {
        for (int i = 0; i < recentPos.length; i++) {
            recentPos[i] = getPosition();
            recentTime[i] = 0.0;
        }
    }

    // Have to overload a bunch of functions to handle drawing a completely different "fieldline"
    public void nextSpatial() {
        if (mNode == null) mNode = makeNode();
        buildLines();
    }

    // original 
    protected void buildLines() {
        numP = buildLineData(pData, 1);
        numN = 0;
        
    }

    protected int buildLineData(float[] points, int sign) {
        reachedStart = false;
        int off = 0;
        int ilinc = 0;
        if ((points == null) || (points.length < (3 * kMax))) points = new float[3 * kMax];
        try {
            double dsArc = Math.abs(sArc);
            double signDsArc = dsArc * sign;
            double sA = sArc;
            int numLoops = 0;
            boolean loop = true;
            Vector3d startPoint = new Vector3d(getPosition());
            thisPos = new Vector3d(getPosition());
            Vector3d curPoint = new Vector3d(getPosition());
            Vector3d nextPoint = new Vector3d();

            lastPos = getPosition();
            lastTime = theEngine.getTime();
            int j = 1;
            while (j < kMax) {

                recentPos[(kMax) - j] = recentPos[((kMax) - j - 1)];
                recentTime[(kMax) - j] = recentTime[((kMax) - j - 1)];
                j++;
            }
            recentPos[0] = lastPos;
            recentTime[0] = lastTime;
            
            while (ilinc < kMax) {
                curPoint.add(recentPos[ilinc], new Vector3d(propSpeed * ilinc * sArc * Math.sin(angle), propSpeed
                    * ilinc * sArc * Math.cos(angle), 0.));
                points[off++] = (float) curPoint.x;
                points[off++] = (float) curPoint.y;
                points[off++] = (float) curPoint.z;

                ilinc++;

            }
            TDebug.println(2, "FLine num points = " + ilinc);
        } catch (ArithmeticException ae) {
            TDebug.printThrown(0, ae);
        }
        renderFlags |= GEOMETRY_CHANGE;
        return ilinc;
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
}
