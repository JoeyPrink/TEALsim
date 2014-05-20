/* $Id: SimplePendulumImpulseApp.java,v 1.9 2010/08/10 18:12:35 stefan Exp $ */

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
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import teal.app.SouthGUI;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.render.HasPosition;
import teal.render.Rendered;
import teal.render.j3d.LineNode;
import teal.render.j3d.ShapeNode;
import teal.render.j3d.geometry.Cylinder;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.primitives.Stem;
import teal.render.scene.TShapeNode;
import teal.sim.constraint.ArcConstraint;
import teal.sim.engine.TEngineControl;
import teal.sim.engine.EngineRendered;
import teal.sim.engine.EngineControl;
import teal.physics.physical.Ball;
import teal.physics.physical.Wall;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.ComponentForceVector;
import teal.sim.spatial.SpatialTextLabel;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyCheck;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;

import teal.physics.em.SimEM;

public class SimplePendulumImpulseApp extends SimEM {

	private static final long serialVersionUID = 3258689905680201780L;
    JTable timetable;
	JTable inferences;
	
	ControlGroup controls;
	JLabel label01;
	JLabel label02;
	JLabel label03;
	JTextArea textarea01;
	JTextArea textarea02;
	PropertyCheck posCheckBox;
	PropertyCheck speedCheckBox;

	SpatialTextLabel lbl1, lbl2, lbl3;
	Color lblColor = new Color(0,0,0);
	ComponentForceVector grav;
	ComponentForceVector normal;
	ComponentForceVector damp;
	ComponentForceVector spring;
	double arrowScale = 0.2*0.2;
	
	Ball ball;
	Vector3d position = new Vector3d(0.,1.,0.);
	Vector3d impPosition = new Vector3d(-0.2,1.,0.);
	Vector3d ballOffset = new Vector3d(0.,-5.,0.);
	Vector3d impBallOffset = new Vector3d(-0.,-5.,-0.);
	
	
	Ball impulseBall;
	boolean collisionOccured = false;
	int fadeMax = 60;
	int fadeCount = 0;
	
	Stem line;
	Stem impline;
	
	
	
	
	// Problem parameters
	double gravity = 9.81; // in m/s2
	double mass = 1.; //0.2; // in kg
	double time_interval = 0.5; // in seconds

	double startAngle = 0.; //Math.PI*0.25;  // Starting angle (from vertical) of pendulum in radians.
	double impStartAngle = -Math.PI*0.5;
//	private EMEngine emModel = null;
	
