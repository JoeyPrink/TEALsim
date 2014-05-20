/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FieldLine.java,v 1.112 2010/09/02 19:44:18 stefan Exp $ 
 * 
 */

package teal.sim.spatial;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.field.Field;


import teal.render.BoundingSphere;
import teal.render.Bounds;
import teal.render.TAbstractRendered;
import teal.render.TealMaterial;
import teal.render.scene.SceneFactory;
import teal.render.scene.TFieldLineNode;
import teal.render.scene.TNode3D;
import teal.sim.engine.TSimEngine;
import teal.util.TDebug;
import teal.visualization.dlic.Streamline;

/**
 * This class handles the calculation and display of field lines.  Field lines are drawn as lines beginning from
 * their "position" value, and following the the direction of the local field.  Note that there are many parameters
 * associated with this class and those that extend it, designed to optimize the rendering of field lines in any
 * given application.
 */

public class FieldLine extends SpatialField {

    private static final long serialVersionUID = 3546366145100919606L;
    
    public static final int BUILD_POSITIVE = 1;
    public static final int BUILD_NEGATIVE = 2;
    public static final int BUILD_BOTH = BUILD_POSITIVE | BUILD_NEGATIVE;
    public static final int EULER = 1;
    public static final int RUNGE_KUTTA = 2;

    public static final int COLOR_FLAT = 1;
    public static final int COLOR_VERTEX = 2;
    public static final int COLOR_VERTEX_FLAT = 3;

    /** the displayState of the line independent of the isDrawn variable */

    protected boolean checkBounds = false;

    protected int colorMode = COLOR_VERTEX_FLAT;
    protected Color3f flatColor;
    double colorScale = 1.0;
    float vred = 0.4f;
    float vgreen = 0.4f;
    float vblue = 1.0f;

    protected int integrationMode = EULER;
    /* The Maximum number of Steps allowed along a Field Line*/
    protected int kMax = 300;     /// changed from 200 jwb 7/15/2008
    protected boolean symmetryChanged = false;
    protected int numClones = 1;
    protected Vector3d symAxis = null;
    protected double sArc = 0.2;   /// changed from 0.1 jwb 7/15/2008
    //protected double hsArc = 0.10;
    // tolerance for Runge-Kutta fieldlines
    protected double rkTolerance = 1e-5;

    protected float minDistance = 0.2f;
    protected double pickRadius = Teal.FieldLinePickRadius;
    protected boolean showPick = false;
    protected transient float[] nData;
    protected transient float[] pData;
    protected int numP = 0;
    protected int numN = 0;
    protected boolean reachedStart;

    protected boolean showMarkers;
    protected int numMarkers = 0;
    protected Vector<Vector3d> markerPositions = null;

    protected int buildDir = BUILD_BOTH;

    protected Bounds modelBounds;

    /////////////////////////
    protected transient float[] nColors;
    protected transient float[] pColors;

    ////////////////////////

    public FieldLine() {
        super();
        nodeType = TAbstractRendered.NodeType.FIELD_LINE;
        setColor(Teal.DefaultEFieldLineColor);
        numClones = 1;
        symAxis = new Vector3d(0, 1, 0);
        isPickable = false;
        mNode = null;
        pData = new float[kMax * 3];
        nData = new float[kMax * 3];

        ////////////////////////
        pColors = new float[kMax * 3];
        nColors = new float[kMax * 3];

        //////////////////////////
        modelBounds = null;

    }

    public FieldLine(int fieldType) {
        this();
        ffType = fieldType;
        assignDefaultColor();

    }

    public FieldLine(Color lineColor) {
        this();
        setColor(lineColor);
    }

    public FieldLine(Vector3d pos) {
        //super();
        this();
        setPosition(pos);
    }

    public FieldLine(Vector3d pos, int fieldType) {
        this(pos);
        ffType = fieldType;
        assignDefaultColor();

    }

    public FieldLine(Vector3d pos, Field fld) {
        this(pos);
        this.field = fld;

    }

    public FieldLine(Field fld) {
        this();
        this.field = fld;

    }

    public void setSimEngine(TSimEngine model) {
        super.setSimEngine(model);
        if (model != null) modelBounds = ((TSimEngine)model).getBoundingArea();
    }

