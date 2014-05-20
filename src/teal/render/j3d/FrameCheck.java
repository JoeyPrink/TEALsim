/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FrameCheck.java,v 1.10 2009/12/22 15:30:53 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import java.util.Enumeration;

import javax.media.j3d.*;

import teal.render.TRenderEngine;
import teal.render.viewer.Viewer;
	
public class FrameCheck extends Behavior{

	protected TRenderEngine mViewer;
	protected int mCount = 1;
	protected boolean isPassive = false;
	
	public FrameCheck(TRenderEngine viewer){
		mViewer = viewer;
	}

	public FrameCheck(TRenderEngine viewer, int count){
		mViewer = viewer;
		mCount = count;
	}

	public void initialize(){
		wakeupOn(new WakeupOnElapsedFrames(mCount,isPassive));
	}

	@SuppressWarnings("unchecked")
	public void processStimulus(Enumeration stim){
		//TDebug.println(1,"frameCheck:triggered");
		wakeupOn(new WakeupOnElapsedFrames(mCount,isPassive));
		setEnable(false);
		mViewer.renderComplete();
	}

}


