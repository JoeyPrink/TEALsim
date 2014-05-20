/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TransformGizmo.java,v 1.16 2010/07/16 21:41:35 stefan Exp $ 
 * 
 */

package teal.render.j3d;


import javax.media.j3d.*;
import javax.vecmath.*;

import teal.config.Teal;
import teal.render.HasPosition;
import teal.render.HasRotation;
import teal.render.TAbstractRendered;
import teal.render.scene.TNode;
import teal.util.TDebug;

/**
 * The TransformGizmo provides a TransformGroup for use by behaviors, 
 * contents are children of an internal transformGroup which may be manipulatied 
 * independently from the base TransformGroup. Currntly this is not a BranchGroup.
 *
 * Any geometry should be constructed with (0,0,0) as the center
 * as rotation and position are relative to absolute origin.
 *
 *
 * @author Phil Bailey
 * @version $Revision: 1.16 $
 *
 **/

public class TransformGizmo
    extends TransformGroup implements TNode, HasRotation
{

    //public final static Vector3d refDirection;
    //public final static Vector3d refPosition;    
 
	protected boolean selected = false;
	protected boolean picked = false;
	protected boolean pickable = false;
	protected boolean selectable = false;
    protected boolean isRotable = true;
    protected boolean isRotating = true;
    protected boolean isVisible = false;
    protected boolean isShown = false;

    /** visiblity controls */
    protected Switch mSwitch;
    protected TransformGroup mContents;


    public TransformGizmo(){
	super();
        
    setCapability(Group.ALLOW_CHILDREN_EXTEND);
    setCapability(Group.ALLOW_CHILDREN_READ);
    setCapability(Group.ALLOW_CHILDREN_WRITE);
    setCapability(Node.ALLOW_PICKABLE_READ);
    setCapability(Node.ALLOW_PICKABLE_WRITE);
    setCapability(Node.ENABLE_PICK_REPORTING);
    setCapability(Node.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    
    mSwitch = new Switch();
    mSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
    mSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
    mSwitch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    mSwitch.setCapability(Group.ALLOW_CHILDREN_READ);
    mSwitch.setCapability(Group.ALLOW_CHILDREN_WRITE);
    mSwitch.setCapability(Node.ALLOW_PICKABLE_READ);
    mSwitch.setCapability(Node.ALLOW_PICKABLE_WRITE);
    mSwitch.setCapability(Node.ENABLE_PICK_REPORTING);
    mSwitch.setCapability(Node.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    addChild(mSwitch);


    mContents = new TransformGroup();
    mContents.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    mContents.setCapability(Group.ALLOW_CHILDREN_READ);
    mContents.setCapability(Group.ALLOW_CHILDREN_WRITE);
    mContents.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    mContents.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    mContents.setCapabilityIsFrequent(Node.ENABLE_PICK_REPORTING);	
    mContents.setCapability(Node.ENABLE_PICK_REPORTING);
    mContents.setCapability(Node.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
		
    mSwitch.addChild(mContents);
	
   setVisible(false);
    }
    
    public TransformGizmo(TAbstractRendered element){
	this();
	setElement(element);
    }
    
    public void detach()
    {
    };	
     public void setElement(TAbstractRendered element){
	setUserData(element);
    if (element != null)
    {
	    if(element instanceof HasPosition)
	        setPosition(((HasPosition)element).getPosition());
	    if(element instanceof HasRotation)
	        setRotation(((HasRotation)element).getRotation());
     }    
     }    
    public TAbstractRendered getElement(){
    	return (TAbstractRendered) getUserData();
    }
    
    /**
    * This wraps the TransformGroup method to support the HasTransform interface.
    */
    public Transform3D getTransform()
	{
        Transform3D trans = new Transform3D();
        getTransform(trans);
        return trans;
	}
    // Use the TransformGroup method
	//public void setTransform(Transform3D trans)
	
    
    public void setRotable(boolean rotable)
    {
    	isRotable = rotable;
    }
 
 	public boolean isRotable()
	{
		return isRotable;
	}
    public void setRotating(boolean state)
    {
    	isRotating = state;
    }
 
 	public boolean isRotating()
	{
		return isRotating;
	}

	public Bounds getBoundingArea()
	{
		return getBounds();
	}
    
    public boolean isPickable()
	{return pickable;
	}
    
    /** 
    * This in combination with setPickMode need to be looked at */
	public void setPickable(boolean b)
	{
		pickable = b;
		super.setPickable(b);
		mContents.setPickable(b);	
	}

    public boolean isPicked(){return picked;}	
	public boolean getPicked(){return picked;}
	public void setPicked(boolean b){picked = b;}
    
	public boolean isSelected(){return selected;}
	public void setSelected(boolean b){selected = b;}
    
	public boolean isSelectable(){return selectable;}
	public void setSelectable(boolean b){selectable = b;}

    public void setShown(boolean state)
    {
        isShown = state;
    }
    public boolean isShown()
    {
        return isShown;
    }
    public void setPosition(Vector3d pos){
	Transform3D tr = new Transform3D();
		getTransform(tr);
		tr.setTranslation(pos);
		setTransform(tr);
    }

    public Vector3d getPosition()
    {
    	Vector3d pos = new Vector3d();
    	Transform3D trans = new Transform3D();
    	getTransform(trans);
    	trans.get(pos);
    	return pos;
    }

    
    public Vector3d getScale()
    {
    	Vector3d s = new Vector3d();
    	Transform3D trans = new Transform3D();
    	getTransform(trans);
    	trans.getScale(s);
    	return s;
    }
    /*
    public double getScale()
    {
    	double s = 0;
    	Transform3D trans = new Transform3D();
    	getTransform(trans);
    	s = trans.getScale();
    	return s;
    }
 */
    public void setScale(double s){
    	Transform3D trans = getTransform3D();
    	trans.setScale(s);
    	setTransform(trans);
    }
    
    public void setScale(Vector3d s){
    	Transform3D trans = getTransform3D();
    	trans.setScale(s);
    	setTransform(trans);
    }


    public Quat4d getRotation()
    {
	/* New way: use the getTransform3D() method. */
	Transform3D trans = getTransform3D();
    	Quat4d rot = new Quat4d();
    	trans.get(rot);
    	return rot;

    }


 
	public void setRotation(Matrix3d rot) {
		Transform3D tr = getTransform3D();
		tr.setRotation(rot);
		setTransform(tr);
		//TDebug.println(0,"setRotationR: " + tr);
	}

	public void setRotation(Quat4d quat) {

		Transform3D tr = getTransform3D();
		tr.setRotation(quat);
		setTransform(tr);
		//TDebug.println(0,"setRotationQ: " + tr);
	}



protected Transform3D  getDirectionTransform(Vector3d refDirection, Vector3d newDirection){
        TDebug.println(3,"setDirection: " + newDirection); 
        Vector3d direction = new Vector3d(newDirection);
		direction.normalize();
		Vector3d axis=new Vector3d();
		axis.cross(refDirection,direction);
        Transform3D trans = getTransform3D();
        AxisAngle4d axisAngle=null;
		double angle=refDirection.angle(direction);
		if (axis.length()!=0){
			axis.normalize();
			axisAngle=new AxisAngle4d(axis.x,axis.y,axis.z,angle);
		}
        else
        {
            if ( angle > Math.PI/2. ) {
                Vector3d u = new Vector3d();
                Vector3d v = new Vector3d();
                do {
                    u.set(Math.random(),Math.random(),Math.random());
                    v.set(refDirection);
                    v.normalize();
                    v.scale(u.dot(v));
                    u.sub(v);
                } while( u.length() < Teal.DoubleZero );
                u.normalize();
                axisAngle=new AxisAngle4d(u,angle);
            }
            else
                axisAngle=new AxisAngle4d(refDirection,0.);            
        }
        trans.setRotation(axisAngle);
        return trans;
	}
   public void setDirection(Vector3d newDirection){
        Transform3D trans = getDirectionTransform(Node3D.refDirection,newDirection);      
        setTransform(trans);
	}
   
    public Transform3D getTransform3D()
    {
    	Transform3D trans = new Transform3D();
    	getTransform(trans);
    	return trans;
    }
    public void setTransform3D(Transform3D trans)
    {
    	setTransform(trans);
    }
  
      /* Need to figure what to do here */
    public void setTransform(Vector3d pos, Vector3d direction){
        setPosition(pos);
        setDirection(direction);
    }
  
    public void setTransform(Vector3d pos, Vector3d direction, double scale){
        setPosition(pos);
        Transform3D trans = getDirectionTransform(Node3D.refDirection,direction);
        trans.setScale(scale);      
        setTransform(trans);
    }
  
    /* Need to figure what to do here */
    public void setTransform(Matrix3d rot,Vector3d pos, double scale){
        Transform3D trans = new Transform3D(rot,pos,scale);
 	    setTransform(trans);
    }
   

    public boolean isVisible()
    {
        return isVisible;
    }
    
    public void setVisible(boolean b)
    {
        //TDebug.println(0,"TransformGizmo:setVisible: " + b);
        isVisible = b;
        if (isVisible && isShown)
        {
            mSwitch.setWhichChild(Switch.CHILD_ALL);
        }
        else
        {
            mSwitch.setWhichChild(-1);
        }
    }
}


