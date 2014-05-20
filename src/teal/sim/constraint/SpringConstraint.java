/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SpringConstraint.java,v 1.12 2007/07/16 22:05:00 pbailey Exp $ 
 * 
 */

package teal.sim.constraint;

import javax.vecmath.Vector3d;
import teal.config.*;

/**
 * @author mesrob
 *
 */
public class SpringConstraint implements Constraint {

	/* We will represent a line in parametric notation:
	 *   x = ax*t + x0;
	 *   y = ay*t + y0;
	 *   z = az*t + z0;
	 * 
	 * Of course, [ax, ay, az] represents a "direction" vector,
	 * and [x0, y0, z0] is some reference "point" on the line.
	 * 
	 * Notice that the reference point is not used explicitly in reaction
	 * calculation. That is because it is assumed that the constrained 
	 * object is already on the line, making the only relevant
	 * information that of the direction.
	 */

	protected Vector3d point = null;
	protected double restLength = 1., coefficient = 1.;
	
	protected Vector3d lastReaction = new Vector3d();

	public SpringConstraint( Vector3d ppoint, double rrestLength, double ccoefficient ) {
		setCoefficients( ppoint, rrestLength, ccoefficient );
	}

	public void setCoefficients( Vector3d ppoint, double rrestLength, double ccoefficient  ) {
		point = new Vector3d(ppoint);
		restLength = rrestLength;
		coefficient = ccoefficient;
	}	

	public void setPoint( Vector3d ppoint ) {
		point = new Vector3d(ppoint);
	}	
	public Vector3d getPoint() {
		return new Vector3d(point);
	}	

	public void setRestLength( double rrestLength ) {
		restLength = rrestLength;
	}
	
	public double getRestLength() {
		return restLength;
	}

	public void setCoefficient( double ccoefficient ) {
		coefficient = ccoefficient;
	}
	
	public double getCoefficient() {
		return coefficient;
	}


	public Vector3d getReaction(Vector3d position, Vector3d velocity, Vector3d action, double mass) {
		Vector3d relativePosition = new Vector3d(position);
        relativePosition.sub(point);
        double displacement =  relativePosition.length() - restLength;
		Vector3d reaction = new Vector3d(relativePosition);
		if( reaction.length() > Teal.DoubleZero ) {
			reaction.normalize();
			reaction.scale(-coefficient*displacement);
		}
		if (reaction != null) lastReaction.set(reaction);
		return reaction;
	}
	
	public Vector3d getLastReaction() {
		return lastReaction;
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
		setCoefficients(	new Vector3d(0., 1., 0.),
							1.,
							1. );
	}

	public void set( Constraint c ) {
		if( c instanceof SpringConstraint ) {
			setCoefficients(	((SpringConstraint) c).getPoint(),
								((SpringConstraint) c).getRestLength(),
								((SpringConstraint) c).getCoefficient() );
		}	
	}
/*
	public Constraint replica() {
		return new SpringConstraint(point, restLength, coefficient);
	}
*/
}
