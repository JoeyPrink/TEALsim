/* $Id: GaussLawSymmetry.java,v 1.5 2010/07/06 19:32:54 pbailey Exp $ */
/**
 * @author John Belcher - Department of Physics / MIT
 * @version $Revision: 1.5 $
 */

package tealsim.physics.em;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.media.j3d.*;
import javax.vecmath.*;

import teal.config.Teal;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.render.Rendered;
import teal.render.j3d.*;
import teal.render.j3d.geometry.Cylinder;
import teal.render.j3d.geometry.Sphere;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.primitives.Line;
import teal.sim.spatial.FieldVector;
import teal.sim.spatial.GeneralVector;
import teal.physics.em.PointCharge;
import teal.physics.em.InfiniteLineCharge;
import teal.physics.em.SimEM;
import teal.ui.control.*;
import teal.util.TDebug;

/** An application to show the normals and electric fields on the surface of a 
 * closed surface to elucidate the geometric concepts behind Gauss's Law.  
 *  
 * @author John Belcher
 * @version 1.0 
 * */

public class GaussLawSymmetry extends SimEM {

    private static final long serialVersionUID = 3257008735204554035L;
    /** The approximate width of the canvas in the standard view. */
    double widthtotal = 6.;
    /** The scale factor for the electric field vectors. */
    double arrowScale = .5;
    Vector3d arrowVectorScale = new Vector3d(1.,.1,1.);
    /** A TEALsim native object for the Gaussian cylinder.  */
    Rendered GaussianCylinder = new Rendered();
    /** A ShapeNode for the Gaussian Cylinder.  */
    ShapeNode ShapeNodeGCylinder = new ShapeNode();
    /** Slider for the y-position of the cylinder.  */
    PropertyDouble posSlider_y = new PropertyDouble();
    /** Slider for the x-position of the cylinder.  */
    PropertyDouble posSlider_x = new PropertyDouble();
    /** Slider for the rotation angle of the cylinder.  */
    PropertyDouble angGCylinder = new PropertyDouble();
    /** Vector for the position of the cylinder. */
    Vector3d posGCylinder = null;
    /** Source of electric field in scene.    */
    PointCharge pc;
    /** Another source of electric field in scene.    */
    InfiniteLineCharge lc;
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
    /** The number of z axis nodes for the electric field vectors on the sides of the cylinder (z is along symmetry axis) */
    int numZSidesCylinder = 4;

    
    public GaussLawSymmetry() {
        super();
        TDebug.setGlobalLevel(0);
        title = "Gauss's Law Symmetry";
        

 //  create source of electric field in scene, here a point charge
        pc = new PointCharge();
        addElement(pc);
        pc.setPosition(new Vector3d(0.,0.,0.));
        pc.setCharge(1.);
        pc.setRadius(.2);
        pc.setDrawn(true);
        

//  create source of electric field in scene, here a line charge
       lc = new InfiniteLineCharge();
   //    addElement(lc);
       lc.setPosition(new Vector3d(0.,0.,0.));
       lc.setCharge(1.);
       lc.setRadius(.2);
       lc.setDirection(new Vector3d(1., 0., 0.));
       lc.setDrawn(true);

        
// create the Gaussian Cylinder using teal.render.geometry and add them to the scene
        posGCylinder = new Vector3d(0.,0.,0);
        ShapeNodeGCylinder.setGeometry(Cylinder.makeGeometry(32, radiusGCylinder, heightGCylinder));
   //     ShapeNodeGCylinder.setTransparency(0.5f);
        GaussianCylinder.setNode3D(ShapeNodeGCylinder);
        GaussianCylinder.setColor(new Color(0, 0, 170));
        GaussianCylinder.setPosition(posGCylinder);
        GaussianCylinder.setDirection(new Vector3d(0.,1.,0.));
        GaussianCylinder.setDrawn(true);
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
	     		theFieldCylinderTop[i][j].setDrawn(true);
	     		addElement(theFieldCylinderTop[i][j]);
	     		theFieldCylinderBottom[i][j] = new FieldVector();
	     		theFieldCylinderBottom[i][j].setPosition(new Vector3d(0,0,0));
	     		theFieldCylinderBottom[i][j].setColor(Teal.PointChargePositiveColor);
	     		theFieldCylinderBottom[i][j].setArrowScale(arrowScale);
	     		theFieldCylinderBottom[i][j].setDrawn(true);
	            addElement(theFieldCylinderBottom[i][j]);
	     		theNormalCylinderTop[i][j] = new GeneralVector();
	     		theNormalCylinderTop[i][j].setPosition(new Vector3d(0,0,0));
	     		theNormalCylinderTop[i][j].setColor(Color.gray);
	     		theNormalCylinderTop[i][j].setArrowScale(arrowScale);
	     		theNormalCylinderTop[i][j].setDrawn(true);
	     		addElement(theNormalCylinderTop[i][j]);
	     		theNormalCylinderBottom[i][j] = new GeneralVector();
	     		theNormalCylinderBottom[i][j].setPosition(new Vector3d(0,0,0));
	     		theNormalCylinderBottom[i][j].setColor(Color.gray);
	     		theNormalCylinderBottom[i][j].setArrowScale(arrowScale);
	     		theNormalCylinderBottom[i][j].setDrawn(true);
	            addElement(theNormalCylinderBottom[i][j]);
		        if (theEngine != null) theEngine.requestSpatial(); 
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
	     		theFieldCylinderSides[i][j].setDrawn(true);
	        	Transform3D offsetTrans = new Transform3D();
	   //     	offsetTrans.setRotation(new AxisAngle4d(1.,0.,0.,Math.PI/2.));
	   // 		offsetTrans.setTranslation(new Vector3d(0., -1., 0.));
	    		theFieldCylinderSides[i][j].setModelOffsetTransform(offsetTrans);
	     		addElement(theFieldCylinderSides[i][j]);
	     		theNormalCylinderSides[i][j] = new GeneralVector();
	     		theNormalCylinderSides[i][j].setPosition(new Vector3d(0,0,0));
	     		theNormalCylinderSides[i][j].setArrowScale(arrowScale);
	     		theNormalCylinderSides[i][j].setDrawn(true);
	     		theNormalCylinderSides[i][j].setColor(Color.gray);
	     		addElement(theNormalCylinderSides[i][j]);
		        if (theEngine != null) theEngine.requestSpatial(); 
        	} 	
        }
        
        PlaceENVectors();
        
 // create the two sliders for the gaussian cylinder position    
        posSlider_x.setText("X Position");
        posSlider_x.setMinimum(-3.);
        posSlider_x.setMaximum(3.0);
        posSlider_x.setPaintTicks(true);
        posSlider_x.addPropertyChangeListener("value", this);
        posSlider_x.setValue(0.);
        posSlider_x.setVisible(true);
        
        posSlider_y.setText("Y Position ");
        posSlider_y.setMinimum(-3.);
        posSlider_y.setMaximum(3.0);
        posSlider_y.setPaintTicks(true);
        posSlider_y.addPropertyChangeListener("value", this);
        posSlider_y.setValue(0.);
        posSlider_y.setVisible(true);
        
