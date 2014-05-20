/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ShapeNode.java,v 1.26 2010/04/30 03:15:59 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.Color;
import java.util.Enumeration;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

import teal.render.HasColor;
import teal.render.TAbstractRendered;
import teal.render.scene.TShapeNode;
import teal.util.TDebug;

import com.sun.j3d.utils.geometry.GeometryInfo;

/**
 * provides SceneGraphNode for the management of a single Element
 * which contains at most one Shape3D and provides access to the shape's
 * methods. May want to support multiple shapes, but use multiple geometries
 * for now.
 *
 * @author Phil Bailey
 * @version $Revision: 1.26 $
 *
 **/

public class ShapeNode extends Node3D implements TShapeNode, ShapeNode3D
{
	
	protected Shape3D mShape = null;

	public ShapeNode(){
		super();
		mShape=new Shape3D();
		
		initShape(mShape);
		mContents.addChild(mShape);
		
	}
	
	public ShapeNode(TAbstractRendered element){
		this();
		setElement(element,true);
	}

	public ShapeNode(Shape3D shape){
		super();
		mShape = shape;
		initShape(mShape);
	}
	
	public ShapeNode(TAbstractRendered element, Shape3D shape){
		this(shape);
		setElement(element,true);
	}
	
	public ShapeNode(TAbstractRendered element,Shape3D shape,Appearance app){
		this(shape);		
		mShape.setAppearance(app);
        setElement(element,false);
	
	}
	
	public ShapeNode(TAbstractRendered element,GeometryInfo geo){
		this();
		mShape.addGeometry(geo.getIndexedGeometryArray());
        setElement(element,true);
        if(element.getMaterial() != null)
        	setAppearance(Node3D.makeAppearance( element.getMaterial()));
	}
	
    public void setElement(TAbstractRendered element,boolean useColor){
        super.setElement(element);	
        if(element.getMaterial() != null)
        	setAppearance(Node3D.makeAppearance( element.getMaterial()));
    }
 
	
	public static void initShape(Shape3D shape){
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		shape.setCapability(Shape3D.ALLOW_BOUNDS_READ);
		shape.setCapability(Shape3D.ALLOW_BOUNDS_WRITE);
		shape.setCapability(Shape3D.ALLOW_COLLISION_BOUNDS_READ);
		shape.setCapability(Shape3D.ALLOW_COLLISION_BOUNDS_WRITE);
		
	}
	
    
    public void removeAllGeometry()
    {
        if (mShape != null)
        {
            synchronized(mShape)
            {
                for(int i = mShape.numGeometries();i >0;)
                {
                    mShape.removeGeometry(--i );
                }
            }
        }
    }
    
    public void setSelected(boolean state)
    {
        selected = state;
        //mShape.setAppearanceOverrideEnable(state);
        if(selected)
            setEmissive(mShape,Color.WHITE,0.6f); 
        else 
            setEmissive(mShape,Color.WHITE,-0.6f);       
        TDebug.println(1,"ShapeSelect state= " + state);
    }
        
    
	@SuppressWarnings("unchecked")
	public void setShape3D(Shape3D shape){
        synchronized(mShape)
        {       
            removeAllGeometry();
		    if(shape != null){
			    mShape.setAppearance(shape.getAppearance());
			    for (Enumeration e = shape.getAllGeometries() ; e.hasMoreElements() ;) {
                    mShape.addGeometry((Geometry) e.nextElement());
			    }
            }
        }   
	}
	
	public Shape3D getShape3D(){
		return mShape;
	}
	
	public void setAppearance(Appearance app){

		mShape.setAppearance(app);
		
	}
	
	public Appearance getAppearance(){
		if(mShape == null)
			return null;
		return mShape.getAppearance();
	}
	
	public Color3f getColor()	{
		Color3f c = null;
		if (mShape != null){
			Appearance app = mShape.getAppearance();
			if (app != null){
			
				Material mat = app.getMaterial();
				if (mat != null){
					Color3f cf = new Color3f();
					mat.getAmbientColor(cf);
					c = cf;
				}
			}
		}
		return c;
	}
	
	public void setColor(Color c){
		setColor(new Color3f(c));
	}
	
	public void setColor(Color3f c){
		//TDebug.println(1,"Setting color to: " + c);
		Appearance app = mShape.getAppearance();
		if (app == null){
            app = makeAppearance(c);
		}
        Material mat = app.getMaterial();
        Color3f c3 = new Color3f(c);
    	mat.setDiffuseColor(c3);
    	//mat.setSpecularColor(c3);
    	c3.scale(0.9f);
    	mat.setAmbientColor(c3);
        app.setMaterial(mat);
        setAppearance(app);
	}
	
	public void setColor(Color4f c4f) {
		Color3f c = new Color3f(c4f.x,c4f.y,c4f.z);
		setColor(c);
	}
	
	public void setShininess(float value){

		Appearance app = mShape.getAppearance();
		if (app == null){		
			app = new Appearance();
			app.setCapability(Appearance.ALLOW_MATERIAL_READ);
			app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
			app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
			app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
			app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
			app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
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
		setAppearance(app);
	}
	
 
    public void setGeometry(GeometryInfo geo){
        setGeometry(geo.getIndexedGeometryArray());
    }
    
	public void setGeometry(Geometry geo){
         synchronized(mShape)
        {
		    removeAllGeometry();
		    mShape.addGeometry(geo);
        }
	}
	
    public void setGeometry(GeometryInfo geo, int idx){
        setGeometry(geo.getIndexedGeometryArray(), idx);
    }
    
	public void setGeometry(Geometry geo, int idx){

		if(mShape.numGeometries() > idx)
			mShape.setGeometry(geo,idx);
		else
			mShape.insertGeometry(geo,idx);	
	}
	
    
    public void addGeometry(GeometryInfo geo){

		addGeometry(geo.getIndexedGeometryArray());
	}
	public void addGeometry(Geometry geo){

		mShape.addGeometry(geo);
	}
	public GeometryInfo getGeometry(){
		if(mShape == null)
			return null;
		return new GeometryInfo((GeometryArray) mShape.getGeometry());
	}
    
	@SuppressWarnings("unchecked")
	public Enumeration getAllGeometries(){
		if(mShape == null)
			return null;
		return mShape.getAllGeometries();
	}
	
	public void setTransparency(float x) {
		Appearance app = mShape.getAppearance();
		if (app == null){		
			app = makeAppearance();
		} 
		// Even if you set the transparency to completely opaque, Java3D seems to have trouble rendering the object correctly.
		// It will appear to behind other transparent objects, etc.  So here we turn off transparency all together if
		// it's opaque.
		if (x < 0.02f) {
			app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NONE,x));
		} else {
			app.setTransparencyAttributes(new TransparencyAttributes(
				TransparencyAttributes.FASTEST, x));
		}
		setAppearance(app);
    }
}






