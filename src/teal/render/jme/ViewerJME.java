/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ViewerJME.java,v 1.15 2010/09/03 16:42:55 stefan Exp $ 
 * 
 */

package teal.render.jme;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.Transform3D;
import javax.swing.JFileChooser;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import org.jdesktop.mtgame.AWTInputComponent;
import org.jdesktop.mtgame.CameraComponent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.InputManager;
import org.jdesktop.mtgame.OnscreenRenderBuffer;
import org.jdesktop.mtgame.ProcessorCollectionComponent;
import org.jdesktop.mtgame.RenderBuffer;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.mtgame.processor.OrbitCameraProcessor;

import teal.render.BoundingSphere;
import teal.render.Bounds;
import teal.render.ColorUtil;
import teal.render.HasNode3D;
import teal.render.TAbstractRendered;
import teal.render.scene.TNode3D;
import teal.render.viewer.AbstractViewer3D;
import teal.render.viewer.SelectManager;
import teal.render.viewer.TBehaviorManager;
import teal.util.TDebug;

import com.jme.light.DirectionalLight;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.FogState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.FogState.DensityFunction;
import com.jme.scene.state.RenderState.StateType;
import org.jdesktop.mtgame.RenderManager;

/**
 * Viewer implementation for jMonkeyEngine
 * 
 * @author Stefan
 * 
 * @see teal.render.j3d.ViewerJ3D
 */
//TODO: a bunch of stuff..this is a rather minimalistic implementation, does not implement
//      some functionality, which should be added in the future
public class ViewerJME extends AbstractViewer3D {

    private static final long serialVersionUID = 3257005466751218224L;
    
    /* Java3D objects */
//    /* package */ SimpleUniverse mUniverse;
//    /* package */ View mView;
//   /* package */ com.sun.j3d.utils.universe.Viewer mViewer;
    /* package */ //ViewPlatform mPlatform;`
//    /* package */ PickCanvas pickCanvas;
        
    protected RenderBuffer renderBuffer;
 
    protected Entity rootEntity;
    
//    protected FrameCheck mFrameCheck;
    ///
//    protected FogBehavior mFogBehavior;

    // SceneGraph Members
    protected Node sceneRoot;
    protected HashSet<LightNode> mLightSwitch;
    protected FogState mFog = null;
    // these values are used in setFogTransform()
    protected double fogTransformFrontScale = 0.99;
    protected double fogTransformBackScale = 1.2;
    // This is a dummy that gets scoped when we want fog turned off
    private static ShapeNode fogDummy = new ShapeNode();
    
    // Picking support
    protected TBehaviorManager mBehaviorMgr;
//    protected MoveGizmo mMoveGizmo = null;

//    protected AlternateAppearance mSelectApp;
    
    protected Node cameraSG = new Node("Camera SG");
    protected CameraNode cameraNode = new CameraNode("Camera", null);

    protected boolean saveFrame = false;
    protected File curDir = null;
    protected JFileChooser fc = null;
    
    //WonderlandStuff wlStuffinTeal = WonderlandStuff.getInstance();

    /**
     * 
     * @see teal.render.j3d.ViewerJ3D#ViewerJ3D()
     */
    public ViewerJME() {
        this(true);
    }

