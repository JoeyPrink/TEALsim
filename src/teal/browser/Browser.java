/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Browser.java,v 1.16 2010/07/23 21:38:06 stefan Exp $
 * 
 */

package teal.browser;

import java.io.*;
import java.net.*;

import javax.swing.*;

import teal.framework.*;
import teal.util.*;

/**
 *  
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.16 $
 */

/**
 * Browser is a simple HTML browser launchable from within the application, which can be used to view local documents 
 * (help files) or open URLs.
 */
public class Browser extends JFrame {

    private static final long serialVersionUID = 3257570611449379128L;
    protected JEditorPane htmlPane;
    protected transient TFramework framework = null;
    protected HTMLlinkListener hListener = null;
    
    /**
     * Add a ContentHandler for mime type 'simulation/tealsim'
     * 
     * @see teal.sim.simulation.tealsim
     */
    static {
    	String simContentPrefix = "teal.sim";
    	String pkgs = System.getProperty("java.content.handler.pkgs", "");
    	if (!pkgs.equals("")) {
    		pkgs = pkgs + "|";
    	}
    	pkgs += simContentPrefix;      
    	System.setProperty("java.content.handler.pkgs", pkgs);
    }
    
    public Browser() {
        super();
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        hListener = new HTMLlinkListener();
        htmlPane.addHyperlinkListener(hListener);
        JScrollPane htmlView = new JScrollPane(htmlPane);
        getContentPane().add(htmlView);
        setSize(400, 400);

    }

    public Browser(String path) {
        this(path, "Teal Help");
    }

    /**
     * Open a Browser with specified title and load the page at the specified path.
     * 
     * @param path Path of document to load.
     * @param title Title of browser window.
     */
    public Browser(String path, String title) {
        this();
        setTitle(title);
        displayURL(path);
        pack();
        show();
    }

    public Browser(TFramework fw) {
        this();
        setFramework(fw);
    }

    public void setFramework(TFramework fw) {
        framework = fw;
        hListener.setFramework(fw);

    }

    public TFramework getFramework() {
        return framework;
    }

    /**
     * Display document at specified path.
     * 
     * @param path Path of document to load.
     */
    public void displayURL(String path) {
        URL url = null;
        try {

            url = URLGenerator.getResource(path);
        } catch (Exception e) {
            url = null;
        }
        if (url != null) {
            displayURL(url);

        }
    }

    /**
     * Display document at specified URL.
     * 
     * @param url URL of document to load.
     */
    private void displayURL(URL url) {
        try {
            htmlPane.setPage(url);
            if (!isShowing()) pack();
            show();
        } catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url);
        }
    }

}
