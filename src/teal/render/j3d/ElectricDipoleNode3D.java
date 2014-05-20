/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ElectricDipoleNode3D.java,v 1.10 2010/04/12 19:52:07 stefan Exp $ 
 * 
 */

package teal.render.j3d;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.config.*;
import teal.render.TAbstractRendered;
import teal.render.j3d.geometry.Sphere;


import teal.util.*;


	/**
	 * Node for rendering an ElectricDipole, which contains multiple shapes.  I think this can be simplified such
	 * it need not implement TUpdatable...
	 */
	public class ElectricDipoleNode3D extends Node3D
	{
		
		TransformGroup redTg;
		TransformGroup blueTg;
		Shape3D redShape;
		Appearance redAppearance;
		Shape3D blueShape;
		Appearance blueAppearance;
		boolean isPositive = false;
	

		public ElectricDipoleNode3D()
		{
			super();
		    initNode(1.0,0.2);               
		}
	
		public ElectricDipoleNode3D(TAbstractRendered elm)
		{
			super();
			setElement(elm);
                      
		}
		
        private void initNode(double length,double radius)
            {
			
			redTg=new TransformGroup();
			redTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			redTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			Transform3D tRed=new Transform3D();
			tRed.setTranslation(new Vector3d(0,length/4.0,0));
			tRed.setRotation(new AxisAngle4d(new Vector3d(0,0,1),Math.PI/2));
			redTg.setTransform(tRed);
			Geometry cyl =Sphere.makeGeometry(24,radius).getIndexedGeometryArray();	
			redShape= new Shape3D(cyl);
			redShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			redShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			redShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
			redShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			redShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
			redShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
			
			redAppearance = new Appearance();
			redAppearance.setMaterial (new Material (new Color3f(Teal.PointChargePositiveColor), new Color3f(), new Color3f(Teal.PointChargePositiveColor), new Color3f(1.f,1.f,1.f), 64));
			
			redTg.addChild(redShape);
		
			blueTg=new TransformGroup();
			blueTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			blueTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			Transform3D tBlue=new Transform3D();
			tBlue.setTranslation(new Vector3d(0,-length/4.0,0));
			tBlue.setRotation(new AxisAngle4d(new Vector3d(0,0,1),-Math.PI/2));
			blueTg.setTransform(tBlue);
	
			blueShape= new Shape3D(cyl);
			blueShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			blueShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			blueShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
			blueShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			blueShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
			blueShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
			blueAppearance = new Appearance();
			blueAppearance.setMaterial (new Material (new Color3f(Teal.PointChargeNegativeColor), new Color3f(), new Color3f(Teal.PointChargeNegativeColor), new Color3f(1.f,1.f,1.f), 64));
			
			blueTg.addChild(blueShape);
			addContents(blueTg);
			addContents(redTg);
				
            }
		public void updateGeometry(double length,double radius)
		{
			TDebug.println(1,"updating eDipole");
            Geometry cyl =Sphere.makeGeometry(24,radius).getIndexedGeometryArray();
            
            Transform3D tRed=new Transform3D();
            tRed.setTranslation(new Vector3d(0,length/4.0,0));
            tRed.setRotation(new AxisAngle4d(new Vector3d(0,0,1),-Math.PI/2));
            redTg.setTransform(tRed);
			redShape.removeAllGeometries();
			redShape.addGeometry(cyl);
            
            Transform3D tBlue=new Transform3D();
            tBlue.setTranslation(new Vector3d(0,-length/4.0,0));
            tBlue.setRotation(new AxisAngle4d(new Vector3d(0,0,1),-Math.PI/2));
			blueTg.setTransform(tBlue);
			blueShape.removeAllGeometries();
			blueShape.addGeometry(cyl);
			fixColor(1.0);
		} 
		
		public void fixColor(double mu) {
		    if((mu >= 0.0) != isPositive){
		    	isPositive = (mu >= 0.0);
	        
				

				if( isPositive) {
					redShape.setAppearance(redAppearance);
					blueShape.setAppearance(blueAppearance);
				} else {
					redShape.setAppearance(blueAppearance);
					blueShape.setAppearance(redAppearance);
				}
		    }
		}
	
	}
