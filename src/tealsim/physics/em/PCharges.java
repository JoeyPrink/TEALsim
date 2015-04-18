/* $Id: PCharges.java,v 1.14 2010/08/10 18:12:34 stefan Exp $ */

/**
 * A demonstration implementation of the TFramework.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.14 $
 */

package tealsim.physics.em;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import teal.render.BoundingSphere;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.core.TElement;
import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.viewer.TViewer;
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.physical.RectangularBox;
import teal.physics.em.PointCharge;
import teal.physics.physical.PhysicalObject;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.RelativeFLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.visualization.dlic.DLIC;
import tealsim.gamification.ZoneRequirement;
import tealsim.gamification.GamificationAgent;
import tealsim.gamification.Task;

public class PCharges extends SimEM {

    private static final long serialVersionUID = 3257009869025653297L;
    
    FieldDirectionGrid fv;
    FieldConvolution mDLIC;
    FieldLineManager fmanager;
    VisualizationControl visGroup;
    ControlGroup controls, gamification;
    PointCharge pc1;
    PointCharge pc2;
    
    GamificationAgent gamificationPanel;
    Task task0;

    public PCharges() {

        super();
        super.initialize();
        title = "Two Point Charges";
        setID(title);
        
        ///// INITIALIZATION OF SIMULATION AND VIEWER PARAMETERS /////
        
        // Here we set the bounding area of the simulation space.  This should be characteristic of the size of the space
        // being used.
       
        setBoundingArea(new BoundingSphere(new Point3d(), 10));
        // Here we set the generalized velocity-based damping of the simulation.        
        setDamping(0.1);
        // Here we set the time step of the simulation.
        setDeltaTime(0.1);
        setGravity(new Vector3d());
 
        
        // setNavigationMode() sets the mouse-based camera navigation modes available in this simulation.  In this case 
        // we have enabled zooming, translation, and rotation of the camera (TViewer.ORBIT merely indicates that we are
        // using functions of the OrbitBehavior class).
        setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM | TViewer.VP_TRANSLATE | TViewer.VP_ROTATE);
        // setShowGizmos() determines whether or not we want to the viewer to display transform gizmos on selected objects.
        setShowGizmos(false);
        // setVisible() on the SimulationModelControl determines whether or not the simulation controls (play, pause, etc.)
        // are visible.
        mSEC.setVisible(true);
        // addPropertyChangeListener() registers the application to receive propertyChangeEvents from an object.  In this
        // case, we want the application to be notified whenever a user "drags" something in the viewer.  This information
        // will be used below to handle user-initiated collisions between objects in the scene.
        if(theScene instanceof TElement) {
        	((TElement)theScene).addPropertyChangeListener("dragging", this);
        }
        
        ///// INITIALIZATION OF SIMULATION OBJECTS /////
        
        // Here we create a rectangular box to contain the PointCharges in the scene.  A RectangularBox is just four (or six) 
        // Walls enclosing a given area or volume.
        // RectangularBox constructor.
        RectangularBox box = new RectangularBox();
        // setPosition() sets the position of the center of the box.
        box.setPosition(new Vector3d(0., 0., 0.));
        // setOrientation() sets the orientation of the box.  The box is aligned such that "width" direction of the box 
        // points along the supplied vector.
        box.setOrientation(new Vector3d(0., 1., 0.));
        // setNormal() sets the normal direction of the box.  This is the "height" direction of the box.
        box.setNormal(new Vector3d(0., 0., 1.));
        // setLength() sets the length of the box.
        box.setLength(20.);
        // setWidth() sets the width of the box.
        box.setWidth(20.);
        // setOpen() determines whether the box is "open" or not.  An "open" box has no top or bottom Walls.
        box.setOpen(true);
        // The RectangularBox class merely handles the creation and positioning of it's component Wall objects.  Those 
        // walls must be added to the simulation by calling addElement on RectangularBox.getWalls().
        addElements(box.getWalls());

        // Here we add the first PointCharge to the simulation.
        // PointCharge constructor.
        pc1 = new PointCharge();
        // setID() gives a String ID to the PointCharge, which we can use internally to identify it.
        pc1.setID("PointCharge 1");
        // setPosition() sets the position of the PointCharge.
        pc1.setPosition(new Vector3d(0, 2, 0));
        // setRadius() sets the radius of the PointCharge.  Radius has no physical significance, and only affects how the
        // PointCharge is rendered.
        pc1.setRadius(0.6);
        // setCharge() sets the charge on the PointCharge.
        pc1.setCharge(2.5);
        // setMass() sets the mass of the PointCharge.
        pc1.setMass(1.);
        // setSelectable() determines whether this object is selectable.  Selectable objects are useful in situations where
        // you might want to perform generalized operations on only certain ("selected") objects.
        pc1.setSelectable(true);
        // setPickable() determines whether this object is pickable with the mouse (ie. generates a pickEvent?).  In 
        // principle, if selectable is set to true, pickable must be set to true as well.
        pc1.setPickable(true);
        // setColliding() determines whether this object will be checked for collisions.
        pc1.setColliding(true);

