/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SolidArrowNode.java,v 1.11 2007/07/16 22:04:55 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import teal.render.TAbstractRendered;

/**
 * Node for rendering a "solid arrow".  A solid arrow differs from a regular arrow in that the shaft of a solid 
 * arrow is a cylinder, rather than a line.
 */
public class SolidArrowNode extends ArrowNode
{

    public SolidArrowNode(){
		super();

    }
	
	public SolidArrowNode(TAbstractRendered element){
		super(element);

	}
    
    
    protected void setDefaultGeometry()
    {
        mShape.setGeometry(sStem,0);
    }

}






