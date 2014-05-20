/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ElectricDipole.java,v 1.31 2010/07/21 21:57:19 stefan Exp $ 
 * 
 */

package teal.physics.em;

import javax.swing.ImageIcon;
import javax.vecmath.*;

import teal.config.Teal;
import teal.render.j3d.ElectricDipoleNode3D;
import teal.render.scene.TNode3D;
import teal.util.*;

/** Represents a Three Dimension Electric Dipole in Cartesian Coordinates.
 */
public class ElectricDipole extends Dipole implements HasCharge, GeneratesE {

    private static final long serialVersionUID = 3761123847226538288L;
    
    protected boolean generatingEPotential = true;;
    protected double charge;
    protected double charge_d;

    /** Default Constructor
     */
    public ElectricDipole() {
        super();
        generatingEField = true;
        setMass(Teal.ElectricDipoleMass);
        setCharge(Teal.ElectricDipoleDefaultCharge);
        radius = Teal.ElectricDipoleRadius;
        length = Teal.ElectricDipoleLength;
    }

    public String toString() {
        return "Electric Dipole " + id;
    }

    public double getCharge() {
        return this.charge;
    }

    public void setCharge(double ch) {
        double c = ch; //Math.abs(ch);
        TDebug.println(1, id + ": setting charge to: " + c);

        charge_d = c;
        Double old = new Double(charge);
        charge = c;
        firePropertyChange("charge", old, new Double(charge));
        if (theEngine != null) theEngine.requestSpatial();
        renderFlags |= COLOR_CHANGE;

    }

    public void render() {
        if ((renderFlags & COLOR_CHANGE) == COLOR_CHANGE) {
            if (mNode != null) ((ElectricDipoleNode3D) mNode).fixColor(charge);
            renderFlags ^= COLOR_CHANGE;
        }
        super.render();
    }

    public void update() {
        if (charge != charge_d)
            setCharge(charge_d);
        super.update();
    }

    public Vector3d getExternalForces() {
        Vector3d externalForces = super.getExternalForces();
        TDebug.println(3, "ExternalForces1=" + externalForces);
//        Matrix3d gradE = ((EMEngine)theEngine).getEField().getGradient(position_d, this);
        Matrix3d gradE = theEngine.getElementByType(EField.class).getGradient(position_d, this);
        TDebug.println(1, "gradE=" + gradE);
        Vector3d eForces = new Vector3d();
        Vector3d dEdx = new Vector3d();
        Vector3d dEdy = new Vector3d();
        Vector3d dEdz = new Vector3d();
        gradE.getColumn(0, dEdx);
        gradE.getColumn(1, dEdy);
        gradE.getColumn(2, dEdz);
        Vector3d dipoleMomentum = getDipoleMoment();
        eForces.x = dipoleMomentum.dot(dEdx);
        eForces.y = dipoleMomentum.dot(dEdy);
        eForces.z = dipoleMomentum.dot(dEdz);
        Vector3d pauli = new Vector3d();
//        pauli.scale(Math.abs(charge_d), ((EMEngine)theEngine).getPField().get(position_d, this));
        pauli.scale(Math.abs(charge_d), theEngine.getElementByType(PField.class).get(position_d, this));
        Vector3d damping = new Vector3d();
        damping.scale(-Teal.ElectricDipoleDamping, velocity_d);
        externalForces.add(eForces);
        externalForces.add(pauli);
        externalForces.add(damping);
        TDebug.println(3, "ExternalForces2=" + externalForces);
        return externalForces;
    }

    protected Vector3d getTorque() {
        // get torsional damping from super
        Vector3d T = super.getTorque();
        Vector3d p = new Vector3d(getDirection());
        p.scale(charge_d);
//        Vector3d E = ((EMEngine)theEngine).getEField().get(position_d, this);
        Vector3d E = theEngine.getElementByType(EField.class).get(position_d, this);
        Vector3d retour = new Vector3d();
        retour.cross(p, E);

        T.add(retour);
        return T;
    }

    /** 
     * Returns dm based on direction scaled by charge does not use an internal DM.
     */
    public Vector3d getDipoleMoment() {      
        Matrix3d mat = new Matrix3d();
        mat.set(orientation_d);
        Vector3d direction = new Vector3d(initialDirection);
        mat.transform(direction);
        Vector3d dipoleMoment = direction;
        dipoleMoment.set(direction);
        dipoleMoment.scale(charge_d);
        return dipoleMoment;
    }

    
    /** this just calls getE(pos) need to fix it. */
    public Vector3d getE(Vector3d pos, double t) {
        return getE(pos);
    }

