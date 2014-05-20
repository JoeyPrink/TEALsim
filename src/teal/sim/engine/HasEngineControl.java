/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HasEngineControl.java,v 1.2 2007/12/04 21:00:35 pbailey Exp $ 
 * 
 */

package teal.sim.engine;


public interface HasEngineControl {

    public TEngineControl getEngineControl();
    public void setEngineControl(TEngineControl engineControl);
}
