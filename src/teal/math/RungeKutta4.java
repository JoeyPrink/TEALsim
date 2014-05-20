/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RungeKutta4.java,v 1.12 2007/12/24 22:33:27 jbelcher Exp $
 * 
 */

package teal.math;


/**
 * Fourth order Runge-Kutta integration implementation.  An Integratable (in most cases, the SimEngine itself) is passed
 * to the integration method, which uses Integratable.getDependentValues() and Integratable.getDependentDerivatives()
 * (and corresponding setters) to advance the solution by the given time step.
 */
public class RungeKutta4 {
	/**
	 * Given a Integratable object, this method computes new values for
	 * all of the dependent variables passed in.
	 * It returns an array of doubles, each of which correspond to new
	 * values of dependent variables returned by
	 * RungeKuttable.getDependentValues()
	 * 
	 * @param rk Object to be integrated.
	 * @param s_start Initial independent value.
	 * @param stepsize Integration step size.
	 */
    public static double[] integrate(Integratable rk, double s_start, double stepsize) {
		//		System.out.println( " Start ____________________________" );
		int n = rk.getNumberDependentValues();
		double[] X_at_s = new double[n];
		double[] dXds_at_s = new double[n];
		double[] X_at_sph = new double[n];
		double[] Xt = new double[n];
		double[] dXt = new double[n];
		double[] dXm = new double[n];
		double h = stepsize;
		double h_2 = stepsize / 2.;
		double h_6 = stepsize / 6.;
		double s_p_h_2 = s_start + h_2;

		rk.getDependentValues(X_at_s, 0);
		rk.getDependentDerivatives(dXds_at_s, 0, s_start);
		for (int i = 0; i < n; i++) {
			Xt[i] = X_at_s[i] + h_2 * dXds_at_s[i];
		}

		rk.setDependentValues(Xt, 0);
		rk.getDependentDerivatives(dXt, 0, s_p_h_2);
		for (int i = 0; i < n; i++) {
			Xt[i] = X_at_s[i] + h_2 * dXt[i];
		}

		rk.setDependentValues(Xt, 0);
		rk.getDependentDerivatives(dXm, 0, s_p_h_2);
		for (int i = 0; i < n; i++) {
			Xt[i] = X_at_s[i] + h * dXm[i];
			dXm[i] = dXt[i] + dXm[i];
		}

		rk.setDependentValues(Xt, 0);
		rk.getDependentDerivatives(dXt, 0, s_start + h);
		for (int i = 0; i < n; i++) {
			X_at_sph[i] = X_at_s[i] + h_6
					* (dXds_at_s[i] + dXt[i] + 2. * dXm[i]);
		}

		//		System.out.println( " End ----------------------------" );
		return X_at_sph;
	}
    
/*
    public static double[] integrate(Integratable rk,double stepsize) {
		double[] X_at_s = rk.getDependentValues();
		double s_start = rk.getIndependentValue();
		double[] dXds_at_s = rk.getDependentDerivatives(X_at_s,0,s_start);
		double h = stepsize;
		double sh;
		int n = X_at_s.length;
		double[] X_at_sph = new double[n];
		double[] Xt = new double[n];
		double[] dXt = new double[n];
		double[] dXm = new double[n];
		double h6 = h/6.;
		double hh = h/2.;
		for (int i=0;i < n;i++) Xt[i] = X_at_s[i]+hh*dXds_at_s[i];
		sh = s_start + hh;
		rk.setDependentValues( Xt ,0);
		dXt = rk.getDependentDerivatives(Xt,0,sh);
		for (int i=0;i < n;i++) Xt[i] = X_at_s[i] + hh*dXt[i];
		rk.setDependentValues( Xt,0 );
		dXm =  rk.getDependentDerivatives(Xt,0,sh);
		for (int i=0;i < n;i++) {
			Xt[i] = X_at_s[i]+h*dXm[i];
			dXm[i] = dXt[i]+dXm[i];
		}
		rk.setDependentValues( Xt,0 );
		dXt = rk.getDependentDerivatives(Xt,0,s_start+h);
		for (int i=0;i < n;i++) {
			X_at_sph[i] = X_at_s[i] + h6 * (dXds_at_s[i] +dXt[i] + 2.*dXm[i]);
		}
		return X_at_sph;
    }
*/	
	/**
	 * Starting at s1, this method returns an estimate of X at s2
	 * with a specified error.
	 *
	 * @param s_start The starting value of the dependent variables
	 * @param eps The desired error
	 */
	//Methods
	
