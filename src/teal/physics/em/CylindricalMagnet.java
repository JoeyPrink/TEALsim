package teal.physics.em;

import javax.vecmath.Vector3d;

/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CylindricalMagnet.java,v 1.4 2010/09/22 15:48:10 pbailey Exp $ 
 * 
 */
/**
 * Represents an cylindrical magnet.  Unlike a point or line dipole, this one has a physical length and
 * radius, and accurately models a bar magnet.
 */
public class CylindricalMagnet extends LineMagneticDipole {

	public CylindricalMagnet() {
		super();

	}

	public CylindricalMagnet(Vector3d pos, Vector3d dir, double len) {
		super(pos, dir, len);
	}
	
	public CylindricalMagnet(Vector3d pos, Vector3d dir, double len, double rad) {
		super(pos, dir, len);
		this.setRadius(rad);
	}
	
    public Vector3d getB(Vector3d pos) {
        Vector3d xp = new Vector3d();
        Vector3d yp = new Vector3d();

        yp.set(getDirection());
        yp.normalize();
        
        double pdoty = pos.dot(yp);
        yp.scale(pdoty);
        xp.set(pos);
        xp.sub(yp);
        double pdotx = xp.length();
        

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
    public double getBFlux(Vector3d position) {
        // TODO Auto-generated method stub
        return 0;
    }

	
}
