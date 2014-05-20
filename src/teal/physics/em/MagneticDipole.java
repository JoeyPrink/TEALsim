/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: MagneticDipole.java,v 1.39 2010/09/02 19:44:18 stefan Exp $ 
 * 
 */

package teal.physics.em;

import javax.swing.ImageIcon;
import javax.vecmath.*;

import teal.config.Teal;
import teal.core.TUpdatable;
import teal.render.HasRotation;
import teal.render.scene.SceneFactory;
import teal.render.scene.TMagDipoleNode3D;
import teal.render.scene.TNode3D;
import teal.util.*;

/**
 * Represents a magnetic point-dipole.  See Section 3.1 of the 
 * <a href="C:\Development\Projects\generalDoc\TEAL_Physics_Math.pdf"> 
 * TEAL Physics and Mathematics</a> documentation.  
 */
public class MagneticDipole extends Dipole {

    private static final long serialVersionUID = 3257290227361068337L;
    
    protected double mu;
    protected boolean feelsBField = true;
    protected boolean avoid_singularity = false;
    protected double avoid_singularity_scale = 1;

    public MagneticDipole() {
        super();
        generatingBField = true;
        radius = Teal.MagnetRadius;
        length = Teal.MagnetLength;

        mu = Teal.MagnetDefaultMu;
        setMass(Teal.MagnetMass);
        setColor(Teal.MagnetColor);
    }

    /** 
     * Returns dipole moment calculated as a function of direction and mu.
     */
    public Vector3d getDipoleMoment() {
        Matrix3d mat = new Matrix3d();
        mat.set(orientation_d);
        Vector3d direction = new Vector3d(initialDirection);
        mat.transform(direction);
        Vector3d dipoleMoment = direction; // getDirection(); // Very very bad!
        dipoleMoment.scale(mu);
        return dipoleMoment;
    }

    /**
     * Returns the magnitude of the dipole moment (mu).
     * 
     * @return the magnitude of the dipole moment.
     */
    public double getMu() {
        return mu;
    }

    /**
     * Sets the magnitude of the dipole moment to this value.
     * 
     * @param ch the new magnitude of the dipole moment.
     */
    public void setMu(double ch) {
        double c = (ch);
        TDebug.println(1, id + ": setting mu to: " + c);
        Double old = new Double(mu);
        mu = c;
        if (theEngine != null) theEngine.requestSpatial();
        firePropertyChange("mu", old, new Double(mu));

        renderFlags |= COLOR_CHANGE;

    }

    public void render() {
        if ((renderFlags & COLOR_CHANGE) == COLOR_CHANGE) {
            if (mNode != null && mNode instanceof TMagDipoleNode3D) ((TMagDipoleNode3D) mNode).fixColor(mu);
            renderFlags ^= COLOR_CHANGE;
        }
        super.render();
    }

    /** 
     * Sets whether this object responds to external magnetic fields.
     * 
     * @param x well does it??
     */
    public void setFeelsBField(boolean x) {
        feelsBField = x;
        if (theEngine != null) theEngine.requestSpatial();
    }

    public Vector3d getExternalForces() {
        Vector3d externalForces = super.getExternalForces();
        if (Double.isNaN(externalForces.length())) {
            TDebug.println(2,"NaN(1) in teal.sim.physical.MagneticDipole.geteExternalForces().");
        }
        try {
            if (feelsBField) {
//                Matrix3d gradB = ((EMEngine)theEngine).getBField().getGradient(position_d, this);
                Matrix3d gradB = theEngine.getElementByType(BField.class).getGradient(position_d, this);
                Vector3d bForces = new Vector3d();
                Vector3d dBdx = new Vector3d();
                Vector3d dBdy = new Vector3d();
                Vector3d dBdz = new Vector3d();
                gradB.getColumn(0, dBdx);
                gradB.getColumn(1, dBdy);
                gradB.getColumn(2, dBdz);
                Vector3d dipoleMoment = getDipoleMoment();
                bForces.x = dipoleMoment.dot(dBdx);
                bForces.y = dipoleMoment.dot(dBdy);
                bForces.z = dipoleMoment.dot(dBdz);
                externalForces.add(bForces);
            }
        } catch (ArithmeticException ae) {
            TDebug.printThrown(0, ae);
        }

        if (Double.isNaN(externalForces.length())) {
            TDebug.println(2,"NaN(2) in teal.sim.physical.MagneticDipole.geteExternalForces().");
        }

        return externalForces;
    }

    protected Vector3d getTorque() {
        //get torsional damping from PhysicalObject
        Vector3d T = super.getTorque();
        Vector3d m = new Vector3d(getDipoleMoment());
//        Vector3d B = ((EMEngine)theEngine).getBField().get(position_d, this);
        Vector3d B = theEngine.getElementByType(BField.class).get(position_d, this);
        Vector3d retour = new Vector3d();
        retour.cross(m, B);
        T.add(retour);
        return T;
    }

