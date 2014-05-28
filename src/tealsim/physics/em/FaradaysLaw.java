/* $Id: FaradaysLaw.java,v 1.16 2010/09/22 15:48:11 pbailey Exp $ */

/**
 * A demonstration implementation of the TFramework.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.16 $
 */

package tealsim.physics.em;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.media.j3d.Appearance;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.plot.CurrentPlot;
import teal.plot.FluxPlot;
import teal.plot.Graph;
import teal.render.BoundingSphere;
import teal.render.Rendered;
import teal.render.j3d.Node3D;
import teal.render.j3d.ShapeNode;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.viewer.TViewer;
import teal.sim.constraint.LDPSConstraint;
import teal.sim.constraint.SpringConstraint;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.EngineObj;
import teal.sim.engine.TEngineControl;
import teal.physics.physical.Ball;
import teal.physics.em.SimEM;
import teal.physics.em.MagneticDipole;
import teal.physics.em.RingOfCurrent;
import teal.sim.properties.IsSpatial;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FluxFieldLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyCheck;
import teal.ui.control.PropertyDouble;
import teal.ui.swing.JTaskPane;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;
import tealsim.gamification.FluxRequirement;
import tealsim.gamification.GamificationAgent;
import tealsim.gamification.Requirement;
import tealsim.gamification.Task;

public class FaradaysLaw extends SimEM {

    private static final long serialVersionUID = 3257289140818097457L;
    
    /** An imported 3DS object to represent current in the ring.  */
    Rendered currentSpheres = new Rendered();
    /** A 3D node for the representation of current flowing in the ring */
    Node3D currentSpheresNode = new Node3D();
    double currentangle;
    double deltaTime = 0.1;
    JLabel label;    //added jwb 7/13/2008
    JLabel score;  //added jwb 7/13/2008
    Watcher watch;  //added jwb 7/13/2008
    boolean icleargraph = false;  //added jwb 7/13/2008
    JButton but = null;
    JButton but1 = null;
    JTaskPane tp;
    PropertyCheck showRingCurrent;
    
    ControlGroup params,graphs;
    VisualizationControl vis;
    RingOfCurrent roc;
    MagneticDipole mag;
    LDPSConstraint mag_constraint;
    LDPSConstraint roc_constraint;
    Ball mag_gizmo;
    Ball roc_gizmo;
    
    GamificationAgent gamificationPanel;
    Task task1, task2, task3;

    FieldLineManager fmanager;
    protected FieldConvolution mDLIC = null;
    protected FieldDirectionGrid fv = null;

    Graph flux_graph;
    FluxPlot flux_plot;
    Graph current_graph;
    CurrentPlot current_plot;

    PropertyDouble sliderroc;
    PropertyDouble slidermag;

    double maximumResistance = 5.;
    double minDist = 0.375;
    double ringDist = 0.02;

    final boolean gizmos_visible = false;
    boolean resetting = false;

    // Bounds (percentages).
    Rectangle viewer_ = new Rectangle(2, 2, 96, 50);
    Rectangle graph1_ = new Rectangle(2, 52, 43, 30);
    Rectangle graph2_ = new Rectangle(45, 52, 54, 30);
    Rectangle slider1_ = new Rectangle(2, 82, 45, 12);
    Rectangle slider2_ = new Rectangle(50, 82, 48, 12);
    Rectangle button1_ = new Rectangle(20, 95, 28, 3);
    Rectangle button2_ = new Rectangle(52, 95, 28, 3);
    // Default size of the application.
    Dimension defaultSize = new Dimension(700, 700);
    Dimension dynamicSize = new Dimension();

