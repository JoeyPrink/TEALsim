/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Rendered.java,v 1.99 2010/07/16 21:41:34 stefan Exp $ 
 * 
 */

package teal.render;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.net.URL;

import javax.media.j3d.Transform3D;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.core.AbstractElement;
import teal.render.TAbstractRendered.NodeType;
import teal.render.j3d.SphereNode;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.scene.Model;
import teal.render.scene.SceneFactory;
import teal.render.scene.TNode3D;
import teal.render.scene.TShapeNode;
import teal.util.TDebug;

/**
 * Provides basic support for any rendered object that may be represented in the Model's views.
 *
 *
 * @author Phil Bailey - Center for Educational Computing Initiatives / MIT
 */

public class Rendered extends AbstractElement implements TRendered {

    private static final long serialVersionUID = 3688503281744098099L;
    protected NodeType nodeType = NodeType.NONE;
    
    protected int screenXRotationAxis = ROTATION_AXIS_Y;
    protected int screenYRotationAxis = ROTATION_AXIS_X;
    protected double rotationAngleSnap = 0.;

    public final static Vector3d initialDirection;

    static {
        initialDirection = new Vector3d(0, 1, 0);
    }

    protected Vector3d position;
    protected Vector3d scale;
    protected Quat4d orientation;

    // TDrawable support
    protected boolean isDrawn = true;
    protected int renderFlags = 0;
    protected TMaterial mMaterial = new TealMaterial();

    // 3D support
    protected transient TNode3D mNode;
    protected boolean showNode = true;
    protected Model mModel = null;
    protected Vector3d modelOffset = null;
   
    protected URL url = null;

    /** Used internally to maintain a base bounds object, normally in default
     * position and orientation.
     */
    protected Bounds bounds = null;

    public static boolean doViewer = true;

    protected boolean isPickable = false;
    protected boolean isPicked = false;
    protected boolean isMoveable = true;
    protected boolean isRotable = false;
    protected boolean isRotating = false;
    protected boolean isHighlighted = false;
    protected boolean isSelected = false;
    protected boolean isSelectable = false;

    // Fog flag
    protected boolean isReceivingFog = false;

    public Rendered() {
        super();
        mMaterial = ColorUtil.getMaterial(Color.GRAY);
        position = new Vector3d();
        scale = new Vector3d(1.,1.,1.);
       
        AxisAngle4d axisAngle = new AxisAngle4d(initialDirection, 0.);
        orientation = new Quat4d();
        orientation.set(axisAngle);
        mNode = null;
        this.nodeType = NodeType.SPHERE; //FIXXME
        //setScale(10.);
    }
/**
 * checks if there is a node, if there is adds the render flag.
 * This does not set the needsSpatial value this method should 
 * be called from within the nextSpatial() method call. 
 **/
    public void registerRenderFlag(int flag){
 
        if (isDrawn()) {
            if (mNode != null) {
                renderFlags |= flag;
            }
        }
    }
    
    /**
     * returns whether or not this Rendered is receiving fog.
     * @return true if receiving fog
     */
    public boolean isReceivingFog() {
        return isReceivingFog;
    }

    /**
     * sets this Rendered to receive fog by adding its node to the scope of the fog node.  Note that if
     * you want fog to affect the entire scene, you need only call setFogEnabled(true) on the viewer, as 
     * the scope of the fog node defaults to the entire scene if no other nodes have been specifically added
     * using this method.
     * 
     * @param fog
     */
    public void setReceivingFog(boolean fog) {
        isReceivingFog = fog;
    }

    public boolean isDrawn() {
        return isDrawn;
    }

    public void setDrawn(boolean b) {
        TDebug.println(2, "Renderd.setDrawn() =  " + b);
        isDrawn = b;
        showNode = b;
        renderFlags |= VISIBILITY_CHANGE;

    }
    public Vector3d getScale(){
        return scale;
    }
    public void setScale(double val){
        setScale(new Vector3d(val,val,val));
    }
    
    public void setScale(Vector3d val){
    	
        this.scale.set(val);
        renderFlags |= SCALE_CHANGE;  
        
    }
    
    public NodeType getNodeType(){
    	return nodeType;
    }
    @Deprecated
    public URL getURL() {
        return url;
    }
    @Deprecated
    public void setURL(URL u) {
        url = u;
    }

