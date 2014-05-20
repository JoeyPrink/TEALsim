package teal.render.scene;

import java.net.URL;

import teal.render.TRendered;
import teal.render.TAbstractRendered.NodeType;
import teal.render.viewer.AbstractViewer3D;
import teal.util.TEALSimSecurityManager;

public class SceneFactory {

  private static TSceneFactory theFactory = null;

  public final static String J3D = "teal.render.j3d.SceneFactoryJ3D";
  public final static String JME = "teal.render.jme.SceneFactoryJME";
  
  /**
   * 
   * @todo find a more efficient solution than synchronizing this getter method
   * @return 
   */
  private static synchronized TSceneFactory getFactory() {

    if (theFactory == null) {
      //by default use the Java3D graphics engine
      setFactory(J3D);
    }

    return theFactory;
  }

  public static void setFactory(String classType) {
    try {
      //on our initial run we set our custom SecurityManager to enable TEALsim
      //to run locally as WebStart application as well
      if(theFactory == null)
        System.setSecurityManager(new TEALSimSecurityManager());
      
      //TODO make a change of the SceneFactory update the current view as well!
      Class<?> factoryClass = Class.forName(classType);
      theFactory = (TSceneFactory) factoryClass.newInstance();
      System.out.println("Set factory to -> " + classType);
    }
    catch (ClassNotFoundException cnfEx) {
      throw new IllegalArgumentException("Could not Find: " + classType, cnfEx);
    }
    catch (IllegalAccessException iaEx) {
      throw new IllegalArgumentException("Acess Error " + classType + ": ", iaEx);
    }
    catch (InstantiationException iEx) {
      throw new IllegalArgumentException("Instantiation Error " + classType + ": ", iEx);
    }
    catch (ClassCastException ccEx) {
      throw new IllegalArgumentException(classType + " cast to TSceneFactory error: ", ccEx);
    }
  }

  public static AbstractViewer3D makeViewer() {
    return getFactory().makeViewer();
  }

  public static TNode3D makeNode(TRendered element) {
    return getFactory().makeNode(element);
  }

  public static TNode3D makeNode(TRendered element, NodeType type) {
    return getFactory().makeNode(element, type);
  }

  public static TNode3D makeNode(NodeType type) {
    return getFactory().makeNode(type);
  }

  public static void refreshNode(TRendered element, int renderFlags) {
    getFactory().refreshNode(element, renderFlags);
  }

  public static TNode3D loadModel(URL path) {
    return getFactory().loadModel(path);
  }

  public static TNode3D load3DS(String path) {
    return getFactory().load3DS(path);
  }

  public static TNode3D load3DS(String path, String mapPath) {
    return getFactory().load3DS(path);
  }
}
