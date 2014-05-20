/*
 * Created on Apr 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package tealsim.physics.em;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import teal.render.BoundingSphere;
import javax.swing.JButton;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.core.THasPropertyChange;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.viewer.TViewer;
import teal.sim.function.VectorGenerator;
import teal.physics.em.SimEM;
import teal.physics.em.PointCharge;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.RadiationFieldLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyCheck;
import teal.ui.control.PropertyCombo;
import teal.util.TDebug;

public class RadiationCharge extends SimEM {

    private static final long serialVersionUID = 3616444609618981941L;
    JButton but = null;
    JButton but1 = null;
    
    ControlGroup params;
    PointCharge pc1;
    PropertyCheck generate = null;
    VectorGenerator wave = null;
    PropertyCombo genselect = null;
    boolean isGenerating = false;
    RadiationFieldLine[] rfls;
    
    protected FieldConvolution mDLIC;

    public RadiationCharge() {

        super();
        TDebug.setGlobalLevel(0);
        title = "Radiation from a Moving Charge";
        
        setBoundingArea(new BoundingSphere(new Point3d(), 10));
        setDamping(0.1);
        setGravity(new Vector3d(0., 0., 0.));
 
        setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM | TViewer.VP_TRANSLATE);
        setShowGizmos(false);
        setRefreshOnDrag(false);
        mSEC.setVisible(false);

        RectangularPlane rec = new RectangularPlane(new Vector3d(-10., -10., 0.), new Vector3d(-10., 10., 0.),
            new Vector3d(10., 10., 0.));
        mDLIC = new FieldConvolution();
        mDLIC.setSize(new Dimension(512, 512));
        //mDLIC.setSize(new Dimension(256,256));
        mDLIC.setComputePlane(rec);

        pc1 = new PointCharge();
        pc1.setID("PointCharge 1");
        pc1.setPosition(new Vector3d(0, 2, 0));
        pc1.setRadius(0.6);
        pc1.setCharge(2.5);
        pc1.setMass(1.);
        pc1.setSelectable(true);
        pc1.setMoveable(true);
        pc1.setPickable(true);
        pc1.setColliding(false);

        
        addElement(pc1);

        isGenerating = true;
        wave = new VectorGenerator(new Vector3d(-20, 0, 0), new Vector3d(20, 0, 0), 2, false);
        wave.setScale(1.);
        //wave.setHz(0.50);
        wave.setSpeed(0.03);
        wave.setStepping(isGenerating);
        wave.addPropertyChangeListener("value", this);

        genselect = new PropertyCombo();
        genselect.add("Stop", new Integer(1));
        genselect.add("Start", new Integer(2));
        genselect.add("Circular", new Integer(4));
        genselect.add("Sinusoidal    ", new Integer(3));
        genselect.addPropertyChangeListener("value", this);
        genselect.setSelectedIndex(3);
        
        genselect.setBounds(35, 500, 250, 50);
        genselect.setText("Motion type:");

        //addElement(genselect);

        generate = new PropertyCheck();
        generate.setText("Motion On:");
        generate.setValue(isGenerating);
        generate.addPropertyChangeListener("value", this);
        generate.setBounds(200, 500, 250, 50);
        generate.setLabelWidth(70);
        addElement(wave);
        //addElement(generate);

        
        params = new ControlGroup();
        params.setText("Parameters");
        params.add(genselect);
        params.add(generate);
        addElement(params);

        int numlines = 32;
        rfls = new RadiationFieldLine[numlines];
        for (int i = 0; i < numlines; i++) {

            rfls[i] = new RadiationFieldLine(pc1, (i / (float) numlines) * 2 * Math.PI);
            rfls[i].setKMax(80);
            rfls[i].setSArc(1.);
            rfls[i].setColor(Teal.DefaultEFieldLineColor);
            rfls[i].setColorMode(FieldLine.COLOR_FLAT);
            addElement(rfls[i]);
        }

        //mViewer.addPropertyChangeListener("dragging",this);
        //mViewer.setMouseMoveScale(new Vector3d(0.01,0.01,0.0));
        if(theEngine instanceof THasPropertyChange) {
        	((THasPropertyChange)theEngine).addPropertyChangeListener("time", this);
        }
        //System.out.println("MOUSE SCALE:  " + mViewer.getMouseMoveScale());

        //theEngine.refresh();
        setDeltaTime(1.0);


        //mSEC.init();
        mSEC.start();

    }

    
        @Override
    public void initialize(){
     resetCamera();
        // addAction for pulldown menus on TEALsim windows     
        addActions();
        reset();
    }

    public void clearFieldLines() {
        for (int i = 0; i < rfls.length; i++) {
            rfls[i].clearHistory();
        }
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.0, 5.2), new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.));
    }

    public void reset() {
        pc1.setPosition(new Vector3d(0, 2, 0));
        clearFieldLines();
        theEngine.requestRefresh();
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (pce == null) return;
        try {
            TDebug.println(1, "RadiationCharge.propertyChange: " + pce.getSource() + " -> " + pce.getPropertyName());
            Object source = pce.getSource();
            if (source == theEngine && pce.getPropertyName().equalsIgnoreCase("time")) {
                theEngine.requestSpatial();
                theEngine.requestRefresh();
            } else if (source == theScene) {
                theEngine.requestSpatial();
                theEngine.requestRefresh();
                
            } else if (source == genselect) {
                Integer myValue = (Integer) pce.getNewValue();
                int mode = myValue.intValue();
                wave.setMode(mode);
                // stupid hack to get rid of bad fieldline transitions between modes
                if (mode == VectorGenerator.MODE_CIRCULAR) {
                    pc1.setPosition(new Vector3d(20 * Math.cos(0.), 20 * Math.sin(0.), 0));
                } else if (mode == VectorGenerator.MODE_LINE_SINUSOID) {
                    pc1.setPosition(new Vector3d());
                } else if (mode == VectorGenerator.MODE_LINE_LINEAR_LEFT) {
                    pc1.setPosition(new Vector3d(-40., 0., 0.));
                } else if (mode == VectorGenerator.MODE_LINE_LINEAR_RIGHT) {
                    pc1.setPosition(new Vector3d(0., 0., 0.));
                }
                clearFieldLines();

            } else if (source == wave) {
                String pn = pce.getPropertyName();
                
                if (pn.compareTo("value") == 0) {
                 pc1.setPosition((Vector3d) pce.getNewValue());
                }
            }

            else if (source == generate) {
                String pn = pce.getPropertyName();
                
                if (pn.compareTo("value") == 0) {
                    boolean state = ((Boolean) pce.getNewValue()).booleanValue();
                    
                    wave.setStepping(state);
                    isGenerating = state;
                }
            } else {
                super.propertyChange(pce);
            }
        } catch (Exception e) {
            
        }
    }

    void addActions() {
        TealAction ta = new TealAction("Radiating Charge", this);
        addAction("Help", ta);
    }

    boolean showFieldLines = true;

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Radiating Charge") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/radiatingcharge.html");
        	}
        } else {
            super.actionPerformed(e);
        }
    }

    

}