    /* (non-Javadoc)
     * @see teal.render.TDrawable#render()
     */
    public void render() {
    	if (mNode == null) {
        	return;
        }
        
        if ((renderFlags & VISIBILITY_CHANGE) == VISIBILITY_CHANGE) {
            if (isDrawn) {
                if (mNode.isVisible() != isDrawn) {
                    mNode.setVisible(isDrawn);
                }
            } else {
                if (mNode.isVisible()) mNode.setVisible(false);
            }
        }
        if ((renderFlags & POSITION_CHANGE) == POSITION_CHANGE) {
            mNode.setPosition(position);

        }
        if ((renderFlags & SCALE_CHANGE) == SCALE_CHANGE) {
            mNode.setScale(scale);
        }
        if ((renderFlags & ROTATION_CHANGE) == ROTATION_CHANGE) {
            mNode.setRotation(orientation);

        }
        if ((renderFlags & COLOR_CHANGE) == COLOR_CHANGE) {
            if (mNode instanceof HasColor) ((HasColor) mNode).setColor(TealMaterial.getColor3f(mMaterial.getDiffuse()));
        }
        renderFlags = 0;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean b) {
        isSelected = b;
        if (mNode != null) mNode.setSelected(b);
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setSelectable(boolean b) {
        isSelectable = b;
        if (mNode != null) mNode.setSelectable(b);
    }

    public void setMoveable(boolean b) {
        boolean old = isMoveable;
        isMoveable = b;
        firePropertyChange("moveable", old, b);
    }

    public boolean getMoveable() {
        return isMoveable;
    }

    public boolean isMoveable() {
        return isMoveable;
    }

    /**
     * 
     */
    public void setRotable(boolean b) {
        isRotable = b;
    }

    /*  */
    public boolean isRotable() {
        return isRotable;
    }
    
    public void setScreenXRotationAxis(int rotationAxis) {
    	this.screenXRotationAxis = rotationAxis;
    }
    public int getScreenXRotationAxis() {
    	return this.screenXRotationAxis;
    }
    public void setScreenYRotationAxis(int rotationAxis){
    	this.screenYRotationAxis = rotationAxis;
    }
    public int getScreenYRotationAxis() {
    	return this.screenYRotationAxis;
    }
    
    public double getRotationAngleSnap() {
    	return this.rotationAngleSnap;
    }
    
    public void setRotationAngleSnap(double snap) {
    	this.rotationAngleSnap = snap;
    }

    /**
     Returns the rotation matrix. If null, checks the quaternion and computes
     corresponding rotation matrix. Otherwise, returns the identity matrix.
     */
    public Matrix3d getRotationMatrix() {
        Matrix3d mat = new Matrix3d();
        if (orientation != null) {
            mat.set(orientation);
        } else {
            mat.setIdentity();
        }
        return mat;
    }

    public void setRotating(boolean state) {
        isRotating = state;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public Quat4d getRotation() {
        return new Quat4d(orientation);
    }

    public void setRotation(Quat4d rot) {
        setRotation(rot, true);
    }

    public void setRotation(Matrix3d rot) {
        setRotation(rot, true);
    }

    /**
     * Sets the rotation of this object (as Quat4d) with the option of not generating a PropertyChangeEvent.
     * 
     * @param rot new rotation as Quat4d
     * @param sendPCE trigger PropertyChangeEvent?
     */
    public void setRotation(Quat4d rot, boolean sendPCE) {
        Quat4d oldrot = new Quat4d(orientation);
        orientation.set(rot);
        if (sendPCE) {
            PropertyChangeEvent pce = new PropertyChangeEvent(this, "rotation", oldrot, orientation);
            firePropertyChange(pce);
        }
        renderFlags |= ROTATION_CHANGE;

    }

    /**
     * Sets the rotation of this object (as Matrix3d) with the option of not generating a PropertyChangeEvent.
     * 
     * @param rot new rotation as Matrix3d
     * @param sendPCE trigger PropertyChangeEvent?
     */
    public void setRotation(Matrix3d rot, boolean sendPCE) {
        Quat4d oldrot = new Quat4d(orientation);
        orientation.set(rot);
        if (sendPCE) {
            PropertyChangeEvent pce = new PropertyChangeEvent(this, "rotation", oldrot, orientation);
            firePropertyChange(pce);
        }
        renderFlags |= ROTATION_CHANGE;
    }

    public Vector3d getDirection() {
        Vector3d direction = new Vector3d(initialDirection);
        getRotationMatrix().transform(direction);
        return direction;
    }
    
    public void setDirection(Vector3d newDirection) {
        TDebug.println(3, "setDirection: " + newDirection);
        if (newDirection.length() == 0.) return;
        Quat4d oldrot = new Quat4d(orientation);
        Vector3d direction = new Vector3d(newDirection);
        direction.normalize();
        Vector3d axis = new Vector3d();
        axis.cross(initialDirection, direction);
        //Transform3D trans = getTransform3D();
        AxisAngle4d axisAngle = null;
        double angle = initialDirection.angle(direction);
        if (axis.length() != 0) {
            axis.normalize();
            axisAngle = new AxisAngle4d(axis.x, axis.y, axis.z, angle);
        } else {
            if (angle > Math.PI / 2.) {
                Vector3d u = new Vector3d();
                Vector3d v = new Vector3d();
                do {
                    u.set(Math.random(), Math.random(), Math.random());
                    v.set(initialDirection);
                    v.normalize();
                    v.scale(u.dot(v));
                    u.sub(v);
                } while (u.length() < Teal.DoubleZero);
                u.normalize();
                axisAngle = new AxisAngle4d(u, angle);
            } else axisAngle = new AxisAngle4d(initialDirection, 0.);
        }
        //trans.setRotation(axisAngle);
        //return trans;
        
        orientation.set(axisAngle);
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "rotation", oldrot, orientation);
        firePropertyChange(pce);
        renderFlags |= ROTATION_CHANGE;
    }
/*
    public void setDirection(Vector3d newDirection) {
        Quat4d oldrot = new Quat4d(orientation);

        newDirection.normalize();
        Vector3d axis = new Vector3d();
        axis.cross(initialDirection, newDirection);
        if (axis.length() < Teal.DoubleZero) {
            if (initialDirection.dot(newDirection) > 0) {
                //System.out.println("Rendered setDirection problem");
                AxisAngle4d axisAngle = new AxisAngle4d(initialDirection, 0.);
                orientation = new Quat4d();
                orientation.set(axisAngle);
                //orientation = new Quat4d();
            } else {
                Vector3d v = new Vector3d();
                v.set(initialDirection);
                axis.set(0., 0., 1.);
                v.scale(axis.dot(v));
                axis.sub(v);
                if (axis.length() < Teal.DoubleZero) {
                    v.set(initialDirection);
                    axis.set(0., 1., 0.);
                    v.scale(axis.dot(v));
                    axis.sub(v);
                    if (axis.length() < Teal.DoubleZero) {
                        v.set(initialDirection);
                        axis.set(1., 0., 0.);
                        v.scale(axis.dot(v));
                        axis.sub(v);
                    }
                }
                axis.normalize();
                double angle = initialDirection.angle(newDirection);
                AxisAngle4d axisAngle = new AxisAngle4d(axis.x, axis.y, axis.z, angle);
                orientation.set(axisAngle);
            }
        } else {
            axis.normalize();
            double angle = initialDirection.angle(newDirection);
            AxisAngle4d axisAngle = new AxisAngle4d(axis.x, axis.y, axis.z, angle);
            orientation.set(axisAngle);
        }
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "rotation", oldrot, orientation);
        firePropertyChange(pce);
        renderFlags |= ROTATION_CHANGE;
    }
*/
    /**
     * Convenience method, calls setPosition(Vector3d pos) with a new Vector3d based on supplied component values.
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(double x, double y, double z) {
        setPosition(new Vector3d(x, y, z));
    }

    public void setPosition(Vector3d pos) {
        setPosition(pos, true);

    }

    /**
     * Sets the position of this object with the option of not triggering a PropertyChangeEvent.
     * 
     * @param pos new position.
     * @param sendPC trigger PropertyChangeEvent?
     */
    public void setPosition(Vector3d pos, boolean sendPC) {
        PropertyChangeEvent pce = null;
        if (sendPC) {
            pce = new PropertyChangeEvent(this, "position", new Vector3d(position), new Vector3d(pos));
        }

        position.set(pos);
        renderFlags |= POSITION_CHANGE;
        if (sendPC) firePropertyChange(pce);

    }

    public Vector3d getPosition() {
        return position;
    }
    
    
    public void setModelOffsetTransform(Transform3D t) {
    	// I don't think this needs a property change event or renderFlag
    	// guarantees that mNode gets built
    	getNode3D();
    	mNode.setModelOffsetTransform(t);
    }
    public Transform3D getModelOffsetTransform() {
    	getNode3D();
    	return mNode.getModelOffsetTransform();
    }
    
    public void setModel(Model modelSpecification) {
    	// guarantees that mNode gets built
    	mModel = modelSpecification;
    }
    public Model getModel() {
    	return mModel;
    }

    
    public void setModelOffsetPosition(Vector3d offset) {
//    	// guarantees that mNode gets built
//    	if(mModel == null)
//    		mModel = new Model();
//    	mModel.setOffset(offset);
    	modelOffset = offset;
    }
    
    public Vector3d getModelOffsetPosition() {
//    	if(mModel != null)
//    		return mModel.getOffset();
//    	else
//    		return null;
    	return modelOffset;
    }

    /**
     * Sets the x component of the object's position.
     * 
     * @param v new x component.
     */
    public void setX(double v) {
        Vector3d pos = new Vector3d(getPosition());
        pos.x = v;
        setPosition(pos);

    }

    /**
     * Sets the y component of the object's position.
     * 
     * @param v new y component.
     */
    public void setY(double v) {
        Vector3d pos = new Vector3d(getPosition());
        pos.y = v;
        setPosition(pos);

    }

    /**
     * Sets the z component of the object's position.
     * 
     * @param v new z component.
     */
    public void setZ(double v) {
        Vector3d pos = new Vector3d(getPosition());
        pos.z = v;
        setPosition(pos);

    }

    /**
     * Returns the x component of the object's position.
     * 
     * @return x component of position.
     */
    public double getX() {
        return position.x;
    }
    /**
     * Returns the y component of the object's position.
     * 
     * @return y component of position.
     */
    public double getY() {
        return position.y;
    }
    /**
     * Returns the z component of the object's position.
     * 
     * @return z component of position.
     */
    public double getZ() {
        return position.z;
    }

    /**
     * Utility to construct a new Transform3D for any rendered object, 
     * scale is always 1.0.
     */
    public static Transform3D getTransform(Rendered obj) {
        Transform3D trans = new Transform3D();
        Vector3d pos = obj.getPosition();
        trans.setTranslation(pos);

        Quat4d rot = obj.getRotation();
        trans.setRotation(rot);

        return trans;
    }

    public void setAbsoluteBounds(Bounds absBounds) {
        bounds = absBounds;
    }

    public Bounds getAbsoluteBounds() {
        return bounds;
    }

    protected void createBounds() {
    }

    public Bounds getBoundingArea() {
        if (bounds == null) {
            createBounds();
        }
        Transform3D trans = new Transform3D();
        trans.set(position);
        Bounds wrk = (Bounds) bounds.clone();
//        wrk.transform(trans);
        return wrk;
    }

    /**
     * This method builds the rendered Node3D for this object.  It should be overloaded by any sub-class that uses a custom
     * Node3D (in other words, almost all of them).
     * 
     * @return a reference to the created node.
     */
    protected TNode3D makeNode() {
        TNode3D node =  SceneFactory.makeNode(this);
        //node.setScale(0.002);
//        node.setElement(this);
       // node.setColor(TealMaterial.getColor3f(mMaterial.getDiffuse()));
        return node;
    }

    /* (non-Javadoc)
     * @see teal.render.TRendered#getNode3D()
     */
    public TNode3D getNode3D() {
        if (mNode == null) {
            TNode3D   node = makeNode();
            setNode3D(node);
        }
        return mNode;
    }

    public final void setNode3D(TNode3D n) {
        mNode = n;
        if (mNode != null) {
            mNode.setElement(this);
            mNode.setPickable(isPickable);
            mNode.setVisible(isDrawn);
        }
    }

    public void setPickable(boolean b) {
        isPickable = b;
        if (mNode != null) mNode.setPickable(isPickable);
    }

    public boolean isPickable() {
        return isPickable;
    }

    public boolean getPickable() {
        return isPickable;
    }

    public void setPicked(boolean b) {
        isPicked = b;
        if (mNode != null) mNode.setPicked(isPicked);
    }

    public boolean isPicked() {
        return isPicked;
    }

    public boolean getPicked() {
        return isPicked;
    }

    public Color3f getColor(){
        return TealMaterial.getColor3f(mMaterial.getDiffuse());
    }

    public void setColor(Color color) {
    	mMaterial.setColor(new Color3f(color));
    	renderFlags |= COLOR_CHANGE;
    	
    }
    public void setColor(Color3f color) {
    	mMaterial.setColor(color);
        renderFlags |= COLOR_CHANGE;
        
    }
    
    public void setColor(Color4f color) {
    	mMaterial.setColor(new Color3f(color.x,color.y,color.z));
    	renderFlags |= COLOR_CHANGE;
    }
    
    public Color4f getColor4f() {
    	Color4f c = new Color4f(mMaterial.getDiffuse().get());
    	return c;
    }

    public TMaterial getMaterial(){
    	return mMaterial;
    }
    
    public void setMaterial(TMaterial mat){
    	mMaterial = mat;
    	renderFlags |= COLOR_CHANGE;
    	
    }
}
