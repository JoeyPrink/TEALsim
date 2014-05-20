/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TrailVisualization.java,v 1.11 2010/04/12 20:13:18 stefan Exp $ 
 * 
 */

package teal.sim.spatial;

import java.awt.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.config.*;
import teal.render.*;
import teal.render.j3d.*;
import teal.render.j3d.geometry.Cylinder;
import teal.render.scene.*;

public class TrailVisualization extends Spatial {

    private static final long serialVersionUID = 3904676089577420080L;
    private int numberOfSegments = 32;
    private double segmentLength = 0.5;
    private double segmentRadius = 0.05;
    private int currentSegment = 0;
    private Vector3d fixedPoint = new Vector3d();
    private Vector3d movingPoint = new Vector3d();

    private Shape3D[] shape3D = null;
    private TransformGroup[] transformGroup = null;
    private Appearance[] appearance = null;
    private Appearance invisible = null;

    private TAbstractRendered object = null;

   

    public TrailVisualization(TAbstractRendered obj, int num, double len) {
        super();
        setPickable(false);
        setObject(obj);
        // setColor(obj.getColor());
        setColor(Color.WHITE);
        numberOfSegments = num;
        segmentLength = len;
        segmentRadius = 0.;
        mNode = makeNode();
    }

    public TrailVisualization(TAbstractRendered obj, int num, double len, double rad) {
        super();
        setPickable(false);
        setObject(obj);
        setColor(obj.getColor());
        numberOfSegments = num;
        segmentLength = len;
        segmentRadius = rad;
        mNode = makeNode();
    }

    public TrailVisualization(TAbstractRendered obj) {
        this(obj, 32, 0.5, 0.05);
    }

    public void setObject(TAbstractRendered obj) {
        object = obj;
        fixedPoint = new Vector3d(object.getPosition());
        movingPoint = new Vector3d(object.getPosition());
    }

    public void reset() {
        fixedPoint = new Vector3d(object.getPosition());
        movingPoint = new Vector3d(object.getPosition());
        currentSegment = 0;
        for (int i = 0; i < numberOfSegments; i++) {
            shape3D[i].setAppearance(invisible);
        }
    }

