/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ActionRecord.java,v 1.3 2007/07/16 22:04:46 pbailey Exp $
 * 
 */

package teal.framework;

/**
 * this inner class  could be moved outside of the Framework,
 * it may be improved to allow for method parameter specification.
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.3 $ 
 */
public class ActionRecord {

    String actionName;
    String iconPath;
    String className;
    String methodName;
    Object instance;

    ActionRecord() {
    }

    ActionRecord(String lab, String path, String clName, String mName) {
        actionName = lab;
        iconPath = path;
        className = clName;
        methodName = mName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String name) {
        actionName = name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String path) {
        iconPath = path;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String name) {
        className = name;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String name) {
        methodName = name;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object obj) {
        instance = obj;
    }
}
/*	
 ActionRecord [] sims =
 {
 
 new Record("Test All","apps.teal.TestModels","createTest_ALL"),
 new SimRecord("Test 1","apps.teal.TestModels","createTest_1"),
 new SimRecord("Test2","apps.teal.TestModels","createTest2"),
 new SimRecord("Test 2xx","apps.teal.TestModels","createTest2xx"),
 new SimRecord("ZOO","apps.teal.TestModels","createZoo"),
 new SimRecord("Many Points","apps.teal.TestModels","createManyPoints"),
 new SimRecord("Infinite Wire","apps.teal.TestModels","createWire"),
 new SimRecord("Many Wires","apps.teal.TestModels","createWires"),
 new SimRecord("Magnet 1","apps.teal.TestModels","createMagTest"),
 new SimRecord("Magnet 2","apps.teal.TestModels","createMagTest2"),
 //This is now a JFrame and not an Applet, so can not be added to Framework		
 //	new SimRecord("Teal3D Zoo","apps.teal.ElectroMagneticZooApplication","")
 
 };
 */
