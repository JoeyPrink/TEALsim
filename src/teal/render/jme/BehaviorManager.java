/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BehaviorManager.java,v 1.2 2010/03/25 21:00:54 stefan Exp $ 
 * 
 */

package teal.render.jme;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;

import teal.render.*;
import teal.render.scene.TNode3D;
import teal.render.viewer.*;
import teal.ui.UIPanel;
import teal.util.TDebug;

import com.sun.j3d.utils.behaviors.vp.ViewPlatformBehavior;
import com.sun.j3d.utils.picking.*;


/**
 * A behavior class to process all viewer mouse events. It should be 
 * possible to provide default handlers for each of the types of behavior 
 * processing that needs to be done, or to be able to specify handlers 
 * for Pick, Translate, Rotate & Zoom.
 *
 * @version  $Revision: 1.2 $
 */

public class BehaviorManager extends Behavior
    implements TBehaviorManager
		
{
 
	TViewer3D mViewer = null; 
    SelectManager selectManager = null;  
      
    PickCanvas pickCanvas = null;
    //protected PickListener pickListener;
    protected TransformChangeListener rotHandler;
    protected TransformChangeListener transHandler;
    protected TransformChangeListener zoomHandler;
       
    protected TransformGroup mTransGroup;
    protected Transform3D transX;
    protected Transform3D transY;
    protected Transform3D transZ;
    protected Transform3D mTransform;
    protected Transform3D refTransform;
    javax.vecmath.Vector3d translation = new javax.vecmath.Vector3d();
    protected WakeupCriterion[] mouseEvents;
    protected WakeupOr mouseCriterion;
    
    protected int flags = 0;
    protected boolean reset = false;
    protected boolean invert = false;
    protected boolean isRelative = true;
    protected boolean wakeUp = false;
    protected int navMode;

    double xAngle = 0;
    double yAngle = 0;
    double xScale = .03;
    double yScale = .03;
    double zScale = .03;

    protected boolean pickEnabled = true;
    protected boolean rotateEnabled = true;
    protected boolean translateEnabled = true;
    protected boolean zoomEnabled = true;
    boolean refreshOnDrag = true;
    boolean cursorOnDrag = true;
    
    // Temp values
    boolean inPick = false;
    boolean wasRunning = false;
    TAbstractRendered pickObj = null;
    HasRotation rotObj = null;
    boolean invalidateImage = false;
    boolean forward = false;
    
    
    protected int x, y;
    protected int lastX, lastY;
    boolean inMove = false;
    boolean inRot = false;
    protected int mButton;
    
    protected Cursor tmpCursor = null;
    protected Cursor transparentCursor = null;
    protected boolean cursorHidden = false;
    
    

    /**
    * Initializes standard fields. Note that this behavior still
    * needs a transform group to work on (use setTransformGroup(tg)) and
    * the transform group must add this behavior.
    */
    public BehaviorManager() {
        this(0);
    }
    /**
    * Initializes standard fields. Note that this behavior still
    * needs a transform group to work on (use setTransformGroup(tg)) and
    * the transform group must add this behavior.
    */
    public BehaviorManager(int options) {
        flags = options;
        mViewer = null;
        pickCanvas = null;
        selectManager = null; // new SelectManagerImpl();
        mTransGroup = null;
        mTransform = new Transform3D();
        refTransform = new Transform3D();
        transX = new Transform3D();
        transY = new Transform3D();
        transZ = new Transform3D();
        reset = true;
        //pickListener = this;
        rotHandler = this;
        transHandler = this;
        zoomHandler = this;
        setScale(0.08);
        byte[] r = new byte[4]; 
        byte[] g = new byte[4];
        byte[] b = new byte[4];
         
        r[0] = (byte) 0;
        r[1] = (byte) 0;
        r[2] = (byte) 128;
        r[3] = (byte) 255;
        
        g[0] = (byte) 0;
        g[1] = (byte) 0;
        g[2] = (byte) 128;
        b[3] = (byte) 255;
        
        b[0] = (byte) 0;
        b[1] = (byte) 0;
        b[2] = (byte) 128;
        b[3] = (byte) 255;
        
        IndexColorModel cm = new IndexColorModel(2,4,r,g,b,0);
        BufferedImage img = new BufferedImage(16,16,BufferedImage.TYPE_BYTE_INDEXED,cm);
        transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(img,new Point(0,0),"transparent");
        
        //getVpBehavior().setEnable(selectManager.getNumberSelected() == 0);
    }

    /**
    * Creates a mouse behavior object with a given transform group, the group must be added to the scene.
    * @param transformGroup The transform group to be manipulated.
    */
    public BehaviorManager(TransformGroup transformGroup) {
        this(0);
        // need to remove old behavior from group 
        setTransformGroup(transformGroup);
    }

    /** Initializes the behavior.
    */

    public void initialize() {
        mouseEvents = new WakeupCriterion[3];
        mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);

        mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
        mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        //mouseEvents[3] = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);      


        mouseCriterion = new WakeupOr(mouseEvents);
        wakeupOn (mouseCriterion);
        x = 0;
        y = 0;
        lastX = 0;
        lastY = 0;
        xAngle = 0;
        yAngle = 0;
        if ((flags & INVERT_INPUT) == INVERT_INPUT) {
            invert = true;
            xScale *= -1;
            yScale *= -1;
            zScale *= -1;
        }
    }

