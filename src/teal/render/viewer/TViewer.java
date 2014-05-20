/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TViewer.java,v 1.20 2010/07/16 21:41:37 stefan Exp $ 
 * 
 */

package teal.render.viewer;

import java.awt.*;
import java.awt.geom.*;
import java.util.Comparator;

import javax.vecmath.*;

import teal.core.*;
import teal.render.Bounds;
import teal.render.HasBoundingArea;
import teal.render.TRenderEngine;
import teal.sim.simulation.TSimulation;

public interface TViewer extends HasBoundingArea, HasID, TRenderEngine {

    /** Masks  and flags for behaviors */
    public final static int NONE=0x0;
    
    /** Object behavior modifiers */
    public final static int TRANSLATE_X=0x1;
    public final static int TRANSLATE_Y=0x2;
    public final static int TRANSLATE_Z=0x4;
    public final static int TRANSLATE=0x8;
    
    public final static int ROTATE_X=0x10;
    public final static int ROTATE_Y=0x20;
    public final static int ROTATE_Z=040;
    public final static int ROTATE=0x80;
    public final static int ZOOM=0x100;
    
    /** ViewPlatformbehavior Navigation modifiers */
    public static final int VP_TRANSLATE=0x1;
    public static final int VP_ROTATE=0x2;   
    public static final int VP_ZOOM =0x4;
    
    /** ViewPlatformbehavior Navigation modes */
    public static final int ORBIT=0x10;
    public static final int ORBIT_ALL = ORBIT|VP_TRANSLATE|VP_ROTATE|VP_ZOOM;

    public static final int EXAMINE=0x20;
    public static final int FLY=0x40;
    public static final int HOVER=0x80;
    public static final int ATTACH=0x100;
    
    /** Action command to trigger a camera reset to a default location */
    public final static String RESET_CAMERA = "Reset Camera";
	/** Action command to trigger dump to TDebug.out of the viewer status */
	public final static String VIEW_STATUS = "View Status";
	
    
    
    /* JComponent methods used most often, added to the interface to 
     *  ease the requirement of casting the Viewer */
    public void setBounds(int x, int y, int w, int h);
    public void setBounds(Rectangle rec);
    public void setPreferredSize(Dimension size);
    public void setMaximumSize(Dimension size);
    public void setMinimumSize(Dimension size);       
	public void setVisible(boolean state);
    
    public Rectangle getBounds();
    public Dimension getPreferredSize();
    public Dimension getMaximumSize();
    public Dimension getMinimumSize();       
	public boolean isVisible();
      
    public void setBoundingArea(Bounds b);
    @SuppressWarnings("unchecked")
    public void setRenderOrder(Comparator cmp);
    
    public void clear();
   	public void dispose();
    /** Provides the ability to trigger a refresh as needed. */
    public void checkRefresh();
    /** Provides the ability to start an evolving view of the rendered objects. */
    //public void checkStart();
       /** Provides the ability to stop an evolving view of the rendered objects. */
    //public void checkStop();
  
    public Color getBackgroundColor();
    public void setBackgroundColor(Color c);
    public Graphics2D getGraphics2D();

    public void setViewerSize( int width, int height);
    public Dimension getViewerSize();
    public void displayBounds();
    
    //public VectorIterator getGridIterator(int resolution);

   // public void addDrawable(TAbstractRendered draw);
   // public void removeDrawable(TAbstractRendered draw);
    
    public void remove();
    public void addDontDraw(Object obj);
    public void removeDontDraw(Object obj);

    //public Vector3d getDirection();

    public void setPickMode(int flags);
    public int getPickMode();
    public void setPickTolerance(float tolerance);
    public void setPicking(boolean enable);
    public void setSimulation(TSimulation sim);
    public AffineTransform getAffineTransform();
    /**
     * @deprecated
     */
    public AffineTransform getInvertedAffineTransform();

    public SelectManager getSelectManager();
    public void setSelectManager(SelectManager sm);
   
    public boolean getCursorOnDrag();
    public void setCursorOnDrag(boolean state); 
    public boolean getRefreshOnDrag();
    public void setRefreshOnDrag(boolean state);
    
    public TBehaviorManager getBehaviorManager();
    public void setNavigationMode(int flags);
    public int getNavigationMode();
  /*
    public boolean getTranslateEnable();
    public void setTranslateEnable(boolean state);
    public boolean getRotateEnable();
    public void setRotateEnable(boolean state);
    public boolean getZoomEnable();
    public void setZoomEnable(boolean state);
*/
    public Vector3d getMouseMoveScale();
    public void setMouseMoveScale(Vector3d scale);
    public Vector3d getVpTranslateScale();
    public void setVpTranslateScale(Vector3d scale);


    public void doStatus();
    public void doStatus(int level);

}
