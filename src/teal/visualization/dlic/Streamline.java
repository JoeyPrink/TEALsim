/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Streamline.java,v 1.3 2009/04/24 19:35:59 pbailey Exp $
 * 
 */

package teal.visualization.dlic;

import java.util.*;

import javax.vecmath.*;

import teal.config.*;
import teal.field.*;
import teal.math.*;

/**
 *
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.3 $ 
 */

public class Streamline implements VectorIterator {

    private Vector3dField field;
    private Vector3d planeNormal = null;
    private double length, evenstep;
    private int reverse;
    private double h_min, h_max, tolerance;
    private double safety = 0.9;
    private Vector<Vector3d> singularities = new Vector<Vector3d>();
    private double singularitylimit;
    private Vector3d lastFieldValue = new Vector3d();

    // Temporary private variables
    private Vector3d x = new Vector3d(), v = new Vector3d();
    private Vector3d dx = new Vector3d(), odx = new Vector3d();
    private Vector3d nx = new Vector3d(), tx = new Vector3d();
    private Vector3d cx = new Vector3d();
    private double ds, h, L, L1, L2;
    private int num;
    private boolean hasNext = false;
    private final double TINY = 100. * Teal.MachineEpsilon;

    private Vector3d k1 = new Vector3d(), k2 = new Vector3d(), k3 = new Vector3d(), k4 = new Vector3d();

    public Streamline(Vector3dField field) {
        this(field, 0.5, 1e-1, 1e1, 5e-2);
    }

    public Streamline(Vector3dField field, double evenstep) {
        this(field, evenstep, 1e-1, 1e1, 5e-2);
    }

    public Streamline(Vector3dField field, double evenstep, double h_min, double h_max, double tolerance) {
        setField(field);
        setLength(0.0);
        setStepSize(evenstep);
        setMinStep(h_min);
        setMaxStep(h_max);
        setTolerance(tolerance);
        setDefaultSingularityLimit();

    }

    public void setField(Vector3dField field) {
        this.field = field;
        //this.field = new UnitField(field); 
    }

    public void setLength(double length) {
        this.length = Math.abs(length);
        reverse = (length < 0.0) ? -1 : 1;
    }

    public void setStepSize(double evenstep) {
        this.evenstep = evenstep;
    }

    public void setDefaultStepSize() {
        setStepSize(0.5);
    }

    public void setMinStep(double h_min) {
        this.h_min = h_min;
    }

    public void setDefaultMinStep() {
        setMinStep(1e-1);
    }

    public void setMaxStep(double h_max) {
        this.h_max = h_max;
    }

