/* $Id: Java3DSImport.java,v 1.5 2010/05/29 14:47:56 pbailey Exp $ */
/**
 * @author John Belcher - Department of Physics / MIT
 * @version $Revision: 1.5 $
 */

package tealsim;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.FileNotFoundException;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import teal.framework.TealAction;
import teal.render.Rendered;
import teal.render.j3d.*;
import teal.render.j3d.geometry.Cylinder;
import teal.render.j3d.geometry.Sphere;
import teal.render.j3d.loaders.Loader3DS;
import teal.sim.simulation.SimWorld;
import teal.ui.control.*;
import teal.util.TDebug;

public class Java3DSImport extends SimWorld {

    private static final long serialVersionUID = 3257008735204554035L;
    
    Rendered nativeObject01 = new Rendered();
    ShapeNode ShapeNodeNative01 = new ShapeNode();
    Rendered nativeObject02 = new Rendered();
    ShapeNode ShapeNodeNative02 = new ShapeNode();
    PropertyDouble posSlider01 = new PropertyDouble();
    PropertyDouble posSlider02 = new PropertyDouble();
 
    public Java3DSImport() {
        super();

        TDebug.setGlobalLevel(0);

        title = "Java3DSImport";
 

        // import two .3DS files objects using Loader3DS
        // The conversion between max units and Java3D units 
        // is 1 Java3D unit = 1 Max inch
        
        double scale3DS = 0.2; // Scale changed to reduce size of Temple
        //double scale3DS = 0.01; // this is an overall scale for .3DS objects
 
        Loader3DS max = new Loader3DS();
    	
        BranchGroup bg01 = 
         max.getBranchGroup("resources/models/Tina7.3ds","resources/models/");
        Node3D node01 = new Node3D();
        node01.setScale(scale3DS);
        node01.addContents(bg01);
        Rendered importedObject01 = new Rendered();
        importedObject01.setNode3D(node01);
        importedObject01.setPosition(new Vector3d(0., 0., 0.));
        addElement(importedObject01);

//        BranchGroup bg02 = 
//         max.getBranchGroup("resources/models/Vessel8.3ds");
//        Node3D node02 = new Node3D();
//        node02.setScale(scale3DS);
//        node02.addContents(bg02);
//        
//        Rendered importedObject02 = new Rendered();
//        importedObject02.setNode3D(node02);
//        importedObject02.setPosition(new Vector3d(0., 0., 0.));
//        //addElement(importedObject02);
//   
 
        
        // set paramters for mouseScale 
        
        mView.setMouseMoveScale(0.05,0.05,0.5);
        mView.setBackgroundColor(new Color(125,125,125));
       //mVView.setFrontClipDistance(0.001);
        //mView.setBackClipDistance(10.0);
        //((Viewer3D)mViewer).getLight2().setEnable(false);
        //((Viewer3D)mViewer).getLight3().setEnable(false);
        
        mSEC.init();  
        resetCamera();
        // addAction for pulldown menus on TEALsim windows     
        addActions();
        
    }

    
    void addActions() {
        TealAction ta = new TealAction("Tutorial_02_03", this);
        addAction("Help", ta);
    }

    public void actionPerformed(ActionEvent e) {
        TDebug.println(1, " Action comamnd: " + e.getActionCommand());
        if (e.getActionCommand().compareToIgnoreCase("Tutorial_02_03") == 0) {
            mFramework.openBrowser("resources/help/tutorial_02_03.html");
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

 
    
}