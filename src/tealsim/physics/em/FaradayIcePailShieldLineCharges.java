/* $Id: FaradayIcePailShieldLineCharges.java,v 1.7 2010/08/10 18:12:33 stefan Exp $ */

/**
 * A demonstration implementation of the TFramework.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.7 $
 */

package tealsim.physics.em;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.swing.*;
import javax.vecmath.*;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.Rendered;
import teal.render.primitives.Pipe;
import teal.render.scene.TShapeNode;
import teal.render.viewer.*;
import teal.sim.collision.*;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.SimEngine;
import teal.sim.engine.TEngineControl;
import teal.physics.em.SimEM;
import teal.physics.physical.PhysicalObject;
import teal.physics.em.InfiniteLineCharge;
import teal.sim.spatial.*;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.visualization.dlic.DLIC;

import teal.util.TDebug;

public class FaradayIcePailShieldLineCharges extends SimEM implements SelectListener {

    private static final long serialVersionUID = 3257846575882646838L;

    JButton groundButton = null;
    JButton ungroundButton = null;
    JButton zeroCentralChargeButton = null;
    JButton resetButton = null;
    JButton changeSignCCButton = null;
    PropertyDouble slider1 = null;
    double radius1 = 2.;
    double radius2 = 4.;
    double radius3 = 6;
    double radius4 = 8.;
    double height = 0.5;
    ControlGroup controls;
    VisualizationControl visGroup;
    
    protected FieldConvolution mDLIC = null;
	SpatialTextLabel lbl,lb2;
    final private int N = 15;
    private double signCentralCharge = -1.;
    private InfiniteLineCharge[] pointCharges = new InfiniteLineCharge[3 * N];
    private InfiniteLineCharge centralCharge = null;
    private double pointChargeRadius = 0.2;
    //	ArrayList outerWalls, innerWalls;

    Rendered ring1, ring2, ring3, ring4;
    Rendered bottomcage, bottomshield, groundcageshield, groundshieldinfinity;
    PhysicalObject cylinder1, cylinder2, cylinder3, cylinder4;

