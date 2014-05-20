/*
 * Created on Jan 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package tealsim.physics.em;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.Rendered;
import teal.render.j3d.Node3D;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.viewer.TViewer;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.em.InfiniteWire;
import teal.physics.em.LineMagneticDipole;
import teal.physics.em.MagneticDipole;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.RelativeFLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyCheck;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

/**
 * @author danziger
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WireAndMagnet extends SimEM {

    private static final long serialVersionUID = 3256720701862917430L;
    MagneticDipole magnet;
    InfiniteWire wire;

    Rendered model;
    Rendered needle;

    PropertyDouble angleslider;
    PropertyCheck arrowtoggle;
    PropertyCheck arrowtoggle2;

    FieldLineManager fmanager;
    protected FieldConvolution mDLIC;

    public WireAndMagnet() {

        super();
        title = "Field of a Magnet Near a Wire";
        TDebug.setGlobalLevel(0);

        // Building the world.
        
        theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 10));
        theEngine.setDeltaTime(0.1); //0.25);
        theEngine.setDamping(0.1);
        theEngine.setGravity(new Vector3d(0., 0., 0.));
       
        
        mDLIC = new FieldConvolution();
        mDLIC.setComputePlane(new RectangularPlane(theEngine.getBoundingArea()));
        //	theEngine.setShowTime(true);

        setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM | TViewer.VP_TRANSLATE | TViewer.ORBIT_ALL);

        //magnet = new MagneticDipole();
        magnet = new LineMagneticDipole();
        magnet.setLength(1.);
        magnet.setMoveable(false);
        //magnet.setDrawn(false);
        magnet.setMu(10.);
        //magnet.addPropertyChangeListener("rotation",this);

        wire = new InfiniteWire();
        wire.setPosition(new Vector3d(0, 0, 6));
        wire.setDirection(new Vector3d(0., 1., 0.));
        addElement(wire);

        Loader3DS max = new Loader3DS();
        boolean loadModels = true;

        if (loadModels) {
            BranchGroup bg = max.getBranchGroup("models/Needle_03b.3DS", "models/");
            Node3D node1 = new Node3D();
            node1.setScale(2.);
            Transform3D trans = new Transform3D();
            Transform3D trans2 = new Transform3D();
            TransformGroup tg = new TransformGroup();
            AxisAngle4d aa2 = new AxisAngle4d(new Vector3d(1, 0, 0), 0.5 * Math.PI);
            trans2.setRotation(aa2);

            TransformGroup tg2 = new TransformGroup();
            tg.setTransform(trans);
            tg2.setTransform(trans2);
            tg.addChild(tg2);
            tg2.addChild(bg);
            node1.addContents(tg);
            magnet.setNode3D(node1);

            BranchGroup bg2 = max.getBranchGroup("models/Body_06.3DS", "models/");
            Node3D node2 = new Node3D();
            node2.setScale(2.);
            node2.addContents(bg2);

            model = new Rendered();
            model.setNode3D(node2);
            model.setPosition(magnet.getPosition());
            model.setDirection(new Vector3d(0, 0, 1));
            addElement(model);

        }
        addElement(magnet);
        // Fieldlines

        int numlines = 12;
        RelativeFLine fl;
        fmanager = new FieldLineManager();
        fmanager.setElementManager(this);
        for (int i = 0; i < numlines; i++) {
            double frac = (double) i / numlines;
            double angle = frac * 2 * Math.PI;
            double offset = 0.;
            fl = new RelativeFLine(magnet, angle + offset, magnet.getLength());
            fl.setType(Field.B_FIELD);
            fl.setColorMode(FieldLine.COLOR_FLAT);
            fl.setIntegrationMode(FieldLine.EULER);
            fmanager.addFieldLine(fl);
        }
        addElement(fmanager);

        FieldDirectionGrid fv = new FieldDirectionGrid();
        fv.setType(teal.field.Field.B_FIELD);
        fv.setResolution(12);

        PropertyDouble currentslider = new PropertyDouble();
        currentslider.setMinimum(-10);
        currentslider.setMaximum(10);
        currentslider.setSliderWidth(200);
        currentslider.addRoute(wire, "current");
        currentslider.setText("Current in Wire");
        currentslider.setBorder(null);
        currentslider.setValue(0.);

        angleslider = new PropertyDouble();
        angleslider.setMinimum(-180);
        angleslider.setMaximum(180);
        angleslider.setSliderWidth(200);
        angleslider.setID("angleslider");
        angleslider.addPropertyChangeListener("value", this);
        angleslider.setText("Magnet Orientation");
        angleslider.setBorder(null);
        angleslider.setValue(45.);

        

        
        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(currentslider);
        controls.add(angleslider);
        addElement(controls);
        
        VisualizationControl viz = new VisualizationControl();
        viz.setFieldConvolution(mDLIC);
        viz.setConvolutionModes(DLIC.DLIC_FLAG_B);
        viz.setFieldLineManager(fmanager);
        viz.setActionFlags(0);
        viz.setSymmetryCount(1);
        viz.setColorPerVertex(false);
        addElement(viz);
        
        //		 Launch
        setShowGizmos(false);
        mSEC.setVisible(true);
        reset();
        resetCamera();
        mSEC.init();

    }

    void addActions() {
        TealAction ta = new TealAction("Wire and Compass", this);
        addAction("Help", ta);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println("Action: " + command);
        if (e.getActionCommand().compareToIgnoreCase("Wire and Compass") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/wireandmagnet.html");
        	}

        } else {

            super.actionPerformed(e);
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {
        //System.out.println("property change  source = " + pce.getSource() + " " + pce.getPropertyName());
        if (pce.getSource() == angleslider) {
            //System.out.println("am i even getting this pce?");
            double angle = ((Double) pce.getNewValue()).doubleValue();
            double rad = (angle / 180.) * Math.PI;
            Vector3d dir = new Vector3d(Math.sin(rad), Math.cos(rad), 0.);
            mSEC.stop();
            magnet.setDirection(dir);
            //mSEC.start();

        } else if (pce.getSource() == magnet) {
            //System.out.println("magnet PCE " + pce.getPropertyName());
            Quat4d quat = new Quat4d(magnet.getRotation());
            needle.setRotation(quat);
        } else {
            super.propertyChange(pce);
        }
    }

    public void reset() {

        resetCamera();
    }

    public void resetCamera() {
        Point3d from = new Point3d(0., 0., 2.5);
        Point3d to = new Point3d(0., 0., 0.);
        Vector3d up = new Vector3d(0., 1., 0.);
        from.scale(0.5);
        to.scale(0.5);
        setLookAt(from, to, up);
    }

    
}
