/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SolidArrowNode.java,v 1.9 2010/06/28 23:07:32 stefan Exp $ 
 * 
 */
package teal.render.jme;

import com.jme.math.Vector3f;
import com.jme.scene.Node;

import teal.render.TAbstractRendered;

public class SolidArrowNode extends ArrowNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8617196873787791275L;

	public SolidArrowNode() {
		super();
	}
	
	public SolidArrowNode(TAbstractRendered element) {
		super(element);
	}
		
	@Override
	protected void setDefaultGeometry() {
		super.setDefaultGeometry();
		//removing line
		this.detachChildNamed(LINE_NAME);
		
		//attaching stem
		UprightCylinder newCyl = new UprightCylinder("ArrowStem",4,Node3D.stemSegments,
				Node3D.stemRadius,Node3D.stemHeight,true);		
		newCyl.setLocalTranslation(new Vector3f(0f,Node3D.stemOffset,0f));
		Node cylHolder = new Node("cylinder holder");
		cylHolder.attachChild(newCyl);
		this.attachChild(cylHolder);
	}
}
