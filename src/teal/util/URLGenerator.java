/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: URLGenerator.java,v 1.5 2010/09/01 20:14:04 stefan Exp $ 
 * 
 */

package teal.util;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Provides for resource location and creation of an URL for the
 * resource, classpath and system configuration paths are searched.
 */

public class URLGenerator {

    public static URL getResource(String name) {
        //TDebug.println("getResource() Target = '" +name + "'");
        int dl = -1;
        URL url = null;
        String test = null;
        try {
        	if(name.toLowerCase().startsWith("http:")||
        			name.toLowerCase().startsWith("file:") ||
        			name.toLowerCase().startsWith("wla:"))
        		return new URL(name);
//            if (name.length() > 5) {
//                test = name.substring(0, 5);
//                //TDebug.println( test);
//                if ((test.compareToIgnoreCase("HTTP:") == 0) || (test.compareToIgnoreCase("FILE:") == 0)) {
//                    url = new URL(name);
//                    return url;
//                }
//            }

            ClassLoader cl = Class.forName("teal.util.URLGenerator").getClassLoader();
            //ClassLoader cl = ClassLoader.getSystemClassLoader();
            url = cl.getResource(name);
            if (url == null) {
                TDebug.println(dl, "getResource(): '" + name + "' NOT FOUND");
                url = ClassLoader.getSystemResource(name);
                TDebug.println(dl, "\tgetSystemResource(): '" + name + "' NOT FOUND");
                if (url == null) {
                    Enumeration<URL> en = null;
                    TDebug.println(dl, "\t trying getSystemResources()");
                    try {
                        en = ClassLoader.getSystemResources(name);
                    } catch (IOException ioe) {
                        TDebug.println(0, ioe.getMessage());
                    }
                    while (en.hasMoreElements()) {
                        URL u = (URL) en.nextElement();
                        TDebug.println(dl, "\t\tURL: " + u);
                    }
                }
            }
        }
        //catch(ClassNotFoundException e){
        catch (Exception e) {
            TDebug.printThrown(e, "URLGenerator: ");
        }
        //TDebug.println("URL = '" + url +"'");
        return url;
    }

    public static URL getResource(String path, String name) {

        return getResource(path + name);
    }

}
