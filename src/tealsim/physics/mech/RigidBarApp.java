/* $Id: RigidBarApp.java,v 1.9 2010/08/10 18:12:35 stefan Exp $ */

/**
 * A demonstration implementation of the TealFramework.
 *
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.9 $
 */
package tealsim.physics.mech;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.vecmath.*;

import teal.sim.engine.*;
import teal.sim.simulation.SimWorld;
import teal.physics.mech.RigidBar;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;

public class RigidBarApp extends SimWorld {

	private static final long serialVersionUID = 3905806361844658487L;

    RigidBar bar;
	
	PropertyDouble slider1 = null;
	PropertyDouble slider1a = null;
	
	// Problem parameters
	double gravity = 9.81;
	
	// Bar properties
	double angle = Math.PI/6;
	Vector3d position_ = new Vector3d();
	double pivot_ = -1;
	Vector3d axis_ = new Vector3d(0,0,1);
	Vector3d arbitrary_ = new Vector3d(Math.cos(angle),Math.sin(angle),0);
	double length_ = 4;
	double mass_ = 3;
	double radius_ = 0.05;

	
	public RigidBarApp() {
		super();
        int lWidth = 40;
        int sWidth = 360;

		title = "Rigid Bar";
		TDebug.setGlobalLevel(-1);
//		setEngine(new EMEngine());
		// Modeling

		// Create a bar
		bar = new RigidBar(position_, pivot_, axis_, length_, radius_ );
		bar.setArbitraryDirection(arbitrary_);
		bar.setMass(mass_);
		Vector3d pivotcoord=new Vector3d(arbitrary_);
		pivotcoord.scale(pivot_);
		pivotcoord.add(position_);
        addElement(bar);


		theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 8));

		// Adding world controllers.
		mSEC.setBounds(500, 440, 400, 32);
		// World parameters and initialization.
		setDamping(0.);
		setGravity( new Vector3d( 0., -gravity, 0.) );
		setDeltaTime(0.0025);
