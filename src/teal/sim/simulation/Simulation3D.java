/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Simulation3D.java,v 1.31 2010/09/22 15:48:10 pbailey Exp $ 
 * 
 */
package teal.sim.simulation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.media.j3d.Transform3D;
import javax.swing.Action;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.app.SimGUI;
import teal.core.AbstractElement;
import teal.core.HasElementManager;
import teal.core.HasID;
import teal.core.TElement;
import teal.framework.HasFramework;
import teal.framework.MenuElement;
import teal.framework.TAbstractFramework;
import teal.framework.TFramework;
import teal.framework.TGui;
import teal.framework.TealAction;
import teal.render.BoundingSphere;
import teal.render.Bounds;
import teal.render.TAbstractRendered;
import teal.render.TDrawable;
import teal.render.TRenderEngine;
import teal.render.TRenderListener;
//import teal.render.j3d.ViewerJ3D;
//import teal.render.j3d.WonderlandStuff;
//import teal.render.j3d.WonderlandViewer;
import teal.render.scene.Scene;
import teal.render.viewer.SelectEvent;
import teal.render.viewer.SelectListener;
import teal.render.viewer.SelectManager;
import teal.render.viewer.ViewerSupport;
import teal.render.viewer.TViewer;
//import teal.render.viewer.TViewer3D;
import teal.sim.TSimElement;
import teal.sim.engine.TEngineControl;
import teal.sim.engine.EngineControl;
import teal.sim.engine.TSimEngine.EngineType;
import teal.ui.control.ControlGroup;
import teal.util.TDebug;

/**
 * The abstract class Simulation3D implements TSimulation and provides management for a simulation 
 * which includes a simulation engine specified in a derived class.
 * All elements related to a specific simulation are managed by the Sim 
 * these could include Actions, simulated objects and controls. 
 * As envisioned a simulation will support 
 * an XML loader/dumper interface and should be able to be basic unit 
 * assigned to a TFramework which supports Simulations.
 * 
 * It may be extended into a simulation with a default constructor and specified Engine.
 * @see teal.sim.simulation.SimWorld
 * @see teal.physics.em.SimEM

 *
 * @version $Revision: 1.31 $
 * @author Phil Bailey
 */
public abstract class Simulation3D extends AbstractElement implements TSimulation {

  private static final long serialVersionUID = 3258126942908789043L;
  protected Bounds boundingArea;
  protected double deltaTime = 0.1;
  // Framework support
  protected String title;
  protected LinkedHashMap<String, HasID> mElements;
//    protected Hashtable<String,HasID> mElements;
  protected ArrayList<Component> guiElements = new ArrayList<Component>();
  ;
    protected ArrayList<MenuElement> menuElements;
  //XXX: this is never used!!
  protected ArrayList<Action> actions;
  protected TFramework mFramework;
  //XXX: WHY THAT???? it's an exact copy of menuElements!!
  protected MenuElement[] meTemplate;
  //XXX this is never used
  protected ArrayList<Object> dontDraw;
  protected ArrayList<TRenderEngine> renderEngines;
  protected ArrayList<TRenderListener> renderListeners;
  protected ArrayList<TAbstractRendered> drawObjects;
  protected Scene theScene;
  protected ViewerSupport mView;
  protected SelectManager mSelect;
  protected EngineType mEngineType;
  // View support     
  protected Dimension viewerSize;
  protected Dimension minimumSize;
  protected Color backgroundColor;
  protected transient Transform3D defaultVpTransform;
  protected Vector3d mouseMoveScale;
  protected int navigationMode;
  protected boolean cursorOnDrag = false;
  protected boolean refreshOnDrag = false;
  protected boolean showGizmos = false;
  protected TGui theGUI;
  //protected TSimEngine theEngine;
  protected EngineControl mSEC;

