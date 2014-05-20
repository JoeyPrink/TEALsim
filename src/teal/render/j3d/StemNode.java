/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: StemNode.java,v 1.14 2007/08/17 19:38:29 jbelcher Exp $ 
 * 
 */

package teal.render.j3d;

import javax.vecmath.Vector3d;

import teal.render.TAbstractRendered;

/**
 * Node for rendering a solid line (that is a cylinder as opposed to a line).  This sentence prevents javadoc errors.  
 */
public class StemNode extends LineNode
{
	protected double radius = 1.; 

    public StemNode(){
		super();
		setDefaultGeometry();
    }
	
	public StemNode(TAbstractRendered element){
		super(element);	
	}
 
    protected void setDefaultGeometry()
    {
        setGeometry(sStem,0);
    }
 
	public void setFromTo(Vector3d from, Vector3d to) {
		Vector3d tmp = new Vector3d(to);
		tmp.sub(from);
		double length = tmp.length();
		if (length > 0) {
			if (!isVisible())
				setVisible(true);
			//setScale(length);
			setScale(new Vector3d(radius, length, radius));
			setPosition(from);
			setDirection(tmp);
		} else if (isVisible())
			setVisible(false);
	}
	/**
	 * @return Returns the radius.
	 */
	public double getRadius() {
		return radius;
	}
	/**
	 * @param radius The radius to set.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}
}






