/* $Id: FloatingCoil.java,v 1.15 2010/09/22 15:48:11 pbailey Exp $ */
/**
 * A demonstration implementation of the TFramework.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.15 $
 */

package tealsim.physics.em;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.Rendered;
import teal.render.j3d.Node3D;
import teal.render.j3d.ShapeNode;
import teal.render.j3d.geometry.Cylinder;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.primitives.Line;
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.physical.PhysicalObject;
import teal.physics.physical.Wall;
import teal.physics.em.MagneticDipole;
import teal.physics.em.RingOfCurrent;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FluxFieldLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyCheck;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;

public class FloatingCoil extends SimEM {

    private static final long serialVersionUID = 3257008735204554035L;
    boolean loadModels = true;
    PropertyDouble magSlider;
    Rendered model;
    RingOfCurrent roc;
    MagneticDipole m1;
    //FallingCoil fcSys;
    Vector3d rocPos;
    double modelOFF = 0.2;
    double baseModelOff = -0.20;
    double ringRad = 0.43;
    double torR = 0.08;
    double magLen = 0.24;
    double magR = 0.09;
    double ringMass = 3.5;
    double currentScale = 0.5;
    Vector3d positive = new Vector3d(1, 0, 0);
    Vector3d negative = new Vector3d(-1, 0, 0);
    Vector3d start = new Vector3d(0, magLen * 0.2, 0);
    double searchRad = magR;
    double fLen = 0.033;
    double minD = 0.03;
    int kMax = 300;
    int fMode = FieldLine.RUNGE_KUTTA;
    private PropertyDouble curSlider;
    private FieldLine fl = null;
    private PropertyCheck colorToggle;
    private FieldLineManager fmanager;
    private FieldConvolution mDLIC;
    private FieldDirectionGrid fv;

    public FloatingCoil() {
        super();

        TDebug.setGlobalLevel(0);

        title = "Floating Ring of Current";
        rocPos = new Vector3d(0., ringRad + (ringRad * 0.02), 0.);

        BoundingSphere bs = new BoundingSphere(new Point3d(0, 1.6, 0), 03.5);

        setMouseMoveScale(0.05,0.05,0.5);

        
        setBoundingArea(bs);
        //theEngine.setDeltaTime(0.02); // Was 0.005
        //theEngine.setDamping(1.5);
 

        theScene.setBoundingArea(bs);

        RectangularPlane rec = new RectangularPlane(bs);
        mDLIC = new FieldConvolution();
        mDLIC.setComputePlane(rec);

        fv = new FieldDirectionGrid();
        fv.setType(Field.B_FIELD);
        fv.setDrawn(false);
        //addElement(fv);

        Line l = new Line(new Vector3d(-0.8, ringRad * 2.0, 0.), new Vector3d(0.8, ringRad * 2.0, 0.));
        l.setColor(Color.BLACK);
        addElement(l);

        Wall wall = new Wall(new Vector3d(0., ringRad - torR, 0.), new Vector3d(2., 0., 0.), new Vector3d(0., 0., 2.));
        wall.setElasticity(0.);
        addElement(wall);

        m1 = new MagneticDipole();
        m1.setMu(10.);
        m1.setPosition(new Vector3d(0., 0., 0.));
        m1.setDirection(new Vector3d(0, 1, 0));
        m1.setPickable(false);
        m1.setRotable(false);
        m1.setMoveable(false);
        m1.setRadius(magR);
        m1.setLength(magLen);

        roc = new RingOfCurrent();
        roc.setID("Ring");
        roc.setDirection(new Vector3d(0., 1., 0.));
        roc.setPosition(rocPos);
        roc.setPickable(true);
        roc.setRotable(true);
        roc.setMoveable(true);
        roc.setInducing(false);
        roc.setRadius(ringRad);
        roc.setThickness(torR);
        roc.setMass(ringMass);
        roc.setInducing(false);
        roc.setInductance(0.1);
        roc.setIntegrationMode(RingOfCurrent.AXIAL);
        roc.setColliding(true);

        SphereCollisionController sccx = new SphereCollisionController(roc);
        sccx.setRadius(torR);
        sccx.setTolerance(0.01);
        roc.setCollisionController(sccx);

        double scale = 10.0 / 39.37;

        Loader3DS max = new Loader3DS();
        //max.setLogDetail(1);

        if (loadModels) {
            BranchGroup bg = max.getBranchGroup("models/LevPart2.3DS", "models/");
            Node3D node1 = new Node3D();
            node1.setScale(scale);
            node1.addContents(bg);
            Rendered levParts = new Rendered();
            levParts.setNode3D(node1);
            levParts.setPosition(new Vector3d(0., baseModelOff, 0.));
            addElement(levParts);
            m1.setDrawn(false);

            BranchGroup bg2 = max.getBranchGroup("models/WireTst2.3DS", "models/");
            Node3D node2 = new Node3D();
            node2.setScale(scale);
            node2.addContents(bg2);
            //model = new Rendered();
            //model.setNode3D(node2);
            //addElement(model);
            roc.setNode3D(node2);
            roc.setModelOffsetPosition(new Vector3d(0, -0.7, 0));
            //roc.setDrawn(false);
            //roc.addPropertyChangeListener("position", this);
            roc.setPosition(rocPos);

            Rendered cylinder = new Rendered();
            ShapeNode cylN = new ShapeNode();
            cylN.setGeometry(Cylinder.makeGeometry(16, 0.13, 0.5));
            cylinder.setNode3D(cylN);
            cylinder.setColor(new Color(160, 140, 110));
            cylinder.setPosition(new Vector3d(0, -0.36, 0));
            addElement(cylinder);
        }

        addElement(m1);
        addElement(roc);
        fmanager = new FieldLineManager();
        
        fl = makeFLine(-200.0, roc, null, fLen, kMax, fMode);
        //fLines.add(fl);
        fmanager.addFieldLine(fl);

        fl = makeFLine(120.0, m1, null, fLen, kMax, fMode);
        fl.setBuildDir(FieldLine.BUILD_NEGATIVE);
        //fl.setKMax(400);
        ((FluxFieldLine) fl).setSearchIntervals(600);
        ((FluxFieldLine) fl).setSearchSubIntervals(600);
       
        fmanager.addFieldLine(fl);

        fl = makeFLine(220.0, m1, null, fLen, kMax, fMode);
        fl.setBuildDir(FieldLine.BUILD_NEGATIVE);
        //((FluxFieldLine)fl).setBrakSteps(400);
        //((FluxFieldLine)fl).setBrentSteps(400);
        fmanager.addFieldLine(fl);
        

        fl = makeFLine(400.0, m1, null, fLen, kMax, fMode);
        fl.setBuildDir(FieldLine.BUILD_NEGATIVE);
        //((FluxFieldLine)fl).setBrakSteps(200);
        //((FluxFieldLine)fl).setBrentSteps(200);
        fmanager.addFieldLine(fl);

        
        fmanager.setElementManager(this);

        curSlider = new PropertyDouble();
        curSlider.setText("Current in Ring");
        curSlider.setMinimum(-32.);
        curSlider.setMaximum(32.0);
        curSlider.setPaintTicks(true);
        curSlider.addPropertyChangeListener("value", this);
        curSlider.setValue(0.);
        curSlider.setVisible(true);

        
        magSlider = new PropertyDouble();
        magSlider.setText("Magnet Strength");
        magSlider.setMinimum(.0);
        magSlider.setMaximum(10.0);
        magSlider.setPaintTicks(true);
        magSlider.addPropertyChangeListener("value", this);
        magSlider.setValue(10.0);
        magSlider.setVisible(true);

        addActions();

        

        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(curSlider);
        addElement(controls);

        VisualizationControl vis = new VisualizationControl();
        vis.setFieldConvolution(mDLIC);
        vis.setFieldVisGrid(fv);
        vis.setFieldLineManager(fmanager);
        addElement(vis);
        
        theScene.setFogEnabled(true);
        theScene.setFogTransformFrontScale(0.0);
        theScene.setFogTransformBackScale(0.35);
        //mSEC.init();
        //resetCamera();
        //reset();
        //mViewer.initFogTransform();
    }
    public void initialize(){
    	super.initialize();
    	resetCamera();
	}
    void addActions() {
        TealAction ta = new TealAction("Floating Coil", this);
        addAction("Help", ta);
    }

