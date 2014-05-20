/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: MagDipoleNode3D.java,v 1.11 2010/09/02 19:44:18 stefan Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.*;

import javax.media.j3d.*;
import javax.vecmath.*;
import teal.render.j3d.geometry.Cylinder;
import teal.render.scene.TMagDipoleNode3D;
import teal.render.TAbstractRendered;
import teal.physics.em.*;

/**
 * Node for rendering a MagneticDipole, which contains multiple shapes.  I think this can be simplified such
 * it need not implement TUpdatable...
 */
public class MagDipoleNode3D extends Node3D implements TMagDipoleNode3D
{
		
		TransformGroup redTg;
		TransformGroup blueTg;
		Shape3D redShape;
		Shape3D blueShape;
		Shape3D centerShape;

	public	MagDipoleNode3D() {
			super();
            initNode(1.0,0.2);

		}


	public	MagDipoleNode3D(TAbstractRendered elm) {
			super(elm);
            initNode(((MagneticDipole)elm).getLength(),((MagneticDipole)elm).getRadius());
  
		}
        
        
    public void setElement(TAbstractRendered md)
    {
        super.setElement(md);
        md.registerRenderFlag(TAbstractRendered.GEOMETRY_CHANGE);
    }
        
    private void initNode(double length,double radius)
            {
  
			
			redTg = new TransformGroup();
			redTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			redTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			Transform3D tRed = new Transform3D();
			tRed.setTranslation(new Vector3d(0, length * 0.375, 0));		
			redTg.setTransform(tRed);
            
			Geometry cyl = Cylinder.makeGeometry(24, radius, length / 4.0).getIndexedGeometryArray();
			redShape = new Shape3D(cyl);
			redShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			redShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			redShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
			redShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			redShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
			redShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

			Appearance redAppearance = new Appearance();
			redAppearance.setMaterial(
				new Material(
					new Color3f(Color.red),
					new Color3f(),
					new Color3f(Color.red),
					new Color3f(Color.red),
					64));
			redShape.setAppearance(redAppearance);
			redTg.addChild(redShape);
			addContents(redTg);

			blueTg = new TransformGroup();
			blueTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			blueTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			Transform3D tBlue = new Transform3D();
			tBlue.setTranslation(new Vector3d(0, -length * 0.375, 0));
		
			blueTg.setTransform(tBlue);

			blueShape = new Shape3D(cyl);
			blueShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			blueShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			blueShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
			blueShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			blueShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
			blueShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
			Appearance blueAppearance = new Appearance();
			blueAppearance.setMaterial(
				new Material(
					new Color3f(Color.blue),
					new Color3f(),
					new Color3f(Color.blue),
					new Color3f(Color.blue),
					64));
			blueShape.setAppearance(blueAppearance);
			blueTg.addChild(blueShape);
			addContents(blueTg);

			Geometry cyl2 = Cylinder.makeGeometry(24, radius, length / 2.0).getIndexedGeometryArray();
			centerShape = new Shape3D(cyl2);
			centerShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			centerShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			centerShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
			centerShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			centerShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
			centerShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
			Appearance centerAppearance = new Appearance();
			centerAppearance.setMaterial(
				new Material(
					new Color3f(Color.gray),
					new Color3f(),
					new Color3f(Color.gray),
					new Color3f(Color.gray),
					90));
			centerShape.setAppearance(centerAppearance);
		
			addContents(centerShape);


            }    
        
	public	void updateGeometry(double length, double radius) {

			//System.out.println("updating magDipole");
            
			Geometry cyl = Cylinder.makeGeometry(24, radius, length / 4.0).getIndexedGeometryArray();
			Geometry cyl2 = Cylinder.makeGeometry(24, radius, length / 2.0).getIndexedGeometryArray();
            Transform3D tRed = new Transform3D();
			tRed.setTranslation(new Vector3d(0,length * 0.375, 0));
            Transform3D tBlue = new Transform3D();
			tBlue.setTranslation(new Vector3d(0,-length * 0.375, 0));
			//tCenter.setTranslation(new Vector3d(-length/4.0,0,0));
			redTg.setTransform(tRed);
			redShape.removeAllGeometries();
			redShape.addGeometry(cyl);
			//centerTg.setTransform(tCenter);
			centerShape.removeAllGeometries();
			centerShape.addGeometry(cyl2);
			
			blueTg.setTransform(tBlue);
			blueShape.removeAllGeometries();
			blueShape.addGeometry(cyl);
			
		}
		
		public void fixColor(double mu) {
        
			Appearance redAppearance = new Appearance();
			redAppearance.setMaterial(
				new Material(
					new Color3f(Color.red),
					new Color3f(),
					new Color3f(Color.red),
					new Color3f(Color.red),
					64));

			Appearance blueAppearance = new Appearance();
			blueAppearance.setMaterial(
				new Material(
					new Color3f(Color.blue),
					new Color3f(),
					new Color3f(Color.blue),
					new Color3f(Color.blue),
					64));

			if( mu >= 0 ) {
				redShape.setAppearance(redAppearance);
				blueShape.setAppearance(blueAppearance);
			} else {
				redShape.setAppearance(blueAppearance);
				blueShape.setAppearance(redAppearance);
			}
		}
		
	}