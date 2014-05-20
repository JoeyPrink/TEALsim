/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Pipe.java,v 1.1 2010/04/12 19:57:53 stefan Exp $
 * 
 */

package teal.render.j3d.geometry;

import javax.vecmath.*;

import teal.util.*;

import com.sun.j3d.utils.geometry.*;

/**
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.1 $
 */

/**
 * This class contains methods for generating "pipe" geometry (a pipe is a hollow cylinder with no end caps).
 */
public class Pipe {

    public static GeometryInfo makeGeometry(int numSeg, double radius, double thickness, double height) {
        return makeGeometry(numSeg, radius, thickness, height, 0.);
    }

    /**
     * Generates pipe geometry.
     * 
     * @param numSeg number of azimuthal segments (this is effectively the "number of sides" of the pipe).
     * @param radius radius of the pipe.
     * @param thickness thickness of the pipe.
     * @param height height of the pipe.
     * @param offset vertical offset of the pipe.
     * @return GeometryInfo for a pipe.
     */
    public static GeometryInfo makeGeometry(int numSeg, double radius, double thickness, double height, double offset) {
        float y = (float) (height / 2.0);
        float inner = (float) (radius - (thickness / 2.0) + offset);
        float outer = (float) (radius + (thickness / 2.0) + offset);
        Point2f[] data = new Point2f[5];
        data[0] = new Point2f(inner, y);
        data[1] = new Point2f(outer, y);
        data[2] = new Point2f(outer, -y);
        data[3] = new Point2f(inner, -y);
        data[4] = new Point2f(inner, y);
        TDebug.println(2,"Pipe makeGeometry");
        AxisShape ax = new AxisShape();
        return ax.makeGeometry(data, numSeg, new Point2f((float) (radius + offset), 0f));
    }

}
