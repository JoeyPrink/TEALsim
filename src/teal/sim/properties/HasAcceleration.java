/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasAcceleration.java,v 1.3 2007/07/16 22:05:07 pbailey Exp $ 
 * 
 */

package teal.sim.properties;

import javax.vecmath.*;

/**
 * WorldObjects that have an acceleration should implement this
 * interface.
 *
 * We assume a WorldObject that implements this interface will store their
 * acceleration in vector format, where the vector is composed of x, y, and z
 * <code>double</code> coordinates.
 *
 * By implementing this interface, a WorldObject allows read/write access
 * to their current acceleration.  They also provide the means to calculate
 * what their acceleration will be given any position, velocity, and time.
 *
 * <code>Vector3d</code> objects can be used to set and retrieve current
 * acceleration.
 *
 * Additionally, double parameters can be passed and returned to avoid the
 * overhead of <code>Vector3d</code> object creation.
 */
public interface HasAcceleration extends HasVelocity {
	
	/* by implementing this interface, we imply we always carry around
	 * our current acceleration and make it read/write accessible to
	 * clients.
	 * These methods provide this functionality
	 */
	public Vector3d getAcceleration();
	public void setAcceleration(Vector3d X);
	
	/* additionally, by implementing this interface, we gaurantee that
	 * we can calculate and return what our acceleration will be given
	 * a position, velocity, and time
	 *
	 * This method provides this functionality.
	 *
	 * Should this be named calculateAcceleration( ... ) to
	 * avoid confusion?
	 */
	public Vector3d getAcceleration(Vector3d position, Vector3d velocity, double time);
}

