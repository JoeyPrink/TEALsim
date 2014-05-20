package tealsim.util;

import imx.loggui.LogMaster;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JFrame;
import teal.app.SimPlayerApp;
import teal.render.scene.SceneFactory;
import teal.sim.simulation.TSimulation;
import tealsim.TealSimApp;

/**
 * Base class for any give command line argument.
 * By definition 2 arguments equal if their class equals, thus prohibiting the
 * use of redundant arguments.
 * 
 * RuntimeArguments behavior is such that they fail late, that is they will not
 * immediately fail if improperly configured but rather once called to execute.
 */
public abstract class RuntimeArgument {

  /**
   * Basic factory method to generate the corresponding RuntimeArgument based
   * on a given switch parameter
   * 
   * @param cmd the switch specifying the current argument
   * @return a RuntimeArgument matching the given provided switch
   */
  public static RuntimeArgument getArgument(String cmd) {
    if (cmd.equalsIgnoreCase("-c")) {
      return new ShowFramerate();
    }
    else if (cmd.equalsIgnoreCase("-a")) {
      return new ShowAllExperiments();
    }
    else if (cmd.equalsIgnoreCase("-n")) {
      return new ShowSingleExperiment();
    }
    else if (cmd.equalsIgnoreCase("-gfx")) {
      return new SpecificGFXEngine();
    }
    else if (cmd.equalsIgnoreCase("-debug")) {
      return new ShowDebugMessages();
    }
    else {
      throw new IllegalArgumentException("No valid argument switch: '" + cmd + "'");
    }
  }
  /*
   * List of dependencies on other arguments
   * Any argument which the current argument depends on is guaranteed to be executed
   * first.
   * Circular dependencies will be detected when executing the argument list,
   * and will throw an exception.
   */
  protected List<Class> dependencies_ = new ArrayList<Class>();
  //list of parameters which may be used by a particular argument
  protected Deque<String> parameters_ = new ArrayDeque<String>();

  /**
   * The main method for any RuntimeArgument which does something within the
   * context of a given (system) environment.
   * 
   * @param environment the context in which this command shall be executed.
   * The RuntimeArgument is required to know beforehand which types it can handle.
   * @throws Exception in case something goes wrong or any parameter is invalid
   */
  public abstract void execute(Object environment) throws Exception;

  /**
   * Setter method to pass in additional parameters if required for a particular
   * argument.
   * 
   * @param parameters the parameters which shall be used by this argument
   */
  public void addParameter(String parameter) {
    parameters_.add(parameter);
  }

  /**
   * Getter method to return the arguments this argument depends on (that is,
   * which arguments have to be executed before this argument)
   * 
   * @return the list of dependencies
   */
  public List<Class> getDependencies() {
    return dependencies_;
  }

  @Override
  public boolean equals(Object obj) {
    //System.out.println("Comparing THIS object ["+this.toString()+"] with THAT object ["+obj.toString()+"]");
    if (obj == null) {
      return false;
    }
    /*
     * since we are using CGLIB in the test suite to mock objects we have to do the
     * class comparison slightly different, since CGLIB will enhance the classes;
     * otherwise we could just use:
     * 
     * if (getClass() != obj.getClass()) {
     *   return false;
     * }
     */
    if(!this.getClass().isAssignableFrom(obj.getClass())) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return this.getClass().hashCode();
  }

  /**
   * Special class which is used to set a list of RuntimeArguments which shall be executed
   * before any other RuntimeArgument
   */
  static class Bootstrap extends RuntimeArgument {

    public Bootstrap() {
      dependencies_.add(ShowDebugMessages.class);
      dependencies_.add(StartSynchronizationServer.class);
    }

    @Override
    public void execute(Object environment) throws Exception {
      return;
    }
  }

  public static class ShowDebugMessages extends RuntimeArgument {

    @Override
    public void execute(Object environment) throws Exception {
      JFrame logging_frame = LogMaster.getLogFrame();
      LogMaster.getLogMaster().configDefault();
      LogMaster.getLogMaster().configExternalLHFF();
      logging_frame.setVisible(true);
      LogMaster.getLogMaster().refresh(); 
    }
  }

  public static class StartSynchronizationServer extends RuntimeArgument {

    @Override
    public void execute(Object environment) throws Exception {
//      throw new UnsupportedOperationException("Not supported yet.");
    }
  }  
  
  public static class ShowFramerate extends RuntimeArgument {

    @Override
    public void execute(Object environment) throws Exception {
      if ((environment instanceof TealSimApp) == false) {
        throw new IllegalArgumentException("May only show the framerate for instances of the TealSimApp class!");
      }

      ((TealSimApp) environment).checkFrameRate = true;
    }
  }

  public static class ShowAllExperiments extends RuntimeArgument {

    public ShowAllExperiments() {
      dependencies_.add(SpecificGFXEngine.class);
    }

    @Override
    public void execute(Object environment) throws Exception {
      if ((environment instanceof TealSimApp) == false) {
        throw new IllegalArgumentException("May only show all experiments for instances of the TealSimApp class!");
      }

      ((TealSimApp) environment).addActions();
    }
  }

  /**
   * Triplet-based implementation of command line parsing.  Instead of property/value pairs acting
   * only on the simulation, we now use element/property/value triplets, where element is a 
   * TElement of the simulation that we want to change a property on.  Using "sim" for the element
   * argument looks for the property on the simulation itself, reducing to the previous implementation.
   */
  public static class ShowSingleExperiment extends RuntimeArgument {

    public ShowSingleExperiment() {
      dependencies_.add(SpecificGFXEngine.class);
    }

    @Override
    public void execute(Object environment) throws Exception {
      if ((environment instanceof SimPlayerApp) == false) {
        throw new IllegalArgumentException("May only show specific experiments for instances of the SimPlayerApp class!");
      }

      Class<?> simClass = Class.forName(parameters_.pop());
      TSimulation simulation = (TSimulation) simClass.newInstance();

      //if a specific simulation is started, then it's supposed to be bootable with certain properties
      try {
        while (!parameters_.isEmpty()) {
          simulation.setProperty(parameters_.pop(), parameters_.pop(), parameters_.pop());
        }
      }
      catch (NoSuchElementException e) {
        throw new IllegalArgumentException("Invalid number of command line arguments.");
      }

      ((SimPlayerApp) environment).getSimPlayer().load(simulation);
    }
  }

  public static class SpecificGFXEngine extends RuntimeArgument {
    
    @Override
    public void execute(Object environment) throws Exception {
      String engine = parameters_.pop();
      //didn't want to use reflection for these 2 options...
      if (engine.equals("J3D")) {
        SceneFactory.setFactory(SceneFactory.J3D);
      }
      else if (engine.equals("JME")) {
        SceneFactory.setFactory(SceneFactory.JME);
      }
    }
  }
}