    /**
     * Constructs a ViewerJME.
     * 
     * @see teal.render.j3d.ViewerJ3D#ViewerJ3D(boolean, boolean)
     */
    public ViewerJME(boolean useDefaultScene) {
        super();
        frontClipDistance = 0.01;
        backClipDistance = 400;
        Logger.getLogger("com.jme.scene.Node").setLevel(Level.OFF);
        //creating world manager if not already there
        
        TealWorldManager.getWorldManager().getRenderManager().setDesiredFrameRate(30);
        

        //mCanvas = initCanvas3D(wlStuffinTeal.getWLUniverse(),tryStereo);
        mCanvas = initCanvas3D();
//        JPanel canvasPanel = new JPanel();
//        canvasPanel.setLayout(new GridBagLayout());
//        canvasPanel.add(mCanvas);
//        window.getContentPane().add(canvasPanel, BorderLayout.CENTER);
        
//        JFrame contentPane = this.getcon
        
        add(mCanvas);
        
        
        
//        ((Canvas3D)mCanvas).setDoubleBufferEnable(true);
        
        //creates display system with renderer
//        DisplaySystem.getDisplaySystem();
//      DisplaySystem.getDisplaySystem().getRenderer();

        rootEntity = new Entity("rootEntity");
        sceneRoot = new Node("sceneRoot");
//        sceneRoot.attachChild(new Box("testbox",new Vector3f(),0.1f,0.1f,0.1f));
        
        RenderComponent srComponent = TealWorldManager.getWorldManager().getRenderManager().createRenderComponent(sceneRoot);
        rootEntity.addComponent(RenderComponent.class, srComponent);
        
        
        if (useDefaultScene) {
            setDefaultLights();
        }
        

        //create camera
        init();
        
        /*
        mUniverse = new SimpleUniverse((Canvas3D)mCanvas);
        //mUniverse = wlStuffinTeal.getWLUniverse();
        mUniverse.getViewingPlatform().setNominalViewingTransform();
        mViewer = mUniverse.getViewer();
        mView = mViewer.getView();
        //mView.setBackClipPolicy(View.VIRTUAL_EYE);
        //mView.setFrontClipPolicy(View.VIRTUAL_EYE);
        */

        
        /*
        mSelectApp = new AlternateAppearance(Node3D.makeAppearance(new Color3f(Color.BLACK),new Color3f(Color.BLACK),80.f,0f,true,PolygonAttributes.POLYGON_LINE));      
        mSelectApp.setInfluencingBounds(AbstractViewer3D.sInfiniteBounds);
        mSelectApp.setCapability(AlternateAppearance.ALLOW_APPEARANCE_READ);
        mSelectApp.setCapability(AlternateAppearance.ALLOW_APPEARANCE_WRITE);
        mSelectApp.addScope(sceneRoot);
        sceneRoot.addChild(mSelectApp);
       */
        
//       RenderUpdateProcessor rup = new RenderUpdateProcessor(10);
//       this.rootEntity.addComponent(RenderUpdateProcessor.class, rup);
        
        /*
        mFrameCheck = new FrameCheck(this,2);
        mFrameCheck.setSchedulingBounds(AbstractViewer3D.sInfiniteBounds);
        mFrameCheck.setEnable(true);
        sceneRoot.addChild(mFrameCheck);
        */
      
        /* TODO:               
        mLightSwitch = new Switch(Switch.CHILD_ALL);
        mLightSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
        mLightSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        mLightSwitch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        mLightSwitch.setCapability(Group.ALLOW_CHILDREN_READ);
        mLightSwitch.setCapability(Group.ALLOW_CHILDREN_WRITE);
	    mLightSwitch.setCapability(Node.ALLOW_PICKABLE_READ);	
        sceneRoot.addChild(mLightSwitch);
 		*/
        
        
        /*
        ////////////
        // Fog node for depth of field
        // Fog Setup
        mFogBehavior = new FogBehavior(this);
        mFogBehavior.setSchedulingBounds(AbstractViewer3D.sInfiniteBounds);
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
        */
        
        /* Picking Support */
/* TODO:
        mMoveGizmo = new MoveGizmo();
        mMoveGizmo.setElement(null);
        ((TransformGizmo)mMoveGizmo).setShown(showGizmos);
        mMoveGizmo.setVisible(false);
        sceneTrans.addChild((Node) mMoveGizmo);
        pickCanvas = new PickCanvas((Canvas3D)mCanvas, sceneRoot);

        
        mBehaviorMgr = new BehaviorManager((TransformGroup) mMoveGizmo);
        ((BehaviorManager)mBehaviorMgr).setSchedulingBounds(AbstractViewer3D.sInfiniteBounds);
        mBehaviorMgr.setViewer(this);
        mBehaviorMgr.setRefreshOnDrag(refreshOnDrag);
        ((BehaviorManager)mBehaviorMgr).addToScene(sceneRoot); 
        setPickMode(PickCanvas.BOUNDS);
        
        setDefaults(); 
        if (useDefaultScene) {
            setDefaultLights();
        }   
       
        setPickTolerance(1.0f);
        
        setPicking(true);
	*/
        
//        displayBounds(new BoundingSphere(mBounds));
//        mCanvas.setVisible(true);

        // This fixes the orbit behavior bug where the transform is buggered the first time you try to rotate.
        // seems like the vpBehavior isn't being enabled on startup (gets enabled after the first mousePressed)
        // but i'm not sure why.  this is obviously a hack until i figure out how to fix this the right way.
        // (that means don't yell at me, Phil!!!)
        //((BehaviorManager)mBehaviorMgr).getVpBehavior().setEnable(true);
        
        TealWorldManager.getWorldManager().addEntity(rootEntity);
        
        displayBounds(new BoundingSphere(mBounds));
       
//       sceneRoot.setLocalScale(0.1f);

    }
    
    
    private void init() {

		final float frontClip = (float)this.frontClipDistance;
    	final float backClip = (float)this.backClipDistance;
    	
    	// FI XXX ME!! this is to prevent from a deadlock when camera is initialized.
    	// It's due to the fact that camera initialization waits until this swing
    	// component is fully initialized. But we are in a method called by the
    	// constructor
        new Thread(new Runnable () {       	                        
        	public void run() {

                cameraSG.attachChild(cameraNode);
//                cameraNode.setLocalTranslation(0, 0f, -15);
                
                WorldManager wm = TealWorldManager.getWorldManager();
                
                Entity cameraEntity = new Entity("DefaultCam");        		
        		CameraComponent cc = wm.getRenderManager()
        			.createCameraComponent(cameraSG, cameraNode,
        				1024, 1024, 20.0f, 1, frontClip , backClip, true);

        		renderBuffer.setCameraComponent(cc);        
        		cameraEntity.addComponent(CameraComponent.class, cc);
        		wm.addEntity(cameraEntity);
        		        	
                // Create the input listener and process for the camera
//                int eventMask = InputManager.KEY_EVENTS | InputManager.MOUSE_EVENTS;

        		//TODO: this is a default setup. replace this with a proper one to work like the J3D version.
        		//      Consider navigation mode! This could be done by writing another version of OrbitCameraProcessor
        		int eventMask = InputManager.MOUSE_EVENTS;
                AWTInputComponent cameraListener = (AWTInputComponent)wm.getInputManager().createInputComponent(mCanvas, eventMask);
                //FPSCameraProcessor eventProcessor = new FPSCameraProcessor(eventListener, cameraNode, wm, camera);
                OrbitCameraProcessor eventProcessor = new OrbitCameraProcessor(cameraListener, cameraSG, wm, cameraEntity);
                eventProcessor.setRunInRenderer(true);
         
                
                //Camera move processor
//                AWTInputComponent selectionListener = (AWTInputComponent)wm.getInputManager().createInputComponent(mCanvas, eventMask);
//                JBSelectionProcessor selector = new JBSelectionProcessor(selectionListener, wm, cameraEntity, cameraEntity, 1024, 1024, eventProcessor);
//                selector.setRunInRenderer(true);
         
                ProcessorCollectionComponent pcc = new ProcessorCollectionComponent();
                pcc.addProcessor(eventProcessor);
//                pcc.addProcessor(selector);

                //comment that out to have no navigation, but have zoom working correctly:
                cameraEntity.addComponent(ProcessorCollectionComponent.class, pcc);
                
        		for(LightNode ln : mLightSwitch) {
        	       wm.getRenderManager().addLight(ln);
        		}
        		
        		
        		//zbuf
        		ZBufferState zbuf = (ZBufferState)wm.getRenderManager().createRendererState(StateType.ZBuffer);
        		zbuf.setEnabled(true);
        		zbuf.setFunction(ZBufferState.TestFunction.LessThan);
//        		zbuf.setWritable(false);
        		sceneRoot.setRenderState(zbuf);
        		
        		//fog
        		mFog = (FogState)wm.getRenderManager().createRendererState(StateType.Fog);
        		mFog.setDensityFunction(DensityFunction.Linear);
        		mFog.setColor(ColorUtil.getColorRGBA(bgColor));
        		mFog.setEnabled(false);
        		sceneRoot.setRenderState(mFog);
        		
        		// The shader object code
        		GLSLShaderObjectsState shader = (GLSLShaderObjectsState) wm.getRenderManager().createRendererState(RenderState.StateType.GLSLShaderObjects);
        		shader.setUniform("color", new ColorRGBA(.0f, 1.0f, 1.0f, 1.0f));
        	 
        		//TealWorldManager.getWorldManager().addRenderUpdater(this, shader);
        		sceneRoot.setRenderState(shader);
        	            		
      	}
      }).start();
    }
    
    
//    protected Canvas3D initCanvas3D(SimpleUniverse universe, boolean stereo) {
        /*GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        if(stereo)
            template.setStereo(GraphicsConfigTemplate3D.PREFERRED);
        GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().
        getDefaultScreenDevice().getBestConfiguration(template);

        TDebug.println(2,"GraphicsConfig: " + gcfg);*/
        
//    	Canvas3D c = universe.getViewer().getView().getCanvas3D(0); //new Canvas3D(gcfg);
//        if(stereo) {
//            TDebug.println(2,"Stereo Available: " + c.getStereoAvailable());
//            TDebug.println(2,"Stereo Enabled: " + c.getStereoEnable());
//
//            c.setStereoEnable(true);
//            TDebug.println(2,"After set Stereo Enabled: " + c.getStereoEnable());
//        }	
//        return c;
//    }

