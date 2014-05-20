/*
 * Created on Oct 2, 2003
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 * Comments
 */

package tealsim.physics.em;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.core.AbstractElement;
import teal.core.HasReference;
import teal.core.TElement;
import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.BoundingSphere;
import teal.render.TAbstractRendered;
import teal.render.viewer.SelectEvent;
import teal.render.viewer.SelectListener;
import teal.render.viewer.TViewer;
import teal.sim.TSimElement;
import teal.sim.collision.HasCollisionController;
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.TEngineControl;
import teal.sim.engine.SimEngine;
import teal.physics.em.SimEM;
import teal.physics.physical.PhysicalObject;
import teal.physics.physical.RectangularBox;
import teal.physics.em.PointCharge;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.RelativeFLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyInteger;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

/**
 * @author danziger
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
public class EMZoo extends SimEM implements SelectListener {

    private static final long serialVersionUID = 3258133535633258035L;
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
    VisualizationControl vis;

    final private int N = 10;
    /*
    private PointCharge[] pointCharges = new PointCharge[N];
    private PointCharge newCharge01;
    private PointCharge newCharge02;
     */
    private ArrayList<TAbstractRendered> selectList = new ArrayList<TAbstractRendered>();

    Vector3d[] positions = { new Vector3d(-0.2, 0.25, 0.), new Vector3d(0., 0.9, 0.), new Vector3d(0., -0.95, 0.),
            new Vector3d(0.95, 0., 0.), new Vector3d(-1.2, 0., 0.), new Vector3d(0.4, 0.45, 0.),
            new Vector3d(0.75, -0.8, 0.), new Vector3d(-0.75, 0.75, 0.), new Vector3d(-0.7, -0.4, 0.),
            new Vector3d(0.25, -0.2, 0.), };

    // point charge charges:
    double[] charges = { 1., 1., 1., 1., 1., -1., -1., -1., -1., -1. };
	//private EMEngine emModel;

    public EMZoo() {

        super();
        //setID("Zoo");
        title = "Electrostatic Zoo";
        
      
        TDebug.setGlobalLevel(0);

        // Building the world.
        setBoundingArea(new BoundingSphere(new Point3d(), 12.));
        setDamping(0.1);
        setGravity(new Vector3d(0., 0., 0.));
        setDeltaTime(1.);

        setShowGizmos(false);
        setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM);
        setRefreshOnDrag(true);
        setCursorOnDrag(false);
        mDLIC = new FieldConvolution();
        //mDLIC.setSize(new Dimension(3000,3000));
        mDLIC.setSize(new Dimension(1024, 1024));
        mDLIC.setComputePlane(new RectangularPlane(new BoundingSphere(new Point3d(), 14.)));

        // Creating components.

        // -> Rectangular Walls
        RectangularBox box = new RectangularBox();
        box.setPosition(new Vector3d(0., 0., 0.));
        box.setOrientation(new Vector3d(0., 1., 0.));
        box.setNormal(new Vector3d(0., 0., 1.));
        box.setLength(20.);
        box.setWidth(20.);
        box.setOpen(true);
        addElements(box.getWalls());

        // Scale positions
        for (int i = 0; i < N; i++) {
            positions[i].scale(3.8);
        }

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

        //JTaskPane tp;
        
        //tp = new JTaskPane();
        params = new ControlGroup();
        params.setText("Parameters");
        params.add(slider1);
        
        //tp.add(params);
        vis = new VisualizationControl();
        
        vis.setText("Field Visualization");
        vis.setFieldConvolution(mDLIC);
        vis.setConvolutionModes(DLIC.DLIC_FLAG_E | DLIC.DLIC_FLAG_EP);
        vis.setFieldVisGrid(fv);
        addElement(vis);
        addElement(params);
        
        addActions();
        
        if(theScene instanceof TElement) {
        	((TElement)theScene).addPropertyChangeListener("dragging", this);
        }
        addSelectListener(this);
        TDebug.println(1, "Calling mode simstateInit");

        //TDebug.println(1, "default FrameRate: " + theEngine.getFrameRate());
        //theEngine.setFrameRate(20.);
        //mDLIC.setColor(new Color(0, 0, 255));

        mSEC.init();
        // Launch
        
        resetCamera();
        resetPointCharges();
    }

    void addActions() {
        TealAction ta = new TealAction("EM Zoo", this);
        addAction("Help", ta);

        
        but2 = new JButton(new TealAction("Delete Selected", "delete", this));
        but2.setBounds(40, 650, 195, 24);
        //addElement(but2);

        but5 = new JButton(new TealAction("Delete All", "delete_all", this));
        but5.setBounds(250, 650, 195, 24);
        //addElement(but5);

        but3 = new JButton(new TealAction("Add Random Positive", "random_positive", this));
        but3.setBounds(40, 690, 195, 24);
        //addElement(but3);

        but4 = new JButton(new TealAction("Add Random Negative", "random_negative", this));
        but4.setBounds(250, 690, 195, 24);
        //addElement(but4);
        
        but6 = new JButton(new TealAction("Toggle Fieldlines on Selected", "toggle_flines", this));
        but6.setBounds(40, 730, 195, 24);
        //addElement(but6);
        

        params.add(but2);
        params.add(but5);
        params.add(but3);
        params.add(but4);
        
        vis.add(but6);
        
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("EM Zoo") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework) mFramework).openBrowser("help/emzoo.html");
        	}
        } else if (e.getActionCommand().compareToIgnoreCase("delete") == 0) {
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
            addElement(randomCharge(1.0, 2.0, 9.0, new Vector3d(0, 0, 0)));
        } else if (e.getActionCommand().compareToIgnoreCase("random_negative") == 0) {
            addElement(randomCharge(-1.0, 2.0, 9.0, new Vector3d(0, 0, 0)));
        } else if (e.getActionCommand().compareToIgnoreCase("delete_all") == 0) {
            clearAllCharges();
        } else if (e.getActionCommand().compareToIgnoreCase("toggle_flines") == 0) {
            TDebug.println(0, "action event toggle_flines!");
            Iterator it = selectList.iterator();
            while (it.hasNext()) {
                toggleFLinesOnObject((PhysicalObject) it.next());
            }
            theEngine.requestRefresh();

        } else if (e.getActionCommand().compareToIgnoreCase("Save DLIC to JPG") == 0) {
            //saveDLICImage();
        } else {

            super.actionPerformed(e);
        }
    }

    private PointCharge randomCharge(double charge, double tolerance, double radius, Vector3d offset) {
        PointCharge newCharge = new PointCharge();
        newCharge.setCharge(charge);
        newCharge.setMass(1.);
        newCharge.setRadius(0.6);
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
        //newCharge.addPropertyChangeListener("position", this);

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

            Collection<TSimElement> elements = getSimElements();
            //TDebug.println(0, elements.size());
            Iterator<TSimElement> myIterator = elements.iterator();
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
                        //System.out.println("dist = " + dist);
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
 
        if(theEngine != null)
        	theEngine.requestSpatial();
    }

    public void removeFLinesFromObject(PhysicalObject myObject) {
        Collection<HasReference> elements = myObject.getReferents();
        if ((elements != null) && (!elements.isEmpty())) {
            Iterator<HasReference> it = elements.iterator();

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

        Collection<HasReference> elements = myObject.getReferents();
        if ((elements != null) && (!elements.isEmpty())) {
            Iterator<HasReference> it = elements.iterator();

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

    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getSource() == slider1) {
            if (!fromApplication) {
                Iterator it = selectList.iterator();
                while (it.hasNext()) {
                    try {
                        double new_charge = ((Integer) slider1.getValue()).intValue();
                        PointCharge pc = (PointCharge) it.next();
                        // Clamped-line radius-to-charge curve.
                        double r1 = 0.6, r2 = 1.2, c1 = 1., c2 = 5.;
                        double radius = (Math.abs(new_charge) - c1) * (r2 - r1) / (c2 - c1) + r1;
                        if (radius < 0.2) radius = 0.2;
                        pc.setRadius(radius);
                        ((SphereCollisionController) pc.getCollisionController()).setRadius(radius);
                        pc.setCharge((int) new_charge);
                        theEngine.requestReorder(pc);

                    } catch (Exception e) {
                    }
                }
            }
        } else if (pce.getPropertyName().equalsIgnoreCase("dragging")) {
            Iterator it = selectList.iterator();
            while (it.hasNext()) {
            	theEngine.requestReorder((HasCollisionController) it.next());
            }
        } else {
            super.propertyChange(pce);
        }
    }

    public void processSelection(SelectEvent se) {
        int status = se.getStatus();
        Object source = se.getSource();
        TDebug.println(0, "selectEvent");

        if (status == SelectEvent.SELECT) {
            //selectList.clear();
            selectList.add((TAbstractRendered)source);
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
            selectList.add((TAbstractRendered)source);
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
/*
    private void clearAllTrails() {
        int simstate = mSEC.getSimState();
        mSEC.stop();

        Collection elements = ((SimEngine)theEngine).getSpatials();
        Iterator it = elements.iterator();

        while (it.hasNext()) {
            Object em = it.next();
            if (em instanceof TrailVisualization) {
                removeElement((IsSpatial) em);
            }
        }

        if (simstate == EngineControl.RUNNING)
            mSEC.start();
        else theEngine.requestRefresh();
    }
*/
    private void clearAllCharges() {
        int simstate = mSEC.getSimState();
        mSEC.stop();

        Collection<TSimElement> elements = getSimElements();
        Iterator<TSimElement> it = elements.iterator();

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
        else{
        	if(theEngine != null){
        		theEngine.requestRefresh();
        	}
        }
    }

   

    public void reset() {
        mSEC.stop();
        resetPointCharges();
        //resetCamera();
    }

    private void resetPointCharges() {
        //		clearAllTrails();
        clearAllCharges();
        for (int i = 0; i < N; i++) {
            //pointCharges[i].setCharge(charges[i]);
            //pointCharges[i].setPosition(positions[i], true);
            //pointCharges[i].setVelocity(new Vector3d());
            double rand = Math.random();
            double sign = 1.0;
            if (rand > 0.5) sign = -1.0;
            PointCharge newCharge = randomCharge(sign, 2., 9.0, new Vector3d(0, 0, 0));
            
            addElement(newCharge);
            if(theEngine != null){
            	theEngine.requestReorder(newCharge);
            }
            //			TrailVisualisation trailVis = new TrailVisualisation(newCharge, 24, 0.75, 0.05);
            //			trailVis.setColor(newCharge.getColor());
            //			addElement(trailVis);
            //addFLinesToObject(newCharge);
        }

    }
    
//    protected void saveDLICImage() {
//        if (mDLIC.isImageGenerated()) {
//            BufferedImage img = (BufferedImage) mDLIC.getImage();
//            if (img != null) {
//                if (fc == null) fc = new JFileChooser();
//                if (curDir != null) fc.setCurrentDirectory(curDir);
//                int status = fc.showSaveDialog(mFramework);
//                if (status == JFileChooser.APPROVE_OPTION) {
//                    File file = null;
//                    curDir = fc.getCurrentDirectory();
//                    try {
//                        file = fc.getSelectedFile();
//                        ImageIO.writeJPEG(img, 300, file);
//                    } catch (IOException fnf) {
//                        TDebug.printThrown(fnf, " Trying to save file: " + file);
//                    }
//
//                }
//            }
//        }
//    }

    /*
     public void resetCamera() {
     mViewer.setLookAt(new Point3d(0.0, 0.0, 1.5), new Point3d(), new Vector3d(0., 1., 0.));

     }
     */
   

}
