/* $Id: GalileosInclinedPlane.java,v 1.7 2010/08/10 18:12:35 stefan Exp $ */

package tealsim.physics.mech;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.*;
import java.text.*;

import teal.render.BoundingSphere;
import javax.swing.*;
import javax.vecmath.*;

import teal.app.*;
import teal.audio.MidiSynthesizer;
import teal.framework.TealAction;
import teal.sim.constraint.WallPlaneConstraint;
import teal.sim.engine.TEngineControl;
import teal.physics.mech.*;
import teal.sim.simulation.SimWorld;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;

public class GalileosInclinedPlane extends SimWorld {

	private static final long serialVersionUID = 3258689905680201780L;
    JTable timetable;
	JTable inferences;

	SlidingBox slidingbox;
	InclinedPlane incline; 	
    MidiSynthesizer synth;
	
	// Esthetics
	double iWidth = 0.5; // in meters
	double box_length = 0.1; // in meters
	double box_width = 0.05; // in meters
	double box_height = 0.05; // in meters

	// Fret infos
	final int fretcount = 3;
	Fret [] fret = new Fret[fretcount];
	Fret endfret = null;
	double fret_radius = 0.005; // in meters
	Color [] fret_color = { Color.RED.brighter(), Color.GREEN, Color.BLUE };

	// Problem parameters
	double gravity = 9.81; // in m/s2
	double mass = 0.2; // in kg
	double time_interval = 0.5; // in seconds

	double iAngle = 2. * Math.PI / 180.; // in radians
	double iLength = 2.+box_length; // in meters
	double iHeight = iLength*Math.sin(iAngle); // in meters
	double iBase = iHeight/Math.tan(iAngle); // in meters
	Vector3d iPosition = new Vector3d(-iBase/2., 0., 0.); // Position of incline right corner.
	Vector3d iDirection = new Vector3d(1, 0, 0); // Orientation of incline.
	