    /*
    public void addBehavior( Behavior b )
    {
    	mUniverse.getLocale().removeBranchGraph( sceneRoot ) ;
    	sceneRoot.addChild( b ) ;
    	mUniverse.getLocale().addBranchGraph( sceneRoot ) ;
    }
    */
    
    /* (non-Javadoc)
     * @see teal.render.viewer.TViewer#getBackgroundColor()
     */
    public Color getBackgroundColor()
    {
//       Color3f color = new Color3f();
//       bgNode.getColor(color);
       return this.bgColor.get();
    }

    /**
     * @see teal.render.viewer.TViewer3D#setSceneScale(javax.vecmath.Vector3d)
     */
    public void setSceneScale(Vector3d scale)
    {
/*        Transform3D t3d = new Transform3D();
        t3d.setScale(scale);
        sceneScale.setTransform(t3d);
        */
    	sceneRoot.setLocalScale(new Vector3f((float)scale.x, (float)scale.y, (float)scale.z));
    }
    
    public Vector3d getSceneScale()
    {
/*    	Transform3D t1 = new Transform3D();
    	Vector3d vec = new Vector3d();
        sceneScale.getTransform(t1);
        t1.getScale(vec);
        return vec;       
        */
    	Vector3f scale = sceneRoot.getLocalTranslation();
    	return new Vector3d(scale.x, scale.y, scale.z);
    }
    // Fog methods
    /**
     * Sets the "front distance" property of the fog node.  That is the distance (from the camera) at which
     * the fog begins.
     * 
     */
    public void setFogFrontDistance(double front) {
    	mFog.setStart((float)front);
    }
    /**
     * returns the "front distance" of the fog node.
     * 
     */
    public double getFogFrontDistance() {
    	return mFog.getStart();
    }
    /**
     * Sets the "back distance" of the fog node. That is the distance (from the camera) beyond which the fog
     * density is 100%.
     * @param back
     */
    public void setFogBackDistance(double back) {
    	mFog.setEnd((float)back);
    }
    /** 
     * returns the "back distance" of the fog node.
     * 
     */
    public double getFogBackDistance() {
    	return mFog.getEnd();
    }
    
