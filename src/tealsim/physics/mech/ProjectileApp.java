/* $Id: ProjectileApp.java,v 1.8 2010/08/10 18:12:35 stefan Exp $ */

/**
 * A demonstration implementation of the TFramework.
 *
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.8 $
 */
package tealsim.physics.mech;

import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.swing.*;
import javax.vecmath.*;

import teal.app.*;
import teal.plot.*;
import teal.physics.physical.*;
import teal.sim.simulation.SimWorld;
import teal.util.TDebug;

public class ProjectileApp extends SimWorld {

	private static final long serialVersionUID = 3258408439259607090L;
    Wall ground;
	Ball ball;
	Graph graph;
	XYGraph positiongraph;
	JTextField angle;
	JTextField velocity;
	JLabel label1;
	JLabel label2;
	
	
	// Esthetics
	double ball_radius = 0.5;

	// Problem parameters
	double initial_velocity = 10.;
	double angleD = 65;
	double angleR = angleD * Math.PI / 180.;
	double gravity = 9.81;
	
	
	public ProjectileApp() {
		super();
		title = "Projectile";
		TDebug.setGlobalLevel(0);
//		setEngine(new EMEngine());
		// Modeling

		ground = new Wall();
		ground.setPosition(0,-ball_radius,0);
		ground.setEdge1( new Vector3d(100,0,0) );
		ground.setEdge2( new Vector3d(0,0,5) );
		ground.setColliding(true);

		ball = new Ball();
		ball.setMass(1.);
		ball.setRadius(ball_radius);
		ball.setElasticity(0.);
		ball.setConstrained(false);
		ball.setMoveable(true);
		ball.addPropertyChangeListener(this);
		ball.setColliding(true);

        addElement(ball);
        addElement(ground);


		theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 8));
		theEngine.requestRefresh();

		mSEC.setBounds(500, 440, 400, 32);

		// Plots
        graph = new Graph();
        graph.setBounds(500,20,400,360);
        //graph.setXPersistence(100.0);
        graph.setWrap(false);
        graph.setClearOnWrap(true);
        graph.setXLabel("Time");
        graph.setYLabel("Position"); 
        graph.addLegend(0,"x(t)");
        graph.addLegend(1,"y(t)");
        addElement(graph);
        
        positiongraph = new XYGraph(ball);
        graph.addPlotItem(positiongraph);

		
		// Input TextFields
		angle = new JTextField(String.valueOf(angleD));
		velocity = new JTextField(String.valueOf(initial_velocity));
		angle.setBounds(530, 410, 150, 20);
		velocity.setBounds(700, 410, 150, 20);
		label1 = new JLabel("Initial velocity angle:");
		label2 = new JLabel("Initial velocity magnitude:");
		label1.setBounds(530, 390, 150, 20);
		label2.setBounds(700, 390, 150, 20);
		


		// World parameters and initialization.
		theEngine.setDamping(0.);
		theEngine.setGravity( new Vector3d( 0., -gravity, 0.) );
		theEngine.setShowTime(false);
		theEngine.setDeltaTime(0.05);
		mSEC.init();
        
		// Building the GUI.
        addElement(angle);
        addElement(velocity);
        addElement(label1);
        addElement(label2);


		// Launch
		addActions();
		reset();
		 mFramework.doStatus(1);

	}
	

	void addActions() {
	}

	public void propertyChange(PropertyChangeEvent pce)
	{
		if( pce.getSource().equals(ball) ) {
//			if( pce.getPropertyName().equalsIgnoreCase("position") ) {
//				Vector3d position = ball.getPosition();
//				Vector3d velocity = ball.getVelocity();
//				double tiny = ball_radius*1e-2;
//				if( position.y < tiny && velocity.y < 0.) {
//				}
//			}
		} else {
			super.propertyChange(pce);
		}
	}

	public void reset() {
		// For some reason I need to put the reset here to avoid the residual plot segments.
		// This means that the simulation controller's reset function is called later.
        mSEC.stop();
		resetBall();
		resetGraph();
        resetCamera();
	}

	private void resetGraph() {
        graph.setXRange(0.,2.);
        graph.setYRange(0.,8.);
        positiongraph.reset();
        graph.clear(false);
	}

	private void resetBall() {
//		Vector3d ballpos = new Vector3d(ground.getNormal());
//		ballpos.scale( (ballpos.y>0?1:-1)*ball.getRadius() );
//		ballpos.add( new Vector3d(0., 0., 0.) );
//		ball.setPosition(ballpos);

		ball.setPosition(new Vector3d());
		initial_velocity = Double.parseDouble(velocity.getText());
		angleD = Double.parseDouble(angle.getText());
		angleR = angleD * Math.PI / 180.;
		ball.setVelocity(
			new Vector3d(
				initial_velocity * Math.cos(angleR),
				initial_velocity * Math.sin(angleR),
				0.));
	}


    public void resetCamera()
    {
//        viewer.setLookAt(new Point3d(-0.1,0.1,0.1), new Point3d(0.,-0.1,-0.1), new Vector3d(0.,1.,0.));
        setLookAt(new Point3d(0.35,0.5,1.), new Point3d(0.25,0.,0.), new Vector3d(0.,1.,0.));
    }
  

}
