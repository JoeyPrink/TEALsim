/* $Id: NewtonsCradle.java,v 1.13 2010/08/10 18:12:35 stefan Exp $ */
/**
 * A demonstration implementation of the TFramework.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.13 $
 */
package tealsim.physics.mech;

import java.awt.Color;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.vecmath.*;

//import teal.render.geometry.*;
//import teal.render.j3d.*;
//import teal.render.j3d.geometry.Sphere;
import teal.render.TealMaterial;
import teal.render.primitives.Stem;
import teal.sim.collision.SphereCollisionController;
import teal.sim.constraint.ArcConstraint;
import teal.sim.engine.TEngineControl;
import teal.sim.simulation.SimWorld;
import teal.physics.physical.Ball;
import teal.ui.swing.LookAndFeelTweaks;
import teal.util.TDebug;

public class NewtonsCradle extends SimWorld {

	private static final long serialVersionUID = 3977859562080514610L;

    // Esthetics
	double ball_radius = 0.5;
	
	// Problem parameters
	double gravity = 9.81;
	
	Ball m1 = null, m2 = null, m3 = null, m4 = null, m5 = null;
	
	public NewtonsCradle() {
		super();
		title = "Newton's Cradle";
		TDebug.setGlobalLevel(0);
//		setEngine(new EMEngine());
		theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 8));
		theEngine.requestRefresh();
		mSEC.setBounds(500, 440, 400, 32);
		setShowGizmos(false);
        setRefreshOnDrag(true);
        setCursorOnDrag(false);
		
        double tolerance = 0.01;
        TealMaterial sphereMaterial = new TealMaterial();
        sphereMaterial.setDiffuse(Color.GRAY);
		Color3f stemColor = new Color3f(Color.GRAY.darker().darker());
		
		m1 = new Ball();
		m1.setMoveable(true);
		m1.setPosition(new Vector3d( 0., 0., 0.));
		//m1.setPosition(new Vector3d(5. * Math.sqrt(2.) / 2., 5. - 5. * Math.sqrt(2.) / 2., 0.));
		m1.setMass(1.);
		m1.setVelocity(new Vector3d(0., 0., 0.));
		m1.setRotable(false);
		m1.setRadius(0.5);
		m1.setMaterial(sphereMaterial);
		SphereCollisionController cg1 = new SphereCollisionController(m1);
		cg1.setRadius(0.5);
		cg1.setTolerance(tolerance);
		m1.setCollisionController(cg1);
		m1.setColliding(true);
		m1.setConstrained(true);
		m1.setConstraint(new ArcConstraint(new Vector3d(0., 5., 0.),
				new Vector3d(0., 0., 1.), 5.));
		m1.addPropertyChangeListener("position", this);		
		m1.setSelectable(true);
		m1.setPickable(true);
		
		m2 = new Ball();
		m2.setMoveable(true);
		m2.setPosition(new Vector3d( -1., 0., 0.));
		//m2.setPosition(new Vector3d(-1. + 5. * Math.sqrt(2.) / 2., 5. - 5. * Math.sqrt(2.) / 2., 0.));
		m2.setMass(1.);
		m2.setVelocity(new Vector3d(0., 0., 0.));
		m2.setRotable(false);
		m2.setRadius(0.5);
		
		m2.setMaterial(sphereMaterial);
		SphereCollisionController cg2 = new SphereCollisionController(m2);
		cg2.setRadius(0.5);
		cg2.setTolerance(tolerance);
		m2.setCollisionController(cg2);
		m2.setColliding(true);
		m2.setConstrained(true);
		m2.setConstraint(new ArcConstraint(new Vector3d(-1., 5., 0.),
				new Vector3d(0., 0., 1.), 5.));
		m2.addPropertyChangeListener("position", this);		
		m2.setSelectable(true);
		m2.setPickable(true);
		
		
		m3 = new Ball();
		m3.setMoveable(true);
		m3.setPosition(new Vector3d( -2., 0., 0.));
		//m3.setPosition(new Vector3d(-2. + 5. * Math.sqrt(2.) / 2., 5. - 5. * Math.sqrt(2.) / 2., 0.));
		m3.setMass(1.);
		m3.setVelocity(new Vector3d(0., 0., 0.));
		m3.setRotable(false);
		m3.setRadius(0.5);
		m3.setMaterial(sphereMaterial);
		SphereCollisionController cg3 = new SphereCollisionController(m3);
		cg3.setRadius(0.5);
		cg3.setTolerance(tolerance);
		m3.setCollisionController(cg3);
		m3.setColliding(true);
		m3.setConstrained(true);
		m3.setConstraint(new ArcConstraint(new Vector3d(-2., 5., 0.),
				new Vector3d(0., 0., 1.), 5.));
		m3.addPropertyChangeListener("position", this);		
		m3.setSelectable(true);
		m3.setPickable(true);
		
		
		m4 = new Ball();
		m4.setMoveable(true);
		m4.setPosition(new Vector3d( -3., 0., 0.));
		//m4.setPosition(new Vector3d(-3. - 5. * Math.sqrt(2.) / 2., 5. - 5. * Math.sqrt(2.) / 2., 0.));
		m4.setMass(1.);
		m4.setVelocity(new Vector3d(0., 0., 0.));
		m4.setRotable(false);
		m4.setRadius(0.5);
		m4.setMaterial(sphereMaterial);
		SphereCollisionController cg4 = new SphereCollisionController(m4);
		cg4.setRadius(0.5);
		cg4.setTolerance(tolerance);
		m4.setCollisionController(cg4);
		m4.setColliding(true);
		m4.setConstrained(true);
		m4.setConstraint(new ArcConstraint(new Vector3d(-3., 5., 0.),
				new Vector3d(0., 0., 1.), 5.));
		m4.addPropertyChangeListener("position", this);		
		m4.setSelectable(true);
		m4.setPickable(true);
		
		
		
		m5 = new Ball();
		m5.setMoveable(true);
		//m5.setPosition(new Vector3d( 1., 5., 0.));
		m5.setPosition(new Vector3d( -4., 0., 0.));
		//m5.setPosition(new Vector3d(-4. - 5. * Math.sqrt(2.) / 2., 5. - 5. * Math.sqrt(2.) / 2., 0.));
		m5.setMass(1.);
		m5.setVelocity(new Vector3d(0., 0., 0.));
		m5.setRotable(false);
		m5.setRadius(0.5);
		m5.setMaterial(sphereMaterial);
		SphereCollisionController cg5 = new SphereCollisionController(m5);
		cg5.setRadius(0.5);
		cg5.setTolerance(tolerance);
		m5.setCollisionController(cg5);
		m5.setColliding(true);
		m5.setConstrained(true);
		m5.setConstraint(new ArcConstraint(new Vector3d(-4., 5., 0.),
				new Vector3d(0., 0., 1.), 5.));
		m5.addPropertyChangeListener("position", this);		
		m5.setSelectable(true);
		m5.setPickable(true);
		
		
		
		