    /**
     * sets fogTransformFrontScale.  This is used in setFogTransform().
     * fogTransformFrontScale multiplies the distance of the camera from the origin when 
     * setting fog via setFogTransform().
     * @param percent
     */
    public void setFogTransformFrontScale(double percent) {
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

    	fogTransformFrontScale = percent;
    }
    
    public double getFogTransformFrontScale() {
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

    	return fogTransformFrontScale;
    }
    
    public void setFogTransformBackScale(double percent) {
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

    	fogTransformBackScale = percent;
    }
    
    public double getFogTransformBackScale() {
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

    	return fogTransformBackScale;
    }
    
   
    /**
     * Sets the influencing bounds of the fog node.  That is the region in which the fog is applied.
     * @param bounds
     */
    public void setFogInfluencingBounds(Bounds bounds) {
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

//    	mFog.setInfluencingBounds(bounds);
    }
    
    public Bounds getFogInfluencingBounds() {
//   		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

//    	return mFog.getInfluencingBounds();
   		return null;
    }
    /**
     * enables or disables the fog node by setting its influencing bounds to either a BoundingSphere
     * of radius 100.0, or to zero, respectively.
     * @param enabled
     */
    public void setFogEnabled(boolean enabled) {
/*    	if (enabled) {
    		//mFog.setInfluencingBounds(new BoundingSphere(new Point3d(0,0,0),100.));
    		mFog.removeAllScopes();
    	}
    	else {
    		//mFog.setInfluencingBounds(new BoundingSphere(new Point3d(0,0,0),0.));
    		mFog.removeAllScopes();
    		mFog.addScope(fogDummy);
    	}
    	*/
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

 		mFog.setEnabled(enabled);
    }
    public boolean isFogEnabled()
    {
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub
//        return (mFog.numScopes() > 0);
 		return mFog.isEnabled();
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
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub
//    	Transform3D trans = new Transform3D();
//    	getUniverse().getViewingPlatform().getViewPlatformTransform().getTransform(trans);
//    	setFogTransform(trans);
    }
    
