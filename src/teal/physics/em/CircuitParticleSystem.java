/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CircuitParticleSystem.java,v 1.8 2009/04/24 19:35:50 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import java.util.Collection;

import teal.physics.ParticleSystem;


public class CircuitParticleSystem extends ParticleSystem {
	
	Circuit circuit = null;
	
	public CircuitParticleSystem(Circuit circuit, Collection<?> particleList) {
		super(particleList);
		this.circuit = circuit;
	}
	
	public int getNumberDependentValues() {
		return circuit.getNumberDependentValues() + super.getNumberDependentValues();
	}

	public void getDependentValues(double[] depValues, int offset) {
		super.getDependentValues(depValues, offset);
		offset += super.getNumberDependentValues();
		circuit.getDependentValues(depValues, offset);
	}

	public void setDependentValues(double[] newValues, int offset) {
		super.setDependentValues(newValues, offset);
		offset += super.getNumberDependentValues();
		circuit.setDependentValues(newValues, offset);
	}

	public void getDependentDerivatives(double[] depDerivatives, int offset, double time) {
		super.getDependentDerivatives(depDerivatives, offset, time);
		offset += super.getNumberDependentValues();
		circuit.getDependentDerivatives(depDerivatives, offset, time);
	}
}