    /**
     * Sets the Fieldline coloring mode to parameter mode.  Currently the available modes are COLOR_FLAT, COLOR_VERTEX, 
     * and COLOR_VERTEX_FLAT.
     * 
     * COLOR_FLAT: sets the Fieldline to a flat color given by setColor().  The color is set by way of the J3D Appearance
     * property of the Fieldline node.
     * 
     * COLOR_VERTEX:  enables per-vertex coloring on the Fieldline, where the exact color at each vertex is proportional
     * to the magnitude of the field at that point.  The exact color is interpolated between white and black, with the 
     * characteristic color given by setVertexColor().  Note that per-vertex coloring requires sending an additional 
     * array of color information to the Fieldline Node, so is theortically more processor intensive, although the 
     * difference is probably negligable.
     * 
     * COLOR_VERTEX_FLAT: enables per-vertex coloring, but only applies a flat color to all the vertices.  The end 
     * result is identical to COLOR_FLAT.  The main reason this mode exists is to provide an easy toggle between a flat
     * color and a per-vertex color.  You can switch between COLOR_VERTEX and COLOR_VERTEX_FLAT without having to detach/
     * reattach nodes to the J3D scenegraph, which saves us from the annoying hassle of actually dealing with that process.  
     * 
     * @param mode
     */
    public void setColorMode(int mode) {
        colorMode = mode;
        renderFlags |= COLOR_CHANGE;
        needsSpatial();
    }

    /**
     * Returns the current colorMode of this Fieldline.  See setColorMode().
     */
    public int getColorMode() {
        return colorMode;
    }

    /**
     * Sets the characteristic color of the fieldline when using COLOR_VERTEX.  Vertex colored
     * fieldlines will interpolate (based on field magnitude) from white to this characteristic color
     * to black.  Parameters should be a value from 0.0f to 1.0f.
     * 
     * Default is "blueish". 
     * 
     * Set the interpolation scaling using setColorScale().
     * @param r
     * @param g
     * @param b
     */
    public void setVertexColor(float r, float g, float b) {
        vred = r;
        vgreen = g;
        vblue = b;
        needsSpatial();
    }

    /**
     * Returns the characteristic color used for interpolation when colorMode is set to COLOR_VERTEX
     * 
     */
    public float[] getVertexColor() {
        float[] vcolor = { vred, vgreen, vblue };
        return vcolor;
    }

    /**
     * Sets the symmetry properties of this Fieldline.  By supplying an axis (ax) and number (count), the Fieldline will
     * be "cloned" count number of times around the axis ax.  The spacing of the clones is automatically determined in 
     * order to fit them evenly around 360 degrees.
     * 
     * This feature is useful for situations where the Fieldlines in a scene are symmetric about some axis.  The
     * shape of the Fieldline is only calculated once, and then simply cloned and transformed about that axis, saving 
     * considerable processing cycles.
     * 
     * @param count
     * @param ax
     */
    public void setSymmetry(int count, Vector3d ax) {
        numClones = count;
        symAxis.set(ax);
        if (mNode != null) {
            symmetryChanged = true;
            if(theEngine != null)
                theEngine.requestRefresh();

        }
    }

    /**
     * Gets the number of clones about the symmetry axis.  See setSymmetry().
     */
    public int getSymmetryCount() {
        return numClones;
    }

    /**
     * Sets the number of clones displayed about the symmetry axis.  See setSymmetry().
     */
    public void setSymmetryCount(int count) {
        numClones = count;
        if (mNode != null) {
            symmetryChanged = true;
            if(theEngine != null)
                theEngine.requestRefresh();
        }

    }

    /**
     * Gets the symmetry axis for this Fieldline.  See setSymmetry().
     */
    public Vector3d getSymmetryAxis() {
        return new Vector3d(symAxis);
    }

    /**
     * Sets the symmetry axis for this Fieldline.  See setSymmetry().
     * 
     * @param ax
     */
    public void setSymmetryAxis(Vector3d ax) {
        symAxis.set(ax);
        if (mNode != null) {
            symmetryChanged = true;
            if(theEngine != null)
                theEngine.requestRefresh();
        }
    }

    /**
     * Sets the tolerance value used in the adaptive stepsize Runge-Kutta algorithm for calculating fieldlines.  This is
     * an important value, and should be set to something proportional to the scale on which you running the simulation 
     * (ie. approximately 1e-4 times the characteristic length scale of your simulation).  
     * 
     * If this value is set inappropriately, you will likely see slowdown associated with the drawing of fieldlines, as 
     * the algorithm struggles to find solutions within the prescribed tolerance.  Sometimes decreasing or increasing the
     * tolerance slightly will significantly improve performance.
     * 
     * @param tolerance
     */
    public void setRKTolerance(double tolerance) {
        rkTolerance = tolerance;
        /**
         * Returns the rkTolerance value of this Fieldline.  See setRKTolerance().
         * @return
         */
    }

