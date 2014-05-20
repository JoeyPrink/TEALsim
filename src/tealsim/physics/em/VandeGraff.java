/* $Id: VandeGraff.java,v 1.15 2010/08/10 18:12:34 stefan Exp $ */

/**
 * 
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.15 $
 */

package tealsim.physics.em;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import teal.render.BoundingSphere;
import javax.media.j3d.Transform3D;
import javax.swing.JLabel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.field.Field;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.plot.Graph;
import teal.plot.TwoBodyEnergyPlot;
import teal.render.scene.TNode3D;
import teal.sim.SimRendered;
import teal.sim.collision.SphereCollisionController;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.em.EField;
import teal.sim.engine.TEngineControl;
import teal.sim.engine.GenericForce;
import teal.sim.engine.SimEngine;
import teal.physics.physical.RectangularBox;
import teal.physics.physical.Wall;
import teal.physics.em.PointCharge;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FluxFieldLine;
import teal.ui.control.PropertyDouble;
import teal.ui.swing.JTaskPaneGroup;
import teal.util.URLGenerator;
import teal.visualization.dlic.DLIC;

public class VandeGraff extends SimEM {

    private static final long serialVersionUID = 3691037668947473206L;

    PropertyDouble slider;
    Graph graph;
    TwoBodyEnergyPlot eGraph;
    JTaskPaneGroup params, graphs;
    VisualizationControl visControl;
    FieldLineManager fmanager;
    FieldConvolution mDLIC;
    
    PointCharge pointCharge;
    PointCharge imageCharge;
    PointCharge vdgCharge;

    Wall mWall;
    SimRendered vgModel;
    TNode3D node1;
    double vdgBaseCharge;
    double scale = 0.25;
    Vector3d modelPosition;
    Vector3d pcPosition;
    Vector3d wallPosition;
    Vector3d cameraPosition;
    double pcCharge = 1.;
    double vdgRadius = 1.;

    // Viewer bounds.
    //Rectangle viewerBounds = null;

