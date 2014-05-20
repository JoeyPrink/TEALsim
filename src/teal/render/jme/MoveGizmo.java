/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: MoveGizmo.java,v 1.4 2010/04/26 21:31:33 stefan Exp $ 
 * 
 */

package teal.render.jme;

import java.awt.Color;
import java.util.BitSet;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.sun.j3d.utils.geometry.Primitive;

import teal.config.Teal;
import teal.render.HasPosition;
import teal.render.TAbstractRendered;
import teal.render.TRendered;
import teal.render.j3d.geometry.GeomUtil;
import teal.util.TDebug;



/**
 * This node is used for rendering the "movement gizmo", an indicator 
 * of orientation that appears when an object 
 * in the viewer is selected.
 */
public class MoveGizmo extends Node3D
{

    public static final int SHOW_BOX = 1;
    public static final int SHOW_X = 2;
    public static final int SHOW_Y = 4;
    public static final int SHOW_Z = 8;
    
    
    protected Box sBox = new Box("Box", new Vector3f(), 0.5f, 0.5f, 0.5f);
    
    protected static GeometryArray sConeX;
    protected static GeometryArray sBaseX;   
    protected static GeometryArray sShaftX;
    protected static GeometryArray sShaftBaseX;
    
    protected static GeometryArray sConeY;
    protected static GeometryArray sBaseY;   
    protected static GeometryArray sShaftY;
    protected static GeometryArray sShaftBaseY;
    
    protected static GeometryArray sConeZ;
    protected static GeometryArray sBaseZ;   
    protected static GeometryArray sShaftZ;
    protected static GeometryArray sShaftBaseZ;
    
    
    
    protected final static double sBoxCoords [] = 
    {
        -0.5,0.5,0.5,
        -0.5,0.5,-0.5,
        0.5,0.5,-0.5,
        0.5,0.5,0.5,
        -0.5,-0.5,0.5,
        -0.5,-0.5,-0.5,
        0.5,-0.5,-0.5,
        0.5,-0.5,0.5 
    };
    
    protected final static int [] sBoxIdx =
    {
        0,1,1,2,2,3,3,0,4,5,5,6,6,7,7,4,0,4,1,5,2,6,3,7
    };
    
    @Override
	public void detach(){};
	
	//FIXXME: see Node3D
	@Override
	public boolean isPickable() {
		return mContents.isCollidable();
	}
	//FIXXME: see Node3D	
	@Override
	public void setPickable(boolean b) {
		mContents.setIsCollidable(true);
	}
	
	@Override
    public void addMarker() {
		TDebug.println(1, "WARNING: MoveGizmo does not support Markers");
    }

	@Override
    public void addMarker(Spatial objNode) {
		TDebug.println(1, "WARNING: MoveGizmo does not support Markers");
    }
    