    protected TNode3D makeNode() {
        Color3f color = object.getColor();
        double radius = segmentRadius;
        Geometry cylinder;
        if (radius == 0.) {
            cylinder = Node3D.sLine;
        } else {
            cylinder = Cylinder.makeGeometry(16, radius, 1).getIndexedGeometryArray();
        }

        Node3D node = new Node3D();

        invisible = Node3D.makeAppearance(color, 0.f, 0.5f, false);
        TransparencyAttributes invisibilityAttributes = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);
        invisibilityAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
        invisible.setTransparencyAttributes(invisibilityAttributes);
        invisible.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);

        shape3D = new Shape3D[numberOfSegments];
        transformGroup = new TransformGroup[numberOfSegments];

        for (int i = 0; i < numberOfSegments; i++) {
            // Shape Creation
            shape3D[i] = new Shape3D(cylinder, invisible);
            shape3D[i].setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            shape3D[i].setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

            transformGroup[i] = new TransformGroup();
            transformGroup[i].addChild(shape3D[i]);
            transformGroup[i].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            transformGroup[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

            node.addChild(transformGroup[i]);
        }

        makeAppearances();

        updateNode3D(node);
        node.setPickable(false);

        return node;
    }

    public void makeAppearances() {
        Color3f color = object.getColor();
        appearance = new Appearance[numberOfSegments];
        float j = 1f;
        float fadeDecrement = 1f / ((float) numberOfSegments);
        for (int i = 0; i < numberOfSegments; i++) {
            // Appearance Pre-Computation
            appearance[i] = Node3D.makeAppearance(color, 0.f, 0.5f, false);
            TransparencyAttributes appearanceAttributes = new TransparencyAttributes(TransparencyAttributes.FASTEST,
                1f - j);
            appearanceAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
            appearance[i].setTransparencyAttributes(appearanceAttributes);
            appearance[i].setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
            appearance[i].setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
            appearance[i].setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);

            Color3f color3f = new Color3f();
            color3f.set(color);
            ColoringAttributes coloringAttributes = new ColoringAttributes(color3f, ColoringAttributes.FASTEST);
            coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
            coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
            appearance[i].setColoringAttributes(coloringAttributes);

            if (segmentRadius == 0.) {
                appearance[i].setLineAttributes(new LineAttributes(2f, LineAttributes.PATTERN_SOLID, true));
            }

            j -= fadeDecrement;
        }
    }

    public void nextSpatial() {
        registerRenderFlag(GEOMETRY_CHANGE);
    }

    public void render() {
        
        if (mNode != null){
            if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {
                updateNode3D((Node3D) mNode);
                renderFlags ^= GEOMETRY_CHANGE;
            }
        }
    }

    public void updateNode3D(Node3D node) {

        if (node == null) return;

        Vector3d position = object.getPosition();
        Vector3d increment = new Vector3d(position);
        increment.sub(fixedPoint);
        if (increment.length() == 0.) return;

       if (increment.length() > segmentLength) {
            currentSegment = (currentSegment + 1) % numberOfSegments;
            fixedPoint = movingPoint;
            increment.set(position);
            increment.sub(fixedPoint);
        }

        movingPoint = new Vector3d(position);
        Vector3d direction = new Vector3d(increment);
        direction.normalize();

        Vector3d segScaling = new Vector3d(1., increment.length(), 1.);
        Vector3d segPosition = new Vector3d(increment);
        segPosition.scale(0.5);
        segPosition.add(fixedPoint);

        // Rotation Transform Computation -----------------------------------		
        Vector3d axis = new Vector3d();
        axis.cross(Node3D.refDirection, direction);
        AxisAngle4d axisAngle = null;
        double angle = Node3D.refDirection.angle(direction);
        if (axis.length() != 0) {
            axis.normalize();
            axisAngle = new AxisAngle4d(axis.x, axis.y, axis.z, angle);
        } else {
            if (angle > Math.PI / 2.) {
                Vector3d u = new Vector3d();
                Vector3d v = new Vector3d();
                do {
                    u.set(Math.random(), Math.random(), Math.random());
                    v.set(Node3D.refDirection);
                    v.normalize();
                    v.scale(u.dot(v));
                    u.sub(v);
                } while (u.length() < Teal.DoubleZero);
                u.normalize();
                axisAngle = new AxisAngle4d(u, angle);
            } else axisAngle = new AxisAngle4d(Node3D.refDirection, 0.);
        }
        // -------------------------------------------------------------------

        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(segPosition);
        transform3D.setScale(segScaling);
        transform3D.setRotation(axisAngle);

        transformGroup[currentSegment].setTransform(transform3D);

        int i = currentSegment;
        int j = 0;
        do {
            /*
             // Attempt for real-time color modification fails. Why?
             ColoringAttributes coloringAttributes =
             appearance[j].getColoringAttributes();
             Color3f color3f = new Color3f();
             coloringAttributes.getColor(color3f);
             System.out.println( "appearance["+j+"]'s color is: " + color3f );
             Color color = new Color(color3f.x, color3f.y, color3f.z);
             color = Color.blue;
             color3f.set(color);
             coloringAttributes.setColor(color3f);
             appearance[j].setColoringAttributes(coloringAttributes);
             */

            shape3D[i].setAppearance(appearance[j]);

            //			float transparency = 
            //				appearance[j].
            //				getTransparencyAttributes().
            //				getTransparency();
            //			System.out.println( "Segment i =  " + i + " just set to transparency = " + transparency);
            //			System.out.println( "appearance["+j+"] used." );
            i = (i - 1 + numberOfSegments) % numberOfSegments;
            j++;
            Appearance segAppearance = shape3D[i].getAppearance();
            if (segAppearance.getTransparencyAttributes().getTransparency() == 1f) {
                break;
            }
        } while (i != currentSegment);

    }

    public void setColor(Color color) {
        super.setColor(color);
        makeAppearances();
    }

   

}
