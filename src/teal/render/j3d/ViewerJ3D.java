/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ViewerJ3D.java,v 1.46 2010/07/16 22:30:35 stefan Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.*;
import java.util.Collection;
import java.util.Iterator;

import javax.media.j3d.*;
import javax.swing.JFileChooser;
import javax.vecmath.*;

import teal.render.HasFog;
import teal.render.HasNode3D;
import teal.render.TAbstractRendered;
import teal.render.scene.TNode3D;
import teal.render.viewer.*;
import teal.sim.simulation.TSimulation;
import teal.util.TDebug;
import teal.visualization.image.ImageIO;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;


/**
 * A Java3D implementation of the AbstractViewer3D/TViewer interface.  Manages all aspects of the Java3D scenegraph, including the Canvas3D
 * on which it is displayed.  
 * 
 */

public class ViewerJ3D extends teal.render.viewer.AbstractViewer3D implements HasUniverse, HasPickCanvas {

    private static final long serialVersionUID = 3257005466751218224L;
    
    /* Java3D objects */
    /* package */ SimpleUniverse mUniverse;
    /* package */ View mView;
    /* package */ com.sun.j3d.utils.universe.Viewer mViewer;
    /* package */ //ViewPlatform mPlatform;
    /* package */ PickCanvas pickCanvas;
    
 
    protected FrameCheck mFrameCheck;
    ///
    protected FogBehavior mFogBehavior;

    // SceneGraph Members
    protected BranchGroup sceneRoot;
    protected TransformGroup sceneScale;
    protected TransformGroup sceneTrans;
    protected Background bgNode;
    protected Switch mLightSwitch;
    protected Fog mFog;
    // these values are used in setFogTransform()
    protected double fogTransformFrontScale = 0.99;
    protected double fogTransformBackScale = 1.2;
    // This is a dummy that gets scoped when we want fog turned off
    private static ShapeNode fogDummy = new ShapeNode();

    protected AlternateAppearance mSelectApp;
    
    protected MoveGizmo mMoveGizmo = null;
    
    protected boolean saveFrame = false;
    protected File curDir = null;
    protected JFileChooser fc = null;

    static 
    {
       sInfiniteBounds = new BoundingSphere(new Point3d(),Double.MAX_VALUE);
    }
   
    public ViewerJ3D() {
        this(true,false);
    }