//		mSEC.init();
//		((SimEngine)theEngine).setShowTime(true);


		slider1 = new PropertyDouble();
		slider1.setPrecision(0.001);
		slider1.setMinimum(-length_/2);
		slider1.setMaximum(length_/2);
		slider1.setBounds(480, 60, 480, 32);
		slider1.addRoute("value", this, "pivot");
		slider1.setText("Pivot");
        slider1.setLabelColor(Color.GRAY);
        slider1.setLabelWidth(lWidth);
        slider1.setSliderWidth(sWidth);
		slider1.setBorder(null);

		slider1a = new PropertyDouble();
		slider1a.setPrecision(0.01);
		slider1a.setMinimum(-180);
		slider1a.setMaximum(180);
		slider1a.setBounds(480, 100, 480, 32);
		slider1a.addRoute("value", this, "angle");
		slider1a.setText("Bar dm");
        slider1a.setLabelColor(Color.BLACK);
        slider1a.setLabelWidth(lWidth);
        slider1a.setSliderWidth(sWidth);
		slider1a.setBorder(null);

		PropertyDouble slider2 = new PropertyDouble();
		slider2.setPrecision(0.001);
		slider2.setMinimum(-length_/2);
		slider2.setMaximum(length_/2);
		slider2.setBounds(480, 162, 480, 32);
		slider2.addRoute("value", this, "force1Position");
		slider2.setText("L1");
        slider2.setLabelColor(Color.RED);
        slider2.setLabelWidth(lWidth);
        slider2.setSliderWidth(sWidth);
		slider2.setBorder(null);

		PropertyDouble slider3 = new PropertyDouble();
		slider3.setPrecision(0.0005);
		slider3.setMinimum(0);
		slider3.setMaximum(200);
		slider3.setBounds(480, 194, 480, 32);
		slider3.addRoute("value", this, "force1Magnitude");
		slider3.setText("F1");
        slider3.setLabelColor(Color.RED);
        slider3.setLabelWidth(lWidth);
        slider3.setSliderWidth(sWidth);
		slider3.setBorder(null);

		PropertyDouble slider4 = new PropertyDouble();
		slider4.setPrecision(0.1);
		slider4.setMinimum(-180);
		slider4.setMaximum(180);
		slider4.setBounds(480, 226, 480, 32);
		slider4.addRoute("value", this, "force1Angle");
		slider4.setText("A1");
        slider4.setLabelColor(Color.RED);
        slider4.setLabelWidth(lWidth);
        slider4.setSliderWidth(sWidth);
		slider4.setBorder(null);

		PropertyDouble slider5 = new PropertyDouble();
		slider5.setPrecision(0.001);
		slider5.setMinimum(-length_/2);
		slider5.setMaximum(length_/2);
		slider5.setBounds(480, 308, 480, 32);
		slider5.addRoute("value", this, "force2Position");
		slider5.setText("L2");
        slider5.setLabelColor(Color.GREEN);
        slider5.setLabelWidth(lWidth);
        slider5.setSliderWidth(sWidth);
		slider5.setBorder(null);

		PropertyDouble slider6 = new PropertyDouble();
		slider6.setPrecision(0.001);
		slider6.setMinimum(0);
		slider6.setMaximum(200);
		slider6.setBounds(480, 340, 480, 32);
		slider6.addRoute("value", this, "force2Magnitude");
		slider6.setText("F2");
        slider6.setLabelColor(Color.GREEN);
        slider6.setLabelWidth(lWidth);
        slider6.setSliderWidth(sWidth);
		slider6.setBorder(null);

		PropertyDouble slider7 = new PropertyDouble();
		slider7.setPrecision(0.1);
		slider7.setMinimum(-180);
		slider7.setMaximum(180);
		slider7.setBounds(480, 372, 480, 32);
		slider7.addRoute("value", this, "force2Angle");
		slider7.setText("A2");
        slider7.setLabelColor(Color.GREEN);
        slider7.setLabelWidth(lWidth);
        slider7.setSliderWidth(sWidth);
		slider7.setBorder(null);


		// Some code to allow us restricting the pivot change only when
		// the simulation is paused.
		mSEC.addPropertyChangeListener("simState",this);
        addElement(slider1);
        addElement(slider1a);
        addElement(slider2);
        addElement(slider3);
        addElement(slider4);
        addElement(slider5);
        addElement(slider6);
        addElement(slider7);

		slider1.setValue(-2);
		slider1a.setValue(angle*180/Math.PI);
		slider2.setValue(2);
		slider3.setValue(15);
		slider4.setValue(-120);
		slider5.setValue(0.667);
		slider6.setValue(20);
		slider7.setValue(168);


		// Launch
		addActions();
		reset();
		 mFramework.doStatus(0);

	}
	
	public double getPivot() { return bar.getPivot(); }
	public void setPivot(double pivot) { bar.setPivot(pivot);}
	public double getAngle() { return bar.getAngle()*180./Math.PI; }
	public void setAngle(double angle) { bar.setAngle(angle*Math.PI/180.);}
	public double getForce1Position() { return bar.getForce1Position(); }
	public void setForce1Position(double position) { bar.setForce1Position(position);}
	public double getForce2Position() { return bar.getForce2Position(); }
	public void setForce2Position(double position) { bar.setForce2Position(position); }
	public double getForce1Magnitude() { return bar.getForce1Magnitude(); }
	public void setForce1Magnitude(double force) { bar.setForce1Magnitude(force); }
	public double getForce2Magnitude() { return bar.getForce2Magnitude(); }
	public void setForce2Magnitude(double force) { bar.setForce2Magnitude(force); }
	public double getForce1Angle() { return bar.getForce1Angle()*180./Math.PI; }
	public void setForce1Angle(double angle) { bar.setForce1Angle(angle*Math.PI/180.); }
	public double getForce2Angle() { return bar.getForce2Angle()*180./Math.PI; }
	public void setForce2Angle(double angle) { bar.setForce2Angle(angle*Math.PI/180.); }


	void addActions() {
	}

	boolean showFieldLines = true;

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareToIgnoreCase("reset") == 0) {
			TDebug.println(0, "Reset called");
			reset();
			//theEngine.refresh(false);
			slider1a.setEnabled(true);
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
			    break;
			case TEngineControl.RUNNING:
				slider1.setEnabled(false);
				slider1a.setEnabled(false);
			    break;
			case TEngineControl.PAUSED:
				slider1.setEnabled(true);
				slider1a.setEnabled(false);
			    break;
			case TEngineControl.ENDED:
				slider1.setEnabled(true);
				slider1a.setEnabled(false);
			    break;
        }
    }

	public void reset() {
		resetBar();
        resetCamera();
		slider1a.setEnabled(true);
	}


	private void resetBar() {
		bar.setPosition(position_);
		bar.setAngularVelocity(new Vector3d());
		bar.setArbitraryDirection(arbitrary_);
		slider1a.setValue(angle*180/Math.PI);
	}


    public void resetCamera()
    {
//        viewer.setLookAt(new Point3d(-0.1,0.1,0.1), new Point3d(0.,-0.1,-0.1), new Vector3d(0.,1.,0.));
//        viewer.setLookAt(new Point3d(-0.05,0.1,0.25), new Point3d(0.01,-0.05,-0.1), new Vector3d(0.,1.,0.));
        setLookAt(new Point3d(0,0,0.5), new Point3d(0,0,0), new Vector3d(0.,1.,0.));
    }
    
	

}
