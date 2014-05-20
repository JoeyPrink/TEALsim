/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: LineMagneticDipole.java,v 1.6 2010/09/22 15:48:10 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import javax.vecmath.Vector3d;

/**
 * Represents an "extended" line magnetic dipole.  Unlike a point dipole, this one has a physical length, and more
 * accurately models a bar magnet.
 */
public class LineMagneticDipole extends MagneticDipole {

    private static final long serialVersionUID = 3257562923475088944L;

    public LineMagneticDipole() {
        super();
        nodeType = nodeType.DIPOLE_MAG;
    }

    public LineMagneticDipole(Vector3d pos, Vector3d dir, double len) {
        super();
        setPosition(pos);
        setDirection(dir);
        setLength(len);
    }

    /* (non-Javadoc)
     * @see teal.sim.physical.em.Dipole#getDipoleMoment()
     */
    public Vector3d getDipoleMoment() {
        // TODO Auto-generated method stub
        return super.getDipoleMoment();
    }

    /* (non-Javadoc)
     * @see teal.sim.physical.em.GeneratesB#getB(javax.vecmath.Vector3d)
     */
    public Vector3d getB(Vector3d pos) {
    	Vector3d relpos = new Vector3d();
    	relpos.set(pos);
    	relpos.sub(position);
        Vector3d xp = new Vector3d();
        Vector3d yp = new Vector3d();

        yp.set(getDirection());  // the y-axis is along the direction of the line dipole
        yp.normalize();
        
        double pdoty = relpos.dot(yp);
        yp.scale(pdoty);
        xp.set(relpos);
        xp.sub(yp);  // this is the vector pos minus the projection of pos along the y-axis
        double pdotx = xp.length();  //  the x-axis is in the plane of the y-axis and the postition
                                     //  vector and is perpendicular to the y-axis
        
        double xdenom1 = Math.pow((pdotx * pdotx + Math.pow(pdoty - 0.5 * this.getLength(), 2)), 1.5);
        double xdenom2 = Math.pow((pdotx * pdotx + Math.pow(pdoty + 0.5 * this.getLength(), 2)), 1.5);
        double Bx = ((this.getMu() * pdotx) / (4 * Math.PI)) * (1. / xdenom1 - 1. / xdenom2);
        double By = (this.getMu() / (4 * Math.PI))
            * (((pdoty - 0.5 * this.getLength()) / xdenom1) - ((pdoty + 0.5 * this.getLength()) / xdenom2));

        
        yp.set(getDirection());
        yp.normalize();
        
        yp.scale(By);
        
        if (xp.length() != 0.) {
            xp.normalize();
            xp.scale(Bx);
            yp.add(xp);
        }
        
        return yp;
    }

    /* (non-Javadoc)
     * @see teal.sim.physical.em.GeneratesB#getBFlux(javax.vecmath.Vector3d)
     */
    public double getBFlux(Vector3d pos) {
        Vector3d xp = new Vector3d();
        Vector3d yp = new Vector3d();
        Vector3d relpos = new Vector3d();
    	relpos.set(pos);
    	relpos.sub(position);
        yp.set(getDirection());  // the y-axis is along the direction of the line dipole
        yp.normalize();
        
        double pdoty = relpos.dot(yp);
        yp.scale(pdoty);
        xp.set(relpos);
        xp.sub(yp);  // this is the vector pos minus the projection of pos along the y-axis
        double pdotx = xp.length();  //  the x-axis is in the plane of the y-axis and the postition
                                     //  vector and is perpendicular to the y-axis
        
        double xdenom1 = Math.pow((pdotx * pdotx + Math.pow(pdoty - 0.5 * this.getLength(), 2)), 0.5);
        double xdenom2 = Math.pow((pdotx * pdotx + Math.pow(pdoty + 0.5 * this.getLength(), 2)), 0.5);
        double BFlux = 100.*(this.getMu() / 2)
            * (((pdoty - 0.5 * this.getLength()) / xdenom1) - ((pdoty + 0.5 * this.getLength()) / xdenom2));

        return BFlux;
    }

}
