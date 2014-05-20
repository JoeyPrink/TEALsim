/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FluxFieldLine.java,v 1.71 2010/03/23 15:20:57 pbailey Exp $ 
 * 
 */

package teal.sim.spatial;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import teal.core.*;
import teal.field.Field;
import teal.sim.engine.EngineRendered;
import teal.sim.engine.TSimEngine;
import teal.physics.em.*;
import teal.util.TDebug;


/**
 * The FluxFieldLine class represents a specialized fieldline that aims to model fieldline motion in a physically
 * plausible manner (see Field Line Motion, Belcher & Olbert).  While regular FieldLines (and RelativeFLines) are built from a static position (or static position
 * relative to some other object), FluxFieldLines continually update their own positions based on the solutions to a 
 * scalar "flux function", such that as the scene evolves, we render the SAME fieldlines (in a physical sense) 
 * regardless of the change in field topology.  In contrast, a regular FieldLine simply renders whatever fieldline 
 * happens to intersect its "position" value at any given moment.
 * 
 * Conceptually, this is done by taking the fieldlines to be isocontours of the scalar flux field.  By assigning a 
 * scalar "flux value" (corresponding to our desired isocontour) to each fieldline, we can perform a root searching 
 * algorithm across some subset  of the space, and isolate the "zero" of the flux function that corresponds to the 
 * isocontour we are looking for. The position where the isocontour intersects our search space represents the 
 * new position of our fieldline.  We effectively "track" the isocontour as the field evolves, and update our 
 * fieldline accordingly.
 * 
 * Note first that in order for FluxFieldLines to work properly, every EMObject in your scene MUST HAVE A FLUX FIELD
 * DEFINED for it.  getBFlux() (or getEFlux()) must return a physically meaningful result.
 * 
 * Second, note that building FluxFieldLines is tricky business, and requires tuning many parameters to fit the 
 * situation at hand (one size does not fit all).  However, this is admittedly a rough implementation, and could 
 * possibly be streamlined in the future.
 */
public class FluxFieldLine extends FieldLine implements HasReference {

    private static final long serialVersionUID = 4121128121891631159L;
    public static final boolean SEARCH_FORWARD = true;
    public static final boolean SEARCH_BACK = false;
    public static final boolean SEARCH_LINE = true;
    public static final boolean SEARCH_CIRCLE = false;

    public static final int CIRCLE_SEARCH_UP = 1;
    public static final int CIRCLE_SEARCH_DOWN = -1;
    
    protected int circleSearchStart;

    protected boolean searchDir;
    protected boolean searchMode;

    protected double fluxValue;
    protected Referenced refObj;
    protected Vector3d objPos;
    protected Vector3d searchAxis;
    protected double objRadius;
    protected Vector3d objDir;

    protected int searchIntervals;
    protected int searchSubIntervals;
    
    protected double searchEpsilon;

    public FluxFieldLine()

    {

        this(0.0, SEARCH_FORWARD, SEARCH_LINE);
        refObj = null;
        objPos = new Vector3d(0, 0, 0);

        setObjDir(new Vector3d(0., 1., 0.));
        setSearchAxis(new Vector3d(1., 0., 0.));

    }

    public FluxFieldLine(double fluxVal, boolean searchDirection, boolean searchMode) {
        super(Field.B_FIELD);
        fluxValue = fluxVal;
        searchDir = searchDirection;
        this.searchMode = searchMode;
        searchIntervals = 100;
        searchSubIntervals = 100;
        searchEpsilon = 1e-4;
        circleSearchStart = CIRCLE_SEARCH_UP;
    }

    public FluxFieldLine(double fluxVal, Referenced ref) {
        this(fluxVal, SEARCH_FORWARD, SEARCH_LINE);
        setReference(ref);

    }

    public FluxFieldLine(double fluxVal, Referenced ref, boolean searchdir) {
        this(fluxVal, searchdir, SEARCH_LINE);
        setReference(ref);

    }

    public FluxFieldLine(double fluxVal, Referenced ref, boolean searchdir, boolean searchmode) {
        this(fluxVal, searchdir, searchmode);
        setReference(ref);

    }

