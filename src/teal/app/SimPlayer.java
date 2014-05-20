/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SimPlayer.java,v 1.91 2010/09/22 15:48:09 pbailey Exp $
 * 
 */

package teal.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.RootPaneContainer;
import javax.swing.ToolTipManager;

import teal.browser.Browser;
import teal.core.AbstractElement;
import teal.core.HasID;
import teal.core.TElement;
import teal.framework.HasFramework;
import teal.framework.MenuBar;
import teal.framework.MenuElement;
import teal.framework.TAbstractMenuBar;
import teal.framework.TAbstractFramework;
import teal.framework.TFramework;
import teal.framework.TGui;
import teal.framework.TStatusBar;
import teal.framework.TToolBar;
import teal.framework.TealAction;
import teal.render.scene.SceneFactory;
import teal.render.viewer.AbstractViewer3D;
import teal.sim.engine.SimEngine;
import teal.sim.engine.TSimEngine;
import teal.sim.simulation.SimulationFactory;
import teal.sim.simulation.TSimulation;
import teal.ui.UIPanel;
import teal.ui.swing.LookAndFeelTweaks;
import teal.util.TDebug;

/**
 * This provides a generic TSimulation presentation engine.  It dynamically loads  
 * TSimulations.  It may be a component of another application or included 
 * in an applet.
 *
 * @see SimPlayerApp
 * @see teal.sim.simulation.TSimulation
 * @see teal.sim.simulation.SimWorld
 *
 * @author Philip Bailey
 *
 **/

public class SimPlayer extends UIPanel implements ActionListener, TAbstractFramework {

    private static final long serialVersionUID = 3257004345797915954L;

    /** Action command to trigger a reset */
    public final static String RESET = "RESET";
    /** Action command to trigger a camera reset to a default location */
    public final static String RESET_CAMERA = "Reset Camera";
    /** Action command to trigger a camera reset to a default location */
    public final static String VIEW_STATUS = "View Status";

    protected RootPaneContainer mParent = null;
    protected TGui mGUI = null;

    protected boolean inApplet = true;
//    protected String id = "Sim Player";
    protected String title = "Sim Player";
    protected MenuBar mMenuBar;
    protected TStatusBar mStatusBar;
    protected TToolBar mToolBar;
    protected AbstractViewer3D mViewer;
    protected Hashtable<String,Object> mElements;
    protected TSimulation theSim;
    protected JFrame theWindow;

    protected JFileChooser fc = null;
    protected File curDir = null;

    public SimPlayer() {
        super();
        id = "Sim Player";
        setOpaque(true);
        TDebug.println(1, "SimPlayer Constructor:");
        mElements = new Hashtable<String,Object>();

        buildUI();

        TealAction ta = new TealAction("Exit", this);
        addAction("File", ta);
    }
    
  public void doStatus(int i){}
  public void displayBounds(){}

    public void setParent(RootPaneContainer parent) {
        mParent = parent;
    }

    private void buildUI() {
        
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

        setBorder(LookAndFeelTweaks.PANEL_BORDER);
        setLayout(LookAndFeelTweaks.createBorderLayout());

        mMenuBar = new MenuBar();
        mToolBar = new TToolBar();
        add(BorderLayout.NORTH, mToolBar);

        mStatusBar = new TStatusBar();
        //mStatusBar.addZone("message", new JLabel(" "), "*");
        add("South", mStatusBar);
        
        initGUI();
    }

    private void initGUI() {
        TDebug.println(1, " InitGUI:");
        setGui(new SimGUI());
    }

    public void setGui(TGui gui) {
        TDebug.println(1, " setGUI: " + gui);
        if (mGUI != null) remove(mGUI.getPanel());
        mGUI = gui;
        if (mGUI != null) {
        	
            add(BorderLayout.CENTER, mGUI.getPanel());
//            mGUI.setFramework(this);
        }
        invalidate();
        validate();
        refreshGUI();
    }

    public TGui getGui() {
        return mGUI;
    }

    public TAbstractMenuBar getTMenuBar() {
        return mMenuBar;
    }

    public TToolBar getTToolBar() {
        return mToolBar;
    }
    