    public double getRKTolerance() {
        return rkTolerance;
    }

    /* (non-Javadoc)
     * @see teal.sim.spatial.SpatialField#assignDefaultColor()
     */
    protected void assignDefaultColor() {
        if (ffType == Field.E_FIELD) {
            setColor(Teal.DefaultEFieldLineColor);
            Color3f col = new Color3f(Teal.DefaultEFieldLineColor);
            setVertexColor(col.x,col.y,col.z);
        } else if (ffType == Field.B_FIELD) {
            setColor(Teal.DefaultBFieldLineColor);
            setVertexColor(0.4f,0.4f,1.0f);
        } else if (ffType == Field.P_FIELD) {
            setColor(Teal.DefaultPFieldColor);
        }
        //else if (ffType == Field.EP_FIELD)
        //{
        //	mColor = Teal.DefaultEPotentialFieldColor;
        //}
    }

    public void setPosition(Vector3d start, boolean sendPC) {
        position = start;
        renderFlags |= POSITION_CHANGE;
    }

    public boolean getCheckBounds() {
        return checkBounds;
    }

    public void setCheckBounds(boolean b) {
        checkBounds = b;
    }

    public void setColor(Color color) {
        super.setColor(color);
        flatColor = new Color3f(color);
        renderFlags |= COLOR_CHANGE;
    }

    public void setPickable(boolean b) {
        setShowPick(b);
        super.setPickable(b);
    }

    public Bounds getBoundingArea() {
        return new BoundingSphere(new Point3d(position), pickRadius);
    }

    public double getPickRadius() {
        return pickRadius;
    }

    public void setPickRadius(double r) {
        pickRadius = r;
        if (mNode != null) {

            ((TFieldLineNode) mNode).setPickRadius(pickRadius);
            ((TFieldLineNode) mNode).setPickVisible(showPick);

        }
    }

    public boolean getShowPick() {
        return showPick;
    }

    public void setShowPick(boolean b) {
        TDebug.println(2, "Setting showPick: " + b + " radius = " + pickRadius);

        showPick = b;
        if (mNode != null) {
            ((TFieldLineNode) mNode).setPickVisible(b);
        }
        if(theEngine != null)
            theEngine.requestRefresh();
    }

    public int getMarkerCount() {
        return numMarkers;
    }

    public void setMarkerCount(int count) {
        numMarkers = count;
        checkMarkers();
    }

    public boolean getShowMarkers() {
        return showMarkers;
    }

    public void setShowMarkers(boolean b) {
        setMarkersVisible(b);
        showMarkers = b;
    }

    protected void setMarkersVisible(boolean b) {
        if (mNode != null) {
            for (int i = 0; i < numMarkers; i++) {
                ((TFieldLineNode) mNode).setMarkerVisible(i, b);
            }
            registerRenderFlag(VISIBILITY_CHANGE);
        }
    }

    /**
     * Sets the value of buildDir for this Fieldline.  The buildDir value describes the direction in which this Fieldline
     * will be built, starting at its position.  The possible values are BUILD_POSITIVE, BUILD_NEGATIVE, or BUILD_BOTH.
     * As the names suggest, BUILD_POSITIVE builds the line in the positive direction (along the direction of the field),
     * BUILD_NEGATIVE builds the line in the negative direction (against the direction of the field), and BUILD_BOTH
     * builds in both directions.
     * 
     * @param dir
     */
    public void setBuildDir(int dir) {
        buildDir = dir;
    }

    /**
     * Returns the value of buildDir.  See setBuildDir().
     */
    public int getBuildDir() {
        return buildDir;
    }

    public void nextSpatial() {
        //TDebug.println(0,getID() + ": nextSpatial");

        if (isDrawn) {
            if (mNode == null) mNode = makeNode();
            buildLines();
            if (showMarkers && (numMarkers > 0)) {
                checkMarkers();
                calculateMarkers();
            }
            renderFlags |= GEOMETRY_CHANGE;
        }

    }

