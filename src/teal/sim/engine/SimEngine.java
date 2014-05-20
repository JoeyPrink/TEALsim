package teal.sim.engine;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import teal.math.Integratable;
import teal.math.MetaIntegratable;
import teal.render.TRenderEngine;
import teal.sim.TSimElement;
import teal.sim.simulation.TSimulation;
import teal.util.TDebug;

/**
 * 
 * This is the implementation of an engine for the desktop version. It
 * adds the synchronization functionality to the {@link AbstractEngine}
 * class. It also implements the abstract methods as well as the 
 * {@code Runnable} interface that enables is to be run in a {@code Thread}.
 * 
 */
public class SimEngine extends AbstractEngine implements Runnable {

  /**
   * 
   */
  private static final long serialVersionUID = 4364647412982347839L;
  protected TSimulation theSim;
  /**
   * Additional simulation state variable triggered in circumstances when the
   * <code>run</code> loop should temporarily suspend regular operation when
   * in <code>EngineControl.RUNNING</code> mode, and instead stay idle, and
   * consitently yield the engine thread. Such measures are necessary when,
   * for instance, doing step-by-step simulation.
   * 
   * Development note: there might be a way to altogether avoid using waiting
   * mode by properly synchronyzing all methods which might interfere with the
   * <code>run</code> loop.
   * 
   * @see TEngineControl#RUNNING
   * @see #run()
   * @see #nextStep()
   */
  protected volatile boolean waiting;
  /**
   * Flag which indicates that the engine thread has started. It is initially
   * set to false, and only set to true once the <code>run</code> loop is
   * entered.
   */
  private volatile boolean threadStarted = false;
//	protected volatile boolean waiting;
  private Object threadStartedLock = new Object();
  protected List<TRenderEngine> renderEngines;

  public SimEngine() {
    this(EngineType.KINETIC);
  }

  public SimEngine(EngineType type) {
    super(type);
    waiting = false;
    /*
     * Should recall why these two lists were carefully synchronized.
     */
    integratingObjs = Collections.synchronizedList(new ArrayList<Integratable>());
    metaintegratingObjs = Collections.synchronizedList(new ArrayList<MetaIntegratable>());

  }

  /**
   * Performs the central loop.
   */
  public void run() {
    if (simState == TEngineControl.NOT) {
      TDebug.println(2, "SimEngine run: EXITING");
      synchronized (threadStartedLock) {
        threadStarted = false;
        return;
      }
    }
    synchronized (threadStartedLock) {
      if (threadStarted) //already started
      {
        return;
      }
      threadStarted = true;
    }
    // TDebug.println(2,"theEngine.run():");
    // TDebug.println(2,"SimEngine run: thread=" + theThread.getName());
    int i = 0;
    while (i < 1000000) {
//			if(this instanceof teal.physics.em.EMEngine){
//				long ts = System.currentTimeMillis();
//				for(int j=0;j<100000;++j)
//					((teal.physics.em.EMEngine)this).getEField();
//					this.getElementByType(EField.class);
//				System.out.println("TIME: " + (System.currentTimeMillis()-ts));
//			}
      synchronized (this) {
        Thread.yield();
        TDebug.println(3, "SimEngine.run() i \t" + i + ": simState " + simState + ",  ");
        
        if (simState == TEngineControl.NOT) {
          TDebug.println(1, "SimEngine run: EXITING IN WHILE LOOP BEFORE SYNCH ADD/REMOVE");
          synchronized (threadStartedLock) {
            threadStarted = false;
            return;
          }
        }
        try {
          // synchronized( this){
          // Run-time addition and removal of SimElements.
          synchronized (toRemoveList) {
            if (!toRemoveList.isEmpty() && simState != TEngineControl.NOT) {
              synchRemoveSimElements(toRemoveList);
              toRemoveList.clear();
            }
          }
          synchronized (toAddList) {
            if (!toAddList.isEmpty() && simState != TEngineControl.NOT) {
              synchAddSimElements(toAddList);
              toAddList.clear();
            }
          }
          // }
          switch (simState) {
            case TEngineControl.NOT:
              TDebug.println(2, "SimEngine run: EXITING FROM WITHIN WHILE LOOP AFTER SYNCH ADD/REMOVE");
              synchronized (threadStartedLock) {
                threadStarted = false;
                return;
              }
            case TEngineControl.INIT:
              doRefresh();
              wait(idleDelay);
              break;
            case TEngineControl.PAUSED:
              // TDebug.println(2,"SimEngine run: PAUSED");
              doReorder();
              doRefresh();
              wait(idleDelay);
//						Thread.sleep(idleDelay);
              break;

            case TEngineControl.ENDED:
              // TDebug.println(2,"SimEngine run: PAUSED");
              doReorder();
              doRefresh();
              wait();
            case TEngineControl.RUNNING:
              // TDebug.println(2,"SimEngine run: RUNNING wait =" +
              // waiting);
              // synchronized (this) {
              if (waiting) {
//							Thread.yield();
//							Thread.sleep(idleDelay);
                wait(idleDelay);
              }
              else {
                startTime = System.currentTimeMillis();
                nextStep();
                Thread.sleep(delay);
                endTime = System.currentTimeMillis();
                padDelay = frameDelay - (startTime - endTime);
                if (padDelay > 0L) {
                  Thread.sleep(padDelay);
                }
                if (isCheckingRate) {
                  checkFrameRate();
                }
              }
              // }
              break;
          }
        }
        catch (InterruptedException e) {
          TDebug.println(1, "SimEngine run InterruptedException: "
                  + e.getMessage());
        }
        i++;
      }
    }
    synchronized (threadStartedLock) {
      threadStarted = false;
    }
  }

