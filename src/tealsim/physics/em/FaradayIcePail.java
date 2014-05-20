/* $Id: FaradayIcePail.java,v 1.9 2010/09/22 15:48:11 pbailey Exp $ */

/**
 * A demonstration implementation of the TFramework.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.9 $
 */

package tealsim.physics.em;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.swing.*;
import javax.vecmath.*;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.Rendered;
import teal.render.primitives.Pipe;

import teal.render.viewer.*;
import teal.sim.collision.*;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.TEngineControl;
import teal.physics.physical.PhysicalObject;
import teal.physics.em.PointCharge;
import teal.sim.simulation.*;
import teal.sim.spatial.*;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.visualization.dlic.DLIC;
import teal.physics.em.SimEM;

import teal.util.TDebug;

public class FaradayIcePail extends SimEM implements SelectListener {

    private static final long serialVersionUID = 3257846575882646838L;

    JButton groundButton = null;
    JButton ungroundButton = null;
    PropertyDouble slider1 = null;
   
    ControlGroup controls;
    VisualizationControl visGroup;
    
    protected FieldConvolution mDLIC = null;

    final private int N = 20;
    private PointCharge[] pointCharges = new PointCharge[2 * N];
    private PointCharge centralCharge = null;
    private double pointChargeRadius = 0.3;
    //	ArrayList outerWalls, innerWalls;

    Rendered ring1, ring2;
    PhysicalObject cylinder1, cylinder2;