    public void render() {
        if (mNode == null) return;
        if (mNeedsSpatial) {
        	if(field == null)
        		return;
//            nextSpatial(); //TODO: check that
            mNeedsSpatial = false;
        }
        if (symmetryChanged) {

            ((TFieldLineNode) mNode).setSymmetry(numClones, symAxis);
            symmetryChanged = false;
        }
        if ((renderFlags & VISIBILITY_CHANGE) == VISIBILITY_CHANGE) {
            mNode.setVisible(showNode);
            renderFlags ^= VISIBILITY_CHANGE;
        }

        if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {
            setLineGeometry((TFieldLineNode) mNode);
            updateMarkers();
            renderFlags ^= GEOMETRY_CHANGE;

        }
        super.render();
    }
    
    protected void checkColorData(int buildFlags){
        if( (buildFlags & FieldLine.BUILD_NEGATIVE) != 0){
            if( (nColors == null) || (nColors.length < kMax * 3)){
                nColors = new float[kMax * 3];
            }
        }
        if( (buildFlags & FieldLine.BUILD_POSITIVE) != 0){
            if((pColors == null) || (pColors.length < kMax * 3)){
                pColors = new float[kMax * 3];
            }
        }
    }

    /**
     * This method determines which geometry should be built, and calls the appropriate build methods.
     */
    protected void buildLines() {
        reachedStart = false;
    	if(nData == null)
    		nData = new float[3*kMax];
    	if(nColors == null)
    		nColors = new float[3*kMax];
    	if(pData == null)
    		pData = new float[3*kMax];
    	if(pColors == null)
    		pColors = new float[3*kMax];
        if (colorMode == COLOR_VERTEX || colorMode == COLOR_VERTEX_FLAT) {
            checkColorData(buildDir);
            if (buildDir == FieldLine.BUILD_BOTH) {
                // Default, build in both directions.            	
                numN = buildLineData(nData, nColors, -1);

                if (reachedStart != true) {
                    numP = buildLineData(pData, pColors, 1);
                } else numP = 0;
            } else if (buildDir == FieldLine.BUILD_NEGATIVE) {
                // build in "negative" direction
                numN = buildLineData(nData, nColors, -1);
                numP = 0;
            } else if (buildDir == FieldLine.BUILD_POSITIVE) {
                // build in "positive" direction
                numN = 0;
                numP = buildLineData(pData, pColors, 1);
            }
        } else {
            if (buildDir == FieldLine.BUILD_BOTH) {
                // Default, build in both directions.
                numN = buildLineData(nData, -1);

                if (reachedStart != true) {
                    numP = buildLineData(pData, 1);
                } else numP = 0;
            } else if (buildDir == FieldLine.BUILD_NEGATIVE) {
                // build in "negative" direction
                numN = buildLineData(nData, -1);
                numP = 0;
            } else if (buildDir == FieldLine.BUILD_POSITIVE) {
                // build in "positive" direction
                numN = 0;
                numP = buildLineData(pData, 1);
            }

        }
        renderFlags |= GEOMETRY_CHANGE;
    }

    protected TNode3D makeNode() {
    	TNode3D node = SceneFactory.makeNode(this);
//        FieldLineNode node = new FieldLineNode(this, numClones, symAxis);
//        node.setPosition(getPosition());
//
//        node.setPickable(isPickable);
//        if(colorMode != FieldLine.COLOR_FLAT){
//            checkColorData(buildDir);
//        }
//            
//        /*
//        if (showPick) {
//             node.setPickGeometry(Sphere.makeGeometry(pickRadius));
//        }
//         */
//        if (showMarkers && (numMarkers > 0)) {
//            node.checkMarkers(numMarkers);
//            setMarkersVisible(showMarkers);
//        }
//        node.setColor(TealMaterial.getColor3f(mMaterial.getDiffuse()));
        showNode = true;
        renderFlags |= (GEOMETRY_CHANGE | COLOR_CHANGE | VISIBILITY_CHANGE);
        return node;
    }

    protected void checkMarkers() {
        if (mNode != null) {
            ((TFieldLineNode) mNode).checkMarkers(numMarkers);
        }
    }

