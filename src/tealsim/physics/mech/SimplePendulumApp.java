/* $Id: SimplePendulumApp.java,v 1.8 2010/08/10 18:12:35 stefan Exp $ */

package tealsim.physics.mech;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import teal.render.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.app.SouthGUI;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.render.HasPosition;
import teal.render.Rendered;
import teal.render.j3d.LineNode;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.primitives.Stem;
import teal.sim.constraint.ArcConstraint;
import teal.physics.physical.Ball;
import teal.physics.physical.Wall;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.ComponentForceVector;
import teal.sim.spatial.SpatialTextLabel;
import teal.ui.UIPanel;
import teal.ui.swing.LookAndFeelTweaks;
import teal.util.TDebug;
import teal.physics.em.SimEM;

public class SimplePendulumApp extends SimEM {

	private static final long serialVersionUID = 3258689905680201780L;

	JTable timetable;

	JTable inferences;

	SpatialTextLabel lbl1, lbl2, lbl3;

	ComponentForceVector grav;

	ComponentForceVector normal;

	ComponentForceVector damp;

	ComponentForceVector spring;

	double arrowScale = 0.2;

	Ball ball;

	Vector3d position = new Vector3d(0., 1., 0.);

	Vector3d ballOffset = new Vector3d(0., -5., 0.);

	// Problem parameters
	double gravity = 9.81; // in m/s2

	double mass = 0.2; // in kg

	double time_interval = 0.5; // in seconds

	double startAngle = Math.PI * 0.25; // Starting angle (from vertical) of
										// pendulum in radians.

	private JTextArea textarea;

	private String hintText;

	private JLabel hintLabel;

	private UIPanel buttoncontrol;

//	private EMEngine emModel = null;

