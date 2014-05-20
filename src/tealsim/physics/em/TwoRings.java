/* $Id: TwoRings.java,v 1.14 2010/08/10 18:12:34 stefan Exp $ */

/**
 * A demonstration implementation of the TFramework.
 *
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.14 $
 */

package tealsim.physics.em;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import teal.render.BoundingSphere;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.app.SimPlayer;
import teal.config.Teal;
import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.em.RingOfCurrent;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.ui.control.PropertyInteger;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;
import teal.visualization.processing.Colorizer;
import teal.visualization.processing.TColorizer;

public class TwoRings extends SimEM {

    private static final long serialVersionUID = 3256438101621881142L;
    FieldConvolution mDLIC;
    VisualizationControl visControl;
    PropertyInteger slider1;
    PropertyDouble slider2;
    PropertyDouble slider3;
    PropertyDouble slider5;
    PropertyDouble slider6;
    double defaultRadius = Teal.RingOfCurrentDefaultRadius * 2.;
    RingOfCurrent roc1;
    RingOfCurrent roc2;
    Vector3d ring1Pos = new Vector3d(0, Teal.RingOfCurrentDefaultRadius, 0);
    Vector3d ring2Pos = new Vector3d(0, -Teal.RingOfCurrentDefaultRadius, 0);

    public TwoRings() {

        super();

        title = "Two Rings of Current";
        setID("TwoRIngsOfCurrent");
       
        BoundingSphere bs = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 4);
        setBoundingArea(bs);
 
        RectangularPlane rec = new RectangularPlane(bs);
        theScene.setBoundingArea(bs);
        setShowGizmos(false);
        //TDebug.setGlobalLevel(1);
        mSEC.setVisible(false);
        mDLIC = new FieldConvolution();
        mDLIC.setSize(new Dimension(512, 512));
        addElement(mDLIC, false);
        mDLIC.setVisible(false);
        mDLIC.setComputePlane(rec);
        mDLIC.setColorizer(new Colorizer(0.65,0.3,0.3,false));
        roc1 = new RingOfCurrent();
        roc1.setID("Ring 1");
        roc1.setPosition(ring1Pos);
        roc1.setRadius(Teal.RingOfCurrentDefaultRadius);
        roc1.setThickness(Teal.RingOfCurrentDefaultTorusRadius);
        roc1.setPickable(true);
        roc1.setMoveable(true);
        roc1.setRotable(true);
        roc1.setSelectable(true);
        addElement(roc1);

        roc2 = new RingOfCurrent();
        roc2.setID("Ring 2");
        roc2.setPosition(ring2Pos);
        roc2.setThickness(Teal.RingOfCurrentDefaultTorusRadius);
        roc2.setPickable(true);
        roc2.setRotable(true);
        roc2.setMoveable(true);
        roc2.setSelectable(true);
        addElement(roc2);

        //TDebug.println(0,"M1 flux " + m1.getBFlux(new Vector3d(5,0,0)));

        FieldDirectionGrid fv = new FieldDirectionGrid();
        fv.setType(Field.B_FIELD);
        fv.setResolution(0);


        slider2 = new PropertyDouble();
        slider2.setMinimum(-10);
        slider2.setMaximum(10);
        slider2.setBounds(35, 550, 415, 50);
        slider2.setPaintTicks(true);
        slider2.setText("Current Ring 1:");
        slider2.setBorder(null);
        slider2.addRoute("value", roc1, "current");
        slider2.setValue(1.0);

        slider5 = new PropertyDouble();
        slider5.setMinimum(0);
        slider5.setMaximum(3);
        slider5.setBounds(35, 610, 415, 50);
        slider5.setPaintTicks(true);
        slider5.addRoute("value", roc1, "radius");
        slider5.setText("Radius Ring 1:");
        slider5.setBorder(null);
        slider5.setValue(defaultRadius);

        slider6 = new PropertyDouble();
        slider6.setMinimum(-10);
        slider6.setMaximum(10);
        slider6.setBounds(35, 670, 415, 50);
        slider6.setPaintTicks(true);
        slider6.addRoute("value", roc2, "current");
        slider6.setText("Current Ring 2:");
        slider6.setBorder(null);
        slider6.setValue(1.0);

        slider3 = new PropertyDouble();
        slider3.setMinimum(0);
        slider3.setMaximum(3);
        slider3.setBounds(35, 730, 415, 50);
        slider3.setPaintTicks(true);
        slider3.addRoute("value", roc2, "radius");
        slider3.setText("Radius Ring 2:");
        slider3.setBorder(null);
        slider3.setValue(defaultRadius);
        
        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(slider2);
        controls.add(slider5);
        controls.add(slider6);
        controls.add(slider3);
        
        visControl = new VisualizationControl();
        visControl.setFieldConvolution(mDLIC);
        visControl.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
        visControl.setFieldVisGrid(fv);
        addElement(controls);
        addElement(visControl);
    
        setDeltaTime(0.5);

        addActions();
        //roc1.setCurrent(10.0);
//        TDebug.println(1, theEngine.getBField().getFlux(
//            new Vector3d(Teal.RingOfCurrentDefaultRadius / 2., Teal.RingOfCurrentDefaultRadius, 0.)));
//        TDebug.println(1, theEngine.getBField().getFlux(
//            new Vector3d(Teal.RingOfCurrentDefaultRadius / 10., Teal.RingOfCurrentDefaultRadius, 0.)));
        //mSEC.init();
        //theEngine.requestRefresh();
        TDebug.println(1, "ROC 1 pos = " + roc1.getPosition());
        TDebug.println(1, "ROC 1 bounds = " + roc1.getBoundingArea());
        //TDebug.println(1,"NavMode: " + mViewer.getNavigationMode());
        //TDebug.println(1, "bounds = " + theEngine.getBoundingArea());
        //TDebug.println("app complete");

    }

    public void reset() {
        mSelect.clearSelected();
        roc1.setPosition(ring1Pos);
        roc1.setDirection(new Vector3d(0., 1., 0.));
        roc2.setPosition(ring2Pos);
        roc2.setDirection(new Vector3d(0., 1., 0.));
        slider2.setValue(1.0);
        slider5.setValue(defaultRadius);
        slider6.setValue(1.0);
        slider3.setValue(defaultRadius);

    }

    void addActions() {
        TealAction ta = new TealAction("Two Current Rings", this);
        addAction("Help", ta);
        ta = new TealAction("Reset", SimPlayer.RESET, this);
        addAction("Actions", ta);

        ta = new TealAction("Two Current Rings", this);
        addAction("Help", ta);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Two Current Rings") == 0) {

        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/tworings.html");
        	}
        }  else {
            super.actionPerformed(e);
        }
    }

}
