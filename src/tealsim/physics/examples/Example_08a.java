/* $Id: Example_08a.java,v 1.6 2010/08/10 18:12:34 stefan Exp $ */
/**
 * @author John Belcher - Department of Physics / MIT
 * @version $Revision: 1.6 $
 */

package tealsim.physics.examples;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.media.j3d.BranchGroup;
import javax.vecmath.*;

import teal.core.TElement;
import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.plot.PlotProperties;
import teal.plot.Graph;
import teal.render.BoundingSphere;
import teal.render.Rendered;
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.physics.physical.PhysicalObject;
import teal.physics.physical.Wall;
import teal.physics.em.MagneticDipole;
import teal.physics.em.RingOfCurrent;
import teal.render.j3d.*;
import teal.render.j3d.loaders.Loader3DS;
import teal.physics.em.SimEM;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FluxFieldLine;
import teal.ui.control.*;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

public class Example_08a extends SimEM {

    private static final long serialVersionUID 
    = 3257008735204554035L;
    
    Rendered nativeObject = new Rendered();
    ShapeNode ShapeNodeNative = new ShapeNode();
    PropertyDouble currentSlider = new PropertyDouble();
    RingOfCurrent floatingCoil;
    Vector3d floatingCoilPos;
    MagneticDipole magDipole;
	Graph position_graph;
	PlotProperties position_plot;
	private FieldLineManager fmanager;
    private FieldLine fl = null;
    private FieldConvolution mDLIC;
    private FieldDirectionGrid fv;
    double ringRad = 0.43;
    double torR = 0.08;
    double ringMass = 3.5;
    double current = -50.;
    double magLen = 0.24;
    double magR = 0.09;
    double searchRad = magR;
    double fLen = 0.033;
    double minD = 0.03;
    int kMax = 300; //300
    int fMode = FieldLine.RUNGE_KUTTA;
    double friction = 0.;
 
