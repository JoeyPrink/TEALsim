/**
 * $ID:$
 */
package teal.physics.em;

import teal.render.viewer.SelectManager;
import teal.render.viewer.SelectManagerImpl;
import teal.sim.TSimElement;
import teal.sim.engine.TSimEngine;
import teal.sim.engine.TSimEngine.EngineType;
import teal.physics.physical.SimKinematic;
import teal.sim.simulation.SimWorld;
import teal.sim.simulation.Simulation3D;


/**
 * @author pbailey
 *
 */
public class SimEM extends SimWorld {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7192933126374062456L;
//	protected EMEngine theEngine;
	
	public SimEM()
	{
	      super();
	      mEngineType = EngineType.ELECTROMAGNETIC;
	}
	
    public void setEngine(TSimEngine model) {
    	if(theEngine != null && theEngine != model){
    		theEngine.dispose();
    		theEngine = null;
    	}
    	theEngine = model;
//    	if(model instanceof EMEngine){
//    		theEngine = (EMEngine) model;
//    		theEngine.setBoundingArea(boundingArea);
//    		theEngine.setDeltaTime(deltaTime);
//    		theEngine.setDamping(damping);
//    		theEngine.setGravity(gravity);
//    	}
//    	else{
//    		throw new IllegalArgumentException("Wrong engine type in SimEM");
//    	}
//        if(mSEC != null)
//        	mSEC.setEngine((TSimEngine)theEngine);
//        loadEngine();
        
    }

    public TSimEngine getEngine() {
        return theEngine;
    }
    
    public void addSimElement(TSimElement elm){
    	theEngine.addSimElement(elm);
    }
    public void removeSimElement(TSimElement elm){
    	theEngine.removeSimElement(elm);
    }


}