        // Here we create a collisionController for the first PointCharge.  The collisionController handles all of the 
        // work related to detecting and resolving collisions with it's object.  Since a PointCharge is represented as a 
        // sphere, we use a SphereCollisionController.
        SphereCollisionController sccx = new SphereCollisionController(pc1);
        // setRadius() on the collisionController sets the radius of the spherical armiture used by the collisionController
        // to detect collisions.  This should be set to the same radius as the PointCharge.
        sccx.setRadius(0.6);
        // setTolerance() sets the tolerance of the collisionController.  The tolerance determines the range of values that
        // are considered a "hit" during collision detection.
        sccx.setTolerance(0.1);
        // setCollisionController() applies the supplied collisionController to the object.
        pc1.setCollisionController(sccx);
        // add the PointCharge to the world.
        addElement(pc1);

        
        // Creation and initialization of the second PointCharge proceeds identically to the first.
        pc2 = new PointCharge();
        pc2.setID("PointCharge 2");
        pc2.setPosition(new Vector3d(0, -2, 0));
        pc2.setRadius(0.6);
        pc2.setCharge(-2.5);
        pc2.setMass(1.);
        pc2.setSelectable(true);
        pc2.setPickable(true);
        pc2.setColliding(true);

        sccx = new SphereCollisionController(pc2);
        sccx.setRadius(0.6);
        sccx.setTolerance(0.1);
        pc2.setCollisionController(sccx);
        addElement(pc2);

        
        ///// CREATE AND INITIALIZE FIELD VISUALIZATION ELEMENTS /////
        
        // Here we create a FieldDirectionGrid, which is a vector field representation rendered as a two dimensional grid
        // of arrows.
        fv = new FieldDirectionGrid();
        // setType() sets the type of field this FieldDirectionGrid should measure (ie. E_FIELD, B_FIELD, etc.).
        fv.setType(Field.E_FIELD);
        
        // Below we create a FieldConvolution object, which renders high-resolution images of a field in two dimensional
        // slices.
        RectangularPlane rec = new RectangularPlane(new Vector3d(-10., -10., -.1), new Vector3d(-10., 10., -.1),
        		new Vector3d(10., 10., 0.));
        //System.out.println("Rec center: " + rec.getCenter() + " scale: "+ rec.getScale());
        mDLIC = new FieldConvolution();
        mDLIC.setSize(new Dimension(512, 512));
        mDLIC.setVisible(false);
        mDLIC.setComputePlane(rec);
        
        
        // Here we create a FieldLineManager and add some FieldLines to it.
        // FieldLineManager constructor.
        fmanager = new FieldLineManager();
        // setElementManager() should pass a reference to this simulation.
        fmanager.setElementManager(this);
        // setColorMode() sets the color mode of the FieldLine.  Using this method, the options are color by vertex (true)
        // or flat color (false).
        fmanager.setColorMode(false);
        
        // Here we use a static method in RelativeFLine to create a collection of FieldLines quickly.
        
        Collection<? extends FieldLine> fls = RelativeFLine.createLines(pc1, teal.field.Field.E_FIELD, 8, false);
        fmanager.setFieldLines(fls);
        
        fls = RelativeFLine.createLines(pc2, teal.field.Field.E_FIELD, 8, false);
        fmanager.addFieldLines(fls);
        fmanager.setColorMode(false);
        
       
        ///// INITIALIZATION OF GUI ELEMENTS /////
        
        // Here we create a slider to control the charge on the first PointCharge.
        PropertyDouble slider2 = new PropertyDouble();
        // setMinimum() sets the minimum value of the slider.
        slider2.setMinimum(-5);
        // setMaximum() sets the maximum value of the slider.
        slider2.setMaximum(5);
        // setPaintTicks() determines whether or not tick marks should be drawn on the slider.
        slider2.setPaintTicks(true);
        // addRoute() sets the object and property that this slider will change.  In this case, the "value" of the 
        // slider should change the "charge" of the first PointCharge.
        slider2.addRoute("value", pc1, "charge");
        // setValue() sets the current value of the slider.  Use this to set the slider's intial value.
        slider2.setValue(2.5);
        // setText() sets the label text for this slider.
        slider2.setText("Q1 Charge");
        //addElement(slider2);