    /**
     * Constructs a ViewerJ3D.
     * 
     * @param useDefaultScene determines whether or not the viewer should use the default lighting arrangement.
     * @param tryStereo determines whether the viewer should be configured for stereoscopic rendering (this should almost always be false).
     */
    public ViewerJ3D(boolean useDefaultScene, boolean tryStereo) {
        super();
        
       
        //mCanvas = initCanvas3D(wlStuffinTeal.getWLUniverse(),tryStereo);
        mCanvas = initCanvas3D(tryStereo);
        add(mCanvas);
        ((Canvas3D)mCanvas).setDoubleBufferEnable(true);

        mUniverse = new SimpleUniverse((Canvas3D)mCanvas);
        mUniverse.getViewingPlatform().setNominalViewingTransform();
        mViewer = mUniverse.getViewer();
        mView = mViewer.getView();
        //mView.setBackClipPolicy(View.VIRTUAL_EYE);
        //mView.setFrontClipPolicy(View.VIRTUAL_EYE);
        
                // Create the root of the branch graph
        sceneRoot = new BranchGroup();
        sceneRoot.setCapability(BranchGroup.ALLOW_DETACH);
        sceneRoot.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        sceneRoot.setCapability(Group.ALLOW_CHILDREN_READ);
        sceneRoot.setCapability(Group.ALLOW_CHILDREN_WRITE);
        sceneRoot.setCapability(Node.ALLOW_BOUNDS_READ);

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        sceneScale = new TransformGroup();
        sceneScale.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        sceneScale.setCapability(Group.ALLOW_CHILDREN_READ);
        sceneScale.setCapability(Group.ALLOW_CHILDREN_WRITE);
        sceneScale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        sceneScale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        sceneRoot.addChild(sceneScale);

        // Create the transform group node and initialize it to the
        // identity.  Enable the TRANSFORM_WRITE capability so that
        // our behavior code can modify it at runtime.  Add it to the
        // root of the subgraph.
        sceneTrans = new TransformGroup();
        sceneTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        sceneTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        sceneTrans.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        sceneTrans.setCapability(Group.ALLOW_CHILDREN_READ);
        sceneTrans.setCapability(Group.ALLOW_CHILDREN_WRITE);
        sceneScale.addChild(sceneTrans);
 
        // Set up the background
        bgNode = new Background(bgColor.x,bgColor.y,bgColor.z);
	    renderFlags ^= AbstractViewer3D.BACKGROUND_CHANGED;
         bgNode.setApplicationBounds(ViewerJ3D.sInfiniteBounds);
         bgNode.setCapability(Background.ALLOW_COLOR_READ);
         bgNode.setCapability(Background.ALLOW_COLOR_WRITE);
         bgNode.setCapability(Background.ALLOW_IMAGE_READ);
         bgNode.setCapability(Background.ALLOW_IMAGE_WRITE);
         bgNode.setCapability(Background.ALLOW_APPLICATION_BOUNDS_READ);
         bgNode.setCapability(Background.ALLOW_APPLICATION_BOUNDS_WRITE);
         sceneRoot.addChild(bgNode);       
        
        mSelectApp = new AlternateAppearance(Node3D.makeAppearance(new Color3f(Color.BLACK),new Color3f(Color.BLACK),80.f,0f,true,PolygonAttributes.POLYGON_LINE));      
        mSelectApp.setInfluencingBounds(ViewerJ3D.sInfiniteBounds);
        mSelectApp.setCapability(AlternateAppearance.ALLOW_APPEARANCE_READ);
        mSelectApp.setCapability(AlternateAppearance.ALLOW_APPEARANCE_WRITE);
        mSelectApp.addScope(sceneRoot);
        sceneRoot.addChild(mSelectApp);
       
        mFrameCheck = new FrameCheck(this,2);
        mFrameCheck.setSchedulingBounds(ViewerJ3D.sInfiniteBounds);
        mFrameCheck.setEnable(true);
        sceneRoot.addChild(mFrameCheck);
        
      
                       
        mLightSwitch = new Switch(Switch.CHILD_ALL);
        mLightSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
        mLightSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        mLightSwitch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        mLightSwitch.setCapability(Group.ALLOW_CHILDREN_READ);
        mLightSwitch.setCapability(Group.ALLOW_CHILDREN_WRITE);
	    mLightSwitch.setCapability(Node.ALLOW_PICKABLE_READ);	
        sceneRoot.addChild(mLightSwitch);
 		
        
        ////////////
        // Fog node for depth of field
        // Fog Setup
        mFogBehavior = new FogBehavior(this);
        mFogBehavior.setSchedulingBounds(ViewerJ3D.sInfiniteBounds);
        mFogBehavior.setEnable(true);
        sceneRoot.addChild(mFogBehavior);
        
        //Color3f col = new Color3f(teal.config.Teal.Background3D);
        mFog = new LinearFog(new Color3f(0.078f,0.078f,0.313f),0.045,0.065);
        mFog.setColor(bgColor.x,bgColor.y,bgColor.z);
        
        mFog.setCapability(Fog.ALLOW_SCOPE_READ);
        mFog.setCapability(Fog.ALLOW_SCOPE_WRITE);
        mFog.setCapability(LinearFog.ALLOW_DISTANCE_READ);
        mFog.setCapability(LinearFog.ALLOW_DISTANCE_WRITE);
        mFog.setCapability(Fog.ALLOW_COLOR_READ);
        mFog.setCapability(Fog.ALLOW_COLOR_WRITE);
        mFog.setCapability(Fog.ALLOW_INFLUENCING_BOUNDS_READ);
        mFog.setCapability(Fog.ALLOW_INFLUENCING_BOUNDS_WRITE);
        
       //mFog.setInfluencingBounds(mBounds);
        mFog.setInfluencingBounds(new BoundingSphere(new Point3d(),100.0));
        //mFog.setInfluencingBounds(new BoundingSphere(new Point3d(),0.001));
        
        //ShapeNode dummy = new ShapeNode();
        mFog.addScope(fogDummy);
        sceneRoot.addChild(mFog);
        initFogTransform();
        ////////////////
        
        /* Picking Support */
        
        mMoveGizmo = new MoveGizmo();
        mMoveGizmo.setElement(null);
        ((TransformGizmo)mMoveGizmo).setShown(showGizmos);
        mMoveGizmo.setVisible(false);
        sceneTrans.addChild((Node) mMoveGizmo);
        pickCanvas = new PickCanvas((Canvas3D)mCanvas, sceneRoot);

        setSelectManager(new SelectManagerImpl());
        mBehaviorMgr = new BehaviorManager((TransformGroup) mMoveGizmo);
        ((BehaviorManager)mBehaviorMgr).setSchedulingBounds(ViewerJ3D.sInfiniteBounds);
        mBehaviorMgr.setViewer(this);
        mBehaviorMgr.setRefreshOnDrag(refreshOnDrag);
        sceneRoot.addChild((Node)mBehaviorMgr); 
        setPickMode(PickCanvas.BOUNDS);
        
        setDefaults(); 
        if (useDefaultScene) {
            setDefaultLights();
        }   
       
        setPickTolerance(1.0f);
        
        setPicking(true);

        mUniverse.addBranchGraph(sceneRoot);
        
        displayBounds(new teal.render.BoundingSphere(mBounds));
        mCanvas.setVisible(true);

        // This fixes the orbit behavior bug where the transform is buggered the first time you try to rotate.
        // seems like the vpBehavior isn't being enabled on startup (gets enabled after the first mousePressed)
        // but i'm not sure why.  this is obviously a hack until i figure out how to fix this the right way.
        // (that means don't yell at me, Phil!!!)
        //((BehaviorManager)mBehaviorMgr).getVpBehavior().setEnable(true);
        
    }
    