    protected void calculateMarkers() {
        int total = 0;
        int dif = 0;
        int i = 0;
        int idx = 0;
        if ((numN > 0) && (numP > 0)) {
            markerPositions = new Vector<Vector3d>();
            total = numN + numP;
            dif = total / (numMarkers);
            for (i = 0; i < numN; i += dif) {
                idx = i * 3;
                Vector3d pos = new Vector3d(nData[idx++], nData[idx++], nData[idx]);
                markerPositions.add(pos);
            }

            for (i = 0; i < numP; i += dif) {
                idx = i * 3;
                Vector3d pos = new Vector3d(pData[idx++], pData[idx++], pData[idx]);
                markerPositions.add(pos);
            }
        } else if (numN > 0) {
            markerPositions = new Vector<Vector3d>();
            total = numN;
            dif = total / (numMarkers);
            for (i = 0; i < (numN); i += dif) {
                idx = i * 3;
                Vector3d pos = new Vector3d(nData[idx++], nData[idx++], nData[idx]);
                markerPositions.add(pos);
            }
        } else if (numP > 0) {
            markerPositions = new Vector<Vector3d>();
            total = numP;
            dif = total / (numMarkers);

            for (i = 0; i < numP; i += dif) {
                idx = i * 3;
                Vector3d pos = new Vector3d(pData[idx++], pData[idx++], pData[idx]);
                markerPositions.add(pos);
            }
        } else {
            markerPositions = null;
            setMarkersVisible(false);
        }
        renderFlags |= GEOMETRY_CHANGE;
        if (markerPositions != null) {
            if (TDebug.getGlobalLevel() >= 2) {
                TDebug.print(getID() + " Markers: ");
                for (i = 0; i < markerPositions.size(); i++) {
                    TDebug.println(" \t" + markerPositions.get(i));
                }

            }
        }
    }

    protected void updateMarkers() {
        if (markerPositions != null) {
            Iterator<Vector3d> it = markerPositions.iterator();
            int i = 0;
            while (it.hasNext() && i < numMarkers) {
                Vector3d pos = it.next();
                ((TFieldLineNode) mNode).setMarkerValues(i, pos, field.get(pos));
                i++;
            }
        }

    }

    protected void setLineGeometry(TFieldLineNode node) {
        if (colorMode == COLOR_VERTEX || colorMode == COLOR_VERTEX_FLAT) {
            node.setLineGeometry(numN, nData, nColors, numP, pData, pColors);
        } else {
            node.setLineGeometry(numN, nData, numP, pData);
        }

    }

