/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RectangularBox.java,v 1.14 2010/07/06 19:31:27 pbailey Exp $ 
 * 
 */

package teal.physics.physical;

import java.util.*;

import javax.vecmath.Vector3d;

import teal.sim.SimObj;

/**
 * Represents a six-sided rectangular box made up of Walls.
 */
public class RectangularBox extends SimObj implements TWallCollection {

    private static final long serialVersionUID = 3834311743462716210L;
    protected Vector3d position;
    protected Vector3d orientation;
    protected Vector3d normal;
    protected double length;
    protected double height;
    protected double width;
    protected boolean isOpen = false;
    private Wall[] wall = new Wall[6];

    public RectangularBox() {
        position = new Vector3d();
        normal = new Vector3d(0, 0, 1);
        orientation = new Vector3d(0, 0, 1);
        length = 1.;
        height = 1.;
        width = 1.;
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

    public void setLength(double llength) {
        length = llength;
    }

    public void setHeight(double hheight) {
        height = hheight;
    }

    public void setWidth(double wwidth) {
        width = wwidth;
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

    public double getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public boolean getOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    private void generateWalls() {
        Vector3d pos = new Vector3d();
        Vector3d rel = new Vector3d();
        Vector3d len = new Vector3d();
        Vector3d wid = new Vector3d();

        wall[0] = new Wall();
        wall[1] = new Wall();
        rel.set(orientation);
        rel.scale(length / 2.);
        pos.set(position);
        pos.add(rel);
        wall[0].setPosition(pos);
        pos.set(position);
        pos.sub(rel);
        wall[1].setPosition(pos);
        len.cross(normal, orientation);
        len.scale(width);
        wid.set(normal);
        wid.scale(height);
        wall[0].setEdge1(len);
        wall[1].setEdge1(len);
        wall[0].setEdge2(wid);
        wall[1].setEdge2(wid);

        wall[2] = new Wall();
        wall[3] = new Wall();
        rel.cross(normal, orientation);
        rel.scale(width / 2.);
        pos.set(position);
        pos.add(rel);
        wall[2].setPosition(pos);
        pos.set(position);
        pos.sub(rel);
        wall[3].setPosition(pos);
        len.set(orientation);
        len.scale(length);
        wid.set(normal);
        wid.scale(height);
        wall[2].setEdge1(len);
        wall[3].setEdge1(len);
        wall[2].setEdge2(wid);
        wall[3].setEdge2(wid);

        if (isOpen == false) {
            wall[4] = new Wall();
            wall[5] = new Wall();
        	     
            rel.set(normal);
            rel.scale(height / 2.);
            pos.set(position);
            pos.add(rel);
            wall[4].setPosition(pos);
            pos.set(position);
            pos.sub(rel);
            wall[5].setPosition(pos);
            len.set(orientation);
            len.scale(length);
            wid.cross(normal, orientation);
            wid.scale(width);
            wall[4].setEdge1(len);
            wall[5].setEdge1(len);
            wall[4].setEdge2(wid);
            wall[5].setEdge2(wid);
        }

    }

    public Collection<Wall> getWalls() {
        generateWalls();
        ArrayList<Wall> array = new ArrayList<Wall>();
        array.add(wall[0]);
        array.add(wall[1]);
        array.add(wall[2]);
        array.add(wall[3]);
        if (isOpen == false) {
            array.add(wall[4]);
            array.add(wall[5]);
        }
        return array;
    }
    
    public boolean contains(Vector3d object) {
           
        if(position.x - length/2 <= object.x && position.y - height/2 <= object.y && 
                position.z - width/2 <= object.z && 
                position.x + length/2 >= object.x && position.y + height/2 >= object.y && 
                position.z + width/2 >= object.z) {
            
            return true;
        }
        
        return false;
    }

}