	public SimplePendulumImpulseApp() {
		super();
		title = "Colliding Pendulums";
		TDebug.setGlobalLevel(0);
        
		setGui(new SouthGUI());
        
        BoundingSphere bs = new BoundingSphere(new Point3d(0, 1.6, 0), 03.5);
        theEngine.setBoundingArea(bs);
        theEngine.setDeltaTime(0.005); 
        theScene.setBoundingArea(bs);
		theEngine.requestRefresh();

		//mSEC.rebuildPanel(EngineControl.DO_DEFAULT);
		mSEC.init();
		
		// ball
		ball = new Ball();
		
		ball.setPosition(new Vector3d(5.,2.,0.));
		ball.setRadius(0.1);
		ball.setColor(new Color(255,100,100));
		
		ball.setMoveable(true);
		ball.setSelectable(false);
		ball.setPickable(false);
		ball.setMass(mass);
		ball.setVelocity(new Vector3d());
		ball.setRotable(false);
		ball.setColliding(true);
		ball.setConstrained(true);
		ball.getCollisionController().setTolerance(ball.getCollisionController().getTolerance()*2.);
		
		ArcConstraint arc = new ArcConstraint(position, new Vector3d(0.,0.,1.), 2.);
		//SpringConstraint s = new SpringConstraint(position, 4.0,50.);
		ball.addConstraint(arc);
		//ball.addConstraint(s);
		
		AxisAngle4d aa = new AxisAngle4d(arc.getNormal(),startAngle);
		Transform3D t = new Transform3D();
		t.setRotation(aa);
		t.transform(ballOffset);
		System.out.println("Transformed vec: " + ballOffset);
		Vector3d pos = new Vector3d(ballOffset);
		pos.add(position);
		
		ball.setPosition(pos);
		
		SpatialTextLabel lbl = new SpatialTextLabel("This is a Ball!", ball);
		lbl.setScale(0.1);
		lbl.setPositionOffset(new Vector3d(0.1,0.1,0.));
		//addElement(lbl);
		
		ball.addPropertyChangeListener("position",this);
		
        addElement(ball);
        
        
//      impulseBall
        impulseBall = new Ball();
		
        impulseBall.setPosition(new Vector3d(5.,2.,0.));
        impulseBall.setRadius(0.1);
        impulseBall.setColor(new Color(100,100,255));
		
        impulseBall.setMoveable(true);
        impulseBall.setSelectable(false);
        impulseBall.setPickable(false);
        impulseBall.setMass(mass*2.47);
        impulseBall.setVelocity(new Vector3d());
        impulseBall.setRotable(false);
        impulseBall.setColliding(true);
        impulseBall.setConstrained(true);
        impulseBall.getCollisionController().setTolerance(ball.getCollisionController().getTolerance()*2.);
		
		ArcConstraint imparc = new ArcConstraint(impPosition, new Vector3d(0.,0.,1.), 2.);
		//SpringConstraint s = new SpringConstraint(position, 4.0,50.);
		impulseBall.addConstraint(imparc);
		//ball.addConstraint(s);
		
		aa = new AxisAngle4d(arc.getNormal(),impStartAngle);
		t = new Transform3D();
		t.setRotation(aa);
		t.transform(impBallOffset);
		System.out.println("Transformed vec: " + impBallOffset);
		pos = new Vector3d(impBallOffset);
		pos.add(impPosition);
		
		impulseBall.setPosition(pos);
		
        addElement(impulseBall);
        
        //VelocityVector v = new VelocityVector(impulseBall);
        //v.getNode3D().setScale(0.1);
        //addElement(v);
        
        
        
        
        
        
        
        
        grav = new ComponentForceVector(ball, ComponentForceVector.TYPE_GRAVITY);
        damp = new ComponentForceVector(ball, ComponentForceVector.TYPE_DAMPING);
        spring = new ComponentForceVector(ball, ComponentForceVector.TYPE_CONSTRAINT);
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
        
        grav.setColor(new Color(100,255,100));
        spring.setColor(new Color(100,100,255));
        damp.setColor(new Color(100,100,100));
        
        lbl1 = new SpatialTextLabel("gravity", grav);
		lbl1.setBaseScale(.25);
		lbl1.setPositionOffset(new Vector3d(0.05,0.0,0.));
		lbl1.setRefDirectionOffset(0.5);
		lbl1.setUseDirectionOffset(true);
		lbl1.setColor(lblColor);
		addElement(lbl1);
		
		lbl2 = new SpatialTextLabel("tension", spring);
		lbl2.setBaseScale(.25);
		lbl2.setPositionOffset(new Vector3d(0.05,0.0,0.));
		lbl2.setRefDirectionOffset(0.5);
		lbl2.setUseDirectionOffset(true);
		lbl2.setColor(lblColor);
		addElement(lbl2);
		
		lbl3 = new SpatialTextLabel("damping", damp);
		lbl3.setBaseScale(.5);
		//lbl.setPositionOffset(new Vector3d(0.01,0.01,0.));
		lbl3.setRefDirectionOffset(0.5);
		lbl3.setUseDirectionOffset(true);
		//addElement(lbl3);
        
        
        addElement(grav);
        //addElement(damp);
        addElement(spring);
        
        Wall wall = new Wall(position, new Vector3d(2.,0.,0.), new Vector3d(0.,0.,2.));
        addElement(wall);
       
        
        setShowGizmos(false);
        
        //Helix line = new Helix(position,(HasPosition) ball);
        line = new Stem(position,(HasPosition) ball);
		line.setColor(new Color(100, 100, 100));
		line.setRadius(0.2f*line.getRadius());
		
		impline = new Stem(impPosition,(HasPosition) impulseBall);
		impline.setColor(new Color(100, 100, 100));
		impline.setRadius(0.2f*impline.getRadius());
		
		EngineRendered wedge = new EngineRendered();
		ShapeNode s = new ShapeNode();
		s.setGeometry(Cylinder.makeGeometry(24, 2., 0.0, 0., true, 0.125*Math.PI,-0.25*1.75*Math.PI));
		s.setColor(new Color3f(new Color(255,100,100)));
		s.setTransparency(0.75f);
		wedge.setNode3D(s);
		wedge.setPosition(position);
		AxisAngle4d aaa = new AxisAngle4d(new Vector3d(1.,0.,0.), -Math.PI*0.5);
		Quat4d q = new Quat4d();
		q.set(aaa);
		wedge.setRotation(q);
		addElement(wedge);
		
		
		
		
		boolean loadModels = false;
		
		Loader3DS max = new Loader3DS();
		if (loadModels) {

            BranchGroup bg2 = max.getBranchGroup("models/Pendulum.3DS", "models/");
            LineNode node2 = new LineNode();
            node2.setScale(0.04);
            node2.addContents(bg2);
            //model = new Rendered();
            //model.setNode3D(node2);
            //addElement(model);
            line.setNode3D(node2);
            Transform3D offsetTrans = new Transform3D();
            offsetTrans.setScale(new Vector3d(0.08,0.025,0.025));
            AxisAngle4d a = new AxisAngle4d(new Vector3d(1.,0.,0.), -Math.PI*0.5);
            offsetTrans.setRotation(a);
            offsetTrans.setTranslation(new Vector3d(0.,0.5,0.));
            line.setModelOffsetTransform(offsetTrans);
            //line.setModelOffsetPosition(new Vector3d(-0., 0.75, 0));
            //roc.setDrawn(false);
            //roc.addPropertyChangeListener("position", this);
            //slidingbox.setPosition(rocPos);

        
		}

		addElement(line);
		addElement(impline);
 
        controls = new ControlGroup();
        controls.setText("Parameters");
        
        PropertyDouble massSlider = new PropertyDouble();
        massSlider.setText("Mass");
        massSlider.addRoute(impulseBall,"mass");
        massSlider.setMinimum(0.1);
        massSlider.setMaximum(8.);//0.8);
        massSlider.setValue(1.); //0.2);
        
        controls.add(massSlider);
        
        posCheckBox = new PropertyCheck();
        posCheckBox.setText("Position OK:");
        posCheckBox.setValue(false);
        posCheckBox.setEnabled(false);
        controls.add(posCheckBox);
        
        speedCheckBox = new PropertyCheck();
        speedCheckBox.setText("Speed OK:");
        speedCheckBox.setValue(false);
        speedCheckBox.setEnabled(false);
        controls.add(speedCheckBox);
        
        label01 = new JLabel(" "); // using this is a spacer
        //controls.add(label01);
        textarea01 = new JTextArea("In the applet window, click and drag the tips of the three vectors to the positions appropriate to this situation.  We are looking for the proper directions of the forces only, not the magnitudes.  When you have the four vectors in the position you think is correct, hit 'test' on the button in the applet and see if you are correct.  The pendulum is on its way down to start.");
        textarea01.setEditable(false);
        textarea01.setLineWrap(true);
        textarea01.setWrapStyleWord(true);
        textarea01.setSize(220,100);
        textarea01.setFont(label01.getFont());
        textarea01.setBackground(controls.getBackground());
        
        //controls.add(textarea01);
        //controls.add(label01);
        JButton testButton = new JButton(new TealAction("Test", "Test", this));
        testButton.setFont(testButton.getFont().deriveFont(Font.BOLD));
        testButton.setBounds(40, 570, 195, 24);
        //controls.add(testButton);
        
        label02 = new JLabel("");
        //controls.add(label02);
        
        
        textarea02 = new JTextArea("");
        textarea02.setEditable(false);
        textarea02.setLineWrap(true);
        textarea02.setWrapStyleWord(true);
        textarea02.setSize(220,100);
        textarea02.setFont(label01.getFont());
        textarea02.setBackground(controls.getBackground());
        controls.add(textarea02);
        //label03 = new JLabel("");
        //controls.add(label03);

        
        addElement(controls);
        
        
        
        
        
        
        

		// Launch
		addActions();
		//reset();
		
		// HACK:  I can't call reset here since the OrientedShape3D of the SpatialTextLabels throws an exception
		// if you try to manipulate it before the viewer is "ready"... Somehow the viewer isn't completely initialized
		// at this point.
		resetSlidingBox();
		resetCamera();
		this.setUserMode(false);
        //mSEC.step();
        //randomizeForceVectors();
		
		
		
		setBackgroundColor(new Color(240,240,255));
		 mFramework.doStatus(0);
        this.updateSpatialLabels();
        //mSEC.step();
        
        //normal.setDirection(new Vector3d(Math.random()*((Math.random() > 0.5)?-1.:1.),Math.random()*((Math.random() > 0.5)?-1.:1.),0. ));
        //randomizeForceVectors();
        reset();
        
        
        
	}

