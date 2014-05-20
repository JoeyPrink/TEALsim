/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GenericToolBar.java,v 1.9 2009/04/24 19:35:49 pbailey Exp $
 * 
 */

package teal.framework;

import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.*;

import teal.util.URLGenerator;

/**
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @author Mesrob
 * @version $Revision: 1.9 $ 
 */
public class GenericToolBar extends JToolBar {

    private static final long serialVersionUID = 3257286928843158839L;
    protected Hashtable<String, JButton> actions = null;

    public GenericToolBar() {
        super();
        actions = new Hashtable<String, JButton>();
    }

    public synchronized void addAction(String action, String text, String iconPath, ActionListener listener) {
        JButton button = null;
        URL url = null;
        ImageIcon icon = null;
        if (iconPath != null && iconPath.length() > 0) url = URLGenerator.getResource(iconPath);
        if (url != null) icon = new ImageIcon(url);
        if (icon != null) {
            button = new JButton(icon);
        } else {
            button = new JButton(action);
        }
        button.setToolTipText(text);
        button.setActionCommand(action);
        button.addActionListener(listener);
        //should worry about duplicate keys
        actions.put(action, button);
        add(button);
    }

    public synchronized void removeAction(String action) {
        JButton button = (JButton) actions.remove(action);
        if (button != null) remove(button);
    }

    public void removeAll() {
        actions.clear();
        super.removeAll();
    }
}