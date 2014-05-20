/* $Id: Pentagon2.java,v 1.6 2010/08/10 18:12:34 stefan Exp $ */

/**
 * A demonstration implementation of the TFramework.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.6 $
 */

package tealsim.physics.em;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.viewer.SelectEvent;
import teal.render.viewer.SelectListener;
import teal.render.viewer.TViewer;
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.physical.PentagonBox;
import teal.physics.em.PointCharge;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

public class Pentagon2 extends SimEM implements SelectListener {

    private static final long serialVersionUID = 3256443620520571449L;
 
    private VisualizationControl vis;
    final private int N = 10;
    private PointCharge[] pointCharges = new PointCharge[2 * N];
    PropertyDouble slider1 = null;
    private PointCharge centralCharge = null;
    private double pointChargeRadius = 0.3;
    
    protected FieldConvolution mDLIC = null;

    /*
     * // Interesting symmetry Vector3d [] positions = { new Vector3d(-0.25, 0., 0.), new Vector3d(0., -2., 0.), new
     * Vector3d(0., -1., 0.), new Vector3d(1., 0., 0.), new Vector3d(-1., 0., 0.), new Vector3d(0.5, 0.5, 0.), new
     * Vector3d(0.5, -0.5, 0.), new Vector3d(-0.5, 0.5, 0.), new Vector3d(-0.5, -0.5, 0.), new Vector3d(0.25, 0., 0.), };
     */

    /*
     * // Must also be symmetry, but top corner collision is less than perfect. Vector3d [] positions = { new
     * Vector3d(-0.25, 0., 0.), new Vector3d(0., 1., 0.), new Vector3d(0., -1., 0.), new Vector3d(1., 0., 0.), new
     * Vector3d(-1., 0., 0.), new Vector3d(0.5, 0.5, 0.), new Vector3d(0.5, -0.5, 0.), new Vector3d(-0.5, 0.5, 0.), new
     * Vector3d(-0.5, -0.5, 0.), new Vector3d(0.25, 0., 0.), };
     */
    // No symmetry.
   /* Vector3d[] positions = { new Vector3d(-0.2, 0.25, 0.), new Vector3d(0., 0.9, 0.), new Vector3d(1.5, -0.95, 0.),
            new Vector3d(1.90, 0., 0.), new Vector3d(-1.2, 1., 0.), new Vector3d(1.4, 0.45, 0.),
            new Vector3d(0.75, -0.8, 0.), new Vector3d(-0.75, 0.8, 0.), new Vector3d(-0.7, -0.4, 0.),
            new Vector3d(0.25, -0.2, 0.), new Vector3d(-1.5, -0.5, 0.), new Vector3d(1., -1.9, 0.), new Vector3d(1., -2.4, 0.),
            new Vector3d(-1.95, 1., 0.), new Vector3d(2.1,.5, 0.), new Vector3d(-0.89, -1.45, 0.),
            new Vector3d(-1.75, 1.2, 0.), new Vector3d(1.75, -1.9, 0.), new Vector3d(1.7, 1.4, 0.),
            new Vector3d(-1.25, 1.2, 0.),}; */

    public Pentagon2() {

        super();
        //super.initialize();
        title = "Pentagon2";
        setID("Pentagon2");
        // Building the world.
      
        theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 8));
        theEngine.setDeltaTime(0.25);
        theEngine.setDamping(0.1);
        theEngine.setGravity(new Vector3d(0., 0., 0.));
        //		theEngine.setShowTime(true);

        setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM | TViewer.VP_TRANSLATE);

        RectangularPlane rec = new RectangularPlane(new Vector3d(-12., -12., 0.), new Vector3d(-12., 12., 0.),
            new Vector3d(12., 12., 0.));
        //System.out.println("Rec center: " + rec.getCenter() + " scale: "+ rec.getScale());
        //mDLIC.setSize(new Dimension(512, 512));
        //mDLIC.setSize(new Dimension(256,256));
        //mDLIC.setComputePlane(rec);
        //System.out.println("after set Rec center: " + rec.getCenter() + " scale: "+ rec.getScale());
        mDLIC = new FieldConvolution();
        mDLIC.setSize(new Dimension(512, 512));
        mDLIC.setVisible(false);  
        mDLIC.setComputePlane(rec);

        // Creating components.

        // -> Pentagon2 Walls
        PentagonBox pentagon = new PentagonBox();
        pentagon.setPosition(new Vector3d(0., 0., 0.));
        pentagon.setOrientation(new Vector3d(0., 1., 0.));
        pentagon.setNormal(new Vector3d(0., 0., 1.));
        pentagon.setRadius(4.);
        pentagon.setThickness(2.);
        addElements(pentagon.getWalls());

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
        centralCharge.setPosition(new Vector3d(0.,6.,0.));
        centralCharge.setRadius(.8);
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
        slider1.setMinimum(-200.);
        slider1.setMaximum(200.);
        //slider1.setBounds(40, 515, 415, 50);
        slider1.setPaintTicks(true);
        slider1.addRoute(centralCharge, "charge");
        slider1.setValue(0.);
        slider1.setText("Central Charge");
        slider1.setBorder(null);
        ControlGroup controls;
        controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(slider1);
        
        addElement(controls);
        vis = new VisualizationControl();
        vis.setFieldConvolution(mDLIC);
        vis.setConvolutionModes(DLIC.DLIC_FLAG_E|DLIC.DLIC_FLAG_EP);

   

        addElement(vis);

        resetPointCharges();
        addActions();
        addSelectListener(this);
        mFramework.doStatus(0);
        mSEC.init();
        resetCamera();
        reset();
    }

    void addActions() {
        TealAction ta = new TealAction("Pentagon2", this);
        addAction("Help", ta);

      
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Pentagon2") == 0) {
            //openBrowser("help/pentagon.html");
            if (mFramework != null) {
            	if(mFramework instanceof TFramework) {
            		((TFramework)mFramework).openBrowser("help/pentagon.html");
            	}
            } else {
                TDebug.println("mFramework is null!");
            }
        } else {
            super.actionPerformed(e);
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {
        super.propertyChange(pce);
    }

    public void reset() {
        super.reset();
        resetPointCharges();
        mSEC.stop();
        //resetCamera();
    }

    private void resetPointCharges() {
        Point3d[] positions = new Point3d[2 * N];
        Point3d position = null;
        double r1 = 1. + pointChargeRadius * 1.1;
        double r2 = 4. * Math.cos(Math.PI / 5.) - pointChargeRadius * 1.1;
        for (int i = 0; i < 2 * N; i++) {
            double ang = 2. * Math.PI * (double) i / (2. * (double) N);
            boolean distinct = true;
            do {
                double rad = (r2 - r1) * Math.random() + r1;
                //				double ang = 2.*Math.PI*Math.random();
                position = new Point3d(rad * Math.cos(ang), rad * Math.sin(ang), 0.);
                if (i > 0) {
                    if (position.distance(positions[i - 1]) < pointChargeRadius * 1.1) {
                        System.out.println("i: " + i + ", Touching: " + position + " and " + positions[i - 1]);
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
        setLookAt(new Point3d(0.0, 0.0, 1.), new Point3d(), new Vector3d(0., 1., 0.));
    }

    public synchronized void dispose() {
        super.dispose();
    }

    public void processSelection(SelectEvent se) {
        TDebug.println(0, se.getSource() + " select state = " + se.getStatus());
    }
}
