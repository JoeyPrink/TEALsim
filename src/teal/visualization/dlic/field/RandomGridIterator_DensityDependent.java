/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RandomGridIterator_DensityDependent.java,v 1.2 2007/07/16 22:05:18 pbailey Exp $
 * 
 */

package teal.visualization.dlic.field;

import java.util.Random;

import javax.vecmath.Vector3d;

import teal.field.Field;
import teal.math.VectorIterator;

/**
 *
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.2 $ 
 */

public class RandomGridIterator_DensityDependent implements VectorIterator {

    private Vector3d v = new Vector3d();
    private int width, height;
    private Random random;
    Field field;

    public RandomGridIterator_DensityDependent(int width, int height, Random random, Field field) {
        this.width = width;
        this.height = height;
        this.random = (random == null) ? new Random() : random;
        this.field = field;
    }

    public void reset() {
    }

    public boolean hasNext() {
        return true;
    }

    public Vector3d nextVec() {
        double h = 2, q, x, y, r;
        Vector3d f = null, f1 = null, f2 = null;
        do {
            x = random.nextInt(width);
            y = random.nextInt(height);
            f = field.get(x, y, 0.);
            f1 = field.get(x - h * f.y / 2., y + h * f.x / 2., 0.);
            f2 = field.get(x + h * f.y / 2., y - h * f.x / 2., 0.);
            f2.sub(f1);
            q = 1 - 1 / (f2.length() / h + 1);
            r = q * random.nextFloat();
        } while (r > 0.001);
        v.x = x;
        v.y = y;
        return v;
    }

}
