/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Ring.java,v 1.1 2010/04/12 19:57:53 stefan Exp $
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
 * This class generates geometry for a "Ring", which is a hollow cylinder.  
 * This geometry is essentially the same as "Pipe", except that
 * it doesn't use AxisShape for the calculations.
 */
public class Ring {

    

    /**
     * Generates GeometryInfo for a Ring.
     * 
     * @param numSeg number of azimuthal segments (this is the "number of sides" of the ring).
     * @param inner inner radius.
     * @param outer outer radius.
     * @param height height.
     * @return GeometryInfo for Ring.
     */
    public static GeometryInfo makeGeometry(int numSeg, double inner, double outer, double height) {
        double deg = 2 * Math.PI / (double) numSeg;
        int numVert = numSeg * 4;
        double rad = -Math.PI * 2.0;
        double z1 = height / 2.0;
        double z0 = height / -2.0;
        double x;
        double y;

        Point3d[] data = new Point3d[numVert];
        int vi = 0;

        for (int i = 0; i < numSeg; i++) {

            x = inner * Math.sin(rad);
            y = inner * Math.cos(rad);
            //data[vi++] = new Point3d(x,y,z0);
            //data[vi++] = new Point3d(x,y,z1);
            data[vi++] = new Point3d(x, z0, y);
            data[vi++] = new Point3d(x, z1, y);

            x = outer * Math.sin(rad);
            y = outer * Math.cos(rad);
            //data[vi++] = new Point3d(x,y,z1);
            //data[vi++] = new Point3d(x,y,z0);

            data[vi++] = new Point3d(x, z1, y);
            data[vi++] = new Point3d(x, z0, y);

            rad += deg;
        }
        int indxCt = 8 * 3 * numSeg;
        int[] geoIdx = new int[indxCt];

        int j = 0;
        // Do inside
        for (int i = 0; i < numVert; i += 4) {
            geoIdx[j++] = (i + 0) % numVert;
            geoIdx[j++] = (i + 1) % numVert;
            geoIdx[j++] = (i + 5) % numVert;

            geoIdx[j++] = (i + 5) % numVert;
            geoIdx[j++] = (i + 4) % numVert;
            geoIdx[j++] = (i + 0) % numVert;
        }

        // Do outside

        for (int i = 0; i < (numSeg * 4); i += 4) {
            geoIdx[j++] = (i + 2) % numVert;
            geoIdx[j++] = (i + 3) % numVert;
            geoIdx[j++] = (i + 6) % numVert;

            geoIdx[j++] = (i + 7) % numVert;
            geoIdx[j++] = (i + 6) % numVert;
            geoIdx[j++] = (i + 3) % numVert;
        }
        // Do top

        for (int i = 0; i < (numSeg * 4); i += 4) {
            geoIdx[j++] = (i + 1) % numVert;
            geoIdx[j++] = (i + 2) % numVert;
            geoIdx[j++] = (i + 5) % numVert;

            geoIdx[j++] = (i + 2) % numVert;
            geoIdx[j++] = (i + 6) % numVert;
            geoIdx[j++] = (i + 5) % numVert;
        }
        // Do bottom

        for (int i = 0; i < (numSeg * 4); i += 4) {
            geoIdx[j++] = (i + 3) % numVert;
            geoIdx[j++] = (i + 0) % numVert;
            geoIdx[j++] = (i + 4) % numVert;

            geoIdx[j++] = (i + 3) % numVert;
            geoIdx[j++] = (i + 4) % numVert;
            geoIdx[j++] = (i + 7) % numVert;
        }

        GeometryInfo gInfo = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);

        gInfo.setCoordinates(data);
        //gInfo.setStripCounts(strips);
        gInfo.setCoordinateIndices(geoIdx);

        // generate normals
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gInfo);
        // stripify
        Stripifier st = new Stripifier();
        st.stripify(gInfo);
        // GeometryArray result = gi.getGeometryArray();
        return gInfo;
        //return gInfo.getIndexedGeometryArray();
    }

}
