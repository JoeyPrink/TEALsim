/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Torus.java,v 1.1 2010/04/12 19:57:53 stefan Exp $
 * 
 */

package teal.render.j3d.geometry;

import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;

/** 
 * Constructs a torus with the ring's center at R1, the radius of the swept ring is R2.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.1 $
 */

public class Torus {

    public static GeometryInfo makeGeometry(double R1, double R2) {
        return makeGeometry(R1, R2, 24, 24);
    }

    /**
     * Generates GeometryInfo for a torus.
     * 
     * @param R1 radius of torus.
     * @param R2 cross-sectional radius of torus.
     * @param divisionCircle number of segments in cross-sectional circle.
     * @param divisionTorus number of azimuthal segments.
     */
    public static GeometryInfo makeGeometry(double R1, double R2, int divisionCircle, int divisionTorus) {
        Point2f[] points = new Point2f[divisionCircle + 1];
        for (int k = 0; k < divisionCircle; k++) {
            points[k] = new Point2f((float) (R1 + R2 * Math.sin(2 * Math.PI * k / divisionCircle)), (float) (R2 * Math
                .cos(2 * Math.PI * k / divisionCircle)));
        }
        //points[divisionCircle]=new Point2f((float)(R1 + R2),0f);
        points[divisionCircle] = new Point2f((float) (R1), (float) R2);
        Point2f center = new Point2f((float) R1, 0f);
        AxisShape as = new AxisShape();
        return as.makeGeometry(points, divisionCircle, center);
    }

}
