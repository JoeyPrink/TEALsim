/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FogBehavior.java,v 1.2 2010/03/25 21:00:54 stefan Exp $ 
 * 
 */

package teal.render.jme;


import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnTransformChange;

import teal.render.viewer.AbstractViewer3D;
import teal.render.viewer.TViewer;
import teal.render.viewer.TViewer3D;
import teal.render.viewer.Viewer;

/**
 * This is a behavior for keeping the FOG at bay.  It checks for viewport transform changes and adjusts the fog depth according to the new
 * camera transform, such that objects never disappear into the fog (or come out of the fog) completely when you zoom in or out. 
 * Note: for now it is calibrated to work best with objects at the origin or in the xy plane.  I should add a setFogWidth() method in 
 * ViewerJ3D that allows you to change the depth this behaviour sets, to account for scenes that may have more of an extension in the z 
 * direction.
 */	
public class FogBehavior extends Behavior{

	protected TViewer3D mViewer;
	protected TransformGroup tGroup = null;
	protected Transform3D trans = new Transform3D();
	
	
	
	public FogBehavior(TViewer3D viewer){
		mViewer = viewer;
		if( viewer instanceof HasUniverse)
		{
			tGroup = ((HasUniverse)mViewer).getUniverse().getViewingPlatform().getViewPlatformTransform();
		}
	}

	
	public void initialize(){
		
		wakeupOn(new WakeupOnTransformChange(tGroup));
	}

	@SuppressWarnings("unchecked")
	public void processStimulus(Enumeration stim){
		
		wakeupOn(new WakeupOnTransformChange(tGroup));
		setEnable(true);
		
		tGroup.getTransform(trans);
		((TViewer3D)mViewer).setFogTransform(trans);
		
	}

}


