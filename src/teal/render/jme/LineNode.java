/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: LineNode.java,v 1.8 2010/08/27 22:31:12 stefan Exp $ 
 * 
 */
package teal.render.jme;

import javax.vecmath.Vector3d;

import com.jme.math.Vector3f;
import com.jme.scene.Line;

import teal.render.HasColor;
import teal.render.TAbstractRendered;
import teal.sim.properties.HasFromTo;

public class LineNode extends ShapeNode implements HasFromTo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7695893891814012335L;
	
	//FIXXME: this is used by SolidArrowNode and just needed to keep this
	// weird hierarchy where an Arrow is inherited from a line.
	// as soon as this hierarchy is not needed outside any more, we
	// should fix that - stefan
	protected final static String LINE_NAME = "defaultLine";

	public LineNode() {
		super();
		setRotable(true);
		setDefaultGeometry();
	}
	
	public LineNode(TAbstractRendered element){
		this();
		doSetElement(element,false);
		if ((element != null) && (element instanceof HasColor))
		{
			setColor(((HasColor)element).getColor());
		}				
	}
	
	protected void setDefaultGeometry() {
		this.attachChild(new Line(LINE_NAME, new Vector3f [] {
				new Vector3f(0,0,0), new Vector3f(0,1,0)
		} ,null, null, null));		
	}

	
	public void setFromTo(Vector3d from3d, Vector3d to3d) {
		Vector3f from = new Vector3f((float)from3d.x, (float)from3d.y, (float)from3d.z);
		Vector3f vec = new Vector3f((float)to3d.x, (float)to3d.y, (float)to3d.z);
		
		vec.subtractLocal(from);
		float length = vec.length();
		if(length > 0) {
			if(!isVisible())
				setVisible(true);
			this.setLocalTranslation(from);
			
			//sets direction
			setDirection(vec.normalize());
			this.setLocalScale(new Vector3f(0,length,0));
		} else if (isVisible()) {
			setVisible(false);
		}
	
	}
	
	

}
