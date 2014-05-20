/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: LDPSConstraint.java,v 1.9 2007/07/16 22:05:00 pbailey Exp $ 
 * 
 */

package teal.sim.constraint;

import javax.vecmath.Vector3d;
import teal.config.*;

/**
 * @author mesrob
 *
 */

// LDPS stands for "locally damped point-spring".

public class LDPSConstraint implements Constraint {

	protected Vector3d point = new Vector3d();
	protected double k1 = 1., k2 = 0., p = 1.;
	protected Vector3d lastReaction = new Vector3d();

	public LDPSConstraint() {
	}

	public void setPoint( Vector3d ppoint ) {
		point = new Vector3d(ppoint);
	}	
	public Vector3d getPoint() {
		return new Vector3d(point);
	}	

	public void setK1( double k1 ) {
		this.k1 = k1;
	}
	
	public void setK2( double k2 ) {
		this.k2 = k2;
	}

	public void setP( double p ) {
		this.p = p;
	}

	public Vector3d getReaction(Vector3d position, Vector3d velocity, Vector3d action, double mass) {

		Vector3d relativePosition = new Vector3d(position);
        relativePosition.sub(point);
        double x =  relativePosition.length();
		Vector3d reaction = new Vector3d(relativePosition);
		if( x > Teal.DoubleZero ) {
			reaction.scale(-k1);
		}

		double v = velocity.length();
		Vector3d damping = new Vector3d(velocity);
		if( v > Teal.DoubleZero ) {
			//damping.scale(1./v);
			damping.scale(-k2*(1.0 + 0.0/v)/(1.+(x/p)*(x/p)));
			reaction.add(damping);
		}
		lastReaction.set(reaction);
		return reaction;
	}

/*
	public Vector3d getVelocity(Vector3d position, Vector3d forced_velocity) {
		return forced_velocity;
	}

	public Vector3d getPosition(Vector3d initial_position, Vector3d targeted_position) {
		return targeted_position;
	}
*/
	public void set_default() {
		setPoint(new Vector3d());
		setK1(1.);
		setK2(0.);
		setP(1.);
	}

	public void set( Constraint c ) {
		if( c instanceof LDPSConstraint ) {
			LDPSConstraint c_ = ((LDPSConstraint) c);
			setPoint(c_.point);
			setK1(c_.k1);
			setK2(c_.k2);
			setP(c_.p);
		}	
	}
/*
	public Constraint replica() {
		LDPSConstraint ssc = new LDPSConstraint();
		ssc.set(this);
		return ssc;
	}
*/
	public Vector3d getLastReaction() {
		return this.lastReaction;
	}
}
