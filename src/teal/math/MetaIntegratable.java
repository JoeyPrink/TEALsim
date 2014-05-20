/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: MetaIntegratable.java,v 1.8 2007/07/16 22:04:47 pbailey Exp $ 
 * 
 */

package teal.math;

 /**
 * This interface extends Integratable, and should be implemented by those
 * TealObjects that need to be integrated, but might have internal variables
 * that are interdependent.
 * 
 * Interdependencies are solved for through the solveInterdependencies
 * method, which needs to be called successively and alternatingly for the
 * different MetaIntegrable objects, for a preset number of times, equivalent
 * to the number of iterations in a numerical routine for the solution of a
 * system of nonlinear equations. This is done in SimEngine's setDependentValues().
 */

public interface MetaIntegratable extends Integratable{
  /**
   * Performs a single step towards the solution of interdependent variables.
   */
  public boolean solveInterdependencies();
}

