/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TealSimApp.java,v 1.14 2010/09/28 21:40:41 pbailey Exp $
 * 
 */
package tealsim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import java.util.Set;
import javax.swing.UIManager;

import teal.app.SimPlayer;
import teal.app.SimPlayerApp;
import teal.framework.TealAction;
import teal.render.jme.TealWorldManager;
import teal.render.scene.SceneFactory;
import teal.sim.simulation.TSimulation;
import teal.sim.simulation.Simulation3D;
import tealsim.util.RuntimeArgument;
import tealsim.util.RuntimeArgumentList;

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
public class TealSimApp extends SimPlayerApp implements ActionListener {

  private static final long serialVersionUID = 3258689927121220656L;
  public boolean checkFrameRate = false;

  public TealSimApp() {
    super();
  }

  public void addActions() {
    TealAction ta;
    ta = new TealAction("Box Induction", "tealsim.physics.em.boxInduction", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Capacitor", "tealsim.physics.em.Capacitor", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("CapacitorOO", "tealsim.physics.em.CapacitorOO", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Charge By Induction", "tealsim.physics.em.ChargeByInduction", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Charge in Magnetic Field Game", "tealsim.physics.em.ChargeInMagneticFieldGame", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Charged Metal Slab", "tealsim.physics.em.ChargedMetalSlab2", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Charges In A Box", "tealsim.physics.em.ChargesInBox", this);
    thePlayer.addAction("Electro-Magnetic", ta);

    ta = new TealAction("Conducting Spherical Shielding", "tealsim.physics.em.ConductingSphericalShellShielding", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Coulomb's Law", "tealsim.physics.em.CoulombsLaw", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Dipole Induction", "tealsim.physics.em.DipoleInduction", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Electrostatic Force", "tealsim.physics.em.ElectrostaticForce", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("EM Radiator", "tealsim.physics.em.EMRadiatorApp", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("EM Videogame", "tealsim.physics.em.EMVideogame", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("EM Team Videogame", "tealsim.physics.em.TeamEMGame", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("EM Zoo", "tealsim.physics.em.EMZoo", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("ExB Drift", "tealsim.physics.em.ExBDrift", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Exploring Electric Field", "tealsim.physics.em.ExploringElectricField", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Exploring Potential", "tealsim.physics.em.ExploringPotential", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Falling Coil", "tealsim.physics.em.FallingCoil", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Falling Coil Cylindrical Magnet", "tealsim.physics.em.FallingCoilCylindricalMagnet", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Falling Magnet", "tealsim.physics.em.FallingMagnet", this);
    thePlayer.addAction("Electro-Magnetic", ta);

    ta = new TealAction("Faraday's Law", "tealsim.physics.em.FaradaysLaw", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Faraday's Law Cylindrcal Magnet", "tealsim.physics.em.FaradaysLawCylindricalMagnet", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Faraday Ice Pail", "tealsim.physics.em.FaradayIcePail", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Faraday Ice Pail Shield", "tealsim.physics.em.FaradayIcePailShieldLineCharges", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Faraday's Law Rotation", "tealsim.physics.em.FaradaysLawRotation", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Faraday's Law Two Coils", "tealsim.physics.em.FaradaysLawTwoCoils", this);
    thePlayer.addAction("Electro-Magnetic", ta);

    ta = new TealAction("Floating Coil", "tealsim.physics.em.FloatingCoil", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Gauss Law Flux", "tealsim.physics.em.GaussLawFlux", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Gauss Law Mag Dipole", "tealsim.physics.em.GaussLawMagDipoleDisk", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Gauss Law Symmetry", "tealsim.physics.em.GaussLawSymmetry", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Mapping Fields", "tealsim.physics.em.MappingFields", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Pentagon", "tealsim.physics.em.Pentagon", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Point Charges", "tealsim.physics.em.PCharges", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Radiating Charge", "tealsim.physics.em.RadiationCharge", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Seafloor", "tealsim.physics.em.SeafloorApp", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("TeachSpin", "tealsim.physics.em.TeachSpin", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("TeamEMGame", "tealsim.physics.em.TeamEMGame", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Torque on an Electric Dipole", "tealsim.physics.em.TorqueOnDipoleE", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Torque on a Magnetic Dipole", "tealsim.physics.em.TorqueOnDipoleB", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Two Rings", "tealsim.physics.em.TwoRings", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Van deGraff", "tealsim.physics.em.VandeGraff", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("Wire and Magnet", "tealsim.physics.em.WireAndMagnet", this);
    thePlayer.addAction("Electro-Magnetic", ta);
    ta = new TealAction("LabVIW TeachSpin", "tealsim.physics.ilab.TeachSpinLV", this);
    thePlayer.addAction("Electro-Magnetic", ta);


    ta = new TealAction("Celestial Mechanics", "tealsim.physics.mech.CelestialApp", this);
    thePlayer.addAction("Mechanics", ta);
    ta = new TealAction("Circular Motion", "tealsim.physics.mech.CircularMotion", this);
    thePlayer.addAction("Mechanics", ta);
    ta = new TealAction("Inclined Plane", "tealsim.physics.mech.InclinedPlaneApp", this);
    thePlayer.addAction("Mechanics", ta);
    ta = new TealAction("Acceleration on an Inclined Plane", "tealsim.physics.mech.GalileosInclinedPlane", this);
    thePlayer.addAction("Mechanics", ta);
    ta = new TealAction("Newton's Cradle", "tealsim.physics.mech.NewtonsCradle", this);
    thePlayer.addAction("Mechanics", ta);
    ta = new TealAction("Simple Pendulum", "tealsim.physics.mech.SimplePendulumApp", this);
    thePlayer.addAction("Mechanics", ta);
    ta = new TealAction("Impulse Pendulum", "tealsim.physics.mech.SimpleImpulsePendulumApp", this);
    thePlayer.addAction("Mechanics", ta);
    ta = new TealAction("Wieghted Pendulum", "tealsim.physics.mech.WeightedPhysicalPendulumApp", this);
    thePlayer.addAction("Mechanics", ta);

    ta = new TealAction("Projectile", "tealsim.physics.mech.ProjectileApp", this);
    thePlayer.addAction("Mechanics", ta);
    ta = new TealAction("Rigid Bar", "tealsim.physics.mech.RigidBarApp", this);
    thePlayer.addAction("Mechanics", ta);

    ta = new TealAction("Example 01", "tealsim.physics.examples.Example_01", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 02", "tealsim.physics.examples.Example_02", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 03", "tealsim.physics.examples.Example_03", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 04", "tealsim.physics.examples.Example_04", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 05", "tealsim.physics.examples.Example_05", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 06", "tealsim.physics.examples.Example_06", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 06a", "tealsim.physics.examples.Example_06a", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 07", "tealsim.physics.examples.Example_07", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 07a", "tealsim.physics.examples.Example_07a", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 08", "tealsim.physics.examples.Example_08", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 08a", "tealsim.physics.examples.Example_08a", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 09", "tealsim.physics.examples.Example_09", this);
    thePlayer.addAction("Examples", ta);
    ta = new TealAction("Example 10", "tealsim.physics.examples.Example_10", this);
    thePlayer.addAction("Examples", ta);

    ta = new TealAction("Remove Sim", "Remove Sim", this);
    thePlayer.addAction("View", ta);
    ta = new TealAction("Use Java3D", "Use Java3D", this);
    thePlayer.addAction("View", ta);
    ta = new TealAction("Use JME", "Use JME", this);
    thePlayer.addAction("View", ta);
  }

  public void actionPerformed(ActionEvent e)
          throws IllegalArgumentException {
    if (e.getActionCommand().compareTo("Use Java3D") == 0) {
      SceneFactory.setFactory("teal.render.j3d.SceneFactoryJ3D");
    }
    else if (e.getActionCommand().compareTo("Use JME") == 0) {
      SceneFactory.setFactory("teal.render.jme.SceneFactoryJME");
    }
    else if (e.getActionCommand().compareTo("Remove Sim") == 0) {
      thePlayer.removeSim();
    }
    else {
      thePlayer.loadSimClass(e.getActionCommand());
      //TDebug.println("Target FrameRate: " + ((Simulation3D)thePlayer.getTSimulation()).getEngine().getFrameRate());
      if (checkFrameRate) {
        ((Simulation3D) thePlayer.getTSimulation()).getEngine().setCheckFrameRate(checkFrameRate);
      }
    }
  }

  public static void main(String[] args) {
    try {
      runTealSim(args);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static TealSimApp runTealSim(String[] args) throws Exception {

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    TealSimApp teal_sim = new TealSimApp();
    teal_sim.init(args);

    return teal_sim;
  }

  public void init(String[] args) throws Exception {

    addWindowListener(new WindowAdapter() {

      public void windowClosing(WindowEvent e) {
        closeApplication();
      }
    });

    new RuntimeArgumentList(args).execute(this);    

    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }

  public void closeApplication() {
    ((Simulation3D) thePlayer.getTSimulation()).getEngine().not();
    TealWorldManager.shutdown();
    dispose();
  }

  
}