  public Simulation3D() {
    super();

    boundingArea = new BoundingSphere(new Point3d(0, 0, 0), 1);
    mElements = new LinkedHashMap<String, HasID>();
    guiElements = new ArrayList<Component>();
    meTemplate = new MenuElement[0];
    menuElements = new ArrayList<MenuElement>();
    actions = new ArrayList<Action>();
    drawObjects = new ArrayList<TAbstractRendered>();
    renderListeners = new ArrayList<TRenderListener>();
    renderEngines = new ArrayList<TRenderEngine>();
    mFramework = null;
    theScene = new Scene();
    setRenderOrder(new SimDrawOrder());
    backgroundColor = teal.config.Teal.Background3DColor;
    mouseMoveScale = new Vector3d(1., 1., 1.);
    viewerSize = new Dimension(450, 450);
    minimumSize = new Dimension(50, 50);
    navigationMode = TViewer.ORBIT_ALL;

    EngineControl sec = new EngineControl(EngineControl.DO_ALL);
    sec.setID("SMC");
    sec.setBounds(45, 473, 400, 35);
    sec.setVisible(true);
    setEngineControl(sec);

    theGUI = new SimGUI();

//        TealAction ta = new TealAction(TViewer.RESET_CAMERA, this);
//        addAction("View", ta);

  }

  public void initialize() {
  }

  public Collection<TSimElement> getSimElements() {
    ArrayList<TSimElement> sElements = new ArrayList<TSimElement>();
    Iterator<HasID> it = mElements.values().iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      if (obj instanceof ControlGroup) {
        Iterator it2 = ((ControlGroup) obj).getElements().iterator();
        while (it2.hasNext()) {
          Object obj2 = it2.next();
          if (obj2 instanceof TSimElement) {
            sElements.add((TSimElement) obj2);
          }
        }
      }
      else if (obj instanceof TSimElement) {
        sElements.add((TSimElement) obj);
      }
    }
    return sElements;
  }

//    public Collection<TAbstractRendered> getRenderedElementsx()
//    {
//    	ArrayList<TAbstractRendered> elements = new ArrayList<TAbstractRendered>();
//    	Iterator<HasID> it = mElements.values().iterator();
//    	while(it.hasNext()){
//    		Object obj = it.next();
//    		if(obj instanceof ControlGroup){
//    			Iterator it2 = ((ControlGroup)obj).getElements().iterator();
//    			while(it2.hasNext()){
//    				Object obj2 = it2.next();
//    				if(obj2 instanceof TAbstractRendered){
//    					elements.add((TAbstractRendered)obj2);
//    				}
//    			}
//    		}
//    		else if(obj instanceof TAbstractRendered){
//    			elements.add((TAbstractRendered)obj);
//    		}
//    	}
//    	return elements;
//    }
  public Bounds getBoundingArea() {
    return boundingArea;
  }

  public void setBoundingArea(Bounds bounds) {
    PropertyChangeEvent pce = new PropertyChangeEvent(this, "boundingArea",
            boundingArea, bounds);
    boundingArea = bounds;
    firePropertyChange(pce);
  }

  public double getDeltaTime() {
    return deltaTime;
  }

  public void setDeltaTime(double delta) {
    PropertyChangeEvent pce = new PropertyChangeEvent(this, "deltaTime",
            deltaTime, delta);
    deltaTime = delta;
    firePropertyChange(pce);
  }

  public Color getBackgroundColor() {
    return backgroundColor;

  }

  public void setBackgroundColor(Color color) {
    PropertyChangeEvent pce = new PropertyChangeEvent(this, "backgroundColor",
            backgroundColor, color);
    backgroundColor = color;
    firePropertyChange(pce);
  }

  public EngineType getEngineType() {
    return mEngineType;
  }

  public Vector3d getMouseMoveScale() {
    return mouseMoveScale;
  }

  public void setMouseMoveScale(Vector3d vec) {
    mouseMoveScale.set(vec);
  }

  public void setMouseMoveScale(double x, double y, double z) {
    setMouseMoveScale(new Vector3d(x, y, z));
  }

  public Iterator<TAbstractRendered> getRendered() {
    return drawObjects.iterator();
  }

  public Collection<TAbstractRendered> getRenderedElements() {
    return drawObjects;
  }

  public void setRenderOrder(SimDrawOrder order) {
  }

  public Dimension getViewerSize() {
    return viewerSize;
  }

  public void setViewerSize(Dimension dim) {
    viewerSize = dim;
  }

  public void setViewerSize(int width, int height) {
    setViewerSize(new Dimension(width, height));
  }

  public Dimension getViewerMinimumSize() {
    return minimumSize;
  }

  public void setViewerMinimumSize(Dimension dim) {
    minimumSize = dim;
  }

  public int getNavigationMode() {
    return navigationMode;
  }

  public void setNavigationMode(int mode) {
    navigationMode = mode;
  }

  public Boolean getShowGizmos() {
    return showGizmos;
  }

  public void setShowGizmos(Boolean state) {
    showGizmos = state;
  }

  public Boolean getCursorOnDrag() {
    return cursorOnDrag;
  }

  public void setCursorOnDrag(Boolean state) {
    cursorOnDrag = state;
  }

  public Boolean getRefreshOnDrag() {
    return refreshOnDrag;
  }

  public void setRefreshOnDrag(Boolean state) {
    refreshOnDrag = state;
  }

  public Transform3D getDefaultViewpoint() {
    return defaultVpTransform;
  }

  public void setDefaultViewpoint(Transform3D trans) {
    PropertyChangeEvent pce = new PropertyChangeEvent(this, "defaultViewpoint",
            defaultVpTransform, trans);
    defaultVpTransform = trans;
    firePropertyChange(pce);
  }

  public void setLookAt(Point3d from, Point3d to, Vector3d angle) {
    Transform3D transform = new Transform3D();
    transform.lookAt(from, to, angle);
    transform.invert();
    if (renderEngines != null) {
      Iterator<TRenderEngine> it = renderEngines.iterator();
      while (it.hasNext()) {
        it.next().setLookAt(from, to, angle);
      }
    }
    setDefaultViewpoint(transform);

  }

  /**
   *  This should query the active viewer via the framework and get the current viewpoint transform
   */
  public Transform3D getViewpoint() {
    return defaultVpTransform;
  }

  /**
   * This should send an event to the framework to set the current viewport.
   */
  public void setViewpoint(Transform3D trans) {
  }

  public void setEngineControl(TEngineControl modelCtr) {

    if (mSEC != null) {
      //remove old mSEC
      removeElement(mSEC);
    }

    if (modelCtr == null) {
      mSEC = null;
      return;
    }

    mSEC = (EngineControl) modelCtr;
    if (getEngine() != null) {
      mSEC.setSimEngine(getEngine());
    }
    if (mSEC instanceof EngineControl) {
      ((EngineControl) mSEC).addResetActionListener(this);
    }
    addElement(mSEC, false);
  }

  public TEngineControl getEngineControl() {
    return mSEC;
  }