    public void setDefaultMaxStep() {
        setMaxStep(1e1);
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public void setDefaultTolerance() {
        setTolerance(5e-2);
    }

    public void setSingularityLimit(double limit) {
        this.singularitylimit = limit;
    }

    public void setDefaultSingularityLimit() {
        setSingularityLimit(10 * evenstep);
    }

    public void addSingularity(Vector3d v) {
        singularities.add(v);
    }

    public void setStart(Tuple3d p, double length) {
        setLength(length);
        setStart(p);
    }

    public void setStart(Tuple3d x0) {
        ds = length;
        h = h_max;
        num = 0;
        L = L1 = L2 = 0.0;
        nx.set(x0);
        field.get(nx, dx);
        projectOnPlane(dx);
        dx.normalize();
        v.set(x0);
        hasNext = true;
    }

    public void setPlaneNormal(Vector3d normal) {
        this.planeNormal = normal;
    }

    public void projectOnPlane(Vector3d x) {
        if (planeNormal == null) return;
        Vector3d y = new Vector3d(planeNormal);
        y.scale(-x.dot(planeNormal));
        x.add(y);
    }

    public void stop() {
        num = -1;
    }

    public boolean stopped() {
        return (num == -1);
    }

    public boolean hasNext() {
        return ((num >= 0) && hasNext && (L < L2));
    }

    public Vector3d nextVec() {
        if (num < 0) return null;
        if (computeNext())
            return v;
        else if (num == 0) {
            num = -1;
            return v;
        } else {
            num = -1;
            hasNext = false;
            return null;
        }

    }

    // FieldLine version of nextVec() -- calls computeNextFL() rather than computeNext()
    public Vector3d nextVecFL() {
        if (num < 0) return null;
        if (computeNextFL())
            return v;
        else if (num == 0) {
            num = -1;
            return v;
        } else {
            num = -1;
            hasNext = false;
            return null;
        }

    }

    // this is the FieldLine version of computeNext() -- doesn't do linear interpolation between steps
    private boolean computeNextFL() {

        Vector3d tmp = new Vector3d();
        /*
         if (L < L2) {
         v.add(cx);
         L += evenstep;
         num++;
         return true;
         }
         */
        while ((ds > 0.0) && (dx.length() > TINY)) {
            x.set(nx);
            odx.set(dx);
            L1 = L2;
            if (h > ds) h = ds;
            double last_h, error;

            // Initial location
            field.get(x, k1);
            lastFieldValue.set(k1);
            projectOnPlane(k1);
            k1.normalize();
            do {
                last_h = h;
                h *= reverse;

                tmp.set(k1);
                tmp.scale(0.5 * h);
                tx.add(x, tmp);

                // 2nd point
                field.get(tx, k2);
                projectOnPlane(k2);
                k2.normalize();
                tmp.set(k2);
                tmp.scale(0.5 * h);
                tx.add(x, tmp);

                //third point
                field.get(tx, k3);
                projectOnPlane(k3);
                k3.normalize();
                tmp.set(k3);
                tmp.scale(h);
                tx.add(x, tmp);

                // Forth point
                field.get(tx, k4);
                projectOnPlane(k4);
                k4.normalize();

                //nx.set( x.add( ( k2.add( k1 ).add( k3 ).add( k4 ) ).scale( h / 6. ) ) ); 
                ///
                k2.scale(2.);
                nx.add(k1, k2);
                k3.scale(2.);
                nx.add(k3);
                nx.add(k4);
                nx.scale(h / 6.);
                nx.add(x);

                field.get(nx, dx);
                projectOnPlane(dx);
                UnitField.unit(dx);
                tx.sub(k4, dx);

                h *= reverse;

                error = h / 6. * tx.length();
                if (error > TINY) {
                    h *= Math.pow(safety * tolerance / error, 0.2);
                    if (h > h_max) h = (ds < h_max) ? ds : h_max;
                } else h = (ds < h_max) ? ds : h_max;

                tx.sub(nx, x);
                last_h = tx.length();

            } while ((error > tolerance) && (last_h > h_min));
            // && ( System.currentTimeMillis() < time2 ) );

            if ((error > tolerance) || (last_h < h_min)) return false;

            ds -= last_h;

            Enumeration<Vector3d> enm = singularities.elements();
            while (enm.hasMoreElements()) {
                Vector3d v = (Vector3d) enm.nextElement();
                tx.sub(v, nx);
                if (tx.lengthSquared() < singularitylimit) return false;
            }

            v.set(nx);
            return true;

            /*			
             L2 = L1 + last_h;
             if (L2 > length)
             L2 = length;
             if ((L1 <= L) && (L < L2)) {
             cx.sub(nx, x);
             cx.scale(evenstep / (L2 - L1));
             v.set(x);
             L += evenstep;
             //System.out.println("Streamline h = " + h);
             return true;
             }
             */
        }
        return false;
    }

    private boolean computeNext() {

        Vector3d tmp = new Vector3d();
        if (L < L2) {
            v.add(cx);
            L += evenstep;
            num++;
            return true;
        }

        while ((ds > 0.0) && (dx.length() > TINY)) {
            x.set(nx);
            odx.set(dx);
            L1 = L2;
            if (h > ds) h = ds;
            double last_h, error;

            // Initial location
            field.get(x, k1);
            lastFieldValue.set(k1);
            projectOnPlane(k1);
            k1.normalize();
            do {
                last_h = h;
                h *= reverse;

                tmp.set(k1);
                tmp.scale(0.5 * h);
                tx.add(x, tmp);

                // 2nd point
                field.get(tx, k2);
                projectOnPlane(k2);
                k2.normalize();
                tmp.set(k2);
                tmp.scale(0.5 * h);
                tx.add(x, tmp);

                //third point
                field.get(tx, k3);
                projectOnPlane(k3);
                k3.normalize();
                tmp.set(k3);
                tmp.scale(h);
                tx.add(x, tmp);

                // Forth point
                field.get(tx, k4);
                projectOnPlane(k4);
                k4.normalize();

                //nx.set( x.add( ( k2.add( k1 ).add( k3 ).add( k4 ) ).scale( h / 6. ) ) ); 
                ///
                k2.scale(2.);
                nx.add(k1, k2);
                k3.scale(2.);
                nx.add(k3);
                nx.add(k4);
                nx.scale(h / 6.);
                nx.add(x);

                field.get(nx, dx);
                projectOnPlane(dx);
                UnitField.unit(dx);
                tx.sub(k4, dx);

                h *= reverse;

                error = h / 6. * tx.length();
                if (error > TINY) {
                    h *= Math.pow(safety * tolerance / error, 0.2);
                    if (h > h_max) h = (ds < h_max) ? ds : h_max;
                } else h = (ds < h_max) ? ds : h_max;

                tx.sub(nx, x);
                last_h = tx.length();

            } while ((error > tolerance) && (last_h > h_min));
            // && ( System.currentTimeMillis() < time2 ) );

            if ((error > tolerance) || (last_h < h_min)) return false;

            ds -= last_h;

            Enumeration<Vector3d> e = singularities.elements();
            while (e.hasMoreElements()) {
                Vector3d v = (Vector3d) e.nextElement();
                tx.sub(v, nx);
                if (tx.lengthSquared() < singularitylimit) return false;
            }

            L2 = L1 + last_h;
            if (L2 > length) L2 = length;
            if ((L1 <= L) && (L < L2)) {
                cx.sub(nx, x);
                cx.scale(evenstep / (L2 - L1));
                v.set(x);
                L += evenstep;
                //System.out.println("Streamline h = " + h);
                return true;
            }

        }
        return false;
    }

    public void reset() {
    }

    public Vector3d getLastFieldValue() {
        return lastFieldValue;
    }

    public void setLastFieldValue(Vector3d lastFieldValue) {
        this.lastFieldValue = lastFieldValue;
    }
}
