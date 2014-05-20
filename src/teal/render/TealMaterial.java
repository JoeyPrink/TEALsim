/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TealMaterial.java,v 1.5 2010/07/06 20:16:08 stefan Exp $ 
 * 
 */

package teal.render;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

import teal.core.*;


/** 
 */



public class TealMaterial extends AbstractElement implements TMaterial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6640557580378573840L;
	
	protected int mCullMode = TMaterial.CULL_NONE;
	protected int mFaceMode = TMaterial.FACE_FILL;
	protected float mShininess = 0.0f;

	//  0.0 being fully opaque and 1.0 being fully transparent.
	protected float mTransparancy = 0.0f;

	protected Color4f mAmbient;
	protected Color4f mDiffuse;
	protected Color4f mEmisive;
	protected Color4f mSpecular;

	public TealMaterial(){
		mDiffuse = new Color4f();
	}

	public TealMaterial(Color color){
		super();
		setDiffuse(new Color4f(color));
	}
	public TealMaterial(Color color,float shininess, float trans){
		super();
		setDiffuse(new Color4f(color));
		mShininess = shininess;
		mTransparancy = 1- trans;
	}
	
	public TealMaterial(Color4f color,float shininess, float trans){
		super();
		setDiffuse(color);
		mShininess = shininess;
		mTransparancy = 1 - trans;
	}
	public void setShininess(float shine){
		mShininess = shine;
	}
	public float getShininess(){
		return mShininess;
	}
	    
	/**
	 * @param trans transparency [0-1]. 0 means opaque
	 */
	public void setTransparancy(float trans){
		mTransparancy = trans;
		if(mAmbient != null) mAmbient.w = 1 -trans;
		if(mDiffuse != null) mDiffuse.w = 1 -trans;
		if(mEmisive != null) mEmisive.w = 1 -trans;
		if(mSpecular != null) mSpecular.w = 1 -trans;
	}
	    
	/**
	 * returns a transparency value between 0.0 and 1.0, where
	 * 0.0 means opaque
	 */
	public float getTransparancy(){
		ArrayList<Float> trans = new ArrayList<Float>();
		if(mAmbient != null) trans.add(1-mAmbient.w);
		if(mDiffuse != null) trans.add(1-mDiffuse.w);
		if(mEmisive != null) trans.add(1-mEmisive.w);
		if(mSpecular != null) trans.add(1-mSpecular.w);
		
		if(trans.size() == 0)
			return mTransparancy;
	    		
		float sum = 0.0f;
		for(Float tr:trans)
			sum += tr;
		mTransparancy = sum / (float)trans.size();
		return 	mTransparancy;	    		
	}

	public void setCullMode(int mode){
		mCullMode = mode;
	}
	public int getCullMode(){
		return mCullMode;
	}
	public void setFaceMode(int mode){
		mFaceMode = mode;
	}
	public int getFaceMode(){
		return mFaceMode;
	}
	   
	public void setAmbient(Color3f col){	   
		mAmbient = getColor4f(col);	
	}
	public void setAmbient(Color4f col){
		mAmbient = col;
	}
	    
	public Color4f getAmbient(){
		return  mAmbient;
	}
//	public void setColor(Color4f col){
//		setColor(new Color3f(col.x,col.y,col.z));
//	}
	    
	public void setColor(Color3f col){
		
		mDiffuse = getColor4f(col);
		mSpecular = getColor4f(col);
		col.scale(0.9f);
		mAmbient = getColor4f(col);
	}
//	public void setColor(Color col){
//		setColor(new Color3f(col));
//	}
	public Color4f getColor(){
		return mDiffuse;
	}    
	public void setDiffuse(Color4f col){
		mDiffuse = col;
	}
	    
	public void setDiffuse(Color3f col){
		mDiffuse = getColor4f(col);
	}
	public void setDiffuse(Color col){
		mDiffuse = new Color4f(col);
	}
	public Color4f getDiffuse(){
		return mDiffuse;
	}
	public void setEmissive(Color3f col){
		mEmisive = getColor4f(col);
	}
	public void setEmissive(Color4f col){
		mEmisive = col;
	}

	public Color4f getEmissive(){
		return mEmisive;
	}
	public void setSpecular(Color3f col){
		mSpecular = getColor4f(col);
	}
	public void setSpecular(Color4f col){
		mSpecular = col;
	}
	public Color4f getSpecular(){
		return mSpecular;
	}
	   
	public void set(TMaterial mat){
		mCullMode = mat.getCullMode();
		mFaceMode = mat.getFaceMode();
		mShininess = mat.getShininess();
		mTransparancy = mat.getTransparancy();
		mAmbient = mat.getAmbient();
		mDiffuse = mat.getDiffuse();
		mEmisive = mat.getEmissive();
		mSpecular = mat.getSpecular();
	}
	    
	public static Color3f getColor3f(Color4f col) {
		if(col == null)	return null;
		return new Color3f(col.x, col.y, col.z);
	}

	protected Color4f getColor4f(Color3f col) {
		if(col == null)	return null;
		float [] color = new float [4];	
		col.get(color);
		color[3] = 1f -mTransparancy;
		return new Color4f(color);
	}
	
}
