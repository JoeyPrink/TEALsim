/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TDrawable.java,v 1.4 2007/07/16 22:04:51 pbailey Exp $ 
 * 
 */

package teal.render;


/**
* An interface for Elements that may be represented in 2D and/or 3D
*/

public interface TDrawable
{
    /** Sets the isDrawn state */
    public void setDrawn(boolean b);
    
    /** if true the Object will be drawn */
    public boolean isDrawn();
    
    /**
    * The optimized commands required to update the objects vizualization, 
    * this expects the underlying visualization objects have been created
    * and any calculations are complete. It may be restricted to methods 
    * that only operate in the rendering thread.
    */
    public void render();
}
	 
