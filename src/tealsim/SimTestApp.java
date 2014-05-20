/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SimTestApp.java,v 1.7 2010/07/16 21:41:40 stefan Exp $
 * 
 */

package tealsim;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.app.SimPlayer;
import teal.app.SimPlayerApp;
import teal.config.Teal;
import teal.field.Field;
import teal.framework.TealAction;
import teal.render.BoundingSphere;
import teal.sim.control.VisualizationControl;
import teal.sim.simulation.TSimulation;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.ui.control.PropertyInteger;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;
import teal.visualization.processing.Colorizer;
//import teal.visualization.processing.ColorizerBeanInfo;


/* imports to support the testing of on the fly sim construction */
import teal.math.RectangularPlane;
import teal.physics.em.*;
import teal.ui.UIPanel;
/* end test imports */

/**
 * Parses arguments to load a specific simulation on startup
 * or a defined collection of Simulations which may be launched from the menubar.
 * Currently only supports simulations which have a default constructor.
 * arguments are:
 * <br/><indent> -n fully_qualified_class_name</indent>
 * <br/><indent> -a allows the user to select a number of Electro-Magnetic and mechanical simulations
 * <br/>Note: the simulation classes must be in the current classpath.
 * 
 * 
 * @see SimPlayer
 * 
 *
 * @author Philip Bailey
 *
 */

public class SimTestApp extends SimPlayerApp implements ActionListener {

    private static final long serialVersionUID = 3258689927121220656L;

    public SimTestApp() {
        super();
    }
     
