/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: LineNode.java,v 1.24 2010/04/12 20:13:17 stefan Exp $ 
 * 
 */

package teal.render.j3d;


import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

import teal.render.HasColor;
import teal.render.TAbstractRendered;
import teal.sim.properties.HasFromTo;

/**
 * provides SceneGraphNode for the management of a single line, may be used 
 * stand-alone for a vector representation or as individual  nodes 
 * in an array of arrows.
 *
 * @author Phil Bailey
 * @version $Revision: 1.24 $
 *
 **/

public class LineNode extends ShapeNode implements HasFromTo
{

  


    public LineNode(){
	    super();
        setRotable(true);
	    setDefaultGeometry();
    }
	public LineNode(TAbstractRendered element){
		this();
		setElement(element);
		if ((element != null) && (element instanceof HasColor))
		{
			setColor(((HasColor)element).getColor());
		}				

	}

    protected void setDefaultGeometry()
    {
        mShape.setGeometry(sLine,0);
    }
    
    public double getlength()
    {
        double len = 0;
        Transform3D trans = new Transform3D();
        mTransform.getTransform(trans);
        len = trans.getScale();
        return len;
    }
        
    public void setLength(double len)
    {
        Transform3D trans = new Transform3D();
        mTransform.getTransform(trans);
        trans.set(len);
        mTransform.setTransform(trans);
    }
    
	public void setFromTo(Vector3d from, Vector3d to) {
		Vector3d tmp = new Vector3d(to);
		tmp.sub(from);
		double length = tmp.length();
		if (length > 0) {
			if (!isVisible())
				setVisible(true);
			setPosition(from);
			setDirection(tmp);
			setScale(new Vector3d(1., length, 1.));
		} else if (isVisible())
			setVisible(false);
	}


}
