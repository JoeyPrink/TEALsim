/* $Id: TeachSpinBase.java,v 1.21 2010/09/22 15:48:11 pbailey Exp $ */
/**
 * A stand-alone simulation of the TeachSpin coil experiment.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.21 $
 */
package tealsim.physics.em;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;

import teal.render.BoundingSphere;
//import javax.media.j3d.Transform3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import teal.math.RectangularPlane;
import teal.render.HasPosition;
import teal.render.j3d.GridNode;
import teal.render.j3d.Node3D;
import teal.render.j3d.ShapeNode;
import teal.render.j3d.geometry.Cylinder;
import teal.render.j3d.loaders.Loader3DS;
import teal.render.primitives.Helix;
import teal.render.primitives.Line;
import teal.render.scene.Model;
import teal.render.scene.TShapeNode;
import teal.sim.SimRendered;
import teal.sim.engine.EngineRendered;
import teal.physics.em.SimEM;
import teal.physics.physical.PhysicalObject;
import teal.physics.em.MagneticDipole;
import teal.physics.em.RingOfCurrent;
import teal.sim.properties.IsSpatial;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.sim.spatial.FluxFieldLine;
import teal.ui.control.meters.ControlMeter;
import teal.util.TDebug;
import teal.util.URLGenerator;

import com.sun.j3d.utils.geometry.GeometryInfo;

public class TeachSpinBase extends SimEM {

	private static final long serialVersionUID = 3258129146076214067L;
    public static final int FRAME_RATE = 1;
	public static final int METER = 2;
	public static final int CURRENT = 4;
	public static final int DUMMY = 8;
	public static final int MODEL = 16;
	public static final int SHOW_RINGS = 32;
	public static final int GENERATOR = 64;
	public static final int PROBE = 128;
	public static final int DEFAULT = METER | MODEL;
	public static final int TEST = METER | DUMMY | SHOW_RINGS;
	public static final int ALL = FRAME_RATE | METER | MODEL | DUMMY | CURRENT;
	// LOD FLAG results
	protected boolean useModel = false;
	protected boolean useCurrentSlider = false;
	protected boolean useMeter = false;
	protected boolean showRings = false;
	protected int nLines = 2;
	// Fieldline defaults
	protected double magS = 0.004;
	protected double magH = 0.01;
	protected int magMax = 100;
	protected double magMinD = 0.005;
	protected double fLen = 0.008; //0.008; //0.005
	protected int kMax = 160 * 1;
	protected double searchRad = 0.005;
	protected double minD = 0.01;
	protected int fMode = FieldLine.RUNGE_KUTTA;
	protected int symCount = 120;
	// physical constants & default values
	protected double radius = 0.06;
	protected double origY = -0.003;
	protected double tRadius = 0.0075;
	protected double coilHOff = 0.037;
	protected double defaultCurrent = 0.0; //-0.163;
	protected double numCoils = 168.0;
	protected double magMass = 0.002;
	protected double magMu = 0.2;
	protected double iScale = 0.1;
	// Object positions
	protected Vector3d modelPos;
	protected Vector3d coilPos;
	protected Vector3d coil2Pos;
	protected Vector3d rocPos;
	protected Vector3d roc2Pos;
	protected Vector3d magPos;
	// World Elements
	protected MagneticDipole m1;
	protected RingOfCurrent roc;
	protected RingOfCurrent roc2;
	protected FieldLine roc2FL1 = null;
	
	// MOdels
	protected SimRendered pb1;
	protected SimRendered model_1;
	protected SimRendered model_2;
	//Optional Elements
	protected TellFlux tf = null;;
	protected GridNode gridNode;
	protected int gridSize = 60;
	// GUI components
	protected ControlMeter meter;
	// State Values
	protected boolean sameState = false;
	protected boolean invertCur = true;
	protected boolean doBoth = true;
	//boolean isGenerating = false;
	protected double curValue = 0.0;
	protected Tuple3d cameras[] = {new Point3d(0., 0.0, 0.03), new Point3d(0., 0., 0.), new Vector3d(0., 1., 0.), new Point3d(0, 2.0, 0.),
			new Point3d(0.0, 0., 0.), new Vector3d(0., 1., 0.)};
	protected Vector3d fluxPoints[] = {
			new Vector3d(radius + (tRadius * 2.0), coilHOff + (tRadius * 2.0), 0.),
			new Vector3d(radius - (tRadius * 2.0), coilHOff - (tRadius * 2.0), 0.)};
	
