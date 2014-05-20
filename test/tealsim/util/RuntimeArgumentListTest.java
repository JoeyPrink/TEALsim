/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tealsim.util;

import java.util.Iterator;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Deque;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import static java.lang.Character.*;

/**
 *
 * @author Chrisi
 */
public class RuntimeArgumentListTest {

  @BeforeClass
  public static void setUpClass() throws Exception {
    System.out.println(">> NOW TESTING RuntimeArgumentList CLASS implementation <<");
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }
  private RuntimeArgument a_;
  private RuntimeArgument b_;
  private RuntimeArgument c_;
  private RuntimeArgument d_;
  private RuntimeArgument e_;
  private Deque<String> output_;
  private List<RuntimeArgument> args_;

  @Before
  public void setUp() {
    a_ = new TestRuntimeArgument() {

      int a;
    }.setName("a");
    b_ = new TestRuntimeArgument() {

      int b;
    }.setName("b");
    c_ = new TestRuntimeArgument() {

      int c;
    }.setName("c");
    d_ = new TestRuntimeArgument() {

      int d;
    }.setName("d");
    e_ = new TestRuntimeArgument() {

      int e;
    }.setName("e");
    args_ = new ArrayList<RuntimeArgument>();
    output_ = new ArrayDeque<String>();
  }

  @After
  public void tearDown() {
  }

  /**
   * Test of getArguments method, of class RuntimeArgumentList.
   */
  @Test
  public void testGetArguments() {
    System.out.println("-->> NOW TESTING getArguments METHOD implementation <<");
    System.out.println("      Testing negative examples.");
    testBadGetArguments(new String[]{"-n", "-n"});
    testBadGetArguments(new String[]{"123"});
    testBadGetArguments(new String[]{"-123"});
    System.out.println("      Testing positive examples.");
    testGoodGetArguments(new String[]{"-a"}); //this command should exist
    testGoodGetArguments(new String[]{"-A"});
    testGoodGetArguments(new String[]{"-A", "-c"});
    testGoodGetArguments(new String[]{"-n", "123", "a", "b", "-c"}); //basically doesn't make a lot of sense but should work
  }

  /**
   * All test in this method are supposed to raise an exception
   */
  private void testBadGetArguments(String[] args) {
    try {
      RuntimeArgumentList result = new RuntimeArgumentList(args);
      fail(Arrays.toString(args) + " must fail by definition.");
    }
    catch (IllegalArgumentException e) {
      System.out.println("[PASS] " + Arrays.toString(args) + " failed properly.");
    }
  }

  private void testGoodGetArguments(String[] args) {
    RuntimeArgumentList result = new RuntimeArgumentList(args);
    System.out.println("[PASS] " + Arrays.toString(args) + " didn't cause an exception");
  }

  /**
   * Test of execute method, of class RuntimeArgumentList.
   * Passing in a circular reference - this MUST fail
   */
  @Test
  public void testExecute0() throws Exception {
    a_.getDependencies().add(b_.getClass());
    b_.getDependencies().add(c_.getClass());
    c_.getDependencies().add(a_.getClass());
    args_.add(a_);
    args_.add(c_);
    args_.add(b_);

    RuntimeArgumentList args_list = new RuntimeArgumentList(args_);
    args_list.enclosing_class_ = this;

    try {
      args_list.execute(output_);
      fail("Circular references in runtime argument dependencies are not allowed");
    }
    catch (IllegalArgumentException ex) {
      System.out.println("[PASS] Circular reference check failed properly.");
    }
    catch (Exception ex) {
      fail("Circular references test failed due to an unexpected exception -> " + ex.toString());
    }
  }

  /**
   * Same test as before just without a circular reference
   */
  @Test
  public void testExecute1() {
    a_.getDependencies().add(b_.getClass());
    b_.getDependencies().add(c_.getClass());

    for (int i = 0; i < 10; i++) {
      output_.clear();
      args_.add(a_);
      args_.add(c_);
      args_.add(b_);
      Collections.shuffle(args_);
      String args_order = "";
      for (RuntimeArgument arg : args_) {
        if (args_order.length() > 0) {
          args_order += "->";
        }
        args_order += ((TestRuntimeArgument) arg).name_;
      }

      RuntimeArgumentList args_list = new RuntimeArgumentList(args_);
      args_list.enclosing_class_ = this;

      try {
        args_list.execute(output_);
        Iterator<String> output_iter = output_.iterator();
        //output must be c -> b -> a
        if (!output_iter.next().equals("c")) {
          throw new RuntimeException("Wrong output - expected 'c'");
        }

        if (!output_iter.next().equals("b")) {
          throw new RuntimeException("Wrong output - expected 'b'");
        }

        if (!output_iter.next().equals("a")) {
          throw new RuntimeException("Wrong output - expected 'a'");
        }

        System.out.println("[PASS] Dependency check [" + i + "] passed properly. Arguments were: " + args_order + " ||Output was:" + Arrays.toString(output_.toArray()));
      }
      catch (Exception ex) {
        fail("Execution of arguments with dependencies failed -> " + ex.toString() + ".\nArguments were: " + args_order + " || Output was:" + Arrays.toString(output_.toArray()));
      }
    }
  }

