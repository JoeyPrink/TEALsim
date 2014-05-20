/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: AxisShape.java,v 1.2 2010/06/07 22:00:31 pbailey Exp $
 * 
 */

package teal.render.j3d.geometry;

import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;

/** 
 * A geometry generation utility class that lofts a polygon by rotating it 
 * around 0,0,0; y values are not changed. Note all x values should be >= zero.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.2 $
 */
public class AxisShape {

    public final static int TEXTURE = 0x1;
    public final static int COLORS = 0x2;
    public final static int NORMALS = 0x4;

    private boolean doTexture = false;
    private boolean doColors = true;
    private boolean calcNormals = true;
    private double creaseAngle = Math.PI / 4.; //Math.PI/4.0;

    public AxisShape() {
    }

    public AxisShape(int flags) {
        doTexture = ((flags | TEXTURE) == TEXTURE);
        doColors = ((flags | COLORS) == COLORS);
        calcNormals = ((flags | NORMALS) == NORMALS);

    }

    public void setCreaseAngle(double ang) {
        creaseAngle = ang;
    }

    public double getCreaseAngle() {
        return creaseAngle;
    }

    public GeometryInfo makeGeometry(Point2f[] pts, int segments) {
        return makeGeometry(pts, segments, new Point2f(0f, 0f), 1, 1, 2.*Math.PI, 0.);
    }

    public GeometryInfo makeGeometry(Point2f[] pts, int segments, Point2f center) {
        return makeGeometry(pts, segments, center, 1, 1, 2.*Math.PI, 0.);
    }
    
    public GeometryInfo makeGeometry(Point2f[] pts, int segments, Point2f center, double latheAngle, double latheAngleOffset) {
        return makeGeometry(pts, segments, center, 1, 1, latheAngle, latheAngleOffset);
    }

    /** this constructs a geometry of indexed Quads swept around 
     * the Y axis. The specified points are used to define the Y components 
     * of the quads the number of segments are used to define the 
     * X component of the Quads. By default texture-mapping coordinates
     * are defined for the verticies wrapping in a right-hand mode.
     */

    public GeometryInfo makeGeometry(Point2f[] pts, int segments, Point2f center, int xTiles, int yTiles, double latheAngle, double latheAngleOffset) {
        // 10 segments reguire eleven points to define the segments.
        int divs = segments + 1;
        // The Y points specify all points, number of Y Quads = points -1.
        int numY = pts.length;
        int xTex = 0;
        int yTex = 0;
        float xdt = 1f;
        float ydt = 1f;

        Point3f vertexCoords[] = null;
        int vertexIndices[] = null;

        Vector3f[] normalCoords = null;
        TexCoord2f[] textureCoords = null;
        int textureIndices[] = null;
        int numberOfVertices = (divs) * numY;
        int numberOfIdx = 4 * (segments) * (numY - 1);

        Vector3f centerPoint = new Vector3f();
        //TDebug.println(0,"Segs: " + segments + " divs: " + divs + " numY: " + numY + " #vert: " + numberOfVertices + " #idx: " + numberOfIdx);

        vertexCoords = new Point3f[numberOfVertices];
        vertexIndices = new int[numberOfIdx];

        if (calcNormals) {

            //centerPoint = new Vector3f();
            normalCoords = new Vector3f[numberOfVertices];
        }
        if (doColors) {
        }
        if (doTexture) {

            textureIndices = new int[numberOfIdx];
            //TDebug.println(1,"numTex indices = " + textureIndices.length);

            xTex = ((divs) / xTiles);
            yTex = numY / yTiles;
            xdt = xTiles / (float) (xTex - 1);
            ydt = yTiles / (float) (yTex - 1);
            //xdt = xTiles/(float) divs;
            //ydt = yTiles/(float) numY;
            //TDebug.println("xTile =" + xTiles + " xdt = " + xdt + " yTile =" + yTiles + " ydt = " + ydt);
            textureCoords = new TexCoord2f[numberOfVertices];
            //TDebug.println(1,"numTex Coords = " + textureCoords.length);

        }
        //double rad = Math.PI;
        //double dRad = -2.0 * Math.PI / (segments);
        double rad = Math.PI + latheAngleOffset;
        //System.out.println("rad = " + rad);
        double dRad = -latheAngle / (segments);
        int vIdx = 0;
        for (int j = 0; j < numY; j++) { // for the Y points
            rad = Math.PI + latheAngleOffset;
            for (int k = 0; k < divs; k++) { //each segment
                Point3f vertex = new Point3f();
                vertex.x = (float) (pts[j].x * Math.cos(rad));
                vertex.y = pts[j].y;
                vertex.z = (float) (pts[j].x * Math.sin(rad));
                vertexCoords[vIdx] = vertex;
                if (calcNormals) {

                    centerPoint.x = (float) (center.x * Math.cos(rad));
                    centerPoint.y = center.y;
                    centerPoint.z = (float) (center.x * Math.sin(rad));
                    Vector3f normal = new Vector3f(vertex);
                    normal.sub(centerPoint);
                    //Vector3f normal=new Vector3f(centerPoint);
                    //normal.sub(vertex);
                    normal.normalize();
                    normalCoords[vIdx] = normal;
                }
                if (doTexture) {
                    textureCoords[vIdx] = new TexCoord2f(k * xdt, j * ydt);
                    //TDebug.println(2,"Tx " + vIdx + " = " + textureCoords[vIdx]);
                }
                vIdx++;
                rad += dRad;
            }
        }

        int off = 0;
        int vec = 0;
        int k = 0;
        // Assign the vertexIndex
        for (int j = 0; j < (numY - 1); j++) {
            k = 0;
            for (; k < segments; k++) {
                //TDebug.print(2,k+" x "+j + " \t= " + vec +", ");			
                vertexIndices[off++] = vec;
                //TDebug.print(2,(vec + divs) + ", ");			
                vertexIndices[off++] = (vec++ + divs);
                //TDebug.print(2,((vec + divs)) + ", ");			
                vertexIndices[off++] = (vec + divs);
                //TDebug.println(2,(vec));             
                vertexIndices[off++] = vec;
            }
            /*
             
             //Last quad per line
             TDebug.print(k+" x "+j + " \t= " + vec +", ");
             vertexIndices[off++]= vec;
             TDebug.print((vec + divs) + ", ");
             vertexIndices[off++]= (vec++ + divs);
             
             TDebug.print((vec ) + ", ");
             vertexIndices[off++]= vec  ;    
             TDebug.println(vec - segments);
             vertexIndices[off++]= vec - segments;
             */
            vec++;
        }
        if (doTexture) {
            off = 0;
            vec = 0;
            for (int j = 0; j < (numY - 1); j++) {
                for (k = 0; k < segments; k++) {
                    //TDebug.print(2,"Tex: " + k+" x "+j + " \t= " + vec +", ");
                    textureIndices[off++] = vec;

                    //TDebug.print(2,(vec + divs) + ", ");
                    textureIndices[off++] = (vec++ + (divs));

                    //TDebug.print(2,((vec + divs)) + ", ");
                    textureIndices[off++] = (vec + (divs));

                    //TDebug.println(2,(vec));
                    textureIndices[off++] = vec;

                }
                vec++;

            }

        }

        GeometryInfo gInfo = new GeometryInfo(GeometryInfo.QUAD_ARRAY);

        gInfo.setCoordinates(vertexCoords);
        gInfo.setCoordinateIndices(vertexIndices);
        if (doTexture) {
            gInfo.setTextureCoordinateParams(1, 2);
            gInfo.setTextureCoordinates(0, textureCoords);
            gInfo.setTextureCoordinateIndices(0, textureIndices);
        }
        //gInfo.setColors(colors);

        if (calcNormals) {
            gInfo.setNormals(normalCoords);
            gInfo.setNormalIndices(vertexIndices);
        }
        gInfo.indexify();
        gInfo.compact();

        if (!calcNormals) {
            // generate normals
            NormalGenerator ng = new NormalGenerator();
            ng.setCreaseAngle(creaseAngle);
            ng.generateNormals(gInfo);
        }
        return gInfo;

    }