    /** Used to calculate the E Field at a point due to the dipole
     *
     * @param pos position at which the e field has to be calculated
     * @return Electric Field
     */
    public Vector3d getE(Vector3d pos) {
        Vector3d n = new Vector3d();
        Vector3d p = new Vector3d(getDipoleMoment());
        Vector3d E = new Vector3d();
        n.sub(pos, position_d);
        double r = n.length();
        if (r == 0.) {
            return new Vector3d();
        }
        n.normalize();
        n.scale(3 * p.dot(n));
        E.add(n);
        E.sub(p);
        E.scale(1 / (r * r * r));
        E.scale(1. / (4. * Math.PI));
        return E;
    }

    // Returns the electric potential.
    public double getEPotential(Vector3d pos) {
        Vector3d n = new Vector3d();
        n.sub(pos, position_d);
        Vector3d p = new Vector3d(getDipoleMoment());
        double r = n.length();
        double pot = p.dot(n) / (r * r * r);
        if (pot > 0)
            return Math.sqrt(pot);
        else if (pot < 0)
            return -Math.sqrt(-pot);
        else return 0;
    }

    public double getEFlux(Vector3d pos) {
        // first crack
        Vector3d zprime = new Vector3d(getDipoleMoment());
        if (zprime.length() == 0.) {
            return 0.;
        } else {
            //zprime.normalize();
            Vector3d r = new Vector3d();
            r.sub(pos, position_d);

            

            // Old, but maybe still relevant
            //    	
            //    	double flux = 100.0 * 0.5*getDipoleMoment().length()*(0.5 - rDotz) / r.length();
            
            double angle = r.angle(zprime);
            double flux = 100. * (0.5 * getDipoleMoment().length() * Math.pow(Math.sin(angle), 2)) * (1. / r.length());

            return flux;
        }

    }

    /** this just calls getB(pos) need to fix it. */
    public Vector3d getB(Vector3d pos, double t) {
        return getB(pos);
    }

    /** Used to calculate the b field due to the dipole
     *
     * @param pos position at which the b field has to be calculated
     */

    public Vector3d getB(Vector3d pos) {
        Vector3d eVector = getE(pos);
        Vector3d bVector = new Vector3d();
        bVector.cross(velocity_d, eVector);
        bVector.scale(1 / 9e16);
        return bVector;
    }

    // placeholder
    public double getBFlux(Vector3d pos) {
        return 0.0;
    }

    public Vector3d getP(Vector3d pos, double t) {
        return getP(pos);
    }

    public Vector3d getP(Vector3d pos) {
        Vector3d r = new Vector3d();
        double E = getE(pos).length();
        r.sub(pos, position_d);
        double rlength = r.length();
        double pauliDistance = 2 * radius;
        r.normalize();
        r.scale(Teal.PauliConstant * E * (Math.pow(rlength / pauliDistance, Teal.PauliPower - 3)));
        return r;
    }

    /** Calculates the gradient of the E field at a point.
     *
     * @param pos position at which the gradient has to be calculated
     * @return the gradient of the efield.
     */
    public Matrix3d getGradientEField(Vector3d pos) {
        Matrix3d m = new Matrix3d();
        Vector3d fieldTest;
        Vector3d field;

        field = getE(pos);
        fieldTest = getE(new Vector3d(pos.x + epsilon, pos.y, pos.z));
        m.m00 = m.m10 = m.m20 = (fieldTest.x - field.x) / epsilon;
        fieldTest = getE(new Vector3d(pos.x, pos.y + epsilon, pos.z));
        m.m01 = m.m11 = m.m21 = (fieldTest.y - field.y) / epsilon;
        fieldTest = getE(new Vector3d(pos.x, pos.y, pos.z + epsilon));
        m.m02 = m.m12 = m.m22 = (fieldTest.z - field.z) / epsilon;

        return m;
    }

   

    /** Creates a new Matrix3d with all values = zero
     *
     * @param pos
     */
    public Matrix3d getGradientBField(Vector3d pos) {

        return new Matrix3d();
    }

    public TNode3D makeNode() {
        ElectricDipoleNode3D node = new ElectricDipoleNode3D();
        node.setElement(this);
        node.updateGeometry(length,radius);
        return node;

    }
    
    protected void updateNodeColor(){
        ((ElectricDipoleNode3D)mNode).fixColor(charge);
    }
    protected void updateNodeGeometry(){
        ((ElectricDipoleNode3D)mNode).updateGeometry(length,radius);
    }
    

    public ImageIcon getIcon() {
        return (ImageIcon) IconCreator.getIcon("Diapole.gif");
    }

}
