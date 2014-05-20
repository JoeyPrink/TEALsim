/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TViewer3D.java,v 1.14 2010/07/21 21:46:50 stefan Exp $ 
 * 
 */

package teal.render.viewer;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.render.Bounds;




/**
 * This interface defines all of the required functionality for a 3D viewer.
 *
 */
public interface TViewer3D extends TViewer 
{

    /**
     * Sets whether or not default lighting should be used.
     */
    public void useDefaultLights(boolean state);
    /**
     * Returns whether or not selection gizmos are being displayed.
     */
    public boolean getShowGizmos();
    /**
     * Sets whether or not selection gizmos should be displayed.
     * 
     * @param state
     */
    public void setShowGizmos(boolean state);
    /**
     * Returns the current view's transform.
     */
    public Transform3D getViewTransform();
    /**
     * Sets the current view's transform.
     * 
     * @param vTrans
     */
    public void setViewTransform(Transform3D vTrans);
    public void moveCamera(Vector3d position,Vector3d direction);
    /**
     * Sets the position and orientation of the "camera" in the scene.
     * 
     * @param from position of the camera.
     * @param to direction the camera is pointing.
     * @param upAngle up-direction of the camera.
     */
    public void setLookAt(Point3d from,Point3d to, Vector3d upAngle);
    /**
     * Sets the field of view of the camera, in radians.
     * @param rad
     */
    public void setFieldOfView(double rad);
    /**
     * Returns the field of view of the camera, in radians.
     */
    public double getFieldOfView();
    public void setCameraChange(boolean bViewerChange);
    public boolean getCameraChange();
    public double getCameraDistance();
    public void setCameraDistance(double distance);
    public double getCameraZAngle();
    public void setCameraZAngle(double angle);

    public Vector3d getViewerAngle();
    public void setViewerAngle( Vector3d viewerAngle);

    /**
     * Gets the distance from the camera of the front clipping plane.
     * @return distance from camera of front clipping plane.
     */
    public double getFrontClipDistance();
    /**
     * Gets the distance from the camera of the back clipping plane
     * @return distance from camera of back clipping plane.
     */
    public double getBackClipDistance();
    /**
     *  Sets the distance from the camera of the back clipping plane. (geometry behind the back clipping plane will not be rendered)
     * @param cd
     */
    public void setBackClipDistance(double cd);
    /**
     * Sets the distance from the camera of the front clipping plane (geometry in front of the front clipping plane will not be rendered).
     * @param cd
     */
    public void setFrontClipDistance(double cd);

    public void setSceneScale(Vector3d scale);
    public Vector3d getSceneScale();
    
    /**
     * Gets the distance from the camera where the fog begins.
     */
    public double getFogFrontDistance();
    /**
     * Sets the distance from the camera where fog begins.  Fog should interpolate from the FogFrontDistance to the FogBackDistance.
     * @param front
     */
    public void setFogFrontDistance(double front);
    /**
     * Gets the distance from the camera where the fog ends.
     */
    public double getFogBackDistance();
    /**
     * Sets the distance from the camera where the fog ends. Fog should interpolate from the FogFrontDistance to the FogBackDistance.
     * @param back
     */
    public void setFogBackDistance(double back);
    /**
     * Gets the influencing bounds of the Fog Behavior (Java3D only?)
     */
    public Bounds getFogInfluencingBounds();
    /**
     * Sets the influencing bounds of the Fog Behavior (Java3D only?)
     * @param bounds
     */
    public void setFogInfluencingBounds(Bounds bounds);
    /**
     * Returns whether or not fog is enabled in the scene.
     */
    public boolean isFogEnabled();
    /**
     * Sets whether or not fog is enabled in the scene.
     * @param enabled
     */
    public void setFogEnabled(boolean enabled);
    public void initFogTransform();
    public double getFogTransformBackScale();
    public void setFogTransformBackScale(double backscale);
    public double getFogTransformFrontScale();
    public void setFogTransformFrontScale(double frontscale);
    public void setFogTransform(Transform3D trans);
    public void setDefaultLights();


}
