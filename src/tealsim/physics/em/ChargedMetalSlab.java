/* $Id: ChargedMetalSlab.java,v 1.11 2010/07/16 21:41:41 stefan Exp $ */

/**
 * A demonstration implementation of the TFramework.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.11 $
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
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.physical.RectangularBox;
import teal.physics.em.PointCharge;
import teal.sim.spatial.FieldConvolution;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

public class ChargedMetalSlab extends SimEM {

    private static final long serialVersionUID = 3618705205575889712L;

    
    protected FieldConvolution mDLIC = null;

    final private int N = 12;
    final private double scale = 2.5;
    private PointCharge[] chargeSet1 = new PointCharge[N];
    private PointCharge[] chargeSet2 = new PointCharge[N];

    Vector3d[] set1Positions = { new Vector3d(4, 0, 0), new Vector3d(4, 0, 0.5), new Vector3d(4, 0, -0.5),
            new Vector3d(4.5, 0, 0), new Vector3d(3.5, 0, 0), new Vector3d(4, -0.5, 0), new Vector3d(4, -0.5, 0.5),
            new Vector3d(4, -0.5, -0.5), new Vector3d(4.5, -0.5, 0), new Vector3d(3.5, -0.5, 0),
            new Vector3d(4, 0.5, 0), new Vector3d(4, 0.5, 0.5), };

    Vector3d[] set2Positions = { new Vector3d(-4, 0, 0), new Vector3d(-4, 0, 0.5), new Vector3d(-4, 0, -0.5),
            new Vector3d(-4.5, 0, 0), new Vector3d(-3.5, 0, 0), new Vector3d(-4, -0.5, 0), new Vector3d(-4, -0.5, 0.5),
            new Vector3d(-4, -0.5, -0.5), new Vector3d(-4.5, -0.5, 0), new Vector3d(-3.5, -0.5, 0),
            new Vector3d(-4, 0.5, 0), new Vector3d(-4, 0.5, 0.5), };

    public ChargedMetalSlab() {

        super();
        title = "Charged Metal Slab";
        TDebug.setGlobalLevel(-11);

        // Building the world.
     
        setBoundingArea(new BoundingSphere(new Point3d(), 16));
        setDamping(0.01);
        setGravity(new Vector3d(0., 0., 0.));
        setDeltaTime(0.5);
        
        mDLIC = new FieldConvolution();
        mDLIC.setSize(new Dimension(512, 512));
        //mDLIC.setSize(new Dimension(256,256));
        mDLIC.setComputePlane(new RectangularPlane(new BoundingSphere(new Point3d(), 18)));

        // Creating components.

        // -> Conductor Slab
        RectangularBox conductor1 = new RectangularBox();
        conductor1.setPosition(new Vector3d(0. * scale, 0. * scale, 0. * scale));
        conductor1.setOrientation(new Vector3d(1., 0., 0.));
        conductor1.setNormal(new Vector3d(0., 1., 0.));
        conductor1.setLength(10. * scale);
        conductor1.setWidth(5. * scale);
        conductor1.setHeight(3. * scale);
        addElements(conductor1.getWalls());

        // -> Point Charges
        double pointChargeRadius = 0.2 * scale;
        for (int i = 0; i < N; i++) {
            set1Positions[i].scale(scale);
            chargeSet1[i] = new PointCharge();
            chargeSet1[i].setRadius(pointChargeRadius);
            chargeSet1[i].setMass(1.0);
            chargeSet1[i].setCharge(5.0);
            chargeSet1[i].setID("set1PointCharge" + i);
            chargeSet1[i].setPickable(true);
            chargeSet1[i].setColliding(true);
            SphereCollisionController sccx1 = new SphereCollisionController(chargeSet1[i]);
            sccx1.setRadius(pointChargeRadius);
            sccx1.setTolerance(0.02);
            //sccx1.setMode(SphereCollisionController.SPHERE_SPHERE);
            chargeSet1[i].setCollisionController(sccx1);
            chargeSet1[i].setGeneratingP(false);
            addElement(chargeSet1[i]);
        }
        for (int i = 0; i < N; i++) {
            set2Positions[i].scale(scale);
            chargeSet2[i] = new PointCharge();
            chargeSet2[i].setRadius(pointChargeRadius);
            chargeSet2[i].setMass(1.0);
            chargeSet2[i].setCharge(5.0);
            chargeSet2[i].setID("set2PointCharge " + i);
            chargeSet2[i].setPickable(true);
            chargeSet2[i].setColliding(true);
            SphereCollisionController sccx2 = new SphereCollisionController(chargeSet2[i]);
            sccx2.setRadius(pointChargeRadius);
            sccx2.setTolerance(0.02);
            //sccx2.setMode(SphereCollisionController.WALL_SPHERE);
            chargeSet2[i].setCollisionController(sccx2);
            chargeSet2[i].setGeneratingP(false);
            addElement(chargeSet2[i]);
        }

        
        VisualizationControl vis = new VisualizationControl();
        vis.setFieldConvolution(mDLIC);
        vis.setConvolutionModes(DLIC.DLIC_FLAG_E |DLIC.DLIC_FLAG_EP);
        addElement(vis);
        

        theScene.setFogEnabled(true);
        theScene.setFogTransformFrontScale(0);
        // Launch
        addActions();
        mSEC.init();
        //reset();
        resetCamera();
        // initFogTransform() needs to be called in the constructor after resetCamera() if a non-default camera
        // position is being used.
        //mViewer.initFogTransform();
        //		theEngine.start();
    }

    void addActions() {
        TealAction ta = new TealAction("Charged Metal Slab", this);
        addAction("Help", ta);

        
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println("Action: " + command);
        if (e.getActionCommand().compareToIgnoreCase("Charged Metal Slab") == 0) {
            if ((mFramework != null) && (mFramework instanceof TFramework)) {
                ((TFramework)mFramework).openBrowser("help/chargedmetalslab.html");
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
        for (int i = 0; i < N; i++) {
            //set1Positions[i].scale(scale);
            chargeSet1[i].setCharge(1.);
            chargeSet1[i].setPosition(set1Positions[i], true);
            chargeSet1[i].setVelocity(new Vector3d());
        }
        for (int i = 0; i < N; i++) {
            //set2Positions[i].scale(scale);
            chargeSet2[i].setCharge(1.);
            chargeSet2[i].setPosition(set2Positions[i], true);
            chargeSet2[i].setVelocity(new Vector3d());
        }
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 1. * scale), new Point3d(), new Vector3d(0., 1., 0.));

    }

    

}