  /**
   * multiple dependencies which must execute in the proper order
   */
  @Test
  public void testExecute2() {
    a_.getDependencies().add(b_.getClass());
    b_.getDependencies().add(c_.getClass());
    b_.getDependencies().add(d_.getClass());
    c_.getDependencies().add(e_.getClass());
    d_.getDependencies().add(e_.getClass());

    for (int i = 0; i < 10; i++) {
      output_.clear();
      args_.add(e_);
      args_.add(c_);
      args_.add(b_);
      args_.add(d_);
      args_.add(a_);

      Collections.shuffle(args_);
      String args_order = "";
      for (RuntimeArgument arg : args_) {
        if (args_order.length() > 0) {
          args_order += "->";
        }
        args_order += ((TestRuntimeArgument) arg).name_;
      }

      RuntimeArgumentList args_list = new RuntimeArgumentList(args_);
      args_list.enclosing_class_ = this;

      try {
        args_list.execute(output_);
      }
      catch (Exception ex) {
        fail("FATAL ERROR");
      }

      List<String[]> allowed_outputs = new ArrayList<String[]>();
      allowed_outputs.add(new String[]{"e", "c", "d", "b", "a"});
      allowed_outputs.add(new String[]{"e", "d", "c", "b", "a"});

      boolean found_match = false;
      for (String[] output : allowed_outputs) {
        if (Arrays.equals(output, output_.toArray())) {
          found_match = true;
          break;
        }
      }

      if (found_match) {
        System.out.println("[PASS] Multiple dependencies check [" + i + "] passed properly. Arguments were: " + args_order + " || Output was:" + Arrays.toString(output_.toArray()));
      }
      else {
        fail("Execution of arguments with dependencies failed. Arguments were: " + args_order + " || Output was:" + Arrays.toString(output_.toArray()));
      }
    }
  }

  /**
   * Execution of runtime arguments where certain dependent arguments are missing
   * (that is they are not passed to the application)
   * The execution must not fail.
   */
  @Test
  public void testExecute3() {
    a_.getDependencies().add(b_.getClass());
    b_.getDependencies().add(c_.getClass());
    b_.getDependencies().add(d_.getClass());
    c_.getDependencies().add(e_.getClass());
    d_.getDependencies().add(e_.getClass());
    d_.getDependencies().add(a_.getClass());

    for (int i = 0; i < 10; i++) {
      output_.clear();
      args_.add(e_);
      args_.add(c_);
      args_.add(d_);
      args_.add(a_);

      Collections.shuffle(args_);
      String args_order = "";
      for (RuntimeArgument arg : args_) {
        if (args_order.length() > 0) {
          args_order += "->";
        }
        args_order += ((TestRuntimeArgument) arg).name_;
      }

      RuntimeArgumentList args_list = new RuntimeArgumentList(args_);
      args_list.enclosing_class_ = this;

      try {
        args_list.execute(output_);
      }
      catch (Exception ex) {
        fail("FATAL ERROR");
      }

      List<String[]> allowed_outputs = new ArrayList<String[]>();
      allowed_outputs.add(new String[]{"a", "e", "c", "d"});
      allowed_outputs.add(new String[]{"a", "e", "d", "c"});
      allowed_outputs.add(new String[]{"e", "c", "a", "d"});
      allowed_outputs.add(new String[]{"e", "a", "c", "d"});
      allowed_outputs.add(new String[]{"e", "a", "d", "c"});

      boolean found_match = false;
      for (String[] output : allowed_outputs) {
        if (Arrays.equals(output, output_.toArray())) {
          found_match = true;
          break;
        }
      }

      if (found_match) {
        System.out.println("[PASS] Multiple dependencies with a missing link in the middle check [" + i + "] passed properly. Arguments were: " + args_order + " || Output was:" + Arrays.toString(output_.toArray()));
      }
      else {
        fail("Execution of arguments with dependencies failed. Arguments were: " + args_order + " || Output was:" + Arrays.toString(output_.toArray()));
      }
    }
  }

  /**
   * Generic test to check whether or not circular dependent RuntimeArguments have 
   * been defined by accident
   */
  @Test
  public void testCircularReferencesDefined() {
    try {
      List<RuntimeArgument> arg_list = new ArrayList<RuntimeArgument>();
      List<RuntimeArgument> verification_list = new ArrayList<RuntimeArgument>();

      for (Class<?> clazz : RuntimeArgument.class.getDeclaredClasses()) {
        if (RuntimeArgument.class.isAssignableFrom(clazz)) {

          RuntimeArgument mock_argument = createMockBuilder((Class<RuntimeArgument>) clazz).withConstructor().addMockedMethod("execute").createMock();

          mock_argument.execute(null); //we expect this method to be called on the mock object

          expectLastCall().times(1); //execute(..) may only be called once
          replay(mock_argument);

          arg_list.add(mock_argument);
          verification_list.add(mock_argument);
        }
      }

      new RuntimeArgumentList(arg_list).execute(null);

      for (RuntimeArgument mock_argument : verification_list) {
        verify(mock_argument);
      }
      System.out.println("[PASS] Circular reference check based on all declared RuntimeArguments passed properly.");
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Verification that no circular reference exists in the list of defined runtime arguments failed -> " + e.toString());
    }
  }

  public static class TestRuntimeArgument extends RuntimeArgument {

    public String name_;

    public TestRuntimeArgument setName(String name) {
      name_ = name;
      return this;
    }

    @Override
    public void execute(Object environment) throws Exception {
//      System.out.println("Now Executing - " + name_);
      ((ArrayDeque) environment).add(name_);
    }
  }
}
