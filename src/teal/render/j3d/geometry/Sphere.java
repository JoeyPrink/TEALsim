/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Sphere.java,v 1.1 2010/04/12 19:57:53 stefan Exp $
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
 * This class generates spherical geometry by lofting a two dimensional half-circle around the y axis.
 */
public class Sphere {

    public static GeometryInfo makeGeometry(double radius) {
        return makeGeometry(12, radius);
    }

    /**
     * Returns GeometryInfo for a Sphere.
     * 
     * @param numSegments number of azimuthal segments (this is the "number of sides" of the sphere).
     * @param radius radius of the sphere.
     * @return GeometryInfo for a sphere.
     */
    public static GeometryInfo makeGeometry(int numSegments, double radius) {
        Point2f[] data = new Point2f[numSegments + 1];
        double deltaR = Math.PI / (double) numSegments;
        data[0] = new Point2f(0f, (float) radius);
        for (int i = 1; i < numSegments; i++) {
            data[i] = new Point2f((float) (radius * Math.sin(i * deltaR)), (float) (radius * Math.cos(i * deltaR)));
        }
        data[numSegments] = new Point2f(0f, (float) -radius);
        //data[numSegments] = new Point2f((float)radius,0.f);
        AxisShape as = new AxisShape();

        return as.makeGeometry(data, numSegments);
    }

}
