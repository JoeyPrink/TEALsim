/* $Id: FaradaysLawRotation.java,v 1.14 2010/09/22 15:48:11 pbailey Exp $ */

/**
 * A demonstration implementation of the TFramework.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.14 $
 */

package tealsim.physics.em;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.media.j3d.Appearance;
import teal.render.BoundingSphere;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JButton;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.plot.CurrentPlot;
import teal.plot.FluxPlot;
import teal.plot.Graph;
import teal.render.j3d.ShapeNode;
import teal.render.viewer.TViewer;
import teal.sim.constraint.LDPSConstraint;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.TEngineControl;
import teal.physics.physical.Ball;
import teal.physics.em.LineMagneticDipole;
import teal.physics.em.SimEM;
import teal.physics.em.ConstantField;
import teal.physics.em.MagneticDipole;
import teal.physics.em.RingOfCurrent;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FluxFieldLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

public class FaradaysLawRotation extends SimEM {

    private static final long serialVersionUID = 3258411716302944053L;
    double deltaTime = 0.1;
    JButton but = null;
    JButton but1 = null;
    
    VisualizationControl vis;
    ControlGroup params,graphs;
    RingOfCurrent roc;
    LineMagneticDipole mag;
    LDPSConstraint mag_constraint;
    LDPSConstraint roc_constraint;
    Ball mag_gizmo;
    //Ball roc_gizmo;
    FieldConvolution mDLIC;
    FieldLineManager fmanager;

    Graph flux_graph;
    FluxPlot flux_plot;
    Graph current_graph;
    CurrentPlot current_plot;

    PropertyDouble sliderroc;
    PropertyDouble slidermag;
    PropertyDouble sliderradius;
    PropertyDouble sliderrot;

    double maximumResistance = 5.;
    double minDist = 0.375;
    double ringDist = 0.02;
    double minimumRingRadius = 0.5;
    double maximumRingRadius = 3.5;
    double initialRingRadius = 1.6;
    Vector3d ringPosition = new Vector3d(0., 0.0, 0.);

    final boolean gizmos_visible = false;
    boolean resetting = false;

    // Bounds (percentages).
    /*
     Rectangle viewer_ = new Rectangle(2,2,96,50);
     Rectangle graph1_ = new Rectangle(2,52,43,30);
     Rectangle graph2_ = new Rectangle(45,52,54,30);
     Rectangle slider1_ = new Rectangle(2,82,45,12);
     Rectangle slider2_ = new Rectangle(50,82,48,12);
     Rectangle button1_ = new Rectangle(20,95,28,3);
     Rectangle button2_ = new Rectangle(52,95,28,3);
     */

    // Default size of the application.
    Dimension defaultSize = new Dimension(700, 700);
    Dimension dynamicSize = new Dimension();

