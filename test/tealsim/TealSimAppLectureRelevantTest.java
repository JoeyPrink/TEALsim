package tealsim;

import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import java.util.Collection;
import org.junit.runners.Parameterized.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Chrisi
 */
@RunWith(value = Parameterized.class)
public class TealSimAppLectureRelevantTest {

  private String[] args_;

  public TealSimAppLectureRelevantTest(String arg1) {
    args_ = arg1.split("\\s+");
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
    System.out.println("* Testing ONLY experiments relevant for the lecture!");
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
    System.out.println("Running test for -> " + Arrays.toString(args_) + "\n");
  }

  @After
  public void tearDown() {
  }

  @Parameters
  public static Collection<Object[]> getTestParameters() {
    Collection<Object[]> parameters = new ArrayList<Object[]>();

    List<String> experiments = new ArrayList<String>();

    experiments.add("tealsim.physics.em.AmperesLaw");
    experiments.add("tealsim.physics.em.AmperesLawCurrentDensity");
    experiments.add("tealsim.physics.em.boxInduction");
    experiments.add("tealsim.physics.em.Capacitor");
    experiments.add("tealsim.physics.em.ConductingSphericalShellShielding");
    experiments.add("tealsim.physics.em.ExploringPotential");
    experiments.add("tealsim.physics.em.FallingCoil");
    experiments.add("tealsim.physics.em.FaradayIcePailShield");
    experiments.add("tealsim.physics.em.FaradaysLaw");
    experiments.add("tealsim.physics.em.FaradaysLawRotation");
    experiments.add("tealsim.physics.em.filledCylinderShell");
    experiments.add("tealsim.physics.em.FloatingCoil");
    experiments.add("tealsim.physics.em.GaussLawFlux");
    experiments.add("tealsim.physics.em.Landscape");
    experiments.add("tealsim.physics.em.Pentagon");
    experiments.add("tealsim.physics.em.RadiationCharge");
    experiments.add("tealsim.physics.em.TwoRings");
    
    for (String file : experiments) {
      parameters.add(new String[]{"-gfx J3D -n " + file});
    }
//    for (String file : experiments) {
//      parameters.add(new String[]{"-n " + file + " -gfx JME"});
//    }
    return parameters;
  }

  /**
   * Test of main method, of class TealSimApp.
   */
  @Test
  public void testMain() {
    try {
      TealSimApp test_app = TealSimApp.runTealSim(args_);
      test_app.closeApplication();
      Thread.sleep(1000);
    }
    catch (Throwable e) {
      e.printStackTrace();
      fail("Failed example :: " + Arrays.toString(args_) + " || Exception: " + e.toString());
    }
  }
}
