/* $Id: GaussLawMagDipoleDisk.java,v 1.6 2010/08/10 18:12:34 stefan Exp $ */
/**
 * A 
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.6 $
 */

package tealsim.physics.em;
import teal.util.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.plot.FluxThroughDiskDueToDipolePlot;
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
import teal.physics.em.MagneticDipole;
import teal.physics.em.RingOfCurrent;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FieldVector;
import teal.sim.spatial.FluxFieldLine;
import teal.sim.spatial.GeneralVector;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.util.URLGenerator;
import teal.visualization.dlic.DLIC;

// This is simulation of a coil of wire falling towards a permanent magnet.  It illustrates Faraday's Law of Induction, 
// as a current is induced in the wire as it moves relative to the magnet.  The resistance of the wire, and the dipole 
// moment of the magnet are adjustable via sliders, and there are several field visualization options available.  
// Additionally, a real-time graph is displayed, plotting current in the wire versus time.
public class GaussLawMagDipoleDisk extends SimEM {

	private static final long serialVersionUID = 3258131375163717170L;
	boolean useModels = true;
	double deltaTime = 0.01;
	double metricScale = 25.0 / 39.37;
	double arrowScaleInitial = 1.;
	double arrowScale = arrowScaleInitial;
	double radiusDiskInitial = 2.;
	double radiusDisk = radiusDiskInitial;
	double heightDisk = 0.01;

    /** The magnetic field vectors on the top of the disk. */
    FieldVector[][] theFieldDiskTop;
    /** The normal vectors on the top of the cylinder. */
    GeneralVector[][] theNormalDiskTop;
    /** The number of radial nodes for the magnetic field vectors on the top of the disk. */
    int numRadDisk = 1;
    /** The number of azimuthal nodes for the magnetic field vectors on the top of the disk.  */
    int numAziTopDisk = 8;
  
	Graph flux_graph;
	FluxThroughDiskDueToDipolePlot flux_plot;
	MagneticDipole mag;
	Rendered modelBase;
	Rendered modelMag;
	Rendered modelMagBase;

	Rendered Disk= new Rendered();
    /** A ShapeNode for the Disk.  */
    ShapeNode ShapeNodeDisk = new ShapeNode();
	double maxDist = 0.1;
	double ringRad = 1.25;
	VisualizationControl visControl;
	FieldDirectionGrid fv;
	FieldLineManager fmanager;
	FieldConvolution mDLIC;
    /** Slider for the y-position of the disk.  */
    PropertyDouble posSlider_y = new PropertyDouble();
    /** Slider for the x-position of the disk.  */
    PropertyDouble posSlider_x = new PropertyDouble();
    /** Slider for the rotation angle of the disk.  */
    PropertyDouble angDisk = new PropertyDouble();
    /** Slider for the radius of the disk.  */
    PropertyDouble radDisk = new PropertyDouble();
    /** Vector for the position of the disk. */
    Vector3d posDisk = new Vector3d();
    /** The angle from the x axis in the xy plane of the gaussian disk. */
    double angleDisk = 0.;