	void randomizeForceVectors() {
		grav.setDirection(new Vector3d(Math.random()*((Math.random() > 0.5)?-1.:1.),Math.random()*((Math.random() > 0.5)?-1.:1.),0. ));
        spring.setDirection(new Vector3d(Math.random()*((Math.random() > 0.5)?-1.:1.),Math.random()*((Math.random() > 0.5)?-1.:1.),0. ));
        damp.setDirection(new Vector3d(Math.random()*((Math.random() > 0.5)?-1.:1.),Math.random()*((Math.random() > 0.5)?-1.:1.),0. ));

	}


	void addActions() {
		TealAction a = new TealAction("Information",this);
		addAction("Help",a);
	}

	boolean showFieldLines = true;

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareToIgnoreCase("Information") == 0) {
			//System.out.println( "The 'InclinedPlaneAppHelp.htm' help file should be loaded here." );
			if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/SimplePendulumImpulseAppHelp.htm");
			}
		} else if (e.getActionCommand().compareToIgnoreCase("Test") == 0) {
			if (checkUserForceDiagram()) {
				label02.setText("Correct!  You're smart!");
				textarea02.setText("");
				//label03.setText("");
				this.setUserMode(false);
				mSEC.start();
			} else {
				label02.setText("Incorrect!");
			}
			System.out.println("BALL POSITION: " + ball.getPosition());
		}
        else {
			super.actionPerformed(e);
		}
	}
	
	public boolean checkUserForceDiagram() {
		
		Vector3d gravCheck = grav.getDirection();
		gravCheck.normalize();
		//Vector3d normalCheck = normal.getDirection();
		//normalCheck.normalize();
		Vector3d tensionCheck = spring.getDirection();
		tensionCheck.normalize();
		Vector3d dampCheck = damp.getDirection();
		dampCheck.normalize();
		
		System.out.println("gravCheck: " + gravCheck);
		//System.out.println("normalCheck: " + normalCheck);
		System.out.println("tensionCheck: " + tensionCheck);
		System.out.println("dampCheck: " + dampCheck);
		
		Vector3d g = new Vector3d(0., -gravity, 0.);
		g.normalize();
		
		//Vector3d n = incline.getNormal();
		//n.normalize();
		
		Vector3d t = ball.getConstraintAtIndex(0).getLastReaction();
		t.normalize();
		
		Vector3d d = new Vector3d(ball.getVelocity());
		d.normalize();
		d.scale(-1.);
		
		
		boolean gravOK = (gravCheck.dot(g) > 0.98) ? true : false;
		boolean tensionOK = (tensionCheck.dot(t) > 0.98) ? true : false;
		boolean dampOK = (dampCheck.dot(d) > 0.98) ? true : false;
		
		int numWrong = 0;
		if (!gravOK) numWrong += 1;
		if (!tensionOK) numWrong += 1;
		if (!dampOK) numWrong += 1;
		
		System.out.println("TEST RESULTS>   gravOK: " + gravOK + " tensionOK: " + tensionOK + " dampOK: " + dampOK);
		
		boolean result = gravOK && tensionOK && dampOK;
		
		String hintText = "";
		String firstWrong = "";
		String secondWrong = "";
		if (numWrong > 0) {
			firstWrong = gravOK ? (tensionOK ? "damping force" : "tension force") : "gravitational force";
			secondWrong = tensionOK ? "damping force" : "tension force";
		}
		
		if (numWrong == 3) {
			hintText = "You got them ALL wrong!";
		} else if (numWrong == 2){
			hintText = "Your " + firstWrong + " and " + secondWrong + " are both wrong!";
		} else if (numWrong == 1) {
			hintText = "Your " + firstWrong + " is wrong.";
		}
		
		hintText = "\nHint: \n" +
				   "Gravitational Force (green): " + (gravOK ? "CORRECT!" : "Incorrect.") + " \n" +
				   "Tension Force (blue): " + (tensionOK ? "CORRECT!" : "Incorrect.") + " \n" +
				   "Damping Force (grey): " + (dampOK ? "CORRECT!" : "Incorrect.") + " \n";
		
		if (!result) textarea02.setText(hintText);
		
		
		
		return result;
		
		//return true;
	}
	
	public boolean checkPendulumAgainstPosition(Vector3d checkPos, double tolerance) {
		// here we test to see if the pendulum is vertical (within a tolerance) and its velocity is zero (within a tolerance)
		Vector3d p = new Vector3d(ball.getPosition());
		p.sub(position);
		p.normalize();
		
		checkPos.normalize();
		double dot = p.dot(checkPos);
		
		boolean nearCheckPos = false;
		
		
		if (dot >= 1.0 - tolerance) nearCheckPos = true;
		
		
		return nearCheckPos;
	}
	
	public boolean checkPendulumAgainstVelocity(Vector3d checkVel, boolean justSpeed, double tolerance) {
		if (justSpeed) {
			if (ball.getVelocity().length() <= tolerance) return true;
		} else {
			return false;
		}
		return false;
	}
	
	public void setUserMode(boolean set) {
		if (set) {
			//normal.setScaleByMagnitude(false);
			//normal.setArrowScale(arrowScale*2.);
			//normal.setUpdating(false);
			
			grav.setScaleByMagnitude(false);
			grav.setArrowScale(arrowScale*2.);
			grav.setUpdating(false);
			
			damp.setScaleByMagnitude(false);
			damp.setArrowScale(arrowScale*2.);
			damp.setUpdating(false);
			
			spring.setScaleByMagnitude(false);
			spring.setArrowScale(arrowScale*2.);
			spring.setUpdating(false);
		} else {
			//normal.setScaleByMagnitude(true);
			//normal.setArrowScale(arrowScale);
			//normal.setUpdating(true);
			
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
		//lbl3.forceUpdate();
	}

	public void propertyChange(PropertyChangeEvent pce)
	{
		if (pce.getSource() == ball) {
			boolean posCheck = this.checkPendulumAgainstPosition(new Vector3d(0.,1.,0.), 0.02);
			boolean velCheck = this.checkPendulumAgainstVelocity(new Vector3d(), true, 0.5);
			//System.out.println("collision has occured, we're moving");
			collisionOccured = true;
			if (collisionOccured && (fadeCount < fadeMax)) {
				//System.out.println("COLLISION OCCURED... FADING");
				((TShapeNode)impulseBall.getNode3D()).setTransparency(fadeCount/(float)fadeMax);
				((TShapeNode)impline.getNode3D()).setTransparency(fadeCount/(float)fadeMax);
				
				fadeCount++;
				if (fadeCount == fadeMax) {
					impulseBall.setColliding(false);
					impulseBall.setIntegrating(false);
					impulseBall.setDrawn(false);
				}
			}
			if (posCheck) {
				//System.out.println("In the zone!!!");
				if (!((Boolean)posCheckBox.getValue()).booleanValue()) posCheckBox.setValue(true);
				if (velCheck) {
					//System.out.println("AND SPEED OK!!!!");
					if (!((Boolean)speedCheckBox.getValue()).booleanValue()) speedCheckBox.setValue(true);
					
					mSEC.setSimState(TEngineControl.PAUSED);
					textarea02.setText("Congratulations!  You managed to stop the pendulum at its apex (within tolerances)!\n\nSuccess Code: 3333");
					
				} else {
					//System.out.println("But too fast!!!!!");
					if (((Boolean)speedCheckBox.getValue()).booleanValue()) speedCheckBox.setValue(false);
				}
			} else {
				if (((Boolean)posCheckBox.getValue()).booleanValue()) posCheckBox.setValue(false);
			}
			
			
		} else {
			super.propertyChange(pce);
		}
	}

	public void reset() {

		resetSlidingBox();
		
		textarea02.setText("");
		posCheckBox.setValue(false);
		speedCheckBox.setValue(false);
        resetCamera();
		updateSpatialLabels();
		mSEC.step();
		theEngine.requestRefresh();
	}

	
	private void resetSlidingBox() {
		Vector3d pos = new Vector3d(ballOffset);
		pos.add(position);
		ball.setPosition(pos, true); 
		ball.setVelocity(new Vector3d());
		
		pos = new Vector3d(impBallOffset);
		pos.add(impPosition);
		impulseBall.setPosition(pos,false);
		impulseBall.setVelocity(new Vector3d());
		((TShapeNode)impulseBall.getNode3D()).setTransparency(0.f);
		((TShapeNode)impline.getNode3D()).setTransparency(0.f);
		impulseBall.setDrawn(true);
		impulseBall.setColliding(true);
		impulseBall.setIntegrating(true);
		fadeCount = 0;
		collisionOccured = false;
	}

    public void resetCamera() {
    	setLookAt(new Point3d(0,0.0,0.3), new Point3d(0,0,0), new Vector3d(0,1,0));
    }
}