    protected Canvas3D initCanvas3D(SimpleUniverse universe, boolean stereo) {
        /*GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        if(stereo)
            template.setStereo(GraphicsConfigTemplate3D.PREFERRED);
        GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().
        getDefaultScreenDevice().getBestConfiguration(template);

        TDebug.println(2,"GraphicsConfig: " + gcfg);*/
        
    	Canvas3D c = universe.getViewer().getView().getCanvas3D(0); //new Canvas3D(gcfg);
        if(stereo) {
            TDebug.println(2,"Stereo Available: " + c.getStereoAvailable());
            TDebug.println(2,"Stereo Enabled: " + c.getStereoEnable());

            c.setStereoEnable(true);
            TDebug.println(2,"After set Stereo Enabled: " + c.getStereoEnable());
        }	
        return c;
    }

    public void addBehavior( Behavior b )
    {
    	mUniverse.getLocale().removeBranchGraph( sceneRoot ) ;
    	sceneRoot.addChild( b ) ;
    	mUniverse.getLocale().addBranchGraph( sceneRoot ) ;
    }
    /* (non-Javadoc)
     * @see teal.render.viewer.TViewer#getBackgroundColor()
     */
    public Color getBackgroundColor()
    {
       Color3f color = new Color3f();
       bgNode.getColor(color);
       return color.get();
    }

    /**
     * @see teal.render.viewer.TViewer3D#setSceneScale(javax.vecmath.Vector3d)
     */
    public void setSceneScale(Vector3d scale)
    {
        Transform3D t3d = new Transform3D();
        t3d.setScale(scale);
        sceneScale.setTransform(t3d);
    }
    
    public Vector3d getSceneScale()
    {
    	Transform3D t1 = new Transform3D();
    	Vector3d vec = new Vector3d();
        sceneScale.getTransform(t1);
        t1.getScale(vec);
        return vec;       
    }
    
  
    // Fog methods
    /**
     * Sets the "front distance" property of the fog node.  That is the distance (from the camera) at which
     * the fog begins.
     * 
     */
    public void setFogFrontDistance(double front) {
    	((LinearFog)mFog).setFrontDistance(front);
    }
    /**
     * returns the "front distance" of the fog node.
     * 
     */
    public double getFogFrontDistance() {
    	return ((LinearFog)mFog).getFrontDistance();
    }
    /**
     * Sets the "back distance" of the fog node. That is the distance (from the camera) beyond which the fog
     * density is 100%.
     * @param back
     */
    public void setFogBackDistance(double back) {
    	((LinearFog)mFog).setBackDistance(back);
    }
    /** 
     * returns the "back distance" of the fog node.
     * 
     */
    public double getFogBackDistance() {
    	return ((LinearFog)mFog).getBackDistance();
    }
    
