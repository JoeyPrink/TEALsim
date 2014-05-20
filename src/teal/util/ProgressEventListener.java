/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ProgressEventListener.java,v 1.6 2007/08/16 22:09:40 jbelcher Exp $ 
 * 
 */

package teal.util;


/** This class defines the behavior of an object that wishes to receive
  * periodic updates on the progress of a lengthy task.
  */

public interface ProgressEventListener 
  {
  /** Set the progress amount to <code>progress</code>, which is a value
    * between 0 and 100. (Out of range values should be silently clipped.)
    *
    * @param progressEvent The percentage of the task completed.
    */

  public void setProgress(ProgressEvent progressEvent);
  }
