/* $Id: InclinedPlaneApp.java,v 1.12 2010/08/10 18:12:35 stefan Exp $ */

package tealsim.physics.mech;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.app.SouthGUI;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.render.HasPosition;
import teal.render.Rendered;
import teal.render.j3d.Node3D;
import teal.render.j3d.ViewerJ3D;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.primitives.Helix;
import teal.sim.constraint.SpringConstraint;
import teal.sim.constraint.WallPlaneConstraint;
import teal.physics.physical.Wall;
import teal.physics.mech.Fret;
import teal.physics.mech.InclinedPlane;
import teal.physics.mech.SlidingBox;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.ComponentForceVector;
import teal.sim.spatial.SpatialTextLabel;
import teal.ui.UIPanel;
import teal.ui.control.PropertyDouble;
import teal.ui.swing.LookAndFeelTweaks;
import teal.util.TDebug;

public class InclinedPlaneApp extends SimWorld {

	private static final long serialVersionUID = 3258689905680201780L;
    JTable timetable;
	JTable inferences;
	
	//UIPanel controls;
	UIPanel buttoncontrol;
	UIPanel textcontrol;
	JLabel label01;
	JLabel label02;
	JLabel label03;
	JTextField answerfield;
	
	SpatialTextLabel lbl1,lbl2,lbl3,lbl4;

	ComponentForceVector grav;
	ComponentForceVector normal;
	ComponentForceVector damp;
	ComponentForceVector spring;
	double arrowScale = 0.2;
	
	SlidingBox slidingbox;
	InclinedPlane incline; 	
	Helix line;
	Wall flatEQ;
	Wall angleEQ;
	
	PropertyDouble angleslider;
    
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

	double iAngle = 45.0 * Math.PI / 180.; // in radians
	double iLength = 2.+box_length; // in meters
	double iHeight = iLength*Math.sin(10. * Math.PI/180.); // in meters
	double iBase = iHeight/Math.tan(10. * Math.PI/180.); // in meters
	Vector3d iPosition = new Vector3d(); //new Vector3d(-iBase/2., 0., 0.); // Position of incline right corner.
	Vector3d iDirection = new Vector3d(1, 0, 0); // Orientation of incline.
	
	double springPos = iLength*1.2;
	double boxPos = iLength*0.8;
	double boxVel = 0; //1.;
	
	Color labelColor = new Color(0,0,0);
	
	private JTextArea textarea;
	private String hintText;
	private JLabel hintLabel;
	
//	private EMEngine emModel = null;
	
	//String testString = "THIS IS A TEST~!";
	
