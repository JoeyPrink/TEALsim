/* $Id: GaussLawFlux.java,v 1.14 2010/09/22 15:48:11 pbailey Exp $ */
/**
 * @author John Belcher - Department of Physics / MIT
 * @version $Revision: 1.14 $
 */

package tealsim.physics.em;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.media.j3d.Transform3D;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.vecmath.*;

import teal.config.Teal;
import teal.core.AbstractElement;
import teal.core.HasReference;
import teal.core.TElement;
import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.render.BoundingSphere;
import teal.render.Rendered;
import teal.render.primitives.Cylinder;
import teal.render.primitives.Sphere;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.primitives.Line;
import teal.render.viewer.SelectEvent;
import teal.render.viewer.SelectListener;
import teal.sim.TSimElement;
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.TEngineControl;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldVector;
import teal.sim.spatial.GeneralVector;
import teal.sim.spatial.RelativeFLine;
import teal.sim.spatial.SpatialTextLabel;
import teal.math.RectangularPlane;
import teal.physics.em.PointCharge;
import teal.physics.em.InfiniteLineCharge;
import teal.physics.em.SimEM;
import teal.physics.physical.PhysicalObject;
import teal.physics.physical.Wall;
import teal.ui.UIPanel;
import teal.ui.control.*;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;


/** An application to show the normals and electric fields on the surface of a 
 * closed surface to elucidate the geometric concepts behind Gauss's Law.  
 *  
 * @author John Belcher
 * @version 1.0 
 * */

public class GaussLawFlux extends SimEM implements SelectListener {

    private static final long serialVersionUID = 3257008735204554035L;
    /** Base for the flux meter. */
    /** Logical value for whether sphere is visible. */
    VisualizationControl vis;
    boolean sphereVisible = false;;
    /** Logical value for whether cylinder is visible. */
    boolean cylinderVisible = false;
    /** Logical value for whether there are any charges in the scene. */
    boolean chargesPresent = true;
    Wall base;
    /** Text label for the flux meter.*/
	SpatialTextLabel lbl;
    /** The approximate width of the canvas in the standard view. */
    double widthtotal = 6.;
    /** The scale factor for the electric field vectors. */
    double arrowScale = .5;
    /** The scale factor for the normal  vectors. */
    double arrowScaleNormal = .3;
    Vector3d arrowVectorScale = new Vector3d(1.,.1,1.);
    /** A TEALsim native object for the Gaussian sphere.  */
    Sphere GaussianSphere = null;
    /** A TEALsim native object for the flux through the Gaussian sphere.  */
    Cylinder GaussianSphereFlux = null;
    /** Vector for the initial position of the cylinder. */
    Vector3d posGSphere = null;
    /** The radius of the gaussian sphere.  */
    double radiusGSphere = 1.;
    /** The height of the cylinder for the flux due to one unit of charge. */
    double oneUnitFlux = .5;
    /** The height of the cylinder representing the flux through the gaussian sphere.  */
    double heightGSphereFlux = oneUnitFlux;
    /** The radius of the cylinder representing the flux through the gaussian sphere.  */
    double radiusGSphereFlux = .5;
    /** The side of the square representing the base of the flux meter.  */
    double sizebase = 1.2;
    /** A TEALsim native object for the Gaussian cylinder.  */
    Cylinder GaussianCylinder = null;
  
    /** A TEALsim native object for the flux through the Gaussian cylinder.  */
    Cylinder GaussianCylinderFlux = null;
   
    /** The height of the cylinder representing the flux through the gaussian sphere.  */
    double heightGCylinderFlux = oneUnitFlux;
    /** The radius of the cylinder representing the flux through the gaussian sphere.  */
    double radiusGCylinderFlux = .5;
    /** Slider for the y-position of the cylinder/sphere.  */
    PropertyDouble posSlider_y = new PropertyDouble();
    /** Slider for the x-position of the cylinder/sphere.  */
    PropertyDouble posSlider_x = new PropertyDouble();
    /** Slider for the rotation angle of the cylinder/sphere.  */
    PropertyDouble angGCylinder = new PropertyDouble();
    /** Vector for the initial position of the cylinder. */
    Vector3d posGCylinder = null;
    /** Vector for the initial position of the cylinder representing flux through the sphere. */
    Vector3d posGCylinderFlux = null;
    /** Vector for the initial position of the cylinder representing flux through the cylinder. */
    Vector3d posGSphereFlux = null;
    /** Source of electric field in scene.    */
    PointCharge pc;
    /** Another source of electric field in scene.    */
    PointCharge pc1;
    /** Another source of electric field in scene.    */
    PointCharge pc2;
    /** The electric field vectors on the sphere. */
    FieldVector[][] theFieldSphere;

    /** The normal vectors on the sphere. */
    GeneralVector[][] theNormalSphere;
    /** The electric field vectors on the top of the cylinder. */
    FieldVector[][] theFieldCylinderTop;
    /** The electric field vectors on the bottom of the cylinder. */
    FieldVector[][] theFieldCylinderBottom;
    /** The electric field vectors on the sides of the cylinder. */
    FieldVector[][] theFieldCylinderSides;
    /** The electric field vectors on the top of the cylinder. */
    GeneralVector[][] theNormalCylinderTop;
    /** The electric field vectors on the bottom of the cylinder. */
    GeneralVector[][] theNormalCylinderBottom;
    /** The electric field vectors on the sides of the cylinder. */
    GeneralVector[][] theNormalCylinderSides;
    /** The radius of the gaussian cylinder.  */
    double radiusGCylinder = .5;
    /** The height of the gaussian cylinder.  */
    double heightGCylinder = 2.;
    /** The angle from the x axis in the xy plane of the gaussian cylinder. */
    double angleGCylinder = 0.;
    /** The number of radial nodes for the electric field vectors on the top and bottom of the cylinder. */
    int numRadCylinder = 1;
    /** The number of azimuthal nodes for the electric field vectors on the top and bottom of the cylinder.  */
    int numAziTopCylinder = 4;
    /** The number of azimuthal nodes for the electric field vectors on the sides of the cylinder. */
    int numAziSidesCylinder = 6;
    /** The number of azimuth angle nodes on the sphere. */
    int numAziSphere = 8;
    /** The number of polar angle nodes on the sphere. */
    int numThetaSphere = 6;
    /** The number of z nodes for the electric field vectors on the sides the cylinder.  */
    int numZSidesCylinder = 4;
    /** UI panel for controls */
	UIPanel GaussianControls;
	/** ButtonGroup for the two choices of Gaussian surfaces.*/
	ButtonGroup optionsGroup;
	/** Radio button for the Cylindrical Gaussian surface.*/
	JRadioButton rad1;
	/** Radio button for the Spherical Gaussian surface.*/
    JRadioButton rad2;       
    
  //  private static final long serialVersionUID = 3258133535633258035L;
    JButton but = null;
    JButton but1 = null;
    JButton but2 = null;
    JButton but3 = null;
    JButton but4 = null;
    JButton but5 = null;
    JButton but6 = null;
    PropertyInteger slider1 = null;
    
