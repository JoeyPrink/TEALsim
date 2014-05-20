/*
 * Created on Oct 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package tealsim.physics.em;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.plot.Graph;
import teal.plot.PlotProperties;
import teal.render.BoundingSphere;
import teal.render.Bounds;
import teal.render.TealMaterial;
import teal.render.primitives.Line;
import teal.sim.collision.SphereCollisionController;
import teal.sim.engine.EngineObj;
import teal.sim.engine.TSimEngine;
import teal.physics.em.SimEM;
import teal.physics.physical.Wall;
import teal.physics.em.ConstantField;
import teal.physics.em.PointCharge;
import teal.sim.properties.IsSpatial;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldLineManager;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.ui.swing.JTaskPaneGroup;
import teal.util.TDebug;

/**
 * @author danziger
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ChargeInMagneticFieldGame extends SimEM {

    private static final long serialVersionUID = 3256443586278208051L;

	Graph position_graph;
	PlotProperties position_plot;
	JButton but = null;
    JButton but1 = null;
    JTaskPaneGroup vis;
    JLabel label;
    JLabel score;
    double minScore = 100000000.;
    PointCharge playerCharge;
    Watcher watch;
    double wallscale = 2.0;
    double wheight = 3.0;
    double wallElasticity = 1.0;
    Vector3d wallheight = new Vector3d(0., 0., wheight);

    
    protected FieldConvolution mDLIC = null;
    FieldLineManager fmanager = null;
    ConstantField BField;
    SphereCollisionController sccx;

    public ChargeInMagneticFieldGame() {

        super();
        title = "Charge In A Magnetic Field";
        
      
        TDebug.setGlobalLevel(0);
        setBoundingArea(new BoundingSphere(new Point3d(0., 0., 0), 12));
        // Building the world.
        setDamping(0.0);
        setGravity(new Vector3d(0., 0., 0.));
       

        BField = new ConstantField(new Vector3d(0., 0., 0.), new Vector3d(0, 0., -1.), .1);
        BField.setID("cylField");
        BField.setMagnitude(.5);
        //BField.addPropertyChangeListener("magnitude", this);
        //BField.setModel(emModel);
        BField.setType(ConstantField.B_FIELD);
        addElement(BField);

        // Creating components.

        // -> Rectangular Walls
        TealMaterial mat = new TealMaterial(Color.GREEN, 0.5f, 0.5f);
        

        double dwidth = 1.; 
        // west wall
        addWall(new Vector3d(-10., 0., 0.), new Vector3d(0., 20, 0.), wallheight,mat);
        //north wall
        addWall(new Vector3d(-dwidth*.5, 10., 0.), new Vector3d(20-dwidth, 0., 0.), wallheight,mat);
        // south wall
        addWall(new Vector3d(0., -10., 0.), new Vector3d(20., 0., 0.), wallheight,mat);
        //east wall
        addWall(new Vector3d(10., -dwidth*.5, 0.), new Vector3d(0., 20-dwidth, 0.), wallheight,mat);
        Line l = new Line(new Vector3d(-10, 0., 0.), new Vector3d(10., 0., 0.));
        l.setColor(Color.WHITE);
        addElement(l);

        // Set charge
        double pointChargeRadius = 0.4;
        playerCharge = new PointCharge();
        playerCharge.setRadius(pointChargeRadius);
        //playerCharge.setPauliDistance(4.*pointChargeRadius);
        playerCharge.setMass(1.);
        playerCharge.setCharge(1.);
        playerCharge.setID("playerCharge");
        playerCharge.setPickable(false);
        playerCharge.setColliding(true);
        playerCharge.setGeneratingP(true);
        playerCharge.setPosition(new Vector3d(-0., 0., 0.));
        playerCharge.setVelocity(new Vector3d(10., 0., 0.));
        playerCharge.setMoveable(true);
        sccx = new SphereCollisionController(playerCharge);
        sccx.setRadius(pointChargeRadius);
        sccx.setTolerance(0.1);
        sccx.setMode(SphereCollisionController.WALL_SPHERE);
        playerCharge.setCollisionController(sccx);
        //playerCharge.addPropertyChangeListener("charge",this );
        addElement(playerCharge);
        
        PropertyDouble chargeSlider = new PropertyDouble();
        chargeSlider.setText("Charge:");
        chargeSlider.setMinimum(-10.);
        chargeSlider.setMaximum(10.);
        chargeSlider.setBounds(40, 535, 415, 50);
        chargeSlider.setPaintTicks(true);
        chargeSlider.addRoute(playerCharge, "charge");
        chargeSlider.setValue(10.);
        //addElement(chargeSlider);
        chargeSlider.setVisible(true);
        label = new JLabel("Current Time:");
        score = new JLabel();
        label.setBounds(40, 595, 140, 50);
        score.setBounds(220, 595, 40, 50);
        label.setVisible(true);
        score.setVisible(true);
        //addElement(label);
        //addElement(score);
        watch = new Watcher();
        addElement(watch);

        //JTaskPane tp = new JTaskPane();
        ControlGroup params = new ControlGroup();
        params.setText("Parameters");
        params.add(chargeSlider);
        params.add(label);
        params.add(score);
        addElement(params);
        //tp.add(params);
        
    	// Create a graph of the height of the coil, and add it to the GUI.  
        // This involves creating a graph, adding a "plot" (which defines the 
        // quantities being plotted), and adding it in its own Control Group.
        
		// Graph constructor.
		position_graph = new Graph();		
		position_graph.setSize(250, 250);		
		position_graph.setXRange(-10, 10.);		
		position_graph.setYRange(-10., 10.);
		position_graph.setWrap(true);
		position_graph.setClearOnWrap(true);
		position_graph.setXLabel("x");		
		position_graph.setYLabel("y");
		// Here we create the PlotItem being drawn by this graph.  
		// We want to plot the y-position of the RingOfCurrent versus time, 
		// so we use  PlotProperties
		position_plot = new PlotProperties();
		position_plot.setObjectX(playerCharge); 
		position_plot.setPropertyX("x");  
		position_plot.setObjectY(playerCharge); 
		position_plot.setPropertyY("y");  
		// adds the supplied PlotItem to the graph.
		position_graph.addPlotItem(position_plot);
                
		
		// Here we create a new Control Group for the graph, and add the graph to that Group.
		ControlGroup graphPanel = new ControlGroup();
		graphPanel.setText("Graphs");
		graphPanel.addElement(position_graph);
		addElement(graphPanel);
		
 
        //tp.add(vis);
        //addElement(tp);

        addActions();
        watch.setActionEnabled(true);
        
        setDeltaTime(.05);
       
    }
    
    public void initialize(){
    	super.initialize();
    	 mSEC.init();
         resetCamera();
	}

    private void addWall(Vector3d pos, Vector3d length, Vector3d height,TealMaterial mat) {
        Wall myWall = new Wall(pos, length, height);
        myWall.setElasticity(wallElasticity);
        myWall.setMaterial(mat);
        myWall.setPickable(false);
        addElement(myWall);
    }

    void addActions() {

        TealAction ta = new TealAction("EM Video Game", this);
        addAction("Help", ta);

        ta = new TealAction("Level Complete", "Level Complete", this);
        watch.setAction(ta);

        

        
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("EM Video Game") == 0) {
            if ((mFramework != null) && (mFramework instanceof TFramework)) {
                ((TFramework)mFramework).openBrowser("help/emvideogame.html");
            }
        } else if (e.getActionCommand().compareToIgnoreCase("Level complete") == 0) {
            if ((mFramework != null) && (mFramework instanceof TFramework)) {
                ((TFramework)mFramework).openBrowser("help/emvideogame.html");
            }
        } else {
            super.actionPerformed(e);
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {
        super.propertyChange(pce);
    }

    public void reset() {
        mSEC.stop();
        mSEC.reset();
        resetPointCharges();
        //theEngine.requestRefresh();
        watch.setActionEnabled(true);
        position_graph.clear(0);
    }

    private void resetPointCharges() {
        playerCharge.setPosition(new Vector3d(-10.0, 0.0, 0.));
        playerCharge.setVelocity(new Vector3d(10., 0., 0.));
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 2.0), new Point3d(), new Vector3d(0., 1., 0.));

    }

    public class Watcher extends EngineObj implements IsSpatial {

        private static final long serialVersionUID = 3761692286114804280L;
        Bounds testBounds = new BoundingSphere(new Point3d(10.,10.,0.),.2);
        //Bounds testBounds = new BoundingBox(new Point3d(8., -16., -1.5), new Point3d(12., -12., 1.5));
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

        public void setBounds(Bounds b) {
            testBounds = b;
        }

        public void nextSpatial() {
            if (theEngine != null) {
                double time = ((TSimEngine)theEngine).getTime();
                score.setText(String.valueOf(time));
                if (actionEnabled) {
                    if (testBounds.intersect(new Point3d(playerCharge.getPosition()))) {
                        System.out.println("congratulations");
                        // Make this a one-shot
                        actionEnabled = false;
                        mSEC.stop();
                        minScore = Math.min(minScore, time);
                        //if (theAction != null) {
                          //  theAction.triggerAction();
                       // }
                    }
                }

            }
        }
    }

  

}