	public GaussLawMagDipoleDisk() {
		super();
		
		// INITIALIZATION OF OBJECTS AND PARAMETERS //
		
		title = "Gauss's Law For A Magnetic Dipole and a Disk";
		setID(title);
		
		// change some features of the lighting, background color, etc., from the default values, if desired
        setBackgroundColor(new Color(180,180,180));
		
		///// Set properties on the SimEngine /////
		// Bounding area represents the characteristic size (physical extent) of the simulation space.
		theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 6));
		// setDeltaTime() sets the time step of the simulation.
		theEngine.setDeltaTime(deltaTime);
		// setDamping() sets the generalized, velocity-based damping on the system.
		theEngine.setDamping(0.);
	//    theEngine.setGravity(new Vector3d(0.,0.,0.));
		
	 // create the Disk using teal.render.geometry and add to the scene
        posDisk = new Vector3d(0.,0.,0);
        ShapeNodeDisk.setGeometry(Cylinder.makeGeometry(32, radiusDisk, heightDisk));
        ShapeNodeDisk.setTransparency(0.5f);
        Disk.setNode3D(ShapeNodeDisk);
        Disk.setColor(new Color(170, 170, 0));
        Disk.setPosition(posDisk);
        Disk.setDirection(new Vector3d(0.,1.,0.));
        Disk.setDrawn(true);
        addElement(Disk);
        
		///// Initialization of the MagneticDipole /////
		// MagneticDipole constructor.
		mag = new MagneticDipole();
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
	//	mag.setAvoidSingularity(true);
	//	mag.setAvoidSingularityScale(1.);
		mag.setIntegrating(false);

	//  create the magnetic field and normal vectors on the top of the disk  
        theFieldDiskTop = new FieldVector[numAziTopDisk][numRadDisk];
        theNormalDiskTop = new GeneralVector[numAziTopDisk][numRadDisk];

        for (int j = 0; j < numRadDisk; j++) {
        	for (int i = 0; i < numAziTopDisk; i++) {
	     		theFieldDiskTop[i][j] = new FieldVector();
	     		theFieldDiskTop[i][j].setFieldType(1);
	     		theFieldDiskTop[i][j].setPosition(new Vector3d(0,0,0));
	     		theFieldDiskTop[i][j].setColor(Teal.DefaultBFieldColor);
	     		theFieldDiskTop[i][j].setArrowScale(arrowScale);
	     		theFieldDiskTop[i][j].setDrawn(true);
	     		addElement(theFieldDiskTop[i][j]);
	     
	     		theNormalDiskTop[i][j] = new GeneralVector();
	     		theNormalDiskTop[i][j].setPosition(new Vector3d(0,0,0));
	     		theNormalDiskTop[i][j].setColor(Color.gray);
	     		theNormalDiskTop[i][j].setArrowScale(arrowScale);
	     		theNormalDiskTop[i][j].setDrawn(true);
	     		addElement(theNormalDiskTop[i][j]);
        	} 	
        }

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



			// Here we create a Rendered object to hold a disk that we generate internally.
			// This will be the wooden disk that the magnet rests on.
			modelMagBase = new Rendered();
			TShapeNode cylN = (TShapeNode) new ShapeNode();
			cylN.setGeometry(Cylinder.makeGeometry(16, 0.3, 2.0));
			modelMagBase.setNode3D(cylN);
			modelMagBase.setColor(new Color(160, 140, 110));
			modelMagBase.setPosition(new Vector3d(0, -1.30, 0));
			addElement(modelMagBase);

			// Here we load an external model and set it to be the model used by the MagneticDipole.
			// This is a model of a magnet (a silver chamfered disk) that replaces the generic disk used by default.
			TNode3D node3 = new Loader3DS().getTNode3D(URLGenerator
					.getResource("models/Magnet_At_Zero.3DS"));
			node3.setScale(metricScale);
			mag.setNode3D(node3);
			mag.setModelOffsetPosition(new Vector3d(0.,-0.5,0.));
			
		}


		addElement(mag);


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
		

		
		// GUI SETUP & INITIALIZATION //
		
		// At this point we should add the GUI elements necessary to control our simulation.  These can be buttons, 
		// sliders, checkboxes, etc..  Such GUI elements are first created and initialized, and then added to Groups
		// that function as "sub-windows" on the GUI panel.
		


		// Here we create a ControlGroup that will contain the sliders we created to control specific parameters of the
		// simulation.
		// ControlGroup constructor.
		ControlGroup controls = new ControlGroup();
		// setText() sets the text label of this Group.
		controls.setText("Parameters");
		// add() adds a created GUI element (slider, etc.) to this Group.

		// addElement() adds the Group to the application.
		addElement(controls);

		
		// Here we create a VisualizationControl Group.  This Group automatically creates controls for manipulating the
		// visualization elements we created above.
		// VisualizationControl constructor.
		visControl = new VisualizationControl();
		// setFieldLineManager() sets the FieldLineManager associated with this Group.  This is the manger we created above.
		visControl.setFieldLineManager(fmanager);
		// setFieldVisGrid sets the FieldDirectionGrid associated with this Group.  This is the grid we created above.
	//	visControl.setFieldVisGrid(fv);
		// setFieldConvolution() sets the FieldConvolution associated with this Group.  This is the convolution we created above.
	//	visControl.setFieldConvolution(mDLIC);
		// setConvolutionModes() sets the types of convolutions we want to access from this generator.  In this case we 
		// want to be able to generate magnetic field images (DLIC_FLAG_B) and magnetic potential images (DLIC_FLAG_BP).
