/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2008 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: tealsim.java,v 1.2 2010/03/23 15:42:34 pbailey Exp $
 * 
 */
package teal.sim.simulation;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;



/**
 * Implements a ContentHandler for the creation of TSimulation objects
 * from a URL with mimetype of "simulation/tealsim".
 * 
 * @author pbailey
 * 
 * @see java.net.ContentHandler#getContent(java.net.URLConnection)
 *
 */
public class tealsim extends ContentHandler {


	public Object getContent(URLConnection connection) throws IOException {
		TSimulation theSim = null;
		theSim = SimulationFactory.loadSimulation(connection.getInputStream());
		
		return theSim;
	}

}
