/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Plane.java,v 1.6 2010/07/16 22:30:34 stefan Exp $
 * 
 */

package teal.math;

import java.io.Serializable;

import javax.vecmath.*;


/** Creates a plane using the Point Normal form (p0,normal) and carries out various functions.  

 <p>We first discuss the Point Normal form for a line, and then extend it to a plane.  This discussion is given in more detail in Section 1.2.2 of the
 <a href = "http://web.mit.edu/viz/soft/visualizations/tealsim/docs.htm">TEAL_Physics_Math </a> documentation.  </p>
 
 <p>Given two end points (lines do not have end points, but this is a typical application) A and B, it is often useful to have a form of the line based
 on a perpendicular vector called the normal. This normal gives clues as to the "facing" direction of the line or the inside-outside halfspaces
 created by the spaces on each side of the line. The normal is said to be the "front facing" direction of the line or the direction of the "inside"
 space. However, the normal can be facing either direction (there are two direction perpendicular to a line) so the "inside direction" or
 "front-facing" normal is merely a convention that must be determined by the modeler. The method that follows will be a consistent
 representation for all lines which will represent the normal as a 90 degree rotation of the line in a counter-clockwise direction. 

 <p>The Point-Normal form of a line is simply n.p=D (the dot product of the normal to the line and a point on the line equals the distance to the
 line along the normal vector). Given points A and B for a line segment we can use either point for the point p, but to be consistent we will
 always use A. The normal is simply the direction vector of the line to point B (B-A) rotated 90 degrees in a counter-clockwise direction.
 The direction vector of AB is (Bx-Ax, By-Ay). From this, it can easily be shown that the slope of the line is dy/dx ((By-Ay)/(Bx-Ax)), and
 the slope of the line perpendicular to this is merely the inverse slope (-dx/dy). To the normal n to this line can be derived as follows:
 <pre>
 direction vector = B-A = (run, rise)
 run = dx = Bx-Ax
 rise = dy = By-Ay
 slope = rise/run = dy/dx
 inverse slope = -run/rise = -dx/dy = ( -(Bx-Ax) / (By-Ay) )

 n = normal vector = (rise, -run) = (dy, -dx) = ( (By-Ay), -(Bx-Ax) )
 </pre>
 <p>However, this gives us the normal vector rotated 90 degrees in a clockwise direction. Since it does not matter which component of the
 normal we negate in order to obtain the inverse slope, we will write the normal n as follows in order to get the 90 degree counter-clockwise
 version: 
 <pre>
 n = normal vector = (-rise, run) = (-dy, dx) = ( -(By-Ay), (Bx-Ax) )

 Now to find D, we can use the normal n, the point A, and the Point-Normal equation (n.p=D):

 D = n.A = (nx * Ax) + (ny * Ay)
 </pre>
 <p>And that's all there is to it. We now have the Point-Normal form that defines the line using the normal. The p in n.p=D refers to all points on
 the line; the normal n and the distance D are constants in the equation. For clarity, if we expand out the Point-Normal form of the line we will
 obtain the more common line equation:
 <pre>
 n.p=D
 nx*px + ny*py = D
 A*x + B*y = D
 A*x + B*y + C = 0
 </pre>
 <u>Intersection with a Line</u>
<p>
 Given the following: 

 An infinite line in Point-Normal form defined as n.p=D where n is 
 the "inside" normal vector to the line, p is any point on the line, 
 and D is the distance along the normal vector to the line:

 LINE: n.p=D

 A ray defined parametrically as p(t) = s + d*t where p(t) is a point 
 on the line at "time" t, s is the starting point, and d is the direction
 vector of the ray. For example, the line from A to B can be defined 
 parametrically as p(t) = A + (B-A)*t where 0<=t<=1 and p(t)
 are all of the points on the line from A to B; however, if t is 
 allowed to go beyond these boundaries (in a positive direction) it will
 define an infinite ray in the direction from A to B:
<pre>
 RAY: p(t) = s + d*t
 </pre>
 <p>We can easily determine the "hit time" t where the ray intersects the line by substituting the parametric ray function into the point-normal form
 of the line. Since p(t) defines a point on the ray at time t and the Point-Normal equation defines all points p on the line, substituting this value
 into the equation results in a function of t that can be used to determine the "hit time" t where the ray's point satisfies the Point-Normal
 equation of the line:
 <pre>
 n.p = D
 n.p(t) = D
 n.(s+d*t) = D
 (n.s) + (n.d)*t = D

 HitTime t = (D-(n.s)) / (n.d) = distance to point of intersection

 NOTE: if (n.d = 0) the ray and the line or plane do not intersect (because
 the ray and the line or plane are parallel since the normal n is
 perpendicular to the ray direction d).
 Also, if the HitTime t is negative, the ray has intersected a line or
 plane in the opposite direction (should be ignored).
 </pre>
 <p>The "hit time" also refers to the distance to the from the ray's starting point s to the intersection point. So now what is the intersection point?
 We can simply evaluate the parametric function of the ray at the hit time t:
 <pre>
 point of intersection = p(HitTime t) = s + d*(HitTime t)


 </pre>
<u>Intersection with a Plane</u>

 <p>Believe it or not, the point of intersection and the distance to the intersection is determined exactly the same as with a line. Alas, the beauty
 of vector-based calculations! The only difference is that the Point-Normal form represents a plane (instead of a line). The line intersection
 can be thought of as two-dimensional, but it can be two or three; however, the plane intersection must be three-dimensional.
 <pre>
 PLANE: n.p=D
 </pre>
 <p>Except, now n is the normal to the plane and D is the distance along the normal to the plane.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.6 $*/


