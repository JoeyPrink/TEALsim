/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Circuit.java,v 1.27 2010/04/12 20:13:17 stefan Exp $ 
 * 
 */

package teal.physics.em;

import java.awt.*;
import java.util.*;

import javax.vecmath.*;

import teal.app.*;
import teal.config.*;
import teal.math.*;
import teal.render.j3d.*;
import teal.render.j3d.geometry.Pipe;
import teal.render.scene.*;
import teal.physics.*;
import teal.sim.properties.*;
import teal.util.*;


/**
 * The teal.physics.em.Circuit class represents an idealized geometry
 * electric circuit, which is defined over an infinite length cylindrical
 * shell.
 */
public class Circuit extends EMObject implements HasRadius,GeneratesE, GeneratesB, IsSpatial {

    private static final long serialVersionUID = 3257002159626268983L;
    
    // Component lists.
	protected ArrayList<Battery> batteryList = new ArrayList<Battery>(); 
	protected ArrayList<Resistor> resistorList = new ArrayList<Resistor>(); 
	protected ArrayList<Capacitor> capacitorList = new ArrayList<Capacitor>();
	protected ArrayList<Particle> particleList = new ArrayList<Particle>();

	// Variables for numerical computation.
	protected int Nsamples = 128;
	protected double [] V = null;
	protected Complex2d [] C = null;
	protected int [] m = new int[2];
	
	// Physical properties.
	public static double mu0 = 1.;
	public static double epsilon0 = 1.;
	protected double radius = 1.;
	protected double current = 0.;
	protected double charge = 0.;
	protected double inductance = 0.;

	// Tolerance on radius.
	private final double tol = 0.;
	
	// Particle system.
	//protected TealSimApp application = null;
	public static final int EDGE = 0x01, NONEDGE = 0x02;
	protected int placementType = EDGE;
	protected double energyQuantum = 0.05;
	protected int Nparticles = 7;

	protected boolean mNeedsSpatial = false;
	
	// Circuit types.
	public static final int TYPE_R = 0x01, TYPE_L = 0x02, TYPE_C = 0x04;
	public static final int	TYPE_RL = TYPE_R | TYPE_L,
							TYPE_RC = TYPE_R | TYPE_C,
							TYPE_LC = TYPE_L | TYPE_C,
							TYPE_RLC = TYPE_R | TYPE_L | TYPE_C; 
	
	public int getCircuitType() {
		int type = 0;
		if(!resistorList.isEmpty()) type |= TYPE_R; 
		if(inductance > 0.) type |= TYPE_L; 
		if(!capacitorList.isEmpty()) type |= TYPE_C;
		return type;
	}
	
	public void needsSpatial()
	{
		mNeedsSpatial = true;
	}
	
	// ***********************************************************************
	// Aspect.
	// ***********************************************************************
	
    protected TNode3D makeNode() {
    	double thickness = radius/20.;
    	double length = radius/3.;
        
        TShapeNode node = (TShapeNode) new ShapeNode();
        node.setGeometry( Pipe.makeGeometry(32, radius, thickness, length));
        node.setColor(new Color3f(Color.ORANGE));
        node.setShininess(0.5f);
        node.setTransparency(0.5f);
        node.setRotable(false);
        return node;
        
        /*
//    	Geometry geometry1 = Cylinder.makeGeometry(32, radius, length); 
//		Appearance appearance1 = Node3D.makeAppearance(Color.ORANGE,0.5f,0.5f,false);
//		appearance1.setTransparencyAttributes(
//			new TransparencyAttributes(TransparencyAttributes.NICEST, 0.95f) );
//    	// Geometry geometry2 = Cylinder.makeGeometry(32, radius, length, false); 
    	//Geometry geometry2 = Pipe.makeGeometry(32, radius+thickness/2., thickness, length, 0.); 
        //Geometry geometry2 = Ring.makeGeometry(32, radius-thickness/2., radius+thickness/2., length);
    	Geometry geometry2 = Ring.makeGeometry(32, radius, radius+thickness, length);
		Appearance appearance2 = Node3D.makeAppearance(Color.ORANGE,0.5f,0.5f,false);
//		appearance2.setTransparencyAttributes(
//			new TransparencyAttributes(TransparencyAttributes.NICEST, 1f) );
//		ShapeNode node1= new ShapeNode();
//    	node1.setRotable(false);
//    	node1.setGeometry(geometry1);
//    	node1.setAppearance(appearance1);
    	ShapeNode node2= new ShapeNode();
    	node2.setRotable(false);
    	node2.setGeometry(geometry2);
    	node2.setAppearance(appearance2);
//    	node2.setAppearance(Node3D.makeAppearance(Color.ORANGE));
//    	MultiShapeNode node = new MultiShapeNode(2);
//    	node.setRotable(false);
//    	node.setGeometry(0, geometry1);
//    	node.setGeometry(1, geometry2);
//    	node.setAppearance(0, appearance1);
//    	node.setAppearance(1, appearance2);
    	return node2;
        */
    }
	
	// ***********************************************************************
	// Component placement and management.
	// ***********************************************************************

	public void placeComponent(Component x) {
		if(x==null) return;
		boolean vacant = true;
		Iterator it = batteryList.iterator();
		while( vacant && it.hasNext() ) {
			vacant &= !x.overlapsWith((Component) it.next());
		}
		it = resistorList.iterator();
		while( vacant && it.hasNext() ) {
			vacant &= !x.overlapsWith((Component) it.next());
		}
		it = capacitorList.iterator();
		while( vacant && it.hasNext() ) {
			vacant &= !x.overlapsWith((Component) it.next());
		}
		if(vacant) {
			if(x instanceof Battery) {
				batteryList.add((Battery)x);
				TDebug.println(1, "Battery placed successfuly." );
			}
			else if(x instanceof Resistor) {
				resistorList.add((Resistor)x);
				TDebug.println(1, "Resistor placed successfuly." );
			}
			else if(x instanceof Capacitor) {
				capacitorList.add((Capacitor)x);
				TDebug.println(1, "Capacitor placed successfuly." );
			}
			else {
				TDebug.println(1, "Invalid component type." );
			}
		} else {
			TDebug.println(1, "Position not vacant. Component " + x + " not placed." );
		}
	}

	public void removeComponent(Component x) {
		if(x instanceof Battery) {
			batteryList.remove(x);
		}
		if(x instanceof Resistor) {
			resistorList.remove(x);
		}
		if(x instanceof Capacitor) {
			capacitorList.remove(x);
		}
	}
	
	// ***********************************************************************
	// Physical property accessors.
	// ***********************************************************************

	public double getInductance() {
		return inductance;
	}

	public void setInductance(double inductance) {
		this.inductance = inductance;
	}

	public double getRadius() {
		return radius;
	}

