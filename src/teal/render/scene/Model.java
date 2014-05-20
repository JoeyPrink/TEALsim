/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Model.java,v 1.1 2010/06/07 23:12:46 pbailey Exp $ 
 * 
 */

package teal.render.scene;

import java.io.Serializable;

import javax.vecmath.Vector3d;

public class Model implements Serializable{
	
	private String path;
	private String texturePath;
	private Vector3d offset;
	private Vector3d scale;
	transient private  TNode3D theModel = null;
	
	public Model(){
	}
	
	public Model(String path){
		this();
		this.path =path;
	}
	
	public Model(String path, String texturePath){
		this(path);
		this.texturePath = texturePath;
	}
	
	public String getPath(){
		return path;
	}
	public void setPath(String path){
		this.path = path;
	}
	public String getTexturePath(){
		return texturePath;
	}
	public void setTexturePath(String texturePath){
		this.texturePath = texturePath;
	}
	
	public Vector3d getOffset(){
		return offset;
	}
	public void setOffset(Vector3d offset){
		this.offset = offset;
	}
	
	public Vector3d getScale(){
		return scale;
	}
	public void setScale(Vector3d scale){
		this.scale = scale;
	}
	
	public void setScale(double value){
		if(scale == null)
			scale = new Vector3d();
		scale.x = value;
		scale.y = value;
		scale.z = value;
	}

}
