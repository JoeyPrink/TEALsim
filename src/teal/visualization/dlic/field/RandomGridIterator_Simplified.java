/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RandomGridIterator_Simplified.java,v 1.2 2007/07/16 22:05:18 pbailey Exp $
 * 
 */

package teal.visualization.dlic.field;

import java.util.Random;

import javax.vecmath.Vector3d;

import teal.math.VectorIterator;

/**
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.2 $ 
 */

public class RandomGridIterator_Simplified implements VectorIterator {

    private Vector3d v = new Vector3d();
    private int width, height;
    private Random random;

    public RandomGridIterator_Simplified(int width, int height, Random random) {
        this.width = width;
        this.height = height;
        this.random = (random == null) ? new Random() : random;
    }

    public boolean hasNext() {
        return true;
    }

    public void reset() {
    }

    public Vector3d nextVec() {
        v.x = random.nextInt(width);
        v.y = random.nextInt(height);
        v.z = 0.0;
        return v;
    }

}