    // Used to distinguish application-issued slider changes, from user-issued ones:
    boolean fromApplication = false;

    File curDir = null;
    JFileChooser fc = null;
    
    protected FieldConvolution mDLIC = null;
    protected FieldDirectionGrid fv = null;
    
    ControlGroup params;
    ControlGroup gaussianControl;

    final private int N = 10;
    /*
    private PointCharge[] pointCharges = new PointCharge[N];
    private PointCharge newCharge01;
    private PointCharge newCharge02;
     */

    private ArrayList selectList = new ArrayList();

    Vector3d[] positions = { new Vector3d(-0.2, 0.25, 0.), new Vector3d(0., 0.9, 0.), new Vector3d(0., -0.95, 0.),
            new Vector3d(0.95, 0., 0.), new Vector3d(-1.2, 0., 0.), new Vector3d(0.4, 0.45, 0.),
            new Vector3d(0.75, -0.8, 0.), new Vector3d(-0.75, 0.75, 0.), new Vector3d(-0.7, -0.4, 0.),
            new Vector3d(0.25, -0.2, 0.), };

    // point charge charges:
    double[] charges = { 1., 1., 1., 1., 1., -1., -1., -1., -1., -1. };
    
    public GaussLawFlux() {
        super();
        TDebug.setGlobalLevel(0);
        title = "Gauss's Law Flux";
        setShowGizmos(false);
        slider1 = new PropertyInteger();
        slider1.setMinimum(-5);
        slider1.setMaximum(5);
        slider1.setBounds(40, 535, 415, 50);
        slider1.setPaintTicks(true);
        slider1.addPropertyChangeListener("value", this);
        slider1.setValue(0);
        slider1.setText("Set Charge");
        slider1.setBorder(null);
        //addElement(slider1);
        //slider1.setEnabled(false);
        mDLIC = new FieldConvolution();
        mDLIC.setSize(new Dimension(512, 512));
        mDLIC.setComputePlane(new RectangularPlane(new BoundingSphere(new Point3d(), 3.)));
        //JTaskPane tp;
        
        //tp = new JTaskPane();
        params = new ControlGroup();
        params.setText("Charge Control Panel");
     //   params.add(slider1);
        addElement(params);
        setCursorOnDrag(false);

 //  create source of electric field in scene, here two point charges
        pc = new PointCharge();
     //   addElement(pc);
        pc.setPosition(new Vector3d(.5,0.,0.));
        pc.setCharge(1.);
        pc.setRadius(.2);
        pc.setDrawn(true);
        pc1 = new PointCharge();
        pc1.setPosition(new Vector3d(-.5,0.,0.));
        pc1.setCharge(-1.);
        pc1.setRadius(.2);
        pc1.setDrawn(true);
    //    addElement(pc1);
        pc2 = new PointCharge();
        pc2.setPosition(new Vector3d(-.8,0.,0.));
        pc2.setCharge(-1.);
        pc2.setRadius(.2);
        pc2.setDrawn(true);
    //    addElement(pc2);

       
    // create the Gaussian Sphere using teal.render.geometry and add them to the scene
            posGSphere = new Vector3d(0.,0.,0);
            GaussianSphere = new Sphere(radiusGSphere);
            GaussianSphere.setColor(new Color(0, 0, 170));
            GaussianSphere.getMaterial().setTransparancy(0.8f);
            GaussianSphere.setPosition(posGSphere);
            GaussianSphere.setDirection(new Vector3d(0.,1.,0.));
            GaussianSphere.setDrawn(false);
          //  GaussianSphere.setMoveable(true);
           // GaussianSphere.setPickable(true);
          //  GaussianSphere.setSelectable(true);
            addElement(GaussianSphere);
        
        //  create the electric field and normal vectors on the sphere  
            theFieldSphere = new FieldVector[numAziSphere][numThetaSphere];
            theNormalSphere = new GeneralVector[numAziSphere][numThetaSphere];
            for (int j = 0; j < numThetaSphere; j++) {
            	for (int i = 0; i < numAziSphere; i++) {
    	     		theFieldSphere[i][j] = new FieldVector();
    	     		theFieldSphere[i][j].setPosition(new Vector3d(0,0,0));
    	     		theFieldSphere[i][j].setColor(Teal.PointChargePositiveColor);
    	     		theFieldSphere[i][j].setArrowScale(arrowScale);
    	     		theFieldSphere[i][j].setDrawn(false);
    	     		addElement(theFieldSphere[i][j]);
    	     		theNormalSphere[i][j] = new GeneralVector();
    	     		theNormalSphere[i][j].setPosition(new Vector3d(0,0,0));
    	     		theNormalSphere[i][j].setColor(new Color(0, 0, 170));
    	     		theNormalSphere[i][j].setArrowScale(arrowScaleNormal);
    	     		theNormalSphere[i][j].setColor(Color.gray);
    	     		theNormalSphere[i][j].setDrawn(false);
    	     		addElement(theNormalSphere[i][j]);
            	} 	
            }
            
         // create the Cylinder representing the flux through the sphere using teal.render.geometry and add it to the scene
            Vector3d posGSphereFluxBase;
            posGSphereFluxBase =  new Vector3d(2.,-2.5,0);

            posGSphereFlux=  new Vector3d(2.,-2.5,0);
            Vector3d posGSphereFluxLabel  = new Vector3d(1.,-2.8,0);
//            ShapeNodeGSphereFlux.setGeometry(Cylinder.makeGeometry(32, radiusGSphereFlux, heightGSphereFlux));
//            ShapeNodeGSphereFlux.setTransparency(0.3f);
            GaussianSphereFlux = new Cylinder( radiusGSphereFlux, heightGSphereFlux);
            GaussianSphereFlux.setColor(new Color(255, 0, 0));
            GaussianSphereFlux.getMaterial().setTransparancy(0.3f);
            GaussianSphereFlux.setPosition(posGSphereFlux);
            GaussianSphereFlux.setDirection(new Vector3d(0.,1.,0.));
            GaussianSphereFlux.setDrawn(false);
            addElement(GaussianSphereFlux);
            
    		lbl = new SpatialTextLabel(" What is this? ", posGSphereFluxLabel);
    		lbl.setBaseScale(0.2);
    		lbl.setPositionOffset(new Vector3d(0.05, 0.0, 0.));
    		lbl.setRefDirectionOffset(0.5);
    		lbl.setUseDirectionOffset(true);
    		addElement(lbl);
            
    	// create the base for the flux meter       
            base = new Wall(posGSphereFluxBase,new Vector3d(sizebase, 0, 0.), new Vector3d(0, 0., sizebase));
            addElement(base);
            
// create the Gaussian Cylinder using teal.render.geometry and add them to the scene
        posGCylinder = new Vector3d(0.,0.,0);
//        ShapeNodeGCylinder.setGeometry(Cylinder.makeGeometry(32, radiusGCylinder, heightGCylinder));
//        ShapeNodeGCylinder.setTransparency(0.8f);
        GaussianCylinder = new Cylinder(radiusGCylinder, heightGCylinder);
        GaussianCylinder.setColor(new Color(0, 0, 170));
        GaussianCylinder.getMaterial().setTransparancy(0.8f);
        GaussianCylinder.setPosition(posGCylinder);
        GaussianCylinder.setDirection(new Vector3d(0.,1.,0.));
        GaussianCylinder.setDrawn(false);
        addElement(GaussianCylinder);
        
//  create the electric field and normal vectors on the top and bottom of the cylinder  
        theFieldCylinderTop = new FieldVector[numAziTopCylinder][numRadCylinder];
        theFieldCylinderBottom = new FieldVector[numAziTopCylinder][numRadCylinder];
        theNormalCylinderTop = new GeneralVector[numAziTopCylinder][numRadCylinder];
        theNormalCylinderBottom = new GeneralVector[numAziTopCylinder][numRadCylinder];
        for (int j = 0; j < numRadCylinder; j++) {
        	for (int i = 0; i < numAziTopCylinder; i++) {
	     		theFieldCylinderTop[i][j] = new FieldVector();
	     		theFieldCylinderTop[i][j].setPosition(new Vector3d(0,0,0));
	     		theFieldCylinderTop[i][j].setColor(Teal.PointChargePositiveColor);
	     		theFieldCylinderTop[i][j].setArrowScale(arrowScale);
	     		theFieldCylinderTop[i][j].setDrawn(false);
	     		addElement(theFieldCylinderTop[i][j]);
	     		theFieldCylinderBottom[i][j] = new FieldVector();
	     		theFieldCylinderBottom[i][j].setPosition(new Vector3d(0,0,0));
	     		theFieldCylinderBottom[i][j].setColor(Teal.PointChargePositiveColor);
	     		theFieldCylinderBottom[i][j].setArrowScale(arrowScale);
	     		theFieldCylinderBottom[i][j].setDrawn(false);
	            addElement(theFieldCylinderBottom[i][j]);
	     		theNormalCylinderTop[i][j] = new GeneralVector();
	     		theNormalCylinderTop[i][j].setPosition(new Vector3d(0,0,0));
	     		theNormalCylinderTop[i][j].setColor(Color.gray);
	     		theNormalCylinderTop[i][j].setArrowScale(arrowScaleNormal);
	     		theNormalCylinderTop[i][j].setDrawn(false);
	     		addElement(theNormalCylinderTop[i][j]);
	     		theNormalCylinderBottom[i][j] = new GeneralVector();
	     		theNormalCylinderBottom[i][j].setPosition(new Vector3d(0,0,0));
	     		theNormalCylinderBottom[i][j].setColor(Color.gray);
	     		theNormalCylinderBottom[i][j].setArrowScale(arrowScaleNormal);
	     		theNormalCylinderBottom[i][j].setDrawn(false);
	            addElement(theNormalCylinderBottom[i][j]);
        	} 	
        }
        
//  create the electric field vectors and normal vectors on the sides of the cylinder  
        theFieldCylinderSides = new FieldVector[numAziSidesCylinder][numZSidesCylinder];
        theNormalCylinderSides = new GeneralVector[numAziSidesCylinder][numZSidesCylinder];
        for (int j = 0; j < numZSidesCylinder; j++) {
        	for (int i = 0; i < numAziSidesCylinder; i++) {
	     		theFieldCylinderSides[i][j] = new FieldVector();
	     		theFieldCylinderSides[i][j].setPosition(new Vector3d(0,0,0));
	     		theFieldCylinderSides[i][j].setColor(Teal.PointChargePositiveColor);
	     		theFieldCylinderSides[i][j].setArrowScale(arrowScale);
	     		theFieldCylinderSides[i][j].setDrawn(false);
	        	Transform3D offsetTrans = new Transform3D();
	   //     	offsetTrans.setRotation(new AxisAngle4d(1.,0.,0.,Math.PI/2.));
	   // 		offsetTrans.setTranslation(new Vector3d(0., -1., 0.));
	    		theFieldCylinderSides[i][j].setModelOffsetTransform(offsetTrans);
	     		addElement(theFieldCylinderSides[i][j]);
	     		theNormalCylinderSides[i][j] = new GeneralVector();
	     		theNormalCylinderSides[i][j].setPosition(new Vector3d(0,0,0));
	     		theNormalCylinderSides[i][j].setArrowScale(arrowScaleNormal);
	     		theNormalCylinderSides[i][j].setDrawn(false);
	     		theNormalCylinderSides[i][j].setColor(Color.gray);
	     		addElement(theNormalCylinderSides[i][j]);
        	} 	
        }
        
        
        // create the Cylinder representing the flux thorugh the cylinder using teal.render.geometry and add it to the scene

//        ShapeNodeGCylinderFlux.setGeometry(Cylinder.makeGeometry(32, radiusGCylinderFlux, heightGCylinderFlux));
//        ShapeNodeGCylinderFlux.setTransparency(0.3f);
        GaussianCylinderFlux= new Cylinder(radiusGCylinderFlux, heightGCylinderFlux);
        GaussianCylinderFlux.setColor(new Color(255, 0, 0));
        GaussianCylinderFlux.getMaterial().setTransparancy(0.3f);
        GaussianCylinderFlux.setPosition(posGSphereFlux);
        GaussianCylinderFlux.setDirection(new Vector3d(0.,1.,0.));
        GaussianCylinderFlux.setDrawn(false);
        addElement(GaussianCylinderFlux);
        
        
        PlaceENVectors();
        GridBagLayout gbl =new GridBagLayout();
        GridBagConstraints con = new GridBagConstraints();
        con.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        GaussianControls = new UIPanel();
        GaussianControls.setLayout(gbl);
        // set radio buttons for choice of gaussian surface     
        UIPanel options = new UIPanel();
        options.setBorder(BorderFactory.createLineBorder(Color.black));
        options.setLayout(new GridLayout(3,1));
        optionsGroup = new ButtonGroup();
        rad1 = new JRadioButton("Gaussian Cylinder");        
        rad2 = new JRadioButton("Gaussian Sphere");       
        rad1.addActionListener(this);
        rad2.addActionListener(this);
		optionsGroup.add(rad1);
		optionsGroup.add(rad2);
		options.add(rad1);
		options.add(rad2);   
	    GaussianControls.add(options);
        //tp = new JTaskPane();
        gaussianControl= new ControlGroup();
        gaussianControl.setText("Gaussian Closed Surfaces");
     //   params.add(slider1);
   //     addElement(gaussianControl);
        setCursorOnDrag(false);
	   addElement(GaussianControls);
        addElement(params);
  //      JButton newBtn = new JButton("New game");
   //     newBtn.addActionListener(this);
  //      gbl.setConstraints(buttonGrid, con);
        
        
 // create the two sliders for the gaussian cylinder position    
        posSlider_x.setText("X Position");
        posSlider_x.setMinimum(-3.);
        posSlider_x.setMaximum(3.0);
        posSlider_x.setValue(0.);
        posSlider_x.setPaintTicks(true);
        posSlider_x.addPropertyChangeListener("value", this);
        
        posSlider_x.setVisible(true);
        
        posSlider_y.setText("Y Position ");
        posSlider_y.setMinimum(-3.);
        posSlider_y.setMaximum(3.0);
        posSlider_y.setValue(0.);
        posSlider_y.setPaintTicks(true);
        posSlider_y.addPropertyChangeListener("value", this);
        
        posSlider_y.setVisible(true);
        
// create the angle orientation slider for the cylinder, where angle is the angle from the x axis    
        angGCylinder.setText("Rotation Angle");
        angGCylinder.setMinimum(-180.);
        angGCylinder.setMaximum(180.0);
        angGCylinder.setValue(0.);
        angGCylinder.setPaintTicks(true);
        angGCylinder.addPropertyChangeListener("value", this);
        
        angGCylinder.setVisible(true);
       
 // add the sliders to the control group and add the control group to the scene
        ControlGroup controls = new ControlGroup();
        controls.setText("Gaussian Surface Position and Orientation");
        controls.add(posSlider_y);
        controls.add(posSlider_x);
        controls.add(angGCylinder);
        addElement(controls);
        vis = new VisualizationControl();
        
        vis.setText("Field Visualization");
        vis.setFieldConvolution(mDLIC);
        vis.setConvolutionModes(DLIC.DLIC_FLAG_E );
        vis.setFieldVisGrid(fv);
        addElement(vis);
      // add four lines to outline the xy plane 
        Outline();
     
// change some features of the lighting, background color, etc., from the default values, if desired
        setBackgroundColor(new Color(200,200,200));
        
// set parameters for mouseScale 
//        Vector3d mouseScale = getVpTranslateScale();
//        mouseScale.x *= 0.05;
//        mouseScale.y *= 0.05;
//        mouseScale.z *= 0.5;
//        setVpTranslateScale(mouseScale);
        
// set initial state
       // mSEC.init(); 
        if(theEngine != null)
        	theEngine.requestRefresh();
        mSEC.setVisible(true);
        mSEC.rebuildPanel(0);
        reset();
        resetCamera();
        // addAction for pulldown menus on TEALsim windows     
        addActions();

    }
    
