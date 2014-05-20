/* $Id: Example_09.java,v 1.6 2010/07/16 21:41:47 stefan Exp $ */
/**
 * @author John Belcher 
 * Revision: 1.0 $
 */

package tealsim.physics.examples;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.vecmath.*;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.plot.PlotProperties;
import teal.plot.Graph;
import teal.render.BoundingSphere;
import teal.sim.collision.SphereCollisionController;
import teal.physics.physical.PhysicalObject;
import teal.physics.physical.Wall;
import teal.physics.em.MagneticDipole;
import teal.physics.em.RingOfCurrent;
import teal.physics.em.SimEM;
import teal.ui.control.*;
import teal.util.TDebug;
import teal.sim.spatial.*;
import teal.sim.control.VisualizationControl;

public class Example_09 extends SimEM {

    private static final long serialVersionUID = 3257008735204554035L;
    
    PropertyDouble currentSlider = new PropertyDouble();
    RingOfCurrent floatingCoil;
    Vector3d floatingCoilPos;
    MagneticDipole magDipole;
	Graph position_graph;
	PlotProperties position_plot;
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
    double friction = 10.;
	private FieldLineManager fmanager;
    private FieldLine fl = null;
    int fMode = FieldLine.RUNGE_KUTTA;
 
    public Example_09() {
        super();

        TDebug.setGlobalLevel(0);

        title = "Example_09";
        
		///// Set properties on the SimEngine /////
		// Bounding area represents the characteristic size of the space.
		// setDeltaTime() sets the time step of the simulation.
		// setDamping() sets the damping on the system.
       
        BoundingSphere bs = new BoundingSphere(new Point3d(0, 1.6, 0), 03.5);
        theEngine.setBoundingArea(bs);
        theEngine.setDeltaTime(0.05); 
        theEngine.setDamping(friction);  
        theScene.setBoundingArea(bs);
              
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
        floatingCoil.setDirection(new Vector3d(0., 1., 0.));
        floatingCoil.setPickable(true);
        floatingCoil.setRotable(true);
        floatingCoil.setMoveable(true);
        floatingCoil.setInducing(false);
        floatingCoil.setRadius(ringRad);
        floatingCoil.setThickness(torR);
        floatingCoil.setMass(ringMass);
        floatingCoil.setInducing(false);
        floatingCoil.setCurrent(current);
		// Here we add a collisionController to the RingOfCurrent 
        //so that it will be registered as a colliding object, and
		// react appropriately when it touches the Wall.  
        SphereCollisionController sccx = 
        	new SphereCollisionController(floatingCoil);
        sccx.setRadius(torR);
        sccx.setTolerance(0.01);
        floatingCoil.setColliding(true);
        floatingCoil.setCollisionController(sccx); 
        floatingCoilPos = new Vector3d(0., 
        		1.25+ ringRad + (ringRad * 0.02), 0.);
        addElement(floatingCoil);
      
        // We create a wall that the floating coil sits on.
		// Wall constructor.  		
        Wall wall = new Wall(new Vector3d(0., 0, 0.), 
        		new Vector3d(2., 0., 0.), new Vector3d(0., 0., 2.));
        wall.setElasticity(1.);
        addElement(wall);   
        
        // create the sliders to control the amount of current
        
        currentSlider.setText("I");
        currentSlider.setMinimum(-100);
        currentSlider.setMaximum(100);
        currentSlider.setPaintTicks(true);
        currentSlider.addPropertyChangeListener("value", this);
        currentSlider.setValue(current);
        currentSlider.setVisible(true);

        // add the slider to a control group and add

        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(currentSlider);
        addElement(controls);

	    // add field lines	
		
	    fmanager = new FieldLineManager();       
        fl = makeFLine(-200.0, floatingCoil, null, fLen, kMax, fMode);
        fmanager.addFieldLine(fl);
        
        fl = makeFLine(-1000.0, floatingCoil, null, fLen, kMax, fMode);
        fmanager.addFieldLine(fl);
        
        fl = makeFLine(220.0, floatingCoil, null, fLen, kMax, fMode);
        fmanager.addFieldLine(fl);
        
        fl = makeFLine(120.0, floatingCoil, null, fLen, kMax, fMode);
        fmanager.addFieldLine(fl);

        fl = makeFLine(120.0, magDipole, null, fLen, kMax, fMode);  // was 120
        fl.setBuildDir(FieldLine.BUILD_NEGATIVE);
        fmanager.addFieldLine(fl);

        fl = makeFLine(220.0, magDipole, null, fLen, kMax, fMode);    // was 220
        fl.setBuildDir(FieldLine.BUILD_NEGATIVE);
        fmanager.addFieldLine(fl);
        
	    fmanager.setElementManager(this);
	    
        VisualizationControl vis = new VisualizationControl();
        vis.setFieldLineManager(fmanager);
        addElement(vis);
        
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
        TealAction tb = new TealAction("Example_06", this);
        addAction("Help", tb);
    }

    public void actionPerformed(ActionEvent e) {
        TDebug.println(1, " Action comamnd: " + e.getActionCommand());
        if (e.getActionCommand().compareToIgnoreCase("Example_06") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/example_06.html");
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
        theEngine.setDamping(friction);
        currentSlider.setValue(current);
		theEngine.requestRefresh();
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.025, 0.4), 
        		new Point3d(0., 0.025, 0.), new Vector3d(0., 1., 0.));
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
    protected FieldLine makeFLine(double val, PhysicalObject obj, Color color, 
    		double fLen, int kMax, int fMode) {
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
        fl.setColorMode(FieldLine.COLOR_VERTEX);
        fl.setReceivingFog(true);
        if (col != null) {
            fl.setColor(col);
        }
        return fl;
    }
}

