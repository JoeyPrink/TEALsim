/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: AbstractViewer3D.java,v 1.29 2010/07/21 21:46:50 stefan Exp $ 
 * 
 */

package teal.render.viewer;

import java.awt.*;
import java.awt.geom.*;
import java.beans.*;
import java.util.*;

import javax.vecmath.*;

import javax.media.j3d.Transform3D;

import teal.config.Teal;
import teal.render.BoundingBox;
import teal.render.BoundingSphere;
import teal.render.Bounds;
import teal.render.HasCanvas;
import teal.render.TAbstractRendered;
import teal.sim.simulation.TSimulation;
import teal.util.*;

/**
 * This provides a base class for a specific implementations of a renderer for a specific 3D renderer.
 * It is independant of any control of the simulation that is is managed by the world model.
 *
 *
 * @author Phil Bailey - Center for Educational Computing Initiatives / MIT
 *     This a  panel on which the results of a simulation  are rendered. So this
 *      baseclass provides methods to manipulate the Parameters of the Objects
 *      being simulated.
 */

public abstract class AbstractViewer3D extends teal.render.viewer.Viewer implements TViewer3D, HasCanvas {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5536062832170951276L;
	public static final int BACKGROUND_CHANGED = 0x10;
	// Set up the ambient light
	public static Color3f ambientColor = new Color3f(0.5f, 0.5f, 0.5f);
	// Set up the directional lights
	public static Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
	public static Vector3f light1Direction  = new Vector3f(4.0f, -7.0f, -12.0f);
	public static Color3f light2Color = new Color3f(0.3f, 0.3f, 0.4f);
	public static Vector3f light2Direction  = new Vector3f(-6.0f, -2.0f, -1.0f);
	public static Vector3f light3Direction  = new Vector3f(-6.0f, 2.0f, 1.0f);
   

    protected boolean useDefaultLights = true;
    protected int renderFlags = 0;
    protected Color4f bgColor;

    protected Canvas mCanvas = null;

    // Picking support
    protected TBehaviorManager mBehaviorMgr;
    protected boolean showGizmos = true;
    protected boolean refreshOnDrag = true;

    // View controls
    protected double frontClipDistance =0.001;
    protected double backClipDistance = 400.;
    
    /* View/Camera Transform Info */
    protected double fieldOfView = 50;
    protected Vector3d cameraPosition;
    protected Point3d lookAtPosition;
    /**the camera angle*/
    protected Vector3d  cameraUpAngle;
    /** The angle looking at the x-y canvas*/
    protected double cameraZAngle = 0;
   /**the camera angle*/
    protected Vector3d cameraAngle;

    
    protected Quat4d cameraOrientation;
    
    // Camera movement 
    /** A distance from the camera to object used by AttachBehavior*/
    protected double cameraDistance = 5.0;

 
   /**Indicate whether the camera direction is changeable or not*/
    protected boolean isCameraDirectionChangeable = true;
 
    
    // Scene  Members
    // protected FrameCheck mFrameCheck;


    public AbstractViewer3D() {
        super();
	mBounds = new BoundingSphere(new Point3d(),500.0);

	bgColor = new Color4f(teal.config.Teal.Background3DColor);
	renderFlags |= AbstractViewer3D.BACKGROUND_CHANGED;
	cameraPosition = new Vector3d(0,0,5);
	lookAtPosition = Teal.ZeroOrigin;
	cameraUpAngle = Teal.UpVector;
	cameraAngle = new Vector3d(0,0,-1);
	//mSceneScale = new Vector3d(1,1,1);

    }
    public void setSimulation(TSimulation sim)
    {
    	if(mSim != null && mSim != sim){
    		// Add more cleanup here
    		mSim.dispose();
    		mSim = null;
    	}
    	mSim = sim;
    	setBackgroundColor(mSim.getBackgroundColor());
    	setBoundingArea(mSim.getBoundingArea());
    	setViewerSize(mSim.getViewerSize());
    	setMouseMoveScale(mSim.getMouseMoveScale());
    	setNavigationMode(mSim.getNavigationMode());
    	
//    	Transform3d trans = mSim.getDefaultViewpoint();
//    	if(trans != null)
//    		setViewTransform(trans);
//    	else
//    		makeTransform();
    	setRefreshOnDrag(mSim.getRefreshOnDrag());
    	
    	setShowGizmos(mSim.getShowGizmos());
    	mSim.addRenderEngine(this);
    	if(mSim.getEngine() != null){
    		addRenderListener(mSim.getEngine());
    		mSim.getEngine().addRenderEngine(this);
    	}
    	addDrawableBulk(mSim.getRenderedElements());
    	//displayBounds();
    }
    synchronized public void addDrawableBulk(Collection<TAbstractRendered> drawn) {
    	Iterator<TAbstractRendered> it = drawn.iterator();
    	while(it.hasNext()){
    		addDrawable((TAbstractRendered)it.next());
    	}
    }
    
