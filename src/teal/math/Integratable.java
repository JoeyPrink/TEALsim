/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Integratable.java,v 1.11 2007/07/16 22:04:47 pbailey Exp $ 
 * 
 */

package teal.math;


/**
 * This interface should be implemented by TealObjects that need to have their
 * properties updated via numerical integration. <br>
 * <br>
 * It assumes you have at least one property (a dependent variable) which
 * changes with respect to another property (a single, independent variable). by
 * implementing this interface you make information concerning the value of your
 * dependent variables and their derivatives available via get/set methods. This
 * information will be used by an integration scheme to update your dependent
 * properties) <br>
 * This information will be used by an integration scheme to calculate new
 * values of your dependent properties as your independent property changes.
 * <br>
 * for example, <br>
 * a PointCharge that wants to update its position and velocity would implement
 * this interface. The dependent properties would be position and velocity, the
 * independent property would be time. <br>
 * When queried for <code>getDependentValues</code>, the PointCharge would
 * return a <code>double[]</code> containing the x, y, and z components of its
 * position and the x, y, z, components of its velocity. <br>
 * When queried for <code>getDependentDerivatives</code>, the PointCharge
 * would return a <code>double[]</code> containing the values of the
 * derivatives of the dependent variables, calculated using a set of values for
 * your dependent variables and a value for your independent variable. <br>
 * When <code>setDependentValues</code> is invoked, the PointCharge would
 * receive a <code>double[]</code> containing new values for its dependent
 * variables. The order of the values in this array would match the order of the
 * values returned by <code>getDependentDerivatives
 * </code>.<br>
 * When queried for <code>getIndependentValue</code>, this PointCharge would
 * return the current time.
 */
public interface Integratable {
	/**
	 * Return the current values of your dependent variable(s) in an array of
	 * doubles.
	 * 
	 * @return A <code>double[]</code> containing the values of your dependent
	 *         variables.
	 */
	public void getDependentValues(double[] depValues, int offset);

	/**
	 * Return the values of the derivatives of your dependent variables, given a
	 * set of values for your dependent variables and a specific value of your
	 * independent variable.
	 * 
	 *  depValues
	 *            <code>double[]</code> containing values of your dependent
	 *            variables that you should use to calculate values of dependent
	 *            derivatives
	 * 
	 * @param indepValue
	 *            <code>double</code> holding the value of the independent
	 *            variable that you should use to calculate values of dependent
	 *            derivatives.
	 * 
	 * @return a <code>double[]</code> containing values of the derivatives of
	 *         your dependent variables given a set of dependent values and an
	 *         independent value.
	 */
	public void getDependentDerivatives(double[] depDerivatives, int offset,
			double indepValue);

	/**
	 * Given a <code>double[]</code>, update the values of your dependent
	 * variables to the doubles contained within. These doubles will be in the
	 * same order returned by <code>getDependentValues</code>.
	 * 
	 *  newDepValues
	 *            a <code>double[]</code> to which you set the values of your
	 *            dependent variables.
	 */
	public void setDependentValues(double[] depValues, int offset);

	/**
	 * Return the value of your independent variable as a double.
	 * 
	 * @return The independent value your dependent variables are changing with.
	 */
	//public double getIndependentValue();
	public int getNumberDependentValues();

	/**
	 * Is this Integratable object actually responsible for contributing to the
	 * object to calculate the dynamic changes.
	 */
	public boolean isIntegrating();

	/**
	 * Sets if dynamic processing for this object is performed by the object.
	 */
	public void setIntegrating(boolean b);

	/*
	 * provides a the ability to reconcile the 'published' values with the
	 * shadow values used in integration. This does not update any visuals nor
	 * does it generate PropertyChangeEvents, <code> update() </code> will still
	 * need to be called.
	 * 
	 * @see teal.core.TUpdatable
	 * @deprecated
	 */

	//public void reconcile();
}
