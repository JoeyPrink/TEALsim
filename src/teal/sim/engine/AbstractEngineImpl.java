package teal.sim.engine;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;

import teal.sim.TSimElement;

/**
 * This class is the base class for the type-specific part of an engine. For now all types but
 * {@link TEngine#EM_ENGINE} are represented by this class. It can also be seen as an interface
 * used by {@link AbstractEngine}.
 * <p/>
 * A subclass can put all its type-specific elements into the elements lists, so they can be queried by
 * the engine.
 * 
 * <p/>
 * Note that this may contain a lot of code which is now in {@link AbstractEngine} and its subclass. As
 * soon as functionality is detected to be type-specific, it should be put into the appropriate subclass
 * of this class.
 * 
 * @author Stefan
 * 
 * @see AbstractEngine
 *
 */
public class AbstractEngineImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6353451867771555199L;

	
	protected HashMap<Class<? extends Object>,Object> elementsByType = new HashMap<Class<? extends Object>, Object>();
	protected HashMap<Class<? extends Object>,Collection<? extends Object>> collectionsByType = new HashMap<Class<? extends Object>,Collection<? extends Object>>();

	// an enum map can get the element in constant time, but needs type-casts when used
	protected EnumMap<AbstractEngine.EngineElementType,Object> typedElements = new EnumMap<AbstractEngine.EngineElementType,Object>(TSimEngine.EngineElementType.class);
	
	
	/**
	 * This needs to be called by the engine whenever an object is added to the engine.
	 * 
	 * @param obj element to add
	 */
	public void addSimElement(TSimElement obj) {		
	}	

	/**
	 * This needs to be called by the engine whenever an object is removed from the engine.
	 * 
	 * @param obj element to add
	 */
	public void removeSimElement(TSimElement obj) {		
	}	

	
	public EnumMap<AbstractEngine.EngineElementType,Object> getTypedElementsEnum(){
		return typedElements;
	}
	
	
	public HashMap<Class<? extends Object>,? extends Object> getTypedElements() {
		return elementsByType;
	}
	
	public HashMap<Class<? extends Object>,Collection<? extends Object>> getTypedCollections() {
		return collectionsByType;
	}

}