	public VandeGraff() {

        super();
        title = "Van de Graff Generator";
        setID("Van de Graff Generator");

        // ***************************************************************************
        // Parameters.
        // ***************************************************************************
        modelPosition = new Vector3d(0., -2.4, 0.);
        pcPosition = new Vector3d(0., 6., 0.);
        wallPosition = new Vector3d(0., 5.8, 0.);

        // ***************************************************************************
        // World, viewer, DLIC and debug configuration.
        // ***************************************************************************
        
        setDamping(0.0);
        setGravity(new Vector3d());
        setBoundingArea(new BoundingSphere(new Point3d(0., 5., 0.), 16));
        setDeltaTime(0.5);
//        setShowTime(false);
//        setFrameRate(40.);

//        theEngine.addPropertyChangeListener("simState", this);
        
        
        mDLIC = new FieldConvolution();
        //mDLIC.setSize(new Dimension(512, 512));
//        mDLIC.setField((EField) theEngine.getEField());
        mDLIC.setField(theEngine.getElementByType(EField.class));
        mDLIC.setComputePlane(new RectangularPlane(theEngine.getBoundingArea()));
        setViewerSize( 450, 450);
        

        // ***************************************************************************
        // Generic force (this might need to be substituted by gravity).
        // ***************************************************************************
        addElement(new GenericForce(new Vector3d(0.0, -0.04, 0.)));

        // ***************************************************************************
        // Image charge.
        // ***************************************************************************
        imageCharge = new PointCharge();
        imageCharge.setID("imageCharge");
        imageCharge.setCharge(-pcCharge * vdgRadius / pcPosition.y);
        imageCharge.setPosition(new Vector3d(0., vdgRadius * vdgRadius / pcPosition.y, 0.));
        imageCharge.setPickable(false);
        imageCharge.setMoveable(false);
        addElement(imageCharge);

        // ***************************************************************************
        // Point charge.
        // ***************************************************************************
        pointCharge = new PointCharge();
        pointCharge.setID("pointCharge");
        pointCharge.setCharge(pcCharge);
        pointCharge.setPosition(pcPosition, true);
        pointCharge.setPickable(true);
        pointCharge.setRadius(0.2);
        pointCharge.setMass(1.0);
        pointCharge.setDrawn(true);
        pointCharge.addPropertyChangeListener("position", this);
        pointCharge.addPropertyChangeListener("charge", this);
        SphereCollisionController scd = new SphereCollisionController(pointCharge);
        scd.setRadius(0.2);
        pointCharge.setCollisionController(scd);
        pointCharge.setColliding(true);
        addElement(pointCharge);

        // ***************************************************************************
        // Stop plate.
        // ***************************************************************************
        RectangularBox stopPlateGenerator = new RectangularBox();
        Vector3d boxPosition = new Vector3d(wallPosition);
        // Either inform objects that they are adhered to each other, or place them
        // slightly apart, so that the situation is detected during the simulation.
        boxPosition.y -= 0.125; // + 0.001;
        stopPlateGenerator.setPosition(boxPosition);
        stopPlateGenerator.setOrientation(new Vector3d(1., 0., 0.));
        stopPlateGenerator.setNormal(new Vector3d(0., 1., 0.));
        stopPlateGenerator.setLength(2.);
        stopPlateGenerator.setWidth(2.);
        stopPlateGenerator.setHeight(0.25);
        Collection<Wall> stopPlateWalls = stopPlateGenerator.getWalls();
        double max_ = Double.NEGATIVE_INFINITY;
        Iterator<Wall> wallIt = stopPlateWalls.iterator();
        while(wallIt.hasNext()){
            Wall currentWall = wallIt.next();
            double y = currentWall.getPosition().y;
            if (y > max_) {
                max_ = y;
                mWall = currentWall;
            }
            currentWall.setElasticity(0.);
        }
        addElements(stopPlateWalls);
        // Either inform objects that they are adhered to each other, or place them
        // slightly apart, so that the situation is detected during the simulation.
        mWall.addAdheredObject(pointCharge);
        pointCharge.addAdheredObject(mWall);

        // ***************************************************************************
        // Van de Graff chrage.
        // ***************************************************************************
        vdgCharge = new PointCharge();
        vdgCharge.setID("vdgCharge");
        vdgCharge.setCharge(5. + pcCharge * vdgRadius / pcPosition.y);
        vdgCharge.setPosition(new Vector3d(0., 0.0, 0.));
        vdgCharge.setPickable(false);
        vdgCharge.setMoveable(false);
        vdgCharge.setRadius(0.1);
        addElement(vdgCharge);

        // ***************************************************************************
        // Field lines.
        // ***************************************************************************
        double[] pcflux = new double[2];
//        pcflux[0] = 0.2 * theEngine.getEField().getFlux(new Vector3d(1., 4., 0.));
        pcflux[0] = 0.2 * theEngine.getElementByType(EField.class).getFlux(new Vector3d(1., 4., 0.));
//        pcflux[1] = 0.8 * theEngine.getEField().getFlux(new Vector3d(1., 5., 0.));
        pcflux[1] = 0.8 * theEngine.getElementByType(EField.class).getFlux(new Vector3d(1., 5., 0.));
        double[] vdgflux = new double[10];
//        vdgflux[0] = 1.4 * theEngine.getEField().getFlux(new Vector3d(1., 1., 0.));
        vdgflux[0] = 1.4 * theEngine.getElementByType(EField.class).getFlux(new Vector3d(1., 1., 0.));
//        vdgflux[1] = 1.0 * theEngine.getEField().getFlux(new Vector3d(2., -1., 0.));
        vdgflux[1] = 1.0 * theEngine.getElementByType(EField.class).getFlux(new Vector3d(2., -1., 0.));
        vdgflux[2] = 17.0;
        vdgflux[3] = 28.0;
        vdgflux[4] = 40.0;
        vdgflux[5] = 55.0;
        vdgflux[6] = 70.0;
        vdgflux[7] = 85.0;
        vdgflux[8] = 100.0;

        fmanager = new FieldLineManager();
        fmanager.setElementManager(this);
        fmanager.setSymmetryCount(2);
       
        for (int i = 0; i < 9; i++) {
            FieldLine fl = new FluxFieldLine(vdgflux[i], vdgCharge, true, false);
            fl.setType(Field.E_FIELD);
            fl.setBuildDir(FluxFieldLine.BUILD_POSITIVE);
            fl.setKMax(400);
            fl.setSArc(0.2);
            fl.setMinDistance(0.001);
            
            fmanager.addFieldLine(fl);

            if (i < pcflux.length) {
                FieldLine pcfl = new FluxFieldLine(pcflux[i], pointCharge, true, false);
                FieldLine pcfl2 = new FluxFieldLine(pcflux[i], pointCharge, false, false);
                pcfl.setType(Field.E_FIELD);
                pcfl2.setType(Field.E_FIELD);
                pcfl.setBuildDir(FluxFieldLine.BUILD_POSITIVE);
                pcfl2.setBuildDir(FluxFieldLine.BUILD_POSITIVE);

                pcfl.setKMax(400);
                pcfl2.setKMax(400);
                pcfl.setMinDistance(0.01);
                pcfl2.setMinDistance(0.01);
                pcfl.setSArc(0.2);
                pcfl2.setSArc(0.2);

                fmanager.addFieldLine(pcfl);
                fmanager.addFieldLine(pcfl2);
            }
            
        }
        fmanager.setColorMode(false);
        fmanager.setSymmetryCount(2);
        
        // ***************************************************************************
        // Create a Rendered3D to hold the Max model.
        // ***************************************************************************
        Transform3D trans = new Transform3D();
        trans.setScale(scale);
        vgModel = new SimRendered();
        vgModel.setURL(URLGenerator.getResource("models/VDG_Stand_inB.3DS"));
        vgModel.setPickable(false);
        vgModel.setMoveable(false);
        vgModel.setModelOffsetTransform(trans);
        vgModel.setPosition(modelPosition);
        addElement(vgModel);

        // ***************************************************************************
        // Graph
        // ***************************************************************************
        graph = new Graph();
        //graph.setBounds(500, 68, 400, 360);
        graph.setXRange(0., 125.);
        graph.setYRange(-0.005, 0.02);
        graph.setXLabel("Time");
        graph.setYLabel("Energy");
 
        JLabel label1 = new JLabel("Electric Energy");
        label1.setForeground(Color.RED);
        //label1.setBounds(660, 20, 200, 24);
        label1.setFont(label1.getFont().deriveFont(Font.BOLD));
        JLabel label2 = new JLabel("Kinetic + Gravitational Energy");
        label2.setForeground(Color.BLUE);
        //label2.setBounds(625, 44, 200, 24);
        label2.setFont(label2.getFont().deriveFont(Font.BOLD));

        eGraph = new TwoBodyEnergyPlot();
        eGraph.setPlotValue(0);
        eGraph.setBodyOne(pointCharge);
        eGraph.setBodyTwo(vdgCharge);
        eGraph.setIndObj(theEngine);
        graph.addPlotItem(eGraph);

        // ***************************************************************************
        // Slider.
        // ***************************************************************************
        slider = new PropertyDouble();
        slider.setText("VDG Charge (\u03BCC):");

        slider.setMinimum(0.);
        slider.setMaximum(50.);
        //slider.setBounds(35, 530, 415, 50);
        slider.setPaintTicks(true);
        slider.setBorder(null);
        slider.addRoute(vdgCharge, "charge");
        slider.addPropertyChangeListener("value", this);
        slider.setValue(1.0);
        slider.setVisible(true);
        slider.setEnabled(false);

        params = new JTaskPaneGroup();
        params.setText("Parameters");
        params.add(slider);
        graphs = new JTaskPaneGroup();
        graphs.setText("Graph");
        graphs.add(label1);
        graphs.add(label2);
        graphs.add(graph);
        // Hack to get around not adding graph as element
        theEngine.addSimElement(graph);
        visControl = new VisualizationControl();
        visControl.setConvolutionModes(DLIC.DLIC_FLAG_E|DLIC.DLIC_FLAG_EP);
        visControl.setActionFlags(VisualizationControl.CHANGE_FL_COLORMODE);
        visControl.setFieldConvolution(mDLIC);
        visControl.setSymmetryCount(2);
        visControl.setFieldLineManager(fmanager);
        visControl.setColorPerVertex(false);
        addElement(graphs);
        addElement(params);
        addElement(visControl);
      

        // ***************************************************************************
        // Launching
        // ***************************************************************************
        //viewerBounds = mViewer.getBounds(); // This line required for maximizing.
        addActions();
//        mSEC.init();
        pointCharge.setPosition(pcPosition);
        pointCharge.setVelocity(new Vector3d());
        mWall.addAdheredObject(pointCharge);
        pointCharge.addAdheredObject(mWall);
        resetCamera();
        slider.setValue(0.);

    }