	protected Vector fLines;
	protected FieldLineManager fmanager;
	protected FieldConvolution mDLIC;
	protected FieldDirectionGrid fv;
	protected File curDir = null;

	/*
	 * Tina edit 24.4
	 */
	protected void setCamera(int loc) {
		int offset = 3 * loc;
		setLookAt((Point3d) cameras[offset++], (Point3d) cameras[offset++], (Vector3d) cameras[offset]);
	}

	public TeachSpinBase() {
		this(2, 168., -1, ALL | GENERATOR);
	}

	public TeachSpinBase(int maxLines, double coilsPerRing, int debugLevel, int lod) {
		super();
		
		//setGui(new SimGUI());
		nLines = maxLines;
		useCurrentSlider = ((lod & CURRENT) == CURRENT);
		useModel = ((lod & MODEL) == MODEL);
		useMeter = ((lod & METER) == METER);
		showRings = ((lod & SHOW_RINGS) == SHOW_RINGS);
		TDebug.setGlobalLevel(debugLevel);
		//TDebug.setOutput("testOut.txt");
		modelPos = new Vector3d(0.038, -0.01, 0.);
		numCoils = coilsPerRing;
		roc2Pos = new Vector3d(0., -coilHOff, 0.);
		rocPos = new Vector3d(0., coilHOff, 0.);
		coilPos = new Vector3d(rocPos);
		coil2Pos = new Vector3d(coilPos.x, coilPos.y + 10, coilPos.z);
		magPos = new Vector3d(0., origY, 0.);
		try {
			title = "TEALsim Teach Spin";
			//mViewer.setBounds(20, 20, 580, 540);
			//mViewer.getBackgroundNode().setColor(new Color3f(new Color(37, 49, 80)));
			BoundingSphere bs = new BoundingSphere(new Point3d(0., 0, 0.), 0.250);
			setBoundingArea(bs);
			theScene.setBoundingArea(bs);
			mDLIC = new FieldConvolution();
			mDLIC.setSize(new Dimension(512,512));
			mDLIC.setComputePlane(new RectangularPlane(bs));
			/*
			 * edit by tina 23.4
			 */
//			Vector3d mouseScale = mView.getMouseMoveScale();
//			mouseScale.x *= 0.01;
//			mouseScale.y *= 0.01;
//			mouseScale.z *= 0.1;
//			setMouseMoveScale(mouseScale);
			
			setShowGizmos(false);
			//Create the EM elements
			m1 = new MagneticDipole();
			m1.setRadius(0.005);
			m1.setLength(0.010);
			m1.setPosition(magPos);
			m1.setRotable(false);
			m1.setMoveable(false);
			m1.setPickable(false);
			m1.setMu(magMu);// 0.2
			m1.setMass(magMass);
			//m1.setDrawn(false);
			roc = new RingOfCurrent();
			roc.setCurrent(defaultCurrent);
			roc.setInducing(false);
			roc.setPosition(rocPos);
			roc.setPickable(false);
			roc.setMoveable(false);
			roc.setRotable(false);
			roc.setRadius(radius);
			roc.setThickness(tRadius);
			//roc.setDrawn(showRings);
			roc2 = new RingOfCurrent();
			roc2.setCurrent(defaultCurrent);
			roc2.setInducing(false);
			roc2.setPosition(roc2Pos);
			roc2.setPickable(false);
			roc2.setMoveable(false);
			roc2.setRotable(false);
			roc2.setRadius(radius);
			roc2.setThickness(tRadius);
			//roc2.setDrawn(showRings);
			//Create a Rendered3D to hold the Max model
			if (useModel) {
               
               Model bgCoil = new Model("models/1_Coil_1.3DS");
               bgCoil.setScale(1./39.37);
               roc.setModel(bgCoil);
    
               //There's probably some way to clone this resource without having to load it again, but I couldn't figure it out.
//               Model bgCoil2 = new Model("models/1_Coil_1.3DS");
//               bgCoil2.setScale(1./39.37);
               roc2.setModel(bgCoil);
              
               
			}
			
            
            addElement(m1);
			addElement(roc);
            addElement(roc2);
            
			// Add GUI components
			TDebug.println(1,"useMeter = " + useMeter);
			if (useMeter) {
				meter = new ControlMeter(160, 160);
				meter.setDisplayRange(-1., 1.);
				meter.setDeviceAndPropertyName("Coil 1", "Current");
			}

			//layoutNormal();
			
			if (useMeter && meter != null)
				addElement(meter);
			
			
			// Optional tools
			if (((lod & PROBE) == PROBE)) {
				tf = new TellFlux();
				tf.setPosition(new Vector3d(0.001, 0.001, 0.));
				tf.setPickable(true);
				tf.setSelectable(true);
				addElement(tf);
			}
			
			if (((lod & DUMMY) == DUMMY)) {
				makeDummy();
			}
			
			setCurrent(defaultCurrent);
			
			fLines = new Vector();
		
			FieldLine fl = null;
			fmanager = new FieldLineManager();
			fmanager.setElementManager(this);
			/// top ring (roc) fieldlines
			fl = makeFLine(-60.0, roc, Color.WHITE, fLen, kMax, FieldLine.RUNGE_KUTTA);
			fl.setRKTolerance(1e-5);
			fl.setSArc(fLen*0.2);
			fl.setKMax(2*kMax);
			fmanager.addFieldLine(fl);
			//fLines.add(fl);
			//addElement(fl);
		
			/// bottom ring (roc2) fieldlines
			roc2FL1 = makeFLine(-60.0, roc2, Color.WHITE, fLen, kMax, FieldLine.RUNGE_KUTTA);
			roc2FL1.setRKTolerance(1e-5);
			roc2FL1.setSArc(fLen*0.2);
			roc2FL1.setKMax(2*kMax);
			fmanager.addFieldLine(roc2FL1);
			//fLines.add(roc2FL1);
			//addElement(roc2FL1);
		
			
			fl = makeFLine(60.0, m1, Color.WHITE, fLen, kMax, fMode);
			//fl.setRKTolerance(1e-5);
			//fLines.add(fl);
			//addElement(fl);
			fmanager.addFieldLine(fl);
			fl = makeFLine(200.0, m1, Color.WHITE, fLen, kMax, fMode);
			//fLines.add(fl);
			//addElement(fl);
			fmanager.addFieldLine(fl);
			fmanager.setColorMode(FieldLine.COLOR_VERTEX_FLAT);
			
			//fmanager.setFieldLines(fLines);
			addElement(fmanager);
	
			Helix line = new Helix(new Vector3d(0., 4. * coilHOff, 0.),(HasPosition) m1);
			line.setColor(new Color(200, 200, 200));
			line.setRadius(0.6f*line.getRadius());
			addElement(line);
			

			
			//theEngine.setCheckFrameRate((lod & FRAME_RATE) == FRAME_RATE);
			/*
			 * Tina edit 24.4
			 */
			theScene.setFogEnabled(true);
			theScene.setFogTransformFrontScale(0.);
			theScene.setFogTransformBackScale(0.02);
			setCamera(0);
						
		} catch (Exception ex) {
			TDebug.println(0, ex.getMessage());
			ex.printStackTrace();
		}
	}

//	public void initGUI() {
//		TDebug.println(1, "SimLabApp.initGUI() ");
//		if (true) {
//			setGui(new SimGUI());
//			//setGui(new TDefaultGUI());
//		} else {
//			setGui(new SimGUI());
//		}
//		TDebug.println(1, "GUI = " + mGUI.getClass().getName());
//	}