public class Plane implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6571242422194043335L;
	
	/** A point on the plane, where point normal form requires this vector and the normal in (p0,normal).  */
    protected Vector3d p0;
	/** The normal to the plane, where point normal form requires this vector and p0 in (p0,normal). */
    protected Vector3d normal;
	/** The distance from the origin to the plane along the plane normal.  This quantity is computed given the 
	 * defining parameters of the plane (p0,normal).
	 * */
    protected double distance;
    /** 
    Creates a plane whose normal is in the + z direction and whose vector p0 is set to the origin. */
    public Plane() {
        p0 = new Vector3d(0.,0.,0.);
        normal = new Vector3d(0., 0., 1.);
        distance = -((normal.x * p0.x) + (normal.y * p0.y) + (normal.z * p0.z));
    }
    /** 
    Creates a plane from a given point on the plane (pt) and a given normal direction (norm).  
    */
    public Plane(Vector3d pt, Vector3d norm) {
        p0 = new Vector3d(pt);
        normal = new Vector3d(norm);
        normal.normalize();
        distance = -((normal.x * p0.x) + (normal.y * p0.y) + (normal.z * p0.z));
    }

    /** 
     Creates a plane from the three specified points a, b, and c.  The point on the plane p0 is set to
     a and the normal to the plane is given by e1 cross e2, where the vector e1 is (a - b), and the 
     vector e2 is (c - b).  All three of the points a, b, c lie on the plane constructed in this way.
     */
    public Plane(Tuple3d a, Tuple3d b, Tuple3d c) {
        p0 = new Vector3d(a);
        Vector3d e1 = new Vector3d();
        Vector3d e2 = new Vector3d();
        e1.sub(a, b);
        e2.sub(c, b);
        normal = new Vector3d();
        normal.cross(e1, e2);
        normal.normalize();
        distance = -((normal.x * p0.x) + (normal.y * p0.y) + (normal.z * p0.z));
    }
    /** 
    Sets three specified points a, b, and c on the plane, and computes the normal and distance of the plane from
    the origin along the normal to the plane.  The normal to the plane is given by 
    e1 cross e2, where the vector e1 is (a - b), and the vector e2 is (c - b).  The point on the plane in the 
    point normal form is set to a.  
    */
    public void set(Tuple3d a, Tuple3d b, Tuple3d c) {
        p0 = new Vector3d(a);
        Vector3d e1 = new Vector3d();
        Vector3d e2 = new Vector3d();
        e1.sub(a, b);
        e2.sub(c, b);
        normal = new Vector3d();
        normal.cross(e1, e2);
        normal.normalize();
        distance = -((normal.x * p0.x) + (normal.y * p0.y) + (normal.z * p0.z));
    }

  	/** findDistance finds the distance of a point (pt) from the plane.  */
    public double findDistance(Tuple3d pt) {
        return distance + ((normal.x * pt.x) + (normal.y * pt.y) + (normal.z * pt.z));

    }
  	/** isOnPlane determines if a point is on the plane.  If the point is on the plane true is
  	 * returned, if the point is not on the plane, false is returned.    */
    public boolean isOnPlane(Tuple3d pt) {
        double d = findDistance(pt);
        return (d == 0);
    }
  	/** getVertex0() returns the vector p0 of the plane, which is specified by the point normal form (p0,normal).    */
    public Vector3d getVertex0() {
        return new Vector3d(p0);
    }
 	/** getPointXY() returns a point on the plane whose xy components are (x,y) and whose z component is calculated
 	 * as below.   */
    public Vector3d getPointXY(double x, double y) {
        Vector3d vec = new Vector3d(x, y, 0.);
        double d = (normal.x * x) + (normal.y * y);
        double d1 = distance + d;
        //System.out.println("d = " + d + "   d1= " + d1);
        if (normal.z != 0) {
            double z = -d1 / normal.z;
            vec.z = z;
        } else vec.z = -d1;
        return vec;
    }
 	/** getPointYZ() returns a point on the plane whose yz components are (y,z) and whose x component is calculated
 	 * as below.     */
    public Vector3d getPointYZ(double y, double z) {
        Vector3d vec = new Vector3d(0., y, z);
        double d = (normal.z * z) + (normal.y * y);
        double d1 = distance + d;
        //System.out.println("d = " + d + "   d1= " + d1);
        if (normal.x != 0) {
            double x = -d1 / normal.x;
            vec.x = x;
        } else vec.x = -d1;
        return vec;
    }
 	/** getPointXZ() returns a point on the plane whose xz components are (x,z) and whose y component is calculated
 	 * as below.     */
    public Vector3d getPointXZ(double x, double z) {
        Vector3d vec = new Vector3d(x, 0., z);
        double d = (normal.x * x) + (normal.z * z);
        double d1 = distance + d;
        //System.out.println("d = " + d + "   d1= " + d1);
        if (normal.y != 0) {
            double y = -d1 / normal.y;
            vec.y = y;
        } else vec.y = -d1;
        return vec;
    }