    /**
     * This version of buildLineData() calculates the fieldline geometry for any of the vertex coloring modes.
     * @param data vertex array
     * @param colors color array
     * @param direction direction to build (ie. forward or backwards along the field)
     * @return the number of steps completed in building the line
     */
    protected int buildLineData(float[] data, float[] colors, int direction) {
        Streamline sline = null;
        reachedStart = false;
        int k = 0;

        //long millisStart = System.currentTimeMillis();

        try {
            int off = 0;
            int coloroff = 0;
            if (field == null) {
                field = assignField();
            }

            if (field != null) {

                double dsArc = Math.abs(sArc);
                double signDsArc = dsArc * direction;
                double sA = sArc;

                int numLoops = 0;
                //double accuracy = 1.e-3;

                boolean loop = true;
                Vector3d startPoint = new Vector3d(getPosition());
                Vector3d curPoint = new Vector3d(startPoint);
                Vector3d nextPoint = new Vector3d();
                Vector3d dX;
                //Vector3d dXStart;

                ////////////
                double fieldmag = 0.;
                ////////////

                if (integrationMode == RUNGE_KUTTA) {
                    sline = new Streamline(field, dsArc, 0.01 * dsArc, 10. * dsArc, dsArc * rkTolerance); //1e-5);
                    sline.setLength(direction * Math.abs(dsArc * kMax));
                    sline.setStart(curPoint);
                }

                if ((data == null) || (data.length < 3 * kMax)) data = new float[3 * kMax];

                data[off++] = (float) curPoint.x;
                data[off++] = (float) curPoint.y;
                data[off++] = (float) curPoint.z;

                /////////////////////////////
                if ((colors == null) || (colors.length < 3 * kMax)) colors = new float[3 * kMax];
                colors[coloroff++] = 1.0f;
                colors[coloroff++] = 1.0f;
                colors[coloroff++] = 1.0f;

                ////////////////////////////
                k++;

                //dXStart = field.deltaField(curPoint, sA);
                while (k < kMax && loop) {
                    // Compute and add next point
                    if (integrationMode == RUNGE_KUTTA) {
                        curPoint = sline.nextVecFL();
                        /////////////////////
                        fieldmag = sline.getLastFieldValue().length();
                        /////////////////////
                        if (curPoint == null) break;
                    } else {
                        dX = field.deltaField(curPoint, sA, true);
                        fieldmag = dX.length();
                        //System.out.println("FL dX = " + dX);
                        if (fieldmag != 0) dX.normalize();
                        nextPoint.add(curPoint, dX);
                        curPoint.interpolate(nextPoint, signDsArc);
                    }
                    //System.out.println("FL k = " + k + " curPoint = " + curPoint);
                    data[off++] = (float) curPoint.x;
                    data[off++] = (float) curPoint.y;
                    data[off++] = (float) curPoint.z;

                    if (colorMode == COLOR_VERTEX_FLAT) {
                        //System.out.println("Color: " + flatColor);
                        colors[coloroff++] = flatColor.x;
                        colors[coloroff++] = flatColor.y;
                        colors[coloroff++] = flatColor.z;
                    } else {
                        float x = (float) (fieldmag / colorScale);
                        //colors[coloroff++] = (float)(k%2);
                        //colors[coloroff++] = (float)(k%2);
                        //colors[coloroff++] = (float)(k%2);
                        float scale = (float) Math.log(2. * Math.log(x + 1.) + 1.);
                        colors[coloroff++] = vred * scale;
                        colors[coloroff++] = vgreen * scale;
                        colors[coloroff++] = vblue * scale;
                        ////////////////////////////////////
                    }
                    // Closed line check
                    if ((k > 10) && (curPoint.epsilonEquals(startPoint, minDistance))) {
                        loop = false;
                        //TDebug.println(3,"\tminDistance at k=" + k + "  point: " + curPoint);
                        reachedStart = true;
                        //TDebug.println(2,"reached start");
                    }
                    // In Bounds check
                    if (checkBounds) {
                        if (modelBounds != null) {
                            if (!modelBounds.intersect(new Point3d(curPoint))) {
                                loop = false;
                                //TDebug.println(3,"\tBounds hit at k=" + k + "  point: " + data);
                            }
                        }
                    }
                    //sA += dsArc;
                    sA += signDsArc;

                    // Check if line intersects an object
                    /* 
                     Iterator it = theEngine.getPhysicalObjs().iterator();
                     while(it.hasNext() && loop) {
                     
                     //if (pos.distance(new Point3f(((PhysicalElement)it.next()).getPosition())) < .75*dsArc)
                     PhysicalElement pObj = (PhysicalElement) it.next();
                     if((!(pObj instanceof RingOfCurrent)) && (!(pObj instanceof InfiniteWire))) {
                     if (pos.distance(new Point3f(pObj.getPosition())) < .75*dsArc) {
                     loop = false;
                     }
                     }
                     }
                     
                     */

                    //	                Iterator it = theEngine.getPhysicalObjs().iterator();
                    //                    while(it.hasNext() && loop) {
                    //
                    //                        //if (pos.distance(new Point3f(((PhysicalElement)it.next()).getPosition())) < .75*dsArc)
                    //                        PhysicalObject pObj = (PhysicalObject) it.next();
                    //                        if((!(pObj instanceof RingOfCurrent)) && (!(pObj instanceof InfiniteWire))) {
                    //                            if (curPoint.epsilonEquals(new Vector3d(pObj.getPosition()),2.75*dsArc)) {
                    //                                loop = false;
                    //                            }
                    //                        }
                    //                    }

                    if (numLoops > 1) loop = false;
                    k++;
                }
            }
            //long millisEnd = System.currentTimeMillis();
            //System.out.println("Fieldline buildLines time = " + (millisEnd - millisStart));
            //TDebug.println(0,"FLine num points = " + k);
        } catch (ArithmeticException ae) {
            TDebug.printThrown(ae);
        }
        return k;

    }

