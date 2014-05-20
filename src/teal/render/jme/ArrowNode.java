/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ArrowNode.java,v 1.9 2010/08/27 22:31:11 stefan Exp $ 
 * 
 */
package teal.render.jme;

import com.jme.scene.Node;

import teal.render.HasColor;
import teal.render.TAbstractRendered;

public class ArrowNode extends LineNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5884694212838987028L;
	
	public ArrowNode() {
		super();
	}
	
	@Override
	protected void setDefaultGeometry() {
		super.setDefaultGeometry();
		UprightCylinder cone = new UprightCylinder("arrow cone", 10, 10, Node3D.coneRadius, 0, Node3D.coneHeight, true, false);
		Node coneparent = new Node("ConeHolder"); //needed for extra translation. Otherwise the translation of 0,1,0 would
												  //be overwritten by Node3D's "setModelOffset..."-methods. Same in j3d package
		cone.setLocalTranslation(0, 1, 0);
		coneparent.attachChild(cone);
		this.attachChild(coneparent);
	}
	
	public ArrowNode(TAbstractRendered element){
		this();
		doSetElement(element,false);
		if ((element != null) && (element instanceof HasColor)) {
			setColor(((HasColor)element).getColor());
		}				
	}
	
}
