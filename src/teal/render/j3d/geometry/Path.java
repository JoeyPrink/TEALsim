/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Path.java,v 1.1 2010/04/12 19:57:53 stefan Exp $
 * 
 */

package teal.render.j3d.geometry;

import java.util.*;

import javax.vecmath.*;

/**
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.1 $
 */

@SuppressWarnings("unchecked")
public class Path extends Vector {

    private static final long serialVersionUID = 3905519397832832825L;
    public String type = "";
    public int sides = 0;

    public boolean add(Object o) {
        if (o instanceof Vector3d) {
            boolean retour = super.add(o);
            computeShape();
            return retour;
        } else {
            return false;
        }
    }

    public Collection getPoint() {
        return this;
    }

    public void setType(String s) {
        type = s;
        computeShape();
    }

    public String getType() {
        return type;
    }

    public void setSides(int i) {
        this.sides = i;
        computeShape();
    }

    public int getSides() {
        return sides;
    }

    protected void computeShape() {
        if (type.compareTo("circle") == 0) {
            if ((size() == 2) && (sides >= 3)) {
                computeCircle();
            }
        }
        if (type.compareTo("rectangle") == 0) {
            if (size() == 2) {
                computeRectangle();
            }
        }
    }

    protected void computeCircle() {
        Vector3d center = new Vector3d((Vector3d) get(0));
        Vector3d p2 = new Vector3d((Vector3d) get(1));
        p2.sub(center);
        double radius = p2.length();
        removeAllElements();
        double angle = 0;
        double incAngle = 2 * Math.PI / sides;
        for (int i = 0; i < sides; i++) {
            super.add(new Vector3d(center.x + radius * Math.cos(angle), center.y + radius * Math.sin(angle), 0));
            angle += incAngle;
        }
        super.add(new Vector3d(center.x + radius, center.y, 0));
    }

    protected void computeRectangle() {
        Vector3d p1 = new Vector3d((Vector3d) get(0));
        Vector3d p2 = new Vector3d((Vector3d) get(1));
        removeAllElements();
        super.add(new Vector3d(p1.x, p1.y, 0));
        super.add(new Vector3d(p1.x, p2.y, 0));
        super.add(new Vector3d(p2.x, p2.y, 0));
        super.add(new Vector3d(p2.x, p1.y, 0));
        super.add(new Vector3d(p1.x, p1.y, 0));
    }
}
