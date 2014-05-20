/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TRendered.java,v 1.22 2010/07/16 21:41:34 stefan Exp $ 
 * 
 */

package teal.render;

import java.awt.Color;

import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import teal.core.TElement;
import teal.render.scene.Model;

/**
* An interface for Elements that may be represented in 2D and/or 3D
*/

public interface TRendered extends TAbstractRendered,
    HasRotation, HasDirection, TElement, HasFog, HasNode3D
{
    /** Sets the isDrawn state */
    public void setDrawn(boolean b);
    
    /** if true the Object will be drawn */
    public boolean isDrawn();
    
   
    
    /**
    * The optimized commands required to update the objects visualization, 
    * this expects the underlying visualization objects have been created
    * and any calculations are complete. It may be restricted to methods 
    * that only operate in the rendering thread.
    */
    
    /* (non-Javadoc)
     * @see teal.render.IsMoveable#setMoveable(boolean)
     */
    public void setMoveable(boolean b);
	public boolean isMoveable();
    
    //public Transform3D getTransform();
	//public void setTransform(Transform3D trans);
    
    public void setRotating(boolean state);
  public boolean isRotating();
  /**
   * Sets this object's rotation as a Matrix3d (rotation matrix).  The value is converted to a Quat4d internally.
   * 
 * @param rot new rotation as Matrix3d.
 */
public void setRotation(Matrix3d rot);
  /**
   * Sets this object's rotation as a Quat4d.
   * 
 * @param orientation new rotation as Quat4d.
 */
public void setRotation(Quat4d orientation);

  /**
   * Returns this object's rotation as a Quat4d.
   * 
 * @return rotation as Quat4d.
 */
public Quat4d getRotation();
  
  
  public boolean isRotable();
  /**
   * Sets whether this object should be allowed to rotate (if false, the object will not respond to rotation
   * behaviors).
   * 
 * @param b 
 */
  public void setRotable(boolean b);
    
    /** Used to access the direction
     *
     * @return direction
     */
	public Vector3d getDirection();
    /** Used to set the direction
     *
     * @param direction
     */
     public void setDirection(Vector3d direction);
    
     /**
      * Sets the position of this object.
      *
      * @param pos the new position.
      */
      public void setPosition(Vector3d pos);
      //public void setPosition(Vector3d pos, boolean sendPropertyChange);

      /**
       * Returns the position of this object.
       * @return the position.
       */
       public Vector3d getPosition();
       
       /**
        * Sets the offset transform of the rendered model for this object.  In the scenegraph, this transform sits below
        * the Position transform for the object, and above the rendered node of the object.  
        * 
     * @param t new offset transform.
     */
    public void setModelOffsetTransform(Transform3D t);
       /**
        * Returns the offset transform of the rendered model for this object.
        * 
     * @return offset transform.
     */
    public Transform3D getModelOffsetTransform();
       /**
        * Convenience method for setting only the position offset of the rendered model.
        * 
     * @param offset position offset.
     */
    public void setModelOffsetPosition(Vector3d offset);
       /**
        * Returns the offset position of the rendered model for this object.
        * 
     * @return the offset position.
     */
    public Vector3d getModelOffsetPosition();
    
    /**
     * Convenience method for setting only the position offset of the rendered model.
     * 
  * @param offset position offset.
  */
 public void setModel(Model modelSpecification);
    /**
     * Returns the offset position of the rendered model for this object.
     * 
  * @return the offset position.
  */
 public Model getModel();
     
    /**
     * This method should return a reference to the rendered (T)Node3D of this object.
     * 
     * @return reference to (T)Node3D.
     */
    
    public Bounds getAbsoluteBounds() ;
    public boolean isReceivingFog() ;
    public double getRotationAngleSnap();
    public int getScreenXRotationAxis();
    public int getScreenYRotationAxis();


    public final static int ROTATION_CHANGE = 0x002;
    public final static int ROTATION_AXIS_NONE = 0;
    public final static int ROTATION_AXIS_X = 1;
    public final static int ROTATION_AXIS_Y = 2;
    public final static int ROTATION_AXIS_Z = 3;


}
	 