    static
     {
        com.sun.j3d.utils.geometry.Cylinder cylinder;
        com.sun.j3d.utils.geometry.Cone cone;
        Point3d offset = new Point3d(0.,0.925,0.);
        Point3d offset2 = new Point3d(0.,1.35,0.);
        sBox = new IndexedLineArray(8,GeometryArray.COORDINATES,24);
        sBox.setCoordinates(0,sBoxCoords);
        ((IndexedLineArray)sBox).setCoordinateIndices(0,sBoxIdx);
        
        cylinder = new com.sun.j3d.utils.geometry.Cylinder(0.05f,0.85f,Primitive.GENERATE_NORMALS|Primitive.GEOMETRY_NOT_SHARED, null);
        cone = new com.sun.j3d.utils.geometry.Cone(0.1f,0.15f,Primitive.GENERATE_NORMALS|Primitive.GEOMETRY_NOT_SHARED, null);
         
         sShaftX = (GeometryArray)cylinder.getShape(com.sun.j3d.utils.geometry.Cylinder.BODY).getGeometry();
         GeomUtil.moveGeometry(sShaftX,offset);
         sShaftBaseX = (GeometryArray) cylinder.getShape(com.sun.j3d.utils.geometry.Cylinder.BOTTOM).getGeometry();
         GeomUtil.moveGeometry(sShaftBaseX,offset);        
        sConeX = (GeometryArray) cone.getShape(com.sun.j3d.utils.geometry.Cone.BODY).getGeometry();
         GeomUtil.moveGeometry(sConeX,offset2);
     	sBaseX = (GeometryArray) cone.getShape(com.sun.j3d.utils.geometry.Cone.CAP).getGeometry();
        GeomUtil.moveGeometry(sBaseX,offset2);
        
        cylinder = new com.sun.j3d.utils.geometry.Cylinder(0.05f,0.85f,Primitive.GENERATE_NORMALS|Primitive.GEOMETRY_NOT_SHARED, null);
        cone = new com.sun.j3d.utils.geometry.Cone(0.1f,0.15f,Primitive.GENERATE_NORMALS|Primitive.GEOMETRY_NOT_SHARED, null);
         
         sShaftY = (GeometryArray) cylinder.getShape(com.sun.j3d.utils.geometry.Cylinder.BODY).getGeometry();
         GeomUtil.moveGeometry(sShaftY,offset);
         GeomUtil.transposeGeometry(sShaftY,GeomUtil.ROTATE_Y);
         sShaftBaseY = (GeometryArray) cylinder.getShape(com.sun.j3d.utils.geometry.Cylinder.BOTTOM).getGeometry();
         GeomUtil.moveGeometry(sShaftBaseY,offset); 
         GeomUtil.transposeGeometry(sShaftBaseY,GeomUtil.ROTATE_Y); 
        sConeY = (GeometryArray) cone.getShape(com.sun.j3d.utils.geometry.Cone.BODY).getGeometry();
         GeomUtil.moveGeometry(sConeY,offset2);
         GeomUtil.transposeGeometry(sConeY,GeomUtil.ROTATE_Y);
     	sBaseY = (GeometryArray) cone.getShape(com.sun.j3d.utils.geometry.Cone.CAP).getGeometry();
        GeomUtil.moveGeometry(sBaseY,offset2);
        GeomUtil.transposeGeometry(sBaseY,GeomUtil.ROTATE_Y);
        
        cylinder = new com.sun.j3d.utils.geometry.Cylinder(0.05f,0.85f,Primitive.GENERATE_NORMALS|Primitive.GEOMETRY_NOT_SHARED, null);
        cone = new com.sun.j3d.utils.geometry.Cone(0.1f,0.15f,Primitive.GENERATE_NORMALS|Primitive.GEOMETRY_NOT_SHARED, null);
         
         sShaftZ = (GeometryArray) cylinder.getShape(com.sun.j3d.utils.geometry.Cylinder.BODY).getGeometry();
         GeomUtil.moveGeometry(sShaftZ,offset);
         GeomUtil.transposeGeometry(sShaftZ,GeomUtil.ROTATE_Z);
         sShaftBaseZ = (GeometryArray) cylinder.getShape(com.sun.j3d.utils.geometry.Cylinder.BOTTOM).getGeometry();
         GeomUtil.moveGeometry(sShaftBaseZ,offset);
         GeomUtil.transposeGeometry(sShaftBaseZ,GeomUtil.ROTATE_Z);  
        sConeZ = (GeometryArray) cone.getShape(com.sun.j3d.utils.geometry.Cone.BODY).getGeometry();
         GeomUtil.moveGeometry(sConeZ,offset2);
         GeomUtil.transposeGeometry(sConeZ,GeomUtil.ROTATE_Z);
     	sBaseZ = (GeometryArray) cone.getShape(com.sun.j3d.utils.geometry.Cone.CAP).getGeometry();
        GeomUtil.moveGeometry(sBaseZ,offset2);
        GeomUtil.transposeGeometry(sBaseZ,GeomUtil.ROTATE_Z);
     };

    protected BitSet mShowBits = null;
    protected Switch mSwAction= null;
    protected Shape3D mBox= null;
    
    protected Shape3D mArrowX = null;   
    protected Shape3D mArrowY = null;
    protected Shape3D mArrowZ = null;
    
    
    public static void initShape(Shape3D shape){
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
    }
    
