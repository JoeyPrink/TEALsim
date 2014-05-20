/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SimDrawOrder.java,v 1.9 2009/04/24 19:35:56 pbailey Exp $ 
 * 
 */

package teal.sim.simulation;

import java.util.Comparator;
import teal.physics.physical.Wall;
import teal.physics.mech.Fret;
import teal.sim.spatial.*;

/**
* Provides the Comparator used to order a sorted list of rendered objects.
*/

@SuppressWarnings("unchecked")
public class SimDrawOrder implements Comparator {

    public int compare(Object x, Object y) {
        int i;
        int k;
        i = getRank(x);
        k = getRank(y);
        return i - k;
    }

    int getRank(Object obj) {
        int rank = 20;

        if (obj instanceof FieldConvolution)
            rank = 0;
        else if (obj instanceof FieldDirectionGrid)
            rank = 4;
        else if (obj instanceof FieldLine)
            rank = 6;
        else if (obj instanceof Wall)
            rank = 8;
        else if (obj instanceof SpatialVector)
            rank = 10;
        else if (obj instanceof Fret) rank = 15;
        return rank;
    }
}