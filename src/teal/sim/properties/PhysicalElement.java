/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PhysicalElement.java,v 1.24 2007/12/04 21:00:51 pbailey Exp $ 
 * 
 */

package teal.sim.properties;

import teal.core.Referenced;
import teal.math.Integratable;
import teal.render.*;
import teal.sim.TSimElement;
import teal.sim.engine.HasSimEngine;

/**
 *
 *
 * @author Phil Bailey
 * @version $Revision: 1.24 $
 */

public interface PhysicalElement extends TSimElement, HasSimEngine, HasPosition, IsMoveable, IsPickable, TDrawable, Integratable, HasVelocity,
    HasMass, Referenced {

}
