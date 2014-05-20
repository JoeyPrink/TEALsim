/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ForceOnDipoleLVApp.java,v 1.1 2008/06/03 15:42:28 pbailey Exp $
 * 
 */

package tealsim.physics.ilab;

import javax.swing.UIManager;

import teal.app.SimPlayerApp;
import tealsim.physics.em.TeachSpinBase;

/**
 * Stand-alone application for the LabVIEW TeachSpin iLab experiments.
 * 
 * 
 * @see TeachSpinLV
 * 
 *
 * @author Philip Bailey
 *
 */

public class ForceOnDipoleLVApp extends SimPlayerApp {

    private static final long serialVersionUID = 3258689927121220656L;

    public ForceOnDipoleLVApp() {
        super();
        
        
    }
    
  /**
   * Launches the TeachSpin application which interfaces with the actual hardware and LabVIEW software.
   * <br/>
   * @param args
   * <br/><indent>-h hostname - required</indent>
   * <br/><indent>-p port - default 43970</indent>
   * <br/><indent>-f numFieldLines - default 2</indent>
   * <br/><indent>-c numCoils - default 168</indent>
   * 
   */
    public static void main(String[] args) {
    	String host = "localhost";
    	int port = 43970;
    	int numLines = 2;
    	int numCoils = 168;
    	
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            ForceOnDipoleLVApp theApp;
            theApp = new ForceOnDipoleLVApp();
         
            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i].compareTo("-h") == 0) {
                    	host = args[++i];
                    	
                    } else if (args[i].compareTo("-p") == 0) {
                    	port = Integer.parseInt(args[++i]);
                    	
                    }
                    else if (args[i].compareTo("-f") == 0) {
                    	numLines = Integer.parseInt(args[++i]);
                    	
                    }
                    else if (args[i].compareTo("-c") == 0) {
                    	numCoils =Integer.parseInt(args[++i]);
	
                    }
                }
            }
                
            TeachSpinLV sim = new TeachSpinLV(host, port, numLines, numCoils, -1, TeachSpinBase.MODEL);
            theApp.thePlayer.load(sim); 
            theApp.setLocationRelativeTo(null);
            theApp.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
