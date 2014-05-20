/* $Id: CelestialApp.java,v 1.12 2010/08/10 18:12:35 stefan Exp $ */

/**
 * A demonstration implementation of the TealFramework.
 *
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.12 $
 */
package tealsim.physics.mech;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.*;

import teal.render.BoundingSphere;
import javax.swing.JButton;
import javax.vecmath.*;


import teal.plot.*;
import teal.render.Rendered;
import teal.render.viewer.SelectListener;
import teal.sim.engine.*;
import teal.physics.em.SimEM;
import teal.physics.physical.Ball;
import teal.sim.spatial.TrailVisualization;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;

public class CelestialApp extends SimEM {

	private static final long serialVersionUID = 3616731595055380279L;
    Ball ball1;
	Ball ball2;
	TrailVisualization trailVis1;
	TrailVisualization trailVis2;	
	Graph graph1 = null;
	Graph graph2 = null;
	PlotItem plot1 = null;
	PlotItem plot2 = null;

	PropertyDouble slider1;
	PropertyDouble slider2;
	PropertyDouble slider3;
	PropertyDouble slider4;
	PropertyDouble slider5;
	PropertyDouble slider6;
	
    JButton defaults;
	
	// Problem parameters
	double gravity = 0.;
	
	// Ball properties
	final double _mass1 = 100;
	final Vector3d _position1 = new Vector3d(4,0,0);
	final Vector3d _velocity1 = new Vector3d(0,2.5,0);
	final double _mass2 = 100;
	final Vector3d _position2 = new Vector3d(-4,0,0);
	final Vector3d _velocity2 = new Vector3d(0,-2.5,0);
	double mass1 = _mass1;
	Vector3d position1 = new Vector3d(_position1);
	Vector3d velocity1 = new Vector3d(_velocity1);
	double mass2 = _mass2;
	Vector3d position2 = new Vector3d(_position2);
	Vector3d velocity2 = new Vector3d(_velocity2);
	
	public CelestialApp() {
		super();
        int lWidth = 60;
        int sWidth = 320;

		title = "Celestial Mechanics";
		TDebug.setGlobalLevel(-1);
		// Modeling

		// Create balls
//		setEngine(new EMEngine());
		ball1=new Ball();
		ball1.setRadius(0.5);
		ball1.setPosition( position1 );
		ball1.setColor(Color.BLUE);
		ball1.setColliding(false);
		ball1.setGeneratingG(true);
		ball1.setMass(mass1);
		ball1.setVelocity( velocity1 );

		ball2=new Ball();
		ball2.setRadius(0.5);
		ball2.setPosition( position2 );
		ball2.setColor(Color.RED);
		ball2.setColliding(false);
		ball2.setGeneratingG(true);
		ball2.setMass(mass2);
		ball2.setVelocity( velocity2 );
		
        addElement(ball1);
        addElement(ball2);

		trailVis1 = new TrailVisualization(ball1, 24, 0.75, 0.05);
//		trailVis1.setColor(ball1.getColor());
		addElement(trailVis1);

		trailVis2 = new TrailVisualization(ball2, 24, 0.75, 0.05);
//		trailVis2.setColor(Color.RED);
		addElement(trailVis2);


		theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 8));

		// Adding world controllers.
		mSEC.setBounds(500, 700, 400, 24);
		// World parameters and initialization.
		theEngine.setDamping(0.);
		theEngine.setGravity(new Vector3d( 0., -gravity, 0.) );
		(theEngine).setShowTime(false);
//		theEngine.setDeltaTime(0.01);
		theEngine.setDeltaTime(0.025);

		// Circular
//		theEngine.setDeltaTime(0.0005);
		mSEC.init();
