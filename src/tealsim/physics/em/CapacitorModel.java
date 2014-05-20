/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CapacitorModel.java, v1.00 15/07/2011 9:16:16 AM cschratter <chrisi@mit.edu> Exp $
 * 
 */
package tealsim.physics.em;

//import java.util.logging.Logger;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import teal.physics.em.PointCharge;
import teal.physics.em.SimEM;
import teal.physics.physical.RectangularBox;
import teal.physics.physical.Wall;
import teal.sim.collision.SphereCollisionController;
import teal.ui.UIPanel;

/**
 *
 * @author cschratter
 */
public class CapacitorModel implements Serializable {
    //reference to the simulation this capacitor is part of
    SimEM simulation_;
    
    String id_;
    
    //this capacitors controls
    public JFormattedTextField tf_charges_;
    public JFormattedTextField tf_charge_value_;
    public JLabel lbl_charges_;
    public JLabel lbl_charge_value_;
    
    //this capacitors position
    public Vector3d position_;
    //this capacitors collection of single charges rendered on the UI
    public ArrayList<PointCharge> pc_list_ = new ArrayList<PointCharge>();

    public double charge_;
    
    public UIPanel pnl_charges_ = new UIPanel();
    public UIPanel pnl_charge_value_ = new UIPanel();
    
