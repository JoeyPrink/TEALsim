/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Wed May 25 11:25:13 EDT 2011 */
package teal;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class Version {


   /** buildDate (set during build process to 1306337113305L). */
   private static Date buildDate = new Date(1306337113305L);

   /**
    * Get buildDate (set during build process to Wed May 25 11:25:13 EDT 2011).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /** year (set during build process to "2011"). */
   private static String year = "2011";

   /**
    * Get year (set during build process to "2011").
    * @return String year
    */
   public static final String getYear() { return year; }


   /** project (set during build process to "TEALsimPlus"). */
   private static String project = "TEALsimPlus";

   /**
    * Get project (set during build process to "TEALsimPlus").
    * @return String project
    */
   public static final String getProject() { return project; }


   /** buildTimestamp (set during build process to "05/25/2011 11:25 AM"). */
   private static String buildTimestamp = "05/25/2011 11:25 AM";

   /**
    * Get buildTimestamp (set during build process to "05/25/2011 11:25 AM").
    * @return String buildTimestamp
    */
   public static final String getBuildTimestamp() { return buildTimestamp; }


   /** version (set during build process to "v0.7"). */
   private static String version = "v0.7";

   /**
    * Get version (set during build process to "v0.7").
    * @return String version
    */
   public static final String getVersion() { return version; }

}