    public MoveGizmo()
    {
        super();
        mShowBits = new BitSet(4);
        mShowBits.set(0,4,true);
        mSwAction = new Switch(Switch.CHILD_MASK, mShowBits);
    
    mSwAction.setCapability(Switch.ALLOW_SWITCH_READ);
    mSwAction.setCapability(Switch.ALLOW_SWITCH_WRITE);
    mSwAction.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    mSwAction.setCapability(Group.ALLOW_CHILDREN_READ);
    mSwAction.setCapability(Group.ALLOW_CHILDREN_WRITE);
	mSwAction.setCapability(Node.ALLOW_PICKABLE_READ);
	mSwAction.setCapability(Node.ALLOW_PICKABLE_WRITE);
    mSwAction.setCapability(Node.ENABLE_PICK_REPORTING);
    mSwAction.setCapability(Node.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    mContents.addChild(mSwAction);
    
    mArrowX = new Shape3D();
    initShape(mArrowX);
    mArrowX.addGeometry(sShaftBaseX);
    mArrowX.addGeometry(sShaftX);
    mArrowX.addGeometry(sBaseX);
    mArrowX.addGeometry(sConeX);
    mArrowX.setAppearance(Node3D.makeAppearance(new Color3f(Teal.ArrowXColor),0.5f));
    
    mArrowY = new Shape3D();
    initShape(mArrowY);
    mArrowY.addGeometry(sShaftBaseY);
    mArrowY.addGeometry(sShaftY);
    mArrowY.addGeometry(sBaseY);
    mArrowY.addGeometry(sConeY);
    mArrowY.setAppearance(Node3D.makeAppearance(new Color3f(Teal.ArrowYColor),0.5f));
    
    mArrowZ = new Shape3D();
    initShape(mArrowZ);
    mArrowZ.addGeometry(sShaftBaseZ);
    mArrowZ.addGeometry(sShaftZ);
    mArrowZ.addGeometry(sBaseZ);
    mArrowZ.addGeometry(sConeZ);
    mArrowZ.setAppearance(Node3D.makeAppearance(new Color3f(Teal.ArrowZColor),0.5f));
    
    mBox = new Shape3D();
    initShape(mBox);
    mBox.addGeometry(sBox);
    mBox.setAppearance(Node3D.makeAppearance(new Color3f(Color.GRAY)));
    mSwAction.insertChild(mBox,0);
    mSwAction.insertChild(mArrowX,1);
    mSwAction.insertChild(mArrowY,2);
    mSwAction.insertChild(mArrowZ,3);
    }  

    // FIXXME:
    // never used!!
/*
    public void showDirection(int directionMask)
    {    
       mShowBits.set(0,(directionMask & SHOW_BOX) == SHOW_BOX);
       mShowBits.set(1,(directionMask & SHOW_X) == SHOW_X);
       mShowBits.set(2,(directionMask & SHOW_Y) == SHOW_Y);
       mShowBits.set(3,(directionMask & SHOW_Z) == SHOW_Z);
       mSwAction.setChildMask(mShowBits);
 
    } 
    */
    
    public void setElement(TAbstractRendered element)
    {
	    setUserData(element);
        if (element != null)
        {
            TDebug.println(1,"moveGizmo setting element: " +element);
	        if(element instanceof HasPosition)
	            setPosition(((HasPosition)element).getPosition());
	        //if(element instanceof HasRotation)
	        //    setRotation(((HasRotation)element).getRotation());
            Bounds bnds = null;
            if(element instanceof TRendered)
                bnds = ((TRendered)element).getAbsoluteBounds();
            if (bnds != null)    
            {
                BoundingSphere bs = new BoundingSphere(bnds);
                TDebug.println(1," scale = " + 2.0 * bs.getRadius());
                setScale( 2.0 * bs.getRadius());
            }else
            {
                setScale(1.);
            }    
            setVisible(true );
        }
        else
        {
            setVisible(false);
        }    
     }       

}   
