/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ArrowNode.java,v 1.18 2007/07/16 22:04:53 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import javax.media.j3d.*;
import javax.vecmath.Vector3f;

import teal.render.*;

/**
 * provides SceneGraphNode for the management of a single arrow, may be used 
 * stand-alone for a vector representation or as individual  nodes 
 * in an array of arrows.
 *
 * @author Phil Bailey
 * @version $Revision: 1.18 $
 *
 **/

public class ArrowNode extends LineNode
{
  
    protected Shape3D mCone = null;
    protected TransformGroup mConeTrans = null;

    public ArrowNode(){
		super();
		mCone = new Shape3D(sCone);
	        mCone.addGeometry(sBase);
	        initShape(mCone);
		Transform3D tran = new Transform3D();
		tran.set( new Vector3f(0.f,1.f,0.f));
		mConeTrans = new TransformGroup();
		mConeTrans.setTransform(tran);
		mConeTrans.addChild(mCone);
		mContents.addChild(mConeTrans);
    }
	
	public ArrowNode(TAbstractRendered element){
		this();
		setElement(element);
		if ((element != null) && (element instanceof HasColor)) {
			setColor(((HasColor)element).getColor());
		}				
	}
    
	public void setAppearance(Appearance app){
		super.setAppearance(app);
		mCone.setAppearance(app);
	}
	
}






