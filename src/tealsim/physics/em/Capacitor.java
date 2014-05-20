/* $Id: Capacitor.java,v 1.16 2010/08/10 18:12:33 stefan Exp $ */

/**
 * Conversion of the application to use the TSimulation interface.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.16 $
 */

package tealsim.physics.em;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import teal.render.BoundingSphere;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
import teal.ui.swing.JCollapsiblePane;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

/**
 * This is a simple simulation of charged particles moving inside a capacitor.
 *
 */
public class Capacitor extends SimEM {

    private static final long serialVersionUID = 3761121648103207990L;

    protected FieldConvolution mDLIC = null;

    JTextField plate1Number;
    JTextField plate2Number;
    JTextField plate1Charge;
    JTextField plate2Charge;

    JLabel plate1Number_label;
    JLabel plate2Number_label;
    JLabel plate1Charge_label;
    JLabel plate2Charge_label;

    final private int N = 12;

    // Dimensions
    Vector3d plate1_position = new Vector3d(0., 5., 0.);
    Vector3d plate2_position = new Vector3d(0., -5., 0.);
    double plate_length = 25.;
    double plate_width = 12.5;
    double plate_height = 5.;
    double pc_radius = 0.5;
    double pc_charge = 5.;
    private int labelWidth = 200;

    public Capacitor() {

        super();

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

        // -> Positive-end Conductor
        RectangularBox conductor1 = new RectangularBox();
        conductor1.setPosition(plate1_position);
        conductor1.setOrientation(new Vector3d(1., 0., 0.));
        conductor1.setNormal(new Vector3d(0., 1., 0.));
        conductor1.setLength(plate_length);
        conductor1.setWidth(plate_width);
        conductor1.setHeight(plate_height);
        Collection<Wall> walls1 = conductor1.getWalls();
        Iterator it = walls1.iterator();
        while (it.hasNext()) {
            Wall wall = (Wall) it.next();
            wall.getCollisionController().setTolerance(0.1);
            addElement(wall);
        }

        //addElements(walls1);

        // -> Negative-end Conductor
        RectangularBox conductor2 = new RectangularBox();
        conductor2.setPosition(plate2_position);
        conductor2.setOrientation(new Vector3d(1., 0., 0.));
        conductor2.setNormal(new Vector3d(0., 1., 0.));
        conductor2.setLength(plate_length);
        conductor2.setWidth(plate_width);
        conductor2.setHeight(plate_height);
        Collection<Wall> walls2 = conductor2.getWalls();
        it = walls2.iterator();
        while (it.hasNext()) {
            Wall wall = (Wall) it.next();
            wall.getCollisionController().setTolerance(0.1);
            addElement(wall);

        }

        //addElements(walls2);

        // -> Point Charges
        addChargesToList1(N);
        addChargesToList2(N);

        // -> Text fields and labels.
        plate1Number_label = new JLabel("Plate 1 - Number of charges: ");
        plate1Number_label.setPreferredSize(new Dimension(labelWidth, plate1Number_label.getPreferredSize().height));
        plate1Number_label.setHorizontalAlignment(SwingConstants.LEFT);
        //guiElements.add(plate1Number_label);

        plate1Number = new JTextField();
        plate1Number.setColumns(4);
        plate1Number.setHorizontalAlignment(SwingConstants.RIGHT);
        plate1Number.setText(String.valueOf(N));
        plate1Number.addActionListener(this);
        //guiElements.add(plate1Number_label);

        UIPanel Plate1Panel = new UIPanel();
        Plate1Panel.add(plate1Number_label);
        Plate1Panel.add(plate1Number);
        //guiElements.add(plate1Panel);

        plate1Charge_label = new JLabel("Individual particle charge: ");
        plate1Charge_label.setPreferredSize(new Dimension(labelWidth, plate1Charge_label.getPreferredSize().height));
        plate1Charge_label.setHorizontalAlignment(SwingConstants.LEFT);

        plate1Charge = new JTextField();
        plate1Charge.setColumns(4);
        plate1Charge.setHorizontalAlignment(SwingConstants.RIGHT);
        plate1Charge.setText(String.valueOf(pc_charge));
        plate1Charge.addActionListener(this);

        UIPanel plate1ChargePanel = new UIPanel();
        plate1ChargePanel.add(plate1Charge_label);
        plate1ChargePanel.add(plate1Charge);
        //guiElements.add(plate1ChargePanel);

        plate2Number_label = new JLabel("Plate 2 - Number of charges: ");
        plate2Number_label.setPreferredSize(new Dimension(labelWidth, plate2Number_label.getPreferredSize().height));
        plate2Number_label.setHorizontalAlignment(SwingConstants.LEFT);

        plate2Number = new JTextField();
        plate2Number.setColumns(4);
        plate2Number.setHorizontalAlignment(SwingConstants.RIGHT);
        plate2Number.setText(String.valueOf(N));
        plate2Number.addActionListener(this);

        UIPanel Plate2Panel = new UIPanel();
        Plate2Panel.add(plate2Number_label);
        Plate2Panel.add(plate2Number);
        //guiElements.add(plate2Panel);

        plate2Charge_label = new JLabel("Individual particle charge: ");
        plate2Charge_label.setPreferredSize(new Dimension(labelWidth, plate2Charge_label.getPreferredSize().height));
        plate2Charge_label.setHorizontalAlignment(SwingConstants.LEFT);

        plate2Charge = new JTextField();
        plate2Charge.setColumns(4);
        plate2Charge.setHorizontalAlignment(SwingConstants.RIGHT);
        plate2Charge.setText(String.valueOf(-pc_charge));
        plate2Charge.addActionListener(this);

        UIPanel plate2ChargePanel = new UIPanel();
        plate2ChargePanel.add(plate2Charge_label);
        plate2ChargePanel.add(plate2Charge);
        //guiElements.add(plate2ChargePanel);

  
        ControlGroup controls = new ControlGroup();
//        JCollapsiblePane controls = new JCollapsiblePane();
        controls.setText("Parameters");
//        addElement(Plate2Panel);
        controls.add(Plate1Panel);
        controls.add(plate1ChargePanel);
        controls.add(Plate2Panel);
        controls.add(plate2ChargePanel);
        addElement(controls);
        VisualizationControl vizPanel = new VisualizationControl();
        vizPanel.setConvolutionModes(DLIC.DLIC_FLAG_E |DLIC.DLIC_FLAG_EP);
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

    }