/** Computes a 4 element axis angle  (x,y,z,angle) components. An axis angle is a rotation of angle (radians) about the vector (x,y,z). */
    public AxisAngle4d getAxisAngle() {
        Vector3d vt = getPointXY(p0.x + 1, p0.y + 1.);
        System.out.print(" a= " + p0 + " b= " + vt);
        vt.sub(p0);
        Vector3d wrk = new Vector3d(0., 0., 1.);
        //a is the angle in radians between the z axis and the plane normal, and is constrained to the range [0,PI].
        double a = wrk.angle(normal);
        //System.out.println("Plane  axis= " + vt + " a= " + a);
        //A 4 element axis angle represented by double precision floating point x,y,z,angle components. An axis angle is a rotation of angle (radians) about the vector (x,y,z). 
        return new AxisAngle4d(vt, a);
    }
/** getDistance() returns the distance from the origin to the plane along the normal direction.  */
    public double getDistance() {
        return distance;
    }
/** setDistance() sets the distance from the origin to the plane along the normal direction.  I am a little suspicious of this
 * because although it sets a new distance, it does nothing to the (p0,normal) form, so that p0 will no longer
 * lie on the plane if we change distance from what was computed using (p0,normal).  */
    public void setDistance(double d) {
        distance = d;
    }
/** getNormal gets the normal direction in the point normal form for the plane (p0,normal).  */
    public Vector3d getNormal() {
        return new Vector3d(normal);
    }
/** setNormal sets the normal direction in the point normal form for the plane (p0,normal).  */
    public void setNormal(Vector3d n) {
        normal = n;
    }
/** setPoint sets p0 in the point normal form for the plane (p0,normal).  */
    public void setPoint(Vector3d point) {
        p0.set(point);
    }
/** getPoint gets p0 in the point normal form for the plane (p0,normal).  */
    public Vector3d getPoint() {
        return new Vector3d(p0);
    }
/** toString gives a text output for the plane giving (p0,normal) and the distance to the plane from 
 * the origin along the normal direction.  
 */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("p0=" + p0 + " N=" + normal);
        buf.append(" D=" + distance);
        return buf.toString();
    }

}