public void addToScene(BranchGroup scene)
{
    scene.addChild(this);
}

    /* package */ void setVpBehavior(ViewPlatformBehavior vpb)
    {
        mViewer.getUniverse().getViewingPlatform().setViewPlatformBehavior(vpb);
    }

    /* package */ ViewPlatformBehavior getVpBehavior()
    {
        return mViewer.getUniverse().getViewingPlatform().getViewPlatformBehavior();
    }

/* package */ void hideCursor(boolean state)
{
    //TDebug.println("HideCursor: " + state);
    if (state)
    {
        if(mViewer instanceof UIPanel) {
            tmpCursor = ((UIPanel)mViewer).getCursor();
            ((UIPanel)mViewer).setCursor(transparentCursor);
	        cursorHidden = true;
        }
    }
    else
    {
        if (tmpCursor != null)
        {
            if(mViewer instanceof UIPanel) {
	        	((UIPanel)mViewer).setCursor(tmpCursor);
	            tmpCursor = null;
	            cursorHidden = false;
            }
        }
    }
}

public void setRefreshOnDrag(boolean state)
{
    refreshOnDrag = state;
}
public boolean getRefreshOnDrag()
{
    return refreshOnDrag;
}
public void setCursorOnDrag(boolean state)
{
    cursorOnDrag = state;
}
public boolean getCursorOnDrag()
{
    return cursorOnDrag;
}

   public SelectManager getSelectManager() {
        return selectManager;
    }

    public void setSelectManager(SelectManager sm) {
        selectManager = sm;
    }

    public void setViewer(TViewer viewer) {
        mViewer = (TViewer3D) viewer;
        pickCanvas = mViewer.getPickCanvas();
        selectManager = mViewer.getSelectManager();
        
    }
    public TViewer getViewer() {
        return mViewer;
    }
    public void setPickCanvas(PickCanvas canvas) {
        pickCanvas = canvas;
    }
    public PickCanvas getPickCanvas() {
        return pickCanvas;
    }
