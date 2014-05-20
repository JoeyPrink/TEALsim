/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RandomGridIterator_Original.java,v 1.2 2007/07/16 22:05:18 pbailey Exp $
 * 
 */

package teal.visualization.dlic.field;

import java.util.Random;

import javax.vecmath.Vector3d;

import teal.math.VectorIterator;

/**
 * RandomGridIterator: Iterates over an integer, rectangular grid in a pseudo-random order
 *
 * Because it is difficult to quickly produce a fully random ordering
 * of all the grid points, this class uses a pseudo-random technique:
 * The grid is divided evenly into square blocks, and a random point is
 * chosen in the top-left block. The corresponding points in all the rest of
 * the blocks are next in the sequence. Points are chosen in the block
 * in a truly random fashion until all the points on the grid are covered.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.2 $ 
 */

public class RandomGridIterator_Original implements VectorIterator {

    public RandomGridIterator_Original(int width, int height)
    /* Constructs a RandomGridIterator that iterates pseudo-randomly over the
     * integer grid [0, width-1] x [0, height-1]. */
    {
        this(width, height, new Random());
    }

    public RandomGridIterator_Original(int width, int height, Random random)
    /* Constructs a RandomGridIterator that iterates pseudo-randomly over the
     * integer grid [0, width-1] x [0, height-1] using the random sequence
     * generator 'random'. */
    {
        xmin = 0;
        ymin = 0;
        xmax = width - 1;
        ymax = height - 1;
        blocksize = (width > height) ? width / 8 : height / 8;
        this.random = random;
        Initialize();
    }

    public RandomGridIterator_Original(int xorigin, int yorigin, int width, int height)
    /* Constructs a RandomGridIterator that iterates pseudo-randomly over the
     * integer grid [xorigin, xorigin+width-1] x [yorigin, yorigin+height-1]. */
    {
        this(xorigin, yorigin, width, height, new Random());
    }

    public RandomGridIterator_Original(int xorigin, int yorigin, int width, int height, Random random)
    /* Constructs a RandomGridIterator that iterates pseudo-randomly over the
     * integer grid [xorigin, xorigin+width-1] x [yorigin, yorigin+height-1]
     * using the random sequence generator 'random'. */
    {
        xmin = xorigin;
        ymin = yorigin;
        xmax = xorigin + width - 1;
        ymax = yorigin + height - 1;
        blocksize = (width > height) ? width / 8 : height / 8;
        this.random = random;
        Initialize();
    }

    public boolean hasNext() {
        return coverage <= coveragelimit;
    }

    public Vector3d nextVec()
    /* Returns: null if there are no more points in the sequence, else
     *          a Vector2d whose value is the next point. The returned Vector2d
     *          may be modified by the caller. The same Vector2d may be
     *          written to again on the subsequent call to next(). */
    {
        if (x > xmax) {
            x = xmin + gx;
            y += blocksize;
            if (y > ymax) {
                if (coverage > coveragelimit) {
                    do {
                        gx = random.nextInt(blocksize);
                        gy = random.nextInt(blocksize);
                    } while (covered[gy][gx]);
                    covered[gy][gx] = true;
                    --coverage;
                    x = xmin + gx;
                    y = ymin + gy;
                } else {
                    if (coverage == 0) {
                        x = xmax + 1;
                        y = ymax + 1;
                        return null;
                    }
                    int n = random.nextInt(coverage);
                    gx = gy = 0;
                    while (covered[gy][gx] || (n > 0)) {
                        if (!covered[gy][gx]) --n;
                        if (++gx == blocksize) {
                            gx = 0;
                            ++gy;
                        }
                    }
                    covered[gy][gx] = true;
                    --coverage;
                    x = xmin + gx;
                    y = ymin + gy;
                }
            }
        }
        v.x = x;
        v.y = y;
        x += blocksize;
        return v;
    }

    private static int coveragelimit = 100;
    private int xmin, xmax, ymin, ymax;
    private int blocksize, coverage, gx, gy, x, y;
    private boolean[][] covered;
    private Random random;
    private Vector3d v = new Vector3d();

    public void reset() {
        Initialize();
    }

    private void Initialize() {
        covered = new boolean[blocksize][blocksize];
        for (int j = 0; j < blocksize; ++j)
            for (int i = 0; i < blocksize; ++i)
                covered[j][i] = false;
        coverage = blocksize * blocksize;

        gx = x = xmax + 1;
        gy = y = ymax + 1;
    }

}