    public FaradaysLaw() {
        super();
        TDebug.setGlobalLevel(-1);

        double ringRad = 1.25;
        title = "Faraday's Law";
        setBoundingArea(new BoundingSphere(new Point3d(), 6));
        setDeltaTime(deltaTime);
        setGravity(new Vector3d());
        setDamping(0.);
        
        // code below added jwb 7/2008 to put in balls that represent current
        double scale3DS = 0.045; // this is an overall scale factor for these .3DS objects
        Loader3DS max = new Loader3DS();
        BranchGroup bg01 = 
         max.getBranchGroup("models/currentring.3DS",
         "models/");
        currentSpheresNode.setScale(scale3DS);
        currentSpheresNode.addContents(bg01);
        currentSpheres.setNode3D(currentSpheresNode);
        currentSpheres.setPosition(new Vector3d(.2, 1.2, 0.));
        currentSpheres.setDrawn(true);
        addElement(currentSpheres);
        // Replaced JCheckBox with PropertyCheck phb 7/15/2008
        // trying to toggle current ring on and off, not working  jwb  7/2008      
        //showRingCurrent = new PropertyCheck("Show Motion of Positive Charges in Ring", RingCurrentShown);
        showRingCurrent = new PropertyCheck();
        showRingCurrent.setValue(true);
        showRingCurrent.setText("Show Positive Q");
        showRingCurrent.addPropertyChangeListener("value",this);
        addElement(showRingCurrent);
        
        setMouseMoveScale(0.07,0.07,1.);
        setCursorOnDrag(false);
        setShowGizmos(false);
        setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM);

        mSEC.setVisible(true);
        mDLIC = new FieldConvolution();
        RectangularPlane rec = new RectangularPlane(new Vector3d(-6., -6., 0.), new Vector3d(-6., 6., 0.),
            new Vector3d(6., 6., 0.));
        mDLIC.setSize(new Dimension(512, 512));
        mDLIC.setComputePlane(rec);
        mDLIC.setAutoGenerate(false);

        addElement(mDLIC);

        mag = new MagneticDipole();
        mag.setMu(1.5);
        mag.setPosition(new Vector3d(5., 0., 0.));
        mag.setDirection(new Vector3d(1, 0, 0));
        mag.setPickable(false);
        mag.setRotable(false);
        mag.setMoveable(true);
        mag.setIntegrating(true);
        mag.setLength(0.75);
        mag.setFeelsBField(false);
        mag.setAvoidSingularity(true);
        mag.setAvoidSingularityScale(1.);

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
        roc.setPosition(new Vector3d());
        roc.setDirection(new Vector3d(1., 0., 0.));
        roc.setPickable(true);
        roc.setRotable(true);
        roc.setMoveable(true);
        roc.setRadius(ringRad);
        roc.addPropertyChangeListener(this);
        roc.setInductance(1.);
        roc.setFeelsBField(false);

        roc_constraint = new LDPSConstraint();
        roc_constraint.setPoint(new Vector3d());
        roc_constraint.setK1(10.);
        roc_constraint.setK2(20.);
        roc_constraint.setP(0.5);
        roc.setConstraint(roc_constraint);
        roc.setConstrained(true);
        roc.addPropertyChangeListener(this);
        roc_constraint = (LDPSConstraint) roc.getConstraint();

        addElement(mag);
        addElement(roc);
        roc.setInducing(true);
        roc.setIntegrationMode(RingOfCurrent.AXIAL);

