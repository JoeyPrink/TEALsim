/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SimPlayerApplet.java,v 1.7 2007/11/02 21:53:46 pbailey Exp $
 * 
 */

package teal.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JApplet;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import teal.sim.simulation.TSimulation;

/**
 * Wrapper Applet for SimPlayer. Parses arguments to load a specific simulation
 * on startup, currently only supports simulations which have a default
 * constructor. arguments are: 
 * <br/><indent> -n fully_qualified_class_name</indent>
 * <br/>
 * Note: the class must be in the current classpath.
 * 
 * 
 * @see SimPlayer
 * 
 * @author Andrew McKinney
 * 
 */

public class SimPlayerApplet extends JApplet implements PropertyChangeListener {

	private static final long serialVersionUID = 3258689927121220656L;

	protected SimPlayer thePlayer;

	public SimPlayerApplet() {
		super();
	}

	public void propertyChange(PropertyChangeEvent pce) {
		if (pce.getPropertyName().compareToIgnoreCase("title") == 0) {
			// setTitle(pce.getNewValue().toString());
		}
	}

	public void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		thePlayer = new SimPlayer();
		thePlayer.addPropertyChangeListener("title", this);
		setContentPane(thePlayer);
		setJMenuBar((JMenuBar)thePlayer.getTMenuBar());

		String simString = getParameter("SIMULATION");
		if (simString != null) {
			System.out.println(simString);
			try {
				thePlayer.loadSimClass(simString);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
}
