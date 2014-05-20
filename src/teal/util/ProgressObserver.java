/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ProgressObserver.java,v 1.4 2007/07/16 22:05:17 pbailey Exp $ 
 * 
 */

package teal.util;

/** This class defines the behavior of an object that wishes to receive
  * periodic updates on the progress of a lengthy task.
  */

public interface ProgressObserver 
  {
  /** Set the progress amount to <code>progress</code>, which is a value
    * between 0 and 100. (Out of range values should be silently clipped.)
    *
    * @param progress The percentage of the task completed.
    */

  public void setProgress(int progress);
  }

