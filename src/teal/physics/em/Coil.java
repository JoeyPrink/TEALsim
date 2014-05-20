/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Coil.java,v 1.21 2010/09/22 15:48:09 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import teal.render.j3d.*;
import teal.render.j3d.geometry.Pipe;
import teal.render.scene.*;
import teal.sim.properties.HasLength;

/**
 * This class extends RingOfCurrent, by providing a new graphical node. All
 * other functionalities are inherited from RingOfCurrent.
 * 
 * @see teal.physics.em.RingOfCurrent
 */

public class Coil extends RingOfCurrent implements HasLength{

	// *************************************************************************
	// Constructor and Standard Methods
	// *************************************************************************

    private static final long serialVersionUID = 3978702878875858488L;
    double length = 0.5;;
    public Coil() {
		super();
		nodeType=nodeType.PIPE;
		mMaterial.setShininess(0.7f);
	}

	public String toString() {
		return "Coil: " + id;
	}
	public double getLength(){
		return length;
		
	}
	
	public void setLength(double len){
		length = len;
	}

	// *************************************************************************
	// Render and Graphics Methods
	// *************************************************************************

	protected TNode3D makeNode() {
		TShapeNode node = (TShapeNode) SceneFactory.makeNode(this);
		node.setColor(teal.render.TealMaterial.getColor3f(mMaterial.getDiffuse()));
		node.setShininess(mMaterial.getShininess());
		node.setPickable(isPickable);
		renderFlags ^= GEOMETRY_CHANGE;
		return node;
	}

	public void render() {
		if (mNode != null) {
			if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {

				if (mNode instanceof TShapeNode) {

					((TShapeNode) mNode).setGeometry(Pipe.makeGeometry(20,
							radius, torusRadius, torusRadius));
				}
				renderFlags ^= GEOMETRY_CHANGE;
			}
			super.render();
		}
	}

}
