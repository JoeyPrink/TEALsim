/* $Id: Example_01.java,v 1.9 2010/07/06 19:32:54 pbailey Exp $ */
/**
 * @author John Belcher - Department of Physics / MIT
 * @version $Revision: 1.9 $
 */

package tealsim.physics.examples;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.vecmath.*;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.render.Rendered;
import teal.render.TealMaterial;
import teal.render.primitives.Cylinder;
import teal.render.primitives.Sphere;
import teal.render.scene.*;
import teal.physics.em.SimEM;
import teal.ui.control.*;
import teal.util.TDebug;

/** Create two native 3D objects and import two.  The native objects are a green sphere and a flat red cylinder (a disk) and
 * the imported two are .3DS objects (a tapered cone and an orange hemisphere).  
 * The vertical position of the red disk is controlled by a slider, as is the orientation of the cone.  
 *  
 * @author John Belcher
 * @version 1.0 
 * */

public class Example_01 extends SimEM {

    private static final long serialVersionUID = 3257008735204554035L;
    /** A TEALsim native object (a red disk).  */
    Rendered nativeObject01;
  
    /** A TEALsim native object (a green sphere).  */
    Rendered nativeObject02;

    /** An imported 3DS object (a hemisphere).  */
    Rendered importedObject01;

    /** An imported 3DS object (a cone).  */
    Rendered importedObject02;
   
    /** Slider for the position of the red disk.  */
    PropertyDouble posSlider01 = new PropertyDouble();
    /** Slider for the rotation angle of the cone.  */
    PropertyDouble angSlider01 = new PropertyDouble();
    /** Slider for the position of the cone.  */
    PropertyDouble posSlider02 = new PropertyDouble();
    
    public Example_01() {
        super();
        TDebug.setGlobalLevel(0);
        title = "Example_01";
        
 // create two objects using teal.render.geometry 
 // and add them to the scene
        
        nativeObject01 = new Cylinder(2.,0.05);
        TealMaterial mat = new TealMaterial();
        mat.setDiffuse(Color.red);
        nativeObject01.setMaterial(mat);
        nativeObject01.setPosition(new Vector3d(0,0.,0.));
        nativeObject01.setDirection(new Vector3d(0.,1.,0.));
        addElement(nativeObject01);
        
        nativeObject02 = new Sphere(0.5);
        nativeObject02.setColor(new Color(0, 255, 0));  // makes the sphere green
        nativeObject02.setPosition(new Vector3d(0, 2, 0));

        nativeObject02.setSelectable(true);
        nativeObject02.setPickable(true);
        nativeObject02.setMoveable(true);
        nativeObject02.setDrawn(true);
        nativeObject02.addPropertyChangeListener(this);
        addElement(nativeObject02);           

 // import two .3DS files objects using Loader3DS
 // The conversion between max units and Java3D units 
 // is 1 Java3D unit = 1 Max inch
        
        double scale3DS = 0.01; // this is an overall scale factor for these .3DS objects
 
       Model geoSphere = new Model("models/geoSphere.3DS","models/");
    	geoSphere.setScale(scale3DS);
       
        importedObject01 = new Rendered();
        importedObject01.setModel(geoSphere);
        importedObject01.setPosition(new Vector3d(0., -1., 0.));
        addElement(importedObject01);

        Model cone  = new Model("models/cone.3DS", "models/");
        cone.setScale(scale3DS);
        
        importedObject02 = new Rendered();
        importedObject02.setModel(cone);
        importedObject02.setPosition(new Vector3d(0., 0., 0.));
        importedObject02.setDirection(new Vector3d(0.,1.,0.));
        addElement(importedObject02);
        
 // create the slider for the disk 
        
        posSlider01.setText("Disk Position ");
        posSlider01.setMinimum(-1.);
        posSlider01.setMaximum(3.0);
        posSlider01.setPaintTicks(true);
        posSlider01.addPropertyChangeListener("value", this);
        posSlider01.setValue(-1.);
        posSlider01.setVisible(true);
        
// create the two sliders for the cone 
        
        angSlider01.setText("Cone Rotation Angle");
        angSlider01.setMinimum(-180.);
        angSlider01.setMaximum(180.0);
        angSlider01.setPaintTicks(true);
        angSlider01.addPropertyChangeListener("value", this);
        angSlider01.setValue(0.);
        angSlider01.setVisible(true);
        
        posSlider02.setText("Cone Position");
        posSlider02.setMinimum(-2.);
        posSlider02.setMaximum(3.0);
        posSlider02.setPaintTicks(true);
        posSlider02.addPropertyChangeListener("value", this);
        posSlider02.setValue(0.);
        posSlider02.setVisible(true);
        
 // add the sliders to control groups and add those to the scene

        ControlGroup controls01 = new ControlGroup();
        controls01.setText("Red Disk");
        controls01.add(posSlider01);
        addElement(controls01);
        
        ControlGroup controls02 = new ControlGroup();
        controls02.setText("Cone");
        controls02.add(angSlider01);
        controls02.add(posSlider02);
        //addElement(controls02);
        
// change some features of the lighting, background color, etc., from the default values, if desired
        
        setBackgroundColor(new Color(240,240,255));
        
 // set paramters for mouseScale 
  
        setMouseMoveScale(new Vector3d(0.05,0.05,0.5));
        
// set initial state

        mSEC.init();  
        //theEngine.requestRefresh();
        mSEC.setVisible(true);
        // the following statement removes the "run" controls since there is nothing to run here
        mSEC.rebuildPanel(0);
       
        // addAction for pulldown menus on TEALsim windows     
        addActions();

    }
    
    @Override
    public void initialize(){
         reset();
        resetCamera();
    }

// add two items to the help menu, one to explain the simulation and the other to explain the 
// veiw and execution controls
  
    void addActions() {
    //    TealAction ta = new TealAction("Execution & View", this);
    //    addAction("Help", ta);
        TealAction tb = new TealAction("Example_01", this);
        addAction("Help", tb);
    }

    
    public void actionPerformed(ActionEvent e) {
        TDebug.println(1, " Action comamnd: " + e.getActionCommand());
        if (e.getActionCommand().compareToIgnoreCase("Example_01") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/example_01.html");
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
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.025, 0.4), 
        	new Point3d(0., 0.025, 0.), new Vector3d(0., 1., 0.));
     
    }

    public void propertyChange(PropertyChangeEvent pce) {
        Object source = pce.getSource();
        if (source == posSlider01) {
            double posV01 = ((Double) pce.getNewValue()).doubleValue();
            nativeObject01.setPosition(new Vector3d(0., posV01, 0.));
        } else 
        if (source == angSlider01) {
            double angV02 = ((Double) pce.getNewValue()).doubleValue();
            double angV02rad = angV02*Math.PI/180.;
            double compx = Math.sin(angV02rad);
            double compy = Math.cos(angV02rad);
            importedObject02.setDirection(new Vector3d(compx, compy, 0.));
        } else {
            super.propertyChange(pce);
        }
        if (source == posSlider02) {
            double posV02 = ((Double) pce.getNewValue()).doubleValue();
            importedObject02.setPosition(new Vector3d(0, posV02, 0.));
        } else {
            super.propertyChange(pce);
        }
    }
    
}
