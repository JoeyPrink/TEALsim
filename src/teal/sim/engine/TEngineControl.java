/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TEngineControl.java,v 1.1 2007/08/14 19:34:10 pbailey Exp $ 
 * 
 */
package teal.sim.engine;

/** 
 * <code>EngineControl</code> objects are the primary means through
 * which the application, and its user, control the model.
 * 
 * 
 * <code>EngineControl</code> object. The <code>SimEngineControl</code> 
 * instance of the latter is architectured to contain the thread object
 * within which the model runs. As such, it can directly initialize, start,
 * step, stop and reset the simulation.
 * 
 * 
 * A note about model states and model control methods. These two
 * concepts should not be confused. The control methods modify the
 * model state, but only do so conditionally on the current state,
 * as well as other factors. They also do other instance and
 * architecture specific changes, such as reset other model variables.
 * 
 */
public interface TEngineControl {

  /**
   * Model state value, where the simulation is not yet initialized.
   */
  public static final int NOT = 0;
  /**
   * Model state value, where the simulation is initialized, but no
   * actual simulation has yet happened, in other words the model
   * has not yet started, stopped or ended, that is has not yet
   * been in any of the <code>RUNNING</code>, <code>PAUSED</code>
   * or <code>ENDED</code> states respectively. The exception being
   * that the model reverts to this state when
   * 
   * state the model reverts to when it is reset.
   */
  public static final int INIT = 1;
  /**
   * Model state value, where the simulation is running continuously,
   * referred to alternatively as the simulation being started.
   */
  public static final int RUNNING = 2;
  /**
   * Model state value, where the simulation is stopped, this differs
   * from <code>ENDED</code> in the sense that the simulation IS allowed
   * to restart, that is return to the <code>RUNNING</code> state.
   */
  public static final int PAUSED = 3;
  /**
   * Model state value, where the simulation has ended, this differs
   * from <code>PAUSED</code> in the sense that the simulation IS NOT
   * allowed to return to the <code>RUNNING</code> state. However, the
   * simulation may be reset, that is revert to the <code>INIT</code>
   * state.
   */
  public static final int ENDED = 4;

  /**
   * Stops the engine if running and sets status to not initialized. 
   * No actions should be performed except init when the engine is in this state.
   *
   */
  public void not();

  /**
   * Initializing the model puts it in the <code>INIT</code> state,
   * and insures that it is ready to start, by performing preliminary
   * The method 
   * 
   *  Initialized the model, by insuring that it is ready to start.
   */
  public void init();

  /** Stops the model at its current state processing of
   *  spatial changes continues.
   */
  public void stop();

  /** suspends the ability to start or step the world, the only 
   *  action at this state is spatial updates and reset.
   */
  public void end();

  /** Starts repeated calls to next step.
   */
  public void start();

  /** Stops or inits the world and then call a single nextStep().
   */
  public void step();

  /**  resumes running at the current world state  */
  public void resume();

  /** Stops the world, resets time and frame count to zero, 
   * and makes sure the workd is ready to run.
   */
  public void reset();

  /** returns the current state of the model.
   */
  public int getSimState();

  /**
   * This is provided for propertyChange support normally should not be
   * called from the application.
   */
  public void setSimState(int state);
}