    public void setFogColor(Color3f col) {
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub

//    	mFog.setColor(col);
    }
    
    
    public Graphics2D getGraphics2D() 
    {
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub

 		return null;
//        return ((Canvas3D)mCanvas).getGraphics2D();
    }

    //FIXXME: not used
//    public javax.media.j3d.Locale get3DLocale()
//    {
//        return mUniverse.getLocale();
//    }

    
//    public Background getBackgroundNode()
//    {
//        return bgNode;
//    }
    
     public double getBackClipDistance()
     {
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
 		
 		return this.backClipDistance;
     }
     
     public void setBackClipDistance(double cd)
     {
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
 		this.backClipDistance = cd;
 		this.setClipDistances();

//            mView.setBackClipDistance(cd);
     }
     
     @Override
     public double getFrontClipDistance()
     {
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

 		return this.frontClipDistance;
//        return mView.getFrontClipDistance();
     } 
     public void setFrontClipDistance(final double cd)
     {
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method"); 		
 		this.frontClipDistance = cd;
 		this.setClipDistances();
//        mView.setFrontClipDistance(cd);
     } 
     
     private void setClipDistances() {
    	 
    	 final float front = (float)this.frontClipDistance;
    	 final float back = (float)this.backClipDistance;
    	 TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
    		 public void update(Object arg) {
    			 renderBuffer.getCameraComponent().setClipDistances(front,back);
    		 };
 		
    	 }, null);    	 
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
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub

//        if (mMoveGizmo != null)
//        {
//            mMoveGizmo.setShown(state);
//            
//        }
//        showGizmos = state;
    }    
    public boolean getShowGizmos()
    {
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub

//        return showGizmos;
		return false;
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
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub

//        pickCanvas.setMode(mode);
    }

    public void setPickTolerance(float tolerance) {
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub

//        pickCanvas.setTolerance(tolerance);
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
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

//        mBehaviorMgr.setMouseMoveScale(scale);
    }

    //private void setViewMode(int mode) {
    //    view.setProjectionPolicy(mode);
    //}

    public double getFieldOfView()
    {
//		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

		return this.fieldOfView;

//        return mView.getFieldOfView();
    }
    
    public void setFieldOfView(double fov)
    {
    	this.fieldOfView = fov;
    	final RenderBuffer rb = renderBuffer;
    	TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater(){
    		public void update(Object arg) {
    			double f = (Double)arg;
    	    	rb.getCameraComponent().setFieldOfView(FastMath.PI*(float)f);    			
    		}
    	}, fov);
