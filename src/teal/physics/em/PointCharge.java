/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PointCharge.java,v 1.43 2010/09/22 15:48:10 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;


import javax.swing.ImageIcon;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.render.BoundingSphere;
import teal.render.scene.SceneFactory;
import teal.render.scene.TNode3D;
import teal.sim.engine.TSimEngine;
import teal.sim.properties.HasRadius;
import teal.util.TDebug;

/**
 * Represents the most basic EM object, a point charge.
 */

/**
 * @author pbailey
 *
 */
public class PointCharge extends EMObject implements HasCharge, GeneratesE, GeneratesP, HasRadius {


    private static final long serialVersionUID = 3257003259120924471L;
    
    protected double charge;
	protected double charge_d;
	protected boolean generatingEField= true;
	protected boolean generatingPField= true;
	protected boolean generatingEPotential= true;
	
	protected int fluxMode;

	protected double radius;
	protected double pauliDistance = -1.;
	
	protected double epsilon= 1.0;

	public PointCharge() {
		super();
		nodeType = NodeType.SPHERE;
		mMaterial.setShininess(0.8f);
		charge= charge_d= 0.0;
		radius= Teal.PointChargeRadius;
		createBounds();
		mass= mass_d= Teal.PointChargeMass;
		fluxMode = 0;
		selectColor();
		setPickable(true);
		setRotable(false);
	}

	protected Shape makeShape() {
		return new Ellipse2D.Double(-radius, -radius, radius * 2.0, radius * 2.0);
	}

	public ImageIcon getIcon() {
		if (icon == null) {
			makeIcon();
		}
		return icon;
	}

	public void makeIcon() {

		int iconSize= Teal.iconSize;
		int border= Teal.iconBorderSize;
		BufferedImage img= new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D g= img.createGraphics();
		g.setColor(Teal.iconBackground);
		g.fillRect(0, 0, iconSize, iconSize);
		GradientPaint gp= new GradientPaint(border, border, Color.white, iconSize - border, iconSize - border, getColor().get());
		g.setPaint(gp);

		g.fill(new Ellipse2D.Double(border, border, iconSize - 2 * border, iconSize - 2 * border));
		icon= new ImageIcon(img);
	}
	protected void selectColor() {
		if (charge > 0) {
			if (getColor().get() != Teal.PointChargePositiveColor)
				setColor(Teal.PointChargePositiveColor);
		}
		else if (charge < 0) {
			if (getColor().get() != Teal.PointChargeNegativeColor)
				setColor(Teal.PointChargeNegativeColor);
		}
		else {
			if (getColor().get() != Teal.PointChargeNeutralColor)
				setColor(Teal.PointChargeNeutralColor);
		}
	}

	protected TNode3D makeNode() {
		TNode3D node= SceneFactory.makeNode(this);

		node.setRotable(false);
        node.setPickable(isPickable);
		return node;
	}
//	protected TNode3D makeNode() {
//		TShapeNode node= (TShapeNode)new SphereNode();
//        node.setElement(this);
//		node.setRotable(false);
//		node.setScale(radius);
//		node.setColor(TealMaterial.getColor3f(mMaterial.getDiffuse()));
//        node.setShininess(mMaterial.getShininess());
//        node.setPickable(isPickable);
//		return node;
//	}

	public Vector3d getE(Vector3d position, double t) {
		return getE(position);
	}

	/**
	 * <code>getE</code> returns the electric field generated by
	 * this PointCharge at a certain position.
	 *
	 * @param pos Distance at which E Field is calculated
	 * @return E Field at position
	 */

	public Vector3d getE(Vector3d pos) {
		Vector3d r= new Vector3d();
		r.sub(pos, position_d);
		double ro= r.lengthSquared();
		r.normalize();
		r.scale(this.charge_d / (Teal.fourPiPermVacuum * ro));
		//TDebug.println(-1,"get E: "+ pos + " = " + r);
		return r;
	}
	
