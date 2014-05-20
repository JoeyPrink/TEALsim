/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: IconCreator.java,v 1.5 2007/12/24 23:03:14 jbelcher Exp $ 
 * 
 */

package teal.util;

import java.net.*;

import javax.swing.*;

/**
 * Provides for resource processing and creation of an Icon independent of the
 * physical location of the image file. It does depend on teal.config.Swing.pathToButton.
 *
 * @see teal.config.swing
 */

public class IconCreator {

    public static Icon getIcon(String path, String fileName) {
        return getIcon(path + fileName);
    }

    public static Icon getIcon(String name) {
        Icon theIcon = null;
        try {
            ClassLoader cl = Class.forName("teal.util.IconCreator").getClassLoader();
            URL url = cl.getResource(name);
            if (url == null) {
                //TDebug.println(1,"getResouce() " + name + " NOT FOUND");
                url = ClassLoader.getSystemResource(name);
            }
            if (url != null) {
                theIcon = new ImageIcon(url);
            } else {
                theIcon = new ImageIcon(name);
            }
        } catch (ClassNotFoundException e) {
        }
        return theIcon;
    }

}