    /**
     * sets fogTransformFrontScale.  This is used in setFogTransform().
     * fogTransformFrontScale multiplies the distance of the camera from the origin when 
     * setting fog via setFogTransform().
     * @param percent
     */
    public void setFogTransformFrontScale(double percent) {
    	fogTransformFrontScale = percent;
    }
    
    public double getFogTransformFrontScale() {
    	return fogTransformFrontScale;
    }
    
    public void setFogTransformBackScale(double percent) {
    	fogTransformBackScale = percent;
    }
    
    public double getFogTransformBackScale() {
    	return fogTransformBackScale;
    }
    
   
    /**
     * Sets the influencing bounds of the fog node.  That is the region in which the fog is applied.
     * @param bounds
     */
    public void setFogInfluencingBounds(teal.render.Bounds bounds) {
    	mFog.setInfluencingBounds(Node3D.getJ3dBounds(bounds));
    }
    
    public teal.render.Bounds getFogInfluencingBounds() {
    	return Node3D.getTealBounds(mFog.getInfluencingBounds());
    }
    /**
     * enables or disables the fog node by setting its influencing bounds to either a BoundingSphere
     * of radius 100.0, or to zero, respectively.
     * @param enabled
     */
    public void setFogEnabled(boolean enabled) {
    	if (enabled) {
    		//mFog.setInfluencingBounds(new BoundingSphere(new Point3d(0,0,0),100.));
    		mFog.removeAllScopes();
    	}
    	else {
    		//mFog.setInfluencingBounds(new BoundingSphere(new Point3d(0,0,0),0.));
    		mFog.removeAllScopes();
    		mFog.addScope(fogDummy);
    	}
    }
    public boolean isFogEnabled()
    {
        return (mFog.numScopes() > 0);
    }
    
    /**
     * This is the method called by the FogBehavior that adjusts the parameters of the fog node as the camera
     * transform changes.  Currently, it is designed to maintain an aesthetically pleasing "depth of field" effect,
     * regardless of the degree to which the camera is "zoomed".  In other words, it strives to keep objects at the
     * origin suitable "fogged", such that their front side is perfectly visible and their back side is moderately 
     * obscured by the fog.  If these adjustments were not made, the amount of fog obscuring an object would be 
     * proportional to the distance between the camera and object.  If you zoomed all the way out, objects at a 
     * given distance would become completely obscured by fog.
     * 
     * @param trans this typically represents the viewport transform supplied by the FogBehavior.
     */
    public void setFogTransform(Transform3D trans) {
    	
    	Vector3d t = new Vector3d();
		trans.get(t);
		double tlen = t.length();
		
		//setFogFrontDistance(fogTransformFrontScale*tlen);
		//setFogBackDistance(fogTransformBackScale*tlen);
		setFogFrontDistance(tlen-fogTransformFrontScale*0.5);
		setFogBackDistance(tlen+fogTransformBackScale*0.5);
		
    }
    
    public void initFogTransform() {
    	Transform3D trans = new Transform3D();
    	getUniverse().getViewingPlatform().getViewPlatformTransform().getTransform(trans);
    	setFogTransform(trans);
    }
    
    public void setFogColor(Color3f col) {
    	mFog.setColor(col);
    }
    
    
    public Graphics2D getGraphics2D() 
    {
        return ((Canvas3D)mCanvas).getGraphics2D();
    }

    public javax.media.j3d.Locale get3DLocale()
    {
        return mUniverse.getLocale();
    }

    
//    public Background getBackgroundNode()
//    {
//        return bgNode;
//    }
    
     public double getBackClipDistance()
     {
            return mView.getBackClipDistance();
     }
     
     public void setBackClipDistance(double cd)
     {
            mView.setBackClipDistance(cd);
     }
     