//        mView.setFieldOfView(fov);
    }
 
    public void doStatus() {
        TDebug.println(id + "\t3D Viewer info:");
        TDebug.println("BoundingArea: " + mBounds.toString());
        TDebug.println("View info:");
//        TDebug.println("\tclipBack: " + mView.getBackClipDistance());
//        TDebug.println("\tclibFront: " + mView.getFrontClipDistance());
//        TDebug.println("\tFOV: " + mView.getFieldOfView());
//        TDebug.println("\tScreenScale: " + mView.getScreenScale());
        TDebug.println("PlatformInfo: ");
        //TDebug.println("\tposition: ");
 //       TransformGroup tg = mUniverse.getViewingPlatform().getViewPlatformTransform();
        Transform3D vTrans = new Transform3D();
 //       tg.getTransform(vTrans);
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

    protected Canvas initCanvas3D() {
    	
    	renderBuffer = TealWorldManager.getWorldManager().getRenderManager().createRenderBuffer(RenderBuffer.Target.ONSCREEN, 1000, 1000); //FIXXME
    	RenderManager tmp = TealWorldManager.getWorldManager().getRenderManager();
      tmp.addRenderBuffer(renderBuffer);

    	Canvas canvas = ((OnscreenRenderBuffer)renderBuffer).getCanvas();
//    	canvas.setBackground(this.bgColor.get());
    	canvas.setVisible(true);
    	canvas.setBounds(0, 0, 1024, 1024); // FIXXME    
    	return canvas;
    	
/*    	
    	
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
//        if(stereo)
//            template.setStereo(GraphicsConfigTemplate3D.PREFERRED);
        GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().
        getDefaultScreenDevice().getBestConfiguration(template);

        TDebug.println(2,"GraphicsConfig: " + gcfg);
        Canvas3D c = new Canvas3D(gcfg);
//        if(stereo) {
//            TDebug.println(2,"Stereo Available: " + c.getStereoAvailable());
//            TDebug.println(2,"Stereo Enabled: " + c.getStereoEnable());

//            c.setStereoEnable(true);
//            TDebug.println(2,"After set Stereo Enabled: " + c.getStereoEnable());
//        }	
        return c; */
    }


    /**
    * Adds an TDrawable Object to the simulation.
    *
    * @param draw
    */
    synchronized public void addDrawable(TAbstractRendered draw) {
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
// 		if(draw instanceof Wall)
// 			return;
// 		if(draw instanceof RingOfCurrent)
//			return;
 		
        super.addDrawable(draw);
        Spatial bg = null;
        if(draw instanceof HasNode3D) {
        	TNode3D node = ((HasNode3D)draw).getNode3D();
        	if(node instanceof Spatial) {
        		bg = (Spatial)node;
        	}
	        	
        } else {
        	TDebug.println(1,"addDrawable: trying to add non Node3D object");
        }
        if(null != bg) {
	    	addBranchGroup(bg);
	    	/*
	    	 * edit by tina 1.5
	    	 */
//	    	if( draw instanceof HasFog && ((HasFog)draw).isReceivingFog()) {
//	    		mFog.addScope(bg);
//	    	}
        }
    }
    
    public void addDrawableBulk(Collection drawn) {
    	synchronized(drawObjects) {
    		this.drawObjects.addAll(drawn);
    	}
    }
    
    public void removeDrawableBulk(Collection drawn) {
    	synchronized(drawObjects) {
    		this.drawObjects.removeAll(drawn);
    	}
    }

    /**
    * Adds an BranchGroup to the simulation.
    *
    * @param bg
    */
    private void addBranchGroup(Spatial bg) {
//        TDebug.println(2,"ViewerJME adding BranchGroup: " + bg.toString());

        if(bg.isLive()) return;

        if(bg.getParent() != null) return;

        sceneRoot.attachChild(bg);
        sceneRoot.updateModelBound();
        TealWorldManager.getWorldManager().addToUpdateList(sceneRoot);

    }
    
    synchronized public void addDrawableToViewingPlatform(TAbstractRendered draw) {
    	super.addDrawable(draw);

//    	TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");

    	if(draw instanceof HasNode3D) {
    		Node3D node3D = (Node3D)((HasNode3D)draw).getNode3D();
    		cameraSG.attachChild(node3D);
    		TealWorldManager.getWorldManager().addToUpdateList(cameraSG);
    	} else {
    		TDebug.println(1,"WARNING: adding drawabel to viewing platform only supported with HasNode3D interface!");
    	}
    		
    	
    	/*
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
		 */
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
    	} else {
    		TDebug.println(1,"can only remode HasNode3D object");
    	}
        super.removeDrawable(draw);
        
    }


    @Override
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
        	renderBuffer.setBackgroundColor(ColorUtil.getColorRGBA(bgColor));
        	renderFlags ^= AbstractViewer3D.BACKGROUND_CHANGED;
        }
        
