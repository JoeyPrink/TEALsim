/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FieldLineNode.java,v 1.12 2011/05/27 15:39:34 pbailey Exp $ 
 * 
 */

package teal.render.j3d;


import java.awt.*;
import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.config.*;
import teal.render.j3d.geometry.Sphere;
import teal.render.scene.TFieldLineNode;
import teal.render.scene.TFog;
import teal.render.HasColor;
import teal.render.HasTransform;
import teal.render.TAbstractRendered;
import teal.sim.spatial.FieldLine;
import teal.util.*;

/**
 * provides SceneGraphNode for the management of a FieldLine, includes 
 * support for displaying pick shape, symmetry, and line direction markers. 
 *
 * @author Phil Bailey
 * @version $Revision: 1.12 $
 *
 **/

public class FieldLineNode extends Node3D implements TFieldLineNode
{
	Shape3D mShape = null;
    SharedGroup mShare = null;
    Group mLines = null;
	Shape3D mPick = null;
    LineStripArray lineGeo;
    int symCount = 1;
    Vector3d symAxis = null;
    int numMarkers = 0;
    boolean showMarkers = false;
    Vector markers = null;
    boolean pickVisible = true;
    double pickRadius;
    int colorMode;
    
    public FieldLineNode(){
        this(1, new Vector3d(0,1,0));
    }

	public FieldLineNode(int num, Vector3d axis){
		super();
        symCount = num;
        symAxis = axis;
        mShare = new SharedGroup();
        mShare.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        mShare.setCapability(Group.ALLOW_CHILDREN_READ);
        mShare.setCapability(Group.ALLOW_CHILDREN_WRITE);
       
		mShape = new Shape3D();        
        mShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		mShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		mShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		mShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
		mShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		mShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE); 
        mShare.addChild(mShape);       
       
        mLines = new Group();
        mLines.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        mLines.setCapability(Group.ALLOW_CHILDREN_READ);
        mLines.setCapability(Group.ALLOW_CHILDREN_WRITE);
        mSwitch.addChild(mLines);
        double r = 0.0;
        if (symCount > 1 )
        {  
            r = (2.0 * Math.PI)/(double) num;
        }
        // Note: Switch[0] is mContents
        for(int i = 0; i < num ;i++)
        {    
            Transform3D trans = new Transform3D();
            AxisAngle4d aa = new AxisAngle4d(symAxis, i * r);
            trans.set(aa);
            CloneNode clone = new CloneNode();
            clone.mContents.addChild(new Link(mShare));
            clone.mContents.setTransform(trans);
            
            mLines.addChild(clone);
				
        }

