/* $Id: CoulombsLaw.java,v 1.12 2010/08/10 18:12:33 stefan Exp $ */

package tealsim.physics.em;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.EngineObj;
import teal.physics.em.EField;
import teal.physics.em.SimEM;
import teal.physics.em.CylindricalField;
import teal.physics.em.GeneratesE;
import teal.physics.em.PointCharge;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FluxFieldLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

/**
 * @author pbailey
 * @version $Revision: 1.12 $
 */

public class CoulombsLaw extends SimEM {

    private static final long serialVersionUID = 3760568602377532465L;

    JButton dlic_But;
    JCheckBox showFLBut;
    protected FieldConvolution mDLIC = null;
    protected FieldDirectionGrid fv = null;
    PointCharge pc1;

    CylindricalField cylField;
    double[] flux = new double[10];
  
    FieldLine[] tempfls1 = new FieldLine[10];
    FieldLine[] tempfls2 = new FieldLine[10];
    FieldLine[] tempfls3 = new FieldLine[10];
    FieldLine[] tempfls4 = new FieldLine[10];
    FieldLineManager fmanager;

    public CoulombsLaw() {

        super();

        title = "Coulomb's Law";
        TDebug.setGlobalLevel(0);

        // Turn off SimModelController
        mSEC.setVisible(false);
        setDeltaTime(0.5);
        BoundingSphere bs = new BoundingSphere(new Point3d(0, 0, 0), 1.5);
        setBoundingArea(bs);
        RectangularPlane recPlane = new RectangularPlane(bs);
        mDLIC = new FieldConvolution();
        mDLIC.setComputePlane(recPlane);

        pc1 = new PointCharge();
        pc1.addPropertyChangeListener("charge", this);
        addElement(pc1);

        //UniformEField field = new UniformEField();

        cylField = new CylindricalField(new Vector3d(0., 0., 0.), new Vector3d(0, 1., 0.), 0.2);
        cylField.setID("cylField");
        cylField.addPropertyChangeListener("magnitude", this);
        addElement(cylField);
        
        fv = new FieldDirectionGrid();
        fv.setType(teal.field.Field.E_FIELD);
        fv.setDrawn(false);
        addElement(fv);
        
        
      
        //initFieldLines();


        PropertyDouble pcSlider = new PropertyDouble();
        pcSlider.setMinimum(-5);
        pcSlider.setMaximum(5);
        pcSlider.setPaintTicks(true);
        pcSlider.addRoute("value", pc1, "charge");
        pcSlider.setValue(0.1);
        pcSlider.setText("Point Charge");

        PropertyDouble fcSlider = new PropertyDouble();
        fcSlider.setMinimum(-0.4);
        fcSlider.setMaximum(0.4);
        fcSlider.setPaintTicks(true);
        fcSlider.addRoute("value", cylField, "magnitude");
        fcSlider.setValue(-0.1);
        fcSlider.setText("Vertical Field");


        addActions();

        
        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        controls.add(pcSlider);
        controls.add(fcSlider);
        addElement(controls);
        

        fmanager = new FieldLineManager();
        addElement(fmanager);

        VisualizationControl vizPanel = new VisualizationControl();
        vizPanel.setFieldLineManager(fmanager);
        vizPanel.setColorPerVertex(false);
        vizPanel.setSymmetryCount(2);
        vizPanel.setFieldConvolution(mDLIC);
        vizPanel.setConvolutionModes(DLIC.DLIC_FLAG_E);
        vizPanel.setFieldVisGrid(fv);
        vizPanel.setShowFV(false);
        vizPanel.setActionFlags(0);
        addElement(vizPanel);
        

        //mSEC.init();

    }
    public void initialize(){
    	super.initialize();
    	initFieldLines();
    }