	// starting at s1, find an estimate of X at s2
	// using a 4-th order Runge-Kutta method with estimate of error and
	// variable step size to stay within a specified error
	public static double[] integrate(Integratable rk, double s_start,
			double stepsize, double eps) {
		int n = rk.getNumberDependentValues(); //  number of dependent variables
		double[] X_at_s = new double[n];
		rk.getDependentValues(X_at_s, 0);
		double[] dXds_at_s = new double[n];
		rk.getDependentDerivatives(dXds_at_s, 0, s_start);
		double hstep = stepsize;
		double hmin = hstep / 100.;
		// we put the end point to s1+h, so that one step would take us from s1 to s2
		double s_end = s_start + hstep;
		//these are varialbes that set by rungeKuttaQualityControl; they are
		//respectively the step actually taken and an estimate of the next step
		//that will give new values with an error less than eps
		double hdid = 0.;
		double hnext = 0.;
		int nstp;
		double[] Xtry = new double[n];
		double[] X = new double[n];
		double[] Xscal = new double[n];
		double[] array = new double[n + 4];
		double h;
		int maxstp = 1000; // maximum number of steps we allow in getting from s1 to s2
		int note = 0; //  these are flags for the quality of the process. if note != 0 flawed
		int nok = 0; // numnber of steps to get to desired result
		int nbad = 0; // number of steps we had to take to get there
		double tiny = 1E-30;
		if ((s_end - s_start) > 0.)
			h = Math.abs(hstep);
		else
			h = -Math.abs(hstep);
		double s = s_start;
		for (int i = 0; i < n; i++)
			Xtry[i] = X_at_s[i];
		for (nstp = 1; nstp <= maxstp; nstp++) {
			if (nstp != 1)
				rk.setDependentValues(Xtry, 0);
			// if nstp = 1 we do not do this because we already have the values
			if (nstp != 1)
				rk.getDependentDerivatives(dXds_at_s, 0, s); // ditto
			for (int i = 0; i < n; i++)
				Xscal[i] = Math.abs(Xtry[i]) + Math.abs(h * dXds_at_s[i]) + tiny;
//			 	this is the array used for error estimate in our scheme where s2 =
//			 	s1 + h, we normally try to go from s1 to s2 in one step; if we
//			 	take more than one step this if statement guarantees that when we
//			 	take a step that passes s2, then we reset the stepsize so that we
//			 	end up exactly at s2 on the last step
			if ((s + h - s_end) * (s + h - s_start) > 0.)
				h = s_end - s;
//			 	this gets us a new value of the dependent values for a step that
//			 	is less than or equal to h, but in any case has an error less
//			 	than eps. The actual size of the step is returned in the higher
//			 	storage location of array, as indicated below.
			array = rungeKuttaQualityControl(rk, Xtry, dXds_at_s, s, h, eps, Xscal);
//			 	the lower n storage positions of array contain the new dependent values
			for (int i = 0; i < n; i++)
				Xtry[i] = array[i];
			note = (int) array[n];
			hdid = array[n + 1];
			hnext = array[n + 2];
			s = array[n + 3];
			if (note == 1) {
				System.out.println(" note = 1 stepped runge kutta "); //
				break;
			}
			if (hdid == h)
				nok = nok + 1;
			else
				nbad = nbad + 1;
			if ((s - s_end) * (s_end - s_start) >= 0.) {
				for (int i = 0; i < n; i++)
					X_at_s[i] = Xtry[i];
				break;
			}
			if (Math.abs(hnext) < hmin) {
				//note = 2; // step size less than preset minimum
				System.out.println(" step size less than preset minimum in stepped runge kutta ");
				// step size too small
				h = hnext;
			}
		}
		if (nstp == maxstp + 1)
			System.out.println(" too many steps in stepped runge kutta " + nstp); //  too many steps
		for (int i = 0; i < n; i++)
			X[i] = X_at_s[i];
		return X;
	}
	
	
    /**
	 * Given a  value of X and dX/ds at s, a stepsize h, an allowed error, and a way to
	 * compute dX/ds at new points, this method will return a new value
	 * of the dependent variables X at s, where s incremented over s by an amount
	 * not more than h, but perhaps much less than h, but with an error in X of less
	 * than the desired error.  The size of the step actually taken is hdid.  If we are
	 * taking too small a step for the error we desire, we return hnext as an estimate
	 * of the step size for the next step which will still be just within our desired error.
	 *
	 * @param Xnew This is the valve of X at s
	 * @param dXds The derivative dX/ds at out initial value of the independent parameter.
	 * @param sgiven This is the initial value of the independent variable.
	 * @param htry The initial value of the stepsize to try, which is subsequently modified depending
	 *             on the estimated error and our desired error.
	 * @param eps Desired error.
	 * @param Xscal Defines the meaning of error.
	 * @return The new value the dependent values, with other things stored in array, e.g. the new s, hdid, hnext, and so on
	 */
    private static double []  rungeKuttaQualityControl(Integratable rk, double[] Xnew, double[] dXds, double sgiven, double htry, double eps, double[] Xscal){
		int n = Xnew.length;
	    double s,ssav,hh,h,temp,errmax,pgrow,pshrnk,fcor,safety,errcon;
	    double[] errvector = new double[n];
	    double[] dXsav = new double[n];
	    double[] Xsav = new double[n];
	    double Xtemp[] = new double[n];
		double hdid=0.; double hnext=0.;
		double[] array = new double[n+4];
		array[n]=0.;
	    fcor = 0.3333333333333333;
	    safety = 0.9;
	    int note = 0;
	    pgrow = -0.333333333333;
	    pshrnk = - 0.5;
	    errcon = Math.pow(safety/4.,3.);
	    ssav=sgiven;
	    for (int i = 0; i < n;i++) {
			Xsav[i] = Xnew[i];
			dXsav[i] = dXds[i];
		}
	    h = htry;
	    for (;;)
	    {
		    hh=0.5*h;
		    // first get RK4 estimate of X at s+h by taking two steps of h/2
		    // this is the first step
		    Xtemp = rungeKuttaFourthOrder(rk, Xsav,dXsav,ssav,hh);
		    s = ssav+hh;
		    rk.getDependentDerivatives(dXds,0,s);
		    // this is the second step--now we have taken two steps of h/2+h/2 = h
			Xnew = rungeKuttaFourthOrder(rk, Xtemp,dXds,s,hh);
		    s = ssav+h;
		    if (s == ssav)
		    {
				note = 1;  // if we are here then we have cut down on the stepsize until not significant
				array[n]=1.;
				break;
		    }
		    // now go to h in one step of h and compare to the two h/2 steps
		    // this allows us to make an estimate of error
		    Xtemp = rungeKuttaFourthOrder(rk, Xsav,dXsav,ssav,h);
		    errmax = 0.0;
		    for (int i=0;i < n;i++)
		    {
				Xtemp[i]=Xnew[i]-Xtemp[i];
				errvector[i]=Xtemp[i]/Xscal[i];
				temp=Math.abs(errvector[i]);
				if (errmax < temp) errmax = temp;
		    }
		    errmax = errmax/eps;
		    if(errmax > 1.0)
		    {
				// if our error is too large shrink stepsize and go back and try again
				h = safety*h*Math.pow(errmax,pshrnk);
		    }
		    else
		    {
				// if our error is ok, estimate the increase in step size
				// on the next call to get desired accuracy, and return
				hdid = h;
				if (errmax > errcon) hnext = safety*h*Math.pow(errmax,pgrow);
				else hnext = 4.0*h;
				break;  // this is how we get out of the for(;;) loop
		    }
			//end of for(;;) loop
	    }
	    
	    // have gotten desired accuracy with stepsize s, which may be less than requested,
	    // now return estimate of X at s within our error, corrected to 6th order
	    for (int i=0;i < n;i++) Xnew[i] = Xnew[i] + fcor*Xtemp[i];
		for (int i=0;i < n;i++) array[i] = Xnew[i]; //  store dependent values in first n locations of array
		array[n]=note;  // store in upper locations other variables, here is note
		array[n+1]=hdid;  //  store the actualy size of the step we took
		array[n+2]=hnext;  //  store the estimate of the next step that will be within eps error limit
		//System.out.println( " n " + n + " length array " + array.length );
		array[n+3]=s;  // store independent variable for the end step here
	    // now return new dependent value arrays plus other information in array
	    return array;
    }
	