/*
    public void setPickListener(PickListener pick) {
        pickListener = pick;
    }
    public PickListener getPickListener() {
        return pickListener;
    }
 */   
       public void setPicking(boolean enable) {
       	// Disabling the vpBehavior was causing a bug in camera rotation that occured if your first action was to select
       	// an object in the viewer.
        //if(getVpBehavior() != null)
           //getVpBehavior().setEnable(!enable);
        pickEnabled = enable;
    }
    
    public int getNavigationMode()
    {
        return navMode;
    }
    public void setNavigationMode(int flag) {
        if ((flag != navMode) || (getVpBehavior() == null)) {

            setVpBehavior(null);
            if ((flag & TViewer.FLY) == TViewer.FLY) {
            /*		
                TFlyBehavior fly = new TFlyBehavior();
                fly.setTarget(mViewer.mUniverse.getViewingPlatform().getViewPlatformTransform());
                fly.setSchedulingBounds(mViewer. mInfiniteBounds );
                fly.setCollisionEnabled( false );
                mViewer.mUniverse.getViewingPlatform().setViewPlatformBehavior( fly );
                mVpBehavior = fly;
                */
            }
            else if ((flag & TViewer.ORBIT) == TViewer.ORBIT) {
                OrbitBehavior orbitBehavior= new OrbitBehavior((Canvas3D)((HasCanvas) mViewer).getCanvas(),				    
                    OrbitBehavior.PROPORTIONAL_ZOOM | OrbitBehavior.REVERSE_ROTATE	
                    | OrbitBehavior.REVERSE_TRANSLATE);
                orbitBehavior.setSchedulingBounds(AbstractViewer3D.sInfiniteBounds);
                orbitBehavior.setEnable(true);
                orbitBehavior.setTranslateEnable((flag & TViewer.VP_TRANSLATE) == TViewer.VP_TRANSLATE);
                orbitBehavior.setRotateEnable((flag & TViewer.VP_ROTATE) == TViewer.VP_ROTATE); 
                orbitBehavior.setZoomEnable((flag & TViewer.VP_ZOOM) == TViewer.VP_ZOOM);  
                //System.out.println("setNavigationMode is being called");
                setVpBehavior(orbitBehavior);      
                //System.out.println("vpBehavior enabled = " + getVpBehavior().getEnable());
            }
            else if (flag==TViewer.HOVER) {
                /*
                //removeAttachBehavior();
                vpBehavior = new THoverBehavior( mUniverse.getViewer().getView() );
                ((THoverBehavior)vpBehavior).setTarget(mUniverse.getViewingPlatform().getViewPlatformTransform() );
                vpBehavior.setSchedulingBounds(mInfiniteBounds );
                mUniverse.getViewingPlatform().setViewPlatformBehavior((THoverBehavior)vpBehavior );
                */
            }
            else 
                if(flag ==TViewer.ATTACH) {
                    /*
                TAttachBehavior atBehavior = new TAttachBehavior(this,mUniverse.getViewer().getView(),mCanvas,sceneRoot);
                atBehavior.setModel(theEngine);
                atBehavior.setTarget(mUniverse.getViewingPlatform().getViewPlatformTransform());
                atBehavior.setSchedulingBounds(mInfiniteBounds);
                setVpBehavior(atBehavior); 
                
                */
            }
            else if (flag==TViewer.NONE) {
                setVpBehavior(null);
            }
            navMode = flag;
        }
    }
    

    /** 
    * Swap a new transformGroup replacing the old one. This allows 
    * manipulators to operate on different nodes. This may be any TransformGroup, 
    * but will most likely be a TransformGizmo.
    * 
    * @see teal.render.j3d.TransformGizmo
    * @param transformGroup The *new* transform group to be manipulated.
    */
    public void setTransformGroup(TransformGroup transformGroup) {
        // need to remove old behavior from group 
        mTransGroup = transformGroup;
        mTransGroup.getTransform(mTransform);
        mTransGroup.getTransform(refTransform);
        transX = new Transform3D();
        transY = new Transform3D();
        reset = true;
    }

    /**
    * Return the TransformGroup on which this node is operating
    */
    public TransformGroup getTransformGroup() {
        return mTransGroup;
    }

    /**
    * set the current Transform3D of the monitored TransformGroup.
    */

    public void setTransform(Transform3D trans) {
    	if(null != mTransGroup) {
    		mTransGroup.setTransform(trans);
    	}
    	if(null != mTransform) {
    		mTransform.set(trans);
    	}
    	if(null != refTransform) {
    		refTransform.set(trans);
    	}
    }

    public void setTransform(Quat4d rot, javax.vecmath.Vector3d pos) {
        setTransform(new Transform3D(rot,pos,1.0));
    }

    public void setTransform(Quat4d rot,javax.vecmath.Vector3d pos,double scale) {
        setTransform(new Transform3D(rot,pos,scale));
    }

    public javax.vecmath.Vector3d getMouseMoveScale()
    {
        return new javax.vecmath.Vector3d(xScale,yScale,zScale);
    }
    public void setMouseMoveScale(javax.vecmath.Vector3d scale)
    {
        xScale = scale.x;
        yScale= scale.y;
        zScale= scale.z;
    }
    public javax.vecmath.Vector3d getVpTranslateScale()
    {
        return new javax.vecmath.Vector3d(((OrbitBehavior)getVpBehavior()).getTransXFactor(),
                    ((OrbitBehavior)getVpBehavior()).getTransYFactor(),
                    ((OrbitBehavior)getVpBehavior()).getZoomFactor());

    } 
    public void setVpTranslateScale(javax.vecmath.Vector3d vec)
    {
        ((OrbitBehavior)getVpBehavior()).setTransXFactor(vec.x);
        ((OrbitBehavior)getVpBehavior()).setTransYFactor(vec.y);
        ((OrbitBehavior)getVpBehavior()).setZoomFactor(vec.z);
    }

    /**
    * Return the x-axis movement multipler.
    **/
    public double getXScale() {
        return xScale;
    }

    /**
    * Return the y-axis movement multipler.
    **/
    public double getYScale() {
        return yScale;
    }


    /**
    * Set the x-axis amd y-axis movement multipler with factor.
    **/
    public void setScale( double factor) {
        xScale = yScale = factor;
        if(invert) {
            xScale *= -1;
            yScale *= -1;
        }

    }

    /**
    * Set the x-axis amd y-axis movement multipler with xFactor and yFactor
    * respectively.
    **/
    public void setScale( double xFactor, double yFactor) {
        xScale = xFactor;
        yScale = yFactor; 
        if(invert) {
            xScale *= -1;
            yScale *= -1;
        }   
    }

    /**
    * Return the y-axis movement multipler.
    **/
    public double getZScale() {
        return zScale;
    }

    /**
    * Set the y-axis movement multipler with factor.
    **/
    public void setZScale( double factor) {
        zScale = factor;
        if(invert) { 
            zScale *= -1;
        }
    }
    /** 
    * Manually wake up the behavior. If MANUAL_WAKEUP flag was set upon 
    * creation, you must wake up this behavior each time it is handled.
    */

    public void wakeup() {
        wakeUp = true;
    }

    /**
    * All mouse manipulators must implement this.
    */

    @SuppressWarnings("unchecked")
	public void processStimulus (Enumeration criteria) {
        //System.out.println("TBehaviorManager: ps");
        WakeupCriterion wakeup;
        AWTEvent[] events;
        MouseEvent evt;

        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();

            if (wakeup instanceof WakeupOnAWTEvent) {
                //System.out.println("TBehaviorManager: WakeupOnAWTEvent");
                events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                if (events.length > 0) {
                    evt = (MouseEvent) events[events.length-1];
                    processEvent(evt);
                }
            }
        }
        wakeupOn(mouseCriterion);
    }

    void processEvent(MouseEvent evt) {
        int dx, dy;
        int mouseID = evt.getID();
        switch(mouseID) {
        case MouseEvent.MOUSE_PRESSED:
            //System.out.println("TBehaviorManager: PRESSED " + evt.getButton());
            mButton = evt.getButton();
            lastX = evt.getX();
            lastY = evt.getY();
            //if((mButton == MouseEvent.BUTTON1)&& (!evt.isShiftDown())) {
            //    if(pickEnabled && (pickListener != null))
            //pickListener.mousePressed(evt);
        mousePressed(evt);    
            if ((!cursorOnDrag)
                && (!selectManager.isSelectionEmpty())
                && (mButton == MouseEvent.BUTTON1)
                && (!evt.isShiftDown())) {
                hideCursor(true);
            }
            break;

        case MouseEvent.MOUSE_RELEASED:
           
            //pickListener.mouseReleased(evt); 
        	//mouseReleased(evt);
            if (cursorHidden)
            {
                hideCursor(false);
            }
             mButton =  0;  
            wakeUp = false;
            break;

        case MouseEvent.MOUSE_MOVED:
            break;

        case MouseEvent.MOUSE_DRAGGED:
            //System.out.println("TBehaviorManager: DRAG ");
            if (!selectManager.isSelectionEmpty()) {
                if (mButton == MouseEvent.BUTTON1) { 
                    if((!evt.isShiftDown())) {
                        // TRANSLATE
                        if(translateEnabled && (transHandler != null)) {
                            //System.out.println("Translate");
                            x = evt.getX();
                            y = evt.getY();

                            dx = x - lastX;
                            dy = y - lastY;

                            if ((!reset) && ((Math.abs(dy) < 50) && (Math.abs(dx) < 50))) {
                                //System.out.println("dx " + dx + " dy " + dy);
                                mTransGroup.getTransform(mTransform);

                                translation.x = dx*xScale; 
                                translation.y = -dy*yScale;
                                transX.setIdentity();
                                transX.set(translation);
                                if (invert) {
                                    mTransform.mul(mTransform, transX);
                                }
                                else {
                                    mTransform.mul(transX, mTransform);
                                }
                                mTransGroup.setTransform(mTransform);
                                Transform3D tmpTrans = new Transform3D();
                                tmpTrans.sub(mTransform,refTransform);
                                refTransform.set(mTransform);
                                
                                //System.out.println("Trans callback: " + transHandler);
                                if (transHandler != null) {
                                    
                                    transHandler.transformChanged( TViewer.TRANSLATE,
                                        tmpTrans );
                                }       
                            }
                            else {
                                reset = false;
                            }
                            lastX = x;
                            lastY = y;
                        }
                    }
                    else {// Shift is down
                        // ROTATE
                        if(rotateEnabled && (rotHandler != null)) {
                            x = evt.getX();
                            y = evt.getY();

                            dx = x - lastX;
                            dy = y - lastY;

                            if (!reset) {	    
                                xAngle = dy * yScale;
                                yAngle = dx * xScale;
                                transX.setIdentity();
                                transY.setIdentity();
                                transZ.setIdentity();
                                Object obj = (((java.util.List)selectManager.getSelected()).get(0));
                                if(obj instanceof TRendered) {
                                	TRendered robj = (TRendered)obj;
									if (robj.getRotationAngleSnap() > 0 ) {
										int xSnap = (int) (xAngle / robj.getRotationAngleSnap());
										xSnap *= robj.getRotationAngleSnap();
										xAngle = xSnap;
										int ySnap = (int) (yAngle / robj.getRotationAngleSnap());
										ySnap *= robj.getRotationAngleSnap();
										yAngle = ySnap;
									}

									switch (robj.getScreenXRotationAxis()) {
									case TRendered.ROTATION_AXIS_NONE:
								    		break;
								    	case TRendered.ROTATION_AXIS_X:
								    		transX.rotX(yAngle);
								    		break;
								    	case TRendered.ROTATION_AXIS_Y:
								    		transX.rotY(yAngle);
								    	break;
								    	case TRendered.ROTATION_AXIS_Z:
								    		transX.rotZ(yAngle);
								    	break;
								    	default:
								    		break;
								 
								    }
								    
									switch (robj.getScreenYRotationAxis()) {
									case TRendered.ROTATION_AXIS_NONE:
								    		break;
								    	case TRendered.ROTATION_AXIS_X:
								    		transY.rotX(xAngle);
								    		break;
								    	case TRendered.ROTATION_AXIS_Y:
								    		transY.rotY(xAngle);
								    	break;
								    	case TRendered.ROTATION_AXIS_Z:
								    		transY.rotZ(xAngle);
								    	break;
								    	default:
								    		break;
								 
								    	}
                                }
                                
                                mTransGroup.getTransform(mTransform);

                                Matrix4d mat = new Matrix4d();
                                mTransform.get(mat);

                                if (invert) {
                                    mTransform.mul(mTransform, transX);
                                    mTransform.mul(mTransform, transY);
                                }
                                else {
                                    mTransform.mul(transX, mTransform);
                                    mTransform.mul(transY, mTransform);
                                }

                                mTransGroup.setTransform(mTransform);

                                Transform3D tmpTrans = new Transform3D();
                                tmpTrans.sub(mTransform,refTransform);
                                refTransform.set(mTransform);

                                if (rotHandler!=null)
                                    rotHandler.transformChanged( TViewer.ROTATE,
                                        mTransform );
                            }
                            else {
                                reset = false;
                            }
                            lastX = x;
                            lastY = y;
                        }
                    }
                }
                else if (mButton == MouseEvent.BUTTON2) {
                    if(zoomEnabled && (zoomHandler != null)) {
                        x = evt.getX();
                        y = evt.getY();
                        dx = x - lastX;
                        dy = y - lastY;

                        if (!reset) {
                            mTransGroup.getTransform(mTransform);

                            translation.z  = dy*zScale;
                            transX.setIdentity();
                            transX.set(translation);

                            if (invert) {
                                mTransform.mul(mTransform, transX);
                            }
                            else {
                                mTransform.mul(transX, mTransform);
                            }
                            mTransGroup.setTransform(mTransform);
                            if (zoomHandler!=null)
                                zoomHandler.transformChanged( TViewer.ZOOM,
                                    mTransform );
                        }
                        else{
                            reset = false;
                        }
                    }

                }
            }
            else {
                 ((MouseMotionListener)getVpBehavior()).mouseDragged(evt); 
            }
            break;
            default:
                break;
            }
        }

        public TransformChangeListener getRotationListener() {
            return rotHandler;
        }
        /**
        * The transformChanged method in the callback class will
        * be called every time the transform is updated
        */
        public void setRotationListener( TransformChangeListener callback ) {
            rotHandler = callback;
        }
        public TransformChangeListener getTranslationListener() {
            return transHandler;
        }
        /**
        * The transformChanged method in the callback class will
        * be called every time the transform is updated
        */
        public void setTranslationListener( TransformChangeListener callback ) {
            transHandler = callback;
        }
        public TransformChangeListener getZoomListener() {
            return zoomHandler;
        }
        /**
        * The transformChanged method in the callback class will
        * be called every time the transform is updated
        */
        public void setZoomListener( TransformChangeListener callback ) {
            zoomHandler = callback;
        }


        public void mousePressed(MouseEvent me) {
            invalidateImage = false;
            inMove = false;
            inRot = false;
            wasRunning = false;
            pickObj = null;
            rotObj = null;
            forward = true;
            int button = me.getButton();
            
            boolean clearSelected = false;

            if (button == MouseEvent.BUTTON1) {
                pickCanvas.setShapeLocation(me);
                PickResult results [] = pickCanvas.pickAllSorted();

                if (results != null) {
                    for( int i = 0; i < results.length;i++) {
                        Object brg = results[i].getNode(PickResult.BRANCH_GROUP);
                        if (brg!=null) {
                            TAbstractRendered obj = null;
                            if(brg instanceof TAbstractRendered) {
                            	obj = (TAbstractRendered) brg;
                            } else if(brg instanceof TNode3D) {
                            	obj = (TAbstractRendered) ((TNode3D)brg).getElement();
                            }
                            if(obj != null) {                       
                                if(obj.isSelectable() && !obj.isSelected()) {
                                    Transform3D trans = new Transform3D();
                                    javax.vecmath.Vector3d pos = obj.getPosition();
                                    trans.setTranslation(pos);
                                    if(obj instanceof TRendered) {
	                                    Quat4d rot = ((TRendered)obj).getRotation();
	                                    trans.setRotation(rot);
                                    }
                                    setTransform(trans);
                                    if(mTransGroup instanceof TransformGizmo) {
                                        ((TransformGizmo)mTransGroup).setVisible(true);       
                                    }
                                    selectManager.addSelected(obj,!me.isControlDown());
                                    break;
                                }
                                else if(obj.isSelectable() && obj.isSelected() && me.isControlDown() /*&& selectManager.getNumberSelected() > 1*/) {
                                    selectManager.removeSelected(obj);
                                    TAbstractRendered ren = null;
                                    int siz = selectManager.getNumberSelected();
                                    if(siz > 1) {
                                        ren = (TAbstractRendered)((java.util.List)selectManager.getSelected()).get(siz -1);
                                        if (ren != null) { 
                                            Transform3D trans = new Transform3D();
                                            javax.vecmath.Vector3d pos = ren.getPosition();
                                            trans.setTranslation(pos);
                                            if(obj instanceof TRendered) {
                                            	Quat4d rot = ((TRendered)obj).getRotation();
                                            	trans.setRotation(rot);
                                            }
                                            setTransform(trans);
                                        }
                                    }     
                                    if (mTransGroup instanceof TransformGizmo) {
                                        ((TransformGizmo)mTransGroup).setVisible(ren != null);      
                                    }
                                    break;
                                }   
                            }
                        }
                    }
                }
                else {
					selectManager.noPickResult();
					clearSelected = selectManager.isSelectionEmpty();
					if (mTransGroup instanceof TransformGizmo) {
					    ((TransformGizmo)mTransGroup).setVisible(false);      
					}
                }
                if (selectManager.disableVpBehaviorWhileSelecting()) getVpBehavior().setEnable(clearSelected);
            }
            else if(me.getButton()==MouseEvent.BUTTON3) {
                if (mTransGroup instanceof TransformGizmo) {
                    ((TransformGizmo)mTransGroup).setVisible(false);      
                }
                selectManager.noPickResult();
                if (selectManager.disableVpBehaviorWhileSelecting()){ 
                	ViewPlatformBehavior vpb = getVpBehavior();
                	if(vpb != null)
                	vpb.setEnable(selectManager.isSelectionEmpty());
                }
            }
            else if (me.getButton()==MouseEvent.BUTTON2) {

                if (mTransGroup instanceof TransformGizmo) {
                    ((TransformGizmo)mTransGroup).setVisible(false);      
                }
                selectManager.noPickResult();
                if (selectManager.disableVpBehaviorWhileSelecting()){
                	ViewPlatformBehavior vpb2 = getVpBehavior();
                	if(vpb2 != null)
                	vpb2.setEnable(selectManager.isSelectionEmpty());
                }
            }
            ((MouseListener)getVpBehavior()).mousePressed(me);
        }

        public void transformChanged(int mode,Transform3D trans) {
            if (mode == TViewer.NONE) {
                return;
            }
            else {

                Collection<TAbstractRendered> objs = selectManager.getSelected();
                if((objs != null) &&( !objs.isEmpty())) {

                    Quat4d quat = new Quat4d();
                    javax.vecmath.Vector3d vec = new javax.vecmath.Vector3d();
                    trans.get(quat,vec);
                    Iterator it = objs.iterator();
                    TAbstractRendered obj = null;
                    
                    switch (mode) {
                      case TViewer.ROTATE:
                            while (it.hasNext()) {
                                obj = (TAbstractRendered) it.next();
                                if (obj instanceof TRendered && ((TRendered)obj).isRotable()) {
                                    ((HasRotation)obj).setRotation(quat);
                                }
                            }
                            if(refreshOnDrag && mViewer != null) mViewer.checkRefresh();
                            break;
                        case TViewer.TRANSLATE:				
                            while (it.hasNext()) {
                                obj = (TAbstractRendered) it.next();
                                javax.vecmath.Vector3d pos = new javax.vecmath.Vector3d(obj.getPosition());
                                pos.add(vec);
                                if (obj.isMoveable()) obj.setPosition(pos);    
                            }
                            if(refreshOnDrag && mViewer != null) mViewer.checkRefresh();
                            break;
                        case TViewer.ZOOM:
                            break;
                        case TViewer.NONE:
                        default:
                            TDebug.println(1,"transformChanged: default");
                            break;
                    }
                   
                }
            }
        }

		public void moveObject(MouseEvent me) {
			if((null != pickObj) && (pickObj instanceof TAbstractRendered) && (pickObj instanceof HasNode3D)) {
			    TNode3D node = ((HasNode3D)pickObj).getNode3D();
			    javax.vecmath.Vector3d v = node.getPosition();
			    pickObj.setPosition(v);
			    mViewer.checkRefresh();
			}
		}
		
		public void rotateObject(MouseEvent me) {
			if((null != rotObj) && (rotObj instanceof TAbstractRendered) && (rotObj instanceof HasNode3D)) {
			    Node3D node = (Node3D) ((HasNode3D)rotObj).getNode3D();
			    Quat4d v = node.getRotation();
			    ((HasRotation)rotObj).setRotation(v);
			    mViewer.checkRefresh();
			}
		}	
		
        public void mouseReleased(MouseEvent me) {
			if (forward) {
				((MouseListener)getVpBehavior()).mouseReleased(me);
			}
			else {
			  	if(inMove) {
			  		moveObject(me);
			  		pickObj = null;
			  		inMove = false;
			  	}
			  	else if(inRot) {
			  		rotateObject(me);
			  		rotObj = null;
			  		inRot = false;
			  	}
			  	mViewer.setPicking(false);
			  	mViewer.setNavigationMode(mViewer.getNavigationMode());
			  	forward = true;
			}	
        }
    }
