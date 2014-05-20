/* $Id: ConductingSphericalShellShielding.java,v 1.10 2010/07/16 21:41:41 stefan Exp $ */

/**
 * A demonstration implementation of the TFramework.
 * 
 * @author John Belcher - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.10 $
 */

package tealsim.physics.em;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Collection;

import teal.render.BoundingSphere;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.Rendered;
import teal.render.primitives.Cylinder;
import teal.render.primitives.Pipe;
import teal.render.primitives.Sphere;
import teal.render.primitives.Torus;
import teal.render.scene.TShapeNode;
import teal.render.viewer.TViewer;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.SimEngine;
import teal.physics.em.SimEM;
import teal.physics.physical.PhysicalObject;
import teal.physics.em.PointCharge;
import teal.physics.em.ConductingSphericalShell;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FieldVector;
import teal.sim.spatial.GeneralVector;
import teal.sim.spatial.RelativeFLine;
import teal.ui.UIPanel;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.visualization.dlic.DLIC;


public class ConductingSphericalShellShielding extends SimEM {

    private static final long serialVersionUID = 3257009869025653297L;
    JButton changeSignCCButton = null;
    double radiusInduced = .15;
    FieldDirectionGrid fv;
    FieldConvolution mDLIC;
    FieldLineManager fmanager;
    VisualizationControl visGroup;
    ControlGroup controls;
    PointCharge pcharge;
    ConductingSphericalShell conductingShell ;
 
    PropertyDouble rposPCslider = new PropertyDouble();
    PropertyDouble angPCslider = new PropertyDouble();
    PropertyDouble RadiusCSslider = new PropertyDouble();
    PropertyDouble thicknessCSslider = new PropertyDouble();
    PropertyDouble radiusPCslider = new PropertyDouble();
    double radius = 5.;
    double height = .1;
    double thickness = 2.5;
    double radiusPointCharge = .3;
    int Ninduced;
	UIPanel FieldToShow;
	ButtonGroup optionsGroup;
	JRadioButton rad1;
    JRadioButton rad2;       
    JRadioButton rad3;
    Sphere[] InducedChargeSpheresInner;
    Sphere[] InducedChargeSpheresOuter;
    double signFreeCharge;
    double anglePC = 0.;

    public ConductingSphericalShellShielding() {

        super();
        super.initialize();
        title = "Shielding By Conducting Shell";
        setID(title);
        signFreeCharge = 1;
        Ninduced = 5;
	    //  create the spheres showing the induced charges and their shapenodes
        InducedChargeSpheresInner = new Sphere[4*Ninduced];
        InducedChargeSpheresOuter = new Sphere[4*Ninduced];
        for (int j = 0; j < 4*Ninduced ; j++) {
     		InducedChargeSpheresInner[j] = new Sphere();
     		InducedChargeSpheresInner[j].setRadius(radiusPointCharge );
     		InducedChargeSpheresInner[j].setDrawn(false);
     		InducedChargeSpheresInner[j].setPosition(new Vector3d(0.,j*.4,0.));
     		addElement(InducedChargeSpheresInner[j]);
     		InducedChargeSpheresOuter[j] = new Sphere();
     		InducedChargeSpheresOuter[j].setRadius(radiusPointCharge );
     		InducedChargeSpheresOuter[j].setDrawn(false);
     		InducedChargeSpheresOuter[j].setPosition(new Vector3d(1.,j*.4,0.));
     		addElement(InducedChargeSpheresOuter[j]);
        } 	
        
        // set up UI panel for choice of fields to show
        
        GridBagLayout gbl =new GridBagLayout();
        FieldToShow = new UIPanel();
        FieldToShow.setLayout(gbl);
        UIPanel options = new UIPanel();
        options.setBorder(BorderFactory.createLineBorder(Color.black));
        options.setLayout(new GridLayout(6,1));
        optionsGroup = new ButtonGroup();
        rad1 = new JRadioButton("Show Total Field");        
        rad2 = new JRadioButton("Show Free Charge Field");       
        rad3 = new JRadioButton("Show Induced Charge Field");
        rad1.setSelected(true);
        rad1.addActionListener(this);
        rad2.addActionListener(this);
        rad3.addActionListener(this);
		optionsGroup.add(rad1);
		optionsGroup.add(rad2);
		optionsGroup.add(rad3);
		options.add(rad1);
		options.add(rad2);
		options.add(rad3);
		
	     FieldToShow.add(options);
	        //gbl.setConstraints(scoreBtn, con);
	        //gameControls.add(scoreBtn);
	
	        addElement(FieldToShow);
        
	 
        
        ///// INITIALIZATION OF SIMULATION AND VIEWER PARAMETERS /////
        // Here we set the bounding area of the simulation space.  This should be characteristic of the size of the space
        // being used.
        setBoundingArea(new BoundingSphere(new Point3d(), 10));
        // setNavigationMode() sets the mouse-based camera navigation modes available in this simulation.  In this case 
        // we have enabled zooming, translation, and rotation of the camera (TViewer.ORBIT merely indicates that we are
        // using functions of the OrbitBehavior class).
        setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM | TViewer.VP_TRANSLATE | TViewer.VP_ROTATE);
        // setShowGizmos() determines whether or not we want to the viewer to display transform gizmos on selected objects.
        setShowGizmos(false);
        // setVisible() on the SimulationModelControl determines whether or not the simulation controls (play, pause, etc.)
        // are visible.
        mSEC.setVisible(true);

        
        ///// INITIALIZATION OF SIMULATION OBJECTS /////
        

