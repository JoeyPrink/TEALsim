/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TMaterial.java,v 1.7 2010/07/06 19:31:27 pbailey Exp $ 
 * 
 */

package teal.render;

import java.awt.Color;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

import teal.render.scene.TSceneObject;

/** Interface for the
 */

public interface TMaterial extends TSceneObject{
	public final static int CULL_NONE = 0;
	  public final static int CULL_BACK = 1;
	    public final static int CULL_FRONT = 2;
	    public final static int CULL_BOTH =CULL_BACK | CULL_FRONT;
	    
	    public final static int FACE_FILL = 0;
		  public final static int FACE_LINES = 1;
		  public final static int FACE_POINTS = 2;
		
	    
	    public void setShininess(float shine);
	    public float getShininess();
	    // Trans 0.0 being fully opaque and 1.0 being fully transparent.
	    public void setTransparancy(float trans);
	    public float getTransparancy();
	    public void setCullMode(int mode);
	    public int getCullMode();
	    public void setFaceMode(int mode);
	    public int getFaceMode();
	    public Color4f getColor();
	    public void setColor(Color3f col);
	    //public void setColor(Color4f col);
	    //public void setColor(Color col);
	    public Color4f getEmissive();
	    public Color4f getDiffuse();
	    public void setDiffuse(Color3f col);
	    public void setDiffuse(Color4f col);
	    public void setDiffuse(Color col);
	    public Color4f getSpecular();
	    public Color4f getAmbient();
	    public void setEmissive(Color4f col);
	    public void setEmissive(Color3f col);
	    public void setSpecular(Color3f col);
	    public void setSpecular(Color4f col);
	    public void setAmbient(Color3f col);
	    public void setAmbient(Color4f col);
	    public void set(TMaterial mat);

}
