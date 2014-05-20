/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CircularField.java,v 1.2 2007/07/16 22:05:18 pbailey Exp $
 * 
 */

package teal.visualization.dlic.field;

import javax.vecmath.Vector2d;

/**
*
* @author Andrew McKinney
* @author Phil Bailey
* @author Michael Danziger
* @version $Revision: 1.2 $ 
*/

public class CircularField {

    private Vector2d origin;

    public CircularField() {
        origin = new Vector2d(0, 0);

    }

    public CircularField(double x, double y) {
        origin = new Vector2d(x, y);
    }

    public CircularField(Vector2d o) {
        origin = new Vector2d(o);

    }

    /*
     public Vector2d get(Vector2d p, Vector2d f)
     {
     Vector2d q = p.sub( origin );
     double r = q.len();
     if( r  < 1e-15 ) return new Vector2d( 0, 0 );
     f.Set( q.y / ( r * r ) , - q.x / ( r * r ) );
     //System.out.println( "Circular Field at (" + p.x + "," + p.y +
     //                    ") = (" + f.x + "," + f.y + ")" );
     return f;
     }
     */

    public Vector2d getVec2(Vector2d p) {
        Vector2d f = new Vector2d();
        return getVec2(p, f);
    }

    public Vector2d getVec2(Vector2d p, Vector2d f) {
        Vector2d q = new Vector2d();
        origin.sub(new Vector2d(50, 0), q);
        p.sub(q, q);
        double r = q.length();
        f.set(q.y / (r * r), -q.x / (r * r));

        origin.sub(new Vector2d(-50, 0), q);
        p.sub(q, q);
        r = q.length();
        if (r < 1e-15) return new Vector2d(0, 0);
        f.add(new Vector2d(-q.y / (r * r), q.x / (r * r)));
        //System.out.println( "Circular Field at (" + p.x + "," + p.y +
        //                    ") = (" + f.x + "," + f.y + ")" );
        return f;
    }

}