     public double getFrontClipDistance()
     {
        return mView.getFrontClipDistance();
     } 
     public void setFrontClipDistance(double cd)
     {
        mView.setFrontClipDistance(cd);
     }    
    
    public PickCanvas getPickCanvas()
    {
        if(pickCanvas == null)
            pickCanvas = new PickCanvas((Canvas3D)mCanvas, sceneRoot);
        return pickCanvas;
    }
    
    /**
     * @deprecated
     *
     */
    public void setTranslateEnable(boolean state)
    {
        //pickTrans.setEnable(state);
    }
    /**
     * @deprecated
     *
     */
    public boolean getTranslateEnable()
    {
        return true;
        //return pickTrans.getEnable();
    }
    /**
     * @deprecated
     *
     */
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
    /**
     * @deprecated
     *
     */
    public boolean getRotateEnable()
    {
        return true;
        //return pickRot.getEnable();
    }
    /**
     * @deprecated
     *
     */
    public void setZoomEnable(boolean state)
    {
        //pickZoom.setEnable(state);
    }
    /**
     * @deprecated
     *
     */
    public boolean getZoomEnable()
    {
        return true;
       //return pickZoom.getEnable();
    }

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
        if (mMoveGizmo != null)
        {
        	mMoveGizmo.setVisible(state);
        	((TransformGizmo)mMoveGizmo).setShown(state);
            
        }
        showGizmos = state;
    }    
    public boolean getShowGizmos()
    {
        return showGizmos;
    }
    public BranchGroup getScene()
    {
        return sceneRoot;
    }
    public SimpleUniverse getUniverse()
    {
        return mUniverse;
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

    public void setPickMode(int mode) {
        pickCanvas.setMode(mode);
    }

    public void setPickTolerance(float tolerance) {
        pickCanvas.setTolerance(tolerance);
    }
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
            mBounds = (teal.render.Bounds) pce.getNewValue();
            makeTransform(getWidth(),getHeight());
            displayBounds();
        }
        else {
            super.propertyChange(pce);
        }
    }

    public double getFieldOfView()
    {
        return mView.getFieldOfView();
    }
    public void setFieldOfView(double fov)
    {
        mView.setFieldOfView(fov);
    }
 
    public void doStatus() {
        TDebug.println(id + "\t3D Viewer info:");
        TDebug.println("BoundingArea: " + mBounds.toString());
        TDebug.println("View info:");
        TDebug.println("\tclipBack: " + mView.getBackClipDistance());
        TDebug.println("\tclibFront: " + mView.getFrontClipDistance());
        TDebug.println("\tFOV: " + mView.getFieldOfView());
        TDebug.println("\tScreenScale: " + mView.getScreenScale());
        TDebug.println("PlatformInfo: ");
        //TDebug.println("\tposition: ");
        TransformGroup tg = mUniverse.getViewingPlatform().getViewPlatformTransform();
        Transform3D vTrans = new Transform3D();
        tg.getTransform(vTrans);
        Vector3d pos = new Vector3d();
        Quat4d ori = new Quat4d();
        double scale = vTrans.get(ori,pos);
        AxisAngle4d axis = new AxisAngle4d();
        axis.set(ori);
        TDebug.println("\tViewPosition: " + pos);
        TDebug.println("\tViewAxisAngle: " + axis);
        TDebug.println("\tViewOrientation: " + ori);
        TDebug.println("\tViewScale: " + scale);
        /*
        TDebug.println("\tAffineTrans: " + getAffineTransform());
        TDebug.println("\t   Inverted: " + getInvertedAffineTransform());
        Transform3D ip2world = new Transform3D();
        mCanvas.getImagePlateToVworld(ip2world);
        TDebug.println("\tip2WTrans:\n" + ip2world);
        Transform3D ip2world2 = new Transform3D();
         mCanvas.getInverseVworldProjection(ip2world,ip2world2);
        TDebug.println("\tinverseWTrans:\n" + ip2world + "right:\n" + ip2world2);
        */
    }

    protected Canvas3D initCanvas3D(boolean stereo) {
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        if(stereo)
            template.setStereo(GraphicsConfigTemplate3D.PREFERRED);
        GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().
        	getDefaultScreenDevice().getBestConfiguration(template);

        TDebug.println("GraphicsConfig: " + gcfg);
        Canvas3D c = new Canvas3D(gcfg);
        if(stereo) {
            TDebug.println(2,"Stereo Available: " + c.getStereoAvailable());
            TDebug.println(2,"Stereo Enabled: " + c.getStereoEnable());

            c.setStereoEnable(true);
            TDebug.println(2,"After set Stereo Enabled: " + c.getStereoEnable());
        }	
        return c;
    }


    /**
    * Adds an TDrawable Object to the simulation.
    *
    * @param draw
    */
    synchronized public void addDrawable(TAbstractRendered draw) {
        super.addDrawable(draw);
        BranchGroup bg = null;
        if(draw instanceof HasNode3D) {
	        if (((HasNode3D)draw).getNode3D() instanceof BranchGroup) {
	        	bg = (BranchGroup) ((HasNode3D)draw).getNode3D();
	        }
        } else if (draw instanceof BranchGroup) {
        	bg = (BranchGroup) draw;
        }
        if(null != bg) {
	    	addBranchGroup(bg);
	    	/*
	    	 * edit by tina 1.5
	    	 */
	    	if( draw instanceof HasFog && ((HasFog)draw).isReceivingFog()) {
	    		mFog.addScope(bg);
	    	}
        }
    }
    
 

    /**
    * Adds an BranchGroup to the simulation.
    *
    * @param bg
    */
    private void addBranchGroup(BranchGroup bg) {
        TDebug.println(2,"AbstractViewer3D adding BranchGroup: " + bg.toString());
        if( bg.isLive() ) return;
        Node parentnode = bg.getParent();
        if(parentnode == null) {
	        sceneTrans.addChild(bg);
        }
        //System.out.println("ViewerJ3D addBranchGroup:  numChildren = " + sceneTrans.numChildren());
    }
    
    synchronized public void addDrawableToViewingPlatform(TAbstractRendered draw) {
    	super.addDrawable(draw);
    	 PlatformGeometry pg = mUniverse.getViewingPlatform().getPlatformGeometry();
    	 if (pg == null) {
    		 pg = new PlatformGeometry();
    		 pg.setCapability(PlatformGeometry.ALLOW_CHILDREN_EXTEND);
    	 }
    	 BranchGroup bg = null;
    	 if(draw instanceof BranchGroup) {
    		 bg = (BranchGroup)draw;
    	 }
    	 else if(draw instanceof HasNode3D) {
    		 TNode3D node3D = ((HasNode3D)draw).getNode3D();
    		 if(node3D instanceof BranchGroup) {
    			 bg = (BranchGroup) node3D;
    		 }
    	 }
    	 if(null != bg) {
			 pg.addChild(bg);
    	 }
		 getUniverse().getViewingPlatform().setPlatformGeometry(pg);
    }



    /** Removes a TDrawable object.
    *
    * @param draw
    */
    public void removeDrawable(TAbstractRendered draw) {
    	if(draw instanceof HasNode3D) {
            TNode3D node = (TNode3D) ((HasNode3D)draw).getNode3D();
            if (node != null) {
                node.detach();
                ((HasNode3D)draw).setNode3D(null);
            }
    	} else if(draw instanceof BranchGroup) {
    		((BranchGroup)draw).detach();
    	}
        super.removeDrawable(draw);
        
    }


    public void render(boolean doRepaint) {	
        //TDebug.println(2,"\tRender");
        //long lapsedTime = System.currentTimeMillis();	
    	/*
    	 * Tina edit 23.4
    	 */
        //mFrameCheck.setEnable(true);
    	/*
    	 * edit end
    	 */
        synchronized(drawObjects) {
	        Iterator<TAbstractRendered> it = drawObjects.iterator();
	        while (it.hasNext())
	        {
	           it.next().render();
	        }
        }
        // setBackgroundColor() wasn't actually being handled until I added this   -- Mike
        if ((renderFlags & AbstractViewer3D.BACKGROUND_CHANGED) == AbstractViewer3D.BACKGROUND_CHANGED) {
        	bgNode.setColor(new Color3f(bgColor.x,bgColor.y,bgColor.z));
        	renderFlags ^= AbstractViewer3D.BACKGROUND_CHANGED;
        }
        
        if (saveFrame) {
        	saveScreenImage();
        	saveFrame = false;
        }
        //TDebug.println(2,"AbstractViewer3D Rendering:");	
        //TDebug.println("\tRender time: " + (System.currentTimeMillis() - lapsedTime));
        //doStatus(2);
        renderComplete();
    }
    
    public void setSaveFrame(boolean save) {
    	saveFrame = save;
    }
    
    public boolean getSaveFrame() {
    	return saveFrame;
    }
    /**
     * This method doesn't work at the moment.  In principle, it should capture an image of the viewer and save
     * it to disk.  I suspect it is broken because it requires the viewer to be running in the OffScreen rendering mode,
     * which it isn't.  I can't find any proof of this requirement in any Java3D documentation, though.
     */
    public synchronized void saveScreenImage() {
		//if (mDLIC.isImageGenerated()) {
			//BufferedImage img = (BufferedImage) mDLIC.getImage();
			
			
			
			//Canvas3D c = (Canvas3D)((ViewerJ3D)mViewer).getCanvas();
    		Canvas3D c = (Canvas3D)mCanvas;
			//System.out.println("Canvas3D isOffscreen? " + c.isOffScreen());
			//c.waitForOffScreenRendering();
			Rectangle rect = c.getBounds();
			BufferedImage buf = new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_RGB);
			javax.media.j3d.Raster ras = new javax.media.j3d.Raster(new Point3f(-1.0f,-1.0f,-1.0f), javax.media.j3d.Raster.RASTER_COLOR,0,0,rect.width,rect.height,
					new ImageComponent2D(ImageComponent.FORMAT_RGB,buf),null);
			//c.getGraphicsContext3D().flush(true);
			c.getGraphicsContext3D().readRaster(ras);
			BufferedImage img = ras.getImage().getImage();
			
			
			if (img != null) {
				if (fc == null)
					fc = new JFileChooser();
				if (curDir != null)
					fc.setCurrentDirectory(curDir);
				int status = fc.showSaveDialog(this);
				if (status == JFileChooser.APPROVE_OPTION) {
					File file = null;
					curDir = fc.getCurrentDirectory();
					try {
						file = fc.getSelectedFile();
						ImageIO.writeJPEG(img, 300, file);
					} catch (IOException fnf) {
						TDebug.printThrown(
							fnf,
							" Trying to save file: " + file);
					}

				}
			}
		//}
	}
    

    public void paint(Graphics g) {
        TDebug.println(2,"AbstractViewer3D:paint called");
        //super.paint(g); dont do this it slows it down
        mCanvas.paint(g);
        paintBorder(g);
    }
    
    protected AmbientLight ambientLightNode;
    protected DirectionalLight light1;
    protected DirectionalLight light2;
    protected DirectionalLight light3;

	public final static BoundingSphere sInfiniteBounds;
    
    public void setDefaultLights() {

        BoundingSphere bounds =
            new BoundingSphere(new Point3d(0.0,0.0,0.0), 1000.0);

        // Set up the ambient light
        Color3f ambientColor = new Color3f(0.5f, 0.5f, 0.5f);
         //Color3f ambientColor = new Color3f(01.f, 01.f, 01.f);
        ambientLightNode = new AmbientLight(ambientColor);
        ambientLightNode.setInfluencingBounds(bounds);
        ambientLightNode.setCapability(Light.ALLOW_COLOR_READ);
        ambientLightNode.setCapability(Light.ALLOW_COLOR_WRITE);
        ambientLightNode.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_READ);
        ambientLightNode.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
        mLightSwitch.addChild(ambientLightNode);

        // Set up the directional lights
        Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
        Vector3f light1Direction  = new Vector3f(4.0f, -7.0f, -12.0f);
        //light1Color.scale(0.5f);
        Color3f light2Color = new Color3f(0.3f, 0.3f, 0.4f);
        Vector3f light2Direction  = new Vector3f(-6.0f, -2.0f, -1.0f);
        //light2Direction.scale(-1.f);
        Vector3f light3Direction  = new Vector3f(-6.0f, 2.0f, 1.0f);

        light1
            = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        light1.setCapability(DirectionalLight.ALLOW_DIRECTION_READ);
        light1.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);
        light1.setCapability(Light.ALLOW_COLOR_READ);
        light1.setCapability(Light.ALLOW_COLOR_WRITE);
        light1.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_READ);
        light1.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
        mLightSwitch.addChild(light1);

        light2
            = new DirectionalLight(light2Color, light2Direction);
        light2.setInfluencingBounds(bounds);
        light2.setCapability(DirectionalLight.ALLOW_DIRECTION_READ);
        light2.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);
        light2.setCapability(Light.ALLOW_COLOR_READ);
        light2.setCapability(Light.ALLOW_COLOR_WRITE);
        light2.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_READ);
        light2.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
        mLightSwitch.addChild(light2);


        light3
            = new DirectionalLight(light1Color, light3Direction);
        light3.setInfluencingBounds(bounds);
        light3.setCapability(DirectionalLight.ALLOW_DIRECTION_READ);
        light3.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);
        light3.setCapability(Light.ALLOW_COLOR_READ);
        light3.setCapability(Light.ALLOW_COLOR_WRITE);
        light3.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_READ);
        light3.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
        mLightSwitch.addChild(light3);

        displayBounds();
    }




    public void dispose() {
        mUniverse.removeAllLocales();

        super.destroy();

    }

    public void remove() {
    }

    public void useDefaultLights(boolean state) {
        int value = state ? Switch.CHILD_ALL : Switch.CHILD_NONE;
        mLightSwitch.setWhichChild(value);
    }