        // Here we add the PointCharge to the simulation.
        // PointCharge constructor.
        pcharge = new PointCharge();
         // setID() gives a String ID to the PointCharge, which we can use internally to identify it.
        pcharge.setID("PointCharge ");
        // setPosition() sets the position of the PointCharge.
        pcharge.setPosition(new Vector3d(8, 0, 0));
        // setRadius() sets the radius of the PointCharge.  Radius has no physical significance, and only affects how the
        // PointCharge is rendered.
        pcharge.setRadius(radiusPointCharge);
        // setCharge() sets the charge on the PointCharge.
        pcharge.setCharge(2.5);
        // setMass() sets the mass of the PointCharge.
        pcharge.setMass(1.);
        // setSelectable() determines whether this object is selectable.  Selectable objects are useful in situations where
        // you might want to perform generalized operations on only certain ("selected") objects.
        pcharge.setSelectable(false);
        // setPickable() determines whether this object is pickable with the mouse (ie. generates a pickEvent?).  In 
        // principle, if selectable is set to true, pickable must be set to true as well.
        pcharge.setPickable(false);
        // setColliding() determines whether this object will be checked for collisions.
        pcharge.setColliding(false);

  
        addElement(pcharge);

        
        // Creation and initialization of the conducting shell
        conductingShell = new ConductingSphericalShell();
        conductingShell.setID(" ConductingSphericalShell ");
        conductingShell.setPosition(new Vector3d(0, 0, 0));
        conductingShell.setFreeChargePosition(new Vector3d(8, 0, 0));
        conductingShell.setRadius(radiusPointCharge);
        conductingShell.setCharge(2.5);
        conductingShell.setMass(1.);
        conductingShell.setSelectable(false);
        conductingShell.setPickable(false);
        conductingShell.setColliding(false);
        conductingShell.setRadius(radius-thickness/2);
        conductingShell.setThickness(thickness);
        conductingShell.setLength(height);
        conductingShell.setDirection(new Vector3d(0.,0.,1.));
        conductingShell.addPropertyChangeListener("value",this);
        addElement(conductingShell);

        
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
    	mDLIC.setColorMode(Teal.ColorMode_MAGNITUDE);
        
     
        // Here we create a FieldLineManager and add some FieldLines to it.
        // FieldLineManager constructor.
        fmanager = new FieldLineManager();
        // setElementManager() should pass a reference to this simulation.
        fmanager.setElementManager(this);
        // setColorMode() sets the color mode of the FieldLine.  Using this method, the options are color by vertex (true)
        // or flat color (false).
        fmanager.setColorMode(false);
        
        // Here we use a static method in RelativeFLine to create a collection of FieldLines quickly.
        Collection fls = RelativeFLine.createLines(pcharge, teal.field.Field.E_FIELD, 8, false);
        fmanager.setFieldLines(fls);
        
        fls = RelativeFLine.createLines(conductingShell, teal.field.Field.E_FIELD, 8, false);
        fmanager.addFieldLines(fls);
        fmanager.setColorMode(false);
    
       
        ///// INITIALIZATION OF GUI ELEMENTS /////
        
        // Here we create a slider to control the yposition of the PointCharge.