    protected void addActions(){
		TealAction ta;
		ta = new TealAction("Load","load", thePlayer);
		thePlayer.addAction("File", ta);
		ta = new TealAction("Save","save", thePlayer);
		thePlayer.addAction("File", ta);
		ta = new TealAction("Load Test","Load Test", this);
		thePlayer.addAction("Actions", ta);
		ta = new TealAction("Box Induction","tealsim.physics.em.boxInduction", this);
		thePlayer.addAction("Actions", ta);
      ta = new TealAction("Capacitor","tealsim.physics.em.Capacitor", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Charge By Induction","tealsim.physics.em.ChargeByInduction", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Charged Metal Slab","tealsim.physics.em.ChargedMetalSlab", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Charge in Magnetic Field","tealsim.physics.em.ChargeInMagneticFieldGame", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Coulomb's Law","tealsim.physics.em.CoulombsLaw", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Electrostatic Force","tealsim.physics.em.ElectrostaticForce", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("EM Radiator","tealsim.physics.em.EMRadiatorApp", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("EM Videogame","tealsim.physics.em.EMVideogame", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("EM Zoo","tealsim.physics.em.EMZoo", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("ExB Drift","tealsim.physics.em.ExBDrift", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Falling Coil","tealsim.physics.em.FallingCoil", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Falling Magnet","tealsim.physics.em.FallingMagnet", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Faraday's Law","tealsim.physics.em.FaradaysLaw", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Faraday's Law Rotation","tealsim.physics.em.FaradaysLawCylindrcalMagnet", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Faraday's Law Rotation","tealsim.physics.em.FaradaysLawRotation", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Faraday's Law Rotation","tealsim.physics.em.FaradaysLawTwoCoils", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Floating Coil","tealsim.physics.em.FloatingCoil", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Mapping Fields","tealsim.physics.em.MappingFields", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Pentagon","tealsim.physics.em.Pentagon", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Point Charge","tealsim.physics.em.PCharges", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Radiating Charge","tealsim.physics.em.RadiationCharge", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Seafloor","tealsim.physics.em.SeafloorApp", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("TeachSpin","tealsim.physics.em.TeachSpin", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Torque on an Electric Dipole","tealsim.physics.em.TorqueOnDipoleE", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Torque on a Magnetic Dipole","tealsim.physics.em.TorqueOnDipoleB", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Two Rings","tealsim.physics.em.TwoRings", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Van deGraff","tealsim.physics.em.VandeGraff", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Wire and Magnet","tealsim.physics.em.WireAndMagnet", this);
	  thePlayer.addAction("Actions", ta);
    
      
      /*
      ta = new TealAction("Inclined Plane","tealsim.physics.mech.InclinedPlaneApp", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Acceleration on an Inclined Plane","tealsim.physics.mech.GalileosInclinedPlane", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Simple Pendulum","tealsim.physics.mech.SimplePendulumApp", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Physical Pendulum","tealsim.physics.mech.WeightedPhysicalPendulumApp", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Impulse Pendulum","tealsim.physics.mech.SimpleImpulsePendulumApp", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Newton's Cradle","tealsim.physics.mech.NewtonsCradle", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Rigid Bar","tealsim.physics.mech.RigidBarApp", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Circular Motion","tealsim.physics.mech.", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Projectile","tealsim.physics.mech.ProjectileApp", this);
      thePlayer.addAction("Actions", ta);
      ta = new TealAction("Celestial Mechanics","tealsim.physics.mech.CelestialApp", this);
      thePlayer.addAction("Actions", ta);
	*/
    }
    
    public void actionPerformed(ActionEvent e)
    throws IllegalArgumentException{
    	if(e.getActionCommand().compareTo("Load Test")== 0){
    	 loadTestSim();
    	
    	}
    	else{
    		thePlayer.loadSimClass(e.getActionCommand());
    	}
    }


    public static void main(String[] args) {
        try {
        	TDebug.setGlobalLevel(0);
            //TDebug.println(0,"Test TDebug out");
            Introspector.flushCaches();
            BeanInfo bInfo = Introspector.getBeanInfo(Class.forName("teal.visualization.processing.Colorizer"));
            PropertyDescriptor [] theProperties = bInfo.getPropertyDescriptors();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SimTestApp theApp = new SimTestApp();
            int loadFlagIndex = -1;
            int actionsFlagIndex = -1;
            if (args.length > 0) {
                for (int i = 0; i < args.length; ++i) {
                    if (args[i].compareTo("-n") == 0) {
                    	loadFlagIndex = i;
                    } else if (args[i].compareTo("-a") == 0) {
                    	actionsFlagIndex = i;
                    }
                }
 
                if (actionsFlagIndex > -1) {
                	theApp.addActions();
                }
                if (loadFlagIndex > -1) {
                	int loadLen = (loadFlagIndex > actionsFlagIndex) ? (args.length - loadFlagIndex) : (actionsFlagIndex - loadFlagIndex);
                    String[] loadArgs = new String[loadLen]; 
                    System.arraycopy(args,loadFlagIndex,loadArgs,0,Math.max(actionsFlagIndex,loadLen));
                    
                    if (loadArgs.length >= 2) { // its a class name
                        String arg2 = loadArgs[1];
                        Class<?> simClass = Class.forName(arg2);
                        TSimulation temp = (TSimulation) simClass.newInstance();
                        
                        
                        // Triplet-based implementation of command line parsing.  Instead of property/value pairs acting
                        // only on the simulation, we now use element/property/value triplets, where element is a 
                        // TElement of the simulation that we want to change a property on.  Using "sim" for the element
                        // argument looks for the property on the simulation itself, reducing to the previous implementation.
                        //if (args.length >= 5 && ((args.length - 2) % 3 == 0)) {
                        if ((loadArgs.length - 2 > 0)) {
                        	if ((loadArgs.length - 2) % 3 == 0) {
                       
	                        	for (int i = 2; i < loadArgs.length; i += 3) {
	                        		String elementName = loadArgs[i];
	                        		String propName = loadArgs[i+1];
	                        		String propValue = loadArgs[i+2];
	                        		
	                        		temp.setProperty(elementName,propName,propValue);
	                        	}
                        	} else {
                            	throw (new Exception("Invalid number of command line arguments."));
                            }
                        }
                        theApp.thePlayer.load(temp); 
                    }
                }  
            }
            theApp.setLocationRelativeTo(null);
            theApp.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadTestSim(){
    	SimEM sim = new SimEM();
    	FieldConvolution mDLIC;
        VisualizationControl visControl;
        PropertyInteger slider1;
        PropertyDouble slider2;
        PropertyDouble slider3;
        PropertyDouble slider5;
        PropertyDouble slider6;
        PropertyDouble slider7;
        PropertyDouble slider8;
        PropertyDouble slider9;
        double defaultRadius = Teal.RingOfCurrentDefaultRadius * 2.;
        RingOfCurrent roc1;
        RingOfCurrent roc2;
        Vector3d ring1Pos = new Vector3d(0, Teal.RingOfCurrentDefaultRadius, 0);
        Vector3d ring2Pos = new Vector3d(0, -Teal.RingOfCurrentDefaultRadius, 0);
        Colorizer colorizer = new Colorizer(0.03,0.002,0.3,false);
        sim.addElement(colorizer);

            sim.setTitle( "Load Test");
            sim.setID("loadTest");
           
            BoundingSphere bs = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 4);
            sim.getEngine().setBoundingArea(bs);
            RectangularPlane rec = new RectangularPlane(bs);
            sim.getScene().setBoundingArea(bs);
            //setShowGizmos(false);
            //TDebug.setGlobalLevel(1);
            ((UIPanel)sim.getEngineControl()).setVisible(false);
            mDLIC = new FieldConvolution();
            mDLIC.setSize(new Dimension(256,256));
            sim.addElement(mDLIC, false);
            mDLIC.setVisible(false);
            mDLIC.setComputePlane(rec);
            mDLIC.setColorizer(colorizer);
            mDLIC.setColorMode(Teal.ColorMode_MAGNITUDE);
            roc1 = new RingOfCurrent();
            roc1.setID("Ring 1");
            roc1.setPosition(ring1Pos);
            roc1.setRadius(Teal.RingOfCurrentDefaultRadius);
            roc1.setThickness(Teal.RingOfCurrentDefaultTorusRadius);
            roc1.setPickable(true);
            roc1.setMoveable(true);
            roc1.setRotable(true);
            roc1.setSelectable(true);
            sim.addElement(roc1);

            roc2 = new RingOfCurrent();
            roc2.setID("Ring 2");
            roc2.setPosition(ring2Pos);
            roc2.setThickness(Teal.RingOfCurrentDefaultTorusRadius);
            roc2.setPickable(true);
            roc2.setRotable(true);
            roc2.setMoveable(true);
            roc2.setSelectable(true);
            sim.addElement(roc2);

            //TDebug.println(0,"M1 flux " + m1.getBFlux(new Vector3d(5,0,0)));

            FieldDirectionGrid fv = new FieldDirectionGrid();
            fv.setType(Field.B_FIELD);
            fv.setResolution(0);


            slider2 = new PropertyDouble();
            slider2.setMinimum(-10);
            slider2.setMaximum(10);
            slider2.setBounds(35, 550, 415, 50);
            slider2.setPaintTicks(true);
            slider2.setText("Current Ring 1:");
            slider2.setBorder(null);
            slider2.addRoute("value", roc1, "current");
            slider2.setValue(1.0);

            slider5 = new PropertyDouble();
            slider5.setMinimum(0);
            slider5.setMaximum(3);
            slider5.setBounds(35, 610, 415, 50);
            slider5.setPaintTicks(true);
            slider5.addRoute("value", roc1, "radius");
            slider5.setText("Radius Ring 1:");
            slider5.setBorder(null);
            slider5.setValue(defaultRadius);

            slider6 = new PropertyDouble();
            slider6.setMinimum(-10);
            slider6.setMaximum(10);
            slider6.setBounds(35, 670, 415, 50);
            slider6.setPaintTicks(true);
            slider6.addRoute("value", roc2, "current");
            slider6.setText("Current Ring 2:");
            slider6.setBorder(null);
            slider6.setValue(1.0);

            slider3 = new PropertyDouble();
            slider3.setMinimum(0);
            slider3.setMaximum(3);
            slider3.setBounds(35, 730, 415, 50);
            slider3.setPaintTicks(true);
            slider3.addRoute("value", roc2, "radius");
            slider3.setText("Radius Ring 2:");
            slider3.setBorder(null);
            slider3.setValue(defaultRadius);
            
            ControlGroup controls = new ControlGroup();
            controls.setText("Parameters");
            controls.add(slider2);
            controls.add(slider5);
            controls.add(slider6);
            controls.add(slider3);
            
            if(false){
            slider7 = new PropertyDouble();
            slider7.setMinimum(0);
            slider7.setMaximum(10);
            slider7.setBounds(35, 670, 415, 50);
            slider7.setPaintTicks(true);
            slider7.addRoute("value", colorizer, "saturationPoint");
            slider7.setText("Saturation Point:");
            slider7.setBorder(null);
            slider7.setValue(1.0);
            
            slider8 = new PropertyDouble();
            slider8.setMinimum(0);
            slider8.setMaximum(3);
            slider8.setBounds(35, 670, 415, 50);
            slider8.setPaintTicks(true);
            slider8.addRoute("value", colorizer, "fallOff");
            slider8.setText("FallOff");
            slider8.setBorder(null);
            slider8.setValue(1.0);
            
            slider9 = new PropertyDouble();
            slider9.setMinimum(0);
            slider9.setMaximum(1.0);
            slider9.setBounds(35, 670, 415, 50);
            slider9.setPaintTicks(true);
            slider9.addRoute("value", colorizer, "hue");
            slider9.setText("Hue:");
            slider9.setBorder(null);
            slider9.setValue(0.65);
            
            
           
            controls.add(slider7);
            controls.add(slider8);
            controls.add(slider9);
    }
            visControl = new VisualizationControl();
            visControl.setFieldConvolution(mDLIC);
            visControl.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
            visControl.setFieldVisGrid(fv);
            sim.addElement(controls);
            sim.addElement(visControl);
        
            sim.getEngine().setDeltaTime(0.5);

            //addSimActions();
           thePlayer.load(sim);
           sim.getEngineControl().init();
           //TDebug.setOutput(System.out);
           
           System.out.println( sim.getID() + " Created");
           colorizer.setSaturationPoint(0.150);
           colorizer.setFallOff(0.015);

    }
    
}
