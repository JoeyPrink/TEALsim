/* $Id: FallingCoilCylindricalMagnet.java,v 1.14 2010/09/22 15:48:11 pbailey Exp $ */
/**
 * A 
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.14 $
 */

package tealsim.physics.em;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import teal.render.BoundingSphere;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.plot.CurrentPlot;
import teal.plot.Graph;
import teal.render.Rendered;
import teal.render.j3d.ShapeNode;
import teal.render.j3d.geometry.Cylinder;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.scene.TNode3D;
import teal.render.scene.TShapeNode;
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.physical.Wall;
import teal.physics.em.LineMagneticDipole;
import teal.physics.em.RingOfCurrent;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FluxFieldLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.util.URLGenerator;
import teal.visualization.dlic.DLIC;

// This is simulation of a coil of wire falling towards a permanent magnet.  It illustrates Faraday's Law of Induction, 
// as a current is induced in the wire as it moves relative to the magnet.  The resistance of the wire, and the dipole 
// moment of the magnet are adjustable via sliders, and there are several field visualization options available.  
// Additionally, a real-time graph is displayed, plotting current in the wire versus time.
public class FallingCoilCylindricalMagnet extends SimEM {

	private static final long serialVersionUID = 3258131375163717170L;
	boolean useModels = true;
	double deltaTime = 0.01;
	double metricScale = 25.0 / 39.37;
	Vector3d ringPos = new Vector3d(0., 3., 0.);;
	Graph current_graph;
	CurrentPlot current_plot;
	RingOfCurrent roc;
	LineMagneticDipole mag;
	Rendered modelBase;
	Rendered modelMag;
	Rendered modelMagBase;
	double maxDist = 0.1;
	double ringRad = 1.25;
	VisualizationControl visControl;
	FieldDirectionGrid fv;
	FieldLineManager fmanager;
	FieldConvolution mDLIC;

