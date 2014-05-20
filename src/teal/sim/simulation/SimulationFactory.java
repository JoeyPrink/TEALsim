/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SimulationFactory.java,v 1.13 2008/02/11 19:48:19 pbailey Exp $ 
 * 
 */

package teal.sim.simulation;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import teal.util.TDebug;

/**
 * Provides the ability to create a TSimulation from an XML input stream
 * and to dump a TSimulation into an XML output stream.
 */

public class SimulationFactory {

	private static String version = "SimulationFactory  " + "$Revision: 1.13 $";

	public static TSimulation loadSimulation(InputStream input)
			throws IOException {
		TSimulation theSim = null;
		boolean hasNext = true;
		int count = 0;
		try {
			XMLDecoder e = new XMLDecoder(new BufferedInputStream(input));
			Object obj = e.readObject();
			if (obj != null) {
				TDebug.println(0, "Loaded First: " + obj.getClass().getName()
						+ ": " + obj);
				if (obj instanceof TSimulation) {
					theSim = (TSimulation) obj;

				} else {
					TDebug.println("ObjectType not as expectd: " + obj);
				}
			}
			while (hasNext) {
				count++;
				TDebug.println(0, count + ": \tLoaded: " + obj);
				try {
					obj = e.readObject();
					TDebug.println(0, "Loaded: " + obj);
				}
				catch (ArrayIndexOutOfBoundsException ore) {
					TDebug.println(0, "Endof objects: total = " + count);
					hasNext = false;
				}
			}
			e.close();
		} catch (Exception fnf) {
			TDebug.printThrown(0, fnf, " Trying to load input");
		}
		TDebug.println(0, "Returning theSim: " + theSim);
		return theSim;
	}

	public static void saveSimulation(TSimulation sim, FileOutputStream output)
			throws IOException {

		if (sim == null) {
			TDebug.println("Error: the simulation has not been created!");
			return;
		}
		try {
			BufferedOutputStream out = new BufferedOutputStream(output);
			
			XMLEncoder e = new XMLEncoder(out);
			String className = sim.getClass().getName();
			if(className.compareTo("teal.physics.em.SimEM") == 0){
				e.writeObject(sim);
				 e.writeObject(version);
			}
			else if(className.compareTo("teal.physics.physical.SimKinematic") == 0){
				e.writeObject(sim);
				 e.writeObject(version);
			}
			// Default type
			else if(sim instanceof Simulation3D) {
				e.writeObject(sim);
				 e.writeObject(version);
				}
			
			
			e.close();

		}

		catch (Exception fnf) {
			TDebug.printThrown(0, fnf, " Error Trying to save simulation: "
					+ fnf.getMessage());
		}

	}
}