		Shape3D pic = new Shape3D();
        pic.setAppearance(FieldLineNode.makeAppearance(new Color3f(Color.gray)));
		pic.setGeometry(Sphere.makeGeometry(Teal.FieldLinePickRadius).getIndexedGeometryArray(true));
        setPickShape(pic);
        mContents.addChild(mPick);
        setVisible(true);
		
	}
	
	public FieldLineNode(TAbstractRendered element){
		this();
		setFieldLine((FieldLine) element);

	}

    public FieldLineNode(TAbstractRendered element, int clones, Vector3d axis){
		this(clones,axis);
		setFieldLine((FieldLine) element);

	}
    
    public synchronized void setSymmetry(int count, Vector3d axis)
    {
        TDebug.println(2,"settingSymCount: " + count);
        symAxis.set(axis);
        int num = count;
        if(num < 0 )
            num = 0;
        int numChildren = mLines.numChildren();   
        if (num != numChildren)
        {
            if (num > numChildren)
            {
                for(int i = (numChildren); i < num; i++)
                {
                    CloneNode tr1 = new CloneNode();
                    tr1.mContents.addChild(new Link(mShare));
                    mLines.addChild(tr1);
                }
            }
            else
            {
               for(int i = numChildren; i > num; i--)
               {
                    mLines.removeChild(i -1 );
               }
               
            }
        }
        TDebug.println(2,"new number of lines = " + mLines.numChildren());
        double r = 0.0;
        if (num > 1)
        {
            
           r = (2.0* Math.PI)/(double)( num );
        }
        for(int i = 0;  i < num;i++)
        {    
            Transform3D trans = new Transform3D();
            AxisAngle4d aa = new AxisAngle4d(symAxis,i * r);
            trans.set(aa);
            Node cNode  =  mLines.getChild(i);
            if (cNode instanceof HasTransform)
            {
                    ((HasTransform)cNode).setTransform(trans);
            }
        }
        
        symCount = num;
    }
	
	public void setFieldLine(FieldLine element)
	{
		setElement(element);
        pickVisible = element.getShowPick();
        pickRadius = element.getPickRadius();
        colorMode = element.getColorMode();
        if (element != null)
        {
		    mPick.setAppearance(FieldLineNode.makeAppearance(element.getColor()));
		    if (pickVisible)
		    {
			    mPick.insertGeometry(Sphere.makeGeometry(pickRadius).getIndexedGeometryArray(true),0);			
		    }
		    else
		    {
			    mPick.removeAllGeometries();
		    }
            lineGeo = makeDefaultGeometry(element,colorMode);
            mShape.insertGeometry(lineGeo,0);
            int num = element.getMarkerCount();
            
            if(num  > 0) {
                numMarkers = num;
                checkMarkers(num);
                
            }
            setColor(element.getColor()); 
            setSymmetry(element.getSymmetryCount(),element.getSymmetryAxis());
        }
	}
	public int getMarkerCount(){
		return numMarkers;
	}
	
	public void setMarkerCount(int count){
		numMarkers = count;
	}
		
	public boolean getShowMarkers(){
		return showMarkers;
	}

	public void setShowMarkers(boolean b){
		showMarkers = b;
	}
	public void checkMarkers(int num) {
        if(markers == null)
            markers = new Vector();
        if(markers.size() < num) {
            Appearance app = mShape.getAppearance();
            for(int i = markers.size();i < num; i ++) {
                ShapeNode mark = new ShapeNode();
                mark.setGeometry(sBase,0);
                mark.setGeometry(sCone,1);
                mark.setVisible(true);
                mark.setAppearance(app);
                mShare.addChild(mark);
                markers.add(mark);
            }
        }
    }
                
    public void setPickRadius(double r) {
		pickRadius = r;
		if (pickVisible) {
			mPick.removeAllGeometries();
			mPick.insertGeometry(Sphere.makeGeometry(pickRadius).getIndexedGeometryArray(true), 0);
		}
	}
    
    public double getPickRadius()
    {
        return pickRadius;
    }
    public void setPickVisible(boolean state)
    {
        pickVisible = state;
    }
    
    public boolean isPickVisible()
    {
        return pickVisible;
    }
    
	public Color3f getColor()	{
		Color3f c = null;
		if (mShape != null){
			Appearance app = mShape.getAppearance();
			if (app != null){
			
				Material mat = app.getMaterial();
				if (mat != null){
					Color3f cf = new Color3f();
					mat.getDiffuseColor(cf);
					c = cf;
				}
			}
		}
		return c;
	}
	
	public void setColor(Color3f c){
		setAppearance(FieldLineNode.makeAppearance(c));
	}
	

	public void setColor(Color q) {
		setColor(new Color3f(q));
	}
    
	public void setAppearance(Appearance app){

		mShape.setAppearance(app);
        setPickAppearance(app);
        if(markers != null) {
            Iterator it = markers.iterator();
            while (it.hasNext()) {
                ShapeNode node = (ShapeNode) it.next();
                node.setAppearance(app);
            }
        }
		
	}
    
    public void setMarkerValues(int idx, Vector3d pos, Vector3d direction) {
        if(markers != null) {
            Node3D node = (Node3D) markers.get(idx);
            if(node != null) {
                node.setPosition(pos);
                node.setDirection(direction);
                if(!node.isVisible())
                	node.setVisible(true);
            }
        }
    }
	public void setMarkerVisible(int idx, boolean state) {
        if(markers != null) {
            Node3D node = (Node3D) markers.get(idx);
            if(node != null)
                node.setVisible(state);
        }
    }
    
	public Appearance getAppearance(){
		if(mShape == null)
			return null;
		return mShape.getAppearance();
	}

    protected LineStripArray makeDefaultGeometry(FieldLine element, int mode)
    {
        LineStripArray line = null;
        int kmax = element.getKMax();
        int strips [] = { kmax,kmax };
        TDebug.println(1,"Max vertex = "  +(2 * kmax));
        
        int format;
        if (mode == 1) {
        	format = GeometryArray.COORDINATES;
        } else {
        	format = GeometryArray.COORDINATES | GeometryArray.COLOR_3;
        }
        
        
        line = new LineStripArray(2 * kmax,format,strips);
        
        line.setCapability(GeometryStripArray.ALLOW_COUNT_READ);
		line.setCapability(GeometryStripArray.ALLOW_COUNT_WRITE); 
        line.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
        line.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
        line.setCapability(GeometryArray.ALLOW_COLOR_READ);
        line.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
         return line;
    } 
            
	public void setPickShape(Shape3D shape){
		mPick = shape;
        mPick.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		mPick.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		mPick.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		mPick.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        
		mPick.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		mPick.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE); 
        mPick.setCapability(Node.ALLOW_PICKABLE_READ);
        mPick.setCapability(Node.ALLOW_PICKABLE_WRITE);
        mPick.setCapability(Node.ENABLE_PICK_REPORTING);
		
	}
	
	public Shape3D getPickShape(){
		return mPick;
	}
 
	public void setPickAppearance(Appearance app){
		if (mPick == null){
			setPickShape(new Shape3D());

		}		
		mPick.setAppearance(app);		
	}
	
	public Appearance getPickAppearance(){
		if(mPick == null)
			return null;
		return mPick.getAppearance();
	}
	
	public Color getPickColor()	{
		Color c = null;
		if (mPick != null){
			Appearance app = mPick.getAppearance();
			if (app != null){
			
				Material mat = app.getMaterial();
				if (mat != null){
					Color3f cf = new Color3f();
					mat.getAmbientColor(cf);
					c = cf.get();
				}
			}
		}
		return c;
	}
	
	public void setPickColor(Color3f c){
		if (mPick == null){
			setPickShape(new Shape3D());
		}
		Appearance app = Node3D.makeAppearance(c);
		mPick.setAppearance(app);
	}
	
	public void setShininess(float value){
		Appearance app = mShape.getAppearance();
		if (app == null){		
			app = new Appearance();
			app.setCapability(Appearance.ALLOW_MATERIAL_READ);
			app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
			app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
			app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		}
		
		Material mat = app.getMaterial();
		if (mat == null){
			mat = new Material();
			mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
			mat.setCapability(Material.ALLOW_COMPONENT_READ);
		}
		Float f = new Float(value * 128);
		mat.setShininess(f.intValue());
		app.setMaterial(mat);
		mShape.setAppearance(app);
	}


    public void setVisible(boolean b)
    {
        super.setVisible(b);
    }
 
	public synchronized void setLineGeometry(int len1, float[] line1, int len2, float[] line2  )
	{       	    	    
	    if((len1 > 1) && (len2 >1))
	    {
 
		int [] strips = {len1,len2};
	       	lineGeo.setStripVertexCounts(strips);
	        lineGeo.setCoordinates(0,line1,0,len1);
	        lineGeo.setCoordinates(len1,line2,0,len2);
	    }
	    else if (len1 > 1)
	    {
	        int [] s1 = {len1};
	      
	        lineGeo.setStripVertexCounts(s1);
	        lineGeo.setCoordinates(0,line1,0,len1);           	        
	    }    
		else if (len2 >1)
	    {
	        int [] s2 = {len2};
	        
	        lineGeo.setStripVertexCounts(s2);
	        lineGeo.setCoordinates(0,line2,0,len2);           	        
	    }
	    else
	    {
	        setVisible(false);
	    }
	}
	
	public synchronized void setLineGeometry(int len1, float[] line1, float[] colors1,int len2, float[] line2,float[] colors2  )
	{
	    if((len1 > 1) && (len2 >1))
	    {
	        int [] strips = {len1,len2};
	       
			lineGeo.setStripVertexCounts(strips);
	        lineGeo.setCoordinates(0,line1,0,len1);
	        lineGeo.setCoordinates(len1,line2,0,len2);
            
	        lineGeo.setColors(0,colors1,0,len1);
	        lineGeo.setColors(len1,colors2,0,len2);
	    }
	    else if (len1 > 1)
	    {
	        int [] s1 = {len1};
	        
	        lineGeo.setStripVertexCounts(s1);
	        lineGeo.setCoordinates(0,line1,0,len1);     
	        lineGeo.setColors(0,colors1,0,len1);
	    }    
		else if (len2 >1)
	    {
	        int [] s2 = {len2};
	        
	        lineGeo.setStripVertexCounts(s2);
	        lineGeo.setCoordinates(0,line2,0,len2);              
	        lineGeo.setColors(0,colors2,0,len2);
	    }
	    else
	    {
	        setVisible(false);
	    }
	}
 
   	public static Appearance makeAppearance(Color3f c) 
    {
        float shininess = 0.8f;
		Appearance app = new Appearance();
	
		// Capabilities
		app.setCapability(Appearance.ALLOW_MATERIAL_READ);
		app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
			
		// Color
        ColoringAttributes coloringAttributes = new ColoringAttributes(new Color3f(c),
				ColoringAttributes.FASTEST);
		coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
        coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
		app.setColoringAttributes(coloringAttributes);
		
		// Material
		Material mat = app.getMaterial();
		if (mat == null) {
			mat = new Material();
			mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
			mat.setCapability(Material.ALLOW_COMPONENT_READ);
		}
		Color3f c3 = new Color3f(c);
		mat.setDiffuseColor(c3);
		mat.setSpecularColor(c3);
        mat.setEmissiveColor(new Color3f(c.get().brighter()));
		c3.scale(0.9f);
		mat.setAmbientColor(c3);
		Float f = new Float(shininess * 128);
		mat.setShininess(f.intValue());
		app.setMaterial(mat);
		return app;
	}

   public static void setEmissive(Shape3D shape, Color base, float amount)
    {
    	Appearance app = shape.getAppearance();
		if (app == null){		
			app = makeAppearance();
		}
		
		Material mat = app.getMaterial();
		if (mat == null){
			mat = new Material();
			mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
			mat.setCapability(Material.ALLOW_COMPONENT_READ);
		}
		Color3f col = new Color3f(base);
        col.scale(amount);
		mat.setEmissiveColor(col);
		app.setMaterial(mat);
		shape.setAppearance(app);
    }

   
    public class CloneNode extends BranchGroup implements HasTransform
    {
            TransformGroup mContents;
            
            CloneNode()
            {
                super();
                setCapability(BranchGroup.ALLOW_DETACH);
                mContents = new TransformGroup();
                mContents.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
                mContents.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
                addChild(mContents);
            }
            
            public Transform3D getTransform()
            {
                Transform3D trans = new Transform3D();
                mContents.getTransform(trans);
                return trans;
                
            }
            public void setTransform(Transform3D trans)
            {
                mContents.setTransform(trans);
            }
    }
    
}