//		visControl.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
		// addElement() adds the VisualizationControl Group to the application.
		addElement(visControl);
		
		// Here we create a graph based on simulation data, and add it to the GUI.  This involves creating a graph, 
		// adding a "plot" to it (which defines the quantities being plotted), and adding it to the GUI in it's own 
		// Group.
		// Graph constructor.
		flux_graph = new Graph();
		// setSize() sets the size of the graph, in pixels.
		flux_graph.setSize(150, 250);
		// setXRange() sets the x-axis range of the graph.
		flux_graph.setXRange(0., 2.);
		// setYRange() sets the y-axis range of the graph.
		flux_graph.setYRange(-3., 3.);
		// setWrap() determines whether the graph should wrap around to the left side once the plot exceeds the width of
		// the graph.
		flux_graph.setWrap(true);
		// setClearOnWrap() determines whether the graph should clear itself before wrapping.  If this is set to false,
		// new data will be plotted on top of old data.
		flux_graph.setClearOnWrap(true);
		// setXLabel() sets the text label of the x-axis.
		flux_graph.setXLabel("Time");
		// setYLabel() sets the text label of the y-axis.
		flux_graph.setYLabel("Flux");
		// Here we create the PlotItem being drawn by this graph.  This defines the properties being plotted. In this case
		// we want to plot flux through disk versus time, so we use a FluxThroughDiskDueToDipolePlot written for this purpose.
		// FluxThroughDiskDueToDipolePlot constructor.
		flux_plot = new FluxThroughDiskDueToDipolePlot();
		// setRing() --The FluxThroughDiskDueToDipolePlot assumes the flux being plotted is due to the dipole.  This sets the 
		// dipole from which to retrieve flux data.
        flux_plot.setMagneticDipole(mag);
        flux_plot.setShapeNode(ShapeNodeDisk);
		// setTimeAutoscale() determines whether the graph should dynamically rescale the independent axis (time) to fit
		// more data.  This is the alternative to enabling "wrapping".
        flux_plot.setRadiusDisk(radiusDisk);
		flux_plot.setTimeAutoscale(false);
		// setCurrentAutoscale() determines whether the graph should dynamically rescale the dependent axis (current) to 
		// fit more data.
		flux_plot.setFluxAutoscale(false);
		// addPlotItem() adds the supplied PlotItem to the graph.
		flux_graph.addPlotItem(flux_plot);
		
		// Here we create a new Group for the graph, and add the graph to that Group.
		ControlGroup graphPanel = new ControlGroup();
		graphPanel.setText("Graphs");
		graphPanel.addElement(flux_graph);
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
		mSEC.init();
		// resetCamera() resets the transform of the camera to the transfrom described by the resetCamera() method below.
		resetCamera();
		// reset() resets simulation parameters to the values described in the reset() method below.
		reset();
		
		// initFogTransform() must be called after setting the fog parameters, if they are different than the the defaults.
		//mViewer.initFogTransform();
		
		 // create the two sliders for the disk position    
        posSlider_x.setText("X Position");
        posSlider_x.setMinimum(-5.);
        posSlider_x.setMaximum(5.0);
        posSlider_x.setPaintTicks(true);
        posSlider_x.addPropertyChangeListener("value", this);
        posSlider_x.setValue(0.);
        posSlider_x.setVisible(true);
        
        posSlider_y.setText("Y Position ");
        posSlider_y.setMinimum(-5.);
        posSlider_y.setMaximum(5.0);
        posSlider_y.setPaintTicks(true);
        posSlider_y.addPropertyChangeListener("value", this);
        posSlider_y.setValue(3.);
        posSlider_y.setVisible(true);
        