	// Table infos
	String[] columnNames1 = {"t1", "t2", "t3", "Incline Angle" };
	Object[][] rowData1 = { { new Double(0.), new Double(0.), new Double(0.), (iAngle*180/Math.PI)+ "°" } };
	String[] columnNames2 = {"s1", "s2", "Dt1", "Dt2", "Dt", "g" };
	Object[][] rowData2 = { {	new Double(0.), new Double(0.), new Double(0.),
								new Double(0.), new Double(0.), new Double(0.) }, };

	
	public GalileosInclinedPlane() {
		super();
        int lWidth = 30;
        int sWidth = 360;
		title = "Inclined Plane - Measurement of g";
		TDebug.setGlobalLevel(0);
//		setEngine(new EMEngine());
        synth = new MidiSynthesizer();

        // Building the world.

		// Solution
        /*
		double [] fret_position = new double [fretcount];
		for( int i = 0; i < fretcount; i++ ) {
			fret_position[i] = 
				gravity
					* Math.sin(iAngle)
					* ((double) i + 1.) * time_interval
					* ((double) i + 1.) * time_interval
					/ 2.;
		}
		*/
		// Some initialization.
		double [] fret_position = {0.25, 0.5, 0.75 };

		
		// Cell Formatting
		DecimalFormat numformat = new DecimalFormat("0.###");
		final JFormattedTextField fixedpointField = new JFormattedTextField(numformat);
		final JFormattedTextField.AbstractFormatter formatter = fixedpointField.getFormatter();

		// Incline
		incline = new InclinedPlane();
		incline.setInclinePosition(iPosition);
		incline.setInclineDirection(iDirection);
		incline.setInclineAngle(iAngle);
		incline.setInclineBase(iBase);
		incline.setInclineWidth(iWidth);
		incline.regenerateWallParameters();
        incline.setColliding(false);
		incline.setPickable(false);
        addElement(incline);

		// Sliding Box
		slidingbox = new SlidingBox(incline);
		slidingbox.setLength(box_length);
		slidingbox.setWidth(box_width);
		slidingbox.setHeight(box_height);
		slidingbox.setMoveable(true);
		slidingbox.setMass(mass);
		slidingbox.setVelocity(new Vector3d());
		slidingbox.setRotable(false);
		slidingbox.setConstrained(false);
		slidingbox.setColliding(false);
		slidingbox.setConstrained(true);
		slidingbox.setConstraint( new WallPlaneConstraint( incline ) );
        addElement(slidingbox);
        
		// Regular frets.
		for( int i = 0; i < fretcount; i++ ) {
			fret[i] = new Fret( new Vector3d(), iWidth , fret_radius);
			fret[i].addPropertyChangeListener( new FretPropertyChangeListener(String.valueOf(i+1)) {
				public void propertyChange(PropertyChangeEvent pce)
				{
					if(pce.getPropertyName().equalsIgnoreCase("fretstate")) {
						boolean pre = ((Boolean)pce.getOldValue()).booleanValue();
						boolean post = ((Boolean)pce.getNewValue()).booleanValue();
//						if( pre==false && post==true && mSEC.getSimState()==EngineControl.RUNNING ) {
						if( post==true && mSEC.getSimState()==TEngineControl.RUNNING ) {
							System.out.println("Fret " + id + " crossed at time " + theEngine.getTime() + ".");
							synth.playNote(60,2,100);
							try {
								timetable.setValueAt(
									formatter.valueToString(
										new Double(theEngine.getTime())),
									0,
									Integer.parseInt(id) - 1);
							} catch (ParseException e) {

							}
						}
					}
				}
			} );
			fret[i].setColor(fret_color[i]);
            fret[i].setDirection(new Vector3d(0.,0.,1.));
			slidingbox.addPropertyChangeListener(fret[i]);
			addElement(fret[i]);
		}
		
		// End-fret.
		Vector3d endfretPosition = incline.locationToPosition(0.);
		endfret = new Fret( endfretPosition, iWidth , fret_radius);
		endfret.addPropertyChangeListener( new FretPropertyChangeListener("End-fret") {
			public void propertyChange(PropertyChangeEvent pce)
			{
				if(pce.getPropertyName().equalsIgnoreCase("fretstate")) {
					boolean pre = ((Boolean)pce.getOldValue()).booleanValue();
					boolean post = ((Boolean)pce.getNewValue()).booleanValue();
//					if( pre==false && post==true && mSEC.getSimState()==EngineControl.RUNNING ) {
					if( post==true && mSEC.getSimState()==TEngineControl.RUNNING ) {
						System.out.println("End fret crossed at time " + theEngine.getTime() + ".");
						mSEC.end();
						double x1 = - getPlacementOf(0);
						double x2 = - getPlacementOf(1);
						double x3 = - getPlacementOf(2);
						double t1 = Double.parseDouble(rowData1[0][0].toString());
						double t2 = Double.parseDouble(rowData1[0][1].toString());
						double t3 = Double.parseDouble(rowData1[0][2].toString());
						double s1 = x2-x1;
						double s2 = x3-x2;
						double Dt1 = t2-t1;
						double Dt2 = t3-t2;
						double Dt = (Dt1+Dt2)/2; // Math.sqrt(Dt1*Dt2) ?
						double g = (s2-s1)/(Dt*Dt*Math.sin(iAngle));
						try {
							rowData2[0][0] = formatter.valueToString(new Double(s1));
							rowData2[0][1] = formatter.valueToString(new Double(s2));
							rowData2[0][2] = formatter.valueToString(new Double(Dt1));
							rowData2[0][3] = formatter.valueToString(new Double(Dt2));
							rowData2[0][4] = formatter.valueToString(new Double(Dt));
							rowData2[0][5] = formatter.valueToString(new Double(g));
						} catch (ParseException e) {
						}
						theGUI.refresh(); 
					}
				}
			}
		});
		endfret.setVisible(false);
        endfret.setDirection(new Vector3d(0.,0.,1.));
		slidingbox.addPropertyChangeListener(endfret);
		addElement(endfret);




		// 3D viewer.
		theEngine.setBoundingArea(new BoundingSphere(new Point3d(), 8));
		mSEC.setBounds(50, 700, 400, 32);

		// World parameters and initialization.
		theEngine.setDamping(0.);
		theEngine.setGravity( new Vector3d( 0., -gravity, 0.) );
		theEngine.setShowTime(false);
		theEngine.setDeltaTime(0.01);
//		theEngine.setDelay(0);
//		theEngine.setNanoDelay(1000);
//		theEngine.setDelayAdaptation(true);
		mSEC.init();
        

		// Sliders
		PropertyDouble slider1 = new PropertyDouble();
		slider1.setLabelVisible(false);
		slider1.setPrecision(0.001);
		slider1.setMinimum(0);
		slider1.setMaximum(2);
		slider1.setBounds(40, 480, 600, 32);
		slider1.addRoute("value", this, "placement0");
		slider1.setValue(2. - fret_position[0]);
		slider1.setText("x1");
        slider1.setLabelColor(Color.RED);
        slider1.setLabelWidth(lWidth);
        slider1.setSliderWidth(sWidth);
		slider1.setBorder(null);

		PropertyDouble slider2 = new PropertyDouble();
		slider2.setLabelVisible(false);
		slider2.setPrecision(0.001);
		slider2.setMinimum(0);
		slider2.setMaximum(2);
		slider2.setBounds(40, 512, 450, 32);
		slider2.addRoute("value", this, "placement1");
		slider2.setValue(2. - fret_position[1]);
		slider2.setText("x2");
        slider2.setLabelColor(Color.GREEN);
        slider2.setLabelWidth(lWidth);
        slider2.setSliderWidth(sWidth);
		slider2.setBorder(null);

		PropertyDouble slider3 = new PropertyDouble();
		slider3.setLabelVisible(false);
		slider3.setPrecision(0.001);
		slider3.setMinimum(0);
		slider3.setMaximum(2);
		slider3.setBounds(40, 544, 450, 32);
		slider3.addRoute("value", this, "placement2");
		slider3.setValue(2. - fret_position[2]);
		slider3.setText("x3");
        slider3.setLabelColor(Color.BLUE);
        slider3.setLabelWidth(lWidth);
        slider3.setSliderWidth(sWidth);
		slider3.setBorder(null);

        // Tables
		int rowsize = 20;

        timetable = new JTable(rowData1, columnNames1);
        timetable.setRowHeight(rowsize);
		timetable.setEnabled(false);

        inferences = new JTable(rowData2, columnNames2);
        inferences.setRowHeight(rowsize);
		inferences.setEnabled(false);

		JScrollPane timepane = new JScrollPane(timetable);
        timepane.setBounds(20, 580, 450, 2*rowsize);
	
		JScrollPane inferencepane = new JScrollPane(inferences);
        inferencepane.setBounds(20, 640, 450, 2*rowsize);


		// Building the GUI.
		addElement(timepane);
		addElement(inferencepane);
		addElement(slider1);
		addElement(slider2);
		addElement(slider3);

		// Launch
		addActions();
		reset();
        mFramework.doStatus(0);

	}

