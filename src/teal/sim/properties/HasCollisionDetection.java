/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasCollisionDetection.java,v 1.9 2009/04/24 19:35:56 pbailey Exp $ 
 * 
 */

package teal.sim.properties;

import java.util.*;


/**
 * Currently a very simple interface to handle  collisions, 
 * note any physical object may collide but only classes which
 * implement this will do the checking.
 *
 */

public interface HasCollisionDetection {

/**
* This will check if a Physical object has moved within the collision bounds
* 

* Should re-think this and add better resolution of resulting intgration and object placement.
*/
  public boolean getCollisionEnabled();
  public void setCollisionEnabled(boolean b);
  public boolean checkCollision(PhysicalElement obj);
  public void checkCollisions(Collection<PhysicalElement> objs);


}