// create the angle orientation slider for the disk, where angle is the angle from the x axis    
        angDisk.setText("Rotation Angle");
        angDisk.setMinimum(-180.);
        angDisk.setMaximum(180.0);
        angDisk.setPaintTicks(true);
        angDisk.addPropertyChangeListener("value", this);
        angDisk.setValue(90.);
        angDisk.setVisible(true);
        
        
 // create the radius slider for the disk  
         radDisk.setText("Radius of Disk");
         radDisk.setMinimum(1.);
         radDisk.setMaximum(6.);
         radDisk.setPaintTicks(true);
         radDisk.addPropertyChangeListener("value", this);
         radDisk.setValue(1.);
         radDisk.setVisible(true);
            
       
 // add the sliders to the control group and add the control group to the scene
        ControlGroup controls1 = new ControlGroup();
        controls1.setText("Disk Position Radius & Orientation");
        controls1.add(posSlider_y);
        controls1.add(posSlider_x);
        controls1.add(angDisk);
        controls1.add(radDisk);
        addElement(controls1);
        
        PlaceBNVectors();
        
     // set initial state


        theEngine.requestRefresh();
        mSEC.setVisible(true);
        reset();
        resetCamera();

        
	}  // end of GaussLawMagDipoleDisk

	   /** Method to place the magnetic field vectors and normals on the disk. */
	public void PlaceBNVectors() {
	// first place the vectors on the top of the cylinder
		Vector3d normalTop = null;
		Vector3d centerTop = new Vector3d(0,0,0);
        double compx = Math.cos(angleDisk*Math.PI/180.);
        double compy = Math.sin(angleDisk*Math.PI/180.);
        normalTop = new Vector3d(compx, compy,0.);
        normalTop.scale(heightDisk/2.);
		centerTop.add(normalTop);
		centerTop.add(posDisk);

        for (int j = 0; j < numRadDisk; j++) {
        	double rad = (j+1)*radiusDisk/(numRadDisk+1);
        	for (int i = 0; i < numAziTopDisk; i++) {
        		double aziangle = i*2.*Math.PI/(numAziTopDisk*1.);
        		Vector3d azipos = new Vector3d(0.,Math.cos(aziangle),Math.sin(aziangle));
        		Vector3d aziposTrans = new Vector3d(0,0,0);
        		Vector3d azidirTrans = new Vector3d(1,0,0);
        		Vector3d azidir = new Vector3d(1.,0.,0.);
        		azipos.scale(rad);
        		aziposTrans.x = azipos.x*compx - azipos.y*compy;
        		aziposTrans.y = azipos.x*compy + azipos.y*compx;
        		aziposTrans.z = azipos.z;
        		azidirTrans.x = azidir.x*compx - azidir.y*compy;
        		azidirTrans.y = azidir.x*compy + azidir.y*compx;
        		azidirTrans.z = azidir.z;
        		aziposTrans.add(centerTop);
	     		theFieldDiskTop[i][j].setPosition(aziposTrans);
	     		theFieldDiskTop[i][j].setScale(arrowScale);
	     		theNormalDiskTop[i][j].setPosition(aziposTrans);
	     		theNormalDiskTop[i][j].setValue(azidirTrans);
	     		theNormalDiskTop[i][j].setDrawn(true);
	     		theNormalDiskTop[i][j].setScale(arrowScale);
	     		// here we make the field vector tip be at the location of the arrow if the arrow points inward at the local normal
	
	        if (theEngine != null) theEngine.requestSpatial(); 
        	} 	
        }
	}

	// In this method we add items ("Actions") to the menus in the top menu bar.
	public void addActions() {
		// Here we create an action and add it to the "Help" menu.  Selecting an action generates a system event 
		// (actionEvent) that is caught by the application and handled according to the procedures defined in the 
		// actionPerformed() method below.
		TealAction ta = new TealAction("Gauss's Law for a Dipole and Disk", this);
		addAction("Help", ta);
        TealAction tb = new TealAction("Execution & View", this);
        addAction("Help", tb);
	}
	// This method is an event handler called whenever an actionEvent is received (assuming an event listener has been
	// added, which is done a TealAction is created).  This method should contain the code you want to run when each
	// ActionEvent is received.
	public void actionPerformed(ActionEvent e) {
		// If the ActionEvent received is the one corresponding to our Help menu item added above, launch the help file. 
		if (e.getActionCommand().compareToIgnoreCase("Gauss's Law for a Dipole and Disk") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework) mFramework).openBrowser("help/gausslawmagdipoledisk.html");
        	}
		} else if (e.getActionCommand().compareToIgnoreCase("Execution & View") == 0) 
            {
            	if(mFramework instanceof TFramework) {
            		((TFramework)mFramework).openBrowser("help/executionView.html");}
		} else {
			super.actionPerformed(e);
		}
	}
	
	
	  public void propertyChange(PropertyChangeEvent pce) {
	        Object source = pce.getSource();
	        if (source == posSlider_x) {
	            double posX = ((Double) pce.getNewValue()).doubleValue();
	            posDisk.x=posX;
	            Disk.setNode3D(ShapeNodeDisk);
	            Disk.setPosition(posDisk);
	            PlaceBNVectors();
	        } else if (source == posSlider_y) {
	            double posY = ((Double) pce.getNewValue()).doubleValue();
	            posDisk.y=posY;
 	            Disk.setNode3D(ShapeNodeDisk);
	            Disk.setPosition(posDisk);
	            PlaceBNVectors();
	        } else if (source == angDisk) {
	        	angleDisk = ((Double) pce.getNewValue()).doubleValue();
	            double angDisk_rad = angleDisk*Math.PI/180.;
	            double compx = Math.cos(angDisk_rad);
	            double compy = Math.sin(angDisk_rad);
	            Disk.setNode3D(ShapeNodeDisk);
	            Disk.setDirection(new Vector3d(compx, compy, 0.));
	            flux_plot.setNormalDisk(new Vector3d(compx, compy, 0.));
	            PlaceBNVectors();
	        } else if (source == radDisk) {
	        	radiusDisk = ((Double) pce.getNewValue()).doubleValue();
	            ShapeNodeDisk.setGeometry(Cylinder.makeGeometry(32, radiusDisk, heightDisk));
	            Disk.setNode3D(ShapeNodeDisk);
	            arrowScale = arrowScaleInitial*radiusDisk/radiusDiskInitial;
	            flux_plot.setRadiusDisk(radiusDisk);
	            PlaceBNVectors();
	        } else {
	            super.propertyChange(pce);
	        }
	 
	    }
	// This method is called whenever the "reset" button of the simulation is pressed.  This should reset all the 
	// simulation objects and parameters to their initial state.
	public void reset() {
		super.reset();



		mag.setPosition(new Vector3d());
		mag.setDirection(new Vector3d(0, 1, 0));

	

		flux_graph.clear(0);
		flux_graph.setXRange(0., 2.);
		flux_graph.setYRange(-3., 3.);
		flux_plot.reset();

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