    public void initialize(){
    	super.initialize();
    	// add one initial positive and one negative charge to begin with
        addElement(randomCharge(1.0, 2.0, 2.5, new Vector3d(0, 0, 0)));
        addElement(randomCharge(-1.0, 2.0, 2.5, new Vector3d(0, 0, 0)));
    	calculateFlux();
    	resetCamera();
    }

// add two items to the help menu, one to explain the simulation and the other to explain the 
// view and execution controls
  
    void addActions() {
      //  TealAction ta = new TealAction("Execution & View", this);
      //  addAction("Help", ta);
        TealAction tb = new TealAction("Gauss's Law", this);
        addAction("Help", tb);
        but2 = new JButton(new TealAction("Delete Selected", "delete", this));
        but2.setBounds(40, 650, 195, 24);
        //addElement(but2);

        but5 = new JButton(new TealAction("Delete All Charges", "delete_all", this));
        but5.setBounds(250, 650, 195, 24);
        //addElement(but5);

        but3 = new JButton(new TealAction("Add Random Positive (orange)", "random_positive", this));
        but3.setBounds(40, 690, 195, 24);
        //addElement(but3);

        but4 = new JButton(new TealAction("Add Random Negative (blue)", "random_negative", this));
        but4.setBounds(250, 690, 195, 24);
        //addElement(but4);
        
        but6 = new JButton(new TealAction("Toggle Fieldlines on Selected", "toggle_flines", this));
        but6.setBounds(40, 730, 195, 24);
        //addElement(but6);
        

  //      params.add(but2);

        params.add(but3);
        params.add(but4);
        params.add(but5);
        
    }

