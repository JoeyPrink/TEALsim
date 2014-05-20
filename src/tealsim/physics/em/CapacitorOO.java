/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CapacitorOO.java, v1.00 14/07/2011 2:57:53 PM cschratter <chrisi@mit.edu> Exp $
 * 
 */
package tealsim.physics.em;

//import java.util.logging.Logger;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JFormattedTextField;

import teal.render.BoundingSphere;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.physics.physical.RectangularBox;
import teal.physics.physical.Wall;
import teal.physics.em.SimEM;
import teal.physics.em.PointCharge;
import teal.sim.spatial.FieldConvolution;
import teal.ui.UIPanel;
import teal.ui.control.ControlGroup;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

/**
 * This is a simple simulation of charged particles moving inside a capacitor.
 * The capacitor simulation has been rewritten to follow basic object-oriented programming paradigms.
 * @author cschratter
 */
public class CapacitorOO extends SimEM {

//  private static Logger logger_ = Logger.getLogger(CapacitorOO.class.getName());

  private static final long serialVersionUID = 3761121648103207990L;
  protected FieldConvolution mDLIC = null;
  //number of charges per capacitor
  static final int N = 12;
  // Dimensions
  Vector3d plate1_position = new Vector3d(0., 5., 0.);
  Vector3d plate2_position = new Vector3d(0., -5., 0.);
  Vector3d plate3_position = new Vector3d(0., -15., 0.);
  static double plate_length = 25.;
  static double plate_width = 12.5;
  static double plate_height = 5.;
  static double pc_radius = 0.5;
  static double pc_charge = 5.;
  static int labelWidth = 200;

  CapacitorModel pos_capacitor_;
  CapacitorModel pos_capacitor2_;
  CapacitorModel neg_capacitor_;

  public CapacitorOO() {
    super();
    System.out.println("ROAR - Created an object oriented Capacitor!!! FEAR ME!");
    title = "Capacitor";
    setID("Capacitor");

//      Building the world.
    setDamping(0.02);
    setGravity(new Vector3d(0., 0., 0.));
    //theEngine.setShowTime(true);
    setDeltaTime(0.25);
    //theEngine.setFrameRate(30.);
    setBoundingArea(new BoundingSphere(new Point3d(), 16));

    mDLIC = new FieldConvolution();
    mDLIC.setSize(new Dimension(512, 512));
    mDLIC.setComputePlane(new RectangularPlane(new BoundingSphere(new Point3d(), 18)));

    // Creating components.
    pos_capacitor_ = new CapacitorModel(this, plate1_position, pc_charge, "1");
    neg_capacitor_ = new CapacitorModel(this, plate2_position, pc_charge * -1, "2");
    pos_capacitor2_ = new CapacitorModel(this, plate3_position, pc_charge, "3");

    ControlGroup controls = new ControlGroup();
//        JCollapsiblePane controls = new JCollapsiblePane();
    controls.setText("Parameters");
//        addElement(Plate2Panel);
    controls.add(pos_capacitor_.pnl_charges_);
    controls.add(pos_capacitor_.pnl_charge_value_);
    controls.add(neg_capacitor_.pnl_charges_);
    controls.add(neg_capacitor_.pnl_charge_value_);
    controls.add(pos_capacitor2_.pnl_charges_);
    controls.add(pos_capacitor2_.pnl_charge_value_);

    addElement(controls);
    VisualizationControl vizPanel = new VisualizationControl();
    vizPanel.setConvolutionModes(DLIC.DLIC_FLAG_E | DLIC.DLIC_FLAG_EP);
    vizPanel.setFieldConvolution(mDLIC);

    addElement(vizPanel);

    addActions();

    theScene.setFogEnabled(true);
    resetCamera();
    theScene.setFogTransformFrontScale(0.0);
    //mViewer.setFogTransformBackScale(0.02);
    // initFogTransform() needs to be called in the constructor after resetCamera() if a non-default camera
    // position is being used.
    //mViewer.initFogTransform();

    mSEC.start();
    System.out.println("ROAR - I AM ALIVE");
  }

  void addActions() {
    TealAction ta = new TealAction("Capacitor", this);
    addAction("Help", ta);
  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    System.out.println("Action: " + command);
    //TODO: this is called when???
    if (e.getActionCommand().compareToIgnoreCase("Capacitor") == 0) {
      if ((mFramework != null) && (mFramework instanceof TFramework)) {
        ((TFramework) mFramework).openBrowser("help/capacitor.html");
      }
      else {
        TDebug.println("mFramework is null!");
      }
    }
    else {
      super.actionPerformed(e);
    }
  }

  public void propertyChange(PropertyChangeEvent pce) {
    super.propertyChange(pce);
  }

  public void reset() {
    pos_capacitor_.resetPointCharges();
    pos_capacitor2_.resetPointCharges();
    neg_capacitor_.resetPointCharges();
    //resetCamera();
  }

  @Override
  public void resetCamera() {
    setLookAt(new Point3d(0.0, 0.0, 2.5), new Point3d(), new Vector3d(0., 1., 0.));
  }

  //--------------------------------------------------------------------------
  
}
