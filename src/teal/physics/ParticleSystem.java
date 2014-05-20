/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ParticleSystem.java,v 1.7 2009/04/24 19:35:49 pbailey Exp $ 
 * 
 */

package teal.physics;

import java.util.*;

import teal.math.Integratable;

public class ParticleSystem implements Integratable {
	
	Collection<?> particleList = null;
	
	public ParticleSystem(Collection<?> particleList) {
		this.particleList = particleList;
	}
	
	public void setIntegrating(boolean x) {}
	
	public boolean isIntegrating() { return true;}
	
	public double getIndependentValue() {
		Iterator it = particleList.iterator();
		if(!it.hasNext()) return 0.;
		EnergyParticle particle = (EnergyParticle) it.next();
		return particle.getIndependentValue();
	}
	
	public void reconcile() {}
	
	public int getNumberDependentValues() {
		return 3 * particleList.size();
	}

	public void getDependentValues(double[] depValues, int offset) {
		Iterator it = particleList.iterator();
		while(it.hasNext()) {
			EnergyParticle particle = (EnergyParticle) it.next();
			particle.getDependentValues(depValues, offset);
			offset += particle.getNumberDependentValues();
		}
	}

	public void setDependentValues(double[] newValues, int offset) {
		Iterator it = particleList.iterator();
		while(it.hasNext()) {
			EnergyParticle particle = (EnergyParticle) it.next();
			particle.setDependentValues(newValues, offset);
			offset += particle.getNumberDependentValues();
		}
	}

	public void getDependentDerivatives(double[] depDerivatvies, int offset,
			double time) {
		Iterator it = particleList.iterator();
		while (it.hasNext()) {
			EnergyParticle particle = (EnergyParticle) it.next();
			particle.getDependentDerivatives(depDerivatvies, offset, time);
			offset += particle.getNumberDependentValues();
		}
	}
}