    public void actionPerformed(ActionEvent e) {
        TDebug.println(1, " Action comamnd: " + e.getActionCommand());
        if (e.getActionCommand().compareToIgnoreCase("Floating Coil") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/floatingcoil.html");
        	}
        }  else {
            super.actionPerformed(e);
        }
    }

    public void reset() {
        roc.setPosition(rocPos);
        roc.setVelocity(new Vector3d());
        roc.setDirection(new Vector3d(0., 1., 0.));
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.025, 0.4), new Point3d(0., 0.025, 0.), new Vector3d(0., 1., 0.));
    }

    public void propertyChange(PropertyChangeEvent pce) {
        Object source = pce.getSource();
        if (source == magSlider) {
            double magF = ((Double) pce.getNewValue()).doubleValue();
            m1.setMu(magF * 1.0);
        } else if (source == curSlider) {
            double cur = ((Double) pce.getNewValue()).doubleValue();
            roc.setCurrent(cur * currentScale); /* amps to levitate/4.9) */
        } else if (pce.getSource() == colorToggle) {
            String pn = pce.getPropertyName();
            System.out.println("ColorToggle: " + pn);
            if (pn.compareTo("value") == 0) {
                boolean state = ((Boolean) pce.getNewValue()).booleanValue();
                System.out.println("ColorToggle: State = " + state);
                fmanager.setColorMode(state);
            }
        } else {
            super.propertyChange(pce);
        }
    }

    protected FieldLine makeFLine(double val, PhysicalObject obj, Color color, double fLen, int kMax, int fMode) {
        Color col = color;
        Vector3d start = new Vector3d(0, 0, 0);
        Vector3d positive = new Vector3d(1, 0, 0);
        FluxFieldLine fl;
        if (obj == null) {
            fl = new FluxFieldLine(val, start, positive, searchRad);
        } else {
            if (obj instanceof RingOfCurrent) {
                fl = new FluxFieldLine(val, obj, true, true);
            } else if (obj instanceof MagneticDipole) {
                fl = new FluxFieldLine(val, obj, true, false);
                fl.setObjRadius(searchRad);
            } else {
                return null;
            }
        }
        fl.setMinDistance(minD * 0.5);
        fl.setIntegrationMode(fMode);
        fl.setKMax(kMax);
        fl.setSArc(fLen);
        fl.setColorMode(FieldLine.COLOR_VERTEX);
        fl.setReceivingFog(true);
        if (col != null) {
            fl.setColor(col);
        }
        return fl;
    }
    
  
}