// create the angle orientation slider for the cylinder, where angle is the angle from the x axis    
        angGCylinder.setText("Rotation Angle");
        angGCylinder.setMinimum(-180.);
        angGCylinder.setMaximum(180.0);
        angGCylinder.setPaintTicks(true);
        angGCylinder.addPropertyChangeListener("value", this);
        angGCylinder.setValue(0.);
        angGCylinder.setVisible(true);
       
 // add the sliders to the control group and add the control group to the scene
        ControlGroup controls = new ControlGroup();
        controls.setText("Cylinder Position and Orientation");
        controls.add(posSlider_y);
        controls.add(posSlider_x);
        controls.add(angGCylinder);
        addElement(controls);
        
      // add four lines to outline the xy plane 
        Outline();
     
// change some features of the lighting, background color, etc., from the default values, if desired
        setBackgroundColor(new Color(180,180,180));
        
// set parameters for mouseScale 
        setMouseMoveScale(0.05,0.05,0.5);
        
// set initial state
        mSEC.init();  
        theEngine.requestRefresh();
        mSEC.setVisible(true);
        reset();
        resetCamera();
        // addAction for pulldown menus on TEALsim windows     
        addActions();
        if (theEngine != null) theEngine.requestSpatial(); 
        GaussianCylinder.setNode3D(ShapeNodeGCylinder);
        GaussianCylinder.setPosition(posGCylinder);

    }

