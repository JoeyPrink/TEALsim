/* $Id: CircularMotion.java,v 1.11 2010/08/10 18:12:35 stefan Exp $ */

/**
 * A demonstration implementation of the TFramework.
 *
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.11 $
 */
package tealsim.physics.mech;

import java.awt.Color;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.vecmath.*;

import teal.app.*;
import teal.render.Rendered;
import teal.render.j3d.geometry.*;
import teal.render.j3d.ShapeNode;
import teal.render.j3d.geometry.Torus;
import teal.render.primitives.Stem;
import teal.sim.constraint.ArcConstraint;
import teal.physics.physical.Ball;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.*;
import teal.ui.control.PropertyCombo;
import teal.util.TDebug;

public class CircularMotion extends SimWorld {

	private static final long serialVersionUID = 3688507692708803127L;
    Ball ball;
	VelocityVector velocity_vector;
	AccelerationVector acceleration_vector;
	ForceVector force_vector;
	TrailVisualization trailVis; 

	PropertyCombo options;

	// Enumeration of first two combo box lines.
	private final int NONE								= -2;
	private final int LINE								= -1;

	// Enumeration of options.
	private final int VELOCITY							= 0;
	private final int ACCELERATION						= 1;
	private final int FORCE								= 2;
	
	// Esthetics
	double ball_radius = 0.5;
	Color trackColor = Color.ORANGE;
	Color ballColor = Color.ORANGE;
	Color velocityColor = Color.ORANGE.darker().darker().darker();
	Color accelerationColor = Color.BLUE.darker().darker().darker();
	Color forceColor = Color.RED.darker().darker().darker();
	
	// Problem parameters
	double gravity = 0.;
	
	
	public CircularMotion() {
		super();
		title = "Circular Motion";
		TDebug.setGlobalLevel(0);
		// Modeling
//		setEngine(new EMEngine());
		ball = new Ball();
		ball.setMass(0.5);
		ball.setRadius(ball_radius);
		ball.setElasticity(0.);
		ball.setConstrained(false);
		ball.setMoveable(true);
		ball.addPropertyChangeListener(this);
		ball.setColliding(true);
		ArcConstraint ac = new ArcConstraint(new Vector3d(0.,0.,0.),new Vector3d(0.,0.,1.),5.);
		ball.setConstraint(ac);
		ball.setConstrained(true);
		ball.setColor(ballColor);
        addElement(ball);
        
        velocity_vector = new VelocityVector(ball);
        velocity_vector.setArrowScale(1.);
        velocity_vector.setColor(velocityColor);
        addElement(velocity_vector);

        acceleration_vector = new AccelerationVector(ball);
        acceleration_vector.setArrowScale(1.);
        acceleration_vector.setColor(accelerationColor);
        addElement(acceleration_vector);

        force_vector = new ForceVector(ball);
        force_vector.setArrowScale(1.);
        force_vector.setColor(forceColor);
        addElement(force_vector);

//		trailVis = new TrailVisualisation(ball, 24, 0.75, 0.05);
//		trailVis.setColor(ball.getColor());
//		addElement(trailVis);

		Stem stem = new Stem(new Vector3d(0., 0., 0.), ball);
		stem.setColor(trackColor);
		((ShapeNode)stem.getNode3D()).setTransparency(0.8f);
//		addElement(stem);

        double torusRadius = 0.2;
        double radius = 5.-torusRadius/2.;
        Rendered ring = new Rendered();
		ShapeNode node = new ShapeNode();
		node.setGeometry(
			Torus.makeGeometry(
				(float) radius,
				(float) torusRadius,
				50,
				50));
		node.setPickable(false);
		node.setDirection(new Vector3d(0.,0.,1.));
		node.setColor(new Color3f(trackColor ));
//		node.setTransparency(0.1f);
        ring.setNode3D(node);
        addElement(ring);
        
		velocity_vector.getNode3D().setVisible(false);
		acceleration_vector.getNode3D().setVisible(false);
		force_vector.getNode3D().setVisible(false);

		options = new PropertyCombo();
		options.add("Physical Vector Quantities:", new Integer(this.NONE) );
		options.add("----------------------------------------", new Integer(this.LINE) );
		options.add("Velocity", new Integer(this.VELOCITY) );
		options.add("Acceleration", new Integer(this.ACCELERATION) );
		options.add("Force", new Integer(this.FORCE) );
		options.setBounds(120,480,300,24);
		options.setFont(options.getFont().deriveFont(14f));
		options.addPropertyChangeListener("value",this);
		options.setSelectedIndex(0);
		addElement(options);


		// World parameters and initialization.
		mSEC.setBounds(50, 510, 400, 32);
		theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 8));
		theEngine.requestRefresh();
		theEngine.setDamping(0.);
		theEngine.setGravity( new Vector3d( 0., -gravity, 0.) );
		theEngine.setShowTime(false);
		theEngine.setDeltaTime(0.05);
		mSEC.init();
        
		// Launch
		addActions();
		reset();
		mFramework.doStatus(1);

	}
	

	void addActions() {
	}

	public void propertyChange(PropertyChangeEvent pce)
	{
		if( pce.getSource() == options ) {
			int option = ((Integer) pce.getNewValue()).intValue();
			switch( option ) {
				case NONE:
					velocity_vector.getNode3D().setVisible(false);
					acceleration_vector.getNode3D().setVisible(false);
					force_vector.getNode3D().setVisible(false);
					break;
				case VELOCITY:
					velocity_vector.getNode3D().setVisible(true);
					acceleration_vector.getNode3D().setVisible(false);
					force_vector.getNode3D().setVisible(false);
					break;
				case ACCELERATION:
					velocity_vector.getNode3D().setVisible(false);
					acceleration_vector.getNode3D().setVisible(true);
					force_vector.getNode3D().setVisible(false);
					break;
				case FORCE:
					velocity_vector.getNode3D().setVisible(false);
					acceleration_vector.getNode3D().setVisible(false);
					force_vector.getNode3D().setVisible(true);
					break;
				default:
					velocity_vector.getNode3D().setVisible(false);
					acceleration_vector.getNode3D().setVisible(false);
					force_vector.getNode3D().setVisible(false);
					options.setSelectedIndex(0);
			}
		} else {
			super.propertyChange(pce);
		}
	}

	public void reset() {
		resetBall();
        resetCamera();
	}


	private void resetBall() {
		ball.setPosition(new Vector3d(-5.,0.,0.));
		ball.setVelocity(new Vector3d(0.,5.,0.));
		if(trailVis!=null)trailVis.reset();
	}


    public void resetCamera()
    {
//        mViewer.setLookAt(new Point3d(0.1,0.5,1.), new Point3d(0.,0.,0.), new Vector3d(0.,1.,0.));
		setLookAt(new Point3d(0.0, 0.0, .7), new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.));
    }
    
	

}