	public double getEFlux(Vector3d pos) {
		
		if (fluxMode == 0) {
			// this is only going to work on the y axis for now
			double flux;
			double q = getCharge();
			double sign = (q >= 0.) ? 1.0 : -1.0;
			Vector3d myPosition = new Vector3d(position_d);
			if ((Math.abs(myPosition.x) > 0.0001) || (Math.abs(myPosition.z) > 0.0001)) 
			{
				TDebug.println(0,"PointCharge.getEFlux:  PointCharge is off y-axis, flux calculation does not apply.");
			}
			
			double a = pos.y - myPosition.y;
			double b = Math.sqrt(Math.pow(pos.x,2.0) + Math.pow(a,2.0));
			flux = 0.5*q*(1. - (a/b));
	        
	       return flux;	
		} else if (fluxMode == 1) {
//			 this is only going to work on the y axis for now
			double flux;
			double q = getCharge();
			double sign = (q >= 0.) ? 1.0 : -1.0;
			Vector3d myPosition = new Vector3d(position_d);
			if ((Math.abs(myPosition.x) > 0.0001) || (Math.abs(myPosition.z) > 0.0001)) 
			{
				TDebug.println(0,"PointCharge.getEFlux:  PointCharge is off y-axis, flux calculation does not apply.");
			}
			
			double a = pos.y - myPosition.y;
			double b = Math.sqrt(Math.pow(pos.x,2.0) + Math.pow(a,2.0));
			flux = 0.5*q*(-1. - (a/b));
	        
	        
			return flux;	
		} else {
			return 0.0;
		}
	}
	
	public int getFluxMode() {
		return fluxMode;
	}
	
	public void setFluxMode(int mode) {
		fluxMode = mode;
	}
	

	public void setGeneratingE(boolean b) {
		generatingEField= b;
		if(theEngine != null)
			theEngine.requestSpatial();
	}

	public boolean isGeneratingE() {
		return generatingEField;
	}
	public Vector3d getP(Vector3d position, double t) {
		return getP(position);
	}

	/**
	 * <code>getP</code> returns the Pauli field generated by this
	 * PointCharge at a point.
	 *
	 * @param pos Distance at which P Field is calculated
	 * @return P Field
	 */
	public Vector3d getP(Vector3d pos) {
		Vector3d R = new Vector3d();
		R.sub(pos, this.position_d);
		double r_2 = R.lengthSquared();
		double r = R.length();

		// k = Pauli power.	
		double k = 12.0;
		// r0 = Pauli distance.	
		double r0 = (pauliDistance < 0.)?(2.*radius):pauliDistance;

		R.normalize();

		// A scale of 1 exactly counterbalances the electric field.
		// Ad-hoc scale.
//		double scale = (r>r0)?0.:(10.*(r0-r)/r0+1.);
		// Pauli scale.
		double scale = Math.pow(r0/r, (k-2.));
		R.scale( Math.abs(this.charge_d) * scale / (Teal.fourPiPermVacuum * r_2));

		return R;
	}

	public void setGeneratingP(boolean b) {
		generatingPField= b;
		if(theEngine != null)
		theEngine.requestSpatial();
	}

	public boolean isGeneratingP() {
		return generatingPField;
	}

	public Vector3d getB(Vector3d pos) {
		Vector3d eVector= getE(pos);
		Vector3d bVector= new Vector3d();
		bVector.cross(velocity_d, eVector);
		bVector.scale(1 / Math.pow(3e8, 2.0));
		return bVector;
	}

	public Matrix3d getGradientBField(Vector3d pos) {
		Matrix3d m= new Matrix3d();
		Vector3d bFieldTest;
		Vector3d bField= getB(pos);

		bFieldTest= getB(new Vector3d(pos.x + this.epsilon, pos.y, pos.z));
		m.m00= m.m10= m.m20= (bFieldTest.x - bField.x) / this.epsilon;
		bFieldTest= getB(new Vector3d(pos.x, pos.y + this.epsilon, pos.z));
		m.m01= m.m11= m.m21= (bFieldTest.y - bField.y) / this.epsilon;
		bFieldTest= getB(new Vector3d(pos.x, pos.y, pos.z + this.epsilon));
		m.m02= m.m12= m.m22= (bFieldTest.z - bField.z) / this.epsilon;

		return m;
	}

