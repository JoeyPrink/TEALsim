/* $Id: FallingMagnet.java,v 1.11 2010/08/10 18:12:33 stefan Exp $ */

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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.border.EtchedBorder;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.plot.CurrentPlot;
import teal.plot.Graph;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
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
import teal.ui.control.meters.ControlMeter;
import teal.ui.swing.JTaskPaneGroup;
import teal.util.TDebug;

public class FallingMagnet extends SimEM {

    private static final long serialVersionUID = 3691044270362932275L;
    double deltaTime = 0.01;
    double maxDist = 0.375;
    JButton but = null;
    JCheckBox but1 = null;
    int symC = 2;
    double arclen = 0.1;
    RingOfCurrent roc;
    MagneticDipole m1;

    FieldLineManager fmanager;
    protected FieldConvolution mDLIC = null;
    protected FieldDirectionGrid fv = null;

    Graph graph;
    CurrentPlot plot;

    //Vector fLines;
    private PropertyCheck colorToggle;
    
    JTaskPaneGroup paramgroup, visgroup;
  
    public FallingMagnet() {

        super();
        TDebug.setGlobalLevel(0);
        double ringRad = 1.25;
        title = "Falling Magnet";
        
   

        setBoundingArea(new BoundingSphere(new Point3d(), 6));
        setDeltaTime(deltaTime);
        setDamping(0.);
       
        setViewerSize(800, 450);
        mSEC.setBounds(230, 475, 400, 32);
        mDLIC = new FieldConvolution();
        RectangularPlane rec = new RectangularPlane(new Vector3d(-6., -6., 0.), new Vector3d(-6., 6., 0.),
            new Vector3d(6., 6., 0.));
        mDLIC.setSize(new Dimension(512, 512));
        mDLIC.setComputePlane(rec);
        
        m1 = new MagneticDipole();
        m1.setMu(1.0);
        m1.setPosition(new Vector3d(0., 2., 0.));
        m1.setDirection(new Vector3d(0, 1, 0));
        m1.setPickable(false);
        m1.setRotable(false);
        m1.setMoveable(true);
        m1.setLength(0.75);
        m1.setAvoidSingularity(true);
        m1.setAvoidSingularityScale(1.);
        m1.addPropertyChangeListener(this);
        m1.setMass(0.005);

        roc = new RingOfCurrent();
        roc.setID("Ring");
        roc.setPosition(new Vector3d(0., 0., 0.));
        roc.setDirection(new Vector3d(0., 1., 0.));
        roc.setPickable(true);
        roc.setRotable(false);
        roc.setMoveable(false);
        roc.setRadius(ringRad);
        roc.setInductance(1.);

        addElement(m1);
        addElement(roc);
        roc.setInducing(true);
        roc.setIntegrationMode(RingOfCurrent.AXIAL);

        int fMode = FieldLine.RUNGE_KUTTA;
        //int fMode = FieldLine.EULER;
        int colorMode = FieldLine.COLOR_VERTEX;
        double colorScale = 0.01;
        FluxFieldLine fl;
        //fLines = new Vector();
        fmanager = new FieldLineManager();
        fmanager.setElementManager(this);

        fl = new FluxFieldLine(-10.0, roc, true, true);
        fl.setIntegrationMode(fMode);
        fl.setSymmetryCount(symC);
        fl.setSArc(arclen);
        fl.setColorMode(colorMode);
        fl.setColorScale(colorScale);

        //addElement(fl);
        //fLines.add(fl);
        fmanager.addFieldLine(fl);

        fl = new FluxFieldLine(19.0, roc, true, true);
        fl.setIntegrationMode(fMode);
        fl.setSymmetryCount(symC);
        fl.setSArc(arclen);
        fl.setColorMode(colorMode);
        fl.setColorScale(colorScale);
        fl.setBuildDir(FieldLine.BUILD_POSITIVE);
        fl.setKMax(50);

        //addElement(fl);
        //fLines.add(fl);
        fmanager.addFieldLine(fl);

        fl = new FluxFieldLine(19.0, m1, true, false);
        fl.setIntegrationMode(fMode);
        fl.setKMax(400);
        fl.setSymmetryCount(symC);
        fl.setSArc(arclen * 0.25);
        fl.setMinDistance(arclen * 2);
        fl.setColorMode(colorMode);
        fl.setColorScale(colorScale);
        //fLines.add(fl);
        fmanager.addFieldLine(fl);
        //addElement(fl);

        fl = new FluxFieldLine(50.0, m1, true, false);
        fl.setIntegrationMode(fMode);
        //fl.setSArc(0.1);
        fl.setKMax(300);
        fl.setSymmetryCount(symC);
        fl.setSArc(arclen);
        fl.setMinDistance(arclen * 2);
        //fl.setMinDistance(maxDist);
        //fl.setColorMode(FieldLine.COLOR_VERTEX);
        fl.setColorMode(colorMode);
        fl.setColorScale(colorScale);
        //fLines.add(fl);
        fmanager.addFieldLine(fl);
        //addElement(fl);

        fl = new FluxFieldLine(100.0, m1, true, false);
        fl.setIntegrationMode(fMode);
        //fl.setSArc(0.1);
        fl.setSymmetryCount(symC);
        fl.setSArc(arclen);
        fl.setMinDistance(arclen * 2);
        //fl.setMinDistance(maxDist);
        fl.setColorMode(colorMode);
        fl.setColorScale(colorScale);
        //fLines.add(fl);
        fmanager.addFieldLine(fl);
        //addElement(fl);
        fmanager.setColorScale(colorScale);
        addElement(fmanager);

        ControlMeter myMeter = new ControlMeter();
        myMeter.setLocation(550, 550);
        //myMeter.setSize(320, 240);
        myMeter.setSize(179, 129);
        myMeter.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        myMeter.setDeviceAndPropertyName(roc.getID(), "Current");
        //myMeter.setLabelStyle(ControlConstants.LABEL_All);
        //		myMeter.setRangeStyle(ControlConstants.RANGE_AUTO);
        //		myMeter.setColorZone(0.05, 0.1, ControlConstants.REDCOLOR.darker());
        //		myMeter.setColorZone(-0.05, 0.05, ControlConstants.BLUECOLOR.darker());
        //		myMeter.setColorZone(-0.1, -0.05, ControlConstants.GREENCOLOR.darker());
        myMeter.setDisplayRange(-0.1, 0.1);
        //myMeter.setAlarmRange(0.0, 0.1);
        //myMeter.setWarningRange(-0.1, 0.0);
        myMeter.setValue(0.0);
        roc.addPropertyChangeListener("current", myMeter);
        //		addElement(myMeter);

        /*
         PropertyInteger slider1 = new PropertyInteger();
         slider1.setMinimum(0);
         slider1.setMaximum(16);
         slider1.setBounds(35, 530, 410, 50);
         slider1.setPaintTicks(true);
         slider1.addRoute("value", fv, "resolution");
         slider1.setValue(8);
         slider1.setText("Vector Field Grid");
         //slider1.setBorder(null);
         addElement(slider1);
         */
        PropertyDouble sliderroc = new PropertyDouble();
        sliderroc.setText("Ring Resistance");
        sliderroc.setLabelWidth(120);
        sliderroc.setMinimum(0.);
        sliderroc.setMaximum(20.);
        //sliderroc.setBounds(35, 540, 410, 50);
        sliderroc.setPaintTicks(true);
        //sliderroc.setBorder(null);
        sliderroc.addRoute(roc, "resistance");
        sliderroc.setValue(2.5);
        sliderroc.setVisible(true);
        //addElement(sliderroc);

        PropertyDouble slidermag = new PropertyDouble();
        slidermag.setText("Magnet Strength (Mu)");
        slidermag.setLabelWidth(120);
        slidermag.setMinimum(0.);
        slidermag.setMaximum(2.0);
        slidermag.setPaintTicks(true);
        //slidermag.setBounds(35, 600, 410, 50);
        //slidermag.setBorder(null);
        slidermag.addRoute(m1, "mu");
        slidermag.setValue(Teal.MagnetDefaultMu);
        slidermag.setVisible(true);
        //addElement(slidermag);

        graph = new Graph();
        //graph.setBounds(520, 480, 275, 175);
        graph.setSize(350, 200);
        graph.setXRange(0., 6.);
        graph.setYRange(-0.4, 0.4);
        graph.setWrap(false);
        graph.setClearOnWrap(false);
        graph.setXLabel("Time");
        graph.setYLabel("Current");
        plot = new CurrentPlot();
        plot.setRing(roc);
        plot.setTimeAutoscale(true);
        plot.setCurrentAutoscale(true);
        graph.addPlotItem(plot);
        //addElement(graph);

       

        fv = new FieldDirectionGrid();
        fv.setType(Field.B_FIELD);
        fv.setDrawn(false);
        addElement(fv);

        

        //JTaskPane taskPane = new JTaskPane();

        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(sliderroc);
        controls.add(slidermag);
        addElement(controls);
        //taskPane.add(controls);

        VisualizationControl vizPanel = new VisualizationControl();
        vizPanel.setFieldLineManager(fmanager);
        vizPanel.setFieldConvolution(mDLIC);
        vizPanel.setFieldVisGrid(fv);
        addElement(vizPanel);

        ControlGroup graphPanel = new ControlGroup();
        graphPanel.setText("Graphs");
        graphPanel.addElement(graph);
        addElement(graphPanel);

        

        theScene.setFogEnabled(true);
        //mViewer.setFogEnabled(false);
        theScene.setFogTransformFrontScale(0.0);
        theScene.setFogTransformBackScale(0.35);
        addActions();
        //theEngine.refresh();
        mSEC.init();
        resetCamera();
        reset();
        //mGUI.refresh();
        System.out.println("Magnetic moment after: " + m1.getMu());
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getSource() == m1) {
            if (pce.getPropertyName().compareToIgnoreCase("position") == 0) {
                //System.out.println( "ROC Position: " + roc.getPosition() );
                if (m1.getY() <= -10.) {
                    mSEC.stop();
                    reset();
                }
            }
        } else if (pce.getSource() == colorToggle) {
            String pn = pce.getPropertyName();
            System.out.println("ColorToggle: " + pn);
            if (pn.compareTo("value") == 0) {
                boolean state = ((Boolean) pce.getNewValue()).booleanValue();
                System.out.println("ColorToggle: State = " + state);
                //setColoring(state);
                fmanager.setColorMode(state);
            }
        } else {
            super.propertyChange(pce);
        }
    }

    void addActions() {
        TealAction ta = new TealAction("Falling Magnet", this);
        addAction("Help", ta);

        //		ta = new TealAction("Reset", this);
        //		addAction("Actions", ta);
        //
        //		TealAction a = new TealAction("Grass Seeds", "DLIC_B", this);
        //		but = new JButton(a);
        //		but.setBounds(225, 725, 195, 24);
        //		addElement(but);
        //
        //		but1 = new JButton(new TealAction("Toggle Field Lines", TOGGLE_LINES, this));
        //		but1.setBounds(435, 725, 195, 24);
        //		addElement(but1);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Falling Magnet") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework) mFramework).openBrowser("help/fallingmagnet.html");
        	}
        } else if (e.getActionCommand().equalsIgnoreCase("Reset")) {
            reset();
        } else {
            super.actionPerformed(e);
        }
    }

    public void reset() {
        roc.setPosition(new Vector3d(0., 0., 0.));
        roc.setDirection(new Vector3d(0., 1., 0.));
        roc.setCurrent(0.0);
        roc.reset();
        m1.setPosition(new Vector3d(0., 2., 0.));
        m1.setDirection(new Vector3d(0, 1, 0));
        m1.setVelocity(new Vector3d());
        graph.clear(0);
        graph.clear(1);
        graph.setXRange(0., 2.);
        graph.setYRange(-0.1, 0.5);
        if(theEngine != null)
        	theEngine.requestRefresh();
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, .7), new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.));
    }
}
