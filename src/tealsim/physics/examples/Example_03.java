/* $Id: Example_03.java,v 1.6 2010/07/16 21:41:45 stefan Exp $ */
/**
 * @author John Belcher 
 * @version $Revision: 1.6 $
 */

package tealsim.physics.examples;

import java.awt.event.ActionEvent;
import javax.vecmath.*;

import teal.core.TElement;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.render.BoundingSphere;
import teal.sim.collision.SphereCollisionController;
import teal.physics.em.PointCharge;
import teal.physics.em.SimEM;
import teal.physics.physical.Wall;
import teal.util.TDebug;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import java.beans.PropertyChangeEvent;
import teal.plot.PlotProperties;
import teal.plot.Graph;


/** A simulation of a charge falling under gravity and bouncing off of a floor, with a graph
 * of the height of the charge versus time.
 * In addition, we add a slider that allows us to vary the amount of damping in the world.  
 * By setting the slider to a non-zero value, the user can get the height of the charge to decay exponentially.  
 * The user can set the amount of damping either by moving the slider or by entering a value in the box to 
 * the right of the slider <b> AND HITTING ENTER</b>. 
*  
* @author John Belcher
* @version 1.0 
* */

public class Example_03 extends SimEM {
	
	private static final long serialVersionUID = 3257008735204554035L;
    /** The friction slider. */
    PropertyDouble frictionSlider = new PropertyDouble();
    /** A graph which will contain the vertical position of the charge. */
	Graph position_graph;
    /** The properties of the position plot.   */
	PlotProperties position_plot;
    /** The friction in the world. */
    double friction;
    /** The falling charge.  */
    PointCharge floatingCharge;
    /** The vector position of the charge.  */
    Vector3d floatingChargePos;
    /** The radius of the sphere representing the charge.  */
    double chargeRad = 0.2;
    /** The mass of charge. */
    double chargeMass = 3.5;
    /** The charge of the charge. */
    double charge = 1.;
    
    public Example_03() {
        super();
        TDebug.setGlobalLevel(0);
        title = "Example_03";      
		///// Set properties on the SimEngine /////
		// Bounding area represents the size of the simulation
		// setDeltaTime() sets the time step of the simulation.
		// setDamping() sets the damping in the system.
       
        BoundingSphere bs = new BoundingSphere(new Point3d(0, 1.6, 0), 03.5);
        theEngine.setBoundingArea(bs);
        theEngine.setDeltaTime(0.02); // Was 0.005
        theEngine.setDamping(0.);  
        theScene.setBoundingArea(bs);
        
        
        floatingCharge = new PointCharge();
        floatingCharge.setID("Charge");
        floatingCharge.setCharge(charge);
        floatingCharge.setDirection(new Vector3d(0., 1., 0.));
        floatingChargePos = new Vector3d(0., 1.25, 0.);
        floatingCharge.setPickable(true);
        floatingCharge.setRotable(true);
        floatingCharge.setMoveable(true);
        floatingCharge.setRadius(chargeRad);
        floatingCharge.setMass(chargeMass);
        
		// Here we add a collisionController to coil so that it will be
        // registered as a colliding object when it touches the "wall".  
        // We represent the coil as a sphere for the purposes of collision
		// detection, since we're only concerned with stopping the coil from 
        // "falling through the floor"
		// SphereCollisionController constructor.
        
        SphereCollisionController sccx = 
        	new SphereCollisionController(floatingCharge);
        sccx.setRadius(chargeRad);
        sccx.setTolerance(0.01);
        floatingCharge.setColliding(true);
        floatingCharge.setCollisionController(sccx); 
        addElement(floatingCharge);
      
        // We create a "wall" that the floating coil will interact with
        
		// Wall constructor.  		
        Wall wall = new Wall(new Vector3d(0., 0, 0.), 
        		new Vector3d(2., 0., 0.), new Vector3d(0., 0., 2.));
        wall.setElasticity(1.);
        addElement(wall);

        // create the sliders to control the amount of friction in the model
        frictionSlider.setText("Friction");
        frictionSlider.setMinimum(0.);
        frictionSlider.setMaximum(2.0);
        frictionSlider.setPaintTicks(true);
        frictionSlider.addPropertyChangeListener("value", this);
        frictionSlider.setValue(0.0);
        frictionSlider.setVisible(true);

        // add the slider to a control group and add this to the scene

        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(frictionSlider);
        addElement(controls);
        
		// Create a graph of the height of the coil, and add it to the GUI.  
        // This involves creating a graph, adding a "plot" (which defines the 
        // quantities being plotted), and adding it in its own Control Group.
        
		// Graph constructor.
		position_graph = new Graph();		
		position_graph.setSize(150, 400);		
		position_graph.setXRange(0., 6.);		
		position_graph.setYRange(0., 2.);
		position_graph.setWrap(true);
		position_graph.setClearOnWrap(true);
		position_graph.setXLabel("Time");		
		position_graph.setYLabel("position");
		// Here we create the PlotItem being drawn by this graph.  
		// We want to plot the y-position of the RingOfCurrent versus time, 
		// so we use  PlotProperties
		position_plot = new PlotProperties();
		position_plot.setObjectX((TElement) theEngine); 
		position_plot.setPropertyX("time");  
		position_plot.setObjectY(floatingCharge); 
		position_plot.setPropertyY("y");  
		// adds the supplied PlotItem to the graph.
		position_graph.addPlotItem(position_plot);
		
		// Here we create a new Control Group for the graph, and add the graph to that Group.
		ControlGroup graphPanel = new ControlGroup();
		graphPanel.setText("Graphs");
		graphPanel.addElement(position_graph);
		addElement(graphPanel);
		
        // set paramters for mouseScale 
        setMouseMoveScale(0.05,0.05,0.5);

        mSEC.init();
        resetCamera();
        // addAction for pulldown menus on TEALsim windows     
        addActions();
        reset();  
    }
    /** Add menu items to our upper toolbar. */
    void addActions() {
        TealAction ta = new TealAction("Execution & View", this);
        addAction("Help", ta);
        TealAction tb = new TealAction("Example_03", this);
        addAction("Help", tb);
    }
    
    /** Set responses for when our menu items are chosen.  */
    public void actionPerformed(ActionEvent e) {
        TDebug.println(1, " Action comamnd: " + e.getActionCommand());
        if (e.getActionCommand().compareToIgnoreCase("Example_03") == 0) 
        {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/example_03.html");
        	}
        } 
        if (e.getActionCommand().compareToIgnoreCase("Execution & View") == 0) 
        {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/executionView.html");
        	}
        else {
            super.actionPerformed(e);}
        }
    }
        
    
    /** Return the simulation to its original configuration.  */
    public void reset() {
        floatingCharge.setPosition(floatingChargePos);
        floatingCharge.setVelocity(new Vector3d(0.,0.,0.));
        position_graph.clear(0);
        theEngine.setDamping(0.);
        frictionSlider.setValue(0.);
		theEngine.requestRefresh();
    }

    /** Reset the camera view to its original view. */
    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.025, 0.4), 
        		new Point3d(0., 0.025, 0.), new Vector3d(0., 1., 0.));
    }   
    /** Define the action initiated by the slider (i.e., set theEngine damping). 
     * @param pce The property change event when the friction slider is changed. */
    public void propertyChange(PropertyChangeEvent pce) {
        Object source = pce.getSource();
        if (source == frictionSlider) {
            friction = ((Double) pce.getNewValue()).doubleValue();
            theEngine.setDamping(friction);     
        } else {
            super.propertyChange(pce);
        }
    }   
}
