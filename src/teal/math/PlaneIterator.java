/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PlaneIterator.java,v 1.3 2007/07/16 22:04:48 pbailey Exp $
 * 
 */

package teal.math;

import java.util.Random;

import javax.vecmath.Vector3d;

/** 
 * Randomly selects points on the plane 
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.3 $
 */

public class PlaneIterator implements VectorIterator {

    protected Plane plane;
    protected Random random;

    public PlaneIterator(Plane p) {
        plane = p;
        init();

    }

    private void init() {
        reset();
    }

    public void reset() {
        random = new Random();
    }

    public boolean hasNext() {
        return true;
    }

    public Vector3d nextVec() {
        double x = random.nextDouble();
        double y = random.nextDouble();
        return plane.getPointXY(x, y);
    }

}
