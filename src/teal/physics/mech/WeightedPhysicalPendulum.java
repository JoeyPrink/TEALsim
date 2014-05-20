/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WeightedPhysicalPendulum.java,v 1.10 2010/08/10 18:12:33 stefan Exp $ 
 * 
 */

package teal.physics.mech;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import teal.render.j3d.ArrowNode;
import teal.render.j3d.BoxNode;
import teal.render.j3d.WeightedPendulumNode;
import teal.render.primitives.Arrow;
import teal.render.scene.TNode3D;
import teal.physics.physical.PhysicalObject;

/**
 * @author danziger
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WeightedPhysicalPendulum extends PhysicalObject {
	double length, width, depth;
	double I; // moment of inertia
	double rod_mass, ring_mass;
	double ring_inner_r, ring_outer_r;
	double ring_pos;
	
	Vector3d pivot_axis = new Vector3d(0,0,1);
	
	public static int FULL_SOLUTION = 0;
	public static int SMALL_ANGLE = 1;
	public static int FIRST_ORDER_CORRECTION = 2;
	
	int mode = FULL_SOLUTION;
	boolean ringChanged = false;
	boolean rodChanged = true;
	
	
	public WeightedPhysicalPendulum(double l, double w, double d, double rodm, double ringm, double ringr1, double ringr2) {
		super();
		length = l;
		width = w;
		depth = d;
		rod_mass = rodm;
		ring_mass = ringm;
		ring_inner_r = ringr1;
		ring_outer_r = ringr2;
		ring_pos = length*0.5;
		setMoveable(false);
	}
	
	public WeightedPhysicalPendulum() {
		length = 10.;
		width = 1.;
		depth = 0.25;
		rod_mass = 1.;
		ring_mass = 5.;
		ring_inner_r = 1.;
		ring_outer_r = 1.2;
		ring_pos = length*0.5;
		setMoveable(false);
		
		// initialize
		//AxisAngle4d aa = new AxisAngle4d(new Vector3d(pivot_axis), Math.PI*0.25);
		//Quat4d rot = new Quat4d();
		//rot.set(aa);
		//setRotation(rot);
	}
	
	double calcMomentOfInertia() {
		double moment = (rod_mass/12.)*(length*length + width*width) + (rod_mass*length*0.5) // rod component
						+ (ring_mass*ring_pos*ring_pos) + 0.5*ring_mass*(ring_inner_r*ring_inner_r+ring_outer_r*ring_outer_r); // ring component
		return moment;
	}
	
	public Vector3d getTorque() {
		
		Vector3d t = new Vector3d(pivot_axis);
		t.normalize();
		double tmag = 0.;
		AxisAngle4d aa = new AxisAngle4d();
		aa.set(orientation_d);
		Vector3d aaxis = new Vector3d(aa.x,aa.y,aa.z);
		double theta = aa.angle * aaxis.dot(pivot_axis);
		
		if (mode == FULL_SOLUTION) {
			tmag = 0.01*(rod_mass*length*0.5 + ring_mass*ring_pos)*theEngine.getGravity().length()*Math.sin(theta);
		}
		// This shouldn't actually be done here.  Moment of inertia should be set normally when any of the 
		// relevent quantities are changed.  But this is ok as long as the "official" MOI is = 1.
		tmag /= calcMomentOfInertia();
		t.scale(-tmag);
		//System.out.println("Pendulum getTorque() = " + t + " theta = " + theta);
		return t;
	}
	
	protected TNode3D makeNode() {
		
		//TNode3D node = (TNode3D) new BoxNode();
		TNode3D node = (TNode3D) new WeightedPendulumNode();
		//node.setScale(new Vector3d(0.5,length,0.5));
		((WeightedPendulumNode)node).updateRodGeometry(length,width,depth);
		((WeightedPendulumNode)node).updateRingGeometry(ring_inner_r,ring_outer_r,0.4);
		((WeightedPendulumNode)node).setRingPosition(ring_pos);
		//((WeightedPendulumNode)node).setBoxScale(new Vector3d(0.5,length,0.5));
		System.out.println("rod length = " + length + " ring_pos = " + ring_pos);
		return node;
	}
	
	public void render() {
		if ((renderFlags & SCALE_CHANGE) == SCALE_CHANGE) {
			//((WeightedPendulumNode)mNode).setBoxScale(new Vector3d(0.5,length,0.5));
			((WeightedPendulumNode)mNode).updateRodGeometry(length,width,depth);
			renderFlags ^= SCALE_CHANGE;
		}
		if (ringChanged) {
			((WeightedPendulumNode)mNode).setRingPosition(ring_pos);
			((WeightedPendulumNode)mNode).updateRingGeometry(ring_inner_r, ring_outer_r, 0.4 );
			ringChanged = false;
		}
		super.render();
	}
	
	/**
	 * @return Returns the depth.
	 */
	public double getDepth() {
		return depth;
	}
	/**
	 * @param depth The depth to set.
	 */
	public void setDepth(double depth) {
		this.depth = depth;
	}
	/**
	 * @return Returns the length.
	 */
	public double getLength() {
		return length;	
	}
	/**
	 * @param l The length to set.
	 */
	public void setLength(double l) {
		double rel = ring_pos/length;
		this.length = l;
		setRing_pos(l*rel);
		renderFlags |= SCALE_CHANGE;
		theEngine.requestSpatial();
	}
	/**
	 * @return Returns the ring_inner_r.
	 */
	public double getRing_inner_r() {
		return ring_inner_r;
	}
	/**
	 * @param ring_inner_r The ring_inner_r to set.
	 */
	public void setRing_inner_r(double ring_inner_r) {
		this.ring_inner_r = ring_inner_r;
		setRingChanged(true);
		theEngine.requestSpatial();
	}
	/**
	 * @return Returns the ring_mass.
	 */
	public double getRing_mass() {
		return ring_mass;
	}
	/**
	 * @param ring_mass The ring_mass to set.
	 */
	public void setRing_mass(double ring_mass) {
		this.ring_mass = ring_mass;
	}
	/**
	 * @return Returns the ring_outer_r.
	 */
	public double getRing_outer_r() {
		return ring_outer_r;
	}
	/**
	 * @param ring_outer_r The ring_outer_r to set.
	 */
	public void setRing_outer_r(double ring_outer_r) {
		this.ring_outer_r = ring_outer_r;
		setRingChanged(true);
		theEngine.requestSpatial();
	}
	/**
	 * @return Returns the rod_mass.
	 */
	public double getRod_mass() {
		return rod_mass;
	}
	/**
	 * @param rod_mass The rod_mass to set.
	 */
	public void setRod_mass(double rod_mass) {
		this.rod_mass = rod_mass;
	}
	/**
	 * @return Returns the width.
	 */
	public double getWidth() {
		return width;
	}
	/**
	 * @param width The width to set.
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	/**
	 * @return Returns the ring_pos.
	 */
	public double getRing_pos() {
		return ring_pos;
	}
	/**
	 * @param pos The pos of the ring to set.
	 */
	public void setRing_pos(double pos) {
		if (pos > length) pos = length;
		if (pos < 0.1) pos = 0.1;
		this.ring_pos = pos;
		setRingChanged(true);
		theEngine.requestSpatial();
	}
	
	private void setRingChanged(boolean t) {
		ringChanged = t;
	}
	/**
	 * @return Returns the pivot_axis.
	 */
	public Vector3d getPivot_axis() {
		return pivot_axis;
	}
	/**
	 * @param pivot_axis The pivot_axis to set.
	 */
	public void setPivot_axis(Vector3d pivot_axis) {
		this.pivot_axis = pivot_axis;
	}
}