/*
    public Rendered pickObjects (BranchGroup shape) {
        TSimElement target= null;
        TSimElement obj= null;
        TNode node3D=null;
        Iterator it= drawObjects.iterator();
        while (it.hasNext()) {
            obj= (Rendered) it.next();
            node3D = obj.getNode3D();
            if (node3D.equals(shape)) {
                target= obj;
                TDebug.println(0, "Target found" + target.getID());
                break;
            }
        }
        return target;
    }
*/

    public void setViewTransform(Transform3D vTrans)
    {
        //vTrans.invert();
        //double [] data = new double[16];
        //vTrans.get(data);
        //TDebug.println("setViewTransform:");
        //TDebug.dump(data);
    	if(vTrans != null)
        mUniverse.getViewingPlatform().getViewPlatformTransform().setTransform(vTrans);
    }


    
    public Transform3D getViewTransform()
    {
        Transform3D trans = new Transform3D();
         mUniverse.getViewingPlatform().getViewPlatformTransform().getTransform(trans);
       return trans;
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
    

    public void setProjectionPolicy(int policy)
    {
        mView.setProjectionPolicy(policy);
    }

   public int getPickMode()
    {
        return pickMode;
    }
    
 
	/**
	 * @return Returns the ambientLightNode.
	 */
	public AmbientLight getAmbientLightNode() {
		return ambientLightNode;
	}
	/**
	 * @param ambientLightNode The ambientLightNode to set.
	 */
	public void setAmbientLightNode(AmbientLight ambientLightNode) {
		this.ambientLightNode = ambientLightNode;
	}
	/**
	 * @return Returns the light1.
	 */
	public DirectionalLight getLight1() {
		return light1;
	}
	/**
	 * @param light1 The light1 to set.
	 */
	public void setLight1(DirectionalLight light1) {
		this.light1 = light1;
	}
	/**
	 * @return Returns the light2.
	 */
	public DirectionalLight getLight2() {
		return light2;
	}
	/**
	 * @param light2 The light2 to set.
	 */
	public void setLight2(DirectionalLight light2) {
		this.light2 = light2;
	}
	/**
	 * @return Returns the light3.
	 */
	public DirectionalLight getLight3() {
		return light3;
	}
	/**
	 * @param light3 The light3 to set.
	 */
	public void setLight3(DirectionalLight light3) {
		this.light3 = light3;
	}
	
	public void setAlternateAppearance(AlternateAppearance app) {
		this.mSelectApp = app; //.setAppearance(app);
	}
	  
}


    