    public FaradaysLawRotation() {
        super();
        TDebug.setGlobalLevel(-1);

        title = "Faraday's Law: Rotating Ring";
        
        setBoundingArea(new BoundingSphere(new Point3d(), 6));
        setDeltaTime(deltaTime);
        setGravity(new Vector3d());
        setDamping(0.);
        
        setShowGizmos(false);
        //setMouseMoveScale(new Vector3d());
        setCursorOnDrag(false);
        setNavigationMode(TViewer.ORBIT_ALL);

        mSEC.setVisible(true);
        RectangularPlane rec = new RectangularPlane(new Vector3d(-6., -6., 0.), new Vector3d(-6., 6., 0.),
            new Vector3d(6., 6., 0.));
        mDLIC = new FieldConvolution();
        mDLIC.setSize(new Dimension(512, 512));
        //mDLIC.setSize(new Dimension(64,64));
        mDLIC.setComputePlane(rec);
        mDLIC.setAutoGenerate(false);
        addElement(mDLIC);

        mag = new LineMagneticDipole();
        mag.setMu(1.0);
        mag.setPosition(new Vector3d(5., 0., 0.));
        mag.setDirection(new Vector3d(1, 0, 0));
        mag.setPickable(false);
        mag.setRotable(false);
        mag.setMoveable(true);
        mag.setIntegrating(true);
        mag.setLength(0.75);
        mag.setFeelsBField(false);
        mag.setAvoidSingularity(true);
        mag.setAvoidSingularityScale(10.);

        mag_constraint = new LDPSConstraint();
        mag_constraint.setPoint(new Vector3d(5., 0., 0.));
        mag_constraint.setK1(10.); //8.);
        mag_constraint.setK2(20.); //30.);
        mag_constraint.setP(0.5); //0.25);
        mag.setConstraint(mag_constraint);
        mag.setConstrained(true);
        mag.addPropertyChangeListener(this);
        mag_constraint = (LDPSConstraint) mag.getConstraint();

        roc = new RingOfCurrent();
        roc.setID("Ring");
        roc.setPosition(ringPosition);
        roc.setDirection(new Vector3d(1., 0., 0.));
        roc.setPickable(true);
        roc.setSelectable(true);
        roc.setRotable(true);
        roc.setMoveable(true);
        roc.setRadius(initialRingRadius);
        roc.addPropertyChangeListener(this);
        roc.setInductance(1.);
        roc.setFeelsBField(false);

        roc_constraint = new LDPSConstraint();
        roc_constraint.setPoint(new Vector3d());
        roc_constraint.setK1(10.);
        roc_constraint.setK2(20.);
        roc_constraint.setP(0.5);
        //roc.setConstraint(roc_constraint);
        //roc.setConstrained(true);
        roc.addPropertyChangeListener(this);
        roc_constraint = (LDPSConstraint) roc.getConstraint();

        ConstantField b = new ConstantField(new Vector3d(), new Vector3d(1, 0, 0), 10.);
        b.setType(ConstantField.B_FIELD);
        addElement(b);
        //addElement(mag);
        addElement(roc);
        roc.setInducing(true);
        roc.setIntegrationMode(RingOfCurrent.GENERAL);
        //roc.setModelOffsetPosition(new Vector3d(0,1,0));

        mag_gizmo = new Ball();
        mag_gizmo.setPosition(mag.getPosition());
        mag_gizmo.setPickable(true);
        mag_gizmo.setMoveable(true);
        mag_gizmo.setSelectable(true);
        mag_gizmo.setColliding(false);
        mag_gizmo.setRadius(0.5);
        mag_gizmo.setColor(Color.LIGHT_GRAY);
        mag_gizmo.getMaterial().setTransparancy(1f);
//        if (!gizmos_visible) {
//            ShapeNode node = (ShapeNode) mag_gizmo.getNode3D();
//            Appearance app = node.getAppearance();
//            app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 1.f));
//            node.setAppearance(app);
//        }
        mag_gizmo.addPropertyChangeListener("position", this);
        addElement(mag_gizmo);

        double[] flux = new double[6];
        flux[0] = -18.;  //-1
        flux[1] = -72.;  //  -20
        flux[2] = -162.;  //  -40
        flux[3] = 18.;
        flux[4] = 72.;
        flux[5] = 162.;
        FluxFieldLine fl = null;
        fmanager = new FieldLineManager();
        fmanager.setElementManager(this);
        for (int i = 0; i < flux.length; i++) {
            fl = new FluxFieldLine(flux[i], new Vector3d(-10.0, 0.0, 0), new Vector3d(0, 1, 0), 5.); // -10
            fl.setIntegrationMode(FluxFieldLine.RUNGE_KUTTA);
            fl.setMinDistance(ringDist);
            //fl.setBuildDir(FieldLine.BUILD_POSITIVE);
            fl.setKMax(fl.getKMax() * 2);
            fixFieldLine(fl, null, 4.f);
            //addElement(fl);
            fmanager.addFieldLine(fl);

            fl = new FluxFieldLine(flux[i], new Vector3d(-10.0, -0.0, 0), new Vector3d(0, -1, 0), 5.); // -10
            fl.setIntegrationMode(FluxFieldLine.RUNGE_KUTTA);
            fl.setMinDistance(ringDist);
            //fl.setBuildDir(FieldLine.BUILD_POSITIVE);
            fl.setKMax(fl.getKMax() * 2);
            fixFieldLine(fl, null, 4.f);
            //addElement(fl);
            fmanager.addFieldLine(fl);
/*
            fl = new FluxFieldLine(flux[i], roc, FluxFieldLine.SEARCH_FORWARD, FluxFieldLine.searchLine); // -10
            fl.setIntegrationMode(FluxFieldLine.RUNGE_KUTTA);
            fl.setMinDistance(ringDist);
            fixFieldLine(fl, null, 4.f);
            //addElement(fl);
            fmanager.addFieldLine(fl);

            fl = new FluxFieldLine(flux[i], roc, FluxFieldLine.SEARCH_BACK, FluxFieldLine.searchLine); // -10
            fl.setIntegrationMode(FluxFieldLine.RUNGE_KUTTA);
            fl.setMinDistance(ringDist);
            fixFieldLine(fl, null, 4.f);
            //addElement(fl);
            fmanager.addFieldLine(fl);
            */
        }
        addElement(fmanager);

        slidermag = new PropertyDouble();
        slidermag.setPrecision(0.01);
        slidermag.setText("Horizontal Field Comp");
        slidermag.setLabelWidth(150);
        slidermag.setMinimum(-0.1);
        slidermag.setMaximum(0.1);
        slidermag.setPaintTicks(true);
        slidermag.addRoute(b, "magnitude");
        //		slidermag.addRoute(this, "mu");
        slidermag.setValue(0.099);
        slidermag.setVisible(true);
        //addElement(slidermag);

        sliderroc = new PropertyDouble();
        sliderroc.setText("Ring Resistance");
        sliderroc.setMinimum(0.);
        sliderroc.setMaximum(maximumResistance);
        sliderroc.setPrecision(0.01);
        sliderroc.setPaintTicks(true);
        sliderroc.addRoute(roc, "resistance");
        sliderroc.setValue(5.0);
        sliderroc.setVisible(true);
        //addElement(sliderroc);

        sliderradius = new PropertyDouble();
        sliderradius.setText("Ring Radius");
        sliderradius.setMinimum(minimumRingRadius);
        sliderradius.setMaximum(maximumRingRadius);
        sliderradius.setPrecision(0.01);
        sliderradius.setPaintTicks(true);
        sliderradius.addRoute(roc, "radius");
        sliderradius.setValue(initialRingRadius);
        sliderradius.setVisible(true);
        //addElement(sliderradius);

        sliderrot = new PropertyDouble();
        sliderrot.setText("Ring Rotation");
        sliderrot.setMinimum(35);
        sliderrot.setMaximum(145);
        sliderrot.setPrecision(0.01);
        sliderrot.setPaintTicks(true);
        sliderrot.addPropertyChangeListener("value", this);
        sliderrot.setValue(90);
        sliderrot.setVisible(true);
        //addElement(sliderrot);

        flux_graph = new Graph();
        flux_graph.setXRange(0., 8.);
        flux_graph.setYRange(-0.5, 0.5);
        //flux_graph.setXPersistence(100.0);
        flux_graph.setWrap(true);
        flux_graph.setClearOnWrap(true);
        flux_graph.setXLabel("Time");
        flux_graph.setYLabel("Flux");
        flux_graph.addLegend(0, "External Flux");
        flux_graph.addLegend(1, "Total Flux");
        flux_plot = new FluxPlot();
        flux_plot.setRing(roc);
        flux_plot.setTimeAutoscale(false);
        flux_plot.setFluxAutoscale(true);
        flux_graph.addPlotItem(flux_plot);
        flux_graph.setSize(400, 260);
        //addElement(flux_graph);

        current_graph = new Graph();
        current_graph.setXRange(0., 8.);
        current_graph.setYRange(-0.5, 0.5);
        //current_graph.setXPersistence(100.0);
        current_graph.setWrap(true);
        current_graph.setClearOnWrap(true);
        current_graph.setXLabel("Time");
        current_graph.setYLabel("Current");
        current_graph.addLegend(0, "Ring  Eddy  I");
        current_plot = new CurrentPlot();
        current_plot.setRing(roc);
        current_plot.setTimeAutoscale(false);
        current_plot.setCurrentAutoscale(true);
        current_graph.addPlotItem(current_plot);
        current_graph.setSize(400, 260);
        //addElement(current_graph);

        
        params = new ControlGroup();
        params.setText("Parameters");
        params.add(sliderrot);
        params.add(slidermag);
        params.add(sliderradius);
        params.add(sliderroc);
        addElement(params);
        graphs = new ControlGroup();
        graphs.setText("Graphs");
        graphs.addElement(flux_graph);
        graphs.addElement(current_graph);
        addElement(graphs);
        vis = new VisualizationControl();
        vis.setText("Field Visualization");
        vis.setFieldConvolution(mDLIC);
        vis.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
        vis.setFieldLineManager(fmanager);
        vis.setSymmetryCount(1);
        vis.setActionFlags(0);
        vis.setColorPerVertex(false);
        addElement(vis);
        

        addActions();
        reset();
        //mSEC.init();
        //mSEC.start();

    }

    
    protected void fixFieldLine(FluxFieldLine fieldline, Color color, float thickness) {
        /*
         fieldline.setKMax(500);
         
         fieldline.setIntegrationMode(FluxFieldLine.RUNGE_KUTTA);
         fieldline.setMinDistance(0.05);
         
         FieldLineNode node = (FieldLineNode) fieldline.getNode3D();
         Appearance app = node.getAppearance();
         app.setLineAttributes(new LineAttributes(0f, // thickness, //
         LineAttributes.PATTERN_SOLID, false));
         app.setColoringAttributes(new ColoringAttributes(new Color3f(color),
         ColoringAttributes.SHADE_FLAT));
         node.setAppearance(app);
         */

        fieldline.setSymmetry(1, new Vector3d(1, 0, 0));
        fieldline.setColorMode(FieldLine.COLOR_FLAT);
        fieldline.setColorScale(0.005);
        //fieldline.setColor(color);
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (resetting) return;
        if (pce.getSource() == roc) {
            if (pce.getPropertyName().equalsIgnoreCase("resistance")) {
                double r = roc.getResistance();
                if (r > maximumResistance) {
                    roc.setResistance(maximumResistance);
                    sliderroc.setValue(maximumResistance);
                    //mGUI.refresh();
                }
                if (r < 0) {
                    roc.setResistance(0);
                    sliderroc.setValue(0);
                    //mGUI.refresh();
                }
            }
            //			else if( pce.getPropertyName().equalsIgnoreCase("rotation") ) {
            //		       	Vector3d direction = roc.getDirection();
            //		       	if(Math.abs(direction.y)>Teal.DoubleZero) {
            //			       	direction.y=0;
            //			       	direction.normalize();
            //			       	roc.setDirection(direction);
            //	                requestRefresh();
            //		       	}
            //				int state = mSEC.getSimState();
            //		       	if( state == EngineControl.PAUSED ) {
            //					mSEC.start();
            //		       	}
            //			}
            else if (pce.getPropertyName().equalsIgnoreCase("position")) {
                roc.setPosition(ringPosition);
                int state = mSEC.getSimState();
                if (state == TEngineControl.PAUSED) {
                    mSEC.start();
                }
            }
        } else if (pce.getSource() == mag_gizmo) {
            if (pce.getPropertyName().equalsIgnoreCase("position")) {
                Vector3d position = (Vector3d) pce.getNewValue();
                if (position.x > 5) position.x = 5;
                if (position.x < -5) position.x = -5;
                position.y = 0;
                position.z = 0;
                mag_gizmo.setPosition(position, false);
                mag_constraint.setPoint(position);
                if(theEngine != null)
                	theEngine.requestRefresh();
                int state = mSEC.getSimState();

                if (state == TEngineControl.PAUSED) {
                    mSEC.start();
                }
            }
        } else if (pce.getSource() == sliderrot) {
            double angle = ((Double) sliderrot.getValue()).doubleValue();
            angle /= 360.;
            angle *= 2 * Math.PI;
            Vector3d dir = new Vector3d(Math.sin(angle), Math.cos(angle), 0);
            roc.setDirection(dir);
        }
        /*		
         else if (pce.getSource() == roc_gizmo) {
         if( pce.getPropertyName().equalsIgnoreCase("position") ) {
         Vector3d position = (Vector3d) pce.getNewValue();
         if( position.x > 5 ) position.x = 5;
         if( position.x < -5 ) position.x = -5;
         position.y = 0;
         position.z = 0;
         roc_gizmo.setPosition(position, false);
         roc_constraint.setPoint(position);
         theEngine.requestRefresh();
         int state = mSEC.getSimState();
         if( state != EngineControl.RUNNING ) {
         mSEC.start();
         }
         }
         }
         */
        else {
            super.propertyChange(pce);
        }
    }