    void addActions() {
        TealAction ta = new TealAction("Capacitor", this);
        addAction("Help", ta);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println("Action: " + command);
        if (e.getActionCommand().compareToIgnoreCase("Capacitor") == 0) {
            if ((mFramework != null) && (mFramework instanceof TFramework)) {
                ((TFramework)mFramework).openBrowser("help/capacitor.html");
            } else {
                TDebug.println("mFramework is null!");
            }
        } else if (e.getSource() == plate1Number) {
            int value = 0;
            try {
                value = Integer.parseInt(plate1Number.getText());
            } catch (NumberFormatException exception) {
                return;
            }
            if (value < 0) {
                value = 0;
                plate1Number.setText("0");
            }
            int change = value - pcList1.size();
            if (change > 0) {
                addChargesToList1(change);
            } else {
                removeChargesFromList1(-change);
            }
        } else if (e.getSource() == plate2Number) {
            int value = 0;
            try {
                value = Integer.parseInt(plate2Number.getText());
            } catch (NumberFormatException exception) {
                return;
            }
            if (value < 0) {
                value = 0;
                plate2Number.setText("0");
            }
            int change = value - pcList2.size();
            if (change > 0) {
                addChargesToList2(change);
            } else {
                removeChargesFromList2(-change);
            }
        } else if (e.getSource() == plate1Charge) {
            double value = 0.;
            try {
                value = Double.parseDouble(plate1Charge.getText());
            } catch (NumberFormatException exception) {
                return;
            }
            for (int i = 0; i < pcList1.size(); i++) {
                PointCharge pc = (PointCharge) pcList1.get(i);
                pc.setCharge(value);
            }
        } else if (e.getSource() == plate2Charge) {
            double value = 0.;
            try {
                value = Double.parseDouble(plate2Charge.getText());
            } catch (NumberFormatException exception) {
                return;
            }
            for (int i = 0; i < pcList2.size(); i++) {
                PointCharge pc = (PointCharge) pcList2.get(i);
                pc.setCharge(value);
            }
        } else {
            super.actionPerformed(e);
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {
        super.propertyChange(pce);
    }

    public void reset() {
        resetPointCharges();
        //resetCamera();

    }

    private void resetPointCharges() {
        removeChargesFromList1(pcList1.size());
        removeChargesFromList2(pcList2.size());
        int N1 = 0;
        try {
            N1 = Integer.parseInt(plate1Number.getText());
        } catch (NumberFormatException e) {
            N1 = N;
            plate1Number.setText(String.valueOf(N));
        }
        if (N1 < 0) {
            N1 = 0;
            plate1Number.setText("0");
        }
        int N2 = 0;
        try {
            N2 = Integer.parseInt(plate2Number.getText());
        } catch (NumberFormatException e) {
            N2 = N;
            plate2Number.setText(String.valueOf(N));
        }
        if (N2 < 0) {
            N2 = 0;
            plate2Number.setText("0");
        }
        addChargesToList1(N1);
        addChargesToList2(N2);

        double charge1 = 0;
        try {
            charge1 = Double.parseDouble(plate1Charge.getText());
        } catch (NumberFormatException e) {
            charge1 = pc_charge;
            plate1Charge.setText(String.valueOf(charge1));
        }
        double charge2 = 0;
        try {
            charge2 = Double.parseDouble(plate2Charge.getText());
        } catch (NumberFormatException e) {
            charge2 = -pc_charge;
            plate2Charge.setText(String.valueOf(charge2));
        }
        for (int i = 0; i < pcList1.size(); i++) {
            PointCharge pc = (PointCharge) pcList1.get(i);
            pc.setCharge(charge1);
        }
        for (int i = 0; i < pcList2.size(); i++) {
            PointCharge pc = (PointCharge) pcList2.get(i);
            pc.setCharge(charge2);
        }
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 2.5), new Point3d(), new Vector3d(0., 1., 0.));

    }