	public double getArea() {
		return Math.PI*radius*radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setCharge(double charge) {
		switch(getCircuitType()) {
		case TYPE_R: // Charge cannot be set.
		case TYPE_L: 
		case TYPE_RL: 
			break;
		case TYPE_C: // Charge can be set to a single value.
			break;
		case TYPE_RC: // Charge can be set.
		case TYPE_LC:
		case TYPE_RLC:
			this.charge = charge;
			break;
		}
	}

	public double getCharge() {
		switch(getCircuitType()) {
		case TYPE_R:
		case TYPE_L:
		case TYPE_RL:
			charge = 0.;
			break;
		case TYPE_C:
			double E = getCircuitEMF();
			double C = getCircuitCapacitance();
			charge = E*C;
			break;
		case TYPE_RC:
		case TYPE_LC:
		case TYPE_RLC:
			break;
		}
		return charge;
	}
	
	public void setCurrent(double current) {
		switch(getCircuitType()) {
		case TYPE_R: // Current cannot be set.
		case TYPE_C:
		case TYPE_RC:
			break;
		case TYPE_L: // Current can be set.
		case TYPE_RL:
		case TYPE_LC:
		case TYPE_RLC:
			this.current = current;
			break;
		}
	}

	public double getCurrent() {
		double dE_dt,E,R,C;
		switch(getCircuitType()) {
			case TYPE_R:
				E = getCircuitEMF();
				R = getCircuitResistance();
				current = E/R;		
				break;
			case TYPE_C:
				dE_dt = getCircuitEMF();
				C = getCircuitCapacitance();
				current = C*dE_dt;
				break;
			case TYPE_RC:
				E = getCircuitEMF();
				R = getCircuitResistance();
				C = getCircuitCapacitance();
				current = (E-charge/C)/R;
				break;
			case TYPE_L:
			case TYPE_RL:
			case TYPE_LC:
			case TYPE_RLC:
				break;
		}
		return current; 
	}

	public double getInducedEMF() {
		double voltage = 0;
		double E,R,C;
		switch(getCircuitType()) {
		case TYPE_R: // No induced EMF.
		case TYPE_C:
		case TYPE_RC:
			break;
		case TYPE_L: 
		case TYPE_RL: 
			E = getCircuitEMF();
			R = getCircuitResistance();
			voltage = E-current*R;
			break;
		case TYPE_LC:
			E = getCircuitEMF();
			C = getCircuitCapacitance();
			voltage = E-charge/C;		
			break;
		case TYPE_RLC:
			E = getCircuitEMF();
			R = getCircuitResistance();
			C = getCircuitCapacitance();
			voltage = E-charge/C-current*R;		
			break;
		}
		return voltage; 
	}

	// ***********************************************************************
	// Integration methods.
	// ***********************************************************************

	protected boolean dynamic = true;
	
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
	public boolean isDynamic() {
		return dynamic;
	}
	
	public int getNumberDependentValues() {
		int number = super.getNumberDependentValues();
		Iterator it = batteryList.iterator();
		while( it.hasNext() ) {
			number += ((Integratable) it.next()).getNumberDependentValues();
		}
		it = resistorList.iterator();
		while( it.hasNext() ) {
			number += ((Integratable) it.next()).getNumberDependentValues();
		}
		it = capacitorList.iterator();
		while( it.hasNext() ) {
			number += ((Integratable) it.next()).getNumberDependentValues();
		}
		if (dynamic) {
			switch (getCircuitType()) {
			case TYPE_R: // Integrate nothing.
			case TYPE_C: // Integrate nothing, charge is given as a function of
						 // the derivative of the circuit emf.
				break;
			case TYPE_RC:  // Integrate charge.
				number += 1;
				break;
			case TYPE_L: // Integrate current.
			case TYPE_RL:
				number += 1;
				break;
			case TYPE_LC: // Integrate charge and current.
			case TYPE_RLC:
				number += 2;
				break;
			}
		}
		return number;
	}

	public void getDependentValues(double [] depValues, int offset) {
		super.getDependentValues(depValues, offset);
		offset += super.getNumberDependentValues();

		Integratable component = null;
		Iterator it = batteryList.iterator();
		while(it.hasNext()) {
			component = ((Integratable) it.next());
			component.getDependentValues(depValues, offset);
			offset += component.getNumberDependentValues();
		}
		it = resistorList.iterator();
		while(it.hasNext()) {
			component = ((Integratable) it.next());
			component.getDependentValues(depValues, offset);
			offset += component.getNumberDependentValues();
		}
		it = capacitorList.iterator();
		while(it.hasNext()) {
			component = ((Integratable) it.next());
			component.getDependentValues(depValues, offset);
			offset += component.getNumberDependentValues();
		}
		if (dynamic) {
			switch (getCircuitType()) {
			case TYPE_R: // Integrate nothing.
			case TYPE_C:
				break;
			case TYPE_RC:  // Integrate charge.
				depValues[offset++] = charge;
				break;
			case TYPE_L: // Integrate current.
			case TYPE_RL:
				depValues[offset++] = current;
				break;
			case TYPE_LC: // Integrate charge and current.
			case TYPE_RLC:
				depValues[offset++] = charge;
				depValues[offset++] = current;
				break;
			}
		}
	}

	public void setDependentValues(double[] newValues, int offset) {
		super.setDependentValues(newValues, offset);
		offset+= super.getNumberDependentValues();
		
		Integratable component = null;
		Iterator it = batteryList.iterator();
		while(it.hasNext()) {
			component = ((Integratable) it.next());
			component.setDependentValues(newValues, offset);
			offset += component.getNumberDependentValues();
		}
		it = resistorList.iterator();
		while(it.hasNext()) {
			component = ((Integratable) it.next());
			component.setDependentValues(newValues, offset);
			offset += component.getNumberDependentValues();
		}
		it = capacitorList.iterator();
		while(it.hasNext()) {
			component = ((Integratable) it.next());
			component.setDependentValues(newValues, offset);
			offset += component.getNumberDependentValues();
		}
		if (dynamic) {
			switch (getCircuitType()) {
			case TYPE_R: // Integrate nothing.
			case TYPE_C:
				break;
			case TYPE_RC: // Integrate charge.
				charge = newValues[offset++];
				break;
			case TYPE_L: // Integrate current.
			case TYPE_RL:
				current = newValues[offset++];
				break;
			case TYPE_LC: // Integrate charge and current.
			case TYPE_RLC:
				charge = newValues[offset++];
				current = newValues[offset++];
				break;
			}
			updateSurfacePotential();
		}
	}

	public void getDependentDerivatives(double[] depDerivatives, int offset, double time) {
		super.getDependentDerivatives(depDerivatives, offset, offset);
		offset+= super.getNumberDependentValues();
		
		Integratable component = null;
		Iterator it = batteryList.iterator();
		while(it.hasNext()) {
			component = ((Integratable) it.next());
			component.getDependentDerivatives(depDerivatives, offset, time);
			offset += component.getNumberDependentValues();
		}
		it = resistorList.iterator();
		while(it.hasNext()) {
			component = ((Integratable) it.next());
			component.getDependentDerivatives(depDerivatives, offset, time);
			offset += component.getNumberDependentValues();
		}
		it = capacitorList.iterator();
		while(it.hasNext()) {
			component = ((Integratable) it.next());
			component.getDependentDerivatives(depDerivatives, offset, time);
			offset += component.getNumberDependentValues();
		}

		if (dynamic) {
			double E,R,C;
			switch (getCircuitType()) {
			case TYPE_R: // Integrate nothing.
			case TYPE_C:
				break;
			case TYPE_RC: // Integrate charge.
				depDerivatives[offset++] = getCurrent(); // Current is computed.
				break;
			case TYPE_L: // Integrate current.
			case TYPE_RL:
				E = getCircuitEMF();
				R = getCircuitResistance();
				depDerivatives[offset++]= -(R*current-E)/inductance;
				break;
			case TYPE_LC: // Integrate charge and current.
			case TYPE_RLC:
				depDerivatives[offset++] = current;
				E = getCircuitEMF();
				R = getCircuitResistance();
				C = getCircuitCapacitance();
				depDerivatives[offset++]= -(R*current-E+charge/C)/inductance;
				break;
			}
		}
	}
	
	public double getCircuitEMF() {
		double temp = 0.;
		Iterator it = batteryList.iterator();
		while(it.hasNext() ) {
			temp += ((Battery) it.next()).getEMF();
		}
		return temp;
	}
	
	public double getCircuitEMFDerivative(double time) {
		double temp = 0.;
		Iterator it = batteryList.iterator();
		while(it.hasNext() ) {
			temp += ((Battery) it.next()).getEMFDerivative(time);
		}
		return temp;
	}

	public double getCircuitEMFSecondDerivative(double time) {
		double temp = 0.;
		Iterator it = batteryList.iterator();
		while(it.hasNext() ) {
			temp += ((Battery) it.next()).getEMFSecondDerivative(time);
		}
		return temp;
	}

	public double getCircuitResistance() {
		double temp = 0.;
		Iterator it = resistorList.iterator();
		while(it.hasNext() ) {
			temp += ((Resistor) it.next()).getResistance();
		}
		return temp;
	}

	public double getCircuitCapacitance() {
		double temp = 0.;
		Iterator it = capacitorList.iterator();
		while(it.hasNext() ) {
			temp += 1./((Capacitor) it.next()).getCapacitance();
		}
		return (temp>0.)?(1./temp):Double.POSITIVE_INFINITY;
	}

	public void update() {
	    super.update();
	    theEngine.requestSpatial();
	    theEngine.requestRefresh();
	    //inform();
	}
	
	public void inform() {
	    /*
		double capacitance = 0.;
	    double Vc = 0.;
	    Iterator it = capacitorList.iterator();
		while(it.hasNext()) {
			Capacitor cap = ((Capacitor) it.next());
			capacitance+=1./cap.getCapacitance();
			Vc+=cap.getVoltage();
		}
		capacitance=1./capacitance;
	    double charge = Vc*capacitance; // V_{c}=\frac{\lambda,C} \therefore \lambda=V_{c}C
	    double current = getCurrent();
	    
	    
	    double U = 0.5*(inductance*current*current+charge*charge/capacitance); 
	    System.out.println("U = " + U);

	    double integral_u = doubleIntegrate();
	    System.out.println("integral_u = " + integral_u);
	    */
		
		System.out.println("--------------------------------------------------------------------------");
		System.out.println("* Resistors:");
		Iterator it2 = resistorList.iterator();
		int i = 0;
		while(it2.hasNext()) {
			i++;
			Resistor resistor = (Resistor) it2.next();
			System.out.println("Resistor"+i+" has absorbed "+resistor.particleCount+" quanta of energy.");
			System.out.println("Resistor"+i+" has dissipated "+resistor.getEnergy() +" amount of energy.");
		}
		System.out.println("* Capacitors:");
		it2 = capacitorList.iterator();
		i = 0;
		while(it2.hasNext()) {
			i++;
			Capacitor capacitor = (Capacitor) it2.next();
			if(capacitor.particleCount>0) {
				System.out.println("Capacitor"+i+" has absorbed "+capacitor.particleCount+" quanta of energy.");
			} else {
				System.out.println("Capacitor"+i+" has released "+(-capacitor.particleCount)+" quanta of energy.");
			}
			System.out.println("Capacitor"+i+" has done an amount "+capacitor.getEnergy()+" of work.");
			System.out.println("Capacitor"+i+" has voltage "+(charge/capacitor.getCapacitance())+" volts.");
		}
		System.out.println("* Batteries:");
		it2 = batteryList.iterator();
		i = 0;
		while(it2.hasNext()) {
			i++;
			Battery battery = (Battery) it2.next();
			if(battery.particleCount>0) {
				System.out.println("Battery"+i+" has released "+battery.particleCount+" quanta of energy.");
			} else {
				System.out.println("Battery"+i+" has absorbed "+(-battery.particleCount)+" quanta of energy.");
			}
			System.out.println("Battery"+i+" has released "+battery.particleCount+" quanta of energy.");
			System.out.println("Battery"+i+" has done an amount "+ battery.getEnergy()+" of work.");
		}

		//  __________________________________________________________
		// /   Computation of field energies from the energy quanta.  \
/*		double magneticEnergy = 0.;
		Iterator it2 = particleList.iterator();
		while(it2.hasNext()) {
			EnergyParticle particle = (EnergyParticle) it2.next();
			magneticEnergy += energyQuantum*particle.getMagneticEnergyRatio();
		}
		System.out.println("Particles' magnetic energy: "+ magneticEnergy + " Inductor energy: " + (0.5*inductance*current*current) );
*/		// \__________________________________________________________/

	}
	
	// ***********************************************************************
	// Numerical computation methods.
	// ***********************************************************************

	public void setNsamples(int Nsamples) {
		this.Nsamples = Nsamples;
		updateSurfacePotential();
	}
	
	public int getNsamples() {
		return Nsamples;
	}
	
	public double [] getSurfacePotential() {
		if(V==null) {
			updateSurfacePotential();
		}
//		double [] C_ = new double[Nsamples];
//		for(int i=0; i<Nsamples; i++) {
//			C_[i] = C[i].getAbs();
//		}
//		return C_;
		return V;
	}		
	
	public void updateSurfacePotential() {
		current = getCurrent();

		TDebug.println(1, "Current = " + current );

		V = new double[Nsamples];
		
		Iterator it = batteryList.iterator();
		while(it.hasNext() ) {
			Battery x = (Battery) it.next();
			x.influence(V);
		}
		
		it = resistorList.iterator();
		while(it.hasNext() ) {
			Resistor x = (Resistor) it.next();
			x.influence(V);
		}
		
		it = capacitorList.iterator();
		while(it.hasNext() ) {
			Capacitor x = (Capacitor) it.next();
			x.influence(V);
		}

		// Note: Angle 0 is always grounded.
		{	
			double t1 = 0;
			double t2 = 2.*Math.PI;
			double VL = getInducedEMF();
			double N = V.length;
			for(double n=0; n<N; n++) {
				double t=(2.*Math.PI)*n/N;
				V[(int)n]+=(Fourier.step(t-t1)-Fourier.step(t-t2))*(-VL*(t-t1)/(t2-t1));
			}
		}		
		
//		System.out.println("Induced EMF: " + getInducedEMF());
		
		// Optimization
		// C = Fourier.dft(V);
		C = Fourier.optimized_dft(V, m);
		// System.out.println("m[0] = " + m[0] + " m[1] = " + m[1]);
		
	}


	// ***********************************************************************
	// Electric field methods.
	// ***********************************************************************

	public Vector3d getE(Vector3d pos) {

		if(V==null) {
			getSurfacePotential();
		}
		
		Vector3d direction = getDirection();
		Quat4d orientation = getOrientation();
		Matrix3d forward = new Matrix3d();
		forward.set(orientation);
		Matrix3d backward = new Matrix3d(forward);
		backward.invert();

		Vector3d relativePosition = new Vector3d(pos);
		relativePosition.sub(position);
		double projection = relativePosition.dot(direction);
		Vector3d kernel = new Vector3d(direction);
		kernel.scale(projection);
		relativePosition.sub(kernel);
		backward.transform(relativePosition);

		double x = relativePosition.x;
		double y = -relativePosition.z;
		
//		System.out.println( "x: " + x);
//		System.out.println( "y: " + y);
		
		double r = relativePosition.length();
		double t = Math.atan2(y, x);

		if( r < 1e-10 ) {
			Vector3d pos_ = new Vector3d(pos);
			pos_.add(new Vector3d(1e-5,1e-5,1e-5));
			return getE(pos_);
		}

		double s = (radius-r)>=0.?1.:-1;
		Complex2d [] CEr = new Complex2d [Nsamples];
		Complex2d [] CEt = new Complex2d [Nsamples];
		int M = (int) Math.ceil((double)Nsamples/2.);
		for(int i=0; i<M; i++) {
			double n = i;
			double s_absn = s*Math.abs(n);
			double alphaV = Math.pow(r/radius,s_absn);
			CEr[i] = new Complex2d(C[i]);
			CEr[i].scale(-s_absn*alphaV/r);
			CEt[i] = new Complex2d(C[i]);
			CEt[i].mul(new Complex2d(0.,-n*alphaV/r));
		}
		for(int i=M; i<Nsamples; i++) {
			double n = i-Nsamples;
			double s_absn = s*Math.abs(n);
			double alphaV = Math.pow(r/radius,s_absn);
			CEr[i] = new Complex2d(C[i]);
			CEr[i].scale(-s_absn*alphaV/r);
			CEt[i] = new Complex2d(C[i]);
			CEt[i].mul(new Complex2d(0.,-n*alphaV/r));
		}
		
		// double Er = Fourier.idft(CEr, (double) t).real();
		// double Et = Fourier.idft(CEt, (double) t).real();
		// Optimization
		double Er = Fourier.optimized_idft(CEr, (double) t, m).real();
		double Et = Fourier.optimized_idft(CEt, (double) t, m).real();

//		System.out.println( "I = " + current );
//		System.out.println( "r = " + r + " t = " + t);
//		System.out.println( "Er = " + Er + " Et = " + Et);
//		System.out.println( "Er_ = " + (current*Math.sin(t)) + " Et_ = " + (current*Math.cos(t)));
//		System.out.println( "Er_R = " + (current*Math.sin(t)/Er) + " Et_R = " + (current*Math.cos(t)/Et));
				
		double Ex = Er*Math.cos(t)-Et*Math.sin(t);
		double Ey = Er*Math.sin(t)+Et*Math.cos(t);
//		System.out.println( "Ex = " + Ex);
//		System.out.println( "Ey = " + Ey);
		
		Vector3d Enocurl = new Vector3d(Ex, 0, -Ey);

		// Current derivative computation.
		double dI_dt= 0.;
		double EMF,dEMF_dt,d2EMF_dt2,R,C;
		switch(getCircuitType()) {
			case TYPE_R:
				dEMF_dt = getCircuitEMFDerivative(theEngine.getTime());
				R = getCircuitResistance();
				dI_dt = dEMF_dt/R;
				break;
			case TYPE_C:
				d2EMF_dt2 = getCircuitEMFSecondDerivative(theEngine.getTime());
				C = getCircuitCapacitance();
				dI_dt = C*d2EMF_dt2;
				break;
			case TYPE_RC:
				dEMF_dt = getCircuitEMFDerivative(theEngine.getTime());
				R = getCircuitResistance();
				C = getCircuitCapacitance();
				dI_dt = (dEMF_dt-current/C)/R;
				break;
			case TYPE_RL:
				EMF = getCircuitEMF();
				R = getCircuitResistance();
				dI_dt = -(R*current-EMF)/inductance;
				break;
			case TYPE_RLC:
				EMF = getCircuitEMF();
				R = getCircuitResistance();
				C = getCircuitCapacitance();
				dI_dt = -(R*current-EMF+charge/C)/inductance;
				break;
		}

		double EtFaraday = 0.;
		if(r<=(1.+tol)*radius) {
			EtFaraday = -mu0*r*dI_dt/2.;
		} else {
			EtFaraday = -mu0*(radius*radius)*dI_dt/r/2.;
		}

/*		boolean batteryReleased = false;
		Iterator it1 = batteryList.iterator();
		while(it1.hasNext()) {
			batteryReleased |= ((Battery) it1.next()).contains(t); 
		}				
		boolean resistorAbsorbed = false;
		Iterator it2 = resistorList.iterator();
		while(it2.hasNext()) {
			resistorAbsorbed |= ((Resistor) it2.next()).contains(t); 
		}				
		if( Math.abs(r-radius) < 0.01*radius && !batteryReleased && !resistorAbsorbed) {
			System.out.println( "Dangerous particle at r = " + r + " t = " + (t/Math.PI) + " pi-rads: ");
			System.out.println( "* E_{\\theta Coulomb} = " + Et );  
			System.out.println( "* E_{\\theta Faraday} = " + EtFaraday );
		}
*/
		Vector3d Enodiv = new Vector3d(-Math.sin(t), 0, -Math.cos(t));
		Enodiv.scale(EtFaraday);
//		System.out.println("Enocurl: " + Enocurl);
//		System.out.println("Enodiv: " + Enodiv);
		
		Vector3d E = new Vector3d();
		E.add(Enocurl);
		E.add(Enodiv);
		forward.transform(E);
		
		return E;
		
//		return new Vector3d(pos.x,pos.y,pos.z);
	}
	
	public Vector3d getE(Vector3d pos, double time) {
		return getE(pos);
	}
	
	public double getEFlux(Vector3d pos){
		double EFlux = 0.;
		return EFlux;
	}
	
	public boolean isGeneratingE() {
		return true;
	}

	// Returns the electric potential.
    public double getEPotential(Vector3d pos) {

		if(V==null) {
			getSurfacePotential();
		}
		
		Vector3d direction = getDirection();
		Quat4d orientation = getOrientation();
		Matrix3d forward = new Matrix3d();
		forward.set(orientation);
		Matrix3d backward = new Matrix3d(forward);
		backward.invert();

		Vector3d relativePosition = new Vector3d(pos);
		relativePosition.sub(position);
		double projection = relativePosition.dot(direction);
		Vector3d kernel = new Vector3d(direction);
		kernel.scale(projection);
		relativePosition.sub(kernel);
		backward.transform(relativePosition);
		
		double x = relativePosition.x;
		double y = -relativePosition.z;
		
//		System.out.println( "x: " + x);
//		System.out.println( "y: " + y);
		
		double r = relativePosition.length();
		double t = Math.atan2(y, x);
		
		double s = (r-radius)>=0.?1.:-1;
		Complex2d [] CV = new Complex2d [Nsamples];
		int M = (int) Math.ceil((double)Nsamples/2.);
		for(int i=0; i<Nsamples; i++) {
			double n = (i<M)?i:i-Nsamples;
			CV[i] = new Complex2d(C[i]);
			CV[i].scale(Math.pow(radius/r, s*Math.abs(n)));
//			System.out.println( n);
		}
//		System.out.print( "CV = [");
//		for(int i=0; i<N; i++) {
//			System.out.print(CV[i].real()+"+"+CV[i].imag()+"i, ");
//		}
//		System.out.println();
		
		
//		System.out.println( "t = " + t + " t_ = " + t_ );
		
		// double V = Fourier.idft(CV, (double) t).real();
		// Optimization
		double V = Fourier.optimized_idft(CV, (double) t, m).real();
//		System.out.println( "V = " + V);
				
		return V;
		
    }

	// ***********************************************************************
	// Magnetic field methods.
	// ***********************************************************************
	public Vector3d getB(Vector3d pos) {
		Vector3d direction = getDirection();
		Quat4d orientation = getOrientation();
		Matrix3d forward = new Matrix3d();
		forward.set(orientation);
		Matrix3d backward = new Matrix3d(forward);
		backward.invert();

		Vector3d relativePosition = new Vector3d(pos);
		relativePosition.sub(position);
		double projection = relativePosition.dot(direction);
		Vector3d kernel = new Vector3d(direction);
		kernel.scale(projection);
		relativePosition.sub(kernel);
		backward.transform(relativePosition);
		
		double x = relativePosition.x;
		double y = -relativePosition.z;
		
		double r = relativePosition.length();
		double t = Math.atan2(y, x);
		
		double current = getCurrent();
		Vector3d B = new Vector3d(0.,1.,0.);
		if(r<=(1.+tol)*radius) {
			B.scale(mu0*current);
		} else {
			B.scale(0.);
		}
		forward.transform(B);
		return B;
	}
	
	public Vector3d getB_unrestricted(Vector3d pos) {
		Vector3d direction = getDirection();
		Quat4d orientation = getOrientation();
		Matrix3d forward = new Matrix3d();
		forward.set(orientation);
		Matrix3d backward = new Matrix3d(forward);
		backward.invert();

		Vector3d relativePosition = new Vector3d(pos);
		relativePosition.sub(position);
		double projection = relativePosition.dot(direction);
		Vector3d kernel = new Vector3d(direction);
		kernel.scale(projection);
		relativePosition.sub(kernel);
		backward.transform(relativePosition);
		
		double x = relativePosition.x;
		double y = -relativePosition.z;
		
		double r = relativePosition.length();
		double t = Math.atan2(y, x);
		
		Vector3d B = new Vector3d(0.,1.,0.);
		B.scale(mu0*current);
		forward.transform(B);
		return B;
	}	
	
	public Vector3d getB(Vector3d pos, double time) {
		return getB(pos);
	}
	
	public double getBFlux(Vector3d pos){
		double BFlux = 0.;
		return BFlux;
	}
	
	public boolean isGeneratingB() {
		return true;
	}

	
	
	// ***********************************************************************
	// Creation and destruction of particles.
	// ***********************************************************************

	
	public double getEnergyQuantum() {
		return energyQuantum;
	}

	public void setEnergyQuantum(double energyQuantum) {
		this.energyQuantum = energyQuantum;
	}

	public void setNparticles(int Nparticles) {
		this.Nparticles = Nparticles;
	}
	
	public int getNparticles() {
		return Nparticles;
	}

	public int getPlacementType() {
		return placementType;
	}

	public void setPlacementType(int placementType) {
		this.placementType = placementType;
	}

	private int strayCount = 0; 

	public void nextSpatial() {
		if( theEngine == null ) return;
		// Particle absorption by components, and stray particle tracking.
		Stack indexStack = new Stack();
		Iterator it = particleList.iterator();
		int index = 0;
		while(it.hasNext()) {
			EnergyParticle particle = (EnergyParticle) it.next();
			Vector3d pos = particle.getPosition();
			if(outside(pos)) {
				double angle = position2angle(pos);
				Iterator it2 = resistorList.iterator();
				boolean resistor_absorbed = false, capacitor_absorbed = false, battery_absorbed = false;
				while(it2.hasNext()) {
					Resistor resistor = (Resistor) it2.next();
					if(resistor.contains(angle)) {
						resistor_absorbed = true;
						resistor.particleCount++;
					}
				}
				it2 = capacitorList.iterator();
				while(it2.hasNext()) {
					Capacitor capacitor = (Capacitor) it2.next();
					if(capacitor.contains(angle)) {
						capacitor_absorbed = true;
						capacitor.particleCount++;
					}
				}
				it2 = batteryList.iterator();
				while(it2.hasNext()) {
					Battery battery = (Battery) it2.next();
					if(battery.contains(angle)) {
						battery_absorbed = true;
						battery.particleCount--;
					}
				}
				if( !resistor_absorbed && !capacitor_absorbed && !battery_absorbed) {
					strayCount++;
					System.out.println( "Stray particle. Count so far: " + strayCount + " Angle (in pi-rads): " + (angle/Math.PI));
				}
				if( (resistor_absorbed && capacitor_absorbed)
					|| (resistor_absorbed && battery_absorbed)
					|| (capacitor_absorbed && battery_absorbed) ) {
					System.out.println( "Error: multiple absorption detected." );
				}
				// Particle removal from application.
				if(dynamic) {
					theEngine.removeSimElement(particle);
				}
				// Particle tagging for removal from local list.
				indexStack.push(new Integer(index));
			}
			index++;
		}
		// Particle removal from local list.
		while(!indexStack.isEmpty()) {
			index = ((Integer) indexStack.pop()).intValue();
			particleList.remove(index);
		}
		// Particle creation in batteries, and quantized work update.
		it = batteryList.iterator();
		while(it.hasNext()) {
			Battery battery = (Battery) it.next();
			double work = battery.getEnergy()-battery.getQuantizedEnergy();
			if(work>0) {
				double n = Math.floor(work/energyQuantum);
				if(n>=Nparticles) {
					n = Nparticles;
					double dt, t;
					switch(placementType) {
						case NONEDGE:
							dt = (battery.t2-battery.t1)/n;
							t = battery.t1+dt/2.;
						break;
						case EDGE:
						default:
							dt = (battery.t2-battery.t1)/(n-1.);
							t = battery.t1;
						break;
					}
					for( int i=0; i<n; i++) {
						EnergyParticle particle = new EnergyParticle();
						particle.setPosition(angle2position(t));
						if(dynamic) {
							theEngine.addSimElement(particle);
						} else {
							particle.setSimEngine(theEngine);
						}
						particleList.add(particle);
						t+=dt;
					}
					battery.setQuantizedEnergy(battery.getQuantizedEnergy()+n*energyQuantum);
					battery.particleCount+=n;
				}
			} else {
				double n = Math.floor(-work/energyQuantum);
				if(n>=Nparticles) {
					n = Nparticles;
					battery.setQuantizedEnergy(battery.getQuantizedEnergy()-n*energyQuantum);
				}
			}
		}
		// Particle creation in capacitors, and quantized work update.
		it = capacitorList.iterator();
		while(it.hasNext()) {
			Capacitor capacitor = (Capacitor) it.next();
			double work = capacitor.getEnergy()-capacitor.getQuantizedEnergy();
			if(work>0) {
				double n = Math.floor(work/energyQuantum);
				if(n>=Nparticles) {
					n = Nparticles;
					double dt, t;
					switch(placementType) {
						case NONEDGE:
							dt = (capacitor.t2-capacitor.t1)/n;
							t = capacitor.t1+dt/2.;
						break;
						case EDGE:
						default:
							dt = (capacitor.t2-capacitor.t1)/(n-1.);
							t = capacitor.t1;
						break;
					}
					for( int i=0; i<n; i++) {
						EnergyParticle particle = new EnergyParticle();
						particle.setPosition(angle2position(t));
						if(dynamic) {
							theEngine.addSimElement(particle);
						} else {
							particle.setSimEngine(theEngine);
						}
						particleList.add(particle);
						t+=dt;
					}
					capacitor.setQuantizedEnergy(capacitor.getQuantizedEnergy()+n*energyQuantum);
					capacitor.particleCount-=n;
				}
			} else {
				double n = Math.floor(-work/energyQuantum);
				if(n>=Nparticles) {
					n = Nparticles;
					capacitor.setQuantizedEnergy(capacitor.getQuantizedEnergy()-n*energyQuantum);
				}
			}
		}
	}

	public void resetParticleSystem() {
		if( theEngine == null ) return;
		Iterator it = batteryList.iterator();
		while(it.hasNext()) {
			Battery battery = (Battery) it.next();
			battery.setEnergy(0.);
			battery.setQuantizedEnergy(0.);
			battery.particleCount = 0;
		}
		it = resistorList.iterator();
		while(it.hasNext()) {
			Resistor resistor = (Resistor) it.next();
			resistor.setEnergy(0.);
			resistor.particleCount = 0;
		}
		it = capacitorList.iterator();
		while(it.hasNext()) {
			Capacitor capacitor = (Capacitor) it.next();
			capacitor.setEnergy(0.);
			capacitor.setQuantizedEnergy(0.);
			capacitor.particleCount = 0;
		}
		it = particleList.iterator();
		while(it.hasNext()) {
			EnergyParticle particle = (EnergyParticle) it.next();
			theEngine.removeSimElement(particle);
		}
		particleList.clear();
		strayCount = 0;
	}

	synchronized public void initializeParticleSystem() {
		if(batteryList.size()==0 || current==0. || theEngine==null ) return;
		boolean absorption = false;
		updateSurfacePotential();
		setDynamic(false);
		CircuitParticleSystem system = new CircuitParticleSystem(this, particleList);
		double dt = theEngine.getDeltaTime();
		int previousNumber = 0, currentNumber = 0;
		do{
			previousNumber = currentNumber;
			double [] dv = RungeKutta4.integrate(system, theEngine.getTime(), dt);
			system.setDependentValues(dv, 0);
			Iterator it = particleList.iterator();
			while(it.hasNext()) {
				EnergyParticle particle = (EnergyParticle) it.next();
				particle.update();
			}
			nextSpatial();
			currentNumber = particleList.size();
			absorption = (currentNumber < previousNumber);
		} while(!absorption);
		Iterator it = batteryList.iterator();
		while(it.hasNext()) {
			Battery battery = (Battery) it.next();
			battery.setEnergy(battery.getEnergy()-battery.getQuantizedEnergy());
			battery.setQuantizedEnergy(0.);
			battery.particleCount = 0;
		}
		it = resistorList.iterator();
		while(it.hasNext()) {
			Resistor resistor = (Resistor) it.next();
			resistor.setEnergy(0.);
			resistor.particleCount = 0;
		}
		it = capacitorList.iterator();
		while(it.hasNext()) {
			Capacitor capacitor = (Capacitor) it.next();
			capacitor.setEnergy(capacitor.getEnergy()-capacitor.getQuantizedEnergy());
			capacitor.setQuantizedEnergy(0.);
			capacitor.particleCount = 0;
		}
		setDynamic(true);
		//theEngine.addSimElements(particleList);
	}
	
	public void uniformlyPlace(double gridwidth, double alpha) {
		// double gridwidth = 0.1;
		double R = alpha*radius;
		int M = (int)(2.*R/gridwidth)+1;
		double [][] x = new double[M][M];
		double [][] y = new double[M][M];
		int K = 0;
		double R_ = gridwidth*Math.floor(R/gridwidth);
		for(int i=0; i<M;i++) {
			x[i] = new double[M];
			y[i] = new double[M];
			for(int j=0; j<M;j++) {
				x[i][j]=-R_+(double)j*gridwidth;
				y[i][j]=R_-(double)i*gridwidth;
				if(x[i][j]*x[i][j]+y[i][j]*y[i][j]<R*R) {
					K++;
				}
			}
		}
		double [] particlex=new double[K];
		double [] particley=new double[K];
		int k = 0;
		for(int i=0; i<M;i++) {
			for(int j=0; j<M;j++) {
				if(x[i][j]*x[i][j]+y[i][j]*y[i][j]<R*R) {
					particlex[k]=x[i][j];
					particley[k]=y[i][j];
					k++;
				}
			}
		}
		for(int i=0; i<K; i++) {
			EnergyParticle particle = new EnergyParticle();
			particle.setPosition(particlex[i],particley[i],0);
			// particle.setModel(theEngine);
			// particle.updateNode3D();
			theEngine.addSimElement(particle);
			particleList.add(particle);
		}
		System.out.println(K + " particles were uniformly placed.");
	}

	
	private boolean outside(Vector3d pos) {
		Vector3d direction = getDirection();
		Quat4d orientation = getOrientation();
		Matrix3d forward = new Matrix3d();
		forward.set(orientation);
		Matrix3d backward = new Matrix3d(forward);
		backward.invert();
		Vector3d relativePosition = new Vector3d(pos);
		relativePosition.sub(position);
		double projection = relativePosition.dot(direction);
		Vector3d kernel = new Vector3d(direction);
		kernel.scale(projection);
		relativePosition.sub(kernel);
		backward.transform(relativePosition);
//		double x = relativePosition.x;
//		double y = -relativePosition.z;
//		System.out.println( "x: " + x);
//		System.out.println( "y: " + y);
		double r = relativePosition.length();
		// double t = Math.atan2(y, x);
		return r>(1.+tol)*radius;
	}
	
	private Vector3d angle2position(double t) {
		Vector3d direction = getDirection();
		Quat4d orientation = getOrientation();
		Matrix3d forward = new Matrix3d();
		forward.set(orientation);
//		Matrix3d backward = new Matrix3d(forward);
//		backward.invert();
		double r = radius;
		double x = r*Math.cos(t);
		double y = r*Math.sin(t);
		Vector3d pos = new Vector3d(x, 0., -y);
		forward.transform(pos);
		return pos;
	}
	
	private double position2angle(Vector3d pos) {
		Vector3d direction = getDirection();
		Quat4d orientation = getOrientation();
		Matrix3d forward = new Matrix3d();
		forward.set(orientation);
		Matrix3d backward = new Matrix3d(forward);
		backward.invert();
		Vector3d relativePosition = new Vector3d(pos);
		relativePosition.sub(position);
		double projection = relativePosition.dot(direction);
		Vector3d kernel = new Vector3d(direction);
		kernel.scale(projection);
		relativePosition.sub(kernel);
		backward.transform(relativePosition);
		double x = relativePosition.x;
		double y = -relativePosition.z;
//		System.out.println( "x: " + x);
//		System.out.println( "y: " + y);
//		double r = relativePosition.length();
		double t = Math.atan2(y, x);
		return t;
	}
	

	// ***********************************************************************
	// Component inner classes.
	// ***********************************************************************

	public abstract class Component implements Integratable {
		public double margin = 0.01*Math.PI;
		protected double energy = 0., quantizedEnergy = 0.;
		protected int particleCount = 0;
		protected double t1 = 0.;
		protected double t2 = 1.;
		public Component(double t1, double t2) {
			setT1(t1);
			setT2(t2);
		}
		public void setT1(double t1) {
			this.t1=Fourier.wrap(t1);
		}
		public void setT2(double t2) {
			this.t2=Fourier.wrap(t2);
		}
		public double getT1(){
			return t1;
		}
		public double getT2() {
			return t2;
		}
		public boolean overlapsWith(Component c){
			boolean type1 = contains(c.t1) || contains(c.t2);
			boolean type2 = c.contains(t1) || c.contains(t2);
			return type1 || type2;
		}
		public boolean contains(double t) {
			return (Fourier.wrap(t-t1)<=Fourier.wrap(t2-t1)+margin)
					||(Fourier.wrap(t2-t)<=Fourier.wrap(t2-t1)+margin);
		}
		public void setEnergy(double energy) {
			this.energy=energy;
		}
		public double getEnergy() {
			return energy;
		}
		public void setQuantizedEnergy(double quantizedEnergy) {
			this.quantizedEnergy=quantizedEnergy;
		}
		public double getQuantizedEnergy() {
			return quantizedEnergy;
		}
		public void getDependentValues(double[] depValues, int offset) {
			depValues[offset] = energy;
		}
		abstract public void getDependentDerivatives(double[] depDerivatvies, int offset,
				double time);
		public void setDependentValues(double[] newDepValues, int offset) {
			energy = newDepValues[offset];
		}
		public double getIndependentValue() {
			return 0.;
		}
		public int getNumberDependentValues() {
			return 1;
		}
		public boolean isIntegrating() {
			return true;
		}
		public void setIntegrating(boolean b) {
		}
	}
	
	public class Battery extends Component {
		protected double emf = 1.;
		public Battery(double position, double width, double emf) {
			super(position,width);
			setEMF(emf);
		}
		public void setEMF(double emf) {
			this.emf=emf;
		}
		public double getEMF() {
			return emf;
		}
		public double getEMFDerivative(double time) {
			return 0.;
		}
		public double getEMFSecondDerivative(double time) {
			return 0.;
		}
		public void influence(double [] V) {
			double E = emf;
			double N = V.length;
			for(double n=0; n<N; n++) {
				double t=(2.*Math.PI)*n/N;
				V[(int)n]+=(Fourier.step(t-t1)-Fourier.step(t-t2))*(E*(t-t1)/(t2-t1))+E*Fourier.step(t-t2);
			}
		}
		public void getDependentDerivatives(double[] depDerivatives, int offset,
				double time) {
			// dW/dt = EI.
			depDerivatives[offset] = emf * current;
		}
	}

	public class Resistor extends Component implements Integratable{
		protected double resistance = 1.;
		public Resistor(double position, double width, double resistance) {
			super(position,width);
			setResistance(resistance);
		}
		public void setResistance(double resistance) {
			this.resistance=resistance;
		}
		public double getResistance() {
			return resistance;
		}
		public void influence(double [] V) {
			double I = current;
			double R = resistance;
			double N = V.length;
			for(double n=0; n<N; n++) {
				double t=(2.*Math.PI)*n/N;
				V[(int)n]+=(Fourier.step(t-t1)-Fourier.step(t-t2))*(-I*R*(t-t1)/(t2-t1))-I*R*Fourier.step(t-t2);
				//double inf = (Fourier.step(t-t1)-Fourier.step(t-t2))*(-I*R*(t-t1)/(t2-t1));
				//System.out.println( "inf at " + n + " = " + inf );
			}
		}
		public void getDependentDerivatives(double[] depDerivatives, int offset,
				double time) {
			// dW/dt = -RI^2. By convention, we look at dissipation, whence
			// the positive sign below.
			depDerivatives[offset] = resistance * current * current;
		}
	}


	// Modified resistor, with custom distributed resistivity.
	
	public class CustomResistor extends Resistor {
		// Resistance used to be forced to 2.*Math.PI;
		public CustomResistor(double position, double width, double resistance) {
			super(position,width, resistance);
		}
		public void influence(double [] V, double I) {
			double R = resistance;
			double N = V.length;
			for(double n=0; n<N; n++) {
				double t=(2.*Math.PI)*n/N;
				V[(int)n]+=-I*(Math.sin(t)+t);
				//double inf = -I*(Math.sin(t)+t);
				//System.out.print( inf + " ");
			}
			System.out.println();
		}
	}
	
	public class Capacitor extends Component {
		protected double capacitance = 1.;
		protected double geometryFactor = 1.;
		public Capacitor(double position, double width, double capacitance) {
			super(position,width);
			setCapacitance(capacitance);
		}
		public void setCapacitance(double capacitance) {
			this.capacitance=capacitance;
		}
		public double getCapacitance() {
			return capacitance;
		}
		public void setGeometryFactor(double geometryFactor) {
			this.geometryFactor=geometryFactor;
		}
		public double getGeometryFactor() {
			return geometryFactor;
		}
		public void influence(double [] V) {
			double Vc = charge/capacitance;
			double N = V.length;
			for(double n=0; n<N; n++) {
				double t=(2.*Math.PI)*n/N;
				V[(int)n]+=(Fourier.step(t-t1)-Fourier.step(t-t2))*(-Vc*(t-t1)/(t2-t1))-Vc*Fourier.step(t-t2);
			}
		}
		public void getDependentDerivatives(double[] depDerivatives, int offset,
				double time) {
			// Work done by the capacitor is positive, when I and V have opposite signs.
			// dW/dt = - alpha * I * Q / C = - alpha * I * V.
			depDerivatives[offset] = -geometryFactor*current*charge/capacitance;;
		}
	}

	/**
	 * Unit vector in direction of ring, orthogonal to the plane. 
	 */
	protected Vector3d v = new Vector3d();
	/**
	 * First unit vector in the plane of the ring 
	 */
	protected Vector3d u1 = new Vector3d();
	/**
	 * Second unit vector in the plane of the ring 
	 */
	protected Vector3d u2 = new Vector3d();

	double integration_radius = 0.;

	/**
		setuv() is called before the integration begins, to define a unit vector in
		the direction of the ring (v), and two vectors on the plane of the ring, to
		define the x and y directions of integration (u1 and u2 respectively).
	 */
	protected void setuv() {
		integration_radius = radius;
		// Gram-Schmidt Orthogonalization.
		Vector3d d = new Vector3d(getDirection());
		d.normalize();
		v.set(d);
		u1.set(1., 0., 0.);
		v.scale(u1.dot(v));
		u1.sub(v);
		if (u1.length() < Teal.DoubleZero) {
			v.set(d);
			u1.set(0., 1., 0.);
			v.scale(u1.dot(v));
			u1.sub(v);
			if (u1.length() < Teal.DoubleZero) {
				v.set(d);
				u1.set(0., 0., 1.);
				v.scale(u1.dot(v));
				u1.sub(v);
			}
		}
	
		v.set(d);
		u1.normalize();
		u2.cross(v, u1);
		if (Double.isNaN(v.length()))
			System.out.println("v is NaN");
		if (Double.isNaN(u1.length()))
			System.out.println("u1 is NaN");
		if (Double.isNaN(u2.length()))
			System.out.println("u2 is NaN");
	}

	/*
		protected void setuv() {
			v.set(getDirection());
			do {		
				v.normalize();
				u1.set(Math.random(),Math.random(),Math.random());
				v.scale(u1.dot(v));
				u1.sub(v);
			} while( u1.length() < Teal.doubleZero );
			v.normalize();
			u1.normalize();
			u2.cross(v,u1);
			if( Double.isNaN(v.length()) ) System.out.println( "v is NaN" );
			if( Double.isNaN(u1.length()) ) System.out.println( "u1 is NaN" );
			if( Double.isNaN(u2.length()) ) System.out.println( "u2 is NaN" );
		}
	*/
	
	/**
	 * f(x,y) is the energy density u = 0.5*(epsilon0*E^2+B^2/mu0)
	 */
	protected double f(double x, double y){
		//		System.out.println(this.id + " is asking for external fields...");
		Vector3d t = new Vector3d();
		t.scale(x, u1);
		t.scaleAdd(y, u2, t);
		t.add(position_d);
		Vector3d B = getB(t);
/*		Vector3d E = getE(t);
		double u = 0.5*(epsilon0*E.lengthSquared()+B.lengthSquared()/mu0);
*/		
		double u = 0.5*(B.lengthSquared()/mu0);
		if(Double.isNaN(u)) {
			System.out.println("NaN in Circuit.f(double, double)");
			System.out.println("u = " + u );
			//System.out.println("E = " + E + " |E| = " + E.length());
			System.out.println("B = " + B + " |B| = " + B.length());
			System.out.println("t = " + t + " |t| = " + t.length());
			System.out.println("x = " + x );
			System.out.println("y = " + y );
			System.out.println("v = " + v + " |v| = " + v.length());
			System.out.println("u1 = " + u1 + " |u1| = " + u1.length());
			System.out.println("u2 = " + u2 + " |u2| = " + u2.length());
			System.exit(1);
		}
		return u;
	}

	/**
		q(r) is equivalent to 2*pi*|r|*f(r,0).
	 */
	protected double q(double r) {
		double value = 2. * Math.PI * Math.abs(r) * f(r, 0);
		return value;
	}

	/**
		g(x) is the partial integral of f(x,y) for fixed x and y varying between
		-SQRT(r^2-x^2) and SQRT(r^2-x^2). In the double integral, it is integrated
		itself for x between -r and r, to yield the flux.
	*/
	protected double g(double x) {
		if(Double.isNaN(x)) {
			System.out.println("NaN in Circuit.g(double)");
			System.out.println("x = " + x );
		}
		/*
			Pseudocode for Rhomberg intergration
			FUNCTION Rhomberg(a,b,maxit,es)
				LOCAL I(10,10)
				n=1
				I1,1=TrapEq(n,a,b)
				iter=0
				DO
					iter=iter+1
					n=2^iter
					Iiter+1,1=TrapEq(n,a,b)
					DO k=2, iter+1
						j=2+iter-k
						Ij,k=(4^(k-1)*Ij+1,k-1-Ij,k-1)/(4^(k-1)-1)
					END DO
					ea=ABS(I1,iter+1-I1,iter)/I1,iter+1)*100
					IF(iter>=maxit OR ea <=es) EXIT
				END DO
				Rhomberg = I1,iter+1
			END Rhomberg
		*/
		// All intergation variables are bulk defined here, unintialized.
		double a = 0., b = 0., c = 0., es = 0., ea = 0., h = 0.;
		double ans = 0.;
		int k = 0, j = 0, iter = 0, m = 0, n = 0, i = 0, maxit = 0;
		// These are the arguments of the integration routine.
		if(x>integration_radius) x=integration_radius;
		a = -Math.sqrt(integration_radius * integration_radius - x * x);
		b = -a;
		maxit = 10;
		es = 1.;
		
		if(Double.isNaN(a)) {
			System.out.println("NaN in Circuit.g(double)");
			System.out.println("a = " + a );
			System.out.println("x = " + x );
			System.out.println("integration_radius = " + integration_radius );
			System.exit(1);
		}

		/*
			I[][] is used to contain integration results at various iteration stages.
			Note also that the algorithm pseudocode uses indices starting from 1. To
			avoid errors, I used the pseudocode as is, and that means that zero-indices
			are simply not used. This can be fixed later by offsetting everything.
		 */
		double[][] I = new double[maxit + 2][maxit + 2];
	
		// The routine starts here, it's an adaptation of the pseudocde.
		iter = 0;
		n = 1;
		h = (b - a);
		I[1][1] = (f(x, a) + f(x, b)) * h / 2.;
	
		try {
	
			do {
				iter++;
				n *= 2;
				h /= 2.;
				I[iter + 1][1] = 0;
				c = a;
				for (i = 0; i < n; i++) {
					I[iter + 1][1] += (f(x, c) + f(x, c + h)) * h / 2.;
					c += h;
				}
				m = 1;
				for (k = 2; k <= iter + 1; k++) {
					j = 2 + iter - k;
					m *= 4;
					I[j][k] = (m * I[j + 1][k - 1] - I[j][k - 1]) / (m - 1);
				}
				ea =
					Math.abs((I[1][iter + 1] - I[1][iter]) / I[1][iter + 1])
						* 100.;
			} while (!(iter >= maxit || ea <= es));
			ans = I[1][iter + 1];
	
		} catch (Exception ex) {
			System.err.println("Caught Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		return ans;
	}

	protected double doubleIntegrate() {
			// v, u1 and u2 are fixed for this integration pass.
			setuv();
	
			// All intergation variables are bulk defined here, unintialized.
			double a = 0., b = 0., c = 0., es = 0., ea = 0., h = 0.;
			double ans = 0.;
			int k = 0, j = 0, iter = 0, m = 0, n = 0, i = 0, maxit = 0;
			// These are the arguments of the integration routine.
			a = - integration_radius;
			b = integration_radius;
			maxit = 10;
			es = 0.01;
	
			/*
				I[][] is used to contain integration results at various iteration stages.
				Note also that the algorithm pseudocode uses indices starting from 1. To
				avoid errors, I used the pseudocode as is, and that means that zero-indices
				are simply not used. This can be fixed later by offsetting everything.
			 */
			double[][] I = new double[maxit + 2][maxit + 2];
	
			// The routine starts here, it's an adaptation of the pseudocde.
			iter = 0;
			n = 1;
			h = (b - a);
			I[1][1] = (g(a) + g(b)) * h / 2.;
	
			try {
	
				do {
					iter++;
					//System.out.println("iter = " + iter + " ea = " + ea);
					n *= 2;
					h /= 2.;
					I[iter + 1][1] = 0;
					c = a;
					for (i = 0; i < n; i++) {
						I[iter + 1][1] += (g(c) + g(c + h)) * h / 2.;
						c += h;
					}
					m = 1;
					for (k = 2; k <= iter + 1; k++) {
						j = 2 + iter - k;
						m *= 4;
						I[j][k] = (m * I[j + 1][k - 1] - I[j][k - 1]) / (m - 1);
					}
					ea =
						Math.abs((I[1][iter + 1] - I[1][iter]) / I[1][iter + 1])
							* 100.;
				} while (!(iter >= maxit || ea <= es));
				ans = I[1][iter + 1];
	
			} catch (Exception ex) {
				System.err.println("Caught Exception: " + ex.getMessage());
				ex.printStackTrace();
			}
	
			return ans;
		}

	protected double singleIntegrate() {
			// v, u1 and u2 are fixed for this integration pass.
			setuv();
	
			// All intergation variables are bulk defined here, unintialized.
			double a = 0., b = 0., c = 0., es = 0., ea = 0., h = 0.;
			double ans = 0.;
			int k = 0, j = 0, iter = 0, m = 0, n = 0, i = 0, maxit = 0;
			// These are the arguments of the integration routine.
			a = 0.;
			b = integration_radius;
			maxit = 10;
			es = 1e-5;
	
			/*
				I[][] is used to contain integration results at various iteration stages.
				Note also that the algorithm pseudocode uses indices starting from 1. To
				avoid errors, I used the pseudocode as is, and that means that zero-indices
				are simply not used. This can be fixed later by offsetting everything.
			 */
			double[][] I = new double[maxit + 2][maxit + 2];
	
			// The routine starts here, it's an adaptation of the pseudocde.
			iter = 0;
			n = 1;
			h = (b - a);
			I[1][1] = (q(a) + q(b)) * h / 2.;
	
			try {
	
				do {
					iter++;
					n *= 2;
					h /= 2.;
					I[iter + 1][1] = 0;
					c = a;
					for (i = 0; i < n; i++) {
						I[iter + 1][1] += (q(c) + q(c + h)) * h / 2.;
						c += h;
					}
					m = 1;
					for (k = 2; k <= iter + 1; k++) {
						j = 2 + iter - k;
						m *= 4;
						I[j][k] = (m * I[j + 1][k - 1] - I[j][k - 1]) / (m - 1);
					}
					ea =
						Math.abs((I[1][iter + 1] - I[1][iter]) / I[1][iter + 1])
							* 100.;
				} while (!(iter >= maxit || ea <= es));
				ans = I[1][iter + 1];
	
			} catch (Exception ex) {
				System.err.println("Caught Exception: " + ex.getMessage());
				ex.printStackTrace();
			}

			return ans;
		}
	
}