    public FluxFieldLine(double fluxVal, Vector3d startPoint, Vector3d direction, double radius) {
        this(fluxVal, SEARCH_FORWARD, SEARCH_LINE);
        setReference(null);
        objPos = startPoint;
        searchAxis = direction;
        objRadius = radius;

    }
    
    public void setSimEngine(TSimEngine model) {
        super.setSimEngine(model);
        nextSpatial();
        
    }

    /**
     * Returns the current fluxValue of this FluxFieldLine
     */
    public double getFluxValue() {
        return fluxValue;
    }

    /**
     * Sets the current fluxValue of this FluxFieldLine to argument newFlux
     * 
     * @param newFlux
     */
    public void setFluxValue(double newFlux) {
        fluxValue = newFlux;
    }

    /**
     * Adds or sets the current Referenced object.
     *
     */
    public void addReference(Referenced elm) {
        setReference(elm);
    }

    /**
     * removes or clears the current Referenced object.
     *
     */
    public void removeReference(Referenced elm) {
        if ((elm != null) && (refObj != null) && (elm == refObj)) {
            refObj.removeReferent(this);
            refObj = null;
        }
    }

    public Referenced getReference() {
        return refObj;
    }

    public void setReference(Referenced elm) {
        if (refObj != null) {
            refObj.removeReferent(this);
        }
        refObj = elm;
        if (elm != null) {
            refObj.addReferent(this);
            objRadius = findObjRadius(elm);
        } else objRadius = -1.;
    }

    /**
     * Returns the value of member variable objPos.  This value represents either the position of the Referenced object
     * assigned to this FluxFieldLine, or simply the value of objPos set directly in the case of no Referenced object.
     * 
     * @return objPos
     */
    public Vector3d getObjPos() {
        return objPos;
    }

    /**
     * Sets the value of member variable objPos to argument newPos.  This method is called automatically at each simulation
     * step if there is a Referenced object associated with the FluxFieldLine.  If there is no Referenced object, you
     * can call this method directly to set the starting position for the root search (the search will then proceed FROM
     * objPos IN THE DIRECTION OF searchAxis FOR A DISTANCE OF objRadius).
     * 
     * @param newPos
     */
    public void setObjPos(Vector3d newPos) {
        objPos = newPos;
    }

    /**
     * Returns the value of searchAxis.  See setSearchAxis().
     * 
     * @return searchAxis
     */
    public Vector3d getSearchAxis() {
        return searchAxis;
    }

    /**
     * Sets searchAxis.  searchAxis represents the primary root search axis, which is defined 
     * slightly differently depending on the type of search you're doing:
     * 
     * - if searching about an object using "SEARCH_LINE", searchAxis is the line along which the 
     * 	 search takes place (ie. from the center to the edge of a ring of current).  This is typically
     *   the local x-direction of the object.
     * - if searching about an object using "SEARCH_CIRCLE", searchAxis is used in conjunction with
     *   objDir to define the coordinate system in which the circular search is taking place.
     *   (ie. if searching around a ring of current, objDir is the direction of the ring, and 
     *   searchAxis is a line from the center to the edge of the ring).  It is the local x-direction
     *   of the object.
     * - if searching between two points (no object), searchAxis is simply the vector pointing from 
     *   objPos (the start position when there is no object) along the line to be searched.  In other 
     * 	 words, the direction of the search.
     * 
     * @param newAxis
     */
    public void setSearchAxis(Vector3d newAxis) {
        newAxis.normalize();
        searchAxis = newAxis;
    }

    /**
     * Returns the value of objRadius.  See setObjRadius().
     * @return objRadius
     */
    public double getObjRadius() {
        return objRadius;
    }

    /**
     * Sets objRadius.  objRadius is defined in two ways, depending on whether a Referenced object
     * is being used or not.  If a Referenced object IS being used (ie. searching around a ring or
     * magnet), objRadius is the radius of that object, which tells the root searching algorithm how
     * far out from the center of the object to search.
     * 
     * If a Referenced object is NOT being used, objRadius represents the distance to search along
     * searchAxis.
     * 
     * @param newRadius
     */
    public void setObjRadius(double newRadius) {
        objRadius = newRadius;
    }

    /**
     * Returns the value of searchDir.  See setSearchDir().
     * 
     * @return searchDir
     */
    public boolean getSearchDir() {
        return searchDir;
    }