    void addActions() {
        TealAction ta = null;
        ta = new TealAction("Faraday's Law: Rotating Ring", this);
        addAction("Help", ta);

       
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Faraday's Law: Rotating Ring") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/faradayslawrotation.html");
        	}
        } else if (e.getActionCommand().equalsIgnoreCase("Manual Mode")) {
            reset();
        } else if (e.getActionCommand().equalsIgnoreCase("Generator Mode")) {
            resetGeneratorMode();

        } else if (e.getActionCommand().equalsIgnoreCase("FM_TOGGLE_LINES")) {
            boolean vis = fmanager.isDrawn();
            fmanager.setDrawn(!vis);

        } else {
            super.actionPerformed(e);
        }
    }

    public void reset() {
        resetting = true;
        if (mSEC.getSimState() == TEngineControl.RUNNING) {
            mSEC.stop();
        }

        roc.setPosition(ringPosition);
        roc.setVelocity(new Vector3d());
        roc.setDirection(new Vector3d(1., 0., 0.));
        roc.setAngularVelocity(new Vector3d(0., 0., 0.));
        roc.setCurrent(0.0);
        roc.reset();

        Vector3d mag_position = new Vector3d(5., 0., 0.);
        mag_gizmo.setPosition(mag_position);
        mag_constraint.setPoint(mag_position);
        mag.setPosition(mag_position);
        mag.setVelocity(new Vector3d(0., 0., 0.));

        flux_graph.clear(0);
        flux_graph.clear(1);
        flux_graph.setXRange(0., 8.);
        flux_graph.setYRange(-0.5, 0.5);
        flux_plot.reset();
        current_graph.clear(0);
        current_graph.setXRange(0., 8.);
        current_graph.setYRange(-0.5, 0.5);
        current_plot.reset();
        if(theEngine != null)
        	theEngine.setTime(0.);
        resetCamera();
        if (mSEC.getSimState() == TEngineControl.PAUSED) {
            mSEC.start();
        }
        resetting = false;
    }

    protected void resetGeneratorMode() {
        resetting = true;
        if (mSEC.getSimState() == TEngineControl.RUNNING) {
            mSEC.stop();
        }
        System.out.println("simState = " + mSEC.getSimState());

        roc.setPosition(ringPosition);
        roc.setVelocity(new Vector3d());
        roc.setDirection(new Vector3d(1., 0., 0.));
        roc.setAngularVelocity(new Vector3d(0., 3., 0.));
        roc.setCurrent(0.0);
        roc.reset();

        Vector3d position = new Vector3d(3., 0., 0.);
        mag_gizmo.setPosition(position);
        mag_constraint.setPoint(position);
        mag.setPosition(position);
        mag.setVelocity(new Vector3d(0., 0., 0.));

        flux_graph.clear(0);
        flux_graph.clear(1);
        flux_graph.setXRange(0., 8.);
        flux_graph.setYRange(-0.5, 0.5);
        flux_plot.reset();
        current_graph.clear(0);
        current_graph.setXRange(0., 8.);
        current_graph.setYRange(-0.5, 0.5);
        current_plot.reset();
        if(theEngine != null)
        	theEngine.setTime(0.);

        resetCamera();
        if (mSEC.getSimState() == TEngineControl.PAUSED) {
            mSEC.start();
        }
        resetting = false;
    }

    public void resetCamera() {
        Point3d from = new Point3d(0., 0., 14.);
        Point3d to = new Point3d(0., 0., 0.);
        Vector3d up = new Vector3d(0., 1., 0.);
        from.scale(0.05);
        to.scale(0.05);
        setLookAt(from, to, up);
    }

    

    

}
