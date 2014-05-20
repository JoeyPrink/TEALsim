/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GeomUtil.java,v 1.1 2010/04/12 19:57:53 stefan Exp $
 * 
 */

package teal.render.j3d.geometry;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.util.*;

/**
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.1 $
 */

/**
 * This class provides some utility functions for manipulating GeometryArrays.
 */
public class GeomUtil {

    public static final int POINT = 4;
    public static final int LINE = 8;
    public static final int QUAD = 16;

    public static final int ROTATE_X = 0;
    public static final int ROTATE_Y = 1;
    public static final int ROTATE_Z = 2;

    /**
     * Prints the contents of the supplied GeometryArray.
     * 
     * @param geom GeometryArray to print.
     */
    public static void printGeometry(GeometryArray geom) {
        double value[] = new double[3];
        int vertCt = geom.getVertexCount();
        TDebug.println(0, "// VertexCount = " + vertCt);
        TDebug.println(0, "geomCoords = {");
        for (int i = 0; i < vertCt; i++) {
            geom.getCoordinate(i, value);
            TDebug.println(0, "\t" + value[0] + ", " + value[1] + ", " + value[2] + ", ");

        }
        TDebug.println(0, "}\n");
    }

    /**
     * Adds an offset translation to all points in the supplied GeometryArray.
     * 
     * @param geom GeometryArray to translate.
     * @param offset Point3d offset to apply to the GeometryArray.
     */
    public static void moveGeometry(GeometryArray geom, Point3d offset) {

        Point3d value = new Point3d();
        int vertCt = geom.getVertexCount();
        for (int i = 0; i < vertCt; i++) {
            geom.getCoordinate(i, value);
            value.add(offset);
            geom.setCoordinate(i, value);
        }
    }

    /**
     * Transposes the coordinates of supplied Geometry about the principle axes.
     * 
     * @param geom GeometryArray to rotate.
     * @param view Axis to transpose about (ROTATE_Y, ROTATE_Z).
     */
    public static void transposeGeometry(GeometryArray geom, int view) {

        double value[] = new double[3];
        double value2[] = new double[3];
        int vertCt = geom.getVertexCount();
        int a = 0;
        int b = 1;
        int c = 2;
        switch (view) {
            case ROTATE_Y:
                a = 1;
                b = 0;
                c = 2;
                break;
            case ROTATE_Z:
                a = 0;
                b = 2;
                c = 1;
                break;
            case ROTATE_X:
            default:
                break;
        }

        for (int i = 0; i < vertCt; i++) {
            geom.getCoordinate(i, value);
            value2[0] = value[a];
            value2[1] = value[b];
            value2[2] = value[c];
            geom.setCoordinate(i, value2);
        }
    }

}