//		((SimEngine)theEngine).setShowTime(true);


		/*
		 * slider1 -> mass 1
		 * slider2 -> position 1 (left right only, X)
		 * slider3 -> velocity 1 (up or down only, Y)
		 * 
		 * slider4 -> mass 2
		 * slider5 -> position 2 (left right only, X)
		 * slider6 -> velocity 2 (up or down only, Y)
		 * 
		 */
		slider1 = new PropertyDouble();
		slider1.setPrecision(0.01);
		slider1.setMinimum(0.1);
		slider1.setMaximum(500);
		slider1.setBounds(20, 500, 480, 32);
		slider1.addRoute("value", this, "mass1");
		slider1.setText("Mass");
        slider1.setLabelColor(ball1.getColor());
        slider1.setLabelWidth(lWidth);
        slider1.setSliderWidth(sWidth);
		slider1.setBorder(null);
		
		slider2 = new PropertyDouble();
		slider2.setPrecision(0.01);
		slider2.setMinimum(-10);
		slider2.setMaximum(10);
		slider2.setBounds(20, 532, 480, 32);
		slider2.addRoute("value", this, "pos1");
		slider2.setText("Position");
        slider2.setLabelColor(ball1.getColor());
        slider2.setLabelWidth(lWidth);
        slider2.setSliderWidth(sWidth);
		slider2.setBorder(null);

		slider3 = new PropertyDouble();
		slider3.setPrecision(0.01);
		slider3.setMinimum(-10);
		slider3.setMaximum(10);
		slider3.setBounds(20, 564, 480, 32);
		slider3.addRoute("value", this, "vel1");
		slider3.setText("Velocity");
        slider3.setLabelColor(ball1.getColor());
        slider3.setLabelWidth(lWidth);
        slider3.setSliderWidth(sWidth);
		slider3.setBorder(null);


		slider4 = new PropertyDouble();
		slider4.setPrecision(0.01);
		slider4.setMinimum(0.1);
		slider4.setMaximum(500);
		slider4.setBounds(20, 596, 480, 32);
		slider4.addRoute("value", this, "mass2");
		slider4.setText("Mass");
        slider4.setLabelColor(ball2.getColor());
        slider4.setLabelWidth(lWidth);
        slider4.setSliderWidth(sWidth);
		slider4.setBorder(null);

		slider5 = new PropertyDouble();
		slider5.setPrecision(0.01);
		slider5.setMinimum(-10);
		slider5.setMaximum(10);
		slider5.setBounds(20, 628, 480, 32);
		slider5.addRoute("value", this, "pos2");
		slider5.setText("Position");
        slider5.setLabelColor(ball2.getColor());
        slider5.setLabelWidth(lWidth);
        slider5.setSliderWidth(sWidth);
		slider5.setBorder(null);

		slider6 = new PropertyDouble();
		slider6.setPrecision(0.01);
		slider6.setMinimum(-10);
		slider6.setMaximum(10);
		slider6.setBounds(20, 660, 480, 32);
		slider6.addRoute("value", this, "vel2");
		slider6.setText("Velocity");
        slider6.setLabelColor(ball2.getColor());
        slider6.setLabelWidth(lWidth);
        slider6.setSliderWidth(sWidth);
		slider6.setBorder(null);
		
		
		defaults = new JButton();
		defaults.setText("Defaults");
		defaults.setBounds(210, 700, 100, 24);
		defaults.addActionListener(this);

		theEngine.addPropertyChangeListener("simState",this);

        addElement(slider1);
        addElement(slider2);
        addElement(slider3);
        addElement(slider4);
        addElement(slider5);
        addElement(slider6);
        addElement(defaults);

		slider1.setValue(mass1);
		slider2.setValue(position1.x);
		slider3.setValue(velocity1.y);
		slider4.setValue(mass2);
		slider5.setValue(position2.x);
		slider6.setValue(velocity2.y);


        graph1 = new Graph();
		graph1.setBounds(500,20,480,340);
        theGUI.addTElement(graph1);
        graph1.setXRange(0.1,50);
        graph1.setYRange(-1250,500);
        //graph1.setXPersistence(100.0);
        graph1.setWrap(false);
        graph1.setClearOnWrap(false);
        addElement(graph1);
        plot1 = new PlanetaryPotentialGraph();
        ((PlanetaryPotentialGraph) plot1).setBodyOne(ball1);
        ((PlanetaryPotentialGraph) plot1).setBodyTwo(ball2);
        ((PlanetaryPotentialGraph) plot1).setIndObj(theEngine);
        graph1.addPlotItem(plot1);
        

        graph2 = new Graph();
		graph2.setBounds(500,340,480,340);
        theGUI.addTElement(graph2);
        graph2.setXRange(-10,10);
        graph2.setYRange(-10,10);
        //graph2.setXPersistence(100.0);
		graph2.setFocusable(false);
        graph2.setWrap(false);
        graph2.setClearOnWrap(false);
        addElement(graph2);
        plot2 = new TwoBodyDistanceGraph();
        ((TwoBodyDistanceGraph) plot2).setBodyOne(ball1);
        ((TwoBodyDistanceGraph) plot2).setBodyTwo(ball2);
        ((TwoBodyDistanceGraph) plot2).setIndObj(theEngine);
        graph2.addPlotItem(plot2);

		// Launch
		addActions();
		reset();
		mFramework.doStatus(0);

	}
	
	public double getMass1() { return ball1.getMass(); }
	public void setMass1(double mass) {
		mass1 = mass;
		ball1.setMass(mass);
		((PlanetaryPotentialGraph) plot1).reset();
		plot1.doPlot(graph1);
		theGUI.refresh();
	}
	public double getPos1() { return ball1.getPosition().x; }
	public void setPos1(double pos) {
		position1.x = pos;
		ball1.setPosition(new Vector3d(pos,0,0));
		((PlanetaryPotentialGraph) plot1).reset();
		plot1.doPlot(graph1);
		((TwoBodyDistanceGraph) plot2).reset();
        graph2.clear(0);
		plot2.doPlot(graph2);
		theGUI.refresh();
		trailVis1.reset();
	}
	public double getVel1() { return ball1.getVelocity().y; }
	public void setVel1(double vel) {
		velocity1.y = vel;
		ball1.setVelocity(new Vector3d(0,vel,0));
		((PlanetaryPotentialGraph) plot1).reset();
		plot1.doPlot(graph1);
		theGUI.refresh();
	}
	public double getMass2() { return ball2.getMass(); }
	public void setMass2(double mass) {
		mass2 = mass;
		ball2.setMass(mass);
		((PlanetaryPotentialGraph) plot1).reset();
		plot1.doPlot(graph1);
		theGUI.refresh();
	}
	public double getPos2() { return ball2.getPosition().x; }
	public void setPos2(double pos) {
		position2.x = pos;
		ball2.setPosition(new Vector3d(pos,0,0));
		((PlanetaryPotentialGraph) plot1).reset();
		plot1.doPlot(graph1);
		((TwoBodyDistanceGraph) plot2).reset();
        graph2.clear(0);
		plot2.doPlot(graph2);
		theGUI.refresh();
		trailVis2.reset();
	}
	public double getVel2() { return ball2.getVelocity().y; }
	public void setVel2(double vel) {
		velocity2.y = vel;
		ball2.setVelocity(new Vector3d(0,vel,0));
		((PlanetaryPotentialGraph) plot1).reset();
		plot1.doPlot(graph1);
		theGUI.refresh();
	}

	void addActions() {
	}

	
	private void enableSliders(boolean enable) {
        slider1.setEnabled(enable);
        slider2.setEnabled(enable);
        slider3.setEnabled(enable);
        slider4.setEnabled(enable);
        slider5.setEnabled(enable);
        slider6.setEnabled(enable);
        defaults.setEnabled(enable);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareToIgnoreCase("reset") == 0) {
			TDebug.println(0, "Reset called");
			reset();
			//theEngine.refresh(false);
	    } if (e.getSource() == defaults) {
	    	defaultBalls();
	    } else {
			super.actionPerformed(e);
		}
	}

	public void propertyChange(PropertyChangeEvent pce)
	{
        if (pce.getPropertyName().compareTo("simState") == 0) {
            simStateChanged(((Integer)pce.getNewValue()).intValue());
        } else {
			super.propertyChange(pce);
		}
	}

    public void simStateChanged(int state) {
        switch (state){
			case TEngineControl.NOT:
			    break;
			case TEngineControl.INIT:
				reset();
			    break;
			case TEngineControl.RUNNING:
				enableSliders(false);
			    break;
			case TEngineControl.PAUSED:
				enableSliders(false);
			    break;
			case TEngineControl.ENDED:
				enableSliders(false);
			    break;
        }
    }

	public void reset() {
		resetBalls();
        resetCamera();
		resetGraph();
		enableSliders(true);
	}

	private void resetGraph() {
        graph1.clear(false);
        graph1.setXRange(0.1,50);
        graph1.setYRange(-1250,500);
    	((PlanetaryPotentialGraph)plot1).reset();
    	plot1.doPlot(graph1);

        graph2.clear(false);
        graph2.setXRange(-10.,10);
        graph2.setYRange(-10.,10.);
    	((TwoBodyDistanceGraph)plot2).reset();
    	plot2.doPlot(graph2);

        theGUI.refresh();
	}


	private void resetBalls() {
		slider1.setValue(mass1);
		slider2.setValue(position1.x);
		slider3.setValue(velocity1.y);
		slider4.setValue(mass2);
		slider5.setValue(position2.x);
		slider6.setValue(velocity2.y);
		ball1.setMass(mass1);
		ball1.setPosition(position1);
		ball1.setVelocity(velocity1);
		ball2.setMass(mass2);
		ball2.setPosition(position2);
		ball2.setVelocity(velocity2);
		trailVis1.reset();
		trailVis2.reset();
	}

	private void defaultBalls() {
		slider1.setValue(_mass1);
		slider2.setValue(_position1.x);
		slider3.setValue(_velocity1.y);
		slider4.setValue(_mass2);
		slider5.setValue(_position2.x);
		slider6.setValue(_velocity2.y);
		ball1.setMass(_mass1);
		ball1.setPosition(_position1);
		ball1.setVelocity(_velocity1);
		ball2.setMass(_mass2);
		ball2.setPosition(_position2);
		ball2.setVelocity(_velocity2);
		trailVis1.reset();
		trailVis2.reset();
	}

    public void resetCamera()
    {
		super.resetCamera();
        setLookAt(new Point3d(0,0,0.5), new Point3d(0,0,0), new Vector3d(0.,1.,0.));
    }
    
	


	/* Dummy methods just to satisfy the interface requirement. */

//    public void addSelectListener(SelectListener listener) {}
//    public void removeSelectListener(SelectListener listener) {}
//    public void addSelected(Rendered obj,boolean clear) {}
//    public void removeSelected(Rendered obj) {}
//    public void clearSelected() {}
//    public Collection getSelected() { return new ArrayList(); }
//    public int getNumberSelected() { return 0; }

}
