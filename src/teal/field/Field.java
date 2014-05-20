/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Field.java,v 1.22 2010/07/21 21:54:28 stefan Exp $
 * 
 */

package teal.field;

import java.io.Serializable;

import javax.vecmath.*;

/**
 * A generic wrapper class for a Vector3dField which gets the potential of the field.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.22 $ 
 */
public abstract class Field implements Vector3dField, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5849538359071491882L;
	
	public static final double epsilon = 0.001;
    public final static int UNDEFINED = 0;
    public final static int B_FIELD = 1;
    public final static int E_FIELD = 2;
    public final static int P_FIELD = 3;
    public final static int D_FIELD = 4;
    public final static int G_FIELD = 5;
    public final static int EP_FIELD = 6;

    
    public abstract int getType();

    public abstract Vector3d get(Vector3d x, Vector3d data);

    /**
     * Deprecated, calls get(Vector3d x, Vector3d data).
     * 
     * @param x
     * @param data
     * @param t
     * @return abstract Vector3d
     */
    public abstract Vector3d get(Vector3d x, Vector3d data, double t);

    public abstract Vector3d get(Vector3d x);

    /**
     * Deprecated, calls get(Vector3d x).
     * 
     * @param x
     * @param t
     * @return theFieldValue
     */
    public abstract Vector3d get(Vector3d x, double t);

    /**
     * This method should return the scalar flux at the supplied position.  Flux type is determined by specific Field 
     * implementation (EField, BField, etc.).
     * 
     * @param x location to calculate flux.
     * @return scalar flux at this point.
     */
    public abstract double getFlux(Vector3d x);

    /**
     * This method should return the scalar potential at the supplied position.  Potential type is determined by the 
     * specific Field implemtation (EField, BField, etc.).
     * 
     * @param x position at which to calculate potential.
     * @return scalar potential at this point.
     */
    public abstract double getPotential(Vector3d x);


    /**
     * Convenience function, calls get(Vector3d x) with a new Vector3d based on the supplied component values.
     * 
     * @param x
     * @param y
     * @param z
     * @return theFieldValue
     */
    public Vector3d get(double x, double y, double z) {
        return get(new Vector3d(x, y, z));
    }


    /**
     * Calculates the gradient of the field at the given position.
     * 
     * @param x position at which to calculate gradient.
     * @return Matrix3d getGradient
     */
    public Matrix3d getGradient(Vector3d x) {
        Vector3d[] F = new Vector3d[] { get(new Vector3d(x.x + epsilon, x.y, x.z)),
                get(new Vector3d(x.x - epsilon, x.y, x.z)), get(new Vector3d(x.x, x.y + epsilon, x.z)),
                get(new Vector3d(x.x, x.y - epsilon, x.z)), get(new Vector3d(x.x, x.y, x.z + epsilon)),
                get(new Vector3d(x.x, x.y, x.z - epsilon)) };
        return computeGradient(F);
    }

    /**
     * Deprecated, same as getGradient(Vector3d x).
     * 
     * @param x
     * @param t
     * @return Matrix3d getGradient
     */
    public Matrix3d getGradient(Vector3d x, double t) {
        Vector3d[] F = new Vector3d[] { get(new Vector3d(x.x + epsilon, x.y, x.z), t),
                get(new Vector3d(x.x - epsilon, x.y, x.z), t), get(new Vector3d(x.x, x.y + epsilon, x.z), t),
                get(new Vector3d(x.x, x.y - epsilon, x.z), t), get(new Vector3d(x.x, x.y, x.z + epsilon), t),
                get(new Vector3d(x.x, x.y, x.z - epsilon), t) };
        return computeGradient(F);
    }

    /**
     * Internal method that actually computes gradient.
     * @param F
     * @return gradient
     */
    protected Matrix3d computeGradient(Vector3d[] F) {
        Matrix3d grad = new Matrix3d();
        double scale = 1.0 / (2.0 * epsilon);
        if (F.length > 5) {
            Vector3d dFdx = new Vector3d();
            Vector3d dFdy = new Vector3d();
            Vector3d dFdz = new Vector3d();
            dFdx.sub(F[0], F[1]);
            dFdy.sub(F[2], F[3]);
            dFdz.sub(F[4], F[5]);
            dFdx.scale(scale);
            dFdy.scale(scale);
            dFdz.scale(scale);
            grad.setColumn(0, dFdx);
            grad.setColumn(1, dFdy);
            grad.setColumn(2, dFdz);
        }
        return grad;
    }

    

    /**
     * This method returns a normalized vector pointing in the direction of the field at the supplied position.
     * 
     * @param pos position at which to calculate field.
     * @param stepArc not used.
     * @return normalized vector tangent to field.
     */
    public Vector3d deltaField(Vector3d pos, double stepArc) {
        Vector3d tangent = new Vector3d();
        Vector3d delta = new Vector3d();

        //delta = get(pos,stepArc);
        delta = get(pos);
        double leng = delta.length();
        if (leng != 0)

        tangent.scale(1 / leng, delta);
        return tangent;
    }

    
    /**
     * Same as deltaField(Vector3d pos, double stepArc), except returns a non-normalized vector.
     * @param pos
     * @param stepArc
     * @param bool
     */
    public Vector3d deltaField(Vector3d pos, double stepArc, boolean bool) {
        Vector3d tangent = new Vector3d();
        Vector3d delta = new Vector3d();

        delta = get(pos);
        tangent.set(delta);
        return tangent;
    }

}
