/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RectangularPlane.java,v 1.10 2010/07/16 22:30:35 stefan Exp $
 * 
 */

package teal.math;

import javax.vecmath.*;

import teal.render.Bounds;
import teal.render.BoundingBox;
import teal.render.BoundingSphere;
import teal.util.TDebug;

/** Creates a plane with special properties.  
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.10 $
 */

public class RectangularPlane extends Plane implements GridIterator {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4676559921687311556L;
	
	/** The Tuple3d b set as a Vector3d.  */
    protected Vector3d p1;
    /** The Tuple3d c set as a Vector3d.  */
    protected Vector3d p2;
/** Creates a standard plane in the xy plane, unit length on a side, with normal along the z axis.  */
    public RectangularPlane() {
        this(new Vector3d(-0.5, 0., -0.5), new Vector3d(-0.5, 0., 0.5), new Vector3d(0.5, 0., 0.5));
    }

    /** 
     Creates a RectangularPlane from the three specified points, normal is generated using right-hand rule ( counter-clockwise ),
     the rectangular bounds are defined by (a.x,a.y, zprojected ) and  ( c.x,c.y, zprojected). The vector b is used to specify the plane, 
     but is not retained, it does not have to define a rectangular vertex.
     */
    public RectangularPlane(Tuple3d a, Tuple3d b, Tuple3d c) {
        super(a, b, c);
        p1 = new Vector3d(b);
        p2 = new Vector3d(c);
        init();
    }
    /** Creates a rectangle using a Bounds object bounds. If bounds is an instance of a Bounding Box, the rectangle
     * created is in the xy plane using the upper and lower bounds of the box in the xy plane, and a value for
     * the z-coordinate of the rectangle which is the average of the upper and lower bound z coordinates.  If bounds
     * is an instance of a Bounding Sphere, the rectangle is in the xy plane centered on the center of the sphere such
     * that the sphere projected onto the xy plane is contained within the rectangle, and a value for the z-coordinate based
     * on the z-coordinate of the bounding sphere.  */
    public RectangularPlane(Bounds bounds) {
        setBounds(bounds);
    }
/** setBounds sets the bounds */
    protected void setBounds(Bounds bounds) {
        if (bounds instanceof BoundingBox) {
            Point3d up = ((BoundingBox) bounds).getUpper();
            Point3d low = ((BoundingBox) bounds).getLower();
            double z = ((up.z - low.z) / 2.) + low.z;

            set(new Vector3d(low.x, low.y, z), new Vector3d(low.x, up.y, z), new Vector3d(up.x, up.y, z));

        } else {
            BoundingSphere bs = null;
            if (bounds instanceof BoundingSphere) {
                bs = (BoundingSphere) bounds;
            } else {
                bs = new BoundingSphere(bounds);
            }
            Point3d pos = new Point3d();
             bs.getCenter(pos);
            
            double r = bs.getRadius();
            set(new Vector3d(pos.x - r, pos.y - r, pos.z), new Vector3d(pos.x - r, pos.y + r, pos.z), new Vector3d(
                pos.x + r, pos.y + r, pos.z));
        }
    }

    public void set(Tuple3d a, Tuple3d b, Tuple3d c) {
        super.set(a, b, c);
        p1 = new Vector3d(b);
        p2 = new Vector3d(c);
    }

    public double getDimensionX() {
        return p2.x - p0.x;
    }

    public double getDimensionY() {
        return p2.y - p0.y;
    }

    public double getDimensionZ() {
        double val = p2.z - p0.z;
        Vector3d p3 = getVertex3();
        double val2 = p3.z - p1.z;

        return (Math.abs(val) > Math.abs(val2) ? val : val2);
    }

    public double getWidth() {
        return getEdge2().length();
    }

    public double getHeight() {
        return getEdge1().length();
    }

    public double getDepth() {
        Vector3d a = getPointXY(p0.x, p2.y);
        Vector3d b = getPointXY(p2.x, p0.y);
        //double max = Math.maxp(p0.z
        return Math.abs(a.z - b.z);
    }

    public Vector3d getCenter() {
        /*
         double x = ((p2.x - p0.x)/2.) + p0.x;
         double y = ((p2.y - p0.y)/2.) + p0.y;
         return getPointXY(x,y);
         */

        Vector3d center = new Vector3d(p2);
        center.add(p0);
        center.scale(0.5);
        return center;

    }

    public Vector3d getVertex0() {
        return new Vector3d(p0);
    }

    public void setVertex0(Vector3d v) {
        super.set(v, p1, p2);
        init();
    }

    public Vector3d getVertex1() {
        return new Vector3d(p1);
    }

