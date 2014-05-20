/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TDebug.java,v 1.12 2009/04/24 19:35:58 pbailey Exp $ 
 * 
 */

package teal.util;

/**
 * debug routines
 */

import imx.loggui.LogMaster;
import java.io.*;
import java.util.*;
import javax.swing.JFrame;

/**
 * @todo: fix...
 * @author cschratter
 */
public class TDebug {

    public final static int gLevel;
    public final static PrintStream gOut;
    protected static int indentValue = 0;
    protected static int indentStep = 2;
    protected static int mLevel = 1000;

    static {
        gLevel = 0;
        gOut = System.out;
    }

    public static void setGlobalLevel(int i) {
//        gLevel = i;
    }
//
    public static int getGlobalLevel() {
        return gLevel;
    }
//
//    public static PrintStream getOutput() {
//        return gOut;
//    }
//
//    public static void setOutput(String outPath) {
//        try {
//            PrintStream out = new PrintStream(new FileOutputStream(outPath));
//            setOutput(out);
//        } catch (Exception e) {
//            System.out.println("Error on TDebug.setOutput");
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public static void setOutput(PrintStream out) {
////        gOut = out;
//    }

    public TDebug() {
        mLevel = 0;
    }

    public TDebug(int level) {
        mLevel = level;
    }
//
    public static String indent() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < indentValue; i++) {
            buf.append(' ');
        }
        return buf.toString();
    }

    public static void indentP() {
        indentValue += indentStep;

    }

    public static void indentN() {
        indentValue -= indentStep;
        if (indentValue < 0) indentValue = 0;
    }

    public static void println(String str) {
        gOut.println(str);
    }

    public static void println(int level, String str) {
        if (level <= gLevel) gOut.println(str);
    }

    public static void println(int str) {
        gOut.println(str);
    }

    public static void println(int level, int str) {
        if (level <= gLevel) gOut.println(str);
    }

    public static void println(double str) {
        gOut.println(str);
    }

    public static void println(int level, double str) {
        if (level <= gLevel) gOut.println(str);
    }

    public static void println(float str) {
        gOut.println(str);
    }

    public static void println(int level, float str) {
        if (level <= gLevel) gOut.println(str);
    }

    public static void println(boolean str) {
        gOut.println(str);
    }

    public static void println(int level, boolean str) {
        if (level <= gLevel) gOut.println(str);
    }

    public static void println(Object str) {
        gOut.println(str);
    }

    public static void println(int level, Object str) {
        if (level <= gLevel) gOut.println(str);
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public static void print(String str) {
        gOut.print(str);
    }

    public static void print(int level, String str) {
        if (level <= gLevel) gOut.print(str);
    }

    public static void print(int level, int str) {
        if (level <= gLevel) gOut.print(str);
    }

    public static void print(int level, double str) {
        if (level <= gLevel) gOut.print(str);
    }

    public static void print(int level, float str) {
        if (level <= gLevel) gOut.print(str);
    }

    public static void print(int level, boolean str) {
        if (level <= gLevel) gOut.print(str);
    }

    public static void print(int level, Object str) {
        if (level <= gLevel) gOut.print(str);
    }

    public static void printThrown(Throwable t) {
        gOut.println(t.getMessage());
        t.printStackTrace(gOut);
    }

    public static void printThrown(int level, Throwable t) {
        if (level <= gLevel) {
            printThrown(t);
        }
    }

    public static void printThrown(Throwable t, String str) {
        gOut.println(str);
        gOut.println(t.getMessage());
        t.printStackTrace(gOut);
    }

    public static void printThrown(int level, Throwable t, String str) {
        if (level <= gLevel) {
            printThrown(t, str);
        }
    }

//    static public void printInfo(int level, Object obj) {
//        if (level <= gLevel) {
//            printInfo(obj);
//        }
//    }

//    static public void printInfo(Object obj) {
//        Class<?> cl = obj.getClass();
//        gOut.println("Info for Object: " + obj);
//        gOut.println("  Class: " + cl.getName());
//        Package pack = cl.getPackage();
//
//        printPackageInfo(pack);
//    }

//    static public void printInfo(int level, String pkgName, String className) {
//        if (level <= gLevel) {
//            printInfo(pkgName, className);
//        }
//    }

//    static public void printInfo(String pkgName, String className) {
//
//        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//        try {
//            classLoader.loadClass(pkgName + "." + className);
//            gOut.println("Info for Class: " + pkgName + "." + className);
//            Package p = Package.getPackage(pkgName);
//            printPackageInfo(p);
//        } catch (ClassNotFoundException e) {
//            gOut.println("Unable to load " + pkgName);
//        }
//
//        gOut.println();
//
//    }

//    static void printPackageInfo(int level, Package p) {
//        if (level <= gLevel) {
//            printPackageInfo(p);
//        }
//    }
//
//    static void printPackageInfo(Package p) {
//        if (p == null) {
//            gOut.println("WARNING: Package Name is null");
//        } else {
//            gOut.println("  Package: " + p.getName());
//            gOut.println("    Specification Title = " + p.getSpecificationTitle());
//            gOut.println("    Specification Vendor = " + p.getSpecificationVendor());
//            gOut.println("    Specification Version = " + p.getSpecificationVersion());
//            gOut.println("    Implementation Title = " + p.getImplementationTitle());
//            gOut.println("    Implementation Vendor = " + p.getImplementationVendor());
//            gOut.println("    Implementation Version = " + p.getImplementationVersion());
//        }
//    }
//
//    static public void printSystemProperties() {
//
//        gOut.println("\nSystem Properties:");
//        Properties prop = System.getProperties();
//        prop.list(gOut);
//        gOut.println("\tEND of Properties\n");
//    }
//
//    static public void printSystemProperties(int level) {
//        if (level <= gLevel) {
//            printSystemProperties();
//        }
//    }

//    public static void dump(double[] data) {
//        TDebug.print("   {");
//        for (int i = 0; i < data.length; i++) {
//            TDebug.print("\t" + data[i] + ((i % 4) == 3 ? ",\n" : ","));
//        }
//        TDebug.println("\t}");
//    }
//
//    public static void dump(float[] data) {
//        TDebug.print("   {");
//        for (int i = 0; i < data.length; i++) {
//            TDebug.print("\t" + data[i] + ((i % 4) == 3 ? ",\n" : ","));
//        }
//        TDebug.println("\t}");
//    }
//
    public void debugln(int level, String str) {
        if (level <= mLevel) gOut.println(str);
    }

    public void debug(int level, String str) {
        if (level <= mLevel) gOut.print(str);
    }

}