    public static GeometryInfo getGeometry(Point2f[] pts, int divs, Point2f center) {
        int numberOfVertices = (divs + 1) * pts.length;
        Point3f vertexCoords[] = new Point3f[numberOfVertices];
        Vector3f normalCoords[] = new Vector3f[numberOfVertices];
        Color3f colors[] = new Color3f[numberOfVertices];
        double rad = 0.;
        for (int k = 0; k < (divs + 1); k++) {
            for (int j = 0; j < pts.length; j++) {
                rad = 2 * k * Math.PI / divs;
                Point3f vertex = new Point3f();
                vertex.x = (float) (pts[j].x * Math.cos(rad));
                vertex.y = pts[j].y;
                vertex.z = (float) (pts[j].x * Math.sin(rad));

                Vector3f centerPoint = new Vector3f();
                centerPoint.x = (float) (center.x * Math.cos(rad));
                centerPoint.y = center.y;
                centerPoint.z = (float) (center.x * Math.sin(rad));
                Vector3f normal = new Vector3f(vertex);
                normal.sub(centerPoint);
                normal.normalize();
                vertexCoords[j * (divs + 1) + k] = vertex;
                normalCoords[j * (divs + 1) + k] = normal;
                colors[j * (divs + 1) + k] = new Color3f((float) (Math.random()), (float) (Math.random()),
                    (float) (Math.random()));
            }
        }
        int vertexIndices[] = new int[4 * divs * (pts.length - 1)];
        int textureIndices[] = new int[4 * divs * (pts.length - 1)];
        for (int k = 0; k < (divs); k++) {
            for (int j = 0; j < (pts.length - 1); j++) {
                vertexIndices[4 * (j * divs + k) + 3] = (divs + 1) * j + k;
                vertexIndices[4 * (j * divs + k) + 2] = (divs + 1) * j + k + 1;
                vertexIndices[4 * (j * divs + k) + 1] = (divs + 1) * (j + 1) + k + 1;
                vertexIndices[4 * (j * divs + k) + 0] = (divs + 1) * (j + 1) + k;
                textureIndices[4 * (j * divs + k) + 3] = 0;
                textureIndices[4 * (j * divs + k) + 2] = 1;
                textureIndices[4 * (j * divs + k) + 1] = 2;
                textureIndices[4 * (j * divs + k) + 0] = 3;
            }
        }
        TexCoord2f[] textureCoords = { new TexCoord2f(1.0f, 1.0f), new TexCoord2f(0.0f, 1.0f),
                new TexCoord2f(0.0f, 0.0f), new TexCoord2f(1.0f, 0.0f) };
        GeometryInfo gInfo = new GeometryInfo(GeometryInfo.QUAD_ARRAY);

        gInfo.setCoordinates(vertexCoords);
        gInfo.setCoordinateIndices(vertexIndices);
        gInfo.setTextureCoordinateParams(1, 2);
        gInfo.setTextureCoordinates(0, textureCoords);
        gInfo.setTextureCoordinateIndices(0, textureIndices);
        gInfo.setNormals(normalCoords);
        gInfo.setNormalIndices(vertexIndices);
        gInfo.convertToIndexedTriangles();
        gInfo.compact();
        return gInfo;
    }

}