        // setMinimum() sets the minimum value of the slider.
        rposPCslider.setMinimum(0.0);
        // setMaximum() sets the maximum value of the slider.
        rposPCslider.setMaximum(20.);
        // setPaintTicks() determines whether or not tick marks should be drawn on the slider.
        rposPCslider.setPaintTicks(true);
        // setValue() sets the current value of the slider.  Use this to set the slider's intial value.
        rposPCslider.setValue(5.);
        // setText() sets the label text for this slider.
        rposPCslider.setText("radius pc");
        // set property change listener on this slider
        rposPCslider.addPropertyChangeListener("value", this);
        
        // Here we create a slider to control the xposition of the PointCharge.
        angPCslider.setMinimum(0.);
        angPCslider.setMaximum(360.);
        angPCslider.setPaintTicks(true);
        angPCslider.setValue(0.);
        angPCslider.setText("angle pc");

        angPCslider.addPropertyChangeListener("value", this);

        // A second slider is created the same way as the first, except that it is assigned to change the outer radius of
        // the conducting shell 
        
        RadiusCSslider.setMinimum(0);
        RadiusCSslider.setMaximum(10);
        RadiusCSslider.setPaintTicks(true);
        RadiusCSslider.setBounds(35, 648, 415, 50);
        RadiusCSslider.setValue(5.);
        RadiusCSslider.setText("Shell radius");
        RadiusCSslider.addRoute("value", conductingShell, "radius");;

        
        // a third slider is created the same way, except that it is assigned to change the thickness
        // of the conducting shell

        thicknessCSslider.setMinimum(1);
        thicknessCSslider.setMaximum(3);
        thicknessCSslider.setPaintTicks(true);
        thicknessCSslider.setBounds(35, 648, 415, 50);
        
        thicknessCSslider.setValue(2.);
        thicknessCSslider.setText("Shell thickness");
        thicknessCSslider.addRoute("value", conductingShell, "thickness");
        
        
        // a fourth slider is created the same way, except that it is assigned to change the thickness
        // of the conducting shell

        radiusPCslider.setMinimum(0);
        radiusPCslider.setMaximum(2);
        radiusPCslider.setPaintTicks(true);
        radiusPCslider.setBounds(35, 648, 415, 50);
        
        radiusPCslider.setValue(.6);
        radiusPCslider.setText("radius pc");
        radiusPCslider.addRoute("value", pcharge, "radius");

        changeSignCCButton = new JButton(new TealAction("Change Sign Free Charge", "Change Sign Free Charge", this));
        changeSignCCButton.setFont(changeSignCCButton.getFont().deriveFont(Font.BOLD));
        changeSignCCButton.setBounds(40, 600, 195, 24);

        
        // Here we create a "Parameters" Group and add the sliders to it.  A Group is a sub-panel of the GUI that can be
        // minimized, etc..
        controls = new ControlGroup();
        controls.setText("Parameters");
   //     controls.add(changeSignCCButton);
        controls.add(rposPCslider);
        controls.add(angPCslider);
 
   //     controls.add(RadiusCSslider);
  //      controls.add(thicknessCSslider);
  //      controls.add(radiusPCslider);
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
        