  public void addSimElements(Collection<TSimElement> objects) {
    //System.out.println("SimEngine addSimElements() being called!");
    synchronized (threadStartedLock) {
      if (threadStarted) {
        synchronized (toAddList) {
          toAddList.addAll(objects);
        }
      }
      else {
        synchAddSimElements(objects);
      }
    }
  }

  public void removeSimElements(Collection<TSimElement> objects) {
    synchronized (threadStartedLock) {
      if (threadStarted) {
        synchronized (toRemoveList) {
          toRemoveList.addAll(objects);
        }
      }
      else {
        synchRemoveSimElements(objects);
      }
    }
  }

  public boolean isThreadRunning() {
    synchronized (threadStartedLock) {
      return threadStarted;
    }
  }

  /**
   * Run-time object addition to the world is now handled by requst. If the
   * world thread has started [threadStarted, a flag set from within run()],
   * addition occurs at the start of the run loop. Otherwise, the objects are
   * added immediately.
   * 
   * <p>* Info from old addSimElement(TSimElement): Used to add a SimElement
   * to the simulation, the SimElement list and all object type specific
   * lists.
   * 
   * <p>* Info from old addSimElement(TSimElement, boolean): Used to add an
   * SimElement to the simulation, the simElement list now only maintains a
   * local ccopy for ease in clearing the world. The maintanance of the
   * general element list has been moved to TEALapplet.
   * 
   * @param obj
   */
  public void addSimElement(TSimElement obj) {
    synchronized (threadStartedLock) {
      if (threadStarted) {
        //if (true) {
        synchronized (toAddList) {
          toAddList.add(obj);
        }
      }
      else {
        synchAddSimElement(obj);
      }
    }
  }

  public void removeSimElement(TSimElement obj) {
    synchronized (threadStartedLock) {
      if (threadStarted) {
//			if (true) {
        synchronized (toRemoveList) {
          toRemoveList.add(obj);
        }
      }
      else {
        synchRemoveSimElement(obj);
      }
    }
  }

  protected void removeSimulation() {
    super.removeSimulation();
    theSim.dispose();
    theSim = null;
  }

  public void setSimulation(TSimulation sim) {
    if (theSim != null) {
      removeSimulation();
    }
    theSim = sim;
    super.setSimulation(sim);
  }
  
  
  private long start_time_ = -1;
  private int frame_number_ = 0;
  private int tick_ = 0;
  private int frame_number_tick_ = 0;
  /**
   * Advances the simulation by the next frame.
   */
  private void nextStep() {
    
    if(start_time_ < 0)
      start_time_ = System.currentTimeMillis();
    
    long start = System.currentTimeMillis();
    doReorder();

    doDynamic();
//		
//		System.out.println("DYNAMIC STEP: " + (end-start));
    update();
    long middle = System.currentTimeMillis();
    doRefresh();
    long end = System.currentTimeMillis();
//		frame++;
    
    System.out.println("Frame["+ frame_number_++ +" | " + (start - start_time_) + "] - Computation time: " + (middle - start) + " | Render time: " + (end - middle));
    int tmp_tick = (int) ((start - start_time_) / 1000);
    if(tmp_tick > tick_) {
      int frames = (frame_number_ - frame_number_tick_);
      System.out.println("Frames for tick ["+ tick_ + " -> "+ tmp_tick +"] = " + frames +" ( -> " + (frames / (tmp_tick - tick_)) + " [fps]) | delta=" + deltaTime);
      
      frame_number_tick_ = frame_number_;
      tick_ = tmp_tick;
    }
  }

  private void singleStep() {
//		waiting = true;
    nextStep();
  }

  public void init() {
    if (theSim != null) {
      setSimState(TEngineControl.INIT);
      theSim.initialize();
      synchronized (this) {
        requestRefresh();
        requestSpatial();
        doRefresh();
      }
    }
  }

  public void not() {
    stop();
    setSimState(TEngineControl.NOT);
  }

  public void stop() {
    synchronized (simStateLock) {
      if (simState == TEngineControl.ENDED) {
        return;
      }
      setSimState(TEngineControl.PAUSED);
    }
  }

  public void end() {
    setSimState(TEngineControl.ENDED);
    synchronized (this) {
      notify();
    }
  }

  public void start() {
    synchronized (simStateLock) {
      if (simState == TEngineControl.ENDED) {
        return;
      }
      setSimState(TEngineControl.RUNNING);
    }
    synchronized (this) {
      notify();
    }

//		waiting = false;		
  }

  public void step() {
    synchronized (simStateLock) {
      if (simState == TEngineControl.ENDED) {
        return;
      }
      setSimState(TEngineControl.PAUSED);
    }
    synchronized (this) {
      singleStep();
    }
  }

  public void resume() {
    start();
  }

  public void reset() {
    stop();
    synchronized (this) {
      time = 0.;
      setImagesValid(false);
      setSimState(TEngineControl.INIT);
      requestRefresh();
      notify();
    }
  }

  public int getSimState() {
    synchronized (simStateLock) {
      return simState;
    }
  }
  private Object simStateLock = new Object();

  public void setSimState(int state) {
    // TDebug.println(1,"SimEngine Setting simState: " + state);
    synchronized (simStateLock) {
      if (state != simState) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this,
                "simState", new Integer(simState), new Integer(state));
        if (engineControl != null) {
          if (engineControl instanceof EngineControl) {
            ((EngineControl) engineControl).displaySimControl(state);
          }
        }
        propSupport.firePropertyChange(pce);
        simState = state;
      }
    }
  }

  public void refresh() {
    // Since waiting is ONLY used for this at the moment
    // we don't need to lock it
    waiting = true;
    synchronized (this) {
      requestRefresh();
    }
    waiting = false;
  }
}