    public Example_08a() {
        super();

        TDebug.setGlobalLevel(0);

        title = "Example_08";
        
		///// Set properties on the SimEngine /////
		// Bounding area is the size of simulation space.
		// setDeltaTime() sets the time step of simulation.
		// setDamping() sets the damping on system.
     
        BoundingSphere bs = new BoundingSphere(
        		new Point3d(0, 1.6, 0), 03.5);
        theEngine.setBoundingArea(bs);
        theEngine.setDeltaTime(0.02); 
        theEngine.setGravity(
        		new Vector3d(0., -9.81, 0.));
        theEngine.setDamping(friction);  
        theScene.setBoundingArea(bs);
 
        // Create electromagnetic objects
        magDipole = new MagneticDipole();
        magDipole.setMu(10.);
        magDipole.setPosition(new Vector3d(0., 0., 0.));
        magDipole.setDirection(new Vector3d(0, 1, 0));
        magDipole.setPickable(false);
        magDipole.setRotable(false);
        magDipole.setMoveable(false);
        magDipole.setRadius(magR);
        magDipole.setLength(magLen);
        addElement(magDipole);

        floatingCoil = new RingOfCurrent();
        floatingCoil.setID("Ring");
        floatingCoil.setDirection(new Vector3d(0.,1.,0.));
        floatingCoil.setPickable(true);
        floatingCoil.setRotable(true);
        floatingCoil.setMoveable(true);
        floatingCoil.setInducing(false);
        floatingCoil.setRadius(ringRad);
        floatingCoil.setThickness(torR);
        floatingCoil.setMass(ringMass);
        floatingCoil.setInducing(false);
        floatingCoil.setInductance(0.1);
        floatingCoil.setCurrent(current);
        
		// Add a collisionController to the RingOfCurrent 
        // so that it will be registered as a colliding object
		// and react appropriately when it touches the Wall.  
        SphereCollisionController sccx = 
        	new SphereCollisionController(floatingCoil);
        sccx.setRadius(torR);
        sccx.setTolerance(0.01);
        floatingCoil.setColliding(true);
        floatingCoil.setCollisionController(sccx); 
        floatingCoilPos = new Vector3d(0., 1.25+ ringRad + 
        		(ringRad * 0.02), 0.);
        addElement(floatingCoil);
      
        // Create a wall that the floating coil collides with
		// Wall constructor.  		
        Wall wall = new Wall(new Vector3d(0., 0, 0.), 
        		new Vector3d(2., 0., 0.), new Vector3d(0., 0., 2.));
        wall.setElasticity(1.);
        addElement(wall);
        
        // create the sliders for the amount of current
        
        currentSlider.setText("I");
        currentSlider.setMinimum(-100);
        currentSlider.setMaximum(100);
        currentSlider.setPaintTicks(true);
        currentSlider.addPropertyChangeListener("value", this);
        currentSlider.setValue(current);
        currentSlider.setVisible(true);

        // add the slider to a control group 

        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(currentSlider);
        addElement(controls);
        
		// Create a graph of the height of the coil
		// Graph constructor.
		position_graph = new Graph();	
		position_graph.setSize(150, 200);		
		position_graph.setXRange(0., 6.);		
		position_graph.setYRange(-0., 2.);
		position_graph.setWrap(true);
		position_graph.setClearOnWrap(true);
		position_graph.setXLabel("Time");		
		position_graph.setYLabel("position");
		// Here we create the PlotItem being drawn 
		position_plot = new PlotProperties();
		position_plot.setObjectX((TElement) theEngine); 
		position_plot.setPropertyX("time");
		position_plot.setObjectY(floatingCoil); 
		position_plot.setPropertyY("y");  
		// adds the supplied PlotItem to the graph.
		position_graph.addPlotItem(position_plot);
		
		// Here we create a new Control Group
		ControlGroup graphPanel = new ControlGroup();
		graphPanel.setText("Graphs");
		graphPanel.addElement(position_graph);
		addElement(graphPanel);
		
		// add field lines
		
	    fmanager = new FieldLineManager();
        fl = makeFLine(-200.0, floatingCoil, null, 
        		fLen, kMax, fMode);
        fmanager.addFieldLine(fl);
        fl = makeFLine(-1000.0, floatingCoil, null, 
        		fLen, kMax, fMode);
        fmanager.addFieldLine(fl);
        fl = makeFLine(120.0, magDipole, null, 
        		fLen, kMax, fMode);
        fl.setBuildDir(FieldLine.BUILD_NEGATIVE);
        ((FluxFieldLine) fl).setSearchIntervals(600);
        ((FluxFieldLine) fl).setSearchSubIntervals(600);
        fmanager.addFieldLine(fl);
        fl = makeFLine(220.0, magDipole, null, 
        		fLen, kMax, fMode);
        fl.setBuildDir(FieldLine.BUILD_NEGATIVE);
        fmanager.addFieldLine(fl);  
        fl = makeFLine(400.0, magDipole, null, 
        		fLen, kMax, fMode);
        fl.setBuildDir(FieldLine.BUILD_NEGATIVE);
	    fmanager.setElementManager(this);   
	          
		// Here we add a FieldConvolution generator
        RectangularPlane rec = new RectangularPlane(
        		new Vector3d(-3., -3., 0.),
				new Vector3d(-3., 3., 0.), 
				new Vector3d(3., 3., 0.));
		mDLIC = new FieldConvolution();
		mDLIC.setSize(new Dimension(700, 700));
		mDLIC.setVisible(false);
		mDLIC.setComputePlane(rec);
       
        // Here we create a FieldDirectionGrid
      	// FieldDirectionGrid constructor.
        fv = new FieldDirectionGrid();
        // setType() sets the type of field 
        fv.setType(Field.B_FIELD);
        
        //now add all these to a VisualizationControl 
        VisualizationControl vis = new VisualizationControl(); 
        vis.setFieldLineManager(fmanager);
        vis.setFieldVisGrid(fv);
        vis.setFieldConvolution(mDLIC);
		vis.setConvolutionModes(
				DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
        addElement(vis);  
        
        // import a .3DS files object using Loader3DS
        double scale3DS = 0.02; 
        Loader3DS max = new Loader3DS();
    	
        BranchGroup bg01 = 
         max.getBranchGroup("models/LDX/vessel.3ds",
         "models/LDX/");
        Node3D node01 = new Node3D();
        node01.setScale(scale3DS);
        node01.addContents(bg01);      
        Rendered importedObject01 = new Rendered();
        importedObject01.setNode3D(node01);
        importedObject01.setPosition(new Vector3d(0., 0., 0.));
        addElement(importedObject01);
        
        // set paramters for mouseScale    
        setMouseMoveScale(0.05,0.05,0.5);

        mSEC.init(); 
        resetCamera();
        // addAction for pulldown menus on TEALsim windows     
        addActions();
        reset();
        
    }
   
    void addActions() {
        TealAction ta = new TealAction("Execution & View", this);
        addAction("Help", ta);
        TealAction tb = new TealAction("Example_08", this);
        addAction("Help", tb);
    }

    public void actionPerformed(ActionEvent e) {
        TDebug.println(1, " Action comamnd: " 
        		+ e.getActionCommand());
        if (e.getActionCommand().compareToIgnoreCase(
        		"Example_08") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser(
            		"help/Example_08.html");
        	}
        }  else {
            super.actionPerformed(e);
        }
        if (e.getActionCommand().compareToIgnoreCase("Execution & View") == 0) 
        {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/executionView.html");
        	}
        }  else {
            super.actionPerformed(e);
        }
    }

    public void reset() {
        floatingCoil.setPosition(floatingCoilPos);
        floatingCoil.setVelocity(new Vector3d());
        floatingCoil.setDirection(new Vector3d(0., 1., 0.));
        position_graph.clear(0);
        theEngine.setDamping(friction);
        currentSlider.setValue(current);
		theEngine.requestRefresh();
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.025, 0.4), 
        		new Point3d(0., 0.025, 0.), 
        		new Vector3d(0., 1., 0.));
    }

    public void propertyChange(PropertyChangeEvent pce) {
        Object source = pce.getSource();
        if (source == currentSlider) {
            current = ((Double) pce.getNewValue()).doubleValue();
            floatingCoil.setCurrent(current);   
        } else {
            super.propertyChange(pce);
        }
    }
    
    protected FieldLine makeFLine(double val, PhysicalObject obj, 
    	Color color, double fLen, int kMax, int fMode) {
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
        //fl.setSymmetryCount(symCount);
        fl.setColorMode(FieldLine.COLOR_VERTEX);
        fl.setReceivingFog(true);
        if (col != null) {
            fl.setColor(col);
        }
        return fl;
    }
}

