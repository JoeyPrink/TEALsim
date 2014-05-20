/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SpiralField.java,v 1.2 2007/07/16 22:05:18 pbailey Exp $
 * 
 */

package teal.visualization.dlic.field;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.2 $ 
 */

public class SpiralField {

    public Vector3d origin;
    public double pitch;

    /* Hack for Field conversion */
    //public Vector3d get(Vector3d p,double t)
    //{
    //	return get(p);
    //}
    public SpiralField() {
        origin = new Vector3d();
        //        System.out.println( "Spiral Field at (0,0,0) created." );
    }

    public SpiralField(double x, double y) {
        origin = new Vector3d(x, y, 0);
        //        System.out.println( "Spiral Field at (" + x + "," + y + ") created." );
    }

    public SpiralField(Vector2d o) {
        origin = new Vector3d(o.x, o.y, 0);
        //        System.out.println( "Spiral Field at (" + o.x + "," + o.y + ") created." );
    }

    public Vector3d get(Vector2d p, Vector2d f) {
        Vector3d tmp = new Vector3d(p.x, p.y, 0);
        tmp.sub(origin);
        double r = tmp.length();
        if (r < 1e-15) return new Vector3d();
        f.set(tmp.y / r - 0.005 * tmp.x, -tmp.x / r - 0.005 * tmp.y);
        //System.out.println( "Spiral Field at (" + p.x + "," + p.y +
        //                    ") = (" + f.x + "," + f.y + ")" );
        return new Vector3d(f.x, f.y, 0);
    }

}