	public void reset() {
		mSEC.stop();
		m1.setPosition(magPos);
		//mViewer.displayBounds();
		mDLIC.setVisible(false);
		 if(theEngine != null)
		theEngine.requestRefresh();
		setCamera(0);
	}

	protected synchronized void setOpposite(boolean state) {
		if (state) {
			setInvert(true);
			setBoth(true);
			//mFramework.getStatusBar().setText("Both Rings - Opposite Current", false);
		}
	}

	protected synchronized void setSame(boolean state) {
		if (state) {
			setInvert(false);
			setBoth(true);
			//mFramework.getStatusBar().setText("Both Rings - Same Current", false);
		}
	}

	protected synchronized void setTopOnly(boolean state) {
		if (state) {
			setInvert(false);
			setBoth(false);
			//mFramework.getStatusBar().setText("Top Ring Only", false);
		}
	}

	protected synchronized void setBoth(boolean state) {
		if (state != doBoth) {
			TDebug.println(0, "setting doBoth to: " + state);
		    if(!state)
                roc2.setCurrent(0.);
			roc2.setGeneratingB(state);
			roc2FL1.setDrawn(state);
			 if(theEngine != null)
            theEngine.requestRefresh();

		}
			doBoth = state;
	}


	protected void setInvert(boolean state) {
		//TDebug.println(0,"setting inverter to: " + state);
		invertCur = state;
	
		setCurrent(getCurrent());
	}

	public double getCurrent() {
		return curValue;
	}

