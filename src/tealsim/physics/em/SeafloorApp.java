/*
 * Created on Sep 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package tealsim.physics.em;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.plot.SeafloorPlot;
import teal.plot.SpatialGraph;
import teal.render.viewer.TViewer;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.em.EMEngine;
import teal.physics.em.Seafloor;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyCheck;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

/**
 * @author danziger
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SeafloorApp extends SimEM {

    private static final long serialVersionUID = 3688789154683435321L;
    SpatialGraph graph;
    SeafloorPlot plot;
    Seafloor sf;

    PropertyDouble heightslider;
    PropertyCheck arrowtoggle;
    PropertyCheck arrowtoggle2;

    public SeafloorApp() {

        super();
        title = "Seafloor Spreading";
        TDebug.setGlobalLevel(0);

        // Building the world.
     
        theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 10));
        theEngine.setDeltaTime(0.25);
        theEngine.setDamping(0.1);
        theEngine.setGravity(new Vector3d(0., 0., 0.));

        //	theEngine.setShowTime(true);

        setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM | TViewer.VP_TRANSLATE);

        sf = new Seafloor();
        //sf.setDip(new Vector3d(10.,1.,0.));
        sf.setApplication(this);
        sf.addStripe(0.);
        sf.addStripe(0.);
        sf.addStripe(0.);
        sf.addStripe(0.);
        //sf.addStripe(0.);
        //sf.addStripe(0.);
        //sf.addStripe(0.);
        //sf.addStripe(0.);
        //sf.addStripe(0.);
        //sf.getB(new Vector3d(0.,2.,0.));
        //sf.setBEnableCreationVars(true);
        addElement(sf);
        //sf.removeStripe(0);

        graph = new SpatialGraph();
        graph.setXRange(0., 12.);
        graph.setYRange(-0.1, 0.5);
        //graph.setXPersistence(100.0);
        graph.setWrap(false);
        graph.setClearOnWrap(true);
        graph.setXLabel("Position (km)");
        graph.setXticklabelscale(10.);
        graph.setYLabel("Field Magnitude (Gamma)");
        graph.setYticklabelscale(20.);
        graph.setDoubleBuffered(true);
        //graph.addLegend(0,"External Flux");
        //graph.addLegend(1,"Total Flux");
        plot = new SeafloorPlot(sf, 100);
        //plot.setTimeAutoscale(false);
        //plot.setFluxAutoscale(true);
        graph.addPlotItem(plot);
        //addElement(graph);

        FieldDirectionGrid fv = new FieldDirectionGrid();
        fv.setType(teal.field.Field.B_FIELD);
        fv.setResolution(12);
        //fv.setGridIterator(recPlane);
        //addElement(fv);

        //		PropertyInteger slider1 = new PropertyInteger();
        //		slider1.setMinimum(0);
        //		slider1.setMaximum(24);
        //		slider1.setBounds(45, 489, 415,50);
        //		slider1.addRoute("value", fv, "resolution");
        //		slider1.setText("Vector Field Grid");
        //		slider1.setBorder(null);
        //		//addElement(slider1);
        //		slider1.setValue(0);

        PropertyDouble latslider = new PropertyDouble();
        latslider.setMinimum(-90);
        latslider.setMaximum(90);
        //latslider.setBounds(45, 600, 415,50);
        latslider.setSliderWidth(200);
        latslider.addRoute(sf, "latitude");
        latslider.setText("Present Latitude");
        latslider.setBorder(null);
        //addElement(latslider);
        latslider.setValue(90);

        PropertyDouble strikeslider = new PropertyDouble();
        strikeslider.setMinimum(0);
        strikeslider.setMaximum(180);
        //strikeslider.setBounds(45, 620, 415,50);
        strikeslider.setSliderWidth(200);
        strikeslider.addRoute(sf, "strike");
        strikeslider.setText("Present Strike");
        strikeslider.setBorder(null);
        //addElement(strikeslider);
        strikeslider.setValue(90);

        PropertyDouble clatslider = new PropertyDouble();
        clatslider.setMinimum(-90);
        clatslider.setMaximum(90);
        //latslider.setBounds(45, 600, 415,50);
        clatslider.setSliderWidth(200);
        clatslider.addRoute(sf, "creationLatitude");
        clatslider.setText("Formation Latitude");
        clatslider.setBorder(null);
        //addElement(latslider);
        clatslider.setValue(90);

        PropertyDouble cstrikeslider = new PropertyDouble();
        cstrikeslider.setMinimum(0);
        cstrikeslider.setMaximum(180);
        //strikeslider.setBounds(45, 620, 415,50);
        cstrikeslider.setSliderWidth(200);
        cstrikeslider.addRoute(sf, "creationStrike");
        cstrikeslider.setText("Formation Strike");
        cstrikeslider.setBorder(null);
        //addElement(strikeslider);
        cstrikeslider.setValue(90);

        heightslider = new PropertyDouble();
        heightslider.setMinimum(0.);
        heightslider.setMaximum(1.);
        //strikeslider.setBounds(45, 620, 415,50);
        heightslider.addRoute(sf, "scanHeight");
        heightslider.setName("heightslider");
        heightslider.setText("Measurement Height (x10 km)");
        heightslider.setLabelWidth(150);
        heightslider.setBorder(null);
        //addElement(strikeslider);
        heightslider.setValue(0.35);

        PropertyCheck createtoggle = new PropertyCheck();
        createtoggle.setText("Use Formation Variables");
        createtoggle.setLabelWidth(150);
        createtoggle.setID("creationvars");
        createtoggle.addRoute(sf, "bEnableCreationVars");
        createtoggle.setValue(false);
        //createtoggle.addPropertyChangeListener("value", this);

        arrowtoggle = new PropertyCheck();
        arrowtoggle.setText("Show Field Arrows");
        arrowtoggle.setLabelWidth(150);
        arrowtoggle.setID("togglefieldlines");
        arrowtoggle.addRoute(sf, "showScanLineFieldArrows");
        arrowtoggle.setValue(false);

        arrowtoggle2 = new PropertyCheck();
        arrowtoggle2.setText("Show Field Component Arrows");
        arrowtoggle2.setLabelWidth(200);
        arrowtoggle2.setID("togglefieldlines");
        arrowtoggle2.addRoute(sf, "showScanLineCompArrows");
        arrowtoggle2.setValue(true);

        ControlGroup graphgroup = new ControlGroup();
        graphgroup.setText("Magnetic Profile");
        graphgroup.addElement(graph);
        addElement(graphgroup);
        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");
        //controls.add(graph);
        controls.add(latslider);
        controls.add(strikeslider);
        controls.add(clatslider);
        controls.add(cstrikeslider);
        controls.add(heightslider);
        controls.add(createtoggle);
        controls.add(arrowtoggle);
        controls.add(arrowtoggle2);
        //controls.add(but);
        //controls.add(but1);
        addElement(controls);
        
        ArrayList<FieldLine> fls = sf.getFieldlines();
        FieldLineManager flm = new FieldLineManager();
        flm.setElementManager(this);
        flm.setSymmetryCount(0);
        
        Iterator it = fls.iterator();
        while (it.hasNext()) {
        	flm.addFieldLine((FieldLine)it.next());
        }
        
        //flm.addFieldLines(fls);
        flm.setDrawn(true);
        //flm.setColorMode(FieldLine.COLOR_FLAT);
        
        VisualizationControl vis = new VisualizationControl();
        vis.setFieldLineManager(flm);
        vis.setSymmetryCount(0);
        RectangularPlane rec = new RectangularPlane(new Vector3d(-6., -6., 0.),
				new Vector3d(-6., 6., 0.), new Vector3d(6., 6., 0.));
		FieldConvolution mDLIC = new FieldConvolution();
		mDLIC.setSize(new Dimension(1024, 1024));
		mDLIC.setVisible(false);
		mDLIC.setComputePlane(rec);
        vis.setFieldConvolution(mDLIC);
		vis.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
		// addElement() adds the VisualizationControl Group to the application.
        addElement(vis);
        
        
        //taskPane.add(graphgroup);
        //taskPane.add(controls);
        //taskPane.add(vis);
        //addElement(taskPane);

        //JScrollPane scroll = new JScrollPane(taskPane);
        //scroll.setBorder(null);

        //addElement(scroll);

        //mDLIC.setField((BField) theEngine.getBField());
        //mDLIC.setField()
        //mDLIC.setComputePlane(new RectangularPlane(theEngine.getBoundingArea()));
        //		 Launch
        setShowGizmos(false);
        mSEC.setVisible(false);
        //addActions();
        reset();
        resetCamera();
        mSEC.init();

    }

    void addActions() {
        TealAction ta = new TealAction("Seafloor Spreading", this);
        addAction("Help", ta);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println("Action: " + command);
        if (e.getActionCommand().compareToIgnoreCase("Seafloor Spreading") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/seafloor.html");
        	}

        } else {
            super.actionPerformed(e);
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getSource() == theEngine && pce.getPropertyName().equalsIgnoreCase("simState")) {

        } //if ( pce.getSource() == fltoggle) {
        //sf.setFieldlineVisibility(((Boolean)fltoggle.getValue()).booleanValue());
        //}
        //super.propertyChange(pce);
    }

    public void reset() {

        resetCamera();
    }

    public void resetCamera() {
        Point3d from = new Point3d(0., 0., 2.5);
        Point3d to = new Point3d(0., 0., 0.);
        Vector3d up = new Vector3d(0., 1., 0.);
        from.scale(0.5);
        to.scale(0.5);
        theScene.setLookAt(from, to, up);
    }

    
}
