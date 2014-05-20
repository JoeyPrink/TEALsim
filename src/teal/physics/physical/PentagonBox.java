/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PentagonBox.java,v 1.13 2010/07/06 19:31:27 pbailey Exp $ 
 * 
 */

package teal.physics.physical;

import java.util.*;

import javax.vecmath.*;

import teal.sim.SimObj;

/**
 * Represents a pentagon-shaped collection of Walls.
 */
public class PentagonBox extends SimObj implements TWallCollection {

    private static final long serialVersionUID = 4049640109269923891L;
    protected Vector3d position;
    protected Vector3d orientation;
    protected Vector3d normal;
    protected double radius;
    protected double thickness = 1.;
    private Wall[] wall = new Wall[5];


    public PentagonBox() {
        position = new Vector3d();
        normal = new Vector3d(0, 0, 1);
        orientation = new Vector3d(0, 0, 1);
        radius = 1.;
    }

    public void setPosition(Vector3d pposition) {
        position = new Vector3d(pposition);
    }

    public void setNormal(Vector3d nnormal) {
        normal = new Vector3d(nnormal);
    }

    public void setOrientation(Vector3d oorientation) {
        orientation = new Vector3d(oorientation);
    }

    public void setRadius(double rradius) {
        radius = rradius;
    }

    public void setThickness(double tthickness) {
        thickness = tthickness;
    }

    public Vector3d getPosition() {
        return position;
    }

    public Vector3d getNormal() {
        return normal;
    }

    public Vector3d getOrientation() {
        return orientation;
    }

    public double getRadius() {
        return radius;
    }

    public double getThickness() {
        return thickness;
    }

    private void generateWalls() {
        AxisAngle4d axisangle = new AxisAngle4d(normal, Math.PI / 5.);
        Quat4d quaternion = new Quat4d();
        quaternion.set(axisangle);
        Matrix3d rotation = new Matrix3d();
        rotation.set(quaternion);

        Vector3d disp = new Vector3d(orientation);
        rotation.transform(disp);

        Vector3d edge = new Vector3d();
        edge.cross(disp, normal);

        disp.scale(radius * Math.cos(Math.PI / 5));
        edge.scale(2. * radius * Math.sin(Math.PI / 5));

        normal.scale(thickness);

        for (int i = 0; i < 5; i++) {
            wall[i] = new Wall();
            wall[i].setPosition(position.x + disp.x, position.y + disp.y, position.z + disp.z);
            wall[i].setEdge1(normal);
            wall[i].setEdge2(edge);

            rotation.transform(disp);
            rotation.transform(disp);
            rotation.transform(edge);
            rotation.transform(edge);
        }
    }

    public Collection<Wall> getWalls() {
        generateWalls();
        ArrayList<Wall> array = new ArrayList<Wall>();
        array.add(wall[0]);
        array.add(wall[1]);
        array.add(wall[2]);
        array.add(wall[3]);
        array.add(wall[4]);
        return array;
    }

}
