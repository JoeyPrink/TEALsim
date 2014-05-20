/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ActionHandler.java,v 1.9 2010/07/23 21:38:07 stefan Exp $
 * 
 */

package teal.framework;

import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import teal.util.URLGenerator;

/**
 * class to parse Record specification of application requests.
 * Should be improved and may be moved out of the Framework.
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.9 $
 */

public class ActionHandler implements ActionListener {

    transient TFramework theApp = null;
    Hashtable<String,ActionRecord> simMap = new Hashtable<String,ActionRecord>();

    ActionHandler(TFramework app) {
        theApp = app;
    }

    void add(String target, ActionRecord rec) {
        simMap.put(rec.actionName, rec);
        TealAction action = new TealAction(rec.actionName, new ImageIcon(URLGenerator.getResource(rec.iconPath)));
        action.addActionListener(this);
        theApp.addAction(target, action);
    }

    public void actionPerformed(ActionEvent e) {
        Object target = null;
        Object classObj = null;
        ActionRecord simRec = null;
        try {
            String name = e.getActionCommand();
            simRec = (ActionRecord) simMap.get(name);
            if (simRec != null) {
                Class<?> theClass = Class.forName(simRec.className);
                System.out.println("Class is: " + theClass.toString());
                classObj = theClass.newInstance();
                System.out.println("ClassObj is: " + classObj.toString());
                if ((simRec.methodName != null) && (simRec.methodName.length() > 0)) {
                    Method theMethod = theClass.getMethod(simRec.methodName, (Class<?>[])null);

                    target = theMethod.invoke(classObj, (Object[])null);
                } else {
                    target = classObj;
                }
                if (target != null) {
                    /*
                     System.out.println("target is: " + target.toString());
                     System.out.println(" targetClass is: " + target.getClass().getName());
                     if(target instanceof Applet)
                     {
                     System.out.println("Adding Applet: " + simRec.label);
                     addTab(simRec.label,(Applet) target);
                     ((Applet)target).start();
                     }
                     else if(target instanceof SimPanel)
                     {
                     System.out.println("Adding SimPanel: " + simRec.label);
                     addTab(simRec.label,(SimPanel) target);
                     }
                     else if(target instanceof JPanel)
                     {
                     System.out.println("Adding Panel: " + simRec.label);
                     addTab(simRec.label,(JPanel) target);
                     }
                     
                     else if(target instanceof Panel)
                     {
                     System.out.println("Adding Panel: " + simRec.label);
                     addTab(simRec.label,(Panel) target);
                     }
                     else
                     {
                     System.out.println("Error Applet: " + simRec.label);
                     }
                     */
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception in SimItemHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
