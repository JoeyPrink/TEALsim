/* $Id: Example_06a.java,v 1.4 2010/07/16 21:41:46 stefan Exp $ */
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
import teal.render.Rendered;
import teal.sim.collision.SphereCollisionController;
import teal.physics.physical.PhysicalObject;
import teal.physics.physical.Wall;
import teal.physics.em.PointCharge;
import teal.render.j3d.*;
import teal.physics.em.SimEM;
import teal.ui.control.*;
import teal.util.TDebug;
import teal.sim.spatial.*;
import teal.sim.control.VisualizationControl;
import teal.field.*;
import teal.config.Teal;

public class Example_06a extends SimEM {

    private static final long serialVersionUID = 3257008735204554035L;
    
    Rendered nativeObject = new Rendered();
    ShapeNode ShapeNodeNative = new ShapeNode();
    PropertyDouble chargeSlider = new PropertyDouble();
    PointCharge fixedCharge;
	Graph position_graph;
	PlotProperties position_plot;
	FieldLineManager fmanager;
    private FieldLine fl = null;
    int fMode = FieldLine.RUNGE_KUTTA;
 
    double fixedChargeRad = 0.2;
    double searchRad = fixedChargeRad;
    double fLen = 0.033;
    double minD = 0.03;
    int kMax = 300; //300
    double friction = 10.;
    /** The falling charge.  */
    PointCharge floatingCharge;
    /** The vector position of the charge.  */
    Vector3d floatingChargePos;
    /** The radius of the sphere representing the charge.  */
    double chargeRad = 0.2;
    /** The mass of charge. */
    double chargeMass = 3.5;
    /** The charge of the fixed charge. */
    double charge = 1.;
 
    public Example_06a() {
        super();

        TDebug.setGlobalLevel(0);

        title = "Example_06a";
        
		///// Set properties on the SimEngine /////
		// Bounding area represents the characteristic size of the space.
		// setDeltaTime() sets the time step of the simulation.
		// setDamping() sets the damping on the system.
       
        BoundingSphere bs = new BoundingSphere(new Point3d(0, 1.6, 0), 03.5);
        theEngine.setBoundingArea(bs);
        theEngine.setDeltaTime(0.005); 
        theEngine.setDamping(friction);  
        theScene.setBoundingArea(bs);
              
        fixedCharge = new PointCharge();
        fixedCharge.setCharge(charge);
        fixedCharge.setPosition(new Vector3d(0., 0., 0.));
        fixedCharge.setDirection(new Vector3d(0, 1, 0));
        fixedCharge.setPickable(false);
        fixedCharge.setRotable(false);
        fixedCharge.setMoveable(false);
        fixedCharge.setRadius(fixedChargeRad);
        addElement(fixedCharge);

        floatingCharge = new PointCharge();
        floatingCharge.setID("Charge");
        floatingCharge.setCharge(0.);
        floatingCharge.setDirection(new Vector3d(0., 1., 0.));
        floatingChargePos = new Vector3d(0., 1.25, 0.);
        floatingCharge.setPickable(true);
        floatingCharge.setRotable(true);
        floatingCharge.setMoveable(true);
        floatingCharge.setRadius(chargeRad);
        floatingCharge.setMass(chargeMass);
        floatingCharge.setDrawn(true);
        
		// Here we add a collisionController to the RingOfCurrent 
        //so that it will be registered as a colliding object, and
		// react appropriately when it touches the Wall.  
        SphereCollisionController sccx = 
        	new SphereCollisionController(floatingCharge);
        sccx.setRadius(chargeRad);
        sccx.setTolerance(0.01);
        floatingCharge.setColliding(true);
        floatingCharge.setCollisionController(sccx); 
        floatingChargePos = new Vector3d(0., 
        		1.25+ chargeRad + (chargeRad * 0.02), 0.);
        addElement(floatingCharge);
      
        // We create a wall that the floating charge sits on.
		// Wall constructor.  		
        Wall wall = new Wall(new Vector3d(0., 0, 0.), 
        		new Vector3d(2., 0., 0.), new Vector3d(0., 0., 2.));
        wall.setElasticity(1.);
        addElement(wall);   
        
     // create the sliders to control the amount of fixed charge
        
        chargeSlider.setText("Qfixed");
        chargeSlider.setMinimum(-4.);
        chargeSlider.setMaximum(4.);
        chargeSlider.setPaintTicks(true);
        chargeSlider.addPropertyChangeListener("value", this);
        chargeSlider.setValue(1.);
        chargeSlider.setVisible(true);
        
        
        // add the slider to a control group and add

        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(chargeSlider);
        addElement(controls);
        
        
	    // add field lines	
	
	    fmanager = new FieldLineManager();     
	    
        fl = makeFLine(.1465, floatingCharge, null, fLen, kMax, fMode);
    //    fmanager.addFieldLine(fl);
        
        fl = makeFLine(0.5, floatingCharge, null, fLen, kMax, fMode);
    //    fmanager.addFieldLine(fl);
        
        fl = makeFLine(0.8536, floatingCharge, null, fLen, kMax, fMode);
    //    fmanager.addFieldLine(fl);

        fl = makeFLine(.5, fixedCharge, null, fLen, kMax, fMode);
        fl.setBuildDir(FieldLine.BUILD_POSITIVE);
       // ((FluxFieldLine) fl).setSearchIntervals(600);
       // ((FluxFieldLine) fl).setSearchSubIntervals(600);
        fmanager.addFieldLine(fl);

        fl = makeFLine(20.0, fixedCharge, null, fLen, kMax, fMode);
        fl.setBuildDir(FieldLine.BUILD_POSITIVE);
   //     fmanager.addFieldLine(fl);
        
        fl = makeFLine(20.0, fixedCharge, null, fLen, kMax, fMode);
        fl.setBuildDir(FieldLine.BUILD_NEGATIVE);        
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
        TealAction tb = new TealAction("Example_05", this);
        addAction("Help", tb);
    }

    public void actionPerformed(ActionEvent e) {
        TDebug.println(1, " Action comamnd: " + e.getActionCommand());
        if (e.getActionCommand().compareToIgnoreCase("Example_05") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/example_05.html");
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
        floatingCharge.setPosition(floatingChargePos);
        floatingCharge.setVelocity(new Vector3d(0.,0.,0.));
        floatingCharge.setDirection(new Vector3d(0., 1., 0.));
        theEngine.setDamping(friction);
        chargeSlider.setValue(1.);
		theEngine.requestRefresh();
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.025, 0.4), 
        		new Point3d(0., 0.025, 0.), new Vector3d(0., 1., 0.));
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
            if (obj instanceof PointCharge) {
                fl = new FluxFieldLine(val, obj, FluxFieldLine.SEARCH_FORWARD, FluxFieldLine.SEARCH_CIRCLE);
                fl.setObjRadius(searchRad);
            } else {
                return null;
            }
        }
        fl.setType(Field.E_FIELD);
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
    
    public void propertyChange(PropertyChangeEvent pce) {
        Object source = pce.getSource();
        if (source == chargeSlider) {
            charge = ((Double) pce.getNewValue()).doubleValue();
            fixedCharge.setCharge(charge);   
        } else {
            super.propertyChange(pce);
        }
    }
    
}