//        if (saveFrame) {
//        	saveScreenImage();
//        	saveFrame = false;
//        }
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
    /* TODO:
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
	*/
    

    public void paint(Graphics g) {
        TDebug.println(2,"AbstractViewer3D:paint called");
        //super.paint(g); dont do this it slows it down
        mCanvas.paint(g);
        paintBorder(g);
    }
    
    protected PointLight ambientLightNode;
    protected DirectionalLight light1;
    protected DirectionalLight light2;
    protected DirectionalLight light3;
    
    
    private LightNode createLight(float x, float y, float z) {
        LightNode lightNode = new LightNode();
        DirectionalLight light = new DirectionalLight();
        light.setDiffuse(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setAmbient(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
        light.setSpecular(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f));
        light.setEnabled(true);
        lightNode.setLight(light);
        lightNode.setLocalTranslation(x, y, z);
        light.setDirection(new Vector3f(-x, -y, -z));
        return (lightNode);
    }

    // TODO:
    public void setDefaultLights() {

    	mLightSwitch = new HashSet<LightNode>();
//        BoundingSphere bounds =
//            new BoundingSphere(new Point3d(0.0,0.0,0.0), 1000.0);

    	LightNode globalLight1 = null;
        LightNode globalLight2 = null;
        LightNode globalLight3 = null;

        float radius = 75.0f;
        float lheight = 50.0f;
        float x = (float)(radius*Math.cos(Math.PI/6));
        float z = (float)(radius*Math.sin(Math.PI/6));
        globalLight1 = createLight(x, lheight, z);
        x = (float)(radius*Math.cos(5*Math.PI/6));
        z = (float)(radius*Math.sin(5*Math.PI/6));
        globalLight2 = createLight(x, lheight, z);
        x = (float)(radius*Math.cos(3*Math.PI/2));
        z = (float)(radius*Math.sin(3*Math.PI/2));
        globalLight3 = createLight(x, lheight, z);
    	
        mLightSwitch.add(globalLight1);
        mLightSwitch.add(globalLight2);
        mLightSwitch.add(globalLight3);
        
    	
        // Set up the ambient light
        ColorRGBA ambientColor = new ColorRGBA(0.5f, 0.5f, 0.5f,1f);
         //Color3f ambientColor = new Color3f(01.f, 01.f, 01.f);
        ambientLightNode = new PointLight();
        ambientLightNode.setAmbient(ambientColor);
        ambientLightNode.setEnabled(true);
        LightNode ambientLight = new LightNode("ambient");
        ambientLight.setLight(ambientLightNode);
        ambientLight.setLocalTranslation(20, 0, 0);
 //       mLightSwitch.add(ambientLight);
        
//        ambientLightNode.setInfluencingBounds(bounds);
//        mLightSwitch.addChild(ambientLightNode);

        // Set up the directional lights
        ColorRGBA light1Color = new ColorRGBA(1.0f, 1.0f, 0.9f, 1f);
        Vector3f light1Direction  = new Vector3f(4.0f, -7.0f, -12.0f);
        //light1Color.scale(0.5f);
        ColorRGBA light2Color = new ColorRGBA(0.3f, 0.3f, 0.4f, 1f);
        Vector3f light2Direction  = new Vector3f(-6.0f, -2.0f, -1.0f);
        //light2Direction.scale(-1.f);
        Vector3f light3Direction  = new Vector3f(-6.0f, 2.0f, 1.0f);

        light1 = new DirectionalLight();
        light1.setDiffuse(light1Color);
        light1.setSpecular(light1Color);
        light1.setDirection(light1Direction);
        light1.setEnabled(true);
        LightNode ln1 = new LightNode("light1");
        ln1.setLight(light2);
//        light1.setInfluencingBounds(bounds);
//        mLightSwitch.addChild(light1);
//        mLightSwitch.add(ln1);

        light2 = new DirectionalLight();
        light2.setDiffuse(light2Color);
        light2.setSpecular(light2Color);
        light2.setDirection(light2Direction);
        LightNode ln2 = new LightNode("light2");
        ln2.setLight(light2);
//        light2.setInfluencingBounds(bounds);
//        mLightSwitch.addChild(light2);
//        mLightSwitch.add(ln2);


        light3 = new DirectionalLight();
        light3.setDiffuse(light1Color);
        light3.setSpecular(light1Color);
        light3.setDirection(light3Direction);
        LightNode ln3 = new LightNode("light3");
        ln3.setLight(light3);
//        light3.setInfluencingBounds(bounds);
        
//        mLightSwitch.add(ln3);

        displayBounds();
    }




    public void dispose() { //FIXXME
//        mUniverse.removeAllLocales();
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
 		sceneRoot.detachAllChildren(); 		//FIXXME
 		renderBuffer.setEnable(false);
 		
 		TealWorldManager.getWorldManager().getRenderManager().quit();
 		TealWorldManager.getWorldManager().getRenderManager().removeRenderBuffer(renderBuffer);
 			
// 		renderBuffer = null;
// 		DisplaySystem.getDisplaySystem().close();
// 		TealWorldManager.getWorldManager().removeEntity(rootEntity);
// 		TealWorldManager.getWorldManager().shutdown();

// 		TealWorldManager.setWorldManager(null);
        super.destroy();
    }

    public void remove() {
    }

    public void useDefaultLights(boolean state) {
 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
 		//TODO

//    	if(state) {
//    		if(!sceneRoot.hasChild(mLightSwitch))
//    			sceneRoot.attachChild(mLightSwitch);
//    	} else {
//    		sceneRoot.detachChild(mLightSwitch);
//    	}
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

    //FIXXXME
    private float trFactor = 5.0f;
    
    @Override
    public void setViewTransform(Transform3D vTrans)
    {
        //vTrans.invert();
        //double [] data = new double[16];
        //vTrans.get(data);
        //TDebug.println("setViewTransform:");
        //TDebug.dump(data);
//        mUniverse.getViewingPlatform().getViewPlatformTransform().setTransform(vTrans);
// 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
 		cameraSG.setLocalTranslation(TransformUtil.getTranslationFromTransform3D(vTrans).mult(trFactor));
 		cameraSG.setLocalScale(TransformUtil.getScaleFromTransform3D(vTrans));
 		cameraSG.setLocalRotation(TransformUtil.getRotationFromTransform3D(vTrans));
 		TealWorldManager.getWorldManager().addToUpdateList(cameraSG);
    }


    @Override    
    public Transform3D getViewTransform()
    {
    	
 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
 		Transform3D trans3d = TransformUtil.getTransform3D(cameraSG);
 		Vector3f trJME = cameraSG.getLocalTranslation();
 		trans3d.set(new javax.vecmath.Vector3f(trFactor*trJME.x,trFactor*trJME.y,trFactor*trJME.z));
 		return trans3d;
//        Transform3D trans = new Transform3D();
//         mUniverse.getViewingPlatform().getViewPlatformTransform().getTransform(trans);
//       return trans;
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
 		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub

//        mView.setProjectionPolicy(policy);
    }

   public int getPickMode()
    {
        return pickMode;
    }
    
 
	/**
	 * @return Returns the ambientLightNode.
	 */
//	public AmbientLight getAmbientLightNode() {
//		return ambientLightNode;
//	}
	/**
	 * @param ambientLightNode The ambientLightNode to set.
	 */
//	public void setAmbientLightNode(AmbientLight ambientLightNode) {
//		this.ambientLightNode = ambientLightNode;
//	}
	/**
	 * @return Returns the light1.
	 */
//	public DirectionalLight getLight1() {
//		return light1;
//	}
	/**
	 * @param light1 The light1 to set.
	 */
//	public void setLight1(DirectionalLight light1) {
//		this.light1 = light1;
//	}
	/**
	 * @return Returns the light2.
	 */
//	public DirectionalLight getLight2() {
//		return light2;
//	}
	/**
	 * @param light2 The light2 to set.
	 */
//	public void setLight2(DirectionalLight light2) {
//		this.light2 = light2;
//	}
	/**
	 * @return Returns the light3.
	 */
//	public DirectionalLight getLight3() {
//		return light3;
//	}
	/**
	 * @param light3 The light3 to set.
	 */
//	public void setLight3(DirectionalLight light3) {
//		this.light3 = light3;
//	}
	
//	public void setAlternateAppearance(AlternateAppearance app) {
//		this.mSelectApp = app; //.setAppearance(app);
//	}



	@Override
	public int getNavigationMode() {
		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setNavigationMode(int flags) {
		TDebug.println("CALLED ViewerJME's " + Thread.currentThread().getStackTrace()[1].getMethodName() + " method");
		// TODO Auto-generated method stub
		
	}
}


    