    public FaradayIcePail() {

        super();
        title = "Faraday's Ice Pail";
        TDebug.setGlobalLevel(0);

        // Building the world.
        setBoundingArea(new BoundingSphere(new Point3d(), 8));
        setDeltaTime(0.25);
        setDamping(0.1);
        setGravity(new Vector3d(0., 0., 0.));
        //		theEngine.setShowTime(true);
        
        //mViewer.setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM | TViewer.VP_TRANSLATE);
        mDLIC = new FieldConvolution();
        //mDLIC.setSize(new Dimension(1024,1024));
        RectangularPlane rec = new RectangularPlane(new Vector3d(-12., -12., 0.), new Vector3d(-12., 12., 0.),
            new Vector3d(12., 12., 0.));
        mDLIC.setComputePlane(rec);
     
        // Creating components.

        cylinder1 = new PhysicalObject();
        CylindricalWallCollisionController cwcc1 = new CylindricalWallCollisionController(cylinder1);
        cwcc1.setTolerance(0.1);
        cwcc1.setDirection(new Vector3d(0., 0., 1.));
        cwcc1.setRadius(8.);
        cylinder1.setCollisionController(cwcc1);
        cylinder1.setColliding(true);
        addElement(cylinder1);

        double thickness1 = 0.1;
        double radius1 = 8.;
        ring1 = new Pipe(radius1 + thickness1 / 2., thickness1, 2.);
     
        ring1.setPickable(false);

        ring1.setColor(new Color3f(Color.ORANGE));
        //		node1.setTransparency(0.1f);
        ring1.setDirection(new Vector3d(0., 0., 1.));
        addElement(ring1);

        cylinder2 = new PhysicalObject();
        CylindricalWallCollisionController cwcc2 = new CylindricalWallCollisionController(cylinder2);
        cwcc2.setTolerance(0.1);
        cwcc2.setDirection(new Vector3d(0., 0., 1.));
        cwcc2.setRadius(5.);
        cylinder2.setCollisionController(cwcc2);
        cylinder2.setColliding(true);
        addElement(cylinder2);

        double thickness2 = 0.1;
        double radius2 = 5.;
        ring2 = new Pipe(radius2 - thickness2 / 2., thickness2, 2.);
       
        ring2.setPickable(false);
        ring2.setDirection(new Vector3d(0., 0., 1.));
        ring2.setColor(Color.ORANGE);
        //ring2.setTransparency(0.1f);
        addElement(ring2);

        // -> Point Charges
        int pos = N;
        int neg = N;
        for (int i = 0; i < 2 * N; i++) {
            pointCharges[i] = new PointCharge();
            pointCharges[i].setRadius(pointChargeRadius);
            pointCharges[i].setMass(1.0);

            double charge = Math.random() > 0.5 ? 1. : -1.;
            if (charge > 0.) {
                if (pos == 0)
                    charge = -1.;
                else pos--;
            }
            if (charge < 0.) {
                if (neg == 0)
                    charge = 1.;
                else neg--;
            }
            pointCharges[i].setCharge(charge);

            pointCharges[i].setGeneratingP(false);

            pointCharges[i].setID("pointCharge" + i);
            pointCharges[i].setPickable(false);
            pointCharges[i].setColliding(true);
            SphereCollisionController sccx = new SphereCollisionController(pointCharges[i]);
            sccx.setRadius(pointChargeRadius);
            sccx.setTolerance(0.5);
            //			sccx.setMode(SphereCollisionController.WALL_SPHERE );
            pointCharges[i].setCollisionController(sccx);
            addElement(pointCharges[i]);
        }
        resetPointCharges();

        centralCharge = new PointCharge();
        centralCharge.setPosition(new Vector3d(0.,0.,0.));
        centralCharge.setRadius(2.);
        centralCharge.setPauliDistance(pointChargeRadius * 2.);
        centralCharge.setMass(1.0);
        centralCharge.setCharge(0.);
        centralCharge.setID("centralCharge");
        centralCharge.setPickable(true);
        centralCharge.setMoveable(false);
        centralCharge.setColliding(false);
        addElement(centralCharge);

        slider1 = new PropertyDouble();
        slider1.setPrecision(1);
        slider1.setMinimum(-100.);
        slider1.setMaximum(100.);
        //slider1.setBounds(40, 515, 415, 50);
        slider1.setPaintTicks(true);
        slider1.addRoute(centralCharge, "charge");
        slider1.setValue(0.);
        slider1.setText("Central Charge");
        slider1.setBorder(null);

        controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(slider1);
        
        TealAction ta = new TealAction("Charging by Induction", this);
        addAction("Help", ta);


        groundButton = new JButton(new TealAction("Ground", "Ground", this));
        groundButton.setFont(groundButton.getFont().deriveFont(Font.BOLD));
        groundButton.setBounds(40, 570, 195, 24);
        controls.add(groundButton);

        ungroundButton = new JButton(new TealAction("Unground", "Unground", this));
        ungroundButton.setFont(ungroundButton.getFont().deriveFont(Font.BOLD));
        ungroundButton.setBounds(40, 600, 195, 24);
        controls.add(ungroundButton);
        
        visGroup = new VisualizationControl();
        visGroup.setFieldConvolution(mDLIC);
        visGroup.setConvolutionModes(DLIC.DLIC_FLAG_E | DLIC.DLIC_FLAG_EP);
        
        addElement(controls);
        addElement(visGroup);
     
        addSelectListener(this);
        //mSEC.init();
        //mFramework.doStatus(0);
        resetCamera();
        reset();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Charging by Induction") == 0) {
            if ((mFramework != null) && (mFramework instanceof TFramework)) {
                ((TFramework)mFramework).openBrowser("help/chargebyinduction.html");
            }
        } else if (e.getActionCommand().compareToIgnoreCase("Ground") == 0) {
            ground();
        } else if (e.getActionCommand().compareToIgnoreCase("Unground") == 0) {
            unground();
        } else {
            super.actionPerformed(e);
        }
    }

    private void ground() {
 
        ring1.setDrawn(false);
        cylinder1.setColliding(false);

    }

    private void unground() {

        int state = mSEC.getSimState();
        if (state == TEngineControl.RUNNING) {
            mSEC.stop();
        }

        ring1.setDrawn(true);
        cylinder1.setColliding(true);
        if(theEngine != null){
        	theEngine.requestReorder(cylinder1);

        	for (int i = 0; i < pointCharges.length; i++) {
        		theEngine.requestReorder(pointCharges[i]);
        	}
        }
        if (state == TEngineControl.RUNNING) {
            mSEC.start();
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {
        super.propertyChange(pce);
    }

    public void reset() {
        
        mSEC.stop();
        resetCamera();

        unground();
        resetPointCharges();
    }

    private void resetPointCharges() {
        Point3d[] positions = new Point3d[2 * N];
        Point3d position = null;
        double r1 = 5. + pointChargeRadius * 1.1;
        double r2 = 8. * Math.cos(Math.PI / 5.) - pointChargeRadius * 1.1;
        for (int i = 0; i < 2 * N; i++) {
            double ang = 2. * Math.PI * (double) i / (2. * (double) N);
            boolean distinct = true;
            do {
                double rad = (r2 - r1) * Math.random() + r1;
                //				double ang = 2.*Math.PI*Math.random();
                position = new Point3d(rad * Math.cos(ang), rad * Math.sin(ang), 0.);
                if (i > 0) {
                    if (position.distance(positions[i - 1]) < pointChargeRadius * 1.1) {
                        //System.out.println("i: " + i + ", Touching: " + position + " and " + positions[i - 1]);
                        distinct = false;
                    }
                }
            } while (!distinct);
            positions[i] = position;
            pointCharges[i].setPosition(new Vector3d(position), true);
            pointCharges[i].setVelocity(new Vector3d());
        }
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 1), new Point3d(), new Vector3d(0., 1., 0.));

    }

    public synchronized void dispose() {
        super.dispose();
    }

    public void processSelection(SelectEvent se) {
        TDebug.println(1, se.getSource() + " select state = " + se.getStatus());
    }

    

}