//		ShapeNode sn1 = new ShapeNode();
//		sn1.setGeometry(Sphere.makeGeometry(16, 0.5));
//		sn1.setAppearance(Node3D.makeAppearance(sphereColor));
//		m1.setNode3D(sn1);
//		
//		ShapeNode sn2 = new ShapeNode();
//		sn2.setGeometry(Sphere.makeGeometry(16, 0.5));
//		sn2.setAppearance(Node3D.makeAppearance(sphereColor));
//		m2.setNode3D(sn2);
//		
//		ShapeNode sn3 = new ShapeNode();
//		sn3.setGeometry(Sphere.makeGeometry(16, 0.5));
//		sn3.setAppearance(Node3D.makeAppearance(sphereColor));
//		m3.setNode3D(sn3);
//
//		ShapeNode sn4 = new ShapeNode();
//		sn4.setGeometry(Sphere.makeGeometry(16, 0.5));
//		sn4.setAppearance(Node3D.makeAppearance(sphereColor));
//		m4.setNode3D(sn4);
//		
//		ShapeNode sn5 = new ShapeNode();
//		sn5.setGeometry(Sphere.makeGeometry(16, 0.5));
//		sn5.setAppearance(Node3D.makeAppearance(sphereColor));
//		m5.setNode3D(sn5);
		
		
		Stem line1_1 = new Stem(new Vector3d(0., 5., 3.), m1);
		line1_1.setColor(stemColor);
		Stem line1_2 = new Stem(new Vector3d(0., 5., -3.), m1);
		line1_2.setColor(stemColor);
		Stem line2_1 = new Stem(new Vector3d(-1., 5., 3.), m2);
		line2_1.setColor(stemColor);
		Stem line2_2 = new Stem(new Vector3d(-1., 5., -3.), m2);
		line2_2.setColor(stemColor);
		Stem line3_1 = new Stem(new Vector3d(-2., 5., 3.), m3);
		line3_1.setColor(stemColor);
		Stem line3_2 = new Stem(new Vector3d(-2., 5., -3.), m3);
		line3_2.setColor(stemColor);
		Stem line4_1 = new Stem(new Vector3d(-3., 5., 3.), m4);
		line4_1.setColor(stemColor);
		Stem line4_2 = new Stem(new Vector3d(-3., 5., -3.), m4);
		line4_2.setColor(stemColor);
		Stem line5_1 = new Stem(new Vector3d(-4., 5., 3.), m5);
		line5_1.setColor(stemColor);
		Stem line5_2 = new Stem(new Vector3d(-4., 5., -3.), m5);
		line5_2.setColor(stemColor);
    /*
		Line line1_1 = new Line(new Vector3d(0., 5., 3.), m1);
		line1_1.setColor(Color.RED);
		Line line1_2 = new Line(new Vector3d(0., 5., -3.), m1);
		line1_2.setColor(Color.RED);
		Line line2_1 = new Line(new Vector3d(-1., 5., 3.), m2);
		line2_1.setColor(Color.GREEN);
		Line line2_2 = new Line(new Vector3d(-1., 5., -3.), m2);
		line2_2.setColor(Color.GREEN);
		Line line3_1 = new Line(new Vector3d(-2., 5., 3.), m3);
		line3_1.setColor(Color.BLUE);
		Line line3_2 = new Line(new Vector3d(-2., 5., -3.), m3);
		line3_2.setColor(Color.BLUE);
		Line line4_1 = new Line(new Vector3d(-3., 5., 3.), m4);
		line4_1.setColor(Color.YELLOW);
		Line line4_2 = new Line(new Vector3d(-3., 5., -3.), m4);
		line4_2.setColor(Color.YELLOW);
		Line line5_1 = new Line(new Vector3d(-4., 5., 3.), m5);
		line5_1.setColor(Color.CYAN);
		Line line5_2 = new Line(new Vector3d(-4., 5., -3.), m5);
		line5_2.setColor(Color.CYAN);
       */ 
		addElement(m1);
		addElement(m2);
		addElement(m3);
		addElement(m4);
		addElement(m5);
		addElement(line1_1);
		addElement(line1_2);
		addElement(line2_1);
		addElement(line2_2);
		addElement(line3_1);
		addElement(line3_2);
		addElement(line4_1);
		addElement(line4_2);
		addElement(line5_1);
		addElement(line5_2);
		
		
		// World parameters and initialization.
		setDamping(0.);
		setGravity(new Vector3d(0., -gravity, 0.));
		//theEngine.setShowTime(false);
		setDeltaTime(0.05);
		//theEngine.addPropertyChangeListener(this);
		//mSEC.init();
		
		
		// Launch
		addActions();
		//reset();
		 //mFramework.doStatus(1);
	}
	
	void addActions() {
	}
	
	public void propertyChange(PropertyChangeEvent pce) {
		
		Object source = pce.getSource();
		if(source == m1) {
			int state = mSEC.getSimState();
			if(state != TEngineControl.RUNNING)
    			theEngine.requestReorder(m1);
		} else if(source == m2) {
			int state = mSEC.getSimState();
			if(state != TEngineControl.RUNNING)
				theEngine.requestReorder(m2);
		} else if(source == m3) {
			int state = mSEC.getSimState();
			if(state != TEngineControl.RUNNING)
				theEngine.requestReorder(m3);
		} else if(source == m4) {
			int state = mSEC.getSimState();
			if(state != TEngineControl.RUNNING)
				theEngine.requestReorder(m4);
		} else if(source == m5) {
			int state = mSEC.getSimState();
			if(state != TEngineControl.RUNNING)
				theEngine.requestReorder(m5);
		} else if (source == theEngine) {
			if (pce.getPropertyName().compareTo("simState") == 0) {
				Integer val = (Integer) pce.getNewValue();
				int state = val.intValue();
                switch (state)
                {
                    case TEngineControl.NOT:
                        break;
                    case TEngineControl.INIT:
                        break;
                    case TEngineControl.RUNNING:
                    	m1.setSelected(false);
                		m2.setSelected(false);
                		m3.setSelected(false);
                		m4.setSelected(false);
                		m5.setSelected(false);
                    	m1.setSelectable(false);
	                	m2.setSelectable(false);
	                	m3.setSelectable(false);
	                	m4.setSelectable(false);
	                	m5.setSelectable(false);
                        break;
                    case TEngineControl.PAUSED:
                    	m1.setSelectable(true);
	                	m2.setSelectable(true);
	                	m3.setSelectable(true);
	                	m4.setSelectable(true);
	                	m5.setSelectable(true);
                        break;
                    case TEngineControl.ENDED:
                    	m1.setSelected(false);
	            		m2.setSelected(false);
	            		m3.setSelected(false);
	            		m4.setSelected(false);
	            		m5.setSelected(false);
                       	m1.setSelectable(false);
	                	m2.setSelectable(false);
	                	m3.setSelectable(false);
	                	m4.setSelectable(false);
	                	m5.setSelectable(false);
                       break;
                }
			}
		} else{
			super.propertyChange(pce);
		}
	}
	
	public void reset() {
		if (m1 != null) {
			m1.setPosition(new Vector3d(0., 0., 0.));
			m1.setVelocity(new Vector3d());
			m1.setSelectable(true);
		}
		if (m2 != null) {
			m2.setPosition(new Vector3d(-1., 0., 0.));
			m2.setVelocity(new Vector3d());
			m2.setSelectable(true);
		}
		if (m3 != null) {
			m3.setPosition(new Vector3d(-2., 0., 0.));
			m3.setVelocity(new Vector3d());
			m3.setSelectable(true);
		}
		if (m4 != null) {
			m4.setPosition(new Vector3d(-3., 0., 0.));
			m4.setVelocity(new Vector3d());
			m4.setSelectable(true);
		}
		if (m5 != null) {
			//m5.setPosition(new Vector3d( 1., 5., 0.));
			m5.setPosition(new Vector3d(-4., 0., 0.));
			m5.setVelocity(new Vector3d());
			m5.setSelectable(true);
		}
		resetCamera();
	}
	
	public void resetCamera() {
		Point3d from = new Point3d(-2.5, 8., 15.);
		Point3d to = new Point3d(-2.5, 1., 0.);
		Vector3d up = new Vector3d(0., 1., 0.);
		from.scale(0.05);
		to.scale(0.05);
		setLookAt(from, to, up);
	}


}