    public FaradayIcePailShieldLineCharges() { 

        super();
        title = "Faraday Ice Pail and Shield ";
        TDebug.setGlobalLevel(0);

        // Building the world.
      
        setBoundingArea(new BoundingSphere(new Point3d(), 8));
        setDeltaTime(0.25);
        setDamping(0.1);
        setGravity(new Vector3d(0., 0., 0.));

       

        //setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM | TViewer.VP_TRANSLATE);
        mDLIC = new FieldConvolution();
        RectangularPlane rec = new RectangularPlane(new Vector3d(-12., -12., 0.), new Vector3d(-12., 12., 0.),
            new Vector3d(12., 12., 0.));
        mDLIC.setComputePlane(rec);
     
        // Creating components.
        
        //  add labels
        
        Vector3d posFaradayCageLabel  = new Vector3d(-1.6,-4.,0);
		lbl = new SpatialTextLabel(" Faraday Ice Pail ", posFaradayCageLabel );
		lbl.setBaseScale(0.5);
		lbl.setPositionOffset(new Vector3d(0.05, 0.0, 0.));
		lbl.setRefDirectionOffset(0.5);
		lbl.setUseDirectionOffset(true);
		addElement(lbl);
		
        Vector3d posShieldLabel  = new Vector3d(3.8,-6.,0);
		lb2 = new SpatialTextLabel(" Grounded Shield ", posShieldLabel );
		lb2.setBaseScale(0.5);
		lb2.setPositionOffset(new Vector3d(0.05, 0.0, 0.));
		lb2.setRefDirectionOffset(0.5);
		lb2.setUseDirectionOffset(true);
		addElement(lb2);
		
		// add spatial structures
		
        // this is the inner wall of the inner wire mesh
		
        cylinder1 = new PhysicalObject();
        CylindricalWallCollisionController cwcc1 = new CylindricalWallCollisionController(cylinder1);
        cwcc1.setTolerance(0.1);
        cwcc1.setDirection(new Vector3d(0., 0., 1.));
        cwcc1.setRadius(radius1);
        cylinder1.setCollisionController(cwcc1);
        cylinder1.setColliding(true);
        addElement(cylinder1);
        double thickness1 = 0.05;
        
        ring1 = new Pipe(radius1 + thickness1 / 2., thickness1, height);
        ring1.setPickable(false);
        ring1.getMaterial().setColor(new Color3f(Color.ORANGE));
        ring1.getMaterial().setTransparancy(0.1f);
        ring1.setDirection(new Vector3d(0., 0., 1.));
        addElement(ring1);

        // this is the outer wall of the inner wire mesh
        cylinder2 = new PhysicalObject();
        CylindricalWallCollisionController cwcc2 = new CylindricalWallCollisionController(cylinder2);
        cwcc2.setTolerance(0.1);
        cwcc2.setDirection(new Vector3d(0., 0., 1.));
        cwcc2.setRadius(radius2);
        cylinder2.setCollisionController(cwcc2);
        cylinder2.setColliding(true);
        addElement(cylinder2);
        double thickness2 = 0.05;
        ring2 = new Pipe(radius2 - thickness2 / 2., thickness2, height);
        
        ring2.setPickable(false);
        ring2.setDirection(new Vector3d(0., 0., 1.));
        ring2.setColor(new Color3f(Color.ORANGE));
        ring2.getMaterial().setTransparancy(0.1f);

        addElement(ring2);

        // this is the inner wall of the shield grid (outer wire mesh)
        
        cylinder3 = new PhysicalObject();
        CylindricalWallCollisionController cwcc3 = new CylindricalWallCollisionController(cylinder3);
        cwcc3.setTolerance(0.1);
        cwcc3.setDirection(new Vector3d(0., 0., 1.));
        cwcc3.setRadius(radius3);
        cylinder3.setCollisionController(cwcc3);
        cylinder3.setColliding(true);
        addElement(cylinder3);
        double thickness3 = .05;
        ring3 = new Pipe(radius3 - thickness3 / 2., thickness3, height);
        ring3.setPickable(false);
        ring3.setDirection(new Vector3d(0., 0., 1.));
        ring3.setColor(new Color3f(Color.ORANGE));
        ring3.getMaterial().setTransparancy(0.1f);
        addElement(ring3);
        
   // this is the outer wall of the shield grid (outer wire mesh)
        
        cylinder4 = new PhysicalObject();
        CylindricalWallCollisionController cwcc4 = new CylindricalWallCollisionController(cylinder4);
        cwcc4.setTolerance(0.1);
        cwcc4.setDirection(new Vector3d(0., 0., 1.));
        cwcc4.setRadius(radius4);
        cylinder4.setCollisionController(cwcc4);
        cylinder4.setColliding(false);
        addElement(cylinder4);
        double thickness4 = 0.05;
        ring4 = new Pipe(radius4 - thickness4 / 2., thickness4, height);
        
        ring4.setPickable(false);
        ring4.setDirection(new Vector3d(0., 0., 1.));
        ring4.setColor(new Color3f(Color.ORANGE));
        ring4.getMaterial().setTransparancy(0.9f);
        addElement(ring4);
        
 // do bottom of the shield, and the cage, and the grounding conductor between shield and cage and shield and infinity
        
        bottomcage = new Pipe( (radius2+radius1)*.5, (radius2-radius1),.1);

        bottomcage.setPickable(false);
        bottomcage.setColor(new Color3f(Color.ORANGE));
        bottomcage.getMaterial().setTransparancy(0.4f);
        bottomcage.setDirection(new Vector3d(0., 0., 1.));
        bottomcage.setPosition(new Vector3d(0., 0., -height/2.));
        addElement(bottomcage);
        
        bottomshield = new Pipe((radius3+radius4)*.5, (radius4-radius3), .1);
      
        bottomshield.setPickable(false);
        bottomshield.setColor(new Color3f(Color.ORANGE));
        bottomshield.setDirection(new Vector3d(0., 0., 1.));
        bottomshield.setPosition(new Vector3d(0., 0., -height/2.));
        addElement(bottomshield);
        
        groundcageshield = new Pipe((radius4+radius1)*.5, (radius4-radius1), .1);
        groundcageshield.setPickable(false);
        groundcageshield.setColor(new Color3f(Color.ORANGE));
        groundcageshield.getMaterial().setTransparancy(0.4f);
        groundcageshield.setDirection(new Vector3d(0., 0., 1.));
        groundcageshield.setPosition(new Vector3d(0., 0., -height/2.));
        groundcageshield.setDrawn(false);
        addElement(groundcageshield);
        
        groundshieldinfinity = new Pipe( radius4+4., 8.,  .1);  
        groundshieldinfinity.setPickable(false);
        groundshieldinfinity.setColor(new Color3f(Color.ORANGE));
        groundshieldinfinity.getMaterial().setTransparancy(0.4f);
        groundshieldinfinity.setDirection(new Vector3d(0., 0., 1.));
        groundshieldinfinity.setPosition(new Vector3d(0., 0., height/2.));

        groundshieldinfinity.setDrawn(true);
        addElement(groundshieldinfinity);
        
        
        // -> Point Charges
        // these are the charges on the inner conductor, N positive and N negative alternating, 
        // and N on the outer conductor (in from infinity), opposite sign of central charge
        for (int i = 0; i < 3 * N; i++) {
            pointCharges[i] = new InfiniteLineCharge();
            pointCharges[i].setRadius(pointChargeRadius);
            pointCharges[i].setMass(1.0);
       


            pointCharges[i].setGeneratingP(true);

            pointCharges[i].setID("pointCharge" + i);
            pointCharges[i].setPickable(false);
            pointCharges[i].setColliding(true);
            pointCharges[i].setMoveable(true);
            SphereCollisionController sccx = new SphereCollisionController(pointCharges[i]);
            sccx.setRadius(pointChargeRadius);
            sccx.setTolerance(0.5);
            pointCharges[i].setCollisionController(sccx);
            addElement(pointCharges[i]);
        }
        resetPointCharges();

        centralCharge = new InfiniteLineCharge();
        centralCharge.setPosition(new Vector3d(0.,0,0.));
        centralCharge.setRadius(.4);
        centralCharge.setMass(1.0);
        centralCharge.setCharge(0.);
        centralCharge.setID("centralCharge");
        centralCharge.setPickable(false);
        centralCharge.setMoveable(false);
        centralCharge.setColliding(false);
        addElement(centralCharge);

        slider1 = new PropertyDouble();
        slider1.setPrecision(1);
        slider1.setMinimum(-200.);
        slider1.setMaximum(200.);
        //slider1.setBounds(40, 515, 415, 50);
        slider1.setPaintTicks(true);
        slider1.addRoute(centralCharge, "charge");
        double centralChargeCharge = signCentralCharge*N;
        slider1.setValue(centralChargeCharge);
        slider1.setText("Central Charge");
        slider1.setBorder(null);

        controls = new ControlGroup();
        controls.setText("Parameters");
  //      controls.add(slider1);
        

        changeSignCCButton = new JButton(new TealAction("Change Sign Central Charge", "Change Sign Central Charge", this));
        changeSignCCButton.setFont(changeSignCCButton.getFont().deriveFont(Font.BOLD));
        changeSignCCButton.setBounds(40, 600, 195, 24);
        controls.add(changeSignCCButton);
        
        groundButton = new JButton(new TealAction("Connect Cage To Shield", "Connect Cage To Shield", this));
        groundButton.setFont(groundButton.getFont().deriveFont(Font.BOLD));
        groundButton.setBounds(40, 570, 195, 24);
        controls.add(groundButton);

        ungroundButton = new JButton(new TealAction("Disconnect Cage From Shield", "Disconnect Cage From Shield", this));
        ungroundButton.setFont(ungroundButton.getFont().deriveFont(Font.BOLD));
        ungroundButton.setBounds(40, 600, 195, 24);
        controls.add(ungroundButton);
        

        zeroCentralChargeButton = new JButton(new TealAction("Zero Central Charge", "Zero Central Charge", this));
        zeroCentralChargeButton.setFont(zeroCentralChargeButton.getFont().deriveFont(Font.BOLD));
        zeroCentralChargeButton.setBounds(40, 600, 195, 24);
        controls.add(zeroCentralChargeButton);


        resetButton = new JButton(new TealAction("Reset", "Reset", this));
        resetButton.setFont(resetButton.getFont().deriveFont(Font.BOLD));
        resetButton.setBounds(40, 600, 195, 24);
      //  controls.add(resetButton);
        
        visGroup = new VisualizationControl();
        visGroup.setFieldConvolution(mDLIC);
        visGroup.setConvolutionModes(DLIC.DLIC_FLAG_E | DLIC.DLIC_FLAG_EP);
        
        addElement(controls);
        addElement(visGroup);
     
        addSelectListener(this);
        mSEC.init();
        resetCamera();
        reset();
        addActions();
    }