    ArrayList<PointCharge> pcList1 = new ArrayList<PointCharge>();
    ArrayList<PointCharge> pcList2 = new ArrayList<PointCharge>();

    public void addChargesToList1(int number) {
        for (int k = 0; k < number; k++) {
            Vector3d position = new Vector3d();
            int count = 0;
            do {
                count++;
                double x = (2. * Math.random() - 1.) * (plate_length / 2. - pc_radius);
                double y = (2. * Math.random() - 1.) * (plate_height / 2. - pc_radius);
                double z = (2. * Math.random() - 1.) * (plate_width / 2. - pc_radius);
                position.set(x, y, z);
                position.add(plate1_position);
            } while (!isValidList1Position(position));

            double charge = 0.;
            if (!pcList1.isEmpty())
                charge = ((PointCharge) pcList1.get(0)).getCharge();
            else charge = pc_charge;

            PointCharge pc = new PointCharge();
            pc.setPosition(position);
            pc.setCharge(charge);
            pc.setRadius(pc_radius);

            pc.setMass(1.0);
            pc.setID("pcList1_charge" + pcList1.size());
            pc.setPickable(false);
            pc.setColliding(true);
            pc.setGeneratingP(false);

            SphereCollisionController scc = new SphereCollisionController(pc);
            scc.setRadius(pc_radius);
            scc.setTolerance(0.5);
            scc.setMode(SphereCollisionController.WALL_SPHERE);
            pc.setCollisionController(scc);
            addElement(pc);
            pcList1.add(pc);
        }
    }

    public void addChargesToList2(int number) {
        for (int k = 0; k < number; k++) {
            Vector3d position = new Vector3d();
            int count = 0;
            do {
                count++;
                double x = (2. * Math.random() - 1.) * (plate_length / 2. - pc_radius);
                double y = (2. * Math.random() - 1.) * (plate_height / 2. - pc_radius);
                double z = (2. * Math.random() - 1.) * (plate_width / 2. - pc_radius);
                position.set(x, y, z);
                position.add(plate2_position);
            } while (!isValidList2Position(position));

            double charge = 0.;
            if (!pcList2.isEmpty())
                charge = ((PointCharge) pcList2.get(0)).getCharge();
            else charge = -pc_charge;

            PointCharge pc = new PointCharge();
            pc.setPosition(position);
            pc.setCharge(charge);
            pc.setRadius(pc_radius);

            pc.setMass(1.0);
            pc.setID("pcList2_charge" + pcList2.size());
            pc.setPickable(false);
            pc.setColliding(true);
            pc.setGeneratingP(false);

            SphereCollisionController scc = new SphereCollisionController(pc);
            scc.setRadius(pc_radius);
            scc.setTolerance(0.5);
            scc.setMode(SphereCollisionController.WALL_SPHERE);
            pc.setCollisionController(scc);
            addElement(pc);
            pcList2.add(pc);
        }
    }

    public boolean isValidList1Position(Vector3d position) {
        for (int i = 0; i < pcList1.size(); i++) {
            PointCharge pc = (PointCharge) pcList1.get(i);
            Point3d pcPosition = new Point3d(pc.getPosition());
            if (pcPosition.distance(new Point3d(position)) < pc_radius * 1.5) {
                return false;
            }
        }
        if (Math.abs(position.x - plate1_position.x) > plate_length / 2. - pc_radius) return false;
        if (Math.abs(position.y - plate1_position.y) > plate_height / 2. - pc_radius) return false;
        if (Math.abs(position.z - plate1_position.z) > plate_width / 2. - pc_radius) return false;
        return true;
    }

    public boolean isValidList2Position(Vector3d position) {
        for (int i = 0; i < pcList2.size(); i++) {
            PointCharge pc = (PointCharge) pcList2.get(i);
            Point3d pcPosition = new Point3d(pc.getPosition());
            if (pcPosition.distance(new Point3d(position)) < pc_radius * 1.5) {
                return false;
            }
        }
        if (Math.abs(position.x - plate2_position.x) > plate_length / 2. - pc_radius) return false;
        if (Math.abs(position.y - plate2_position.y) > plate_height / 2. - pc_radius) return false;
        if (Math.abs(position.z - plate2_position.z) > plate_width / 2. - pc_radius) return false;
        return true;
    }

    public void removeChargesFromList1(int number) {
        if (number > pcList1.size()) number = pcList1.size();
        for (int k = 0; k < number; k++) {
            int i = pcList1.size() - 1;
            PointCharge pc = (PointCharge) pcList1.get(i);
            removeElement(pc);
            pcList1.remove(i);
        }
    }

    public void removeChargesFromList2(int number) {
        if (number > pcList2.size()) number = pcList2.size();
        for (int k = 0; k < number; k++) {
            int i = pcList2.size() - 1;
            PointCharge pc = (PointCharge) pcList2.get(i);
            removeElement(pc);
            pcList2.remove(i);
        }
    }

}