	public InclinedPlaneApp() {
		super();
		title = "Inclined Plane";
		TDebug.setGlobalLevel(0);
		
		setGui(new SouthGUI());
		
//	    emModel = new EMEngine();
		setBoundingArea(new BoundingSphere(new Point3d(), 8));
		// World parameters and initialization.
		setDamping(0.1);
		setGravity( new Vector3d( 0., -gravity, 0.) );
		setDeltaTime(0.01);
//		setEngine(emModel);
		
        // Building the world.

		// Incline
		incline = new InclinedPlane();
		incline.setID("Incline");
		incline.setInclinePosition(iPosition);
		incline.setInclineDirection(iDirection);
		incline.setInclineAngle(iAngle);
		incline.setInclineBase(iBase);
		incline.setInclineWidth(iWidth);
		incline.regenerateWallParameters();
        incline.setColliding(false);
		incline.setPickable(false);
        
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
		incline.setTolerance(0.1);
		
		Vector3d position = incline.locationToPosition(iLength);
		slidingbox.addConstraint(new SpringConstraint(position, 1.25, 5.));
		slidingbox.addConstraint(new WallPlaneConstraint( incline ));
		
		boolean loadModels = true;
		Loader3DS max = new Loader3DS();
		if (loadModels) {

            BranchGroup bg2 = max.getBranchGroup("models/CartB.3DS", "models/");
            Node3D node2 = new Node3D();
            node2.setScale(0.04);
            node2.addContents(bg2);
            slidingbox.setNode3D(node2);
            slidingbox.setModelOffsetPosition(new Vector3d(-0., 0.75, 0));           
        }
        addElement(slidingbox);
        addElement(incline);

        grav = new ComponentForceVector(slidingbox, ComponentForceVector.TYPE_GRAVITY);
        damp = new ComponentForceVector(slidingbox, ComponentForceVector.TYPE_DAMPING);
        spring = new ComponentForceVector(slidingbox, ComponentForceVector.TYPE_CONSTRAINT);
        spring.setConstraintIndex(0);
        normal = new ComponentForceVector(slidingbox, ComponentForceVector.TYPE_CONSTRAINT);
        normal.setConstraintIndex(1);
        
        
        grav.setArrowScale(arrowScale);
        damp.setArrowScale(arrowScale);
        spring.setArrowScale(arrowScale);
        normal.setArrowScale(arrowScale);
        
        grav.setMoveable(false); // this just prevents the user from translating the arrows with the mouse
        damp.setMoveable(false);
        spring.setMoveable(false);
        normal.setMoveable(false);
        
        normal.setPickable(true);
        normal.setRotable(true);
        normal.setScreenYRotationAxis(Rendered.ROTATION_AXIS_Z);
        normal.setScreenXRotationAxis(Rendered.ROTATION_AXIS_NONE);
        //normal.setRotationAngleSnap(.05);
        normal.setSelectable(false);
        normal.setUpdating(true);
        
        grav.setPickable(true);
        grav.setRotable(true);
        grav.setScreenYRotationAxis(Rendered.ROTATION_AXIS_Z);
        grav.setScreenXRotationAxis(Rendered.ROTATION_AXIS_NONE);
        grav.setSelectable(false);
        
        spring.setPickable(true);
        spring.setRotable(true);
        spring.setScreenYRotationAxis(Rendered.ROTATION_AXIS_Z);
        spring.setScreenXRotationAxis(Rendered.ROTATION_AXIS_NONE);
        spring.setSelectable(false);
        spring.setScaleByMagnitude(false);
        
        damp.setPickable(true);
        damp.setRotable(true);
        damp.setScreenYRotationAxis(Rendered.ROTATION_AXIS_Z);
        damp.setScreenXRotationAxis(Rendered.ROTATION_AXIS_NONE);
        damp.setSelectable(false);
        
        normal.setColor(new Color(255,100,100));
        grav.setColor(new Color(100,255,100));
        spring.setColor(new Color(100,100,255));
        damp.setColor(new Color(100,100,100));
        
        
        lbl1 = new SpatialTextLabel("gravity", grav);
		lbl1.setBaseScale(0.15);
		lbl1.setPositionOffset(new Vector3d(0.05,0.00,0.));
		lbl1.setRefDirectionOffset(0.5);
		lbl1.setUseDirectionOffset(true);
		lbl1.setColor(labelColor);
		addElement(lbl1);
		
		lbl2 = new SpatialTextLabel("spring", spring);
		lbl2.setBaseScale(0.15);
		lbl2.setPositionOffset(new Vector3d(0.05,0.0,0.));
		lbl2.setRefDirectionOffset(0.5);
		lbl2.setUseDirectionOffset(true);
		lbl2.setColor(labelColor);
		addElement(lbl2);
		
		lbl3 = new SpatialTextLabel("damping", damp);
		lbl3.setBaseScale(0.15);
		lbl3.setPositionOffset(new Vector3d(0.05,0.0,0.));
		lbl3.setRefDirectionOffset(0.5);
		lbl3.setUseDirectionOffset(true);
		lbl3.setColor(labelColor);
		addElement(lbl3);
		
		lbl4 = new SpatialTextLabel("normal", normal);
		lbl4.setBaseScale(0.15);
		lbl4.setPositionOffset(new Vector3d(0.05,0.0,0.));
		lbl4.setRefDirectionOffset(0.5);
		lbl4.setUseDirectionOffset(true);
		lbl4.setColor(labelColor);
		addElement(lbl4);
        
        
        addElement(grav);
        addElement(damp);
        addElement(spring);
        addElement(normal);
       
        
        setShowGizmos(false);
        
        line = new Helix(position,(HasPosition) slidingbox);
		line.setColor(labelColor); //new Color(200, 200, 200));
		line.setRadius(2.*0.6f*line.getRadius());
		addElement(line);
		
		Vector3d eqpos = new Vector3d(((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getPoint());
		Vector3d slope = new Vector3d(Math.cos(iAngle),Math.sin(iAngle),0.);
		slope.normalize();
		slope.scale(-((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getRestLength());
		eqpos.add(slope);
		
		flatEQ = new Wall(eqpos, new Vector3d(0.,0.05,0.), new Vector3d(0.,0.,1.));
		flatEQ.setColor(new Color(255,0,0));
		addElement(flatEQ);
        
		slope.normalize();
		slope.scale(((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getRestLength() + slidingbox.getMass()*getGravity().length()*Math.sin(iAngle));
		Vector3d angleeqpos = new Vector3d(((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getPoint());
		angleeqpos.add(slope);
		angleEQ = new Wall(angleeqpos, new Vector3d(0.,0.05,0.), new Vector3d(0.,0.,1.));
		angleEQ.setColor(new Color(0,0,255));
		addElement(angleEQ);
		
		SpatialTextLabel eqlbl = new SpatialTextLabel("equilibrium (flat)", flatEQ);
		eqlbl.setBaseScale(0.08);
		eqlbl.setPositionOffset(new Vector3d(0.05,0.0,0.));
		eqlbl.setRefDirectionOffset(0.5);
		eqlbl.setUseDirectionOffset(true);
		eqlbl.setColor(labelColor);
		addElement(eqlbl);
		
		
		eqlbl = new SpatialTextLabel("equilibrium (angle)", angleEQ);
		eqlbl.setBaseScale(0.08);
		eqlbl.setPositionOffset(new Vector3d(0.05,0.0,0.));
		eqlbl.setRefDirectionOffset(0.3);
		eqlbl.setUseDirectionOffset(true);
		eqlbl.setColor(labelColor);
		addElement(eqlbl);

		mSEC.init();

        buttoncontrol = new UIPanel();
        buttoncontrol.setBorder(LookAndFeelTweaks.PANEL_BORDER);
//        buttoncontrol.setLayout(LookAndFeelTweaks.createHorizontalPercentLayout());
        
        JButton testButton = new JButton(new TealAction("Test", "Test", this));
        testButton.setFont(testButton.getFont().deriveFont(Font.BOLD));
        
//        answerfield = new JTextField();
//        answerfield.setBorder(null);
//        answerfield.setBackground(buttoncontrol.getBackground());
        
        buttoncontrol.add(testButton);
//        buttoncontrol.add(testButton, "75");
//        buttoncontrol.add(answerfield, "*");
        addElement(buttoncontrol);
        
        angleslider = new PropertyDouble();
        angleslider.setID("angleslider");
        angleslider.setText("Incline Angle");
        angleslider.setMinimum(0.);
        angleslider.setMaximum(90.);
        angleslider.setSliderWidth(150);
        angleslider.addPropertyChangeListener("value", this);
        angleslider.setValue(45.);
        //controls.add(angleslider);
        //addElement(angleslider);
        
        hintLabel = new JLabel();
        hintLabel.setText(" ");
        addElement(hintLabel);
        
        textarea = new JTextArea();
        textarea.setBorder(new BevelBorder(BevelBorder.LOWERED));
        textarea.setRows(4);
        textarea.setLineWrap(true);
        addElement(textarea);

		// Launch
		addActions();
		
//		 HACK:  I can't call reset here since the OrientedShape3D of the SpatialTextLabels throws an exception
		// if you try to manipulate it before the viewer is "ready"... Somehow the viewer isn't completely initialized
		// at this point.
		resetSlidingBox();
        resetCamera();
        setUserMode(false);
        mSEC.step();
        
        
        randomizeForceVectors();
        //setUserMode(true);
       
        
        //mViewer.doStatus(0);
        mSEC.step();
        mSEC.setVisible(false);
        this.setUserMode(true);
        this.randomizeForceVectors();
        System.out.println("mFramework = " + mFramework);
        
        setBackgroundColor(new Color(240,240,255));
        //mViewer.setAlternateAppearance(null);
	}
	
	public void randomizeForceVectors() {
		normal.setDirection(new Vector3d(Math.random()*((Math.random() > 0.5)?-1.:1.),Math.random()*((Math.random() > 0.5)?-1.:1.),0. ));
        grav.setDirection(new Vector3d(Math.random()*((Math.random() > 0.5)?-1.:1.),Math.random()*((Math.random() > 0.5)?-1.:1.),0. ));
        spring.setDirection(new Vector3d(Math.random()*((Math.random() > 0.5)?-1.:1.),Math.random()*((Math.random() > 0.5)?-1.:1.),0. ));
        damp.setDirection(new Vector3d(Math.random()*((Math.random() > 0.5)?-1.:1.),Math.random()*((Math.random() > 0.5)?-1.:1.),0. ));
	}

	

	void addActions() {
		TealAction a = new TealAction("Information",this);
		addAction("Help",a);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareToIgnoreCase("Information") == 0) {
			if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/InclinedPlaneAppHelp.htm");
			}
		} else if (e.getActionCommand().compareToIgnoreCase("Test") == 0) {
			if (checkUserForceDiagram()) {
				hintLabel.setText(" ");
				textarea.setText("");
				int answerCode = (iAngle == 0) ? 2222 : 4444;
				textarea.setText("Correct!  Answer Code: " + answerCode);
				this.setUserMode(false);
				mSEC.start();
			} else {
				hintLabel.setText("Hint:");
				textarea.setText(hintText);
			}
		}
        else {
			super.actionPerformed(e);
		}
	}
	
	public boolean checkUserForceDiagram() {
		Vector3d gravCheck = grav.getDirection();
		gravCheck.normalize();
		Vector3d normalCheck = normal.getDirection();
		normalCheck.normalize();
		Vector3d springCheck = spring.getDirection();
		springCheck.normalize();
		Vector3d dampCheck = damp.getDirection();
		dampCheck.normalize();
		
		System.out.println("gravCheck: " + gravCheck);
		System.out.println("normalCheck: " + normalCheck);
		System.out.println("springCheck: " + springCheck);
		System.out.println("dampCheck: " + dampCheck);
		
		Vector3d g = getGravity();
		g.normalize();
		
		Vector3d n = incline.getNormal();
		n.normalize();
		
		Vector3d s = slidingbox.getConstraintAtIndex(0).getLastReaction();
		s.normalize();
		
		Vector3d d = new Vector3d(slidingbox.getVelocity());
		d.normalize();
		d.scale(-1.);
		
		System.out.println("gravity = " + g);
		System.out.println("normal = " + n);
		System.out.println("spring = " + s);
		System.out.println("damping = " + d);
		
		boolean gravOK = (gravCheck.dot(g) > 0.98) ? true : false;
		boolean normalOK = (normalCheck.dot(n) > 0.98) ? true : false;
		boolean springOK = (springCheck.dot(s) > 0.98) ? true : false;
		boolean dampOK = (dampCheck.dot(d) > 0.98) ? true : false;
		
		int numWrong = 0;
		if (!gravOK) numWrong += 1;
		if (!normalOK) numWrong += 1;
		if (!springOK) numWrong += 1;
		if (!dampOK) numWrong += 1;
		
		System.out.println("TEST RESULTS>   gravOK: " + gravOK + " normalOK: " + normalOK + " springOK: " + springOK + " dampOK: " + dampOK);
		
		boolean result = gravOK && normalOK && springOK && dampOK;
		
		hintText = "";
		
		hintText = "Gravitational Force (green): " + (gravOK ? "CORRECT!" : "Incorrect.") + " \n" +
		   "Spring Force (blue): " + (springOK ? "CORRECT!" : "Incorrect.") + " \n" +
		   "Normal Force (red): " + (normalOK ? "CORRECT!" : "Incorrect.") + " \n" +
		   "Damping Force (grey): " + (dampOK ? "CORRECT!" : "Incorrect.");
		
		//Vector3d box = slidingbox.getPosition();
		//box.sub(((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getPoint());
		//System.out.println("Spring Length: " + box.length());
		return result;
	}
	
	public void setUserMode(boolean set) {
		if (set) {
			normal.setScaleByMagnitude(false);
			normal.setArrowScale(arrowScale*2.);
			normal.setUpdating(false);
			normal.setSelectable(true);
			normal.setRotable(true);
			
			grav.setScaleByMagnitude(false);
			grav.setArrowScale(arrowScale*2.);
			grav.setUpdating(false);
			grav.setSelectable(true);
			grav.setRotable(true);
			
			damp.setScaleByMagnitude(false);
			damp.setArrowScale(arrowScale*2.);
			damp.setUpdating(false);
			damp.setSelectable(true);
			damp.setRotable(true);
			
			spring.setScaleByMagnitude(false);
			spring.setArrowScale(arrowScale*2.);
			spring.setUpdating(false);
			spring.setSelectable(true);
			spring.setRotable(true);
		} else {
			normal.setScaleByMagnitude(true);
			normal.setArrowScale(arrowScale);
			normal.setUpdating(true);
			normal.setSelectable(false);
			normal.setRotable(false);
			
			grav.setScaleByMagnitude(true);
			grav.setArrowScale(arrowScale);
			grav.setUpdating(true);
			grav.setSelectable(false);
			grav.setRotable(false);
			
			damp.setScaleByMagnitude(true);
			damp.setArrowScale(arrowScale);
			damp.setUpdating(true);
			damp.setSelectable(false);
			damp.setRotable(false);
			
			spring.setScaleByMagnitude(true);
			spring.setArrowScale(arrowScale);
			spring.setUpdating(true);
			spring.setSelectable(false);
			spring.setRotable(false);
		}
	}
	
	public void updateSpatialLabels() {
		lbl1.forceUpdate();
		lbl2.forceUpdate();
		lbl3.forceUpdate();
		lbl4.forceUpdate();
	}

	public void propertyChange(PropertyChangeEvent pce)
	{
		if (pce.getSource() == angleslider) {
            //System.out.println("am i even getting this pce?");
            double angle = ((Double) pce.getNewValue()).doubleValue();
            setAngle(angle);
		} else {
			super.propertyChange(pce);
		}
	}

	public void reset() {

		
		resetSlidingBox();
        resetCamera();
        setUserMode(false);
        mSEC.step();
        
        
        randomizeForceVectors();
        setUserMode(true);
        updateSpatialLabels();
        
        //mGUI.refresh();
	}

	
	private void resetSlidingBox() {
		Vector3d position = incline.locationToPosition(boxPos); //iLength);
		// Below, the 'false' argument prevents from triggering the frets, in the case
		// when it is located at the very beginning of the incline. If the fret is
		// triggered by this reset, no change of state will be picked when the
		// simulation starts running.
		slidingbox.setPosition(position, false); 
		Vector3d vel = new Vector3d(position);
		vel.sub(incline.locationToPosition(0.));
		vel.normalize();
		vel.scale(boxVel);
		slidingbox.setVelocity(vel); //new Vector3d());
	}

    public void resetCamera() {
        //mViewer.setLookAt(new Point3d(-0.1,0.1,0.1), new Point3d(0.,-0.1,-0.1), new Vector3d(0.,1.,0.));
    	//mViewer.setLookAt(new Point3d(0,0.1,0.15), new Point3d(0,0,0), new Vector3d(0,1,0));
    	setLookAt(new Point3d(0,0.05,0.2), new Point3d(0,0.05,0), new Vector3d(0,1,0));
    }
    

	
	public void setAngle(double angle) {
		double rad = (angle / 180.) * Math.PI;
        mSEC.stop();
        
        iAngle = rad;
        incline.setInclineAngle(iAngle);
		
        //setUserMode(true);
        
		Vector3d position = incline.locationToPosition(springPos);
		slidingbox.setPosition(position,false);
		slidingbox.updateFromIncline();
		//position.add(new Vector3d(0.4*Math.cos(iAngle),0.4*Math.sin(iAngle),0.));
		
		//setUserMode(true);
		
		((SpringConstraint)slidingbox.getConstraintAtIndex(0)).setPoint(position);
		line.setPosition(position);
		//System.out.println("SpringConstraint::getPoint() : " + ((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getPoint());
		
		Vector3d eqpos = new Vector3d(((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getPoint());
		Vector3d slope = new Vector3d(Math.cos(iAngle),Math.sin(iAngle),0.);
		slope.normalize();
		Vector3d offset = new Vector3d();
		offset.cross(slope, new Vector3d(0.,0.,1.));
		offset.normalize();
		offset.scale(-0.6);
		slope.scale(-((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getRestLength());
		eqpos.add(slope);
		eqpos.add(offset);
		flatEQ.setPosition(eqpos);
		flatEQ.setRotation(slidingbox.getRotation());
		
		slope.normalize();
		slope.scale(((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getRestLength() + (slidingbox.getMass()*getGravity().length()*Math.sin(iAngle))/((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getCoefficient());
		Vector3d angleeqpos = new Vector3d(((SpringConstraint)slidingbox.getConstraintAtIndex(0)).getPoint());
		angleeqpos.add(slope);
		angleeqpos.add(offset);
		angleEQ.setPosition(angleeqpos);
		angleEQ.setRotation(slidingbox.getRotation());
		
        
        theEngine.requestSpatial();
        
        
        reset();
	}
	
	public double getAngle() {
		return iAngle;
	}
	/**
	 * @return Returns the boxPos.
	 */
	public double getBoxPos() {
		return boxPos;
	}
	/**
	 * @param boxPos The boxPos to set.
	 */
	public void setBoxPos(double boxPos) {
		this.boxPos = boxPos;
		reset(); //SlidingBox();
	}
	/**
	 * @return Returns the boxVel.
	 */
	public double getBoxVel() {
		return boxVel;
		
	}
	/**
	 * @param boxVel The boxVel to set.
	 */
	public void setBoxVel(double boxVel) {
		this.boxVel = boxVel;
		reset(); //SlidingBox();
	}
}