	/**
	 * this returns a newly constructed Matrix with all values set to zero.
	 */
	public Matrix3d getGradientEField(Vector3d pos) {
		return new Matrix3d();
	}
	public double getEPotential(Vector3d pos, double time) {
		return getEPotential(pos);
	}
	public double getEPotential(Vector3d pos) {
		Vector3d r = new Vector3d();
		r.sub(pos, this.position_d);
		return getCharge()/r.length();  // fixed major problem, before 8/6/2008 this was returing charge 
		//times distance, not charge/distance  jwb
	}
	public boolean isGeneratingEPotential() {
		return generatingEPotential;
	}

	public double getCharge() {
		return this.charge;
	}

    public void setCharge(double ch) {
        setCharge(ch,true);
    }
	public void setCharge(double ch,boolean needsRefresh) {
		if (ch != charge) {
			Double old = new Double(charge);
			charge_d= ch;
			charge= ch;
			selectColor();
			firePropertyChange("charge",old,new Double( charge));
			if (theEngine != null)
				theEngine.requestSpatial();	
		}
	}

	public double getRadius() {
		return this.radius;
	}

	public void setRadius(double r) {
		if (r != radius) {
			Double old = new Double(radius);
			
			radius= r;
            bounds = null;
            createBounds();
            firePropertyChange("radius",old,new Double( radius));
			if (mNode != null)
            {
                 mNode.setScale(radius);
            }
			if (theEngine != null)
				theEngine.requestRefresh();
		}
	}
	
	public double getPauliDistance()
	{
		return this.pauliDistance;
	}
	
	public void setPauliDistance(double distance)
	{
		this.pauliDistance = distance;
		if(theEngine != null)
			theEngine.requestSpatial();
	}
	
	

	protected void createBounds()
	{
	    bounds = new BoundingSphere(new Point3d(),radius);
	}

	//TODO Check if this is correct
	public Vector3d getExternalForces() {
		Vector3d externalForces= super.getExternalForces();
		Vector3d temp= new Vector3d();
		if (isMoveable()) {
//			temp.scale(charge_d, ((EMEngine)theEngine).getEField().get(position_d, this)); // F=qE
//			temp.scale(charge_d, theEngine.getElementByType(EField.class).get(position_d, this)); // F=qE			
			temp.scale(charge_d, ((EField)theEngine.getElementByType(TSimEngine.EngineElementType.EFIELD)).get(position_d, this)); // F=qE			
			externalForces.add(temp);  // force from EField
//			temp.scale(Math.abs(charge_d), ((EMEngine)theEngine).getPField().get(position_d, this));
//			temp.scale(Math.abs(charge_d), theEngine.getElementByType(PField.class).get(position_d, this));
			temp.scale(Math.abs(charge_d), ((PField)theEngine.getElementByType(TSimEngine.EngineElementType.PFIELD)).get(position_d, this));
			externalForces.add(temp); // Pauli repulsion
//			temp.cross(velocity_d,((EMEngine)theEngine).getBField().get(position_d,this));
//			temp.cross(velocity_d,theEngine.getElementByType(BField.class).get(position_d,this));
			temp.cross(velocity_d,((BField)theEngine.getElementByType(TSimEngine.EngineElementType.BFIELD)).get(position_d,this));
			temp.scale(charge_d);
			//System.out.println("q vxB force" + temp);
			externalForces.add(temp);  //  q vxB force
		}
		return externalForces;
	}
	public void update() {
		
		if (charge != charge_d) {
			setCharge(charge_d);
            
        }
        super.update();
	}

	public String toString() {
		return ("Point Charge:" + id);
	}
}