	public FallingCoilCylindricalMagnet() {
		super();
		
		// INITIALIZATION OF OBJECTS AND PARAMETERS //
		
		title = "Falling Ring of Current";
		setID(title);
		

		///// Set properties on the SimEngine /////
		// Bounding area represents the characteristic size (physical extent) of the simulation space.
		setBoundingArea(new BoundingSphere(new Point3d(), 6));
		// setDeltaTime() sets the time step of the simulation.
		setDeltaTime(deltaTime);
		// setDamping() sets the generalized, velocity-based damping on the system.
		setDamping(0.);
		


		

		///// Initialization of the MagneticDipole /////
		// MagneticDipole constructor.
		mag = new LineMagneticDipole();
		// setMu() sets the strength of the MagneticDipole.
		mag.setMu(1.0);
		// setPosition() sets the position of the MagneticDipole.
		mag.setPosition(new Vector3d(0., 0., 0.));
		// setDirection() sets the direction (orientation) of the MagneticDipole.
		mag.setDirection(new Vector3d(0, -1., 0));
		// setPickable() determines whether or not this object will be pickable in the Viewer with the mouse.
		// Setting an object's pickable property to TRUE is a prerequisite for being able to interact with it
		// using the mouse.
		mag.setPickable(false);
		// setRotable() determines whether or not this object is free to rotate.  In this case, we want the MagneticDipole
		// to be aligned with the y-axis.
		mag.setRotable(false);
		// setMoveable() determines whether or not this object is free to move (translate).  In this case, we want
		// the MagneticDipole fixed at the origin.
		mag.setMoveable(false);
		// setLength() sets the length of the MagneticDipole.  For a MagneticDipole, length has no physical 
		// significance, and is merely a property of it's rendered model (for an LineMagneticDipole, on the 
		// other hand, setLength() does have physical significance).
		mag.setLength(0.75);
		// The following two properties are associated with computations required to calculate the induced current
		// in a RingOfCurrent from this MagneticDipole.
		mag.setAvoidSingularity(true);
		mag.setAvoidSingularityScale(1.);

		///// Initialization of the RingOfCurrent /////
		// RingOfCurrent constructor.
		roc = new RingOfCurrent();
		// setPosition() sets the position of the RingOfCurrent.
		roc.setPosition(ringPos);
		// setDirection() sets the direction (orientation) of the RingOfCurrent.
		roc.setDirection(new Vector3d(0., 1., 0.));
		// setPickable() determines whether or not this object will be pickable in the Viewer with the mouse.
		// Setting an object's pickable property to TRUE is a prerequisite for being able to interact with it
		// using the mouse. 
		roc.setPickable(true);
		// setRotable() determines whether or not this object is free to rotate.  In this case, we want the RingOfCurrent
		// to be aligned with the y-axis.
		roc.setRotable(false);
		// setMoveable() determines whether or not this object is free to move (translate).  In this case, we want the
		// RingOfCurrent to fall towards the MagneticDipole.
		roc.setMoveable(true);
		// setRadius() sets the radius of the RingOfCurrent.
		roc.setRadius(ringRad);
		// setInductance() sets the inductance of the RingOfCurrent.
		roc.setInductance(1.);
		// setMass() sets the mass of the RingOfCurrent.
		roc.setMass(0.01);
		// setInducing() determines whether current can be induced in this RingOfCurrent.  Calculating induced current is
		// a relatively expensive process, and if you are not concerned with it, this property should be set to false.
		// In this case, we are concerned with induction, so we set it to true.
		roc.setInducing(true);
		// setIntegrationMode() determines the integration mode used in calculating the current induced in this 
		// RingOfCurrent.  There are two modes:  a simplified axial solution which can be used when the the flux through
		// the ring will be symmetric about it's axis, and an off-axis solution when symmetry is not present.  In this
		// case we are dealing with axially symmetric geometry, so we use the AXIAL solution.
		roc.setIntegrationMode(RingOfCurrent.AXIAL);
		

		///// Initializing Imported Models /////
		// The following block loads external models built in 3ds max and/or extra geometry not associated with any 
		// simulation objects.
		/////
		if (useModels) {

			// Generate a Rendered object to hold the first model, load the model, and transform it appropriately.
			// This will be the wooden base holding the magnet.
			modelBase = new Rendered();
			TNode3D node = new Loader3DS().getTNode3D(URLGenerator
					.getResource("models/Main_Base_at_Zero.3DS"));
			node.setScale(metricScale);
			modelBase.setNode3D(node);
			modelBase.setPosition(new Vector3d(0., -2.45, 0.));
			addElement(modelBase);

			// Here we load an external model and set it to be the model used by the RingOfCurrent.
			// This is a model of a coil of wire that replaces the generic "torus" that is used as the model by default.
			TNode3D node1 = new Loader3DS().getTNode3D(URLGenerator
					.getResource("models/Coil_at_Zero.3DS"));
			node1.setScale(metricScale);
			roc.setNode3D(node1);

			// Here we create a Rendered object to hold a cylinder that we generate internally.
			// This will be the wooden cylinder that the magnet rests on.
			modelMagBase = new Rendered();
			TShapeNode cylN = (TShapeNode) new ShapeNode();
			cylN.setGeometry(Cylinder.makeGeometry(16, 0.3, 2.0));
			modelMagBase.setNode3D(cylN);
			modelMagBase.setColor(new Color(160, 140, 110));
			modelMagBase.setPosition(new Vector3d(0, -1.30, 0));
			addElement(modelMagBase);

			// Here we load an external model and set it to be the model used by the MagneticDipole.
			// This is a model of a magnet (a silver chamfered cylinder) that replaces the generic cylinder used by default.
			TNode3D node3 = new Loader3DS().getTNode3D(URLGenerator
					.getResource("models/Magnet_At_Zero.3DS"));
			node3.setScale(metricScale);
			mag.setNode3D(node3);
			mag.setModelOffsetPosition(new Vector3d(0.,-0.5,0.));
			
		}

		// Here we create a wall overlapping the rectangular base that the MagneticDipole sits on.  As the RingOfCurrent
		// falls over the MagneticDipole, it will stop when it collides with this wall.
		// Wall constructor.
		Wall wall = new Wall(new Vector3d(0., -2.320, 0.), new Vector3d(2., 0.,
				0.), new Vector3d(0., 0., 2.));
		// setElasticity() sets the elasticity of the wall.  This affects the amount of energy lost by an object bouncing 
		// off of this wall.  In this case, we don't want the RingOfCurrent to bounce at all, so we give the wall zero
		// elasticity.
		wall.setElasticity(0.);
		// setDrawn() determines whether the Viewer should actually render this object (the object will still be part of
		// the simulation even if it isn't drawn).  In this case, we do not want to draw this wall, since it is already
		// being represented visually by the model we imported to represent the wooden base.
		wall.setDrawn(false);
		
		// Here we add a collisionController to the RingOfCurrent so that it will be registered as a colliding object, and
		// react appropriately when it touches the Wall.  We represent the ring as a sphere for the purposes of collision
		// detection, since we're only concerned with stopping the ring from "falling through the floor", and it is not 
		// free to rotate.  To resolve RingOfCurrent collisions correctly, we would have to write a TorusCollisionController.
		// SphereCollisionController constructor.
		SphereCollisionController sccx = new SphereCollisionController(roc);
		// setRadius() sets the radius of the spherical collision armiture.  In this case we set it to be cross-sectional
		// radius of the RingOfCurrent.
		sccx.setRadius(roc.getThickness());
		// setTolerance() sets the tolerance of the collisionController, which determines how close the objects much be to
		// one another to register a collision.  For this simple case, a large tolerance is acceptable.
		sccx.setTolerance(0.01);
		// setColliding() determines whether this object should be tested for collisions.
		roc.setColliding(true);
		// setCollisionController() applies the supplied collisionController to the simulation object.
		roc.setCollisionController(sccx);
		
		// IMPORTANT:  Here we add the simulation objects to the SimEngine.
		addElement(wall);
		addElement(mag);
		addElement(roc);

		///// END INITIALIZATION OF SIMULATION OBJECTS AND PARAMETERS /////
		
		
		// INITIALIZATION OF FIELD VISUALIZATION ELEMENTS //
		
		// In this section we create and initialize field visualization elements such as fieldlines, vector field grids, and DLIC
		// generators.  These elements will be added to the simulation by way of the GUI, which also creates controls for
		// interacting with them.
		
		// Here we create a FieldDirectionGrid, which is a vector field grid represented in the Viewer as a grid of arrows
		// that point in the direction of their local field.
		// FieldDirectionGrid constructor.
		fv = new FieldDirectionGrid();
		// setType() sets the type of field (electric, magnetic, etc.) that this vector field grid should measure.
		fv.setType(Field.B_FIELD);

		// Here we add fieldlines to the simulation, which are often associated with specific simulation objects.
		// First, we create a FieldLineManager, which manages groups of FieldLines.
		// FieldLineManager constructor.
		fmanager = new FieldLineManager();
		// setSymmetryCount() determines the symmetry of the fieldlines.  If the field in question is symmetric about a
		// given axis, we can save an enormous amount of calculation time by calculating ONE FieldLine, and then transforming
		// it several times around the axis of symmetry for a three dimensional representation.  In this case, we set the
		// default symmetry count to 40.  This means that each FieldLine will be transformed and rotated 40 times around
		// the axis of symmetry (the default axis of symmetry is the y-axis).
        fmanager.setSymmetryCount(40);
		fmanager.setID("fieldLineManager test name");
		// setElementManager() this associates the FieldLineManager with this simulation.  This is necessary for the 
		// FieldLineManager to be able to add it it's FieldLines to the simulation.
		fmanager.setElementManager(this);
		// Here we create two FluxFieldLines and add them to the manager.  See FluxFieldLine/FieldLine documentation for
		// details on how FluxFieldLines work.
		fmanager.addFieldLine(new FluxFieldLine(-10.0, roc, true, true));
		fmanager.addFieldLine(new FluxFieldLine(-30.0, roc, true, true));

		// Here we create several more FluxFieldLines and add them to the manager.  The first one below represents a 
		// FieldLine with a very small arclength, so we need to make some slight adjustments to it's default parameters.
		// FluxFieldLine constructor
		FluxFieldLine fl = new FluxFieldLine(15.0, mag, true, false);
		// setSArc() sets the individual step size of the FieldLine.  In this case, since we know the arclength will be small,
		// we want a smaller than usual step size.
		fl.setSArc(0.1);
		// setKMax() sets the maximum number of steps taken along the FieldLine.  Since we have used a step size that is 
		// roughly one half the default, we increase the number of steps slightly to compensate, and to make sure the line
		// is long enough at all times during the simulation.
		fl.setKMax(400);
		// setMinDistance() see FieldLine for details.  This sets the minimum distance from it's starting point that the 
		// FieldLine will terminate.  This is primarily used to terminate FieldLines "closed" FieldLines that loop back 
		// around on to themselves.
		fl.setMinDistance(maxDist);
		fmanager.addFieldLine(fl);
		fmanager.addFieldLine(new FluxFieldLine(25.0, mag, true, false));
		fmanager.addFieldLine(new FluxFieldLine(100.0, mag, true, false));
		
		// Here we set a few more properties of the FieldLines by way of the FieldLineManager.
		// setIntegrationMode() sets the integration mode used to calculate the FieldLines.  Options are currently EULER or
		// RUNGE_KUTTA.
		fmanager.setIntegrationMode(FieldLine.RUNGE_KUTTA);
		// setColorMode() sets method by which the FieldLines are colored.  Currently there are effectively two modes, one
		// which colors the FieldLine by vertex, and one that gives them a flat color at all points.  COLOR_VERTEX is the
		// by-vertex method, where the color at each vertex is determined by the magnitude of the field at that point.
		fmanager.setColorMode(FieldLine.COLOR_VERTEX);
		// setColorScale() determines the rate of interpolation of colors in the COLOR_VERTEX coloring mode.
		fmanager.setColorScale(0.01);
		
		
		// Here we add a FieldConvolution generator to the simulation.  A FieldConvolution generates high-resolution 
		// images of a two-dimensional slice of the field.  Below we create the generator and specify the size of the slice.
        RectangularPlane rec = new RectangularPlane(new Vector3d(-6., -6., 0.),
				new Vector3d(-6., 6., 0.), new Vector3d(6., 6., 0.));
		mDLIC = new FieldConvolution();
		mDLIC.setSize(new Dimension(1024, 1024));
		mDLIC.setVisible(false);
		mDLIC.setComputePlane(rec);
		
		
		// GUI SETUP & INITIALIZATION //
		
		// At this point we should add the GUI elements necessary to control our simulation.  These can be buttons, 
		// sliders, checkboxes, etc..  Such GUI elements are first created and initialized, and then added to Groups
		// that function as "sub-windows" on the GUI panel.
		
		// Here we add a slider (of type double) to control the resistance of the RingOfCurrent.
		// PropertyDouble constructor.
		PropertyDouble sliderroc = new PropertyDouble();
		// setText() sets the text label for this slider.
		sliderroc.setText("Ring Resistance");
		// setMinimum() sets the minimum value of this slider.
		sliderroc.setMinimum(0.);
		// setMaximum() sets the maximum value of this slider.
		sliderroc.setMaximum(10.);
		// setPaintTicks() determines whether tick marks should be drawn on the slider.
		sliderroc.setPaintTicks(true);
		// addRoute() sets the simulation object and specific value that this slider should modify.  In this case, we
		// want to modify the "resistance" parameter of our RingOfCurrent.
		sliderroc.addRoute(roc, "resistance");
		// setValue() sets the current value of the slider.  This is used to supply the initial setting of the slider.
		sliderroc.setValue(0.);
		// setVisible() determines whether or not this slider is visible.  
		sliderroc.setVisible(true);

		// Here we add a slider (of type double) to control the magnetic moment of the MagneticDipole.
		PropertyDouble slidermag = new PropertyDouble();
		// setText() sets the text label for this slider.
		slidermag.setText("Magnet Moment");
		// setMinimum() sets the minimum value of this slider.
		slidermag.setMinimum(0.);
		// setMaximum() sets the maximum value of this slider.
		slidermag.setMaximum(4.0);
		// setPaintTicks() determines whether tick marks should be drawn on the slider.
		slidermag.setPaintTicks(true);
		// addRoute() sets the simulation object and specific value that this slider should modify.  In this case, we
		// want to modify the "mu" parameter of our MagneticDipole ("mu" represents the magntic moment of the magnet).
		slidermag.addRoute(mag, "mu");
		// setValue() sets the current value of the slider.  This is used to supply the initial setting of the slider.
		slidermag.setValue(3.);
		// setVisible() determines whether or not this slider is visible.
		slidermag.setVisible(true);

		// Here we create a ControlGroup that will contain the sliders we created to control specific parameters of the
		// simulation.
		// ControlGroup constructor.
		ControlGroup controls = new ControlGroup();
		// setText() sets the text label of this Group.
		controls.setText("Parameters");
		// add() adds a created GUI element (slider, etc.) to this Group.
		controls.add(sliderroc);
		controls.add(slidermag);
		// addElement() adds the Group to the application.
		addElement(controls);

		
		// Here we create a VisualizationControl Group.  This Group automatically creates controls for manipulating the
		// visualization elements we created above.
		// VisualizationControl constructor.
		visControl = new VisualizationControl();
		// setFieldLineManager() sets the FieldLineManager associated with this Group.  This is the manger we created above.
		visControl.setFieldLineManager(fmanager);
		// setFieldVisGrid sets the FieldDirectionGrid associated with this Group.  This is the grid we created above.
		visControl.setFieldVisGrid(fv);
		// setFieldConvolution() sets the FieldConvolution associated with this Group.  This is the convolution we created above.
		visControl.setFieldConvolution(mDLIC);
		// setConvolutionModes() sets the types of convolutions we want to access from this generator.  In this case we 
		// want to be able to generate magnetic field images (DLIC_FLAG_B) and magnetic potential images (DLIC_FLAG_BP).
		visControl.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
		// addElement() adds the VisualizationControl Group to the application.
		addElement(visControl);
		
		// Here we create a graph based on simulation data, and add it to the GUI.  This involves creating a graph, 
		// adding a "plot" to it (which defines the quantities being plotted), and adding it to the GUI in it's own 
		// Group.
		// Graph constructor.
		current_graph = new Graph();
		// setSize() sets the size of the graph, in pixels.
		current_graph.setSize(100, 125);
		// setXRange() sets the x-axis range of the graph.
		current_graph.setXRange(0., 6.);
		// setYRange() sets the y-axis range of the graph.
		current_graph.setYRange(-0.4, 0.4);
		// setWrap() determines whether the graph should wrap around to the left side once the plot exceeds the width of
		// the graph.
		current_graph.setWrap(false);
		// setClearOnWrap() determines whether the graph should clear itself before wrapping.  If this is set to false,
		// new data will be plotted on top of old data.
		current_graph.setClearOnWrap(false);
		// setXLabel() sets the text label of the x-axis.
		current_graph.setXLabel("Time");
		// setYLabel() sets the text label of the y-axis.
		current_graph.setYLabel("Current");
		// Here we create the PlotItem being drawn by this graph.  This defines the properties being plotted. In this case
		// we want to plot current in the RingOfCurrent versus time, so we use a CurrentPlot written for this purpose.
		// CurrentPlot constructor.
		current_plot = new CurrentPlot();
		// setRing() --The CurrentPlot assumes the current being plotted is from a RingOfCurrent.  This sets the 
		// RingOfCurrent from which to retrieve current data.
		current_plot.setRing(roc);
		// setTimeAutoscale() determines whether the graph should dynamically rescale the independent axis (time) to fit
		// more data.  This is the alternative to enabling "wrapping".
		current_plot.setTimeAutoscale(true);
		// setCurrentAutoscale() determines whether the graph should dynamically rescale the dependent axis (current) to 
		// fit more data.
		current_plot.setCurrentAutoscale(true);
		// addPlotItem() adds the supplied PlotItem to the graph.
		current_graph.addPlotItem(current_plot);
		
		// Here we create a new Group for the graph, and add the graph to that Group.
		ControlGroup graphPanel = new ControlGroup();
		graphPanel.setText("Graphs");
		graphPanel.addElement(current_graph);
		addElement(graphPanel);
		
		// Here we set some parameters on the Viewer.
		// setFogEnabled() determines whether fog should be enabled in the Viewer.  Setting this here, on the Viewer, 
		// overrides any settings on the specific objects above.
		theScene.setFogEnabled(true);
		// setFogTransformFrontScale() sets distance in FRONT of the camera target at which to start fog interpolation.
		theScene.setFogTransformFrontScale(0.0);
		// setFogTransformBackScale() sets the distance BEHIND the camera target at which to end fog interpolation.  
		// Positions beyond this point are "fully fogged".
		theScene.setFogTransformBackScale(0.35);
		// addActions() adds some GUI elements as described in the addActions() method below.
		addActions();
		// mSEC.init() initializes the Simulation Model Controls.  These are the controls for the simulation itself 
		// (ie. play, pause, stop, rewind, etc.)
		//mSEC.init();
		// resetCamera() resets the transform of the camera to the transfrom described by the resetCamera() method below.
		resetCamera();
		// reset() resets simulation parameters to the values described in the reset() method below.
		reset();
		
		// initFogTransform() must be called after setting the fog parameters, if they are different than the the defaults.
		//mViewer.initFogTransform();
	}