    synchronized public void removeDrawableBulk(Collection<TAbstractRendered> drawn) {
    	Iterator<TAbstractRendered> it = drawn.iterator();
    	while(it.hasNext()){
    		removeDrawable((TAbstractRendered)it.next());
    	}
    }
    public void setBackgroundColor(Color color)
    {
	bgColor.set(new Color4f(color));
	renderFlags |= AbstractViewer3D.BACKGROUND_CHANGED;
    }
           
    protected void setDefaults()
    {
        setBackClipDistance(400.0);
        setFrontClipDistance(0.0001);
        setSceneScale( new Vector3d(0.05,0.05,0.05));
        setBackgroundColor(teal.config.Teal.Background3DColor);
        setPickTolerance(1.0f);          
        setPicking(true);
        setNavigationMode(ORBIT_ALL);
    }
    
 
   
    public TBehaviorManager getBehaviorManager()
    {
        return mBehaviorMgr;
    }
    
    public Canvas getCanvas()
    {
        return mCanvas;
    }
    public void setDefaultView() {

        moveCamera(new Vector3d(0.0,0.0,2.4), new Vector3d(0,0,0));
    }
    public void setCameraDistance(double distance) {
        this.cameraDistance = distance;
    }
    public double getCameraDistance() {
        return this.cameraDistance;
    }
    public void setCameraZAngle(double angle) {
        this.cameraZAngle = angle;
    }
    public double getCameraZAngle() {
        return cameraZAngle;
    }

    public int getNavigationMode() 
    {
        return navMode;
    }

    public void setNavigationMode() {
        setNavigationMode(navMode);
    }
    public void setNavigationMode(int flag) {
        if (flag != navMode) {
            mBehaviorMgr.setNavigationMode(flag);
            navMode = flag;
        }
    }

/*
    public PickCanvas getPickCanvas()
    {
        if(pickCanvas == null)
            pickCanvas = new PickCanvas(mCanvas, sceneRoot);
        return pickCanvas;
    }
    */
    /*
    public void setTranslateEnable(boolean state)
    {
        //pickTrans.setEnable(state);
    }
    public boolean getTranslateEnable()
    {
        return true;
        //return pickTrans.getEnable();
    }
    public void setRotateEnable(boolean state)
    {
        //pickRot.setEnable(state);
        if (state)
        {
            //pickRot.setupCallback(pickListen);
        }
        else
        {
            //pickRot.setupCallback(null);
        }
    }
    public boolean getRotateEnable()
    {
        return true;
        //return pickRot.getEnable();
    }
    public void setZoomEnable(boolean state)
    {
        //pickZoom.setEnable(state);
    }
    public boolean getZoomEnable()
    {
        return true;
       //return pickZoom.getEnable();
    }
    */
   public void setRefreshOnDrag(boolean state)
    {
        if(mBehaviorMgr != null)
            mBehaviorMgr.setRefreshOnDrag(state);
    }    
    public boolean getRefreshOnDrag()
    {
        boolean status = false;
        if(mBehaviorMgr != null)
            status = mBehaviorMgr.getRefreshOnDrag();
        return status;    
    }
    
    public void setCursorOnDrag(boolean state)
      {
          if(mBehaviorMgr != null)
              mBehaviorMgr.setCursorOnDrag(state);

      }
      public boolean getCursorOnDrag()
      {
          boolean status = false;
          if(mBehaviorMgr != null)
              status = mBehaviorMgr.getCursorOnDrag();
          return status;    
      }
    
    public void setShowGizmos(boolean state)
    {
        showGizmos = state;
    }    
    public boolean getShowGizmos()
    {
        return showGizmos;
    }

