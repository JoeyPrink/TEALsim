package teal.render.j3d;

import com.sun.j3d.utils.universe.ConfiguredUniverse;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class WonderlandStuff {
	
	SimpleUniverse universe = null;
	
	private static final WonderlandStuff cellUniverseInstance = new WonderlandStuff(); 
	
    public static WonderlandStuff getInstance()  
    {  
        return cellUniverseInstance;  
    } 
    
    public void setWLUniverse(ConfiguredUniverse universe) {
    	this.universe = universe;
    }
    
    public boolean isUniverse() {
    	return this.universe.getViewer().getView().isViewRunning();
    }
    
    public SimpleUniverse getWLUniverse() {
    	return this.universe;
    }

}
