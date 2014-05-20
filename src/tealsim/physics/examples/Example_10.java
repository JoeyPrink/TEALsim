/*
 * Created on Oct 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package tealsim.physics.examples;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.plot.Graph;
import teal.plot.PlotProperties;
import teal.render.j3d.Node3D;
import teal.render.j3d.WallNode;
import teal.render.primitives.Line;
import teal.sim.collision.SphereCollisionController;
import teal.sim.engine.EngineObj;
import teal.physics.physical.Wall;
import teal.physics.em.ConstantField;
import teal.physics.em.PointCharge;
import teal.sim.properties.IsSpatial;
import teal.physics.em.SimEM;
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
public class Example_10 extends SimEM {

    private static final long serialVersionUID = 3256443586278208051L;

	Graph position_graph;
	PlotProperties position_plot;
	PropertyDouble velXSlider;
	PropertyDouble velYSlider;
	JButton but = null;
    JButton but1 = null;
    JTaskPaneGroup vis;
    JLabel label;
    JLabel score;
    double minScore = 100000000.;
    PointCharge playerCharge;
    double wallscale = 2.0;
    double wheight = 3.0;
    double wallElasticity = 1.0;
    Vector3d wallheight = new Vector3d(0., 0., wheight);
    Appearance myAppearance;
	double velX = 10.;
	double velY = 0.;
    
    protected FieldConvolution mDLIC = null;
    FieldLineManager fmanager = null;
    ConstantField BField;
    ConstantField EField;
    SphereCollisionController sccx;

    public Example_10() {

        super();
        title = "Example_09";
        
//        EMEngine emModel = new EMEngine();
        TDebug.setGlobalLevel(0);

        // Building the world.
        setDamping(0.0);
        setGravity(new Vector3d(0., 0., 0.));
//        setEngine(emModel);

        BField = new ConstantField(new Vector3d(0., 0., 0.), new Vector3d(0, 0., 1.), 1.);
        BField.setID("cylField");
        BField.setMagnitude(1.);
        BField.setType(ConstantField.B_FIELD);
        addElement(BField);
        
        EField = new ConstantField(new Vector3d(0., 0., 0.), new Vector3d(0, 1., 0.), .1);
        EField.setID("cylField");
        EField.setMagnitude(2.);
        EField.setType(ConstantField.E_FIELD);
        addElement(EField);

        // Creating components.

        // -> Rectangular Walls
        myAppearance = Node3D.makeAppearance(new Color3f(Color.GRAY), 0.5f, 0.5f, false);
        myAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.5f));

        double dwidth = 0.; 
        // west wall
        addWall(new Vector3d(-10., 0., 0.), new Vector3d(0., 10, 0.), wallheight);
        //north wall
        addWall(new Vector3d(-dwidth*.5, 5, 0.), new Vector3d(20-dwidth, 0., 0.), wallheight);
        // southwall
        addWall(new Vector3d(0., -5., 0.), new Vector3d(20., 0., 0.), wallheight);
        //east wall
        addWall(new Vector3d(10., -dwidth*.5, 0.), new Vector3d(0., 10-dwidth, 0.), wallheight);
        Line l = new Line(new Vector3d(-10, 0., 0.), new Vector3d(10., 0., 0.));
        l.setColor(Color.WHITE);
        addElement(l);

        // Set charge
        double pointChargeRadius = 0.4;
        playerCharge = new PointCharge();
        playerCharge.setRadius(pointChargeRadius);
        //playerCharge.setPauliDistance(4.*pointChargeRadius);
        playerCharge.setMass(1.);
        playerCharge.setCharge(10.);
        playerCharge.setID("playerCharge");
        playerCharge.setPickable(false);
        playerCharge.setColliding(true);
        playerCharge.setGeneratingP(true);
        playerCharge.setPosition(new Vector3d(-8., 0., 0.));
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
        chargeSlider.setValue(5.);
        chargeSlider.setVisible(true);
        
        velXSlider = new PropertyDouble();
        velXSlider.setText("Y Velocity:");
        velXSlider.setMinimum(-10.);
        velXSlider.setMaximum(10.);
        velXSlider.setBounds(40, 535, 415, 50);
        velXSlider.setPaintTicks(true);
        velXSlider.addPropertyChangeListener("value",this);
        velXSlider.setValue(10.);

        velYSlider = new PropertyDouble();
        velYSlider.setText("Z velocity:");
        velYSlider.setMinimum(-10.);
        velYSlider.setMaximum(10.);
        velYSlider.setBounds(40, 535, 415, 50);
        velYSlider.setPaintTicks(true);
        velYSlider.addPropertyChangeListener("value",this);
        velYSlider.setValue(0.);


        //JTaskPane tp = new JTaskPane();
        ControlGroup params = new ControlGroup();
        params.setText("Parameters");
        params.add(chargeSlider);
        params.add(velXSlider);
        params.add(velYSlider);
        addElement(params);
        //tp.add(params);
        
    	// Create a graph of the xy trajectory of the charge and add it to the GUI.  
        // This involves creating a graph, adding a "plot" (which defines the 
        // quantities being plotted), and adding it in its own Control Group.
        
		// Graph constructor.
		position_graph = new Graph();		
		position_graph.setSize(400, 200);		
		position_graph.setXRange(-10, 10.);		
		position_graph.setYRange(-5., 5.);
		position_graph.setWrap(true);
		position_graph.setClearOnWrap(true);
		position_graph.setXLabel("y");		
		position_graph.setYLabel("z");
		// Here we create the PlotItem being drawn by this graph.  
		// We want to plot the xy-position of the charge as time progresses 
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

        addActions();
        
        theEngine.setDeltaTime(.05);
        mSEC.init();

        resetCamera();
        reset();
    }

    private void addWall(Vector3d pos, Vector3d length, Vector3d height) {
        Wall myWall = new Wall(pos, length, height);
        myWall.setElasticity(wallElasticity);
        myWall.setColor(Color.GREEN);
        myWall.setPickable(false);
        WallNode myNode = (WallNode) myWall.getNode3D();
        myNode.setFillAppearance(myAppearance);
        addElement(myWall);
    }

    void addActions() {
        TealAction ta = new TealAction("Execution & View", this);
        addAction("Help", ta);
        TealAction tb = new TealAction("Example_09", this);
        addAction("Help", tb);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Example_09") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework) mFramework).openBrowser("help/example_09.html");
        	}
        	
        } else if (e.getActionCommand().compareToIgnoreCase("Execution & View") == 0)  {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/executionView.html");
        	}
        } else {
            super.actionPerformed(e);
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {

    	if (pce.getSource()==velXSlider) {
    		velX= ((Double)velXSlider.getValue()).doubleValue();
    	    playerCharge.setVelocity(new Vector3d(velX, velY, 0.));
    	}
      	if (pce.getSource()==velYSlider) {
    		velY= ((Double)velYSlider.getValue()).doubleValue();
    	    playerCharge.setVelocity(new Vector3d(velX, velY, 0.));
    	}
        super.propertyChange(pce);
    }

    public void reset() {
        mSEC.stop();
        mSEC.reset();
        resetPointCharges();
        theEngine.requestRefresh();
        position_graph.clear(0);
    }

    private void resetPointCharges() {
        playerCharge.setPosition(new Vector3d(-8.0, 0.0, 0.));
        playerCharge.setVelocity(new Vector3d(velX, velY, 0.));
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 1.5), new Point3d(), new Vector3d(0., 1., 0.));

    }

}