    public void setSelectManager(SelectManager sm)
    {
        selectManager = sm;
        if(mBehaviorMgr != null)
            mBehaviorMgr.setSelectManager(sm);

    }

    public void setPicking(boolean enable) {
         if(mBehaviorMgr != null)
            mBehaviorMgr.setPicking(enable);
        isPicking = enable;
    }
/*
    public void setPickMode(int mode) {
        pickCanvas.setMode(mode);
    }

    public void setPickTolerance(float tolerance) {
        pickCanvas.setTolerance(tolerance);
    }
    */
    public Vector3d getVpTranslateScale()
    {
        
        return mBehaviorMgr.getVpTranslateScale();

    } 
    public void setVpTranslateScale(Vector3d vec)
    {
       mBehaviorMgr.setVpTranslateScale(vec);
    }
           
    public Vector3d getMouseMoveScale()
    {
   
        return mBehaviorMgr.getMouseMoveScale();
    }
    
    public void setMouseMoveScale(Vector3d scale)
    {
        mBehaviorMgr.setMouseMoveScale(scale);
    }

    //private void setViewMode(int mode) {
    //    view.setProjectionPolicy(mode);
    //}

    /**
    * Since these are from an external source we will not fire a listener.
    * May change this at a latter point.
    */
    public void propertyChange(PropertyChangeEvent pce) {
        String pcName = pce.getPropertyName();
        
        if( pcName.equals("boundingArea")) {
            TDebug.println(-1,getID() + " boundingArea PC recieved");
            mBounds = (Bounds) pce.getNewValue();
            makeTransform(getWidth(),getHeight());
            displayBounds();
        }
        else {
            super.propertyChange(pce);
        }
    }

   public void doStatus(int i) {
    if(i <= TDebug.getGlobalLevel())
        doStatus();
   }
    public void setViewerAngle( Vector3d viewerAngle) {
        cameraAngle = viewerAngle;
    }
    public Vector3d getViewerAngle() {
        return cameraAngle;
    }
    public void setCameraChange(boolean bViewerChange) {
        isCameraDirectionChangeable = bViewerChange;
    }
    public boolean getCameraChange() {
        return isCameraDirectionChangeable;
    }

    public void setViewerSize(int w, int h)
    {
        setViewerSize( new Dimension(w,h) );
    }

    public void setViewerSize(Dimension size)
    {
        setSize(size);
    }
    public Dimension getViewerSize()
    {
        return getSize();
    }

    /**
    * Sets the size of the Canvas3d, also re-calculates the
    * transforms, view and viewingPlatform parameters.
    */
    protected void doResize( int w, int h) {		
        makeTransform(w,h);
        mCanvas.setSize(w,h);		
    }
    /**
    * Sets the size & location of the viewArea, also re-calculates the
    * drawing transform.
    */
    public synchronized void setBounds(int x,int y, int w, int h) {
        super.setBounds(x,y,w,h);
        doResize(w,h);
    }

    /**
    * Sets the size of the viewArea, also re-calculates the
    * drawing transform.
    */
    public void setSize( int w, int h) {
        super.setSize(w,h);
        doResize(w,h);
    }

    protected void makeTransform(){
    	makeTransform(getWidth(), getHeight());
    }
    protected void makeTransform(double w, double h) {
        AffineTransform tf = null;

        if (mBounds != null) {
            BoundingBox boundingBox = new BoundingBox(mBounds);
            Point3d lower = boundingBox.getLower();
            Point3d upper =  boundingBox.getUpper();
           
            double width = (double) getWidth();
            double height = (double) getHeight();
            tf = AffineTransform.getTranslateInstance((w/2.0) + lower.x,(h/2.0) + lower.y);
            tf.scale(width/Math.abs(upper.x - lower.x), -Math.abs(height/(upper.y-lower.y)));
        }
        else
        {   tf = AffineTransform.getTranslateInstance(w/2.0,h/2.0);
            tf.scale(1.0,-1.0);
        }
        affTrans = tf;
    }

    /**
    * Sets the World coordinates displayed within the viewArea, also re-calculates the
    * drawing transform.
    */