    public Vector3d getB(Vector3d pos) {
        Vector3d B = new Vector3d();
        Vector3d n = new Vector3d();
        Vector3d m = new Vector3d(getDipoleMoment());
        n.sub(pos, position_d);
        double r = n.length();
        double tiny1 = avoid_singularity_scale * 1e-1;
        double tiny2 = avoid_singularity_scale * 1e-2;
        Vector3d direction = new Vector3d(m);
        direction.normalize();
        double depth = Math.abs(n.dot(direction));

        if (r < tiny1 && depth < tiny2 && avoid_singularity) {
            double a = Math.sqrt(r * r - depth * depth);
            double area = Math.PI * a * a;
            B.set(m);
            B.scale(2. / a);
            B.scale(1. / area);
        } else {
            n.normalize();
            n.scale(3. * m.dot(n));
            B.add(n);
            B.sub(m);
            B.scale(1. / (r * r * r));
            B.scale(Teal.PermitivityVacuumOver4Pi);
        }
        return B;
    }

    public Vector3d getB(Vector3d pos, double t) {
        return getB(pos);
    }

    /**
     * Setting "avoid singularity" to true is equivalent to approximating the B field
     * by a uniform value within a disk around the singular point of the magnet. 
     */
    public void setAvoidSingularity(boolean x) {
        avoid_singularity = x;
    }

    public boolean getAvoidSingularity() {
        return avoid_singularity;
    }

    /**
     * The dimension of the disk within which the B field is approximated.  The approximation is a uniform 
     * value is of radius scale*1e-1 and thickness 2.*scale*1e-2. 
     */
    public void setAvoidSingularityScale(double x) {
        avoid_singularity_scale = x;
    }

    public double getAvoidSingularityScale() {
        return avoid_singularity_scale;
    }

    /**
     * Returns the "flux" value of this dipole at the given position.  This uses the flux function as given 
     * by equation (3.1.3.1) of the <a href="C:\Development\Projects\generalDoc\TEAL_Physics_Math.pdf"> 
     * TEAL Physics and Mathematics</a>  documentation.  Note that we are taking mu naught to be 
     * 1 in this expression and multiplying by a factor of 100 simply to make the flux values in a reasonable 
     * range.  Any change in this factor of 100 should be accompanied by similar changes in anything else 
     * used in magnetic flux calculations, e.g. the ring of current.  
     */
    public double getBFlux(Vector3d pos) {
        Vector3d zprime = new Vector3d(getDipoleMoment());
        if (zprime.length() == 0.) {
            return 0.;
        } else {
            //zprime.normalize();
            Vector3d r = new Vector3d();
            r.sub(pos, position_d);
            double angle = r.angle(zprime);
            double flux = 100. * (0.5 * getDipoleMoment().length() * Math.pow(Math.sin(angle), 2)) * (1. / r.length());
            return flux;
        }

    }

    public Vector3d getE(Vector3d pos) {
        // non-relativistic calculation of E field of moving
        // magnetic dipole as -VxB, assuming V in m/sec
        Vector3d bVector = getB(pos);
        Vector3d eVector = new Vector3d();
        eVector.cross(velocity, bVector);
        eVector.scale(-1);
        return eVector;
    }

    // Placeholder. Must return the electric potential.
    public double getEPotential(Vector3d pos) {
        return 0.;
    }

    public Vector3d getE(Vector3d pos, double t) {
        // non-relativistic calculation of E field of moving
        // magnetic dipole as -VxB, assuming V in m/sec
        Vector3d bVector = getB(pos);
        Vector3d eVector = new Vector3d();

        eVector.cross(velocity, bVector);
        eVector.scale(-1);
        return eVector;
    }

    public Matrix3d getGradientBField(Vector3d pos) {
        Matrix3d m = new Matrix3d();
        Vector3d bFieldTest;
        Vector3d bField = getB(pos);

        bFieldTest = getB(new Vector3d(pos.x + epsilon, pos.y, pos.z));
        m.m00 = m.m10 = m.m20 = (bFieldTest.x - bField.x) / epsilon;

        bFieldTest = getB(new Vector3d(pos.x, pos.y + epsilon, pos.z));
        m.m01 = m.m11 = m.m21 = (bFieldTest.y - bField.y) / epsilon;

        bFieldTest = getB(new Vector3d(pos.x, pos.y, pos.z + epsilon));
        m.m02 = m.m12 = m.m22 = (bFieldTest.z - bField.z) / epsilon;

        return m;
    }

    /**
     * this returns a newly constructed Matrix with all values set to zero.
     */
    public Matrix3d getGradientEField(Vector3d pos) {
        return new Matrix3d();
    }

    public ImageIcon getIcon() {
        return (ImageIcon) IconCreator.getIcon("Magnet.gif");
    }

    protected TNode3D makeNode() {
        TNode3D node = SceneFactory.makeNode(this);
        if (mNode instanceof TMagDipoleNode3D){
        	((TMagDipoleNode3D)node).updateGeometry(length,radius);
        	((TMagDipoleNode3D)node).fixColor(mu);
        }
        node.setRotation(orientation);
        return node;
    }
    protected void updateNodeColor(){
    	if (mNode instanceof TMagDipoleNode3D) ((TMagDipoleNode3D)mNode).fixColor(mu);
    }
    protected void updateNodeGeometry(){
    	if (mNode instanceof TMagDipoleNode3D) ((TMagDipoleNode3D)mNode).updateGeometry(length,radius);
    }
    protected void updateNode() {
        if (mNode != null) {
            if (mNode instanceof TMagDipoleNode3D) 
            	((TUpdatable) mNode).update();
            ((HasRotation) mNode).setRotation(orientation);
        }
    }

}
