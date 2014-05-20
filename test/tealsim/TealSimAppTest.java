/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tealsim;

import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import java.util.Collection;
import org.junit.runners.Parameterized.Parameters;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Stack;
import java.io.File;
import java.util.List;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unsophisticated test suite checking which of the existing experiments start
 * and close without exceptions.
 * 
 * @author Chrisi
 */
@RunWith(value = Parameterized.class)
public class TealSimAppTest {

  private String[] args_;

  public TealSimAppTest(String arg1) {
    args_ = arg1.split("\\s+");
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
    System.out.println("* TestApplicationTest: testMain()");
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
    System.out.println("Running test for -> " + args_[1] + "\n");
  }

  @After
  public void tearDown() {
  }

  @Parameters
  public static Collection<Object[]> getTestParameters() {
    Collection<Object[]> parameters = new ArrayList<Object[]>();

    int count = 0;
//    for (String file : readAllExperiments()) {
////      if(!file.equals("tealsim.physics.em.Capacitor") && count++ > 1)
////        continue;
////      System.out.println(file);
//      parameters.add(new String[]{"-n", file});
//    }
    return parameters;
  }

  /**
   * Test of main method, of class TealSimApp.
   */
  @Test
  public void testMain() {
//    try {
//      TealSimApp test_app = TealSimApp.runTealSim(args_);
//      test_app.closeApplication();
//    }
//    catch (Throwable e) {
//      fail("Failed example :: " + args_[1] + " || Exception: " + e.getMessage());
//    }
  }

  /**
   * Iterative algorithm to retrieve all (java) files in a given directory tree
   * 
   * @return all java files in a set directory tree
   */
  public static List<String> readAllExperiments() {

    // This filter only returns directories
    FileFilter directoryFilter = new FileFilter() {

      public boolean accept(File file) {
        return file.isDirectory();
      }
    };

    // This filter only returns java files
    FileFilter javaFileFilter = new FileFilter() {

      public boolean accept(File file) {
        if (!file.isFile()) {
          return false;
        }

        int dotposition = file.getName().lastIndexOf(".");
        String ext = file.getName().substring(dotposition + 1, file.getName().length());

        return ext.equals("java");
      }
    };

    Stack<File> dirs = new Stack<File>();
    List<String> files = new ArrayList<String>();

    File base_dir = new File("./src/tealsim/physics");

    dirs.push(base_dir);
    while (!dirs.empty()) {
      File current_dir = dirs.pop();
      dirs.addAll(Arrays.asList(current_dir.listFiles(directoryFilter)));

      //convert all java files in the current dir
      for (File file : current_dir.listFiles(javaFileFilter)) {
        try {
          String tmp = file.getCanonicalPath().substring(base_dir.getCanonicalPath().length() - 15, file.getCanonicalPath().length() - 5);
          tmp = tmp.replace(File.separatorChar, '.');
          files.add(tmp);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return files;
  }
}