    /*
     public void setScale(double s) {
     scale = s;
     node1.setScale(scale);
     }
     public double getScale() {
     return scale;
     }
     */
    void addActions() {
        TealAction ta = new TealAction("Van de Graff", this);
        addAction("Help", ta);
        ta = new TealAction("Full Screen", this);
        addAction("View", ta);
        ta = new TealAction("Normal", this);
        addAction("View", ta);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("Van de Graff") == 0) {

        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/vandegraff.html");
        	}
        }       
        else {
            super.actionPerformed(e);
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {

        Object source = pce.getSource();
        if (source == pointCharge) {
            /*
             
             */
            if (pce.getPropertyName().compareTo("charge") == 0) {
                // imageCharge.charge = -(pointCharge.charge * ( vdgRadius/ pointCharge.y))
                Double val = (Double) pce.getNewValue();
                Vector3d pcp = pointCharge.getPosition();
                double c = -val.doubleValue() * (vdgRadius / pcp.y);
                imageCharge.setCharge(c);
            } else if (pce.getPropertyName().compareTo("position") == 0) {
                Vector3d pos = (Vector3d) pce.getNewValue();
                double y = vdgRadius * vdgRadius / pos.y;
                imageCharge.setY(y);
            }
        } else if (source == vdgCharge) {
            if (pce.getPropertyName().compareTo("charge") == 0) {
                Double val = (Double) pce.getNewValue();
                vdgBaseCharge = val.doubleValue();
                //double modifier = pointCharge.getCharge() / pointCharge.getPosition().y;
                double modifier = pointCharge.getCharge() * vdgRadius / pointCharge.getPosition().y;
                vdgCharge.setCharge((vdgBaseCharge + modifier));
            }
        } else if (source == slider) {
            graph.clear(0);
            graph.clear(1);
            graph.repaint();
        } else if (source == theEngine) {
            if (pce.getPropertyName().compareTo("simState") == 0) {
                Integer val = (Integer) pce.getNewValue();
                int state = val.intValue();
                switch (state) {
                    case TEngineControl.NOT:
                        slider.setEnabled(false);
                        break;
                    case TEngineControl.INIT:
                        slider.setEnabled(true);
                        break;
                    case TEngineControl.RUNNING:
                        slider.setEnabled(false);
                        break;
                    case TEngineControl.PAUSED:
                        slider.setEnabled(true);
                        break;
                    case TEngineControl.ENDED:
                        break;
                }
            }
        } else {
            super.propertyChange(pce);
        }
    }

    public void reset() {
        mSEC.stop();
        pointCharge.setPosition(pcPosition);
        pointCharge.setVelocity(new Vector3d());
        mWall.addAdheredObject(pointCharge);
        pointCharge.addAdheredObject(mWall);
        resetCamera();
        slider.setValue(0.);
        theEngine.requestReorder(pointCharge);
        theEngine.requestRefresh();
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, 0.3, 1.8), new Point3d(0., 0.3, 0.), new Vector3d(0., 1., 0.));

    }

}
