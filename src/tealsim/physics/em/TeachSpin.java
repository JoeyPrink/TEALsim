/* $Id: TeachSpin.java,v 1.13 2010/09/22 15:48:11 pbailey Exp $ */
/**
 * A stand-alone simulation of the TeachSpin coil experiment.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.13 $
 */
package tealsim.physics.em;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;

import teal.render.BoundingSphere;
import javax.swing.JCheckBox;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.core.THasPropertyChange;
import teal.field.Field;
import teal.framework.TealAction;
import teal.sim.constraint.SpringConstraint;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.TEngineControl;
import teal.sim.engine.EngineControl;
import teal.sim.function.WaveGenerator;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyCheck;
import teal.ui.control.PropertyDouble;
import teal.ui.control.PropertyInteger;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

public class TeachSpin extends TeachSpinBase {

	private static final long serialVersionUID = 3257284725574875443L;
    //Optional Elements
	WaveGenerator wave = null;
	JCheckBox generate = null;
	boolean isGenerating = false;
	PropertyDouble dt = null;
	PropertyInteger res;
	PropertyDouble current;
	PropertyCheck invert;
	PropertyCheck both;
	PropertyDouble ampslider;
	PropertyDouble hzslider;
	private FieldDirectionGrid fv;
	
	private PropertyCheck colorToggle;
	

	public TeachSpin() {
		this(2, 168., -1, CURRENT | MODEL | GENERATOR);
	}

	public TeachSpin(int maxLines, double coilsPerRing, int debugLevel, int lod) {
		
		super(maxLines, coilsPerRing, debugLevel, lod);
		title = "TEALsim Teach Spin";
		
		
		setGravity(new Vector3d(0, 0, 0));
		setDeltaTime(0.05);
		//setModel(emModel);
		BoundingSphere bs = new BoundingSphere(new Point3d(0., 0, 0.), 0.400);
		theScene.setBoundingArea(bs);
		
		m1.setMass(20.0); //magMass);
		m1.setMoveable(true);
		m1.setConstrained(true);
		m1.setConstraint(new SpringConstraint(new Vector3d(0., 4. * coilHOff, 0.), 4. * coilHOff, 9000.0));
		m1.setDrawn(true);
		
		// Add GUI components
		mSEC.rebuildPanel(EngineControl.DO_STEP | EngineControl.DO_DEFAULT);
		mSEC.setVisible(true);
		
		wave = new WaveGenerator();
		wave.setScale(0.16);
		wave.setHz(.1);
		wave.setStepping(isGenerating);
		wave.addPropertyChangeListener("value", this);
		addElement(wave);
		
//		generate = new PropertyCheck();
//		generate.setText("Generate");
//		generate.setValue(isGenerating);
//		generate.addPropertyChangeListener("value", this);
//		//addElement(generate);
		
		/*
		 * Tina edit
		 */
		generate = new JCheckBox(new TealAction("Generating", "GENERATOR_ON", this));
		generate.setSelected(isGenerating);
		//addElement(generate);
		
		
		
		fv = new FieldDirectionGrid();
		fv.setType(Field.B_FIELD);
		fv.setDrawn(false);
		
		
		invert = new PropertyCheck();
		invert.setText("Opposite Direction");
		invert.setID("Invert Current");
		invert.setValue(invertCur);
		invert.addPropertyChangeListener("value", this);
		
		both = new PropertyCheck();
		both.setText("Top Only");
		both.setID("Both Rings");
		both.setValue(doBoth);
		both.addPropertyChangeListener("value", this);
		
		if (useCurrentSlider) {
			current = new PropertyDouble();
			current.setText("Current");
			current.setMinimum(-0.1);
			current.setMaximum(0.1);
			current.setPaintTicks(true);
			current.addPropertyChangeListener("value", this);
			current.setValue(defaultCurrent);
		}
		
//		addElement(res);
//		addElement(invert);
//		addElement(both);
		
		hzslider = new PropertyDouble();
		hzslider.setText("Frequency");
		hzslider.setMinimum(0.);
		hzslider.setMaximum(5.);
		hzslider.setPaintTicks(true);
		hzslider.addRoute(wave, "hz");
		hzslider.setValue(0.);
		hzslider.setVisible(true);
		
		ampslider = new PropertyDouble();
		ampslider.setText("Amplitude");
		ampslider.setMinimum(0.);
		ampslider.setMaximum(0.1);
		ampslider.setPaintTicks(true);
		ampslider.addRoute(wave, "scale");
		ampslider.setValue(0.);
		ampslider.setVisible(true);
				
		ControlGroup controls = new ControlGroup();
		controls.setText("Parameters");
		if (useCurrentSlider && current != null)
			controls.add(current);
		
		
		controls.add(invert);
		controls.add(both);
		addElement(controls);
		
		ControlGroup siggen = new ControlGroup();
		siggen.setText("Signal Generator");
		siggen.add(generate);
		siggen.add(hzslider);
		siggen.add(ampslider);
		addElement(siggen);
		
		
		VisualizationControl vizPanel = new VisualizationControl();
		vizPanel.setFieldConvolution(mDLIC);
		vizPanel.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
		vizPanel.setFieldLineManager(fmanager);
		vizPanel.setFieldVisGrid(fv);
		vizPanel.setShowFV(false);
		addElement(vizPanel);
		
				
		ControlGroup graphPanel = new ControlGroup();
		graphPanel.setText("Graphs");
//		graphPanel.add(current_graph);
		//taskPane.add(graphPanel);
			
		//mSEC.init();
		if(theEngine != null)
		theEngine.requestRefresh();
		
		setDamping(0.4);
		if(theEngine instanceof THasPropertyChange) {
			((THasPropertyChange)theEngine).addPropertyChangeListener(this);
		}
		// John's initial conditions:
		setCurrent(0.0);
		both.setValue(false);
		ampslider.setValue(0.04);
		hzslider.setValue(0.3);
		invert.setValue(false);
		generate.setSelected(true);
		wave.setStepping(generate.isSelected());
		isGenerating = generate.isSelected();
		current.setEnabled(!generate.isSelected());
		 
		//mViewer.setFogTransformBackScale(0.1);
		//mViewer.setFogTransformFrontScale(0.0);
        TealAction ta = new TealAction("Save",this);
        addAction("File",ta);
		
		mSEC.setSimState(TEngineControl.RUNNING);
	}