        // Final initializations
        mSEC.init();
        // hide run controls
        mSEC.rebuildPanel(0);
        addActions();
        resetCamera();
        PlaceInducedCharges();

    }
    
    // This method is called whenever a propertyChangeEvent is received, if the app is registered as a listener.
    public void propertyChange(PropertyChangeEvent pce) {
        Object source = pce.getSource();
        if (source == rposPCslider) {
            double rposPC01 = ((Double) pce.getNewValue()).doubleValue();
            Vector3d currentPosition = new Vector3d(0.,0.,0.);
            currentPosition = pcharge.getPosition();
            double radcurrent = currentPosition.length();
          //  nativeObject01.setNode3D(ShapeNodeNative01);
            if (rposPC01 ==0.) rposPC01=.001;
            double xposPC01 = rposPC01*currentPosition.x/radcurrent;
            double yposPC01 = rposPC01*currentPosition.y/radcurrent;
            pcharge.setPosition(new Vector3d(xposPC01,yposPC01, 0.));
            conductingShell.setFreeChargePosition(new Vector3d(xposPC01,yposPC01, 0.));
            PlaceInducedCharges();
        } else {
            super.propertyChange(pce);
        }
        if (source == angPCslider) {
            double angPC01 = ((Double) pce.getNewValue()).doubleValue();
            anglePC = angPC01;
            Vector3d currentPosition = new Vector3d(0.,0.,0.);
            currentPosition = pcharge.getPosition();
            double rposPC01 = currentPosition.length();
          //  nativeObject01.setNode3D(ShapeNodeNative01);
            double xposPC01 = rposPC01*Math.cos(angPC01*Math.PI/180.);
            double yposPC01 = rposPC01*Math.sin(angPC01*Math.PI/180.);
            pcharge.setPosition(new Vector3d(xposPC01,yposPC01, 0.));
            conductingShell.setFreeChargePosition(new Vector3d(xposPC01,yposPC01, 0.));
            PlaceInducedCharges();
        } else {
            super.propertyChange(pce);
        }
        if (source == thicknessCSslider) {
            double thickness01 = ((Double) pce.getNewValue()).doubleValue();
          //  nativeObject01.setNode3D(ShapeNodeNative01);
            conductingShell.setThickness(thickness01);
        } else {
            super.propertyChange(pce);
        }
    }
    // This method resets the camera transform to it's initial state.
    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 1.2), new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.));
    }
    // This method is called whenever the simulation's "reset" button is pressed.  It should reset all the simulation
    // objects and parameters to their initial states.
    public void reset() {
        super.reset();
        pcharge.setPosition(new Vector3d(8, 0, 0));
        conductingShell.setPosition(new Vector3d(0, 0, 0));
        conductingShell.setFreeChargePosition(new Vector3d(8, 0, 0));
        //if(theEngine != null)
        //	theEngine.refresh();
    }

    // This method adds ActionEvent triggers to the top menus.  In this case, we add an Action to the Help menu to open the
    // help file for this simulation.  Here we just define the trigger.  
    void addActions() {
        TealAction ta = new TealAction("Shielding", this);
        addAction("Help", ta);
        

    }
    // This method is called when an ActionEvent is received.  Here we tell the application to launch the Help file when
    // the Help ActionEvent defined above is received.
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Shielding") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/shielding.html");
        	}
        }
        else if(e.getSource() == rad1){
        	pcharge.setCharge(2.5);
        	conductingShell.setCharge(2.5);
        }
        else if(e.getSource() == rad2){
        	pcharge.setCharge(2.5);
        	conductingShell.setCharge(0.000000001); // don't set it exactly to zero to keep coloring of shell
        }
        else if(e.getSource() == rad3){
           	pcharge.setCharge(0.);
        	conductingShell.setCharge(2.5);
        }
        else if (e.getActionCommand().compareToIgnoreCase("Change Sign Free Charge") == 0) {
           	signFreeCharge = -signFreeCharge;
           	System.out.println(" here's Johnny!!!!!" + signFreeCharge);
            PlaceInducedCharges();
            }
        
        else {
            super.actionPerformed(e);
        }
    }
    
    public void PlaceInducedCharges() {
    	 Vector3d PosFreeCharge = new Vector3d(0.,0.,0.);
    	 PosFreeCharge = conductingShell.getFreeChargePosition();
    	 double radiusfree = PosFreeCharge.length();
    	 double fraction = 0.;
    	 if (radiusfree < radius-thickness) {
    		 // here we treat the case where the free charge is inside the inner radius of the conducting shell
    		 double ratio = radiusfree/radius;
    		 double xsphere, ysphere;
    		 double xsphere1, ysphere1;
    	     System.out.println(" radius  "+ radius  + " radiusfree "+ radiusfree );
          // first place charges on outer surface, uniformly with the same sign as the free charge
    	     for (int i = 0; i<2*Ninduced; i++){
    	    	 // here we place the 2*Ninduced charges spheres on the right hand side of the sphere
    	    	 double anglei = i*180./(2.*Ninduced-1.);
    	    	 ysphere = (radius-radiusInduced)*Math.cos(anglei*Math.PI/180.);
    	    	 xsphere = (radius-radiusInduced)*Math.sin(anglei*Math.PI/180.);
    	    	
    	    	 InducedChargeSpheresOuter[i].setPosition(new Vector3d(xsphere,ysphere,0.));
    	    	 if (signFreeCharge > 0.) InducedChargeSpheresOuter[i].setColor(Teal.PointChargePositiveColor);
    	    	 else InducedChargeSpheresInner[i].setColor(Teal.PointChargeNegativeColor);
    	    	 InducedChargeSpheresOuter[i].setDrawn(true);
    	    	 // now do the left hand side
    	    	 xsphere = -xsphere;
    	    	
    	    	 InducedChargeSpheresOuter[i+2*Ninduced].setPosition(new Vector3d(xsphere,ysphere,0.));
    	    	 if (signFreeCharge > 0.) InducedChargeSpheresOuter[i+2*Ninduced].setColor(Teal.PointChargePositiveColor);
    	    	 else InducedChargeSpheresOuter[i+2*Ninduced].setColor(Teal.PointChargeNegativeColor);
    	    	 InducedChargeSpheresOuter[i+2*Ninduced].setDrawn(true);
    	     }
    	     
    	     // now place the charges on the inner surface of the shell
    	     
    	     for (int i = 0; i<2*Ninduced; i++){
    	    	 // here we place the 2*Ninduced charges spheres on the right hand side of the sphere
    	    	 fraction = -1.*(i+1)/(2.*Ninduced+1);
    	    	 double guess = 0.;
    	    	 double newguess = 0.;
    	    	 double fn = 0.;
    	    	 double fn1  =0.;
    	    	 double derivative = 0.;
    	    	 for (int j = 0; j<12; j++) {
    	    		 guess = newguess;
	    	    	 fn1 =.5*(ratio*ratio-1.)*(1./Math.sqrt(1.-2.*ratio*guess+ratio*ratio)-1./(1.+ratio))/ratio;
	    	    	 fn = fn1 - fraction;
	    	    	 derivative = (ratio*ratio-1.)/Math.pow((1.-2.*ratio*guess+ratio*ratio), 1.5);
	    	    	 newguess = guess -fn/derivative;
	    	    	 if(newguess < -1.)newguess = -.99999;
	    	    	 if(newguess > 1.)newguess = .99999;
	    	    	// System.out.println(" fraction "+fraction+" guess "+guess+" newguess "+newguess);
	    	    	// System.out.println(" fn1 "+ fn1 +" derivative "+ derivative);
    	    	 }
    	    	 ysphere = (radius-thickness+radiusInduced)*Math.sqrt(1.-newguess*newguess);
    	    	 xsphere = (radius-thickness+radiusInduced)*newguess;
    	    	 xsphere1 = xsphere*Math.cos(anglePC*Math.PI/180.) - ysphere*Math.sin(anglePC*Math.PI/180.);
    	    	 ysphere1 = ysphere*Math.cos(anglePC*Math.PI/180.) + xsphere*Math.sin(anglePC*Math.PI/180.);
    	    	 InducedChargeSpheresInner[i].setPosition(new Vector3d(xsphere1,ysphere1,0.));
    	    	 if (signFreeCharge > 0.) InducedChargeSpheresInner[i].setColor(Teal.PointChargeNegativeColor);
    	    	 else InducedChargeSpheresInner[i].setColor(Teal.PointChargePositiveColor);
    	    	 InducedChargeSpheresInner[i].setDrawn(true);
    	    	 // now do the left hand side
    	    	 ysphere = -ysphere;
    	    	 xsphere1 = xsphere*Math.cos(anglePC*Math.PI/180.) - ysphere*Math.sin(anglePC*Math.PI/180.);
    	    	 ysphere1 = ysphere*Math.cos(anglePC*Math.PI/180.) + xsphere*Math.sin(anglePC*Math.PI/180.);
    	    	 InducedChargeSpheresInner[i+2*Ninduced].setPosition(new Vector3d(xsphere1,ysphere1,0.));
    	    	 if (signFreeCharge > 0.) InducedChargeSpheresInner[i+2*Ninduced].setColor(Teal.PointChargeNegativeColor);
    	    	 else InducedChargeSpheresInner[i+2*Ninduced].setColor(Teal.PointChargePositiveColor);
    	    	 InducedChargeSpheresInner[i+2*Ninduced].setDrawn(true);
    	     }
    	  
    	 } 
    	 	 if (radiusfree >= radius) {
        		 // here we treat the case where the free charge is external to the conducting shell
        		 double ratio = radiusfree/radius;
        		 // the parameter below is the cosine of the angle at which the induced charge density on
        		 // the outer surface of the shell goes to zero
        		 double inter = Math.pow(ratio*(ratio*ratio-1.), 2./3.);
        		 double coszero = .5*(ratio*ratio+1.-inter)/ratio;
        		 double angzero = Math.acos(coszero)*180./Math.PI;
        		 double xsphere1, ysphere1;
        		 double qincmax = .5*(1.+coszero)-.5*(ratio*ratio-1.)*(1./Math.sqrt(1.-2.*ratio*coszero+ratio*ratio)-1./(1.+ratio));
        	    // System.out.println(" radius  "+ radius  + " radiusfree "+ radiusfree );
        	    // System.out.println(" ratio "+ ratio +" coszero " + coszero + " qincmax "+ qincmax);
        	     for (int i = 0; i<Ninduced; i++){
        	    	 // here we place the first Ninduced positive charges spheres on the right hand side of the sphere
        	    	 double anglei = 180.-i*(180.-angzero)/Ninduced;
        	    	 double ysphere = (radius-radiusInduced)*Math.sin(anglei*Math.PI/180.);
        	    	 double xsphere = (radius-radiusInduced)*Math.cos(anglei*Math.PI/180.);
        	    	 xsphere1 = xsphere*Math.cos(anglePC*Math.PI/180.) - ysphere*Math.sin(anglePC*Math.PI/180.);
        	    	 ysphere1 = ysphere*Math.cos(anglePC*Math.PI/180.) + xsphere*Math.sin(anglePC*Math.PI/180.);
        	    	 InducedChargeSpheresOuter[i].setPosition(new Vector3d(xsphere1,ysphere1,0.));
        	    	 if (signFreeCharge > 0.) InducedChargeSpheresOuter[i].setColor(Teal.PointChargePositiveColor);
        	    	 else InducedChargeSpheresOuter[i].setColor(Teal.PointChargeNegativeColor);
        	    	 InducedChargeSpheresOuter[i].setDrawn(true);
        	    	 InducedChargeSpheresInner[i].setDrawn(false);
        	    	 // now do the left hand side
        	    	 ysphere = -ysphere;
        	    	 xsphere1 = xsphere*Math.cos(anglePC*Math.PI/180.) - ysphere*Math.sin(anglePC*Math.PI/180.);
        	    	 ysphere1 = ysphere*Math.cos(anglePC*Math.PI/180.) + xsphere*Math.sin(anglePC*Math.PI/180.);
        	    	 InducedChargeSpheresOuter[i+Ninduced].setPosition(new Vector3d(xsphere1,ysphere1,0.));
        	    	 if (signFreeCharge > 0.) InducedChargeSpheresOuter[i+Ninduced].setColor(Teal.PointChargePositiveColor);
        	    	 else InducedChargeSpheresOuter[i+Ninduced].setColor(Teal.PointChargeNegativeColor);
        	    	 InducedChargeSpheresOuter[i+Ninduced].setDrawn(true);
        	    	 InducedChargeSpheresInner[i+Ninduced].setDrawn(false);
        	     }
        	     // now do the negative induced charges 
        	     for (int i = 0; i<Ninduced; i++){
        	    	 double anglei = i*(angzero)/Ninduced;
        	    	 double ysphere = (radius-radiusInduced)*Math.sin(anglei*Math.PI/180.);
        	    	 double xsphere = (radius-radiusInduced)*Math.cos(anglei*Math.PI/180.);
        	    	 xsphere1 = xsphere*Math.cos(anglePC*Math.PI/180.) - ysphere*Math.sin(anglePC*Math.PI/180.);
        	    	 ysphere1 = ysphere*Math.cos(anglePC*Math.PI/180.) + xsphere*Math.sin(anglePC*Math.PI/180.);
        	    	 if (signFreeCharge > 0.) InducedChargeSpheresOuter[i+3*Ninduced].setColor(Teal.PointChargeNegativeColor);
        	    	 else InducedChargeSpheresOuter[i+3*Ninduced].setColor(Teal.PointChargePositiveColor);
        	    	 if (signFreeCharge > 0.) InducedChargeSpheresOuter[i+2*Ninduced].setColor(Teal.PointChargeNegativeColor);
        	    	 else InducedChargeSpheresOuter[i+2*Ninduced].setColor(Teal.PointChargePositiveColor);
        	    	 InducedChargeSpheresOuter[i+2*Ninduced].setPosition(new Vector3d(xsphere1,ysphere1,0.));
        	    	 InducedChargeSpheresOuter[i+2*Ninduced].setDrawn(true);
        	    	 InducedChargeSpheresInner[i+2*Ninduced].setDrawn(false);
        	    	 ysphere = -ysphere;
        	    	 xsphere1 = xsphere*Math.cos(anglePC*Math.PI/180.) - ysphere*Math.sin(anglePC*Math.PI/180.);
        	    	 ysphere1 = ysphere*Math.cos(anglePC*Math.PI/180.) + xsphere*Math.sin(anglePC*Math.PI/180.);
        	    	 // now do charges of left hand side
        	    	 InducedChargeSpheresOuter[i+3*Ninduced].setPosition(new Vector3d(xsphere1,ysphere1,0.));
        	    	 InducedChargeSpheresOuter[i+3*Ninduced].setDrawn(true);
        	    	 InducedChargeSpheresInner[i+3*Ninduced].setDrawn(false);
        	     }
    	  
    	 
    	 }
    	 	 if (radiusfree >= radius-thickness && radiusfree < radius) {
        		 // here we treat the case where the free charge is inside the conducing shell itself
        		 double xsphere, ysphere;
        	     System.out.println(" radius  "+ radius  + " radiusfree "+ radiusfree );
              // first place charges on outer surface, uniformly with the same sign as the free charge
        	     for (int i = 0; i<2*Ninduced; i++){
        	    	 // here we place the 2*Ninduced charges spheres on the right hand side of the sphere
        	    	 double anglei = i*180./(2.*Ninduced-1.);
        	    	 ysphere = (radius-radiusInduced)*Math.sin(anglei*Math.PI/180.);
        	    	 xsphere = (radius-radiusInduced)*Math.cos(anglei*Math.PI/180.);
        	    	 InducedChargeSpheresOuter[i].setPosition(new Vector3d(xsphere,ysphere,0.));
        	    	 if (signFreeCharge > 0.) InducedChargeSpheresOuter[i].setColor(Teal.PointChargePositiveColor);
        	    	 else InducedChargeSpheresInner[i].setColor(Teal.PointChargeNegativeColor);
        	    	 InducedChargeSpheresOuter[i].setDrawn(true);
        	    	 // now do the left hand side
        	    	 ysphere = -ysphere;
        	    	 InducedChargeSpheresOuter[i+2*Ninduced].setPosition(new Vector3d(xsphere,ysphere,0.));
        	    	 if (signFreeCharge > 0.) InducedChargeSpheresOuter[i+2*Ninduced].setColor(Teal.PointChargePositiveColor);
        	    	 else InducedChargeSpheresOuter[i+2*Ninduced].setColor(Teal.PointChargeNegativeColor);
        	    	 InducedChargeSpheresOuter[i+2*Ninduced].setDrawn(true);
        	     }
        	    // now place the charges around the free charge with the opposite sign 
        	     for (int i = 0; i<2*Ninduced; i++){
        	    	 // here we place the 2*Ninduced charges spheres on the right hand side of the sphere
        	    	 double anglei = i*180./(2.*Ninduced-1.);
        	    	 Vector3d pospointcharge = new Vector3d(0.,0.,0.);
        	    	 pospointcharge = pcharge.getPosition();
        	    	 ysphere = pospointcharge.y+(radiusInduced+radiusPointCharge)*Math.sin(anglei*Math.PI/180.);
        	    	 xsphere = pospointcharge.x + (radiusInduced+radiusPointCharge)*Math.cos(anglei*Math.PI/180.);
        	    	 InducedChargeSpheresInner[i].setPosition(new Vector3d(xsphere,ysphere,0.));
        	    	 if (signFreeCharge > 0.) InducedChargeSpheresInner[i].setColor(Teal.PointChargeNegativeColor);
        	    	 else InducedChargeSpheresInner[i].setColor(Teal.PointChargePositiveColor);
        	    	 InducedChargeSpheresInner[i].setDrawn(true);
        	    	 // now do the left hand side
        	    	 ysphere = pospointcharge.y-(radiusInduced+radiusPointCharge)*Math.sin(anglei*Math.PI/180.);;
        	    	 InducedChargeSpheresInner[i+2*Ninduced].setPosition(new Vector3d(xsphere,ysphere,0.));
        	    	 if (signFreeCharge > 0.) InducedChargeSpheresInner[i+2*Ninduced].setColor(Teal.PointChargeNegativeColor);
        	    	 else InducedChargeSpheresInner[i+2*Ninduced].setColor(Teal.PointChargePositiveColor);
        	    	 InducedChargeSpheresInner[i+2*Ninduced].setDrawn(true);
        	     }
    	 	 }
    }

}