    /**
     * This version of buildLineData() calculates fieldline geometry with only vertex data (no per-vertex color data).
     * @param data vertex array
     * @param direction direction along field in which to build
     * @return number of steps completed in building the line
     */
    protected int buildLineData(float[] data, int direction) {
        Streamline sline = null;
        reachedStart = false;
        int k = 0;

        try {
            int off = 0;
            //int coloroff = 0;
            if (field == null) {
                field = assignField();
            }

            if (field != null) {

                double dsArc = Math.abs(sArc);
                double signDsArc = dsArc * direction;
                double sA = sArc;

                int numLoops = 0;
                //double accuracy = 1.e-3;

                boolean loop = true;
                Vector3d startPoint = new Vector3d(getPosition());
                Vector3d curPoint = new Vector3d(startPoint);
                Vector3d nextPoint = new Vector3d();
                Vector3d dX;
                //Vector3d dXStart;

                ////////////
                //double fieldmag = 0.;
                ////////////

                if (integrationMode == RUNGE_KUTTA) {
                    sline = new Streamline(field, dsArc, 0.01 * dsArc, 10. * dsArc, dsArc * rkTolerance); //1e-8);
                    sline.setLength(direction * Math.abs(dsArc * kMax));
                    sline.setStart(curPoint);
                }

                if ((data == null) || (data.length < 3 * kMax)) data = new float[3 * kMax];

                data[off++] = (float) curPoint.x;
                data[off++] = (float) curPoint.y;
                data[off++] = (float) curPoint.z;

                /////////////////////////////
                //if ((colors == null) || ( colors.length < 3*kMax))
                //	colors = new float[3*kMax];
                //colors[coloroff++] = 1.0f;
                //colors[coloroff++] = 1.0f;
                //colors[coloroff++] = 1.0f;
                //
                ////////////////////////////
                k++;

                //dXStart = field.deltaField(curPoint, sA);
                while (k < kMax && loop) {
                    // Compute and add next point
                    if (integrationMode == RUNGE_KUTTA) {
                        curPoint = sline.nextVecFL();
                        /////////////////////
                        //fieldmag = sline.getLastFieldValue().length();
                        /////////////////////
                        if (curPoint == null) break;
                    } else {
                        dX = field.deltaField(curPoint, sA);
                        nextPoint.add(curPoint, dX);
                        curPoint.interpolate(nextPoint, signDsArc);
                    }
                    data[off++] = (float) curPoint.x;
                    data[off++] = (float) curPoint.y;
                    data[off++] = (float) curPoint.z;

                    ////////////////////////////////////
                    // float red = 0.4f;
                    //float green = 0.4f;
                    //float blue = 1.0f;
                    //float x = (float) fieldmag/1.f;
                    //colors[coloroff++] = 0.4f + (float) (Math.log(fieldmag / 100.));
                    //colors[coloroff++] = 0.4f + (float) (Math.log(fieldmag / 100.));
                    //colors[coloroff++] = 1.0f;
                    //colors[coloroff++] = red*(float)Math.log(2.*Math.log(x+1.)+1.);
                    //colors[coloroff++] = green*(float)Math.log(2.*Math.log(x+1.)+1.);
                    //colors[coloroff++] = blue*(float)Math.log(2.*Math.log(x+1.)+1.);
                    ////////////////////////////////////

                    // testing something
                    //if(curPoint.y <= 0.  && k > 10) loop = false;

                    // Closed line check
                    if ((k > 10) && (curPoint.epsilonEquals(startPoint, minDistance))) {
                        loop = false;
                        //TDebug.println("\tminDistance at k=" + k + "  point: " + curPoint);
                        reachedStart = true;
                        //TDebug.println(2,"reached start");
                    }
                    // In Bounds check
                    if (checkBounds) {
                        if (modelBounds != null) {
                            if (!modelBounds.intersect(new Point3d(curPoint))) {
                                loop = false;
                                //TDebug.println("\tBounds hit at k=" + k + "  point: " + data);
                            }
                        }
                    }
                    //sA += dsArc;
                    sA += signDsArc;

                    // Check if line intersects an object
                    /*
                     Iterator it = theEngine.getPhysicalObjs().iterator();
                     while(it.hasNext() && loop) {

                     //if (pos.distance(new Point3f(((PhysicalElement)it.next()).getPosition())) < .75*dsArc)
                     PhysicalObject pObj = (PhysicalObject) it.next();
                     if((!(pObj instanceof RingOfCurrent)) && (!(pObj instanceof InfiniteWire))) {
                     if (curPoint.epsilonEquals(new Vector3d(pObj.getPosition()),.75*dsArc)) {
                     loop = false;
                     }
                     }
                     }
                     */

                    if (numLoops > 1) loop = false;
                    k++;
                }
            }
            //TDebug.println(0,"FLine num points = " + k);
        } catch (ArithmeticException ae) {
            TDebug.printThrown(ae);
        }
        return k;

    }

    public boolean checkProximityToCenter(double radius, Vector3d center, Vector3d prevPoint, Vector3d presPoint) {
        if ((prevPoint.y - center.y) * (presPoint.y - center.y) < 0) {
            TDebug.println(3, "Cross");
            double r1 = ((center.x - prevPoint.x) * (center.x - prevPoint.x) + (center.y - prevPoint.y)
                * (center.y - prevPoint.y));
            double r2 = ((center.x - presPoint.x) * (center.x - presPoint.x) + (center.y - presPoint.y)
                * (center.y - presPoint.y));
            double r = radius * radius;
            if (r1 < r && r2 < r) {

                return true;
            }
        }

        return false;
    }

