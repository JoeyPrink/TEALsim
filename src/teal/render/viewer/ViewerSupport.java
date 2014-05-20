package teal.render.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.core.AbstractElement;
import teal.render.Bounds;

public class ViewerSupport extends AbstractElement{
		protected Bounds boundingArea;
	   protected Dimension viewerSize; 
	    protected Dimension minimumSize;
	    protected Color backgroundColor;
//	    protected Transform3D defaultVpTransform;
	    protected Vector3d mouseMoveScale;
	    protected int navigationMode;
	    protected boolean cursorOnDrag = false;
	    protected boolean refreshOnDrag = false;
	    protected boolean showGizmos = false;
	    
	    public ViewerSupport()
	    {
	    	   backgroundColor = teal.config.Teal.Background3DColor;
	    	   mouseMoveScale = new Vector3d(1.,1.,1.);
	           viewerSize = new Dimension(450, 450);
	           minimumSize= new Dimension(50, 50);
	    }
	    
	    public Color getBackgroundColor(){
	    	return backgroundColor;
	    	
	    }
	    public void setBackgroundColor(Color color){
	    	PropertyChangeEvent pce = new PropertyChangeEvent(this,"backgroundColor",
	    	        backgroundColor, color);
	    	backgroundColor = color;
	    	 firePropertyChange(pce);
	    }
	    
	    /*
	    public Transform3D getDefaultViewpoint()
	    {
	        return defaultVpTransform;
	    }*/
	    
/*	    
	  public void setDefaultViewpoint(Transform3D trans)
	    {
		  PropertyChangeEvent pce = new PropertyChangeEvent(this,"defaultViewpoint",
	    	        boundingArea, trans);
		  defaultVpTransform = trans;
		  firePropertyChange(pce);
	    }
	  public void setLookAt(Point3d from, Point3d to, Vector3d angle)
	    {
		  Transform3D transform = new Transform3D();
		  transform.lookAt(from,to,angle);
	        transform.invert();
	        setDefaultViewpoint(transform);
			
	    }
	    */
	  
	  public Vector3d getMouseMoveScale(){
	    	return mouseMoveScale;
	    }
	    public void setMouseMoveScale(Vector3d vec){
	    	mouseMoveScale.set(vec);
	    }
	    public void setMouseMoveScale(double x, double y, double z){
	    	setMouseMoveScale(new Vector3d(x,y,z));
	    }
	    
	    public Dimension getViewerSize(){
	    	return viewerSize;
	    }
	    public void setViewerSize(Dimension dim){
	    	viewerSize = dim;
	    }
	    
	    public void setViewerSize(int width,int height){
	    	setViewerSize(new Dimension(width,height));
	    }
	    public Dimension getViewerMinimumSize(){
	    	return minimumSize;
	    }
	    public void setViewerMinimumSize(Dimension dim)
	    {
	    	minimumSize = dim;
	    }
	    
	    public int getNavigationMode(){
	    	return navigationMode;
	    }
	    public void setNavigationMode(int mode){
	    	navigationMode = mode;
	    }
	    public Boolean getShowGizmos(){
	    	return showGizmos;
	    }
	    public void setShowGizmos(Boolean state){
	    	showGizmos = state;
	    }
	    public Boolean getCursorOnDrag(){
	    	return cursorOnDrag;
	    }
	    public void setCursorOnDrag(Boolean state){
	    	cursorOnDrag = state;
	    }
	    public Boolean getRefreshOnDrag(){
	    	return refreshOnDrag;
	    }
	    public void setRefreshOnDrag(Boolean state){
	    	refreshOnDrag = state;
	    }


}