    public void intialize(){
    	 theEngine.setAnnihilating(true);
    }
    
    void addActions() {
        TealAction ta = new TealAction("Execution & View", this);
        addAction("Help", ta);
        TealAction tb = new TealAction("Faraday Ice Pail & Shield", this);
        addAction("Help", tb);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Faraday Ice Pail & Shield") == 0) {
            if ((mFramework != null) && (mFramework instanceof TFramework)) {
                ((TFramework)mFramework).openBrowser("help/FaradayIcePailShield.html");
            }
        }
        if (e.getActionCommand().compareToIgnoreCase("Execution & View") == 0) 
            {
            	if((mFramework != null) && (mFramework instanceof TFramework)) {
            		((TFramework)mFramework).openBrowser("help/executionView.html");
            	}
            }
        if (e.getActionCommand().compareToIgnoreCase("Connect Cage To Shield") == 0) {
            ground();}
        if (e.getActionCommand().compareToIgnoreCase("Disconnect Cage From Shield") == 0) {
            unground();}
        if (e.getActionCommand().compareToIgnoreCase("Zero Central Charge") == 0) {
            zeroCentralCharge();}
        if (e.getActionCommand().compareToIgnoreCase("Change Sign Central Charge") == 0) {
            changeSignCentralCharge();}
        if (e.getActionCommand().compareToIgnoreCase("Reset") == 0) {
            reset();}
         else {
          super.actionPerformed(e);
        }
    }
        

    private void ground() {
 
        ring2.setDrawn(false);
        cylinder2.setColliding(false);
        
        ring3.setDrawn(false);
        cylinder3.setColliding(false);
        groundcageshield.setDrawn(true);
        cylinder4.setColliding(true);
    }

    private void unground() {

        int state = mSEC.getSimState();
        if (state == TEngineControl.RUNNING) {
            mSEC.stop();
        }

        ring2.setDrawn(true);
        cylinder2.setColliding(true);
        ring3.setDrawn(true);
        cylinder3.setColliding(true);
        
        groundcageshield.setDrawn(false);

        if (state == TEngineControl.RUNNING) {
            mSEC.start();
        }
        
    }
    
    private void zeroCentralCharge() {
  
        for (int i = 0; i < N; i++) {
        	int j = i+2*N;
            pointCharges[j].setCharge(0.);       
            pointCharges[j].setDrawn(false);      
        }
        
   
        
        centralCharge.setCharge(0.);
    }


    public void propertyChange(PropertyChangeEvent pce) {
        super.propertyChange(pce);
    }

    public void changeSignCentralCharge() {
    	signCentralCharge = -signCentralCharge;
        resetPointCharges();
        mSEC.stop();
        //resetCamera();
        for (int i = 0; i < N; i++) {
        	int j = i+2*N;
            pointCharges[j].setCharge(-signCentralCharge);       
            pointCharges[j].setDrawn(true);      
        }
        centralCharge.setCharge(signCentralCharge*N);
        cylinder4.setColliding(false);
        unground();
    }
    
    public void reset() {
        resetPointCharges();
        mSEC.stop();
        //resetCamera();
        for (int i = 0; i < N; i++) {
        	int j = i+2*N;
            pointCharges[j].setCharge(-signCentralCharge);       
            pointCharges[j].setDrawn(true);      
        }
        centralCharge.setCharge(signCentralCharge*N);
        unground();
    }

    private void resetPointCharges() {
        Point3d[] positions = new Point3d[3 * N];
        Point3d position = null;
        
        double charge = 1.;
        double r1 = radius1;
        double r2 = radius2;
        double r3 = radius3;
        double r4 = radius4;
        double ang;
        double rad;
        cylinder4.setColliding(false);
        for (int i = 0; i < 3 * N; i++) {
        // set charge signs
            if ( i < 2 * N) {
                	charge = -1.*charge;
             }
            else charge = -signCentralCharge;
            pointCharges[i].setCharge(charge);
                
         // set position of charges
            if (i < 2 * N) {
            	rad = (r2 - r1) * .5 + r1;
            	ang = 2. * Math.PI * (double) i / (2. * (double) N);
            }
            else {
            	ang = 2. * Math.PI * (double) i / ((double) N);
            	rad = r4+3.;
            }
            position = new Point3d(rad * Math.cos(ang), rad * Math.sin(ang), 0.);
            positions[i] = position;
            pointCharges[i].setPosition(new Vector3d(position), true);
            pointCharges[i].setVelocity(new Vector3d());
        }
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 1), new Point3d(), new Vector3d(0., 1., 0.));

    }

    public synchronized void dispose() {
        super.dispose();
    }

    public void processSelection(SelectEvent se) {
        TDebug.println(0, se.getSource() + " select state = " + se.getStatus());
    }

    

}