        // A second slider is created the same way as the first, except that it is assigned to affect the charge on the
        // second PointCharge.
        PropertyDouble slider3 = new PropertyDouble();
        slider3.setMinimum(-5);
        slider3.setMaximum(5);
        slider3.setPaintTicks(true);
        slider3.setBounds(35, 648, 415, 50);
        slider3.addRoute("value", pc2, "charge");
        slider3.setValue(-2.5);
        slider3.setText("Q2 Charge");
        //addElement(slider3);

        // Here we create a "Parameters" Group and add the sliders to it.  A Group is a sub-panel of the GUI that can be
        // minimized, etc..
        controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(slider2);
        controls.add(slider3);
        addElement(controls);
        
        
        // Here we create the VisualizationControl Group, which automatically creates controls for the visualization 
        // elements we created above.
        visGroup = new VisualizationControl();
        // setFieldConvolution() assigns a FieldConvolution to this group.  We pass it the one created above.
        visGroup.setFieldConvolution(mDLIC);
        // setConvolutionModes() determines which convolution modes will be available in this Group.  In this case,
        // we want to show the electric field (DLIC_FLAG_E), and the electric potential (DLIC_FLAG_EP). 
        visGroup.setConvolutionModes(DLIC.DLIC_FLAG_E | DLIC.DLIC_FLAG_EP);
        // setFieldVisGrid() assigns a FieldDirectionGrid to this group.  We pass it the one created above.
        visGroup.setFieldVisGrid(fv);
        // setFieldLineManager() assigns a FieldLineManager to this group.  We pass it the one created above.
        visGroup.setFieldLineManager(fmanager);
        // setSymmetryCount() sets the symmetry count of the FieldLines in this group.  See setSymmetryCount() in FieldLine.
        visGroup.setSymmetryCount(1);
        // setActionFlags() determines which FieldLine properties will be available in this group.  A value of zero 
        // indicates that neither the symmetry nor the color mode of the FieldLines should be editable.
        visGroup.setActionFlags(0);
        // setColorPerVertex() determines whether or not per-vertex coloring will be used on the FieldLines in this group.
        visGroup.setColorPerVertex(false);
        // add the VisualizationControl to the application.
        addElement(visGroup);
        
        // task 2: current task
        /*task0 = new Task("TASK 1", 440, 40);
        task0.addDescription("Move the positive charge\n (15 points possible)");
        HashMap<PhysicalObject, ArrayList<PhysicalObject>> objects = new HashMap<PhysicalObject, ArrayList<PhysicalObject>>();
        RectangularBox target_zone = new RectangularBox();
        
        ZoneRequirement reqZ = new ZoneRequirement();
        task0.addRequirement(reqZ);
        gamificationPanel.addTask(task0);
        
        gamification = new ControlGroup();
        gamification.setText("Gamification");
        gamification.addElement(gamificationPanel);
        addElement(gamification);
        */

        // Final initializations
        mSEC.init();
        addActions();
        resetCamera();

    }
    
    // This method is called whenever a propertyChangeEvent is received, if the app is registered as a listener.
    public void propertyChange(PropertyChangeEvent pce) {
    	// If the user "drags" something in the viewer, request a  reorder on the selected object.  This logic handles the
    	// case where a user drags one object in to another one.  Strictly speaking, this is a "collision", but since it 
    	// involves external forces (the user interaction), it can't be resolved by the simulation's integrator.  Instead,
    	// we simply tell the simulation to push the objects away from each other if the user makes them overlap.
        if (pce.getSource() == theScene) {
            if (pc1.isSelected()) theEngine.requestReorder(pc1);
            if (pc2.isSelected()) theEngine.requestReorder(pc2);
        }
    }
    // This method resets the camera tranform to it's initial state.
    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 1.2), new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.));
    }
    // This method is called whenever the simulation's "reset" button is pressed.  It should reset all the simulation
    // objects and paramters to their initial states.
    public void reset() {
        super.reset();
        pc1.setPosition(new Vector3d(0, 2, 0));
        pc2.setPosition(new Vector3d(0, -2, 0));
        //theEngine.refresh();
    }

    // This method adds ActionEvent triggers to the top menus.  In this case, we add an Action to the Help menu to open the
    // help file for this simulation.  Here we just define the trigger.  
    void addActions() {
        TealAction ta = new TealAction("Two Point Charges", this);
        addAction("Help", ta);

    }
    // This method is called when an ActionEvent is received.  Here we tell the application to launch the Help file when
    // the Help ActionEvent defined above is received.
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Two Point Charges") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/twopointcharge.html");
        	}
        }
        else {
            super.actionPerformed(e);
        }
    }

}