	public SimplePendulumApp() {
		super();
		title = "Simple Pendulum";
		TDebug.setGlobalLevel(0);

		setGui(new SouthGUI());

//		emModel = new EMEngine();
		setBoundingArea(new BoundingSphere(new Point3d(), 8));
		// World parameters and initialization.
		setDamping(0.05);
		setGravity(new Vector3d(0., -gravity, 0.));
		setDeltaTime(0.01);
		// emModel.setDelay(0);
		// emModel.setNanoDelay(1000);
		// emModel.setDelayAdaptation(true);
//		setEngine(emModel);

		// Sliding Box
		ball = new Ball();

		ball.setPosition(new Vector3d(5., 2., 0.));
		ball.setRadius(0.1);
		ball.setColor(new Color(255, 100, 100));

		ball.setMoveable(true);
		ball.setSelectable(false);
		ball.setPickable(false);
		ball.setMass(mass);
		ball.setVelocity(new Vector3d());
		ball.setRotable(false);
		ball.setColliding(false);
		ball.setConstrained(true);

		ArcConstraint arc = new ArcConstraint(position,
				new Vector3d(0., 0., 1.), 2.);
		// SpringConstraint s = new SpringConstraint(position, 4.0,50.);
		ball.addConstraint(arc);
		// ball.addConstraint(s);

		AxisAngle4d aa = new AxisAngle4d(arc.getNormal(), startAngle);
		Transform3D t = new Transform3D();
		t.setRotation(aa);
		t.transform(ballOffset);
		System.out.println("Transformed vec: " + ballOffset);
		Vector3d pos = new Vector3d(ballOffset);
		pos.add(position);

		ball.setPosition(pos);

		SpatialTextLabel lbl = new SpatialTextLabel("This is a Ball!", ball);
		lbl.setScale(0.1);
		lbl.setPositionOffset(new Vector3d(0.1, 0.1, 0.));
		// addElement(lbl);

		addElement(ball);
		grav = new ComponentForceVector(ball, ComponentForceVector.TYPE_GRAVITY);
		damp = new ComponentForceVector(ball, ComponentForceVector.TYPE_DAMPING);
		spring = new ComponentForceVector(ball,
				ComponentForceVector.TYPE_CONSTRAINT);
		spring.setConstraintIndex(0);

		grav.setArrowScale(arrowScale);
		damp.setArrowScale(arrowScale);
		spring.setArrowScale(arrowScale);

		grav.setMoveable(false);
		damp.setMoveable(false);
		spring.setMoveable(false);

		grav.setPickable(true);
		grav.setRotable(true);
		grav.setScreenYRotationAxis(Rendered.ROTATION_AXIS_Z);
		grav.setScreenXRotationAxis(Rendered.ROTATION_AXIS_NONE);
		grav.setSelectable(true);

		spring.setPickable(true);
		spring.setRotable(true);
		spring.setScreenYRotationAxis(Rendered.ROTATION_AXIS_Z);
		spring.setScreenXRotationAxis(Rendered.ROTATION_AXIS_NONE);
		spring.setSelectable(true);
		spring.setScaleByMagnitude(false);

		damp.setPickable(true);
		damp.setRotable(true);
		damp.setScreenYRotationAxis(Rendered.ROTATION_AXIS_Z);
		damp.setScreenXRotationAxis(Rendered.ROTATION_AXIS_NONE);
		damp.setSelectable(true);

		grav.setColor(new Color(100, 255, 100));
		spring.setColor(new Color(100, 100, 255));
		damp.setColor(new Color(100, 100, 100));

		lbl1 = new SpatialTextLabel("gravity", grav);
		lbl1.setBaseScale(0.2);
		lbl1.setPositionOffset(new Vector3d(0.05, 0.0, 0.));
		lbl1.setRefDirectionOffset(0.5);
		lbl1.setUseDirectionOffset(true);
		addElement(lbl1);

		lbl2 = new SpatialTextLabel("tension", spring);
		lbl2.setBaseScale(0.2);
		lbl2.setPositionOffset(new Vector3d(0.05, 0.0, 0.));
		lbl2.setRefDirectionOffset(0.5);
		lbl2.setUseDirectionOffset(true);
		addElement(lbl2);

		lbl3 = new SpatialTextLabel("damping", damp);
		lbl3.setBaseScale(0.2);
		// lbl.setPositionOffset(new Vector3d(0.01,0.01,0.));
		lbl3.setRefDirectionOffset(0.5);
		lbl3.setUseDirectionOffset(true);
		addElement(lbl3);

		addElement(grav);
		addElement(damp);
		addElement(spring);

		Wall wall = new Wall(position, new Vector3d(2., 0., 0.), new Vector3d(
				0., 0., 2.));
		addElement(wall);

		setShowGizmos(false);

		// Helix line = new Helix(position,(HasPosition) ball);
		Stem line = new Stem(position, (HasPosition) ball);
		line.setColor(new Color(200, 200, 200));
		line.setRadius(0.2f * line.getRadius());

		boolean loadModels = false;

		Loader3DS max = new Loader3DS();
		if (loadModels) {

			BranchGroup bg2 = max.getBranchGroup(
					"models/Pendulum.3DS", "models/");
			LineNode node2 = new LineNode();
			node2.setScale(0.04);
			node2.addContents(bg2);
			// model = new Rendered();
			// model.setNode3D(node2);
			// addElement(model);
			line.setNode3D(node2);
			Transform3D offsetTrans = new Transform3D();
			offsetTrans.setScale(new Vector3d(0.08, 0.025, 0.025));
			AxisAngle4d a = new AxisAngle4d(new Vector3d(1., 0., 0.),
					-Math.PI * 0.5);
			offsetTrans.setRotation(a);
			offsetTrans.setTranslation(new Vector3d(0., 0.5, 0.));
			line.setModelOffsetTransform(offsetTrans);
			// line.setModelOffsetPosition(new Vector3d(-0., 0.75, 0));
			// roc.setDrawn(false);
			// roc.addPropertyChangeListener("position", this);
			// slidingbox.setPosition(rocPos);

		}

		addElement(line);

		// 3D viewer.
		mSEC.setVisible(false);
		mSEC.init();

		buttoncontrol = new UIPanel();
		buttoncontrol.setBorder(LookAndFeelTweaks.PANEL_BORDER);

		JButton testButton = new JButton(new TealAction("Test", "Test", this));
		testButton.setFont(testButton.getFont().deriveFont(Font.BOLD));
		buttoncontrol.add(testButton);

		addElement(buttoncontrol);
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
		// reset();

		// HACK: I can't call reset here since the OrientedShape3D of the
		// SpatialTextLabels throws an exception
		// if you try to manipulate it before the viewer is "ready"... Somehow
		// the viewer isn't completely initialized
		// at this point.
		resetSlidingBox();
		resetCamera();
		mSEC.step();
		// randomizeForceVectors();

		 mFramework.doStatus(0);
		mSEC.step();
		this.setUserMode(true);
		// normal.setDirection(new Vector3d(Math.random()*((Math.random() >
		// 0.5)?-1.:1.),Math.random()*((Math.random() > 0.5)?-1.:1.),0. ));
		randomizeForceVectors();
		setBackgroundColor(new Color(240,240,255));

	}

	void randomizeForceVectors() {
		grav.setDirection(new Vector3d(Math.random()
				* ((Math.random() > 0.5) ? -1. : 1.), Math.random()
				* ((Math.random() > 0.5) ? -1. : 1.), 0.));
		spring.setDirection(new Vector3d(Math.random()
				* ((Math.random() > 0.5) ? -1. : 1.), Math.random()
				* ((Math.random() > 0.5) ? -1. : 1.), 0.));
		damp.setDirection(new Vector3d(Math.random()
				* ((Math.random() > 0.5) ? -1. : 1.), Math.random()
				* ((Math.random() > 0.5) ? -1. : 1.), 0.));

	}

	void addActions() {
		TealAction a = new TealAction("Information", this);
		addAction("Help", a);
	}

