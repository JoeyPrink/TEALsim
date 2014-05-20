/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Constrainable.java,v 1.3 2009/04/24 19:35:55 pbailey Exp $ 
 * 
 */

package teal.sim.constraint;

import java.util.Collection;

/**
 * @author mesrob
 *
 */
public interface Constrainable {

	public boolean isConstrained();
	public void setConstrained(boolean x);

	public void setConstraint(Constraint c);
	public Constraint getConstraint();
	
	// Adding methods for multiple constraints.  Constraints are now stored in an ArrayList.
	public void addConstraint(Constraint c);
    public Collection<Constraint> getConstraints();
    public void clearConstraints();
    public Constraint getConstraintAtIndex(int index);
    public void removeConstraintAtIndex(int index);
    public int getNumConstraints();
	
}
