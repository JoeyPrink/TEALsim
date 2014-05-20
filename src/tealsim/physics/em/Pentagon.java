/* $Id: Pentagon.java,v 1.11 2010/08/10 18:12:34 stefan Exp $ */

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
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

public class Pentagon extends SimEM implements SelectListener {

    private static final long serialVersionUID = 3256443620520571449L;
 
    private VisualizationControl vis;

    final private int N = 10;
    private PointCharge[] pointCharges = new PointCharge[N];
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
    Vector3d[] positions = { new Vector3d(-0.2, 0.25, 0.), new Vector3d(0., 0.9, 0.), new Vector3d(0., -0.95, 0.),
            new Vector3d(0.95, 0., 0.), new Vector3d(-1.2, 0., 0.), new Vector3d(0.4, 0.45, 0.),
            new Vector3d(0.75, -0.8, 0.), new Vector3d(-0.75, 0.75, 0.), new Vector3d(-0.7, -0.4, 0.),
            new Vector3d(0.25, -0.2, 0.), };

    public Pentagon() {

        super();
        //super.initialize();
        title = "Pentagon";
        setID("Pentagon");
        // Building the world.
        
        setBoundingArea(new BoundingSphere(new Point3d(), 8));
        setDeltaTime(0.25);
        setDamping(0.1);
        setGravity(new Vector3d(0., 0., 0.));
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

        // -> Pentagon Walls
        PentagonBox pentagon = new PentagonBox();
        pentagon.setPosition(new Vector3d(0., 0., 0.));
        pentagon.setOrientation(new Vector3d(0., 1., 0.));
        pentagon.setNormal(new Vector3d(0., 0., 1.));
        pentagon.setRadius(4.);
        pentagon.setThickness(2.);
        addElements(pentagon.getWalls());

        // -> Point Charges
        double pointChargeRadius = 0.25;
        for (int i = 0; i < N; i++) {
            pointCharges[i] = new PointCharge();
            pointCharges[i].setRadius(pointChargeRadius);
            pointCharges[i].setMass(1.0);
            pointCharges[i].setCharge(1.);
            pointCharges[i].setID("pointCharge" + i);
            pointCharges[i].setPickable(true);
            pointCharges[i].setColliding(true);
            SphereCollisionController sccx = new SphereCollisionController(pointCharges[i]);
            sccx.setRadius(pointChargeRadius);
            sccx.setTolerance(0.02);
            sccx.setMode(SphereCollisionController.WALL_SPHERE);
            pointCharges[i].setCollisionController(sccx);
            addElement(pointCharges[i]);
        }

        vis = new VisualizationControl();
        vis.setFieldConvolution(mDLIC);
        vis.setConvolutionModes(DLIC.DLIC_FLAG_E|DLIC.DLIC_FLAG_EP);


        addElement(vis);

        resetPointCharges();
        addActions();
        addSelectListener(this);
        mSEC.init();
        resetCamera();
        reset();
    }

    void addActions() {
        TealAction ta = new TealAction("Pentagon", this);
        addAction("Help", ta);

      
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Pentagon") == 0) {
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
        for (int i = 0; i < N; i++) {
            pointCharges[i].setPosition(positions[i], true);
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