    void initFieldLines() {
        Vector3d startPoint;
       

        /*
        flux[0] = 0.; //theEngine.getEField().getFlux(new Vector3d(1.5,0.,0.));
        flux[1] = 0.; //theEngine.getEField().getFlux(new Vector3d(2.,0.,0.));
        flux[2] = theEngine.getEField().getFlux(new Vector3d(4.0, 0., 0.));
        flux[3] = theEngine.getEField().getFlux(new Vector3d(6.0, 0., 0.));
        flux[4] = theEngine.getEField().getFlux(new Vector3d(8.0, 0., 0.));
        flux[5] = theEngine.getEField().getFlux(new Vector3d(10., 0., 0.));
        flux[6] = theEngine.getEField().getFlux(new Vector3d(12., 0., 0.));
        flux[7] = theEngine.getEField().getFlux(new Vector3d(14., 0., 0.));
        flux[8] = theEngine.getEField().getFlux(new Vector3d(2., 0., 0.));
        flux[9] = theEngine.getEField().getFlux(new Vector3d(.5, 0., 0.));
        */
        flux[0] = 0.; //theEngine.getEField().getFlux(new Vector3d(1.5,0.,0.));
        flux[1] = 0.; //theEngine.getEField().getFlux(new Vector3d(2.,0.,0.));
        flux[2] = theEngine.getElementByType(EField.class).getFlux(new Vector3d(4.0, 0., 0.));
        flux[3] = theEngine.getElementByType(EField.class).getFlux(new Vector3d(6.0, 0., 0.));
        flux[4] = theEngine.getElementByType(EField.class).getFlux(new Vector3d(8.0, 0., 0.));
        flux[5] = theEngine.getElementByType(EField.class).getFlux(new Vector3d(10., 0., 0.));
        flux[6] = theEngine.getElementByType(EField.class).getFlux(new Vector3d(12., 0., 0.));
        flux[7] = theEngine.getElementByType(EField.class).getFlux(new Vector3d(14., 0., 0.));
        flux[8] = theEngine.getElementByType(EField.class).getFlux(new Vector3d(2., 0., 0.));
        flux[9] = theEngine.getElementByType(EField.class).getFlux(new Vector3d(.5, 0., 0.));

       
        for (int i = 0; i < flux.length; i++) {
            startPoint = new Vector3d(0.0001, 3.0, 0.);
            tempfls1[i] = new FluxFieldLine(flux[i] * 0.02, startPoint, new Vector3d(1., 0., 0.), 8.0);

            ((FluxFieldLine) tempfls1[i]).setKMax(60);
            ((FluxFieldLine) tempfls1[i]).setSArc(0.1);
            ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
            ((FluxFieldLine) tempfls1[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls1[i].setType(Field.E_FIELD);
            tempfls1[i].setMinDistance(tempfls1[i].getMinDistance() * 0.5);
            addElement(tempfls1[i]);

            startPoint = new Vector3d(-0.0001, 3.0, 0.);

            tempfls2[i] = new FluxFieldLine(flux[i] * 0.02, startPoint, new Vector3d(-1., 0., 0.), 8.0);
            ((FluxFieldLine) tempfls2[i]).setSearchAxis(new Vector3d(-1.0, 0., 0.));
            ((FluxFieldLine) tempfls2[i]).setFluxValue(flux[i] * 0.02);
            ((FluxFieldLine) tempfls2[i]).setKMax(60);
            ((FluxFieldLine) tempfls2[i]).setSArc(0.1);
            ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
            ((FluxFieldLine) tempfls2[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls2[i].setType(Field.E_FIELD);
            tempfls2[i].setMinDistance(tempfls2[i].getMinDistance() * 0.5);
            addElement(tempfls2[i]);

            tempfls3[i] = new FluxFieldLine(flux[i] * 0.02, pc1, FluxFieldLine.SEARCH_FORWARD,
                FluxFieldLine.SEARCH_CIRCLE);
            ((FluxFieldLine) tempfls3[i]).setKMax(60);
            ((FluxFieldLine) tempfls3[i]).setSArc(0.1);
            ((FluxFieldLine) tempfls3[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
            ((FluxFieldLine) tempfls3[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls3[i].setType(Field.E_FIELD);
            tempfls3[i].setMinDistance(tempfls3[i].getMinDistance() * 0.5);
            addElement(tempfls3[i]);

            tempfls4[i] = new FluxFieldLine(flux[i] * 0.02, pc1, FluxFieldLine.SEARCH_BACK, FluxFieldLine.SEARCH_CIRCLE);
            ((FluxFieldLine) tempfls4[i]).setKMax(60);
            ((FluxFieldLine) tempfls4[i]).setSArc(0.1);
            ((FluxFieldLine) tempfls4[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
            ((FluxFieldLine) tempfls4[i]).setIntegrationMode(FieldLine.RUNGE_KUTTA);
            tempfls4[i].setType(Field.E_FIELD);
            tempfls4[i].setMinDistance(tempfls4[i].getMinDistance() * 0.5);
            addElement(tempfls4[i]);

            fmanager.addFieldLine(tempfls1[i]);
            fmanager.addFieldLine(tempfls2[i]);
            fmanager.addFieldLine(tempfls3[i]);
            fmanager.addFieldLine(tempfls4[i]);

        }

        
    }

    void configureFieldLines(double pccharge, double fieldcharge) {
        pc1.setFluxMode(0);
        Vector3d startPoint;
        if (fieldcharge > 0.) {
            if (pccharge > 0.) {
                //Vector3d startPoint;
                pc1.setFluxMode(1);
                for (int i = 0; i < flux.length; i++) {
                    startPoint = new Vector3d(0.0001, -3.0, 0.);
                    ((FluxFieldLine) tempfls1[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls1[i]).setSearchAxis(new Vector3d(1.0, 0., 0.));
                    ((FluxFieldLine) tempfls1[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls1[i]).setKMax(60);
                    ((FluxFieldLine) tempfls1[i]).setSArc(0.1);
                    ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_POSITIVE);

                    startPoint = new Vector3d(-0.0001, -3.0, 0.);
                    ((FluxFieldLine) tempfls2[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls2[i]).setSearchAxis(new Vector3d(-1.0, 0., 0.));
                    ((FluxFieldLine) tempfls2[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls2[i]).setKMax(60);
                    ((FluxFieldLine) tempfls2[i]).setSArc(0.1);
                    ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    ((FluxFieldLine) tempfls3[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls3[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    ((FluxFieldLine) tempfls4[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls4[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    ((FluxFieldLine) tempfls1[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls2[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls3[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls4[i]).setDrawn(true);
                }
            } else if (pccharge < 0.) {
                for (int i = 0; i < flux.length; i++) {
                    startPoint = new Vector3d(0.0001, 3.0, 0.);
                    ((FluxFieldLine) tempfls1[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls1[i]).setSearchAxis(new Vector3d(1.0, 0., 0.));
                    ((FluxFieldLine) tempfls1[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls1[i]).setKMax(60);
                    ((FluxFieldLine) tempfls1[i]).setSArc(0.1);
                    ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
                    startPoint = new Vector3d(-0.0001, 3.0, 0.);
                    ((FluxFieldLine) tempfls2[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls2[i]).setSearchAxis(new Vector3d(-1.0, 0., 0.));

                    ((FluxFieldLine) tempfls2[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls2[i]).setKMax(60);
                    ((FluxFieldLine) tempfls2[i]).setSArc(0.1);
                    ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);

                    ((FluxFieldLine) tempfls3[i]).setSearchDir(FluxFieldLine.SEARCH_FORWARD);
                    ((FluxFieldLine) tempfls3[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls3[i]).setKMax(60);
                    ((FluxFieldLine) tempfls3[i]).setSArc(0.1);
                    ((FluxFieldLine) tempfls3[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);

                    ((FluxFieldLine) tempfls4[i]).setSearchDir(FluxFieldLine.SEARCH_BACK);
                    ((FluxFieldLine) tempfls4[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls4[i]).setKMax(60);
                    ((FluxFieldLine) tempfls4[i]).setSArc(0.1);
                    ((FluxFieldLine) tempfls4[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);

                    ((FluxFieldLine) tempfls1[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls2[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls3[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls4[i]).setDrawn(true);
                }
            } else if (pccharge == 0.) {

                for (int i = 0; i < flux.length; i++) {
                    startPoint = new Vector3d(0.0001, 3.0, 0.);

                    ((FluxFieldLine) tempfls1[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls1[i]).setSearchAxis(new Vector3d(1.0, 0., 0.));

                    ((FluxFieldLine) tempfls1[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls1[i]).setKMax(60);
                    ((FluxFieldLine) tempfls1[i]).setSArc(0.1);
                    ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);

                    startPoint = new Vector3d(-0.0001, 3.0, 0.);

                    ((FluxFieldLine) tempfls2[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls2[i]).setSearchAxis(new Vector3d(-1.0, 0., 0.));

                    ((FluxFieldLine) tempfls2[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls2[i]).setKMax(60);
                    ((FluxFieldLine) tempfls2[i]).setSArc(0.1);
                    ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);

                    ((FluxFieldLine) tempfls1[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls2[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls3[i]).setDrawn(false);
                    ((FluxFieldLine) tempfls4[i]).setDrawn(false);
                }
            }
        } else if (fieldcharge < 0.) {
            if (pccharge > 0.) {
                for (int i = 0; i < flux.length; i++) {
                    startPoint = new Vector3d(0.0001, 3.0, 0.);
                    ((FluxFieldLine) tempfls1[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls1[i]).setSearchAxis(new Vector3d(1.0, 0., 0.));
                    ((FluxFieldLine) tempfls1[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    startPoint = new Vector3d(-0.0001, 3.0, 0.);
                    ((FluxFieldLine) tempfls2[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls2[i]).setSearchAxis(new Vector3d(-1.0, 0., 0.));
                    ((FluxFieldLine) tempfls2[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    ((FluxFieldLine) tempfls3[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls3[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    ((FluxFieldLine) tempfls4[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls4[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    ((FluxFieldLine) tempfls1[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls2[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls3[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls4[i]).setDrawn(true);
                }
            } else if (pccharge < 0.) {
                for (int i = 0; i < flux.length; i++) {
                    pc1.setFluxMode(1);
                    startPoint = new Vector3d(0.0001, -3.0, 0.);
                    ((FluxFieldLine) tempfls1[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls1[i]).setSearchAxis(new Vector3d(1.0, 0., 0.));
                    ((FluxFieldLine) tempfls1[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
                    startPoint = new Vector3d(-0.0001, -3.0, 0.);
                    ((FluxFieldLine) tempfls2[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls2[i]).setSearchAxis(new Vector3d(-1.0, 0., 0.));
                    ((FluxFieldLine) tempfls2[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
                    ((FluxFieldLine) tempfls3[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls3[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
                    ((FluxFieldLine) tempfls4[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls4[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
                    ((FluxFieldLine) tempfls1[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls2[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls3[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls4[i]).setDrawn(true);
                }
            } else if (pccharge == 0.) {
                for (int i = 0; i < flux.length; i++) {
                    startPoint = new Vector3d(0.0001, 3.0, 0.);
                    ((FluxFieldLine) tempfls1[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls1[i]).setSearchAxis(new Vector3d(1.0, 0., 0.));
                    ((FluxFieldLine) tempfls1[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls1[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    startPoint = new Vector3d(-0.0001, 3.0, 0.);
                    ((FluxFieldLine) tempfls2[i]).setObjPos(startPoint);
                    ((FluxFieldLine) tempfls2[i]).setSearchAxis(new Vector3d(-1.0, 0., 0.));
                    ((FluxFieldLine) tempfls2[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls2[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    ((FluxFieldLine) tempfls1[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls2[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls3[i]).setDrawn(false);
                    ((FluxFieldLine) tempfls4[i]).setDrawn(false);
                }
            }
        } else if (fieldcharge == 0.) {
            if (pccharge > 0.) {
                for (int i = 0; i < flux.length; i++) {
                    ((FluxFieldLine) tempfls1[i]).setDrawn(false);
                    ((FluxFieldLine) tempfls2[i]).setDrawn(false);
                    ((FluxFieldLine) tempfls3[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls3[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    ((FluxFieldLine) tempfls4[i]).setFluxValue(flux[i] * 0.02);
                    ((FluxFieldLine) tempfls4[i]).setBuildDir(FieldLine.BUILD_POSITIVE);
                    ((FluxFieldLine) tempfls3[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls4[i]).setDrawn(true);
                }
            } else if (pccharge < 0.) {
                for (int i = 0; i < flux.length; i++) {
                    ((FluxFieldLine) tempfls1[i]).setDrawn(false);
                    ((FluxFieldLine) tempfls2[i]).setDrawn(false);
                    ((FluxFieldLine) tempfls3[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls3[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
                    ((FluxFieldLine) tempfls4[i]).setFluxValue(-flux[i] * 0.02);
                    ((FluxFieldLine) tempfls4[i]).setBuildDir(FieldLine.BUILD_NEGATIVE);
                    ((FluxFieldLine) tempfls3[i]).setDrawn(true);
                    ((FluxFieldLine) tempfls4[i]).setDrawn(true);
                }
            } else if (pccharge == 0.) {
                for (int i = 0; i < flux.length; i++) {
                    ((FluxFieldLine) tempfls1[i]).setDrawn(false);
                    ((FluxFieldLine) tempfls2[i]).setDrawn(false);
                    ((FluxFieldLine) tempfls3[i]).setDrawn(false);
                    ((FluxFieldLine) tempfls4[i]).setDrawn(false);
                }
            }
        }
    }

    boolean state = false;

    public void propertyChange(PropertyChangeEvent pce) {
        Object source = pce.getSource();
        if (source == cylField) {
            if (pce.getPropertyName().compareTo("magnitude") == 0) {
                Double val = (Double) pce.getNewValue();
                configureFieldLines(pc1.getCharge(), val.doubleValue());
            }
        }
        if (source == pc1) {
            if (pce.getPropertyName().compareTo("charge") == 0) {
                Double val = (Double) pce.getNewValue();
                configureFieldLines(val.doubleValue(), cylField.getMagnitude());
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
        		((TFramework) mFramework).openBrowser("www/help/pchargehelp.html");
        	}
        } else if (e.getActionCommand().compareToIgnoreCase("FM_TOGGLE_LINES") == 0) {
            if (showFieldLines) {
                fmanager.setDrawn(false);
                showFieldLines = false;
            } else {
                fmanager.setDrawn(true);
                showFieldLines = true;
            }
            theEngine.requestSpatial();
            theEngine.requestRefresh();
        } else {
            super.actionPerformed(e);
        }
    }

    public class UniformEField extends EngineObj implements GeneratesE {

        private static final long serialVersionUID = 3907215944341206576L;
        Vector3d value = null;

        public UniformEField() {
            value = new Vector3d();
        }

        public void setValue(Vector3d v) {
            theEngine.requestRefresh();
            value.set(v);
        }

        public void setZ(double v) {
            theEngine.requestRefresh();
            value.y = v;
        }

        public double getZ() {
            return value.y;
        }

        public Vector3d getE(Vector3d position, double time) {
            return new Vector3d(value);
        }

        public Vector3d getE(Vector3d position) {
            return new Vector3d(value);
        }

        public double getEFlux(Vector3d position) {
            return 0;
        }

        public boolean isGeneratingE() {
            return true;
        }

        public double getEPotential(Vector3d position) {
            return 0;
        }
    }

  
}
