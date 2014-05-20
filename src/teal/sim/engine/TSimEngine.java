/*
 * $Id: TSimEngine.java,v 1.13 2010/08/25 19:33:55 stefan Exp $ 
 */
package teal.sim.engine;

import java.util.Collection;

import javax.vecmath.Vector3d;

import teal.core.HasID;
import teal.core.TElement;
import teal.render.scene.Scene;
import teal.render.Bounds;
import teal.render.TRenderEngine;
import teal.render.TRenderListener;
import teal.sim.TSimElement;
import teal.sim.collision.HasCollisionController;
import teal.sim.properties.PhysicalElement;
import teal.sim.simulation.TSimulation;


/** 
 * @TODO check documentation!
 * 
 * COPIED FROM TEngine!
 * The TEngine provides the minimum interface for a simulation including both the simulated objects and forces
 * that make up the world and provide an interface to the renders that model the results. 
 * Details about a specific evironment 
 * are provided by the actual world implementation or additional interfaces. This is isolates HasEngine 
 * from needing too many details.
 */

public interface TSimEngine extends TEngineControl, TElement, TRenderListener  {

  //XXX: try
  public enum EngineElementType {

    RENDER_ENGINES,
    BFIELD, EFIELD, PFIELD, GFIELD,
    PHYSICAL_OBJECTS,
    DAMPING,
    GRAVITY;
  }

  public enum EngineType {
    BIOCHEMIC,
    KINETIC,
    ELECTROMAGNETIC
  }
	 
	public void setEngineControl(TEngineControl control);
	public TEngineControl getEngineControl();
    

    /**
     * Checks to find if the world needs to be refreshed, and refreshes if needed.
     */
    public void refresh();
//    public void enableRender(boolean state);
    
    public void requestRefresh();
    public void requestSpatial();
   
    public void renderComplete(TRenderEngine viewer);

  

    /** Releases the world's resources, places the world's simState to NOT,
     * does not delete or destroy the actual world object */
    public void dispose();  
  
  /**
   * adds an element to the TSimulation and insures that it is added
   * to all the lists and viewers it should be.
   */
  public void addSimElement(TSimElement obj);

  public void addSimElements(Collection<TSimElement> objects);

  public void setSimulation(TSimulation sim);

  /**
   * Removes all objects ,viewers, spatial, etc from the world.
   */
//    public void removeAll();
  /**
   * Removes an Element from the simulation insuring that it is
   * removed from all lists,viewers and cleaned up.
   */
  public void removeSimElement(TSimElement obj);

  /**
   * Removes a collection of SimElements from the simulation, insuring that they are
   * removed from all lists,viewers and cleaned up.
   * 
   * @param objects collection to remove.
   */
  public void removeSimElements(Collection<TSimElement> objects);

  /** 
   * Allows the addition of <code>TViewer</code> objects. This method is
   * internally called by the <code>addSimElement</code> methods. For most
   * <code>SimElement</code>s, the add operation is basically an addition
   * to the proper list. Adding a viewer might entail additional configuration,
   * which justifies having the associated procedure in a standalone method.  
   */
  public void addRenderEngine(TRenderEngine viewer);

  public void removeRenderEngine(TRenderEngine viewer);

  public void removeRenderEngines();

  //public Scene getScene();
  //public void setScene (Scene scene);
  /**
   * Returns the bounding area of the world.
   * 
   * @return bounding area.
   */
  public Bounds getBoundingArea();

  /**
   * Sets the bounding area of the world.
   * 
   * @param bounds bounding area.
   */
  public void setBoundingArea(Bounds bounds);

  /**
   * Returns the damping for the world.
   * 
   * @return time step.
   */
  public double getDamping();

  /**
   * Sets the damping for the world.
   * 
   * @param damping rate.
   */
  public void setDamping(double damping);

  /**
   * Returns the time step of the world.
   * 
   * @return time step.
   */
  public double getDeltaTime();

  /**
   * Sets the time step of the simulation.
   * 
   * @param dTime time step.
   */
  public void setDeltaTime(double dTime);

  /**
   * Returns the current simulation time (milliseconds since simulation start?)
   * 
   * @return time, in milliseconds, since the start of the simulation.
   */
  public double getTime();

  /**
   * Sets the current time of the simulation (in milliseconds).  Note that this method does not advance (or rewind) the
   * simulation to the desired time, but rather just resets the clock.
   * 
   * @param time new time.
   */
  public void setTime(double time);

//    public boolean getShowTime();
  /**
   * Sets whether the simulation time is printed to the console.
   * 
   * @param b
   */
  public void setShowTime(boolean b);

  /**
   * Returns the framerate of the simulation.
   * 
   * @return frame rate (frames per second).
   */
  public double getFrameRate();

  /**
   * Sets the desired frame rate of the simulation.  The effectiveness of this method depends on the computational
   * complexity of the simulation.
   * 
   * @param fps desired frame rate (frames per second).
   */
  public void setFrameRate(double fps);

  public void setCheckFrameRate(boolean b);

//    public void checkFrameRate();
  /**
   * This should return the forces on the supplied PhysicalElement.  I don't think this is being used anymore?
   * 
   * @param ph
   * @return forces
   */
  public Vector3d getForces(PhysicalElement ph);

  public Object getElementByType(EngineElementType type);

  public <T> T getElementByType(Class<T> type);

  public <T> Collection<T> getCollectionByType(Class<T> type);

  public void setGravity(Vector3d gravityVec);

  public Vector3d getGravity();

  public boolean isThreadRunning();

  public void setAnnihilating(boolean Annihilating);

  public void requestReorder(HasCollisionController x);
}
