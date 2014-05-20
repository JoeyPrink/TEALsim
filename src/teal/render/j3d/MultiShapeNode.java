/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: MultiShapeNode.java,v 1.15 2010/04/09 17:00:07 pbailey Exp $ 
 * 
 */

package teal.render.j3d;


import java.awt.Color;
import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.Color3f;

import teal.render.TAbstractRendered;
import teal.render.HasColor;

/**
 * No longer in use.  Includes support methods for nodes comprised of multiple shapes.
 */
public class MultiShapeNode extends Node3D // implements ShapeNode3D
{
	/**
	 *	Contains the multiple Shape3Ds of the MultiShapeNode
	 */
	protected ArrayList<Shape3D> mShapes = null;

	/**
	 *	Default constructor: creates multiple generic Shape3D.
	 */
	public MultiShapeNode(int quantity){
		super();
		mShapes = new ArrayList<Shape3D>();
		for(int i=0; i<quantity; i++) {
			Shape3D mShape=new Shape3D();
			initShape(mShape);
			mShapes.add(mShape);
			mContents.addChild(mShape);
		}
	}
	
	/**
	 *	Default constructor: creates a single generic Shape3D.
	 */
	public MultiShapeNode(){
		this(1);
	}

	/**
	 * Constructor: accepts a Rendered for this node.
	 */
	public MultiShapeNode(TAbstractRendered element){
		this();
		setElement(element,true);
	}

	/**
	 * Constructor: accepts a collection of Shape3Ds.
	 */
	public MultiShapeNode(Collection<Shape3D> shapes){
		super();
		mShapes = new ArrayList<Shape3D>();
		Iterator<Shape3D> it = shapes.iterator();
		while( it.hasNext() ) {
			Object obj = it.next();
			if( obj instanceof Shape3D ) {
				Shape3D mShape = (Shape3D) obj;
				initShape(mShape);
				mShapes.add(mShape);
				mContents.addChild(mShape);
			}	
		}
	}

	/**
	 *	Constructor: accepts a Rendered and a collection of Shape3Ds.
	 */
	public MultiShapeNode(TAbstractRendered element, Collection<Shape3D> shapes){
		this(shapes);
		setElement(element,true);
	}
	
	/**
	 *	Sets the appearances of the entire collection of Shape3Ds to a collection of Appearances.
	 */
	public void setAppearances(Collection<Appearance> appearances) {
		int k = 0;
		Iterator<Appearance> it = appearances.iterator();
		while( it.hasNext() && k < mShapes.size() ) {
			Object obj = it.next();
			if( obj instanceof Appearance ) {
				Appearance app = (Appearance) obj;
				setAppearance(k++, app);
			}	
		}
	}

	/**
	 *	Sets the appearances of the entire collection of Shape3Ds to a single Appearance.
	 */
	public void setAppearances(Appearance app) {
		for( int k = 0; k < mShapes.size(); k++ ) {
			setAppearance(k, app);
		}
	}


	/**
	 *	Sets the appearance of the Shape3D at the given index.
	 */
	public void setAppearance(int index, Appearance app) {
		Shape3D mShape = (Shape3D) mShapes.get(index);
		mShape.setAppearance(app);
	}

	/**
	 *	Gets the appearances of the entire collection of Shape3Ds.
	 */
	public ArrayList<Appearance> getAppearances() {
		ArrayList<Appearance> appearances = new ArrayList<Appearance>();
		int k = 0;
		while( k < mShapes.size() ) {
			Appearance app = getAppearance(k++);
			appearances.add(app);
		}
		return appearances;
	}

	/**
	 *	Gets the appearance of the Shape3D at the given index.
	 */
	public Appearance getAppearance(int index) {
		Shape3D mShape = (Shape3D) mShapes.get(index);
		return mShape.getAppearance();
	}



	/**
	 *	Sets the Shape3D at the given index.
	 * 
	 * 	QUESTION:
	 *	Should we copy appearances and geometries, as in the single shape version?
	 * 
	
			public void setShape3D(Shape3D shape){
				mShape.removeAllGeometries();
				if(shape != null){
					mShape.setAppearance(shape.getAppearance());
					for (Enumeration e = shape.getAllGeometries() ; e.hasMoreElements() ;) {
		              mShape.addGeometry((Geometry) e.nextElement());
					}
		        }
			}
	 * 
	 * 
	 */
	public void setShape3D(int index, Shape3D shape) {
		mShapes.set(index, shape);
	}

	/**
	 *	Gets the Shape3D at the given index.
	 */
	public Shape3D getShape3D(int index) {
		return (Shape3D) mShapes.get(index);
	}


	/**
	 *	[[[ ]]] setElement
	 */	
    public void setElement(TAbstractRendered element, boolean useColor){
        super.setElement(element);	
	    if(useColor && (element instanceof HasColor)) {
	        setAppearances(Node3D.makeAppearance( ((HasColor)element).getColor() ));
	    }
    }
 
	/**
	 *	[[[ initShape ]]]
	 */	
	public static void initShape(Shape3D shape){
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		
	}
	
	
	/**
	 *	[[[ ]]] Gets the Color of the Shape3D at the given index.
	 */
	public Color getColor(int index)	{
		Shape3D mShape = getShape3D(index);
		Color c = null;
		if (mShape != null){
			Appearance app = mShape.getAppearance();
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
	

	/**
	 *	Sets the Color of the Shape3D at the given index.
	 */
	public void setColor(int index, Color c){
		Appearance app = getAppearance(index);

		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

		app.setColoringAttributes(
			new ColoringAttributes(
				new Color3f(c),
				ColoringAttributes.SHADE_GOURAUD));
		setAppearance(index, app);
	}
	
	
	/**
	 *	[[[ ]]] Sets the shininess of the Shape3D at the given index.
	 */
	public void setShininess(int index, float value){
		Appearance app = getAppearance(index);
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
		setAppearance(index, app);
	}
	

	/**
	 *	[[[ ]]] Sets the Geometry of the Shape3D at the given index.
	 */
	public void setGeometry(int index, Geometry geo){
		Shape3D mShape = getShape3D(index);
		mShape.removeAllGeometries();
		mShape.addGeometry(geo);
	}
	
	/**
	 *	[[[ ]]] Sets the specific Geometry of the Shape3D at the given index.
	 */
	public void setGeometry(int index, Geometry geo, int geo_index){
		Shape3D mShape = getShape3D(index);
		if(mShape.numGeometries() > geo_index)
			mShape.setGeometry(geo,geo_index);
		else
			mShape.insertGeometry(geo,geo_index);	
	}
	
	/**
	 *	[[[ ]]] Adds a Geometry to the Shape3D at the given index.
	 */
	public void addGeometry(int index, Geometry geo){
		Shape3D mShape = getShape3D(index);
		mShape.addGeometry(geo);
	}

	/**
	 *	[[[ ]]] Gets the Geometry of the Shape3D at the given index.
	 */
	public Geometry getGeometry(int index){
		Shape3D mShape = getShape3D(index);
		if(mShape == null)
			return null;
		return mShape.getGeometry();
	}

	/**
	 *	[[[ ]]] Gets all the geometries of the Shape3D at the given index.
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getAllGeometries(int index){
		Shape3D mShape = getShape3D(index);
		if(mShape == null)
			return null;
		return mShape.getAllGeometries();
	}
	
	/**
	 *	[[[ ]]] Removes all the geometries of the Shape3D at the given index.
	 */
	public void removeGeometry(int index){
		Shape3D mShape = getShape3D(index);
		if ( mShape != null){
		
			mShape.removeAllGeometries();
		}
	}

}