	/**
	 * This is the basic fourth order RungeKutta stepping routine.
	 * Given the value X of the dependent variables at s, and a
	 * step size h, and the ability to evaluate the derivatives
	 * dX/ds at any s, the routine returns an estimate of the value
	 * of X at s + h.
	 *
	 * @param X_at_s This is the initial value of X at the starting value of s.
	 * @param dXds_at_s This is the initial value of the derivatives dX/ds at the
	 * starting value of s.  This derivative must be provided to
	 * the routine.
	 * @param s_start The initial value of the independent variable.
	 * @param h The step size.
	 * @return An estimate of the value of the dependent variables X at
	 * s + h.
	 */
	private static double[] rungeKuttaFourthOrder(Integratable rk, double[] X_at_s, double[] dXds_at_s,
												  double s_start, double h)
	{
		int n = X_at_s.length;
		double sh;
		double[] X_at_sph = new double[n];
		double[] Xt = new double[n];
		double[] dXt = new double[n];
		double[] dXm = new double[n];
		double h6 = h/6.;
		double hh = h/2.;
		for (int i=0;i < n;i++) Xt[i] = X_at_s[i]+hh*dXds_at_s[i];
		sh = s_start + hh;
		rk.setDependentValues( Xt,0 );
		rk.getDependentDerivatives(dXt,0,sh);
		for (int i=0;i < n;i++) Xt[i] = X_at_s[i] + hh*dXt[i];
		rk.setDependentValues( Xt,0 );
	    rk.getDependentDerivatives(dXm,0,sh);
		for (int i=0;i < n;i++)
		{
			Xt[i] = X_at_s[i]+h*dXm[i];
			dXm[i] = dXt[i]+dXm[i];
		}
		rk.setDependentValues( Xt,0 );
	    rk.getDependentDerivatives(dXt,0,s_start+h);
		for (int i=0;i < n;i++)
			X_at_sph[i] = X_at_s[i] + h6 * (dXds_at_s[i] +dXt[i] + 2.*dXm[i]);
		rk.setDependentValues (X_at_s,0);  // make sure we return things to the way they were before returning
		return X_at_sph;
	}
}
