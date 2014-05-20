/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: VisualizationControl.java,v 1.39 2010/09/24 21:00:22 pbailey Exp $ 
 * 
 */

package teal.sim.control;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import teal.field.Potential;
import teal.framework.HasFramework;
import teal.framework.TAbstractFramework;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.sim.engine.SimEngine;
import teal.sim.engine.TEngineControl;
import teal.sim.engine.TSimEngine;
import teal.physics.GField;
import teal.physics.em.BField;
import teal.physics.em.EField;
import teal.physics.em.PField;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.sim.spatial.FieldLineManager;
import teal.ui.ProgressBar;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyInteger;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;

/**
 * @author pbailey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VisualizationControl extends ControlGroup implements HasFramework, ActionListener {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 9163152182205240108L;
    public static final int CHANGE_FL_COLORMODE = 0x01;
    public static final int CHANGE_FL_SYMMETRY = 0x02;
    //public static final int CHANGE_FL_COLORMODE = 0x01;
    //public static final int CHANGE_FL_COLORMODE = 0x01;

    private transient TFramework fWork;
    private FieldConvolution fconvolution;
    private FieldDirectionGrid fieldVis;
    private FieldLineManager manager = null;
    private JCheckBox showFVisCB;
    private JCheckBox showLinesCB;
    private JCheckBox colorModeCB;
    private ProgressBar convoProgress;
    private PropertyInteger fvSlider;
    private PropertyInteger flSlider;
    private int convoFlags;
    private ArrayList<JButton> convoButtons = null;

    private boolean showVectorField = false;
    private boolean showFieldLines = true;
    private int actionFlags = CHANGE_FL_COLORMODE | CHANGE_FL_SYMMETRY;
    private boolean perVertexColor = true;
    
    private volatile int numExternalListeners = 0;
    
    /**
     * these actions are shared among all the clients in client-server mode (wonderland)
     * These should be the button click actions, but e.g. the checkbox indicating whether
     * fieldlines are shown or not may not be shared among clients
     */
    private HashSet<TealAction> sharedActions = new HashSet<TealAction>();;

    /**
     * 
     */
    public VisualizationControl() {
        super();
        setText("Field Visualization");
        showFVisCB = new JCheckBox("Show Vector Field Grid", showVectorField);
        showFVisCB.addActionListener(this);
        fvSlider = new PropertyInteger();
        fvSlider.setMinimum(0);
        fvSlider.setMaximum(25);
        fvSlider.setPaintTicks(true);
        fvSlider.setValue(20);
        fvSlider.setText("Resolution");

        showLinesCB = new JCheckBox("Field Lines", showFieldLines);
        showLinesCB.addActionListener(this);

        flSlider = new PropertyInteger();
        flSlider.setMinimum(1);
        flSlider.setMaximum(80);
        flSlider.setPaintTicks(true);
        flSlider.setValue(40);
        flSlider.setText("Number of Lines");

        colorModeCB = new JCheckBox("Vertex Coloring");
        colorModeCB.addActionListener(this);
        colorModeCB.setSelected(perVertexColor);
        setFVControlsVisible(false);
        setFLControlsVisible(false);
        setDLICControlsVisible(false);
        add(showFVisCB);
        add(fvSlider);
        add(showLinesCB);
        add(flSlider);
        add(colorModeCB);

    }
    /**
     * this adds the functionality to add external listeners to
     * actions that may be shared among all clients in client-server
     * mode (Wonderland)
     * 
     * @param listener a new {@code ActionListener} listening to shared actions
     * 
     */
    public synchronized void addListenerToSharedActions(ActionListener listener){
    	for(TealAction action:sharedActions) {
    		action.addActionListener(listener);
    		if(numExternalListeners == 0)
    			action.removeActionListener(this);
    	}
    	numExternalListeners++;
    }

    /**
     * 
     * @param listener
     * @see VisualizationControl#addListenerToSharedActions(ActionListener)
     */
    public synchronized void removeListenerFromSharedActions(ActionListener listener){
    	
    	for(TealAction action:sharedActions) {
    		action.removeActionListener(listener);    		
    		if(numExternalListeners == 1) // last external listener to be removed
    			action.addActionListener(this);
    	}
    	numExternalListeners--;
    }

    public TFramework getFramework() {
        return fWork;
    }

    /* (non-Javadoc)
     * @see teal.sim.engine.HasSimEngine#setModel(teal.sim.engine.TSimEngine)
     */
    public void setFramework(TFramework eMgr) {
        if ((fWork != null) && (fWork != eMgr) && (fWork instanceof TFramework)) {
        	TFramework tfWork = (TFramework)fWork;
            //remove any existing Elements
            if (fconvolution != null) {
                tfWork.removeTElement(fconvolution);
            }
            if (fieldVis != null) {
                tfWork.removeTElement(fieldVis);
            }
            if (manager != null) {
                tfWork.removeTElement(manager);
            }
        }
//        if(eMgr instanceof TFramework) {
	        fWork = eMgr;
	        if (fWork != null) {
	        	TFramework tfWork = (TFramework)fWork;
	            if (fconvolution != null) {
	                tfWork.addTElement(fconvolution, false);
	            }
	            if (fieldVis != null) {
	                tfWork.addTElement(fieldVis, false);
	            }
	            if (manager != null) {
	                tfWork.addTElement(manager, false);
	            }
	        }
//        }
    }

    public void setActionFlags(int value) {
        actionFlags = value;
        setFLControlsVisible(showLinesCB.isSelected());
    }

    private void setFLControlsVisible(boolean b) {
        showLinesCB.setVisible(b);
        flSlider.setVisible(b && ((actionFlags & CHANGE_FL_SYMMETRY) == CHANGE_FL_SYMMETRY));
        colorModeCB.setVisible(b && ((actionFlags & CHANGE_FL_COLORMODE) == CHANGE_FL_COLORMODE));
    }

    private void setFVControlsVisible(boolean b) {
        showFVisCB.setVisible(b);
        fvSlider.setVisible(b);
    }

    private void setDLICControlsVisible(boolean b) {
        showFVisCB.setVisible(b);
        fvSlider.setVisible(b);
    }

    public void setConvolutionModes(int cFlags) {
        convoFlags = cFlags;
        buildConvoActions();
    }

    public int getConvoFlags() {
        return convoFlags;
    }

    public void setFieldConvolution(FieldConvolution fc) {
        fconvolution = fc;
        buildConvoActions();
        if (fconvolution != null) {
            mElements.add(fc);
            if ((fWork != null) && (fWork instanceof TFramework)) {
                ((TFramework)fWork).addTElement(fconvolution, false);
            }
        }
    }

    public void setColorPerVertex(boolean state) {
        colorModeCB.setSelected(state);
        if (manager != null) {
            manager.setColorMode(state);
        }
    }

    private void buildConvoActions() {
        if (convoButtons != null) {
            if (!convoButtons.isEmpty()) {
                Iterator it = convoButtons.iterator();
                while (it.hasNext()) {
                    JButton b = (JButton) it.next();
                    remove(b);
                }
            }
            convoButtons = null;
        }
        
        if ((convoFlags > 0) && (fconvolution != null)) {
            JButton but = null;
            convoButtons = new ArrayList<JButton>();
            
            if ((convoFlags & DLIC.DLIC_FLAG_E) == DLIC.DLIC_FLAG_E) {
            	TealAction efAction = new TealAction("Electric Field:  Grass Seeds", String.valueOf(DLIC.DLIC_FLAG_E), this);
            	sharedActions.add(efAction);
                but = new JButton(efAction);
                but.setFont(but.getFont().deriveFont(Font.BOLD));
                convoButtons.add(but);
                add(but);
            }
            
            if ((convoFlags & DLIC.DLIC_FLAG_B) == DLIC.DLIC_FLAG_B) {
            	TealAction mfAction = new TealAction("Magnetic Field:  Iron Filings", String.valueOf(DLIC.DLIC_FLAG_B), this);
            	sharedActions.add(mfAction);
                but = new JButton(mfAction);
                but.setFont(but.getFont().deriveFont(Font.BOLD));
                convoButtons.add(but);
                add(but);
            }
            
            if ((convoFlags & DLIC.DLIC_FLAG_G) == DLIC.DLIC_FLAG_G) {
            	TealAction gAction = new TealAction("Gravity", String.valueOf(DLIC.DLIC_FLAG_G), this);
            	sharedActions.add(gAction);
                but = new JButton(gAction);
                convoButtons.add(but);
                add(but);
            }
            
            if ((convoFlags & DLIC.DLIC_FLAG_P) == DLIC.DLIC_FLAG_P) {
            	TealAction pAction = new TealAction("Pauli Forces", String.valueOf(DLIC.DLIC_FLAG_P), this);
            	sharedActions.add(pAction);
                but = new JButton(pAction);
                convoButtons.add(but);
                add(but);
            }
            
            if ((convoFlags & DLIC.DLIC_FLAG_EP) == DLIC.DLIC_FLAG_EP) {
            	TealAction epAction = new TealAction("Electric Potential", String.valueOf(DLIC.DLIC_FLAG_EP), this);
            	sharedActions.add(epAction);
                but = new JButton(epAction);
                but.setFont(but.getFont().deriveFont(Font.BOLD));
                convoButtons.add(but);
                add(but);
            }
            
            if ((convoFlags & DLIC.DLIC_FLAG_BP) == DLIC.DLIC_FLAG_BP) {
            	TealAction mpAction = new TealAction("Magnetic Potential", String.valueOf(DLIC.DLIC_FLAG_BP), this);
            	sharedActions.add(mpAction);
                but = new JButton(mpAction);
                convoButtons.add(but);
                add(but);
            }
            
            if ((convoFlags & DLIC.DLIC_FLAG_EF) == DLIC.DLIC_FLAG_EF) {
            	TealAction efAction = new TealAction("Electic Flux", String.valueOf(DLIC.DLIC_FLAG_EF), this);
            	sharedActions.add(efAction);
                but = new JButton(efAction);
                convoButtons.add(but);
                add(but);
            }
            
            if ((convoFlags & DLIC.DLIC_FLAG_BF) == DLIC.DLIC_FLAG_BF) {
            	TealAction mfAction = new TealAction("Magnetic Flux", String.valueOf(DLIC.DLIC_FLAG_BF), this);
            	sharedActions.add(mfAction);
                but = new JButton(mfAction);
                convoButtons.add(but);
                add(but);
            }
            
            if (convoProgress == null) {
                convoProgress = new ProgressBar();
                fconvolution.addProgressEventListener(convoProgress);
                add(convoProgress);
            }
        }

    }

    public FieldConvolution getFieldConvolution() {
        return fconvolution;
    }

    public void setFieldLineManager(FieldLineManager mgr) {
        if (mgr != null) {
            manager = mgr;
            flSlider.addRoute(manager, "symmetryCount");
            manager.setSymmetryCount(((Integer) flSlider.getValue()).intValue());
            manager.setColorMode(perVertexColor ? FieldLine.COLOR_VERTEX : FieldLine.COLOR_VERTEX_FLAT);
            setFLControlsVisible(true);
            mElements.add(manager);
            if ((fWork != null) && (fWork instanceof TFramework)) {
                ((TFramework)fWork).addTElement(manager, false);
            }
        } else {
            setFLControlsVisible(false);
        }

    }

    public FieldLineManager getFieldLineManager() {
        return manager;
    }

    public void setFieldVisGrid(FieldDirectionGrid mgr) {
        if (mgr != null) {
            fieldVis = mgr;
            fvSlider.addRoute("value", fieldVis, "resolution");
            fieldVis.setResolution(((Integer) fvSlider.getValue()).intValue());
            setFVControlsVisible(true);
            setShowFV(mgr.isDrawn());
            mElements.add(mgr);
            if ((fWork != null) && (fWork instanceof TFramework)) {
                ((TFramework)fWork).addTElement(fieldVis, false);
            }
        } else {
            setFVControlsVisible(false);
        }

    }

    public FieldDirectionGrid getFieldVisGrid() {
        return fieldVis;
    }

    public void setSymmetryCount(int num) {
        flSlider.setValue(num);
    }

    public int getSymmetryCount() {
        return (((Integer) flSlider.getValue()).intValue());
    }

    public void setShowFV(boolean state) {
        if (showFVisCB.isSelected() != state) showFVisCB.setSelected(state);
        setFVEnabled(state);
    }

    protected void setFVEnabled(boolean state) {
        fvSlider.setEnabled(state);
        fieldVis.setDrawn(state);
    }

    public void setShowLines(boolean state) {
        if (showLinesCB.isSelected() != state) showLinesCB.setSelected(state);
        setLinesEnabled(state);

    }

    protected void setLinesEnabled(boolean state) {
        flSlider.setEnabled(state);
        colorModeCB.setEnabled(state);
        manager.setDrawn(state);
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getSource() == colorModeCB) {
            String pn = pce.getPropertyName();
            if (pn.compareTo("value") == 0) {
                boolean state = ((Boolean) pce.getNewValue()).booleanValue();
                manager.setColorMode(state);
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == showLinesCB) {
            setLinesEnabled(showLinesCB.isSelected());

        } else if (evt.getSource() == showFVisCB) {
            setFVEnabled(showFVisCB.isSelected());

        } else if (evt.getSource() == colorModeCB) {
            perVertexColor = colorModeCB.isSelected();
            manager.setColorMode(colorModeCB.isSelected() ? FieldLine.COLOR_VERTEX : FieldLine.COLOR_VERTEX_FLAT);

        }

        else {
            int cmd = Integer.parseInt(evt.getActionCommand());
            if (fconvolution != null) {
            	Cursor cr = null;
            	if(fWork instanceof TFramework) {
	                cr = ((TFramework)fWork).getAppCursor();
	                ((TFramework)fWork).setAppCursor(new Cursor(Cursor.WAIT_CURSOR));
            	}
                Thread.yield();
                TSimEngine model = fconvolution.getSimEngine();
                if (model != null) {
                    TEngineControl smc = model.getEngineControl();
                    if (smc.getSimState() == TEngineControl.RUNNING) {
                        smc.stop();
                        model.refresh();
                        Thread.yield();
                    }

                    switch (cmd) {
                        case DLIC.DLIC_FLAG_E:
//                            fconvolution.setField(((EMEngine)model).getEField());
                        	fconvolution.setField(model.getElementByType(EField.class));
                            fconvolution.generateFieldImage();
                            break;
                        case DLIC.DLIC_FLAG_B:
//                            fconvolution.setField(((EMEngine)model).getBField());
                        	fconvolution.setField(model.getElementByType(BField.class));
                            fconvolution.generateFieldImage();
                            break;
                        case DLIC.DLIC_FLAG_G:
//                            fconvolution.setField(((EMEngine)model).getGField());
                        	fconvolution.setField(model.getElementByType(GField.class));
                            fconvolution.generateFieldImage();
                            break;
                        case DLIC.DLIC_FLAG_P:
//                            fconvolution.setField(((EMEngine)model).getPField());
                        	fconvolution.setField(model.getElementByType(PField.class));
                            fconvolution.generateFieldImage();
                            break;
                        case DLIC.DLIC_FLAG_EP:
//                            fconvolution.setField(new Potential(((EMEngine)model).getEField()));
                            fconvolution.setField(new Potential(model.getElementByType(EField.class)));                            
                            fconvolution.generateFieldImage();
                            break;
                        case DLIC.DLIC_FLAG_BP:
//                            fconvolution.setField(new Potential(((EMEngine)model).getBField()));
                            fconvolution.setField(new Potential(model.getElementByType(BField.class)));
                            fconvolution.generateFieldImage();
                            break;
                        case DLIC.DLIC_FLAG_EF:
//                            fconvolution.setField(((EMEngine)model).getEField());
                            fconvolution.setField(model.getElementByType(EField.class));
                            fconvolution.generateColorMappedFluxImage();
                            break;
                        case DLIC.DLIC_FLAG_BF:
//                            fconvolution.setField(((EMEngine)model).getBField());
                            fconvolution.setField(model.getElementByType(BField.class));
                            fconvolution.generateColorMappedFluxImage();
                            break;
                        default:
                            break;
                    }
                    fconvolution.getImage();
                } else {
                    TDebug.println(0, "DLIC model is null");
                }
            	if(fWork instanceof TFramework) {
            		((TFramework)fWork).setAppCursor(cr);
            	}
            }
        }

    }

}