	public void setCurrent(double d) {
		//TDebug.println(0,"setCurrent: " + d);
		curValue = d;
		double cValue = -curValue * numCoils;
		//double cValue = -curValue ;
		roc.setCurrent(cValue);
		if (useMeter)
			meter.setValue(d);
		//current.setValue(d,false);
		if (doBoth) {
			try {
				if (invertCur) {
					roc2.setCurrent(-cValue);
				} else {
					roc2.setCurrent(cValue);
				}
			} catch (Exception eex) {
				TDebug.printThrown(0, eex);
			}
		}
		 if(theEngine != null)
		theEngine.requestRefresh();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareToIgnoreCase("Reset Camera") == 0) {
			TDebug.println(0,"Setting Camera 0");
			setCamera(0);
			;
		} else if (e.getActionCommand().compareToIgnoreCase("Camera 1") == 0) {
			setCamera(0);
		} else if (e.getActionCommand().compareToIgnoreCase("Camera 2") == 0) {
			setCamera(1);
		} else {
			super.actionPerformed(e);
		}
	}

	public void setNumCoils(double num) {
		numCoils = num;
		 if(theEngine != null)
		theEngine.requestRefresh();
	}



	protected FieldLine makeFLine(double val, PhysicalObject obj, Color col) {
		return makeFLine(val, obj, null, fLen, kMax, fMode);
	}

	protected FieldLine makeFLine(double val, PhysicalObject obj, Color color, double fLen, int kMax, int fMode) {
		Vector3d start = new Vector3d(0, 0, 0);
		Vector3d positive = new Vector3d(1, 0, 0);
		FluxFieldLine fl;
		if (obj == null) {
			fl = new FluxFieldLine(val, start, positive, searchRad);
		} else {
			if (obj instanceof RingOfCurrent) {
				fl = new FluxFieldLine(val, obj, true, true);
			} else if (obj instanceof MagneticDipole) {
				fl = new FluxFieldLine(val, obj, true, false);
				fl.setObjRadius(searchRad);
			} else {
				return null;
			}
		}
		fl.setMinDistance(minD);
		fl.setIntegrationMode(fMode);
		fl.setKMax(kMax);
		fl.setSArc(fLen);
		fl.setSymmetryCount(symCount);
		fl.setColorMode(FieldLine.COLOR_VERTEX);
		fl.setColorScale(fl.getColorScale() * 5.);
		
		//fl.setReceivingFog(true);
		
		return fl;
	}

	protected void makeDummy() {
		SimRendered cd1 = new SimRendered();
		SimRendered cd2 = new SimRendered();
		SimRendered d3 = new SimRendered();
		cd1.setPosition(new Vector3d(0., -coilHOff, 0.));
		cd2.setPosition(new Vector3d(0., coilHOff, 0.));
		TShapeNode cy1 = new ShapeNode();
		TShapeNode cy2 = new ShapeNode();
		TShapeNode cy3 = new ShapeNode();
		GeometryInfo cylg = Cylinder.makeGeometry(12, 0.065, 0.015);
		cy1.setGeometry(cylg);
		cy1.setColor(new Color3f(Color.GREEN));
		cy2.setGeometry(cylg);
		cy2.setColor(new Color3f(Color.BLUE));
		cd1.setNode3D(cy1);
		cd2.setNode3D(cy2);
		GeometryInfo g = Cylinder.makeGeometry(12, 0.005, 0.054);
		cy3.setGeometry(g);
		d3.setColor(Color.GRAY);
		d3.setNode3D(cy3);
		addElement(cd1);
		addElement(cd2);
		addElement(d3);
		double y = -0.04;
		for (int i = 0; i < 9; i++) {
			Line l = new Line(new Vector3d(-0.04, y, 0.), new Vector3d(0.04, y, 0.));
			l.setColor(Color.BLACK);
			y += 0.01;
			addElement(l);
		}
	}
	
	
	public class TellFlux extends EngineRendered implements IsSpatial {

		private static final long serialVersionUID = 3689068430771042099L;

        public TellFlux() {
			super();
		}

		public void needsSpatial() {
			
		}
		public void setPosition(Vector3d pos) {
			super.setPosition(pos);
			if (theEngine != null) {
				//TDebug.println("position: " + getPosition() + "\tFlux: " +
				// theEngine.getBField().getFlux(getPosition()));
			}
		}

		public void nextSpatial() {
			if (theEngine != null) {
				//TDebug.println("position: " + getPosition() + "\tFlux: " +
				// theEngine.getBField().getFlux(getPosition()));
			}
		}
	}
}
