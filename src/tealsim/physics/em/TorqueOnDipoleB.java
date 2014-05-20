/*
 * Created on Jul 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package tealsim.physics.em;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.swing.JButton;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.core.TElement;
import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.primitives.Line;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.EngineControl;
import teal.physics.em.EField;
import teal.physics.em.SimEM;
import teal.physics.em.ConstantField;
import teal.physics.em.MagneticDipole;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FluxFieldLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;
import teal.sim.spatial.FluxFieldLine;
public class TorqueOnDipoleB extends SimEM {

    private static final long serialVersionUID = 3544677282338845495L;
    JButton but;
    JButton but1;

    MagneticDipole pc1;

    ConstantField BField;
    int numlines = 4;
    double[] flux = new double[numlines];
    FieldLine[] tempfls1 = new FieldLine[numlines];
    FieldLine[] tempfls2 = new FieldLine[numlines];
    FieldLine[] tempfls3 = new FieldLine[numlines];
    FieldLine[] tempfls4 = new FieldLine[numlines];
    FieldLine[] tempfls5 = new FieldLine[numlines];
    FieldLine[] tempfls6 = new FieldLine[numlines];

    FieldLineManager fmanager;
    
    protected FieldConvolution mDLIC;
    
    double fluxScale = 3.25;

    Line line;

    public TorqueOnDipoleB() {

        super();

        title = "Torque on a Magnetic Dipole";
        
        TDebug.setGlobalLevel(0);
        mSEC.setVisible(false);
        BoundingSphere bs = new BoundingSphere(new Point3d(0, 0, 0), 1.5);
        setBoundingArea(bs);
        RectangularPlane recPlane = new RectangularPlane(bs);
        mDLIC = new FieldConvolution();
        mDLIC.setComputePlane(recPlane);
        pc1 = new MagneticDipole();
        pc1.setID("BField");
        pc1.setDirection(new Vector3d(1., 1., 0.));
        pc1.setMoveable(false);
        pc1.setLength(0.1);
        pc1.setRadius(0.05);
        pc1.setMu(0.025);

        //pc1.setCurrent(0.0);
        pc1.addPropertyChangeListener("mu", this);
        //UniformEField field = new UniformEField();
        BField = new ConstantField(new Vector3d(0., 0., 0.), new Vector3d(0., 1., 0.), 0.2);
        BField.setID("cylField");
        BField.setMagnitude(0.01);
        BField.addPropertyChangeListener("magnitude", this);

        addElement(pc1);
        addElement(BField);

        line = new Line(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0));
        line.setColor(new Color(200, 200, 200));
        //addElement(line);

        FieldDirectionGrid fv = new FieldDirectionGrid();
        fv.setType(teal.field.Field.B_FIELD);
        fv.setResolution(12);
        //fv.setGridIterator(recPlane);
        //addElement(fv);

        PropertyDouble slider2 = new PropertyDouble();
        slider2.setPrecision(0.0005);
        slider2.setMinimum(-.025);
        slider2.setMaximum(.025);
        //slider2.setBounds(45, 539, 415,50);
        slider2.addRoute("value", pc1, "mu");
        slider2.setText("Dipole Moment");
        slider2.setBorder(null);
        //addElement(slider2);
        slider2.setValue(0.025);

        PropertyDouble slider3 = new PropertyDouble();
        slider3.setMinimum(-0.3);
        slider3.setMaximum(0.3);
        //slider3.setBounds(45, 589, 415,50);
        slider3.addRoute("value", BField, "magnitude");
        slider3.setText("External Field Magnitude");
        slider3.setBorder(null);
        //addElement(slider3);
        slider3.setValue(0.01);

        PropertyDouble slider4 = new PropertyDouble();
        slider4.setMinimum(0.);
        slider4.setMaximum(1.);
        //slider4.setBounds(45, 619, 415,50);
        slider4.addRoute("value", (TElement) getEngine(), "damping");
        slider4.setText("Damping");
        slider4.setBorder(null);
        //addElement(slider4);
        slider4.setValue(0.01);

        

        
        

        

        for (int i = 0; i < numlines; i++) {
            Vector3d pos = new Vector3d(0.1 * Math.sin(0.5 * Math.PI * (i / 10.) + Math.PI * 0.25), 0.1 * Math.cos(0.5
                * Math.PI * (i / 10.) + Math.PI * 0.25), 0.);
            flux[i] = theEngine.getElementByType(teal.physics.em.BField.class).getFlux(pos);
            System.out.println("position: " + pos + " flux: " + flux[i]);
        }
        initFieldLines();

        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");

        controls.add(slider2);
        controls.add(slider3);
        controls.add(slider4);
        addElement(controls);
        

        VisualizationControl viz = new VisualizationControl();
        viz.setText("Field Visualization");
        viz.setFieldConvolution(mDLIC);
        viz.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
        viz.setFieldLineManager(fmanager);
        viz.setActionFlags(0);
        viz.setColorPerVertex(false);
        viz.setSymmetryCount(1);
        addElement(viz);
        
        
        mSEC.rebuildPanel(EngineControl.DO_ALL);
        mSEC.setVisible(true);
        theEngine.requestRefresh();
        theEngine.setDeltaTime(0.5);
        theEngine.setDamping(0.0);
        mSEC.init();

        addActions();

    }

    void initFieldLines() {
        Vector3d startPoint;
        fmanager = new FieldLineManager();
        fmanager.setElementManager(this);
        for (int i = 0; i < flux.length; i++) {
            startPoint = new Vector3d(0.0001, 1.50, 0.);
            tempfls1[i] = new FluxFieldLine(flux[i] * fluxScale, startPoint, new Vector3d(1., 0., 0.), 4.0);

            ((FluxFieldLine) tempfls1[i]).setKMax(60);
            ((FluxFieldLine) tempfls1[i]).setSArc(0.05);
            ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
            ((FluxFieldLine) tempfls1[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls1[i].setType(Field.B_FIELD);
            ((FluxFieldLine) tempfls1[i]).setSearchIntervals(800);
            ((FluxFieldLine) tempfls1[i]).setSearchSubIntervals(800);
            //addElement(tempfls1[i]);
            fmanager.addFieldLine(tempfls1[i]);

            startPoint = new Vector3d(-0.0001, 1.5, 0.);
            tempfls2[i] = new FluxFieldLine(flux[i] * fluxScale, startPoint, new Vector3d(-1., 0., 0.), 4.0);

            ((FluxFieldLine) tempfls2[i]).setSearchAxis(new Vector3d(-1.0, 0., 0.));

            ((FluxFieldLine) tempfls2[i]).setFluxValue(flux[i] * fluxScale);
            ((FluxFieldLine) tempfls2[i]).setKMax(60);
            ((FluxFieldLine) tempfls2[i]).setSArc(0.05);
            ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
            ((FluxFieldLine) tempfls2[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls2[i].setType(Field.B_FIELD);
            ((FluxFieldLine) tempfls2[i]).setSearchIntervals(800);
            ((FluxFieldLine) tempfls2[i]).setSearchSubIntervals(800);
            //addElement(tempfls2[i]);
            fmanager.addFieldLine(tempfls2[i]);

            tempfls3[i] = new FluxFieldLine(flux[i] * fluxScale, pc1, FluxFieldLine.SEARCH_FORWARD,
                FluxFieldLine.SEARCH_CIRCLE);
            ((FluxFieldLine) tempfls3[i]).setCircleSearchStart(FluxFieldLine.CIRCLE_SEARCH_DOWN);
            //((FluxFieldLine)tempfls3[i]).setReference(pc1);
            //((FluxFieldLine)tempfls2[i]).setObjPos(startPoint);
            //((FluxFieldLine)tempfls2[i]).setSearchAxis(new Vector3d(-1.0,0.,0.));
            //((FluxFieldLine)tempfls2[i]).setObjRadius(8.);
            //((FluxFieldLine)tempfls3[i]).setSearchMode(FluxFieldLine.SEARCH_CIRCLE);
            //((FluxFieldLine)tempfls3[i]).setSearchDir(FluxFieldLine.SEARCH_FORWARD);
            //((FluxFieldLine)tempfls3[i]).setFluxValue(flux[i]*fluxScale);
            ((FluxFieldLine) tempfls3[i]).setKMax(60);
            ((FluxFieldLine) tempfls3[i]).setSArc(0.05);
            tempfls3[i].setMinDistance(0.05);
            ((FluxFieldLine) tempfls3[i]).setBuildDir(FieldLine.BUILD_BOTH);
            ((FluxFieldLine) tempfls3[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls3[i].setType(Field.B_FIELD);
            //addElement(tempfls3[i]);
            fmanager.addFieldLine(tempfls3[i]);

            tempfls4[i] = new FluxFieldLine(flux[i] * fluxScale, pc1, FluxFieldLine.SEARCH_BACK,
                FluxFieldLine.SEARCH_CIRCLE);
            ((FluxFieldLine) tempfls4[i]).setCircleSearchStart(FluxFieldLine.CIRCLE_SEARCH_DOWN);
            //((FluxFieldLine)tempfls4[i]).setReference(pc1);
            //((FluxFieldLine)tempfls2[i]).setObjPos(startPoint);
            //((FluxFieldLine)tempfls2[i]).setSearchAxis(new Vector3d(-1.0,0.,0.));
            //((FluxFieldLine)tempfls2[i]).setObjRadius(8.);
            //((FluxFieldLine)tempfls4[i]).setSearchMode(FluxFieldLine.SEARCH_CIRCLE);
            //((FluxFieldLine)tempfls4[i]).setSearchDir(FluxFieldLine.SEARCH_BACK);
            //((FluxFieldLine)tempfls4[i]).setFluxValue(flux[i]*fluxScale);
            ((FluxFieldLine) tempfls4[i]).setKMax(60);
            ((FluxFieldLine) tempfls4[i]).setSArc(0.05);
            tempfls4[i].setMinDistance(0.05);
            ((FluxFieldLine) tempfls4[i]).setBuildDir(FieldLine.BUILD_BOTH);
            ((FluxFieldLine) tempfls4[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls4[i].setType(Field.B_FIELD);
            //addElement(tempfls4[i]);
            fmanager.addFieldLine(tempfls4[i]);
            //((FluxFieldLine)tempfls3[i]).setDrawn(true);
            //((FluxFieldLine)tempfls4[i]).setDrawn(true);

            tempfls5[i] = new FluxFieldLine(flux[i] * fluxScale, pc1, FluxFieldLine.SEARCH_FORWARD,
                FluxFieldLine.SEARCH_CIRCLE);
            //((FluxFieldLine)tempfls5[i]).setCircleSearchStart(FluxFieldLine.CIRCLE_SEARCH_DOWN);
            //((FluxFieldLine)tempfls4[i]).setReference(pc1);
            //((FluxFieldLine)tempfls2[i]).setObjPos(startPoint);
            //((FluxFieldLine)tempfls2[i]).setSearchAxis(new Vector3d(-1.0,0.,0.));
            //((FluxFieldLine)tempfls2[i]).setObjRadius(8.);
            //((FluxFieldLine)tempfls4[i]).setSearchMode(FluxFieldLine.SEARCH_CIRCLE);
            //((FluxFieldLine)tempfls4[i]).setSearchDir(FluxFieldLine.SEARCH_BACK);
            //((FluxFieldLine)tempfls4[i]).setFluxValue(flux[i]*fluxScale);
            ((FluxFieldLine) tempfls5[i]).setKMax(60);
            ((FluxFieldLine) tempfls5[i]).setSArc(0.05);
            tempfls5[i].setMinDistance(0.05);
            ((FluxFieldLine) tempfls5[i]).setBuildDir(FieldLine.BUILD_BOTH);
            ((FluxFieldLine) tempfls5[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls5[i].setType(Field.B_FIELD);
            //addElement(tempfls5[i]);
            fmanager.addFieldLine(tempfls5[i]);
            //((FluxFieldLine)tempfls3[i]).setDrawn(true);
            //((FluxFieldLine)tempfls4[i]).setDrawn(true);

            tempfls6[i] = new FluxFieldLine(flux[i] * fluxScale, pc1, FluxFieldLine.SEARCH_BACK,
                FluxFieldLine.SEARCH_CIRCLE);
            //((FluxFieldLine)tempfls6[i]).setCircleSearchStart(FluxFieldLine.CIRCLE_SEARCH_DOWN);
            //((FluxFieldLine)tempfls4[i]).setReference(pc1);
            //((FluxFieldLine)tempfls2[i]).setObjPos(startPoint);
            //((FluxFieldLine)tempfls2[i]).setSearchAxis(new Vector3d(-1.0,0.,0.));
            //((FluxFieldLine)tempfls2[i]).setObjRadius(8.);
            //((FluxFieldLine)tempfls4[i]).setSearchMode(FluxFieldLine.SEARCH_CIRCLE);
            //((FluxFieldLine)tempfls4[i]).setSearchDir(FluxFieldLine.SEARCH_BACK);
            //((FluxFieldLine)tempfls4[i]).setFluxValue(flux[i]*fluxScale);
            ((FluxFieldLine) tempfls6[i]).setKMax(60);
            ((FluxFieldLine) tempfls6[i]).setSArc(0.05);
            tempfls6[i].setMinDistance(0.05);
            ((FluxFieldLine) tempfls6[i]).setBuildDir(FieldLine.BUILD_BOTH);
            ((FluxFieldLine) tempfls6[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls6[i].setType(Field.B_FIELD);
            //addElement(tempfls6[i]);
            fmanager.addFieldLine(tempfls6[i]);
            //((FluxFieldLine)tempfls3[i]).setDrawn(true);
            //((FluxFieldLine)tempfls4[i]).setDrawn(true);

        }
        addElement(fmanager);
    }

    void configureFieldLines(double pccharge, double fieldcharge) {
        if (fieldcharge < 0) {
            for (int i = 0; i < flux.length; i++) {
                ((FluxFieldLine) tempfls1[i]).setFluxValue(-flux[i] * fluxScale);
                ((FluxFieldLine) tempfls2[i]).setFluxValue(-flux[i] * fluxScale);

                ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
            }
        } else if (fieldcharge > 0) {
            for (int i = 0; i < flux.length; i++) {
                ((FluxFieldLine) tempfls1[i]).setFluxValue(flux[i] * fluxScale);
                ((FluxFieldLine) tempfls2[i]).setFluxValue(flux[i] * fluxScale);

                ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
                ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
            }
        }
    }

    boolean state = false;

    public void propertyChange(PropertyChangeEvent pce) {

        Object source = pce.getSource();

        if (source == BField) {
            if (pce.getPropertyName().compareTo("magnitude") == 0) {
                Double val = (Double) pce.getNewValue();
                configureFieldLines(pc1.getMu(), val.doubleValue());

            }

        }
        if (source == pc1) {
            if (pce.getPropertyName().compareTo("mu") == 0) {
                Double val = (Double) pce.getNewValue();
                configureFieldLines(val.doubleValue(), BField.getMagnitude());
            }
        }

    }

    void addActions() {
        TealAction ta = new TealAction("Coulomb's Law", this);
        addAction("Help", ta);

    }

    boolean showFieldLines = true;

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Coulomb's Law") == 0) {

        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("www/help/pchargehelp.html");
        	}

        } else {
            super.actionPerformed(e);
        }
    }

    

}