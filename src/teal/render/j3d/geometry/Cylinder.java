/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Cylinder.java,v 1.1 2010/04/12 19:57:53 stefan Exp $
 * 
 */

package teal.render.j3d.geometry;

import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;

/**
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.1 $
 */

/**
 * This class contains static methods for generating geometry for a Cylinder, using AxisShape to loft a rectangle around
 * the y axis.
 */
public class Cylinder {

    public static GeometryInfo makeGeometry(int numSeg, double radius, double height) {
        return makeGeometry(numSeg, radius, height, 0., true);
    }

    public static GeometryInfo makeGeometry(int numSeg, double radius, double height, boolean closed) {
        return makeGeometry(numSeg, radius, height, 0., closed);
    }

    public static GeometryInfo makeGeometry(int numSeg, double radius, double height, double offset) {
        return makeGeometry(numSeg, radius, height, offset, true);
    }

    /**
     * Makes cylindrical geometry with given parameters.
     *
     * @param numSeg number of azimuthal segments (this is effectively the "number of sides" of the cylinder).
     * @param radius Radius of the cylinder.
     * @param height Height of the cylinder.
     * @param offset Vertical offset of the cylinder from the origin.
     * @param closed does this cylinder have a closed top and bottom?
     */
    public static GeometryInfo makeGeometry(int numSeg, double radius, double height, double offset, boolean closed) {
        Point2f[] data = null;
        float offs = (float) offset;
        float y = (float) height / 2.0f;
        if (closed) {
            data = new Point2f[4];
            data[0] = new Point2f(0f, y + offs);
            data[1] = new Point2f((float) radius, y + offs);
            data[2] = new Point2f((float) radius, -y + offs);
            data[3] = new Point2f(0f, -y + offs);
        } else {
            data = new Point2f[2];
            data[0] = new Point2f((float) radius, y + offs);
            data[1] = new Point2f((float) radius, -y + offs);
        }
        AxisShape ax = new AxisShape();
        return ax.makeGeometry(data, numSeg, new Point2f(0f, offs));
    }
    
    // sliced cylinder (or "wedge")
    public static GeometryInfo makeGeometry(int numSeg, double radius, double height, double offset, boolean closed, double latheAngle, double latheAngleOffset) {
        Point2f[] data = null;
        float offs = (float) offset;
        float y = (float) height / 2.0f;
        if (closed) {
            data = new Point2f[4];
            data[0] = new Point2f(0f, y + offs);
            data[1] = new Point2f((float) radius, y + offs);
            data[2] = new Point2f((float) radius, -y + offs);
            data[3] = new Point2f(0f, -y + offs);
        } else {
            data = new Point2f[2];
            data[0] = new Point2f((float) radius, y + offs);
            data[1] = new Point2f((float) radius, -y + offs);
        }
        AxisShape ax = new AxisShape();
        return ax.makeGeometry(data, numSeg, new Point2f(0f, offs), latheAngle, latheAngleOffset);
    }

}
