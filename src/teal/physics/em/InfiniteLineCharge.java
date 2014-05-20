/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: InfiniteLineCharge.java,v 1.6 2010/07/21 21:57:19 stefan Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.*;


import teal.render.scene.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.media.j3d.BoundingSphere;
import javax.swing.ImageIcon;
import javax.vecmath.*;

import teal.config.*;
import teal.render.TealMaterial;
import teal.render.j3d.SphereNode;
import teal.sim.properties.HasLength;
import teal.sim.properties.HasRadius;
import teal.util.TDebug;

/**
 * Represents an infinite line of charge.
 */
public class InfiniteLineCharge extends EMObject implements HasRadius, HasLength, HasCharge, GeneratesE {

    private static final long serialVersionUID = 3257005470995724084L;

    double charge;
	protected transient double charge_d;
    protected boolean generatingEField = true;
    protected boolean generatingEPotential = true;
    protected boolean generatingPField = false;
    protected double length;
    protected double radius;	
    protected int fluxMode;

    //Vector3d orientation;

    public InfiniteLineCharge() {
        super();
		charge= charge_d= 0.0;
		radius= Teal.PointChargeRadius;
		createBounds();
		mass= mass_d= Teal.PointChargeMass;
		fluxMode = 0;
		selectColor();
		setPickable(true);
		setRotable(false);
        setCharge(Teal.InfiniteLineChargeDefaultCharge);
        setMass(Teal.InfiniteLineChargeMass);
        radius = Teal.InfiniteLineChargeDefaultRadius;
        length = 20.0;
        
    }
    
	protected Shape makeShape() {
		return new Ellipse2D.Double(-radius, -radius, radius * 2.0, radius * 2.0);
	}

    /**
     Stub must be replaced 
     */
    public double getEFlux(Vector3d pos) {
        return 0.;
    }
	public void makeIcon() {

		int iconSize= Teal.iconSize;
		int border= Teal.iconBorderSize;
		BufferedImage img= new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D g= img.createGraphics();
		g.setColor(Teal.iconBackground);
		g.fillRect(0, 0, iconSize, iconSize);
		GradientPaint gp= new GradientPaint(border, border, Color.white, iconSize - border, iconSize - border, mMaterial.getDiffuse().get());
		g.setPaint(gp);

		g.fill(new Ellipse2D.Double(border, border, iconSize - 2 * border, iconSize - 2 * border));
		icon= new ImageIcon(img);
	}



	protected TNode3D makeNode() {
		TShapeNode node= (TShapeNode)new SphereNode();
        node.setElement(this);
		node.setRotable(false);
		node.setScale(radius);
		node.setColor( TealMaterial.getColor3f(mMaterial.getDiffuse()));
        node.setShininess(mMaterial.getShininess());
        node.setPickable(isPickable);
		return node;
	}


	/**
	 * <code>getE</code> returns the electric field generated by
	 * this PointCharge at a certain position.
	 *
	 * @param pos Distance at which E Field is calculated
	 * @return E Field at position
	 */


    public void setGeneratingE(boolean b) {
        generatingEField = b;
        if (theEngine != null) theEngine.requestSpatial();
    }

    public boolean isGeneratingE() {
        return generatingEField;
    }

    public void setGeneratingP(boolean b) {
        generatingPField = b;
        if (theEngine != null) theEngine.requestSpatial();
    }

    public boolean isGeneratingP() {
        return generatingPField;
    }

 


    public Vector3d getE(Vector3d x, double t) {
        return getE(x);
    }

    public Vector3d getE(Vector3d x) {
    	
        Vector3d E = new Vector3d();
        Vector3d r = new Vector3d();
        Vector3d rperp = new Vector3d();
        Vector3d rpar = new Vector3d();
        Vector3d v = new Vector3d();

        // This should get the distance to the closest point on the wire
        r.sub(x, position_d);
        v = getDirection();
        rpar.scale(r.dot(v) / (v.length() * v.length()), v);
        rperp.sub(r, rpar);
        //System.out.println(" x " + x );
        if (rperp.length() == 0) return new Vector3d();
        else {
             E.scale(charge / (rperp.length() * rperp.length()),rperp);
           //  System.out.println(" x " + x + " E "+ E);
        return E;
        }
    }

    // Placeholder. Must return the electric potential.
    public double getEPotential(Vector3d pos) {
        return 0.;
    }
    
    public Vector3d getDirection() {
        Vector3d direction = new Vector3d(0.,0.,1.);
        return direction;
    }

    public double getLength() {
        return length;
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

	protected void selectColor() {
		if (charge > 0) {
			if (mMaterial.getDiffuse().get() != Teal.PointChargePositiveColor)
				setColor(Teal.PointChargePositiveColor);
		}
		else if (charge < 0) {
			if (mMaterial.getDiffuse().get() != Teal.PointChargeNegativeColor)
				setColor(Teal.PointChargeNegativeColor);
		}
		else {
			if (mMaterial.getDiffuse().get() != Teal.PointChargeNeutralColor)
				setColor(Teal.PointChargeNeutralColor);
		}
	}

    public void setLength(double h) {
        length = h;
        renderFlags |= GEOMETRY_CHANGE;
        if (theEngine != null) theEngine.requestSpatial();
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double r) {
        radius = r;
        renderFlags |= GEOMETRY_CHANGE;
        if (theEngine != null) theEngine.requestRefresh();
    }

	public Vector3d getExternalForces() {
		Vector3d externalForces= super.getExternalForces();
		Vector3d temp= new Vector3d();
		if (isMoveable()) {
//			temp.scale(charge_d, ((EMEngine)theEngine).getEField().get(position_d, this)); // F=qE
			temp.scale(charge_d,theEngine.getElementByType(EField.class).get(position_d, this)); // F=qE
			externalForces.add(temp);  // force from EField
//			temp.scale(Math.abs(charge_d), ((EMEngine)theEngine).getPField().get(position_d, this));
			temp.scale(Math.abs(charge_d), theEngine.getElementByType(PField.class).get(position_d, this));
			externalForces.add(temp); // Pauli repulsion
//			temp.cross(velocity_d,((EMEngine)theEngine).getBField().get(position_d,this));
			temp.cross(velocity_d,theEngine.getElementByType(BField.class).get(position_d,this));
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
		return ("Line Charge:" + id);
	}
	
    public void render() {
        if (mNode != null) {
            if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {

                if (mNode != null) {
                    
                //    ((WireNode) mNode).setArrowDirection(current);
                }
                renderFlags ^= GEOMETRY_CHANGE;
                

            }
            super.render();
        }
    }

}