    public boolean checkProximityToCenter(double radius, double[] center, double[] prevPoint, double[] presPoint) {
        if ((prevPoint[1] - center[1]) * (presPoint[1] - center[1]) < 0) {
            TDebug.println(3, "Cross");
            double r1 = ((center[0] - prevPoint[0]) * (center[0] - prevPoint[0]) + (center[1] - prevPoint[1])
                * (center[1] - prevPoint[1]));
            double r2 = ((center[0] - presPoint[0]) * (center[0] - presPoint[0]) + (center[1] - presPoint[1])
                * (center[1] - presPoint[1]));
            double r = radius * radius;
            if (r1 < r && r2 < r) {

                return true;
            }
        }

        return false;
    }

    public void update() {
        //mShape = shape_d;
    }

    /**
     * Sets the value of minDistance to parameter d.  The value of minDistance defines a condition under which the 
     * building of a fieldline is terminated.  If, in the course of building, the latest point on a fieldline comes
     * within minDistance of its start point, the line will terminate.  This handles the case of closed fieldlines 
     * (ie. almost all magnetic fieldlines), and prevents them (usually) from wrapping around themselves repeatedly.
     * 
     * @param d
     */
    public void setMinDistance(double d) {
        minDistance = (float) d;
    }

    /**
     * See setMinDistance(double d).
     * 
     * @param min
     */
    public void setMinDistance(float min) {
        minDistance = min;
    }

    /**
     * Returns the value of minDistance.  See setMinDistance().
     * 
     * @return minDistance
     */
    public float getMinDistance() {
        return minDistance;
    }

    /**
     * Retuns the value of kMax.  See setKMax().
     * @return kMax
     */
    public int getKMax() {
        return kMax;
    }

    /**
     * Sets the value of kMax for this Fieldline.  kMax is the maximum number of points that will be built along this
     * fieldline.
     * 
     * @param k
     */
    public void setKMax(int k) {

        if ((nData == null) || (nData.length < 3 * k)) nData = new float[3 * k];
        if ((pData == null) || (pData.length < 3 * k)) pData = new float[3 * k];
        if ((nColors == null) || (nColors.length < 3 * k)) nColors = new float[3 * k];
        if ((pColors == null) || (pColors.length < 3 * k)) pColors = new float[3 * k];
        kMax = k;
    }

    public double getSArc() {
        return sArc;
    }

    /**
     * Sets the value of sArc for this Fieldline.  sArc is the default length of each segment of this Fieldline.  Note
     * that if the Fieldline is in Runge-Kutta mode, the actually length of each segment will be modified to satisfy 
     * the adaptive stepsize routine.
     * 
     * @param s
     */
    public void setSArc(double s) {
        sArc = s;
    }

    /*	
     public double getHsArc()
     {
     return  hsArc;
     }
     
     public void setHsArc(double h)
     {
     hsArc = h;
     }
     
     */
    public double getIntegrationMode() {
        return integrationMode;
    }

    /**
     * Sets the integrationMode for this fieldline.  Currently, the two modes are EULER and RUNGE_KUTTA.  
     * 
     * EULER:  uses a simple Euler integration scheme for calculating fieldlines.  Step sizes are fixed at length sArc.
     * This is basically a "quick and dirty" mode.
     * 
     * RUNGE_KUTTA:  uses a fourth order Runge-Kutta integration scheme with adaptive stepsize control.  Steps sizes vary
     * according to the topography of the fieldline and the value of rkTolerance.  This mode is considerably more 
     * accurate than EULER, but also more expensive.
     * 
     * @param mode
     */
    public void setIntegrationMode(int mode) {
        integrationMode = mode;
    }

    /**
     * @return Returns the colorScale.
     */
    public double getColorScale() {
        return colorScale;
    }

    /**
     * @param colorScale The colorScale to set.
     */
    public void setColorScale(double colorScale) {
        this.colorScale = colorScale;
    }
    
//    private void readObject(java.io.ObjectInputStream s)
//    throws java.io.IOException, ClassNotFoundException {
//    	// Read in any hidden stuff
//    	s.defaultReadObject();
//
//    	pData = new float[kMax * 3];
//        nData = new float[kMax * 3];
//
//        pColors = new float[kMax * 3];
//        nColors = new float[kMax * 3];
//
//    }
}