    PropertyChangeListener color_toggle_ = new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
//        System.out.println("edit Valid changed to -> " + (Boolean) evt.getNewValue());
        Object source = evt.getSource();
        if (source instanceof JFormattedTextField) {
          if ((Boolean) evt.getNewValue()) //field is valid - show it white
          {
            ((JFormattedTextField) source).setBackground(new Color(255, 255, 255));
            ((JFormattedTextField) source).setToolTipText(null);
          }
          else { //field is invalid - show it red
            ((JFormattedTextField) source).setBackground(new Color(255, 170, 170));
            ((JFormattedTextField) source).setToolTipText("Input invalid!");
          }
        }
      }
    };
    
    public CapacitorModel() {
      
    }
    
    public CapacitorModel(SimEM simulation, Vector3d position, double initialCharge, String ID) {
      simulation_ = simulation;
      position_ = position;
      id_ = ID;
      
      RectangularBox conductor = new RectangularBox();
      conductor.setPosition(position);
      conductor.setOrientation(new Vector3d(1., 0., 0.));
      conductor.setNormal(new Vector3d(0., 1., 0.));
      conductor.setLength(CapacitorOO.plate_length);
      conductor.setWidth(CapacitorOO.plate_width);
      conductor.setHeight(CapacitorOO.plate_height);
      Collection<Wall> walls = conductor.getWalls();
      Iterator it = walls.iterator();
      while (it.hasNext()) {
        Wall wall = (Wall) it.next();
        wall.getCollisionController().setTolerance(0.1);
        simulation.addElement(wall);
      }
      
      charge_ = initialCharge;
      //addElements(walls1);

      // -> Point Charges
      addChargesToList(CapacitorOO.N);

      // -> Text fields and labels.
      lbl_charges_ = new JLabel("Capacitor " + ID + "- Number of charges: ");
      lbl_charges_.setPreferredSize(new Dimension(CapacitorOO.labelWidth, lbl_charges_.getPreferredSize().height));
      lbl_charges_.setHorizontalAlignment(SwingConstants.LEFT);
      //guiElements.add(plate1Number_label);

      //naturally only integer values are allowed when setting the amount of charges
      NumberFormatter pos_integer_formatter = new StrictNumberFormatter(NumberFormat.getIntegerInstance());
      pos_integer_formatter.setMinimum(new Integer(0));
      tf_charges_ = new JFormattedTextField(pos_integer_formatter);
      tf_charges_.setValue(new Integer(CapacitorOO.N));
      tf_charges_.setColumns(4);
      tf_charges_.addPropertyChangeListener("value", new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          int change = (Integer) tf_charges_.getValue() - pc_list_.size();
          if (change > 0) {
            addChargesToList(change);
          }
          else {
            removeChargesFromList(-change);
          }
        }
      });
      tf_charges_.addPropertyChangeListener("editValid", color_toggle_);
      tf_charges_.setFocusLostBehavior(JFormattedTextField.COMMIT);
      tf_charges_.setHorizontalAlignment(SwingConstants.RIGHT);
      //guiElements.add(plate1Number_label);


      pnl_charges_.add(lbl_charges_);
      pnl_charges_.add(tf_charges_);
      //guiElements.add(plate1Panel);

      lbl_charge_value_ = new JLabel("Individual particle charge: ");
      lbl_charge_value_.setPreferredSize(new Dimension(CapacitorOO.labelWidth, lbl_charge_value_.getPreferredSize().height));
      lbl_charge_value_.setHorizontalAlignment(SwingConstants.LEFT);


      tf_charge_value_ = new JFormattedTextField(new StrictNumberFormatter(NumberFormat.getInstance()));
      tf_charge_value_.setValue(new Double(charge_));
      tf_charge_value_.setColumns(4);
      tf_charge_value_.addPropertyChangeListener("value", new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          double value = ((Number) tf_charge_value_.getValue()).doubleValue();
          for (PointCharge pc : pc_list_) {
            pc.setCharge(value);
          }
        }
      });
      tf_charge_value_.addPropertyChangeListener("editValid", color_toggle_);
      tf_charge_value_.setFocusLostBehavior(JFormattedTextField.COMMIT);
      tf_charge_value_.setHorizontalAlignment(SwingConstants.RIGHT);

      pnl_charge_value_.add(lbl_charge_value_);
      pnl_charge_value_.add(tf_charge_value_);
      //guiElements.add(plate1ChargePanel);      
    }

    public void addChargesToList(int number) {
      for (int k = 0; k < number; k++) {
        Vector3d position = new Vector3d();
        int count = 0;
        do {
          count++;
          double x = (2. * Math.random() - 1.) * (CapacitorOO.plate_length / 2. - CapacitorOO.pc_radius);
          double y = (2. * Math.random() - 1.) * (CapacitorOO.plate_height / 2. - CapacitorOO.pc_radius);
          double z = (2. * Math.random() - 1.) * (CapacitorOO.plate_width / 2. - CapacitorOO.pc_radius);
          position.set(x, y, z);
          position.add(position_);
        }
        while (!isValidListPosition(position));

        //set the value of this charge to match those already in the list or otherwise the global value
        double charge = pc_list_.isEmpty() ? charge_ : pc_list_.get(0).getCharge();

        PointCharge pc = new PointCharge();
        pc.setPosition(position);
        pc.setCharge(charge);
        pc.setRadius(CapacitorOO.pc_radius);

        pc.setMass(1.0);
        pc.setID("pcList_" + id_ + "_charge" + pc_list_.size());
        pc.setPickable(false);
        pc.setColliding(true);
        pc.setGeneratingP(false);

        SphereCollisionController scc = new SphereCollisionController(pc);
        scc.setRadius(CapacitorOO.pc_radius);
        scc.setTolerance(0.5);
        scc.setMode(SphereCollisionController.WALL_SPHERE);
        pc.setCollisionController(scc);
        simulation_.addElement(pc);
        pc_list_.add(pc);
      }
    }

    public void removeChargesFromList(int number) {
      if (number > pc_list_.size()) {
        number = pc_list_.size();
      }

      for (int k = 0; k < number; k++) {
        int i = pc_list_.size() - 1;
        PointCharge pc = (PointCharge) pc_list_.get(i);
        simulation_.removeElement(pc);
        pc_list_.remove(i);
      }
    }

    public boolean isValidListPosition(Vector3d position) {
      for (PointCharge pc : pc_list_) {
        Point3d pcPosition = new Point3d(pc.getPosition());
        if (pcPosition.distance(new Point3d(position)) < CapacitorOO.pc_radius * 1.5) {
          return false;
        }
      }
      if (Math.abs(position.x - position_.x) > CapacitorOO.plate_length / 2. - CapacitorOO.pc_radius) {
        return false;
      }
      if (Math.abs(position.y - position_.y) > CapacitorOO.plate_height / 2. - CapacitorOO.pc_radius) {
        return false;
      }
      if (Math.abs(position.z - position_.z) > CapacitorOO.plate_width / 2. - CapacitorOO.pc_radius) {
        return false;
      }
      return true;
    }    
    
    public void resetPointCharges() {

      removeChargesFromList(pc_list_.size());
      addChargesToList((Integer) tf_charges_.getValue());

      double value = ((Number) tf_charge_value_.getValue()).doubleValue();
      for (PointCharge pc : pc_list_) {
        pc.setCharge(value);
      }
    }
  
  public static class StrictNumberFormatter extends NumberFormatter {

    @Override
    public Object stringToValue(String text) throws ParseException {
      /* check if we can parse the full string, to avoid Java's default behaviour
       * which only parses until the first unrecognizable character and discard
       * any additional characters
       */
      ParsePosition pos = new ParsePosition(0);
      Object parsed_value = getFormat().parseObject(text, pos);
      if (pos.getIndex() != text.length()) {
        throw new ParseException("Did not parse the complete string", 0);
      }

      /* this will reject the new text if the values before and after
       * rounding do not match
       */
      Object parsedValue = super.stringToValue(text);
//      String expectedText = super.valueToString(parsedValue);
//      if (!super.stringToValue(expectedText).equals(parsedValue)) {
//        throw new ParseException("Rounding occurred", 0);
//      }
      return parsedValue;
    }

    public StrictNumberFormatter(NumberFormat nf) {
      super(nf);
    }
  }
}

