/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package teal.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author Chrisi
 */
public class Tools {

  
  static void displayJarFilesEntries(){
    String cp = System.getProperty("java.class.path");
    String pathSep = File.pathSeparator;  
    String[] jarOrDirectories = cp.split(pathSep);
    for(String fileName : jarOrDirectories){
        File file = new File(fileName);
        if(file.isFile()){
            JarFile jarFile;
            try{
                jarFile = new JarFile(fileName);
            } catch(final IOException e){
                throw new RuntimeException(e);
            }
            System.out.println(" Entries of jar file " + jarFile.getName());
            for(final Enumeration<JarEntry> enumJar = jarFile.entries(); enumJar.hasMoreElements();){
                JarEntry entry = enumJar.nextElement();
                if(entry.getName().indexOf("png") != -1)
                  System.out.println(entry.getName());
            }
        }
    }
}
}