        public void setBoundingArea(Bounds bb) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this,"boundingArea",
            new BoundingSphere(mBounds), new BoundingSphere(bb));
        mBounds = bb;
        makeTransform(getWidth(),getHeight());
        displayBounds();
        firePropertyChange(pce);
    }

    public void displayBounds() {
        displayBounds(new BoundingSphere(mBounds));
    }
    
    public void displayBounds(BoundingSphere bounds) {
    
        TDebug.println(1,getID() + "displayBounds: " +bounds);
        double radius = bounds.getRadius();
        Point3d center = new Point3d();
        bounds.getCenter(center);
        Vector3d viewVector  = new Vector3d(center);
        
        double viewDistance =   0.06 * ((2.0 *radius)	/ Math.tan(getFieldOfView()));
        viewVector.z += viewDistance;
        TDebug.println(1,"ViewDistance: " + viewDistance +" Tan: " + Math.tan(getFieldOfView())+ " Vector: " + viewVector);
        Transform3D viewTransform = new Transform3D();
        viewTransform.set(viewVector);

        setViewTransform(viewTransform);

        doStatus(1);
    }


    public void setLookAt(Point3d from, Point3d to, Vector3d angle)
    {
        Transform3D vTrans = new Transform3D();
        vTrans.lookAt(from,to,angle);
        vTrans.invert();
		setViewTransform(vTrans);
    }

    abstract public void setViewTransform(Transform3D vTrans);
    
    public void paint(Graphics g) {
        TDebug.println(2,"AbstractViewer3D:paint called");
        //super.paint(g); dont do this it slows it down
        mCanvas.paint(g);
        paintBorder(g);
    }



    public void dispose() {
       // mUniverse.removeAllLocales();

        super.destroy();

    }

    public void remove() {
    }

    public void moveCamera(Vector3d p, Vector3d v) {
        if (isCameraDirectionChangeable == false) {
            v.set(cameraAngle);
        }
        else{
            if (cameraZAngle == 90) {
                v.set(0,0,-1);
            }
            else{
                if (v.x==0 && v.y==0 && v.z==0) {
                    v.set(1,1,0);
                }
                double angle = Math.toRadians(cameraZAngle);
                v.z = -1 * Math.sqrt((Math.pow(v.x,2)+Math.pow(v.y,2)))* Math.tan(angle);
            }
        }
        v.normalize();
        Vector3d newP = new Vector3d(v);
        newP.scale(-cameraDistance);
        newP.add(p);
        Matrix3d trans = new Matrix3d();
        double angle1,angle2; //the x-y rotate angle and the z rotate angle
        if (v.x == 0 && v.y == 0) {
            angle1 = 0;
            angle2 = Math.asin(Math.abs(v.z)/v.z);
        }
        else{
            angle1 = Math.asin(v.x/Math.sqrt((Math.pow(v.x,2)+Math.pow(v.y,2))));
            if (v.y <0)
                angle1 = Math.PI - angle1;
            angle2 = Math.asin(v.z/Math.sqrt((Math.pow(v.x,2)+Math.pow(v.y,2)+Math.pow(v.z,2))));
        }
        trans.m00 = Math.cos(angle1);
        trans.m01 = -1*Math.sin(angle1)*Math.sin(angle2);
        trans.m02 = -1*Math.sin(angle1)*Math.cos(angle2);
        trans.m10 = -1*Math.sin(angle1);
        trans.m11 = -1*Math.cos(angle1)*Math.sin(angle2);
        trans.m12 = -1*Math.cos(angle1)*Math.cos(angle2);
        trans.m20 = 0;
        trans.m21 = Math.cos(angle2);
        trans.m22 = -1*Math.sin(angle2);
        Transform3D combine = new Transform3D(trans, newP,1);
        setViewTransform(combine);
    }


 

/*
    public void setVpBehavior(ViewPlatformBehavior b)
    {
        vpBehavior = b;
	    mUniverse.getViewingPlatform().setViewPlatformBehavior(b );
        if(b == null)  
             mUniverse.getViewingPlatform().setNominalViewingTransform();
    }
    
    public Behavior getVpBehavior()
    {
        return vpBehavior;
    }
    
    */
    
     /*
    public void setProjectionPolicy(int policy)
    {
        mView.setProjectionPolicy(policy);
    }
    */
     public Vector3d getCameraAngle() {
        return cameraAngle;
    }
 
 
   public int getPickMode()
    {
        return pickMode;
    }
    
   public abstract void setFogTransform(Transform3D trans) ;
 
}


    