        mag_gizmo = new Ball();
        mag_gizmo.setPosition(mag.getPosition());
        mag_gizmo.setPickable(true);
        mag_gizmo.setSelectable(true);
        mag_gizmo.setColliding(false);
        mag_gizmo.setRadius(0.5);
        mag_gizmo.setColor(Color.LIGHT_GRAY);
        if (!gizmos_visible) {
            ShapeNode node = (ShapeNode) mag_gizmo.getNode3D();
            Appearance app = node.getAppearance();
            app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 1.f));
            node.setAppearance(app);
        }
        mag_gizmo.addPropertyChangeListener("position", this);
        addElement(mag_gizmo);

        roc_gizmo = new Ball();
        roc_gizmo.setPosition(roc.getPosition());
        roc_gizmo.setPickable(true);
        roc_gizmo.setSelectable(true);
        roc_gizmo.setColliding(false);
        roc_gizmo.setRadius(0.5);
        roc_gizmo.setColor(Color.LIGHT_GRAY);
        if (!gizmos_visible) {
            ShapeNode node = (ShapeNode) roc_gizmo.getNode3D();
            Appearance app = node.getAppearance();
            app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 1.f));
            node.setAppearance(app);
        }
        roc_gizmo.addPropertyChangeListener("position", this);
        addElement(roc_gizmo);

        FluxFieldLine fl = null;
        fmanager = new FieldLineManager();
        fmanager.setElementManager(this);
        
        fl = new FluxFieldLine(-8.0, roc, true, true); // -10
        fl.setIntegrationMode(FluxFieldLine.RUNGE_KUTTA);
        fl.setMinDistance(ringDist);
        fl.setColorMode(FieldLine.COLOR_VERTEX);
        
        //addElement(fl);
        fmanager.addFieldLine(fl);

        fl = new FluxFieldLine(-4.0, roc, true, true); // -30
        fl.setIntegrationMode(FluxFieldLine.RUNGE_KUTTA);
        fl.setMinDistance(ringDist);
        
        //addElement(fl);
        fmanager.addFieldLine(fl);

        fl = new FluxFieldLine(8.0, roc, true, true); // -30
        fl.setIntegrationMode(FluxFieldLine.RUNGE_KUTTA);
        fl.setMinDistance(ringDist * 2.);
        
        //addElement(fl);

        fl = new FluxFieldLine(18.0, mag, true, false);
        fl.setSArc(0.1);
        fl.setMinDistance(minDist);
        
        //addElement(fl);
        fmanager.addFieldLine(fl);

        fl = new FluxFieldLine(30.0, mag, true, false);
        fl.setSArc(0.1);
        fl.setMinDistance(minDist);
        
        //addElement(fl);
        fmanager.addFieldLine(fl);

        fl = new FluxFieldLine(50.0, mag, true, false);
        fl.setSArc(0.1);
        fl.setMinDistance(minDist);
        fl.setColorMode(FieldLine.COLOR_VERTEX);
        //addElement(fl);
        fmanager.addFieldLine(fl);
        //fmanager.setColorMode(FieldLine.COLOR_VERTEX);
        fmanager.setSymmetryAxis(new Vector3d(1.,0.,0.));
        addElement(fmanager);

        // Gamification Client startet==========================================
        gamificationPanel = new GamificationAgent(mSEC);
        
        // task 1:
        task1 = new Task();
        task1.addDescription("this is task1");
        task1.addHint("Hint Me");
        FluxRequirement flux_req = new FluxRequirement();
        flux_req.addRing(roc);
        flux_req.addFluxRange(0.2, 0.3);
        task1.addRequirement(flux_req);
        gamificationPanel.addTask(task1);

        // task 2:
        task2 = new Task();
        task2.addDescription("this is task2");
        task2.addHint("Hint Me");
        FluxRequirement flux_req2 = new FluxRequirement();
        flux_req2.addRing(roc);
        flux_req2.addFluxValue(0.4);
        task2.addRequirement(flux_req2);
        gamificationPanel.addTask(task2);
        
        addElement(gamificationPanel);
        
        slidermag = new PropertyDouble();
        slidermag.setText("Dipole Moment");
        slidermag.setMinimum(0.);
        slidermag.setMaximum(4.0);
        slidermag.setPaintTicks(true);
        slidermag.addRoute(mag, "mu");
        //		slidermag.addRoute(this, "mu");
      //  slidermag.setValue(Teal.MagnetDefaultMu);
        slidermag.setValue(1.5);
        slidermag.setVisible(true);
        // addElement(slidermag);

        sliderroc = new PropertyDouble();
        sliderroc.setText("Ring Resistance");
        sliderroc.setMinimum(0.);
        sliderroc.setMaximum(maximumResistance);
        sliderroc.setPrecision(0.01);
        sliderroc.setPaintTicks(true);
        sliderroc.addRoute(roc, "resistance");
        sliderroc.setValue(4.);
        sliderroc.setVisible(true);
        //addElement(sliderroc);

        flux_graph = new Graph();
        flux_graph.addGamification(gamificationPanel);
        flux_graph.setXRange(0., 12.);
        flux_graph.setYRange(-0.1, 0.5);
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
        //addElement(flux_graph);

        current_graph = new Graph();
        current_graph.setXRange(0., 12.);
        current_graph.setYRange(-0.4, 0.4);
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
        //addElement(current_graph);
        
 
        
        //tp = new JTaskPane();
        label = new JLabel("Current Time:");    //added jwb 7/13/2008
        score = new JLabel();     //added jwb 7/13/2008
        label.setBounds(40, 595, 140, 50);    //added jwb 7/13/2008
        score.setBounds(220, 595, 40, 50);     //added jwb 7/13/2008
        label.setVisible(true);    //added jwb 7/13/2008
        score.setVisible(true);    //added jwb 7/13/2008
        watch = new Watcher();    //added jwb 7/13/2008
        addElement(watch);    //added jwb 7/13/2008
        
        params = new ControlGroup();
        params.setText("Parameters");
   //     params.add(slidermag);
        params.add(sliderroc);
   //     params.add(showRingCurrent);
    //    params.add(label);    //added jwb 7/13/2008
  //      params.add(score);    //added jwb 7/13/2008
        addElement(params);
        //tp.add(params);
        graphs = new ControlGroup();
        graphs.setText("Graphs");
        graphs.addElement(flux_graph);
        graphs.addElement(current_graph);
        addElement(graphs);
        //tp.add(graphs);
        
        VisualizationControl vizPanel = new VisualizationControl();
        vizPanel.setFieldConvolution(mDLIC);
        vizPanel.setFieldLineManager(fmanager);
    //    vizPanel.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
        vizPanel.setConvolutionModes(DLIC.DLIC_FLAG_B);
        vizPanel.setSymmetryCount(2);
        vizPanel.setColorPerVertex(false);
        addElement(vizPanel);
       // tp.add(vizPanel);
        
        addActions();
        watch.setActionEnabled(true);     //added jwb 7/13/2008
        reset();
        mSEC.init();
      //  mSEC.start();

    }

    /*	public void setMu(double mu) {
     mSEC.stop();
     mag.setMu(mu);
     mSEC.start();
     }
     public double getMu() {
     return mag.getMu();
     }
     */
    

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
        } else if (pce.getSource() == mag_gizmo) {
            if (pce.getPropertyName().equalsIgnoreCase("position")) {
                Vector3d position = (Vector3d) pce.getNewValue();
                if (position.x > 5) position.x = 5;
                if (position.x < -5) position.x = -5;
                position.y = 0;
                position.z = 0;
                mag_gizmo.setPosition(position, false);
                mag_constraint.setPoint(position);
                theEngine.requestRefresh();
                int state = mSEC.getSimState();
                if (state == TEngineControl.PAUSED) {
                    mSEC.start();
                }
            }
        } else if (pce.getSource() == showRingCurrent) {
               if (pce.getPropertyName().equalsIgnoreCase("value")) {
           			System.out.println("Got here");         	   
           			Object obj = showRingCurrent.getValue();
           			if( obj instanceof Boolean )
           			{
           				currentSpheres.setDrawn( ( (Boolean)obj ).booleanValue() );
           				theEngine.requestRefresh();
           			}
           		
               }      
        } else if (pce.getSource() == roc_gizmo) {
            if (pce.getPropertyName().equalsIgnoreCase("position")) {
                Vector3d position = (Vector3d) pce.getNewValue();
                if (position.x > 5) position.x = 5;
                if (position.x < -5) position.x = -5;
                position.y = 0;
                position.z = 0;
                roc_gizmo.setPosition(position, false);
                roc_constraint.setPoint(position);
                theEngine.requestRefresh();
                int state = mSEC.getSimState();
                if (state == TEngineControl.PAUSED) {
                   mSEC.start();
               }
            }

        } else {
            super.propertyChange(pce);
        }
    }

    void addActions() {
        TealAction ta = null;
        ta = new TealAction("Faraday's Law", this);
        addAction("Help", ta);

        ta = new TealAction("Manual Mode", this);
        addAction("Actions", ta);
        ta = new TealAction("Generator Mode", this);
        addAction("Actions", ta);
       
        ta = new TealAction("Level Complete", "Level Complete", this);    //added jwb 7/13/2008
        watch.setAction(ta);                                                //added jwb 7/13/2008
       
         ta = new TealAction("Start Gamification", this);
        addAction("Actions", ta);
        
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Faraday's Law") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework) mFramework).openBrowser("help/faradayslaw.html");
        	}
        } else if (e.getActionCommand().equalsIgnoreCase("Manual Mode")) {
            reset();
        } else if (e.getActionCommand().equalsIgnoreCase("Generator Mode")) {
            resetGeneratorMode();
        } else if (e.getActionCommand().equalsIgnoreCase("Reset Layout")) {
            //resetLayout();
        }
        else if (e.getActionCommand().equalsIgnoreCase("Start Gamification")) {
           gamificationPanel.startTasks();
        }
        else {
            super.actionPerformed(e);
        }
    }

    public void reset() {
        resetting = true;
        if (mSEC.getSimState() == TEngineControl.RUNNING) {
            mSEC.stop();
        }

        Vector3d roc_position = new Vector3d(0., 0., 0.);
        roc_gizmo.setPosition(roc_position);
        roc_constraint.setPoint(roc_position);
        roc.setPosition(roc_position);
        roc.setVelocity(new Vector3d());
        roc.setDirection(new Vector3d(1., 0., 0.));
        roc.setCurrent(0.0);
        roc.reset();

        Vector3d mag_position = new Vector3d(5., 0., 0.);
        mag_gizmo.setPosition(mag_position);
        mag_constraint.setPoint(mag_position);
        mag.setPosition(mag_position);
        mag.setVelocity(new Vector3d(0., 0., 0.));
        mag.setConstraint(mag_constraint);

        flux_graph.clear(0);
        flux_graph.clear(1);
        flux_graph.setXRange(0., 12.);
        flux_graph.setYRange(-0.1, 0.5);
        flux_plot.reset();
        current_graph.clear(0);
        current_graph.setXRange(0., 12.);
        current_graph.setYRange(-0.4, 0.4);
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

        Vector3d roc_position = new Vector3d(-0.1, 0., 0.);
        roc_gizmo.setPosition(roc_position);
        roc_constraint.setPoint(roc_position);
        roc.setPosition(roc_position);
        roc.setVelocity(new Vector3d());
        roc.setDirection(new Vector3d(1., 0., 0.));
        roc.setCurrent(0.0);
        roc.reset();

        Vector3d mag_position = new Vector3d(2., 0., 0.);
        mag_gizmo.setPosition(mag_position);
        mag_constraint.setPoint(mag_position);
        mag.setPosition(mag_position);
        mag.setVelocity(new Vector3d(0., 0., 0.));
        SpringConstraint spring = new SpringConstraint(new Vector3d(-2., 0., 0.), 3., 5.);
        mag.setConstraint(spring);

        flux_graph.clear(0);
        flux_graph.clear(1);
        flux_graph.setXRange(0., 12.);
        flux_graph.setYRange(-0.1, 0.5);
        flux_plot.reset();
        current_graph.clear(0);
        current_graph.setXRange(0., 12.);
        current_graph.setYRange(-0.4, 0.4);
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
    // this whole Watcher class added jwb 7/13/2008
    public class Watcher extends EngineObj implements IsSpatial {

        private static final long serialVersionUID = 3761692286114804280L;
        TealAction theAction = null;
        boolean actionEnabled = false;
        boolean mNeedsSpatial = false;

        public void needsSpatial() {
            mNeedsSpatial = true;
        }

        public void setAction(TealAction ac) {
            theAction = ac;
        }

        public void setActionEnabled(boolean state) {
            actionEnabled = state;
        }

        public boolean getActionEnabled() {
            return actionEnabled;
        }



        public void nextSpatial() {
            if (theEngine != null) {
                double time = theEngine.getTime();
                Vector3d posroc = roc.getPosition();
                score.setText(String.valueOf(time));
                  if (actionEnabled) {
                     if (time > 11.9) {
                         theEngine.setTime(0.);
                         mSEC.stop();
                         icleargraph = true;       
                     }
                    if ( time < 11.9 && icleargraph == true) {
                    	icleargraph = false;
                    	flux_graph.clear(0);
                    	flux_graph.clear(1);
                    	current_graph.clear(0);
                    }     
                    currentSpheres.setPosition(new Vector3d(0.0+posroc.x,.0, -.1));
                    double currentroc = roc.getCurrent();
                    currentangle = currentangle - currentroc*5.;
                    double compx = Math.sin(currentangle);
                    double compy = Math.cos(currentangle);
                    currentSpheres.setNode3D(currentSpheresNode);
                    currentSpheres.setDirection(new Vector3d(0., compx, compy));
                }
            }
        }
    }

//    public void resetLayout() {
//        setSize(defaultSize);
//        repaint();
//    }

//    public void fixLayout() {
//        Dimension menuSize = this.mMenuBar.getSize();
//        int mX = menuSize.width;
//        int mY = menuSize.height;
//        int X = dynamicSize.width;
//        int Y = dynamicSize.height - 2 * mY; // Accounts also for the status bar ~ size of menu bar
//        Rectangle viewerBounds = new Rectangle(X * viewer_.x / 100, Y * viewer_.y / 100, X * viewer_.width / 100, Y
//            * viewer_.height / 100);
//        Rectangle graph1Bounds = new Rectangle(X * graph1_.x / 100, Y * graph1_.y / 100, X * graph1_.width / 100, Y
//            * graph1_.height / 100);
//        Rectangle graph2Bounds = new Rectangle(X * graph2_.x / 100, Y * graph2_.y / 100, X * graph2_.width / 100, Y
//            * graph2_.height / 100);
//        Rectangle slider1Bounds = new Rectangle(X * slider1_.x / 100, Y * slider1_.y / 100, X * slider1_.width / 100, Y
//            * slider1_.height / 100);
//        Rectangle slider2Bounds = new Rectangle(X * slider2_.x / 100, Y * slider2_.y / 100, X * slider2_.width / 100, Y
//            * slider2_.height / 100);
//        Rectangle button1Bounds = new Rectangle(X * button1_.x / 100, Y * button1_.y / 100, X * button1_.width / 100, Y
//            * button1_.height / 100);
//        Rectangle button2Bounds = new Rectangle(X * button2_.x / 100, Y * button2_.y / 100, X * button2_.width / 100, Y
//            * button2_.height / 100);
//        mViewer.setBounds(viewerBounds);
//        current_graph.setBounds(graph1Bounds);
//        flux_graph.setBounds(graph2Bounds);
//        sliderroc.setSliderWidth(slider1Bounds.width - 75);
//        sliderroc.setBounds(slider1Bounds);
//        slidermag.setSliderWidth(slider2Bounds.width - 75);
//        slidermag.setBounds(slider2Bounds);
//        but.setBounds(button1Bounds);
//        but1.setBounds(button2Bounds);
//        mGUI.refresh();
//    }
//
//    public void paint(Graphics g) {
//        Dimension size = new Dimension(getSize());
//        if (dynamicSize.width != size.width || dynamicSize.height != size.height) {
//            dynamicSize = size;
//            //fixLayout();
//        }
//        super.paint(g);
//    }
//
//    public static void main(String args[]) {
//
//        LookAndFeelTweaks.setLookAndFeel();
//        TealBasicApp theApp = new FaradaysLaw();
//        theApp.show();
//
//        //		BasicApp theApp = new FaradaysLaw();
//        //		BasicApp.initApplication();
//        //		((FaradaysLaw)theApp).resetLayout();
//        //		theApp.start();
//    }

}
