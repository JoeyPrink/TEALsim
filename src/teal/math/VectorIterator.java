/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: VectorIterator.java,v 1.5 2007/07/16 22:04:48 pbailey Exp $ 
 * 
 */

package teal.math;

import javax.vecmath.Vector3d;

/** VectorIterator:
   *   Interface for producing a sequence of Vector3d values.
   *
   * This interface is mostly used to iterate over a set of locations in
   * a field, plane or data structure, each vector returned may be 
   * a location or a constructed value. For example, RectangularPlane produces the sequence of
   * points going left-right, top-down in scanline order, while
   * RandomGridIterator produces pseudo-random points that cover a
   * rectangular region.
   */

public interface VectorIterator {
   
    /** Returns: null if there are no more points in the sequence, else
   *          a new Vector3d whose value is the next point. The returned Vector3d
   *          may be modified by the caller. The same Vector3d may be
   *          written to again on the subsequent call to nextVec(). */
  public Vector3d nextVec();
  public boolean hasNext();
  public void reset();

}