    public TStatusBar getStatusBar() {
        return mStatusBar;
    }

    public void setInApplet(boolean state) {
        inApplet = state;
    }

    public boolean isInApplet() {
        return inApplet;
    }

    public void setTitle(String t) {
        firePropertyChange("title", title, t);
        title = t;
    }

    public String getTitle() {
        return title;
    }

    public Cursor getAppCursor() {
        Cursor c = getCursor();
return c;
    }

    public void setAppCursor(Cursor cur) {
        setCursor(cur);
    }

    public JFileChooser getFileChooser() {
        if (fc == null) fc = new JFileChooser();
        return fc;
    }

    public void load() {
        File file = null;
        if (fc == null) fc = new JFileChooser();
        if (curDir != null) fc.setCurrentDirectory(curDir);
        int status = fc.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            curDir = fc.getCurrentDirectory();
            file = fc.getSelectedFile();
            load(file);
        }
    }

    public void load(File file) {
        try {
            load(new FileInputStream(file));
        } catch (FileNotFoundException fnf) {
            TDebug.println(0, "File not found: " + file);
        }
    }

    public void load(URL url) {
        try {
            load(url.openStream());
        } catch (IOException ioe) {
          TDebug.println(ioe.getMessage());
        }
    }

    public void load(InputStream input) {
        TSimulation sim = null;

        try {
            sim = SimulationFactory.loadSimulation(input);
            if (theSim != null) {
                load(sim);
            }
        } catch (Exception fnf) {
            TDebug.printThrown(0, fnf, " Trying to load input");
        }
    }
    
   
    
    public void load(TSimulation sim) {
        if (theSim != null) {
            remove(theSim);
            theSim.dispose();
            theSim = null;
            if(mViewer != null){
            	mViewer.dispose();
            	mViewer = null;
            }
        }
        
        SimEngine eng = new SimEngine(sim.getEngineType());
/*        SimEngine eng = null;
        switch(sim.getEngineType()){
          case TEngine.BIOCHEM_ENGINE:
          case TEngine.KINETIC_ENGINE:
        	eng = new SimEngine();
      	  	break;
          case TEngine.EM_ENGINE:
        	eng = new EMEngine();
        	break;
          default:
        	  break;
        }*/

        mViewer = SceneFactory.makeViewer();
        eng.addRenderEngine(mViewer);
        eng.setSimulation(sim);
        
        theSim = sim;
        theSim.setEngine(eng);
        theSim.setFramework(this);
        setGui(theSim.getGui());
        
        mGUI.addComponent(mViewer);
        
        if (theWindow != null) 
        {
        	theWindow.setSize(this.getGui().getPreferredSize());
        	theWindow.pack();
        }

        setTitle(theSim.getTitle());
        MenuElement[] menu = theSim.getMenuElements();
        for (int i = 0; i < menu.length; i++) {
            addAction(menu[i].getSection(), menu[i].getAction());
        }
        addElements(theSim.getGuiElements());
        theSim.getEngineControl().init();
        theSim.initialize();
        refreshGUI();
        //eng.init();
        
        // Initialize the viewer
        //mViewer.setSimulation(theSim);
        //mViewer.render();
        
    }
    
    public void loadSimClass(String classType)
    throws IllegalArgumentException
    {
    	TSimulation sim = null;
        if (theSim != null) theSim.getEngineControl().stop();
        try{
        	Class<?> simClass = Class.forName(classType);
        	 sim = (TSimulation) simClass.newInstance();
        }
        catch(ClassNotFoundException cnfEx){
        	throw new IllegalArgumentException("Could not Find: " + classType,cnfEx);
        }
        catch(IllegalAccessException iaEx){
        	throw new IllegalArgumentException("Acess Error " + classType +  ": ",iaEx);
        }
        catch(InstantiationException iEx){
        	throw new IllegalArgumentException("Instantiation Error " + classType +  ": ",iEx);
        }
        catch(ClassCastException ccEx){
        	throw new IllegalArgumentException(classType + " cast to TSimulation error: ",ccEx);
        }
        load(sim);
    }

    public void save() {
        if (theSim == null) {
            TDebug.println("Error: the simulation has not been created!");
            return;
        }
        File curDir = null;
        File file = null;

        if (fc == null) fc = new JFileChooser();
        if (curDir != null) fc.setCurrentDirectory(curDir);
        int status = fc.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {

            curDir = fc.getCurrentDirectory();
            try {
                file = fc.getSelectedFile();

                SimulationFactory.saveSimulation(theSim, new FileOutputStream(file));

            }

            catch (IOException fnf) {
                TDebug.printThrown(0, fnf, " Trying to save file: " + file);
            }
        }

    }

    public TElement fetchTElement(String id) {
  TElement elm = (TElement) mElements.get(id);

        return elm;
    }

    public void addElement(Object element) throws IllegalArgumentException {
        addElement(element, true);
    }

    public void addElement(Object element, boolean addToList) throws IllegalArgumentException {
        TDebug.println(1, "SimPlayer addElement: " + element);
        if (element instanceof HasID) {
            addTElement((HasID) element, addToList);
        } else if (element instanceof Action) {
            addAction((Action) element);
        } else if (element instanceof Component) {
            addComponent((Component) element);
        } else {
            throw new IllegalArgumentException("Error: element type of object " + element + " is not supported");
        }
    }

    public void removeElements() {
        Collection<Object> elements = new ArrayList<Object>(mElements.values());
        removeElements(elements);
    }

    public void removeElements(Collection<?> elements) {
        Iterator<?> it = elements.iterator();
        while (it.hasNext()) {
            removeElement(it.next());
        }
    }

    public void removeElement(Object element) {
        if (element instanceof HasID) {
            removeTElement((HasID) element);
        } else if (element instanceof Action) {
            removeAction((Action) element);
        } else if (element instanceof Component) {
            removeComponent((Component) element);
        }

    }

    public void addElements(Collection<?> elements) throws IllegalArgumentException {
        Iterator<?> it = elements.iterator();
        while (it.hasNext()) {
            addElement(it.next(), true);
        }
    }

    //FI XXX ME: THIS IS VERY WEIRD!! if elem is a component it is added TWICE!!
    public void addAction(Action elm) {
        TDebug.println(1, "SimPlayer addAction: " + elm);
        if (elm instanceof Component) {
            mGUI.addComponent((Component) elm);
        }
        if (elm instanceof Action) {
            //  addAction((Action) elm);
        }
    }

    public void removeAction(Action a) {
    }

    public void addAction(String target, Action ac) {

        TDebug.println(1, "AddAction: " + target + " :  " + ac);
        if (mMenuBar != null) mMenuBar.addAction(target, ac);
    }

    public void removeAction(String target, Action ac) {

        if (mMenuBar != null) mMenuBar.removeAction(target, ac);
    }

    public void removeAction(MenuElement me) {
        removeAction(me.getSection(), me.getAction());
    }

    public void addTElement(HasID elm) {
        addTElement(elm, true);
    }

    public void addTElement(HasID elm, boolean addToList) {
    	AbstractElement.checkID(elm);
        TDebug.println(1, "SimPlayer addTElement: " + elm);
        if (addToList) mElements.put(elm.getID(), elm);
        if (elm instanceof HasFramework) {
            ((HasFramework) elm).setFramework(this);
        }
        if (elm instanceof Component) {
            mGUI.addComponent((Component) elm);
        }

        if (elm instanceof Action) {
            addAction((Action) elm);
        }
    }

    public void removeTElement(HasID elm) {
        if (elm instanceof Component) {
            mGUI.removeComponent((Component) elm);
        }
        if (elm instanceof Action) {
            removeAction((Action) elm);
        }
    }

    public void addComponent(Component elm) {
        mGUI.addComponent(elm);
    }

    public void removeComponent(Component elm) {
        mGUI.removeComponent(elm);
    }

    public String toString() {
        String returnString = (id);
        return returnString;
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        String temp = this.id;
        this.id = new String(id);
        firePropertyChange("ID", temp, id);
    }

    public Object getProperty(String name)
    //throws NoSuchMethodException
    {
        TDebug.println(2, " In getProperty() " + getID() + ": " + name);
        Object obj = null;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name, this.getClass());
            Method theMethod = pd.getReadMethod();
            if (theMethod != null) {
                obj = theMethod.invoke(this, null);
            } else {
                TDebug.println(2, this + ": Getter method for " + name + " not found");
            }
        } catch (IntrospectionException ie) {
            TDebug.println(2, this + " IntrospectionEx: " + ie.getMessage());
        } catch (InvocationTargetException cnfe) {
            TDebug.println(2, cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.println(2, ille.getMessage());
        }
        return obj;
    }

    public boolean setProperty(String name, Object prop)
    {
        TDebug.println(2, " In setProperty() " + getID() + ": " + name + " = " + prop.toString());
        boolean status = false;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name, this.getClass());
            Method theMethod = pd.getWriteMethod();
            if (theMethod != null) {
//                Object param[] = { prop };
                theMethod.invoke(this, prop);
                status = true;
            } else {
                TDebug.println(2, this + ": Setter method for " + name + " not found");
            }
        } catch (IntrospectionException ie) {
        TDebug.println(2, this + " IntrospectionEx: " + ie.getMessage());
        } catch (InvocationTargetException cnfe) {
            TDebug.println(2, cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.println(2, ille.getMessage());
        }
        return status;
    }
    
    public TSimulation getTSimulation(){
    	return theSim;
    }
    
    public void setTSimulation(TSimulation simulation){
    	load(simulation);
    }

    public Browser openBrowser(String str) {
        Browser browser = new Browser(this);
        browser.displayURL(str);
        browser.setSize(550, 400);
        browser.setVisible(true);

        return browser;
    }

    public void displayMessage(String text,boolean expires){
    	mStatusBar.setText("Switch Error: Same & topOnly are set!", false);
    }
    /** the following methods wrap access to the PropertyChangeSupport member */
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce != null) {
            TDebug.println(3, getID() + ": in propertyChange trying to set " + pce.getPropertyName());
            setProperty(pce.getPropertyName(), pce.getNewValue());
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
    	String command = e.getActionCommand();
        if (e.getActionCommand().compareToIgnoreCase("Exit") == 0) {
            dispose();
            System.exit(0);
        }
        else if (command.compareToIgnoreCase("New") == 0) {
            TDebug.println(1, "New");
        } else if (command.compareToIgnoreCase("Load") == 0) {
            TDebug.println(1, "Load");
            load();
        } else if (command.compareToIgnoreCase("Save") == 0) {
            TDebug.println(1, "save");
            save();
        } else if (command.compareToIgnoreCase("Remove") == 0) {
            TDebug.println(1, "remove");
            if (theSim != null) {
                removeSim();
            }

        } 
        } catch (Exception ex) {
        	TDebug.printThrown(0, ex);
        }
    }

    public void removeSim() {
        if (theSim != null) {
            remove(theSim);
            theSim.dispose();
            theSim = null;
        }
        if(mViewer != null){
        	mViewer.dispose();
        	mViewer = null;
        }
        refreshGUI();
    }

    public void refreshGUI() {
       revalidate();
       if (mParent != null) ((JComponent) mParent).repaint();
    }

    public synchronized void dispose() {
        removeSim();
    }

    /** Removes all Simulation specific menuItems and actions from the framework, 
     this does not dispose of the simulation or remove the framework reference. 
     */

    protected void remove(TSimulation sim) {
        if (sim != null) {

            MenuElement[] me = theSim.getMenuElements();
            for (int i = 0; i < me.length; i++) {
                removeAction(me[i].getSection(), me[i].getAction());
            }
            removeElements(theSim.getGuiElements());
            sim.setFramework(null);

        } else {
            TDebug.println("remove(TSimulation) sim is null");
        }
    }

   
    
    public HasID getTElementByID(String id) {
    	return (HasID)mElements.get(id);
    }

	/**
	 * @return Returns the theWindow.
	 */
	public JFrame getTheWindow() {
		return theWindow;
	}
	/**
	 * 
	 * @param theWindow The theWindow to set.
	 */
	public void setTheWindow(JFrame theWindow) {
		this.theWindow = theWindow;
		theWindow.setSize(this.getGui().getPreferredSize());
	}
	
}