	// In this method we add items ("Actions") to the menus in the top menu bar.
	public void addActions() {
		// Here we create an action and add it to the "Help" menu.  Selecting an action generates a system event 
		// (actionEvent) that is caught by the application and handled according to the procedures defined in the 
		// actionPerformed() method below.
		TealAction ta = new TealAction("Falling Coil", this);
		addAction("Help", ta);
	}
	// This method is an event handler called whenever an actionEvent is received (assuming an event listener has been
	// added, which is done a TealAction is created).  This method should contain the code you want to run when each
	// ActionEvent is received.
	public void actionPerformed(ActionEvent e) {
		// If the ActionEvent received is the one corresponding to our Help menu item added above, launch the help file. 
		if (e.getActionCommand().compareToIgnoreCase("Falling Coil") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework) mFramework).openBrowser("help/fallingcoil.html");
        	}
		} else {
			super.actionPerformed(e);
		}
	}
	
	// This method is called whenever the "reset" button of the simulation is pressed.  This should reset all the 
	// simulation objects and parameters to their initial state.
	public void reset() {
		super.reset();
		roc.setPosition(ringPos);
		roc.setVelocity(new Vector3d());
		roc.setDirection(new Vector3d(0., 1., 0.));
		roc.setCurrent(0.0);

		mag.setPosition(new Vector3d());
		mag.setDirection(new Vector3d(0, 1, 0));

		roc.reset();

		current_graph.clear(0);
		current_graph.setXRange(0., 6.);
		current_graph.setYRange(-0.4, 0.4);
		current_plot.reset();
		if(theEngine != null)
			theEngine.requestRefresh();
	}
	
	// This method is called whenever the "Reset Camera" action is triggered.  This should reset the camera transform
	// to it's initial state.
	public void resetCamera() {
		//Point3d from = new Point3d(0., 0., 14.);
		Point3d from = new Point3d(0., 0., 18.);
		Point3d to = new Point3d(0., 0., 0.);
		Vector3d up = new Vector3d(0., 1., 0.);
		from.scale(0.05);
		to.scale(0.05);
		setLookAt(from, to, up);
	}

}