    /**
     * Sets searchDir.  searchDir controls the direction in which the root search will take place.
     * In the case of SEARCH_LINE, this means either forwards or backwards along searchAxis (ie.
     * from the center of a ring to the positive-x edge, or to the negative-x edge).
     * In the case of SEARCH_CIRCLE, this means either clockwise or counterclockwise around the 
     * circle.
     * @param newDir
     */
    public void setSearchDir(boolean newDir) {
        searchDir = newDir;
    }

    /**
     * Returns the value of searchMode.  See setSearchMode().
     * @return searchMode
     */
    public boolean getSearchMode() {
        return searchMode;
    }

    /**
     * Sets the value of searchMode to argument newMode.  searchMode currently has two possible values, corresponding to
     * the final static ints SEARCH_LINE and SEARCH_CIRCLE.  SEARCH_LINE indicates that the root searching algorithm will 
     * look along a line in space, while SEARCH_CIRCLE will look along a circular path.  The latter is typically only defined
     * when using a Referenced object.  The FluxFieldLine will calculate a circular path about the object on which to search
     * for roots.  The SEARCH_LINE mode can be used both with or without a Referenced object.  If a Referenced object exists,
     * the FluxFieldLine calculates a line along which to search relative to the object (typically starting at the object 
     * and extending out to its radius along its local x-direction).  If a Referenced object doesn't exist, the SEARCH_LINE
     * mode is used according to the rules described in setObjPos().
     * 
     * @param newMode
     */
    public void setSearchMode(boolean newMode) {
        searchMode = newMode;
    }

    /**
     * Returns the value of objDir.  See setObjDir().
     * @return objDir
     */
    public Vector3d getObjDir() {
        return objDir;
    }

    /**
     * Sets the value of objDir to argument newObjDir.  The value objDir is only meaningful when a Referenced object is
     * set for this FluxFieldLine.  It represents the direction that object is pointing, and is updated at each simulation
     * step by calling getDirection() on the object.  Otherwise, this value is not used.
     * 
     * @param newObjDir
     */
    public void setObjDir(Vector3d newObjDir) {
        objDir = newObjDir;
    }

    /**
     * Returns the value of searchIntervals.  See setBrakSteps().
     * @return searchIntervals
     */
    public int getSearchIntervals() {
        return searchIntervals;
    }

    /**
     * Sets the value of searchIntervals to argument steps.  The value of searchIntervals represents the number of steps used by the
     * root finding method "bracket" a root on the search interval.  The TEALRoots() method divides the interval
     * into searchIntervals intervals to find a root.  By comparing the flux value calculated at each sub-interval to that
     * calculated at its neighbors, the algorithm can determine if a "zero crossing" (ie. root) has occured in that 
     * sub-interval.  It then further refines the search on that interval.  
     * 
     * The default value for searchIntervals is 100.  In principle, searchIntervals needn't be much larger than this, but you may 
     * run in to a situation where your search interval is very long and/or your flux function is funky enough that you
     * get multiple "zero-crossing" per sub-interval.  In this case you might want to increase searchIntervals to compensate.
     * 
     * @param steps
     */
    public void setSearchIntervals(int steps) {
        searchIntervals = steps;
    }

    /**
     * Returns the value of searchSubIntervals.  See setBrentSteps().
     * @return searchSubIntervals
     */
    public int getSearchSubIntervals() {
        return searchSubIntervals;
    }

    /**
     * Sets the value of searchSubIntervals to argument steps.  This represents the number of sub-intervals used in refining
     * a root search, used in a previous implementation of the root searching algorithm.  Currently not used.
     * 
     * @param steps
     */
    public void setSearchSubIntervals(int steps) {
        searchSubIntervals = steps;
    }

    public int getCircleSearchStart() {
        return circleSearchStart;
    }

    /**
     * Sets circleSearchStart.  When using SEARCH_CIRCLE about an object, circleSearchStart controls
     * whether the search starts at 0 degrees (ie. "above" the object) or 180 degrees (ie. "below"
     * the object).
     * 
     * Note that this is different from searchDir, which, in this case, would control whether the 
     * search moves clockwise or counterclockwise, starting from circleSearchStart.
     * 
     * @param start
     */
    public void setCircleSearchStart(int start) {
        circleSearchStart = start;
    }

