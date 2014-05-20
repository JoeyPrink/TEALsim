/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GeometryData.java,v 1.1 2010/04/12 19:57:53 stefan Exp $
 * 
 */

package teal.render.j3d.geometry;

/** 
 * Provides a generalized container for geometry, including 
 * Vertex, color, normals and texture info. This will be used
 * to construct the actual geometry specific to the target 
 * 3D environment.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.1 $
 */

public class GeometryData {

    protected final int type;
    protected int flags;
    protected float[] vertices;
    protected float[] normals;
    protected float[] color;
    protected float[] texture;

    public GeometryData(int geoType, float[] vert, float[] norm, float[] colors, float[] tex) {
        type = geoType;
        vertices = vert;
        normals = norm;
        color = colors;
        texture = tex;
    }

    public int getType() {
        return (type);
    }

    /**
     * Returns an array of vertex data, stored as floats.  Each triplet of floats represents one vertex.
     * 
     * @return float array of vertices.
     */
    public float[] getVertices() {
        return vertices;
    }

    /**
     * Sets the vertex array to the supplied array of floats.  Each consecutive triplet of floats should represent one vertex.
     * 
     * @param vert the new vertex array.
     */
    public void setVertices(float[] vert) {
        vertices = vert;
    }

    /**
     * Returns an array of normal data, stored as floats.  Each consecutive triplet of floats represents one normal.
     * 
     * @return float array of normals.
     */
    public float[] getNormals() {
        return normals;
    }

    /**
     * Sets the normal array to the supplied array of floats.  Each consecutive triplet of floats should represent one normal.
     * 
     * @param vert the new float array of normals.
     */
    public void setNormals(float[] vert) {
        normals = vert;
    }

    /**
     * Returns an array of color data, stored as floats.  Each consecutive triplet of floats represents one color 
     * (RGB, in float values 0.f to 1.f).
     * 
     * @return a float array of colors.
     */
    public float[] getColors() {
        return color;
    }

    /**
     * Sets the color array to the supplied array of floats.  Each consecutive triplet of floats should represent one 
     * color (RGB, in float values of 0.f to 1.f).
     * 
     * @param vert the new float array of colors.
     */
    public void setColors(float[] vert) {
        color = vert;
    }

    
    public float[] getTextureCoords() {
        return texture;
    }

    public void setTextureCoords(float[] vert) {
        texture = vert;
    }

}