	public double getPlacementOf(int i) {
		double placement = incline.positionToLocation(fret[i].getPosition());
		return placement;
	}

	public void setPlacementOf(int i, double placement) {
		Vector3d position = incline.locationToPosition(placement);
		mSEC.stop();
		fret[i].setPosition(position);
		theEngine.requestRefresh();
	}
	
	public double getPlacement0() { return getPlacementOf(0); }
	public void setPlacement0(double placement) {	setPlacementOf(0, placement); }
	public double getPlacement1() { return getPlacementOf(1); }
	public void setPlacement1(double placement) {	setPlacementOf(1, placement); }
	public double getPlacement2() { return getPlacementOf(2); }
	public void setPlacement2(double placement) {	setPlacementOf(2, placement); }


	void addActions() {
		TealAction a = new TealAction("Information",this);
		addAction("Help",a);
	}

	boolean showFieldLines = true;

	public void actionPerformed(ActionEvent e) {
		Cursor cr = null;
		if (e.getActionCommand().compareToIgnoreCase("Information") == 0) {
			//System.out.println( "The 'InclinedPlaneAppHelp.htm' help file should be loaded here." );
            mFramework.openBrowser("help/InclinedPlaneAppHelp.htm");
		} 
        else {
			super.actionPerformed(e);
		}
	}

	public void propertyChange(PropertyChangeEvent pce)
	{
		super.propertyChange(pce);
	}

	public void reset() {

		resetTables();
		resetSlidingBox();
        resetCamera();
        
        theGUI.refresh();
	}

	private void resetTables() {
		rowData1[0][0] = new Double(0.);
		rowData1[0][1] = new Double(0.);
		rowData1[0][2] = new Double(0.);
		rowData2[0][0] = new Double(0.);
		rowData2[0][1] = new Double(0.);
		rowData2[0][2] = new Double(0.);
		rowData2[0][3] = new Double(0.);
		rowData2[0][4] = new Double(0.);
		rowData2[0][5] = new Double(0.);
	}

	private void resetSlidingBox() {
		Vector3d position = incline.locationToPosition(iLength);
		// Below, the 'false' argument prevents from triggering the frets, in the case
		// when it is located at the very beginning of the incline. If the fret is
		// triggered by this reset, no change of state will be picked when the
		// simulation starts running.
		slidingbox.setPosition(position, false); 
		slidingbox.setVelocity(new Vector3d());
	}

    public void resetCamera() {
        setLookAt(new Point3d(-0.1,0.1,0.1), new Point3d(0.,-0.1,-0.1), new Vector3d(0.,1.,0.));
    }
    


	private class FretPropertyChangeListener implements PropertyChangeListener {
		protected String id = "";
		public FretPropertyChangeListener( String iid) {
			id = iid;
		}
		public void propertyChange(PropertyChangeEvent pce) {
		}
	}
}