//    public Vector3d getMouseMoveScale(){
//    	return mouseMoveScale;
//    }
//    public void setMouseMoveScale(Vector3d vec){
//    	mouseMoveScale.set(vec);
//    }
//    public void setMouseMoveScale(double x, double y, double z){
//    	setMouseMoveScale(new Vector3d(x,y,z));
//    }

  public SelectManager getSelectManager() {
    return mSelect;
  }

  public void setSelectManager(SelectManager sManager) {
    mSelect = sManager;
    //if(mViewer != null)
    //	mViewer.setSelectManager(mSelect);
  }

  public void setScene(Scene scene) {
    theScene = scene;
//        if(getEngine() != null)
//        	getEngine().setScene(mViewer);

    //if(mSelect != null)
    //	mViewer.setSelectManager(mSelect);
    addElement(theScene, false);
    //loadViewer();
  }

  public Scene getScene() {
    return theScene;
  }

  public TFramework getFramework() {
    return mFramework;
  }

  public void setFramework(TFramework fw) {
    mFramework = fw;
    for (HasID obj : mElements.values()) {
      if (obj instanceof ControlGroup) {
        for (Object obj2 : ((ControlGroup) obj).getElements()) {
          if (obj2 instanceof HasElementManager) {
            if (obj2 instanceof HasFramework) {
              ((HasFramework) obj2).setFramework(fw);
            }
            else {
              if (fw instanceof TFramework) {
                ((HasElementManager) obj2).setElementManager((TFramework) fw);
              }
            }
          }
        }
      }
      else if (obj instanceof HasElementManager) {
        if (obj instanceof HasFramework) {
          ((HasFramework) obj).setFramework(fw);
        }
        else {
          if (fw instanceof TFramework) {
            ((HasElementManager) obj).setElementManager((TFramework) fw);
          }
        }
      }
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String t) {
    title = t;
    if (mFramework != null && mFramework instanceof TAbstractFramework) {
      ((TAbstractFramework) mFramework).setTitle(t);
    }
  }

  public void addElement(Object element) throws IllegalArgumentException {

    addElement(element, true);
  }

  public void addElement(Object element, boolean addToList) throws IllegalArgumentException {
    TDebug.println(1, "Simulation3D addElement: " + element);
    if (element instanceof HasID) {
      addTElement((HasID) element, addToList);
    }
    else if (element instanceof Action) {
      addAction((Action) element);
    }
    else if (element instanceof Component) {
      addComponent((Component) element);
    }
    else {
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
    }
    else if (element instanceof Action) {
      removeAction((Action) element);
    }
    else if (element instanceof Component) {
      removeComponent((Component) element);
    }
  }

  public void addElements(Collection< ?> elements) throws IllegalArgumentException {
    Iterator<?> it = elements.iterator();
    while (it.hasNext()) {
      addElement(it.next(), true);
    }
  }

  public void addAction(Action elm) {
    TDebug.println(1, "Simulation3D addAction: " + elm);
    if (elm instanceof Component) {
      addComponent((Component) elm);
    }
    if (elm instanceof Action) {
      addAction((Action) elm);
    }
  }

  public void removeAction(Action a) {
  }

  public void addAction(String target, Action ac) {
    TDebug.println(1, "Simulation3D addMenuAction: " + ac);
    menuElements.add(new MenuElement(target, ac));
    if (mFramework != null) {
      if (mFramework instanceof TFramework) {
        ((TFramework) mFramework).addAction(target, ac);
      }
    }
  }

  public void removeAction(String target, Action ac) {
    menuElements.remove(new MenuElement(target, ac));
    if (mFramework != null) {
      if (mFramework instanceof TFramework) {
        ((TFramework) mFramework).removeAction(target, ac);
      }
    }
  }

  public MenuElement[] getMenuElements() {
    return (MenuElement[]) menuElements.toArray(meTemplate);
  }

  public void addTElement(HasID elm) {
    addTElement(elm, true);
  }

  public void addTElement(HasID elm, boolean addToList) {
    AbstractElement.checkID(elm);
    TDebug.println(1, "Simulation3D addTElement: " + elm);
    if (addToList) {
      mElements.put(elm.getID(), elm);
    }
    if (elm instanceof TSimElement) {
      if (getEngine() != null) {
        TDebug.println(2, elm + " added to SimEngine!");
        addSimElement((TSimElement) elm);

      }
      else {
        TDebug.println(2, elm + " was not added to SimEngine!");
      }
    }
    if (elm instanceof ControlGroup) {
      Collection<Object> innerElements = ((ControlGroup) elm).getElements();
      Iterator<Object> it = innerElements.iterator();
      while (it.hasNext()) {
        Object obj = it.next();
        TDebug.println(2, "ControlGroup element = " + obj);
        // Recursive call this should not be totally recursive as the obj is contained

        if (mFramework != null) {
          if (obj instanceof HasElementManager) {
            if (obj instanceof HasFramework) {
              ((HasFramework) obj).setFramework(mFramework);
            }
            else {
              if (mFramework instanceof TFramework) {
                ((HasElementManager) obj).setElementManager((TFramework) mFramework);
              }
            }
          }
        }
        if (obj instanceof TSimElement) {
          if (getEngine() != null) {
            TDebug.println(2, obj + ": TSimObject  added to SimEngine!");
            addSimElement((TSimElement) obj);
          }
          else {
            TDebug.println(2, obj + " was not added to SimEngine!");
          }
        }
        if (obj instanceof TAbstractRendered) {
          addDrawable((TAbstractRendered) obj);
        }
      }
    }
    if (elm instanceof TAbstractRendered) {
      addDrawable((TAbstractRendered) elm);
    }

    if (elm instanceof Action) {
      addAction((Action) elm);
    }
    if (elm instanceof Component) {
      addComponent((Component) elm);
    }
    if (mFramework != null) {
      if (elm instanceof HasElementManager) {

        if (elm instanceof HasFramework) {
          ((HasFramework) elm).setFramework(mFramework);
        }
        else {
          if (mFramework instanceof TFramework) {
            ((HasElementManager) elm).setElementManager((TFramework) mFramework);
          }
        }
      }
    }

  }

  public void removeTElement(HasID elm) {
    if (elm instanceof TSimElement) {
      removeSimElement((TSimElement) elm);
    }
    if (elm instanceof TAbstractRendered) {
      if (((TAbstractRendered) elm).isSelected()) {
        mSelect.removeSelected((TAbstractRendered) elm);
      }
      removeDrawable((TAbstractRendered) elm);
    }
    if (elm instanceof ControlGroup) {
      Collection<Object> innerElements = ((ControlGroup) elm).getElements();
      Iterator<Object> it = innerElements.iterator();
      while (it.hasNext()) {
        Object obj = it.next();
        TDebug.println(0, "ControlGroup element = " + obj);
        // Recursive call this should not be toally recursive as the obj is contained

        if (mFramework != null) {
          if (obj instanceof HasElementManager) {
            if (obj instanceof HasFramework) {
              ((HasFramework) obj).setFramework(null);
            }
            else {
              ((HasElementManager) obj).setElementManager(null);
            }
          }
        }
        if (obj instanceof TSimElement) {
          if (getEngine() != null) {
            TDebug.println(2, obj + ": TSimObject  removing SimEngine!");
            removeSimElement((TSimElement) obj);
          }
          else {
            TDebug.println(2, obj + " was not removed no SimEngine!");
          }
        }
        if (obj instanceof TAbstractRendered) {
          removeDrawable((TAbstractRendered) obj);
        }
      }
    }
    if (elm instanceof Action) {
      removeAction((Action) elm);
    }
    if (elm instanceof Component) {
      removeComponent((Component) elm);
    }
    mElements.remove(elm);

  }

  private void addComponent(Component elm) {
    TDebug.println(1, "Simulation3D addComponent: " + elm);
    if (guiElements == null) {
      guiElements = new ArrayList<Component>();
    }
    guiElements.add(elm);
  }

  private void removeComponent(Component elm) {
    if (guiElements != null) {
      guiElements.remove(elm);
    }
    if (mFramework != null) {
      if (mFramework instanceof TFramework) {
        ((TFramework) mFramework).removeComponent(elm);
      }
    }
  }

  public void addTElements(Collection<TElement> elms) {
    Iterator<TElement> it = elms.iterator();
    while (it.hasNext()) {
      addTElement((TElement) it.next());
    }
  }

  // Test
//    public void addTElementsBulk(Hashtable elms) {
//    	Hashtable elements = new Hashtable(mElements.size() + elms.size());
//    	elements.putAll(mElements);
//    	elements.putAll(elms);
//    	mElements = elements;
//    }
  public void removeTelementsBulk(Hashtable elms) {
    Iterator<?> it = elms.values().iterator();
    while (it.hasNext()) {
      //System.out.println("SimWorld::removeTelementsBulk() element = " + it.next().getClass().getName());
      String key = ((TElement) it.next()).getID();
      if (mElements.containsKey(key)) {
        mElements.remove(key);
      }
    }
  }

  /**
   * Adds an TDrawable Object to the simulation.
   *
   * @param draw
   */
  private void addDrawable(TAbstractRendered draw) {
    synchronized (drawObjects) {
      drawObjects.add(draw);
      if (renderEngines != null) {
        Iterator<TRenderEngine> it = renderEngines.iterator();
        while (it.hasNext()) {
          it.next().addDrawable(draw);
        }
      }
    }
  }

  /** Removes a TDrawable object.
   *
   * @param draw
   */
  public void removeDrawable(TAbstractRendered draw) {
    drawObjects.remove(draw);
    if (renderEngines != null) {
      Iterator<TRenderEngine> it = renderEngines.iterator();
      while (it.hasNext()) {
        it.next().removeDrawable(draw);
      }
    }
  }

  /**
   * This returns all elements in the simulation including Viewers, actions, GUIelements, models and controls.
   * It will be used to construct the XML that specifies the simulation.
   * @see teal.sim.simulation.SimulationFactory
   **/
  public Collection<HasID> getElements() {
    return mElements.values();
  }

  public Collection<Component> getGuiElements() {
    return guiElements;
  }

  public Collection<Action> getActions() {
    return actions;
  }

  public void renderComplete(TRenderEngine rEngine) {
    if (renderListeners != null) {
      Iterator<TRenderListener> ri = renderListeners.iterator();
      while (ri.hasNext()) {
        ri.next().renderComplete(rEngine);
      }
    }
  }

  public void addRenderEngine(TRenderEngine renderEngine) {
    if (renderEngines == null) {
      renderEngines = new ArrayList<TRenderEngine>();
    }
    renderEngines.add(renderEngine);
    if (getEngine() != null) {
      getEngine().addRenderEngine(renderEngine);
    }

    loadRenderEngine(renderEngine);
  }

  public void removeRenderEngine(TRenderEngine renderEngine) {
    if (renderEngines != null) {
      renderEngines.remove(renderEngine);
      if (getEngine() != null) {
        getEngine().removeRenderEngine(renderEngine);
      }
    }
  }

  public synchronized void clearRenderLEngines() {
    if (renderEngines != null) {
      ArrayList<TRenderEngine> engines = new ArrayList<TRenderEngine>(renderEngines);
      Iterator<TRenderEngine> it = engines.iterator();
      while (it.hasNext()) {
        renderEngines.remove(it.next());
      }
    }
  }

  public void addRenderListener(TRenderListener rlistener) {
    if (renderListeners == null) {
      renderListeners = new ArrayList<TRenderListener>();
    }
    renderListeners.add(rlistener);
  }

  public void removeRenderListener(TRenderListener rlistener) {
    if (renderListeners != null) {
      renderListeners.remove(rlistener);
    }
  }

  public Iterator<TRenderListener> getRenderListeners() {
    return renderListeners.iterator();
  }

  public synchronized void clearRenderListeners() {
    if (renderListeners != null) {
      ArrayList<TRenderListener> listeners = new ArrayList<TRenderListener>(renderListeners);
      Iterator<TRenderListener> it = listeners.iterator();
      while (it.hasNext()) {
        renderListeners.remove(it.next());
      }
    }
  }

  public void addSelectListener(SelectListener listener) {
    mSelect.addSelectListener(listener);
  }

  public void removeSelectListener(SelectListener listener) {
    mSelect.removeSelectListener(listener);
  }

  public void addSelected(TAbstractRendered obj, boolean clear) {
    mSelect.addSelected(obj, clear);
  }

  public void removeSelected(TAbstractRendered obj) {
    mSelect.removeSelected(obj);
  }

  public void clearSelected() {

    mSelect.clearSelected();

  }

  public int getNumberSelected() {
    return mSelect.getNumberSelected();
  }

  public Collection<TAbstractRendered> getSelected() {
    return mSelect.getSelected();
  }

  public void reset() {
    if (mSEC != null) {
      mSEC.stop();
    }
  }

  public void resetCamera() {
    TDebug.println(2, "Sim: resetCamera");
    if (mFramework != null) {
      mFramework.displayBounds();
    }
  }

  public void dispose() {
    mFramework = null;
    drawObjects.clear();
  }

  public void actionPerformed(ActionEvent evt) {
    String command = evt.getActionCommand();
    if (command.compareToIgnoreCase("reset") == 0) {
      TDebug.println(1, "Reset called");
      reset();
    }
    else if (command.compareToIgnoreCase(TViewer.RESET_CAMERA) == 0) {
      TDebug.println(1, "Reset Camera called");
      resetCamera();
    }
    else if (command.compareToIgnoreCase("VIEW STATUS") == 0) {
      TDebug.println(1, "view Status");
      if (mFramework != null) {
        mFramework.doStatus(0);
      }
      // Load & save stubs
    }
    else {
      if (mFramework != null) {
        if (mFramework instanceof TFramework) {
          ((TFramework) mFramework).actionPerformed(evt);
        }
      }
    }
  }

  public TEngineControl getSimModelControl() {
    return mSEC;
  }

  public TGui getGui() {

    return theGUI;
  }

  public void setGui(TGui g) {
    theGUI = g;
  }

  /* (non-Javadoc)
   * @see teal.core.TElementManager#getTElementByID(java.lang.String)
   */
  public HasID getTElementByID(String id) {
    if (mElements.containsKey(id)) {
      return (HasID) mElements.get(id);
    }
    return null;
  }

  // setProperty method for command line arguments
  // May want to throw some more specific exceptions here
  public void setProperty(String telement, String property, String value) {
    try {
      // get the Class representation of a String
      Class<?> classArray[] = {Class.forName("java.lang.String")};

      // "sim" is a reserved keyword to represent the simulation itself
      if (telement.compareToIgnoreCase("sim") == 0) {
        // get the Class representation of the property we are trying to change
        Class<?> propClass = this.getProperty(property).getClass();
        // get the Constructor for this Class that takes a String as an argument
        Constructor<?> c = propClass.getConstructor(classArray);
        String s[] = {value};
        // set the property using a new instance of the obtained Constructor with the String argument "value"
        this.setProperty(property, c.newInstance(s));
      }
      else {
        // in this case we are trying to get at an element in the simulation.  Otherwise the process is the same.
        TElement t = (TElement) this.getTElementByID(telement);
        if (t != null) {
          Class<?> propClass = t.getProperty(property).getClass();
          Constructor<?> c = propClass.getConstructor(classArray);
          String s[] = {value};
          t.setProperty(property, c.newInstance(s));
        }
      }
    }
    catch (Exception e) {
      TDebug.println(e.getMessage());
      e.printStackTrace();
    }
  }

  protected void loadEngine() {
    if (getEngine() == null) {
      return;
    }

    Iterator it = mElements.values().iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      if (obj instanceof ControlGroup) {
        Iterator it2 = ((ControlGroup) obj).getElements().iterator();
        while (it2.hasNext()) {
          Object obj2 = it2.next();
          if (obj2 instanceof TSimElement) {
            addSimElement((TSimElement) obj2);
          }
        }
      }
      else if (obj instanceof TSimElement) {
        addSimElement((TSimElement) obj);
      }
    }
  }

  protected void loadRenderEngine(TRenderEngine renderEngine) {
    if (theScene == null) {
      return;
    }
    Iterator<?> it = mElements.values().iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      if (obj instanceof ControlGroup) {
        Iterator it2 = ((ControlGroup) obj).getElements().iterator();
        while (it2.hasNext()) {
          Object obj2 = it2.next();
          if (obj2 instanceof TAbstractRendered) {
            renderEngine.addDrawable((TAbstractRendered) obj2);
          }
        }
      }
      else if (obj instanceof TAbstractRendered) {
        renderEngine.addDrawable((TAbstractRendered) obj);
      }
    }
  }

  protected synchronized void processDrawnObjs(Object type, boolean state) {
    //TDebug.println(0,"ProcessingDrawnObjs:");
    synchronized (drawObjects) {
      Iterator<TAbstractRendered> it = drawObjects.iterator();
      while (it.hasNext()) {
        TDrawable d = (TDrawable) it.next();
        //TDebug.println(0,"\t" + d);
        if (type instanceof Class) {
          if (((Class<?>) type).isInstance(d)) {
            d.setDrawn(state);
            //TDebug.println(0,"\t\t" + state);

          }
        }
        else if (d.equals(type)) {
          d.setDrawn(state);
          //TDebug.println(0,"\t\t" + state);

        }
      }
    }
  }

  /*
  public void addDontDraw(Object obj)
  {
  if(dontDraw == null)
  dontDraw = new ArrayList<Object>();
  if(! (dontDraw.contains(obj)))
  dontDraw.add(obj);
  processDrawnObjs(obj, false);    
  }
  
  public void removeDontDraw(Object obj)
  {
  if(dontDraw != null);
  {
  dontDraw.remove(obj);
  processDrawnObjs(obj, true);
  }
  }
  
   */
  protected boolean checkDraw(TDrawable d) {
    if (d.isDrawn()) {
      boolean status = true;

      if ((dontDraw != null) && (dontDraw.size() > 0)) {
        Iterator<?> it = dontDraw.iterator();
        while (it.hasNext()) {
          Object obj = it.next();
          if (obj instanceof Class) {
            if (((Class<?>) obj).isInstance(d)) {
              status = false;
              break;
            }
          }
          else if (d.equals(obj)) {
            status = false;
            break;
          }
        }
      }
      return (status);
    }
    else {
      return false;
    }
  }

  public void processSelection(SelectEvent se) {
  }

  private void writeObject(java.io.ObjectOutputStream s)
          throws java.io.IOException {
    s.defaultWriteObject();

    if (defaultVpTransform == null) {
      //indicate that transform is null;
      s.writeBoolean(false);
      return;
    }
    s.writeBoolean(true);

    double[] transform = new double[16];
    this.defaultVpTransform.get(transform);

    // Write out all elements in the proper order.
    for (int i = 0; i < 16; i++) {
      s.writeDouble(transform[i]);
    }

  }

  private void readObject(java.io.ObjectInputStream s)
          throws java.io.IOException, ClassNotFoundException {
    // Read in any hidden stuff
    s.defaultReadObject();

    //check if transform was null;
    if (s.readBoolean() == false) {
      return;
    }

    double[] transform = new double[16];

    // Read in all elements in the proper order.
    for (int i = 0; i < 16; i++) {
      transform[i] = s.readDouble();
    }
    this.defaultVpTransform = new Transform3D(transform);
//    	guiElements = new ArrayList<Component>();
  }
}
