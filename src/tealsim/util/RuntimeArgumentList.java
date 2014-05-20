/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tealsim.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import tealsim.util.RuntimeArgument.Bootstrap;

/**
 *
 * @author Chrisi
 */
public class RuntimeArgumentList {

  /**
   * Generates a set of RuntimeArguments based on a given array of strings 
   * (which usually comes from the command line)
   * 
   * @param cmds the array of strings to be split up into separate application 
   * parameters
   * @return a set of unique (that is no type of argument occurs twice)
   * RuntimeArguments parsed from the given input
   */
  public RuntimeArgumentList(String[] args) {
    LinkedList<String> cmds = new LinkedList<String>(Arrays.asList(args));
    List<RuntimeArgument> arguments = new ArrayList<RuntimeArgument>();

    while (!cmds.isEmpty()) {
      //first argument is always considered to be the switch defining the type of the argument
      RuntimeArgument tmp = RuntimeArgument.getArgument(cmds.pop());
      //we don't allow two arguments of the same type/class
      if (arguments.contains(tmp)) {
        throw new CircularReferenceException("You may not provide arguments of the same type twice");
      }

      arguments.add(tmp);
      /*
       * argument types are expected to start with a minus, therefore treat all
       * tokens succeeding the current token without an initial minus as a 
       * parameter for the current token/argument.
       */
      while (!cmds.isEmpty() && cmds.peek().startsWith("-") == false) {
        tmp.addParameter(cmds.pop());
      }
    }

    arguments_ = arguments;
    arguments_.add(new Bootstrap());    
  }

  public RuntimeArgumentList(List<RuntimeArgument> arguments) {
    arguments_ = arguments;
    arguments_.add(new Bootstrap());
  }
  
  //the set of all unique arguments passed to the application
  private List<RuntimeArgument> arguments_;
  //the call stack which is used to trace circular dependencies
  private List<RuntimeArgument> arg_stack_ = new Stack<RuntimeArgument>();

  /*
   * in case the RuntimeArgumentList is used in conjuction with anonymous classes
   * used as RuntimeArguments this parameter can take a reference to the enclosing 
   * class to enable creation of new instances of the RuntimeArguments (which is
   * necessary for the underlying circular reference check algorithm)
   */
  public Object enclosing_class_ = null;

  /**
   * Executes all runtime arguments based on the provided environment. This method
   * ensures that dependencies of particular arguments are honored.
   * 
   * @param environment the context in which this command shall be executed.
   * The RuntimeArguments are required to know beforehand which types they can handle.
   * @throws Exception in case something goes wrong or any parameter is invalid
   */
  public void execute(Object environment) throws Exception {
    //ensure that calling execute from the outside uses a fresh stack 'trace' each time
    arg_stack_.clear();

    int index = -1;
    
    while (!arguments_.isEmpty()) {
      /* in the first run we always execute the bootstraper, then we just pop
         the first element off the 'stack' */
      index = (index == -1) ? arguments_.indexOf(new Bootstrap()) : 0;
      RuntimeArgument arg = arguments_.get(index); 
      recursiveExecute(arg, environment);
    }
  }

  /**
   * Recursive implementation of the execute command which calls itself in case
   * dependent arguments are found (and therefore executing these first). After 
   * the call of this method the arguments_ list is guaranteed not to contain the 
   * argument passed in as parameter anymore.
   * 
   * @param arg the RuntimeArgument to execute
   * @param environment the environment for the RuntimeArgument
   * @throws Exception basically an IllegalArgumentException indicating which
   * command violated the runtime restrictions (e.g. circular dependencies, not
   * allowed argument/environment combination, ...)
   */
  private void recursiveExecute(RuntimeArgument arg, Object environment) throws Exception {

    if (!arg.getDependencies().isEmpty()) {
      if (arg_stack_.contains(arg)) {
        throw new IllegalArgumentException("Found a circular reference with last argument being '" + arg.toString() + "'");
      }

      arg_stack_.add(arg);
      //loop through all dependencies of the current argument and check if any
      //of them are present to be executed first
      for (Class dependency : arg.getDependencies()) {
        
        Object dependency_instance = null;
        try {
          dependency_instance = dependency.getConstructor().newInstance();
        }
        catch (NoSuchMethodException e) {
          /*
           * this indicates that we are dealing with an anonymous class defined
           * in the enclosing class, therefore try it one more time passing in the
           * reference to the enclosing class.
           * REMINDER: if the class to instantiated was originally constructed with
           * anything but a no-argument constructor, then this call will fail
           * because it will expect the same arguments!
           */
          dependency_instance = dependency.getDeclaredConstructors()[0].newInstance(enclosing_class_);
        }
        //execute the dependency first
        if (arguments_.contains(dependency_instance)) {
          recursiveExecute(arguments_.get(arguments_.indexOf(dependency_instance)), environment);
        }
      }
      arg_stack_.remove(arg);
    }
    arg.execute(environment);
    arguments_.remove(arg);
  }
  
  public static class CircularReferenceException extends IllegalArgumentException {

    private CircularReferenceException(String message) {
      super(message);
    }
  }
}