	public void reset() {
		m1.setPosition(new Vector3d(0., 0., 0.));
		setCurrent(0.);
		current.setValue(0.);
		current.setEnabled(false);
		generate.setSelected(false);
		//		John's initial conditions:
		both.setValue(false);
		ampslider.setValue(0.04);
		ampslider.setEnabled(true);
		hzslider.setValue(0.3);
		hzslider.setEnabled(true);
		invert.setValue(false);
		generate.setSelected(true);
		super.reset();
		mSEC.setSimState(TEngineControl.RUNNING);
	}
	
	public int getNumLines()
    {
        return symCount;
    }
    
    public synchronized void setNumLines(int num)
    {
        Iterator it = fLines.iterator();
        while (it.hasNext())
        {
            FieldLine f =(FieldLine) it.next();
            f.setSymmetryCount(num);
        }
        if(theEngine != null)
        theEngine.requestRefresh();
        symCount = num;
    }

    protected void setBoth(boolean state) {
		super.setBoth(state);
		both.setValue(!state, false);
	}

    protected void setInvert(boolean state) {
		super.setInvert(state);
		invert.setValue(invertCur, false);
	}
	
	void setColoring(boolean state) {
		Iterator it = fLines.iterator();
		int mode = (state) ? FieldLine.COLOR_VERTEX : FieldLine.COLOR_VERTEX_FLAT;
		while ( it.hasNext()) {
			FieldLine f = (FieldLine)it.next();
			f.setColorMode(mode);
			//f.needsSpatial();
		}
//		System.out.println("setColoring()");
		 if(theEngine != null)
		theEngine.requestRefresh();
		//colorToggle.setValue(!state);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("Reset")) {
			reset();
		} else if (e.getActionCommand().compareToIgnoreCase("GENERATOR_ON") == 0) {
			//if (((JCheckBox)e.getSource()).isSelected() == true) {
				boolean state = ((JCheckBox)e.getSource()).isSelected();
				wave.setStepping(state);
				isGenerating = state;
				current.setEnabled(!state);
				ampslider.setEnabled(state);
				hzslider.setEnabled(state);
				if (!isGenerating)
					setCurrent(0.0);
			//}
		} else if (e.getActionCommand().compareToIgnoreCase("SCREEN_CAP") == 0){
			//((ViewerJ3D)mViewer).setSaveFrame(true);
		} else {
		
			super.actionPerformed(e);
		}
	}

	public void propertyChange(PropertyChangeEvent pce) {
		if (pce == null)
			return;
		try {
			//TDebug.println(-1,"TeachSpinLV.propertyChange: " +
			// pce.getSource() + " -> "+ pce.getPropertyName());
			Object source = pce.getSource();
			if (source == wave) {
				String pn = pce.getPropertyName();
				//TDebug.println(0,"PropertChange: " + pn+ " = " +
				// pce.getNewValue() + " class:"+
				// pce.getNewValue().getClass().getName());
				if (pn.compareTo("value") == 0) {
					//if(generate)
					TDebug.println(1, "generated wave value: " + ((Double) pce.getNewValue()).doubleValue());
					setCurrent(((Double) pce.getNewValue()).doubleValue());
				}
			} else if (source == generate) {
				String pn = pce.getPropertyName();
				//TDebug.println(2,"PropertyChange: " + pn+ " = " +
				// pce.getNewValue() + " class:"+
				// pce.getNewValue().getClass().getName());
				if (pn.compareTo("value") == 0) {
					boolean state = ((Boolean) pce.getNewValue()).booleanValue();
					TDebug.println(0, "setting generate to: " + state);
					wave.setStepping(state);
					isGenerating = state;
					current.setEnabled(!state);
					if (!isGenerating)
						setCurrent(0.);
				}
			} else if (source == current) {
				//TDebug.println(0,"PropertyChange: " + pce.getPropertyName()+
				// " = " + pce.getNewValue() + " class:"+
				// pce.getNewValue().getClass().getName());
				String pn = pce.getPropertyName();
				if (pn.compareTo("value") == 0) {
					double val = ((Double) pce.getNewValue()).doubleValue();
					//TDebug.println(0,"setting current to: " + val);
					setCurrent(val);
				}
			} else if (source == both) {
//				TDebug.println(0,"PropertyChange: " + pce.getPropertyName()+
//				 " = " + pce.getNewValue() + " class:"+
//				 pce.getNewValue().getClass().getName());
				String pn = pce.getPropertyName();
				if (pn.compareTo("value") == 0) {
					boolean state = ((Boolean) pce.getNewValue()).booleanValue();
					if (state == doBoth) {
						setBoth(!state);
					}
				}
			} else if (source == invert) {
				//TDebug.println(0,"PropertyChange: " + pce.getPropertyName()+
				// " = " + pce.getNewValue() + " class:"+
				// pce.getNewValue().getClass().getName());
//				Transform3D trans = new Transform3D();
//				mViewer.getUniverse().getViewingPlatform().getViewPlatformTransform().getTransform(trans);
//				Vector3d t = new Vector3d();
//				trans.get(t);
//				System.out.println("View Transform translation: " + t);
//				double tlen = t.length();
//				mViewer.setFogFrontDistance(tlen);
//				mViewer.setFogBackDistance(tlen+0.2*tlen);
				String pn = pce.getPropertyName();
				if (pn.compareTo("value") == 0) {
					boolean state = ((Boolean) pce.getNewValue()).booleanValue();
					setInvert(state);
					//TDebug.println(0,"setting inverter to: " + state);
					//invertCur = state;
					//setCurrent(getCurrent());
				}
			} else if (source == colorToggle) {
				//TDebug.println(0,"PropertyChange: " + pce.getPropertyName()+
				// " = " + pce.getNewValue() + " class:"+
				// pce.getNewValue().getClass().getName());
				String pn = pce.getPropertyName();
				if (pn.compareTo("value") == 0) {
					boolean state = ((Boolean) pce.getNewValue()).booleanValue();
					//setColoring(state);
					fmanager.setColorMode(state);
				}
			}
			if (source == theEngine && pce.getPropertyName().equalsIgnoreCase("simState")) {
				if (mSEC.getSimState() == EngineControl.DO_RESET) {
					reset();
				}
			} else {
				//TDebug.println(0,"Calling super");
				super.propertyChange(pce);
			}
		} catch (Exception e) {
			TDebug.printThrown(0, e);
		}
	}

	
}