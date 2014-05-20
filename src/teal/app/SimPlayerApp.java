/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SimPlayerApp.java,v 1.42 2010/07/12 14:58:27 stefan Exp $
 * 
 */

package teal.app;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import teal.sim.simulation.TSimulation;

/**
 * Generic stand-alone wrapper application for SimPlayer. 
 * The base class for Simulation applications.
 * 
 * @see SimPlayer
 * 
 * 
 * @author Philip Bailey
 * 
 */

public class SimPlayerApp extends JFrame implements PropertyChangeListener {

	private static final long serialVersionUID = 3258689927121220656L;

	protected SimPlayer thePlayer;

	public SimPlayerApp() {
		super();
		thePlayer = new SimPlayer();
		thePlayer.setTheWindow(this);
		thePlayer.addPropertyChangeListener("title", this);
		setContentPane(thePlayer);
		setJMenuBar((JMenuBar) thePlayer.getTMenuBar());
	}
	
	public SimPlayer getSimPlayer(){
		return thePlayer;
	}

	public void propertyChange(PropertyChangeEvent pce) {
		if (pce.getPropertyName().compareToIgnoreCase("title") == 0) {
			setTitle(pce.getNewValue().toString());
		}
	}

	/**
     * Parses arguments to load a specific simulation on startup.  Currently only 
     * supports simulations which
     * have a default constructor. Arguments are: <br/><indent> -n
     * fully_qualified_class_name</indent> <br/>Note: the simulation class must
     * be in the current classpath. 
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SimPlayerApp theApp = new SimPlayerApp();
			if (args.length >= 2) {
				// Specify a simulation to load by class name
				if (args[0].compareTo("-n") == 0) {
					try{
					Class<?> simClass = Class.forName(args[1]);
					TSimulation temp = (TSimulation) simClass.newInstance();

					// Triplet-based implementation of command line parsing.
					// Instead of property/value pairs acting
					// only on the simulation, we now use element/property/value
					// triplets, where element is a
					// TElement of the simulation that we want to change a
					// property on. Using "sim" for the element
					// argument looks for the property on the simulation itself,
					// reducing to the previous implementation.
					// if (args.length >= 5 && ((args.length - 2) % 3 == 0)) {
					if ((args.length > 2)) {
						if ((args.length - 2) % 3 == 0) {

							for (int i = 2; i < args.length; i += 3) {
								String elementName = args[i];
								String propName = args[i + 1];
								String propValue = args[i + 2];

								temp.setProperty(elementName, propName,
										propValue);
							}
						} else {
							throw (new Exception(
									"Invalid number of command line arguments."));
						}
					}
					theApp.thePlayer.load(temp);
					}
					catch(IllegalArgumentException iae){
						throw new IllegalArgumentException("Error loading class " + args[1] + "; ",iae);
					}
				}
			}
			theApp.setLocationRelativeTo(null);
			theApp.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