    public void nextSpatial() {
        if (isDrawn) {
            //find root
            //set position

            Vector3d newPos = null;

            newPos = findRoot();

            if (newPos == null) {
                //TDebug.println(0,"begin if root not found...");
                showNode = false;
                renderFlags |= VISIBILITY_CHANGE;

            }

            else {
                setPosition(newPos);
                TDebug.println(2,"nextSpatial Root" + newPos.toString());
                showNode = true;
                renderFlags |= VISIBILITY_CHANGE;
                super.nextSpatial();

            }
        }
    }

    public double findObjRadius(Referenced refObj) {
        double r = objRadius; 
        if (refObj != null) {

            if (refObj instanceof RingOfCurrent) {
                r = ((RingOfCurrent) refObj).getRadius();
            } else if (refObj instanceof Dipole) {
                if (refObj instanceof LineMagneticDipole) {
                	r = ((Dipole) refObj).getLength() *1.5;
                } else {
                	r = ((Dipole) refObj).getLength() / 4.;
                }
            } else if (refObj instanceof PointCharge) {
                r = ((PointCharge) refObj).getRadius();
            } else {
                r = -1.0;
            }
        }
        return r;
    }

    protected Vector3d findRoot() {
        if (refObj != null) {
            // this is what should happen if there is a refObject
        	if (refObj instanceof teal.sim.engine.EngineRendered) {
                //	    		set objPos and find searchAxis
                setObjPos(((EngineRendered) refObj).getPosition());

                Vector3d zprime = new Vector3d(((teal.sim.engine.EngineRendered) refObj).getDirection());
                
                // fluxValue should flip sign if the ring is pointing downwards
                // not sure how to deal with this yet

                Vector3d xprime = new Vector3d();
                zprime.normalize();
                Vector3d temp = new Vector3d();
                temp.cross(zprime, new Vector3d(0, 1, 0));
                // simple way to fix flipping problem, there may be a more sound method to find this axis
                if (temp.z < 0.0) temp.scale(-1.0);

                if (temp.length() == 0.0) {
                    xprime.set(1., 0., 0.);
                } else {
                    xprime.cross(zprime, temp);
                    xprime.normalize();
                }

                setSearchAxis(xprime);
                setObjDir(zprime);
                ////////////////////////////////
                if (objRadius <= 0.) objRadius = findObjRadius(refObj);

            }

        } else {
            // this really shouldn't do anything on the grounds that if we're not using a refObject
            // we're not updating the searchAxis or anything
        }

        // Commence root search!  
        // Only search out to 0.99*objRadius to avoid singularities (particularly with RingOfCurrent)
        //double root1d = certRt(0.0, 0.99 * objRadius, searchIntervals);
        
        double[] roots1d = TEALRoots(0.0,0.99*objRadius, searchIntervals);
        double root1d = roots1d.length > 0 ? roots1d[0] : -1.;
        //System.out.println("FluxFieldLine " + this + " searching for: " + this.fluxValue + " CertRt: " + root1d + " TEALRoots: " + troot1d + " DIFF: " + (root1d-troot1d));
        //root1d *= 0.1;
  
        if (root1d < 0.0) {
            //TDebug.println(0,"Root not found: " + fluxValue);
            return null;
        } else {
            Vector3d root3d = mapTo3D(root1d);
            return root3d;
        }

    }

