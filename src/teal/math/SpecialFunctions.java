package teal.math;

import javax.vecmath.*;

public class SpecialFunctions {
	  /**
     * Elliptic integrals: 
     *	This algorithm for the calculation of the complete elliptic
     *	integral (CEI) is presented in papers by Ronald Bulirsch,
     *	Numerical Calculation of Elliptic Integrals and 
     *	Elliptic Functions, Numerische Mathematik 7,
     *	78-90 (1965) and Ronald Bulirsch: Numerical Calculation 
     *	of Elliptic Integrals and Elliptic Functions III,
     *	Numerische Mathematik 13,305-315 (1969).  The definition
     *	of the complete elliptic integral is given in equation (1.1.1.1) of the
     * <a href="C:\Development\Projects\generalDoc\TEAL_Physics_Math.pdf"> 
     * TEAL Physics and Mathematics</a> documentation. 
     */

    public static double ellipticIntegral(double kcc, double pp, double aa, double bb, double accuracy) {
        double ca, kc, p, a, b, e, m, f, q, g;
        ca = accuracy;
        kc = kcc;
        p = pp;
        a = aa;
        b = bb;
        if ( kc != 0.0 ) 
        {
        	kc = Math.abs(kc);
        	e = kc;
        	m = 1.0;
        	
        	if (p > 0.) 
        	{
        		p = Math.sqrt(p);
        		b = b/p;
        	} 
        	else 
        	{
        		f = Math.pow(kc,2.0);
        		q = 1.-f;
        		g = 1.-p;
        		f = f-p;
        		q = q*(b-a*p);
        		p = Math.sqrt(f/g);
        		a = (a-b)/g;
        		b = -q/(p*Math.pow(g,2.0)) + a*p;
        	}

        	f = a;
        	a = b/p + a;
        	g = e/p;
        	b = 2.0*(f*g + b);
        	p = p + g;
        	g = m;
        	m = m + kc;
        	
        	while (Math.abs(g - kc) > g*ca) 
        	{
        		kc = 2.0*Math.sqrt(e);
        		e = kc*m;
        		f = a;
        		a = b/p + a;
        		g = e/p;
        		b = 2.0*(f*g + b);
        		p = p + g;
        		g = m;
        		m = m + kc;
        	}
        	
        	return (Math.PI / 2.)*(a*m + b)/(m*(m + p));
        	
        }
        
        else 
        {
        	return 0.0;
        }
    
    }
    
    /**
     * Flux through a disk due to a dipole: 
     *	This algorithm calculates the flux through a disk due to
     *  a dipole.  The derivation of the expression we use here is 
     *	given in Section 4.2 of the
     * <a href="C:\Development\Projects\generalDoc\TEAL_Physics_Math.pdf"> 
     * TEAL Physics and Mathematics</a> documentation. 
     * 
     * @param posDip the position of the dipole.
     * @param dirDip the orientation of the dipole.
     * @param posDisk the position of the disk.
     * @param dirDisk the orientation of the normal to the disk.  
     * @radDisk radius the radius of the disk.
     * @dipMoment dipMoment the dipole moment.
     * @return the flux through the disk due to the dipole.  This value should be multiplie by mho naught over 4 Pi or 1 over 4 Pi 
     * episilon naught depending on whether this is an electric dipole or a magnetic dipole.  
     */
    public static double FluxThroughRingDueToDipole(Vector3d posDip, Vector3d dirDip, Vector3d posDisk, Vector3d dirDisk, double radDisk, double dipMoment) {
    //    System.out.println("posDip "+posDip+ " dirDip " + dirDip + " postion disk y " + posDisk.y + " dirDisk "	+dirDisk+ " radDisk " + radDisk);
    	// construct the coordinate system described in Section 4.2
    	Vector3d zaxis = new Vector3d();
    	Vector3d xaxis = new Vector3d();
    	Vector3d yaxis = new Vector3d();
    	Vector3d radius = new Vector3d();
    	zaxis = dirDisk;
    	zaxis.normalize();
    	radius.sub(posDip,posDisk);
    	yaxis.cross(zaxis, radius);
    	if (yaxis.length() == 0.) yaxis.cross(zaxis, new Vector3d(1.,2.,3.));
    	yaxis.normalize();
        xaxis.cross(yaxis, zaxis);
        xaxis.normalize();
    //    System.out.println("xaxis "+xaxis+" yaxis "+yaxis+" zaxis " + zaxis);
        
        // construct the vectors Rplus and Rminus
        Vector3d Rplus = new Vector3d();
        Vector3d Rminus = new Vector3d();
        Vector3d axaxis = new Vector3d();
        axaxis.scale(radDisk,xaxis);
    //    System.out.println("axaxis "+axaxis);
        Rplus.add(radius,axaxis);
        Rminus.sub(radius,axaxis);
   //     System.out.println("Rplus "+Rplus+" Rminus "+Rminus);
        double Rminuslength = Rminus.length();
        double Rpluslength = Rplus.length();
        if (Rminuslength <= 0.001) Rminuslength = .001;
        if (Rpluslength <= 0.001) Rpluslength = .001;
        double kc = Rminuslength/Rpluslength;
        Vector3d cplusvector = new Vector3d();
        Vector3d cminusvector = new Vector3d();
        Vector3d dirDipNorm = dirDip;
        dirDipNorm.normalize();
        cplusvector.cross(dirDipNorm,Rplus);
        cminusvector.cross(dirDipNorm,Rminus);
      //  System.out.println("cplusvector "+cplusvector+" cminusvector "+cminusvector);
        double cplus =(cplusvector.dot(yaxis))/radDisk;
        double cminus = -(cminusvector.dot(yaxis))/radDisk;
      //  System.out.println("kc "+kc+"  cplus "+cplus+" cminus "+cminus);
        double flux = (4.*dipMoment*radDisk*radDisk/Math.pow(Rpluslength, 3.))*ellipticIntegral(kc,kc*kc,cplus,cminus,.001);
  //      System.out.println(posDisk.y+", "+flux);
        return flux;

    }
}