// add two items to the help menu, one to explain the simulation and the other to explain the 
// veiw and execution controls
  
    void addActions() {
        TealAction ta = new TealAction("Execution & View", this);
        addAction("Help", ta);
        TealAction tb = new TealAction("Gauss's Law Symmetry", this);
        addAction("Help", tb);
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
        if (e.getActionCommand().compareToIgnoreCase("Gauss's Law Symmetry") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/gausslawsymmetry.html");
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

    /** Method to place the electric field vectors and normals on the cylinder. */
	public void PlaceENVectors() {
	// first place the vectors on the top of the cylinder
		Vector3d normalTop = null;
		Vector3d centerTop = new Vector3d(0,0,0);
        double compx = Math.cos(angleGCylinder*Math.PI/180.);
        double compy = Math.sin(angleGCylinder*Math.PI/180.);
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
	     		theNormalCylinderTop[i][j].setDrawn(true);
	     		// here we make the field vector tip be at the location of the arrow if the arrow points inward at the local normal
	        	Transform3D offsetTrans = new Transform3D();
	     		double dot = theFieldCylinderTop[i][j].getValue().dot(azidirTrans);
	        	if ( dot > 0. ) offsetTrans.setTranslation(new Vector3d(0., 0., 0.));
	        	else offsetTrans.setTranslation(new Vector3d(0., -1.1, 0.));
	       	theFieldCylinderTop[i][j].setModelOffsetTransform(offsetTrans);
	        if (theEngine != null) 
	        	theEngine.requestSpatial(); 
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
	     		theNormalCylinderBottom[i][j].setDrawn(true);
	     		// here we make the field vector tip be at the location of the arrow if the arrow points inward at the local normal
	        	Transform3D offsetTrans = new Transform3D();
	     		double dot = theFieldCylinderBottom[i][j].getValue().dot(azidirTrans);
	        	if ( dot > 0. ) offsetTrans.setTranslation(new Vector3d(0., 0., 0.));
	        	else offsetTrans.setTranslation(new Vector3d(0., -1.1, 0.));
	     		theFieldCylinderBottom[i][j].setModelOffsetTransform(offsetTrans);
	     		 if (theEngine != null) theEngine.requestSpatial();
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
     		theNormalCylinderSides[i][j].setDrawn(true);
     		// here we make the field vector tip be at the location of the arrow if the arrow points inward at the local normal
        	Transform3D offsetTrans = new Transform3D();
     		double dot = theFieldCylinderSides[i][j].getValue().dot(azidirTrans);
        	if ( dot > 0. ) offsetTrans.setTranslation(new Vector3d(0., 0., 0.));
        	else offsetTrans.setTranslation(new Vector3d(0., -1.1, 0.));
        	 if (theEngine != null) 
        		 theEngine.requestSpatial();
        	} 	   	
        }	

	}  // end method PlaceENVectors
	
	
    public void propertyChange(PropertyChangeEvent pce) {
        Object source = pce.getSource();
        if (source == posSlider_x) {
            double posX = ((Double) pce.getNewValue()).doubleValue();
            posGCylinder.x=posX;
            GaussianCylinder.setNode3D(ShapeNodeGCylinder);
            GaussianCylinder.setPosition(posGCylinder);
            PlaceENVectors();
        } else if (source == posSlider_y) {
            double posY = ((Double) pce.getNewValue()).doubleValue();
            posGCylinder.y=posY;
            GaussianCylinder.setNode3D(ShapeNodeGCylinder);
            GaussianCylinder.setPosition(posGCylinder);
            PlaceENVectors();
        } else if (source == angGCylinder) {
        	angleGCylinder = ((Double) pce.getNewValue()).doubleValue();
            double angGCylinder_rad = angleGCylinder*Math.PI/180.;
            double compx = Math.cos(angGCylinder_rad);
            double compy = Math.sin(angGCylinder_rad);
            GaussianCylinder.setNode3D(ShapeNodeGCylinder);
            GaussianCylinder.setDirection(new Vector3d(compx, compy, 0.));
            PlaceENVectors();
        } else {
            super.propertyChange(pce);
        }
 
    }

    public void reset() {       
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 0.4), 
        	new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.)); 
    }
}