    /**
     * This method transforms the one dimensional position x in "root finding space" to its corresponding position in
     * 3d space, typically a point along a line or arc.  You would want to override or modify this method if you 
     * wanted to develop different root searching intervals.
     *   
     * @param x the one dimensional position in "root finding space".
     */
    private Vector3d mapTo3D(double x) {
        // Convert 1D "root-finding space" position x to a worldspace Vector3d

        //if we're looking on a line:
        if (getSearchMode() == SEARCH_LINE) {
            Vector3d pos = new Vector3d(getSearchAxis());
            if (getSearchDir() == SEARCH_FORWARD) {
                pos.scale(x);
            } else {
                pos.scale(-x);
            }
            pos.add(getObjPos());
            return pos;
        } else // Circle Mode
        {
            
            Vector3d xprime = new Vector3d(getSearchAxis());
            Vector3d zprime = new Vector3d(getObjDir());
            double fraction = (x / (0.99 * getObjRadius())) * Math.PI;
            if (getSearchDir() == true) {
                xprime.scale(Math.sin((fraction))); //- Math.PI * 0.5)));
                zprime.scale(circleSearchStart * -Math.cos((fraction))); //- Math.PI * 0.5)));
                //xprime.scale(Math.cos((fraction ))); //- Math.PI * 0.5)));
                //zprime.scale(-Math.sin((fraction ))); //- Math.PI * 0.5)));
            } else {
                xprime.scale(-Math.sin((fraction))); // - Math.PI * 0.5)));
                zprime.scale(circleSearchStart * -Math.cos((fraction))); // - Math.PI * 0.5)));
                //xprime.scale(Math.cos((fraction ))); //- Math.PI * 0.5)));
                //zprime.scale(-Math.sin((fraction ))); //- Math.PI * 0.5)));
            }
            //TDebug.println(0,"xprime = " + xprime + " zprime = " + zprime);
            Vector3d circle = new Vector3d();
            circle = xprime;
            circle.add(zprime);
            circle.scale(0.99 * getObjRadius());
            circle.add(getObjPos());
            return circle;
            
        }

    }

    // ROOT FINDING CODE
    
    private double[] TEALRoots(double xs, double xf, int intervals) {
    	double x1, x2, deltax, f1, f2, root;
    	ArrayList<Double> roots = new ArrayList<Double>();
    	Double[] roots_Double;
    	double[] roots_double;
    	double close = searchEpsilon;
    	x1 = xs;
    	deltax = (xf-xs)/((double)intervals);
    	f1 = field.getFlux(mapTo3D(x1)) - fluxValue;
    	for (int i = 0; i < intervals; i++) {
    		x2 = x1 + deltax;
    		f2 = field.getFlux(mapTo3D(x2))- fluxValue;
            TDebug.println(2,"FluxFieldLine Search:  i " +i+ " x1 " + x1 + " f1 " +f1 + " flux " + fluxValue + " VectorPos " + mapTo3D(x1).toString()) ;
    		if (f1*f2 < 0.) {
    			root = TEALNewtonRaphson(x1,x2,close);
    			roots.add(new Double(root));
    		}
    		f1 = f2;
    		x1 = x2;
    	}
    	roots_Double = new Double[roots.size()];
    	roots_Double = (Double[])roots.toArray(roots_Double);
    	roots_double = new double[roots_Double.length];
    	for (int i = 0; i < roots_double.length; i++) {
    		roots_double[i] = roots_Double[i].doubleValue();
    	}
    	return roots_double;
    }
    
    private double TEALNewtonRaphson(double x1, double x2, double close) {
    	double xguess, xplus, xminus, tolerance, deltax, fxguess, fplus, fminus, fderivative, xnewguess;
    	double eps = 0.001;//0.000003
    	deltax = (x2-x1)*eps;
    	xguess =(x1+x2)/2;
    	double xcompare = xguess;
    	int nloop = 0;
    	tolerance = 1.;
    	while (tolerance > close && nloop < 10) {
    		xplus = xguess + deltax;
    		xminus = xguess - deltax;
    		// evaluate derivative at center of interval using central difference approximation
    		fxguess = field.getFlux(mapTo3D(xguess))- fluxValue;
    		fplus = field.getFlux(mapTo3D(xplus))- fluxValue;
    		fminus = field.getFlux(mapTo3D(xminus))- fluxValue;
    		
    		fderivative = (fplus - fminus)/(2*deltax);
    		// get estimate of zero of function using Newton Raphson
    		xnewguess = xguess - fxguess/fderivative;
    		tolerance = Math.abs(xnewguess - xguess);
    		
    		tolerance /= xcompare;
    		
    		xguess = xnewguess;
    		nloop++;
    		
    	}
    	return xguess;
    }

	/**
	 * @return Returns the searchEpsilon.
	 */
	public double getSearchEpsilon() {
		return searchEpsilon;
	}

	/**
	 * @param searchEpsilon The searchEpsilon to set.
	 */
	public void setSearchEpsilon(double searchEpsilon) {
		this.searchEpsilon = searchEpsilon;
	}
    
}