    public void Outline() {
        Line one = new Line(new Vector3d(-widthtotal/2.,-widthtotal/2., 0.), new Vector3d(-widthtotal/2.,widthtotal/2., 0.));
        one.setColor(Color.white);
        addElement(one);
        Line two = new Line(new Vector3d(-widthtotal/2.,-widthtotal/2., 0.), new Vector3d(widthtotal/2.,-widthtotal/2., 0.));
        two.setColor(Color.white);
        addElement(two);
        Line three = new Line(new Vector3d(widthtotal/2.,widthtotal/2., 0.), new Vector3d(-widthtotal/2.,widthtotal/2., 0.));
        three.setColor(Color.white);
        addElement(three);
        Line four = new Line(new Vector3d(widthtotal/2.,widthtotal/2., 0.), new Vector3d(widthtotal/2.,-widthtotal/2., 0.));
        four.setColor(Color.white);
        addElement(four);
    }
    
    public void actionPerformed(ActionEvent e) {
        TDebug.println(1, " Action comamnd: " + e.getActionCommand());
        if (e.getActionCommand().compareToIgnoreCase("Gauss's Law") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/gausslawflux.html");
        	}
        }
        else if (e.getActionCommand().compareToIgnoreCase("Execution & View") == 0) 
        {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/executionView.html");
        	}
        }
     else if (e.getActionCommand().compareToIgnoreCase("delete") == 0) {
        int simstate = mSEC.getSimState();
        mSEC.stop();
        if (selectList.size() != 0) {
            TDebug.println(0, "selectList.size() = " + selectList.size());
            //Iterator it = selectList.iterator();
            while (selectList.size() > 0) {

                Object myObject = selectList.get(0);
                TDebug.println(0, "Removing: " + ((AbstractElement) myObject).getID());
                removeFLinesFromObject((PhysicalObject) myObject);
                removeElement((TSimElement) myObject);

            }
            theEngine.requestRefresh();
            //selectList.clear();

        }
        //TDebug.println(0,"DElete button works!");
        if (simstate == TEngineControl.RUNNING) mSEC.start();
    } else if (e.getActionCommand().compareToIgnoreCase("random_positive") == 0) {
        addElement(randomCharge(1.0, 2.0, 2.5, new Vector3d(0, 0, 0)));
        chargesPresent = true;
        calculateFlux();
        MakeFieldVisible(true);
    } else if (e.getActionCommand().compareToIgnoreCase("random_negative") == 0) {
        addElement(randomCharge(-1.0, 2.0, 2.5, new Vector3d(0, 0, 0)));
        chargesPresent = true;
        calculateFlux();
        MakeFieldVisible(true);
    } else if (e.getActionCommand().compareToIgnoreCase("delete_all") == 0) {
        clearAllCharges();
        chargesPresent = false;
        MakeFieldVisible(false);
    } else if (e.getActionCommand().compareToIgnoreCase("toggle_flines") == 0) {
        TDebug.println(0, "action event toggle_flines!");
        Iterator it = selectList.iterator();
        while (it.hasNext()) {
            toggleFLinesOnObject((PhysicalObject) it.next());
        }
        theEngine.requestRefresh();

    }
        else if(e.getSource() == rad1){
            GaussianCylinder.setDrawn(true);	
            GaussianCylinderFlux.setDrawn(true);
            cylinderVisible = true;
            for (int j = 0; j < numRadCylinder; j++) {
            	for (int i = 0; i < numAziTopCylinder; i++) {
    	     		if(chargesPresent) theFieldCylinderTop[i][j].setDrawn(true); else theFieldCylinderTop[i][j].setDrawn(false);
    	     		if(chargesPresent) theFieldCylinderBottom[i][j].setDrawn(true); else theFieldCylinderBottom[i][j].setDrawn(false);
    	     		theNormalCylinderTop[i][j].setDrawn(true);
    	     		theNormalCylinderBottom[i][j].setDrawn(true);
            	} 	
            }
            for (int j = 0; j < numZSidesCylinder; j++) {
            	for (int i = 0; i < numAziSidesCylinder; i++) {
            		if(chargesPresent) theFieldCylinderSides[i][j].setDrawn(true); else theFieldCylinderSides[i][j].setDrawn(false);
    	     		theNormalCylinderSides[i][j].setDrawn(true);
            	} 	
            }
            GaussianSphereFlux.setDrawn(false);
            GaussianSphere.setDrawn(false);	
            sphereVisible = false;
            for (int j = 0; j < numThetaSphere; j++) {
            	for (int i = 0; i < numAziSphere; i++) {
    	     		theFieldSphere[i][j].setDrawn(false);
    	     		theNormalSphere[i][j].setDrawn(false);
            	} 	
            }
            calculateFlux();
        }
        else if(e.getSource() == rad2){
            GaussianSphere.setDrawn(true);	
            GaussianSphereFlux.setDrawn(true);
            sphereVisible = true;
            for (int j = 0; j < numThetaSphere; j++) {
            	for (int i = 0; i < numAziSphere; i++) {
            		if(chargesPresent) theFieldSphere[i][j].setDrawn(true); else theFieldSphere[i][j].setDrawn(false);
    	     		theNormalSphere[i][j].setDrawn(true);
            	} 	
            }
            GaussianCylinderFlux.setDrawn(false);
            GaussianCylinder.setDrawn(false);	
            cylinderVisible = false;
            for (int j = 0; j < numRadCylinder; j++) {
            	for (int i = 0; i < numAziTopCylinder; i++) {
    	     		theFieldCylinderTop[i][j].setDrawn(false);
    	     		theFieldCylinderBottom[i][j].setDrawn(false);
    	     		theNormalCylinderTop[i][j].setDrawn(false);
    	     		theNormalCylinderBottom[i][j].setDrawn(false);
            	} 	
            }
            for (int j = 0; j < numZSidesCylinder; j++) {
            	for (int i = 0; i < numAziSidesCylinder; i++) {
    	     		theFieldCylinderSides[i][j].setDrawn(false);
    	     		theNormalCylinderSides[i][j].setDrawn(false);
            	} 	
            }
            calculateFlux();
        }
         else {
            super.actionPerformed(e);
        }
    }

    /** Method to place the electric field vectors and normals on the cylinder and on the sphere. */
	public void PlaceENVectors() {
        double compx = Math.cos(angleGCylinder*Math.PI/180.);
        double compy = Math.sin(angleGCylinder*Math.PI/180.);
        
		// first place the vectors on the sphere
	    for (int j = 0; j < numThetaSphere; j++) {
	    	double cosvalue = (j+1)*2./(numThetaSphere*1.+1.)-1.;
	    	double acosangle = Math.acos(cosvalue);
	    	acosangle =  j*Math.PI/(numThetaSphere*1.-1.);
        	for (int i = 0; i < numAziSphere; i++) {
	    		double aziangle = i*2.*Math.PI/(numAziSphere*1.);  	
	    		Vector3d azidir = new Vector3d(Math.cos(acosangle),Math.cos(aziangle)*Math.sin(acosangle),Math.sin(aziangle)*Math.sin(acosangle));
	    //		// System.out.println(" i "+i+" j "+j+" acosangle "+acosangle+" aziangle "+aziangle+" azidir "+azidir);
	    		Vector3d azipos = new Vector3d(azidir);
	    		azipos.scale(radiusGSphere);
	    		Vector3d aziposTrans = new Vector3d(0,0,0);
	    		Vector3d azidirTrans = new Vector3d(0,0,0);
	    		aziposTrans.x = azipos.x*compx - azipos.y*compy;
	    		aziposTrans.y = azipos.x*compy + azipos.y*compx;
	    		aziposTrans.z = azipos.z;
	    		azidirTrans.x = azidir.x*compx - azidir.y*compy;
	    		azidirTrans.y = azidir.x*compy + azidir.y*compx;
	    		azidirTrans.z = azidir.z;
	    		aziposTrans.add(posGCylinder);
	     		theFieldSphere[i][j].setPosition(aziposTrans);
	     	//	theFieldSphere[i][j].setDrawn(false);
	     		theNormalSphere[i][j].setPosition(aziposTrans);
	     		theNormalSphere[i][j].setValue(azidirTrans);
	//     		theNormalSphere[i][j].setDrawn(false);
	     		// here we make the field vector tip be at the location of the arrow if the arrow points inward at the local normal
	   //     	Transform3D offsetTrans = new Transform3D();
	   //  		double dot = theFieldSphere[i][j].getValue().dot(azidirTrans);
	        //	if ( dot > 0. ) offsetTrans.setTranslation(new Vector3d(0., 0., 0.));
	       // 	else offsetTrans.setTranslation(new Vector3d(0., -1.1, 0.));
     	//	theFieldCylinderSides[i][j].setModelOffsetTransform(offsetTrans);
        	} 	   	
        }	
		
		
	// now place the vectors on the top of the cylinder
		Vector3d normalTop = null;
		Vector3d centerTop = new Vector3d(0,0,0);

        normalTop = new Vector3d(compx, compy,0.);
        normalTop.scale(heightGCylinder/2.);
		centerTop.add(normalTop);
		centerTop.add(posGCylinder);

        for (int j = 0; j < numRadCylinder; j++) {
        	double rad = (j+1)*radiusGCylinder/(numRadCylinder+1);
        	for (int i = 0; i < numAziTopCylinder; i++) {
        		double aziangle = i*2.*Math.PI/(numAziTopCylinder*1.);
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
	     		theFieldCylinderTop[i][j].setPosition(aziposTrans);
	     		theNormalCylinderTop[i][j].setPosition(aziposTrans);
	     		theNormalCylinderTop[i][j].setValue(azidirTrans);
//	     		theFieldCylinderTop[i][j].setDrawn(false);
//	     		theNormalCylinderTop[i][j].setDrawn(false);
	     		// here we make the field vector tip be at the location of the arrow if the arrow points inward at the local normal
	   //     	Transform3D offsetTrans = new Transform3D();
	  //   		double dot = theFieldCylinderTop[i][j].getValue().dot(azidirTrans);
	    //    	if ( dot > 0. ) offsetTrans.setTranslation(new Vector3d(0., 0., 0.));
	    //    	else offsetTrans.setTranslation(new Vector3d(0., -1.1, 0.));
	    // 		theFieldCylinderTop[i][j].setModelOffsetTransform(offsetTrans);
        	} 	
        }

        	
  // now place the vectors on the bottom of the cylinder
		Vector3d normalBottom = null;
		Vector3d centerBottom = new Vector3d(0,0,0);
        normalBottom = new Vector3d(compx, compy,0.);
        normalBottom.scale(-heightGCylinder/2.);
		centerBottom.add(normalBottom);
		centerBottom.add(posGCylinder);
		
        for (int j = 0; j < numRadCylinder; j++) {
        	double rad = (j+1)*radiusGCylinder/(numRadCylinder+1);
        	for (int i = 0; i < numAziTopCylinder; i++) {
        		double aziangle = i*2.*Math.PI/(numAziTopCylinder*1.);
        		Vector3d azipos = new Vector3d(0.,Math.cos(aziangle),Math.sin(aziangle));
        		Vector3d aziposTrans = new Vector3d(0,0,0);
        		Vector3d azidirTrans = new Vector3d(-1,0,0);
        		Vector3d azidir = new Vector3d(-1.,0.,0.);
        		azipos.scale(rad);
        		aziposTrans.x = azipos.x*compx - azipos.y*compy;
        		aziposTrans.y = azipos.x*compy + azipos.y*compx;
        		aziposTrans.z = azipos.z;
        		azidirTrans.x = azidir.x*compx - azidir.y*compy;
        		azidirTrans.y = azidir.x*compy + azidir.y*compx;
        		azidirTrans.z = azidir.z;
        		aziposTrans.add(centerBottom);
	     		theFieldCylinderBottom[i][j].setPosition(aziposTrans);
	     		theNormalCylinderBottom[i][j].setPosition(aziposTrans);
	     		theNormalCylinderBottom[i][j].setValue(azidirTrans);
//	     		theNormalCylinderBottom[i][j].setDrawn(false);
//	     		theFieldCylinderBottom[i][j].setDrawn(false);
	     		// here we make the field vector tip be at the location of the arrow if the arrow points inward at the local normal
	 //       	Transform3D offsetTrans = new Transform3D();
	 //    		double dot = theFieldCylinderBottom[i][j].getValue().dot(azidirTrans);
	        //	if ( dot > 0. ) offsetTrans.setTranslation(new Vector3d(0., 0., 0.));
	        //	else offsetTrans.setTranslation(new Vector3d(0., -1.1, 0.));
	     	//	theFieldCylinderBottom[i][j].setModelOffsetTransform(offsetTrans);
        	} 	   	
        }		
		
// now place the vectors on the sides of the cylinder
		Vector3d normalSides = null;
		Vector3d centerSides = new Vector3d(0,0,0);
        normalSides = new Vector3d(compx, compy,0.);
      //  normalSides.scale(-heightGCylinder/2.);
		centerSides.add(normalSides);
		centerSides.add(posGCylinder);

	    for (int j = 0; j < numZSidesCylinder; j++) {
	    	double zvalue = (j+1)*heightGCylinder/(numZSidesCylinder+1)-heightGCylinder/2.;
        	for (int i = 0; i < numAziSidesCylinder; i++) {
    		double aziangle = i*2.*Math.PI/(numAziSidesCylinder*1.);
    		Vector3d azipos = new Vector3d(zvalue,radiusGCylinder*Math.cos(aziangle),radiusGCylinder*Math.sin(aziangle));
    		Vector3d azidir = new Vector3d(0.,Math.cos(aziangle),Math.sin(aziangle));
    		Vector3d aziposTrans = new Vector3d(0,0,0);
    		Vector3d azidirTrans = new Vector3d(0,0,0);
    		aziposTrans.x = azipos.x*compx - azipos.y*compy;
    		aziposTrans.y = azipos.x*compy + azipos.y*compx;
    		aziposTrans.z = azipos.z;
    		azidirTrans.x = azidir.x*compx - azidir.y*compy;
    		azidirTrans.y = azidir.x*compy + azidir.y*compx;
    		azidirTrans.z = azidir.z;
    		aziposTrans.add(posGCylinder);
     		theFieldCylinderSides[i][j].setPosition(aziposTrans);
     		theNormalCylinderSides[i][j].setPosition(aziposTrans);
     		theNormalCylinderSides[i][j].setValue(azidirTrans);
   //  		theNormalCylinderSides[i][j].setDrawn(true);
   //  		theFieldCylinderSides[i][j].setDrawn(true)	;
     		// here we make the field vector tip be at the location of the arrow if the arrow points inward at the local normal
     //   	Transform3D offsetTrans = new Transform3D();
     //		double dot = theFieldCylinderSides[i][j].getValue().dot(azidirTrans);
        //	if ( dot > 0. ) offsetTrans.setTranslation(new Vector3d(0., 0., 0.));
       // 	else offsetTrans.setTranslation(new Vector3d(0., -1.1, 0.));
     	//	theFieldCylinderSides[i][j].setModelOffsetTransform(offsetTrans);
        	} 	   	
        }	
	    
	}  // end method PlaceENVectors
	
	
    public void propertyChange(PropertyChangeEvent pce) {
        Object source = pce.getSource();
        if (source == posSlider_x) {
            double posX = ((Double) pce.getNewValue()).doubleValue();
            posGCylinder.x=posX;
            //GaussianCylinder.setNode3D(ShapeNodeGCylinder);
            GaussianCylinder.setPosition(posGCylinder);
            posGSphere.x=posX;
            //GaussianSphere.setNode3D(ShapeNodeGSphere);
            GaussianSphere.setPosition(posGSphere);
            // see if point charge inside or outside of the sphere and cylinder after this move
            calculateFlux();
            PlaceENVectors();
            
        } else if (source == posSlider_y) {
            double posY = ((Double) pce.getNewValue()).doubleValue();
            posGCylinder.y=posY;
            //GaussianCylinder.setNode3D(ShapeNodeGCylinder);
            GaussianCylinder.setPosition(posGCylinder);
            posGSphere.y=posY;
            //GaussianSphere.setNode3D(ShapeNodeGSphere);
            GaussianSphere.setPosition(posGSphere);
            calculateFlux();
            PlaceENVectors();
        } else if (source == angGCylinder) {
        	angleGCylinder = ((Double) pce.getNewValue()).doubleValue();
            double angGCylinder_rad = angleGCylinder*Math.PI/180.;
            double compx = Math.cos(angGCylinder_rad);
            double compy = Math.sin(angGCylinder_rad);
            //GaussianCylinder.setNode3D(ShapeNodeGCylinder);
            GaussianCylinder.setDirection(new Vector3d(compx, compy, 0.));
            //GaussianSphere.setNode3D(ShapeNodeGSphere);
            GaussianSphere.setDirection(new Vector3d(compx, compy, 0.));
            calculateFlux();
            PlaceENVectors();
        } else if (source instanceof PointCharge) {
            double chargeInCylinder = 0.;
//            Collection elements = ((EMEngine)theEngine).getPhysicalObjs();
            Collection elements = theEngine.getCollectionByType(PhysicalObject.class);
            TDebug.println(0, elements.size());
            Iterator myIterator = elements.iterator();
            while (myIterator.hasNext() == true) {			
                Object myObject = myIterator.next();
                if (myObject instanceof PointCharge) {
                	Vector3d r = new Vector3d();
                	double myCharge = 0.;
                	myCharge = ((PointCharge) myObject).getCharge();
                    r.set(((PhysicalObject) myObject).getPosition());
                	boolean insidecylinder = insideCylinder(r);
                	if (insidecylinder)	chargeInCylinder = chargeInCylinder + myCharge;
                 //   // System.out.println("chargeInCylinder "+chargeInCylinder+" myCharge "+ myCharge);
                }
            }
            heightGCylinderFlux = oneUnitFlux*(chargeInCylinder);
            //ShapeNodeGCylinderFlux.setGeometry(Cylinder.makeGeometry(32, radiusGCylinderFlux, heightGCylinderFlux));
            //GaussianCylinderFlux.setNode3D(ShapeNodeGCylinderFlux);
            GaussianCylinderFlux.setLength(heightGCylinderFlux);
            GaussianCylinderFlux.setPosition(new Vector3d(2.,-2.5+heightGCylinderFlux/2.,0));
            double chargeInSphere = 0.;
            myIterator = elements.iterator();
           while (myIterator.hasNext() == true) {			
               Object myObject = myIterator.next();
               if (myObject instanceof PointCharge) {
               	Vector3d r = new Vector3d();
               	double myCharge = 0.;
               	myCharge = ((PointCharge) myObject).getCharge();
                   r.set(((PhysicalObject) myObject).getPosition());
               	boolean insidesphere = insideSphere(r);
               	if (insidesphere)	chargeInSphere = chargeInSphere + myCharge;
                 //  // System.out.println("chargeInCylinder "+chargeInCylinder+" myCharge "+ myCharge);
               }
           }
           heightGSphereFlux = oneUnitFlux*(chargeInSphere);
           //ShapeNodeGSphereFlux.setGeometry(Cylinder.makeGeometry(32, radiusGSphereFlux, heightGSphereFlux));
           //GaussianSphereFlux.setNode3D(ShapeNodeGSphereFlux);
           GaussianSphereFlux.setLength(heightGSphereFlux);
           GaussianSphereFlux.setPosition(new Vector3d(2.,-2.5+heightGSphereFlux/2.,0));

        } else {
            super.propertyChange(pce);
        }
 
    }

    public boolean insideCylinder(Vector3d position){
    	boolean inside = false;
    	Vector3d relativePosition = new Vector3d();
    	Vector3d cylinderAxis = new Vector3d();
    	cylinderAxis = GaussianCylinder.getDirection();
    	cylinderAxis.normalize();
    	relativePosition.sub(position,posGCylinder);
    	// compute the coordinates of position in a frame centered on the cylinder
    	double zcoordinate = relativePosition.dot(cylinderAxis);
    	cylinderAxis.scale(zcoordinate);
    	relativePosition.sub(cylinderAxis);
    	double rhocoordinate = relativePosition.length();
    	if (Math.abs(zcoordinate)<=heightGCylinder/2. && rhocoordinate <= radiusGCylinder) inside = true;
    	return inside;
    }
    

    public boolean insideSphere(Vector3d position){
    	boolean inside = false;
    	Vector3d relativePosition = new Vector3d();
    	relativePosition.sub(position,posGSphere);
    	// compute the distance of the point charge from the center of the gaussian sphere
    	double radialdistance = relativePosition.length();
    	if (radialdistance <= radiusGSphere) inside = true;
    	return inside;
    }
    
    public void reset() {       
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 0.4), 
        	new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.)); 
    }
    
    public void processSelection(SelectEvent se) {
        int status = se.getStatus();
        Object source = se.getSource();
        TDebug.println(0, "selectEvent");

        if (status == SelectEvent.SELECT) {
            //selectList.clear();
            selectList.add(source);
            TDebug.println(0, "Selected: " + ((AbstractElement) source).getID());
      
            try {
                PointCharge pc = (PointCharge) source;
                fromApplication = true;
                slider1.setEnabled(true);
                slider1.setValue((int) pc.getCharge());
                fromApplication = false;
                //				pc.setIntegrating(false);
            } catch (Exception e) {
            }

        } else if (status == SelectEvent.MULTI_SELECT) {
            selectList.add(source);
            TDebug.println(0, "Multi-Selected: " + ((AbstractElement) source).getID());

            try {
                PointCharge pc = (PointCharge) source;
                fromApplication = true;
                slider1.setEnabled(true);
                slider1.setValue((int) pc.getCharge());
                fromApplication = false;
                //				pc.setIntegrating(false);
            } catch (Exception e) {
            }

        } else if (status == SelectEvent.NOT_SELECTED) {
            TDebug.println(0, "DESELECT MESSAGE");
            selectList.remove(source);

            try {
                int size = selectList.size();
                if (size != 0) {
                    PointCharge pc = (PointCharge) selectList.get(selectList.size() - 1);
                    fromApplication = true;
                    slider1.setEnabled(true);
                    slider1.setValue((int) pc.getCharge());
                } else {
                    //slider1.setEnabled(false);
                }
            } catch (Exception e) {
            }
        }
        
       
    }
    
    private PointCharge randomCharge(double charge, double tolerance, double radius, Vector3d offset) {
        PointCharge newCharge = new PointCharge();
        newCharge.setCharge(charge);
        newCharge.setMass(1.);
        newCharge.setRadius(0.2);
        newCharge.setPauliDistance(1.2);
        newCharge.setSelectable(true);
        newCharge.setPickable(true);
        newCharge.setColliding(true);
        newCharge.setGeneratingP(true);
        SphereCollisionController sccx = new SphereCollisionController(newCharge);
        sccx.setRadius(0.6);
        sccx.setTolerance(0.1);
        //		sccx.setElasticity(0.);
        //		sccx.setMode(SphereCollisionController.WALL_SPHERE);
        newCharge.setCollisionController(sccx);
        newCharge.addPropertyChangeListener(this);

        boolean positionOK = false;
        double rand;
        double signx;
        double signy;
        Vector3d testPos = new Vector3d();
        while (positionOK == false) {
            positionOK = true;

            rand = Math.random();
            signx = 1.;
            signy = 1.;
            if (rand > 0.5) signx = -1.0;
            rand = Math.random();
            if (rand > 0.5) signy = -1.0;
            testPos.set(new Vector3d(signx * Math.random() * radius, signy * Math.random() * radius, 0.));

//            Collection elements = ((EMEngine)theEngine).getPhysicalObjs();
            Collection elements = theEngine.getCollectionByType(PhysicalObject.class);

            //TDebug.println(0, elements.size());
            Iterator myIterator = elements.iterator();
            int i = 0;
            while (myIterator.hasNext() == true) {
                				if (i > 500)
                				{
                					//Give up
                					TDebug.println(0,"addRandomCharge() : Could not find suitable position!");
                					break;
                				}

                Vector3d r = new Vector3d();
                Object myObject = myIterator.next();
                if (myObject instanceof PointCharge) {

                    r.set(((PhysicalObject) myObject).getPosition());
                    r.sub(testPos);
                    double dist = r.length();
                    //TDebug.println(0,"r = " + dist);
                    if (dist <= tolerance) {
                        positionOK = false;
                        //// System.out.println("dist = " + dist);
                        break;
                    }
                }
                i++;
            }
        }
        testPos.add(offset);
        newCharge.setPosition(testPos);
        return newCharge;
    }

    public void addFLinesToObject(PhysicalObject myObject) {
        int numLines = 6;
        for (int j = 0; j < numLines; j++) {
            double angle = ((j) / (float) numLines) * 2. * Math.PI;
            TDebug.println(0, "angle: " + angle);
            RelativeFLine fl = new RelativeFLine(myObject, angle);
            fl.setType(Field.E_FIELD);
            fl.setKMax(50);
            addElement(fl);
        }
        //theEngine.requestRefresh();
        theEngine.requestSpatial();
    }

    public void removeFLinesFromObject(PhysicalObject myObject) {
        Collection elements = myObject.getReferents();
        if ((elements != null) && (!elements.isEmpty())) {
            Iterator it = elements.iterator();

            while (it.hasNext()) {
                HasReference element = (HasReference) it.next();
                element.removeReference(myObject);
                removeElement((TElement) element);
            }
            
        }

    }

    public void toggleFLinesOnObject(PhysicalObject myObject) {
        TDebug.println(0, "toggleFLinesOnObject is being called!");
        int simstate;
        simstate = mSEC.getSimState();
        mSEC.stop();
        boolean hasLines = false;

        Collection elements = myObject.getReferents();
        if ((elements != null) && (!elements.isEmpty())) {
            Iterator it = elements.iterator();

            while (it.hasNext()) {
                Object element = it.next();
                if (element instanceof RelativeFLine) {
                    if (((RelativeFLine) element).getReference() == myObject) {
                        hasLines = true;
                        break;
                    }
                }
            }
        }
        TDebug.println(0, "Has fieldlines = " + hasLines);
        if (hasLines == true) {
            removeFLinesFromObject(myObject);
        } else {
            addFLinesToObject(myObject);
        }
        if (simstate == TEngineControl.RUNNING) mSEC.start();
    }

    private void clearAllCharges() {
        int simstate = mSEC.getSimState();
        mSEC.stop();

//        Collection elements = ((EMEngine)theEngine).getPhysicalObjs();
        Collection elements = theEngine.getCollectionByType(PhysicalObject.class);

        Iterator it = elements.iterator();

        while (it.hasNext()) {
            Object em = it.next();
            if (em instanceof PointCharge) {
                removeFLinesFromObject((PhysicalObject) em);
                removeElement((PointCharge) em);
            }
        }
        //mSEC.step();
        if (simstate == TEngineControl.RUNNING)
            mSEC.start();
        else theEngine.requestRefresh();
    }
 
    private void MakeFieldVisible(boolean fieldvisible) {
    if(sphereVisible){
	    for (int j = 0; j < numThetaSphere; j++) {
	    	for (int i = 0; i < numAziSphere; i++) {
	     		theFieldSphere[i][j].setDrawn(fieldvisible);
	    	} 	
	    }
    }
    if(cylinderVisible){
	    for (int j = 0; j < numRadCylinder; j++) {
	    	for (int i = 0; i < numAziTopCylinder; i++) {
	     		theFieldCylinderTop[i][j].setDrawn(fieldvisible);
	     		theFieldCylinderBottom[i][j].setDrawn(fieldvisible);
	    	} 	
	    }
	    for (int j = 0; j < numZSidesCylinder; j++) {
	    	for (int i = 0; i < numAziSidesCylinder; i++) {
	     		theFieldCylinderSides[i][j].setDrawn(fieldvisible);
	    	} 	
	    }
    }
    }
    
    private void calculateFlux(){
    double chargeInCylinder = 0.;
//    Collection elements = ((EMEngine)theEngine).getPhysicalObjs();
    Collection elements = theEngine.getCollectionByType(PhysicalObject.class);
  
    //TDebug.println(0, elements.size());
    Iterator myIterator = elements.iterator();
    while (myIterator.hasNext() == true) {			
        Object myObject = myIterator.next();
        if (myObject instanceof PointCharge) {
        	Vector3d r = new Vector3d();
        	double myCharge = 0.;
        	myCharge = ((PointCharge) myObject).getCharge();
            r.set(((PhysicalObject) myObject).getPosition());
        	boolean insidecylinder = insideCylinder(r);
        	if (insidecylinder)	chargeInCylinder = chargeInCylinder + myCharge;
       //     // System.out.println("chargeInCylinder "+chargeInCylinder+" myCharge "+ myCharge);
        }
    }
    heightGCylinderFlux = oneUnitFlux*(chargeInCylinder);
    //ShapeNodeGCylinderFlux.setGeometry(Cylinder.makeGeometry(32, radiusGCylinderFlux, heightGCylinderFlux));
    //GaussianCylinderFlux.setNode3D(ShapeNodeGCylinderFlux);
    GaussianCylinderFlux.setLength(heightGCylinderFlux);
    GaussianCylinderFlux.setPosition(new Vector3d(2.,-2.5+heightGCylinderFlux/2.,0));
    double chargeInSphere = 0.;
    myIterator = elements.iterator();
   while (myIterator.hasNext() == true) {			
       Object myObject = myIterator.next();
       if (myObject instanceof PointCharge) {
       	Vector3d r = new Vector3d();
       	double myCharge = 0.;
       	myCharge = ((PointCharge) myObject).getCharge();
           r.set(((PhysicalObject) myObject).getPosition());
       	boolean insidesphere = insideSphere(r);
       	if (insidesphere)	chargeInSphere = chargeInSphere + myCharge;
        //   // System.out.println("chargeInCylinder "+chargeInCylinder+" myCharge "+ myCharge);
       }
   }
   heightGSphereFlux = oneUnitFlux*(chargeInSphere);
   //ShapeNodeGSphereFlux.setGeometry(Cylinder.makeGeometry(32, radiusGSphereFlux, heightGSphereFlux));
   //GaussianSphereFlux.setNode3D(ShapeNodeGSphereFlux);
   GaussianSphereFlux.setLength(heightGSphereFlux);
   GaussianSphereFlux.setPosition(new Vector3d(2.,-2.5+heightGSphereFlux/2.,0));
    } 
}
