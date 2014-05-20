/*
 * Created on Jul 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package tealsim.physics.mech;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.swing.JButton;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.plot.Graph;
import teal.plot.PendulumPlot;
import teal.render.primitives.Line;
import teal.sim.engine.TEngineControl;
import teal.sim.engine.EngineControl;
import teal.physics.mech.WeightedPhysicalPendulum;
import teal.sim.simulation.SimWorld;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;

public class WeightedPhysicalPendulumApp extends SimWorld {

    private static final long serialVersionUID = 3544677282338845495L;
    JButton but;
    JButton but1;

    WeightedPhysicalPendulum pen;
    
    PropertyDouble slider7;
    Graph graph;

    

    Line line;

    public WeightedPhysicalPendulumApp() {

        super();

        title = "Weighted Physical Pendulum";
        
//        EMEngine emModel = new EMEngine();
        TDebug.setGlobalLevel(0);
        mSEC.setVisible(false);
        BoundingSphere bs = new BoundingSphere(new Point3d(0, 0, 0), 1.5);
        setBoundingArea(bs);
        setGravity(new Vector3d(0.,-1.,0.));
//        setEngine(emModel);
        pen = new WeightedPhysicalPendulum();
        pen.setID("Pendulum");
        pen.setRing_mass(2.);
        pen.setRotable(true);
        addElement(pen);
        
        
        PropertyDouble slider2 = new PropertyDouble();
        slider2.setMinimum(0.1);
        slider2.setMaximum(20.);
        slider2.addRoute("value", pen, "length");
        slider2.setText("Pendulum Length");
        slider2.setBorder(null);
        slider2.setValue(5.);
        
        PropertyDouble slider3 = new PropertyDouble();
        slider3.setMinimum(0.1);
        slider3.setMaximum(10.);
        slider3.addRoute("value", pen, "ring_pos");
        slider3.setText("Ring Position");
        slider3.setBorder(null);
        slider3.setValue(5.);
        
        PropertyDouble slider4 = new PropertyDouble();
        slider4.setMinimum(0.0);
        slider4.setMaximum(100.);
        slider4.addRoute("value", pen, "ring_mass");
        slider4.setText("Ring Mass");
        slider4.setBorder(null);
        slider4.setValue(5.);
        
        PropertyDouble slider5 = new PropertyDouble();
        slider5.setMinimum(0.0);
        slider5.setMaximum(5.);
        slider5.addRoute("value", pen, "ring_inner_r");
        slider5.setText("Ring Inner Radius");
        slider5.setBorder(null);
        slider5.setValue(0.25);
        
        PropertyDouble slider6 = new PropertyDouble();
        slider6.setMinimum(0.0);
        slider6.setMaximum(10.);
        slider6.addRoute("value", pen, "ring_outer_r");
        slider6.setText("Ring Outer Radius");
        slider6.setBorder(null);
        slider6.setValue(1.);
        
        slider7 = new PropertyDouble();
        slider7.setID("angle_slider");
        slider7.setMinimum(-Math.PI);
        slider7.setMaximum(Math.PI);
        slider7.addPropertyChangeListener("value", this);
        slider7.setText("Pendulum Angle");
        slider7.setBorder(null);
        slider7.setValue(1.);

        graph = new Graph();
        graph.setXRange(0., 300.);
        graph.setYRange(-2., 2.);
        graph.setSize(400,200);
        graph.setWrap(true);
        graph.setClearOnWrap(true);
        graph.setXLabel("Time");
        graph.setYLabel("Amplitude");
        graph.addLegend(0, "Angle (theta)");
        graph.addLegend(1, "Torque");
        PendulumPlot plot = new PendulumPlot();
        plot.setPendulum(pen);
        plot.setTimeAutoscale(false);
        plot.setFluxAutoscale(true);
        graph.addPlotItem(plot);
        
        

        ControlGroup controls = new ControlGroup();
        controls.setText("Parameters");

        controls.add(slider2);
        controls.add(slider3);
        controls.add(slider4);
        controls.add(slider5);
        controls.add(slider6);
        controls.add(slider7);
        controls.addElement(graph);
        
        addElement(controls);
        

        
        reset();
        
        mSEC.rebuildPanel(EngineControl.DO_ALL);
        mSEC.setVisible(true);
//        theEngine.requestRefresh();
        setDeltaTime(2.5);
        setDamping(0.0);
        mSEC.init();

        addActions();

    }

    

    public void reset() {
    	pen.setAngularVelocity(new Vector3d(0,0,0));
    	AxisAngle4d aa = new AxisAngle4d(new Vector3d(pen.getPivot_axis()), Math.PI*0.25);
		Quat4d rot = new Quat4d();
		rot.set(aa);
		pen.setRotation(rot);
		slider7.setValue(Math.PI*0.25);
		graph.clear(false);
		resetCamera();
    }

    public void resetCamera() {
		Point3d from = new Point3d(0., 0., 1.);
		Point3d to = new Point3d(0., 0., 0.);
		Vector3d up = new Vector3d(0., 1., 0.);
		//from.scale(0.05);
		to.scale(0.05);
		setLookAt(from, to, up);
	}
    
    public void propertyChange(PropertyChangeEvent pce) {

        Object source = pce.getSource();
        if (source == slider7) {
        	double angle = ((Double)slider7.getValue()).doubleValue();
        	AxisAngle4d aa = new AxisAngle4d(pen.getPivot_axis(),angle);
        	Quat4d q = new Quat4d();
        	q.set(aa);
        	pen.setRotation(q);
        	if (graph != null) graph.clear(false);
        	if (mSEC.getSimState() == TEngineControl.RUNNING) mSEC.setSimState(TEngineControl.PAUSED);
        	// kill angular velocity 
        	pen.setAngularVelocity(new Vector3d(0,0,0));
        	
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