/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Classifier.java,v 1.6 2009/04/24 19:35:58 pbailey Exp $ 
 * 
 */

package teal.util;

import java.util.*;

/** test for a list of classes and instances which may or may not
 be used as part of some condition.
 **/

public class Classifier extends TreeSet {

    private static final long serialVersionUID = 3257286941794842680L;

    public Classifier() {
        super(new ClassComparator());
    }

    public void dump() {
        int level = 0;
        int i = 0;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Class) {
                TDebug.println(level, i + "  Class:    " + ((Class<?>) obj).getName());
            } else {
                TDebug.println(level, i + "  Instance: " + obj.toString());
            }
            i++;
        }
    }

}