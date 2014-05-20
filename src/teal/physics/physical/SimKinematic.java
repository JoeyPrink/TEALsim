/**
 * $ID:$
 */
package teal.physics.physical;

import java.beans.PropertyChangeEvent;

import javax.vecmath.Vector3d;

import teal.app.SimGUI;
import teal.framework.TealAction;
import teal.render.j3d.ViewerJ3D;
import teal.render.viewer.SelectManager;
import teal.render.viewer.SelectManagerImpl;
import teal.render.viewer.TViewer;
import teal.sim.TSimElement;
import teal.sim.engine.EngineControl;
import teal.sim.engine.SimEngine;
import teal.sim.engine.TSimEngine;
import teal.sim.simulation.SimDrawOrder;
import teal.sim.simulation.SimWorld;
import teal.sim.simulation.Simulation3D;

//// temp until a real Kinematic Engine
//import teal.physics.em.EMEngine;


/**
 * @author pbailey
 *
 */
public class SimKinematic extends SimWorld {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3861979513483479136L;
	/**
	 * 
	 */
	
//	protected EMEngine theEngine;
	 
	
	public SimKinematic()
	{
	      super();
	       
	        SelectManager select = new SelectManagerImpl();
	        setSelectManager(select);
		
	}
	
    public void setEngine(TSimEngine model) {
    	if(theEngine != null){
    		theEngine.dispose();
    		theEngine = null;
    	}
//    	if(model instanceof EMEngine){
    		theEngine = model;
    		theEngine.setBoundingArea(boundingArea);
    		theEngine.setDeltaTime(deltaTime);
    		theEngine.setDamping(damping);
    		theEngine.setGravity(gravity);
//    	}
//    	else{
//    		throw new IllegalArgumentException("Wrong engine type in SimEM");
//    	}
        //if(mViewer != null)
        //	theEngine.setScene(mViewer);
        if(mSEC != null)
        	mSEC.setSimEngine(theEngine);
        loadEngine();
        
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