	//boolean showFieldLines = true;

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareToIgnoreCase("Information") == 0) {
			// System.out.println( "The 'InclinedPlaneAppHelp.htm' help file
			// should be loaded here." );
			if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/SimplePendulumAppHelp.htm");
			}
		} else if (e.getActionCommand().compareToIgnoreCase("Test") == 0) {
			if (checkUserForceDiagram()) {
				hintLabel.setText(" ");
				textarea.setText("");
				textarea.setText("Correct!  Answer Code: 1111");
				this.setUserMode(false);
				mSEC.start();
			} else {
				hintLabel.setText("Hint:");
				textarea.setText(hintText);
			}
		} else {
			super.actionPerformed(e);
		}
	}

	public boolean checkUserForceDiagram() {

		Vector3d gravCheck = grav.getDirection();
		gravCheck.normalize();
		// Vector3d normalCheck = normal.getDirection();
		// normalCheck.normalize();
		Vector3d tensionCheck = spring.getDirection();
		tensionCheck.normalize();
		Vector3d dampCheck = damp.getDirection();
		dampCheck.normalize();

		System.out.println("gravCheck: " + gravCheck);
		// System.out.println("normalCheck: " + normalCheck);
		System.out.println("tensionCheck: " + tensionCheck);
		System.out.println("dampCheck: " + dampCheck);

		Vector3d g = theEngine.getGravity();
		g.normalize();

		// Vector3d n = incline.getNormal();
		// n.normalize();

		Vector3d t = ball.getConstraintAtIndex(0).getLastReaction();
		t.normalize();

		Vector3d d = new Vector3d(ball.getVelocity());
		d.normalize();
		d.scale(-1.);

		boolean gravOK = (gravCheck.dot(g) > 0.98) ? true : false;
		boolean tensionOK = (tensionCheck.dot(t) > 0.98) ? true : false;
		boolean dampOK = (dampCheck.dot(d) > 0.98) ? true : false;

		int numWrong = 0;
		if (!gravOK)
			numWrong += 1;
		if (!tensionOK)
			numWrong += 1;
		if (!dampOK)
			numWrong += 1;

		System.out.println("TEST RESULTS>   gravOK: " + gravOK + " tensionOK: "
				+ tensionOK + " dampOK: " + dampOK);

		boolean result = gravOK && tensionOK && dampOK;

		hintText = " ";
		hintText = "Gravitational Force (green): "
				+ (gravOK ? "CORRECT!" : "Incorrect.") + " \n"
				+ "Tension Force (blue): "
				+ (tensionOK ? "CORRECT!" : "Incorrect.") + " \n"
				+ "Damping Force (grey): "
				+ (dampOK ? "CORRECT!" : "Incorrect.") + " \n";

		// if (!result) textarea.setText(hintText);

		return result;

		// return true;
	}

	public void setUserMode(boolean set) {
		if (set) {
			// normal.setScaleByMagnitude(false);
			// normal.setArrowScale(arrowScale*2.);
			// normal.setUpdating(false);

			grav.setScaleByMagnitude(false);
			grav.setArrowScale(arrowScale * 2.);
			grav.setUpdating(false);

			damp.setScaleByMagnitude(false);
			damp.setArrowScale(arrowScale * 2.);
			damp.setUpdating(false);

			spring.setScaleByMagnitude(false);
			spring.setArrowScale(arrowScale * 2.);
			spring.setUpdating(false);
		} else {
			// normal.setScaleByMagnitude(true);
			// normal.setArrowScale(arrowScale);
			// normal.setUpdating(true);

			grav.setScaleByMagnitude(true);
			grav.setArrowScale(arrowScale);
			grav.setUpdating(true);

			damp.setScaleByMagnitude(true);
			damp.setArrowScale(arrowScale);
			damp.setUpdating(true);

			spring.setScaleByMagnitude(true);
			spring.setArrowScale(arrowScale);
			spring.setUpdating(true);
		}

	}

	public void updateSpatialLabels() {
		lbl1.forceUpdate();
		lbl2.forceUpdate();
		lbl3.forceUpdate();
	}

	public void propertyChange(PropertyChangeEvent pce) {
		super.propertyChange(pce);
	}

	public void reset() {

		resetSlidingBox();

		resetCamera();
		mSEC.step();
		randomizeForceVectors();
		setUserMode(true);
		updateSpatialLabels();
		// mGUI.refresh();
	}

	private void resetSlidingBox() {
		Vector3d pos = new Vector3d(ballOffset);
		pos.add(position);
		ball.setPosition(pos, false);
		ball.setVelocity(new Vector3d());
	}

	public void resetCamera() {
		// mViewer.setLookAt(new Point3d(-0.1,0.1,0.1), new
		// Point3d(0.,-0.1,-0.1), new Vector3d(0.,1.,0.));
		setLookAt(new Point3d(0, 0.0, 0.3), new Point3d(0, 0, 0),
				new Vector3d(0, 1, 0));
	}
}
