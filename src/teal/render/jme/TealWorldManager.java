package teal.render.jme;

import com.jme.system.DisplaySystem;
import org.jdesktop.mtgame.WorldManager;

import teal.util.TDebug;

public class TealWorldManager {

  private static WorldManager worldManager = null;

  public static void setWorldManager(WorldManager wm) {
    if (worldManager != null) {
      TDebug.println(1, "WARNING: other worldmanager already used!");
    }
    worldManager = wm;
  }

  public static WorldManager getWorldManager() {

    if (worldManager == null) {
      System.out.println("Creating a new WorldManager");
      //FIXXME: just for now to
      try {
        worldManager = (WorldManager) Class.forName("org.jdesktop.wonderland.client.jme.ClientContextJME").getDeclaredMethod("getWorldManager", (Class<?>[]) null).invoke(null);
      }
      catch (Exception e) { //not possible (perhaps not in wonderland)
        TDebug.println(1, e);
        worldManager = new WorldManager("TealSimWM");
      }
    }

    return worldManager;
  }

  public static void shutdown() {
    if (worldManager != null) {
      
      worldManager.shutdown();
//      worldManager.getProcessorManager().notify();
      worldManager = null;
      DisplaySystem.resetSystemProvider();
      System.gc();
    }
  }
}