    public void setVertex1(Vector3d v) {
        super.set(p0, v, p2);
        p1.set(v);
        init();
    }

    public Vector3d getVertex2() {
        return new Vector3d(p2);
    }

    public void setVertex2(Vector3d v) {
        super.set(p0, p1, v);
        p2.set(v);
        init();
    }

    public Vector3d getVertex3() {
        Vector3d vec = new Vector3d(p0);
        vec.add(p2);
        vec.sub(p1);
        return vec;
    }

    public Vector3d getScale() {
        Vector3d vec = new Vector3d(getWidth(), getHeight(), getDepth());
        if (vec.x == 0) vec.x = 1.0;
        if (vec.y == 0) vec.y = 1.0;
        if (vec.z == 0) vec.z = 1.0;
        return vec;
    }

    public AxisAngle4d getAxisAngle() {
        Vector3d vt = new Vector3d(p2);
        ;
        //        System.out.print(" a= " +p0 + " b= " + vt);
        vt.sub(p0);
        Vector3d wrk = new Vector3d(0., 0., 1.);

        double a = wrk.angle(normal);
        //        System.out.println("RecPlane axis= " + vt + "angle= " +a);
        return new AxisAngle4d(vt, a);
    }

    public Vector3d getEdge1() {
        Vector3d edge = new Vector3d();
        edge.sub(p1, p0);
        return edge;
    }

    public Vector3d getEdge2() {
        Vector3d edge = new Vector3d();
        edge.sub(p2, p1);
        return edge;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getVertex0() + ", " + getVertex1() + ", " + getVertex2() + ", " + getVertex3());
        buf.append("\n\t" + getNormal() + ", " + getCenter() + ", " + getWidth() + " x " + getHeight() + " x "
            + getDepth() + "\n\t" + getDimensionX() + ", " + getDimensionY() + ", " + getDimensionZ());
        return buf.toString();
    }

    // the following are for VecGridIterator
    protected boolean useXY = true;
    protected Vector3d pos = null;
    protected boolean valid = false;
    protected int resolutionX = 12;
    protected int resolutionY = 12;
    protected double dx;
    protected double dy;
    protected int nX;
    protected int nY;
    protected int i;
    protected int maxI;
    protected int j;
    protected int maxJ;

    protected void init() {

        dx = getDimensionX() / (double) resolutionX;
        dy = getDimensionY() / (double) resolutionY;
        double dz = getDimensionZ() / (double) resolutionY;
        TDebug.println(2, this + "\nt dx= " + dx + " dy=" + dy + " dz=" + dz);
        nX = resolutionX - 1;
        nY = resolutionY - 1;
        i = 0;
        j = 0;

        if (dy == 0) {
            useXY = false;
            dy = dz;

        }
        reset();
        TDebug.println(2, "useXY = " + useXY);
    }

    public void reset() {
        i = 0;
        j = 0;

        if (useXY) {
            pos = new Vector3d(getPointXY(p0.x + dx / 2., p0.y + dy / 2.));
        } else {
            pos = new Vector3d(getPointXZ(p0.x + dx / 2., p0.z + dy / 2.));
        }
        //nY = (int) (d.height/dy) -1;

        //TDebug.println(1,"GridIterator: " + pt0 + "\t" + pt2+"\tres= "+resolution);
        //TDebug.println("GridIterator dX="+dx+ "\tdY="+dy+"\tstart: " + pos);
        valid = true;
    }

    public int getResolutionX() {
        return resolutionX;
    }

    public void setResolutionX(int res) {
        if (res > 0) {
            resolutionX = res;
            init();
        }
    }

    public int getResolutionY() {
        return resolutionY;
    }

    public void setResolutionY(int res) {
        if (res > 0) {
            resolutionY = res;
            init();
        }
    }

    public double getDX() {
        return dx;
    }

    public double getDY() {
        return dy;
    }

    public void setResolution(int resX,int resY) {
        if (resX > 0) resolutionX = resX;
        if (resY > 0) resolutionY = resY;
        init();
    }

    public boolean hasNext() {
        return valid;
    }

    /**
     * The next position converted into a planar world projection.
     */
    public Vector3d nextVec() {
        Vector3d wrk = null;
        if (valid) {
            wrk = new Vector3d(pos);
            if (j < nY) {
                pos.x += dx;
                j++;
            } else {
                pos.x = p0.x + dx / 2.;
                if (useXY)
                    pos.y += dy;
                else pos.z += dy;
                i++;
                j = 0;
            }
            if (i > nX) {
                valid = false;
            }
            if (useXY)
                pos = getPointXY(pos.x, pos.y);
            else pos = getPointXZ(pos.x, pos.z);
        }
        return wrk;
    }

}
